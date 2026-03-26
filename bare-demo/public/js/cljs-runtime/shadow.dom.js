goog.provide('shadow.dom');
shadow.dom.transition_supported_QMARK_ = true;

/**
 * @interface
 */
shadow.dom.IElement = function(){};

var shadow$dom$IElement$_to_dom$dyn_12720 = (function (this$){
var x__5498__auto__ = (((this$ == null))?null:this$);
var m__5499__auto__ = (shadow.dom._to_dom[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$1(this$) : m__5499__auto__.call(null,this$));
} else {
var m__5497__auto__ = (shadow.dom._to_dom["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$1(this$) : m__5497__auto__.call(null,this$));
} else {
throw cljs.core.missing_protocol("IElement.-to-dom",this$);
}
}
});
shadow.dom._to_dom = (function shadow$dom$_to_dom(this$){
if((((!((this$ == null)))) && ((!((this$.shadow$dom$IElement$_to_dom$arity$1 == null)))))){
return this$.shadow$dom$IElement$_to_dom$arity$1(this$);
} else {
return shadow$dom$IElement$_to_dom$dyn_12720(this$);
}
});


/**
 * @interface
 */
shadow.dom.SVGElement = function(){};

var shadow$dom$SVGElement$_to_svg$dyn_12724 = (function (this$){
var x__5498__auto__ = (((this$ == null))?null:this$);
var m__5499__auto__ = (shadow.dom._to_svg[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$1(this$) : m__5499__auto__.call(null,this$));
} else {
var m__5497__auto__ = (shadow.dom._to_svg["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$1(this$) : m__5497__auto__.call(null,this$));
} else {
throw cljs.core.missing_protocol("SVGElement.-to-svg",this$);
}
}
});
shadow.dom._to_svg = (function shadow$dom$_to_svg(this$){
if((((!((this$ == null)))) && ((!((this$.shadow$dom$SVGElement$_to_svg$arity$1 == null)))))){
return this$.shadow$dom$SVGElement$_to_svg$arity$1(this$);
} else {
return shadow$dom$SVGElement$_to_svg$dyn_12724(this$);
}
});

shadow.dom.lazy_native_coll_seq = (function shadow$dom$lazy_native_coll_seq(coll,idx){
if((idx < coll.length)){
return (new cljs.core.LazySeq(null,(function (){
return cljs.core.cons((coll[idx]),(function (){var G__11766 = coll;
var G__11767 = (idx + (1));
return (shadow.dom.lazy_native_coll_seq.cljs$core$IFn$_invoke$arity$2 ? shadow.dom.lazy_native_coll_seq.cljs$core$IFn$_invoke$arity$2(G__11766,G__11767) : shadow.dom.lazy_native_coll_seq.call(null,G__11766,G__11767));
})());
}),null,null));
} else {
return null;
}
});

/**
* @constructor
 * @implements {cljs.core.IIndexed}
 * @implements {cljs.core.ICounted}
 * @implements {cljs.core.ISeqable}
 * @implements {cljs.core.IDeref}
 * @implements {shadow.dom.IElement}
*/
shadow.dom.NativeColl = (function (coll){
this.coll = coll;
this.cljs$lang$protocol_mask$partition0$ = 8421394;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(shadow.dom.NativeColl.prototype.cljs$core$IDeref$_deref$arity$1 = (function (this$){
var self__ = this;
var this$__$1 = this;
return self__.coll;
}));

(shadow.dom.NativeColl.prototype.cljs$core$IIndexed$_nth$arity$2 = (function (this$,n){
var self__ = this;
var this$__$1 = this;
return (self__.coll[n]);
}));

(shadow.dom.NativeColl.prototype.cljs$core$IIndexed$_nth$arity$3 = (function (this$,n,not_found){
var self__ = this;
var this$__$1 = this;
var or__5142__auto__ = (self__.coll[n]);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return not_found;
}
}));

(shadow.dom.NativeColl.prototype.cljs$core$ICounted$_count$arity$1 = (function (this$){
var self__ = this;
var this$__$1 = this;
return self__.coll.length;
}));

(shadow.dom.NativeColl.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this$){
var self__ = this;
var this$__$1 = this;
return shadow.dom.lazy_native_coll_seq(self__.coll,(0));
}));

(shadow.dom.NativeColl.prototype.shadow$dom$IElement$ = cljs.core.PROTOCOL_SENTINEL);

(shadow.dom.NativeColl.prototype.shadow$dom$IElement$_to_dom$arity$1 = (function (this$){
var self__ = this;
var this$__$1 = this;
return self__.coll;
}));

(shadow.dom.NativeColl.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"coll","coll",-1006698606,null)], null);
}));

(shadow.dom.NativeColl.cljs$lang$type = true);

(shadow.dom.NativeColl.cljs$lang$ctorStr = "shadow.dom/NativeColl");

(shadow.dom.NativeColl.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"shadow.dom/NativeColl");
}));

/**
 * Positional factory function for shadow.dom/NativeColl.
 */
shadow.dom.__GT_NativeColl = (function shadow$dom$__GT_NativeColl(coll){
return (new shadow.dom.NativeColl(coll));
});

shadow.dom.native_coll = (function shadow$dom$native_coll(coll){
return (new shadow.dom.NativeColl(coll));
});
shadow.dom.dom_node = (function shadow$dom$dom_node(el){
if((el == null)){
return null;
} else {
if((((!((el == null))))?((((false) || ((cljs.core.PROTOCOL_SENTINEL === el.shadow$dom$IElement$))))?true:false):false)){
return el.shadow$dom$IElement$_to_dom$arity$1(null);
} else {
if(typeof el === 'string'){
return document.createTextNode(el);
} else {
if(typeof el === 'number'){
return document.createTextNode((""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(el)));
} else {
return el;

}
}
}
}
});
shadow.dom.query_one = (function shadow$dom$query_one(var_args){
var G__11774 = arguments.length;
switch (G__11774) {
case 1:
return shadow.dom.query_one.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return shadow.dom.query_one.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(shadow.dom.query_one.cljs$core$IFn$_invoke$arity$1 = (function (sel){
return document.querySelector(sel);
}));

(shadow.dom.query_one.cljs$core$IFn$_invoke$arity$2 = (function (sel,root){
return shadow.dom.dom_node(root).querySelector(sel);
}));

(shadow.dom.query_one.cljs$lang$maxFixedArity = 2);

shadow.dom.query = (function shadow$dom$query(var_args){
var G__11776 = arguments.length;
switch (G__11776) {
case 1:
return shadow.dom.query.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return shadow.dom.query.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(shadow.dom.query.cljs$core$IFn$_invoke$arity$1 = (function (sel){
return (new shadow.dom.NativeColl(document.querySelectorAll(sel)));
}));

(shadow.dom.query.cljs$core$IFn$_invoke$arity$2 = (function (sel,root){
return (new shadow.dom.NativeColl(shadow.dom.dom_node(root).querySelectorAll(sel)));
}));

(shadow.dom.query.cljs$lang$maxFixedArity = 2);

shadow.dom.by_id = (function shadow$dom$by_id(var_args){
var G__11778 = arguments.length;
switch (G__11778) {
case 2:
return shadow.dom.by_id.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 1:
return shadow.dom.by_id.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(shadow.dom.by_id.cljs$core$IFn$_invoke$arity$2 = (function (id,el){
return shadow.dom.dom_node(el).getElementById(id);
}));

(shadow.dom.by_id.cljs$core$IFn$_invoke$arity$1 = (function (id){
return document.getElementById(id);
}));

(shadow.dom.by_id.cljs$lang$maxFixedArity = 2);

shadow.dom.build = shadow.dom.dom_node;
shadow.dom.ev_stop = (function shadow$dom$ev_stop(var_args){
var G__11780 = arguments.length;
switch (G__11780) {
case 1:
return shadow.dom.ev_stop.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return shadow.dom.ev_stop.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 4:
return shadow.dom.ev_stop.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(shadow.dom.ev_stop.cljs$core$IFn$_invoke$arity$1 = (function (e){
if(cljs.core.truth_(e.stopPropagation)){
e.stopPropagation();

e.preventDefault();
} else {
(e.cancelBubble = true);

(e.returnValue = false);
}

return e;
}));

(shadow.dom.ev_stop.cljs$core$IFn$_invoke$arity$2 = (function (e,el){
shadow.dom.ev_stop.cljs$core$IFn$_invoke$arity$1(e);

return el;
}));

(shadow.dom.ev_stop.cljs$core$IFn$_invoke$arity$4 = (function (e,el,scope,owner){
shadow.dom.ev_stop.cljs$core$IFn$_invoke$arity$1(e);

return el;
}));

(shadow.dom.ev_stop.cljs$lang$maxFixedArity = 4);

/**
 * check wether a parent node (or the document) contains the child
 */
shadow.dom.contains_QMARK_ = (function shadow$dom$contains_QMARK_(var_args){
var G__11786 = arguments.length;
switch (G__11786) {
case 1:
return shadow.dom.contains_QMARK_.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return shadow.dom.contains_QMARK_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(shadow.dom.contains_QMARK_.cljs$core$IFn$_invoke$arity$1 = (function (el){
return goog.dom.contains(document,shadow.dom.dom_node(el));
}));

(shadow.dom.contains_QMARK_.cljs$core$IFn$_invoke$arity$2 = (function (parent,el){
return goog.dom.contains(shadow.dom.dom_node(parent),shadow.dom.dom_node(el));
}));

(shadow.dom.contains_QMARK_.cljs$lang$maxFixedArity = 2);

shadow.dom.add_class = (function shadow$dom$add_class(el,cls){
return goog.dom.classlist.add(shadow.dom.dom_node(el),cls);
});
shadow.dom.remove_class = (function shadow$dom$remove_class(el,cls){
return goog.dom.classlist.remove(shadow.dom.dom_node(el),cls);
});
shadow.dom.toggle_class = (function shadow$dom$toggle_class(var_args){
var G__11789 = arguments.length;
switch (G__11789) {
case 2:
return shadow.dom.toggle_class.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return shadow.dom.toggle_class.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(shadow.dom.toggle_class.cljs$core$IFn$_invoke$arity$2 = (function (el,cls){
return goog.dom.classlist.toggle(shadow.dom.dom_node(el),cls);
}));

(shadow.dom.toggle_class.cljs$core$IFn$_invoke$arity$3 = (function (el,cls,v){
if(cljs.core.truth_(v)){
return shadow.dom.add_class(el,cls);
} else {
return shadow.dom.remove_class(el,cls);
}
}));

(shadow.dom.toggle_class.cljs$lang$maxFixedArity = 3);

shadow.dom.dom_listen = (cljs.core.truth_((function (){var or__5142__auto__ = (!((typeof document !== 'undefined')));
if(or__5142__auto__){
return or__5142__auto__;
} else {
return document.addEventListener;
}
})())?(function shadow$dom$dom_listen_good(el,ev,handler){
return el.addEventListener(ev,handler,false);
}):(function shadow$dom$dom_listen_ie(el,ev,handler){
try{return el.attachEvent((""+"on"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(ev)),(function (e){
return (handler.cljs$core$IFn$_invoke$arity$2 ? handler.cljs$core$IFn$_invoke$arity$2(e,el) : handler.call(null,e,el));
}));
}catch (e11790){if((e11790 instanceof Object)){
var e = e11790;
return console.log("didnt support attachEvent",el,e);
} else {
throw e11790;

}
}}));
shadow.dom.dom_listen_remove = (cljs.core.truth_((function (){var or__5142__auto__ = (!((typeof document !== 'undefined')));
if(or__5142__auto__){
return or__5142__auto__;
} else {
return document.removeEventListener;
}
})())?(function shadow$dom$dom_listen_remove_good(el,ev,handler){
return el.removeEventListener(ev,handler,false);
}):(function shadow$dom$dom_listen_remove_ie(el,ev,handler){
return el.detachEvent((""+"on"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(ev)),handler);
}));
shadow.dom.on_query = (function shadow$dom$on_query(root_el,ev,selector,handler){
var seq__11791 = cljs.core.seq(shadow.dom.query.cljs$core$IFn$_invoke$arity$2(selector,root_el));
var chunk__11792 = null;
var count__11793 = (0);
var i__11794 = (0);
while(true){
if((i__11794 < count__11793)){
var el = chunk__11792.cljs$core$IIndexed$_nth$arity$2(null,i__11794);
var handler_12795__$1 = ((function (seq__11791,chunk__11792,count__11793,i__11794,el){
return (function (e){
return (handler.cljs$core$IFn$_invoke$arity$2 ? handler.cljs$core$IFn$_invoke$arity$2(e,el) : handler.call(null,e,el));
});})(seq__11791,chunk__11792,count__11793,i__11794,el))
;
shadow.dom.dom_listen(el,cljs.core.name(ev),handler_12795__$1);


var G__12799 = seq__11791;
var G__12800 = chunk__11792;
var G__12801 = count__11793;
var G__12802 = (i__11794 + (1));
seq__11791 = G__12799;
chunk__11792 = G__12800;
count__11793 = G__12801;
i__11794 = G__12802;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__11791);
if(temp__5823__auto__){
var seq__11791__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__11791__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__11791__$1);
var G__12804 = cljs.core.chunk_rest(seq__11791__$1);
var G__12805 = c__5673__auto__;
var G__12806 = cljs.core.count(c__5673__auto__);
var G__12807 = (0);
seq__11791 = G__12804;
chunk__11792 = G__12805;
count__11793 = G__12806;
i__11794 = G__12807;
continue;
} else {
var el = cljs.core.first(seq__11791__$1);
var handler_12808__$1 = ((function (seq__11791,chunk__11792,count__11793,i__11794,el,seq__11791__$1,temp__5823__auto__){
return (function (e){
return (handler.cljs$core$IFn$_invoke$arity$2 ? handler.cljs$core$IFn$_invoke$arity$2(e,el) : handler.call(null,e,el));
});})(seq__11791,chunk__11792,count__11793,i__11794,el,seq__11791__$1,temp__5823__auto__))
;
shadow.dom.dom_listen(el,cljs.core.name(ev),handler_12808__$1);


var G__12809 = cljs.core.next(seq__11791__$1);
var G__12810 = null;
var G__12811 = (0);
var G__12812 = (0);
seq__11791 = G__12809;
chunk__11792 = G__12810;
count__11793 = G__12811;
i__11794 = G__12812;
continue;
}
} else {
return null;
}
}
break;
}
});
shadow.dom.on = (function shadow$dom$on(var_args){
var G__11799 = arguments.length;
switch (G__11799) {
case 3:
return shadow.dom.on.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return shadow.dom.on.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(shadow.dom.on.cljs$core$IFn$_invoke$arity$3 = (function (el,ev,handler){
return shadow.dom.on.cljs$core$IFn$_invoke$arity$4(el,ev,handler,false);
}));

(shadow.dom.on.cljs$core$IFn$_invoke$arity$4 = (function (el,ev,handler,capture){
if(cljs.core.vector_QMARK_(ev)){
return shadow.dom.on_query(el,cljs.core.first(ev),cljs.core.second(ev),handler);
} else {
var handler__$1 = (function (e){
return (handler.cljs$core$IFn$_invoke$arity$2 ? handler.cljs$core$IFn$_invoke$arity$2(e,el) : handler.call(null,e,el));
});
return shadow.dom.dom_listen(shadow.dom.dom_node(el),cljs.core.name(ev),handler__$1);
}
}));

(shadow.dom.on.cljs$lang$maxFixedArity = 4);

shadow.dom.remove_event_handler = (function shadow$dom$remove_event_handler(el,ev,handler){
return shadow.dom.dom_listen_remove(shadow.dom.dom_node(el),cljs.core.name(ev),handler);
});
shadow.dom.add_event_listeners = (function shadow$dom$add_event_listeners(el,events){
var seq__11808 = cljs.core.seq(events);
var chunk__11809 = null;
var count__11810 = (0);
var i__11811 = (0);
while(true){
if((i__11811 < count__11810)){
var vec__11830 = chunk__11809.cljs$core$IIndexed$_nth$arity$2(null,i__11811);
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11830,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11830,(1),null);
shadow.dom.on.cljs$core$IFn$_invoke$arity$3(el,k,v);


var G__12826 = seq__11808;
var G__12827 = chunk__11809;
var G__12828 = count__11810;
var G__12829 = (i__11811 + (1));
seq__11808 = G__12826;
chunk__11809 = G__12827;
count__11810 = G__12828;
i__11811 = G__12829;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__11808);
if(temp__5823__auto__){
var seq__11808__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__11808__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__11808__$1);
var G__12832 = cljs.core.chunk_rest(seq__11808__$1);
var G__12833 = c__5673__auto__;
var G__12834 = cljs.core.count(c__5673__auto__);
var G__12835 = (0);
seq__11808 = G__12832;
chunk__11809 = G__12833;
count__11810 = G__12834;
i__11811 = G__12835;
continue;
} else {
var vec__11833 = cljs.core.first(seq__11808__$1);
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11833,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11833,(1),null);
shadow.dom.on.cljs$core$IFn$_invoke$arity$3(el,k,v);


var G__12838 = cljs.core.next(seq__11808__$1);
var G__12839 = null;
var G__12840 = (0);
var G__12841 = (0);
seq__11808 = G__12838;
chunk__11809 = G__12839;
count__11810 = G__12840;
i__11811 = G__12841;
continue;
}
} else {
return null;
}
}
break;
}
});
shadow.dom.set_style = (function shadow$dom$set_style(el,styles){
var dom = shadow.dom.dom_node(el);
var seq__11863 = cljs.core.seq(styles);
var chunk__11864 = null;
var count__11865 = (0);
var i__11866 = (0);
while(true){
if((i__11866 < count__11865)){
var vec__11885 = chunk__11864.cljs$core$IIndexed$_nth$arity$2(null,i__11866);
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11885,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11885,(1),null);
goog.style.setStyle(dom,cljs.core.name(k),(((v == null))?"":v));


var G__12843 = seq__11863;
var G__12844 = chunk__11864;
var G__12845 = count__11865;
var G__12846 = (i__11866 + (1));
seq__11863 = G__12843;
chunk__11864 = G__12844;
count__11865 = G__12845;
i__11866 = G__12846;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__11863);
if(temp__5823__auto__){
var seq__11863__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__11863__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__11863__$1);
var G__12847 = cljs.core.chunk_rest(seq__11863__$1);
var G__12848 = c__5673__auto__;
var G__12849 = cljs.core.count(c__5673__auto__);
var G__12850 = (0);
seq__11863 = G__12847;
chunk__11864 = G__12848;
count__11865 = G__12849;
i__11866 = G__12850;
continue;
} else {
var vec__11893 = cljs.core.first(seq__11863__$1);
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11893,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11893,(1),null);
goog.style.setStyle(dom,cljs.core.name(k),(((v == null))?"":v));


var G__12851 = cljs.core.next(seq__11863__$1);
var G__12852 = null;
var G__12853 = (0);
var G__12854 = (0);
seq__11863 = G__12851;
chunk__11864 = G__12852;
count__11865 = G__12853;
i__11866 = G__12854;
continue;
}
} else {
return null;
}
}
break;
}
});
shadow.dom.set_attr_STAR_ = (function shadow$dom$set_attr_STAR_(el,key,value){
var G__11898_12858 = key;
var G__11898_12860__$1 = (((G__11898_12858 instanceof cljs.core.Keyword))?G__11898_12858.fqn:null);
switch (G__11898_12860__$1) {
case "id":
(el.id = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(value)));

break;
case "class":
(el.className = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(value)));

break;
case "for":
(el.htmlFor = value);

break;
case "cellpadding":
el.setAttribute("cellPadding",value);

break;
case "cellspacing":
el.setAttribute("cellSpacing",value);

break;
case "colspan":
el.setAttribute("colSpan",value);

break;
case "frameborder":
el.setAttribute("frameBorder",value);

break;
case "height":
el.setAttribute("height",value);

break;
case "maxlength":
el.setAttribute("maxLength",value);

break;
case "role":
el.setAttribute("role",value);

break;
case "rowspan":
el.setAttribute("rowSpan",value);

break;
case "type":
el.setAttribute("type",value);

break;
case "usemap":
el.setAttribute("useMap",value);

break;
case "valign":
el.setAttribute("vAlign",value);

break;
case "width":
el.setAttribute("width",value);

break;
case "on":
shadow.dom.add_event_listeners(el,value);

break;
case "style":
if((value == null)){
} else {
if(typeof value === 'string'){
el.setAttribute("style",value);
} else {
if(cljs.core.map_QMARK_(value)){
shadow.dom.set_style(el,value);
} else {
goog.style.setStyle(el,value);

}
}
}

break;
default:
var ks_12885 = cljs.core.name(key);
if(cljs.core.truth_((function (){var or__5142__auto__ = goog.string.startsWith(ks_12885,"data-");
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return goog.string.startsWith(ks_12885,"aria-");
}
})())){
el.setAttribute(ks_12885,value);
} else {
(el[ks_12885] = value);
}

}

return el;
});
shadow.dom.set_attrs = (function shadow$dom$set_attrs(el,attrs){
return cljs.core.reduce_kv((function (el__$1,key,value){
shadow.dom.set_attr_STAR_(el__$1,key,value);

return el__$1;
}),shadow.dom.dom_node(el),attrs);
});
shadow.dom.set_attr = (function shadow$dom$set_attr(el,key,value){
return shadow.dom.set_attr_STAR_(shadow.dom.dom_node(el),key,value);
});
shadow.dom.has_class_QMARK_ = (function shadow$dom$has_class_QMARK_(el,cls){
return goog.dom.classlist.contains(shadow.dom.dom_node(el),cls);
});
shadow.dom.merge_class_string = (function shadow$dom$merge_class_string(current,extra_class){
if(cljs.core.seq(current)){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(current)+" "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(extra_class));
} else {
return extra_class;
}
});
shadow.dom.parse_tag = (function shadow$dom$parse_tag(spec){
var spec__$1 = cljs.core.name(spec);
var fdot = spec__$1.indexOf(".");
var fhash = spec__$1.indexOf("#");
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((-1),fdot)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((-1),fhash)))){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [spec__$1,null,null], null);
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((-1),fhash)){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [spec__$1.substring((0),fdot),null,clojure.string.replace(spec__$1.substring((fdot + (1))),/\./," ")], null);
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((-1),fdot)){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [spec__$1.substring((0),fhash),spec__$1.substring((fhash + (1))),null], null);
} else {
if((fhash > fdot)){
throw (""+"cant have id after class?"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(spec__$1));
} else {
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [spec__$1.substring((0),fhash),spec__$1.substring((fhash + (1)),fdot),clojure.string.replace(spec__$1.substring((fdot + (1))),/\./," ")], null);

}
}
}
}
});
shadow.dom.create_dom_node = (function shadow$dom$create_dom_node(tag_def,p__11912){
var map__11913 = p__11912;
var map__11913__$1 = cljs.core.__destructure_map(map__11913);
var props = map__11913__$1;
var class$ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__11913__$1,new cljs.core.Keyword(null,"class","class",-2030961996));
var tag_props = ({});
var vec__11914 = shadow.dom.parse_tag(tag_def);
var tag_name = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11914,(0),null);
var tag_id = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11914,(1),null);
var tag_classes = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11914,(2),null);
if(cljs.core.truth_(tag_id)){
(tag_props["id"] = tag_id);
} else {
}

if(cljs.core.truth_(tag_classes)){
(tag_props["class"] = shadow.dom.merge_class_string(class$,tag_classes));
} else {
}

var G__11922 = goog.dom.createDom(tag_name,tag_props);
shadow.dom.set_attrs(G__11922,cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(props,new cljs.core.Keyword(null,"class","class",-2030961996)));

return G__11922;
});
shadow.dom.append = (function shadow$dom$append(var_args){
var G__11928 = arguments.length;
switch (G__11928) {
case 1:
return shadow.dom.append.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return shadow.dom.append.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(shadow.dom.append.cljs$core$IFn$_invoke$arity$1 = (function (node){
if(cljs.core.truth_(node)){
var temp__5823__auto__ = shadow.dom.dom_node(node);
if(cljs.core.truth_(temp__5823__auto__)){
var n = temp__5823__auto__;
document.body.appendChild(n);

return n;
} else {
return null;
}
} else {
return null;
}
}));

(shadow.dom.append.cljs$core$IFn$_invoke$arity$2 = (function (el,node){
if(cljs.core.truth_(node)){
var temp__5823__auto__ = shadow.dom.dom_node(node);
if(cljs.core.truth_(temp__5823__auto__)){
var n = temp__5823__auto__;
shadow.dom.dom_node(el).appendChild(n);

return n;
} else {
return null;
}
} else {
return null;
}
}));

(shadow.dom.append.cljs$lang$maxFixedArity = 2);

shadow.dom.destructure_node = (function shadow$dom$destructure_node(create_fn,p__11941){
var vec__11943 = p__11941;
var seq__11944 = cljs.core.seq(vec__11943);
var first__11945 = cljs.core.first(seq__11944);
var seq__11944__$1 = cljs.core.next(seq__11944);
var nn = first__11945;
var first__11945__$1 = cljs.core.first(seq__11944__$1);
var seq__11944__$2 = cljs.core.next(seq__11944__$1);
var np = first__11945__$1;
var nc = seq__11944__$2;
var node = vec__11943;
if((nn instanceof cljs.core.Keyword)){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("invalid dom node",new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"node","node",581201198),node], null));
}

if((((np == null)) && ((nc == null)))){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(function (){var G__11952 = nn;
var G__11953 = cljs.core.PersistentArrayMap.EMPTY;
return (create_fn.cljs$core$IFn$_invoke$arity$2 ? create_fn.cljs$core$IFn$_invoke$arity$2(G__11952,G__11953) : create_fn.call(null,G__11952,G__11953));
})(),cljs.core.List.EMPTY], null);
} else {
if(cljs.core.map_QMARK_(np)){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(create_fn.cljs$core$IFn$_invoke$arity$2 ? create_fn.cljs$core$IFn$_invoke$arity$2(nn,np) : create_fn.call(null,nn,np)),nc], null);
} else {
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(function (){var G__11956 = nn;
var G__11957 = cljs.core.PersistentArrayMap.EMPTY;
return (create_fn.cljs$core$IFn$_invoke$arity$2 ? create_fn.cljs$core$IFn$_invoke$arity$2(G__11956,G__11957) : create_fn.call(null,G__11956,G__11957));
})(),cljs.core.conj.cljs$core$IFn$_invoke$arity$2(nc,np)], null);

}
}
});
shadow.dom.make_dom_node = (function shadow$dom$make_dom_node(structure){
var vec__11966 = shadow.dom.destructure_node(shadow.dom.create_dom_node,structure);
var node = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11966,(0),null);
var node_children = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11966,(1),null);
var seq__11969_12932 = cljs.core.seq(node_children);
var chunk__11970_12933 = null;
var count__11971_12934 = (0);
var i__11972_12935 = (0);
while(true){
if((i__11972_12935 < count__11971_12934)){
var child_struct_12937 = chunk__11970_12933.cljs$core$IIndexed$_nth$arity$2(null,i__11972_12935);
var children_12938 = shadow.dom.dom_node(child_struct_12937);
if(cljs.core.seq_QMARK_(children_12938)){
var seq__12120_12939 = cljs.core.seq(cljs.core.map.cljs$core$IFn$_invoke$arity$2(shadow.dom.dom_node,children_12938));
var chunk__12122_12940 = null;
var count__12123_12941 = (0);
var i__12124_12942 = (0);
while(true){
if((i__12124_12942 < count__12123_12941)){
var child_12946 = chunk__12122_12940.cljs$core$IIndexed$_nth$arity$2(null,i__12124_12942);
if(cljs.core.truth_(child_12946)){
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,child_12946);


var G__12947 = seq__12120_12939;
var G__12948 = chunk__12122_12940;
var G__12949 = count__12123_12941;
var G__12950 = (i__12124_12942 + (1));
seq__12120_12939 = G__12947;
chunk__12122_12940 = G__12948;
count__12123_12941 = G__12949;
i__12124_12942 = G__12950;
continue;
} else {
var G__12951 = seq__12120_12939;
var G__12952 = chunk__12122_12940;
var G__12953 = count__12123_12941;
var G__12954 = (i__12124_12942 + (1));
seq__12120_12939 = G__12951;
chunk__12122_12940 = G__12952;
count__12123_12941 = G__12953;
i__12124_12942 = G__12954;
continue;
}
} else {
var temp__5823__auto___12955 = cljs.core.seq(seq__12120_12939);
if(temp__5823__auto___12955){
var seq__12120_12956__$1 = temp__5823__auto___12955;
if(cljs.core.chunked_seq_QMARK_(seq__12120_12956__$1)){
var c__5673__auto___12957 = cljs.core.chunk_first(seq__12120_12956__$1);
var G__12958 = cljs.core.chunk_rest(seq__12120_12956__$1);
var G__12959 = c__5673__auto___12957;
var G__12960 = cljs.core.count(c__5673__auto___12957);
var G__12961 = (0);
seq__12120_12939 = G__12958;
chunk__12122_12940 = G__12959;
count__12123_12941 = G__12960;
i__12124_12942 = G__12961;
continue;
} else {
var child_12962 = cljs.core.first(seq__12120_12956__$1);
if(cljs.core.truth_(child_12962)){
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,child_12962);


var G__12963 = cljs.core.next(seq__12120_12956__$1);
var G__12964 = null;
var G__12965 = (0);
var G__12966 = (0);
seq__12120_12939 = G__12963;
chunk__12122_12940 = G__12964;
count__12123_12941 = G__12965;
i__12124_12942 = G__12966;
continue;
} else {
var G__12968 = cljs.core.next(seq__12120_12956__$1);
var G__12969 = null;
var G__12970 = (0);
var G__12971 = (0);
seq__12120_12939 = G__12968;
chunk__12122_12940 = G__12969;
count__12123_12941 = G__12970;
i__12124_12942 = G__12971;
continue;
}
}
} else {
}
}
break;
}
} else {
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,children_12938);
}


var G__12972 = seq__11969_12932;
var G__12973 = chunk__11970_12933;
var G__12974 = count__11971_12934;
var G__12975 = (i__11972_12935 + (1));
seq__11969_12932 = G__12972;
chunk__11970_12933 = G__12973;
count__11971_12934 = G__12974;
i__11972_12935 = G__12975;
continue;
} else {
var temp__5823__auto___12976 = cljs.core.seq(seq__11969_12932);
if(temp__5823__auto___12976){
var seq__11969_12977__$1 = temp__5823__auto___12976;
if(cljs.core.chunked_seq_QMARK_(seq__11969_12977__$1)){
var c__5673__auto___12978 = cljs.core.chunk_first(seq__11969_12977__$1);
var G__12981 = cljs.core.chunk_rest(seq__11969_12977__$1);
var G__12982 = c__5673__auto___12978;
var G__12983 = cljs.core.count(c__5673__auto___12978);
var G__12984 = (0);
seq__11969_12932 = G__12981;
chunk__11970_12933 = G__12982;
count__11971_12934 = G__12983;
i__11972_12935 = G__12984;
continue;
} else {
var child_struct_12985 = cljs.core.first(seq__11969_12977__$1);
var children_12986 = shadow.dom.dom_node(child_struct_12985);
if(cljs.core.seq_QMARK_(children_12986)){
var seq__12140_12987 = cljs.core.seq(cljs.core.map.cljs$core$IFn$_invoke$arity$2(shadow.dom.dom_node,children_12986));
var chunk__12142_12988 = null;
var count__12143_12989 = (0);
var i__12144_12990 = (0);
while(true){
if((i__12144_12990 < count__12143_12989)){
var child_12993 = chunk__12142_12988.cljs$core$IIndexed$_nth$arity$2(null,i__12144_12990);
if(cljs.core.truth_(child_12993)){
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,child_12993);


var G__12998 = seq__12140_12987;
var G__12999 = chunk__12142_12988;
var G__13000 = count__12143_12989;
var G__13001 = (i__12144_12990 + (1));
seq__12140_12987 = G__12998;
chunk__12142_12988 = G__12999;
count__12143_12989 = G__13000;
i__12144_12990 = G__13001;
continue;
} else {
var G__13002 = seq__12140_12987;
var G__13003 = chunk__12142_12988;
var G__13004 = count__12143_12989;
var G__13005 = (i__12144_12990 + (1));
seq__12140_12987 = G__13002;
chunk__12142_12988 = G__13003;
count__12143_12989 = G__13004;
i__12144_12990 = G__13005;
continue;
}
} else {
var temp__5823__auto___13007__$1 = cljs.core.seq(seq__12140_12987);
if(temp__5823__auto___13007__$1){
var seq__12140_13008__$1 = temp__5823__auto___13007__$1;
if(cljs.core.chunked_seq_QMARK_(seq__12140_13008__$1)){
var c__5673__auto___13011 = cljs.core.chunk_first(seq__12140_13008__$1);
var G__13012 = cljs.core.chunk_rest(seq__12140_13008__$1);
var G__13013 = c__5673__auto___13011;
var G__13014 = cljs.core.count(c__5673__auto___13011);
var G__13015 = (0);
seq__12140_12987 = G__13012;
chunk__12142_12988 = G__13013;
count__12143_12989 = G__13014;
i__12144_12990 = G__13015;
continue;
} else {
var child_13016 = cljs.core.first(seq__12140_13008__$1);
if(cljs.core.truth_(child_13016)){
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,child_13016);


var G__13017 = cljs.core.next(seq__12140_13008__$1);
var G__13018 = null;
var G__13019 = (0);
var G__13020 = (0);
seq__12140_12987 = G__13017;
chunk__12142_12988 = G__13018;
count__12143_12989 = G__13019;
i__12144_12990 = G__13020;
continue;
} else {
var G__13021 = cljs.core.next(seq__12140_13008__$1);
var G__13022 = null;
var G__13023 = (0);
var G__13024 = (0);
seq__12140_12987 = G__13021;
chunk__12142_12988 = G__13022;
count__12143_12989 = G__13023;
i__12144_12990 = G__13024;
continue;
}
}
} else {
}
}
break;
}
} else {
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,children_12986);
}


var G__13025 = cljs.core.next(seq__11969_12977__$1);
var G__13026 = null;
var G__13027 = (0);
var G__13028 = (0);
seq__11969_12932 = G__13025;
chunk__11970_12933 = G__13026;
count__11971_12934 = G__13027;
i__11972_12935 = G__13028;
continue;
}
} else {
}
}
break;
}

return node;
});
(cljs.core.Keyword.prototype.shadow$dom$IElement$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.Keyword.prototype.shadow$dom$IElement$_to_dom$arity$1 = (function (this$){
var this$__$1 = this;
return shadow.dom.make_dom_node(new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [this$__$1], null));
}));

(cljs.core.PersistentVector.prototype.shadow$dom$IElement$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.PersistentVector.prototype.shadow$dom$IElement$_to_dom$arity$1 = (function (this$){
var this$__$1 = this;
return shadow.dom.make_dom_node(this$__$1);
}));

(cljs.core.LazySeq.prototype.shadow$dom$IElement$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.LazySeq.prototype.shadow$dom$IElement$_to_dom$arity$1 = (function (this$){
var this$__$1 = this;
return cljs.core.map.cljs$core$IFn$_invoke$arity$2(shadow.dom._to_dom,this$__$1);
}));
if(cljs.core.truth_(((typeof HTMLElement) != 'undefined'))){
(HTMLElement.prototype.shadow$dom$IElement$ = cljs.core.PROTOCOL_SENTINEL);

(HTMLElement.prototype.shadow$dom$IElement$_to_dom$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1;
}));
} else {
}
if(cljs.core.truth_(((typeof DocumentFragment) != 'undefined'))){
(DocumentFragment.prototype.shadow$dom$IElement$ = cljs.core.PROTOCOL_SENTINEL);

(DocumentFragment.prototype.shadow$dom$IElement$_to_dom$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1;
}));
} else {
}
/**
 * clear node children
 */
shadow.dom.reset = (function shadow$dom$reset(node){
return goog.dom.removeChildren(shadow.dom.dom_node(node));
});
shadow.dom.remove = (function shadow$dom$remove(node){
if((((!((node == null))))?(((((node.cljs$lang$protocol_mask$partition0$ & (8388608))) || ((cljs.core.PROTOCOL_SENTINEL === node.cljs$core$ISeqable$))))?true:false):false)){
var seq__12174 = cljs.core.seq(node);
var chunk__12175 = null;
var count__12176 = (0);
var i__12177 = (0);
while(true){
if((i__12177 < count__12176)){
var n = chunk__12175.cljs$core$IIndexed$_nth$arity$2(null,i__12177);
(shadow.dom.remove.cljs$core$IFn$_invoke$arity$1 ? shadow.dom.remove.cljs$core$IFn$_invoke$arity$1(n) : shadow.dom.remove.call(null,n));


var G__13043 = seq__12174;
var G__13044 = chunk__12175;
var G__13045 = count__12176;
var G__13046 = (i__12177 + (1));
seq__12174 = G__13043;
chunk__12175 = G__13044;
count__12176 = G__13045;
i__12177 = G__13046;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__12174);
if(temp__5823__auto__){
var seq__12174__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__12174__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__12174__$1);
var G__13056 = cljs.core.chunk_rest(seq__12174__$1);
var G__13057 = c__5673__auto__;
var G__13058 = cljs.core.count(c__5673__auto__);
var G__13059 = (0);
seq__12174 = G__13056;
chunk__12175 = G__13057;
count__12176 = G__13058;
i__12177 = G__13059;
continue;
} else {
var n = cljs.core.first(seq__12174__$1);
(shadow.dom.remove.cljs$core$IFn$_invoke$arity$1 ? shadow.dom.remove.cljs$core$IFn$_invoke$arity$1(n) : shadow.dom.remove.call(null,n));


var G__13067 = cljs.core.next(seq__12174__$1);
var G__13068 = null;
var G__13069 = (0);
var G__13070 = (0);
seq__12174 = G__13067;
chunk__12175 = G__13068;
count__12176 = G__13069;
i__12177 = G__13070;
continue;
}
} else {
return null;
}
}
break;
}
} else {
return goog.dom.removeNode(node);
}
});
shadow.dom.replace_node = (function shadow$dom$replace_node(old,new$){
return goog.dom.replaceNode(shadow.dom.dom_node(new$),shadow.dom.dom_node(old));
});
shadow.dom.text = (function shadow$dom$text(var_args){
var G__12197 = arguments.length;
switch (G__12197) {
case 2:
return shadow.dom.text.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 1:
return shadow.dom.text.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(shadow.dom.text.cljs$core$IFn$_invoke$arity$2 = (function (el,new_text){
return (shadow.dom.dom_node(el).innerText = new_text);
}));

(shadow.dom.text.cljs$core$IFn$_invoke$arity$1 = (function (el){
return shadow.dom.dom_node(el).innerText;
}));

(shadow.dom.text.cljs$lang$maxFixedArity = 2);

shadow.dom.check = (function shadow$dom$check(var_args){
var G__12208 = arguments.length;
switch (G__12208) {
case 1:
return shadow.dom.check.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return shadow.dom.check.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(shadow.dom.check.cljs$core$IFn$_invoke$arity$1 = (function (el){
return shadow.dom.check.cljs$core$IFn$_invoke$arity$2(el,true);
}));

(shadow.dom.check.cljs$core$IFn$_invoke$arity$2 = (function (el,checked){
return (shadow.dom.dom_node(el).checked = checked);
}));

(shadow.dom.check.cljs$lang$maxFixedArity = 2);

shadow.dom.checked_QMARK_ = (function shadow$dom$checked_QMARK_(el){
return shadow.dom.dom_node(el).checked;
});
shadow.dom.form_elements = (function shadow$dom$form_elements(el){
return (new shadow.dom.NativeColl(shadow.dom.dom_node(el).elements));
});
shadow.dom.children = (function shadow$dom$children(el){
return (new shadow.dom.NativeColl(shadow.dom.dom_node(el).children));
});
shadow.dom.child_nodes = (function shadow$dom$child_nodes(el){
return (new shadow.dom.NativeColl(shadow.dom.dom_node(el).childNodes));
});
shadow.dom.attr = (function shadow$dom$attr(var_args){
var G__12229 = arguments.length;
switch (G__12229) {
case 2:
return shadow.dom.attr.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return shadow.dom.attr.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(shadow.dom.attr.cljs$core$IFn$_invoke$arity$2 = (function (el,key){
return shadow.dom.dom_node(el).getAttribute(cljs.core.name(key));
}));

(shadow.dom.attr.cljs$core$IFn$_invoke$arity$3 = (function (el,key,default$){
var or__5142__auto__ = shadow.dom.dom_node(el).getAttribute(cljs.core.name(key));
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return default$;
}
}));

(shadow.dom.attr.cljs$lang$maxFixedArity = 3);

shadow.dom.del_attr = (function shadow$dom$del_attr(el,key){
return shadow.dom.dom_node(el).removeAttribute(cljs.core.name(key));
});
shadow.dom.data = (function shadow$dom$data(el,key){
return shadow.dom.dom_node(el).getAttribute((""+"data-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name(key))));
});
shadow.dom.set_data = (function shadow$dom$set_data(el,key,value){
return shadow.dom.dom_node(el).setAttribute((""+"data-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name(key))),(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(value)));
});
shadow.dom.set_html = (function shadow$dom$set_html(node,text){
return (shadow.dom.dom_node(node).innerHTML = text);
});
shadow.dom.get_html = (function shadow$dom$get_html(node){
return shadow.dom.dom_node(node).innerHTML;
});
shadow.dom.fragment = (function shadow$dom$fragment(var_args){
var args__5882__auto__ = [];
var len__5876__auto___13117 = arguments.length;
var i__5877__auto___13118 = (0);
while(true){
if((i__5877__auto___13118 < len__5876__auto___13117)){
args__5882__auto__.push((arguments[i__5877__auto___13118]));

var G__13119 = (i__5877__auto___13118 + (1));
i__5877__auto___13118 = G__13119;
continue;
} else {
}
break;
}

var argseq__5883__auto__ = ((((0) < args__5882__auto__.length))?(new cljs.core.IndexedSeq(args__5882__auto__.slice((0)),(0),null)):null);
return shadow.dom.fragment.cljs$core$IFn$_invoke$arity$variadic(argseq__5883__auto__);
});

(shadow.dom.fragment.cljs$core$IFn$_invoke$arity$variadic = (function (nodes){
var fragment = document.createDocumentFragment();
var seq__12287_13174 = cljs.core.seq(nodes);
var chunk__12288_13175 = null;
var count__12289_13176 = (0);
var i__12290_13177 = (0);
while(true){
if((i__12290_13177 < count__12289_13176)){
var node_13185 = chunk__12288_13175.cljs$core$IIndexed$_nth$arity$2(null,i__12290_13177);
fragment.appendChild(shadow.dom._to_dom(node_13185));


var G__13187 = seq__12287_13174;
var G__13188 = chunk__12288_13175;
var G__13189 = count__12289_13176;
var G__13190 = (i__12290_13177 + (1));
seq__12287_13174 = G__13187;
chunk__12288_13175 = G__13188;
count__12289_13176 = G__13189;
i__12290_13177 = G__13190;
continue;
} else {
var temp__5823__auto___13195 = cljs.core.seq(seq__12287_13174);
if(temp__5823__auto___13195){
var seq__12287_13198__$1 = temp__5823__auto___13195;
if(cljs.core.chunked_seq_QMARK_(seq__12287_13198__$1)){
var c__5673__auto___13200 = cljs.core.chunk_first(seq__12287_13198__$1);
var G__13201 = cljs.core.chunk_rest(seq__12287_13198__$1);
var G__13202 = c__5673__auto___13200;
var G__13203 = cljs.core.count(c__5673__auto___13200);
var G__13204 = (0);
seq__12287_13174 = G__13201;
chunk__12288_13175 = G__13202;
count__12289_13176 = G__13203;
i__12290_13177 = G__13204;
continue;
} else {
var node_13205 = cljs.core.first(seq__12287_13198__$1);
fragment.appendChild(shadow.dom._to_dom(node_13205));


var G__13206 = cljs.core.next(seq__12287_13198__$1);
var G__13207 = null;
var G__13208 = (0);
var G__13209 = (0);
seq__12287_13174 = G__13206;
chunk__12288_13175 = G__13207;
count__12289_13176 = G__13208;
i__12290_13177 = G__13209;
continue;
}
} else {
}
}
break;
}

return (new shadow.dom.NativeColl(fragment));
}));

(shadow.dom.fragment.cljs$lang$maxFixedArity = (0));

/** @this {Function} */
(shadow.dom.fragment.cljs$lang$applyTo = (function (seq12283){
var self__5862__auto__ = this;
return self__5862__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq(seq12283));
}));

/**
 * given a html string, eval all <script> tags and return the html without the scripts
 * don't do this for everything, only content you trust.
 */
shadow.dom.eval_scripts = (function shadow$dom$eval_scripts(s){
var scripts = cljs.core.re_seq(/<script[^>]*?>(.+?)<\/script>/,s);
var seq__12316_13212 = cljs.core.seq(scripts);
var chunk__12317_13213 = null;
var count__12318_13214 = (0);
var i__12319_13215 = (0);
while(true){
if((i__12319_13215 < count__12318_13214)){
var vec__12329_13217 = chunk__12317_13213.cljs$core$IIndexed$_nth$arity$2(null,i__12319_13215);
var script_tag_13218 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12329_13217,(0),null);
var script_body_13219 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12329_13217,(1),null);
eval(script_body_13219);


var G__13224 = seq__12316_13212;
var G__13225 = chunk__12317_13213;
var G__13226 = count__12318_13214;
var G__13227 = (i__12319_13215 + (1));
seq__12316_13212 = G__13224;
chunk__12317_13213 = G__13225;
count__12318_13214 = G__13226;
i__12319_13215 = G__13227;
continue;
} else {
var temp__5823__auto___13228 = cljs.core.seq(seq__12316_13212);
if(temp__5823__auto___13228){
var seq__12316_13229__$1 = temp__5823__auto___13228;
if(cljs.core.chunked_seq_QMARK_(seq__12316_13229__$1)){
var c__5673__auto___13230 = cljs.core.chunk_first(seq__12316_13229__$1);
var G__13231 = cljs.core.chunk_rest(seq__12316_13229__$1);
var G__13232 = c__5673__auto___13230;
var G__13233 = cljs.core.count(c__5673__auto___13230);
var G__13234 = (0);
seq__12316_13212 = G__13231;
chunk__12317_13213 = G__13232;
count__12318_13214 = G__13233;
i__12319_13215 = G__13234;
continue;
} else {
var vec__12334_13235 = cljs.core.first(seq__12316_13229__$1);
var script_tag_13236 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12334_13235,(0),null);
var script_body_13237 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12334_13235,(1),null);
eval(script_body_13237);


var G__13238 = cljs.core.next(seq__12316_13229__$1);
var G__13239 = null;
var G__13240 = (0);
var G__13241 = (0);
seq__12316_13212 = G__13238;
chunk__12317_13213 = G__13239;
count__12318_13214 = G__13240;
i__12319_13215 = G__13241;
continue;
}
} else {
}
}
break;
}

return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3((function (s__$1,p__12337){
var vec__12338 = p__12337;
var script_tag = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12338,(0),null);
var script_body = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12338,(1),null);
return clojure.string.replace(s__$1,script_tag,"");
}),s,scripts);
});
shadow.dom.str__GT_fragment = (function shadow$dom$str__GT_fragment(s){
var el = document.createElement("div");
(el.innerHTML = s);

return (new shadow.dom.NativeColl(goog.dom.childrenToNode_(document,el)));
});
shadow.dom.node_name = (function shadow$dom$node_name(el){
return shadow.dom.dom_node(el).nodeName;
});
shadow.dom.ancestor_by_class = (function shadow$dom$ancestor_by_class(el,cls){
return goog.dom.getAncestorByClass(shadow.dom.dom_node(el),cls);
});
shadow.dom.ancestor_by_tag = (function shadow$dom$ancestor_by_tag(var_args){
var G__12345 = arguments.length;
switch (G__12345) {
case 2:
return shadow.dom.ancestor_by_tag.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return shadow.dom.ancestor_by_tag.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(shadow.dom.ancestor_by_tag.cljs$core$IFn$_invoke$arity$2 = (function (el,tag){
return goog.dom.getAncestorByTagNameAndClass(shadow.dom.dom_node(el),cljs.core.name(tag));
}));

(shadow.dom.ancestor_by_tag.cljs$core$IFn$_invoke$arity$3 = (function (el,tag,cls){
return goog.dom.getAncestorByTagNameAndClass(shadow.dom.dom_node(el),cljs.core.name(tag),cljs.core.name(cls));
}));

(shadow.dom.ancestor_by_tag.cljs$lang$maxFixedArity = 3);

shadow.dom.get_value = (function shadow$dom$get_value(dom){
return goog.dom.forms.getValue(shadow.dom.dom_node(dom));
});
shadow.dom.set_value = (function shadow$dom$set_value(dom,value){
return goog.dom.forms.setValue(shadow.dom.dom_node(dom),value);
});
shadow.dom.px = (function shadow$dom$px(value){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((value | 0))+"px");
});
shadow.dom.pct = (function shadow$dom$pct(value){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(value)+"%");
});
shadow.dom.remove_style_STAR_ = (function shadow$dom$remove_style_STAR_(el,style){
return el.style.removeProperty(cljs.core.name(style));
});
shadow.dom.remove_style = (function shadow$dom$remove_style(el,style){
var el__$1 = shadow.dom.dom_node(el);
return shadow.dom.remove_style_STAR_(el__$1,style);
});
shadow.dom.remove_styles = (function shadow$dom$remove_styles(el,style_keys){
var el__$1 = shadow.dom.dom_node(el);
var seq__12378 = cljs.core.seq(style_keys);
var chunk__12379 = null;
var count__12380 = (0);
var i__12381 = (0);
while(true){
if((i__12381 < count__12380)){
var it = chunk__12379.cljs$core$IIndexed$_nth$arity$2(null,i__12381);
shadow.dom.remove_style_STAR_(el__$1,it);


var G__13254 = seq__12378;
var G__13255 = chunk__12379;
var G__13256 = count__12380;
var G__13257 = (i__12381 + (1));
seq__12378 = G__13254;
chunk__12379 = G__13255;
count__12380 = G__13256;
i__12381 = G__13257;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__12378);
if(temp__5823__auto__){
var seq__12378__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__12378__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__12378__$1);
var G__13259 = cljs.core.chunk_rest(seq__12378__$1);
var G__13260 = c__5673__auto__;
var G__13261 = cljs.core.count(c__5673__auto__);
var G__13262 = (0);
seq__12378 = G__13259;
chunk__12379 = G__13260;
count__12380 = G__13261;
i__12381 = G__13262;
continue;
} else {
var it = cljs.core.first(seq__12378__$1);
shadow.dom.remove_style_STAR_(el__$1,it);


var G__13264 = cljs.core.next(seq__12378__$1);
var G__13265 = null;
var G__13266 = (0);
var G__13267 = (0);
seq__12378 = G__13264;
chunk__12379 = G__13265;
count__12380 = G__13266;
i__12381 = G__13267;
continue;
}
} else {
return null;
}
}
break;
}
});

/**
* @constructor
 * @implements {cljs.core.IRecord}
 * @implements {cljs.core.IKVReduce}
 * @implements {cljs.core.IEquiv}
 * @implements {cljs.core.IHash}
 * @implements {cljs.core.ICollection}
 * @implements {cljs.core.ICounted}
 * @implements {cljs.core.ISeqable}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.ICloneable}
 * @implements {cljs.core.IPrintWithWriter}
 * @implements {cljs.core.IIterable}
 * @implements {cljs.core.IWithMeta}
 * @implements {cljs.core.IAssociative}
 * @implements {cljs.core.IMap}
 * @implements {cljs.core.ILookup}
*/
shadow.dom.Coordinate = (function (x,y,__meta,__extmap,__hash){
this.x = x;
this.y = y;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2230716170;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
(shadow.dom.Coordinate.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__5448__auto__,k__5449__auto__){
var self__ = this;
var this__5448__auto____$1 = this;
return this__5448__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__5449__auto__,null);
}));

(shadow.dom.Coordinate.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__5450__auto__,k12392,else__5451__auto__){
var self__ = this;
var this__5450__auto____$1 = this;
var G__12415 = k12392;
var G__12415__$1 = (((G__12415 instanceof cljs.core.Keyword))?G__12415.fqn:null);
switch (G__12415__$1) {
case "x":
return self__.x;

break;
case "y":
return self__.y;

break;
default:
return cljs.core.get.cljs$core$IFn$_invoke$arity$3(self__.__extmap,k12392,else__5451__auto__);

}
}));

(shadow.dom.Coordinate.prototype.cljs$core$IKVReduce$_kv_reduce$arity$3 = (function (this__5468__auto__,f__5469__auto__,init__5470__auto__){
var self__ = this;
var this__5468__auto____$1 = this;
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3((function (ret__5471__auto__,p__12419){
var vec__12420 = p__12419;
var k__5472__auto__ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12420,(0),null);
var v__5473__auto__ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12420,(1),null);
return (f__5469__auto__.cljs$core$IFn$_invoke$arity$3 ? f__5469__auto__.cljs$core$IFn$_invoke$arity$3(ret__5471__auto__,k__5472__auto__,v__5473__auto__) : f__5469__auto__.call(null,ret__5471__auto__,k__5472__auto__,v__5473__auto__));
}),init__5470__auto__,this__5468__auto____$1);
}));

(shadow.dom.Coordinate.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__5463__auto__,writer__5464__auto__,opts__5465__auto__){
var self__ = this;
var this__5463__auto____$1 = this;
var pr_pair__5466__auto__ = (function (keyval__5467__auto__){
return cljs.core.pr_sequential_writer(writer__5464__auto__,cljs.core.pr_writer,""," ","",opts__5465__auto__,keyval__5467__auto__);
});
return cljs.core.pr_sequential_writer(writer__5464__auto__,pr_pair__5466__auto__,"#shadow.dom.Coordinate{",", ","}",opts__5465__auto__,cljs.core.concat.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"x","x",2099068185),self__.x],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"y","y",-1757859776),self__.y],null))], null),self__.__extmap));
}));

(shadow.dom.Coordinate.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__12391){
var self__ = this;
var G__12391__$1 = this;
return (new cljs.core.RecordIter((0),G__12391__$1,2,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"x","x",2099068185),new cljs.core.Keyword(null,"y","y",-1757859776)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator(self__.__extmap):cljs.core.nil_iter())));
}));

(shadow.dom.Coordinate.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__5446__auto__){
var self__ = this;
var this__5446__auto____$1 = this;
return self__.__meta;
}));

(shadow.dom.Coordinate.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__5443__auto__){
var self__ = this;
var this__5443__auto____$1 = this;
return (new shadow.dom.Coordinate(self__.x,self__.y,self__.__meta,self__.__extmap,self__.__hash));
}));

(shadow.dom.Coordinate.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__5452__auto__){
var self__ = this;
var this__5452__auto____$1 = this;
return (2 + cljs.core.count(self__.__extmap));
}));

(shadow.dom.Coordinate.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__5444__auto__){
var self__ = this;
var this__5444__auto____$1 = this;
var h__5251__auto__ = self__.__hash;
if((!((h__5251__auto__ == null)))){
return h__5251__auto__;
} else {
var h__5251__auto____$1 = (function (coll__5445__auto__){
return (145542109 ^ cljs.core.hash_unordered_coll(coll__5445__auto__));
})(this__5444__auto____$1);
(self__.__hash = h__5251__auto____$1);

return h__5251__auto____$1;
}
}));

(shadow.dom.Coordinate.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this12393,other12394){
var self__ = this;
var this12393__$1 = this;
return (((!((other12394 == null)))) && ((((this12393__$1.constructor === other12394.constructor)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this12393__$1.x,other12394.x)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this12393__$1.y,other12394.y)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this12393__$1.__extmap,other12394.__extmap)))))))));
}));

(shadow.dom.Coordinate.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__5458__auto__,k__5459__auto__){
var self__ = this;
var this__5458__auto____$1 = this;
if(cljs.core.contains_QMARK_(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"y","y",-1757859776),null,new cljs.core.Keyword(null,"x","x",2099068185),null], null), null),k__5459__auto__)){
return cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(cljs.core._with_meta(cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,this__5458__auto____$1),self__.__meta),k__5459__auto__);
} else {
return (new shadow.dom.Coordinate(self__.x,self__.y,self__.__meta,cljs.core.not_empty(cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(self__.__extmap,k__5459__auto__)),null));
}
}));

(shadow.dom.Coordinate.prototype.cljs$core$IAssociative$_contains_key_QMARK_$arity$2 = (function (this__5455__auto__,k12392){
var self__ = this;
var this__5455__auto____$1 = this;
var G__12442 = k12392;
var G__12442__$1 = (((G__12442 instanceof cljs.core.Keyword))?G__12442.fqn:null);
switch (G__12442__$1) {
case "x":
case "y":
return true;

break;
default:
return cljs.core.contains_QMARK_(self__.__extmap,k12392);

}
}));

(shadow.dom.Coordinate.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__5456__auto__,k__5457__auto__,G__12391){
var self__ = this;
var this__5456__auto____$1 = this;
var pred__12444 = cljs.core.keyword_identical_QMARK_;
var expr__12445 = k__5457__auto__;
if(cljs.core.truth_((pred__12444.cljs$core$IFn$_invoke$arity$2 ? pred__12444.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"x","x",2099068185),expr__12445) : pred__12444.call(null,new cljs.core.Keyword(null,"x","x",2099068185),expr__12445)))){
return (new shadow.dom.Coordinate(G__12391,self__.y,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_((pred__12444.cljs$core$IFn$_invoke$arity$2 ? pred__12444.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"y","y",-1757859776),expr__12445) : pred__12444.call(null,new cljs.core.Keyword(null,"y","y",-1757859776),expr__12445)))){
return (new shadow.dom.Coordinate(self__.x,G__12391,self__.__meta,self__.__extmap,null));
} else {
return (new shadow.dom.Coordinate(self__.x,self__.y,self__.__meta,cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(self__.__extmap,k__5457__auto__,G__12391),null));
}
}
}));

(shadow.dom.Coordinate.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__5461__auto__){
var self__ = this;
var this__5461__auto____$1 = this;
return cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.MapEntry(new cljs.core.Keyword(null,"x","x",2099068185),self__.x,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"y","y",-1757859776),self__.y,null))], null),self__.__extmap));
}));

(shadow.dom.Coordinate.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__5447__auto__,G__12391){
var self__ = this;
var this__5447__auto____$1 = this;
return (new shadow.dom.Coordinate(self__.x,self__.y,G__12391,self__.__extmap,self__.__hash));
}));

(shadow.dom.Coordinate.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__5453__auto__,entry__5454__auto__){
var self__ = this;
var this__5453__auto____$1 = this;
if(cljs.core.vector_QMARK_(entry__5454__auto__)){
return this__5453__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth(entry__5454__auto__,(0)),cljs.core._nth(entry__5454__auto__,(1)));
} else {
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3(cljs.core._conj,this__5453__auto____$1,entry__5454__auto__);
}
}));

(shadow.dom.Coordinate.getBasis = (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"x","x",-555367584,null),new cljs.core.Symbol(null,"y","y",-117328249,null)], null);
}));

(shadow.dom.Coordinate.cljs$lang$type = true);

(shadow.dom.Coordinate.cljs$lang$ctorPrSeq = (function (this__5494__auto__){
return (new cljs.core.List(null,"shadow.dom/Coordinate",null,(1),null));
}));

(shadow.dom.Coordinate.cljs$lang$ctorPrWriter = (function (this__5494__auto__,writer__5495__auto__){
return cljs.core._write(writer__5495__auto__,"shadow.dom/Coordinate");
}));

/**
 * Positional factory function for shadow.dom/Coordinate.
 */
shadow.dom.__GT_Coordinate = (function shadow$dom$__GT_Coordinate(x,y){
return (new shadow.dom.Coordinate(x,y,null,null,null));
});

/**
 * Factory function for shadow.dom/Coordinate, taking a map of keywords to field values.
 */
shadow.dom.map__GT_Coordinate = (function shadow$dom$map__GT_Coordinate(G__12401){
var extmap__5490__auto__ = (function (){var G__12452 = cljs.core.dissoc.cljs$core$IFn$_invoke$arity$variadic(G__12401,new cljs.core.Keyword(null,"x","x",2099068185),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"y","y",-1757859776)], 0));
if(cljs.core.record_QMARK_(G__12401)){
return cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,G__12452);
} else {
return G__12452;
}
})();
return (new shadow.dom.Coordinate(new cljs.core.Keyword(null,"x","x",2099068185).cljs$core$IFn$_invoke$arity$1(G__12401),new cljs.core.Keyword(null,"y","y",-1757859776).cljs$core$IFn$_invoke$arity$1(G__12401),null,cljs.core.not_empty(extmap__5490__auto__),null));
});

shadow.dom.get_position = (function shadow$dom$get_position(el){
var pos = goog.style.getPosition(shadow.dom.dom_node(el));
return shadow.dom.__GT_Coordinate(pos.x,pos.y);
});
shadow.dom.get_client_position = (function shadow$dom$get_client_position(el){
var pos = goog.style.getClientPosition(shadow.dom.dom_node(el));
return shadow.dom.__GT_Coordinate(pos.x,pos.y);
});
shadow.dom.get_page_offset = (function shadow$dom$get_page_offset(el){
var pos = goog.style.getPageOffset(shadow.dom.dom_node(el));
return shadow.dom.__GT_Coordinate(pos.x,pos.y);
});

/**
* @constructor
 * @implements {cljs.core.IRecord}
 * @implements {cljs.core.IKVReduce}
 * @implements {cljs.core.IEquiv}
 * @implements {cljs.core.IHash}
 * @implements {cljs.core.ICollection}
 * @implements {cljs.core.ICounted}
 * @implements {cljs.core.ISeqable}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.ICloneable}
 * @implements {cljs.core.IPrintWithWriter}
 * @implements {cljs.core.IIterable}
 * @implements {cljs.core.IWithMeta}
 * @implements {cljs.core.IAssociative}
 * @implements {cljs.core.IMap}
 * @implements {cljs.core.ILookup}
*/
shadow.dom.Size = (function (w,h,__meta,__extmap,__hash){
this.w = w;
this.h = h;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2230716170;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
(shadow.dom.Size.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__5448__auto__,k__5449__auto__){
var self__ = this;
var this__5448__auto____$1 = this;
return this__5448__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__5449__auto__,null);
}));

(shadow.dom.Size.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__5450__auto__,k12477,else__5451__auto__){
var self__ = this;
var this__5450__auto____$1 = this;
var G__12488 = k12477;
var G__12488__$1 = (((G__12488 instanceof cljs.core.Keyword))?G__12488.fqn:null);
switch (G__12488__$1) {
case "w":
return self__.w;

break;
case "h":
return self__.h;

break;
default:
return cljs.core.get.cljs$core$IFn$_invoke$arity$3(self__.__extmap,k12477,else__5451__auto__);

}
}));

(shadow.dom.Size.prototype.cljs$core$IKVReduce$_kv_reduce$arity$3 = (function (this__5468__auto__,f__5469__auto__,init__5470__auto__){
var self__ = this;
var this__5468__auto____$1 = this;
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3((function (ret__5471__auto__,p__12499){
var vec__12500 = p__12499;
var k__5472__auto__ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12500,(0),null);
var v__5473__auto__ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12500,(1),null);
return (f__5469__auto__.cljs$core$IFn$_invoke$arity$3 ? f__5469__auto__.cljs$core$IFn$_invoke$arity$3(ret__5471__auto__,k__5472__auto__,v__5473__auto__) : f__5469__auto__.call(null,ret__5471__auto__,k__5472__auto__,v__5473__auto__));
}),init__5470__auto__,this__5468__auto____$1);
}));

(shadow.dom.Size.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__5463__auto__,writer__5464__auto__,opts__5465__auto__){
var self__ = this;
var this__5463__auto____$1 = this;
var pr_pair__5466__auto__ = (function (keyval__5467__auto__){
return cljs.core.pr_sequential_writer(writer__5464__auto__,cljs.core.pr_writer,""," ","",opts__5465__auto__,keyval__5467__auto__);
});
return cljs.core.pr_sequential_writer(writer__5464__auto__,pr_pair__5466__auto__,"#shadow.dom.Size{",", ","}",opts__5465__auto__,cljs.core.concat.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"w","w",354169001),self__.w],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"h","h",1109658740),self__.h],null))], null),self__.__extmap));
}));

(shadow.dom.Size.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__12476){
var self__ = this;
var G__12476__$1 = this;
return (new cljs.core.RecordIter((0),G__12476__$1,2,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"w","w",354169001),new cljs.core.Keyword(null,"h","h",1109658740)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator(self__.__extmap):cljs.core.nil_iter())));
}));

(shadow.dom.Size.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__5446__auto__){
var self__ = this;
var this__5446__auto____$1 = this;
return self__.__meta;
}));

(shadow.dom.Size.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__5443__auto__){
var self__ = this;
var this__5443__auto____$1 = this;
return (new shadow.dom.Size(self__.w,self__.h,self__.__meta,self__.__extmap,self__.__hash));
}));

(shadow.dom.Size.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__5452__auto__){
var self__ = this;
var this__5452__auto____$1 = this;
return (2 + cljs.core.count(self__.__extmap));
}));

(shadow.dom.Size.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__5444__auto__){
var self__ = this;
var this__5444__auto____$1 = this;
var h__5251__auto__ = self__.__hash;
if((!((h__5251__auto__ == null)))){
return h__5251__auto__;
} else {
var h__5251__auto____$1 = (function (coll__5445__auto__){
return (-1228019642 ^ cljs.core.hash_unordered_coll(coll__5445__auto__));
})(this__5444__auto____$1);
(self__.__hash = h__5251__auto____$1);

return h__5251__auto____$1;
}
}));

(shadow.dom.Size.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this12478,other12479){
var self__ = this;
var this12478__$1 = this;
return (((!((other12479 == null)))) && ((((this12478__$1.constructor === other12479.constructor)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this12478__$1.w,other12479.w)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this12478__$1.h,other12479.h)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this12478__$1.__extmap,other12479.__extmap)))))))));
}));

(shadow.dom.Size.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__5458__auto__,k__5459__auto__){
var self__ = this;
var this__5458__auto____$1 = this;
if(cljs.core.contains_QMARK_(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"w","w",354169001),null,new cljs.core.Keyword(null,"h","h",1109658740),null], null), null),k__5459__auto__)){
return cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(cljs.core._with_meta(cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,this__5458__auto____$1),self__.__meta),k__5459__auto__);
} else {
return (new shadow.dom.Size(self__.w,self__.h,self__.__meta,cljs.core.not_empty(cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(self__.__extmap,k__5459__auto__)),null));
}
}));

(shadow.dom.Size.prototype.cljs$core$IAssociative$_contains_key_QMARK_$arity$2 = (function (this__5455__auto__,k12477){
var self__ = this;
var this__5455__auto____$1 = this;
var G__12520 = k12477;
var G__12520__$1 = (((G__12520 instanceof cljs.core.Keyword))?G__12520.fqn:null);
switch (G__12520__$1) {
case "w":
case "h":
return true;

break;
default:
return cljs.core.contains_QMARK_(self__.__extmap,k12477);

}
}));

(shadow.dom.Size.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__5456__auto__,k__5457__auto__,G__12476){
var self__ = this;
var this__5456__auto____$1 = this;
var pred__12523 = cljs.core.keyword_identical_QMARK_;
var expr__12524 = k__5457__auto__;
if(cljs.core.truth_((pred__12523.cljs$core$IFn$_invoke$arity$2 ? pred__12523.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"w","w",354169001),expr__12524) : pred__12523.call(null,new cljs.core.Keyword(null,"w","w",354169001),expr__12524)))){
return (new shadow.dom.Size(G__12476,self__.h,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_((pred__12523.cljs$core$IFn$_invoke$arity$2 ? pred__12523.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"h","h",1109658740),expr__12524) : pred__12523.call(null,new cljs.core.Keyword(null,"h","h",1109658740),expr__12524)))){
return (new shadow.dom.Size(self__.w,G__12476,self__.__meta,self__.__extmap,null));
} else {
return (new shadow.dom.Size(self__.w,self__.h,self__.__meta,cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(self__.__extmap,k__5457__auto__,G__12476),null));
}
}
}));

(shadow.dom.Size.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__5461__auto__){
var self__ = this;
var this__5461__auto____$1 = this;
return cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.MapEntry(new cljs.core.Keyword(null,"w","w",354169001),self__.w,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"h","h",1109658740),self__.h,null))], null),self__.__extmap));
}));

(shadow.dom.Size.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__5447__auto__,G__12476){
var self__ = this;
var this__5447__auto____$1 = this;
return (new shadow.dom.Size(self__.w,self__.h,G__12476,self__.__extmap,self__.__hash));
}));

(shadow.dom.Size.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__5453__auto__,entry__5454__auto__){
var self__ = this;
var this__5453__auto____$1 = this;
if(cljs.core.vector_QMARK_(entry__5454__auto__)){
return this__5453__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth(entry__5454__auto__,(0)),cljs.core._nth(entry__5454__auto__,(1)));
} else {
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3(cljs.core._conj,this__5453__auto____$1,entry__5454__auto__);
}
}));

(shadow.dom.Size.getBasis = (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"w","w",1994700528,null),new cljs.core.Symbol(null,"h","h",-1544777029,null)], null);
}));

(shadow.dom.Size.cljs$lang$type = true);

(shadow.dom.Size.cljs$lang$ctorPrSeq = (function (this__5494__auto__){
return (new cljs.core.List(null,"shadow.dom/Size",null,(1),null));
}));

(shadow.dom.Size.cljs$lang$ctorPrWriter = (function (this__5494__auto__,writer__5495__auto__){
return cljs.core._write(writer__5495__auto__,"shadow.dom/Size");
}));

/**
 * Positional factory function for shadow.dom/Size.
 */
shadow.dom.__GT_Size = (function shadow$dom$__GT_Size(w,h){
return (new shadow.dom.Size(w,h,null,null,null));
});

/**
 * Factory function for shadow.dom/Size, taking a map of keywords to field values.
 */
shadow.dom.map__GT_Size = (function shadow$dom$map__GT_Size(G__12482){
var extmap__5490__auto__ = (function (){var G__12534 = cljs.core.dissoc.cljs$core$IFn$_invoke$arity$variadic(G__12482,new cljs.core.Keyword(null,"w","w",354169001),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"h","h",1109658740)], 0));
if(cljs.core.record_QMARK_(G__12482)){
return cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,G__12534);
} else {
return G__12534;
}
})();
return (new shadow.dom.Size(new cljs.core.Keyword(null,"w","w",354169001).cljs$core$IFn$_invoke$arity$1(G__12482),new cljs.core.Keyword(null,"h","h",1109658740).cljs$core$IFn$_invoke$arity$1(G__12482),null,cljs.core.not_empty(extmap__5490__auto__),null));
});

shadow.dom.size__GT_clj = (function shadow$dom$size__GT_clj(size){
return (new shadow.dom.Size(size.width,size.height,null,null,null));
});
shadow.dom.get_size = (function shadow$dom$get_size(el){
return shadow.dom.size__GT_clj(goog.style.getSize(shadow.dom.dom_node(el)));
});
shadow.dom.get_height = (function shadow$dom$get_height(el){
return shadow.dom.get_size(el).h;
});
shadow.dom.get_viewport_size = (function shadow$dom$get_viewport_size(){
return shadow.dom.size__GT_clj(goog.dom.getViewportSize());
});
shadow.dom.first_child = (function shadow$dom$first_child(el){
return (shadow.dom.dom_node(el).children[(0)]);
});
shadow.dom.select_option_values = (function shadow$dom$select_option_values(el){
var native$ = shadow.dom.dom_node(el);
var opts = (native$["options"]);
var a__5738__auto__ = opts;
var l__5739__auto__ = a__5738__auto__.length;
var i = (0);
var ret = cljs.core.PersistentVector.EMPTY;
while(true){
if((i < l__5739__auto__)){
var G__13601 = (i + (1));
var G__13602 = cljs.core.conj.cljs$core$IFn$_invoke$arity$2(ret,(opts[i]["value"]));
i = G__13601;
ret = G__13602;
continue;
} else {
return ret;
}
break;
}
});
shadow.dom.build_url = (function shadow$dom$build_url(path,query_params){
if(cljs.core.empty_QMARK_(query_params)){
return path;
} else {
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(path)+"?"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(clojure.string.join.cljs$core$IFn$_invoke$arity$2("&",cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p__12563){
var vec__12565 = p__12563;
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12565,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12565,(1),null);
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name(k))+"="+cljs.core.str.cljs$core$IFn$_invoke$arity$1(encodeURIComponent((""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)))));
}),query_params))));
}
});
shadow.dom.redirect = (function shadow$dom$redirect(var_args){
var G__12583 = arguments.length;
switch (G__12583) {
case 1:
return shadow.dom.redirect.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return shadow.dom.redirect.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(shadow.dom.redirect.cljs$core$IFn$_invoke$arity$1 = (function (path){
return shadow.dom.redirect.cljs$core$IFn$_invoke$arity$2(path,cljs.core.PersistentArrayMap.EMPTY);
}));

(shadow.dom.redirect.cljs$core$IFn$_invoke$arity$2 = (function (path,query_params){
return (document["location"]["href"] = shadow.dom.build_url(path,query_params));
}));

(shadow.dom.redirect.cljs$lang$maxFixedArity = 2);

shadow.dom.reload_BANG_ = (function shadow$dom$reload_BANG_(){
return (document.location.href = document.location.href);
});
shadow.dom.tag_name = (function shadow$dom$tag_name(el){
var dom = shadow.dom.dom_node(el);
return dom.tagName;
});
shadow.dom.insert_after = (function shadow$dom$insert_after(ref,new$){
var new_node = shadow.dom.dom_node(new$);
goog.dom.insertSiblingAfter(new_node,shadow.dom.dom_node(ref));

return new_node;
});
shadow.dom.insert_before = (function shadow$dom$insert_before(ref,new$){
var new_node = shadow.dom.dom_node(new$);
goog.dom.insertSiblingBefore(new_node,shadow.dom.dom_node(ref));

return new_node;
});
shadow.dom.insert_first = (function shadow$dom$insert_first(ref,new$){
var temp__5821__auto__ = shadow.dom.dom_node(ref).firstChild;
if(cljs.core.truth_(temp__5821__auto__)){
var child = temp__5821__auto__;
return shadow.dom.insert_before(child,new$);
} else {
return shadow.dom.append.cljs$core$IFn$_invoke$arity$2(ref,new$);
}
});
shadow.dom.index_of = (function shadow$dom$index_of(el){
var el__$1 = shadow.dom.dom_node(el);
var i = (0);
while(true){
var ps = el__$1.previousSibling;
if((ps == null)){
return i;
} else {
var G__13638 = ps;
var G__13639 = (i + (1));
el__$1 = G__13638;
i = G__13639;
continue;
}
break;
}
});
shadow.dom.get_parent = (function shadow$dom$get_parent(el){
return goog.dom.getParentElement(shadow.dom.dom_node(el));
});
shadow.dom.parents = (function shadow$dom$parents(el){
var parent = shadow.dom.get_parent(el);
if(cljs.core.truth_(parent)){
return cljs.core.cons(parent,(new cljs.core.LazySeq(null,(function (){
return (shadow.dom.parents.cljs$core$IFn$_invoke$arity$1 ? shadow.dom.parents.cljs$core$IFn$_invoke$arity$1(parent) : shadow.dom.parents.call(null,parent));
}),null,null)));
} else {
return null;
}
});
shadow.dom.matches = (function shadow$dom$matches(el,sel){
return shadow.dom.dom_node(el).matches(sel);
});
shadow.dom.get_next_sibling = (function shadow$dom$get_next_sibling(el){
return goog.dom.getNextElementSibling(shadow.dom.dom_node(el));
});
shadow.dom.get_previous_sibling = (function shadow$dom$get_previous_sibling(el){
return goog.dom.getPreviousElementSibling(shadow.dom.dom_node(el));
});
shadow.dom.xmlns = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(new cljs.core.PersistentArrayMap(null, 2, ["svg","http://www.w3.org/2000/svg","xlink","http://www.w3.org/1999/xlink"], null));
shadow.dom.create_svg_node = (function shadow$dom$create_svg_node(tag_def,props){
var vec__12600 = shadow.dom.parse_tag(tag_def);
var tag_name = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12600,(0),null);
var tag_id = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12600,(1),null);
var tag_classes = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12600,(2),null);
var el = document.createElementNS("http://www.w3.org/2000/svg",tag_name);
if(cljs.core.truth_(tag_id)){
el.setAttribute("id",tag_id);
} else {
}

if(cljs.core.truth_(tag_classes)){
el.setAttribute("class",shadow.dom.merge_class_string(new cljs.core.Keyword(null,"class","class",-2030961996).cljs$core$IFn$_invoke$arity$1(props),tag_classes));
} else {
}

var seq__12605_13694 = cljs.core.seq(props);
var chunk__12606_13695 = null;
var count__12607_13696 = (0);
var i__12608_13697 = (0);
while(true){
if((i__12608_13697 < count__12607_13696)){
var vec__12619_13702 = chunk__12606_13695.cljs$core$IIndexed$_nth$arity$2(null,i__12608_13697);
var k_13703 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12619_13702,(0),null);
var v_13704 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12619_13702,(1),null);
el.setAttributeNS((function (){var temp__5823__auto__ = cljs.core.namespace(k_13703);
if(cljs.core.truth_(temp__5823__auto__)){
var ns = temp__5823__auto__;
return cljs.core.get.cljs$core$IFn$_invoke$arity$2(cljs.core.deref(shadow.dom.xmlns),ns);
} else {
return null;
}
})(),cljs.core.name(k_13703),v_13704);


var G__13706 = seq__12605_13694;
var G__13707 = chunk__12606_13695;
var G__13708 = count__12607_13696;
var G__13709 = (i__12608_13697 + (1));
seq__12605_13694 = G__13706;
chunk__12606_13695 = G__13707;
count__12607_13696 = G__13708;
i__12608_13697 = G__13709;
continue;
} else {
var temp__5823__auto___13711 = cljs.core.seq(seq__12605_13694);
if(temp__5823__auto___13711){
var seq__12605_13713__$1 = temp__5823__auto___13711;
if(cljs.core.chunked_seq_QMARK_(seq__12605_13713__$1)){
var c__5673__auto___13714 = cljs.core.chunk_first(seq__12605_13713__$1);
var G__13715 = cljs.core.chunk_rest(seq__12605_13713__$1);
var G__13716 = c__5673__auto___13714;
var G__13717 = cljs.core.count(c__5673__auto___13714);
var G__13718 = (0);
seq__12605_13694 = G__13715;
chunk__12606_13695 = G__13716;
count__12607_13696 = G__13717;
i__12608_13697 = G__13718;
continue;
} else {
var vec__12624_13720 = cljs.core.first(seq__12605_13713__$1);
var k_13721 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12624_13720,(0),null);
var v_13722 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12624_13720,(1),null);
el.setAttributeNS((function (){var temp__5823__auto____$1 = cljs.core.namespace(k_13721);
if(cljs.core.truth_(temp__5823__auto____$1)){
var ns = temp__5823__auto____$1;
return cljs.core.get.cljs$core$IFn$_invoke$arity$2(cljs.core.deref(shadow.dom.xmlns),ns);
} else {
return null;
}
})(),cljs.core.name(k_13721),v_13722);


var G__13726 = cljs.core.next(seq__12605_13713__$1);
var G__13727 = null;
var G__13728 = (0);
var G__13729 = (0);
seq__12605_13694 = G__13726;
chunk__12606_13695 = G__13727;
count__12607_13696 = G__13728;
i__12608_13697 = G__13729;
continue;
}
} else {
}
}
break;
}

return el;
});
shadow.dom.svg_node = (function shadow$dom$svg_node(el){
if((el == null)){
return null;
} else {
if((((!((el == null))))?((((false) || ((cljs.core.PROTOCOL_SENTINEL === el.shadow$dom$SVGElement$))))?true:false):false)){
return el.shadow$dom$SVGElement$_to_svg$arity$1(null);
} else {
return el;

}
}
});
shadow.dom.make_svg_node = (function shadow$dom$make_svg_node(structure){
var vec__12643 = shadow.dom.destructure_node(shadow.dom.create_svg_node,structure);
var node = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12643,(0),null);
var node_children = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12643,(1),null);
var seq__12646_13739 = cljs.core.seq(node_children);
var chunk__12648_13740 = null;
var count__12649_13741 = (0);
var i__12650_13742 = (0);
while(true){
if((i__12650_13742 < count__12649_13741)){
var child_struct_13743 = chunk__12648_13740.cljs$core$IIndexed$_nth$arity$2(null,i__12650_13742);
if((!((child_struct_13743 == null)))){
if(typeof child_struct_13743 === 'string'){
var text_13744 = (node["textContent"]);
(node["textContent"] = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(text_13744)+cljs.core.str.cljs$core$IFn$_invoke$arity$1(child_struct_13743)));
} else {
var children_13745 = shadow.dom.svg_node(child_struct_13743);
if(cljs.core.seq_QMARK_(children_13745)){
var seq__12674_13746 = cljs.core.seq(children_13745);
var chunk__12676_13747 = null;
var count__12677_13748 = (0);
var i__12678_13749 = (0);
while(true){
if((i__12678_13749 < count__12677_13748)){
var child_13750 = chunk__12676_13747.cljs$core$IIndexed$_nth$arity$2(null,i__12678_13749);
if(cljs.core.truth_(child_13750)){
node.appendChild(child_13750);


var G__13751 = seq__12674_13746;
var G__13752 = chunk__12676_13747;
var G__13753 = count__12677_13748;
var G__13754 = (i__12678_13749 + (1));
seq__12674_13746 = G__13751;
chunk__12676_13747 = G__13752;
count__12677_13748 = G__13753;
i__12678_13749 = G__13754;
continue;
} else {
var G__13758 = seq__12674_13746;
var G__13759 = chunk__12676_13747;
var G__13760 = count__12677_13748;
var G__13761 = (i__12678_13749 + (1));
seq__12674_13746 = G__13758;
chunk__12676_13747 = G__13759;
count__12677_13748 = G__13760;
i__12678_13749 = G__13761;
continue;
}
} else {
var temp__5823__auto___13762 = cljs.core.seq(seq__12674_13746);
if(temp__5823__auto___13762){
var seq__12674_13763__$1 = temp__5823__auto___13762;
if(cljs.core.chunked_seq_QMARK_(seq__12674_13763__$1)){
var c__5673__auto___13765 = cljs.core.chunk_first(seq__12674_13763__$1);
var G__13767 = cljs.core.chunk_rest(seq__12674_13763__$1);
var G__13768 = c__5673__auto___13765;
var G__13769 = cljs.core.count(c__5673__auto___13765);
var G__13770 = (0);
seq__12674_13746 = G__13767;
chunk__12676_13747 = G__13768;
count__12677_13748 = G__13769;
i__12678_13749 = G__13770;
continue;
} else {
var child_13771 = cljs.core.first(seq__12674_13763__$1);
if(cljs.core.truth_(child_13771)){
node.appendChild(child_13771);


var G__13772 = cljs.core.next(seq__12674_13763__$1);
var G__13773 = null;
var G__13774 = (0);
var G__13775 = (0);
seq__12674_13746 = G__13772;
chunk__12676_13747 = G__13773;
count__12677_13748 = G__13774;
i__12678_13749 = G__13775;
continue;
} else {
var G__13776 = cljs.core.next(seq__12674_13763__$1);
var G__13777 = null;
var G__13778 = (0);
var G__13779 = (0);
seq__12674_13746 = G__13776;
chunk__12676_13747 = G__13777;
count__12677_13748 = G__13778;
i__12678_13749 = G__13779;
continue;
}
}
} else {
}
}
break;
}
} else {
node.appendChild(children_13745);
}
}


var G__13780 = seq__12646_13739;
var G__13781 = chunk__12648_13740;
var G__13782 = count__12649_13741;
var G__13783 = (i__12650_13742 + (1));
seq__12646_13739 = G__13780;
chunk__12648_13740 = G__13781;
count__12649_13741 = G__13782;
i__12650_13742 = G__13783;
continue;
} else {
var G__13784 = seq__12646_13739;
var G__13785 = chunk__12648_13740;
var G__13786 = count__12649_13741;
var G__13787 = (i__12650_13742 + (1));
seq__12646_13739 = G__13784;
chunk__12648_13740 = G__13785;
count__12649_13741 = G__13786;
i__12650_13742 = G__13787;
continue;
}
} else {
var temp__5823__auto___13788 = cljs.core.seq(seq__12646_13739);
if(temp__5823__auto___13788){
var seq__12646_13789__$1 = temp__5823__auto___13788;
if(cljs.core.chunked_seq_QMARK_(seq__12646_13789__$1)){
var c__5673__auto___13790 = cljs.core.chunk_first(seq__12646_13789__$1);
var G__13791 = cljs.core.chunk_rest(seq__12646_13789__$1);
var G__13792 = c__5673__auto___13790;
var G__13793 = cljs.core.count(c__5673__auto___13790);
var G__13794 = (0);
seq__12646_13739 = G__13791;
chunk__12648_13740 = G__13792;
count__12649_13741 = G__13793;
i__12650_13742 = G__13794;
continue;
} else {
var child_struct_13795 = cljs.core.first(seq__12646_13789__$1);
if((!((child_struct_13795 == null)))){
if(typeof child_struct_13795 === 'string'){
var text_13796 = (node["textContent"]);
(node["textContent"] = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(text_13796)+cljs.core.str.cljs$core$IFn$_invoke$arity$1(child_struct_13795)));
} else {
var children_13797 = shadow.dom.svg_node(child_struct_13795);
if(cljs.core.seq_QMARK_(children_13797)){
var seq__12686_13798 = cljs.core.seq(children_13797);
var chunk__12688_13799 = null;
var count__12689_13800 = (0);
var i__12690_13801 = (0);
while(true){
if((i__12690_13801 < count__12689_13800)){
var child_13802 = chunk__12688_13799.cljs$core$IIndexed$_nth$arity$2(null,i__12690_13801);
if(cljs.core.truth_(child_13802)){
node.appendChild(child_13802);


var G__13803 = seq__12686_13798;
var G__13804 = chunk__12688_13799;
var G__13805 = count__12689_13800;
var G__13806 = (i__12690_13801 + (1));
seq__12686_13798 = G__13803;
chunk__12688_13799 = G__13804;
count__12689_13800 = G__13805;
i__12690_13801 = G__13806;
continue;
} else {
var G__13808 = seq__12686_13798;
var G__13809 = chunk__12688_13799;
var G__13810 = count__12689_13800;
var G__13811 = (i__12690_13801 + (1));
seq__12686_13798 = G__13808;
chunk__12688_13799 = G__13809;
count__12689_13800 = G__13810;
i__12690_13801 = G__13811;
continue;
}
} else {
var temp__5823__auto___13812__$1 = cljs.core.seq(seq__12686_13798);
if(temp__5823__auto___13812__$1){
var seq__12686_13814__$1 = temp__5823__auto___13812__$1;
if(cljs.core.chunked_seq_QMARK_(seq__12686_13814__$1)){
var c__5673__auto___13815 = cljs.core.chunk_first(seq__12686_13814__$1);
var G__13816 = cljs.core.chunk_rest(seq__12686_13814__$1);
var G__13817 = c__5673__auto___13815;
var G__13818 = cljs.core.count(c__5673__auto___13815);
var G__13819 = (0);
seq__12686_13798 = G__13816;
chunk__12688_13799 = G__13817;
count__12689_13800 = G__13818;
i__12690_13801 = G__13819;
continue;
} else {
var child_13820 = cljs.core.first(seq__12686_13814__$1);
if(cljs.core.truth_(child_13820)){
node.appendChild(child_13820);


var G__13821 = cljs.core.next(seq__12686_13814__$1);
var G__13822 = null;
var G__13823 = (0);
var G__13824 = (0);
seq__12686_13798 = G__13821;
chunk__12688_13799 = G__13822;
count__12689_13800 = G__13823;
i__12690_13801 = G__13824;
continue;
} else {
var G__13825 = cljs.core.next(seq__12686_13814__$1);
var G__13826 = null;
var G__13827 = (0);
var G__13828 = (0);
seq__12686_13798 = G__13825;
chunk__12688_13799 = G__13826;
count__12689_13800 = G__13827;
i__12690_13801 = G__13828;
continue;
}
}
} else {
}
}
break;
}
} else {
node.appendChild(children_13797);
}
}


var G__13844 = cljs.core.next(seq__12646_13789__$1);
var G__13845 = null;
var G__13846 = (0);
var G__13847 = (0);
seq__12646_13739 = G__13844;
chunk__12648_13740 = G__13845;
count__12649_13741 = G__13846;
i__12650_13742 = G__13847;
continue;
} else {
var G__13848 = cljs.core.next(seq__12646_13789__$1);
var G__13849 = null;
var G__13850 = (0);
var G__13851 = (0);
seq__12646_13739 = G__13848;
chunk__12648_13740 = G__13849;
count__12649_13741 = G__13850;
i__12650_13742 = G__13851;
continue;
}
}
} else {
}
}
break;
}

return node;
});
(shadow.dom.SVGElement["string"] = true);

(shadow.dom._to_svg["string"] = (function (this$){
if((this$ instanceof cljs.core.Keyword)){
return shadow.dom.make_svg_node(new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [this$], null));
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("strings cannot be in svgs",new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"this","this",-611633625),this$], null));
}
}));

(cljs.core.PersistentVector.prototype.shadow$dom$SVGElement$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.PersistentVector.prototype.shadow$dom$SVGElement$_to_svg$arity$1 = (function (this$){
var this$__$1 = this;
return shadow.dom.make_svg_node(this$__$1);
}));

(cljs.core.LazySeq.prototype.shadow$dom$SVGElement$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.LazySeq.prototype.shadow$dom$SVGElement$_to_svg$arity$1 = (function (this$){
var this$__$1 = this;
return cljs.core.map.cljs$core$IFn$_invoke$arity$2(shadow.dom._to_svg,this$__$1);
}));

(shadow.dom.SVGElement["null"] = true);

(shadow.dom._to_svg["null"] = (function (_){
return null;
}));
shadow.dom.svg = (function shadow$dom$svg(var_args){
var args__5882__auto__ = [];
var len__5876__auto___13853 = arguments.length;
var i__5877__auto___13854 = (0);
while(true){
if((i__5877__auto___13854 < len__5876__auto___13853)){
args__5882__auto__.push((arguments[i__5877__auto___13854]));

var G__13855 = (i__5877__auto___13854 + (1));
i__5877__auto___13854 = G__13855;
continue;
} else {
}
break;
}

var argseq__5883__auto__ = ((((1) < args__5882__auto__.length))?(new cljs.core.IndexedSeq(args__5882__auto__.slice((1)),(0),null)):null);
return shadow.dom.svg.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__5883__auto__);
});

(shadow.dom.svg.cljs$core$IFn$_invoke$arity$variadic = (function (attrs,children){
return shadow.dom._to_svg(cljs.core.vec(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"svg","svg",856789142),attrs], null),children)));
}));

(shadow.dom.svg.cljs$lang$maxFixedArity = (1));

/** @this {Function} */
(shadow.dom.svg.cljs$lang$applyTo = (function (seq12707){
var G__12708 = cljs.core.first(seq12707);
var seq12707__$1 = cljs.core.next(seq12707);
var self__5861__auto__ = this;
return self__5861__auto__.cljs$core$IFn$_invoke$arity$variadic(G__12708,seq12707__$1);
}));


//# sourceMappingURL=shadow.dom.js.map
