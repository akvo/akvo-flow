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

(defn btn
  ([button-type attrs icn]
     (btn button-type attrs icn ""))
  ([button-type attrs icn caption]
     {:pre [(string? button-type)
            (map? attrs)
            (keyword? icn)
            (string? caption)]}
     [:button.btn
      (update-in attrs [:class] (fn [classes]
                                  (if classes
                                    (str classes " " button-type)
                                    button-type)))
      (icon icn) (when-not (empty? caption)
                   (str " " caption))]))

(defn btn-primary
  ([attrs icn]
     (btn-primary attrs icn ""))
  ([attrs icn caption]
     (btn "btn-primary" attrs icn caption)))

(defn btn-link
  ([attrs icn]
     (btn-link attrs icn ""))
  ([attrs icn caption]
     (btn "btn-link" attrs icn caption)))


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
