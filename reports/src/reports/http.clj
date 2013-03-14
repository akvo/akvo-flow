(ns reports.http
  (:use compojure.core
        [ring.middleware params reload]
        ring.util.response)
  (:require [clojure.data.json :as json]
            [reports.scheduler :as sch]
            [compojure.route :as route]
            [compojure.handler :as handler]))

(defn generate-report [params]
  (let [criteria (json/read-str (params :criteria)) ;; TODO: needs validation
        callback (params :callback)
        resp (sch/generate-report criteria)]
    (-> (response (format "%s(%s);" callback (json/write-str resp)))
      (content-type "text/javascript")
      (charset "UTF-8"))))

(defn invalidate-cache [params]
  (let [criteria (json/read-str (params :criteria))] ;; TODO: validation
    (response (sch/invalidate-cache criteria))))

(defroutes main-routes
  (GET "/generate" [:as {params :params}]
        (generate-report params))

  (POST "/invalidate" [:as {params :params}]
        (invalidate-cache params))

  (route/not-found "Page not found"))

(def app
  (-> (handler/api main-routes)
    (wrap-reload '(reports.http reports.scheduler))))
