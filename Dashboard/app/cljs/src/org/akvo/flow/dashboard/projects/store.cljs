(ns org.akvo.flow.dashboard.projects.store
    (:require  [clojure.set :as set]
               [org.akvo.flow.dashboard.app-state :refer (app-state)]
               [org.akvo.flow.dashboard.dispatcher :as dispatcher]
               [org.akvo.flow.dashboard.ajax-helpers :as ajax]
               [ajax.core :refer (GET POST PUT DELETE)])
    (:require-macros [org.akvo.flow.dashboard.dispatcher :refer (dispatch-loop)]))

(defn get-project-folders [projects]
  (let [v (vals (get projects :by-id))]
    (println (take 3 v))
    v))



(dispatch-loop
 :projects/fetch _
 (GET "/rest/survey_groups"
      (merge ajax/default-ajax-config
             {:handler (fn [response]
                         (println "GOTHERE!")
                         (swap! app-state assoc-in [:projects :by-id]
                                (ajax/index-by "keyId" (get response "survey_groups"))))})))
