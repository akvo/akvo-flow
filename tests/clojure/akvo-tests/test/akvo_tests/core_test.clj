(ns akvo-tests.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io :only (resource)]
            [akvo-tests.api-key :as api :refer :all]
            [clj-http.client :as client :only (get)]))

(def cfg (read-string (slurp (io/resource "config.clj"))))

(def secret "") ;; FIXME

(deftest api-key-test
  (testing "Testing authenticated requests"
    (doseq [data (:test-api-key cfg)]
      (let [url (data "url")
            base-params (dissoc data "url")
            params (assoc base-params "ts" (api/generate-timestamp))
            base-qs (api/generate-query-string params)
            req-url (str url "?" base-qs "&h=" (api/generate-apikey secret base-qs))]
        (try
          (is (= 200 (:status (client/get req-url))))
          (catch Exception e
            (prn e)))))))
