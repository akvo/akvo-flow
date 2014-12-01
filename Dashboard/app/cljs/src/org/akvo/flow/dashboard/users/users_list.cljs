(ns org.akvo.flow.dashboard.users.users-list
  (:require [clojure.string :as s]
            [org.akvo.flow.dashboard.dispatcher :refer (dispatch)]
            [org.akvo.flow.dashboard.components.dialog :refer (dialog)]
            [org.akvo.flow.dashboard.components.grid :refer (grid)]
            [org.akvo.flow.dashboard.components.bootstrap :as b]
            [org.akvo.flow.dashboard.ajax-helpers :refer (default-ajax-config)]
            [org.akvo.flow.dashboard.users.user-details :refer (user-details)]
            [org.akvo.flow.dashboard.users.store :as store]
            [org.akvo.flow.dashboard.projects.store :as projects-store]
            [org.akvo.flow.dashboard.user-auth.store :as user-auth-store]
            [org.akvo.flow.dashboard.app-state :refer (app-state)]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros (html)]
            [ajax.core :refer (ajax-request GET POST PUT DELETE)]))

(defn target-value [evt]
  (-> evt .-target .-value))

(def empty-user
  {"admin" false
   "logoutUrl" nil
   "config" nil
   "emailAddress" ""
   "superAdmin" false
   "permissionList" "20"
   "userName" ""
   "keyId" nil})

(defn user-actions [{:keys [user on-action]} owner]
  (om/component
   (html
    [:span
     [:a {:on-click #(do (dispatch :user-auth/fetch nil)
                         (dispatch :roles/fetch nil)
                         (dispatch :projects/fetch nil)
                         (on-action user))} "Edit"]
     [:a {:on-click #(dispatch :delete-user user)} "Remove"]])))

(defn columns [owner]
  (let [on-action (fn [user]
                    (om/set-state! owner :current-user-id (get user "keyId")))]
    [{:title "User name"
      :cell-fn #(get % "userName")
      :sort-by "userName"}
     {:title "Email"
      :cell-fn #(get % "emailAddress")
      :sort-by "emailAddress"}
     {:title "Permission list"
      :cell-fn #(if (= (get % "permissionList") "10")
                  "Admin"
                  "User")}
     {:title "Actions"
      :component user-actions
      :component-data-fn (fn [user]
                           {:user user
                            :on-action on-action})}]))

(defn users [{:keys [users user_roles projects user-auth]} owner]
  (reify
    om/IInitState
    (init-state [this]
      {:pagination {:offset 0
                    :limit 20}
       :sort {:sort-by "emailAddress"
              :sort-order "ascending"}
       :current-user-id nil})

    om/IDidMount
    (did-mount [this]
      (dispatch :fetch-users nil))

    om/IRenderState
    (render-state [this {:keys [current-user-id] :as state}]
      (html
       [:div.topNav-spacer.panels
        [:div.mypanel {:class (if current-user-id "opened" "closed")}
         [:div.topMargin
          [:form.form-inline.text-left.paddingTop {:role "form"}
           [:div.form-group.pull-right
            (b/btn-primary {:class "btn-md"
                            :type "button"
                            :on-click #(om/set-state! owner :current-user-id 0)}
                           :plus "Add new user")]]
          (om/build grid
                    {:data (store/get-by-range users
                                               (merge (:pagination state)
                                                      (:sort state)))
                     :sort (:sort state)
                     :on-sort (fn [sort-by sort-order]
                                (om/set-state! owner :sort {:sort-by sort-by :sort-order sort-order}))
                     :range (:pagination state)
                     :on-range (fn [offset limit]
                                 (om/set-state! owner :pagination {:offset offset :limit limit}))
                     :key-fn #(get % "keyId")
                     :columns (columns owner)})]]
        [:div.mypanel {:class (if current-user-id "opened" "closed")}
         [:div
          (om/build user-details {:user (if (or (nil? current-user-id)
                                                (zero? current-user-id))
                                          empty-user
                                          (store/get-user users current-user-id))
                                  :close! #(om/set-state! owner :current-user-id nil)
                                  :projects-store projects
                                  :roles-store user_roles
                                  :user-auth-store user-auth
                                  :user-roles (store/get-roles user_roles)})]]]))))
