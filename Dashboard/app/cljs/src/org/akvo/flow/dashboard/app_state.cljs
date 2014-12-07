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
      (swap! app-state assoc :current-page page)
      (recur))))
