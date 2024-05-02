(ns sniff.visibility.events)


(def is-visible (atom true))
(def offpage-time
  "Used in handling visibility changes"
  (atom nil))

(defn tab-change
  "Check our is-visible atom. If true, stop the timer, if not true, then start a timer
  that sends off-page events to the backend"
  [e]
  (let [visible? @is-visible
        last-time @offpage-time
        event {:event :tab-switch :time (js/Date.)}]
    (swap! is-visible not) ; Swap our is-visible value after deref
    (if (not visible?)
      (swap! offpage-time js/Date.)
      (reset! offpage-time nil))
    (if (not (nil? last-time))
      (assoc event :elapsed (- (-> (js/Date.) .getTime) (js/Date. last-time)))
      event)))
