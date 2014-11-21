(ns org.akvo.flow.dashboard.users.user-details
  (:require [org.akvo.flow.dashboard.components.bootstrap :as b]
            [org.akvo.flow.dashboard.dispatcher :refer (dispatch)]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros (html)]))

(defn panel-header [{:keys [on-save]} owner])

(defn update-input! [owner key]
  (fn [event]
    (om/set-state! owner key (-> event .-target .-value))))

(defn user-edit-section [{:keys [on-save user]} owner]
  (reify
    om/IInitState
    (init-state [this] user)

    om/IWillReceiveProps
    (will-receive-props [this {:keys [user]}]
      (om/set-state! owner user))

    om/IRenderState
    (render-state [this {:strs [userName emailAddress] :as state}]
      (html
       [:div.userEditSection
        [:h2 "User info:"]
        [:form
         [:div.form-group
          [:label.control-label.text-left {:for "username"} "Name"]
          [:input.form-control {:value userName
                                :placeholder "Enter full name"
                                :on-change (update-input! owner "userName")}]]
         [:div.form-group
          [:label.control-label.text-left {:for "email"} "Email"]
          [:input.form-control {:value emailAddress
                                :placeholder "example@gmail.com"
                                :on-change (update-input! owner "emailAddress")}]]
         [:div.form-group
          (b/btn-primary {:class (when (= state user) "disabled")
                          :on-click #(on-save state)}
                         (b/icon :floppy-disk) " Save user info")]]]))))

#_(defn roles-and-permissions-section [{:keys [user projects roles]} owner]
  (om/component
   (html
    [:div.userRolesPerm
     [:h2 "Roles and permissions:"]
     [:form.form-inline.text-left.paddingTop.roleEditSelect {:role "form"}
      [:div.form-group
       (om/build b/dropdown {:data-source (roles/get-roles roles)
                             :placeholder "Select role"
                             :on-change #(println "selected" %)})]
      [:div.form-group
       (om/build b/dropdown {:data-source (projects/get-projects {:parent nil})
                             :placeholder "Select project"
                             :on-change #(println "selected" %)})]
      [:div.form-group
       (b/btn-primary {:on-click #(println "submit!")}
                      (b/icon :plus)
                      " Add")]]
     (om/build grid
               {})])))

(defn api-keys [{:keys [user]} owner])

(defn user-details [{:keys [user projects]} owner]
  (om/component
   (html
    [:div
     #_(om/build panel-header {:user user})
     (om/build user-edit-section {:user user
                                  :on-save #(dispatch :edit-user %)})
     #_(om/build roles-and-permissions-section {:user user :projects projects})
     #_(om/build api-keys {:user user})])))
