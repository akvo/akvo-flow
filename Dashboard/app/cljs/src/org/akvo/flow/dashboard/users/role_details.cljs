;; Copyright (C) 2014, 2017 Stichting Akvo (Akvo Foundation)
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

(ns org.akvo.flow.dashboard.users.role-details
  (:require [clojure.string :as s]
            [clojure.set :as set]
            [org.akvo.flow.dashboard.dispatcher :refer (dispatch)]
            [org.akvo.flow.dashboard.components.grid :refer (grid)]
            [org.akvo.flow.dashboard.components.bootstrap :as b]
            [org.akvo.flow.dashboard.users.store :as store]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros (html)])
  (:require-macros [org.akvo.flow.dashboard.t :refer (t>)]))

(def required-permissions
   {"PROJECT_FOLDER_CREATE" (t> _create_folders)
    "PROJECT_FOLDER_READ" (t> _access_folders)
    "PROJECT_FOLDER_UPDATE" (t> _edit_folders)
    "PROJECT_FOLDER_DELETE" (t> _delete_folders)
    "FORM_CREATE" (t> _create_forms)
    "FORM_READ" (t> _access_forms)
    "FORM_UPDATE" (t> _edit_forms)
    "FORM_DELETE" (t> _delete_forms)
    "DATA_CLEANING" (t> _data_cleaning)
    "DATA_READ" (t> _read_data)
    "DATA_UPDATE" (t> _edit_data)
    "DATA_DELETE" (t> _delete_data)
    "DEVICE_MANAGE" (t> _device_assignment_manage)
    "CASCADE_MANAGE" (t> _cascade_manage)})

(def data-approval-enabled
  (aget js/window "parent" "FLOW" "Env" "enableDataApproval"))

(def all-permissions
  (if data-approval-enabled
    (assoc required-permissions "DATA_APPROVE_MANAGE" (t> _data_approval_manage))
    required-permissions))

(defn header-section [{:keys [role on-close]} owner]
  (om/component
   (html
    [:div.row.panelHeader
     [:div.col-xs-9.text-left.panelTitle
      [:h4 (b/icon :pencil) " " (t> _edit) " " [:span.userRole (get role "name")]]]
     [:div.col-xs-3.text-right
      (b/btn-primary {:on-click on-close} :arrow-left (t> _go_back))]])))

(defn checkbox [owner value caption checked?]
  [:div.checkbox
   [:label
    [:input {:type "checkbox" :value value :checked checked?
             :on-change #(let [permissions (set (om/get-state owner "permissions"))
                               value (-> % .-target .-value)]
                            (if (contains? permissions value)
                              (om/set-state! owner "permissions" (disj permissions value))
                              (om/set-state! owner "permissions" (conj permissions value))))}]
    caption]])

(defn role-edit-section [{:keys [role on-save on-close]} owner]
  (reify
    om/IInitState
    (init-state [this]
      (or role {"name" ""
                "permissions" []}))

    om/IWillReceiveProps
    (will-receive-props [this {:keys [role]}]
      (om/set-state! owner (or role {"name" "" "permissions" []})))

    om/IRenderState
    (render-state [this {:strs [name permissions] :as role}]
      (html
       [:div.roleEditSection.topMargin
        [:h2 (t> _list_of_permissions) ":"]
        [:form.paddingTop.roleEditSelect {:role "form"}
         [:div.form-group
          [:label.control-label.text-left {:for "userRoleName"} (t> _role_name)]
          [:input.form-control {:name "userRoleName" :value name
                                :on-change #(om/set-state! owner "name" (-> % .-target .-value))}]]
         [:div.topMargin.permissionList
          (for [[value caption] (sort-by first all-permissions)]
            (checkbox owner value caption (contains? (set permissions) value)))]
         [:div.form-group
          (b/btn-primary {:class (if (empty? name) "disabled")
                          :on-click #(do (.preventDefault %)
                                         (on-save role))}
                         :floppy-disk (t> _save_permissions_set))
          (b/btn-link {:class "cancelAction"
                       :on-click #(do (.preventDefault %) (on-close))}
                      :remove (t> _cancel))]]]))))

(defn role-details [{:keys [role open? on-save on-close]} owner]
  (reify
    om/IRender
    (render [this]
      (html
       [:div.mypanel {:class (if open? "opened" "closed")}
        (om/build header-section {:role role :on-close on-close})
        (om/build role-edit-section {:role role :on-save on-save :on-close on-close})]))))
