(ns org.akvo.flow.dashboard.users.store
  (:require  [clojure.set :as set]
             [org.akvo.flow.dashboard.app-state :refer (app-state)]
             [org.akvo.flow.dashboard.dispatcher :as dispatcher]
             [org.akvo.flow.dashboard.ajax-helpers :as ajax]
             [cljs.core.async :as async]
             [ajax.core :refer (GET POST PUT DELETE)])
  (:require-macros [cljs.core.async.macros :refer (go go-loop)]))

(defn get-user
  [users user-id]
  {:pre [(integer? user-id)]}
  (get-in users [:by-id user-id]))

(def default-range-params {:limit 20
                           :offset 0
                           :sort-by "emailAddress"
                           :sort-order "ascending"})

(defn get-by-range [users params]
  {:pre [(set/subset? (set (keys params))
                      #{:limit :offset :sort-by :sort-order})]}
  (let [{:keys [limit offset sort-by sort-order]} (merge default-range-params params)
        users (vals (:by-id users))]
    (->> users
         (cljs.core/sort-by #(get % sort-by))
         (drop offset)
         (take limit))))

;; Events

(let [chan (dispatcher/register :new-user)]
  (go-loop []
    (let [[_ new-user] (<! chan)]
      (assert new-user)
      (POST "/rest/users"
            (merge ajax/default-ajax-config
                   {:params {"user" new-user}
                    :handler (fn [response]
                               (let [user (get response "user")
                                     user-id (get user "keyId")]
                                 (swap! app-state assoc-in [:users :by-id user-id] user)))})))
    (recur)))

(let [chan (dispatcher/register :edit-user)]
  (go-loop []
    (let [[_ user] (<! chan)
          user-id (get user "keyId")]
      (assert user-id (str "No user-id for user " user))
      (PUT (str "/rest/users/" user-id)
           (merge ajax/default-ajax-config
                  {:params {"user" user}
                   :handler (fn [response]
                              (let [user (get response "user")
                                    user-id (get user "keyId")]
                                (swap! app-state assoc-in [:users :by-id user-id] user)))})))
    (recur)))


(let [chan (dispatcher/register :delete-user)]
  (go-loop []
    (let [[_ user] (<! chan)
          user-id (get user "keyId")]
      (assert user-id (str "No user-id for user " user))
      (DELETE (str "/rest/users/" user-id)
              (merge ajax/default-ajax-config
                     {:handler (fn [response]
                                 (swap! app-state update-in [:users :by-id] #(dissoc % user-id)))})))
    (recur)))

(let [chan (dispatcher/register :new-access-key)]
  (go-loop []
    (let [[_ {:keys [user access-key]}] (<! chan)]
      (swap! app-state assoc-in [:users :by-id (get user "keyId") "accessKey"] access-key)
      (recur))))

(let [chan (dispatcher/register :fetch-users)]
  (go-loop []
    (let [_ (<! chan)]
      (GET "/rest/users"
           (merge ajax/default-ajax-config
                  {:handler (fn [response]
                              (let [users-index (reduce (fn [users user]
                                                         (assoc users (get user "keyId") user))
                                                       {}
                                                       (get response "users"))]
                                (swap! app-state assoc-in [:users :by-id] users-index)))})))))
