(ns cloxiang.utils
    (:use [cljs.core.async :only [take! chan]]
          [cljs.core :only [clj->js]]))

(defn mget [object method]
  "\"method get\" grabs a still properly scoped function from a javascript object"
  (let [function (aget object method)]
    (fn [& args]
      (.apply function object
        (to-array args)))))

(def CHAN_TYPE  (type (chan)))

(defn handle-channel
  "Given a \"result\" (a value or a channel that will eventually give you a value) and a callback, makes sure the value gets into the callback"
   [result fn1]
        (cond
            (= CHAN_TYPE (type result))
              (take! result
                #(handle-channel % fn1))
            (string? result)
              (fn1 result)
            (map? result)
              (-> result clj->js fn1)
            :else
              (-> result str fn1)))
