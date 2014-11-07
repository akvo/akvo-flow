(ns org.akvo.flow.dashboard.users.store
  (:require  [clojure.set :as set]
             [org.akvo.flow.dashboard.app-state :refer (app-state)]
             [org.akvo.flow.dashboard.dispatcher :as dispatcher]
             [org.akvo.flow.dashboard.ajax-helpers :as ajax]
             [cljs.core.async :as async]
             [ajax.core :refer (GET POST PUT DELETE)])
  (:require-macros [cljs.core.async.macros :refer (go go-loop)]))

(defn fetch-and-update-user
  [user-id]
  (GET (str "/rest/users/" user-id)
       (merge ajax/default-ajax-config
              {:handler #(swap! app-state assoc-in [:users :by-id user-id] (get % "user"))})))

(defn fetch-and-update-users-range [params]
  {:pre [(= (set (keys params))
            #{:limit :offset :sort-by :sort-order})]}
  (GET (str "/rest/users/fetch" (ajax/query-str params))
       (merge ajax/default-ajax-config
              {:handler (fn [response]
                          (swap! app-state assoc-in [:users :range params] (get response "users")))})))

(defn get-by-id
  "Find a user by user-id"
  [data user-id]
  {:pre [(integer? user-id)]}
  (if-let [user (get-in data [:users :by-id user-id])]
    user
    (do (fetch-and-update-user user-id)
        nil)))

(def default-range-params {:limit 20
                           :offset 0
                           :sort-by "emailAddress"
                           :sort-order "ascending"})

(defn get-by-range [params]
  {:pre [(set/subset? (set (keys params))
                      #{:limit :offset :sort-by :sort-order})]}
  (let [params (merge default-range-params params)
        users (get-in @app-state [:users :range params])]
    (if users
      users
      (do (fetch-and-update-users-range params)
          :pending))))

(defn find-index-by
  "Returns the index of (the first) item in coll when comparing using
  the result of applying key-fn, or -1 if not present."
  [key-fn item coll]
  (let [k (key-fn item)]
    (or (->> coll
             (map-indexed (fn [i val] (if (= k (key-fn val)) i)))
             (remove nil?)
             first)
        -1)))

(defn replace-user-in-range [app-state range-key user]
  (let [range (get-in app-state [:users :range range-key])
        user-idx (find-index-by #(get % "keyId") user range)]
    (if (neg? user-idx)
      app-state
      (assoc-in app-state [:users :range range-key user-idx] user))))

;; Possible optimization: short-circuit when found for a particular offset/sort-order
(defn update-ranges [user]
  (let [ks (-> @app-state :users :range keys)]
    (doseq [range-key ks]
      (swap! app-state replace-user-in-range range-key user))))

;; Events

(let [chan (dispatcher/register :new-user)]
  (go-loop []
    (let [[_ new-user] (<! chan)]
      (POST "/rest/users"
            (merge ajax/default-ajax-config
                   {:params {"user" new-user}
                    :handler (fn [response]
                               (let [user (get response "user")
                                     user-id (get user "keyId")]
                                 (swap! app-state assoc-in [:users :by-id user-id])))})))
    (recur)))

(let [chan (dispatcher/register :edit-user)]
  (go-loop []
    (let [[_ {:keys [user]}] (<! chan)
          user-id (get user "keyId")]
      (PUT (str "/rest/users/" user-id)
           (merge ajax/default-ajax-config
                  {:params {"user" user}
                   :handler (fn [response]
                              (let [user (get response "user")
                                    user-id (get user "keyId")]
                                (update-ranges user)
                                (swap! app-state assoc-in [:users :by-id user-id] user)))})))
    (recur)))


(let [chan (dispatcher/register :delete-user)]
  (go-loop []
    (let [[_ user] (<! chan)
          user-id (get user "keyId")]
      (DELETE (str "/rest/users/" user-id)
              (merge ajax/default-ajax-config
                     {:handler (fn [response]
                                 (let [user (get response "user")
                                       user-id (get user "keyId")]
                                   (swap! app-state update-in [:users :by-id] #(dissoc % user-id))))})))
    (recur)))

(let [chan (dispatcher/register :new-access-key)]
  (go-loop []
    (let [[_ {:keys [user access-key]}] (<! chan)]
      (swap! app-state assoc-in [:users :by-id (get user "keyId") "accessKey"] access-key)
      (recur))))
