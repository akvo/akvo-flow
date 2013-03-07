(ns reports.scheduler
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.conversion :as qc]
            [reports.exporter :as exp]))

(j/defjob ExportJob
  [ctx]
  (let [{base-url "base-url"
         exportType "exportType"
         sid "sid"
         opts "opts"} (qc/from-job-data ctx)]
    (exp/doexport exportType base-url sid opts)))

(defn get-executing-jobs []
  "Returns a list of executing jobs in form of JobExecutionContext"
  (.getCurrentlyExecutingJobs @qs/*scheduler*))


(defn get-job-key [k]
  "Returns a JobKey for the given argument"
  (if-not (= (class k) org.quartz.JobKey)
    (j/key (str k))
    k))

(defn job-executing? [k]
  "Returns true if there is a running job for that particular `job key`"
  (let [jkey (get-job-key k)]
    (= 1 (count
           (filter #(= (.getKey (.getJobDetail %)) jkey) (get-executing-jobs))))))