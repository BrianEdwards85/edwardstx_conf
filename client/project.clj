(defproject us.edwardstx/conf-client "0.4.5"
  :description "Client for getting service config from config service"
  :url "https://github.com/BrianEdwards85/edwardstx_conf"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]
                 [org.clojure/data.json "0.2.6"]
                 [aleph "0.4.4-alpha1"]

                 [buddy "1.2.0"]
                 [clj-crypto "1.0.2"
                  :exclusions [org.bouncycastle/bcprov-jdk15on bouncycastle/bcprov-jdk16]]

                 [lock-key "1.4.1"]
                 [yogthos/config "0.8"]
                 [digest "1.4.5"]])
