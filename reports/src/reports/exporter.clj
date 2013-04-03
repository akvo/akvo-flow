(ns reports.exporter
  (:require [clojure.java.io :as io])
  (:import java.io.File
           java.util.UUID
           org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory))

(defn- get-file-extension [t]
  (if (= t "SURVEY_FORM")
    "xls"
    "xlsx"))

(defn- get-path []
  (format "%s/%s/%s" (System/getProperty "java.io.tmpdir") "akvo/flow/reports" (UUID/randomUUID)))

(defn- get-file [et id]
  (let [path (get-path)]
    (.mkdirs (io/file path))
    (io/file (format "%s/%s-%s.%s" path et id (get-file-extension et)))))

(defn ^File export-report [type base id options]
  (let [exporter (.getExporter (SurveyDataImportExportFactory.) type)
        file (get-file type id)]
    (.export exporter {"surveyId" id} file base options)
    file))
