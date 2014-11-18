(ns org.akvo.flow.dashboard.app-state
  (:require [org.akvo.flow.dashboard.dispatcher :as dispatcher])
  (:require-macros [cljs.core.async.macros :refer (go-loop)]))

(def app-state (atom {:current-page {:name :users/users-list
                                     :params nil}
                      :current-locale :en}))

(let [chan (dispatcher/register :locale-changed)]
  (go-loop []
    (let [[_ new-locale] (<! chan)]
      (swap! app-state assoc :current-locale new-locale)
      (recur))))

(let [chan (dispatcher/register :navigate)]
  (go-loop []
    (let [[_ page] (<! chan)]
      (prn page)
      (swap! app-state assoc :current-page page)
      (recur))))
