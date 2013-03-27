(ns reports.exporter
  (:require [clojure.java.io :as io])
  (:import java.util.UUID
           org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory))

(defonce base-path
  (.getAbsolutePath (io/file (System/getProperty "java.io.tmpdir"))))

(defn- get-ext [t]
  (if (= t "SURVEY_FORM")
    "xls"
    "xlsx"))

(defn- getfile [et id]
  (let [path (format "%s/akvo-flow-reports/%s" base-path (UUID/randomUUID))]
    (do
      (.mkdir (io/file path))
      (io/file (format "%s/%s-%s.%s" path et id (get-ext et))))))


(defn doexport [type base id opts]
  (let [exp (.getExporter (SurveyDataImportExportFactory.) type)
        f (getfile type id)]
    (.export exp {"surveyId" id} f base opts) f))
