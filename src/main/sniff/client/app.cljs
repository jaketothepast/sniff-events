(ns sniff.client.app
  (:require [goog.events :as gevents]
            [goog.dom :as gdom]
            [clojure.math]
            [cljs.core.async :refer [go]]
            [cljs.core.async.interop :refer-macros [<p!]]
            [sniff.mouse.events :as mouse]
            [lambdaisland.fetch :as fetch]
            [sniff.clipboard.events :as clipboard]
            [sniff.visibility.events :as visibility]))

(def event-stream
  "Stream of events as they happen on the page."
  (atom []))

(def app-config
  (atom {}))


(def document (.-document js/window))

(defn log-event [event]
  (swap! event-stream conj event))

(defn start-session []
  {:event :start :time (js/Date.)})

(defn to-backend
  "Ship this event off to the backend"
  [{:keys [type] :as evt}]
  (let [{:keys [student assignment backend]} @app-config]
    (fetch/post backend {:query-params {:student_id student :assignment_id assignment :event_type (.-event evt)}})))

(defn event-logger
  "Wrap the original function, sending the event to the backend."
  [orig-fn]
  (fn [& args]
    (let [evt (orig-fn args)] ;; get our event
      (when (not (nil? evt))
        (js/console.log (clj->js evt))
        (to-backend (clj->js evt))))))

(def event-handlers
  "All event handlers needed to sniff events"
  {"DOMContentLoaded" start-session
   "mousedown" mouse/handle-mouse
   "mouseup" mouse/handle-mouse
   "cut" clipboard/handle-cut
   "copy" clipboard/handle-copy
   "paste" clipboard/handle-paste
   "visibilitychange" visibility/tab-change})

(def media-devices (.-mediaDevices js/navigator))
(def constraints (clj->js {:video true}))
(defn startup-video []
  (let [video-element (gdom/getElement "video")]
    (go
      (let [video-device (<p! (.getUserMedia media-devices constraints))]
        (set! (.-srcObject video-element) video-device)
        (.play video-element)))))

(defn page-setup
  "Register listeners, peform authentication, and setup the stream of events to the backend server."
  []
  (startup-video)
  (gevents/removeAll document)
  (doseq [[event handler] event-handlers]
    (gevents/listen document event (event-logger handler))))

(defn init [student assignment backend]
  (js/console.log "initializing")
  (reset! app-config {:student student :assignment assignment :backend backend})
  (page-setup))

