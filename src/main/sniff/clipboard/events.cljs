(ns sniff.clipboard.events)

(defn handle-cut [e]
  {:event :cut :time (js/Date.)})

(defn handle-copy [e]
  {:event :copy :time (js/Date.)})

(defn handle-paste [e]
  {:event :paste :time (js/Date.)})
