(ns sniff.client.app
  (:require [goog.dom :as gdom]
            [goog.events :as gevents]
            [clojure.math]
            [lambdaisland.fetch :as fetch]
            [sniff.mouse.events :as mouse]))

(def event-stream
  "Stream of suspicious events on page"
  (atom []))

(def student-id 1)
(def assignment-id 1)
(def event-url "http://localhost:3000/events.json")

(defn log-event
  "Send the event to our running backend"
  [{:keys [event]}]
  (prn (:body (fetch/post event-url {:query-params {:event (str event) :student_id student-id :assignment_id assignment-id}}))))

(defn page-setup
  "Register listeners, peform authentication, and setup the stream of events to the backend server."
  []
  (let [html (first (gdom/getElementsByTagName "html"))]
    (gevents/removeAll html)
    (gevents/listen html "mousedown" mouse/handle-mouse)
    (gevents/listen html "mouseup" (fn [e]
                                     (-> (mouse/handle-mouse e)
                                         log-event)))))
