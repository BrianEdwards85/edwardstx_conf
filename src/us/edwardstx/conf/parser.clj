(ns us.edwardstx.conf.parser
  (:require [clojure.string :as str]
            [com.rpl.specter :as s]))

(defn split-keys [k]
  (if (vector? k)
    (split-keys (first k))
    (str/split k #"\.")))

(defn first-key [k]
  (-> k split-keys first keyword))

(defn rest-key [k]
  (str/join "." (rest (split-keys k))))

(defn key-type [k]
  (if (= 1 (count (split-keys (first k)))) :tk :sk))

(defn rehydrate [v] (apply hash-map (mapcat identity v)))

(defn keyword-map [m]
  (apply hash-map (mapcat (fn [x] [(keyword (first x)) (second x)]) m)))

(defn sharpen [m]
  (let [{:keys [tk sk]} (group-by key-type m)]
    (keyword-map
     (rehydrate
      (concat
       tk
       (s/transform [s/ALL s/LAST]
                    #(sharpen
                      (rehydrate
                       (s/transform [s/ALL s/FIRST] rest-key %)))
                    (group-by first-key sk)))))))

