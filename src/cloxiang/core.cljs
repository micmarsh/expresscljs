(ns cloxiang.core
    (:use-macros [cljs.core.async.macros :only [go]])

    (:use
          [cljs.core.async :only [>! chan]]


          [cloxiang.express :only [initialize]]
          [cloxiang.handlers :only [register-player]]
          [cloxiang.sockets :only [accept-sockets, on-open, on-close]]))

(defn stringify [json]
    (.stringify js/JSON json))

(defn -main [& args]
  ;TODO
  ;   socket setup (need to figure out how sockets actually work for that)
  ;   TESTS in cljs, how tricky
    (initialize
      [:GET "/" #(identity "yo world")]
      [:GET "/whatup" (let [i (atom 0)]
                         #(-> i (swap! inc) str))]
      [:GET "/yo" #(let [c (chan)]
                      (go (>! c "yoooo"))
                      c)]

      {:port 1337
       :static "public"}))

(set! *main-cli-fn* -main)
