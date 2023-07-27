(ns squint-hiccup-example.core
  (:require
   [cheshire.core :as json]
   [hiccup.page :as hpage]
   [hiccup2.core :as hiccup]
   [hiccup.util :refer [raw-string]]
   [ring.adapter.jetty :as jetty]
   [ring.util.response :as response]
   [squint.compiler :as squint]))

(defn render-hiccup [hiccup]
  (-> (hiccup/html (hpage/doctype :html5) hiccup)
      str
      (response/response)
      (response/content-type "text/html")))

(def squint-core-js-url
  "https://cdn.jsdelivr.net/npm/squint-cljs@0.0.12/lib/cljs_core.js")

(def squint-import
  [:script {:type "importmap"}
   (raw-string
    (json/encode
     {:imports {"squint-cljs/core.js" squint-core-js-url}}))])

(def squint-js
  (squint/compile-string
   (pr-str
    '(println (+ 1 1)))))

(def squint-module
  [:script {:type "module"}
   (raw-string squint-js)])

(defn handler [req]
  (render-hiccup
   [:html
    [:head
     squint-import
     squint-module]
    [:body
     "squint-hiccup-example"]]))

(defn -main [& args]
  (jetty/run-jetty #'handler {:port 3000, :join? false}))

(comment
  (def server (-main))
  (.stop server)
  )
