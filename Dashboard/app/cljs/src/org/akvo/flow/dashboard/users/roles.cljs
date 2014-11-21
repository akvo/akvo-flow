(ns org.akvo.flow.dashboard.users.roles
  (:require [clojure.string :as s]
            [org.akvo.flow.dashboard.dispatcher :refer (dispatch)]
            [org.akvo.flow.dashboard.components.dialog :refer (dialog)]
            [org.akvo.flow.dashboard.components.grid :refer (grid)]
            [org.akvo.flow.dashboard.ajax-helpers :refer (default-ajax-config)]
            [org.akvo.flow.dashboard.users.store :as store]
            [org.akvo.flow.dashboard.app-state :refer (app-state)]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros (html)]
            [ajax.core :refer (ajax-request GET POST PUT DELETE)]))

(defn roles-and-permissions [{:keys [users]} owner]
  (reify

    om/IInitState
    (init-state [this]
      {:panel :grid})

    om/IWillMount
    (will-mount [this]
      (dispatch :roles/fetch nil)
      (dispatch :projects/fetch nil))

    om/IRenderState
    (render-state [this state]
      (html
       [:div
        [:div {:class "row topMargin"}
         [:div {:class "col-lg-3 col-md-3 col-sm-3"}]
         [:div {:class "col-lg-2 col-md-2 col-sm-2"}]
         [:div {:class "col-lg-2 col-lg-offset-5 col-md-2 col-md-offset-5 col-sm-2 col-sm-offset-5"}
          [:form {:class "navbar-form navbar-right"}
           [:button {:type "button" :class "btn btn-primary btn-md"
                     :on-click #(om/set-state! owner :panel :create-role)}
            [:span {:class "glyphicon glyphicon-plus"}]
            "Add new role"]]]]

        (om/build grid
                  {:data nil
                   :columns [{:title "Role"}
                             {:title "Number of users"}
                             {:title "Action"}]})]))))
