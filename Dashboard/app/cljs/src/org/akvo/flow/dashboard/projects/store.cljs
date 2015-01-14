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

(ns org.akvo.flow.dashboard.projects.store
    (:require  [clojure.set :as set]
               [org.akvo.flow.dashboard.app-state :refer (app-state)]
               [org.akvo.flow.dashboard.dispatcher :as dispatcher]
               [org.akvo.flow.dashboard.ajax-helpers :as ajax]
               [ajax.core :refer (GET POST PUT DELETE)])
    (:require-macros [org.akvo.flow.dashboard.dispatcher :refer (dispatch-loop)]))

(defn get-by-id [projects id]
  {:pre [(integer? id)]}
  (get-in projects [:by-id id]))

(defn get-projects [projects parent-id]
  {:pre [(or (nil? parent-id) ;; nil is root
             (number? parent-id))]}
  (->> (get projects :by-id)
       vals
       (filter #(= (get % "parentId") parent-id))))

(defn loading? [projects]
  (:loading? projects))

(dispatch-loop
 :projects/fetch _
 (swap! app-state assoc-in [:projects :loading?] true)
 (GET "/rest/survey_groups"
      (merge ajax/default-ajax-config
             {:handler (fn [response]
                         (swap! app-state assoc-in [:projects :loading?] false)
                         (swap! app-state assoc-in [:projects :by-id]
                                (ajax/index-by "keyId" (get response "survey_groups"))))})))
