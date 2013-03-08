(ns reports.scheduler
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.conversion :as qc]
            [reports.exporter :as exp]))

(j/defjob ExportJob
  [ctx]
  (let [{base-url "baseURL"
         exportType "exportType"
         sid "surveyId"
         opts "opts"} (qc/from-job-data ctx)]
    (exp/doexport exportType base-url sid opts)))


(defn get-executing-jobs []
  "Returns a list of executing jobs in form of JobExecutionContext"
    (.getCurrentlyExecutingJobs @qs/*scheduler*))

(defn filter-executing-jobs [k]
  "Filter the list of executing jobs by key (usually just 1 occurrence)"
  (let [jkey (j/key k)]
    (filter #(= (.getKey (.getJobDetail %)) jkey) (get-executing-jobs))))

(defn job-executing? [k]
  "Returns true if there is a running job for that particular key"
  (if (empty? (filter-executing-jobs k))
      false true))

(defn schedule-job [params]
  (let [id (str (hash (str params)))
        jkey (j/key id)
        job (j/build
              (j/of-type ExportJob)
              (j/using-job-data params)
              (j/with-identity jkey))
        trigger (t/build
                  (t/with-identity (t/key id))
                  (t/start-now))]
    (qs/maybe-schedule job trigger)
    {"status" "OK"}))