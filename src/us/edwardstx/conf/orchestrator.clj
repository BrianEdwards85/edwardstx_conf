(ns us.edwardstx.conf.orchestrator
  (:require [us.edwardstx.conf.krypto :as krypto]
            [com.stuartsierra.component :as component]
            [us.edwardstx.conf.parser :refer [sharpen]]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [manifold.deferred :as d]
            [us.edwardstx.conf.data.conf :refer [get-service-conf]]
            ))

(defrecord Orchestrator [db krypto]
  component/Lifecycle

  (start [this]
    this)

  (stop [this]
    this))

(defn new-orchestrator []
  (map->Orchestrator {}))

(defn get-conf [db service]
  (d/chain (get-service-conf db service)
           sharpen
           json/write-str))

(defn encrypt-conf [{:keys [db krypto]} conf {:keys [key-map] :as claims}]
  (if (and conf claims)
    (krypto/encrypt (:service-key key-map) conf)
    nil))

(defn validate-token-and-encrypt-conf [{:keys [db krypto] :as orchestrator} service token]
  (let [conf (get-conf db service)
        claims (krypto/validate-token krypto token)]
    (d/chain (d/zip conf claims)
             (fn [x] (if (= (-> x second :sub) service) x nil))
             #(apply encrypt-conf (conj % orchestrator)))))
