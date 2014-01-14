(ns cloxiang.core
    (:use [cloxiang.express :only [initialize]]
          [cloxiang.handlers :only [register-player]]
          [cloxiang.sockets :only [accept-sockets, on-open, on-close]]))

(defn stringify [json]
    (.stringify js/JSON json))

(defn -main [& args]

    (let [express (js/require "express")
          http (js/require "http")
          app (express)
          server (.createServer http app)
          port 3000
          sockets (accept-sockets server)]
        (doto app
            (.use (.logger express))

            (.get "/" (fn [req res]
                (do (.send res "Yo World")
                    (println req))))
            (.get "/move" (fn [req res]
                (println (aget req "socket"))))
            (.get "/\\w{5}" register-player)

            (.listen port))
        (on-open sockets println)
        (on-close sockets println)
        (println app)
        (println (str "Express server started on port: " port))))

(set! *main-cli-fn* -main)
