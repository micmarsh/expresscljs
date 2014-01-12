(ns cloxiang.handlers)

(def colors ["black" "red"])

(def join-game
    (let [i (atom 0)]
        (fn [id]
        (let [number (swap! i inc)
              index (mod number 2)]
            (colors index)))))

(defn register-player [request response]
    (let [url (aget request "url")
          id (rest url)
          color (join-game id)]
        (.send response color)))
