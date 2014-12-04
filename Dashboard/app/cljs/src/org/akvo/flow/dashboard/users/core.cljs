(ns org.akvo.flow.dashboard.users.core
  (:require [org.akvo.flow.dashboard.dispatcher :refer (dispatch)]
            [org.akvo.flow.dashboard.app-state :refer (app-state)]
            [org.akvo.flow.dashboard.users.users-list :refer (users)]
            [org.akvo.flow.dashboard.users.roles :refer (roles-and-permissions)]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros (html)])
  (:require-macros [org.akvo.flow.dashboard.t :refer (t>)]))

(enable-console-print!)

(defn tabs [{:keys [name]} owner]
  (om/component
   (html
    [:ul {:class "nav nav-tabs tabsBlock"}
     [:li (if (= name :users/users-list)
            {:class "active"})
      [:a {:on-click #(dispatch :navigate {:name :users/users-list
                                           :params nil})}
       (t> _dashboard_users)]]
     [:li (if (= name :users/roles-and-permissions)
            {:class "active"})
      [:a {:on-click #(dispatch :navigate {:name :users/roles-and-permissions
                                           :params nil})}
       (t> _roles_and_permissions)]]])))

(defn panel [{:keys [component component-data active?]} owner]
  (om/component
   (html
    [:div {:class (str "mypanel " (if active? "opened" "closed"))}
     (om/build component component-data)])))

(defn root-component [data owner]
  (om/component
   (html
    [:div.container-fluid
     [:section {:class "" :role "main"}
      (om/build tabs (:current-page data))
      (if (= :users/users-list (-> data :current-page :name))
        (om/build users data)
        (om/build roles-and-permissions data))]])))


(defn value-component [data owner {:keys [component]}]
  (reify om/IRender
    (render [this]
      (om/build component (om/value data)))))

(defn ^:export init []
  (om/root value-component
           app-state
           {:opts {:component root-component}
            :target (.getElementById js/document "app")}))
