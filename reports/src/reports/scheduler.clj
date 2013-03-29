(ns reports.scheduler
  (:refer-clojure :exclude [key])
  (:import java.io.File
           org.quartz.Scheduler)
  (:require [clojure.string :as string :only (split join)]
            [clojurewerkz.quartzite [conversion :as conversion]
                                    [jobs :as jobs]
                                    [scheduler :as scheduler]
                                    [triggers :as triggers]]
            [reports.exporter :as exporter]))

(def cache (ref {}))

(jobs/defjob ExportJob [context]
  (let [{:strs [baseURL exportType surveyId opts reportId]} (conversion/from-job-data context)
        ^File report (exporter/doexport exportType baseURL surveyId opts)
        path (string/join "/" (take-last 2 (string/split (.getAbsolutePath report) #"/")))]
    (dosync
      (alter cache conj {{:id reportId
                          :surveyId surveyId
                          :baseURL baseURL} path}))
    (scheduler/delete-job (jobs/key reportId))))

(defn- get-executing-jobs []
  "Returns a list of executing jobs in the form of ^org.quartz.JobExecutionContext"
  (.getCurrentlyExecutingJobs ^Scheduler @scheduler/*scheduler*))

(defn- filter-executing-jobs [key]
  "Filter the list of executing jobs by key (usually returns just 1 job)"
  (filter #(= (.. % (getJobDetail) (getKey)) (jobs/key key))
          (get-executing-jobs)))

(defn- job-executing? [key]
  "Returns true if there is a running job for that particular key"
  (if (empty? (filter-executing-jobs key)) false true))

(defn- report-id [m]
  "Generates a unique identifier based on the map"
  (format "id%s" (hash (str m))))

(defn- get-job [job-type id params]
  "Returns a Job specification for the given parameters"
  (jobs/build
    (jobs/of-type job-type)
    (jobs/using-job-data (conj params {"reportId" id} ))
    (jobs/with-identity (jobs/key id))))

(defn- get-trigger [id]
  "Returns a Trigger to be executed now"
  (triggers/build
    (triggers/with-identity (triggers/key id))
    (triggers/start-now)))

(defn- schedule-job [params]
  "Schedule a report for generation. For concurrent requests only schedules the report
   once."
  (let [id (report-id params)
        job (get-job ExportJob id params)
        trigger (get-trigger id)]
    (scheduler/maybe-schedule job trigger)
    {"status" "OK"
     "message" "PROCESSING"}))

(defn- get-report-by-id [id]
  "Returns a report from the cache or nil if not found"
  (let [found (filter #(= id (:id %)) (keys @cache))]
    (if (empty? found)
      nil
      (@cache (nth found 0)))))

(defn generate-report [params]
  "Returns the cached report for the given parameters, or schedules the report for generation"
  (if-let [file (get-report-by-id (report-id params))]
   {"status" "OK"
    "file" file}
   (schedule-job params)))

(defn invalidate-cache [params]
  "Invalidates (removes) a given file from the cache"
  (let [baseURL (params "baseURL")]
    (doseq [sid (params "surveyIds")]
      (dosync
        (doseq [key (keys @cache) :when (and (= (:baseURL key) baseURL) (= (str sid) (:surveyId key)))]
        (alter cache dissoc key))))
    "OK"))
