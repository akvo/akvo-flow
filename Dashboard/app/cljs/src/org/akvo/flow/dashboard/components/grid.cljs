(ns ^{:doc "Reusable grid component"}
  org.akvo.flow.dashboard.components.grid
  (:require [cljs.core.async :as async]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros (html)])
  (:require-macros [cljs.core.async.macros :refer (go-loop)]))

(comment

  ;; Example

  (def the-data
    [{:user-id 134321
      :username "abc"
      :email "abc@gmail.com"}
     {:user-id 999999
      :username "xyz"
      :email "zyx@gmail.com"}])

  (om/build grid
            {;; The id of this table
             :id "deviceDataTable"
             ;; The data to be rendered
             :data the-data

             ;; On sort callback. Will be called with :sort-id and :sort-order
             :on-sort callback
             :sort {:sort-by "username"
                    :sort-order "ascending"}

             ;; on-range will be called with :offset and :limit
             :on-range callback
             :range {:offset 100
                     :limit 20}

             ;; Description of the columns
             :columns [{:title "Id"
                        :cell-fn :user-id}
                       {;; The title of this column
                        :title "Username"
                        ;; Make the column sortable by adding a :sort-by
                        :sort-by "username"

                        ;; A function that returns the cell value. This function can return a
                        ;; * simple value: string/number/nil etc.
                        ;; * a sablono vector
                        ;; * an om/react component
                        :cell-fn :username
                        ;; (optional) class name to add to the <td>
                        :class "some-class"
                        }
                       {:title "Actions"
                        :cell-fn edit-and-delete-component}]}))

(defn pagination-controls [{:keys [range on-range]} owner]
  (om/component
   (let [offset (or (:offset range) 0)
         limit (or (:limit range) 20)]
     (html
      [:div
       [:span "Show:"
        (if (= limit 10) " 10" [:a {:on-click #(on-range offset 10)} " 10"])
        (if (= limit 20) " 20" [:a {:on-click #(on-range offset 20)} " 20"])
        (if (= limit 50) " 50" [:a {:on-click #(on-range offset 50)} " 50"])]
       [:span
        (if (zero? offset)
          "«previous"
          [:a {:on-click #(on-range (let [new-offset (- offset limit)]
                                      (if (neg? new-offset) 0 new-offset))
                                    limit)}
           "«previous"])
        (str " " (inc offset) " - " (+ offset limit) " ")
        [:a {:on-click #(on-range (+ offset limit) limit)}
         "next»"]]]))))

(defn change-direction [dir]
  (if (= dir "ascending")
    "descending"
    "ascending"))

(defn table-head [{:keys [columns sort on-sort] :as data} owner]
  (om/component
   (let [current-sort-by (:sort-by sort)
         current-sort-order (:sort-order sort)]
     (html
      [:tr
       (->> columns
            (map-indexed
             (fn [idx {:keys [title sort-by]}]
               [:th {:class (if sort-by
                              (if (= current-sort-by sort-by)
                                (if (= current-sort-order "ascending")
                                  "sorting_asc"
                                  "sorting_desc")
                                "")
                              "noArrows")}
                [:a (when sort-by
                      {:on-click #(on-sort sort-by
                                           (if (= current-sort-by sort-by)
                                             (change-direction current-sort-order)
                                             "ascending"))})
                 (if (fn? title)
                   (om/build title {})
                   title)]])))]))))

(defn table-row [{:keys [row columns]} owner]
  (om/component
   (html
    [:tr
     (for [col columns]
       (let [class (:class col)
             item ((:cell-fn col) row)]
         [:td {:class (if class class "")}
          (if (fn? item)
            (om/build item {})
            item)]))])))

(defn grid [data owner]
  (om/component
   (html
    [:div {}
     (when (:on-range data)
       (om/build pagination-controls (select-keys data [:range :on-range])))
     [:table {:id (:id data)}
      [:thead (om/build table-head (select-keys data [:columns :sort :on-sort]))]
      [:tbody (om/build-all table-row
                            (map (fn [row columns]
                                   {:row row
                                    :columns columns})
                                 (:data data)
                                 (repeat (:columns data))))]]])))
