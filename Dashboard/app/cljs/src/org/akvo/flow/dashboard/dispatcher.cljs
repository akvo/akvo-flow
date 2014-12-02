(ns org.akvo.flow.dashboard.dispatcher
  (:require [cljs.core.async :as async :refer [chan put! pub sub unsub]]))

(let [dispatch-chan (chan)
      dispatch-pub (pub dispatch-chan (fn [[tag & _]] tag))]

  (defn register [tag]
    (let [sub-chan (chan)]
      (sub dispatch-pub tag sub-chan)
      sub-chan))

  (defn unregister [tag chan]
    (unsub dispatch-pub tag chan))

  (defn dispatch [tag arg]
    (put! dispatch-chan [tag arg])))
