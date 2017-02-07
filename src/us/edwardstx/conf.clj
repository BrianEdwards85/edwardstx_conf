(ns us.edwardstx.conf
  (:require [aleph.http :as http]
            [us.edwardstx.conf.handler :refer [app semaphore]])
  (:gen-class))

(defn create-server [p]
  (http/start-server app {:port p}))

(defonce server-atom (atom nil))

(defn start-or-restart-server [p]
  (swap! server-atom (fn [old-server]
                       (when old-server (.close old-server))
                       (create-server p))))

(defn stop-server []
  (swap! server-atom (fn [old-server]
                       (when old-server (.close old-server))
                       nil)))

(defn -main [& args]
  (start-or-restart-server 8888)
  @semaphore
  (stop-server)
  )
