(ns us.edwardstx.conf.server
  (:require [com.stuartsierra.component :as component]
            [aleph.http :as http]
            [clojure.tools.logging :as log]))

(defrecord Http-Server [http-server handler env]
  component/Lifecycle

  (start [this]
    (let [server (http/start-server (:http-handler handler) env)]
      (do
        (log/info (str "Started server on " (:port env)))
        (assoc this :http-server server))))

  (stop [this]
    (.close http-server)
    (assoc this :http-server nil )))

(defn block-on-server [http-server]
  (-> http-server :handler :semaphore deref))

(defn new-http-server [env]
  (map->Http-Server {:env (select-keys env [:port])}))


