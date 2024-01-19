(ns starter.browser
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))



(defn init []
  
  [:div])

(defn start []
  (rdom/render [init] (js/document.getElementById "app")))

(start)