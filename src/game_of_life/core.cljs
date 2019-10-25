(ns game-of-life.core
  (:require
   [game-of-life.life :as l]
   [reagent.core :as r]
   [impi.core :as impi]))


(def matrix [[0 0 0 0]
             [0 1 0 0]
             [0 1 0 0]
             [0 0 0 0]])
(def CELL-SIZE 20)

(defonce *state (atom nil))


(defn make-cell [[row col value]]
  (let [x (* CELL-SIZE col)
        y (* CELL-SIZE row)]
    {:impi/key [x y]
     :pixi.object/type :pixi.object.type/graphics
     :pixi.object/position [x y]
     :pixi.object/interactive? true
     :pixi.event/click    [:click [row col] (- 1 value)]
     :pixi.event/mouse-down    [:mouse-down]
     :pixi.event/mouse-up      [:mouse-up]
     :pixi.object/hit-area [0 0 CELL-SIZE CELL-SIZE]
     :pixi.graphics/shapes
     [{:pixi.shape/type :pixi.shape.type/rounded-rectangle
       :pixi.shape/size [CELL-SIZE CELL-SIZE]
       :pixi.shape/line
       {:pixi.line/color 0xbbbbbb
        :pixi.line/width 2}
       :pixi.rounded-rectangle/radius 4
       :pixi.shape/fill
       {:pixi.fill/color (if (= 0 value)
                           0xffffff
                           0x000000)}}]}))

(defn set-cell [coords v]
  (swap! *state update :cells (fn [cells]
                                (map #(if (= coords (take 2 %))
                                        (conj (pop %) v)
                                        %)
                                     cells))))


(defn make-scene [cells]
  {:pixi/renderer
   {:pixi.renderer/size [400 400]
    :pixi.renderer/background-color 0xbbbbbb}
   :pixi/listeners
   {:click      (fn [_ coords v] (set-cell coords v))
    :mouse-down (fn [x] (prn :mouse-down x))
    :mouse-up   (fn [x] (prn :mouse-up x))
    :mouse-over (fn [x] (prn :mouse-over x))
    :mouse-out  (fn [x] (prn :mouse-out x))}
   :pixi/stage
   {:pixi.object/type :pixi.object.type/container
    :pixi.container/children
    (vec (map make-cell cells))}})



(defn render [el {:keys [cells]}]
  (impi/mount :scene (make-scene cells) el))

(defn main-loop
  [el state]
  (js/console.log "game loop")
  (let [new-life (l/update-life @state)]
    (reset! state new-life)
    (js/setTimeout #(main-loop el state) 1000)))


(defn main []
  (let [el (.getElementById js/document "app")
        init-life (l/new-life 8 8)]
    (reset! *state init-life)
    (main-loop el *state)
    (letfn [(render-loop []
              (render el @*state)
              (js/requestAnimationFrame render-loop))]
      (js/requestAnimationFrame render-loop))))


(set! (.-onload js/window) main)



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

