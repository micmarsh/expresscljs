(ns cloxiang.sockets
    (:use [cljs.core :only
            [clj->js js->clj]]
          [cloxiang.utils :only [handle-channel mget]]))

(def websocket
    (aget
        (js/require "websocket")
            "server"))

(defn printret [thing]
    (do
        (println thing)
        thing))

(defn make-args [server]
    {:httpServer server
     :autoAcceptConnections true})

(defn accept-sockets [server]
    (-> server
        make-args
        clj->js
        websocket.))

(defn- get-route [request]
    (-> request
        (aget "resourceURL")
        (aget "path")))

(defn on-open [sockets route callback]
    (.on sockets "request"
        (fn [req]
            (if (= route (get-route req))
                (callback req)))))


(defn- connection [req]
  (let [accept (mget req "accept")
        open-conn (partial accept nil)]
        (open-conn (aget req "origin"))))

(defn- on [type sockets route callback]
    (on-open sockets route
        (fn [req]
            (let [conn (connection req)
                  from-route (get-route req)]
                  (.on conn type
                    (fn [input]
                        (if (= route from-route)
                            (callback req input))))))))

(def on-message (partial on "message"))
(def on-close (partial on "close"))

; planing:

; the way the api looks:
; [:socket-open "/foo" (fn[req](aget req "property"))]
; [:socket-message "/foo" (fn[msg](str "woo " msg))]
; ;socket close prolly can't write back, so just use for side effects
; program to abstraction: need a async->socket fn:
; (defn a-s [asyn-cb]
;    (fn [req & [msg]]
;       (let [result (asyn-cb (req or msg if msg))
;             conn (get connection from req)]
;           (handle-channel result #(conn.sendUTF (str %))))))
;                                   if not closed or whatevs

;THEN can wrap that^ around the things U pass to on-open, etc.
;, the things that come from 'vecs' down there

(defn async->socket [async-callback]
  (fn [req & [msg]]
    (let [result (async-callback (if msg msg req))
          conn (connection req)]
          (handle-channel result
            (fn [result]
              (if = (aget conn "state") "open")
                (->> result str (.sendUTF conn)))))))

(def functions {
  :open on-open
  :message on-message
  :close on-close
  })

(defn with-sockets [app & vecs]
    (let [http (js/require "http")
          server (.createServer http app)
          sockets (accept-sockets server)]
          (doseq [[type route callback] vecs
                  function [(type functions)]]
            (function sockets route
              (async->socket callback)))
          app))
