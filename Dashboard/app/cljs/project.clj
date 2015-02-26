(defproject projects "0.8.0"
  :description "Standalone apps for the Akvo Flow Dashboard"
  :url "http://akvo.org/products/akvoflow/"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2913"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.omcljs/om "0.8.8"]
                 [cljs-ajax "0.3.10"]
                 [sablono "0.3.4"]]

  :plugins [[lein-cljsbuild "1.0.5"]]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :notify-command ["./postcompile.sh"]
              :compiler {
                :main org.akvo.flow.dashboard.users.core.main
                :output-to "users.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}
             {:id "adv"
              :source-paths ["src"]
              ;;:notify-command ["./postcompile-adv.sh"]
              :compiler {
                :main org.akvo.flow.dashboard.users.core.main
                :output-to "users.js"
                :optimizations :advanced}}]})
