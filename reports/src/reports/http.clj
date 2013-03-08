(ns reports.http
  (:require [reports.scheduler :as sch])
  (:use ring.middleware.json
        ring.middleware.reload
        ring.util.response
        ring.adapter.jetty))

(defn handler [{params :json-params}]
  (if params
    (response (sch/schedule-job params))
    {:status 400
     :body {"message" "BAD REQUEST"
            "sample_payload" {"baseURL" "http://localhost:8888"
                      "surveyId" "123"
                      "exportType" "RAW_DATA"
                      "locale" "en"
                      "imgPrefix" "http://sample.s3.amazonaws.com/images"
                      "opts" {}}}}))

(def app
  (-> #'handler
    (wrap-reload '(reports.http))
    (wrap-json-body)
    (wrap-json-params)
    (wrap-json-response)))

(defn boot []
  (run-jetty #'app {:port 8000 :join? false}))

