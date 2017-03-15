(ns us.edwardstx.conf.data.conf
  (:require [manifold.deferred :as d]
            [us.edwardstx.conf.data.db :refer [get-connection]]
            [yesql.core :refer [defqueries]]))

(defqueries "sql/conf.sql")

(def parsers {"boolean" #(Boolean/parseBoolean %) "integer" #(Integer/parseInt %)})

(defn parse-val [m]
  (if-let [parser-name (:parser m)]
    (if-let [parser (get parsers parser-name)]
      (assoc m :val (parser (get m :val)))
      m)
    m))

(defn get-service-conf [db service]
  (d/future
    (apply hash-map (mapcat
                     (fn [x] [(:key x) (:val x)])
                     (map parse-val
                          (get-service-config-sql {:service service}
                                                  (get-connection db)))))))

(defn get-service-secret [db service]
  (d/future
    (-> {:service service}
        (get-service-secret-sql (get-connection db))
        first
        :secret
        )))
