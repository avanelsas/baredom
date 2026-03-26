goog.provide('app.components.x_grid.x_grid');
goog.scope(function(){
  app.components.x_grid.x_grid.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_grid.x_grid.key_root = "__x_grid_root";
app.components.x_grid.x_grid.key_base = "__x_grid_base";
app.components.x_grid.x_grid.key_initialized = "__x_grid_initialized";
app.components.x_grid.x_grid.getv = (function app$components$x_grid$x_grid$getv(el,k){
return app.components.x_grid.x_grid.goog$module$goog$object.get(el,k);
});
app.components.x_grid.x_grid.setv_BANG_ = (function app$components$x_grid$x_grid$setv_BANG_(el,k,v){
return app.components.x_grid.x_grid.goog$module$goog$object.set(el,k,v);
});
app.components.x_grid.x_grid.initialized_QMARK_ = (function app$components$x_grid$x_grid$initialized_QMARK_(el){
return app.components.x_grid.x_grid.getv(el,app.components.x_grid.x_grid.key_initialized) === true;
});
app.components.x_grid.x_grid.mark_initialized_BANG_ = (function app$components$x_grid$x_grid$mark_initialized_BANG_(el){
return app.components.x_grid.x_grid.setv_BANG_(el,app.components.x_grid.x_grid.key_initialized,true);
});
app.components.x_grid.x_grid.read_inputs = (function app$components$x_grid$x_grid$read_inputs(el){
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"auto-flow","auto-flow",-1524521728),new cljs.core.Keyword(null,"align-items","align-items",-267946462),new cljs.core.Keyword(null,"columns","columns",1998437288),new cljs.core.Keyword(null,"min-column-size","min-column-size",-1066019378),new cljs.core.Keyword(null,"column-gap","column-gap",384822863),new cljs.core.Keyword(null,"gap","gap",80255254),new cljs.core.Keyword(null,"inline","inline",1399884222),new cljs.core.Keyword(null,"row-gap","row-gap",-1809905537),new cljs.core.Keyword(null,"justify-items","justify-items",1638310783)],[el.getAttribute(app.components.x_grid.model.attr_auto_flow),el.getAttribute(app.components.x_grid.model.attr_align_items),el.getAttribute(app.components.x_grid.model.attr_columns),el.getAttribute(app.components.x_grid.model.attr_min_column_size),el.getAttribute(app.components.x_grid.model.attr_column_gap),el.getAttribute(app.components.x_grid.model.attr_gap),el.hasAttribute(app.components.x_grid.model.attr_inline),el.getAttribute(app.components.x_grid.model.attr_row_gap),el.getAttribute(app.components.x_grid.model.attr_justify_items)]);
});
app.components.x_grid.x_grid.style_text = (function app$components$x_grid$x_grid$style_text(){
return "\n  :host {\n  display:block;\n  }\n\n  :host([inline]) {\n  display:inline-block;\n  }\n\n  .base {\n  display:grid;\n  box-sizing:border-box;\n  grid-template-columns:var(--x-grid-columns);\n  row-gap:var(--x-grid-row-gap);\n  column-gap:var(--x-grid-column-gap);\n  align-items:var(--x-grid-align-items);\n  justify-items:var(--x-grid-justify-items);\n  grid-auto-flow:var(--x-grid-auto-flow);\n\n  @media (prefers-reduced-motion: reduce) {\n  .base {\n    transition: none;\n    }\n  }\n  }\n  ";
});
app.components.x_grid.x_grid.apply_state_BANG_ = (function app$components$x_grid$x_grid$apply_state_BANG_(base,state){
var style = base.style;
base.setAttribute("data-gap",new cljs.core.Keyword(null,"gap","gap",80255254).cljs$core$IFn$_invoke$arity$1(state));

style.setProperty("--x-grid-columns",new cljs.core.Keyword(null,"columns","columns",1998437288).cljs$core$IFn$_invoke$arity$1(state));

style.setProperty("--x-grid-row-gap",new cljs.core.Keyword(null,"row-gap","row-gap",-1809905537).cljs$core$IFn$_invoke$arity$1(state));

style.setProperty("--x-grid-column-gap",new cljs.core.Keyword(null,"column-gap","column-gap",384822863).cljs$core$IFn$_invoke$arity$1(state));

style.setProperty("--x-grid-align-items",new cljs.core.Keyword(null,"align-items","align-items",-267946462).cljs$core$IFn$_invoke$arity$1(state));

style.setProperty("--x-grid-justify-items",new cljs.core.Keyword(null,"justify-items","justify-items",1638310783).cljs$core$IFn$_invoke$arity$1(state));

return style.setProperty("--x-grid-auto-flow",new cljs.core.Keyword(null,"auto-flow","auto-flow",-1524521728).cljs$core$IFn$_invoke$arity$1(state));
});
app.components.x_grid.x_grid.render_BANG_ = (function app$components$x_grid$x_grid$render_BANG_(el){
var state = app.components.x_grid.model.derive_state(app.components.x_grid.x_grid.read_inputs(el));
var base = app.components.x_grid.x_grid.getv(el,app.components.x_grid.x_grid.key_base);
if(cljs.core.truth_(base)){
return app.components.x_grid.x_grid.apply_state_BANG_(base,state);
} else {
return null;
}
});
app.components.x_grid.x_grid.init_dom_BANG_ = (function app$components$x_grid$x_grid$init_dom_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style = document.createElement("style");
var base = document.createElement("div");
var slot = document.createElement("slot");
(style.textContent = app.components.x_grid.x_grid.style_text());

base.setAttribute("part","base");

(base.className = "base");

base.appendChild(slot);

root.appendChild(style);

root.appendChild(base);

app.components.x_grid.x_grid.setv_BANG_(el,app.components.x_grid.x_grid.key_root,root);

return app.components.x_grid.x_grid.setv_BANG_(el,app.components.x_grid.x_grid.key_base,base);
});
app.components.x_grid.x_grid.init_element_BANG_ = (function app$components$x_grid$x_grid$init_element_BANG_(el){
if(app.components.x_grid.x_grid.initialized_QMARK_(el)){
} else {
app.components.x_grid.x_grid.init_dom_BANG_(el);

app.components.x_grid.x_grid.mark_initialized_BANG_(el);
}

app.components.x_grid.x_grid.render_BANG_(el);

return el;
});
app.components.x_grid.x_grid.connected_callback = (function app$components$x_grid$x_grid$connected_callback(el){
return app.components.x_grid.x_grid.init_element_BANG_(el);
});
app.components.x_grid.x_grid.attribute_changed_callback = (function app$components$x_grid$x_grid$attribute_changed_callback(el,_,___$1,___$2){
if(app.components.x_grid.x_grid.initialized_QMARK_(el)){
return app.components.x_grid.x_grid.render_BANG_(el);
} else {
return app.components.x_grid.x_grid.init_element_BANG_(el);
}
});
app.components.x_grid.x_grid.element_class = (function app$components$x_grid$x_grid$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_grid.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
return app.components.x_grid.x_grid.connected_callback(this$);
}));

(klass.prototype.attributeChangedCallback = (function (name,old_value,new_value){
var this$ = this;
return app.components.x_grid.x_grid.attribute_changed_callback(this$,name,old_value,new_value);
}));

return klass;
});
app.components.x_grid.x_grid.register_BANG_ = (function app$components$x_grid$x_grid$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_grid.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_grid.model.tag_name,app.components.x_grid.x_grid.element_class());
}
});
app.components.x_grid.x_grid.init_BANG_ = (function app$components$x_grid$x_grid$init_BANG_(){
return app.components.x_grid.x_grid.register_BANG_();
});

//# sourceMappingURL=app.components.x_grid.x_grid.js.map
