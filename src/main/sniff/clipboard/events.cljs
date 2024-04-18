(ns sniff.clipboard.events)

(defn handle-cut [e]
  {:type :cut :time (js/Date.)})

(defn handle-copy [e]
  {:type :copy :time (js/Date.)})

(defn handle-paste [e]
  {:type :paste :time (js/Date.)})
