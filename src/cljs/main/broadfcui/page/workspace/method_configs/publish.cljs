(ns broadfcui.page.workspace.method-configs.publish
  (:require
   [dmohs.react :as react]
   [broadfcui.common.components :as comps]
   [broadfcui.common.input :as input]
   [broadfcui.common.style :as style]
   [broadfcui.components.modals :as modals]
   [broadfcui.endpoints :as endpoints]
   [broadfcui.utils :as utils]
   ))


(react/defc PublishDialog
  {:render
   (fn [{:keys [props state refs]}]
     (let [{:keys [workspace-id config-id]} props
           {:keys [namespace name]} config-id]
       [modals/OKCancelForm
        {:header "Publish Method Configuration"
         :get-first-element-dom-node #((@refs "mcNamespace") :access-field)
         :content
         (react/create-element
          [:div {:style {:width 500}}
           (when (:publishing? @state)
             [comps/Blocker {:banner "Publishing Method Configuration..."}])
           (style/create-form-label "Method Configuration Namespace")
           [input/TextField {:style {:width "100%"} :ref "mcNamespace"
                             :defaultValue namespace
                             :predicates [(input/nonempty "Namespace")]}]
           (style/create-form-label "Method Configuration Name")
           [input/TextField {:style {:width "100%"} :ref "mcName"
                             :defaultValue name
                             :predicates [(input/nonempty "Name")]}]
           (style/create-validation-error-message (:validation-errors @state))
           [comps/ErrorViewer {:error (:error @state)}]])
         :dismiss (:dismiss props)
         :ok-button
         {:text "Publish"
          :onClick
          #(let [[ns n & fails] (input/get-and-validate refs "mcNamespace" "mcName")]
             (swap! state assoc :validation-errors fails :error nil)
             (when-not fails
               (swap! state assoc :publishing? true :error nil)
               (endpoints/call-ajax-orch
                {:endpoint (endpoints/copy-method-config-to-repo workspace-id)
                 :headers utils/content-type=json
                 :payload {:configurationNamespace ns
                           :configurationName n
                           :sourceNamespace namespace
                           :sourceName name}
                 :on-done (fn [{:keys [success? get-parsed-response]}]
                            (swap! state dissoc :publishing?)
                            (if success?
                              ((:dismiss props))
                              (swap! state assoc :error (get-parsed-response false))))})))}}]))})
