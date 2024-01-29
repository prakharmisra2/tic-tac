(ns starter.browser
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [starter.events :as sevt]
            [re-frame.core :as rf]
            ))
;;function to draw noughts
(defn draw-circle [x y]
  (let [cx (+ 50 (* x 100))
        cy (+ 50 (* y 100))
        r 30]
    [:svg
     [:circle {:cx cx
               :cy cy
               :r r
               :stroke "grey"
               :stroke-width 10
               :fill "white"}]]))

;;HTML component to display winner
(defn display-winner [winn]
  (when winn
    [:div
     [:h1 {:style {:color "red"}} "Winner is " (first winn)]]))

;;function that makes rectangle to make the place click-able on the svg component
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
             :onClick #(rf/dispatch [::sevt/play-move y x])}]]))
;;function to draw cross
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
;;function to draw game board 
(defn draw-board1 []
  (let [size 300
        x-ini 10
        y-ini 10
        x-cor 100
        y-cor 100
        board @(rf/subscribe [::sevt/load-board])
        winner @(rf/subscribe [::sevt/load-winner])]

    (into
     [:svg {:viewBox "0 0 900 900"
            :margin "10%"
            }
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
     (into
      (vec (for [x (range 3)
                 y (range 3)
                 :let [cell (get-in board [x y])]]
             (if (= cell "x")
               (draw-cross y x)
               (if (= cell "o")
                 (draw-circle y x)
                 (when-not winner (draw-rectangle y x))))))
      (when winner          ;draw line when winner is decided 
        (let [[_ [y1 x1] [y2 x2]] winner
              xini (+ 50 (* x1 100))
              yini (+ 50 (* y1 100))
              xend (+ 50 (* x2 100))
              yend (+ 50 (* y2 100))] [:svg
                                       [:line {:x1 xini
                                               :y1 yini
                                               :x2 xend
                                               :y2 yend
                                               :stroke "green"
                                               :stroke-width 4}]]))))))
;;function to check for 'draw' in the match and display it.
(defn match-draw [board] 
  (let [winner @(rf/subscribe [::sevt/load-winner])]
    (when (and
         (every? #(not= "_" %) (flatten board))
         (not winner))
    [:div 
     [:h1 {:style {:color "blue"}} [:b "Match draw -- GAME OVER"]]])))

(defn init []

  [:div 
   {
    :margin "10%"
    :padding "10%"
   }
   
   [:h1 "Noughts & Crosses"] 
   [:input {:type "button"
            :value "turn of O"
            :on-click #(rf/dispatch [::sevt/update-turn "o"])}]

   [:input {:type "button"
            :value "turn of X"
            :on-click #(rf/dispatch [::sevt/update-turn "x"])}]

   [:input {:type "button"
            :value "reset!"
            :on-click #(rf/dispatch [::sevt/load-game])}]

   
   [display-winner @(rf/subscribe [::sevt/load-winner])]

   [match-draw @(rf/subscribe [::sevt/load-board])]

   [draw-board1]])

(defn start []
  (rf/dispatch [::sevt/load-game])      ;load the initial game 
  (rdom/render [init] (js/document.getElementById "app"))
  )

(start)