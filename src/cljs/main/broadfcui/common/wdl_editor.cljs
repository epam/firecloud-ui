(ns broadfcui.common.wdl-editor
  (:require
    [dmohs.react :as react]
    [broadfcui.common.codemirror :refer [CodeMirror]]
    [broadfcui.utils :as utils]
    [broadfcui.common.pipeline :refer [PipelineBuilder]]))

(react/defc WDLEditor
  {:render
   (fn [{:keys [props]}]
     [:div {}
      [:div {}
       "Buttons"]
      [:div {}
       [CodeMirror {:text (:WDL props) :read-only? (:read-only? props)}]]])
   :refresh
   (constantly nil)})
