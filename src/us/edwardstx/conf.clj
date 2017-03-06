(ns us.edwardstx.conf
  (:require [config.core :refer [env]]
            [com.stuartsierra.component :as component]
            [us.edwardstx.conf.data.conf :as db-conf]
            [us.edwardstx.conf.data.db :as db]))


(defn init-system [env]
  (component/system-map
   :db (db/new-database env)))

