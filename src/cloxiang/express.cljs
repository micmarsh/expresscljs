(ns cloxiang.express
      (:use
        [cljs.core :only [clj->js js->clj]]
        [cljs.core.async :only [take! chan]]
        [clojure.string :only [lower-case]])
      (:use-macros [cljs.core.async.macros :only [go]]))

(def handle-channel
  (let [chan-type (type (chan))]
    (fn [result fn1]
        (do (println result)
        (cond
            (= chan-type (type result))
              (take! result
                #(handle-channel % fn1))
            (string? result)
              (fn1 result)
            (map? result)
              (-> result clj->js fn1)
            :else
              (-> result str fn1))))))

(defn- async->express [async-callback]
    (fn [request response]
        (let [result (-> request js->clj async-callback)]
            (handle-channel result
              (fn [value]
                (.send response value))))))

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
          port (atom 3000)
          static (atom nil)]
          ; server (.createServer http app)
          ; sockets (accept-sockets server)]
          (.use app (.logger express))
          (doseq [item args]
            (cond
              (vector? item)
                (register-route app item)
              (map? item)
                (do
                  (reset! port (:port item))
                  (reset! static (:static item)))))
          (if @static
              (.use app
                (.static express
                  (.join (js/require "path")
                    js/__dirname @static))))
          (.listen app @port)
          (println (str "Express server listening on port " @port))))
