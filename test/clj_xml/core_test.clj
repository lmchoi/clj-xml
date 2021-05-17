(ns clj-xml.core-test
  (:require [clojure.test :refer :all]
            [clj-xml.core :as xml]))

(defn- create-child [some-value]
  {:attrs   nil
   :content some-value
   :tag     :child})

(defn- create-xml-zipper [some-value]
  [{:attrs   nil
    :content some-value
    :tag     :node}
   nil])

(deftest xml-str->zipper-test
  (let [no-content-zipper (create-xml-zipper nil)]
    (testing "Has no content"
      (is (= no-content-zipper
             (xml/xml-str->zipper "<node></node>")))
      (is (= no-content-zipper
             (xml/xml-str->zipper "<node/>"))))
    (testing "Content at root level"
      (is (= (create-xml-zipper ["some-value"])
             (xml/xml-str->zipper "<node>some-value</node>"))))
    (testing "Nested content"
      (is (= (create-xml-zipper [{:attrs   nil
                                  :content ["some-value"]
                                  :tag     :child}])
             (xml/xml-str->zipper (str "<node><child>some-value</child></node>")))))
    (testing "Multiple children"
      (let [children [(create-child ["A"])
                      (create-child ["B"])]]
        (is (= (create-xml-zipper children)
               (xml/xml-str->zipper (str "<node><child>A</child><child>B</child></node>"))))))))

(deftest get-in-test
  (let [child-node (create-child ["some-value"])]
    (testing "Tag not found"
      (is (= nil
             (xml/get-in (create-xml-zipper ["some-value"]) [:not-exist]))))
    (testing "Nested tag not found"
      (is (= nil
             (xml/get-in (create-xml-zipper ["some-value"]) [:not-exist :also-not-exist]))))
    (testing "Has no content"
      (is (= nil
             (xml/get-in (create-xml-zipper nil) [:node]))))
    (testing "Content at root level"
      (is (= "some-value"
             (xml/get-in (create-xml-zipper ["some-value"]) [:node]))))
    (testing "Get grandchild content"
      (is (= "some-value"
             (xml/get-in (create-xml-zipper [child-node]) [:node :child]))))
    (testing "Get node with nested content"
      (is (= child-node
             (xml/get-in (create-xml-zipper [child-node]) [:node]))))
    (testing "get-in will only return the first child"
      (is (= "A"
             (xml/get-in (create-xml-zipper [(create-child ["A"])
                                             (create-child ["B"])])
                         [:node :child]))))))

(deftest get-all-test
  (testing "No content found"
    (is (= []
           (xml/get-all [(create-child ["A"])]
                        :not-this-tag))))
  (testing "Get all content for the given tag"
    (is (= ["A" "B" "C"]
           (xml/get-all [(create-child ["A"])
                         (create-child ["B"])
                         (create-child ["C"])]
                        :child))))
  (testing "Get nested content"
    (is (= [{:attrs nil, :content ["A"], :tag :child}
            {:attrs nil, :content ["B"], :tag :child}]
           (xml/get-all [(create-child [(create-child ["A"])])
                         (create-child [(create-child ["B"])])]
                        :child))))
  (testing "Should get content of the given tag only"
    (is (= ["A" "B"]
           (xml/get-all [(create-child ["A"])
                         {:attrs   nil
                          :content ["not a node"]
                          :tag     :not-this-tag}
                         (create-child ["B"])]
                        :child))))
  (testing "Should not get nested node"
    (is (= ["A" "B"]
           (xml/get-all [(create-child ["A"])
                         {:attrs   nil
                          :content [(create-child "should not get this child")]
                          :tag     :not-this-tag}
                         (create-child ["B"])]
                        :child)))))
