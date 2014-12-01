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
             })

)

(defn dropdown [{:keys [placeholder selected choices on-select data label-component label-component-data-fn]} owner]
  (reify
    om/IInitState
    (init-state [this]
      {:id (name (gensym "dropdown"))})

    om/IRenderState
    (render-state [this {:keys [id]}]
      (println "Rendering dropdown")
      (let [label-component-data-fn (or label-component-data-fn identity)]
        (html
         [:div.dropdown
          [:button.btn.btn-default.dropdown-toggle {:type "button" :id id :data-toggle "dropdown" :aria-expanded "true"}
           (if selected
             (om/build label-component (label-component-data-fn selected))
             placeholder)
           " "
           (caret)]
          [:ul.dropdown-menu {:role "menu" :aria-labelledby id}
           (for [item data]
             [:li {:role "presentation"}
              [:a {:role "menuitem"
                   :tab-index "-1"
                   :href "#"
                   :on-click #(do (om/set-state! owner :selected item)
                                  (on-select item))}
               (om/build label-component (label-component-data-fn item))]])]])))))


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
