(ns clj-xml.core
  (:require [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip])
  (:import (javax.xml.parsers SAXParserFactory)))

(defn get-all-content [xml-zipper tag]
  "Returns an array of contents of all the elements found for a given tag."
  {:added "0.1.0"}
  (->> xml-zipper
       (filter #(= (:tag %) tag))
       (map :content)
       (flatten)))

(defn get-content-in [xml-zipper tags]
  "Returns the nested content in a xml elements zipper,
   where ks is a sequence of tags. Returns nil if the tag
   is not present."
  {:added "0.1.0"}
  (first (reduce get-all-content
                 xml-zipper
                 tags)))

(declare newSAXParser parse)
(defn- non-validating [s ch]
  (..
    (doto
      (SAXParserFactory/newInstance)
      (.setFeature
        "http://apache.org/xml/features/nonvalidating/load-external-dtd" false))
    (newSAXParser)
    (parse s ch)))

(defn xml-str->zipper [xml-str]
  "Returns a zipper for xml elements (as from xml/parse, each
   containing :tag, :attrs and :content) for a given a xml string"
  {:added "0.1.0"}
  (-> xml-str
      (.getBytes)
      (io/input-stream)
      (xml/parse non-validating)
      (zip/xml-zip)))
