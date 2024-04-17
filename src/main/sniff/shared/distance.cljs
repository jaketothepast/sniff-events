(ns sniff.shared.distance
  (:require [clojure.math]))

(defn euclidean
  [p1 p2]
  (js/console.log "GETTING DISTANCE")
  (let [x-hat (- (:x p1) (:x p2))
        y-hat (- (:y p1) (:y p2))]
    (clojure.math/sqrt
     (+ (* x-hat x-hat)
        (* y-hat y-hat)))))
