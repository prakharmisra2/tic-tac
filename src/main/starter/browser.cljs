(ns starter.browser
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))
(def game (r/atom {:board [["x" "_" "_"]
                               ["_" "o" "x"]
                               ["_" "_" "_"]]
                       :move []
                       :turn "o"}))

(comment
  
  (get-in @game [:board 1]) 
  (swap! game update-in [:move] conj [3 4])

  (swap! game #(assoc-in % [:move]  [1 2]))
  

  (swap! game #(update-in % [:move] conj [1 2]))
  
  )

(defn draw-circle [x y]
  (let [cx (+ 50 (* x 100))
        cy (+ 50 (* y 100))
        r 30]
    [:svg 
     [:circle {:cx cx
               :cy cy
               :r r
               :stroke "grey"
               :stroke-width 10}]
     [:circle {:cx cx
               :cy cy
               :r (- r 5)
               :fill "white"}]]))

(defn draw-cross [x y]
  (let [x1 (+ 20 (* 100 x))
        y1 (+ 20 (* 100 y))
        x2 (+ 80 (* 100 x))
        y2 (+ 80 (* 100 y))]
    [:svg [:line {:x1 x1
                  :y1 y1
                  :x2 x2
                  :y2 y2
                  :stroke "red"
                  :stroke-width 10
                  }]
     [:line {:x1 x1
             :y1 y2
             :x2 x2
             :y2 y1
             :stroke "red"
             :stroke-width 10}]]))
(defn draw-board [board]
  (let [size 300
        x-ini 10
        y-ini 10
        x-cor 100
        y-cor 100
        x 1
        y 1]
    
    (into 
     [:svg {:viewBox "0 0 700 600"}
      [:rect  {:x            x-ini
               :y            y-ini
               :width        (- size x-ini)
               :height       (- size y-ini)
               :fill         "white"
               :stroke       "black"
               :stroke-width 1}]
      [:line {:x1   x-ini
              :y1   y-cor
              :x2   size
              :y2   y-cor
              :stroke "black"
              :stroke-width 5}]
      [:line {:x1   x-ini
              :y1   (* y-cor 2)
              :x2   size
              :y2   (* y-cor 2)
              :stroke "black"
              :stroke-width 5}]
      [:line {:x1   x-cor
              :y1   y-ini
              :x2   x-cor
              :y2   size
              :stroke "black"
              :stroke-width 5}]
      [:line {:x1   (* x-cor 2)
              :y1   y-ini
              :x2   (* x-cor 2)
              :y2   size
              :stroke "black"
              :stroke-width 5}]
      ]
     (for [x (range 3)
           y (range 3)
           :let [cell (get-in board [x y])]]
       (if (= cell "x")
         (draw-cross y x)
         (if (= cell "o")
           (draw-circle y x)
           nil)))
     
     )))





(defn init []
  
  [:div 
   [:h1 "Noughts & Crosses"]
   [draw-board (:board @game)]
   
   ])

(defn start []
  (rdom/render [init] (js/document.getElementById "app")))

(start)