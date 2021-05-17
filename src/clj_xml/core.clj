(ns clj-xml.core
  (:require [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip])
  (:import (javax.xml.parsers SAXParserFactory)))

(defn get-all-content [xml-zipper tag]
  (->> xml-zipper
       (filter #(= (:tag %) tag))
       (map :content)
       (flatten)))

(defn get-content-in [xml-zipper ks]
  (first (reduce get-all-content
                 xml-zipper
                 ks)))

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
  (-> xml-str
      (.getBytes)
      (io/input-stream)
      (xml/parse non-validating)
      (zip/xml-zip)))
