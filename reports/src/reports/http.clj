(ns reports.http
  (:use compojure.core
        [ring.middleware json reload]
        ring.util.response)
  (:require [reports.scheduler :as sch]
            [compojure.route :as route]
            [compojure.handler :as handler]))

(defn- sample-payload []
  {"baseURL" "http://instance.akvoflow.org"
   "surveyId" "123"
   "opts" {"exportType" "RAW_DATA"
           "locale" "en"
           "imgPrefix" "http://instance.s3.amazonaws.com/images"}})


(defroutes main-routes
  (POST "/generate" [:as {params :json-params}]
        (response (sch/generate-report params)))

  (POST "/invalidate" [:as {params :json-params}]
        (response (sch/invalidate-cache params)))

  (route/not-found "Page not found"))


(def app
  (-> (handler/site main-routes)
    (wrap-reload '(reports.http reports.scheduler))
    (wrap-json-params)
    (wrap-json-response)))
