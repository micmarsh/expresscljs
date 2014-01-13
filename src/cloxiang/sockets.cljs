(ns cloxiang.sockets
    (:use [cljs.core :only
            [clj->js js->clj]]))

(def websocket (js/require "websocket"))

(defn accept-sockets [server]
    (-> server
        #({:httpServer %
            :autoAcceptConnections true})
        clj->js
        websocket.))

; (defn onopen [])
