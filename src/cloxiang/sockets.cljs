(ns cloxiang.sockets
    (:use [cljs.core :only
            [clj->js js->clj]]
          [cloxiang.utils :only [handle-channel mget]]))

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

; (defn- get-route [request]
;     (-> request
;         (aget "resourceURL")
;         (aget "path")))

; (defn on-open [sockets route callback]
;     (println "on open method " callback)
;     (println (aget sockets "on"))
;     (.on sockets "request"
;         (fn [req]
;             (do (println "oh shit an on-open" route (get-route req))
;             (if (= route (get-route req))
;                 (callback req))))))


; (defn- connection [req]
;   (let [accept (mget req "accept")
;         open-conn (partial accept nil)]
;         (open-conn (aget req "origin"))))

; (defn- on [type sockets route callback]
;     (on-open sockets route
;         (fn [req]
;             (let [conn (connection req)
;                   n (println "setting" type)]
;                   (.on conn type
;                     ;route checking handled on on-open
;                     #(callback req %))))))

; (def on-message (partial on "message"))
; (def on-close (partial on "close"))

; (defn async->socket [async-callback]
;   (println "actually converting function")
;   (fn [req & [msg]]
;     (let [result (async-callback (if msg msg req))
;           conn (connection req)
;           n (println "lolz converting function")]
;           (handle-channel result
;             (fn [result]
;               (if (= (aget conn "state") "open")
;                 (->> result str (.sendUTF conn))))))))

; (def functions {
;   :open on-open
;   :message on-message
;   :close on-close
;   })

(defn with-sockets [server & vecs]
    (let [n (println "yo homies calling with-sockets")
          sockets (accept-sockets server)]
          (.on sockets "connection"
            (fn [ws]
              (println "yo world")))
          ; (doseq [[type route callback] vecs
          ;         function [(type functions)]
          ;         z [(println "yo homies processing one of those vecs"
          ;           type route callback)]]
          ;   (function sockets route
          ;     (async->socket callback)))
          (println "oh")
          server))
