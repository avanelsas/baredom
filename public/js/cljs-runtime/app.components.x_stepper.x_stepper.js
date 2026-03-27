goog.provide('app.components.x_stepper.x_stepper');
goog.scope(function(){
  app.components.x_stepper.x_stepper.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_stepper.x_stepper.k_refs = "__xStepperRefs";
app.components.x_stepper.x_stepper.k_model = "__xStepperModel";
app.components.x_stepper.x_stepper.k_handlers = "__xStepperHandlers";
app.components.x_stepper.x_stepper.style_text = (""+":host{"+"display:block;"+"color-scheme:light dark;"+"--x-stepper-indicator-size:2rem;"+"--x-stepper-connector-thickness:2px;"+"--x-stepper-step-gap:0.75rem;"+"--x-stepper-font-size:0.875rem;"+"--x-stepper-label-font-weight:500;"+"--x-stepper-desc-font-size:0.75rem;"+"--x-stepper-radius:999px;"+"--x-stepper-motion:120ms;"+"--x-stepper-press-scale:0.93;"+"--x-stepper-focus-ring:rgba(0,0,0,0.55);"+"--x-stepper-disabled-opacity:0.5;"+"--x-stepper-complete-bg:rgba(16,140,72,1);"+"--x-stepper-complete-color:#fff;"+"--x-stepper-complete-connector:rgba(16,140,72,1);"+"--x-stepper-current-bg:rgba(0,102,204,1);"+"--x-stepper-current-color:#fff;"+"--x-stepper-upcoming-bg:rgba(0,0,0,0.08);"+"--x-stepper-upcoming-color:rgba(0,0,0,0.45);"+"--x-stepper-idle-connector:rgba(0,0,0,0.12);"+"--x-stepper-label-done-color:rgba(0,0,0,0.85);"+"--x-stepper-label-current-color:rgba(0,0,0,0.85);"+"--x-stepper-label-upcoming-color:rgba(0,0,0,0.4);"+"--x-stepper-desc-color:rgba(0,0,0,0.4);}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-stepper-focus-ring:rgba(255,255,255,0.7);"+"--x-stepper-complete-bg:rgba(60,210,120,1);"+"--x-stepper-complete-color:#000;"+"--x-stepper-complete-connector:rgba(60,210,120,1);"+"--x-stepper-current-bg:rgba(80,160,255,1);"+"--x-stepper-current-color:#000;"+"--x-stepper-upcoming-bg:rgba(255,255,255,0.1);"+"--x-stepper-upcoming-color:rgba(255,255,255,0.35);"+"--x-stepper-idle-connector:rgba(255,255,255,0.12);"+"--x-stepper-label-done-color:rgba(255,255,255,0.9);"+"--x-stepper-label-current-color:rgba(255,255,255,0.9);"+"--x-stepper-label-upcoming-color:rgba(255,255,255,0.3);"+"--x-stepper-desc-color:rgba(255,255,255,0.35);}}"+"[part=container]{"+"display:flex;"+"align-items:flex-start;"+"margin:0;padding:0;list-style:none;}"+":host([data-orientation=horizontal]) [part=container]{"+"flex-direction:row;}"+":host([data-orientation=horizontal]) [part=step]{"+"display:flex;"+"flex-direction:column;"+"align-items:center;"+"flex:1;"+"min-width:0;}"+":host([data-orientation=horizontal]) [part=step-track]{"+"display:flex;"+"align-items:center;"+"width:100%;}"+":host([data-orientation=horizontal]) [part=step-connector]{"+"flex:1;"+"height:var(--x-stepper-connector-thickness);"+"min-width:8px;}"+":host([data-orientation=horizontal]) [part=step-content]{"+"display:flex;"+"flex-direction:column;"+"align-items:center;"+"text-align:center;"+"padding:var(--x-stepper-step-gap) 4px 0;"+"width:100%;}"+":host([data-orientation=vertical]) [part=container]{"+"flex-direction:column;}"+":host([data-orientation=vertical]) [part=step]{"+"display:flex;"+"flex-direction:row;"+"gap:var(--x-stepper-step-gap);}"+":host([data-orientation=vertical]) [part=step-track]{"+"display:flex;"+"flex-direction:column;"+"align-items:center;"+"flex-shrink:0;"+"width:var(--x-stepper-indicator-size);}"+":host([data-orientation=vertical]) [part=step-connector]{"+"flex:1;"+"width:var(--x-stepper-connector-thickness);"+"min-height:16px;"+"margin:4px 0;}"+":host([data-orientation=vertical]) [part=step-content]{"+"display:flex;"+"flex-direction:column;"+"flex:1;"+"min-width:0;"+"padding-bottom:var(--x-stepper-step-gap);}"+"[part=step-indicator]{"+"display:inline-flex;"+"align-items:center;"+"justify-content:center;"+"width:var(--x-stepper-indicator-size);"+"height:var(--x-stepper-indicator-size);"+"border-radius:var(--x-stepper-radius);"+"border:none;"+"padding:0;margin:0;"+"cursor:pointer;"+"font-size:0.875em;"+"font-weight:600;"+"font-family:inherit;"+"flex-shrink:0;"+"position:relative;z-index:1;"+"transition:"+"background var(--x-stepper-motion) ease,"+"color var(--x-stepper-motion) ease,"+"transform 80ms ease;}"+"[part=step-indicator]:focus{outline:none;}"+"[part=step-indicator]:focus-visible{"+"outline:2px solid var(--x-stepper-focus-ring);"+"outline-offset:3px;}"+"[data-state=complete] [part=step-indicator]{"+"background:var(--x-stepper-complete-bg);"+"color:var(--x-stepper-complete-color);}"+"[data-state=current] [part=step-indicator]{"+"background:var(--x-stepper-current-bg);"+"color:var(--x-stepper-current-color);"+"cursor:default;}"+"[data-state=upcoming] [part=step-indicator]{"+"background:var(--x-stepper-upcoming-bg);"+"color:var(--x-stepper-upcoming-color);}"+"[data-state=complete] [part=step-connector]{"+"background:var(--x-stepper-complete-connector);}"+"[data-state=current] [part=step-connector],"+"[data-state=upcoming] [part=step-connector]{"+"background:var(--x-stepper-idle-connector);}"+"[part=step]:last-child [part=step-connector]{"+"display:none;}"+"[part=step-label]{"+"font-size:var(--x-stepper-font-size);"+"line-height:1.3;"+"font-family:inherit;}"+"[data-state=complete] [part=step-label]{"+"color:var(--x-stepper-label-done-color);}"+"[data-state=current] [part=step-label]{"+"color:var(--x-stepper-label-current-color);"+"font-weight:var(--x-stepper-label-font-weight);}"+"[data-state=upcoming] [part=step-label]{"+"color:var(--x-stepper-label-upcoming-color);}"+"[part=step-description]{"+"font-size:var(--x-stepper-desc-font-size);"+"color:var(--x-stepper-desc-color);"+"line-height:1.3;"+"margin-top:0.2em;}"+":host(:not([disabled])) [data-state=complete] [part=step-indicator]:hover,"+":host(:not([disabled])) [data-state=upcoming] [part=step-indicator]:hover{"+"filter:brightness(0.88);}"+":host(:not([disabled])) [data-state=complete] [part=step-indicator]:active,"+":host(:not([disabled])) [data-state=upcoming] [part=step-indicator]:active{"+"transform:scale(var(--x-stepper-press-scale));}"+":host([disabled]){"+"opacity:var(--x-stepper-disabled-opacity);}"+":host([disabled]) [part=step-indicator]{"+"cursor:default;pointer-events:none;}"+":host([data-size=sm]){"+"--x-stepper-indicator-size:1.5rem;"+"--x-stepper-font-size:0.8125rem;"+"--x-stepper-desc-font-size:0.6875rem;}"+":host([data-size=lg]){"+"--x-stepper-indicator-size:2.5rem;"+"--x-stepper-font-size:1rem;"+"--x-stepper-desc-font-size:0.875rem;}"+"@media (prefers-reduced-motion:reduce){"+"[part=step-indicator]{transition:none;}}");
app.components.x_stepper.x_stepper.init_dom_BANG_ = (function app$components$x_stepper$x_stepper$init_dom_BANG_(el){
var root_23179 = el.attachShadow(({"mode": "open"}));
var style_23180 = document.createElement("style");
var container_23181 = document.createElement("div");
(style_23180.textContent = app.components.x_stepper.x_stepper.style_text);

container_23181.setAttribute("part","container");

container_23181.setAttribute("role","list");

root_23179.appendChild(style_23180);

root_23179.appendChild(container_23181);

app.components.x_stepper.x_stepper.goog$module$goog$object.set(el,app.components.x_stepper.x_stepper.k_refs,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"root","root",-448657453),root_23179,new cljs.core.Keyword(null,"container","container",-1736937707),container_23181], null));

return null;
});
app.components.x_stepper.x_stepper.ensure_refs_BANG_ = (function app$components$x_stepper$x_stepper$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_stepper.x_stepper.goog$module$goog$object.get(el,app.components.x_stepper.x_stepper.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_stepper.x_stepper.init_dom_BANG_(el);

return app.components.x_stepper.x_stepper.goog$module$goog$object.get(el,app.components.x_stepper.x_stepper.k_refs);
}
});
app.components.x_stepper.x_stepper.read_model = (function app$components$x_stepper$x_stepper$read_model(el){
return app.components.x_stepper.model.normalize(new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"steps-raw","steps-raw",1741812533),el.getAttribute(app.components.x_stepper.model.attr_steps),new cljs.core.Keyword(null,"current-raw","current-raw",-1440665509),el.getAttribute(app.components.x_stepper.model.attr_current),new cljs.core.Keyword(null,"orientation-raw","orientation-raw",-471053928),el.getAttribute(app.components.x_stepper.model.attr_orientation),new cljs.core.Keyword(null,"size-raw","size-raw",-2036217423),el.getAttribute(app.components.x_stepper.model.attr_size),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),el.hasAttribute(app.components.x_stepper.model.attr_disabled)], null));
});
app.components.x_stepper.x_stepper.step_aria_label = (function app$components$x_stepper$x_stepper$step_aria_label(idx,label,state){
return (""+"Step "+cljs.core.str.cljs$core$IFn$_invoke$arity$1((idx + (1)))+": "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(label)+" ("+cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var G__23130 = state;
var G__23130__$1 = (((G__23130 instanceof cljs.core.Keyword))?G__23130.fqn:null);
switch (G__23130__$1) {
case "complete":
return "completed";

break;
case "current":
return "current";

break;
case "upcoming":
return "upcoming";

break;
default:
throw (new Error((""+"No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__23130__$1))));

}
})())+")");
});
app.components.x_stepper.x_stepper.make_step_node_BANG_ = (function app$components$x_stepper$x_stepper$make_step_node_BANG_(idx,p__23131,state,disabled_QMARK_){
var map__23132 = p__23131;
var map__23132__$1 = cljs.core.__destructure_map(map__23132);
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23132__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var description = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23132__$1,new cljs.core.Keyword(null,"description","description",-1428560544));
var step_el = document.createElement("div");
var track_el = document.createElement("div");
var btn_el = document.createElement("button");
var num_el = document.createElement("span");
var conn_el = document.createElement("div");
var content_el = document.createElement("div");
var label_el = document.createElement("span");
var desc_el = document.createElement("span");
var state_str = app.components.x_stepper.model.state__GT_attr(state);
var label_str = (function (){var or__5142__auto__ = label;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
step_el.setAttribute("part","step");

step_el.setAttribute("role","listitem");

step_el.setAttribute("data-index",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(idx)));

step_el.setAttribute("data-state",state_str);

track_el.setAttribute("part","step-track");

btn_el.setAttribute("part","step-indicator");

btn_el.setAttribute("type","button");

btn_el.setAttribute("aria-label",app.components.x_stepper.x_stepper.step_aria_label(idx,label_str,state));

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(state,new cljs.core.Keyword(null,"current","current",-1088038603))){
btn_el.setAttribute("aria-current","step");
} else {
btn_el.removeAttribute("aria-current");
}

btn_el.setAttribute("tabindex",(cljs.core.truth_(disabled_QMARK_)?"-1":"0"));

num_el.setAttribute("part","step-number");

num_el.setAttribute("aria-hidden","true");

(num_el.textContent = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(state,new cljs.core.Keyword(null,"complete","complete",-500388775)))?"\u2713":(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((idx + (1))))));

conn_el.setAttribute("part","step-connector");

conn_el.setAttribute("aria-hidden","true");

content_el.setAttribute("part","step-content");

label_el.setAttribute("part","step-label");

(label_el.textContent = label_str);

desc_el.setAttribute("part","step-description");

if(cljs.core.truth_((function (){var and__5140__auto__ = description;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(description,"");
} else {
return and__5140__auto__;
}
})())){
(desc_el.textContent = description);

(desc_el.style.display = "block");
} else {
(desc_el.style.display = "none");
}

btn_el.appendChild(num_el);

track_el.appendChild(btn_el);

track_el.appendChild(conn_el);

content_el.appendChild(label_el);

content_el.appendChild(desc_el);

step_el.appendChild(track_el);

step_el.appendChild(content_el);

return step_el;
});
app.components.x_stepper.x_stepper.apply_model_BANG_ = (function app$components$x_stepper$x_stepper$apply_model_BANG_(el,p__23134){
var map__23136 = p__23134;
var map__23136__$1 = cljs.core.__destructure_map(map__23136);
var m = map__23136__$1;
var steps = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23136__$1,new cljs.core.Keyword(null,"steps","steps",-128433302));
var current = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23136__$1,new cljs.core.Keyword(null,"current","current",-1088038603));
var orientation = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23136__$1,new cljs.core.Keyword(null,"orientation","orientation",623557579));
var size = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23136__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23136__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var map__23139_23185 = app.components.x_stepper.x_stepper.ensure_refs_BANG_(el);
var map__23139_23186__$1 = cljs.core.__destructure_map(map__23139_23185);
var container_23187 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23139_23186__$1,new cljs.core.Keyword(null,"container","container",-1736937707));
var container_23188__$1 = container_23187;
el.setAttribute("data-orientation",app.components.x_stepper.model.orientation__GT_attr(orientation));

el.setAttribute("data-size",app.components.x_stepper.model.size__GT_attr(size));

(container_23188__$1.innerHTML = "");

var seq__23143_23189 = cljs.core.seq(cljs.core.map_indexed.cljs$core$IFn$_invoke$arity$2(cljs.core.vector,steps));
var chunk__23144_23190 = null;
var count__23145_23191 = (0);
var i__23146_23192 = (0);
while(true){
if((i__23146_23192 < count__23145_23191)){
var vec__23159_23193 = chunk__23144_23190.cljs$core$IIndexed$_nth$arity$2(null,i__23146_23192);
var idx_23194 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__23159_23193,(0),null);
var step_23195 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__23159_23193,(1),null);
var state_23196 = app.components.x_stepper.model.step_state(idx_23194,current);
container_23188__$1.appendChild(app.components.x_stepper.x_stepper.make_step_node_BANG_(idx_23194,step_23195,state_23196,disabled_QMARK_));


var G__23197 = seq__23143_23189;
var G__23198 = chunk__23144_23190;
var G__23199 = count__23145_23191;
var G__23200 = (i__23146_23192 + (1));
seq__23143_23189 = G__23197;
chunk__23144_23190 = G__23198;
count__23145_23191 = G__23199;
i__23146_23192 = G__23200;
continue;
} else {
var temp__5823__auto___23201 = cljs.core.seq(seq__23143_23189);
if(temp__5823__auto___23201){
var seq__23143_23202__$1 = temp__5823__auto___23201;
if(cljs.core.chunked_seq_QMARK_(seq__23143_23202__$1)){
var c__5673__auto___23203 = cljs.core.chunk_first(seq__23143_23202__$1);
var G__23204 = cljs.core.chunk_rest(seq__23143_23202__$1);
var G__23205 = c__5673__auto___23203;
var G__23206 = cljs.core.count(c__5673__auto___23203);
var G__23207 = (0);
seq__23143_23189 = G__23204;
chunk__23144_23190 = G__23205;
count__23145_23191 = G__23206;
i__23146_23192 = G__23207;
continue;
} else {
var vec__23162_23208 = cljs.core.first(seq__23143_23202__$1);
var idx_23209 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__23162_23208,(0),null);
var step_23210 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__23162_23208,(1),null);
var state_23211 = app.components.x_stepper.model.step_state(idx_23209,current);
container_23188__$1.appendChild(app.components.x_stepper.x_stepper.make_step_node_BANG_(idx_23209,step_23210,state_23211,disabled_QMARK_));


var G__23212 = cljs.core.next(seq__23143_23202__$1);
var G__23213 = null;
var G__23214 = (0);
var G__23215 = (0);
seq__23143_23189 = G__23212;
chunk__23144_23190 = G__23213;
count__23145_23191 = G__23214;
i__23146_23192 = G__23215;
continue;
}
} else {
}
}
break;
}

app.components.x_stepper.x_stepper.goog$module$goog$object.set(el,app.components.x_stepper.x_stepper.k_model,m);

return null;
});
app.components.x_stepper.x_stepper.update_from_attrs_BANG_ = (function app$components$x_stepper$x_stepper$update_from_attrs_BANG_(el){
var new_m_23216 = app.components.x_stepper.x_stepper.read_model(el);
var old_m_23217 = app.components.x_stepper.x_stepper.goog$module$goog$object.get(el,app.components.x_stepper.x_stepper.k_model);
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_m_23217,new_m_23216)){
app.components.x_stepper.x_stepper.apply_model_BANG_(el,new_m_23216);
} else {
}

return null;
});
app.components.x_stepper.x_stepper.on_container_click = (function app$components$x_stepper$x_stepper$on_container_click(el,e){
var m = (function (){var or__5142__auto__ = app.components.x_stepper.x_stepper.goog$module$goog$object.get(el,app.components.x_stepper.x_stepper.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_stepper.x_stepper.read_model(el);
}
})();
if(cljs.core.truth_(new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m))){
} else {
var target_23219 = e.target;
var btn_23220 = target_23219.closest("[part=step-indicator]");
if(cljs.core.truth_(btn_23220)){
var step_23221 = btn_23220.closest("[part=step]");
var idx_23222 = parseInt(step_23221.getAttribute("data-index"),(10));
var cur_23223 = new cljs.core.Keyword(null,"current","current",-1088038603).cljs$core$IFn$_invoke$arity$1(m);
if(((typeof idx_23222 === 'number') && (((cljs.core.not(isNaN(idx_23222))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(idx_23222,cur_23223)))))){
var detail_23225 = cljs.core.clj__GT_js(app.components.x_stepper.model.change_detail(cur_23223,idx_23222));
var ev_23226 = (new CustomEvent(app.components.x_stepper.model.event_change,({"detail": detail_23225, "bubbles": true, "composed": true, "cancelable": true})));
var ok_QMARK__23227 = el.dispatchEvent(ev_23226);
if(cljs.core.truth_(ok_QMARK__23227)){
el.setAttribute(app.components.x_stepper.model.attr_current,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(idx_23222)));
} else {
}
} else {
}
} else {
}
}

return null;
});
app.components.x_stepper.x_stepper.add_listeners_BANG_ = (function app$components$x_stepper$x_stepper$add_listeners_BANG_(el){
var map__23167_23228 = app.components.x_stepper.x_stepper.ensure_refs_BANG_(el);
var map__23167_23229__$1 = cljs.core.__destructure_map(map__23167_23228);
var container_23230 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23167_23229__$1,new cljs.core.Keyword(null,"container","container",-1736937707));
var container_23231__$1 = container_23230;
var click_h_23232 = (function (e){
return app.components.x_stepper.x_stepper.on_container_click(el,e);
});
container_23231__$1.addEventListener("click",click_h_23232);

app.components.x_stepper.x_stepper.goog$module$goog$object.set(el,app.components.x_stepper.x_stepper.k_handlers,({"click": click_h_23232}));

return null;
});
app.components.x_stepper.x_stepper.remove_listeners_BANG_ = (function app$components$x_stepper$x_stepper$remove_listeners_BANG_(el){
var temp__5823__auto___23233 = app.components.x_stepper.x_stepper.goog$module$goog$object.get(el,app.components.x_stepper.x_stepper.k_handlers);
if(cljs.core.truth_(temp__5823__auto___23233)){
var hs_23234 = temp__5823__auto___23233;
var refs_23235 = app.components.x_stepper.x_stepper.goog$module$goog$object.get(el,app.components.x_stepper.x_stepper.k_refs);
if(cljs.core.truth_(refs_23235)){
var container_23236 = new cljs.core.Keyword(null,"container","container",-1736937707).cljs$core$IFn$_invoke$arity$1(refs_23235);
var click_h_23237 = app.components.x_stepper.x_stepper.goog$module$goog$object.get(hs_23234,"click");
if(cljs.core.truth_((function (){var and__5140__auto__ = container_23236;
if(cljs.core.truth_(and__5140__auto__)){
return click_h_23237;
} else {
return and__5140__auto__;
}
})())){
container_23236.removeEventListener("click",click_h_23237);
} else {
}
} else {
}
} else {
}

app.components.x_stepper.x_stepper.goog$module$goog$object.set(el,app.components.x_stepper.x_stepper.k_handlers,null);

return null;
});
app.components.x_stepper.x_stepper.install_property_accessors_BANG_ = (function app$components$x_stepper$x_stepper$install_property_accessors_BANG_(proto){
Object.defineProperty(proto,app.components.x_stepper.model.attr_steps,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_stepper.model.attr_steps);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "[]";
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_stepper.model.attr_steps,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_stepper.model.attr_steps);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_stepper.model.attr_current,({"get": (function (){
var this$ = this;
return app.components.x_stepper.model.parse_current(this$.getAttribute(app.components.x_stepper.model.attr_current));
}), "set": (function (v){
var this$ = this;
if((v == null)){
return this$.removeAttribute(app.components.x_stepper.model.attr_current);
} else {
return this$.setAttribute(app.components.x_stepper.model.attr_current,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((v | 0))));
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_stepper.model.attr_orientation,({"get": (function (){
var this$ = this;
return app.components.x_stepper.model.orientation__GT_attr(app.components.x_stepper.model.parse_orientation(this$.getAttribute(app.components.x_stepper.model.attr_orientation)));
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_stepper.model.attr_orientation,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_stepper.model.attr_orientation);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_stepper.model.attr_size,({"get": (function (){
var this$ = this;
return app.components.x_stepper.model.size__GT_attr(app.components.x_stepper.model.parse_size(this$.getAttribute(app.components.x_stepper.model.attr_size)));
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_stepper.model.attr_size,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_stepper.model.attr_size);
}
}), "enumerable": true, "configurable": true}));

return Object.defineProperty(proto,app.components.x_stepper.model.attr_disabled,({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_stepper.model.attr_disabled);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_stepper.model.attr_disabled,"");
} else {
return this$.removeAttribute(app.components.x_stepper.model.attr_disabled);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_stepper.x_stepper.element_class = (function app$components$x_stepper$x_stepper$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_stepper.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_stepper.x_stepper.ensure_refs_BANG_(this$);

app.components.x_stepper.x_stepper.remove_listeners_BANG_(this$);

app.components.x_stepper.x_stepper.add_listeners_BANG_(this$);

app.components.x_stepper.x_stepper.update_from_attrs_BANG_(this$);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
app.components.x_stepper.x_stepper.remove_listeners_BANG_(this$);

return null;
}));

(klass.prototype.attributeChangedCallback = (function (_name,old_val,new_val){
var this$ = this;
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
app.components.x_stepper.x_stepper.update_from_attrs_BANG_(this$);
} else {
}

return null;
}));

app.components.x_stepper.x_stepper.install_property_accessors_BANG_(klass.prototype);

return klass;
});
app.components.x_stepper.x_stepper.register_BANG_ = (function app$components$x_stepper$x_stepper$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_stepper.model.tag_name))){
} else {
customElements.define(app.components.x_stepper.model.tag_name,app.components.x_stepper.x_stepper.element_class());
}

return null;
});
app.components.x_stepper.x_stepper.init_BANG_ = (function app$components$x_stepper$x_stepper$init_BANG_(){
return app.components.x_stepper.x_stepper.register_BANG_();
});

//# sourceMappingURL=app.components.x_stepper.x_stepper.js.map
