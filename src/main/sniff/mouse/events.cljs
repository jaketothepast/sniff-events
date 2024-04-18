(ns sniff.mouse.events)

(def evt-buffer
  "Temporary, to store previous mouse movement on click/drag"
  (atom []))

(defn mouse-position [e]
  {:x (.-clientX e)
   :y (.-clientY e)})

(defn pos
  "Get the position of the element laid out in the viewport"
  [elm]
  (let [client-rect (-> elm
                        .getBoundingClientRect)]
    {:x (.-x client-rect)
     :y (.-y client-rect)}))

(defn equal-point? [p1 p2]
  (and (= (:x p1) (:x p2))
       (= (:y p1) (:y p2))))

(defn click-event
  "Get the distance between P1 and P2"
  [p1 p2]
  (if (equal-point? p1 p2)
    {:event :click
     :start p1
     :time (js/Date.)}
    {:event :click-and-drag
     :start p1
     :end p2
     :time (js/Date.)}))

(defn handle-mouse [e]
  (let [val (and (not-empty @evt-buffer) (first @evt-buffer))
        pos (mouse-position e)]
    (if val
      ;; Calculate distance.
      (do
        (reset! evt-buffer [])
        (click-event val pos))
      (swap! evt-buffer conj pos))))
