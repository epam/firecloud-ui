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
       [WDLEditorErrorsPanel {:wasRunned true :errors #js []  :isBuilding false}]
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

(react/defc WDLEditorErrorsPanel
   { :get-initial-state
     (fn [{:keys [props]}]
        {:is-show-error false})
     :render
    (fn [{:keys [this props state]}]
      (let [{:keys [wasRunned errors isBuilding]} props]
        [:div {}
      [:div {:className "message-block"}
       [:div {}
        (if (and isBuilding (not wasRunned)) [:span {:className "build-ok"} "building..."])
        (if (and (= errors.length 0) wasRunned)[:span {:className "build-ok"  :onClick (fn [e](swap! state assoc :is-show-error true))}
         [:i {:className "fa fa-check" :aria-hidden 'true'}]  "done"])
        (if (and (> errors.length 0) wasRunned)[:span {:className "build-error"  :onClick (fn [e](swap! state assoc :is-show-error true))}
         [:i {:className "fa fa-exclamation-triangle" :aria-hidden 'true'}]  errors.length])
       ]]
       (if (:is-show-error @state) [:div {:className "error-trace" :id "error"}
                       [:div {:className "error-trace-header"}
                        [:span {:className "error-trace-header-name"} "ERRORS"
                         [:span {:className "error-trace-header-count"} (str " ("errors.length ")")]]
                        [:i {:className "fa fa-times" :onClick (fn [e](swap! state assoc :is-show-error false))}]]
                       [:div {:className "error-trace-container"}
                        (doseq [[key value] errors] [:div {:className "error-stacktrace"} value])]
        ])]
      ))})
