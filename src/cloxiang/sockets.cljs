(ns cloxiang.sockets
    (:use [cljs.core :only
            [clj->js js->clj]]))

(def websocket
    (aget
        (js/require "websocket")
            "server"))

(defn accept-sockets [server]
    (-> server
        #({:httpServer %
            :autoAcceptConnections true})
        clj->js
        websocket.))

(defn on-open [sockets callback]
    (.on sockets "request" callback))

(defn- on [type sockets callback]
    (on-open sockets
        (fn [req]
            (let [accept (aget req  "accept")
                  open-conn (partial accept "echo-protocol")
                  conn (open-conn (aget req "origin"))]
                  (.on conn type callback)))))

(def on-message (partial on "message"))
(def on-close (partial on "close"))
