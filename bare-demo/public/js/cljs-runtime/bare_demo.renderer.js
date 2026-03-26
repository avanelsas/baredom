goog.provide('bare_demo.renderer');
bare_demo.renderer.on_key_QMARK_ = (function bare_demo$renderer$on_key_QMARK_(k){
return clojure.string.starts_with_QMARK_(cljs.core.name(k),"on-");
});
bare_demo.renderer.event_name = (function bare_demo$renderer$event_name(k){
return cljs.core.subs.cljs$core$IFn$_invoke$arity$2(cljs.core.name(k),(3));
});
bare_demo.renderer.set_prop_BANG_ = (function bare_demo$renderer$set_prop_BANG_(el,k,v){
var attr = cljs.core.name(k);
if(bare_demo.renderer.on_key_QMARK_(k)){
return el.addEventListener(bare_demo.renderer.event_name(k),v);
} else {
if((v == null)){
return el.removeAttribute(attr);
} else {
if(v === true){
return el.setAttribute(attr,"");
} else {
if(v === false){
return el.removeAttribute(attr);
} else {
return el.setAttribute(attr,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));

}
}
}
}
});
bare_demo.renderer.create_element = (function bare_demo$renderer$create_element(p__21223){
var vec__21224 = p__21223;
var seq__21225 = cljs.core.seq(vec__21224);
var first__21226 = cljs.core.first(seq__21225);
var seq__21225__$1 = cljs.core.next(seq__21225);
var tag = first__21226;
var args = seq__21225__$1;
var has_props_QMARK_ = ((cljs.core.seq(args)) && (cljs.core.map_QMARK_(cljs.core.first(args))));
var props = ((has_props_QMARK_)?cljs.core.first(args):null);
var children = ((has_props_QMARK_)?cljs.core.rest(args):args);
var el = document.createElement(cljs.core.name(tag));
var seq__21227_21271 = cljs.core.seq(props);
var chunk__21228_21272 = null;
var count__21229_21273 = (0);
var i__21230_21274 = (0);
while(true){
if((i__21230_21274 < count__21229_21273)){
var vec__21241_21275 = chunk__21228_21272.cljs$core$IIndexed$_nth$arity$2(null,i__21230_21274);
var k_21276 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21241_21275,(0),null);
var v_21277 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21241_21275,(1),null);
bare_demo.renderer.set_prop_BANG_(el,k_21276,v_21277);


var G__21278 = seq__21227_21271;
var G__21279 = chunk__21228_21272;
var G__21280 = count__21229_21273;
var G__21281 = (i__21230_21274 + (1));
seq__21227_21271 = G__21278;
chunk__21228_21272 = G__21279;
count__21229_21273 = G__21280;
i__21230_21274 = G__21281;
continue;
} else {
var temp__5823__auto___21282 = cljs.core.seq(seq__21227_21271);
if(temp__5823__auto___21282){
var seq__21227_21283__$1 = temp__5823__auto___21282;
if(cljs.core.chunked_seq_QMARK_(seq__21227_21283__$1)){
var c__5673__auto___21284 = cljs.core.chunk_first(seq__21227_21283__$1);
var G__21285 = cljs.core.chunk_rest(seq__21227_21283__$1);
var G__21286 = c__5673__auto___21284;
var G__21287 = cljs.core.count(c__5673__auto___21284);
var G__21288 = (0);
seq__21227_21271 = G__21285;
chunk__21228_21272 = G__21286;
count__21229_21273 = G__21287;
i__21230_21274 = G__21288;
continue;
} else {
var vec__21244_21289 = cljs.core.first(seq__21227_21283__$1);
var k_21290 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21244_21289,(0),null);
var v_21291 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21244_21289,(1),null);
bare_demo.renderer.set_prop_BANG_(el,k_21290,v_21291);


var G__21292 = cljs.core.next(seq__21227_21283__$1);
var G__21293 = null;
var G__21294 = (0);
var G__21295 = (0);
seq__21227_21271 = G__21292;
chunk__21228_21272 = G__21293;
count__21229_21273 = G__21294;
i__21230_21274 = G__21295;
continue;
}
} else {
}
}
break;
}

var seq__21247_21296 = cljs.core.seq(cljs.core.mapcat.cljs$core$IFn$_invoke$arity$variadic(bare_demo.renderer.create_nodes,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([children], 0)));
var chunk__21248_21297 = null;
var count__21249_21298 = (0);
var i__21250_21299 = (0);
while(true){
if((i__21250_21299 < count__21249_21298)){
var node_21301 = chunk__21248_21297.cljs$core$IIndexed$_nth$arity$2(null,i__21250_21299);
el.appendChild(node_21301);


var G__21302 = seq__21247_21296;
var G__21303 = chunk__21248_21297;
var G__21304 = count__21249_21298;
var G__21305 = (i__21250_21299 + (1));
seq__21247_21296 = G__21302;
chunk__21248_21297 = G__21303;
count__21249_21298 = G__21304;
i__21250_21299 = G__21305;
continue;
} else {
var temp__5823__auto___21306 = cljs.core.seq(seq__21247_21296);
if(temp__5823__auto___21306){
var seq__21247_21307__$1 = temp__5823__auto___21306;
if(cljs.core.chunked_seq_QMARK_(seq__21247_21307__$1)){
var c__5673__auto___21308 = cljs.core.chunk_first(seq__21247_21307__$1);
var G__21309 = cljs.core.chunk_rest(seq__21247_21307__$1);
var G__21310 = c__5673__auto___21308;
var G__21311 = cljs.core.count(c__5673__auto___21308);
var G__21312 = (0);
seq__21247_21296 = G__21309;
chunk__21248_21297 = G__21310;
count__21249_21298 = G__21311;
i__21250_21299 = G__21312;
continue;
} else {
var node_21313 = cljs.core.first(seq__21247_21307__$1);
el.appendChild(node_21313);


var G__21314 = cljs.core.next(seq__21247_21307__$1);
var G__21315 = null;
var G__21316 = (0);
var G__21317 = (0);
seq__21247_21296 = G__21314;
chunk__21248_21297 = G__21315;
count__21249_21298 = G__21316;
i__21250_21299 = G__21317;
continue;
}
} else {
}
}
break;
}

return el;
});
bare_demo.renderer.create_nodes = (function bare_demo$renderer$create_nodes(x){
if((x == null)){
return cljs.core.PersistentVector.EMPTY;
} else {
if(x === false){
return cljs.core.PersistentVector.EMPTY;
} else {
if(typeof x === 'string'){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [document.createTextNode(x)], null);
} else {
if(typeof x === 'number'){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [document.createTextNode((""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(x)))], null);
} else {
if(cljs.core.vector_QMARK_(x)){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [bare_demo.renderer.create_element(x)], null);
} else {
if(cljs.core.seq_QMARK_(x)){
return cljs.core.mapcat.cljs$core$IFn$_invoke$arity$variadic(bare_demo.renderer.create_nodes,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([x], 0));
} else {
return cljs.core.PersistentVector.EMPTY;

}
}
}
}
}
}
});
bare_demo.renderer.render_BANG_ = (function bare_demo$renderer$render_BANG_(container,view_fn){
(container.innerHTML = "");

var seq__21263 = cljs.core.seq(bare_demo.renderer.create_nodes((view_fn.cljs$core$IFn$_invoke$arity$0 ? view_fn.cljs$core$IFn$_invoke$arity$0() : view_fn.call(null))));
var chunk__21264 = null;
var count__21265 = (0);
var i__21266 = (0);
while(true){
if((i__21266 < count__21265)){
var node = chunk__21264.cljs$core$IIndexed$_nth$arity$2(null,i__21266);
container.appendChild(node);


var G__21325 = seq__21263;
var G__21326 = chunk__21264;
var G__21327 = count__21265;
var G__21328 = (i__21266 + (1));
seq__21263 = G__21325;
chunk__21264 = G__21326;
count__21265 = G__21327;
i__21266 = G__21328;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__21263);
if(temp__5823__auto__){
var seq__21263__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__21263__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__21263__$1);
var G__21334 = cljs.core.chunk_rest(seq__21263__$1);
var G__21335 = c__5673__auto__;
var G__21336 = cljs.core.count(c__5673__auto__);
var G__21337 = (0);
seq__21263 = G__21334;
chunk__21264 = G__21335;
count__21265 = G__21336;
i__21266 = G__21337;
continue;
} else {
var node = cljs.core.first(seq__21263__$1);
container.appendChild(node);


var G__21338 = cljs.core.next(seq__21263__$1);
var G__21339 = null;
var G__21340 = (0);
var G__21341 = (0);
seq__21263 = G__21338;
chunk__21264 = G__21339;
count__21265 = G__21340;
i__21266 = G__21341;
continue;
}
} else {
return null;
}
}
break;
}
});
bare_demo.renderer.mount_BANG_ = (function bare_demo$renderer$mount_BANG_(container,view_fn,state_atom){
bare_demo.renderer.render_BANG_(container,view_fn);

return cljs.core.add_watch(state_atom,new cljs.core.Keyword("bare-demo.renderer","render","bare-demo.renderer/render",956111525),(function (_,___$1,___$2,___$3){
return bare_demo.renderer.render_BANG_(container,view_fn);
}));
});

//# sourceMappingURL=bare_demo.renderer.js.map
