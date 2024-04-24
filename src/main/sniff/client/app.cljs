(ns sniff.client.app
  (:require [goog.events :as gevents]
            [clojure.math]
            [sniff.mouse.events :as mouse]
            [lambdaisland.fetch :as fetch]
            [sniff.clipboard.events :as clipboard]))

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

(defn handle-visibility-change []
  (when (.-hidden document)
    {:type :tab-switch :time (js/Date.)}))

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
   "visibilitychange" handle-visibility-change})

(defn page-setup
  "Register listeners, peform authentication, and setup the stream of events to the backend server."
  []
  (gevents/removeAll document)
  (doseq [[event handler] event-handlers]
    (gevents/listen document event (event-logger handler))))

(defn init [student assignment backend]
  (js/console.log "initializing")
  (reset! app-config {:student student :assignment assignment :backend backend})
  (page-setup))
