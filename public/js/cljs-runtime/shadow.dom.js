goog.provide('shadow.dom');
shadow.dom.transition_supported_QMARK_ = true;

/**
 * @interface
 */
shadow.dom.IElement = function(){};

var shadow$dom$IElement$_to_dom$dyn_12827 = (function (this$){
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
return shadow$dom$IElement$_to_dom$dyn_12827(this$);
}
});


/**
 * @interface
 */
shadow.dom.SVGElement = function(){};

var shadow$dom$SVGElement$_to_svg$dyn_12831 = (function (this$){
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
return shadow$dom$SVGElement$_to_svg$dyn_12831(this$);
}
});

shadow.dom.lazy_native_coll_seq = (function shadow$dom$lazy_native_coll_seq(coll,idx){
if((idx < coll.length)){
return (new cljs.core.LazySeq(null,(function (){
return cljs.core.cons((coll[idx]),(function (){var G__11728 = coll;
var G__11729 = (idx + (1));
return (shadow.dom.lazy_native_coll_seq.cljs$core$IFn$_invoke$arity$2 ? shadow.dom.lazy_native_coll_seq.cljs$core$IFn$_invoke$arity$2(G__11728,G__11729) : shadow.dom.lazy_native_coll_seq.call(null,G__11728,G__11729));
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
var G__11755 = arguments.length;
switch (G__11755) {
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
var G__11760 = arguments.length;
switch (G__11760) {
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
var G__11765 = arguments.length;
switch (G__11765) {
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
var G__11772 = arguments.length;
switch (G__11772) {
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
var G__11793 = arguments.length;
switch (G__11793) {
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
var G__11814 = arguments.length;
switch (G__11814) {
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
}catch (e11824){if((e11824 instanceof Object)){
var e = e11824;
return console.log("didnt support attachEvent",el,e);
} else {
throw e11824;

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
var seq__11831 = cljs.core.seq(shadow.dom.query.cljs$core$IFn$_invoke$arity$2(selector,root_el));
var chunk__11832 = null;
var count__11833 = (0);
var i__11834 = (0);
while(true){
if((i__11834 < count__11833)){
var el = chunk__11832.cljs$core$IIndexed$_nth$arity$2(null,i__11834);
var handler_12904__$1 = ((function (seq__11831,chunk__11832,count__11833,i__11834,el){
return (function (e){
return (handler.cljs$core$IFn$_invoke$arity$2 ? handler.cljs$core$IFn$_invoke$arity$2(e,el) : handler.call(null,e,el));
});})(seq__11831,chunk__11832,count__11833,i__11834,el))
;
shadow.dom.dom_listen(el,cljs.core.name(ev),handler_12904__$1);


var G__12905 = seq__11831;
var G__12906 = chunk__11832;
var G__12907 = count__11833;
var G__12908 = (i__11834 + (1));
seq__11831 = G__12905;
chunk__11832 = G__12906;
count__11833 = G__12907;
i__11834 = G__12908;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__11831);
if(temp__5823__auto__){
var seq__11831__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__11831__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__11831__$1);
var G__12914 = cljs.core.chunk_rest(seq__11831__$1);
var G__12915 = c__5673__auto__;
var G__12916 = cljs.core.count(c__5673__auto__);
var G__12917 = (0);
seq__11831 = G__12914;
chunk__11832 = G__12915;
count__11833 = G__12916;
i__11834 = G__12917;
continue;
} else {
var el = cljs.core.first(seq__11831__$1);
var handler_12919__$1 = ((function (seq__11831,chunk__11832,count__11833,i__11834,el,seq__11831__$1,temp__5823__auto__){
return (function (e){
return (handler.cljs$core$IFn$_invoke$arity$2 ? handler.cljs$core$IFn$_invoke$arity$2(e,el) : handler.call(null,e,el));
});})(seq__11831,chunk__11832,count__11833,i__11834,el,seq__11831__$1,temp__5823__auto__))
;
shadow.dom.dom_listen(el,cljs.core.name(ev),handler_12919__$1);


var G__12920 = cljs.core.next(seq__11831__$1);
var G__12921 = null;
var G__12922 = (0);
var G__12923 = (0);
seq__11831 = G__12920;
chunk__11832 = G__12921;
count__11833 = G__12922;
i__11834 = G__12923;
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
var G__11853 = arguments.length;
switch (G__11853) {
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
var seq__11897 = cljs.core.seq(events);
var chunk__11898 = null;
var count__11899 = (0);
var i__11900 = (0);
while(true){
if((i__11900 < count__11899)){
var vec__11911 = chunk__11898.cljs$core$IIndexed$_nth$arity$2(null,i__11900);
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11911,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11911,(1),null);
shadow.dom.on.cljs$core$IFn$_invoke$arity$3(el,k,v);


var G__12943 = seq__11897;
var G__12944 = chunk__11898;
var G__12945 = count__11899;
var G__12946 = (i__11900 + (1));
seq__11897 = G__12943;
chunk__11898 = G__12944;
count__11899 = G__12945;
i__11900 = G__12946;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__11897);
if(temp__5823__auto__){
var seq__11897__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__11897__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__11897__$1);
var G__12951 = cljs.core.chunk_rest(seq__11897__$1);
var G__12952 = c__5673__auto__;
var G__12953 = cljs.core.count(c__5673__auto__);
var G__12954 = (0);
seq__11897 = G__12951;
chunk__11898 = G__12952;
count__11899 = G__12953;
i__11900 = G__12954;
continue;
} else {
var vec__11932 = cljs.core.first(seq__11897__$1);
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11932,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11932,(1),null);
shadow.dom.on.cljs$core$IFn$_invoke$arity$3(el,k,v);


var G__12956 = cljs.core.next(seq__11897__$1);
var G__12957 = null;
var G__12958 = (0);
var G__12959 = (0);
seq__11897 = G__12956;
chunk__11898 = G__12957;
count__11899 = G__12958;
i__11900 = G__12959;
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
var seq__11941 = cljs.core.seq(styles);
var chunk__11942 = null;
var count__11943 = (0);
var i__11944 = (0);
while(true){
if((i__11944 < count__11943)){
var vec__11955 = chunk__11942.cljs$core$IIndexed$_nth$arity$2(null,i__11944);
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11955,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11955,(1),null);
goog.style.setStyle(dom,cljs.core.name(k),(((v == null))?"":v));


var G__12963 = seq__11941;
var G__12964 = chunk__11942;
var G__12965 = count__11943;
var G__12966 = (i__11944 + (1));
seq__11941 = G__12963;
chunk__11942 = G__12964;
count__11943 = G__12965;
i__11944 = G__12966;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__11941);
if(temp__5823__auto__){
var seq__11941__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__11941__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__11941__$1);
var G__12972 = cljs.core.chunk_rest(seq__11941__$1);
var G__12973 = c__5673__auto__;
var G__12974 = cljs.core.count(c__5673__auto__);
var G__12975 = (0);
seq__11941 = G__12972;
chunk__11942 = G__12973;
count__11943 = G__12974;
i__11944 = G__12975;
continue;
} else {
var vec__11961 = cljs.core.first(seq__11941__$1);
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11961,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11961,(1),null);
goog.style.setStyle(dom,cljs.core.name(k),(((v == null))?"":v));


var G__12983 = cljs.core.next(seq__11941__$1);
var G__12984 = null;
var G__12985 = (0);
var G__12986 = (0);
seq__11941 = G__12983;
chunk__11942 = G__12984;
count__11943 = G__12985;
i__11944 = G__12986;
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
var G__11974_12992 = key;
var G__11974_12993__$1 = (((G__11974_12992 instanceof cljs.core.Keyword))?G__11974_12992.fqn:null);
switch (G__11974_12993__$1) {
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
var ks_13006 = cljs.core.name(key);
if(cljs.core.truth_((function (){var or__5142__auto__ = goog.string.startsWith(ks_13006,"data-");
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return goog.string.startsWith(ks_13006,"aria-");
}
})())){
el.setAttribute(ks_13006,value);
} else {
(el[ks_13006] = value);
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
shadow.dom.create_dom_node = (function shadow$dom$create_dom_node(tag_def,p__11979){
var map__11980 = p__11979;
var map__11980__$1 = cljs.core.__destructure_map(map__11980);
var props = map__11980__$1;
var class$ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__11980__$1,new cljs.core.Keyword(null,"class","class",-2030961996));
var tag_props = ({});
var vec__11981 = shadow.dom.parse_tag(tag_def);
var tag_name = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11981,(0),null);
var tag_id = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11981,(1),null);
var tag_classes = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11981,(2),null);
if(cljs.core.truth_(tag_id)){
(tag_props["id"] = tag_id);
} else {
}

if(cljs.core.truth_(tag_classes)){
(tag_props["class"] = shadow.dom.merge_class_string(class$,tag_classes));
} else {
}

var G__11984 = goog.dom.createDom(tag_name,tag_props);
shadow.dom.set_attrs(G__11984,cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(props,new cljs.core.Keyword(null,"class","class",-2030961996)));

return G__11984;
});
shadow.dom.append = (function shadow$dom$append(var_args){
var G__11986 = arguments.length;
switch (G__11986) {
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

shadow.dom.destructure_node = (function shadow$dom$destructure_node(create_fn,p__11991){
var vec__11992 = p__11991;
var seq__11993 = cljs.core.seq(vec__11992);
var first__11994 = cljs.core.first(seq__11993);
var seq__11993__$1 = cljs.core.next(seq__11993);
var nn = first__11994;
var first__11994__$1 = cljs.core.first(seq__11993__$1);
var seq__11993__$2 = cljs.core.next(seq__11993__$1);
var np = first__11994__$1;
var nc = seq__11993__$2;
var node = vec__11992;
if((nn instanceof cljs.core.Keyword)){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("invalid dom node",new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"node","node",581201198),node], null));
}

if((((np == null)) && ((nc == null)))){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(function (){var G__11995 = nn;
var G__11996 = cljs.core.PersistentArrayMap.EMPTY;
return (create_fn.cljs$core$IFn$_invoke$arity$2 ? create_fn.cljs$core$IFn$_invoke$arity$2(G__11995,G__11996) : create_fn.call(null,G__11995,G__11996));
})(),cljs.core.List.EMPTY], null);
} else {
if(cljs.core.map_QMARK_(np)){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(create_fn.cljs$core$IFn$_invoke$arity$2 ? create_fn.cljs$core$IFn$_invoke$arity$2(nn,np) : create_fn.call(null,nn,np)),nc], null);
} else {
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(function (){var G__11997 = nn;
var G__11998 = cljs.core.PersistentArrayMap.EMPTY;
return (create_fn.cljs$core$IFn$_invoke$arity$2 ? create_fn.cljs$core$IFn$_invoke$arity$2(G__11997,G__11998) : create_fn.call(null,G__11997,G__11998));
})(),cljs.core.conj.cljs$core$IFn$_invoke$arity$2(nc,np)], null);

}
}
});
shadow.dom.make_dom_node = (function shadow$dom$make_dom_node(structure){
var vec__11999 = shadow.dom.destructure_node(shadow.dom.create_dom_node,structure);
var node = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11999,(0),null);
var node_children = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11999,(1),null);
var seq__12002_13042 = cljs.core.seq(node_children);
var chunk__12003_13043 = null;
var count__12004_13045 = (0);
var i__12005_13046 = (0);
while(true){
if((i__12005_13046 < count__12004_13045)){
var child_struct_13054 = chunk__12003_13043.cljs$core$IIndexed$_nth$arity$2(null,i__12005_13046);
var children_13055 = shadow.dom.dom_node(child_struct_13054);
if(cljs.core.seq_QMARK_(children_13055)){
var seq__12032_13057 = cljs.core.seq(cljs.core.map.cljs$core$IFn$_invoke$arity$2(shadow.dom.dom_node,children_13055));
var chunk__12034_13058 = null;
var count__12035_13059 = (0);
var i__12036_13060 = (0);
while(true){
if((i__12036_13060 < count__12035_13059)){
var child_13063 = chunk__12034_13058.cljs$core$IIndexed$_nth$arity$2(null,i__12036_13060);
if(cljs.core.truth_(child_13063)){
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,child_13063);


var G__13071 = seq__12032_13057;
var G__13072 = chunk__12034_13058;
var G__13073 = count__12035_13059;
var G__13074 = (i__12036_13060 + (1));
seq__12032_13057 = G__13071;
chunk__12034_13058 = G__13072;
count__12035_13059 = G__13073;
i__12036_13060 = G__13074;
continue;
} else {
var G__13077 = seq__12032_13057;
var G__13078 = chunk__12034_13058;
var G__13079 = count__12035_13059;
var G__13080 = (i__12036_13060 + (1));
seq__12032_13057 = G__13077;
chunk__12034_13058 = G__13078;
count__12035_13059 = G__13079;
i__12036_13060 = G__13080;
continue;
}
} else {
var temp__5823__auto___13082 = cljs.core.seq(seq__12032_13057);
if(temp__5823__auto___13082){
var seq__12032_13084__$1 = temp__5823__auto___13082;
if(cljs.core.chunked_seq_QMARK_(seq__12032_13084__$1)){
var c__5673__auto___13085 = cljs.core.chunk_first(seq__12032_13084__$1);
var G__13086 = cljs.core.chunk_rest(seq__12032_13084__$1);
var G__13087 = c__5673__auto___13085;
var G__13088 = cljs.core.count(c__5673__auto___13085);
var G__13089 = (0);
seq__12032_13057 = G__13086;
chunk__12034_13058 = G__13087;
count__12035_13059 = G__13088;
i__12036_13060 = G__13089;
continue;
} else {
var child_13094 = cljs.core.first(seq__12032_13084__$1);
if(cljs.core.truth_(child_13094)){
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,child_13094);


var G__13097 = cljs.core.next(seq__12032_13084__$1);
var G__13098 = null;
var G__13099 = (0);
var G__13100 = (0);
seq__12032_13057 = G__13097;
chunk__12034_13058 = G__13098;
count__12035_13059 = G__13099;
i__12036_13060 = G__13100;
continue;
} else {
var G__13107 = cljs.core.next(seq__12032_13084__$1);
var G__13108 = null;
var G__13109 = (0);
var G__13110 = (0);
seq__12032_13057 = G__13107;
chunk__12034_13058 = G__13108;
count__12035_13059 = G__13109;
i__12036_13060 = G__13110;
continue;
}
}
} else {
}
}
break;
}
} else {
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,children_13055);
}


var G__13113 = seq__12002_13042;
var G__13114 = chunk__12003_13043;
var G__13115 = count__12004_13045;
var G__13116 = (i__12005_13046 + (1));
seq__12002_13042 = G__13113;
chunk__12003_13043 = G__13114;
count__12004_13045 = G__13115;
i__12005_13046 = G__13116;
continue;
} else {
var temp__5823__auto___13118 = cljs.core.seq(seq__12002_13042);
if(temp__5823__auto___13118){
var seq__12002_13119__$1 = temp__5823__auto___13118;
if(cljs.core.chunked_seq_QMARK_(seq__12002_13119__$1)){
var c__5673__auto___13120 = cljs.core.chunk_first(seq__12002_13119__$1);
var G__13121 = cljs.core.chunk_rest(seq__12002_13119__$1);
var G__13122 = c__5673__auto___13120;
var G__13123 = cljs.core.count(c__5673__auto___13120);
var G__13124 = (0);
seq__12002_13042 = G__13121;
chunk__12003_13043 = G__13122;
count__12004_13045 = G__13123;
i__12005_13046 = G__13124;
continue;
} else {
var child_struct_13125 = cljs.core.first(seq__12002_13119__$1);
var children_13126 = shadow.dom.dom_node(child_struct_13125);
if(cljs.core.seq_QMARK_(children_13126)){
var seq__12042_13132 = cljs.core.seq(cljs.core.map.cljs$core$IFn$_invoke$arity$2(shadow.dom.dom_node,children_13126));
var chunk__12044_13133 = null;
var count__12045_13134 = (0);
var i__12046_13135 = (0);
while(true){
if((i__12046_13135 < count__12045_13134)){
var child_13138 = chunk__12044_13133.cljs$core$IIndexed$_nth$arity$2(null,i__12046_13135);
if(cljs.core.truth_(child_13138)){
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,child_13138);


var G__13148 = seq__12042_13132;
var G__13149 = chunk__12044_13133;
var G__13150 = count__12045_13134;
var G__13151 = (i__12046_13135 + (1));
seq__12042_13132 = G__13148;
chunk__12044_13133 = G__13149;
count__12045_13134 = G__13150;
i__12046_13135 = G__13151;
continue;
} else {
var G__13154 = seq__12042_13132;
var G__13155 = chunk__12044_13133;
var G__13156 = count__12045_13134;
var G__13157 = (i__12046_13135 + (1));
seq__12042_13132 = G__13154;
chunk__12044_13133 = G__13155;
count__12045_13134 = G__13156;
i__12046_13135 = G__13157;
continue;
}
} else {
var temp__5823__auto___13159__$1 = cljs.core.seq(seq__12042_13132);
if(temp__5823__auto___13159__$1){
var seq__12042_13160__$1 = temp__5823__auto___13159__$1;
if(cljs.core.chunked_seq_QMARK_(seq__12042_13160__$1)){
var c__5673__auto___13162 = cljs.core.chunk_first(seq__12042_13160__$1);
var G__13164 = cljs.core.chunk_rest(seq__12042_13160__$1);
var G__13165 = c__5673__auto___13162;
var G__13166 = cljs.core.count(c__5673__auto___13162);
var G__13167 = (0);
seq__12042_13132 = G__13164;
chunk__12044_13133 = G__13165;
count__12045_13134 = G__13166;
i__12046_13135 = G__13167;
continue;
} else {
var child_13168 = cljs.core.first(seq__12042_13160__$1);
if(cljs.core.truth_(child_13168)){
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,child_13168);


var G__13169 = cljs.core.next(seq__12042_13160__$1);
var G__13170 = null;
var G__13171 = (0);
var G__13172 = (0);
seq__12042_13132 = G__13169;
chunk__12044_13133 = G__13170;
count__12045_13134 = G__13171;
i__12046_13135 = G__13172;
continue;
} else {
var G__13174 = cljs.core.next(seq__12042_13160__$1);
var G__13175 = null;
var G__13176 = (0);
var G__13177 = (0);
seq__12042_13132 = G__13174;
chunk__12044_13133 = G__13175;
count__12045_13134 = G__13176;
i__12046_13135 = G__13177;
continue;
}
}
} else {
}
}
break;
}
} else {
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,children_13126);
}


var G__13180 = cljs.core.next(seq__12002_13119__$1);
var G__13181 = null;
var G__13182 = (0);
var G__13183 = (0);
seq__12002_13042 = G__13180;
chunk__12003_13043 = G__13181;
count__12004_13045 = G__13182;
i__12005_13046 = G__13183;
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
var seq__12096 = cljs.core.seq(node);
var chunk__12097 = null;
var count__12098 = (0);
var i__12099 = (0);
while(true){
if((i__12099 < count__12098)){
var n = chunk__12097.cljs$core$IIndexed$_nth$arity$2(null,i__12099);
(shadow.dom.remove.cljs$core$IFn$_invoke$arity$1 ? shadow.dom.remove.cljs$core$IFn$_invoke$arity$1(n) : shadow.dom.remove.call(null,n));


var G__13259 = seq__12096;
var G__13260 = chunk__12097;
var G__13261 = count__12098;
var G__13262 = (i__12099 + (1));
seq__12096 = G__13259;
chunk__12097 = G__13260;
count__12098 = G__13261;
i__12099 = G__13262;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__12096);
if(temp__5823__auto__){
var seq__12096__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__12096__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__12096__$1);
var G__13267 = cljs.core.chunk_rest(seq__12096__$1);
var G__13268 = c__5673__auto__;
var G__13269 = cljs.core.count(c__5673__auto__);
var G__13270 = (0);
seq__12096 = G__13267;
chunk__12097 = G__13268;
count__12098 = G__13269;
i__12099 = G__13270;
continue;
} else {
var n = cljs.core.first(seq__12096__$1);
(shadow.dom.remove.cljs$core$IFn$_invoke$arity$1 ? shadow.dom.remove.cljs$core$IFn$_invoke$arity$1(n) : shadow.dom.remove.call(null,n));


var G__13272 = cljs.core.next(seq__12096__$1);
var G__13273 = null;
var G__13274 = (0);
var G__13275 = (0);
seq__12096 = G__13272;
chunk__12097 = G__13273;
count__12098 = G__13274;
i__12099 = G__13275;
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
var G__12126 = arguments.length;
switch (G__12126) {
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
var G__12166 = arguments.length;
switch (G__12166) {
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
var G__12206 = arguments.length;
switch (G__12206) {
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
var len__5876__auto___13323 = arguments.length;
var i__5877__auto___13324 = (0);
while(true){
if((i__5877__auto___13324 < len__5876__auto___13323)){
args__5882__auto__.push((arguments[i__5877__auto___13324]));

var G__13325 = (i__5877__auto___13324 + (1));
i__5877__auto___13324 = G__13325;
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
var seq__12275_13332 = cljs.core.seq(nodes);
var chunk__12276_13333 = null;
var count__12277_13334 = (0);
var i__12278_13335 = (0);
while(true){
if((i__12278_13335 < count__12277_13334)){
var node_13338 = chunk__12276_13333.cljs$core$IIndexed$_nth$arity$2(null,i__12278_13335);
fragment.appendChild(shadow.dom._to_dom(node_13338));


var G__13339 = seq__12275_13332;
var G__13340 = chunk__12276_13333;
var G__13341 = count__12277_13334;
var G__13342 = (i__12278_13335 + (1));
seq__12275_13332 = G__13339;
chunk__12276_13333 = G__13340;
count__12277_13334 = G__13341;
i__12278_13335 = G__13342;
continue;
} else {
var temp__5823__auto___13343 = cljs.core.seq(seq__12275_13332);
if(temp__5823__auto___13343){
var seq__12275_13371__$1 = temp__5823__auto___13343;
if(cljs.core.chunked_seq_QMARK_(seq__12275_13371__$1)){
var c__5673__auto___13372 = cljs.core.chunk_first(seq__12275_13371__$1);
var G__13375 = cljs.core.chunk_rest(seq__12275_13371__$1);
var G__13376 = c__5673__auto___13372;
var G__13377 = cljs.core.count(c__5673__auto___13372);
var G__13378 = (0);
seq__12275_13332 = G__13375;
chunk__12276_13333 = G__13376;
count__12277_13334 = G__13377;
i__12278_13335 = G__13378;
continue;
} else {
var node_13380 = cljs.core.first(seq__12275_13371__$1);
fragment.appendChild(shadow.dom._to_dom(node_13380));


var G__13393 = cljs.core.next(seq__12275_13371__$1);
var G__13394 = null;
var G__13395 = (0);
var G__13396 = (0);
seq__12275_13332 = G__13393;
chunk__12276_13333 = G__13394;
count__12277_13334 = G__13395;
i__12278_13335 = G__13396;
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
(shadow.dom.fragment.cljs$lang$applyTo = (function (seq12268){
var self__5862__auto__ = this;
return self__5862__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq(seq12268));
}));

/**
 * given a html string, eval all <script> tags and return the html without the scripts
 * don't do this for everything, only content you trust.
 */
shadow.dom.eval_scripts = (function shadow$dom$eval_scripts(s){
var scripts = cljs.core.re_seq(/<script[^>]*?>(.+?)<\/script>/,s);
var seq__12296_13401 = cljs.core.seq(scripts);
var chunk__12297_13402 = null;
var count__12298_13403 = (0);
var i__12299_13404 = (0);
while(true){
if((i__12299_13404 < count__12298_13403)){
var vec__12315_13417 = chunk__12297_13402.cljs$core$IIndexed$_nth$arity$2(null,i__12299_13404);
var script_tag_13418 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12315_13417,(0),null);
var script_body_13419 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12315_13417,(1),null);
eval(script_body_13419);


var G__13422 = seq__12296_13401;
var G__13423 = chunk__12297_13402;
var G__13424 = count__12298_13403;
var G__13425 = (i__12299_13404 + (1));
seq__12296_13401 = G__13422;
chunk__12297_13402 = G__13423;
count__12298_13403 = G__13424;
i__12299_13404 = G__13425;
continue;
} else {
var temp__5823__auto___13426 = cljs.core.seq(seq__12296_13401);
if(temp__5823__auto___13426){
var seq__12296_13429__$1 = temp__5823__auto___13426;
if(cljs.core.chunked_seq_QMARK_(seq__12296_13429__$1)){
var c__5673__auto___13431 = cljs.core.chunk_first(seq__12296_13429__$1);
var G__13432 = cljs.core.chunk_rest(seq__12296_13429__$1);
var G__13433 = c__5673__auto___13431;
var G__13434 = cljs.core.count(c__5673__auto___13431);
var G__13435 = (0);
seq__12296_13401 = G__13432;
chunk__12297_13402 = G__13433;
count__12298_13403 = G__13434;
i__12299_13404 = G__13435;
continue;
} else {
var vec__12324_13438 = cljs.core.first(seq__12296_13429__$1);
var script_tag_13439 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12324_13438,(0),null);
var script_body_13440 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12324_13438,(1),null);
eval(script_body_13440);


var G__13442 = cljs.core.next(seq__12296_13429__$1);
var G__13443 = null;
var G__13444 = (0);
var G__13445 = (0);
seq__12296_13401 = G__13442;
chunk__12297_13402 = G__13443;
count__12298_13403 = G__13444;
i__12299_13404 = G__13445;
continue;
}
} else {
}
}
break;
}

return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3((function (s__$1,p__12329){
var vec__12330 = p__12329;
var script_tag = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12330,(0),null);
var script_body = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12330,(1),null);
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
var G__12339 = arguments.length;
switch (G__12339) {
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
var seq__12385 = cljs.core.seq(style_keys);
var chunk__12386 = null;
var count__12387 = (0);
var i__12388 = (0);
while(true){
if((i__12388 < count__12387)){
var it = chunk__12386.cljs$core$IIndexed$_nth$arity$2(null,i__12388);
shadow.dom.remove_style_STAR_(el__$1,it);


var G__13488 = seq__12385;
var G__13489 = chunk__12386;
var G__13490 = count__12387;
var G__13491 = (i__12388 + (1));
seq__12385 = G__13488;
chunk__12386 = G__13489;
count__12387 = G__13490;
i__12388 = G__13491;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__12385);
if(temp__5823__auto__){
var seq__12385__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__12385__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__12385__$1);
var G__13493 = cljs.core.chunk_rest(seq__12385__$1);
var G__13494 = c__5673__auto__;
var G__13495 = cljs.core.count(c__5673__auto__);
var G__13496 = (0);
seq__12385 = G__13493;
chunk__12386 = G__13494;
count__12387 = G__13495;
i__12388 = G__13496;
continue;
} else {
var it = cljs.core.first(seq__12385__$1);
shadow.dom.remove_style_STAR_(el__$1,it);


var G__13499 = cljs.core.next(seq__12385__$1);
var G__13500 = null;
var G__13501 = (0);
var G__13502 = (0);
seq__12385 = G__13499;
chunk__12386 = G__13500;
count__12387 = G__13501;
i__12388 = G__13502;
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

(shadow.dom.Coordinate.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__5450__auto__,k12410,else__5451__auto__){
var self__ = this;
var this__5450__auto____$1 = this;
var G__12429 = k12410;
var G__12429__$1 = (((G__12429 instanceof cljs.core.Keyword))?G__12429.fqn:null);
switch (G__12429__$1) {
case "x":
return self__.x;

break;
case "y":
return self__.y;

break;
default:
return cljs.core.get.cljs$core$IFn$_invoke$arity$3(self__.__extmap,k12410,else__5451__auto__);

}
}));

(shadow.dom.Coordinate.prototype.cljs$core$IKVReduce$_kv_reduce$arity$3 = (function (this__5468__auto__,f__5469__auto__,init__5470__auto__){
var self__ = this;
var this__5468__auto____$1 = this;
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3((function (ret__5471__auto__,p__12432){
var vec__12433 = p__12432;
var k__5472__auto__ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12433,(0),null);
var v__5473__auto__ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12433,(1),null);
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

(shadow.dom.Coordinate.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__12409){
var self__ = this;
var G__12409__$1 = this;
return (new cljs.core.RecordIter((0),G__12409__$1,2,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"x","x",2099068185),new cljs.core.Keyword(null,"y","y",-1757859776)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator(self__.__extmap):cljs.core.nil_iter())));
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

(shadow.dom.Coordinate.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this12411,other12412){
var self__ = this;
var this12411__$1 = this;
return (((!((other12412 == null)))) && ((((this12411__$1.constructor === other12412.constructor)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this12411__$1.x,other12412.x)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this12411__$1.y,other12412.y)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this12411__$1.__extmap,other12412.__extmap)))))))));
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

(shadow.dom.Coordinate.prototype.cljs$core$IAssociative$_contains_key_QMARK_$arity$2 = (function (this__5455__auto__,k12410){
var self__ = this;
var this__5455__auto____$1 = this;
var G__12458 = k12410;
var G__12458__$1 = (((G__12458 instanceof cljs.core.Keyword))?G__12458.fqn:null);
switch (G__12458__$1) {
case "x":
case "y":
return true;

break;
default:
return cljs.core.contains_QMARK_(self__.__extmap,k12410);

}
}));

(shadow.dom.Coordinate.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__5456__auto__,k__5457__auto__,G__12409){
var self__ = this;
var this__5456__auto____$1 = this;
var pred__12462 = cljs.core.keyword_identical_QMARK_;
var expr__12463 = k__5457__auto__;
if(cljs.core.truth_((pred__12462.cljs$core$IFn$_invoke$arity$2 ? pred__12462.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"x","x",2099068185),expr__12463) : pred__12462.call(null,new cljs.core.Keyword(null,"x","x",2099068185),expr__12463)))){
return (new shadow.dom.Coordinate(G__12409,self__.y,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_((pred__12462.cljs$core$IFn$_invoke$arity$2 ? pred__12462.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"y","y",-1757859776),expr__12463) : pred__12462.call(null,new cljs.core.Keyword(null,"y","y",-1757859776),expr__12463)))){
return (new shadow.dom.Coordinate(self__.x,G__12409,self__.__meta,self__.__extmap,null));
} else {
return (new shadow.dom.Coordinate(self__.x,self__.y,self__.__meta,cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(self__.__extmap,k__5457__auto__,G__12409),null));
}
}
}));

(shadow.dom.Coordinate.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__5461__auto__){
var self__ = this;
var this__5461__auto____$1 = this;
return cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.MapEntry(new cljs.core.Keyword(null,"x","x",2099068185),self__.x,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"y","y",-1757859776),self__.y,null))], null),self__.__extmap));
}));

(shadow.dom.Coordinate.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__5447__auto__,G__12409){
var self__ = this;
var this__5447__auto____$1 = this;
return (new shadow.dom.Coordinate(self__.x,self__.y,G__12409,self__.__extmap,self__.__hash));
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
shadow.dom.map__GT_Coordinate = (function shadow$dom$map__GT_Coordinate(G__12422){
var extmap__5490__auto__ = (function (){var G__12488 = cljs.core.dissoc.cljs$core$IFn$_invoke$arity$variadic(G__12422,new cljs.core.Keyword(null,"x","x",2099068185),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"y","y",-1757859776)], 0));
if(cljs.core.record_QMARK_(G__12422)){
return cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,G__12488);
} else {
return G__12488;
}
})();
return (new shadow.dom.Coordinate(new cljs.core.Keyword(null,"x","x",2099068185).cljs$core$IFn$_invoke$arity$1(G__12422),new cljs.core.Keyword(null,"y","y",-1757859776).cljs$core$IFn$_invoke$arity$1(G__12422),null,cljs.core.not_empty(extmap__5490__auto__),null));
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

(shadow.dom.Size.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__5450__auto__,k12510,else__5451__auto__){
var self__ = this;
var this__5450__auto____$1 = this;
var G__12518 = k12510;
var G__12518__$1 = (((G__12518 instanceof cljs.core.Keyword))?G__12518.fqn:null);
switch (G__12518__$1) {
case "w":
return self__.w;

break;
case "h":
return self__.h;

break;
default:
return cljs.core.get.cljs$core$IFn$_invoke$arity$3(self__.__extmap,k12510,else__5451__auto__);

}
}));

(shadow.dom.Size.prototype.cljs$core$IKVReduce$_kv_reduce$arity$3 = (function (this__5468__auto__,f__5469__auto__,init__5470__auto__){
var self__ = this;
var this__5468__auto____$1 = this;
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3((function (ret__5471__auto__,p__12520){
var vec__12521 = p__12520;
var k__5472__auto__ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12521,(0),null);
var v__5473__auto__ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12521,(1),null);
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

(shadow.dom.Size.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__12509){
var self__ = this;
var G__12509__$1 = this;
return (new cljs.core.RecordIter((0),G__12509__$1,2,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"w","w",354169001),new cljs.core.Keyword(null,"h","h",1109658740)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator(self__.__extmap):cljs.core.nil_iter())));
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

(shadow.dom.Size.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this12511,other12512){
var self__ = this;
var this12511__$1 = this;
return (((!((other12512 == null)))) && ((((this12511__$1.constructor === other12512.constructor)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this12511__$1.w,other12512.w)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this12511__$1.h,other12512.h)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this12511__$1.__extmap,other12512.__extmap)))))))));
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

(shadow.dom.Size.prototype.cljs$core$IAssociative$_contains_key_QMARK_$arity$2 = (function (this__5455__auto__,k12510){
var self__ = this;
var this__5455__auto____$1 = this;
var G__12542 = k12510;
var G__12542__$1 = (((G__12542 instanceof cljs.core.Keyword))?G__12542.fqn:null);
switch (G__12542__$1) {
case "w":
case "h":
return true;

break;
default:
return cljs.core.contains_QMARK_(self__.__extmap,k12510);

}
}));

(shadow.dom.Size.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__5456__auto__,k__5457__auto__,G__12509){
var self__ = this;
var this__5456__auto____$1 = this;
var pred__12544 = cljs.core.keyword_identical_QMARK_;
var expr__12545 = k__5457__auto__;
if(cljs.core.truth_((pred__12544.cljs$core$IFn$_invoke$arity$2 ? pred__12544.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"w","w",354169001),expr__12545) : pred__12544.call(null,new cljs.core.Keyword(null,"w","w",354169001),expr__12545)))){
return (new shadow.dom.Size(G__12509,self__.h,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_((pred__12544.cljs$core$IFn$_invoke$arity$2 ? pred__12544.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"h","h",1109658740),expr__12545) : pred__12544.call(null,new cljs.core.Keyword(null,"h","h",1109658740),expr__12545)))){
return (new shadow.dom.Size(self__.w,G__12509,self__.__meta,self__.__extmap,null));
} else {
return (new shadow.dom.Size(self__.w,self__.h,self__.__meta,cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(self__.__extmap,k__5457__auto__,G__12509),null));
}
}
}));

(shadow.dom.Size.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__5461__auto__){
var self__ = this;
var this__5461__auto____$1 = this;
return cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.MapEntry(new cljs.core.Keyword(null,"w","w",354169001),self__.w,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"h","h",1109658740),self__.h,null))], null),self__.__extmap));
}));

(shadow.dom.Size.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__5447__auto__,G__12509){
var self__ = this;
var this__5447__auto____$1 = this;
return (new shadow.dom.Size(self__.w,self__.h,G__12509,self__.__extmap,self__.__hash));
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
shadow.dom.map__GT_Size = (function shadow$dom$map__GT_Size(G__12514){
var extmap__5490__auto__ = (function (){var G__12556 = cljs.core.dissoc.cljs$core$IFn$_invoke$arity$variadic(G__12514,new cljs.core.Keyword(null,"w","w",354169001),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"h","h",1109658740)], 0));
if(cljs.core.record_QMARK_(G__12514)){
return cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,G__12556);
} else {
return G__12556;
}
})();
return (new shadow.dom.Size(new cljs.core.Keyword(null,"w","w",354169001).cljs$core$IFn$_invoke$arity$1(G__12514),new cljs.core.Keyword(null,"h","h",1109658740).cljs$core$IFn$_invoke$arity$1(G__12514),null,cljs.core.not_empty(extmap__5490__auto__),null));
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
var G__13858 = (i + (1));
var G__13859 = cljs.core.conj.cljs$core$IFn$_invoke$arity$2(ret,(opts[i]["value"]));
i = G__13858;
ret = G__13859;
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
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(path)+"?"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(clojure.string.join.cljs$core$IFn$_invoke$arity$2("&",cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p__12579){
var vec__12581 = p__12579;
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12581,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12581,(1),null);
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name(k))+"="+cljs.core.str.cljs$core$IFn$_invoke$arity$1(encodeURIComponent((""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)))));
}),query_params))));
}
});
shadow.dom.redirect = (function shadow$dom$redirect(var_args){
var G__12596 = arguments.length;
switch (G__12596) {
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
var G__13889 = ps;
var G__13890 = (i + (1));
el__$1 = G__13889;
i = G__13890;
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
var vec__12623 = shadow.dom.parse_tag(tag_def);
var tag_name = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12623,(0),null);
var tag_id = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12623,(1),null);
var tag_classes = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12623,(2),null);
var el = document.createElementNS("http://www.w3.org/2000/svg",tag_name);
if(cljs.core.truth_(tag_id)){
el.setAttribute("id",tag_id);
} else {
}

if(cljs.core.truth_(tag_classes)){
el.setAttribute("class",shadow.dom.merge_class_string(new cljs.core.Keyword(null,"class","class",-2030961996).cljs$core$IFn$_invoke$arity$1(props),tag_classes));
} else {
}

var seq__12636_13909 = cljs.core.seq(props);
var chunk__12637_13910 = null;
var count__12638_13911 = (0);
var i__12639_13912 = (0);
while(true){
if((i__12639_13912 < count__12638_13911)){
var vec__12657_13915 = chunk__12637_13910.cljs$core$IIndexed$_nth$arity$2(null,i__12639_13912);
var k_13916 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12657_13915,(0),null);
var v_13917 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12657_13915,(1),null);
el.setAttributeNS((function (){var temp__5823__auto__ = cljs.core.namespace(k_13916);
if(cljs.core.truth_(temp__5823__auto__)){
var ns = temp__5823__auto__;
return cljs.core.get.cljs$core$IFn$_invoke$arity$2(cljs.core.deref(shadow.dom.xmlns),ns);
} else {
return null;
}
})(),cljs.core.name(k_13916),v_13917);


var G__13920 = seq__12636_13909;
var G__13921 = chunk__12637_13910;
var G__13922 = count__12638_13911;
var G__13923 = (i__12639_13912 + (1));
seq__12636_13909 = G__13920;
chunk__12637_13910 = G__13921;
count__12638_13911 = G__13922;
i__12639_13912 = G__13923;
continue;
} else {
var temp__5823__auto___13924 = cljs.core.seq(seq__12636_13909);
if(temp__5823__auto___13924){
var seq__12636_13925__$1 = temp__5823__auto___13924;
if(cljs.core.chunked_seq_QMARK_(seq__12636_13925__$1)){
var c__5673__auto___13956 = cljs.core.chunk_first(seq__12636_13925__$1);
var G__13957 = cljs.core.chunk_rest(seq__12636_13925__$1);
var G__13958 = c__5673__auto___13956;
var G__13959 = cljs.core.count(c__5673__auto___13956);
var G__13960 = (0);
seq__12636_13909 = G__13957;
chunk__12637_13910 = G__13958;
count__12638_13911 = G__13959;
i__12639_13912 = G__13960;
continue;
} else {
var vec__12663_13961 = cljs.core.first(seq__12636_13925__$1);
var k_13962 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12663_13961,(0),null);
var v_13963 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12663_13961,(1),null);
el.setAttributeNS((function (){var temp__5823__auto____$1 = cljs.core.namespace(k_13962);
if(cljs.core.truth_(temp__5823__auto____$1)){
var ns = temp__5823__auto____$1;
return cljs.core.get.cljs$core$IFn$_invoke$arity$2(cljs.core.deref(shadow.dom.xmlns),ns);
} else {
return null;
}
})(),cljs.core.name(k_13962),v_13963);


var G__13965 = cljs.core.next(seq__12636_13925__$1);
var G__13966 = null;
var G__13967 = (0);
var G__13968 = (0);
seq__12636_13909 = G__13965;
chunk__12637_13910 = G__13966;
count__12638_13911 = G__13967;
i__12639_13912 = G__13968;
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
var vec__12691 = shadow.dom.destructure_node(shadow.dom.create_svg_node,structure);
var node = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12691,(0),null);
var node_children = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12691,(1),null);
var seq__12695_13972 = cljs.core.seq(node_children);
var chunk__12697_13973 = null;
var count__12698_13974 = (0);
var i__12699_13975 = (0);
while(true){
if((i__12699_13975 < count__12698_13974)){
var child_struct_13977 = chunk__12697_13973.cljs$core$IIndexed$_nth$arity$2(null,i__12699_13975);
if((!((child_struct_13977 == null)))){
if(typeof child_struct_13977 === 'string'){
var text_13980 = (node["textContent"]);
(node["textContent"] = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(text_13980)+cljs.core.str.cljs$core$IFn$_invoke$arity$1(child_struct_13977)));
} else {
var children_13981 = shadow.dom.svg_node(child_struct_13977);
if(cljs.core.seq_QMARK_(children_13981)){
var seq__12741_13982 = cljs.core.seq(children_13981);
var chunk__12743_13983 = null;
var count__12744_13984 = (0);
var i__12745_13985 = (0);
while(true){
if((i__12745_13985 < count__12744_13984)){
var child_13990 = chunk__12743_13983.cljs$core$IIndexed$_nth$arity$2(null,i__12745_13985);
if(cljs.core.truth_(child_13990)){
node.appendChild(child_13990);


var G__13992 = seq__12741_13982;
var G__13993 = chunk__12743_13983;
var G__13994 = count__12744_13984;
var G__13995 = (i__12745_13985 + (1));
seq__12741_13982 = G__13992;
chunk__12743_13983 = G__13993;
count__12744_13984 = G__13994;
i__12745_13985 = G__13995;
continue;
} else {
var G__13996 = seq__12741_13982;
var G__13997 = chunk__12743_13983;
var G__13998 = count__12744_13984;
var G__13999 = (i__12745_13985 + (1));
seq__12741_13982 = G__13996;
chunk__12743_13983 = G__13997;
count__12744_13984 = G__13998;
i__12745_13985 = G__13999;
continue;
}
} else {
var temp__5823__auto___14001 = cljs.core.seq(seq__12741_13982);
if(temp__5823__auto___14001){
var seq__12741_14002__$1 = temp__5823__auto___14001;
if(cljs.core.chunked_seq_QMARK_(seq__12741_14002__$1)){
var c__5673__auto___14004 = cljs.core.chunk_first(seq__12741_14002__$1);
var G__14005 = cljs.core.chunk_rest(seq__12741_14002__$1);
var G__14006 = c__5673__auto___14004;
var G__14007 = cljs.core.count(c__5673__auto___14004);
var G__14008 = (0);
seq__12741_13982 = G__14005;
chunk__12743_13983 = G__14006;
count__12744_13984 = G__14007;
i__12745_13985 = G__14008;
continue;
} else {
var child_14009 = cljs.core.first(seq__12741_14002__$1);
if(cljs.core.truth_(child_14009)){
node.appendChild(child_14009);


var G__14010 = cljs.core.next(seq__12741_14002__$1);
var G__14011 = null;
var G__14012 = (0);
var G__14013 = (0);
seq__12741_13982 = G__14010;
chunk__12743_13983 = G__14011;
count__12744_13984 = G__14012;
i__12745_13985 = G__14013;
continue;
} else {
var G__14016 = cljs.core.next(seq__12741_14002__$1);
var G__14017 = null;
var G__14018 = (0);
var G__14019 = (0);
seq__12741_13982 = G__14016;
chunk__12743_13983 = G__14017;
count__12744_13984 = G__14018;
i__12745_13985 = G__14019;
continue;
}
}
} else {
}
}
break;
}
} else {
node.appendChild(children_13981);
}
}


var G__14020 = seq__12695_13972;
var G__14021 = chunk__12697_13973;
var G__14022 = count__12698_13974;
var G__14023 = (i__12699_13975 + (1));
seq__12695_13972 = G__14020;
chunk__12697_13973 = G__14021;
count__12698_13974 = G__14022;
i__12699_13975 = G__14023;
continue;
} else {
var G__14025 = seq__12695_13972;
var G__14026 = chunk__12697_13973;
var G__14027 = count__12698_13974;
var G__14028 = (i__12699_13975 + (1));
seq__12695_13972 = G__14025;
chunk__12697_13973 = G__14026;
count__12698_13974 = G__14027;
i__12699_13975 = G__14028;
continue;
}
} else {
var temp__5823__auto___14031 = cljs.core.seq(seq__12695_13972);
if(temp__5823__auto___14031){
var seq__12695_14032__$1 = temp__5823__auto___14031;
if(cljs.core.chunked_seq_QMARK_(seq__12695_14032__$1)){
var c__5673__auto___14033 = cljs.core.chunk_first(seq__12695_14032__$1);
var G__14034 = cljs.core.chunk_rest(seq__12695_14032__$1);
var G__14035 = c__5673__auto___14033;
var G__14036 = cljs.core.count(c__5673__auto___14033);
var G__14037 = (0);
seq__12695_13972 = G__14034;
chunk__12697_13973 = G__14035;
count__12698_13974 = G__14036;
i__12699_13975 = G__14037;
continue;
} else {
var child_struct_14039 = cljs.core.first(seq__12695_14032__$1);
if((!((child_struct_14039 == null)))){
if(typeof child_struct_14039 === 'string'){
var text_14042 = (node["textContent"]);
(node["textContent"] = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(text_14042)+cljs.core.str.cljs$core$IFn$_invoke$arity$1(child_struct_14039)));
} else {
var children_14043 = shadow.dom.svg_node(child_struct_14039);
if(cljs.core.seq_QMARK_(children_14043)){
var seq__12757_14044 = cljs.core.seq(children_14043);
var chunk__12759_14045 = null;
var count__12760_14046 = (0);
var i__12761_14047 = (0);
while(true){
if((i__12761_14047 < count__12760_14046)){
var child_14049 = chunk__12759_14045.cljs$core$IIndexed$_nth$arity$2(null,i__12761_14047);
if(cljs.core.truth_(child_14049)){
node.appendChild(child_14049);


var G__14050 = seq__12757_14044;
var G__14051 = chunk__12759_14045;
var G__14052 = count__12760_14046;
var G__14053 = (i__12761_14047 + (1));
seq__12757_14044 = G__14050;
chunk__12759_14045 = G__14051;
count__12760_14046 = G__14052;
i__12761_14047 = G__14053;
continue;
} else {
var G__14055 = seq__12757_14044;
var G__14056 = chunk__12759_14045;
var G__14057 = count__12760_14046;
var G__14058 = (i__12761_14047 + (1));
seq__12757_14044 = G__14055;
chunk__12759_14045 = G__14056;
count__12760_14046 = G__14057;
i__12761_14047 = G__14058;
continue;
}
} else {
var temp__5823__auto___14061__$1 = cljs.core.seq(seq__12757_14044);
if(temp__5823__auto___14061__$1){
var seq__12757_14063__$1 = temp__5823__auto___14061__$1;
if(cljs.core.chunked_seq_QMARK_(seq__12757_14063__$1)){
var c__5673__auto___14064 = cljs.core.chunk_first(seq__12757_14063__$1);
var G__14065 = cljs.core.chunk_rest(seq__12757_14063__$1);
var G__14066 = c__5673__auto___14064;
var G__14067 = cljs.core.count(c__5673__auto___14064);
var G__14068 = (0);
seq__12757_14044 = G__14065;
chunk__12759_14045 = G__14066;
count__12760_14046 = G__14067;
i__12761_14047 = G__14068;
continue;
} else {
var child_14070 = cljs.core.first(seq__12757_14063__$1);
if(cljs.core.truth_(child_14070)){
node.appendChild(child_14070);


var G__14073 = cljs.core.next(seq__12757_14063__$1);
var G__14074 = null;
var G__14075 = (0);
var G__14076 = (0);
seq__12757_14044 = G__14073;
chunk__12759_14045 = G__14074;
count__12760_14046 = G__14075;
i__12761_14047 = G__14076;
continue;
} else {
var G__14078 = cljs.core.next(seq__12757_14063__$1);
var G__14079 = null;
var G__14080 = (0);
var G__14081 = (0);
seq__12757_14044 = G__14078;
chunk__12759_14045 = G__14079;
count__12760_14046 = G__14080;
i__12761_14047 = G__14081;
continue;
}
}
} else {
}
}
break;
}
} else {
node.appendChild(children_14043);
}
}


var G__14082 = cljs.core.next(seq__12695_14032__$1);
var G__14083 = null;
var G__14084 = (0);
var G__14085 = (0);
seq__12695_13972 = G__14082;
chunk__12697_13973 = G__14083;
count__12698_13974 = G__14084;
i__12699_13975 = G__14085;
continue;
} else {
var G__14086 = cljs.core.next(seq__12695_14032__$1);
var G__14087 = null;
var G__14088 = (0);
var G__14089 = (0);
seq__12695_13972 = G__14086;
chunk__12697_13973 = G__14087;
count__12698_13974 = G__14088;
i__12699_13975 = G__14089;
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
var len__5876__auto___14096 = arguments.length;
var i__5877__auto___14097 = (0);
while(true){
if((i__5877__auto___14097 < len__5876__auto___14096)){
args__5882__auto__.push((arguments[i__5877__auto___14097]));

var G__14098 = (i__5877__auto___14097 + (1));
i__5877__auto___14097 = G__14098;
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
(shadow.dom.svg.cljs$lang$applyTo = (function (seq12790){
var G__12791 = cljs.core.first(seq12790);
var seq12790__$1 = cljs.core.next(seq12790);
var self__5861__auto__ = this;
return self__5861__auto__.cljs$core$IFn$_invoke$arity$variadic(G__12791,seq12790__$1);
}));


//# sourceMappingURL=shadow.dom.js.map
