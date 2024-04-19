(ns sniff.client.app
  (:require [goog.events :as gevents]
            [clojure.math]
            [sniff.mouse.events :as mouse]
            [sniff.clipboard.events :as clipboard]))

(def event-stream
  "Stream of events as they happen on the page."
  (atom []))

(def document (.-document js/window))

(defn log-event [event]
  (swap! event-stream conj event))

(defn start-session []
  (log-event {:type :start :time (js/Date.)}))

(defn handle-visibility-change []
  (js/console.log "Handling change" (.-hidden document)))

(defn page-setup
  "Register listeners, peform authentication, and setup the stream of events to the backend server."
  []
  (gevents/removeAll document)
  (gevents/listen document "DOMContentLoaded" start-session)
  (gevents/listen document "mousedown" mouse/handle-mouse)
  (gevents/listen document "mouseup" mouse/handle-mouse)
  (gevents/listen document "cut" clipboard/handle-cut)
  (gevents/listen document "copy" clipboard/handle-copy)
  (gevents/listen document "paste" clipboard/handle-paste)
  (gevents/listen document "visibilitychange" handle-visibility-change))
