(ns broadfcui.common.resizer
  (:require
    [dmohs.react :as react]
    ))

(def ^:private _resizingEditor)

(def ^:private _startTargetWidth)

(def ^:private _startX)


(def ^:private newMaxWidth)

(defn get-client-x [event]  event.clientX)

(defn on-resize [event, props]
                         (if (not _resizingEditor) true)
                         (let [offset (- _startX (get-client-x event))
                               newTargetWidth (+ _startTargetWidth offset)]
                           ((:flex props) (< newTargetWidth (:flexAfter props)))
                           (if (or (< newTargetWidth (:minTargetWidth props)) (> newTargetWidth (_getMaxTargetWidth))) true)
                           (def element (.getElementById js/document (:target props)))
                           (set! element.style.width  (str newTargetWidth "px"))
                           )
 )

(defn on-resize-end [event, props]
  (set! _resizingEditor false)
  )

(defn _getMaxTargetWidth [props]
  (set! newMaxWidth (/ js/window.screen.width 2))
    (if (< newMaxWidth (:minTargetWidth props)) (set! newMaxWidth (:minTargetWidth props)))
    newMaxWidth
  )

(react/defc Resizer
  {:render (fn [{:keys [props state]}]
              [:div {:className "pb-resizer noselect" :id "pb-resizer" :onMouseDown (fn [e]  (set! _resizingEditor true)
                                                                                             (set! _startX (get-client-x e))
                                                                                             (set! _startTargetWidth (.-clientWidth (.getElementById js/document (:target props))))
                                                                                             (if e.stopPropagation (.stopPropagation e))
                                                                                             (if e.preventDefault (.preventDefault e))
                                                                                             (set! event.cancelBubble true)
                                                                                             (set! event.returnValue false)
                                                                                             ) }
               [:div {:className "resizer-stick"}]
               [:div {:className "resizer-stick"}]
               [:div {:className "resizer-stick"}]
             ])
   })
