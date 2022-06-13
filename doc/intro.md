# Documentation for clj-data-adapter

## Functions
- <a id='k-k-to-n-k'></a> **kebab-key->namespaced-key** [namespace k] <br>
  - Converts kebab cased to namespaced keys
  - namespace ^string : namespace in string format
  - k ^keyword : keyword in kebab-key format to be converted
  - returns ^keyword : keyword with prepended `namespace` + `/`
- <a id='k-k-to-s-s'></a> **kebab-key->snake-str** [k] <br>
    - Converts kebab key cased to str snake cased
    - k ^keyword : keyword in kebab-key format to be converted
    - returns ^string : str in snake key format
- <a id='n-k-to-k-k'></a> **namespaced-key->kebab-key** [k] <br>
    - Converts namespaced keys to kebab cased key
    - k ^keyword : keyword in namespaced-key format to be converted
    - returns ^keyword : keyword with namespace removed
- <a id='s-k-to-k-k'></a> **snake-key->kebab-key** [k] <br>
    - Converts a key snake cased to kebab cased key
    - k ^keyword : keyword in snake case key format to be converted
    - returns ^keyword : keyword in kebab cased format
- <a id='s-k-to-k-s'></a> **snake-key->kebab-str** [k] <br>
    - Converts a snake key cased to str kebab cased
    - k ^keyword : keyword in snake case key format to be converted
    - returns ^string : str in kebab case format
- <a id='s-k-to-k-k'></a> **snake-str->kebab-key** [s] <br>
    - Converts string snake cased to keyword kebab cased
    - s ^string : str in snake case format to be converted
    - returns ^keyword : keyword in kebab case format
- <a id='transform-keys'></a> **transform-keys** [transform-key-fn coll]
  - Recursively transforms all map keys in coll with the transform-key-fn and optional namespace.
  - transform-key-fn ^fn : function receiving key as parameter and converting it
  - coll ^collection : collection being single map itself or a collection of maps to get keys transformed recursively
```clojure
(transform-keys kebab-key->snake-str {:my-message "something" :payload {:my-name "Lenin"}})
; => {"my_message" "something", "payload" {"my_name" "Lenin"}}

(transform-keys (partial kebab-key->namespaced-key "test") {:id 1, :name "croissant", :unit-grams 200, :price 5.40M})
; => {:test/id 1, :test/name "croissant", :test/unit-grams 200, :test/price 5.40M}
```

