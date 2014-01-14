(ns cloxiang.core
    (:use [cloxiang.express :only [initialize]]
          [cloxiang.handlers :only [register-player]]
          [cloxiang.sockets :only [accept-sockets, on-open, on-close]]))

(defn stringify [json]
    (.stringify js/JSON json))

(defn -main [& args]
    (initialize
      [:get "/" #(identity "yo world")]
      [:get "/whatup" (let [i (atom 0)]
                         #(-> i (swap! inc) str))]))

(set! *main-cli-fn* -main)
