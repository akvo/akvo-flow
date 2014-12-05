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

(ns org.akvo.flow.dashboard.user-auth.store
  (:require  [clojure.set :as set]
             [org.akvo.flow.dashboard.app-state :refer (app-state)]
             [org.akvo.flow.dashboard.dispatcher :as dispatcher]
             [org.akvo.flow.dashboard.ajax-helpers :as ajax]
             [ajax.core :refer (GET POST PUT DELETE)])
  (:require-macros [org.akvo.flow.dashboard.dispatcher :refer (dispatch-loop)]))


(defn get-by-user-id [user-auth user-id]
  {:pre [(integer? user-id)]}
  (get-in user-auth [:by-user-id user-id]))

;; TODO: index?
(defn get-by-role-id [user-auth role-id]
  {:pre [(integer? role-id)]}
  (->> (get user-auth :by-id)
       vals
       (filter #(= (get % "roleId") role-id))))



(dispatch-loop
 :user-auth/fetch _
 (GET "/rest/user_auth"
      (merge ajax/default-ajax-config
             {:handler (fn [response]
                         (let [user-auth (get response "user_auth")]
                           (swap! app-state assoc-in [:user-auth :by-id]
                                  (ajax/index-by "keyId" user-auth))
                           (swap! app-state assoc-in [:user-auth :by-user-id]
                                  (group-by #(get % "userId") user-auth))))})))

(dispatch-loop
 :user-auth/create auth
 (let [{:keys [user role object-path]} auth]
   (assert (integer? user))
   (assert (integer? role))
   (assert (string? object-path))
   (POST "/rest/user_auth"
         (merge ajax/default-ajax-config
                {:params {"userId" user
                          "roleId" role
                          "objectPath" object-path}
                 :handler (fn [{:strs [user_auth]}]
                            (swap! app-state assoc-in [:user-auth :by-id (get user_auth "keyId")] user_auth)
                            (swap! app-state update-in [:user-auth :by-user-id (get user_auth "userId")] conj user_auth))}))))

(dispatch-loop
 :user-auth/delete user-auth
 (let [key-id (get user-auth "keyId")
       user-id (get user-auth "userId")]
    (assert (integer? (get user-auth "keyId")))
    (DELETE (str "/rest/user_auth/" key-id)
            (merge ajax/default-ajax-config
                   {:handler (fn [_]
                               (swap! app-state update-in [:user-auth :by-id] dissoc key-id)
                               (swap! app-state update-in [:user-auth :by-user-id user-id]
                                      (fn [user-auths]
                                        (vec (remove #(= (get % "keyId") key-id) user-auths)))))}))))
