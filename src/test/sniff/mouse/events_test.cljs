(ns sniff.mouse.events-test
  (:require [sniff.mouse.events :as events]
            [cljs.test :refer (deftest is)]))

(def test-click
  (js-obj "clientX" 1 "clientY" 1))

(deftest extracts-point
  (is (= {:x 1 :y 1} (events/mouse-position test-click))))
