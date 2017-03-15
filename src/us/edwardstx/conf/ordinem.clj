(ns us.edwardstx.conf.ordinem
  (:require [com.stuartsierra.component :as component]
            [us.edwardstx.conf.parser :refer [sharpen]]
            [us.edwardstx.conf.data.conf :refer [get-service-conf]]))


(defrecord Ordinem [env db conf]
  component/Lifecycle

  (start [this]
    (assoc this :conf
     (assoc
      (merge env
             (sharpen @(get-service-conf db (:service-name env))))
      :service-id (str (java.util.UUID/randomUUID)))))

  (stop [this]
        (assoc this :conf nil)))

(defn new-ordinem [env]
  (map->Ordinem {:env env}))
