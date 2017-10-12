(ns broadfcui.common.pipeline
  (:require
    [dmohs.react :as react]
    [clojure.string :as string]
    [broadfcui.common.style :as style]))

(def ^:private Pipeline (aget js/window "webpack-deps" "Pipeline"))

(def ^:private diagram)

(def ^:private WDL)

(def ^:private workflow)

(react/defc PipelineBuilder
  {:get-initial-state (fn [{:keys [props]}] {:ports-are-active false})
   :render
   (fn [{:keys [this state]}]
     [:div {:style {:height "100%" :margin "block" :overflow "hidden"}
            :display "block"}
     [:div {:className "editor-buttons-wrapper"}
      [:div {:className "editor-buttons" :title "zoom in"}
       [:span {:className "fa fa-search-plus"
               :onClick (fn [e](.zoomIn diagram.zoom))}]]
      [:div {:className "editor-buttons" :title "zoom out"}
       [:span {:className "fa fa-search-minus"
               :onClick (fn [e](.zoomOut diagram.zoom))}]]
      [:div {:className "editor-buttons screenshot-button" :title "take screenshot"}
       [:span {:className "fa fa fa-camera"
               :onClick (fn [e](.getPNG diagram.paper (fn [data](let [element (.createElement js/document "a")]
                                                                    (.setAttribute element "href" (.createObjectURL js/window.URL data))
                                                                    (def currentDate (subs (.toISOString (js/Date.)) 0 10))
                                                                    (set! currentDate (string/replace currentDate #"/-/g" ""))
                                                                    (.setAttribute element "download" (str "workflow" currentDate ".png"))
                                                                    (set! element.style.display "none")
                                                                    (.appendChild js/document.body element)
                                                                    (.click element)
                                                                    (.removeChild js/document.body element)
                                                                   ))))}]]
      [:div {:className "editor-buttons" :title "autolayout"}
       [:span {:className "fa fa-object-group"
               :onClick (fn [e](.layout diagram))}]]
      [:div {:className "editor-buttons" :title "fit to screen"}
       [:span {:className "fa fa-expand"
               :onClick (fn [e](.fitToPage diagram.zoom))}]]
      [:div {:className "editor-buttons" :title "toggle links"}
       [:span {:className (if (:ports-are-active @state) "icon-enabled fa fa-exchange" "icon-disabled fa fa-exchange")
               :onClick (fn [e]
                          (swap! state assoc :ports-are-active (not (:ports-are-active @state)))
                          (.togglePorts diagram true (not (:ports-are-active @state)))
                          (._update diagram))}]]
      ]
     [:div {:id "pipeline-builder" :style {:width "100%" :height "calc(100vh - 500px)" :overflow "hidden" :minHeight "517px"}}]])
   :component-did-mount
   (fn [{:keys [props this]}]
     (set! WDL (:WDL props))
     (set! diagram (Pipeline.Visualizer. (.getElementById js/document "pipeline-builder") true))
     (if (string/trim WDL)
       (let [parseResult (Pipeline.parse WDL)]
         (if parseResult.status
           (do
             (set! workflow (first parseResult.model))
             (.attachTo diagram workflow)))
         (if (not (:isParsed props))
           ((:onResultParseWDL props) (if parseResult.status #js [] #js [parseResult.message]) false true)))))
   :component-will-receive-props
   (fn [{:keys [props next-props this]}]
     (set! WDL (:WDL next-props))
     (if (string/trim WDL)
       (let [parseResult (Pipeline.parse WDL)]
         (if parseResult.status
           (do
             (set! workflow (first parseResult.model))
             (.attachTo diagram workflow)))
         (if (not (:isParsed next-props))
           ((:onResultParseWDL next-props) (if parseResult.status #js [] #js [parseResult.message]) false true)))))
   })
