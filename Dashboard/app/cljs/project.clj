(defproject projects "0.1.0-SNAPSHOT"
  :description "Standalone apps for the Akvo Flow Dashboard"
  :url "http://akvo.org/products/akvoflow/"

  :dependencies [[org.clojure/clojure "1.7.0-alpha2"]
                 [org.clojure/clojurescript "0.0-2371"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [om "0.7.3"]
                 [cljs-ajax "0.3.3"]
                 [sablono "0.2.22"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :notify-command ["./postcompile.sh"]
              :compiler {
                :output-to ~(str (System/getenv "DASHBOARD_TAB") ".js")
                :output-dir "out"
                :optimizations :none
                :source-map true}}
             {:id "adv"
              :source-paths ["src"]
              :notify-command ["./postcompile-adv.sh"]
              :compiler {
                :output-to ~(str (System/getenv "DASHBOARD_TAB") ".js")
                :optimizations :advanced
                :pretty-print false
                :preamble ["react/react.min.js"]
                :externs ["react/externs/react.js"]}}]})
