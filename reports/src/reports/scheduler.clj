(ns reports.scheduler
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.conversion :as qc]
            [reports.exporter :as exp]))

(def cache (ref {}))

(j/defjob ExportJob
  [ctx]
  (let [{baseURL "baseURL"
         exportType "exportType"
         sid "surveyId"
         opts "opts"
         reportId "reportId"} (qc/from-job-data ctx)
        reportPath (exp/doexport exportType baseURL sid opts)]
    (dosync
      (alter cache conj {reportId reportPath}))))


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
  (str (hash (str m))))

(defn- schedule-job [params]
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
    {"status" "OK"}))

(defn run-report [params]
 (if-let [f (@cache (report-id params))]
   {"filename" (.getName f)}
   (schedule-job params)))