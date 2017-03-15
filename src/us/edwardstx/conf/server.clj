(ns us.edwardstx.conf.server
  (:require [com.stuartsierra.component :as component]
            [aleph.http :as http]
            [clojure.tools.logging :as log]))

(defrecord Http-Server [http-server handler env]
  component/Lifecycle

  (start [this]
      (assoc this
             :http-server (http/start-server (:http-handler handler) env)))

  (stop [this]
    (.close http-server)
    (assoc this :http-server nil )))

(defn block-on-server [http-server]
  (-> http-server :handler :semaphore deref))

(defn new-http-server [env]
  (map->Http-Server {:env (select-keys env [:port])}))


