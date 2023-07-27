(ns squint-hiccup-example.core
  (:require
   [cheshire.core :as json]
   [hiccup.page :as hpage]
   [hiccup.util :refer [raw-string]]
   [hiccup2.core :as hiccup]
   [ring.adapter.jetty :as jetty]
   [ring.util.response :as response]
   [squint.compiler :as squint]))

(defn render-hiccup [hiccup]
  (-> (hiccup/html (hpage/doctype :html5) hiccup)
      str
      (response/response)
      (response/content-type "text/html")))

(def squint-core-js-url
  "https://cdn.jsdelivr.net/npm/squint-cljs@0.0.12/core.js")

(def squint-import
  [:script {:type "importmap"}
   (raw-string
    (json/encode
     {:imports {"squint-cljs/core.js" squint-core-js-url}}))])

(def squint-code
  '(let [by-id #(document.getElementById %1)
         append! (fn [parent children]
                   (let [children (if (seqable? children) children [children])]
                     (doseq [c children]
                       (.appendChild parent c)))
                   nil)
         tag (fn [name children]
               (doto (document.createElement name)
                 (append! children)))
         text (fn [& xs]
                (document.createTextNode (apply str xs)))]
       (append! (by-id "main")
                (tag "p"
                     (tag "ul"
                          (for [i (range 10)]
                            (tag "li" (text "hello " i))))))))

(comment
  (println
   "\n----------------------------------------\n"
   (squint/compile-string
    (pr-str squint-code)))
  )

(def squint-script
  [:script {:type "module"}
   (raw-string
    (squint/compile-string
     (pr-str squint-code)))])

(defn handler [req]
  (render-hiccup
   [:html
    [:head
     squint-import
     squint-script]
    [:body
     [:div#main
      "squint-hiccup-example"]]]))

(defn -main [& args]
  (jetty/run-jetty #'handler {:port 3000, :join? false}))

(comment
  (def server (-main))
  (.stop server)
  )
