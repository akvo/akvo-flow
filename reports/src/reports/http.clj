(ns reports.http
  (:require [reports.exporter :as exp])
  (:use ring.middleware.params
        ring.util.response
        ring.adapter.jetty))

(set! *warn-on-reflection* true)

(def custom-mime-type {:xlsx "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"})

(def base-url "http://localhost:8888")
 
(defn export-handler [{{surveyId "surveyId" rtype "reportType"} :params}]
  (-> (response (exp/doexport rtype base-url surveyId nil))
      (content-type (:xlsx custom-mime-type))))

(def app 
  (-> export-handler wrap-params))
