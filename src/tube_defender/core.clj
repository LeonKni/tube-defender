(ns tube-defender.core
  (:require  [quil.core :refer :all]
             [simplecs.core :as sc]
             [tube-defender.key-input :as ki]
             [tube-defender.render :as render])
  (:gen-class))

;;;;;;;;;;;;;;;;components;;;;;;;;;;;;;;;;;;

(sc/defcomponent rat [] {})
(sc/defcomponent hero [] {})
(sc/defcomponent disc [] {})
(sc/defcomponent position [x y] {:x x :y y})
(sc/defcomponent volley-multiple [vm] {:vm vm})
(sc/defcomponent velocity [v] {:v v})

;;;;;;;;;;;;;;;;;;;systems;;;;;;;;;;;;;;;;;;;
(sc/defcomponentsystem rat-mover :rat  []
  [ces entity _]
  (sc/letc ces entity
           [y [:position :y]]
           (sc/update-entity ces
                             entity
                             [:position :y] inc)))

;;;;;;;hero mover should use key bindings instead of just calling inc
(sc/defcomponentsystem hero-mover :hero  []
  [ces entity _]
  (sc/letc ces entity
           [x [:position :x]]
           (let [left (contains? @ki/input-keys)
                 right (contains? @ki/input-keys)
                 both (and left right)]
             (cond both ces
                   left (sc/update-entity ces entity [:position :x] dec)
                   right (sc/update-entity ces entity [:position :x inc])))))


;;;;;;;disc mover should use keybindings instead of just calling inc;;;;;;;
(sc/defcomponentsystem disc-mover :disc []
  [ces entity _]
  (sc/letc ces entity
  [x [:position :x]
   y [:position :y]]
  (sc/update-entity (sc/update-entity ces entity [:position :y] inc) entity [:position :x] inc)))

;;;;;disc position validator will make sure the junk don't fly off the dern screen;;;;;
(sc/defcomponentsystem disc-mover :disc []
  [ces entity _]
  (sc/letc ces entity
  [x [:position :x]
   y [:position :y]]
;;if edge of screen, reverse x, if top of screen reverse y, if bottom and not on player, stop
  ;(sc/update-entity (sc/update-entity ces entity [:position :y] inc) entity [:position :x] inc)
  ))


;;;;;;;;volley multiple should inc if disc is caught by hero, but that logic is outside of this system. all it cares about is how to do it;;;;;
(sc/defcomponentsystem volley-multiplier :disc []
  [ces entity _]
  (sc/update-entity ces entity [:volley-multiple :vm] inc))

;;;;;;;;;;;;;;;;;;canonic component entity system;;;;;;;;;;;;;;;;
(def ces (atom (sc/make-ces {:entities [[(hero)
                                         (position 200 500)
                                         (velocity 0)]
                                        [(disc)
                                         (position 220 480)
                                         (velocity 10)
                                         (volley-multiple 1)]
                                        [(rat)
                                         (position 20 40)
                                         (velocity 1)]
                                        [(rat)
                                         (position 30 60)
                                         (velocity 1)]]
                             :systems [(rat-mover) (hero-mover)]})))


;;;;;;;;;;;;;;;;;;;;;;;;test rat ces update;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; y should inc to 3
(let [rat-ces (atom (sc/make-ces {:entities [[(rat) (position 10 2) (velocity 0)]]
                                  :systems [(rat-mover)]}))]
  (swap! rat-ces sc/advance-ces))

;;;;;;;;;;;;;;;;;;;;;;;test hero ces update;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; x should inc to 201
(let [hero-ces (atom (sc/make-ces {:entities [[(hero) (position 200 400) (velocity 0)]]
                                   :systems [(hero-mover)]}))]
  (swap! hero-ces sc/advance-ces))

;;;;;;;;;;;;;;;;test disc ces update;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(let [disc-ces (atom (sc/make-ces {:entities [[(disc) (position 200 400) (velocity 0)]]
                                   :systems [(disc-mover)]}))]
  (swap! disc-ces sc/advance-ces))

;;;;;;;;;;;;;;;test volley multiple incrementer;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(let [dvm-ces (atom (sc/make-ces {:entities [[(disc) (position 200 400) (velocity 0) (volley-multiple 1)]]
                                  :systems [(volley-multiplier)]}))]
  (swap! dvm-ces sc/advance-ces))



(defn setup []
  (smooth)
  (no-stroke))

(defn advance-state []
  (swap! ces sc/advance-ces))

(defn -main
  "main"
  [& args]
  (ki/startSketch))

(defn setup []
  (smooth)
  (frame-rate 60)
  (background 200))

(defn startSketch []
  (defsketch tube-defender
    :title "TUBE DEFENDER"
    :size (params :screen-dimensions)
    :setup setup
    :draw render
    :target :perm-frame
    :key-pressed ki/keydown-event
    :key-released ki/keyup-event))

(startSketch)
