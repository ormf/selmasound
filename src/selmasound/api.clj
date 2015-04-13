(ns selmasound.api
  (:import [java.lang.management ManagementFactory])
  (:require [clojure.stacktrace]
            [selmasound.samples.selmasound]
            [selmasound.samples.ssound search-results]
            [selmasound.samples.ssound url]
            )
   (:use ;; [overtone.live]
         [overtone.helpers.ns])
  )

(defn immigrate-selmasound-api []
  (immigrate ;; 'overtone.live
             'selmasound.samples.selmasound
;;             'selmasound.samples.ssound.search_results
;;             'selmasound.samples.ssound.url
             ))

(immigrate-selmasound-api)
