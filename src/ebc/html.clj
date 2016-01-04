; eBooks Collection ebc - html
; Functions that use enlive to generate html

; Copyright (c) 2014 - 2016 Burkhardt Renz, THM. All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php).
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.

(ns ebc.html
  (:require [ebc.consts :as c]
            [ebc.util :refer :all]
            [net.cgrand.enlive-html :refer :all]))

#_(set! *warn-on-reflection* true)

;; Helper that writes html page with the encoding of the operating system

(defn
  fs-encoding
  "Encoding of names in the file system according to platform.
   This is quite a mess.
   We use 
   ISO-8859-1 for Windows,
   UTF-8      for all other operating systems."
  []
  (let [osname (System/getProperty "os.name")]
    (if (.startsWith osname "Windows")
      "ISO-8859-1"
      "UTF-8")))
  
(defn spit-page
  "Writes enlive page with the encoding of the operating system"
  [path page]
    (spit path (reduce str page) :encoding (fs-encoding)))

;; Generates header
(defsnippet header "ebc-header.html" [:header]
  [title supertitle]
  [:#title]      (content title)
  [:#supertitle] (content supertitle)
  [:#title2]     (content (str "generated by ebc " (now))))
  
;; Generates footer
(defsnippet footer "ebc-footer.html" [:footer]
  [])

;; Generates navigation
;  first a link
;  then a section
; then the whole navigation
(defsnippet nav-link "ebc-nav.html" [[:.nav-link (nth-of-type 1)]]
  [{:keys [caption html]}]
  [:a] (do-> (content caption) (set-attr :href html)))

(defsnippet nav-section "ebc-nav.html" {[:.nav-title] [[:.nav-link (nth-of-type 1)]]}
  [{:keys [title links]}]
  [:.nav-title] (content title)
  [:.nav-link]  (substitute (map nav-link links)))

(defsnippet nav "ebc-nav.html" [:nav]
  [nav-data]
  [:nav]  (content (map nav-section nav-data)))

;; Generates result page from search
; first a link
; then  the s-links section
; finally the whole page

(defsnippet slink "ebc-links.html" [:.slink]
  [{:keys [author path title ext size date] :as result}]
  [:#score]  (content (format "%1.2f" (:_score (meta result))))
  [:b]       (content author)
  [:a]       (set-attr :href path)
  [:#title]  (content title)
  [:#ext]    (content ext)
  [:#size]   (content size)
  [:#date]   (content date))
  
(defsnippet slinks "ebc-links.html" [:#search-links]
  [{:keys [title links]}]
  [:#criteria] (content title)
  [:.slink]    (substitute (map slink links)))

(deftemplate results "ebc-base.html"
  [title nav-data s-links]
  [:meta]        (set-attr :content (str "text/html; charset=" (fs-encoding)))
  [:title]       (content title)
  [:header]      (substitute (header title "\u00a0"))
  [:nav]         (substitute (nav nav-data))
  [:links]       (substitute (slinks s-links))
  [:footer]      (substitute (footer)))
  
;; Generates index page

(defsnippet index-p "ebc-index.html" [:links]
  [])

(deftemplate indexp "ebc-base.html"
  [title nav-data]
  [:meta]        (set-attr :content (str "text/html; charset=" (fs-encoding)))
  [:title]       (content title)
  [:header]      (substitute (header title "\u00a0"))
  [:nav]         (substitute (nav nav-data))
  [:links]       (substitute (index-p))
  [:footer]      (substitute (footer)))
  
;; Generate author pages

(defsnippet alink "ebc-links.html" [:.alink]
  [{:keys [author path title type ext size date]} prev]
  [:b]       (if (= author (:author prev)) (content "--:") (content (str author ":")))
  [:a]       (set-attr :href path)
  [:#important] (if (= type "x") (content " "))
  [:#title]  (content title)
  [:#ext]    (content ext)
  [:#size]   (content size)
  [:#date]   (content date))

(defsnippet alink-sec "ebc-links.html" {[:#author-links :> :h2] [:#author-links :> :p]}
  [{:keys [title links]}]
  [:h2]      (do-> (content title) (set-attr :id "sec"))
  [:p]       (content (map alink links (cons {:author ""} links))))

(deftemplate alinks "ebc-base.html"
  [title nav-data a-links]
  [:meta]        (set-attr :content (str "text/html; charset=" (fs-encoding)))
  [:title]       (content title)
  [:header]      (substitute (header title "\u00a0"))
  [:nav]         (substitute (nav nav-data))
  [:links]       (content (map alink-sec a-links))
  [:footer]      (substitute (footer)))

;; Generate title pages

(defsnippet tlink "ebc-links.html" [:.tlink]
  [{:keys [path title type sort-authors ext size date]}]
  [:a]       (set-attr :href path)
  [:#title]  (content title)
  [:#important] (if (= type "x") (content " "))
  [:b]       (content (sort-str sort-authors))
  [:#ext]    (content ext)
  [:#size]   (content size)
  [:#date]   (content date))

(defsnippet tlink-sec "ebc-links.html" {[:#title-links :> :h2] [:#title-links :> :p]}
  [{:keys [title links]}]
  [:h2]      (do-> (content title) (set-attr :id "sec"))
  [:p]       (content (map tlink links)))

(deftemplate tlinks "ebc-base.html"
  [title nav-data t-links]
  [:meta]        (set-attr :content (str "text/html; charset=" (fs-encoding)))
  [:title]       (content title)
  [:header]      (substitute (header title "\u00a0"))
  [:nav]         (substitute (nav nav-data))
  [:links]       (content (map tlink-sec t-links))
  [:footer]      (substitute (footer)))

;; Generate dates pages

(defsnippet dlink "ebc-links.html" [:.dlink]
  [{:keys [path sort-title type sort-authors ext size]}]
  [:a]       (set-attr :href path)
  [:#title]  (content (sort-str sort-title))
  [:#important] (if (= type "x") (content " "))
  [:b]       (content (sort-str sort-authors))
  [:#ext]    (content ext)
  [:#size]   (content size))

(defsnippet dlink-sec "ebc-links.html" {[:#date-links :> :h2] [:#date-links :> :p]}
  [{:keys [title links]}]
  [:h2]      (do-> (content title) (set-attr :id "sec"))
  [:p]       (content (map dlink links)))

(deftemplate dlinks "ebc-base.html"
  [title nav-data d-links]
  [:meta]        (set-attr :content (str "text/html; charset=" (fs-encoding)))
  [:title]       (content title)
  [:header]      (substitute (header title "\u00a0"))
  [:nav]         (substitute (nav nav-data))
  [:links]       (content (map dlink-sec d-links))
  [:footer]      (substitute (footer)))

;; Generate category pages

(defsnippet clink "ebc-links.html" [:.clink]
  [{:keys [path sort-title type sort-authors ext size date]} prev]
  [:a]       (set-attr :href path)
  [:#title]  (content (sort-str sort-title))
  [:#important] (if (= type "x") (content " "))
  [:b]       (if (= (sort-str sort-authors) (sort-str (:sort-authors prev))) 
               (content "--:") (content (str (sort-str sort-authors) ":")))
  [:#ext]    (content ext)
  [:#size]   (content size)
  [:#date]   (content date))

(defsnippet clink-sec "ebc-links.html" {[:#subject-links :> :h2] [:#subject-links :> :p]}
  [{:keys [title links]}]
  [:h2]      (do-> (content (sort-str title)) (set-attr :id "sec"))
  [:p]       (content (map clink links (cons {:sort-authors (sort-key "")} links))))

(deftemplate clinks "ebc-base.html"
  [[title supertitle] nav-data c-links]
  [:meta]        (set-attr :content (str "text/html; charset=" (fs-encoding)))
  [:title]       (if (empty? title) (content supertitle) (content (str supertitle ", " title)))
  [:header]      (if (empty? title) (substitute (header supertitle "\u00a0"))
                   (substitute (header (str "--, " title) supertitle)))
  [:nav]         (substitute (nav nav-data))
  [:links]       (content (map clink-sec c-links))
  [:footer]      (substitute (footer)))
