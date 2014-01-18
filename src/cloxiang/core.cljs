(ns express.core
    (:use
          [cljs.core.async :only [put! chan]]

          [express.express :only [initialize]]
          [express.handlers :only [register-player]]
          [express.sockets :only [with-sockets]]))

(defn stringify [json]
    (.stringify js/JSON json))

(def chan-test #(let [c (chan)]
                      (put! c "yoooo")
                      c))

(defn -main [& args] (->

    (initialize
      [:GET "/" #(identity "yo world")]
      [:GET "/whatup" (let [i (atom 0)]
                         #(-> i (swap! inc) str))]
      [:GET "/yo" chan-test]
      {:port 1337
       :static "public"})

    (with-sockets
        [:open "/yo" #(println "waaaaaah")]
        [:message "/yo" chan-test])
    ))

(set! *main-cli-fn* -main)
