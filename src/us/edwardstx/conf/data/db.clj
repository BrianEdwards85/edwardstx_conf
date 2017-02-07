(ns us.edwardstx.conf.data.db
  (:require [hikari-cp.core :refer :all]
            [config.core :refer [env]]))
;;            [clojure.data.json :as json]
;;            [clojure.java.io :as io]))

;;(def ds-options
;;   (-> "db.json"
;;       io/resource
;;       slurp
;;       (json/read-str :key-fn keyword)))

(def ds-options
    {:jdbc-url (:jdbc-url env)
     :classname (:classname env)
     :subname (:subname env)
     :username (:username env)
     :password (:password env)
     :subprotocol (:subprotocol env)})

(def ds (make-datasource ds-options))

(defn convert-arrays [r]
  (apply hash-map
         (mapcat
          (fn [c]
            [(first c)
             (let [v (second c)]
               (if (instance? java.sql.Array v)
                 (vec (.getArray v))
                 v))])
          r)))

