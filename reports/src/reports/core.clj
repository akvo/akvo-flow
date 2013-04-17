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