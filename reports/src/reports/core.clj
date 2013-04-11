(ns reports.core
  (:use compojure.core
        ring.middleware.params
        ring.middleware.multipart-params
        ring.util.response
        ring.adapter.jetty)
  (:require [cheshire.core :as json]
            [compojure [handler :as handler] [route :as route]]
            [clojurewerkz.quartzite.scheduler :as quartzite-scheduler]
            [reports [scheduler :as scheduler] [uploader :as uploader]])
  (:gen-class))

(defn- generate-report [params]
  (let [criteria (json/parse-string (:criteria params)) ; TODO: validation
        callback (:callback params)
        resp (scheduler/generate-report criteria)]
    (-> (response (format "%s(%s);" callback (json/generate-string resp)))
        (content-type "text/javascript")
        (charset "UTF-8"))))

(defn- invalidate-cache [params]
  (let [criteria (json/parse-string (:criteria params))] ; TODO: validation
    (response (scheduler/invalidate-cache criteria))))

(defroutes ^:private endpoints
  (GET "/" [] "OK")

  (GET "/generate" [:as {params :params}]
        (generate-report params))

  (POST "/invalidate" [:as {params :params}]
        (invalidate-cache params))
  
  (POST "/upload" [:as {params :params}]
        (-> (response (uploader/save-file params))
            (header "Access-Control-Allow-Origin" "*")))
  
  (OPTIONS "/upload" [:as {params :params}] 
        (-> (response "OK")
            (header "Access-Control-Allow-Origin" "*")))

  (route/not-found "Page not found"))

(defn init []
  (quartzite-scheduler/initialize)
  (quartzite-scheduler/start))

(def app (handler/site endpoints))

(defn -main [& [port]]
  (init)
  (run-jetty #'app {:join? false
                    :port (if port (Integer/valueOf port) 8080)}))