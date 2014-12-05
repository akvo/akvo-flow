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

(ns org.akvo.flow.dashboard.users.roles
  (:require [clojure.string :as s]
            [clojure.set :as set]
            [org.akvo.flow.dashboard.dispatcher :refer (dispatch)]
            [org.akvo.flow.dashboard.components.grid :refer (grid)]
            [org.akvo.flow.dashboard.components.bootstrap :as b]
            [org.akvo.flow.dashboard.users.store :as store]
            [org.akvo.flow.dashboard.user-auth.store :as user-auth-store]
            [org.akvo.flow.dashboard.users.role-details :refer (role-details all-permissions)]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros (html)])
  (:require-macros [org.akvo.flow.dashboard.t :refer (t>)]))

(defn target-value [evt]
  (-> evt .-target .-value))

(defn set-input! [evt owner korks]
  (om/set-state! owner korks (target-value evt)))

(defn update-input! [evt owner korks f & args]
  (let [old-state (om/get-state owner korks)]
    (om/set-state! owner korks (apply f old-state (target-value evt) args))))

(defn toggle! [owner korks]
  (om/set-state! owner korks (not (om/get-state owner korks))))

(defn role-actions [{:keys [on-action disabled?]} owner]
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
                                      (on-action ::delete))}
                      (t> _yes))
          " / "
          (b/btn-link {:on-click #(om/set-state! owner :confirm-delete? false)} (t> _no))]
         [:span
          (b/btn-link {:on-click #(on-action ::show-edit-view)} :pencil (t> _edit))
          " "
          (b/btn-link {:class (if disabled? "disabled" "") :on-click #(om/set-state! owner :confirm-delete? true)} :remove (t> _delete))])))))


(defmulti do-role-action (fn [action owner role] action))

(defmethod do-role-action ::delete
  [_ owner role]
  (dispatch :roles/delete (get role "keyId")))

(defmethod do-role-action ::show-edit-view
  [_ owner role]
  (toggle! owner :role-details-view?)
  (om/set-state! owner :current-role role))

(defmethod do-role-action ::show-create-view
  [_ owner role])

(defn user-role-count [user-auth-store role]
  (count (user-auth-store/get-by-role-id user-auth-store (get role "keyId"))))

(defn roles-and-permissions [{:keys [user_roles user-auth]} owner]
  (reify

    om/IInitState
    (init-state [this]
      {:role-details-view? false
       :current-role nil})

    om/IWillMount
    (will-mount [this]
      (dispatch :roles/fetch nil)
      (dispatch :projects/fetch nil))

    om/IRenderState
    (render-state [this {:keys [role-details-view? current-role]}]
      (html
       [:div.panels
        [:div.mypanel {:class (if role-details-view? "opened" "closed")}
         [:div.row.topMargin
          [:div.col-lg-3.col-md-3.col-sm-3]
          [:div.col-lg-2.col-md-2.col-sm-2]
          [:div.col-lg-2.col-lg-offset-5.col-md-2.col-md-offset-5.col-sm-2.col-sm-offset-5
           [:form.navbar-form.navbar-right
            (b/btn-primary {:on-click #(do (.preventDefault %)
                                           (toggle! owner :role-details-view?)
                                           (om/set-state! owner :current-role nil))}
                           :plus (t> _add_new_role))]]]
         (om/build grid
                   {:data (store/get-roles user_roles)
                    :columns [{:title (t> _role)
                               :cell-fn #(get % "name")}
                              {:title (t> _number_of_users)
                               :class "text-center"
                               :cell-fn #(user-role-count user-auth %)}
                              {:title (t> _permissions)
                               :cell-fn #(s/join ", " (map (partial get all-permissions) (get % "permissions")))}
                              {:title (t> _actions)
                               :class "text-center"
                               :component role-actions
                               :component-data-fn (fn [role]
                                                    {:disabled? (not (zero? (user-role-count user-auth role)))
                                                     :on-action (fn [action]
                                                                  (do-role-action action owner role))})}]})]
        (om/build role-details
                  {:open? role-details-view?
                   :on-save (fn [role]
                              (if (contains? role "keyId")
                                (dispatch :roles/edit role)
                                (dispatch :roles/create role))
                              (toggle! owner :role-details-view?))
                   :on-close #(toggle! owner :role-details-view?)
                   :role current-role})]))))
