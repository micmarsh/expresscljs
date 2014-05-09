(ns overnight.server
      (:use
        [cljs.core :only [clj->js js->clj]]
        [cljs.core.async :only [take! chan]]
        [clojure.string :only [lower-case]]
        [overnight.utils :only [handle-channel mget]]))


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
          (doseq [item args]
            (cond
              (vector? item)
                (register-route app item)
              (map? item)
                (do
                  (reset! port (:port item))
                  (reset! static (:static item))) 
              (fn? item)
                (.use app item)))
          (if @static
              (.use app
                (.static express
                  (.join (js/require "path")
                    js/__dirname @static))))
          (.use app (.logger express))
          (.listen server (or @port 3000))
          (println (str "Express server listening on port " (or @port 3000)))
          server))
