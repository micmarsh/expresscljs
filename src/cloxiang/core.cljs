(ns cloxiang.core)

(defn -main [& args]
    (let [express (js/require "express")
          app (express)
          port 3000]
        (doto app
            (.use (.logger express))
            (.get "/" (fn [req res]
                (.send res "Hello World")))
            (.listen port))
        (println (str "Express server started on port: " port))))

(set! *main-cli-fn* -main)
