(ns broadfcui.common.wdl
  (:require
   [dmohs.react :as react]
   [broadfcui.common.codemirror :refer [CodeMirror]]
   [broadfcui.common.wdl-editor :refer [WDLEditor]]
   [broadfcui.utils :as utils]
   [broadfcui.common.pipeline :refer [PipelineBuilder]]
   ))

(react/defc WDLViewer
  {:render
   (fn [{:keys [props]}]
     [:div {:style {:margin "2.5rem 1.5rem" :display "flex"}}
      [:div {:style {:flex "1 1 60%"}}
        [PipelineBuilder{:WDL (:wdl props) :read-only? (:read-only? props)}]
      ]
      [:div {:className "right-editor"
             :style {:flex "1 1 40%" :minWidth "300px"}}
        [WDLEditor {:WDL (:wdl props) :read-only? (:read-only? props) :onWDLChange (fn [newWDL]((:onWDLChange props) newWDL))}]
      ]])
   :refresh
   (constantly nil)})
