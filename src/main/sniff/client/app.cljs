(ns sniff.client.app
  (:require [goog.events :as gevents]
            [goog.dom :as gdom]
            [clojure.math]
            [cljs.core.async :refer [go]]
            [cljs.core.async.interop :refer-macros [<p!]]
            [sniff.mouse.events :as mouse]
            [lambdaisland.fetch :as fetch]
            [sniff.clipboard.events :as clipboard]
            [sniff.visibility.events :as visibility]
            ["@mediapipe/tasks-vision" :refer (FilesetResolver FaceLandmarker)]))

(def event-stream
  "Stream of events as they happen on the page."
  (atom []))

(def app-config
  (atom {:student nil
         :assignment nil
         :backend nil
         :landmarker nil
         :last-video-time -1}))

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

(defn predict-frame
  "Given the video element, start predicting the frame"
  [video-element landMarker]
  (let [start-time-ms (js/performance.now)]
    (-> (.detectForVideo landMarker video-element start-time-ms)
        js/console.log)
    (js/window.requestAnimationFrame #(predict-frame video-element landMarker))))

(defn startup-video
  "Start the video playing in the video element."
  [landMarker]
  (let [video-element (gdom/getElement "video")
        media-devices (.-mediaDevices js/navigator)
        constraints #js {:video true}]
    (go
      (let [video-device (<p! (.getUserMedia media-devices constraints))]
        (set! (.-srcObject video-element) video-device)
        (.addEventListener video-element "loadeddata" #(predict-frame video-element landMarker))
        (.play video-element)))))


;; Creating the model landmarker task.
(def model-path "models/face_landmarker.task")
(defn create-face-landmarker
  "Create our faceLandmarker"
  []
  (go
    ;; Bring in the fileset resolver that pulls in wasm tasks.
    (let [vision (<p! (FilesetResolver.forVisionTasks "https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@latest/wasm"))
          landMarker (<p! (FaceLandmarker.createFromOptions
                           vision
                           #js {:baseOptions #js {:modelAssetPath model-path}
                                :runningMode "VIDEO"
                                :outputFaceBlendshapes true
                                :numFaces 1}))]
      (startup-video landMarker))))

(defn page-setup
  "Register listeners, peform authentication, and setup the stream of events to the backend server."
  []
  (gevents/removeAll document)
  (doseq [[event handler] event-handlers]
    (gevents/listen document event (event-logger handler))))

(defn init [student assignment backend]
  ;; Initialize the model as well
  (reset! app-config {:student student :assignment assignment :backend backend :last-video-time -1})
  (create-face-landmarker)
  (page-setup))
