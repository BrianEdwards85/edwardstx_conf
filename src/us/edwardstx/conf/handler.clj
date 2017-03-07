(ns us.edwardstx.conf.handler
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log]
            [us.edwardstx.conf.orchestrator :as orchestrator]
            [compojure.core :refer [GET POST routes]]
            [byte-streams :as bs]
            [manifold.deferred :as d]))

(def notfound {:status 404 :body "not found"})

(defn wrap-orchestrator [orchestrator semaphore handler]
  (fn [request]
    (handler (assoc request
                    :orchestrator orchestrator
                    :semaphore semaphore))))

(denf resp-or-notfound [x]
      (if x
        x
        notfound))

(defn get-conf [r]
  (let [conf-name (get-in r [:route-params :id])
        key (-> r :body bs/to-string)
        orchestrator (:orchestrator r)]
    (d/chain (orchestrator/validate-token-and-encrypt-conf orchestrator conf-name key)
             resp-or-notfound)))


(def app-routes []
  (routes
   (POST "/api/v1/conf/:id" [id] get-conf )
   (GET "/api/v1/health" "Healthy")))

(defrecord Handler [http-handler semaphore orchestrator]
  component/Lifecycle

  (start [this]
    (->> (app-routes)
         (wrap-orchestrator orchestrator semaphore)
         (assoc this :semaphore semaphore :http-handler)))

  (stop [this]
    (assoc this :http-handler nil)))

(defn new-handler []
  (map->Handler {}))

