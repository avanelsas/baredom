goog.provide('app.components.x_stat.x_stat');
goog.scope(function(){
  app.components.x_stat.x_stat.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_stat.x_stat.key_root = "__x_stat_root";
app.components.x_stat.x_stat.key_base = "__x_stat_base";
app.components.x_stat.x_stat.key_label = "__x_stat_label";
app.components.x_stat.x_stat.key_value = "__x_stat_value";
app.components.x_stat.x_stat.key_hint = "__x_stat_hint";
app.components.x_stat.x_stat.key_initialized = "__x_stat_initialized";
app.components.x_stat.x_stat.getv = (function app$components$x_stat$x_stat$getv(el,k){
return app.components.x_stat.x_stat.goog$module$goog$object.get(el,k);
});
app.components.x_stat.x_stat.setv_BANG_ = (function app$components$x_stat$x_stat$setv_BANG_(el,k,v){
return app.components.x_stat.x_stat.goog$module$goog$object.set(el,k,v);
});
app.components.x_stat.x_stat.initialized_QMARK_ = (function app$components$x_stat$x_stat$initialized_QMARK_(el){
return app.components.x_stat.x_stat.getv(el,app.components.x_stat.x_stat.key_initialized) === true;
});
app.components.x_stat.x_stat.mark_initialized_BANG_ = (function app$components$x_stat$x_stat$mark_initialized_BANG_(el){
return app.components.x_stat.x_stat.setv_BANG_(el,app.components.x_stat.x_stat.key_initialized,true);
});
app.components.x_stat.x_stat.read_inputs = (function app$components$x_stat$x_stat$read_inputs(el){
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"trend","trend",54563841),new cljs.core.Keyword(null,"align","align",1964212802),new cljs.core.Keyword(null,"variant","variant",-424354234),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"hint","hint",439639918),new cljs.core.Keyword(null,"size","size",1098693007),new cljs.core.Keyword(null,"loading","loading",-737050189),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Keyword(null,"emphasis","emphasis",293543451)],[el.getAttribute(app.components.x_stat.model.attr_trend),el.getAttribute(app.components.x_stat.model.attr_align),el.getAttribute(app.components.x_stat.model.attr_variant),el.getAttribute(app.components.x_stat.model.attr_value),el.getAttribute(app.components.x_stat.model.attr_hint),el.getAttribute(app.components.x_stat.model.attr_size),el.hasAttribute(app.components.x_stat.model.attr_loading),el.getAttribute(app.components.x_stat.model.attr_label),el.getAttribute(app.components.x_stat.model.attr_emphasis)]);
});
app.components.x_stat.x_stat.set_or_remove_BANG_ = (function app$components$x_stat$x_stat$set_or_remove_BANG_(el,attr,value){
if(cljs.core.truth_(value)){
return el.setAttribute(attr,value);
} else {
return el.removeAttribute(attr);
}
});
app.components.x_stat.x_stat.apply_host_a11y_BANG_ = (function app$components$x_stat$x_stat$apply_host_a11y_BANG_(el,state){
el.setAttribute("role","figure");

app.components.x_stat.x_stat.set_or_remove_BANG_(el,"aria-busy",new cljs.core.Keyword(null,"aria-busy","aria-busy",793864247).cljs$core$IFn$_invoke$arity$1(state));

return app.components.x_stat.x_stat.set_or_remove_BANG_(el,"aria-label",new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(state));
});
app.components.x_stat.x_stat.apply_state_BANG_ = (function app$components$x_stat$x_stat$apply_state_BANG_(base,state){
base.setAttribute("data-variant",new cljs.core.Keyword(null,"variant","variant",-424354234).cljs$core$IFn$_invoke$arity$1(state));

base.setAttribute("data-align",new cljs.core.Keyword(null,"align","align",1964212802).cljs$core$IFn$_invoke$arity$1(state));

base.setAttribute("data-size",new cljs.core.Keyword(null,"size","size",1098693007).cljs$core$IFn$_invoke$arity$1(state));

base.setAttribute("data-emphasis",new cljs.core.Keyword(null,"emphasis","emphasis",293543451).cljs$core$IFn$_invoke$arity$1(state));

base.setAttribute("data-trend",new cljs.core.Keyword(null,"trend","trend",54563841).cljs$core$IFn$_invoke$arity$1(state));

if(cljs.core.truth_(new cljs.core.Keyword(null,"loading","loading",-737050189).cljs$core$IFn$_invoke$arity$1(state))){
return base.setAttribute("data-loading","true");
} else {
return base.removeAttribute("data-loading");
}
});
app.components.x_stat.x_stat.render_BANG_ = (function app$components$x_stat$x_stat$render_BANG_(el){
var state = app.components.x_stat.model.derive_state(app.components.x_stat.x_stat.read_inputs(el));
var base = app.components.x_stat.x_stat.getv(el,app.components.x_stat.x_stat.key_base);
var label_node = app.components.x_stat.x_stat.getv(el,app.components.x_stat.x_stat.key_label);
var value_node = app.components.x_stat.x_stat.getv(el,app.components.x_stat.x_stat.key_value);
var hint_node = app.components.x_stat.x_stat.getv(el,app.components.x_stat.x_stat.key_hint);
if(cljs.core.truth_(base)){
app.components.x_stat.x_stat.apply_host_a11y_BANG_(el,state);

app.components.x_stat.x_stat.apply_state_BANG_(base,state);

(label_node.textContent = (function (){var or__5142__auto__ = new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(state);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})());

(value_node.textContent = (function (){var or__5142__auto__ = new cljs.core.Keyword(null,"value","value",305978217).cljs$core$IFn$_invoke$arity$1(state);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})());

return (hint_node.textContent = (function (){var or__5142__auto__ = new cljs.core.Keyword(null,"hint","hint",439639918).cljs$core$IFn$_invoke$arity$1(state);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})());
} else {
return null;
}
});
app.components.x_stat.x_stat.css_text = (""+":host{display:block;color-scheme:light dark;}"+".base{display:grid;gap:6px;}"+".label{font-size:12px;opacity:0.8;}"+".value{font-weight:600;font-size:20px;}"+".hint{font-size:12px;opacity:0.7;}"+".base[data-loading='true']{opacity:0.6;}");
app.components.x_stat.x_stat.init_dom_BANG_ = (function app$components$x_stat$x_stat$init_dom_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style_el = document.createElement("style");
var base = document.createElement("div");
var icon = document.createElement("div");
var body = document.createElement("div");
var label = document.createElement("div");
var value = document.createElement("div");
var hint = document.createElement("div");
var label_span = document.createElement("span");
var value_span = document.createElement("span");
var hint_span = document.createElement("span");
var icon_slot = document.createElement("slot");
var label_slot = document.createElement("slot");
var value_slot = document.createElement("slot");
var hint_slot = document.createElement("slot");
var default_slot = document.createElement("slot");
(style_el.textContent = app.components.x_stat.x_stat.css_text);

base.setAttribute("part","base");

icon.setAttribute("part","icon");

body.setAttribute("part","body");

label.setAttribute("part","label");

value.setAttribute("part","value");

hint.setAttribute("part","hint");

icon_slot.setAttribute("name","icon");

label_slot.setAttribute("name","label");

value_slot.setAttribute("name","value");

hint_slot.setAttribute("name","hint");

icon.appendChild(icon_slot);

label.appendChild(label_slot);

label.appendChild(label_span);

value.appendChild(value_slot);

value.appendChild(value_span);

hint.appendChild(hint_slot);

hint.appendChild(hint_span);

body.appendChild(label);

body.appendChild(value);

body.appendChild(hint);

body.appendChild(default_slot);

base.appendChild(icon);

base.appendChild(body);

root.appendChild(style_el);

root.appendChild(base);

app.components.x_stat.x_stat.setv_BANG_(el,app.components.x_stat.x_stat.key_root,root);

app.components.x_stat.x_stat.setv_BANG_(el,app.components.x_stat.x_stat.key_base,base);

app.components.x_stat.x_stat.setv_BANG_(el,app.components.x_stat.x_stat.key_label,label_span);

app.components.x_stat.x_stat.setv_BANG_(el,app.components.x_stat.x_stat.key_value,value_span);

return app.components.x_stat.x_stat.setv_BANG_(el,app.components.x_stat.x_stat.key_hint,hint_span);
});
app.components.x_stat.x_stat.init_element_BANG_ = (function app$components$x_stat$x_stat$init_element_BANG_(el){
if(app.components.x_stat.x_stat.initialized_QMARK_(el)){
} else {
app.components.x_stat.x_stat.init_dom_BANG_(el);

app.components.x_stat.x_stat.mark_initialized_BANG_(el);
}

app.components.x_stat.x_stat.render_BANG_(el);

return el;
});
app.components.x_stat.x_stat.connected_callback = (function app$components$x_stat$x_stat$connected_callback(el){
return app.components.x_stat.x_stat.init_element_BANG_(el);
});
app.components.x_stat.x_stat.attribute_changed_callback = (function app$components$x_stat$x_stat$attribute_changed_callback(el,_,___$1,___$2){
if(app.components.x_stat.x_stat.initialized_QMARK_(el)){
return app.components.x_stat.x_stat.render_BANG_(el);
} else {
return app.components.x_stat.x_stat.init_element_BANG_(el);
}
});
app.components.x_stat.x_stat.element_class = (function app$components$x_stat$x_stat$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_stat.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
return app.components.x_stat.x_stat.connected_callback(this$);
}));

(klass.prototype.attributeChangedCallback = (function (name,old_value,new_value){
var this$ = this;
return app.components.x_stat.x_stat.attribute_changed_callback(this$,name,old_value,new_value);
}));

return klass;
});
app.components.x_stat.x_stat.register_BANG_ = (function app$components$x_stat$x_stat$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_stat.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_stat.model.tag_name,app.components.x_stat.x_stat.element_class());
}
});
app.components.x_stat.x_stat.init_BANG_ = (function app$components$x_stat$x_stat$init_BANG_(){
return app.components.x_stat.x_stat.register_BANG_();
});

//# sourceMappingURL=app.components.x_stat.x_stat.js.map
