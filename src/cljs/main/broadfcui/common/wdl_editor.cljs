(ns broadfcui.common.wdl-editor
  (:require
    [dmohs.react :as react]
    [cljsjs.clipboard]
    [broadfcui.common.codemirror :refer [CodeMirror]]
    [broadfcui.utils :as utils]
    [clojure.string :as string]
    [broadfcui.common.pipeline :refer [PipelineBuilder]]))

(def ^:private Downloadjs (aget js/window "webpack-deps" "Downloadjs"))

(def ^:private clipboard-atom (atom nil))

(react/defc- EditorButton
  {:render
    (fn [{:keys [this props]}]
        [:button {:className "button-wdl"
          :onClick (:onClickCallback props)}
          [:i {:className (str "fa fa-" (:icon-name props))}] [:span {:className "action-wdl"} (:button-text props)]])})

(react/defc- ClipboardButton
 {:render
  (fn [{:keys [props]}]
    [:span {}
      [:textarea {:id "copy-to-clipboard-wdl-textarea" :value (:WDL props) :readOnly true
                  :style {:position "fixed" :top "-999999px" :left "-999999px"}}]
      [:button.clipboard {:id "copy-clipboard-button-id" :className "button-wdl" :data-clipboard-target "#copy-to-clipboard-wdl-textarea"}
        [:i {:className "fa fa-files-o"}] [:span {:className "action-wdl"} (:label props)]]])
  :component-did-mount
    #(let [clipboard (js/Clipboard. "#copy-clipboard-button-id")]
       (reset! clipboard-atom clipboard))
  :component-will-unmount
    #(when-not (nil? @clipboard-atom)
       (.destroy @clipboard-atom)
       (reset! clipboard-atom nil))})

(react/defc- WDLEditorButtons
  {:render
   (fn [{:keys [props]}]
     [:div {:className "title-right-block noselect"}
      [ClipboardButton {:label "Clipboard" :WDL (:WDL props)}]
      [EditorButton {:icon-name "download" :button-text "Download"
                     :onClickCallback (fn [e] (let [currentDate (string/replace (subs (.toISOString (js/Date.)) 0 10) #"/-/g" "")]
                                          (Downloadjs (js/Blob. #js [(:WDL props)]) (str "wdl-src" currentDate ".wdl") "text/plain")))}]
      [EditorButton {:icon-name "play" :button-text "Build"
                     :onClickCallback (fn [e] ((:onWDLBuildClick props)))}]])})

(react/defc WDLEditorErrorsPanel
  {:get-initial-state
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
               [:i {:className "fa fa-exclamation-triangle" :aria-hidden 'true'}]  errors.length])]]
          (if (:is-show-error @state)
            [:div {:className "error-trace" :id "error"}
              [:div {:className "error-trace-header"}
                [:span {:className "error-trace-header-name"} "ERRORS"
                  [:span {:className "error-trace-header-count"} (str " ("errors.length ")")]]
                [:i {:className "fa fa-times" :onClick (fn [e](swap! state assoc :is-show-error false))}]]
              [:div {:className "error-trace-container"}
                [:div {} (for [error errors]
                  [:div {:className "error-stacktrace"} error])]]])]))})

(react/defc WDLEditor
  {:get-initial-state
   (fn [{:keys [props]}] {:currentWDLState (:WDL props)})
   :render
    (fn [{:keys [props state]}]
      (let [{:keys [currentWDLState]} @state]
        [:div {:className "wdl-editor"}
         [:div {:className "wdl-title noselect"}
          [WDLEditorButtons {:WDL currentWDLState :onWDLBuildClick (fn [] ((:onWDLChange props) (:currentWDLState @state)))}]
          [WDLEditorErrorsPanel {:wasRunned (:wasRunned props) :errors (:errors props)  :isBuilding (:isBuilding props)}]]
         [:div {:className "textarea-wrapper"}
          [CodeMirror {:text currentWDLState
                       :read-only? (:read-only? props)
                       :initialize (fn [self]
                                     (self :add-listener "change"
                                           (fn [] (swap! state assoc :currentWDLState (js->clj (self :call-method "getValue"))))))}]]]))
   :refresh
    (constantly nil)})
