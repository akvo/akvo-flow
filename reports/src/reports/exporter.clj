(ns reports.exporter
  (:require [clojure.java.io :as io])
  (:import java.util.UUID
           org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory))

;; Raw Data Report
;; exportType = "RAW_DATA"
;; factoryClass = org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory
;; criteria = {"surveyId"  id}
;; options = {"exportMode" "RAW_DATA"
;;            "locale" "en"
;;            "imgPrefix" "http_path_photo_url"
;;            "generateTabFormat" false}

;; Comprehensive Report
;; exportType = "GRAPHICAL_SURVEY_SUMMARY"
;; factoryClass = org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory
;; criteria = {"surveyId" id}
;; options = {"locale" "en" 
;;            "performRollup" true
;;            "nocharts" true
;;            "imgPrefix" "http_path_photo_url"}

;; Survey Form
;; exportType = "SURVEY_FORM"
;; factoryClass = org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory
;; criteria = {"surveyId" id}
;; options = nil

(defn- getfile []
  (io/file (format "/tmp/%s.xlsx" (UUID/randomUUID))))


(defn doexport [type base id opts]
  (let [exp (.getExporter (SurveyDataImportExportFactory.) type)
        f (getfile)]
    (.export exp {"surveyId" id} f base opts) f))
