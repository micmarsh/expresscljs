(ns cloxiang.core
    (:use-macros [cljs.core.async.macros :only [go]])

    (:use
          [cljs.core.async :only [>! chan]]


          [cloxiang.express :only [initialize]]
          [cloxiang.handlers :only [register-player]]
          [cloxiang.sockets :only [with-sockets]]))

(defn stringify [json]
    (.stringify js/JSON json))

(def chan-test #(let [c (chan)]
                      (go (>! c "yoooo"))
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
