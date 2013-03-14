(ns reports.scheduler
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.conversion :as qc]
            [reports.exporter :as exp]
            [clojure.string :as s :only (split join)]))

(def cache (ref {}))

(j/defjob ExportJob
  [ctx]
  (let [{baseURL "baseURL"
         exportType "exportType"
         sid "surveyId"
         opts "opts"
         reportId "reportId"} (qc/from-job-data ctx)
         report (exp/doexport exportType baseURL sid opts)
         path (s/join "/" (take-last 2 (s/split (.getAbsolutePath report) #"/")))]
    (dosync
      (alter cache conj {{:id reportId
                          :surveyId sid
                          :baseURL baseURL} path}))
    (qs/delete-job (j/key reportId))))


(defn- get-executing-jobs []
  "Returns a list of executing jobs in form of JobExecutionContext"
    (.getCurrentlyExecutingJobs @qs/*scheduler*))

(defn- filter-executing-jobs [k]
  "Filter the list of executing jobs by key (usually just 1 occurrence)"
  (let [jkey (j/key k)]
    (filter #(= (.getKey (.getJobDetail %)) jkey) (get-executing-jobs))))

(defn- job-executing? [k]
  "Returns true if there is a running job for that particular key"
  (if (empty? (filter-executing-jobs k))
      false true))

(defn- report-id [m]
  "Generates a unique identifier based on the map"
  (format "id%s" (hash (str m))))

(defn- get-job [jtype id params]
  "Returns a Job specification for the given parameters"
  (j/build
      (j/of-type jtype)
      (j/using-job-data (conj params {"reportId" id} ))
      (j/with-identity (j/key id))))

(defn- get-trigger [id]
  "Returns a Trigger to be executed now"
  (t/build
    (t/with-identity (t/key id))
    (t/start-now)))

(defn- schedule-job [params]
  "Schedule a report for generation. For concurrent requests only schedules the report
   once."
  (let [id (report-id params)
        job (get-job ExportJob id params)
        trigger (get-trigger id)]
    (qs/maybe-schedule job trigger)
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
  (if-let [f (get-report-by-id (report-id params))]
   {"status" "OK"
    "file" f}
   (schedule-job params)))


(defn invalidate-cache [params]
  "Invalidates (removes) a given file from the cache"
  (let [baseURL (params "baseURL")]
    (doseq [sid (params "surveyIds")]
    (dosync
      (doseq [k (keys @cache) :when (and (= (k :baseURL) baseURL) (= (str sid) (k :surveyId)))]
      (alter cache dissoc k))))
    "OK"))
