(ns snake-game.utils
  (:require [re-frame.core :as rf]))


(def key-code->move
  "Mapping from the integer key code to the direction vector corresponding to that key"
  {38 [0 -1]
   40 [0 1]
   39 [1 0]
   37 [-1 0]})

(defn generate-random-point
  [snake [width height]]
  (let [snake-positions (into #{} (:body snake))
        board-positions (for [x (range width) y (range height)]
                          [x y])]
    (when-let [free-rand-pos (seq (remove snake-positions board-positions))]
      (rand-nth free-rand-pos))))


(defn move-snake
  "Move the whole snake based on positions and directions for each snake body segments"
  [{:keys [direction body]} point]
  (let [[x y] (map #(+ %1 %2) direction (first body))
        [direct-x direct-y] direction
        head-new-position (cond
                            (and (= direct-x 1) (= x 35)) [0 y]
                            (and (= direct-x -1) (= x -1)) [35 y]
                            (and (= direct-y 1) (= y 25)) [x 0]
                            (and (= direct-y -1) (= y -1)) [x 25]
                            :else [x y])]
    (cond
      (= head-new-position point) (do
                                    (rf/dispatch [:generate-point])
                                    (rf/dispatch [:get-score])
                                    (into [] (cons head-new-position body)))
      ((into #{} body) head-new-position) (do
                                            (println (str head-new-position))
                                            (rf/dispatch [:game-over])
                                            (into [] (drop-last (cons head-new-position body))))
      :else (into [] (drop-last (cons head-new-position body))))

    ))

(defn change-snake-direction
  "Changes the snake head direction, only when it's perpendicular to the old head direction"
  [[new-x new-y] [x y]]
  (if (or (= x new-x)
          (= y new-y))
    [x y]
    [new-x new-y]))