(ns starter.browser
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(def ^:private empty-game
  {:board [["_" "_" "_"]
           ["_" "_" "_"]
           ["_" "_" "_"]]
   :moves []
   :turn "o"
   :winner nil})

(def *game (r/atom empty-game))
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
(defn hori-check [board]
  (cond
    (= (map #(get-in board [0 %]) [0 1 2]) ["x" "x" "x"]) ["x" [0 0] [0 2]]
    (= (map #(get-in board [0 %]) [0 1 2]) ["o" "o" "o"]) ["o" [0 0] [0 2]]
    (= (map #(get-in board [1 %]) [0 1 2]) ["x" "x" "x"]) ["x" [1 0] [1 2]]
    (= (map #(get-in board [1 %]) [0 1 2]) ["o" "o" "o"]) ["o" [1 0] [1 2]]
    (= (map #(get-in board [2 %]) [0 1 2]) ["x" "x" "x"]) ["x" [2 0] [2 2]]
    (= (map #(get-in board [2 %]) [0 1 2]) ["o" "o" "o"]) ["o" [2 0] [2 2]]
    :else nil))
(defn vert-check [board]
  (cond
    (= (map #(get-in board [% 0]) [0 1 2]) ["x" "x" "x"]) ["x" [0 0] [2 0]]
    (= (map #(get-in board [% 0]) [0 1 2]) ["o" "o" "o"]) ["o" [0 0] [2 0]]
    (= (map #(get-in board [% 1]) [0 1 2]) ["x" "x" "x"]) ["x" [0 1] [2 1]]
    (= (map #(get-in board [% 1]) [0 1 2]) ["o" "o" "o"]) ["o" [0 1] [2 1]]
    (= (map #(get-in board [% 2]) [0 1 2]) ["x" "x" "x"]) ["x" [0 2] [2 2]]
    (= (map #(get-in board [% 2]) [0 1 2]) ["o" "o" "o"]) ["o" [0 2] [2 2]]
    :else nil))
(defn dig-check [board]
  (cond
    (= (map #(get-in board [% %]) [0 1 2]) ["x" "x" "x"]) ["x" [0 0] [2 2]]
    (= (map #(get-in board [% %]) [0 1 2]) ["o" "o" "o"]) ["o" [0 0] [2 2]]
    (= (map #(get-in board [%1 %2]) [0 1 2] [2 1 0]) ["o" "o" "o"]) ["o" [0 2] [2 0]]
    (= (map #(get-in board [%1 %2]) [0 1 2] [2 1 0]) ["x" "x" "x"]) ["x" [0 2] [2 0]]
    :else nil))
(defn winner [board]
  (let [win (or (hori-check board)
                (vert-check board)
                (dig-check board))]
    win))

(defn display-winner [winn]
  (when winn
    [:div
     [:h1 {:style {:color "red"}} "Winner is " (first winn)]]))

(defn update-board [board [c x y]]
  (assoc-in board [x y] c))


(defn play-move [game x y]
  (let [c (get-in game [:turn])
        move [c x y]
        next-turn (if (= c "o")
                    "x"
                    "o")
        new-board (update-board (:board game) move)]
    (-> game
        (update-in [:moves] conj move)
        (assoc :board new-board)
        (assoc :turn next-turn)
        (assoc :winner (winner new-board)))))

(comment
  (def *game (r/atom {:board [["o" "_" "x"]
                              ["o" "x" "x"]
                              ["x" "_" "o"]]
                      :moves []
                      :turn "o"
                      :winner nil}))
  (get-in @*game [:board 1])
  @*game
  (swap! *game update-in [:moves] conj [3 4])

  (swap! *game #(assoc-in % [:moves]  [1 2]))

  (swap! *game #(update-in % [:moves] conj ["o" 1 2]))
  @*game
  (swap! *game update :turn (constantly "x"))
  (get-in @*game [:turn 1])
  (swap! *game #(update-in % [:moves] conj [1 2])) 
  (js/console.log @*game) 
  
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
             :onClick #(do
                         (swap! *game play-move y x)
                         (js/console.log @*game))}]]))


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
(defn draw-board [board winner]
  (let [size 300
        x-ini 10
        y-ini 10
        x-cor 100
        y-cor 100]

    (into
     [:svg {:viewBox "0 0 400 400"}
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
      (when winner
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
(defn update-turn [game tur]
  (assoc game :turn  tur))

(defn init []

  [:div
   
   [:h1 "Noughts & Crosses"]

   [display-winner (:winner @*game)]

   [:input {:type "button"
            :value "turn of O"
            :on-click #(swap! *game update-turn "o")}]
   
   [:input {:type "button"
            :value "turn of X"
            :on-click #(swap! *game update-turn "x")}]
   
   [:input {:type "button"
            :value "reset!"
            :on-click #(reset! *game empty-game)}]
   
   [draw-board (:board @*game) (:winner @*game)]])

(defn start []
  (rdom/render [init] (js/document.getElementById "app")))

(start)