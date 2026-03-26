goog.provide('app.components.x_select.x_select');
goog.scope(function(){
  app.components.x_select.x_select.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_select.x_select.k_refs = "__xSelectRefs";
app.components.x_select.x_select.k_handlers = "__xSelectHandlers";
app.components.x_select.x_select.style_text = (""+":host{"+"display:inline-block;"+"color-scheme:light dark;"+"--x-select-height-sm:2rem;"+"--x-select-height-md:2.5rem;"+"--x-select-height-lg:3rem;"+"--x-select-radius:0.5rem;"+"--x-select-font-size-sm:0.75rem;"+"--x-select-font-size-md:0.875rem;"+"--x-select-font-size-lg:1rem;"+"--x-select-padding-inline:0.75rem;"+"--x-select-bg:#ffffff;"+"--x-select-bg-disabled:#f8fafc;"+"--x-select-fg:#0f172a;"+"--x-select-fg-disabled:#94a3b8;"+"--x-select-placeholder-fg:#94a3b8;"+"--x-select-border:#cbd5e1;"+"--x-select-border-hover:#94a3b8;"+"--x-select-border-focus:#3b82f6;"+"--x-select-chevron:#64748b;"+"--x-select-focus-ring:#93c5fd;"+"--x-select-shadow:0 1px 2px rgba(15,23,42,0.06);"+"--x-select-transition-duration:140ms;"+"}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-select-bg:#1f2937;"+"--x-select-bg-disabled:#111827;"+"--x-select-fg:#f1f5f9;"+"--x-select-border:#374151;"+"--x-select-border-hover:#4b5563;"+"--x-select-border-focus:#60a5fa;"+"--x-select-chevron:#94a3b8;"+"}"+"}"+"[part=wrapper]{"+"position:relative;"+"display:flex;"+"align-items:stretch;"+"height:var(--x-select-height-md);"+"border:1px solid var(--x-select-border);"+"border-radius:var(--x-select-radius);"+"background:var(--x-select-bg);"+"box-shadow:var(--x-select-shadow);"+"transition:border-color var(--x-select-transition-duration) ease,"+"box-shadow var(--x-select-transition-duration) ease;"+"overflow:hidden;"+"font-size:var(--x-select-font-size-md);"+"}"+"[part=wrapper][data-size=sm]{"+"height:var(--x-select-height-sm);"+"font-size:var(--x-select-font-size-sm);"+"}"+"[part=wrapper][data-size=lg]{"+"height:var(--x-select-height-lg);"+"font-size:var(--x-select-font-size-lg);"+"}"+"[part=wrapper][data-disabled]{"+"background:var(--x-select-bg-disabled);"+"opacity:0.6;"+"pointer-events:none;"+"}"+"[part=wrapper]:hover:not([data-disabled]){"+"border-color:var(--x-select-border-hover);"+"}"+"[part=wrapper]:focus-within:not([data-disabled]){"+"border-color:var(--x-select-border-focus);"+"box-shadow:0 0 0 3px var(--x-select-focus-ring);"+"}"+"[part=select]{"+"appearance:none;"+"-webkit-appearance:none;"+"flex:1;"+"min-width:0;"+"background:transparent;"+"border:none;"+"outline:none;"+"color:var(--x-select-fg);"+"font-size:inherit;"+"font-family:inherit;"+"padding-inline:var(--x-select-padding-inline);"+"padding-inline-end:calc(var(--x-select-padding-inline) + 1.75rem);"+"cursor:pointer;"+"width:100%;"+"}"+"[part=select]:disabled{"+"color:var(--x-select-fg-disabled);"+"cursor:default;"+"}"+"[part=select] option[data-placeholder]{"+"color:var(--x-select-placeholder-fg);"+"}"+"[part=chevron]{"+"position:absolute;"+"right:var(--x-select-padding-inline);"+"top:50%;"+"transform:translateY(-50%);"+"display:flex;"+"align-items:center;"+"justify-content:center;"+"pointer-events:none;"+"color:var(--x-select-chevron);"+"width:1rem;"+"height:1rem;"+"}"+"slot{"+"display:none;"+"}"+"@media (prefers-reduced-motion:reduce){"+"[part=wrapper]{transition:none;}"+"}");
app.components.x_select.x_select.chevron_svg = (""+"<svg xmlns=\"http://www.w3.org/2000/svg\""+" width=\"16\" height=\"16\" viewBox=\"0 0 16 16\""+" fill=\"none\" stroke=\"currentColor\""+" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\""+" aria-hidden=\"true\">"+"<polyline points=\"4 6 8 10 12 6\"/>"+"</svg>");
app.components.x_select.x_select.make_el = (function app$components$x_select$x_select$make_el(tag){
return document.createElement(tag);
});
app.components.x_select.x_select.set_attr_BANG_ = (function app$components$x_select$x_select$set_attr_BANG_(el,attr,val){
return el.setAttribute(attr,val);
});
app.components.x_select.x_select.remove_attr_BANG_ = (function app$components$x_select$x_select$remove_attr_BANG_(el,attr){
return el.removeAttribute(attr);
});
app.components.x_select.x_select.has_attr_QMARK_ = (function app$components$x_select$x_select$has_attr_QMARK_(el,attr){
return el.hasAttribute(attr);
});
app.components.x_select.x_select.get_attr = (function app$components$x_select$x_select$get_attr(el,attr){
return el.getAttribute(attr);
});
app.components.x_select.x_select.set_bool_attr_BANG_ = (function app$components$x_select$x_select$set_bool_attr_BANG_(el,attr,value){
if(cljs.core.truth_(value)){
return app.components.x_select.x_select.set_attr_BANG_(el,attr,"");
} else {
return app.components.x_select.x_select.remove_attr_BANG_(el,attr);
}
});
app.components.x_select.x_select.make_shadow_BANG_ = (function app$components$x_select$x_select$make_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style_el = app.components.x_select.x_select.make_el("style");
var wrapper_el = app.components.x_select.x_select.make_el("div");
var select_el = app.components.x_select.x_select.make_el("select");
var ph_opt_el = app.components.x_select.x_select.make_el("option");
var chevron_el = app.components.x_select.x_select.make_el("span");
var slot_el = app.components.x_select.x_select.make_el("slot");
(style_el.textContent = app.components.x_select.x_select.style_text);

app.components.x_select.x_select.set_attr_BANG_(wrapper_el,"part","wrapper");

app.components.x_select.x_select.set_attr_BANG_(select_el,"part","select");

app.components.x_select.x_select.set_attr_BANG_(chevron_el,"part","chevron");

app.components.x_select.x_select.set_attr_BANG_(chevron_el,"aria-hidden","true");

app.components.x_select.x_select.set_attr_BANG_(ph_opt_el,"data-placeholder","");

app.components.x_select.x_select.set_attr_BANG_(ph_opt_el,"value","");

(ph_opt_el.hidden = true);

(ph_opt_el.disabled = true);

(ph_opt_el.selected = true);

(chevron_el.innerHTML = app.components.x_select.x_select.chevron_svg);

select_el.appendChild(ph_opt_el);

wrapper_el.appendChild(select_el);

wrapper_el.appendChild(chevron_el);

root.appendChild(style_el);

root.appendChild(wrapper_el);

root.appendChild(slot_el);

var refs = ({"root": root, "wrapper": wrapper_el, "select": select_el, "placeholder-opt": ph_opt_el, "slot": slot_el, "chevron": chevron_el});
app.components.x_select.x_select.goog$module$goog$object.set(el,app.components.x_select.x_select.k_refs,refs);

return refs;
});
app.components.x_select.x_select.read_model = (function app$components$x_select$x_select$read_model(el){
return app.components.x_select.model.normalize(new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),app.components.x_select.x_select.has_attr_QMARK_(el,app.components.x_select.model.attr_disabled),new cljs.core.Keyword(null,"required-present?","required-present?",-1253354196),app.components.x_select.x_select.has_attr_QMARK_(el,app.components.x_select.model.attr_required),new cljs.core.Keyword(null,"size-raw","size-raw",-2036217423),app.components.x_select.x_select.get_attr(el,app.components.x_select.model.attr_size),new cljs.core.Keyword(null,"placeholder-raw","placeholder-raw",-986138657),app.components.x_select.x_select.get_attr(el,app.components.x_select.model.attr_placeholder),new cljs.core.Keyword(null,"value-raw","value-raw",-1649205133),app.components.x_select.x_select.get_attr(el,app.components.x_select.model.attr_value),new cljs.core.Keyword(null,"name-raw","name-raw",1493628068),app.components.x_select.x_select.get_attr(el,app.components.x_select.model.attr_name)], null));
});
app.components.x_select.x_select.render_BANG_ = (function app$components$x_select$x_select$render_BANG_(el){
var temp__5823__auto__ = app.components.x_select.x_select.goog$module$goog$object.get(el,app.components.x_select.x_select.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var wrapper_el = app.components.x_select.x_select.goog$module$goog$object.get(refs,"wrapper");
var select_el = app.components.x_select.x_select.goog$module$goog$object.get(refs,"select");
var ph_opt_el = app.components.x_select.x_select.goog$module$goog$object.get(refs,"placeholder-opt");
var m = app.components.x_select.x_select.read_model(el);
var disabled_QMARK_ = new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m);
var required_QMARK_ = new cljs.core.Keyword(null,"required?","required?",-872514462).cljs$core$IFn$_invoke$arity$1(m);
var size = new cljs.core.Keyword(null,"size","size",1098693007).cljs$core$IFn$_invoke$arity$1(m);
var placeholder = new cljs.core.Keyword(null,"placeholder","placeholder",-104873083).cljs$core$IFn$_invoke$arity$1(m);
var value = new cljs.core.Keyword(null,"value","value",305978217).cljs$core$IFn$_invoke$arity$1(m);
var name_val = new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m);
(select_el.disabled = disabled_QMARK_);

if(cljs.core.truth_(required_QMARK_)){
app.components.x_select.x_select.set_attr_BANG_(select_el,app.components.x_select.model.attr_required,"");
} else {
app.components.x_select.x_select.remove_attr_BANG_(select_el,app.components.x_select.model.attr_required);
}

if(((typeof name_val === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(name_val,"")))){
app.components.x_select.x_select.set_attr_BANG_(select_el,app.components.x_select.model.attr_name,name_val);
} else {
app.components.x_select.x_select.remove_attr_BANG_(select_el,app.components.x_select.model.attr_name);
}

if(((typeof placeholder === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(placeholder,"")))){
(ph_opt_el.textContent = placeholder);

(ph_opt_el.hidden = false);
} else {
(ph_opt_el.hidden = true);
}

(select_el.value = (function (){var or__5142__auto__ = value;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})());

app.components.x_select.x_select.set_attr_BANG_(wrapper_el,"data-size",size);

if(cljs.core.truth_(disabled_QMARK_)){
app.components.x_select.x_select.set_attr_BANG_(wrapper_el,"data-disabled","");
} else {
app.components.x_select.x_select.remove_attr_BANG_(wrapper_el,"data-disabled");
}

if((((name_val == null)) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(name_val,"")))){
return app.components.x_select.x_select.set_attr_BANG_(select_el,"aria-label",(function (){var or__5142__auto__ = placeholder;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "select";
}
})());
} else {
return app.components.x_select.x_select.remove_attr_BANG_(select_el,"aria-label");
}
} else {
return null;
}
});
app.components.x_select.x_select.sync_options_BANG_ = (function app$components$x_select$x_select$sync_options_BANG_(el){
var temp__5823__auto__ = app.components.x_select.x_select.goog$module$goog$object.get(el,app.components.x_select.x_select.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var select_el = app.components.x_select.x_select.goog$module$goog$object.get(refs,"select");
var ph_opt_el = app.components.x_select.x_select.goog$module$goog$object.get(refs,"placeholder-opt");
var slot_el = app.components.x_select.x_select.goog$module$goog$object.get(refs,"slot");
var assigned = slot_el.assignedElements(({"flatten": true}));
while(true){
var last_child_23070 = select_el.lastChild;
if(cljs.core.truth_((function (){var and__5140__auto__ = last_child_23070;
if(cljs.core.truth_(and__5140__auto__)){
return (!((last_child_23070 === ph_opt_el)));
} else {
return and__5140__auto__;
}
})())){
select_el.removeChild(last_child_23070);

continue;
} else {
}
break;
}

var seq__23054_23071 = cljs.core.seq(cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(assigned));
var chunk__23055_23072 = null;
var count__23056_23073 = (0);
var i__23057_23074 = (0);
while(true){
if((i__23057_23074 < count__23056_23073)){
var node_23075 = chunk__23055_23072.cljs$core$IIndexed$_nth$arity$2(null,i__23057_23074);
var tag_lower_23076 = node_23075.tagName.toLowerCase();
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(tag_lower_23076,"option")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(tag_lower_23076,"optgroup")))){
select_el.appendChild(node_23075.cloneNode(true));
} else {
}


var G__23077 = seq__23054_23071;
var G__23078 = chunk__23055_23072;
var G__23079 = count__23056_23073;
var G__23080 = (i__23057_23074 + (1));
seq__23054_23071 = G__23077;
chunk__23055_23072 = G__23078;
count__23056_23073 = G__23079;
i__23057_23074 = G__23080;
continue;
} else {
var temp__5823__auto___23081__$1 = cljs.core.seq(seq__23054_23071);
if(temp__5823__auto___23081__$1){
var seq__23054_23082__$1 = temp__5823__auto___23081__$1;
if(cljs.core.chunked_seq_QMARK_(seq__23054_23082__$1)){
var c__5673__auto___23083 = cljs.core.chunk_first(seq__23054_23082__$1);
var G__23084 = cljs.core.chunk_rest(seq__23054_23082__$1);
var G__23085 = c__5673__auto___23083;
var G__23086 = cljs.core.count(c__5673__auto___23083);
var G__23087 = (0);
seq__23054_23071 = G__23084;
chunk__23055_23072 = G__23085;
count__23056_23073 = G__23086;
i__23057_23074 = G__23087;
continue;
} else {
var node_23088 = cljs.core.first(seq__23054_23082__$1);
var tag_lower_23089 = node_23088.tagName.toLowerCase();
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(tag_lower_23089,"option")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(tag_lower_23089,"optgroup")))){
select_el.appendChild(node_23088.cloneNode(true));
} else {
}


var G__23090 = cljs.core.next(seq__23054_23082__$1);
var G__23091 = null;
var G__23092 = (0);
var G__23093 = (0);
seq__23054_23071 = G__23090;
chunk__23055_23072 = G__23091;
count__23056_23073 = G__23092;
i__23057_23074 = G__23093;
continue;
}
} else {
}
}
break;
}

return app.components.x_select.x_select.render_BANG_(el);
} else {
return null;
}
});
app.components.x_select.x_select.dispatch_BANG_ = (function app$components$x_select$x_select$dispatch_BANG_(el,event_name,detail){
return el.dispatchEvent((new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": false}))));
});
app.components.x_select.x_select.make_change_handler = (function app$components$x_select$x_select$make_change_handler(el){
return (function (_){
var temp__5823__auto__ = app.components.x_select.x_select.goog$module$goog$object.get(el,app.components.x_select.x_select.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var sel = app.components.x_select.x_select.goog$module$goog$object.get(refs,"select");
var value = sel.value;
var label = (((sel.selectedOptions.length > (0)))?(sel.selectedOptions[(0)]).text:"");
return app.components.x_select.x_select.dispatch_BANG_(el,app.components.x_select.model.event_select_change,({"value": value, "label": label}));
} else {
return null;
}
});
});
app.components.x_select.x_select.make_slotchange_handler = (function app$components$x_select$x_select$make_slotchange_handler(el){
return (function (_){
return app.components.x_select.x_select.sync_options_BANG_(el);
});
});
app.components.x_select.x_select.add_listeners_BANG_ = (function app$components$x_select$x_select$add_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_select.x_select.goog$module$goog$object.get(el,app.components.x_select.x_select.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var select_el = app.components.x_select.x_select.goog$module$goog$object.get(refs,"select");
var slot_el = app.components.x_select.x_select.goog$module$goog$object.get(refs,"slot");
var change_h = app.components.x_select.x_select.make_change_handler(el);
var slotchange_h = app.components.x_select.x_select.make_slotchange_handler(el);
var handlers = ({"change": change_h, "slotchange": slotchange_h});
select_el.addEventListener("change",change_h);

slot_el.addEventListener("slotchange",slotchange_h);

return app.components.x_select.x_select.goog$module$goog$object.set(el,app.components.x_select.x_select.k_handlers,handlers);
} else {
return null;
}
});
app.components.x_select.x_select.remove_listeners_BANG_ = (function app$components$x_select$x_select$remove_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_select.x_select.goog$module$goog$object.get(el,app.components.x_select.x_select.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var temp__5823__auto____$1 = app.components.x_select.x_select.goog$module$goog$object.get(el,app.components.x_select.x_select.k_handlers);
if(cljs.core.truth_(temp__5823__auto____$1)){
var handlers = temp__5823__auto____$1;
var select_el_23094 = app.components.x_select.x_select.goog$module$goog$object.get(refs,"select");
var slot_el_23095 = app.components.x_select.x_select.goog$module$goog$object.get(refs,"slot");
select_el_23094.removeEventListener("change",app.components.x_select.x_select.goog$module$goog$object.get(handlers,"change"));

slot_el_23095.removeEventListener("slotchange",app.components.x_select.x_select.goog$module$goog$object.get(handlers,"slotchange"));

return app.components.x_select.x_select.goog$module$goog$object.set(el,app.components.x_select.x_select.k_handlers,null);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_select.x_select.connected_BANG_ = (function app$components$x_select$x_select$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_select.x_select.goog$module$goog$object.get(el,app.components.x_select.x_select.k_refs))){
} else {
app.components.x_select.x_select.make_shadow_BANG_(el);
}

app.components.x_select.x_select.remove_listeners_BANG_(el);

app.components.x_select.x_select.add_listeners_BANG_(el);

app.components.x_select.x_select.sync_options_BANG_(el);

return app.components.x_select.x_select.render_BANG_(el);
});
app.components.x_select.x_select.disconnected_BANG_ = (function app$components$x_select$x_select$disconnected_BANG_(el){
return app.components.x_select.x_select.remove_listeners_BANG_(el);
});
app.components.x_select.x_select.attribute_changed_BANG_ = (function app$components$x_select$x_select$attribute_changed_BANG_(el,_name,_old,_new){
return app.components.x_select.x_select.render_BANG_(el);
});
app.components.x_select.x_select.define_bool_prop_BANG_ = (function app$components$x_select$x_select$define_bool_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return app.components.x_select.x_select.has_attr_QMARK_(this$,attr_name);
}), "set": (function (v){
var this$ = this;
return app.components.x_select.x_select.set_bool_attr_BANG_(this$,attr_name,cljs.core.boolean$(v));
})}));
});
app.components.x_select.x_select.define_string_prop_BANG_ = (function app$components$x_select$x_select$define_string_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var or__5142__auto__ = app.components.x_select.x_select.get_attr(this$,attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return null;
}
}), "set": (function (v){
var this$ = this;
if((((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined)))){
return app.components.x_select.x_select.set_attr_BANG_(this$,attr_name,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return app.components.x_select.x_select.remove_attr_BANG_(this$,attr_name);
}
})}));
});
app.components.x_select.x_select.element_class = (function app$components$x_select$x_select$element_class(){
var cls = (class extends HTMLElement {});
var proto = cls.prototype;
Object.defineProperty(cls,"observedAttributes",({"get": (function (){
return app.components.x_select.model.observed_attributes;
})}));

app.components.x_select.x_select.define_bool_prop_BANG_(proto,"disabled",app.components.x_select.model.attr_disabled);

app.components.x_select.x_select.define_bool_prop_BANG_(proto,"required",app.components.x_select.model.attr_required);

app.components.x_select.x_select.define_string_prop_BANG_(proto,"value",app.components.x_select.model.attr_value);

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_select.x_select.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_select.x_select.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_select.x_select.attribute_changed_BANG_(this$,n,o,v);
}));

return cls;
});
app.components.x_select.x_select.init_BANG_ = (function app$components$x_select$x_select$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_select.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_select.model.tag_name,app.components.x_select.x_select.element_class());
}
});

//# sourceMappingURL=app.components.x_select.x_select.js.map
