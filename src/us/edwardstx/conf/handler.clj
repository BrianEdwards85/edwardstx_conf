(ns us.edwardstx.conf.handler
  (:require [compojure.core :refer [GET defroutes]]
            [manifold.deferred :as d]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [compojure.route :refer [not-found resources]]))

(def semaphore (d/deferred))

(defn root [r]
  "Hello World.")

(defroutes routes
  (GET "/" [] root))

(def app (-> routes wrap-cookies wrap-reload))
