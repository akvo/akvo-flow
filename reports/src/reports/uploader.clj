(ns reports.uploader
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pprint])
  (:import java.io.File))

(defn save-file [params]
  (let [identifier (format "/%s/%s/%s" (System/getProperty "java.io.tmpdir") "akvo/flow/uploads" (params :resumableIdentifier))
        path (io/file identifier)
        tempfile (params :file)]
    (pprint/pprint params)
    (if-not (.exists ^File path)
      (.mkdirs path))
    (io/copy (tempfile :tempfile)
             (io/file (format "%s/%s.%s" identifier (params :resumableFilename) (params :resumableChunkNumber))))
    "OK"))
  