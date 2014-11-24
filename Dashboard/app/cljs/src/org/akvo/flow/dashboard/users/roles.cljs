(ns org.akvo.flow.dashboard.users.roles
  (:require [clojure.string :as s]
            [org.akvo.flow.dashboard.dispatcher :refer (dispatch)]
            [org.akvo.flow.dashboard.components.grid :refer (grid)]
            [org.akvo.flow.dashboard.components.bootstrap :as b]
            [org.akvo.flow.dashboard.users.store :as store]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros (html)]))

(def permissions
  {:folder {:create "PROJECT_FOLDER_CREATE"
            :read "PROJECT_FOLDER_READ"
            :update "PROJECT_FOLDER_UPDATE"
            :delete "PROJECT_FOLDER_DELETE"}
   :form {:create "FORM_CREATE"
          :read "FORM_READ"
          :update "FORM_UPDATE"
          :delete "FORM_DELETE"}})

(defn roles-and-permissions [{:keys [roles]} owner]
  (reify

    om/IInitState
    (init-state [this])

    om/IWillMount
    (will-mount [this]
      (dispatch :roles/fetch nil)
      (dispatch :projects/fetch nil))

    om/IRenderState
    (render-state [this state]
      (html
       [:div
        [:div.row.topMargin
         [:div.col-lg-3.col-md-3.col-sm-3]
         [:div.col-lg-2.col-md-2.col-sm-2]
         [:div.col-lg-2.col-lg-offset-5.col-md-2.col-md-offset-5.col-sm-2.col-sm-offset-5
          [:form.navbar-form.navbar-right
           (b/btn-primary {:on-click #(do (.preventDefault %)
                                          (dispatch :roles/create nil))}
                          :plus "Add new role!")]]]
        (om/build grid
                  {:data (store/get-roles roles)
                   :columns [{:title "Role"
                              :cell-fn #(get % "name")}
                             {:title "Number of users"
                              :cell-fn (constantly 0)}
                             {:title "Action"
                              :cell-fn (constantly "")}]})]))))
