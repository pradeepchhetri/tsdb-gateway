(ns tsdb-gateway.component
  (:require [tsdb-gateway.redis :as redis]
            [clojure.tools.logging :as logging]
            [com.stuartsierra.component :as component]
            [clojure.core.async :as async :refer [chan]]))

(defrecord PubSub [conf channels listener]
  component/Lifecycle
  (start [component]
         (logging/info "Starting Redis Pub/Sub Component")
         (let [conn {:pool {}
                     :spec {:host (:redis-server conf)
                            :port (:redis-port conf)}}
               listener (redis/subscribe-topic (:receive channels) conn "datapoints")]
           (assoc component :conn conn :listener listener)))
  (stop [component]
        (logging/info "Stopping Redis Pub/Sub Component")
        (redis/unsubscribe listener)
        (redis/close listener)
        (assoc component :conn nil :listener nil)))

(defn new-pubsub [conf]
  (map->PubSub {:conf conf}))

(defrecord PubSub-Channels []
  component/Lifecycle
  (start [component]
         (logging/info "Starting Pub/Sub Channels Component")
         (assoc component :send (chan) :receive (chan)))
  (stop [component]
        (logging/info "Stopping Pub/Sub Channels Component")
        (assoc component :send nil :receive nil)))

(defn new-pubsub-channels []
  (map->PubSub-Channels {}))
