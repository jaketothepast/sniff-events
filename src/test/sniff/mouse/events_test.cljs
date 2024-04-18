(ns sniff.mouse.events-test
  (:require [sniff.mouse.events :as events]
            [cljs.test :refer (deftest is)]))

(def test-click
  (js-obj "clientX" 1 "clientY" 1))

(def test-click-not-same
  (js-obj "clientX" 1 "clientY" 2))

(deftest extracts-point
  (is (= {:x 1 :y 1} (events/mouse-position test-click))))

(deftest click-event
  (let [event (do (events/handle-mouse test-click)
                  (events/handle-mouse test-click))]
    (is (= :click (:event event)))))

(deftest click-and-drag-event
  (let [event (do (events/handle-mouse test-click)
                  (events/handle-mouse test-click-not-same))]
    (is (= :click-and-drag (:event event)))))

(deftest event-logs-time
  (is (not= nil (:time (do (events/handle-mouse test-click)
                           (events/handle-mouse test-click))))))
