(ns reports.core-test
  (:use clojure.test
        reports.scheduler))

(deftest a-test
  (testing "Testing keys"
    (is (= (org.quartz.JobKey. "job1") (get-job-key "job1")))))
