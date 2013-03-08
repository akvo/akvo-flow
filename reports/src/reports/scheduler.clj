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


(defn get-job-key [k]
  "Returns a JobKey for the given argument"
  (if-not (= (class k) org.quartz.JobKey)
    (j/key (str k))
    k))

(defn get-executing-jobs []
  "Returns a list of executing jobs in form of JobExecutionContext"
    (.getCurrentlyExecutingJobs @qs/*scheduler*))

(defn filter-executing-jobs [k]
  "Filter the list of executing jobs by key (usually just 1 occurrence)"
  (let [jkey (get-job-key k)]
    (filter #(= (.getKey (.getJobDetail %)) jkey) (get-executing-jobs))))

(defn job-executing? [k]
  "Returns true if there is a running job for that particular key"
  (if (empty? (filter-executing-jobs k))
      false true))


(defn schedule-job [params]
  (let [job (j/build
              (j/of-type ExportJob)
              (j/using-job-data params)
              (j/with-identity
                (j/key "job1")))
        trigger (t/build
                  (t/start-now))
        execution (qs/schedule job trigger)]
    {"execution" (str execution)}))