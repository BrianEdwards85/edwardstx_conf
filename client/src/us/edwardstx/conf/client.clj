(ns us.edwardstx.conf.client
  (:require [clojure.data.json :as json]
            [clojure.string :as s]
            [clojure.java.io :as io]
            [digest :refer [md5]]
            [lock-key.core :as crypt]
            [byte-streams :as bs]
            [aleph.http :as http]))

(def secret
  (delay
   (-> "key.txt"
       io/resource
       io/file
       slurp
       s/trim)))

(defn get-conf-impl [s]
  (let [m (md5 @secret)
        headers {:headers {:keyhash m}}]
    (-> (str "https://conf.edwardstx.us/conf/" s)
        @(client/get headers)
        :body
        bs/to-string
        (crypt/decrypt-from-base64 @secret)
        (json/read-str :key-fn keyword))))

(def get-conf (memoize get-conf-impl))

