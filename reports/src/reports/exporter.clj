(ns reports.exporter
  (:require [clojure.java.io :as io])
  (:import java.util.UUID
           org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory))

(defn- get-ext [t]
  (if (= t "SURVEY_FORM")
    "xls"
    "xlsx"))

(defn- getfile [et id]
  (let [path (str "/var/tmp/akvo/flow/reports/" (UUID/randomUUID))]
    (do
      (.mkdirs (io/file path))
      (io/file (format "%s/%s-%s.%s" path et id (get-ext et))))))

(defn doexport [type base id opts]
  (let [exp (.getExporter (SurveyDataImportExportFactory.) type)
        f (getfile type id)]
    (.export exp {"surveyId" id} f base opts) f))
