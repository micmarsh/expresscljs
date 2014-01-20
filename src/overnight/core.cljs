(ns overnight.core
    (:use
          [cljs.core.async :only [put! chan]]

          [overnight.server :only [initialize]]
          [overnight.sockets :only [with-sockets]]))

(defn stringify [json]
    (.stringify js/JSON json))

(def chan-test #(let [c (chan)]
                      (put! c "yoooo")
                      c))

(defn -main [& args] (->
    (initialize
      [:get "/" #(identity "yo world")]
      [:get "/whatup" (let [i (atom 0)]
                         #(-> i (swap! inc) str))]
      [:get "/yo" chan-test]

      {:port 1337
       :static "public"})

    (with-sockets
        [:open "/\\w{3}" #(println "waaaaaah")]
        [:message "/yo" (fn [msg ws]
                          (do
                            (println msg)
                            (println ws)
                            "yo"))])
    ))

(set! *main-cli-fn* -main)
