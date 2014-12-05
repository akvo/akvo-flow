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
