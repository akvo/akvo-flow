;; Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
;;
;; This file is part of Akvo FLOW.
;;
;; Akvo FLOW is free software: you can redistribute it and modify it under the terms of
;; the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
;; either version 3 of the License or any later version.
;;
;; Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
;; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
;; See the GNU Affero General Public License included below for more details.
;;
;; The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.

(ns org.akvo.flow.dashboard.ajax-helpers
  (:require [clojure.string :as s]
            [org.akvo.flow.dashboard.app-state :refer (app-state)]
            [ajax.core :as ajax :refer (json-request-format GET)]))

(def default-ajax-config
  {:error-handler #(.error js/console (pr-str %))
   :format (json-request-format)})

(defn query-str [params]
  (->> params
       (map (fn [[k v]] (str (name k) "=" v)))
       (s/join "&")
       (str "?")))

(defn index-by [key coll]
  (assert key )
  (reduce (fn [index res]
            (let [elem (get res key ::not-found)]
              (assert (not= elem ::not-found) (str "No value under key " key))
              (assoc index elem res)))
          {}
          coll))

(defn fetch-and-index [resource resource-name]
  {:pre [(string? resource) (keyword? resource-name)]}
  (GET resource
       (merge default-ajax-config
              {:handler (fn [response]
                          (swap! app-state assoc-in [resource-name :by-id]
                                 (index-by "keyId" (get response (name resource-name)))))})))
