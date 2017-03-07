(ns us.edwardstx.conf
  (:require [config.core :refer [env]]
            [com.stuartsierra.component :as component]
            [us.edwardstx.conf.data.conf :as db-conf]
            [us.edwardstx.conf.data.db :as db]
            [us.edwardstx.conf.krypto :as krypto]
            [us.edwardstx.conf.server :as server]
            [us.edwardstx.conf.handler :refer [new-handler]]
            [us.edwardstx.conf.orchestrator :as orchestrator]))


(defn init-system [env]
  (component/system-map
   :db (db/new-database env)
   :krypto (component/using
            (krypto/new-krypto)
            [:db])
   :orchestrator (component/using
                  (orchestrator/new-orchestrator)
                  [:db :krypto])
   :handler (component/using
             (new-handler)
             [:orchestrator])
   :server (component/using
            (server/new-http-server env)
            [:handler])
   ))

