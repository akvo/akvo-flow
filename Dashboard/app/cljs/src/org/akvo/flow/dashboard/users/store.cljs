;; Copyright (C) 2014 - 2015 Stichting Akvo (Akvo Foundation)
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

(ns org.akvo.flow.dashboard.users.store
  (:require  [clojure.set :as set]
             [clojure.string :as s]
             [org.akvo.flow.dashboard.app-state :refer (app-state)]
             [org.akvo.flow.dashboard.dispatcher :as dispatcher]
             [org.akvo.flow.dashboard.ajax-helpers :as ajax]
             [ajax.core :refer (GET POST PUT DELETE url-request-format)])
  (:require-macros [org.akvo.flow.dashboard.dispatcher :refer (dispatch-loop)]))

(defn get-user
  [users user-id]
  {:pre [(integer? user-id)]}
  (get-in users [:by-id user-id]))

(def default-range-params {:limit 20
                           :offset 0
                           :sort-by "emailAddress"
                           :sort-order "ascending"})

(defn user-comparator [key order]
  (fn [user-a user-b]
    (* (if (= order "ascending") 1 -1)
       (compare (get user-a key)
                (get user-b key)))))

(defn get-by-range [users params]
  {:pre [(set/subset? (set (keys params))
                      #{:limit :offset :sort-by :sort-order})]}
  (let [{:keys [limit offset sort-by sort-order]} (merge default-range-params params)
        users (vals (:by-id users))]
    (->> users
         (sort (user-comparator sort-by sort-order))
         (drop offset)
         (take limit))))

(defn user-count [users]
  (count (vals (:by-id users))))

(defn get-roles [roles]
  (-> roles :by-id vals))

(defn get-role [roles role-id]
  {:pre [(integer? role-id)]}
  (get-in roles [:by-id role-id]))

;; Dispatch loops

(dispatch-loop
 :new-user new-user
 (assert new-user)
 (POST "/rest/users"
       (merge ajax/default-ajax-config
              {:params {"user" (update-in new-user ["emailAddress"] s/lower-case)}
               :handler (fn [response]
                          (let [user (get response "user")
                                user-id (get user "keyId")]
                            (swap! app-state assoc-in [:users :by-id user-id] user)))})))

(dispatch-loop
 :edit-user user
 (let [user-id (get user "keyId")]
   (assert user-id (str "No user-id for user " user))
   (PUT (str "/rest/users/" user-id)
        (merge ajax/default-ajax-config
               {:params {"user" (update-in user ["emailAddress"] s/lower-case)}
                :handler (fn [response]
                           (let [user (get response "user")
                                 user-id (get user "keyId")]
                             (swap! app-state assoc-in [:users :by-id user-id] user)))}))))


(dispatch-loop
 :delete-user user
 (let [user-id (get user "keyId")]
   (assert user-id (str "No user-id for user " user))
   (DELETE
    (str "/rest/users/" user-id)
    (merge ajax/default-ajax-config
           {:format (url-request-format)
            :handler (fn [response]
                       (swap! app-state update-in [:users :by-id] #(dissoc % user-id)))}))))

(dispatch-loop
 :new-access-key {:keys [user access-key]}
 (swap! app-state assoc-in [:users :by-id (get user "keyId") "accessKey"] access-key))

(dispatch-loop
 :fetch-users _
 (ajax/fetch-and-index "/rest/users" :users))

(dispatch-loop
 :roles/fetch _
 (ajax/fetch-and-index "/rest/user_roles" :user_roles))

(dispatch-loop
 :roles/create role
 (POST "/rest/user_roles"
       (merge ajax/default-ajax-config
              {:params role
               :handler (fn [response]
                          (let [role (get response "user_roles")
                                role-id (get role "keyId")]
                            (swap! app-state assoc-in [:user_roles :by-id role-id] role)))})))

(dispatch-loop
 :roles/edit role ;{:strs [keyId] :as role}
 (let [key-id (get role "keyId")]
   (assert (integer? key-id))
   (PUT (str "/rest/user_roles/" key-id)
        (merge ajax/default-ajax-config
               {:params (dissoc role "keyId")
                :handler (fn [response]
                           (let [role (get response "user_roles")]
                             (swap! app-state assoc-in [:user_roles :by-id key-id] role)))}))))

(dispatch-loop
 :roles/delete key-id
 (assert (integer? key-id))
 (DELETE
  (str "/rest/user_roles/" key-id)
  (merge ajax/default-ajax-config
         {:format (url-request-format)
          :handler (fn [response]
                     (swap! app-state update-in [:user_roles :by-id] dissoc key-id))})))
