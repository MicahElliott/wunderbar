(defproject wunder "0.3.0-SNAPSHOT"
  :description "Wunderbar Weather Widget: status bar and shell prompt"
  :url "http://github.com/MicahElliott/wunderbar"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [environ "1.1.0"]
                 [http-kit "2.2.0"]
                 [org.clojure/data.json "0.2.6"]
                 ;; [com.taoensso/timbre "4.10.0"]
                 [environ "1.1.0"]]
  :plugins [[lein-environ "1.1.0"]
            [lein-codox "0.10.3"]]
  ;; :profiles {:dev {:env {:timbre-level "info"
  ;;                        :wunderloc "97005"}}}
  :main wunder.core
  :aot [wunder.core])
