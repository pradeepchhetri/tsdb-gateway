(ns tsdb-gateway.redis
  (:require [clojure.tools.logging :as logging]
            [taoensso.carmine :as carmine :refer (wcar)]
            [clojure.core.async :as async :refer [<! put! go-loop]]))

(defn exec-send-loop
  "loop for sending datapoints by publishing them to redis publish topic"
  [send-chan conn topic]
  (go-loop [] (let [msg (<! send-chan)]
                (carmine/wcar conn (carmine/publish topic msg))
                (recur))))

(defn msg-handler
  "create handler function for datapoints from redis pub/sub"
  [recv-chan]
  (fn [[msg-type topic payload]]
    (when (= msg-type "message")
      (put! recv-chan payload))))

(defn subscribe-topic
  "subscribe to topic, put items on specified channel"
  [recv-chan conn topic]
  (carmine/with-new-pubsub-listener
    (:spec conn)
    {"datapoints" (msg-handler recv-chan)}
    (carmine/subscribe topic)))

(defn unsubscribe
  "unsubscribe listener from all topics"
  [listener]
  (carmine/with-open-listener listener (carmine/unsubscribe)))

(defn close
  "close listener"
  [listener]
  (carmine/close-listener listener))

(defn redis-up?
  "checks redis status"
  [conn]
  (try
    (carmine/wcar conn
      (carmine/ping))
    (catch java.net.SocketException e
      false)))
