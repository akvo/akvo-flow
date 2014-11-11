(ns ^{:doc "Reusable dialog component"}
  org.akvo.flow.dashboard.components.dialog
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros (html)]))


(comment

  ;; Example usage
  (om/build dialog {:title "Add user"
                    :text "Fill in the form below to create a new user"
                    ;; Content is optional
                    :content form-component
                    :content-data data-for-component
                    :buttons [{:caption "Create"
                               :class "ok smallBtn"
                               :action #(some action)}
                              {:caption "Cancel"
                               :action #(some action)}]})
)

(defn button-item [data owner]
  (let [{:keys [caption class action]} data]
    (om/component
     (html
      [:li [:a {:class class
                :on-click action}
            caption]]))))

(defn dialog [data owner]
  (let [{:keys [title text content buttons content-data]} data]
    (om/component
     (html
      [:div.overlay.display
       [:div.blanket]
       [:div.dialogWrap
        [:div.confirmDialog.dialog
         [:h2 title]
         [:p.dialogMsg text]
         (if content (om/build content content-data) [:div])
         [:div.buttons.menuCentre
          (apply dom/ul nil
                 (om/build-all button-item buttons))]]]]))))
