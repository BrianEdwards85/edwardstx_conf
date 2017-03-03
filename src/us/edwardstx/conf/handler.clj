(ns us.edwardstx.conf.handler
  (:require [compojure.core :refer [GET POST defroutes]]
            [manifold.deferred :as d]
            [byte-streams :as bs]
;;            [ring.middleware.reload :refer [wrap-reload]]
            [us.edwardstx.conf.orchestrator :refer [get-encrypted-conf validate-token-get-encrypt-conf]]
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

(defn get-conf [r]
  (let [conf-name (get-in r [:route-params :id])
        key (-> r :body bs/to-string)]
    (d/chain
     (validate-token-get-encrypt-conf conf-name key)
     #(if-let [resp %]
        resp
        notfound))))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (POST "/api/v1/conf/:id" [id] get-conf )
  (GET "/conf/:id" [id] encrypted-conf)
  (not-found notfound))

(def app app-routes)
;;  (wrap-reload app-routes))
