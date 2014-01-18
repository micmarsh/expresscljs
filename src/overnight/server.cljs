(ns overnight.server
      (:use
        [cljs.core :only [clj->js js->clj]]
        [cljs.core.async :only [take! chan]]
        [clojure.string :only [lower-case]]
        [overnight.utils :only [handle-channel mget]])
      (:use-macros [cljs.core.async.macros :only [go]]))


(defn- async->express [async-callback]
    (fn [request response]
        (let [result (-> request js->clj async-callback)]
            (handle-channel result
              (fn [value]
                (.send response value))))))

(defn- register-route [app route]
    (let [[type path callback] route
          fn-name (-> type name lower-case)
          method (mget app fn-name)]
        (method path
            (async->express callback))))

(defn initialize [& args]
    (let [express (js/require "express")
          http (js/require "http")
          app (express)
          server (.createServer http app)
          port (atom 3000)
          static (atom nil)]
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
          (.listen server @port)
          (println (str "Express server listening on port " @port))
          server))
