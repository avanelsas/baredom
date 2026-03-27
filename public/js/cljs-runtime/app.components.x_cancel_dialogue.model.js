goog.provide('app.components.x_cancel_dialogue.model');
app.components.x_cancel_dialogue.model.tag_name = "x-cancel-dialogue";
app.components.x_cancel_dialogue.model.attr_open = "open";
app.components.x_cancel_dialogue.model.attr_disabled = "disabled";
app.components.x_cancel_dialogue.model.attr_headline = "headline";
app.components.x_cancel_dialogue.model.attr_message = "message";
app.components.x_cancel_dialogue.model.attr_confirm_text = "confirm-text";
app.components.x_cancel_dialogue.model.attr_cancel_text = "cancel-text";
app.components.x_cancel_dialogue.model.attr_danger = "danger";
app.components.x_cancel_dialogue.model.attr_portal = "portal";
app.components.x_cancel_dialogue.model.event_cancel_request = "x-cancel-dialogue-cancel-request";
app.components.x_cancel_dialogue.model.event_cancel = "x-cancel-dialogue-cancel";
app.components.x_cancel_dialogue.model.event_confirm_request = "x-cancel-dialogue-confirm-request";
app.components.x_cancel_dialogue.model.event_confirm = "x-cancel-dialogue-confirm";
app.components.x_cancel_dialogue.model.default_headline = "Discard changes?";
app.components.x_cancel_dialogue.model.default_confirm_text = "Discard";
app.components.x_cancel_dialogue.model.default_cancel_text = "Keep editing";
app.components.x_cancel_dialogue.model.observed_attributes = ["open","disabled","headline","message","confirm-text","cancel-text","danger","portal"];
/**
 * Returns true if the attr value indicates presence (not nil), false otherwise.
 */
app.components.x_cancel_dialogue.model.parse_bool_attr = (function app$components$x_cancel_dialogue$model$parse_bool_attr(s){
return (!((s == null)));
});
/**
 * Produce a normalized view-model map from raw attribute values.
 */
app.components.x_cancel_dialogue.model.normalize = (function app$components$x_cancel_dialogue$model$normalize(p__21960){
var map__21961 = p__21960;
var map__21961__$1 = cljs.core.__destructure_map(map__21961);
var open_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21961__$1,new cljs.core.Keyword(null,"open-present?","open-present?",965047899));
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21961__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var headline_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21961__$1,new cljs.core.Keyword(null,"headline-raw","headline-raw",1141142757));
var message_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21961__$1,new cljs.core.Keyword(null,"message-raw","message-raw",952235685));
var confirm_text_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21961__$1,new cljs.core.Keyword(null,"confirm-text-raw","confirm-text-raw",-1317418708));
var cancel_text_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21961__$1,new cljs.core.Keyword(null,"cancel-text-raw","cancel-text-raw",615622099));
var danger_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21961__$1,new cljs.core.Keyword(null,"danger-present?","danger-present?",-439648505));
var portal_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21961__$1,new cljs.core.Keyword(null,"portal-raw","portal-raw",2293766));
return new cljs.core.PersistentArrayMap(null, 8, [new cljs.core.Keyword(null,"open?","open?",1238443125),cljs.core.boolean$(open_present_QMARK_),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),cljs.core.boolean$(disabled_present_QMARK_),new cljs.core.Keyword(null,"headline","headline",-157157727),(function (){var or__5142__auto__ = (function (){var and__5140__auto__ = typeof headline_raw === 'string';
if(and__5140__auto__){
var and__5140__auto____$1 = cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(headline_raw,"");
if(and__5140__auto____$1){
return headline_raw;
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
})();
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_cancel_dialogue.model.default_headline;
}
})(),new cljs.core.Keyword(null,"message","message",-406056002),((((typeof message_raw === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(message_raw,""))))?message_raw:null),new cljs.core.Keyword(null,"confirm-text","confirm-text",-1839494031),(function (){var or__5142__auto__ = (function (){var and__5140__auto__ = typeof confirm_text_raw === 'string';
if(and__5140__auto__){
var and__5140__auto____$1 = cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(confirm_text_raw,"");
if(and__5140__auto____$1){
return confirm_text_raw;
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
})();
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_cancel_dialogue.model.default_confirm_text;
}
})(),new cljs.core.Keyword(null,"cancel-text","cancel-text",1885137831),(function (){var or__5142__auto__ = (function (){var and__5140__auto__ = typeof cancel_text_raw === 'string';
if(and__5140__auto__){
var and__5140__auto____$1 = cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(cancel_text_raw,"");
if(and__5140__auto____$1){
return cancel_text_raw;
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
})();
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_cancel_dialogue.model.default_cancel_text;
}
})(),new cljs.core.Keyword(null,"danger?","danger?",181682216),cljs.core.boolean$(danger_present_QMARK_),new cljs.core.Keyword(null,"portal","portal",2002989957),((((typeof portal_raw === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(portal_raw.trim(),""))))?portal_raw.trim():null)], null);
});
/**
 * Build the x-cancel-dialogue-cancel-request CustomEvent detail.
 */
app.components.x_cancel_dialogue.model.cancel_request_detail = (function app$components$x_cancel_dialogue$model$cancel_request_detail(reason){
return ({"reason": reason});
});
/**
 * Build the x-cancel-dialogue-confirm-request CustomEvent detail.
 */
app.components.x_cancel_dialogue.model.confirm_request_detail = (function app$components$x_cancel_dialogue$model$confirm_request_detail(){
return ({});
});
app.components.x_cancel_dialogue.model.property_api = new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"open","open",-1763596448),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_cancel_dialogue.model.attr_open], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_cancel_dialogue.model.attr_disabled], null),new cljs.core.Keyword(null,"headline","headline",-157157727),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_cancel_dialogue.model.attr_headline], null),new cljs.core.Keyword(null,"message","message",-406056002),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_cancel_dialogue.model.attr_message], null),new cljs.core.Keyword(null,"confirmText","confirmText",-1040851815),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_cancel_dialogue.model.attr_confirm_text], null),new cljs.core.Keyword(null,"cancelText","cancelText",365257455),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_cancel_dialogue.model.attr_cancel_text], null),new cljs.core.Keyword(null,"danger","danger",-624338030),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_cancel_dialogue.model.attr_danger], null)], null);
app.components.x_cancel_dialogue.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_cancel_dialogue.model.event_cancel_request,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"reason","reason",-2070751759),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null),app.components.x_cancel_dialogue.model.event_cancel,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),cljs.core.PersistentArrayMap.EMPTY], null),app.components.x_cancel_dialogue.model.event_confirm_request,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true,new cljs.core.Keyword(null,"detail","detail",-1545345025),cljs.core.PersistentArrayMap.EMPTY], null),app.components.x_cancel_dialogue.model.event_confirm,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),cljs.core.PersistentArrayMap.EMPTY], null)]);

//# sourceMappingURL=app.components.x_cancel_dialogue.model.js.map
