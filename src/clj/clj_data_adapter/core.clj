(ns clj-data-adapter.core
  (:require [clojure.walk :as walk]
    [clojure.string :as str]))

(defn- add-namespace
  [name namespace]
  (str namespace "/" name))

(defn kebab-key->namespaced-key
  "Converts kebab cased to namespaced keys"
  [namespace k]
  (-> k
      (name)
      (add-namespace namespace)
      (keyword)))

(defn kebab-key->snake-str
  "Converts kebab key cased to str snake cased"
  [k]
  (-> k
      (name)
      (str/replace "-" "_")))

(defn namespaced-key->kebab-key
  "Converts namespaced keys to kebab cased key"
  [k]
  (-> k
      (name)
      (str/split #".*\/")
      (last)
      (keyword)))

(defn snake-key->kebab-key
  "Converts a key snake cased to kebab cased key"
  [k]
  (-> (name k)
      (str/replace "_" "-")
      (keyword)))

(defn snake-key->kebab-str
  "Converts a snake key cased to str kebab cased"
  [k]
  (-> k
      (name)
      (str/replace "_" "-")))

(defn snake-str->kebab-key
  "Converts string snake cased to keyword kebab cased"
  [s]
  (-> s
      (str/replace "_" "-")
      (keyword)))

(defn transform-keys
  "Recursively transforms all map keys in coll with the transform-key-fn and optional namespace."
  [transform-key-fn coll]
  (letfn [(transform [x] (if (map? x)
                           (into {} (map (fn [[k v]] [(transform-key-fn k) v]) x))
                           x))]
    (walk/postwalk transform coll)))

