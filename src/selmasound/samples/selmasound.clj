(ns ^{:doc "An API for interacting with the selma online sample resource."
      :author "Sam Aaron, Kevin Neaton"}
  selmasound.samples.selmasound
  (:use [selmasound.samples.ssound.url]
        [selmasound.samples.ssound.search-results]
        [overtone.sc.node]
        [overtone.helpers.lib :only [defrecord-ifn]])
  (:require [clojure.data.json :as json]
            [overtone.libs.asset :as asset]
            [overtone.sc.sample :as samp]))

;;; (def ^:dynamic *api-key* "47efd585321048819a2328721507ee23")
(def ^:dynamic *ss-api-key* "830a6d38f780418a95f2520cbb2802bb")

(defrecord-ifn SelmasoundSample
  [id size n-channels rate status path args name selmasound-id]
  samp/sample-player
  to-sc-id*
  (to-sc-id [this] (:id this)))

(derive SelmasoundSample :overtone.sc.sample/playable-sample)

(defmethod print-method SelmasoundSample [b w]
  (.write w (format "#<selmasound[%s]: %d %s %fs %s %d>"
                    (name @(:status b))
                    (:selmasound-id b)
                    (:name b)
                    (:duration b)
                    (cond
                     (= 1 (:n-channels b)) "mono"
                     (= 2 (:n-channels b)) "stereo"
                     :else (str (:n-channels b) " channels"))
                    (:id b))))

(defn- url-with-key
  "Appends the api_key to a url. Takes an optional map of params."
  [url & [params]]
  (let [params (assoc params :api_key *ss-api-key*)]
    (build-url url params)))

(def ^:private base-url "http://selma.hfmdk-frankfurt.de/selma/sample-lib")

(defn- selmasound-url
  "Generate a selmasound.org api url. Accepts an optional map of query-params as the last argument."
  [& url-tail]
  (apply str base-url url-tail))

(defn- slurp-json
  "Slurp and read the json asset."
  [f]
  (json/read-json (slurp f)))

(def ^:private slurp-json-mem
  "A memoized version of slurp-json."
  (memoize slurp-json))

(defn- slurp-json-asset
  "Download, cache, and slurp-json."
  [url]
  (slurp-json (asset/asset-path url)))

;; ## Sound Info
(defn- info-url
  "Generate a selmasound url for fetching a json datastructure representing the
  info for a given id."
  [id]
  (selmasound-url "/samples/" id))

(defn selmasound-info
  "Returns a map containing information pertaining to a particular selmasound.
  The selmasound id may be specified as an integer or string."
  [id]
  (slurp-json-asset (info-url id)))

;; ## Sound Serve
(defn- sound-serve-url
  "Generate a selmasound url for fetching the original audio file by id."
  [id]
  (selmasound-url "/samples/" id))

(defn selmasound-path
  "Download, cache, and persist the selmasound audio file specified by
  id. Returns the path to a cached local copy of the audio file."
  [id]
  (let [url  (sound-serve-url id)]
    (asset/asset-path url id)))

(defn selmasound-sample
  "Download, cache and persist the selmasound audio file specified by
   id. Creates a buffer containing the sample loaded onto the server and
   returns a playable sample capable of playing the sample when called
   as a fn."
  [id & args]
  (let [path      (selmasound-path id)
        smpl      (apply samp/load-sample path args)
        free-smpl (assoc smpl :selmasound-id (hash id))
        ]
    (map->SelmasoundSample free-smpl)))

(defn selmasound
  "Download, cache and persist the selmasound audio file specified by
   id. Creates a buffer containing the sample loaded onto the server and
   returns a playable sample capable of playing the sample when called
   as a fn."
  [id & args]
  (apply selmasound-sample id args))

;; ## Pack Info
(defn- pack-info-url
  [id]
  (selmasound-url "/packs/" id))

(defn selmasound-pack-info
  "Get information about a selmasound sample pack. Returns a map of pack
  properties for the given pack id."
  [id]
  (slurp-json-asset (pack-info-url id)))

;; ## Pack Serve
(defn- pack-serve-url
  "Selmasound url for fetching a zipped sample pack by id."
  [id]
  (selmasound-url "/packs/" id "/serve"))

(defn selmasound-pack-dir
  "Download, cache, and persist all of the sounds in the selmasound sample pack
  specified by id. Returns the path to a local directory containing the cached
  audio files."
  [id]
  (let [url (pack-serve-url id)]
  (asset/asset-bundle-dir url)))

;; ## Sound Search
(defn- search-url
  "Generate a selmasound url for fetching a json datastructure representing the
  the results of a search."
  [params]
  (selmasound-url "/sounds/search" params))

(defn- normalize-search-args
  "Takes a sequence of search args and returns a map of params for use with
  selmasound-search*"
  ([] (normalize-search-args nil))
  ([args]
     (let [ks (first args)
           ks (when (coll? ks)
                ks)
           args (if ks
                  (rest args)
                  args)
           q (first args)
           q (when (string? q)
               q)
           args (if q
                  (rest args)
                  args)]
       (-> (apply hash-map args)
           (assoc :ks ks :sounds_per_page 100)
           (update-in [:q] #(or q % ""))))))

(defn- selmasound-search*
  [params]
  (let [ks     (:ks params)
        params (if ks
                 (assoc params
                   :ks nil
                   :fields (name (first ks)))
                 params)
        url    (search-url params)
        next   (next-fn *ss-api-key* params)
        resp   (api-seq url slurp-json-mem next)
        count  (:num_results (first resp))
        sounds (lazy-cats (map :sounds resp))
        sounds (if ks
                 (map #(get-in % ks) sounds)
                 sounds)]
    (search-results count sounds)))

(defn selmasound-search
  "Search selmasound.org. Returns an instance of SearchResults containing a
  LazySeq over the sounds matching your query. Makes a single api call to get
  the first page of the results and the total number of matches. Additional api
  calls will be made as necessary as the LazySeq is realized. Use #'count to get
  the number total number of sounds without realizing the entire seq.

  Examples:

  Search for sounds matching the query \"kick drum\"

   (selmasound-search :q \"kick drum\")
   (selmasound-search \"kick drum\") ;same as above.

  Use a keyseq to filter the results per #'clojure.core/get-in

   (selmasound-search [:id] \"kick drum\")

  Get just the ids for all of the sounds in the pack called \"MISStereoPiano\"
  matching the query \"LOUD\".

   (selmasound-search [:id] \"LOUD\" :f \"pack:MISStereoPiano\"

  For more information about search params see...
  http://www.selmasound.org/docs/api/resources.(first ks)html#sound-search-resource"
  {:arglists '([ks* q* & params])}
  [& args]
  (selmasound-search* (normalize-search-args args)))

(defmacro selmasound-searchm
  "Search selmasound.org and expand the results at macro expansion time."
  {:arglists '([ks* q* & params])}
  [& args]
  ;(println "Compiling selmasound search results...")
  (let [params (normalize-search-args args)
        search (selmasound-search* params)]
    (dorun search)
    `[~@search]))

(defn selmasound-search-paths
  "Search and download. Downloads a caches the sound file matching your search
  query. Returns a collection of local file paths to the cached sound files."
  {:arglists '([query* & params])}
  [& args]
  (let [params (-> (normalize-search-args args)
                   (assoc :fields "id")
                   (dissoc :ks))]
    (map (fn [sound]
           (let [url  (sound-serve-url (:id sound))
                 name (:original_filename sound)]
             (asset/asset-path url name)))
         (selmasound-search* params))))
