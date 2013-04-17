;  Copyright (C) 2013 Stichting Akvo (Akvo Foundation)
;
;  This file is part of Akvo FLOW.
;
;  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
;  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
;  either version 3 of the License or any later version.
;
;  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
;  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
;  See the GNU Affero General Public License included below for more details.
;
;  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.

(ns reports.scheduler
  (:refer-clojure :exclude [key])
  (:import [org.quartz JobExecutionContext Scheduler]
           java.io.File
           java.util.UUID)
  (:require [clojure.string :as string :only (split join)]
            [clojurewerkz.quartzite [conversion :as conversion]
                                    [jobs :as jobs]
                                    [scheduler :as scheduler]
                                    [triggers :as triggers]])
  (:use [reports.exporter :only (export-report)]
        [reports.uploader :only (bulk-upload)]))

(def cache (ref {}))

(defn- valid-report? [report-path]
  "Returns if a File is a valid one (exists and non zero length)"
  (if (and (.exists ^File report-path)
           (> (.length ^File report-path) 0))
    true false))

(defn- get-path [report-file]
  "Returns the path to the report or `INVALID_PATH` if not valid File"
  (if (valid-report? report-file)
    (string/join "/" (take-last 2 (string/split (.getAbsolutePath ^File report-file) #"/")))
    "INVALID_PATH"))

(jobs/defjob ExportJob [job-data]
  (let [{:strs [baseURL exportType surveyId opts id]} (conversion/from-job-data job-data)
        report (export-report exportType baseURL surveyId opts)
        path (get-path report)]
    (dosync
      (alter cache conj {{:id id
                          :surveyId surveyId
                          :baseURL baseURL} path}))
    (scheduler/delete-job (jobs/key id))))


(jobs/defjob BulkUploadJob [job-data]
  (let [{:strs [baseURL uniqueIdentifier filename uploadDomain id]} (conversion/from-job-data job-data)]
    (bulk-upload baseURL uniqueIdentifier filename uploadDomain)
    (scheduler/delete-job (jobs/key id))))

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

(defn- get-random-id []
  "Generates a unique identifier UUID"
  (str (UUID/randomUUID)))

(defn- get-job [job-type id params]
  "Returns a Job specification for the given parameters"
  (jobs/build
    (jobs/of-type job-type)
    (jobs/using-job-data (conj params {"id" id} ))
    (jobs/with-identity (jobs/key id))))

(defn- get-trigger [id]
  "Returns a Trigger to be executed now"
  (triggers/build
    (triggers/with-identity (triggers/key id))
    (triggers/start-now)))

(defn- schedule-job [job-type id params]
  "Schedule a report for generation. For concurrent requests only schedules the report
   once."
  (let [job (get-job job-type id params)
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
    (schedule-job ExportJob (report-id params) params)))

(defn process-and-upload [params]
  (schedule-job BulkUploadJob (get-random-id) params))