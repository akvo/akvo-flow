(ns org.akvo.flow.dashboard.t)

(defmacro t> [key]
  `(let [locale# (-> js/window
                     (aget "parent")
                     (aget "localStorage")
                     (aget "locale")
                     .toUpperCase)
         k# ~(name key)
         s# (-> js/window
                (aget "parent")
                (aget "Ember")
                (aget (cljs.core/str "STRINGS" "_" locale#))
                (aget k#))]
     (if s#
       s#
       k#)))
