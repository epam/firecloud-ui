(ns broadfcui.page.method-repo.method.wdl
  (:require
   [dmohs.react :as react]
   [broadfcui.common.codemirror :refer [CodeMirror]]
   [broadfcui.utils :as utils]
   [broadfcui.common.pipeline :refer [PipelineBuilder]]
   ))

(react/defc WDLViewer
  {:render
   (fn [{:keys [props]}]
     [:div {:style {:margin "2.5rem 1.5rem" :display "flex"}}
      [:div {:style {:flex "1 1 60%"}}
       [PipelineBuilder{:WDL (:wdl props)}]
      ]
      [:div {:style {:flex "1 1 40%" :minWidth "300px"}}
       [CodeMirror {:text (:wdl props)}]
      ]])
   :refresh
   (constantly nil)})
