(ns cloxiang.express
      (:use
        [cljs.core.async :only [<!]]
        [clojure.string :only [lower-case]])
      (:use-macros [cljs.core.async.macros :only [go]]))

(defn- async->express [async-callback]
    (fn [request response]
        (let [result (async-callback request)]
            (cond (string? result)
                (.send response result)
                :else ; it's a channel ho shit
                    (go
                        (.send response (<! result)))))))

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
          app (express)]
          ; server (.createServer http app)
          ; sockets (accept-sockets server)]
          (.use app (.logger express))
          (doseq [item args]
            (cond (vector? item)
                (register-route app item)))
          (.listen app 3000)))
