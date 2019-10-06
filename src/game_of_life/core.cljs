(ns game-of-life.core
  (:require
   [game-of-life.life :as l]
   [reagent.core :as r]
   [impi.core :as impi]))


(defonce matrix (atom [[0 0 0 0]
                       [0 1 0 0]
                       [0 1 0 0]
                       [0 0 0 0]]))

(def CELL-SIZE 20)


(defn make-cell [[row col value]]
  {:pixi.shape/type :pixi.shape.type/rounded-rectangle
      :pixi.shape/position (map #(* CELL-SIZE %) [col row])
      :pixi.shape/size [CELL-SIZE CELL-SIZE]
      :pixi.shape/line
      {:pixi.line/color 0xbbbbbb
       :pixi.line/width 2}
      :pixi.rounded-rectangle/radius 4
      :pixi.shape/fill
      {:pixi.fill/color (if (= 0 value)
                          0xffffff
                          0x000000)}})


(defn make-scene [cells]
  {:pixi/renderer
    {:pixi.renderer/size [400 400]
     :pixi.renderer/background-color 0xbbbbbb}
    :pixi/stage
    {:impi/key :gfx
     :pixi.object/type :pixi.object.type/graphics
     :pixi.object/position [0 0]
     :pixi.graphics/shapes
     (vec (map make-cell cells))}})


(defn render [el cells]
  (js/console.log "cells: " cells)
  (impi/mount :scene (make-scene cells) el))

(defn main-loop
  [el life]
  (js/console.log "game loop")
  (let [new-life (l/update-life life)]
    (render el (:cells life))
    (js/setTimeout #(main-loop el new-life) 1000)))


(defn main []
  (let [el (.getElementById js/document "app")
        init-life (l/new-life 4 4 @matrix)]
    (main-loop el init-life)))

(set! (.-onload js/window) main)


(comment
  (reset! matrix [[0 0 true] [1 1 true][2 2 true][3 3 true]]))


;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (js/console.log "start"))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  (js/console.log "init")
  (start))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop")
  (impi/unmount :scene))

