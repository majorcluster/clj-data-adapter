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

(defn- split-upper-case-groups
  [s]
  (reduce (fn [splitted char-n]
            (let [is-first (empty? splitted)
                  count-converted (count splitted)
                  splitted-latest (dec count-converted)
                  str-when-not-nil #(when-not (nil? %) (str %))
                  count-latest (count (-> splitted
                                          last))
                  latest-single (<= count-latest 1)
                  latest-uppercase (when-not is-first (re-matches #"^[A-Z]$" (-> splitted
                                                                                  last
                                                                                  last
                                                                                  str-when-not-nil)))
                  str-char (str char-n)
                  current-uppercase (re-matches #"^[A-Z]$" str-char)]
              (cond is-first (conj splitted str-char)
                    (or (and latest-uppercase current-uppercase)
                        (and latest-uppercase (not current-uppercase) latest-single)
                        (and (not latest-uppercase) (not current-uppercase))) (assoc splitted splitted-latest
                                                                                              (-> splitted
                                                                                                  (get splitted-latest)
                                                                                                  (str str-char)))
                    :else (conj splitted str-char))))
          [] s))

(defn camel-cased-key->kebab-key
  [k]
  (let [named-key (name k)
        len (count named-key)]
    (if (<= len 1) (keyword (str/lower-case named-key))
                   (->> named-key
                        split-upper-case-groups
                        (map (fn [s-group]
                               (str/lower-case s-group)))
                        (reduce #(str %1 "-" %2))
                        keyword))))

(defn kebab-key->namespaced-key
  "Converts kebab cased to namespaced keys"
  [namespace k]
  (-> k
      name
      (add-namespace namespace)
      keyword))

(defn kebab-key->snake-str
  "Converts kebab key cased to str snake cased"
  [k]
  (-> k
      name
      (str/replace "-" "_")))

(defn namespaced-key->kebab-key
  "Converts namespaced keys to kebab cased key"
  [k]
  (-> k
      name
      (str/split #".*\/")
      last
      keyword))

(defn snake-key->kebab-key
  "Converts a key snake cased to kebab cased key"
  [k]
  (-> k
      name
      (str/replace "_" "-")
      keyword))

(defn snake-key->kebab-str
  "Converts a snake key cased to str kebab cased"
  [k]
  (-> k
      name
      (str/replace "_" "-")))

(defn snake-str->kebab-key
  "Converts string snake cased to keyword kebab cased"
  [s]
  (-> s
      (str/replace "_" "-")
      keyword))

(defn str->uuid
  [_ v]
  (cond (is-uuid? v) (UUID/fromString v)
        :else v))

(defn uuid->str
  [_ v]
  (cond (uuid? v) (.toString v)
        :else v))

(defn- custom-get-in
  [m col default-val]
  (let [mapped (reduce (fn [acc-m el]
                          (cond (fn? el) (assoc acc-m ::fn el)
                                :else (->> el
                                           (conj (::selectors acc-m))
                                           (assoc acc-m ::selectors))))
                        {::selectors []
                         ::fn nil}
                        col)
        extraction-fn (fn [val-so-far]
                        (cond (nil? (::fn mapped)) val-so-far
                              :else ((::fn mapped) val-so-far)))
        apply-default (fn [val-so-far]
                        (if (nil? val-so-far) default-val val-so-far))]
    (-> m
        (get-in (::selectors mapped))
        extraction-fn
        apply-default)))

(defn transform
 "Recursively transform map m using placeholder-map as a source
 for extracting values and building it
 ex. (transform {:a {:name :a-name} :b \"Fixed value\" :c-name [:c :name]} {:a-name \"Aaay\" :c {:name \"C\"}})
    => {:a {:name \"Aaay\"} :b \"Fixed value\" :c-name \"C\"}
 ex. (transform {:a-first-name [:a :names 0] :a-last-name [:a :names last]} {:a {:names [\"Aaay\" \"the letter\"]}})\n
    => {:a-first-name \"Aaay\", :a-last-name \"the letter\"}"
 ([acc-m placeholder-map m]
  (reduce (fn [acc-m [k v]]
            (cond (keyword? v) (assoc acc-m k (get m v v))
                  (map? v) (assoc acc-m k (transform {} v m))
                  (coll? v) (assoc acc-m k (custom-get-in m v v))
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
