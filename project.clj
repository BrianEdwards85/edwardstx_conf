(defproject us.edwardstx/conf "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [clj-time "0.11.0"]
                 [com.rpl/specter "0.13.0"]
                 [yesql "0.5.3"]
                 [org.postgresql/postgresql "9.4.1208.jre7"]
                 [hikari-cp "1.7.5"]
                 [compojure "1.5.1"]
                 [lock-key "1.4.1"]
                 [digest "1.4.5"]
                 [ring/ring-devel "1.5.1"]
                 [yogthos/config "0.8"]
                 [aleph "0.4.2-alpha12"]]

  :main us.edwardstx.conf

  )

