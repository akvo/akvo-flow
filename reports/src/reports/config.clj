(ns reports.config
  (:import org.apache.commons.io.FileUtils
           java.io.File)
  (:require [clojure.java.io :as io]))


(defn- load-properties [file]
  "Returns a map {uploadUrl, {props}}
   Loads a java.util.Properties file. It assumes that is an UploadConstants.properties
   and that the `uploadUrl` key is present."
  (with-open [is ^java.io.InputStream (io/input-stream file)]
    (let [props (java.util.Properties.)]
      (.load props is)
      (assoc {} (.getProperty ^java.util.Properties props "uploadUrl") props))))

(defn- list-properties-files [path]
  "List all files in the path (including subfolders) filtering by .properties files"
  (let [exts (into-array String ["properties"])]
    (FileUtils/listFiles (io/file path) exts true)))

(defn load-settings [path]
  "Returns a map of all UploadConstants.properties files in a directory"
  (loop [files (filter #(= "UploadConstants.properties" (.getName ^File %)) (list-properties-files path))
         m {}]
    (if-not (seq files)
      m
      (recur (next files) (into m (load-properties (first files)))))))