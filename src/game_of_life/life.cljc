(ns game-of-life.life)

(defn matrix->cells [matrix]
    (mapcat (fn [r row]
              (map #(vector r %1 %2) (range) row))
            (range) matrix))

(defn new-life 
  ([width height]
   {:width width
    :height height
    :cells [(for [ col (range width)
                     row (range height)]
              [row col 0])]})
  ([width height matrix]
   {:width width
    :height height
    :cells (matrix->cells matrix)}))



(defn shift-right [cell, w]
  (update cell 1 #(rem (+ 1 %) w)))


(defn update-life [life]
  (assoc life :cells (map shift-right (:cells life) (repeat (:width life)))))

(comment
  (take 5 (repeat 4)))



