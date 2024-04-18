(ns sniff.client.app
  (:require [goog.events :as gevents]
            [clojure.math]
            [sniff.mouse.events :as mouse]
            [sniff.clipboard.events :as clipboard]))

(def event-stream
  "Stream of events as they happen on the page."
  (atom []))

(def document (.-document js/window))

(defn start-session []
  (swap! event-stream conj {:type :start :time (js/Date.)}))

(defn page-setup
  "Register listeners, peform authentication, and setup the stream of events to the backend server."
  []
  (gevents/removeAll document)
  (gevents/listen document "DOMContentLoaded" start-session)
  (gevents/listen document "mousedown" mouse/handle-mouse)
  (gevents/listen document "mouseup" mouse/handle-mouse)
  (gevents/listen document "cut" clipboard/handle-cut)
  (gevents/listen document "copy" clipboard/handle-copy)
  (gevents/listen document "paste" clipboard/handle-paste))
