(defproject us.edwardstx/conf "0.1.0-SNAPSHOT"
  :description "Edwardstx.us configuration service"
  :url "https://github.com/BrianEdwards85/edwardstx_conf"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [yesql "0.5.3"]
                 [org.postgresql/postgresql "9.4.1208.jre7"]
                 [hikari-cp "1.7.5"]
                 [com.rpl/specter "0.13.0"]
                 [lock-key "1.4.1"]
                 [digest "1.4.5"]
                 [yogthos/config "0.8"]
                 [ring/ring-devel "1.5.1"]
                 [compojure "1.5.1"]
                 [aleph "0.4.2-alpha12"]]

  :main us.edwardstx.conf

  )

