(ns org.akvo.flow.dashboard.users.store
  (:require  [clojure.set :as set]
             [org.akvo.flow.dashboard.app-state :refer (app-state)]
             [org.akvo.flow.dashboard.dispatcher :as dispatcher]
             [org.akvo.flow.dashboard.ajax-helpers :as ajax]
             [ajax.core :refer (GET POST PUT DELETE)])
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

;; Dispatch loops

(dispatch-loop
 :new-user new-user
 (assert new-user)
 (POST "/rest/users"
       (merge ajax/default-ajax-config
              {:params {"user" new-user}
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
               {:params {"user" user}
                :handler (fn [response]
                           (let [user (get response "user")
                                 user-id (get user "keyId")]
                             (swap! app-state assoc-in [:users :by-id user-id] user)))}))))


(dispatch-loop
 :delete-user user
 (let [user-id (get user "keyId")]
   (assert user-id (str "No user-id for user " user))
   (DELETE (str "/rest/users/" user-id)
           (merge ajax/default-ajax-config
                  {:handler (fn [response]
                              (swap! app-state update-in [:users :by-id] #(dissoc % user-id)))}))))

(dispatch-loop
 :new-access-key {:keys [user access-key]}
 (swap! app-state assoc-in [:users :by-id (get user "keyId") "accessKey"] access-key))




(dispatch-loop
 :fetch-users _
 (ajax/fetch-and-index "/rest/users" :users))

(dispatch-loop
 :roles/fetch _
 (ajax/fetch-and-index "/rest/user_roles/all" :roles))
