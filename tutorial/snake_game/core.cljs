(ns snake_game.core
  (:require [reagent.core :as reagent :refer [atom]]
            [snake-game.view :as view]
            [re-frame.core :as rf]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce move-snake (js/setInterval #(rf/dispatch [:move]) 100))

(defn run
  []
  (rf/dispatch-sync [:initialize])
  (reagent/render-component [view/game-component]
                            (. js/document (getElementById "app"))))

(run)

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
