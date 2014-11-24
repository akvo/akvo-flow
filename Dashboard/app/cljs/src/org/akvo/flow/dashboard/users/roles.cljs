(ns org.akvo.flow.dashboard.users.roles
  (:require [clojure.string :as s]
            [clojure.set :as set]
            [org.akvo.flow.dashboard.dispatcher :refer (dispatch)]
            [org.akvo.flow.dashboard.components.grid :refer (grid)]
            [org.akvo.flow.dashboard.components.bootstrap :as b]
            [org.akvo.flow.dashboard.users.store :as store]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros (html)]))

(def all-permissions
  #{"PROJECT_FOLDER_CREATE"
    "PROJECT_FOLDER_READ"
    "PROJECT_FOLDER_UPDATE"
    "PROJECT_FOLDER_DELETE"
    "FORM_CREATE"
    "FORM_READ"
    "FORM_UPDATE"
    "FORM_DELETE"})

(defn target-value [evt]
  (-> evt .-target .-value))

(defn set-input! [owner korks]
  (fn [evt]
    (om/set-state! owner korks (target-value evt))))

(defn update-input! [owner korks f & args]
  (fn [evt]
    (let [old-state (om/get-state owner korks)]
      (om/set-state! owner korks (apply f old-state (target-value evt) args)))))

(defn create-new-form-view [{:keys [on-save]} owner]
  (reify
    om/IInitState
    (init-state [this]
      {:permissions #{}
       :role-name ""})

    om/IRenderState
    (render-state [this {:keys [permissions role-name]}]
      (html
       [:div
        [:input {:value role-name :on-change (set-input! owner :role-name)}]
        (for [permission permissions]
          [:span permission])
        [:select {:on-change (update-input! owner :permissions conj)}
         (for [permission (set/difference all-permissions permissions)]
           [:option {:value permission} permission])]
        (b/btn-primary {:on-click #(on-save {"name" role-name
                                             "permissions" permissions})}
                       :save "Save")]))))

(defn roles-and-permissions [{:keys [roles]} owner]
  (reify

    om/IInitState
    (init-state [this]
      {:create-role-view? false})

    om/IWillMount
    (will-mount [this]
      (dispatch :roles/fetch nil)
      (dispatch :projects/fetch nil))

    om/IRenderState
    (render-state [this {:keys [create-role-view?]}]
      (html
       [:div
        [:div.row.topMargin
         [:div.col-lg-3.col-md-3.col-sm-3]
         [:div.col-lg-2.col-md-2.col-sm-2]
         [:div.col-lg-2.col-lg-offset-5.col-md-2.col-md-offset-5.col-sm-2.col-sm-offset-5
          [:form.navbar-form.navbar-right
           (b/btn-primary {:on-click #(do (.preventDefault %)
                                          (om/set-state! owner :create-role-view? true))}
                          :plus "Add new role!")]]]
        (when create-role-view?
          (om/build create-new-form-view
                    {:on-save #(do (om/set-state! owner :create-role-view? false)
                                   (dispatch :roles/create %))}))
        (om/build grid
                  {:data (store/get-roles roles)
                   :columns [{:title "Role"
                              :cell-fn #(get % "name")}
                             {:title "Number of users"
                              :cell-fn (constantly 0)}
                             {:title "Action"
                              :cell-fn (constantly "")}]})]))))
