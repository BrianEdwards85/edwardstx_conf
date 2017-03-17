(ns us.edwardstx.conf
  (:require [config.core :refer [env]]
            [com.stuartsierra.component :as component]
            [us.edwardstx.conf.data.conf :as db-conf]
            [us.edwardstx.conf.data.db :as db]
            [us.edwardstx.conf.krypto :as krypto]
            [us.edwardstx.conf.server :as server]
            [us.edwardstx.conf.handler :refer [new-handler]]
            [us.edwardstx.conf.ordinem :refer [new-ordinem]]
            [us.edwardstx.conf.logging :refer [new-logging]]
            [us.edwardstx.conf.orchestrator :as orchestrator])
  (:gen-class))

(defonce system (atom {}))

(defn init-system [env]
  (component/system-map
   :db (db/new-database env)
   :conf (component/using
          (new-ordinem env)
          [:db])
   :logging (component/using
             (new-logging)
             [:conf])
   :krypto (component/using
            (krypto/new-krypto)
            [:db])
   :orchestrator (component/using
                  (orchestrator/new-orchestrator)
                  [:db :krypto])
   :handler (component/using
             (new-handler)
             [:orchestrator :conf])
   :server (component/using
            (server/new-http-server env)
            [:handler])
   ))

(defn -main [& args]
  (reset! system (init-system env))
  (swap! system component/start)
  (deref (get-in @system [:handler :semaphore]))
  (component/stop @system)
  (shutdown-agents))

