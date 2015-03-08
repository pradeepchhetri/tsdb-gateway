(defproject tsdb-gateway "0.1.0-SNAPSHOT"
  :description "A gateway to send metrics to opentsdb."
  :url "http://github.com/pradeepchhetri/tsdb-gateway"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-alpha3"]
                 [org.clojure/tools.logging "0.3.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [com.stuartsierra/component "0.2.2"]
                 [com.taoensso/carmine "2.7.1"]])
