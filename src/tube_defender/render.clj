(ns tube-defender.render
  (:require [quil.core :refer :all]
            [simplecs.core :as sc]))

(def params {:screen-dimensions [400 400]
             :background-colour 255
             :blob-colour 0
             :blob-radius 10
             :screen-bounds [0 380]})

(defn render-bg
  "Render the background. Everything else to be rendered is drawn on top of this."
  []
  (background-float (params :background-colour))
  (fill (params :blob-colour)))

(defn render-hud
  "Render the game's hud (head up display). This is drawn on top of everything else"
  []
  (text (str (deref tube-defender.key-input/input-keys)) 10 350))


(defn draw-rat
  "Draw a rat at the given x y"
  [pos]
  (fill-int 128)
  (ellipse (:x pos) (:y pos) 10 10))

(defn render-rats
  "Render the rat entities in the game"
  [ces]
  (dorun
    (let [rat-entities (sc/entities-with-component @ces :rat)
          rat-positions (map #(sc/get-component @ces % :position) rat-entities)]
         (map draw-rat rat-positions))))

(defn render 
  "Renders every entity in the given game state"
  []
  (let [ces tube-defender.core/ces]
    (render-bg)
    (render-rats ces)
    (render-hud)))