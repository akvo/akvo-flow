(ns ^{:doc "Bootstrap helpers and components"}
  org.akvo.flow.dashboard.components.bootstrap
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros (html)]))

(defn icon [n]
  {:pre [(keyword? n)]}
  [:span {:class (str "glyphicon glyphicon-" (name n))}])

(defn caret []
  [:span.caret])

(defn dropdown [{:keys [id placeholder selected choices on-select]} owner]
  (reify
    om/IInitState
    (init-state [this]
      {:selected placeholder})

    om/IRenderState
    (render-state [this state]
      (html
      [:div.dropdown
       [:button.btn.btn-default.dropdown-toggle {:type "button" :id id :data-toggle "dropdown" :aria-expanded "true"}
        (:selected state) " " (caret)]
       [:ul.dropdown-menu {:role "menu" :aria-labelledby id}
        (for [choice choices]
          [:li {:role "presentation"}
           [:a {:role "menuitem"
                :tab-index "-1"
                :href "#"
                :on-click #(do (om/set-state! owner :selected (:label choice))
                               (on-select id (:id choice)))}
            (:label choice)]])]]))))

(defn btn-primary [attrs & children]
  {:pre [(map? attrs)]}
  [:button.btn.btn-primary attrs children])

(defn btn-link [attrs & children]
  {:pre [(map? attrs)]}
  [:button.btn.btn-link attrs children])
