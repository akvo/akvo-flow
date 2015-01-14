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

(ns org.akvo.flow.dashboard.users.users-list
  (:require [clojure.string :as s]
            [org.akvo.flow.dashboard.dispatcher :refer (dispatch)]
            [org.akvo.flow.dashboard.components.dialog :refer (dialog)]
            [org.akvo.flow.dashboard.components.grid :refer (grid)]
            [org.akvo.flow.dashboard.components.bootstrap :as b]
            [org.akvo.flow.dashboard.ajax-helpers :refer (default-ajax-config)]
            [org.akvo.flow.dashboard.dom-helpers :refer (scroll-to-top)]
            [org.akvo.flow.dashboard.users.user-details :refer (user-details)]
            [org.akvo.flow.dashboard.users.store :as store]
            [org.akvo.flow.dashboard.projects.store :as projects-store]
            [org.akvo.flow.dashboard.user-auth.store :as user-auth-store]
            [org.akvo.flow.dashboard.app-state :refer (app-state)]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros (html)]
            [ajax.core :refer (ajax-request GET POST PUT DELETE)])
  (:require-macros [org.akvo.flow.dashboard.t :refer (t>)]))

(def empty-user
  {"admin" false
   "logoutUrl" nil
   "config" nil
   "emailAddress" ""
   "superAdmin" false
   "permissionList" "20"
   "userName" ""})

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
          [:strong (t> _delete) "?"]
          (b/btn-link {:on-click #(do (om/set-state! owner :confirm-delete? false)
                                        (dispatch :delete-user user))}
                      (t> _yes))
          " / "
          (b/btn-link {:on-click #(om/set-state! owner :confirm-delete? false)} (t> _no))]
         [:span
          (b/btn-link {:on-click #(do
                                    (dispatch :projects/fetch nil)
                                    (scroll-to-top)
                                    (on-action user))}
                      :pencil (t> _edit))
          " "
          (b/btn-link {:on-click #( om/set-state! owner :confirm-delete? true)}
                      :remove (t> _delete))])))))

(defn user-roles [{:keys [user user-auth-store roles-store]} owner]
 (om/component
  (let [roles (->> (get user "keyId")
                   (user-auth-store/get-by-user-id user-auth-store)
                   (map (fn [{:strs [roleId objectPath] :as r}]
                          (let [role (store/get-role roles-store roleId)]
                            [:div [:strong (get role "name")]
                             " (" (if (= objectPath "/")
                                    (t> _all_folders_and_surveys)
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
    [{:title (t> _user_name)
      :cell-fn #(get % "userName")
      :sort-by "userName"}
     {:title (t> _email)
      :cell-fn #(get % "emailAddress")
      :sort-by "emailAddress"}
     {:title (t> _roles)
      :component user-roles
      :component-data-fn (fn [user]
                           {:user user
                            :user-auth-store user-auth-store
                            :roles-store roles-store})}
     {:title (t> _api_keys)
      :class "text-center"
      :component api-user-mark}
     {:title (t> _actions)
      :class "text-center"
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
       :sort {:sort-by "userName"
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
       [:div.panels
        [:div.mypanel {:class (if current-user-id "opened" "closed")}
         [:div.row.topMargin
          [:div.col-lg-3.col-md-3.col-sm-3]
          [:div.col-lg-2.col-md-2.col-sm-2]
          [:div.col-lg-2.col-lg-offset-5.col-md-2.col-md-offset-5.col-sm-2.col-sm-offset-5
           [:form.navbar-form.navbar-right
            (b/btn-primary {:class "btn-md"
                            :type "button"
                            :on-click #(do (scroll-to-top)
                                           (om/set-state! owner :current-user-id 0))}
                           :plus (t> _add_new_user))]]]
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
                    :columns (columns owner user-auth user_roles)})]
        [:div.mypanel {:class (if current-user-id "opened" "closed")}
         [:div
          (om/build user-details {:user (if (or (nil? current-user-id)
                                                (zero? current-user-id))
                                          empty-user
                                          (store/get-user users current-user-id))
                                  :close! #(do (scroll-to-top)
                                               (om/set-state! owner :current-user-id nil))
                                  :projects-store projects
                                  :roles-store user_roles
                                  :user-auth-store user-auth
                                  :user-roles (store/get-roles user_roles)})]]]))))
