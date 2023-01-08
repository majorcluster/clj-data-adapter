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
              {:test/id 4, :test/price 9.40M}]))))
  (testing "when fn returns nil, removes kv"
    (is (= [{:test/id 1, :test/name "croissant", :test/price 5.40M}
            {:test/id 4, :test/price 9.40M}]
           (transform-keys
             #(if (= :test/unit-grams %) nil %)
             [{:test/id 1, :test/name "croissant", :test/unit-grams 200, :test/price 5.40M}
              {:test/id 4, :test/price 9.40M}]))))
  (testing "empties give no ex"
    (is (= {}
           (transform-keys namespaced-key->kebab-key {})))
    (is (= [{}]
           (transform-keys namespaced-key->kebab-key [{}])))
    (is (= nil
           (transform-keys namespaced-key->kebab-key nil)))
    (is (= [nil]
           (transform-keys namespaced-key->kebab-key [nil])))))

(deftest transform-values-test
  (testing "empties give no ex"
    (is (= {}
           (transform-values str->uuid {})))
    (is (= [{}]
           (transform-values str->uuid [{}])))
    (is (= nil
           (transform-values str->uuid nil)))
    (is (= [nil]
           (transform-values str->uuid [nil]))))
  (testing "converts str uuids into uuid"
    (is (= {:name "pita" :id #uuid "4a26cc9f-e854-4f93-b6c5-cda86c48544c"}
           (transform-values str->uuid {:name "pita" :id "4a26cc9f-e854-4f93-b6c5-cda86c48544c"})))
    (is (= [{:name "pita" :id #uuid "4a26cc9f-e854-4f93-b6c5-cda86c48544c"}{:name "croissant"}]
           (transform-values str->uuid [{:name "pita" :id "4a26cc9f-e854-4f93-b6c5-cda86c48544c"}{:name "croissant"}])))
    (is (= [{:name "pita" :id #uuid "4a26cc9f-e854-4f93-b6c5-cda86c48544c"}
            {:name "croissant" :id #uuid "4a26cc9f-e854-4f93-b6c5-cda86c48544a"}]
           (transform-values str->uuid [{:name "pita" :id "4a26cc9f-e854-4f93-b6c5-cda86c48544c"}
                                        {:name "croissant" :id "4a26cc9f-e854-4f93-b6c5-cda86c48544a"}]))))
  (testing "converts uuids into str"
    (is (= {:name "pita" :id "4a26cc9f-e854-4f93-b6c5-cda86c48544c"}
           (transform-values uuid->str {:name "pita" :id #uuid "4a26cc9f-e854-4f93-b6c5-cda86c48544c"})))
    (is (= [{:name "pita" :id "4a26cc9f-e854-4f93-b6c5-cda86c48544c"}{:name "croissant"}]
           (transform-values uuid->str [{:name "pita" :id #uuid "4a26cc9f-e854-4f93-b6c5-cda86c48544c"}{:name "croissant"}])))
    (is (= [{:name "pita" :id "4a26cc9f-e854-4f93-b6c5-cda86c48544c"}
            {:name "croissant" :id "4a26cc9f-e854-4f93-b6c5-cda86c48544a"}]
           (transform-values uuid->str [{:name "pita" :id #uuid "4a26cc9f-e854-4f93-b6c5-cda86c48544c"}
                                        {:name "croissant" :id #uuid "4a26cc9f-e854-4f93-b6c5-cda86c48544a"}]))))
  (testing "converts value based on key"
    (let [my-fn #(cond (= :password %) (str %2 "-hashed")
                       :else %2)]
      (is (= {:name "croissant"}
             (transform-values my-fn {:name "croissant"})))
      (is (= {:name "croissant" :password "open sesame-hashed"}
             (transform-values my-fn {:name "croissant" :password "open sesame"})))
      (is (= [{:name "pita" :password "my-pwd-hashed"}
              {:name "croissant" :password "open sesame-hashed"}]
             (transform-values my-fn [{:name "pita" :password "my-pwd"}
                                      {:name "croissant" :password "open sesame"}])))))
  (testing "when fn returns nil removes kv"
    (is (= {:name "pita" :id #uuid "4a26cc9f-e854-4f93-b6c5-cda86c48544c"}
           (transform-values #(if (= %2 "Lebanon") nil %2)
                             {:name "pita" :id #uuid "4a26cc9f-e854-4f93-b6c5-cda86c48544c" :from "Lebanon"})))
    (is (= {:name "pita" :id #uuid "4a26cc9f-e854-4f93-b6c5-cda86c48544c"}
           (transform-values #(if (= % :from) nil %2)
                             {:name "pita" :id #uuid "4a26cc9f-e854-4f93-b6c5-cda86c48544c" :from "Lebanon"})))
    (is (= [{:name "pita" :id #uuid "4a26cc9f-e854-4f93-b6c5-cda86c48544c"}{:name "croissant"}]
           (transform-values #(if (= %2 "Lebanon") nil %2)
                             [{:name "pita" :id #uuid "4a26cc9f-e854-4f93-b6c5-cda86c48544c" :from "Lebanon"}{:name "croissant"}])))))

(deftest transform-test
  (testing "simple transform"
    (is (= {:name "john baker" :age 54}
           (transform {:name :baker-name
                       :age 54}
                      {:baker-name "john baker"}))))
  (testing "no transform"
    (is (= {:name :baker-name}
           (transform {:name :baker-name}
                      {:baker-full-name "john baker"})))
    (is (= {}
           (transform {}
                      {:baker-full-name "john baker"}))))
  (testing "deep transform with keywords and fixed value"
    (is (= {:bakery {:new {:name "Padoca"
                           :street "Geologicka"}
                     :old {:name "Pekarstvi"}
                     :old-name "Pekarstvi"
                     :fixed-val 10}}
           (transform {:bakery {:new {:name :new-name
                                      :street :new-street}
                                :old {:name :old-name}
                                :old-name :old-name
                                :fixed-val 10}}
                      {:new-name "Padoca"
                       :new-street "Geologicka"
                       :old-name "Pekarstvi"}))))
  (testing "deep transform with keywords, fixed value and fn"
    (is (= {:bakery {:new {:name "Padoca"
                           :street "new-street=Geologicka"}
                     :old {:name "Pekarstvi"}
                     :fixed-val 10}}
           (transform {:bakery {:new {:name :new-name
                                      :street (fn [_ k]
                                                (str "new-" (name k) "=Geologicka"))}
                                :old {:name :old-name}
                                :fixed-val 10}}
                      {:new-name "Padoca"
                       :new-street "Geologicka"
                       :old-name "Pekarstvi"})))))
