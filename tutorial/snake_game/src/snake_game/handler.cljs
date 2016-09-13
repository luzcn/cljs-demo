(ns snake-game.handler
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]
            [snake-game.utils :as utils]
            [plumbing.core :refer-macros [letk]]
            [goog.events :as events]))


(def board [35 25])
(def snake {:direction [1 0]
            :body [[3 2] [2 2] [1 2] [0 2]]})

;; register handlers
(rf/reg-event-db
  :initialize
  (fn [db _]
    (merge db {:board board
               :snake snake
               :point (utils/generate-random-point snake board)
               :scores 0
               :game-running true})))


(rf/reg-event-db
  :move
  (fn [db _]
    (letk [[snake point game-running] db]
      (if game-running
        (assoc-in db [:snake :body] (utils/move-snake snake point))
        db))))


(rf/reg-event-db
  :change-direction
  (fn [db [_ new-direction]]
    (assoc-in db [:snake :direction] (utils/change-snake-direction new-direction (-> db :snake :direction)))))

(rf/reg-event-db
  :generate-point
  (fn [db _]
    (assoc-in db [:point] (utils/generate-random-point (:snake db) (:board db)))))

(rf/reg-event-db
  :get-score
  (fn [db _]
    (assoc-in db [:scores] (inc (:scores db)))))

(rf/reg-event-db
  :game-over
  (fn [db _]
    (assoc-in db [:game-running] false)))

;;Register global event listener for keydown event.
;;Processes key strokes according to `utils/key-code->move` mapping
(defonce key-handler
         (events/listen js/window "keydown"
                        (fn [e]
                          (let [key-code (.-keyCode e)]
                            (if (contains? utils/key-code->move key-code)
                              (rf/dispatch [:change-direction (utils/key-code->move key-code)])
                              (rf/dispatch [:game-over]))))))

;; register subs
(rf/reg-sub
  :snake
  (fn [db _]
    (-> db :snake :body)))

(rf/reg-sub
  :board
  (fn [db _]
    (:board db)))

(rf/reg-sub
  :point
  (fn [db _]
    (:point db)))

(rf/reg-sub
  :scores
  (fn [db _]
    (:scores db)))

(rf/reg-sub
  :game-status
  (fn [db _]
    (:game-running db)))