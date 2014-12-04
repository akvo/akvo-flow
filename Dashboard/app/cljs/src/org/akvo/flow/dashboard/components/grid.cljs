(ns ^{:doc "Reusable grid component"}
  org.akvo.flow.dashboard.components.grid
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros (html)]))

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
            {;; The id of the grid
             :id "deviceDataTable"
             ;; The data to be rendered
             :data the-data

             ;; On sort callback. Will be called with :sort-id and :sort-order
             :on-sort callback
             :sort {:sort-by "username"
                    ;; Sort order must be either "ascending" or "descending"
                    :sort-order "ascending"}

             ;; on-range will be called with :offset and :limit
             :on-range callback
             :range {:offset 100
                     :limit 20}

             ;; Function to generate a react-key. Will be called with a record from the data
             :key-fn :id

             ;; Description of the columns
             :columns [{:title "Id"
                        ;; (optional) class name to add to the <td>
                        :class "some-class"
                        ;; A function that returns the cell value. This function can return a
                        ;; * simple value: string/number/nil etc.
                        ;; * a sablono vector
                        :cell-fn :username
                        ;; or
                        :component some-component
                        ;; (om/build component (component-data-fn row))
                        :component-data-fn identity
                        ;; Make the column sortable by adding a :sort-by
                        :sort-by "username"
                        }
                       {... ...}]})
  )


(defn pagination-controls [{:keys [range on-range]} owner]
  (om/component
   (let [offset (or (:offset range) 0)
         limit (or (:limit range) 20)]
     (html
      [:div
       [:nav
        [:ul.pager
         [:li {:role "presentation"}
          [:a {:on-click #(on-range (let [new-offset (- offset limit)]
                                      (if (neg? new-offset) 0 new-offset))
                                    limit)}
           "« previous"]]
         [:li [:strong (str " " (inc offset) " - " (+ offset limit) " ")]]
         [:li
          [:a {:on-click #(on-range (+ offset limit) limit)}
             "next »"]]]]]))))

(defn change-direction [dir]
  (if (= dir "ascending")
    "descending"
    "ascending"))

(defn table-head [{:keys [columns sort on-sort] :as data} owner]
  (om/component
   (let [current-sort-by (:sort-by sort)
         current-sort-order (:sort-order sort)]
     (html
      [:tr {:class "tabHeader"}
       (->> columns
            (map-indexed
             (fn [idx {:keys [title sort-by]}]
               [:th {:key (str "col_" idx) ;; TODO could also be derived from e.g. title
                     :class (if sort-by
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
     (map-indexed (fn [idx {:keys [class cell-fn component component-data-fn title]}]
                    (let [class (or class "")
                          component-data-fn (or component-data-fn identity)
                          item  (cond
                                 cell-fn (cell-fn row)
                                 component (let [data (component-data-fn row)]
                                             (om/build component data)))]
                      [:td {:class class
                            :key (str "col_" idx)} item]))
                  columns)])))

(defn grid [data owner]
  (om/component
   (html
    [:div
     [:div {:class "table-responsive"}
      [:table {:id (:id data) :class "table table-striped dataTable"}
      [:thead (om/build table-head (select-keys data [:columns :sort :on-sort]))]
      [:tbody
       (map (fn [row columns]
              (om/build table-row {:row row :columns columns} (if-let [key-fn (:key-fn data)]
                                                                {:react-key (str "k" (key-fn row))})))
            (:data data)
            (repeat (:columns data)))]]]
     (when (:on-range data)
       (om/build pagination-controls (select-keys data [:range :on-range])))])))
