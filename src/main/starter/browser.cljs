(ns starter.browser
   (:require [reagent.core :as r]
             [reagent.dom :as rdom]))
 
 (def *game (r/atom {:board [["_" "_" "_"]
                            ["_" "_" "_"]
                            ["_" "_" "_"]]
                    :moves []
                    :turn "o"}))
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

 (defn update-board [board [c x y]]
   (assoc-in board [x y] c))

 (defn play-move [game x y]
   (let [c (get-in game [:turn])
         move [c x y]
         next-turn (if (= c "o")
                     "x"
                     "o")]
     (-> (update-in game [:moves] conj move)
         (update :board update-board move)
         (assoc :turn next-turn))))

 (comment
   (def *game (r/atom {:board [["_" "_" "_"]
                              ["_" "_" "_"]
                              ["_" "_" "_"]]
                      :moves []
                      :turn "o"}))
   (get-in @*game [:board 1])
   @*game
   (swap! *game update-in [:moves] conj [3 4])

   (swap! *game #(assoc-in % [:moves]  [1 2]))

   (swap! *game #(update-in % [:moves] conj ["o" 1 2]))
   @*game
   (swap! *game update :turn (constantly "x"))
   (get-in @*game [:turn 1])
   (swap! *game #(update-in % [:moves] conj [1 2]))
   
   {:on-click (js/alert "hello")} 
   )
    
 
(defn draw-rectangle [x y]
  (let [x1 (+ 12 (* 98 x))
        y1 (+ 12 (* 98 y))
        width (- 100 15)
        height (- 100 15)]
    [:svg
     [:rect {:x x1
             :y y1
             :width width
             :height height
             :fill "white"
             :stroke "white"
             :onClick (swap! *game play-move y x)}]]))


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
                  :stroke-width 10}]
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
        y-cor 100]

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
              :stroke-width 5}]]
     (for [x (range 3)
           y (range 3)
           :let [cell (get-in board [x y])]]
       (if (= cell "x")
         (draw-cross y x)
         (if (= cell "o")
           (draw-circle y x)
           (draw-rectangle y x)))))))

(defn init []

  [:div
   [:h1 "Noughts & Crosses"]
   [draw-board (:board @*game)]])

(defn start []
  (rdom/render [init] (js/document.getElementById "app")))

(start)