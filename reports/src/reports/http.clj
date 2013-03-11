(ns reports.http
  (:use compojure.core
        [ring.middleware json reload]
        ring.util.response)
  (:require [reports.scheduler :as sch]
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
  (if (empty? params)
    {:status 400 :body {"sample" generate-sample}}
    (response (sch/generate-report params))))

(defn invalidate-cache [params]
  (if (empty? params)
    {:status 400 :body {"sample" invalidate-sample}}
    (response (sch/invalidate-cache params))))

(defroutes main-routes
  (POST "/generate" [:as {params :json-params}]
        (generate-report params))

  (POST "/invalidate" [:as {params :json-params}]
        (invalidate-cache params))

  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
    (wrap-reload '(reports.http reports.scheduler))
    (wrap-json-params)
    (wrap-json-response)))
