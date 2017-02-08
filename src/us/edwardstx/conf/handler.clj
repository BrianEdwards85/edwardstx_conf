(ns us.edwardstx.conf.handler
  (:require [compojure.core :refer [GET defroutes]]
            [manifold.deferred :as d]
            [ring.middleware.reload :refer [wrap-reload]]
            [us.edwardstx.conf.orchestrator :refer [get-encrypted-conf]]
            [compojure.route :refer [not-found]]))

(def semaphore (d/deferred))

(def notfound {:status 404 :body "not found"})

(defn encrypted-conf [r]
  (let [conf-name (get-in r [:route-params :id])
        keyhash (get-in r [:headers "keyhash"])]
    (d/chain
     (get-encrypted-conf conf-name)
     (fn [{:keys [md5 conf]}]
       (if (= keyhash md5)
         conf
         notfound))))) 

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/conf/:id" [id] encrypted-conf)
  (not-found notfound))

(def app
  (wrap-reload app-routes))
