;; Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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

(ns ^{:doc "Bootstrap helpers and components"}
  org.akvo.flow.dashboard.components.bootstrap
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros (html)]))

(defn icon [n]
  {:pre [(keyword? n)]}
  [:span {:class (str "glyphicon glyphicon-" (name n))}])

(defn fa-icon [n]
  {:pre [(keyword? n)]}
  [:span {:class (str "fa fa-" (name n))}])

(defn caret []
  [:span.caret])

(defn btn
  ([button-type attrs caption]
     (btn button-type attrs ::no-icon caption))
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
      (when-not (= icn ::no-icon)
        (icon icn))
      (str (if-not (= icn ::no-icon) " ") caption)]))

(defn btn-primary
  ([attrs caption]
     (btn-primary attrs ::no-icon caption))
  ([attrs icn caption]
     (btn "btn-primary" attrs icn caption)))

(defn btn-link
  ([attrs caption]
     (btn-link attrs ::no-icon caption))
  ([attrs icn caption]
     (btn "btn-link" attrs icn caption)))


(comment
  usage for dropdown component:

  (om/build dropdown
            {;; What to show if nothing is selected
             :placeholder "Select something"
             ;; Data
             :data [{... ...} {... ...}]
             ;; Component to use as labels
             ;; will be built
             ;; (om/build label-component (label-component-data-fn {... ...})
             :label-component <some-component>
             ;; defaults to identity
             :label-component-data-fn <some-fn>

             :on-change <callback>
             })

)

(defn dropdown [{:keys [placeholder data selected label label-fn label-data-fn on-select]} owner]
  (reify
    om/IInitState
    (init-state [this]
      {:id (name (gensym "__dropdown_"))})
    om/IRenderState
    (render-state [this {:keys [id]}]
      (let [label-data-fn (or label-data-fn identity)]
        (html
         [:div.dropdown
          [:button.btn.btn-default.dropdown-toggle {:type "button" :id id :data-toggle "dropdown" :aria-expanded "true"}
           (if selected
             (if label-fn
               (label-fn selected)
               (om/build label (label-data-fn selected)))
             placeholder)
           " "
           (caret)]
          [:ul.dropdown-menu {:role "menu" :aria-labelledby id}
           (for [item data]
             [:li {:role "presentation"}
              [:a {:role "menuitem"
                   :tab-index "-1"
                   ;;  :href "#"
                   :on-click #(on-select item)}
               (if label-fn
                 (label-fn item)
                 (om/build label (label-data-fn item)))]])]])))))


(defn select [{:keys [placeholder data selected label-fn key-fn on-select]} owner]
  (om/component
   (html
    [:select.form-control
     {:on-change (fn [evt]
                   (let [key (-> evt .-target .-value)
                         sel (some (fn [record]
                                     (if (= key (key-fn record))
                                       record))
                                   data)]
                     (assert sel (str "No such key:" key))
                     (on-select sel)))
      :value (if selected
               (key-fn selected)
               "__placeholder")}
     (when (and placeholder (not selected))
       [:option {:disabled true :value "__placeholder"} placeholder])
     (map (fn [record]
            (let [key (key-fn record)]
              (assert (string? key) "key-fn must return a string")
              [:option {:value key} (label-fn record)]))
          data)])))
