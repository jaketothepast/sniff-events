(ns sniff.client.app
  (:require [goog.events :as gevents]
            [clojure.math]
            [sniff.mouse.events :as mouse]))

(def event-stream
  "Stream of suspicious events on page"
  (atom []))

(def document (.-document js/window))

(defn log-result [wrapped]
  (fn [e] (js/console.log (clj->js (wrapped e)))))

(defn page-setup
  "Register listeners, peform authentication, and setup the stream of events to the backend server."
  []
  (gevents/removeAll document)
  (gevents/listen document "mousedown" (log-result mouse/handle-mouse))
  (gevents/listen document "mouseup" (log-result mouse/handle-mouse)))
