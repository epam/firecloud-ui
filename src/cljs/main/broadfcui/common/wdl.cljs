(ns broadfcui.common.wdl
  (:require
   [dmohs.react :as react]
   [broadfcui.common.codemirror :refer [CodeMirror]]
   [broadfcui.common.wdl-editor :refer [WDLEditor]]
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
     [:div {:style {:margin "2.5rem 1.5rem" :display "flex"}}
      [:div {:style {:flex "1 1 60%"}}
        [PipelineBuilder{:WDL (:wdl props)
                         :isParsed (:isParsed @state)
                         :read-only? (:read-only? props)
                         :onResultParseWDL (fn [newErrors newIsBuilding newWasRunned]
                          (swap! state merge{:errors newErrors :isParsed   true :isBuilding newIsBuilding :wasRunned  newWasRunned})
        )}]
      ]
      [:div {:className "right-editor"
             :style {:flex "1 1 40%" :minWidth "300px"}}
        [WDLEditor {:WDL (:wdl props)
                    :errors (:errors @state)
                    :isBuilding (:isBuilding @state)
                    :wasRunned (:wasRunned @state)
                    :read-only? (:read-only? props)
                    :onWDLChange (fn [newWDL]((swap! state assoc :isParsed false)
                                               ((:onWDLChange props) newWDL)
                                              ))}]
      ]])
   :refresh
   (constantly nil)
   :component-did-mount (fn [{:keys [state]}]
                           (swap! state merge{:isParsed false}))})
