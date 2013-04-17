;  Copyright (C) 2013 Stichting Akvo (Akvo Foundation)
;
;  This file is part of Akvo FLOW.
;
;  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
;  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
;  either version 3 of the License or any later version.
;
;  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
;  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
;  See the GNU Affero General Public License included below for more details.
;
;  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.

(ns reports.core
  (:use compojure.core
        ring.middleware.params
        ring.middleware.multipart-params
        ring.util.response
        ring.adapter.jetty)
  (:require [cheshire.core :as json]
            [compojure [handler :as handler] [route :as route]]
            [clojurewerkz.quartzite.scheduler :as quartzite-scheduler]
            [reports [scheduler :as scheduler] [uploader :as uploader] [config :as config]])
  (:gen-class))

(defn- generate-report [params]
  (let [criteria (json/parse-string (params "criteria")) ; TODO: validation
        callback (params "callback")
        resp (scheduler/generate-report criteria)]
    (-> (response (format "%s(%s);" callback (json/generate-string resp)))
        (content-type "text/javascript")
        (charset "UTF-8"))))

(defn- invalidate-cache [params]
  (let [criteria (json/parse-string (params "criteria"))] ; TODO: validation
    (response (scheduler/invalidate-cache criteria))))

(defn- transform-map [orig]
  "Returns a new map transforming keyword based keys into strings
   This is required to avoid cast exceptions in Quartz"
  (into {}
        (for [[k v] orig]
          [(name k) v])))

(defroutes ^:private endpoints
  (GET "/" [] "OK")

  (GET "/generate" [:as {params :params}]
        (generate-report (transform-map params)))

  (POST "/invalidate" [:as {params :params}]
        (invalidate-cache (transform-map params)))
  
  (POST "/upload" [:as {params :params}]
        (if (contains? params :file)
          (-> (response (uploader/save-chunk (transform-map params)))
            (header "Access-Control-Allow-Origin" "*"))
          (-> (response (get (scheduler/process-and-upload (transform-map params)) "status"))
            (header "Access-Control-Allow-Origin" "*"))))
  
  (OPTIONS "/upload" [:as {params :params}] 
        (-> (response "OK")
            (header "Access-Control-Allow-Origin" "*")))

  (route/not-found "Page not found"))

(defn init []
  (quartzite-scheduler/initialize)
  (quartzite-scheduler/start))

(def app (handler/site endpoints))

(defn -main [& [config-folder port]]
  (uploader/set-config! (config/load-settings config-folder))
  (init)
  (run-jetty #'app {:join? false
                    :port (if port (Integer/valueOf port) 8080)}))