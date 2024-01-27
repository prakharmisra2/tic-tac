(ns starter.events
  (:require [re-frame.core :as rf]))

(def ^:private empty-game
  {:board [["_" "_" "_"]
           ["_" "_" "_"]
           ["_" "_" "_"]]
   :moves []
   :turn "o"
   :winner nil})

(rf/reg-event-fx
 ::load-game
 (fn  [{:keys [db]} _]
   (js/console.log "load-game")
   {:db (merge db empty-game)}))

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

(defn update-board [board [c x y]]
  (assoc-in board [x y] c))

(rf/reg-event-db 
 ::play-move 
 (fn [db [_ x y]]
   (let [c (get-in db [:turn])
         move [c x y]
         next-turn (if (= c "o")
                     "x"
                     "o")
         new-board (update-board (:board db) move)]
     (-> db
         (update-in [:moves] conj move)
         (assoc :board new-board)
         (assoc :turn next-turn)
         (assoc :winner (winner new-board)))))
 )

;event for update-turn
(rf/reg-event-db
 ::update-turn 
 (fn [db [_ turn]]
   (assoc db :turn turn)))

