(ns broadfcui.common.wdl-editor
  (:require
    [dmohs.react :as react]
    [broadfcui.common.codemirror :refer [CodeMirror]]
    [broadfcui.utils :as utils]
    [broadfcui.common.pipeline :refer [PipelineBuilder]]))

(react/defc WDLEditor
  {:render
   (fn [{:keys [props]}]
     [:div {:className "wdl-editor"}
      [:div {:className "wdl-title noselect"}
       [WDLEditorButtons {:onWDLChange (:onWDLChange props)}]
       ;[:div {}
       ; "Errors"]
       ]
      [:div {:className "textarea-wrapper"}
       [CodeMirror {:text (:WDL props) :read-only? (:read-only? props)}]]])
   :refresh
   (constantly nil)})

(react/defc- EditorButton
  {:render
    (fn [{:keys [this props]}]
        [:button {:className "button-wdl"
          :onClick (:onClickCallback props)}
          [:i {:className (str "fa fa-" (:icon-name props))}] [:span {:className "action-wdl"} (:button-text props)]])})


(react/defc- WDLEditorButtons
  {:render
   (fn [{:keys [props]}]
     [:div {:className "title-right-block noselect"}
      [EditorButton {:icon-name "files-o" :button-text "Clipboard"
                     :onClickCallback (fn [e] ())}]         ;todo click handler
      [EditorButton {:icon-name "download" :button-text "Download"
                     :onClickCallback (fn [e] ())}]         ;todo click handler
      [EditorButton {:icon-name "play" :button-text "Build"
                     :onClickCallback (fn [e]
                                        (def newWDL "workflow SimpleVariantSelection {
  File gatk
  File refFasta
  File refIndex
  File refDict
  String name

  call haplotypeCaller {
    input:
      GATK = gatk,
      RefFasta = refFasta,
      RefIndex = refIndex,
      RefDict = refDict,
      sampleName = name,
  }
}

task haplotypeCaller {
  File GATK
  File RefFasta
  File RefIndex
  File RefDict
  String sampleName
  File inputBAM
  File bamIndex


  output {
    File rawVCF = \"${sampleName}.raw.indels.snps.vcf\"
  }
}
" )
                                                ((:onWDLChange props) newWDL)
                                                )}]         ;todo click handler
      ])})


(react/defc WDLEditorErrorsPanel                            ;todo
  {:render
   (fn [{:keys [props]}]
     [:div {:className ""}])})
