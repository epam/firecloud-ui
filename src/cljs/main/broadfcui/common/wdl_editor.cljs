(ns broadfcui.common.wdl-editor
  (:require
    [dmohs.react :as react]
    [broadfcui.common.codemirror :refer [CodeMirror]]
    [broadfcui.utils :as utils]
    [broadfcui.common.pipeline :refer [PipelineBuilder]]))

(react/defc WDLEditor
  {:get-initial-state (fn [{:keys [props]}] {:currentWDLState (:WDL props)})
  :render
    (fn [{:keys [props state]}]
      (let [{:keys [currentWDLState]} @state]
        [:div {:className "wdl-editor"}
          [:div {:className "wdl-title noselect"}
            [WDLEditorButtons {:onWDLBuildClick (fn [] ((:onWDLChange props) (:currentWDLState @state)))}]
            [WDLEditorErrorsPanel {:wasRunned (:wasRunned props) :errors (:errors props)  :isBuilding (:isBuilding props)}]
          ]
          [:div {:className "textarea-wrapper"}
            [CodeMirror {:text       currentWDLState
              :read-only? (:read-only? props)
              :initialize (fn [self]
                (self :add-listener "change"
                      (fn [] (swap! state assoc :currentWDLState (js->clj (self :call-method "getValue"))))))
              }]]]))
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
                     :onClickCallback (fn [e] ((:onWDLBuildClick props)))}]
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
       (if (:is-show-error @state)
         [:div {:className "error-trace" :id "error"}
                       [:div {:className "error-trace-header"}
                        [:span {:className "error-trace-header-name"} "ERRORS"
                         [:span {:className "error-trace-header-count"} (str " ("errors.length ")")]]
                        [:i {:className "fa fa-times" :onClick (fn [e](swap! state assoc :is-show-error false))}]]
                       [:div {:className "error-trace-container"}
                        [:div {} (for [error errors]
                        [:div {:className "error-stacktrace"} error])]]
        ])]
      ))})
