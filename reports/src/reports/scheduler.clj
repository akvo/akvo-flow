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
      (alter cache conj {reportId path}))
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
  "Returns the `id` plus hashCode of a 'stringified' version of a map"
  (format "id%s" (hash (str m))))

(defn- schedule-job [params]
  "Schedule a report for generation. For concurrent requests only schedules the report
   once."
  (let [id (report-id params)
        jkey (j/key id)
        job (j/build
              (j/of-type ExportJob)
              (j/using-job-data (conj params {"reportId" id} ))
              (j/with-identity jkey))
        trigger (t/build
                  (t/with-identity (t/key id))
                  (t/start-now))]
    (qs/maybe-schedule job trigger)
    {"status" "OK"
     "message" "PROCESSING"}))

(defn generate-report [params]
  "Returns the cached report for the given parameters, or schedules the report for execution"
  (if-let [f (@cache (report-id params))]
   {"status" "OK"
    "file" f}
   (schedule-job params)))


(defn invalidate-cache [params]
  "Invalidates (remove) a given file from the cache"
  params)