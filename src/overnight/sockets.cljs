(ns overnight.sockets
    (:use [cljs.core :only
            [clj->js js->clj]]
          [overnight.utils :only [handle-channel mget]]))

(def websocket
    (aget
        (js/require "ws")
            "Server"))

(defn printret [thing]
    (do
        (println thing)
        thing))

(defn make-args [server] {:server server})

(defn accept-sockets [server]
    (-> server
        make-args
        clj->js
        websocket.))

(defn- get-route [socket]
    (-> socket
        (aget "upgradeReq")
        (aget "url")))

(defn- matches? [should-be route]
  (let [regex (js/RegExp. should-be)
        matches (.match route regex)]
        (or (= should-be route)
            (= route (first matches)))))

(defn on-open [sockets route callback]
    (.on sockets "connection"
        (fn [ws]
            (if (matches? route (get-route ws))
                (callback ws)))))

(defn- on [type sockets route callback]
    (on-open sockets route
        (fn [ws]
          (.on ws type
            #(callback ws %)))))

(def on-message (partial on "message"))
(def on-close (partial on "close"))

(defn async->socket [async-callback]
  (fn [ws & [msg]]
    (let [result (if msg (async-callback msg ws)
                         (async-callback ws))]
          (handle-channel result
            (fn [result]
              ;TODO somehow check to make sure ws is open b4 sending
                (->> result str (.send ws)))))))

(def functions {
  :open on-open
  :message on-message
  :close on-close
  })

(defn with-sockets [server & vecs]
    (let [sockets (accept-sockets server)]
      (doseq [[type route callback] vecs
              function [(type functions)]]
        (function sockets route
          (async->socket callback)))
      server))
