(ns reports.http
  (:use compojure.core
        [ring.middleware params reload]
        ring.util.response)
  (:require [clojure.data.json :as json]
            [reports.scheduler :as sch]
            [compojure.route :as route]
            [compojure.handler :as handler]))

(def generate-sample
  {"baseURL" "http://instance.akvoflow.org"
   "surveyId" "123"
   "opts" {"exportType" "RAW_DATA"
           "locale" "en"
           "imgPrefix" "http://instance.s3.amazonaws.com/images"}})

(def invalidate-sample
  {"baseURL" "http://instance.akvoflow.org"
   "surveyIds" [123 456 789]})

(defn generate-report [params]
  (let [criteria (json/read-str (params :criteria)) ;; TODO: needs validation
        callback (params :callback)
        resp (sch/generate-report criteria)]
    (-> (response (format "%s(%s);" callback (json/write-str resp)))
      (content-type "text/javascript")
      (charset "UTF-8"))))

(defn invalidate-cache [params]
  (if (empty? params)
    {:status 400 :body {"sample" invalidate-sample}}
    (response (sch/invalidate-cache params))))

(defroutes main-routes
  (GET "/generate" [:as {params :params}]
        (generate-report params))

  (POST "/invalidate" [:as {params :params}]
        (invalidate-cache params))

  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
    (wrap-reload '(reports.http reports.scheduler))
    (wrap-params)))
