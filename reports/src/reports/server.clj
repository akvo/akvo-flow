(ns reports.server
  (:use ring.adapter.jetty)
  (:require [reports.http :as http] 
            [clojurewerkz.quartzite.scheduler :as qs]))

(defn -main [& args]
  (qs/initialize)
  (qs/start)
  (run-jetty #'http/app {:port 8000 :join? false}))