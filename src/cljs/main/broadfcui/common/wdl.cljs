(ns broadfcui.common.wdl
  (:require
   [dmohs.react :as react]
   [broadfcui.common.codemirror :refer [CodeMirror]]
   [broadfcui.common.wdl-editor :refer [WDLEditor]]
   [broadfcui.common.resizer :refer [Resizer]]
   [broadfcui.utils :as utils]
   [broadfcui.common.pipeline :refer [PipelineBuilder]]
   ))

(react/defc WDLViewer
  {
    :get-initial-state (fn [{:keys [props]}]
                         {:isParsed false
                          :errors #js []
                          :isBuilding true
                          :wasRunned false})
    :render (fn [{:keys [props state]}]
     [:div {:style {:margin "2.5rem 0"
                    :position "relative"
                    :width "100%"}}
      [:div {:style {:width "100%"
                     :margin-left "1.5rem"
                     :minWidth "300px"}}
        [PipelineBuilder{:WDL (:wdl props)
                         :isParsed (:isParsed @state)
                         :read-only? (:read-only? props)
                         :onResultParseWDL (fn [newErrors newIsBuilding newWasRunned]
                          (swap! state merge{:errors newErrors :isParsed   true :isBuilding newIsBuilding :wasRunned  newWasRunned})
        )}]
      ]
      [:div {:className "right-editor" :id "right-editor"
             :style {:position "absolute"
                     :top "0"
                     :right "0"
                     :minWidth "450px"
                     :maxWidth "50vw"}}
        [:div {:style {:position "relative"}}
          [Resizer {:target "right-editor" :minTargetWidth 450 :flexAfter 320}]
          [WDLEditor {:WDL (:wdl props)
                    :errors (:errors @state)
                    :isBuilding (:isBuilding @state)
                    :wasRunned (:wasRunned @state)
                    :read-only? (:read-only? props)
                    :onWDLChange (fn [newWDL]((swap! state assoc :isParsed false)
                                               ((:onWDLChange props) newWDL)
                                              ))}]]]])
   :refresh
   (constantly nil)
   :component-did-mount
   (fn [{:keys [state]}]
       (swap! state merge{:isParsed false}))})
