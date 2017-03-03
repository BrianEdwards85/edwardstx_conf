(ns us.edwardstx.conf.orchestrator
  (:require [manifold.deferred :as d]
            [clojure.data.json :as json]
            [clojure.tools.logging :as log]
            [lock-key.core :as crypt]
            [digest :refer [md5]]
            [clj-crypto.core :as crypto]
            [buddy.sign.jwt :as jwt]
            [us.edwardstx.conf.data.conf :refer [get-service-conf get-service-secret]]
            [us.edwardstx.conf.data.services :refer [get-service-key]]
            [us.edwardstx.conf.parser :refer [sharpen]]))

(def ec-cipher (crypto/create-cipher "ECIES" ))

(defn read-public-key [key-string]
  (->> key-string
       crypto/decode-base64
       (assoc {:algorithm "ECDSA"} :bytes)
       crypto/decode-public-key))

(def authority-key (d/chain (get-service-key "auth.edwardstx.us") read-public-key))

(defn unsign [token]
  (try
    (jwt/unsign token @authority-key {:alg :es256})
    (catch Exception e
      (println (.getMessage e))
      (log/warn e "Unable to validate/unsign token")
      nil)))

(defn validate-token-and-encrypt-conf [claims service-key-str service-key serive-conf]
  (if (= service-key-str (:key claims))
    (do
      (log/info "Sending conf: " serive-conf)
      (crypto/encode-base64-as-str (crypto/encrypt service-key serive-conf ec-cipher)))
    (do
      (log/warn "Claim key did not match DB key for " claims)
      nil)))

(defn validate-token-get-encrypt-conf [service token]
  (let [service-key-str (get-service-key service)
        service-key (d/chain service-key-str read-public-key)
        serive-conf (d/chain (get-service-conf service) sharpen json/write-str)]
    (if-let [service-claims (unsign token)]
      (if (= service (:sub service-claims))
        (d/chain (d/zip service-key-str service-key serive-conf)
                 #(apply validate-token-and-encrypt-conf (conj % service-claims)))
        (do
          (log/warn "Request service did not match token sub: " service "<>" (:sub service-claims) )
          nil))
      (do
        (log/warn "Unable to validate/unsign token for " service)
        nil))))

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
