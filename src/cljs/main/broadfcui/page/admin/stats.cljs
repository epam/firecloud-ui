(ns broadfcui.page.admin
  (:require
    clojure.string
    [dmohs.react :as react]
    [broadfcui.common :as common]
    [broadfcui.common.components :as components]
    [broadfcui.common.icons :as icons]
    [broadfcui.common.input :as input]
    [broadfcui.common.style :as style]
    [broadfcui.config :as config]
    [broadfcui.endpoints :as endpoints]
    [broadfcui.nav :as nav]
    [broadfcui.utils :as utils]
    [goog.dom :as gdom]
    ))

(react/defc AdminStats
  {:get-initial-state
   (fn []
     {:expanded false})
   :render
   (fn [{:keys [props state]}]
     [:div {}
      [:h2 {} (:label props)]
      [:div {:ref "chart-container" :style {:display "inline-block" :padding "0.25em 0 0 0"}}
        [:div {:ref "users-chart" :style {:display "inline-block" :padding "0.25em 0 0 0"}}]
        [:div {:ref "wf-chart" :style {:display "inline-block" :padding "0.25em 0 0 0"}}]
        [:div {:style {:clear "all"}}]
        [:div {:ref "wfbox-chart" :style {:display "inline-block" :padding "0.25em 0 0 0"}}]
        [:div {:ref "wftiming-chart" :style {:display "inline-block" :padding "0.25em 0 0 0"}}]
      ]])
   :component-did-mount #((:this %) :-update-element)
   :component-did-update #((:this %) :-update-element)
   :-update-element
    (fn [{:keys [props state refs]}]
         (.adminStats js/window (get @refs "chart-container") (clj->js (:data props)) "Admin Stats Dashboard"))})

(react/defc Page
  (utils/log-methods "statspage"
  {:render
   (fn [{:keys [this props state]}]
       (utils/cljslog (keys @state))
       [:div {}
         (when (some? (:data (:response @state)))
          [AdminStats {:label (str "Admin Stats Dashboard: "
                                (:startDate (:data (:response @state)))
                                " to "
                                (:startDate (:data (:response @state))))
                       :data (:data (:response @state))}]
         )
       ]

   )
   :component-did-mount
   (fn [{:keys [state]}]
     (endpoints/call-ajax-orch
      {:endpoint 
       (endpoints/get-admin-stats {"startDate" "2017-03-01" "endDate" "2017-03-15"})
        :on-done (fn [{:keys [success? get-parsed-response xhr status-code]}]
                   (swap! state assoc
                      :loading? false
                      :response (if success?
                                       {:data (get-parsed-response)
                                        :status status-code}
                                       {:error (.-responseText xhr)
                                        :status status-code})))
                     
                     }))}))

(defn render [props]
  (react/create-element Page props))
