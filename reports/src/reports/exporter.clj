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

(defonce base-path
  (.getAbsolutePath (io/file (System/getProperty "user.home"))))

(defn- get-ext [t]
  (if (= t "SURVEY_FORM")
    "xls"
    "xlsx"))

(defn- getfile [et id]
  (let [path (format "%s/tmp/%s" base-path (UUID/randomUUID))
        _ (.mkdir (io/file path))]
    (io/file (format "%s/%s-%s.%s" path et id (get-ext et)))))


(defn doexport [type base id opts]
  (let [exp (.getExporter (SurveyDataImportExportFactory.) type)
        f (getfile type id)]
    (.export exp {"surveyId" id} f base opts) f))
