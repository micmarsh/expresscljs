(ns cloxiang.core
    (:use [cloxiang.handlers
        :only [register-player]
           cloxiang.sockets
        :only [accept-sockets]]))

(defn stringify [json]
    (.stringify js/JSON json))

(defn -main [& args]

    (let [express (js/require "express")
          app (express)
          port 3000
          sockets (accept-sockets app)]
        (do
        (doto app
            (.use (.logger express))

            (.get "/" (fn [req res]
                (do (.send res "Yo World")
                    (println req))))
            (.get "/move" (fn [req res]
                (println (aget req "socket"))))
            (.get "/\\w{5}" register-player)

            (.listen port))
        (println app)

        )

        (println (str "Express server started on port: " port))))

(set! *main-cli-fn* -main)
