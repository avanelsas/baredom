goog.provide('app.components.x_pagination.x_pagination');
goog.scope(function(){
  app.components.x_pagination.x_pagination.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_pagination.x_pagination.k_refs = "__xPaginationRefs";
app.components.x_pagination.x_pagination.k_handlers = "__xPaginationHandlers";
app.components.x_pagination.x_pagination.style_text = (""+":host{"+"display:block;"+"color-scheme:light dark;"+"--x-pagination-gap:0.25rem;"+"--x-pagination-button-size:2rem;"+"--x-pagination-button-radius:0.375rem;"+"--x-pagination-button-bg:transparent;"+"--x-pagination-button-color:rgba(0,0,0,0.75);"+"--x-pagination-button-border:1px solid rgba(0,0,0,0.15);"+"--x-pagination-button-hover-bg:rgba(0,0,0,0.06);"+"--x-pagination-button-hover-color:rgba(0,0,0,0.9);"+"--x-pagination-current-bg:rgba(0,0,0,0.88);"+"--x-pagination-current-color:#fff;"+"--x-pagination-current-border:1px solid transparent;"+"--x-pagination-disabled-opacity:0.4;"+"--x-pagination-font-size:0.875rem;"+"--x-pagination-ellipsis-color:rgba(0,0,0,0.45);}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-pagination-button-color:rgba(255,255,255,0.75);"+"--x-pagination-button-border:1px solid rgba(255,255,255,0.15);"+"--x-pagination-button-hover-bg:rgba(255,255,255,0.08);"+"--x-pagination-button-hover-color:rgba(255,255,255,0.95);"+"--x-pagination-current-bg:rgba(255,255,255,0.90);"+"--x-pagination-current-color:#000;"+"--x-pagination-ellipsis-color:rgba(255,255,255,0.40);}}"+":host([data-size='sm']){"+"--x-pagination-button-size:1.75rem;"+"--x-pagination-font-size:0.75rem;}"+":host([data-size='lg']){"+"--x-pagination-button-size:2.5rem;"+"--x-pagination-font-size:1rem;}"+"[part~='nav']{"+"display:block;}"+"[part~='list']{"+"display:flex;"+"flex-wrap:wrap;"+"align-items:center;"+"list-style:none;"+"margin:0;"+"padding:0;"+"gap:var(--x-pagination-gap);}"+"[part~='button']{"+"display:inline-flex;"+"align-items:center;"+"justify-content:center;"+"min-width:var(--x-pagination-button-size);"+"height:var(--x-pagination-button-size);"+"padding:0 0.5rem;"+"border:var(--x-pagination-button-border);"+"border-radius:var(--x-pagination-button-radius);"+"background:var(--x-pagination-button-bg);"+"color:var(--x-pagination-button-color);"+"font:inherit;"+"font-size:var(--x-pagination-font-size);"+"cursor:pointer;"+"user-select:none;"+"box-sizing:border-box;"+"transition:background 0.15s,color 0.15s;}"+"[part~='button']:hover:not(:disabled){"+"background:var(--x-pagination-button-hover-bg);"+"color:var(--x-pagination-button-hover-color);}"+"[part~='button'][data-current]{"+"background:var(--x-pagination-current-bg);"+"color:var(--x-pagination-current-color);"+"border:var(--x-pagination-current-border);"+"font-weight:600;"+"cursor:default;}"+"[part~='button']:disabled{"+"opacity:var(--x-pagination-disabled-opacity);"+"cursor:not-allowed;}"+"[part~='ellipsis']{"+"display:inline-flex;"+"align-items:center;"+"justify-content:center;"+"min-width:var(--x-pagination-button-size);"+"height:var(--x-pagination-button-size);"+"color:var(--x-pagination-ellipsis-color);"+"font-size:var(--x-pagination-font-size);"+"user-select:none;}"+"@media (prefers-reduced-motion:reduce){"+"[part~='button']{transition:none !important;}}");
app.components.x_pagination.x_pagination.make_prev_li_BANG_ = (function app$components$x_pagination$x_pagination$make_prev_li_BANG_(){
var li = document.createElement("li");
var btn = document.createElement("button");
var sl = document.createElement("slot");
li.setAttribute("part","item item-prev");

btn.setAttribute("part","button button-prev");

btn.setAttribute("aria-label","Previous page");

sl.setAttribute("name",app.components.x_pagination.model.slot_prev);

(sl.textContent = "Prev");

btn.appendChild(sl);

li.appendChild(btn);

return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"li","li",723558921),li,new cljs.core.Keyword(null,"btn","btn",1978294651),btn], null);
});
app.components.x_pagination.x_pagination.make_next_li_BANG_ = (function app$components$x_pagination$x_pagination$make_next_li_BANG_(){
var li = document.createElement("li");
var btn = document.createElement("button");
var sl = document.createElement("slot");
li.setAttribute("part","item item-next");

btn.setAttribute("part","button button-next");

btn.setAttribute("aria-label","Next page");

sl.setAttribute("name",app.components.x_pagination.model.slot_next);

(sl.textContent = "Next");

btn.appendChild(sl);

li.appendChild(btn);

return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"li","li",723558921),li,new cljs.core.Keyword(null,"btn","btn",1978294651),btn], null);
});
app.components.x_pagination.x_pagination.init_dom_BANG_ = (function app$components$x_pagination$x_pagination$init_dom_BANG_(el){
var root_22928 = el.attachShadow(({"mode": "open"}));
var style_22929 = document.createElement("style");
var nav_22930 = document.createElement("nav");
var ol_22931 = document.createElement("ol");
var prev_22932 = app.components.x_pagination.x_pagination.make_prev_li_BANG_();
var nxt_22933 = app.components.x_pagination.x_pagination.make_next_li_BANG_();
(style_22929.textContent = app.components.x_pagination.x_pagination.style_text);

nav_22930.setAttribute("part","nav");

ol_22931.setAttribute("part","list");

ol_22931.appendChild(new cljs.core.Keyword(null,"li","li",723558921).cljs$core$IFn$_invoke$arity$1(prev_22932));

ol_22931.appendChild(new cljs.core.Keyword(null,"li","li",723558921).cljs$core$IFn$_invoke$arity$1(nxt_22933));

nav_22930.appendChild(ol_22931);

root_22928.appendChild(style_22929);

root_22928.appendChild(nav_22930);

app.components.x_pagination.x_pagination.goog$module$goog$object.set(el,app.components.x_pagination.x_pagination.k_refs,new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"nav","nav",719540477),nav_22930,new cljs.core.Keyword(null,"ol","ol",932524051),ol_22931,new cljs.core.Keyword(null,"prev-li","prev-li",940258334),new cljs.core.Keyword(null,"li","li",723558921).cljs$core$IFn$_invoke$arity$1(prev_22932),new cljs.core.Keyword(null,"prev-btn","prev-btn",-352689882),new cljs.core.Keyword(null,"btn","btn",1978294651).cljs$core$IFn$_invoke$arity$1(prev_22932),new cljs.core.Keyword(null,"next-li","next-li",648524823),new cljs.core.Keyword(null,"li","li",723558921).cljs$core$IFn$_invoke$arity$1(nxt_22933),new cljs.core.Keyword(null,"next-btn","next-btn",-286265938),new cljs.core.Keyword(null,"btn","btn",1978294651).cljs$core$IFn$_invoke$arity$1(nxt_22933)], null));

return null;
});
app.components.x_pagination.x_pagination.ensure_refs_BANG_ = (function app$components$x_pagination$x_pagination$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_pagination.x_pagination.goog$module$goog$object.get(el,app.components.x_pagination.x_pagination.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_pagination.x_pagination.init_dom_BANG_(el);

return app.components.x_pagination.x_pagination.goog$module$goog$object.get(el,app.components.x_pagination.x_pagination.k_refs);
}
});
app.components.x_pagination.x_pagination.make_page_button_BANG_ = (function app$components$x_pagination$x_pagination$make_page_button_BANG_(n,current_QMARK_,disabled_QMARK_){
var li = document.createElement("li");
var btn = document.createElement("button");
li.setAttribute("part","item item-page");

li.setAttribute("data-page",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(n)));

btn.setAttribute("part","button button-page");

btn.setAttribute("aria-label",(""+"Page "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(n)));

btn.setAttribute("data-page",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(n)));

if(cljs.core.truth_(current_QMARK_)){
btn.setAttribute("aria-current","page");

btn.setAttribute("data-current","");
} else {
}

if(cljs.core.truth_(disabled_QMARK_)){
btn.setAttribute("disabled","");
} else {
}

(btn.textContent = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(n)));

li.appendChild(btn);

return li;
});
app.components.x_pagination.x_pagination.make_ellipsis_li_BANG_ = (function app$components$x_pagination$x_pagination$make_ellipsis_li_BANG_(){
var li = document.createElement("li");
var span = document.createElement("span");
li.setAttribute("part","item item-ellipsis");

span.setAttribute("part","ellipsis");

span.setAttribute("aria-hidden","true");

(span.textContent = "\u2026");

li.appendChild(span);

return li;
});
app.components.x_pagination.x_pagination.read_model = (function app$components$x_pagination$x_pagination$read_model(el){
return app.components.x_pagination.model.normalize(new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"page-raw","page-raw",-1198808870),el.getAttribute(app.components.x_pagination.model.attr_page),new cljs.core.Keyword(null,"total-pages-raw","total-pages-raw",430880208),el.getAttribute(app.components.x_pagination.model.attr_total_pages),new cljs.core.Keyword(null,"sibling-count-raw","sibling-count-raw",-620117200),el.getAttribute(app.components.x_pagination.model.attr_sibling_count),new cljs.core.Keyword(null,"boundary-count-raw","boundary-count-raw",410083250),el.getAttribute(app.components.x_pagination.model.attr_boundary_count),new cljs.core.Keyword(null,"size-raw","size-raw",-2036217423),el.getAttribute(app.components.x_pagination.model.attr_size),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),el.hasAttribute(app.components.x_pagination.model.attr_disabled),new cljs.core.Keyword(null,"label-raw","label-raw",-83844350),el.getAttribute(app.components.x_pagination.model.attr_label)], null));
});
app.components.x_pagination.x_pagination.render_BANG_ = (function app$components$x_pagination$x_pagination$render_BANG_(el){
var refs_22934 = app.components.x_pagination.x_pagination.ensure_refs_BANG_(el);
var ol_22935 = new cljs.core.Keyword(null,"ol","ol",932524051).cljs$core$IFn$_invoke$arity$1(refs_22934);
var nav_22936 = new cljs.core.Keyword(null,"nav","nav",719540477).cljs$core$IFn$_invoke$arity$1(refs_22934);
var prev_btn_22937 = new cljs.core.Keyword(null,"prev-btn","prev-btn",-352689882).cljs$core$IFn$_invoke$arity$1(refs_22934);
var next_btn_22938 = new cljs.core.Keyword(null,"next-btn","next-btn",-286265938).cljs$core$IFn$_invoke$arity$1(refs_22934);
var prev_li_22939 = new cljs.core.Keyword(null,"prev-li","prev-li",940258334).cljs$core$IFn$_invoke$arity$1(refs_22934);
var next_li_22940 = new cljs.core.Keyword(null,"next-li","next-li",648524823).cljs$core$IFn$_invoke$arity$1(refs_22934);
var m_22941 = app.components.x_pagination.x_pagination.read_model(el);
var map__22917_22942 = m_22941;
var map__22917_22943__$1 = cljs.core.__destructure_map(map__22917_22942);
var page_22944 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22917_22943__$1,new cljs.core.Keyword(null,"page","page",849072397));
var total_pages_22945 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22917_22943__$1,new cljs.core.Keyword(null,"total-pages","total-pages",685894112));
var sibling_count_22946 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22917_22943__$1,new cljs.core.Keyword(null,"sibling-count","sibling-count",1756034573));
var boundary_count_22947 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22917_22943__$1,new cljs.core.Keyword(null,"boundary-count","boundary-count",1637925987));
var size_22948 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22917_22943__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var label_22949 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22917_22943__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var items_22950 = app.components.x_pagination.model.build_page_items(page_22944,total_pages_22945,sibling_count_22946,boundary_count_22947);
nav_22936.setAttribute("aria-label",label_22949);

el.setAttribute("data-size",size_22948);

if(cljs.core.truth_(app.components.x_pagination.model.prev_disabled_QMARK_(m_22941))){
prev_btn_22937.setAttribute("disabled","");
} else {
prev_btn_22937.removeAttribute("disabled");
}

if(cljs.core.truth_(app.components.x_pagination.model.next_disabled_QMARK_(m_22941))){
next_btn_22938.setAttribute("disabled","");
} else {
next_btn_22938.removeAttribute("disabled");
}

(ol_22935.innerHTML = "");

ol_22935.appendChild(prev_li_22939);

var seq__22918_22951 = cljs.core.seq(items_22950);
var chunk__22919_22952 = null;
var count__22920_22953 = (0);
var i__22921_22954 = (0);
while(true){
if((i__22921_22954 < count__22920_22953)){
var item_22955 = chunk__22919_22952.cljs$core$IIndexed$_nth$arity$2(null,i__22921_22954);
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"ellipsis","ellipsis",998505738),new cljs.core.Keyword(null,"type","type",1174270348).cljs$core$IFn$_invoke$arity$1(item_22955))){
ol_22935.appendChild(app.components.x_pagination.x_pagination.make_ellipsis_li_BANG_());
} else {
ol_22935.appendChild(app.components.x_pagination.x_pagination.make_page_button_BANG_(new cljs.core.Keyword(null,"n","n",562130025).cljs$core$IFn$_invoke$arity$1(item_22955),cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"n","n",562130025).cljs$core$IFn$_invoke$arity$1(item_22955),page_22944),new cljs.core.Keyword(null,"disabled","disabled",-1529784218).cljs$core$IFn$_invoke$arity$1(m_22941)));
}


var G__22956 = seq__22918_22951;
var G__22957 = chunk__22919_22952;
var G__22958 = count__22920_22953;
var G__22959 = (i__22921_22954 + (1));
seq__22918_22951 = G__22956;
chunk__22919_22952 = G__22957;
count__22920_22953 = G__22958;
i__22921_22954 = G__22959;
continue;
} else {
var temp__5823__auto___22960 = cljs.core.seq(seq__22918_22951);
if(temp__5823__auto___22960){
var seq__22918_22961__$1 = temp__5823__auto___22960;
if(cljs.core.chunked_seq_QMARK_(seq__22918_22961__$1)){
var c__5673__auto___22962 = cljs.core.chunk_first(seq__22918_22961__$1);
var G__22963 = cljs.core.chunk_rest(seq__22918_22961__$1);
var G__22964 = c__5673__auto___22962;
var G__22965 = cljs.core.count(c__5673__auto___22962);
var G__22966 = (0);
seq__22918_22951 = G__22963;
chunk__22919_22952 = G__22964;
count__22920_22953 = G__22965;
i__22921_22954 = G__22966;
continue;
} else {
var item_22967 = cljs.core.first(seq__22918_22961__$1);
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"ellipsis","ellipsis",998505738),new cljs.core.Keyword(null,"type","type",1174270348).cljs$core$IFn$_invoke$arity$1(item_22967))){
ol_22935.appendChild(app.components.x_pagination.x_pagination.make_ellipsis_li_BANG_());
} else {
ol_22935.appendChild(app.components.x_pagination.x_pagination.make_page_button_BANG_(new cljs.core.Keyword(null,"n","n",562130025).cljs$core$IFn$_invoke$arity$1(item_22967),cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"n","n",562130025).cljs$core$IFn$_invoke$arity$1(item_22967),page_22944),new cljs.core.Keyword(null,"disabled","disabled",-1529784218).cljs$core$IFn$_invoke$arity$1(m_22941)));
}


var G__22968 = cljs.core.next(seq__22918_22961__$1);
var G__22969 = null;
var G__22970 = (0);
var G__22971 = (0);
seq__22918_22951 = G__22968;
chunk__22919_22952 = G__22969;
count__22920_22953 = G__22970;
i__22921_22954 = G__22971;
continue;
}
} else {
}
}
break;
}

ol_22935.appendChild(next_li_22940);

return null;
});
app.components.x_pagination.x_pagination.dispatch_page_change_BANG_ = (function app$components$x_pagination$x_pagination$dispatch_page_change_BANG_(el,page){
return el.dispatchEvent((new CustomEvent(app.components.x_pagination.model.event_page_change,({"detail": ({"page": page}), "bubbles": true, "composed": true, "cancelable": false}))));
});
app.components.x_pagination.x_pagination.find_button_in_path = (function app$components$x_pagination$x_pagination$find_button_in_path(ev){
var path = ev.composedPath();
var len = path.length;
var i = (0);
while(true){
if((i < len)){
var node = (path[i]);
var tag = (cljs.core.truth_(node)?node.tagName:null);
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2("BUTTON",tag)){
return node;
} else {
var G__22972 = (i + (1));
i = G__22972;
continue;
}
} else {
return null;
}
break;
}
});
app.components.x_pagination.x_pagination.on_click_BANG_ = (function app$components$x_pagination$x_pagination$on_click_BANG_(el,ev){
var btn = app.components.x_pagination.x_pagination.find_button_in_path(ev);
if(cljs.core.truth_((function (){var and__5140__auto__ = btn;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(btn.hasAttribute("disabled"));
} else {
return and__5140__auto__;
}
})())){
var refs = app.components.x_pagination.x_pagination.goog$module$goog$object.get(el,app.components.x_pagination.x_pagination.k_refs);
var prev_btn = new cljs.core.Keyword(null,"prev-btn","prev-btn",-352689882).cljs$core$IFn$_invoke$arity$1(refs);
var next_btn = new cljs.core.Keyword(null,"next-btn","next-btn",-286265938).cljs$core$IFn$_invoke$arity$1(refs);
var page_attr = btn.getAttribute("data-page");
if((btn === prev_btn)){
var m = app.components.x_pagination.x_pagination.read_model(el);
var cur = new cljs.core.Keyword(null,"page","page",849072397).cljs$core$IFn$_invoke$arity$1(m);
if((cur > (1))){
return app.components.x_pagination.x_pagination.dispatch_page_change_BANG_(el,(cur - (1)));
} else {
return null;
}
} else {
if((btn === next_btn)){
var m = app.components.x_pagination.x_pagination.read_model(el);
var cur = new cljs.core.Keyword(null,"page","page",849072397).cljs$core$IFn$_invoke$arity$1(m);
var tot = new cljs.core.Keyword(null,"total-pages","total-pages",685894112).cljs$core$IFn$_invoke$arity$1(m);
if((cur < tot)){
return app.components.x_pagination.x_pagination.dispatch_page_change_BANG_(el,(cur + (1)));
} else {
return null;
}
} else {
if((!((page_attr == null)))){
var n = parseInt(page_attr,(10));
if(cljs.core.truth_(isFinite(n))){
return app.components.x_pagination.x_pagination.dispatch_page_change_BANG_(el,n);
} else {
return null;
}
} else {
return null;
}
}
}
} else {
return null;
}
});
app.components.x_pagination.x_pagination.add_listeners_BANG_ = (function app$components$x_pagination$x_pagination$add_listeners_BANG_(el){
var refs_22981 = app.components.x_pagination.x_pagination.ensure_refs_BANG_(el);
var ol_22982 = new cljs.core.Keyword(null,"ol","ol",932524051).cljs$core$IFn$_invoke$arity$1(refs_22981);
var handler_22983 = (function (ev){
return app.components.x_pagination.x_pagination.on_click_BANG_(el,ev);
});
ol_22982.addEventListener("click",handler_22983);

app.components.x_pagination.x_pagination.goog$module$goog$object.set(el,app.components.x_pagination.x_pagination.k_handlers,({"click": handler_22983}));

return null;
});
app.components.x_pagination.x_pagination.remove_listeners_BANG_ = (function app$components$x_pagination$x_pagination$remove_listeners_BANG_(el){
var temp__5823__auto___22984 = app.components.x_pagination.x_pagination.goog$module$goog$object.get(el,app.components.x_pagination.x_pagination.k_handlers);
if(cljs.core.truth_(temp__5823__auto___22984)){
var hs_22985 = temp__5823__auto___22984;
var temp__5823__auto___22986__$1 = app.components.x_pagination.x_pagination.goog$module$goog$object.get(el,app.components.x_pagination.x_pagination.k_refs);
if(cljs.core.truth_(temp__5823__auto___22986__$1)){
var refs_22987 = temp__5823__auto___22986__$1;
var ol_22988 = new cljs.core.Keyword(null,"ol","ol",932524051).cljs$core$IFn$_invoke$arity$1(refs_22987);
var on_click_22989 = app.components.x_pagination.x_pagination.goog$module$goog$object.get(hs_22985,"click");
if(cljs.core.truth_((function (){var and__5140__auto__ = ol_22988;
if(cljs.core.truth_(and__5140__auto__)){
return on_click_22989;
} else {
return and__5140__auto__;
}
})())){
ol_22988.removeEventListener("click",on_click_22989);
} else {
}
} else {
}
} else {
}

app.components.x_pagination.x_pagination.goog$module$goog$object.set(el,app.components.x_pagination.x_pagination.k_handlers,null);

return null;
});
app.components.x_pagination.x_pagination.def_bool_prop_BANG_ = (function app$components$x_pagination$x_pagination$def_bool_prop_BANG_(proto,attr){
return Object.defineProperty(proto,attr,({"get": (function (){
var this$ = this;
return this$.hasAttribute(attr);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(attr,"");
} else {
return this$.removeAttribute(attr);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_pagination.x_pagination.def_int_prop_BANG_ = (function app$components$x_pagination$x_pagination$def_int_prop_BANG_(proto,prop_name,attr,default$){
return Object.defineProperty(proto,prop_name,({"get": (function (){
var this$ = this;
return app.components.x_pagination.model.parse_pos_int(this$.getAttribute(attr),default$);
}), "set": (function (v){
var this$ = this;
if((!((v == null)))){
return this$.setAttribute(attr,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(attr);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_pagination.x_pagination.install_property_accessors_BANG_ = (function app$components$x_pagination$x_pagination$install_property_accessors_BANG_(proto){
app.components.x_pagination.x_pagination.def_int_prop_BANG_(proto,app.components.x_pagination.model.attr_page,app.components.x_pagination.model.attr_page,app.components.x_pagination.model.default_page);

app.components.x_pagination.x_pagination.def_int_prop_BANG_(proto,app.components.x_pagination.model.attr_total_pages,app.components.x_pagination.model.attr_total_pages,app.components.x_pagination.model.default_total_pages);

app.components.x_pagination.x_pagination.def_int_prop_BANG_(proto,app.components.x_pagination.model.attr_sibling_count,app.components.x_pagination.model.attr_sibling_count,app.components.x_pagination.model.default_sibling_count);

app.components.x_pagination.x_pagination.def_int_prop_BANG_(proto,app.components.x_pagination.model.attr_boundary_count,app.components.x_pagination.model.attr_boundary_count,app.components.x_pagination.model.default_boundary_count);

return app.components.x_pagination.x_pagination.def_bool_prop_BANG_(proto,app.components.x_pagination.model.attr_disabled);
});
app.components.x_pagination.x_pagination.element_class = (function app$components$x_pagination$x_pagination$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_pagination.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_pagination.x_pagination.ensure_refs_BANG_(this$);

app.components.x_pagination.x_pagination.remove_listeners_BANG_(this$);

app.components.x_pagination.x_pagination.add_listeners_BANG_(this$);

app.components.x_pagination.x_pagination.render_BANG_(this$);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
app.components.x_pagination.x_pagination.remove_listeners_BANG_(this$);

return null;
}));

(klass.prototype.attributeChangedCallback = (function (_attr_name,old_val,new_val){
var this$ = this;
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
app.components.x_pagination.x_pagination.render_BANG_(this$);
} else {
}

return null;
}));

app.components.x_pagination.x_pagination.install_property_accessors_BANG_(klass.prototype);

return klass;
});
app.components.x_pagination.x_pagination.register_BANG_ = (function app$components$x_pagination$x_pagination$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_pagination.model.tag_name))){
} else {
customElements.define(app.components.x_pagination.model.tag_name,app.components.x_pagination.x_pagination.element_class());
}

return null;
});
app.components.x_pagination.x_pagination.init_BANG_ = (function app$components$x_pagination$x_pagination$init_BANG_(){
return app.components.x_pagination.x_pagination.register_BANG_();
});

//# sourceMappingURL=app.components.x_pagination.x_pagination.js.map
