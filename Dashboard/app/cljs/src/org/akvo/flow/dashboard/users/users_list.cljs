(ns org.akvo.flow.dashboard.users.users-list
  (:require [clojure.string :as s]
            [org.akvo.flow.dashboard.dispatcher :refer (dispatch)]
            [org.akvo.flow.dashboard.components.dialog :refer (dialog)]
            [org.akvo.flow.dashboard.components.grid :refer (grid)]
            [org.akvo.flow.dashboard.components.bootstrap :as b]
            [org.akvo.flow.dashboard.ajax-helpers :refer (default-ajax-config)]
            [org.akvo.flow.dashboard.users.store :as store]
            [org.akvo.flow.dashboard.app-state :refer (app-state)]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros (html)]
            [ajax.core :refer (ajax-request GET POST PUT DELETE)]))

(defn target-value [evt]
  (-> evt .-target .-value))

(defn user-form [{:keys [user on-change]} owner]
  (om/component
   (html
    [:div
     [:label "Username:"]
     [:input {:type "text" :size "40" :value (get user "userName")
              :on-change #(on-change "userName" (target-value %))}]
     [:br]
     [:label "Email address:"]
     [:input {:type "text" :size "40" :value (get user "emailAddress")
              :on-change #(on-change "emailAddress" (target-value %))}]
     [:br]
     [:label "Select permission level"]
     [:select {:value (get user "permissionList")
               :on-change #(on-change "permissionList" (target-value %))}
      [:option {:value "10"} "Admin"]
      [:option {:value "20"} "User"]]])))

(defn user-dialog [{:keys [user close! tag]} owner]
  (reify
    om/IInitState
    (init-state [this]
      {:user user})
    om/IWillReceiveProps
    (will-receive-props [this {:keys [user]}]
      (om/set-state! owner :user user))
    om/IRenderState
    (render-state [this state]
      (om/build dialog
                {:title "Add new user"
                 :text "Please provide a user name, email address and permission level below."
                 :content user-form
                 :content-data {:user (:user state)
                                :on-change (fn [key val] (om/set-state! owner [:user key] val))}
                 :buttons [{:caption "Save"
                            :action #(do (dispatch tag (:user state))
                                         (close!))}
                           {:caption "Cancel"
                            :action close!}]}))))

(def empty-user
  {"admin" false
   "logoutUrl" nil
   "config" nil
   "emailAddress" ""
   "superAdmin" false
   "permissionList" "20"
   "userName" ""
   "keyId" nil})

(defn new-user-dialog [{:keys [close!]} owner]
  (om/component
   (om/build user-dialog {:close! close!
                          :tag :new-user
                          :user empty-user})))

(defn edit-user-dialog [{:keys [user close!]} owner]
  (om/component
   (om/build user-dialog {:close! close!
                          :tag :edit-user
                          :user user})))

(defn delete-user-dialog [{:keys [user close!]} owner]
  (om/component
   (om/build dialog
             {:title "Are you sure you want to delete this user?"
              :text "This can not be undone!"
              :buttons [{:caption "Ok"
                         :action #(do (dispatch :delete-user user)
                                      (close!))}
                        {:caption "Cancel"
                         :action close!}]})))

(defn generate-apikeys [owner user]
  (POST (str "/rest/users/" (get user "keyId") "/apikeys")
        (merge default-ajax-config
               {:handler (fn [response]
                           (let [access-key (get-in response ["apikeys" "accessKey"])
                                 secret (get-in response ["apikeys" "secret"])]
                             (om/set-state! owner {:access-key access-key
                                                   :secret secret})
                             (dispatch :new-access-key {:access-key access-key
                                                        :user user})))})))

(defn revoke-apikeys [owner user]
  (DELETE (str "/rest/users/" (get user "keyId") "/apikeys")
          (merge default-ajax-config
                 {:handler (fn [response]
                             (om/set-state! owner {:access-key nil :secret nil})
                             (dispatch :new-access-key {:access-key nil :user user}))})))

(defn manage-apikeys [{:keys [user secret access-key owner]} _]
  (om/component
   (html [:div
          [:label "Access key:"]
          [:input {:type "text" :size 40 :value access-key}]
          (when secret
            [:div
             [:label "Secret:"]
             [:input {:type "text" :size 40 :value secret}]
             [:p "The secret key will never be shown again! If it is lost a new one must be generated"]])
          [:a {:on-click #(generate-apikeys owner user)} "(Re)generate"]
          " "
          [:a {:on-click #(revoke-apikeys owner user)} "Revoke"]])))

(defn manage-apikeys-dialog [{:keys [user close!]} owner]
  (reify
    om/IInitState
    (init-state [this]
      {:secret nil
       :access-key (get user "accessKey")})
    om/IRenderState
    (render-state [this {:keys [secret access-key]}]
      (om/build dialog
                {:title "Manage API keys"
                 :text "You can (re)generate or revoke an api key for this user"
                 :content-data {:user user
                                :secret secret
                                :access-key access-key
                                :owner owner}
                 :content manage-apikeys
                 :buttons [{:caption "Close"
                            :action close!}]}))))

(defn edit-user [{:keys [user close!]} owner]
  (om/component
   (html
    [:div
     [:div.row.panelHeader
      [:div.col-xs-9.text-left.panelTitle
       [:h4 (b/icon :pencil)
        [:span.userName (get user "userName")] " Roles and Permissions"]]
      [:div.col-xs-3.text-right
       (b/btn-primary {} (b/icon :floppy-disk) " Save")
       (b/btn-link {:on-click close!}  (b/icon :remove) " Cancel")]]
     [:form.form-inline.text-left.paddingTop.roleEditSelect {:role "form"}
      [:div.form-group
       (om/build b/dropdown {:id "chooseRole"
                             :caption "Select Role"
                             :choices ["Admin" "Project Editor" "Translator" "User"]
                             :on-select #(println %1 %2)})]
      [:div.form-group
       (om/build b/dropdown {:id "chooseAssignment"
                             :caption "Select Assignment"
                             :choices ["All" "Africa" "Asia" "South America" "Burundi population survey"]
                             :on-select #(println %1 %2)})]
      [:div.form-group
       (b/btn-primary {:class "btn-xs"} (b/icon :plus) " Add")]]])))

(defn user-actions [{:keys [user on-action]} owner]
  (om/component
   (html
    [:span
     [:a {:on-click #(on-action edit-user user)} "Edit"]
     [:a {:on-click #(on-action delete-user-dialog user)} "Remove"]
     [:a {:on-click #(on-action manage-apikeys-dialog user)} "api"]])))

(defn columns [owner]
  (let [on-action (fn [component user]
                    (om/set-state! owner :dialog {:component component :user-id (get user "keyId")}))]
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

(defn users [{:keys [users]} owner]
  (reify
    om/IInitState
    (init-state [this]
      {:pagination {:offset 0
                    :limit 20}
       :sort {:sort-by "emailAddress"
              :sort-order "ascending"}
       :dialog nil})

    om/IDidMount
    (did-mount [this]
      (dispatch :fetch-users nil))

    om/IRenderState
    (render-state [this state]
      (html
       [:div.topNav-spacer.panels
        [:div.mypanel {:class (if (:dialog state) "opened" "closed")}
         [:div.topMargin
          [:form.form-inline.text-left.paddingTop {:role "form"}
           [:div.form-group.pull-right
            (b/btn-primary {:class "btn-md"
                            :type "button"
                            :on-click #(om/set-state! owner :dialog {:component new-user-dialog})}
                           (b/icon :plus) " Add new user")]]
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
        [:div.mypanel {:class (if (:dialog state) "opened" "closed")}
         (let [dialog (or (:dialog state) {:component new-user-dialog})
               {:keys [component user-id]} dialog]
           [:div
            [:hr]
            (om/build component {:user (if user-id (store/get-user users user-id))
                                 :close! #(om/set-state! owner :dialog nil)})
            [:hr]])]]))))
