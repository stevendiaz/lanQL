(defproject lanql "0.1.0-SNAPSHOT"
  :description "lanQL implementation written in Clojure with Lacinia"
  :url "https://github.com/stevendiaz/lanql"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.3.443"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [org.postgresql/postgresql "42.1.4"]
                 [com.stuartsierra/component "0.3.2"]
                 [com.walmartlabs/lacinia "0.25.0"]
                 [com.walmartlabs/lacinia-pedestal "0.7.0"]
                 [alaisi/postgres.async "0.8.0"]
                 [io.aviso/logging "0.2.0"]
                 [factory-time "0.1.2"]
                 [clj-time "0.14.4"]
                 [honeysql "0.9.3"]])
