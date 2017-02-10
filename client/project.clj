(defproject us.edwardstx.conf/client "0.3.2"
  :description "Client for getting service config from config service"
  :url "https://github.com/BrianEdwards85/edwardstx_conf"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [aleph "0.4.2-alpha12"]
                 [lock-key "1.4.1"]
                 [yogthos/config "0.8"]
                 [digest "1.4.5"]])
