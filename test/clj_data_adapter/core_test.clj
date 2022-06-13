(ns clj-data-adapter.core-test
  (:require [clojure.test :refer :all]
            [clj-data-adapter.core :refer :all]))

(deftest transform-keys-test
  (testing "converts snake cased str to kebab cased keyword"
    (is (= {:my-message "something" :payload {:my-name "Lenin"}}
           (transform-keys snake-str->kebab-key {"my_message" "something", "payload" {"my_name" "Lenin"}}))))
  (testing "converts kebab cased key to snake cased str"
    (is (= {"my_message" "something", "payload" {"my_name" "Lenin"}}
           (transform-keys kebab-key->snake-str {:my-message "something" :payload {:my-name "Lenin"}}))))
  (testing "converts snake cased key to kebab cased sr"
    (is (= {"my-message" "something", "payload" {"my-name" "Lenin"}}
           (transform-keys snake-key->kebab-str {"my_message" "something" :payload {"my_name" "Lenin"}}))))
  (testing "coverts snake cased str from vector to kebab cased keyword"
    (is (= [{:my-message "something" :payload {:my-name "Lenin"}}{:my-age 43}]
           (transform-keys snake-str->kebab-key [{"my_message" "something", "payload" {"my_name" "Lenin"}} {"my_age" 43}]))))
  (testing "converts kebab cased keys from vector to snake cased str"
    (is (= [{"my_message" "something", "payload" {"my_name" "Lenin"}}{"my_age" 43}]
           (transform-keys kebab-key->snake-str [{:my-message "something" :payload {:my-name "Lenin"}}{:my-age 43}]))))
  (testing "converts snaked keys in a list to kebab cased keys"
    (is (= '({:id 1, :name "croissant", :unit-grams 200, :price 5.40M})
           (transform-keys snake-key->kebab-key '({:id 1, :name "croissant", :unit_grams 200, :price 5.40M})))))
  (testing "converts kebab cased keys to namespaced keys"
    (is (= {:test/id 1, :test/name "croissant", :test/unit-grams 200, :test/price 5.40M}
           (transform-keys (partial kebab-key->namespaced-key "test") {:id 1, :name "croissant", :unit-grams 200, :price 5.40M}))))
  (testing "converts kebab cased keys to namespaced keys recursively"
    (is (= {:test/id 1, :test/status {:test/status-active true
                                      :test/status-name "opened"}}
           (transform-keys (partial kebab-key->namespaced-key "test") {:id 1, :status {:status-active true
                                                                                       :status-name "opened"}}))))
  (testing "converts kebab cased keys in a vector to namespaced keys"
    (is (= [{:test/id 1, :test/name "croissant", :test/unit-grams 200, :test/price 5.40M}
            {:test/id 4, :test/price 9.40M}]
           (transform-keys
             (partial kebab-key->namespaced-key "test")
             [{:id 1, :name "croissant", :unit-grams 200, :price 5.40M}
              {:id 4, :price 9.40M}]))))
  (testing "converts namespaced keys to kebab cased keys"
    (is (= {:id 1, :name "croissant", :unit-grams 200, :price 5.40M}
           (transform-keys namespaced-key->kebab-key {:test/id 1, :test/name "croissant", :test/unit-grams 200, :test/price 5.40M}))))
  (testing "converts namespaced keys in a vector to kebab cased keys"
    (is (= [{:id 1, :name "croissant", :unit-grams 200, :price 5.40M}
            {:id 4, :price 9.40M}]
           (transform-keys
             namespaced-key->kebab-key
             [{:test/id 1, :test/name "croissant", :test/unit-grams 200, :test/price 5.40M}
              {:test/id 4, :test/price 9.40M}])))))