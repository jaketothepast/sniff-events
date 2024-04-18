(ns sniff.client.app-test
  (:require [cljs.test :refer (deftest is)]
            [sniff.client.app :as app]))

(deftest a-failing-test
  (is (= 1 1)))

(deftest test-start-session
  (app/start-session)
  (is (= :start (:type (first @app/event-stream)))))
