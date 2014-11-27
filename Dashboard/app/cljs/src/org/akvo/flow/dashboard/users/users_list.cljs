(ns org.akvo.flow.dashboard.users.users-list
  (:require [clojure.string :as s]
            [org.akvo.flow.dashboard.dispatcher :refer (dispatch)]
            [org.akvo.flow.dashboard.components.dialog :refer (dialog)]
            [org.akvo.flow.dashboard.components.grid :refer (grid)]
            [org.akvo.flow.dashboard.components.bootstrap :as b]
            [org.akvo.flow.dashboard.ajax-helpers :refer (default-ajax-config)]
            [org.akvo.flow.dashboard.users.store :as store]
            [org.akvo.flow.dashboard.users.user-details :refer (user-details)]
            [org.akvo.flow.dashboard.projects.store :as projects-store]
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
     [:a {:on-click #(on-action user)} "Edit"]
     [:a {:on-click #(dispatch :delete-user user)} "Remove"]])))

(defn columns [owner]
  (let [on-action (fn [user]
                    (om/set-state! owner :current-user user))]
    [{:title "#"
      :cell-fn :row-number}
     {:title "User name"
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

(defn users [{:keys [users user_roles projects]} owner]
  (reify
    om/IInitState
    (init-state [this]
      {:pagination {:offset 0
                    :limit 20}
       :sort {:sort-by "emailAddress"
              :sort-order "ascending"}
       :current-user nil})

    om/IDidMount
    (did-mount [this]
      (dispatch :fetch-users nil))

    om/IRenderState
    (render-state [this {:keys [current-user] :as state}]
      (html
       [:div.topNav-spacer.panels
        [:div.mypanel {:class (if current-user "opened" "closed")}
         [:div.topMargin
          [:form.form-inline.text-left.paddingTop {:role "form"}
           [:div.form-group.pull-right
            (b/btn-primary {:class "btn-md"
                            :type "button"
                            :on-click #(om/set-state! owner :current-user empty-user)}
                           :plus "Add new user")]]
          (om/build grid
                    {:data (let [data (store/get-by-range users
                                                          (merge (:pagination state)
                                                                 (:sort state)))]
                             (map (fn [row row-number]
                                    (assoc row :row-number (inc row-number)))
                                  data
                                  (range)))
                     :sort (:sort state)
                     :on-sort (fn [sort-by sort-order]
                                (om/set-state! owner :sort {:sort-by sort-by :sort-order sort-order}))
                     :range (:pagination state)
                     :on-range (fn [offset limit]
                                 (om/set-state! owner :pagination {:offset offset :limit limit}))
                     :key-fn #(get % "keyId")
                     :columns (columns owner)})]]
        [:div.mypanel {:class (if current-user "opened" "closed")}
         [:div
          (om/build user-details {:user current-user
                                  :close! #(om/set-state! owner :current-user nil)
                                  :projects (projects-store/get-project-folders projects)
                                  :user-roles (store/get-roles user_roles)})]]]))))
