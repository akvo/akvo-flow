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
  (reify
    om/IInitState
    (init-state [this]
      {:confirm-delete? false})
    om/IRenderState
    (render-state [this {:keys [confirm-delete?]}]
      (html
       (if confirm-delete?
         [:span
          [:strong "Delete? "]
          [:button.btn.btn-link {:on-click #(do (om/set-state! owner :confirm-delete? false)
                                        (dispatch :delete-user user))} "Yes"]
          " / "
          [:button.btn.btn-link {:on-click #(om/set-state! owner :confirm-delete? false)} "No"]]
         [:span
          [:button.btn.btn-link {:on-click #(do
                              (dispatch :projects/fetch nil)
                              (on-action user))}
           (b/icon :pencil) " Edit"]
          " "

          [:button.btn.btn-link {:on-click #( om/set-state! owner :confirm-delete? true)}
           (b/icon :remove) " Delete"]])))))

(defn user-roles [{:keys [user user-auth-store roles-store]} owner]
 (om/component
  (let [roles (->> (get user "keyId")
                   (user-auth-store/get-by-user-id user-auth-store)
                   (map (fn [{:strs [roleId objectPath] :as r}]
                          (let [role (store/get-role roles-store roleId)]
                            [:div [:strong (get role "name")]
                             " (" (if (= objectPath "/")
                                    "All folders & surveys"
                                    objectPath) ")"]))))]
    (html [:div roles]))))

(defn api-user-mark [{:strs [accessKey]} owner]
  (om/component
   (html (if accessKey
           (b/icon :ok)
           [:div]))))

(defn columns [owner user-auth-store roles-store]
  (let [on-action (fn [user]
                    (om/set-state! owner :current-user-id (get user "keyId")))]
    [{:title "User name"
      :cell-fn #(get % "userName")
      :sort-by "userName"}
     {:title "Email"
      :cell-fn #(get % "emailAddress")
      :sort-by "emailAddress"}
     {:title "Roles"
      :component user-roles
      :component-data-fn (fn [user]
                           {:user user
                            :user-auth-store user-auth-store
                            :roles-store roles-store})}
     {:title "API keys"
      :component api-user-mark}
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
      (dispatch :fetch-users nil)
      (dispatch :user-auth/fetch nil)
      (dispatch :roles/fetch nil))

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
                     :columns (columns owner user-auth user_roles)})]]
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
