(defproject us.edwardstx/conf "0.2.5"
  :description "Edwardstx.us configuration service"
  :url "https://github.com/BrianEdwards85/edwardstx_conf"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [clj-time "0.11.0"]

                 [yesql "0.5.3"]
                 [org.postgresql/postgresql "9.4.1208.jre7"]
                 [hikari-cp "1.7.5"]

                 [yogthos/config "0.8"]
                 [com.rpl/specter "0.13.0"]
                 [compojure "1.5.1"]
                 [aleph "0.4.3"]
                 [com.stuartsierra/component "0.3.2"]

                 [buddy "1.2.0"]
                 [clj-crypto "1.0.2"
                  :exclusions [org.bouncycastle/bcprov-jdk15on bouncycastle/bcprov-jdk16]]

                 [com.novemberain/langohr "4.1.0"]

                 [org.clojure/tools.logging "0.3.1"]
                 [org.apache.logging.log4j/log4j-core "2.7"]
                 [org.apache.logging.log4j/log4j-slf4j-impl "2.7"]
                 [org.springframework.amqp/spring-rabbit "2.0.0.M2"
                  :exclusions [org.springframework/spring-web org.springframework/spring-tx]]]
  :repositories [["spring.milestone" "https://repo.spring.io/libs-milestone"]]

  :uberjar-name "conf.jar"

  :main us.edwardstx.conf
  :profiles {:uberjar {:aot :all}
             :dev {:resource-paths ["env/dev/resources" "resources"]
                   :env {:dev true}} }

  )

