(ns us.edwardstx.conf.data.services
(:require [us.edwardstx.conf.data.db :refer [ds]]
          [manifold.deferred :as d]
          [yesql.core :refer [defqueries]]))

(defqueries "sql/services.sql"
  {:connection {:datasource  ds}})

(defn get-service-key [service]
  (d/future
    (:public_key (first (get-service-key-sql {:service service})))))

