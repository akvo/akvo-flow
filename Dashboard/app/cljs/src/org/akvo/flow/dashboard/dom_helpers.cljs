(ns org.akvo.flow.dashboard.dom-helpers
  (:import [goog.fx.dom Scroll]))

(defn scroll-to-top
  ([] (scroll-to-top 500))
  ([time]
     (let [body (-> js/window .-parent .-document .-body)]
       (.play
        (Scroll. body
                 (array 0 (.-scrollTop body))
                 (array 0 0)
                 time)))))
