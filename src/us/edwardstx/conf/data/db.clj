(ns us.edwardstx.conf.data.db
  (:require [hikari-cp.core :refer :all]
            [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]))

(defrecord Database [options datasource]
  component/Lifecycle

  (start [this]
    (log/info "Connecting to database: " (assoc options :password "*********"))
    (let [ds (make-datasource options)]
      (log/info "Connected to database")
      (if-let [init-fn (:init-fn options)]
        (init-fn ds))
      (assoc this :datasource ds)))

  (stop [this]
    (log/info "Disconnecting from database")
    (close-datasource datasource)
    (assoc this :datasource nil)))


(defn new-database [options]
  (map->Database {:options (select-keys options [:jdbc-url :classname :subname :username :password :subprotocol])}))

(defn get-connection [db]
  {:connection (select-keys db [:datasource])})
