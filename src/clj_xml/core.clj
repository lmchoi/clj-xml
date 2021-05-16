(ns clj-xml.core
  (:require [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip])
  (:import (javax.xml.parsers SAXParserFactory)))

(defn get-in [xml-zipper ks]
  (first (reduce (fn [xz k]
                   (->> xz
                        (filter #(= (:tag %) k))
                        (map :content)
                        (flatten)))
                 xml-zipper
                 ks)))

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
