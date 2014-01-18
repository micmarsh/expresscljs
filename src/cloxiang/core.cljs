(ns cloxiang.core
    (:use-macros [cljs.core.async.macros :only [go]])

    (:use
          [cljs.core.async :only [>! chan]]


          [cloxiang.express :only [initialize]]
          [cloxiang.handlers :only [register-player]]
          [cloxiang.sockets :only [with-sockets]]))

(defn stringify [json]
    (.stringify js/JSON json))

(defn -main [& args] (->

    (initialize
      [:GET "/" #(identity "yo world")]
      [:GET "/whatup" (let [i (atom 0)]
                         #(-> i (swap! inc) str))]
      [:GET "/yo" #(let [c (chan)]
                      (go (>! c "yoooo"))
                      c)]
      {:port 1337
       :static "public"})

    (with-sockets
        [:open "/yo" #(println "waaaaaah")]
        [:message "/yo" identity])
    ))

(set! *main-cli-fn* -main)
