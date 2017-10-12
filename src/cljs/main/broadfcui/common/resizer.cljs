(ns broadfcui.common.resizer
  (:require
    [dmohs.react :as react]
    ))

(def ^:private _resizingEditor)
(def ^:private _startTargetWidth)
(def ^:private _startX)
(def ^:private newMaxWidth)

(defn get-client-x [event]  event.clientX)

(react/defc Resizer
{:render
 (fn [{:keys [props state]}]
   [:div {:className   "pb-resizer noselect" :id "pb-resizer"
          :onMouseDown (fn [e] (set! _resizingEditor true)
                         (set! _startX (get-client-x e))
                         (set! _startTargetWidth (.-clientWidth (.getElementById js/document (:target props))))
                         (if e.stopPropagation (.stopPropagation e))
                         (if e.preventDefault (.preventDefault e))
                         (set! event.cancelBubble true)
                         (set! event.returnValue false))}
    [:div {:className "resizer-stick"}]
    [:div {:className "resizer-stick"}]
    [:div {:className "resizer-stick"}]])
 :component-did-mount
 (fn [{:keys [this props state locals]}]
   (let [on-resize (fn [event]
                     (if _resizingEditor
                       (let [offset (- _startX (get-client-x event))
                             newTargetWidth (+ _startTargetWidth offset)
                             element (.getElementById js/document (:target props))]
                         (if (and (> newTargetWidth (:minTargetWidth props)) (< newTargetWidth (this :-getMaxTargetWidth props)))
                           (set! element.style.width (str newTargetWidth "px"))))))
         on-resize-end (fn [event]
                         (set! _resizingEditor false))]
     (swap! locals assoc :on-resize on-resize)
     (swap! locals assoc :on-resize-end on-resize-end)
     (.addEventListener js/window "mousemove" on-resize)
     (.addEventListener js/window "mouseup" on-resize-end)))
 :component-will-unmount
 (fn [{:keys [locals]}]
   (.removeEventListener js/window "mousemove" (:on-resize @locals))
   (.removeEventListener js/window "mouseup" (:on-resize-end @locals)))
 :-getMaxTargetWidth
 (fn [props]
    (set! newMaxWidth (/ js/window.screen.width 2))
     (if (< newMaxWidth (:minTargetWidth props)) (set! newMaxWidth (:minTargetWidth props))) newMaxWidth)})
