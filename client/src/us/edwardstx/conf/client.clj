(ns us.edwardstx.conf.client
  (:require [clojure.data.json :as json]

            [digest :refer [md5]]
            [config.core :refer [env]]
            [lock-key.core :as crypt]

            [manifold.deferred :as d]
            [clj-crypto.core :as crypto]

            [byte-streams :as bs]
            [aleph.http :as http]))

(def ec-cipher (crypto/create-cipher "ECIES"))

(defn get-conf-impl []
  (let [secret (:conf-secret env)
        m (md5 secret)
        s (:service-name env)
        headers {:headers {:keyhash m}}]
    (-> (str (:conf-host env) s)
        (http/get headers)
        deref
        :body
        bs/to-string
        (crypt/decrypt-from-base64 secret)
        (json/read-str :key-fn keyword))))


(def get-conf-v0 (memoize get-conf-impl))

(defn get-conf-v1 [service token key]
  (d/chain
   (http/post (str "https://conf.edwardstx.us/api/v1/conf/" service) {:body token})
   :body
   bs/to-string)
   #(crypto/decrypt key % ec-cipher)
   json/read-str)

(defn get-conf
  ([] (get-conf-v0))
  ([service token key] (get-conf-v1 service token key)))
