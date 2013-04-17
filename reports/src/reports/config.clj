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

(ns reports.config
  (:import org.apache.commons.io.FileUtils
           java.io.File)
  (:require [clojure.java.io :as io]
            [clojure.string :as string :only (split)]))



(defn- get-domain [url]
  (last (string/split url #"/")))

(defn- load-properties [file]
  "Returns a map {uploadUrl, {props}}
   Loads a java.util.Properties file. It assumes that is an UploadConstants.properties
   and that the `uploadUrl` key is present."
  (with-open [is ^java.io.InputStream (io/input-stream file)]
    (let [props (java.util.Properties.)]
      (.load props is)
      (assoc {} (get-domain (.getProperty ^java.util.Properties props "uploadUrl")) (into {} props)))))

(defn- list-properties-files [path]
  "List all files in the path (including subfolders) filtering by .properties files"
  (let [exts (into-array String ["properties"])]
    (FileUtils/listFiles (io/file path) ^"[Ljava.lang.String;" exts true)))

(defn load-settings [path]
  "Returns a map of all UploadConstants.properties files in a directory"
  (loop [files (filter #(= "UploadConstants.properties" (.getName ^File %)) (list-properties-files path))
         m {}]
    (if-not (seq files)
      m
      (recur (next files) (into m (load-properties (first files)))))))