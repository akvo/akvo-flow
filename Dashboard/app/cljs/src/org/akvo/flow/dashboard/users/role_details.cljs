(ns org.akvo.flow.dashboard.users.role-details
  (:require [clojure.string :as s]
            [clojure.set :as set]
            [org.akvo.flow.dashboard.dispatcher :refer (dispatch)]
            [org.akvo.flow.dashboard.components.grid :refer (grid)]
            [org.akvo.flow.dashboard.components.bootstrap :as b]
            [org.akvo.flow.dashboard.users.store :as store]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros (html)]))

(def all-permissions
  {"PROJECT_FOLDER_CREATE" "Create folders"
   "PROJECT_FOLDER_READ" "Access folders"
   "PROJECT_FOLDER_UPDATE" "Edit folders"
   "PROJECT_FOLDER_DELETE" "Delete folders"
   "FORM_CREATE" "Create forms"
   "FORM_READ" "Access forms"
   "FORM_UPDATE" "Edit forms"
   "FORM_DELETE" "Delete forms"})

(defn header-section [{:keys [role on-close]} owner]
  (om/component
   (html
    [:div.row.panelHeader
     [:div.col-xs-9.text-left.panelTitle
      [:h4 (b/icon :pencil) " Edit " [:span.userRole (get role "name")]]]
     [:div.col-xs-3.text-right
      (b/btn-primary {:on-click on-close} :arrow-left "Go back")]])))

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

(defn role-edit-section [{:keys [role on-save]} owner]
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
        [:h2 "List of permissions:"]
        [:form.paddingTop.roleEditSelect {:role "form"}
         [:div.form-group
          [:label.control-label.text-left {:for "userRoleName"} "Role name"]
          [:input.form-control {:name "userRoleName" :value name
                                :on-change #(om/set-state! owner "name" (-> % .-target .-value))}]]
         [:div.topMargin.permissionList
          (for [[value caption] all-permissions]
            (checkbox owner value caption (contains? (set permissions) value)))]
         [:div.form-group
          (b/btn-primary {:on-click #(do (.preventDefault %)
                                         (on-save role))}
                         :floppy-disk "Save permissions set")
          (b/btn-link {:class "cancelAction" :on-click #(.preventDefault %)} :remove "Cancel")]]]))))

(defn role-details [{:keys [role open? on-save on-close]} owner]
  (reify
    om/IRender
    (render [this]
      (html
       [:div.mypanel {:id "panel1"
                      :class (if open? "opened" "closed")}
        (om/build header-section {:role role :on-close on-close})
        (om/build role-edit-section {:role role :on-save on-save})]))))
