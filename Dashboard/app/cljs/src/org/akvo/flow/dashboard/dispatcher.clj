(ns org.akvo.flow.dashboard.dispatcher
  (:require [cljs.core.async.macros :refer (go-loop)]))

(defmacro dispatch-loop [key binding & body]
  `(let [chan# (org.akvo.flow.dashboard.dispatcher/register ~key)]
     (go-loop []
       (let [[_# ~binding] (~'<! chan#)]
         ~@body
         (recur)))))
