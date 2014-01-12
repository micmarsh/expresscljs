(ns cloxiang.core
    (:use [cloxiang.handlers
        :only [register-player]]))

(defn stringify [json]
    (.stringify js/JSON json))
(defn log [thing]
    (.log js/console thing))

(defn -main [& args]
    (let [express (js/require "express")
          app (express)
          port 3000]
        (doto app
            (.use (.logger express))
            (.get "/" (fn [req res]
                (do (.send res "Yo World")
                    (log req))))
            (.get "/move" (fn [req res]
                (log (aget req "socket"))))
            (.get "/\\w{5}" register-player)
            (.listen port))
        (println (str "Express server started on port: " port))))

(set! *main-cli-fn* -main)
