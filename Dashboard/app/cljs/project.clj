(defproject projects "0.8.0"
  :description "Standalone apps for the Akvo Flow Dashboard"
  :url "http://akvo.org/products/akvoflow/"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2913"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.omcljs/om "0.8.8"]
                 [cljs-ajax "0.3.10"]
                 [sablono "0.3.4"]]

  :plugins [[lein-cljsbuild "1.0.5"]
            [lein-shell "0.4.0"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false}  ["../../../GAE/war/admin/frames/users.html"
                                     "../../../GAE/war/admin/frames/users.js"
                                     "../../../GAE/war/admin/frames/out/"]

  :aliases {"copyhtml" ["shell" "./cp-html.sh"]
            "copyhtml-production" ["shell" "./cp-html.sh" "--production"]
            "build" ["do"
                     ["clean"]
                     ["copyhtml-production"]
                     ["cljsbuild" "once" "adv"]]
            "watch" ["do"
                     ["clean"]
                     ["copyhtml"]
                     ["cljsbuild" "auto" "dev"]]}

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :compiler {
                :main org.akvo.flow.dashboard.users.core
                :output-to "../../../GAE/war/admin/frames/users.js"
                :output-dir "../../../GAE/war/admin/frames/out"
                :asset-path "out"
                :optimizations :none
                :source-map true}}
             {:id "adv"
              :source-paths ["src"]
              :compiler {
                :main org.akvo.flow.dashboard.users.core
                :output-to "../../../GAE/war/admin/frames/users.js"
                :optimizations :advanced}}]})
