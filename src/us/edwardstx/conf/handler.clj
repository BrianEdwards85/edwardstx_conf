(ns us.edwardstx.conf.handler
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [us.edwardstx.conf.orchestrator :as orchestrator]
            [compojure.core :refer [GET POST routes]]
            [compojure.route :refer [not-found]]
            [byte-streams :as bs]
            [manifold.deferred :as d]))

(def notfound {:status 404 :body "not found"})

(defn wrap-orchestrator [orchestrator conf semaphore handler]
  (fn [request]
    (handler (assoc request
                    :orchestrator orchestrator
                    :semaphore semaphore
                    :conf conf))))

(defn resp-or-notfound [x]
      (if x
        x
        notfound))

(defn get-conf [r]
  (let [conf-name (get-in r [:route-params :id])
        key (-> r :body bs/to-string)
        orchestrator (:orchestrator r)]
    (d/chain (orchestrator/validate-token-and-encrypt-conf orchestrator conf-name key)
             resp-or-notfound)))

(defn get-health [{:keys [conf] :as r}]
  (json/write-str {:health "Healthy"
                   :service (:service-name conf)
                   :instance (:service-id conf)}))


(defn app-routes []
  (routes
   (GET "/" [] "Configuration Service")
   (POST "/api/v1/conf/:id" [id] get-conf )
   (GET "/api/v1/health" [] get-health)
   (not-found notfound)))

(defrecord Handler [http-handler semaphore orchestrator conf]
  component/Lifecycle

  (start [this]
    (->> (app-routes)
         (wrap-orchestrator orchestrator (:conf conf) semaphore)
         (assoc this :semaphore semaphore :http-handler)))

  (stop [this]
    (assoc this :http-handler nil)))

(defn new-handler []
  (map->Handler {:semaphore (d/deferred)}))

