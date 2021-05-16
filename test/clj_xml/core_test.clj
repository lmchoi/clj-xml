(ns clj-xml.core-test
  (:require [clojure.test :refer :all]
            [clj-xml.core :as xml]))

(let [no-content-zipper [{:attrs   nil
                          :content nil
                          :tag     :node}
                         nil]
      root-value "some-value"
      root-content-zipper [{:attrs   nil
                            :content [root-value]
                            :tag     :node}
                           nil]]
  (deftest empty-content->zipper
    (is (= no-content-zipper (xml/xml-str->zipper "<node></node>")))
    (is (= no-content-zipper (xml/xml-str->zipper "<node/>"))))

  (deftest root-tag->zipper
    (is (= root-content-zipper
           (xml/xml-str->zipper (str "<node>" root-value "</node>")))))

  (deftest get-in-root-level
    (is (= root-value (xml/get-in root-content-zipper [:node])))
    (is (= nil (xml/get-in no-content-zipper [:node])))))
