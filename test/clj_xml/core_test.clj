(ns clj-xml.core-test
  (:require [clojure.test :refer :all]
            [clj-xml.core :as xml]))

(let [some-value "the-content"
      root-content-zipper [{:attrs   nil
                            :content [some-value]
                            :tag     :node}
                           nil]
      no-content-zipper [{:attrs   nil
                          :content nil
                          :tag     :node}
                         nil]]
  (deftest empty-content->zipper
    (is (= no-content-zipper (xml/xml-str->zipper "<node></node>")))
    (is (= no-content-zipper (xml/xml-str->zipper "<node/>"))))

  (deftest root-tag->zipper
    (is (= root-content-zipper
           (xml/xml-str->zipper (str "<node>" some-value "</node>")))))

  (deftest get-in-root-level
    (is (= nil (xml/get-in no-content-zipper [:node])))
    (is (= some-value (xml/get-in root-content-zipper [:node])))))

(let [some-value "the-content"
      nested-content-zipper [{:attrs   nil
                              :content [{:attrs   nil
                                         :content ["the-content"]
                                         :tag     :child}]
                              :tag     :node}
                             nil]]
  (deftest nested-tags->zipper
    (is (= nested-content-zipper
           (xml/xml-str->zipper (str "<node><child>" some-value "</child></node>")))))

  (deftest get-in-root-level
    (is (= some-value (xml/get-in nested-content-zipper [:node :child])))
    (is (= {:attrs   nil
            :content ["the-content"]
            :tag     :child} (xml/get-in nested-content-zipper [:node])))))

(let [children [{:attrs   nil
                 :content ["A"]
                 :tag     :child}
                {:attrs   nil
                 :content ["B"]
                 :tag     :child}]
      multiple-children-zipper [{:attrs   nil
                                 :content children
                                 :tag     :node}
                                nil]]
  (deftest multiple-children-tags->zipper
    (is (= multiple-children-zipper
           (xml/xml-str->zipper (str "<node><child>A</child><child>B</child></node>")))))

  (deftest get-in-root-level
    (testing "get-in will only return the first child"
      (is (= "A" (xml/get-in multiple-children-zipper [:node :child])))))

  (deftest get-all-children
    (is (= ["A" "B"] (xml/get-all children :child))))

  (deftest get-current-level-only
    (is (= ["A"] (xml/get-all [{:attrs   nil
                                :content ["A"]
                                :tag     :child}
                               {:attrs   nil
                                :content ["C"]
                                :tag     :not-child}
                               {:attrs   nil
                                :content [{:attrs   nil
                                           :content ["D"]
                                           :tag     :child}]
                                :tag     :not-child}] :child)))))
