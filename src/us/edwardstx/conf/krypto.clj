(ns us.edwardstx.conf.krypto
  (:require [clj-crypto.core :as crypto]
            [manifold.deferred :as d]
            [clojure.tools.logging :as log]
            [buddy.sign.jwt :as jwt]
            [com.stuartsierra.component :as component]
            [us.edwardstx.conf.data.services :as services]))

(def ec-cipher (crypto/create-cipher "ECIES" ))

(defn read-public-key [key-string]
  (->> key-string
       crypto/decode-base64
       (assoc {:algorithm "ECDSA"} :bytes)
       crypto/decode-public-key))


(defrecord Krypto [db  authority-key key-cache]
  component/Lifecycle

  (start [this]
    (log/info "Loading Krypto")
    (->> "auth.edwardstx.us"
         (services/get-service-key db)
         deref
         read-public-key
         (assoc this :key-cache (atom {}) :authority-key )))

  (stop [this]
    (assoc this :authority-key nil)))

(defn new-krypto []
  (map->Krypto {}))

(defn unsign [krypto token]
  (try
    (jwt/unsign token (:authority-key krypto) {:alg :es256})
    (catch Exception e
      (println (.getMessage e))
      (log/warn e "Unable to validate/unsign token")
      nil)))

(defn load-service-key [krypto service]
  (let [service-key-str (services/get-service-key (:db krypto) service)
        service-key  (d/chain service-key-str read-public-key)
        service-key-map (d/chain
                         (d/zip service-key-str service-key)
                         (fn [x] {:service-key-str (first x) :service-key (second x)}))
        key-cache (:key-cache krypto)]
    (d/chain service-key-map (fn [km]
                               (swap! key-cache
                                      #(assoc % service km))))
    service-key-map))

(defn get-service-key [krypto service]
  (if-let [service-key  (get (:key-cache krypto) service)]
    (d/success-deferred service-key)
    (load-service-key krypto service)))

(defn validate-token [krypto token]
  (if-let [claims (unsign krypto token)]
    (let [key (get-service-key krypto (:sub claims))]
      (d/chain key (fn [km] (if (= (:key claims) (:service-key-str km))
                              (assoc claims :key-map km)
                              nil))))
    nil))

(defn clear-cache [krypto]
  (reset! (:key-cache krypto) {}))

(defn encrypt [key d]
  (crypto/encode-base64-as-str (crypto/encrypt key d ec-cipher)))

