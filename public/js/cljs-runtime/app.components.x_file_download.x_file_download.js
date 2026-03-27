goog.provide('app.components.x_file_download.x_file_download');
goog.scope(function(){
  app.components.x_file_download.x_file_download.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_file_download.x_file_download.k_refs = "__xFileDownloadRefs";
app.components.x_file_download.x_file_download.k_handlers = "__xFileDownloadHandlers";
app.components.x_file_download.x_file_download.style_text = (""+":host{"+"display:inline-block;"+"color-scheme:light dark;"+"--x-file-download-bg:#2563eb;"+"--x-file-download-color:#ffffff;"+"--x-file-download-hover-bg:#1d4ed8;"+"--x-file-download-active-bg:#1e40af;"+"--x-file-download-border-radius:6px;"+"--x-file-download-padding:0.5rem 1rem;"+"--x-file-download-font-size:0.875rem;"+"--x-file-download-font-weight:500;"+"--x-file-download-gap:0.375rem;"+"--x-file-download-icon-size:1em;"+"--x-file-download-focus-ring:#60a5fa;"+"--x-file-download-disabled-opacity:0.45;"+"--x-file-download-transition:background 120ms ease;"+"}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-file-download-bg:#3b82f6;"+"--x-file-download-hover-bg:#2563eb;"+"--x-file-download-active-bg:#1d4ed8;"+"--x-file-download-focus-ring:#93c5fd;"+"}"+"}"+"[part=anchor]{"+"display:inline-flex;"+"align-items:center;"+"gap:var(--x-file-download-gap);"+"padding:var(--x-file-download-padding);"+"background:var(--x-file-download-bg);"+"color:var(--x-file-download-color);"+"border-radius:var(--x-file-download-border-radius);"+"font-size:var(--x-file-download-font-size);"+"font-weight:var(--x-file-download-font-weight);"+"text-decoration:none;"+"cursor:pointer;"+"box-sizing:border-box;"+"transition:var(--x-file-download-transition);"+"outline:none;"+"user-select:none;"+"-webkit-user-select:none;"+"}"+"[part=anchor]:hover{"+"background:var(--x-file-download-hover-bg);"+"}"+"[part=anchor]:active{"+"background:var(--x-file-download-active-bg);"+"}"+"[part=anchor]:focus-visible{"+"outline:2px solid var(--x-file-download-focus-ring);"+"outline-offset:2px;"+"}"+"[part=icon]{"+"display:inline-flex;"+"align-items:center;"+"justify-content:center;"+"width:var(--x-file-download-icon-size);"+"height:var(--x-file-download-icon-size);"+"flex-shrink:0;"+"}"+"[part=content]{"+"display:inline;"+"}"+":host([data-disabled]) [part=anchor]{"+"opacity:var(--x-file-download-disabled-opacity);"+"pointer-events:none;"+"cursor:default;"+"}"+"@media (prefers-reduced-motion:reduce){"+"[part=anchor]{transition:none;}"+"}");
app.components.x_file_download.x_file_download.download_icon_svg = "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 16 16\" fill=\"currentColor\" width=\"1em\" height=\"1em\" aria-hidden=\"true\"><path d=\"M8 1a.75.75 0 0 1 .75.75v6.19l1.97-1.97a.75.75 0 1 1 1.06 1.06l-3.25 3.25a.75.75 0 0 1-1.06 0L4.22 7.03a.75.75 0 0 1 1.06-1.06L7.25 7.94V1.75A.75.75 0 0 1 8 1ZM2.5 13.25a.75.75 0 0 1 .75-.75h9.5a.75.75 0 0 1 0 1.5h-9.5a.75.75 0 0 1-.75-.75Z\"/></svg>";
app.components.x_file_download.x_file_download.make_el = (function app$components$x_file_download$x_file_download$make_el(tag){
return document.createElement(tag);
});
app.components.x_file_download.x_file_download.set_attr_BANG_ = (function app$components$x_file_download$x_file_download$set_attr_BANG_(el,attr,val){
return el.setAttribute(attr,val);
});
app.components.x_file_download.x_file_download.remove_attr_BANG_ = (function app$components$x_file_download$x_file_download$remove_attr_BANG_(el,attr){
return el.removeAttribute(attr);
});
app.components.x_file_download.x_file_download.has_attr_QMARK_ = (function app$components$x_file_download$x_file_download$has_attr_QMARK_(el,attr){
return el.hasAttribute(attr);
});
app.components.x_file_download.x_file_download.get_attr = (function app$components$x_file_download$x_file_download$get_attr(el,attr){
return el.getAttribute(attr);
});
app.components.x_file_download.x_file_download.set_bool_attr_BANG_ = (function app$components$x_file_download$x_file_download$set_bool_attr_BANG_(el,attr,value){
if(cljs.core.truth_(value)){
return app.components.x_file_download.x_file_download.set_attr_BANG_(el,attr,"");
} else {
return app.components.x_file_download.x_file_download.remove_attr_BANG_(el,attr);
}
});
app.components.x_file_download.x_file_download.make_shadow_BANG_ = (function app$components$x_file_download$x_file_download$make_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style_el = app.components.x_file_download.x_file_download.make_el("style");
var anchor_el = app.components.x_file_download.x_file_download.make_el("a");
var icon_el = app.components.x_file_download.x_file_download.make_el("span");
var content_el = app.components.x_file_download.x_file_download.make_el("span");
var slot_el = app.components.x_file_download.x_file_download.make_el("slot");
(style_el.textContent = app.components.x_file_download.x_file_download.style_text);

app.components.x_file_download.x_file_download.set_attr_BANG_(anchor_el,"part","anchor");

app.components.x_file_download.x_file_download.set_attr_BANG_(icon_el,"part","icon");

app.components.x_file_download.x_file_download.set_attr_BANG_(content_el,"part","content");

(icon_el.innerHTML = app.components.x_file_download.x_file_download.download_icon_svg);

content_el.appendChild(slot_el);

anchor_el.appendChild(icon_el);

anchor_el.appendChild(content_el);

root.appendChild(style_el);

root.appendChild(anchor_el);

var refs = ({"anchor-el": anchor_el, "icon-el": icon_el, "content-el": content_el});
app.components.x_file_download.x_file_download.goog$module$goog$object.set(el,app.components.x_file_download.x_file_download.k_refs,refs);

return refs;
});
app.components.x_file_download.x_file_download.read_model = (function app$components$x_file_download$x_file_download$read_model(el){
return app.components.x_file_download.model.normalize(new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"href-raw","href-raw",-1680949453),app.components.x_file_download.x_file_download.get_attr(el,app.components.x_file_download.model.attr_href),new cljs.core.Keyword(null,"filename-raw","filename-raw",-706184256),app.components.x_file_download.x_file_download.get_attr(el,app.components.x_file_download.model.attr_filename),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),app.components.x_file_download.x_file_download.has_attr_QMARK_(el,app.components.x_file_download.model.attr_disabled),new cljs.core.Keyword(null,"aria-label-raw","aria-label-raw",-412828103),app.components.x_file_download.x_file_download.get_attr(el,app.components.x_file_download.model.attr_aria_label)], null));
});
app.components.x_file_download.x_file_download.render_BANG_ = (function app$components$x_file_download$x_file_download$render_BANG_(el){
var temp__5823__auto__ = app.components.x_file_download.x_file_download.goog$module$goog$object.get(el,app.components.x_file_download.x_file_download.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var anchor_el = app.components.x_file_download.x_file_download.goog$module$goog$object.get(refs,"anchor-el");
var m = app.components.x_file_download.x_file_download.read_model(el);
var href = new cljs.core.Keyword(null,"href","href",-793805698).cljs$core$IFn$_invoke$arity$1(m);
var filename = new cljs.core.Keyword(null,"filename","filename",-1428840783).cljs$core$IFn$_invoke$arity$1(m);
var disabled_QMARK_ = new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m);
var aria_label = new cljs.core.Keyword(null,"aria-label","aria-label",455891514).cljs$core$IFn$_invoke$arity$1(m);
(anchor_el.href = href);

if(((typeof filename === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(filename,"")))){
app.components.x_file_download.x_file_download.set_attr_BANG_(anchor_el,"download",filename);
} else {
app.components.x_file_download.x_file_download.remove_attr_BANG_(anchor_el,"download");
}

if(cljs.core.truth_(disabled_QMARK_)){
app.components.x_file_download.x_file_download.set_attr_BANG_(anchor_el,"aria-disabled","true");
} else {
app.components.x_file_download.x_file_download.remove_attr_BANG_(anchor_el,"aria-disabled");
}

if(cljs.core.truth_(aria_label)){
app.components.x_file_download.x_file_download.set_attr_BANG_(anchor_el,"aria-label",aria_label);
} else {
app.components.x_file_download.x_file_download.remove_attr_BANG_(anchor_el,"aria-label");
}

return app.components.x_file_download.x_file_download.set_bool_attr_BANG_(el,"data-disabled",disabled_QMARK_);
} else {
return null;
}
});
app.components.x_file_download.x_file_download.make_click_handler = (function app$components$x_file_download$x_file_download$make_click_handler(el){
return (function (evt){
var disabled_QMARK_ = app.components.x_file_download.x_file_download.has_attr_QMARK_(el,app.components.x_file_download.model.attr_disabled);
if(cljs.core.truth_(disabled_QMARK_)){
return evt.preventDefault();
} else {
var m = app.components.x_file_download.x_file_download.read_model(el);
var detail = ({"href": new cljs.core.Keyword(null,"href","href",-793805698).cljs$core$IFn$_invoke$arity$1(m), "filename": new cljs.core.Keyword(null,"filename","filename",-1428840783).cljs$core$IFn$_invoke$arity$1(m)});
var custom = (new CustomEvent(app.components.x_file_download.model.event_click,({"detail": detail, "bubbles": true, "composed": true, "cancelable": true})));
el.dispatchEvent(custom);

if(cljs.core.truth_(custom.defaultPrevented)){
return evt.preventDefault();
} else {
return null;
}
}
});
});
app.components.x_file_download.x_file_download.add_listeners_BANG_ = (function app$components$x_file_download$x_file_download$add_listeners_BANG_(el){
var refs_22657 = app.components.x_file_download.x_file_download.goog$module$goog$object.get(el,app.components.x_file_download.x_file_download.k_refs);
var anchor_22658 = app.components.x_file_download.x_file_download.goog$module$goog$object.get(refs_22657,"anchor-el");
var click_h_22659 = app.components.x_file_download.x_file_download.make_click_handler(el);
anchor_22658.addEventListener("click",click_h_22659);

app.components.x_file_download.x_file_download.goog$module$goog$object.set(el,app.components.x_file_download.x_file_download.k_handlers,({"click": click_h_22659}));

return null;
});
app.components.x_file_download.x_file_download.remove_listeners_BANG_ = (function app$components$x_file_download$x_file_download$remove_listeners_BANG_(el){
var hs_22660 = app.components.x_file_download.x_file_download.goog$module$goog$object.get(el,app.components.x_file_download.x_file_download.k_handlers);
var refs_22661 = app.components.x_file_download.x_file_download.goog$module$goog$object.get(el,app.components.x_file_download.x_file_download.k_refs);
if(cljs.core.truth_((function (){var and__5140__auto__ = hs_22660;
if(cljs.core.truth_(and__5140__auto__)){
return refs_22661;
} else {
return and__5140__auto__;
}
})())){
var anchor_22662 = app.components.x_file_download.x_file_download.goog$module$goog$object.get(refs_22661,"anchor-el");
var click_h_22663 = app.components.x_file_download.x_file_download.goog$module$goog$object.get(hs_22660,"click");
if(cljs.core.truth_(click_h_22663)){
anchor_22662.removeEventListener("click",click_h_22663);
} else {
}
} else {
}

app.components.x_file_download.x_file_download.goog$module$goog$object.set(el,app.components.x_file_download.x_file_download.k_handlers,null);

return null;
});
app.components.x_file_download.x_file_download.connected_BANG_ = (function app$components$x_file_download$x_file_download$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_file_download.x_file_download.goog$module$goog$object.get(el,app.components.x_file_download.x_file_download.k_refs))){
} else {
app.components.x_file_download.x_file_download.make_shadow_BANG_(el);
}

app.components.x_file_download.x_file_download.remove_listeners_BANG_(el);

app.components.x_file_download.x_file_download.add_listeners_BANG_(el);

return app.components.x_file_download.x_file_download.render_BANG_(el);
});
app.components.x_file_download.x_file_download.disconnected_BANG_ = (function app$components$x_file_download$x_file_download$disconnected_BANG_(el){
return app.components.x_file_download.x_file_download.remove_listeners_BANG_(el);
});
app.components.x_file_download.x_file_download.attribute_changed_BANG_ = (function app$components$x_file_download$x_file_download$attribute_changed_BANG_(el,_name,_old,_new){
return app.components.x_file_download.x_file_download.render_BANG_(el);
});
app.components.x_file_download.x_file_download.define_string_prop_BANG_ = (function app$components$x_file_download$x_file_download$define_string_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var or__5142__auto__ = app.components.x_file_download.x_file_download.get_attr(this$,attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}), "set": (function (v){
var this$ = this;
if((((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined)))){
return app.components.x_file_download.x_file_download.set_attr_BANG_(this$,attr_name,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return app.components.x_file_download.x_file_download.remove_attr_BANG_(this$,attr_name);
}
})}));
});
app.components.x_file_download.x_file_download.define_bool_prop_BANG_ = (function app$components$x_file_download$x_file_download$define_bool_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return app.components.x_file_download.x_file_download.has_attr_QMARK_(this$,attr_name);
}), "set": (function (v){
var this$ = this;
return app.components.x_file_download.x_file_download.set_bool_attr_BANG_(this$,attr_name,cljs.core.boolean$(v));
})}));
});
app.components.x_file_download.x_file_download.element_class = (function app$components$x_file_download$x_file_download$element_class(){
var cls = (class extends HTMLElement {});
var proto = cls.prototype;
Object.defineProperty(cls,"observedAttributes",({"get": (function (){
return app.components.x_file_download.model.observed_attributes;
})}));

app.components.x_file_download.x_file_download.define_string_prop_BANG_(proto,"href",app.components.x_file_download.model.attr_href);

app.components.x_file_download.x_file_download.define_string_prop_BANG_(proto,"filename",app.components.x_file_download.model.attr_filename);

app.components.x_file_download.x_file_download.define_bool_prop_BANG_(proto,"disabled",app.components.x_file_download.model.attr_disabled);

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_file_download.x_file_download.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_file_download.x_file_download.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_file_download.x_file_download.attribute_changed_BANG_(this$,n,o,v);
}));

return cls;
});
app.components.x_file_download.x_file_download.init_BANG_ = (function app$components$x_file_download$x_file_download$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_file_download.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_file_download.model.tag_name,app.components.x_file_download.x_file_download.element_class());
}
});

//# sourceMappingURL=app.components.x_file_download.x_file_download.js.map
