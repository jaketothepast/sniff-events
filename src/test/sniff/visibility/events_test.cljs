(ns sniff.visibility.events-test
  (:require [sniff.visibility.events :as events]
            [cljs.test :refer (deftest is)]))

(deftest tab-change-event
  (let [tc (events/tab-change "fake")]
    (is (= :tab-switch (:event tc)))
    (is (>= (js/Date.) (:time tc)))))

(deftest tab-change-elapsed
  (let [tc (do (events/tab-change "fake") (events/tab-change "fake"))]
    (is (< 0 (:elapsed tc)))))
