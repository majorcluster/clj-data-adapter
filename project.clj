(defproject org.clojars.majorcluster/clj-data-adapter "0.8.2"
  :description "A Clojure data adapter library to convert your data between your layers"
  :url "https://github.com/mtsbarbosa/clj-data-adapter"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :source-paths ["src/clj"]
  :profiles {:dev {:plugins [[com.github.clojure-lsp/lein-clojure-lsp "1.3.17"]]
                   :dependencies [[nubank/matcher-combinators "3.8.3"]]}}
  :aliases {"diagnostics"     ["clojure-lsp" "diagnostics"]
            "format"          ["clojure-lsp" "format" "--dry"]
            "format-fix"      ["clojure-lsp" "format"]
            "clean-ns"        ["clojure-lsp" "clean-ns" "--dry"]
            "clean-ns-fix"    ["clojure-lsp" "clean-ns"]
            "lint"            ["do" ["diagnostics"]  ["format"] ["clean-ns"]]
            "lint-fix"        ["do" ["format-fix"] ["clean-ns-fix"]]})
