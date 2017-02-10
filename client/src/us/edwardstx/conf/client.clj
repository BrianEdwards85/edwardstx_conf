(ns us.edwardstx.conf.client
  (:require [clojure.data.json :as json]
            [digest :refer [md5]]
            [config.core :refer [env]]
            [lock-key.core :as crypt]
            [byte-streams :as bs]
            [aleph.http :as http]))

(defn get-conf-impl []
  (let [secret (:conf-secret env)
        m (md5 secret)
        s (:service-name env)
        headers {:headers {:keyhash m}}]
    (-> (str (:conf-host env) s)
        (http/get headers)
        deref
        :body
        bs/to-string
        (crypt/decrypt-from-base64 secret)
        (json/read-str :key-fn keyword))))

(def get-conf (memoize get-conf-impl))

