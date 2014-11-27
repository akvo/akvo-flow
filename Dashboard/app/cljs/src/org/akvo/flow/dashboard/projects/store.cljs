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

(dispatch-loop
 :projects/fetch _
 (GET "/rest/survey_groups"
      (merge ajax/default-ajax-config
             {:handler (fn [response]
                         (swap! app-state assoc-in [:projects :by-id]
                                (ajax/index-by "keyId" (get response "survey_groups"))))})))
