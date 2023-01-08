(ns clj-data-adapter.core
  (:require [clojure.walk :as walk]
            [clojure.string :as str])
  (:import (java.util UUID)))

(def uuid-pattern
  #"^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")

(defn is-uuid?
  "checks if is a string and matches uuid pattern"
  [id]
  (cond (string? id) (re-matches uuid-pattern id)
        :else false))

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

(defn str->uuid
  [_ v]
  (cond (is-uuid? v) (UUID/fromString v)
        :else v))

(defn uuid->str
  [_ v]
  (cond (uuid? v) (.toString v)
        :else v))
(defn transform
 "Recursively transform map m using placeholder-map as a source
 for extracting values and building it
 ex. (transform {:a {:name :a-name} :b \"Fixed value\"} {:a-name \"Aaay\"})
    => {:a {:name \"Aaay\"} :b \"Fixed value\"}"
 ([acc-m placeholder-map m]
  (reduce (fn [acc-m [k v]]
            (cond (keyword? v) (assoc acc-m k (get m v v))
                  (map? v) (assoc acc-m k (transform {} v m))
                  (fn? v) (assoc acc-m k (v m k))
                  :else (assoc acc-m k v)))
          acc-m placeholder-map))
  ([placeholder-map m]
   (transform {} placeholder-map m)))

(defn transform-keys
  "Recursively transforms all map keys in coll with the transform-fn [k], when result of fn is nil removes the kv"
  [transform-fn coll]
  (letfn [(transform [x] (if (map? x)
                           (into {} (map (fn [[k v]]
                                           (if-let [new-key (transform-fn k)]
                                             [new-key v])) x))
                           x))]
    (walk/postwalk transform coll)))

(defn transform-values
  "Recursively transforms all map values in coll with the transform-fn [k v], when result of fn is nil removes the kv"
  [transform-fn coll]
  (letfn [(transform [x] (if (map? x)
                           (into {} (map (fn [[k v]]
                                           (if-let [value (transform-fn k v)]
                                             [k value])) x))
                           x))]
    (walk/postwalk transform coll)))
