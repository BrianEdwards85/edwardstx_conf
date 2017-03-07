(ns us.edwardstx.conf.data.services
  (:require [us.edwardstx.conf.data.db :refer [get-connection]]
            [manifold.deferred :as d]
            [yesql.core :refer [defqueries]]))

(defqueries "sql/services.sql")

(defn get-service-key [db service]
  (d/future
    (:public_key (first (get-service-key-sql {:service service} (get-connection db))))))

