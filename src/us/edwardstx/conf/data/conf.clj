(ns us.edwardstx.conf.data.conf
  (:require [us.edwardstx.conf.data.db :refer [ds]]
            [manifold.deferred :as d]
            [yesql.core :refer [defqueries]]))

(defqueries "sql/conf.sql"
  {:connection {:datasource  ds}})


(defn get-service-conf [service]
  (d/future
    (apply hash-map (mapcat
                     (fn [x] [(:key x) (:val x)])
                     (get-service-config-sql {:service service})))))

(defn get-service-secret [service]
  (d/future
    (-> {:service service}
        get-service-secret-sql
        first
        :secret
        )))
