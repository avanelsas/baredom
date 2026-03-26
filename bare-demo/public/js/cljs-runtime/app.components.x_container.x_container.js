goog.provide('app.components.x_container.x_container');
app.components.x_container.x_container.state_key = "__xContainerState";
app.components.x_container.x_container.get_prop = (function app$components$x_container$x_container$get_prop(obj,k){
return (obj[k]);
});
app.components.x_container.x_container.set_prop_BANG_ = (function app$components$x_container$x_container$set_prop_BANG_(obj,k,v){
return (obj[k] = v);
});
app.components.x_container.x_container.has_attr_QMARK_ = (function app$components$x_container$x_container$has_attr_QMARK_(el,attr){
return el.hasAttribute(attr);
});
app.components.x_container.x_container.get_attr = (function app$components$x_container$x_container$get_attr(el,attr){
return el.getAttribute(attr);
});
app.components.x_container.x_container.set_bool_attr_BANG_ = (function app$components$x_container$x_container$set_bool_attr_BANG_(el,attr,value){
if(cljs.core.truth_(value)){
return el.setAttribute(attr,"");
} else {
return el.removeAttribute(attr);
}
});
app.components.x_container.x_container.get_el_state = (function app$components$x_container$x_container$get_el_state(el){
return app.components.x_container.x_container.get_prop(el,app.components.x_container.x_container.state_key);
});
app.components.x_container.x_container.set_el_state_BANG_ = (function app$components$x_container$x_container$set_el_state_BANG_(el,state){
return app.components.x_container.x_container.set_prop_BANG_(el,app.components.x_container.x_container.state_key,state);
});
app.components.x_container.x_container.get_default_true_bool = (function app$components$x_container$x_container$get_default_true_bool(el,attr_name){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("false",el.getAttribute(attr_name));
});
app.components.x_container.x_container.read_public_state = (function app$components$x_container$x_container$read_public_state(el){
return app.components.x_container.model.public_state(new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"as","as",1148689641),app.components.x_container.x_container.get_attr(el,app.components.x_container.model.attr_as),new cljs.core.Keyword(null,"size","size",1098693007),app.components.x_container.x_container.get_attr(el,app.components.x_container.model.attr_size),new cljs.core.Keyword(null,"padding","padding",1660304693),app.components.x_container.x_container.get_attr(el,app.components.x_container.model.attr_padding),new cljs.core.Keyword(null,"center","center",-748944368),app.components.x_container.x_container.get_default_true_bool(el,app.components.x_container.model.attr_center),new cljs.core.Keyword(null,"fluid","fluid",-1823657759),app.components.x_container.x_container.has_attr_QMARK_(el,app.components.x_container.model.attr_fluid),new cljs.core.Keyword(null,"label","label",1718410804),app.components.x_container.x_container.get_attr(el,app.components.x_container.model.attr_label)], null));
});
app.components.x_container.x_container.define_bool_prop_BANG_ = (function app$components$x_container$x_container$define_bool_prop_BANG_(proto,prop,attr){
return Object.defineProperty(proto,prop,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return app.components.x_container.x_container.has_attr_QMARK_(this$,attr);
}), "set": (function (v){
var this$ = this;
return app.components.x_container.x_container.set_bool_attr_BANG_(this$,attr,cljs.core.boolean$(v));
})}));
});
app.components.x_container.x_container.define_default_true_bool_prop_BANG_ = (function app$components$x_container$x_container$define_default_true_bool_prop_BANG_(proto,prop,attr){
return Object.defineProperty(proto,prop,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return app.components.x_container.x_container.get_default_true_bool(this$,attr);
}), "set": (function (v){
var this$ = this;
if(cljs.core.boolean$(v)){
return this$.removeAttribute(attr);
} else {
return this$.setAttribute(attr,"false");
}
})}));
});
app.components.x_container.x_container.style_text = "\n  :host{\n  display:block;\n  color-scheme:light dark;\n\n  --x-container-max-width-xs:480px;\n  --x-container-max-width-sm:640px;\n  --x-container-max-width-md:768px;\n  --x-container-max-width-lg:1024px;\n  --x-container-max-width-xl:1280px;\n\n  --x-container-padding-sm:0.5rem;\n  --x-container-padding-md:1rem;\n  --x-container-padding-lg:1.5rem;\n\n  --x-container-padding-block:0;\n  --x-container-margin-inline:auto;\n\n  --x-container-bg:transparent;\n  --x-container-color:#0f172a;\n  --x-container-border:transparent;\n  --x-container-radius:0;\n  --x-container-shadow:none;\n  }\n\n  @media (prefers-color-scheme: dark){\n  :host{\n  --x-container-bg:transparent;\n  --x-container-color:#e5e7eb;\n  --x-container-border:transparent;\n  --x-container-radius:0;\n  --x-container-shadow:none;\n  }\n  }\n\n  [part='base']{\n  display:block;\n  box-sizing:border-box;\n  width:100%;\n  max-width:var(--x-container-max-width-lg);\n  padding-inline:var(--x-container-padding-md);\n  padding-block:var(--x-container-padding-block);\n  margin-inline:0;\n  background:var(--x-container-bg);\n  color:var(--x-container-color);\n  border:1px solid var(--x-container-border);\n  border-radius:var(--x-container-radius);\n  box-shadow:var(--x-container-shadow);\n  }\n\n  [part='base'][data-size='xs']{max-width:var(--x-container-max-width-xs);}\n  [part='base'][data-size='sm']{max-width:var(--x-container-max-width-sm);}\n  [part='base'][data-size='md']{max-width:var(--x-container-max-width-md);}\n  [part='base'][data-size='lg']{max-width:var(--x-container-max-width-lg);}\n  [part='base'][data-size='xl']{max-width:var(--x-container-max-width-xl);}\n  [part='base'][data-size='full']{max-width:none;width:100%;}\n\n  [part='base'][data-padding='none']{padding-inline:0;}\n  [part='base'][data-padding='sm']{padding-inline:var(--x-container-padding-sm);}\n  [part='base'][data-padding='md']{padding-inline:var(--x-container-padding-md);}\n  [part='base'][data-padding='lg']{padding-inline:var(--x-container-padding-lg);}\n\n  [part='base'][data-center='true']{\n  margin-inline:var(--x-container-margin-inline);\n  }\n\n  [part='base'][data-center='false']{\n  margin-inline:0;\n  }\n\n  [part='base'][data-fluid='true']{\n  max-width:none;\n  width:100%;\n  }\n\n  ";
app.components.x_container.x_container.create_shadow_BANG_ = (function app$components$x_container$x_container$create_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style = document.createElement("style");
var base = document.createElement("div");
var slot = document.createElement("slot");
(style.textContent = app.components.x_container.x_container.style_text);

base.setAttribute("part","base");

base.appendChild(slot);

root.appendChild(style);

root.appendChild(base);

return ({"root": root, "base": base, "slot": slot});
});
app.components.x_container.x_container.ensure_root_tag_BANG_ = (function app$components$x_container$x_container$ensure_root_tag_BANG_(state,tag){
var base = (state["base"]);
var current = base.tagName;
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(current.toLowerCase(),tag)){
var root = (state["root"]);
var slot = (state["slot"]);
var new_el = document.createElement(tag);
new_el.setAttribute("part","base");

new_el.appendChild(slot);

root.replaceChild(new_el,base);

return (state["base"] = new_el);
} else {
return null;
}
});
app.components.x_container.x_container.render_BANG_ = (function app$components$x_container$x_container$render_BANG_(el,state){
var public$ = app.components.x_container.x_container.read_public_state(el);
app.components.x_container.x_container.ensure_root_tag_BANG_(state,new cljs.core.Keyword(null,"as","as",1148689641).cljs$core$IFn$_invoke$arity$1(public$));

var base = (state["base"]);
base.setAttribute("data-size",new cljs.core.Keyword(null,"size","size",1098693007).cljs$core$IFn$_invoke$arity$1(public$));

base.setAttribute("data-padding",new cljs.core.Keyword(null,"padding","padding",1660304693).cljs$core$IFn$_invoke$arity$1(public$));

base.setAttribute("data-center",(cljs.core.truth_(new cljs.core.Keyword(null,"center","center",-748944368).cljs$core$IFn$_invoke$arity$1(public$))?"true":"false"));

base.setAttribute("data-fluid",(cljs.core.truth_(new cljs.core.Keyword(null,"fluid","fluid",-1823657759).cljs$core$IFn$_invoke$arity$1(public$))?"true":"false"));

var temp__5821__auto__ = new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(public$);
if(cljs.core.truth_(temp__5821__auto__)){
var label = temp__5821__auto__;
return base.setAttribute("aria-label",label);
} else {
return base.removeAttribute("aria-label");
}
});
app.components.x_container.x_container.connected_BANG_ = (function app$components$x_container$x_container$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_container.x_container.get_el_state(el))){
} else {
var state_21169 = app.components.x_container.x_container.create_shadow_BANG_(el);
app.components.x_container.x_container.set_el_state_BANG_(el,state_21169);
}

return app.components.x_container.x_container.render_BANG_(el,app.components.x_container.x_container.get_el_state(el));
});
app.components.x_container.x_container.attribute_changed_BANG_ = (function app$components$x_container$x_container$attribute_changed_BANG_(el,_,___$1,___$2){
var temp__5823__auto__ = app.components.x_container.x_container.get_el_state(el);
if(cljs.core.truth_(temp__5823__auto__)){
var state = temp__5823__auto__;
return app.components.x_container.x_container.render_BANG_(el,state);
} else {
return null;
}
});
app.components.x_container.x_container.make_constructor = (function app$components$x_container$x_container$make_constructor(){
var ctor_ref = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(null);
var ctor = (function (){
return Reflect.construct(HTMLElement,[],cljs.core.deref(ctor_ref));
});
cljs.core.reset_BANG_(ctor_ref,ctor);

return ctor;
});
app.components.x_container.x_container.define_element_BANG_ = (function app$components$x_container$x_container$define_element_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_container.model.tag_name))){
return null;
} else {
var proto = Object.create(HTMLElement.prototype);
var ctor = app.components.x_container.x_container.make_constructor();
Object.setPrototypeOf(ctor,HTMLElement);

(proto["constructor"] = ctor);

app.components.x_container.x_container.define_default_true_bool_prop_BANG_(proto,"center",app.components.x_container.model.attr_center);

app.components.x_container.x_container.define_bool_prop_BANG_(proto,"fluid",app.components.x_container.model.attr_fluid);

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_container.x_container.connected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (name,old,new$){
var this$ = this;
return app.components.x_container.x_container.attribute_changed_BANG_(this$,name,old,new$);
}));

(ctor["observedAttributes"] = app.components.x_container.model.observed_attributes);

(ctor["prototype"] = proto);

return customElements.define(app.components.x_container.model.tag_name,ctor);
}
});
app.components.x_container.x_container.init_BANG_ = (function app$components$x_container$x_container$init_BANG_(){
return app.components.x_container.x_container.define_element_BANG_();
});

//# sourceMappingURL=app.components.x_container.x_container.js.map
