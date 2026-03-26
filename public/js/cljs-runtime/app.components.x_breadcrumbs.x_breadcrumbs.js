goog.provide('app.components.x_breadcrumbs.x_breadcrumbs');
goog.scope(function(){
  app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_breadcrumbs.x_breadcrumbs.k_refs = "__xBreadcrumbsRefs";
app.components.x_breadcrumbs.x_breadcrumbs.k_model = "__xBreadcrumbsModel";
app.components.x_breadcrumbs.x_breadcrumbs.k_handlers = "__xBreadcrumbsHandlers";
app.components.x_breadcrumbs.x_breadcrumbs.style_text = (""+":host{"+"display:block;"+"color-scheme:light dark;"+"--x-breadcrumbs-color:rgba(0,0,0,0.55);"+"--x-breadcrumbs-color-current:rgba(0,0,0,0.88);"+"--x-breadcrumbs-color-hover:rgba(0,0,0,0.80);"+"--x-breadcrumbs-separator-color:rgba(0,0,0,0.35);"+"--x-breadcrumbs-font-size:0.875rem;"+"--x-breadcrumbs-gap:0.25rem;"+"--x-breadcrumbs-disabled-opacity:0.55;}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-breadcrumbs-color:rgba(255,255,255,0.50);"+"--x-breadcrumbs-color-current:rgba(255,255,255,0.90);"+"--x-breadcrumbs-color-hover:rgba(255,255,255,0.75);"+"--x-breadcrumbs-separator-color:rgba(255,255,255,0.30);}}"+":host([data-size='sm']){--x-breadcrumbs-font-size:0.75rem;}"+":host([data-size='lg']){--x-breadcrumbs-font-size:1rem;}"+":host([disabled]){opacity:var(--x-breadcrumbs-disabled-opacity);pointer-events:none;}"+"[part=root]{"+"display:block;"+"font-size:var(--x-breadcrumbs-font-size);}"+"[part=list]{"+"display:flex;"+"flex-wrap:wrap;"+"align-items:center;"+"list-style:none;"+"margin:0;"+"padding:0;"+"gap:var(--x-breadcrumbs-gap);}"+":host([data-wrap='false']) [part=list]{flex-wrap:nowrap;}"+".crumb{"+"display:inline-flex;"+"align-items:center;"+"gap:var(--x-breadcrumbs-gap);}"+".crumb-content{"+"color:var(--x-breadcrumbs-color);"+"text-decoration:none;"+"cursor:pointer;}"+".crumb-content:hover{"+"color:var(--x-breadcrumbs-color-hover);}"+".crumb[data-current] .crumb-content{"+"color:var(--x-breadcrumbs-color-current);"+"font-weight:500;}"+":host([data-variant='subtle']) .crumb-content{"+"opacity:0.75;}"+":host([data-variant='text']) .crumb-content{"+"text-decoration:none;"+"border-bottom:none;}"+".separator{"+"color:var(--x-breadcrumbs-separator-color);"+"user-select:none;"+"aria-hidden:true;"+"flex-shrink:0;}"+".ellipsis{"+"display:inline-flex;"+"align-items:center;"+"gap:var(--x-breadcrumbs-gap);}"+".ellipsis-btn{"+"color:var(--x-breadcrumbs-color);"+"background:none;"+"border:none;"+"padding:0;"+"cursor:pointer;"+"font:inherit;"+"line-height:1;}"+".ellipsis-btn:hover{"+"color:var(--x-breadcrumbs-color-hover);}"+"slot{display:none;}"+"@media (prefers-reduced-motion:reduce){"+"[part=list]{transition:none !important;}}");
app.components.x_breadcrumbs.x_breadcrumbs.init_dom_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$init_dom_BANG_(el){
var root_21985 = el.attachShadow(({"mode": "open"}));
var style_21986 = document.createElement("style");
var nav_21987 = document.createElement("nav");
var ol_21988 = document.createElement("ol");
var slot_el_21989 = document.createElement("slot");
(style_21986.textContent = app.components.x_breadcrumbs.x_breadcrumbs.style_text);

nav_21987.setAttribute("part","root");

ol_21988.setAttribute("part","list");

nav_21987.appendChild(ol_21988);

root_21985.appendChild(style_21986);

root_21985.appendChild(nav_21987);

root_21985.appendChild(slot_el_21989);

app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object.set(el,app.components.x_breadcrumbs.x_breadcrumbs.k_refs,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"nav","nav",719540477),nav_21987,new cljs.core.Keyword(null,"ol","ol",932524051),ol_21988,new cljs.core.Keyword(null,"slot-el","slot-el",1985374132),slot_el_21989], null));

return null;
});
app.components.x_breadcrumbs.x_breadcrumbs.ensure_refs_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object.get(el,app.components.x_breadcrumbs.x_breadcrumbs.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_breadcrumbs.x_breadcrumbs.init_dom_BANG_(el);

return app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object.get(el,app.components.x_breadcrumbs.x_breadcrumbs.k_refs);
}
});
app.components.x_breadcrumbs.x_breadcrumbs.read_model = (function app$components$x_breadcrumbs$x_breadcrumbs$read_model(el){
return app.components.x_breadcrumbs.model.normalize(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"variant-raw","variant-raw",1353213250),new cljs.core.Keyword(null,"max-items-raw","max-items-raw",-340487390),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),new cljs.core.Keyword(null,"wrap-raw","wrap-raw",-1468030804),new cljs.core.Keyword(null,"items-after-raw","items-after-raw",-1677187220),new cljs.core.Keyword(null,"size-raw","size-raw",-2036217423),new cljs.core.Keyword(null,"preserve-aria-current-present?","preserve-aria-current-present?",729931506),new cljs.core.Keyword(null,"items-before-raw","items-before-raw",1580997624),new cljs.core.Keyword(null,"aria-label-raw","aria-label-raw",-412828103),new cljs.core.Keyword(null,"separator-raw","separator-raw",1358337914),new cljs.core.Keyword(null,"aria-describedby-raw","aria-describedby-raw",-1672877860)],[el.getAttribute(app.components.x_breadcrumbs.model.attr_variant),el.getAttribute(app.components.x_breadcrumbs.model.attr_max_items),el.hasAttribute(app.components.x_breadcrumbs.model.attr_disabled),el.getAttribute(app.components.x_breadcrumbs.model.attr_wrap),el.getAttribute(app.components.x_breadcrumbs.model.attr_items_after),el.getAttribute(app.components.x_breadcrumbs.model.attr_size),el.hasAttribute(app.components.x_breadcrumbs.model.attr_preserve_aria_current),el.getAttribute(app.components.x_breadcrumbs.model.attr_items_before),el.getAttribute(app.components.x_breadcrumbs.model.attr_aria_label),el.getAttribute(app.components.x_breadcrumbs.model.attr_separator),el.getAttribute(app.components.x_breadcrumbs.model.attr_aria_describedby)]));
});
app.components.x_breadcrumbs.x_breadcrumbs.make_separator_el = (function app$components$x_breadcrumbs$x_breadcrumbs$make_separator_el(_el,sep_text){
var span = document.createElement("span");
span.setAttribute("class","separator");

span.setAttribute("aria-hidden","true");

(span.textContent = sep_text);

return span;
});
app.components.x_breadcrumbs.x_breadcrumbs.make_ellipsis_el = (function app$components$x_breadcrumbs$x_breadcrumbs$make_ellipsis_el(_el,sep_text){
var li = document.createElement("li");
var sep = document.createElement("span");
var btn = document.createElement("button");
li.setAttribute("class","ellipsis");

sep.setAttribute("class","separator");

sep.setAttribute("aria-hidden","true");

(sep.textContent = sep_text);

btn.setAttribute("class","ellipsis-btn");

btn.setAttribute("aria-label","Show hidden breadcrumbs");

(btn.textContent = "\u2026");

li.appendChild(sep);

li.appendChild(btn);

return li;
});
app.components.x_breadcrumbs.x_breadcrumbs.make_crumb_el = (function app$components$x_breadcrumbs$x_breadcrumbs$make_crumb_el(_el,source_child,current_QMARK_){
var li = document.createElement("li");
var wrapper = document.createElement("span");
var clone = source_child.cloneNode(true);
li.setAttribute("class","crumb");

wrapper.setAttribute("class","crumb-content");

wrapper.appendChild(clone);

li.appendChild(wrapper);

if(cljs.core.truth_(current_QMARK_)){
li.setAttribute("data-current","");

wrapper.setAttribute("aria-current","page");
} else {
}

return li;
});
app.components.x_breadcrumbs.x_breadcrumbs.rebuild_list_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$rebuild_list_BANG_(el,p__21936){
var map__21937 = p__21936;
var map__21937__$1 = cljs.core.__destructure_map(map__21937);
var _m = map__21937__$1;
var separator = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21937__$1,new cljs.core.Keyword(null,"separator","separator",-1628749125));
var max_items = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21937__$1,new cljs.core.Keyword(null,"max-items","max-items",-1969244147));
var items_before = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21937__$1,new cljs.core.Keyword(null,"items-before","items-before",-1151895719));
var items_after = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21937__$1,new cljs.core.Keyword(null,"items-after","items-after",358736705));
var preserve_aria_current = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21937__$1,new cljs.core.Keyword(null,"preserve-aria-current","preserve-aria-current",682773185));
var map__21938_21990 = app.components.x_breadcrumbs.x_breadcrumbs.ensure_refs_BANG_(el);
var map__21938_21991__$1 = cljs.core.__destructure_map(map__21938_21990);
var slot_el_21992 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21938_21991__$1,new cljs.core.Keyword(null,"slot-el","slot-el",1985374132));
var ol_21993 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21938_21991__$1,new cljs.core.Keyword(null,"ol","ol",932524051));
var slot_el_21994__$1 = slot_el_21992;
var ol_21995__$1 = ol_21993;
var children_21996 = cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(slot_el_21994__$1.assignedElements());
var total_21997 = cljs.core.count(children_21996);
var plan_21998 = app.components.x_breadcrumbs.model.build_plan(total_21997,max_items,items_before,items_after);
var visible_21999 = new cljs.core.Keyword(null,"visible","visible",-1024216805).cljs$core$IFn$_invoke$arity$1(plan_21998);
var ellipsis_at_22000 = new cljs.core.Keyword(null,"ellipsis-at","ellipsis-at",-628372349).cljs$core$IFn$_invoke$arity$1(plan_21998);
(ol_21995__$1.innerHTML = "");

var seq__21939_22001 = cljs.core.seq(cljs.core.map_indexed.cljs$core$IFn$_invoke$arity$2(cljs.core.vector,visible_21999));
var chunk__21940_22002 = null;
var count__21941_22003 = (0);
var i__21942_22004 = (0);
while(true){
if((i__21942_22004 < count__21941_22003)){
var vec__21954_22005 = chunk__21940_22002.cljs$core$IIndexed$_nth$arity$2(null,i__21942_22004);
var pos_22006 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21954_22005,(0),null);
var idx_22007 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21954_22005,(1),null);
var child_22008 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(children_21996,idx_22007,null);
var last_QMARK__22009 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(pos_22006,(cljs.core.count(visible_21999) - (1)));
var current_QMARK__22010 = ((last_QMARK__22009) && (cljs.core.not(preserve_aria_current)));
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(pos_22006,ellipsis_at_22000)){
ol_21995__$1.appendChild(app.components.x_breadcrumbs.x_breadcrumbs.make_ellipsis_el(el,separator));
} else {
}

if((((pos_22006 > (0))) && ((!(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(pos_22006,ellipsis_at_22000)))))){
ol_21995__$1.appendChild(app.components.x_breadcrumbs.x_breadcrumbs.make_separator_el(el,separator));
} else {
}

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(pos_22006,ellipsis_at_22000)){
ol_21995__$1.appendChild(app.components.x_breadcrumbs.x_breadcrumbs.make_separator_el(el,separator));
} else {
}

if(cljs.core.truth_(child_22008)){
ol_21995__$1.appendChild(app.components.x_breadcrumbs.x_breadcrumbs.make_crumb_el(el,child_22008,current_QMARK__22010));
} else {
}


var G__22011 = seq__21939_22001;
var G__22012 = chunk__21940_22002;
var G__22013 = count__21941_22003;
var G__22014 = (i__21942_22004 + (1));
seq__21939_22001 = G__22011;
chunk__21940_22002 = G__22012;
count__21941_22003 = G__22013;
i__21942_22004 = G__22014;
continue;
} else {
var temp__5823__auto___22015 = cljs.core.seq(seq__21939_22001);
if(temp__5823__auto___22015){
var seq__21939_22016__$1 = temp__5823__auto___22015;
if(cljs.core.chunked_seq_QMARK_(seq__21939_22016__$1)){
var c__5673__auto___22017 = cljs.core.chunk_first(seq__21939_22016__$1);
var G__22018 = cljs.core.chunk_rest(seq__21939_22016__$1);
var G__22019 = c__5673__auto___22017;
var G__22020 = cljs.core.count(c__5673__auto___22017);
var G__22021 = (0);
seq__21939_22001 = G__22018;
chunk__21940_22002 = G__22019;
count__21941_22003 = G__22020;
i__21942_22004 = G__22021;
continue;
} else {
var vec__21957_22022 = cljs.core.first(seq__21939_22016__$1);
var pos_22023 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21957_22022,(0),null);
var idx_22024 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21957_22022,(1),null);
var child_22025 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(children_21996,idx_22024,null);
var last_QMARK__22026 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(pos_22023,(cljs.core.count(visible_21999) - (1)));
var current_QMARK__22027 = ((last_QMARK__22026) && (cljs.core.not(preserve_aria_current)));
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(pos_22023,ellipsis_at_22000)){
ol_21995__$1.appendChild(app.components.x_breadcrumbs.x_breadcrumbs.make_ellipsis_el(el,separator));
} else {
}

if((((pos_22023 > (0))) && ((!(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(pos_22023,ellipsis_at_22000)))))){
ol_21995__$1.appendChild(app.components.x_breadcrumbs.x_breadcrumbs.make_separator_el(el,separator));
} else {
}

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(pos_22023,ellipsis_at_22000)){
ol_21995__$1.appendChild(app.components.x_breadcrumbs.x_breadcrumbs.make_separator_el(el,separator));
} else {
}

if(cljs.core.truth_(child_22025)){
ol_21995__$1.appendChild(app.components.x_breadcrumbs.x_breadcrumbs.make_crumb_el(el,child_22025,current_QMARK__22027));
} else {
}


var G__22028 = cljs.core.next(seq__21939_22016__$1);
var G__22029 = null;
var G__22030 = (0);
var G__22031 = (0);
seq__21939_22001 = G__22028;
chunk__21940_22002 = G__22029;
count__21941_22003 = G__22030;
i__21942_22004 = G__22031;
continue;
}
} else {
}
}
break;
}

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(ellipsis_at_22000,cljs.core.count(visible_21999))){
ol_21995__$1.appendChild(app.components.x_breadcrumbs.x_breadcrumbs.make_ellipsis_el(el,separator));
} else {
}

return null;
});
app.components.x_breadcrumbs.x_breadcrumbs.apply_model_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$apply_model_BANG_(el,p__21960){
var map__21961 = p__21960;
var map__21961__$1 = cljs.core.__destructure_map(map__21961);
var m = map__21961__$1;
var size = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21961__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var variant = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21961__$1,new cljs.core.Keyword(null,"variant","variant",-424354234));
var wrap = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21961__$1,new cljs.core.Keyword(null,"wrap","wrap",851669987));
var disabled = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21961__$1,new cljs.core.Keyword(null,"disabled","disabled",-1529784218));
var aria_label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21961__$1,new cljs.core.Keyword(null,"aria-label","aria-label",455891514));
var aria_describedby = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21961__$1,new cljs.core.Keyword(null,"aria-describedby","aria-describedby",1826540471));
var map__21962_22032 = app.components.x_breadcrumbs.x_breadcrumbs.ensure_refs_BANG_(el);
var map__21962_22033__$1 = cljs.core.__destructure_map(map__21962_22032);
var nav_22034 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21962_22033__$1,new cljs.core.Keyword(null,"nav","nav",719540477));
var nav_22035__$1 = nav_22034;
el.setAttribute("data-size",size);

el.setAttribute("data-variant",variant);

el.setAttribute("data-wrap",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.boolean$(wrap))));

if(cljs.core.truth_(aria_label)){
nav_22035__$1.setAttribute("aria-label",aria_label);
} else {
nav_22035__$1.setAttribute("aria-label","Breadcrumb");
}

if(cljs.core.truth_(aria_describedby)){
nav_22035__$1.setAttribute("aria-describedby",aria_describedby);
} else {
nav_22035__$1.removeAttribute("aria-describedby");
}

app.components.x_breadcrumbs.x_breadcrumbs.rebuild_list_BANG_(el,m);

app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object.set(el,app.components.x_breadcrumbs.x_breadcrumbs.k_model,m);

return null;
});
app.components.x_breadcrumbs.x_breadcrumbs.update_from_attrs_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$update_from_attrs_BANG_(el){
var new_m_22036 = app.components.x_breadcrumbs.x_breadcrumbs.read_model(el);
app.components.x_breadcrumbs.x_breadcrumbs.apply_model_BANG_(el,new_m_22036);

return null;
});
app.components.x_breadcrumbs.x_breadcrumbs.add_listeners_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$add_listeners_BANG_(el){
var map__21971_22037 = app.components.x_breadcrumbs.x_breadcrumbs.ensure_refs_BANG_(el);
var map__21971_22038__$1 = cljs.core.__destructure_map(map__21971_22037);
var slot_el_22039 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21971_22038__$1,new cljs.core.Keyword(null,"slot-el","slot-el",1985374132));
var slot_el_22040__$1 = slot_el_22039;
var on_slot_22041 = (function (_){
return app.components.x_breadcrumbs.x_breadcrumbs.update_from_attrs_BANG_(el);
});
if(cljs.core.truth_(slot_el_22040__$1)){
slot_el_22040__$1.addEventListener("slotchange",on_slot_22041);
} else {
}

app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object.set(el,app.components.x_breadcrumbs.x_breadcrumbs.k_handlers,({"slot": on_slot_22041}));

return null;
});
app.components.x_breadcrumbs.x_breadcrumbs.remove_listeners_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$remove_listeners_BANG_(el){
var temp__5823__auto___22042 = app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object.get(el,app.components.x_breadcrumbs.x_breadcrumbs.k_handlers);
if(cljs.core.truth_(temp__5823__auto___22042)){
var hs_22043 = temp__5823__auto___22042;
var temp__5823__auto___22044__$1 = app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object.get(el,app.components.x_breadcrumbs.x_breadcrumbs.k_refs);
if(cljs.core.truth_(temp__5823__auto___22044__$1)){
var refs_22045 = temp__5823__auto___22044__$1;
var slot_el_22046 = new cljs.core.Keyword(null,"slot-el","slot-el",1985374132).cljs$core$IFn$_invoke$arity$1(refs_22045);
var on_slot_22047 = app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object.get(hs_22043,"slot");
if(cljs.core.truth_((function (){var and__5140__auto__ = slot_el_22046;
if(cljs.core.truth_(and__5140__auto__)){
return on_slot_22047;
} else {
return and__5140__auto__;
}
})())){
slot_el_22046.removeEventListener("slotchange",on_slot_22047);
} else {
}
} else {
}
} else {
}

app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object.set(el,app.components.x_breadcrumbs.x_breadcrumbs.k_handlers,null);

return null;
});
app.components.x_breadcrumbs.x_breadcrumbs.def_string_prop_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$def_string_prop_BANG_(proto,attr){
return Object.defineProperty(proto,attr,({"get": (function (){
var this$ = this;
return this$.getAttribute(attr);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(attr,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(attr);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_breadcrumbs.x_breadcrumbs.def_string_prop_default_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$def_string_prop_default_BANG_(proto,attr,default$){
return Object.defineProperty(proto,attr,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(attr);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return default$;
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(attr,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(attr);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_breadcrumbs.x_breadcrumbs.def_bool_prop_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$def_bool_prop_BANG_(proto,attr){
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
app.components.x_breadcrumbs.x_breadcrumbs.def_int_prop_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$def_int_prop_BANG_(proto,attr,default$){
return Object.defineProperty(proto,attr,({"get": (function (){
var this$ = this;
return app.components.x_breadcrumbs.model.parse_pos_int(this$.getAttribute(attr),default$);
}), "set": (function (v){
var this$ = this;
if((!((v == null)))){
return this$.setAttribute(attr,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(attr);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_breadcrumbs.x_breadcrumbs.install_property_accessors_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$install_property_accessors_BANG_(proto){
app.components.x_breadcrumbs.x_breadcrumbs.def_string_prop_default_BANG_(proto,app.components.x_breadcrumbs.model.attr_separator,app.components.x_breadcrumbs.model.default_separator);

app.components.x_breadcrumbs.x_breadcrumbs.def_string_prop_default_BANG_(proto,app.components.x_breadcrumbs.model.attr_size,app.components.x_breadcrumbs.model.default_size);

app.components.x_breadcrumbs.x_breadcrumbs.def_string_prop_default_BANG_(proto,app.components.x_breadcrumbs.model.attr_variant,app.components.x_breadcrumbs.model.default_variant);

app.components.x_breadcrumbs.x_breadcrumbs.def_bool_prop_BANG_(proto,app.components.x_breadcrumbs.model.attr_wrap);

app.components.x_breadcrumbs.x_breadcrumbs.def_bool_prop_BANG_(proto,app.components.x_breadcrumbs.model.attr_disabled);

app.components.x_breadcrumbs.x_breadcrumbs.def_bool_prop_BANG_(proto,app.components.x_breadcrumbs.model.attr_preserve_aria_current);

app.components.x_breadcrumbs.x_breadcrumbs.def_int_prop_BANG_(proto,app.components.x_breadcrumbs.model.attr_max_items,null);

app.components.x_breadcrumbs.x_breadcrumbs.def_int_prop_BANG_(proto,app.components.x_breadcrumbs.model.attr_items_before,app.components.x_breadcrumbs.model.default_items_before);

app.components.x_breadcrumbs.x_breadcrumbs.def_int_prop_BANG_(proto,app.components.x_breadcrumbs.model.attr_items_after,app.components.x_breadcrumbs.model.default_items_after);

app.components.x_breadcrumbs.x_breadcrumbs.def_string_prop_BANG_(proto,app.components.x_breadcrumbs.model.attr_aria_label);

return app.components.x_breadcrumbs.x_breadcrumbs.def_string_prop_BANG_(proto,app.components.x_breadcrumbs.model.attr_aria_describedby);
});
app.components.x_breadcrumbs.x_breadcrumbs.element_class = (function app$components$x_breadcrumbs$x_breadcrumbs$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_breadcrumbs.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_breadcrumbs.x_breadcrumbs.ensure_refs_BANG_(this$);

app.components.x_breadcrumbs.x_breadcrumbs.remove_listeners_BANG_(this$);

app.components.x_breadcrumbs.x_breadcrumbs.add_listeners_BANG_(this$);

app.components.x_breadcrumbs.x_breadcrumbs.update_from_attrs_BANG_(this$);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
app.components.x_breadcrumbs.x_breadcrumbs.remove_listeners_BANG_(this$);

return null;
}));

(klass.prototype.attributeChangedCallback = (function (attr_name,old_val,new_val){
var this$ = this;
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
app.components.x_breadcrumbs.x_breadcrumbs.update_from_attrs_BANG_(this$);
} else {
}

return null;
}));

app.components.x_breadcrumbs.x_breadcrumbs.install_property_accessors_BANG_(klass.prototype);

return klass;
});
app.components.x_breadcrumbs.x_breadcrumbs.register_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_breadcrumbs.model.tag_name))){
} else {
customElements.define(app.components.x_breadcrumbs.model.tag_name,app.components.x_breadcrumbs.x_breadcrumbs.element_class());
}

return null;
});
app.components.x_breadcrumbs.x_breadcrumbs.init_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$init_BANG_(){
return app.components.x_breadcrumbs.x_breadcrumbs.register_BANG_();
});

//# sourceMappingURL=app.components.x_breadcrumbs.x_breadcrumbs.js.map
