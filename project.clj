(defproject overnight "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2138"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]]
  :plugins [[lein-cljsbuild "1.0.1"]
            [lein-npm "0.2.0"]]
  :node-dependencies
         [[express "3.4.7"]
          [ ws "0.4.31"]]
  :cljsbuild {
    :builds [{
      :source-paths ["src/"]
      :compiler { :output-to "app.js"
                  :target :nodejs
                  :optimizations :simple
                  :pretty-print true }}]})
