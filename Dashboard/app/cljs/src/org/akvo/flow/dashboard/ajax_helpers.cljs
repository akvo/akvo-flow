(ns org.akvo.flow.dashboard.ajax-helpers
  (:require [clojure.string :as s]
            [ajax.core :refer (json-request-format)]))

(def default-ajax-config
  {:error-handler #(.error js/console %)
   :format (json-request-format)})

(defn query-str [params]
  (->> params
       (map (fn [[k v]] (str (name k) "=" v)))
       (s/join "&")
       (str "?")))
