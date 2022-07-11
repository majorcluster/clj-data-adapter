# clj-data-adapter

[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.majorcluster/clj-data-adapter.svg)]

A Clojure library designed to have handy data conversions
Particularly useful when transferring data between ports (external sources) into your logic layers and vice-versa

## Usage

* Add the dependency:
```clojure
[org.clojars.majorcluster/clj-data-adapter "LAST RELEASE NUMBER"]
```

## Publish
### Requirements
* Leiningen (of course 😄)
* GPG (mac => brew install gpg)
* Clojars account
* Enter clojars/tokens page in your account -> generate one and use for password
```shell
export GPG_TTY=$(tty) && lein deploy clojars
```


## Documentation
[Read the docs](https://github.com/mtsbarbosa/clj-data-adapter/tree/main/doc/intro.md)
