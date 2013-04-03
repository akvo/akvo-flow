(ns reports.scheduler
  (:refer-clojure :exclude [key])
  (:import [org.quartz JobExecutionContext Scheduler]
           java.io.File)
  (:require [clojure.string :as string :only (split join)]
            [clojurewerkz.quartzite [conversion :as conversion]
                                    [jobs :as jobs]
                                    [scheduler :as scheduler]
                                    [triggers :as triggers]])
  (:use [reports.exporter :only (export-report)]))

(def cache (ref {}))

(defn- valid-report? [report-path]
  "Returns if a File is a valid one (exists and non zero length)"
  (if (and (.exists ^File report-path)
           (> 0 (.length ^File report-path)))
    true false))

(defn- get-path [report-file]
  "Returns the path to the report or `INVALID_PATH` if not valid File"
  (if (valid-report? report-file)
    (string/join "/" (take-last 2 (string/split (.getAbsolutePath ^File report-file) #"/")))
    "INVALID_PATH"))

(jobs/defjob ExportJob [job-data]
  (let [{:strs [baseURL exportType surveyId opts reportId]} (conversion/from-job-data job-data)
        report (export-report exportType baseURL surveyId opts)
        path (get-path report)]
    (dosync
      (alter cache conj {{:id reportId
                          :surveyId surveyId
                          :baseURL baseURL} path}))
    (scheduler/delete-job (jobs/key reportId))))

(defn- get-executing-jobs-by-key [key]
  "Get a list of executing jobs by key (usually returns just 1 job)"
  (filter #(= (.. ^JobExecutionContext % (getJobDetail) (getKey)) (jobs/key key))
          (.getCurrentlyExecutingJobs ^Scheduler @scheduler/*scheduler*)))

(defn- job-executing? [key]
  "Returns true if there is a running job for that particular key"
  (if (seq (get-executing-jobs-by-key key)) true false))

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
    (if (seq found)
      (@cache (nth found 0)))))

(defn invalidate-cache [params]
  "Invalidates (removes) a given file from the cache"
  (let [baseURL (params "baseURL")]
    (doseq [sid (params "surveyIds")]
      (dosync
        (doseq [key (keys @cache) :when (and (= (:baseURL key) baseURL) (= (str sid) (:surveyId key)))]
        (alter cache dissoc key))))
    "OK"))

(defn generate-report [params]
  "Returns the cached report for the given parameters, or schedules the report for generation"
  (if-let [file (get-report-by-id (report-id params))]
    (if (= file "INVALID_PATH")
      (do
        (invalidate-cache {"baseURL" (params "baseURL")
                           "surveyIds" [(params "surveyId")]})
        {"status" "ERROR"
         "message" "_error_generating_report"})
      {"status" "OK"
       "file" file})
    (schedule-job params)))
