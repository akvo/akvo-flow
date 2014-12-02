(ns org.akvo.flow.locale
  (:refer-clojure :exclude (munge))
  (:require [clojure.java.io :as io]
            [clojure.string :as s])
  (:import [java.util Properties]))

;; TODO:
;; * When we find a new key, add it to ui-strings.properties and write
;;   the props back to disk.
;; * What to do about unused keys? Can we detect and warn/delete them?

(defn load-props [file]
  (let [props (Properties.)]
    (with-open [r (io/reader file :encoding "UTF-8")]
      (.load props r))
    (into {} props)))

(def locales {:ui (load-props "../../../GAE/src/locale/ui-strings.properties")
              :en (load-props "../../../GAE/src/locale/en.properties")
              :fr (load-props "../../../GAE/src/locale/fr.properties")
              :es (load-props "../../../GAE/src/locale/es.properties")})

(defn munge [k]
  (s/replace (str "_" (name k)) "-" "_"))

(defmacro t [k]
  (let [s (get-in locales [:ui (munge k)])
        v {:en (get-in locales [:en s])
           :fr (get-in locales [:fr s])
           :es (get-in locales [:es s])}]
    `(get ~v (:current-locale @org.akvo.flow.app-state/app-state))))
