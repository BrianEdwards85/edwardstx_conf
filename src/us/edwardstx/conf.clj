(ns us.edwardstx.conf
  (:require [aleph.http :as http]
            [config.core :refer [env]]
            [clojure.tools.nrepl.server :as nrepl]
            [us.edwardstx.conf.handler :refer [app semaphore]])
  (:gen-class))


(defn create-server [p]
  (http/start-server app {:port p}))

(defonce http-server-atom (atom nil))
(defonce nrepl-server-atom (atom nil))

(defn start-or-restart-server [p]
  (swap! http-server-atom (fn [old-server]
                       (when old-server (.close old-server))
                       (create-server p))))

(defn stop-server []
  (swap! http-server-atom (fn [old-server]
                       (when old-server (.close old-server))
                       nil)))

(defn -main [& args]
  (start-or-restart-server (:http-port env))
  (reset! nrepl-server-atom (nrepl/start-server :port (:nrepl-port env)))
  @semaphore
  (stop-server)
  (nrepl/stop-server @nrepl-server-atom)
  )
