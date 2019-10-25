(ns game-of-life.life)


(defn matrix->cells [matrix]
    (mapcat (fn [r row]
              (map #(vector r %1 %2) (range) row))
            (range) matrix))


(defn new-life
  ([width height]
   {:width width
    :height height
    :cells (for [col (range width)
                    row (range height)]
              [row col 0])})
  ([width height matrix]
   {:width width
    :height height
    :cells (matrix->cells matrix)}))


(defn shift-right [cell, w]
  (update cell 1 #(rem (+ 1 %) w)))


(defn- distance [[x1 y1] [x2 y2]]
  (Math/sqrt (+
               (Math/pow (- x1 x2) 2)
               (Math/pow (- y1 y2) 2))))


(defn- is-neighbour [a b]
  (and
    (not= a b)
    (< (distance a b) 2)))


(defn- list-neighbours [c cells]
  (filter #(is-neighbour c %) cells))


(defn update-cell [c cells]
  (let [nei (list-neighbours c cells)
        alive (filter #(= 1 (last %)) nei)]
    (cond
      (< (count alive) 2) (conj (pop c) 0)
      (= (count alive) 2) c
      (= (count alive) 3) (conj (pop c) 1)
      (> (count alive) 3) (conj (pop c) 0))))


(defn update-life [{:keys [width height cells] :as life}]
  (assoc life :cells (for [c cells]
                       (update-cell c cells))))

