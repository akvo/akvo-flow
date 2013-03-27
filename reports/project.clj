(defproject reports "0.1.0-SNAPSHOT"
  :description "HTTP layer to applets functionality"
  :url "https://github.com/akvo/akvo-flow"
  :license {:name "GNU Affero General Public License"
            :url "https://www.gnu.org/licenses/agpl"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.logging "0.2.6"]
                 [ring/ring-core "1.1.8"]
                 [ring/ring-servlet "1.1.8"]
                 [compojure "1.1.5"]
                 [cheshire "5.0.2"]
                 [clojurewerkz/quartzite "1.0.1"]
                 [org.apache.poi/poi "3.8"]
                 [org.apache.poi/poi-ooxml "3.8"]
                 [exporterapplet "1.0.0"]
                 [org.json/json "20090211"]
                 [jfree/jfreechart "1.0.13"]]
  :plugins [[lein-ring "0.8.3"]]
  :resource-paths ["src/resource"]
  :ring {:handler reports.core/app
         :init reports.core/init})
