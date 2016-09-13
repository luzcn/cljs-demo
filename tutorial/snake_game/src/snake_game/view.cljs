(ns snake-game.view
  (:require [re-frame.core :as rf]
            [reagent.core]
            [snake-game.handler]))

(defn render-game []
  (let [board (rf/subscribe [:board])
        snake-body (rf/subscribe [:snake])
        point (rf/subscribe [:point])]
    (fn []
      (let [[width height] @board
            snake-positions (into #{} @snake-body)
            current-point @point]
        [:table.stage {:style {:height 377
                               :width 527}}
         [:tbody
          (for [y (range height)]
            ^{:key (str y)}
            [:tr
             (for [x (range width)]
               (cond
                 (snake-positions [x y]) ^{:key (str x)} [:td.snake-on-cell]
                 (= current-point [x y]) ^{:key (str x)} [:td.point]
                 :else ^{:key (str x)} [:td.cell]))])]]))))


(defn score
  "Renders player's score"
  []
  (let [points (rf/subscribe [:scores])]
    (fn []
      [:div.score (str "Score: " @points)])))

(defn game-over
  "Renders the game over overlay if the game is finished"
  []
  (let [game-state (rf/subscribe [:game-status])]
    (fn []
      (if-not @game-state
        [:div
         [:div.overlay
          [:div.play {:on-click #(rf/dispatch [:initialize])}
           [:h1 "↺"]]]]))))


(defn game-component []
  (fn []
    [:div
     [score]
     [game-over]
     [render-game]]))