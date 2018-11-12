(ns game-of-life.core
  (:require
   [game-of-life.life :as l]))


(defn matrix->grid [matrix]
    (mapcat (fn [r row]
              (map #(vector r %1 %2) (range) row))
            (range) matrix))

(def matrix
    [[0 0 0 0]
     [0 1 0 0]
     [0 1 0 0]
     [0 0 0 0]])

(defn render [canvas cells]
  (js/console.log "cells: " cells)
  (let [ctx (.getContext canvas "2d")
        nrow 4
        ncol 4
        rectWidth (/ (.-width canvas) ncol)
        rectHeight (/ (.-height canvas) nrow)]

    (set! (.-fillStyle ctx) "green")
    (doseq [[y x v] (filter #(< 0 (last %)) cells)]
          (.fillRect ctx (* x rectWidth) (* y rectHeight)  rectWidth rectHeight))))

(defn clear-canvas [canvas]
  (.clearRect (.getContext canvas "2d")
   0 0
   (.-width canvas)
   (.-height canvas)))

(defn main-loop 
  [canvas life]
  (js/console.log "game loop")
  (clear-canvas canvas)
  (let [new-life (l/update-life life)]
    (render canvas (:cells life))
    (js/setTimeout #(main-loop canvas new-life) 1000)))
    

(defn main []
  (def canvas (.getElementById js/document "canvas"))
  (def init-life (l/new-life 4 4 matrix))
  (main-loop canvas init-life))

(set! (.-onload js/window) main)


(comment
  (.-offsetWidth canvas)
  (def matrix [[0 0 true] [1 1 true][2 2 true][3 3 true]]))



;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (js/console.log "start"))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (js/console.log "init")
  (start))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop")
  (and canvas (clear-canvas canvas)))

