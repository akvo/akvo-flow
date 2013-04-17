(ns reports.uploader
  (:import java.io.File
           org.apache.commons.io.FileUtils
           org.apache.ant.compress.taskdefs.Unzip
           org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory)
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pprint]))


(def configs (atom {}))

(defn set-config! [configs-map]
  (swap! configs into configs-map))

(defn- get-path []
  (format "%s/%s" (System/getProperty "java.io.tmpdir") "akvo/flow/uploads"))

(defn save-chunk [params]
  "Saves the current produced ring temp file in a different folder.
   The expected `params` is a ring map containing a `file` part o the multipart request."
  (let [identifier (format "%s/%s" (get-path) (params "resumableIdentifier"))
        path (io/file identifier)
        tempfile (params "file")]
    (if-not (.exists ^File path)
      (.mkdirs path))
    (io/copy (tempfile :tempfile)
             (io/file (format "%s/%s.%s" identifier (params "resumableFilename") (params "resumableChunkNumber"))))
    "OK"))

(defn- combine [directory filename no-parts]
  "Combine parts of a file into a whole, e.g. file1.zip.1, file1.zip.2 -> file1.zip
   The produced output will be in the same folder where the parts are located"
  (let [f (io/file (format "%s/%s" directory filename))]
    (doseq [idx (range 1 (+ 1 no-parts))]
      (FileUtils/writeByteArrayToFile f (FileUtils/readFileToByteArray (io/file (format "%s/%s.%s" directory filename idx))) true))))

(defn- unzip-file [directory filename]
  "Extract the uploaded content to a folder `zip-content`"
  (let [dest (io/file (format "%s/%s" directory "zip-content"))]
    (if-not (.exists ^File dest)
      (.mkdirs dest))
    (doto (Unzip.)
      (.setSrc (io/file (format "%s/%s" directory filename)))
      (.setDest dest)
      (.execute))
    dest))

(defn get-criteria [upload-domain]
  (let [config (@configs upload-domain)]
    {"uploadBase" (config "uploadUrl")
     "awsId" (config "s3Id")
     "dataPolicy" (config "surveyDataS3Policy")
     "dataSig" (config "surveyDataS3Sig")
     "imagePolicy" (config "imageS3Policy")
     "imageSig" (config "imageS3Sig")}))

(defn- upload [directory base-url upload-domain]
  "Upload the content to S3 and notifies the server"
  (let [importer (.getImporter (SurveyDataImportExportFactory.) "BULK_SURVEY")]
    (.executeImport importer directory base-url (get-criteria upload-domain))))

(defn bulk-upload [base-url unique-identifier filename upload-domain]
  "Combines the parts, extracts and uploads the content of a zip file"
  (let [path (format "%s/%s" (get-path) unique-identifier)
        no-parts (count (seq (FileUtils/listFiles (io/file path) nil false)))]
    (combine path filename no-parts)
    (upload (unzip-file path filename) base-url upload-domain)))
