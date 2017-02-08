(ns us.edwardstx.conf.orchestrator
  (:require [manifold.deferred :as d]
            [clojure.data.json :as json]
            [lock-key.core :as crypt]
            [digest :refer [md5]]
            [us.edwardstx.conf.data.conf :refer [get-service-conf get-service-secret]]
            [us.edwardstx.conf.parser :refer [sharpen]]))

(defn encrypt-hash [p]
  (let [secret (first p)
        conf (second p)]
    (if (and secret conf)
      {:md5 (md5 secret)
       :conf (lock-key.core/encrypt-as-base64 conf secret)}
      (Exception. "Service not found"))))

(defn get-encrypted-conf [service]
  (d/chain
   (d/zip (get-service-secret service) (d/chain (get-service-conf service) sharpen json/write-str))
   encrypt-hash))

