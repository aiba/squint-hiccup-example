(ns squint-hiccup-example.core
  (:require
   [hiccup.page :as hpage]
   [hiccup2.core :as hiccup]
   [ring.adapter.jetty :as jetty]
   [ring.util.response :as response]))

(defn render-hiccup [hiccup]
  (-> (hiccup/html (hpage/doctype :html5) hiccup)
      str
      (response/response)
      (response/content-type "text/html")))

(defn handler [req]
  (render-hiccup
   [:html
    [:body
     "hello world"]]))

(defn -main [& args]
  (jetty/run-jetty #'handler {:port 3000, :join? false}))

(comment
  (def server (-main))
  (.stop server)
  )
