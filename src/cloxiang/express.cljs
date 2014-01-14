(ns cloxiang.express
      (:use
        [cljs.core :only [clj->js js->clj]]
        [cljs.core.async :only [<!, chan]]
        [clojure.string :only [lower-case]])
      (:use-macros [cljs.core.async.macros :only [go]]))

(def handle-channel
  (let [chan-type (type (chan))]
    (fn [result]
        (cond
            (= chan-type (type result))
              (go (handle-channel (<! result)))
            (string? result)
              result))))

(defn- async->express [async-callback]
    (fn [request response]
        (let [result (async-callback request)]
            (->> result
                handle-channel
                (.send response)))))

(defn- register-route [app route]
    (let [[type path callback] route
          fn-name (-> type name lower-case)]
          ;SCOPE!
        (.call (aget app fn-name) app
            path
            (async->express callback))))

(defn initialize [& args]
    (let [express (js/require "express")
          http (js/require "http")
          app (express)
          port (atom 3000)]
          ; server (.createServer http app)
          ; sockets (accept-sockets server)]
          (.use app (.logger express))
          (doseq [item args]
            (cond
              (vector? item)
                (register-route app item)
              (map? item)
                (reset! port (:port item))))
          (.listen app @port)
          (println (str "Express server listening on port " @port))))
