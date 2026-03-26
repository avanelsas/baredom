goog.provide('shadow.dom');
shadow.dom.transition_supported_QMARK_ = true;

/**
 * @interface
 */
shadow.dom.IElement = function(){};

var shadow$dom$IElement$_to_dom$dyn_12851 = (function (this$){
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
return shadow$dom$IElement$_to_dom$dyn_12851(this$);
}
});


/**
 * @interface
 */
shadow.dom.SVGElement = function(){};

var shadow$dom$SVGElement$_to_svg$dyn_12854 = (function (this$){
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
return shadow$dom$SVGElement$_to_svg$dyn_12854(this$);
}
});

shadow.dom.lazy_native_coll_seq = (function shadow$dom$lazy_native_coll_seq(coll,idx){
if((idx < coll.length)){
return (new cljs.core.LazySeq(null,(function (){
return cljs.core.cons((coll[idx]),(function (){var G__11771 = coll;
var G__11772 = (idx + (1));
return (shadow.dom.lazy_native_coll_seq.cljs$core$IFn$_invoke$arity$2 ? shadow.dom.lazy_native_coll_seq.cljs$core$IFn$_invoke$arity$2(G__11771,G__11772) : shadow.dom.lazy_native_coll_seq.call(null,G__11771,G__11772));
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
var G__11804 = arguments.length;
switch (G__11804) {
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
var G__11814 = arguments.length;
switch (G__11814) {
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
var G__11817 = arguments.length;
switch (G__11817) {
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
var G__11827 = arguments.length;
switch (G__11827) {
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
var G__11842 = arguments.length;
switch (G__11842) {
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
var G__11859 = arguments.length;
switch (G__11859) {
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
}catch (e11902){if((e11902 instanceof Object)){
var e = e11902;
return console.log("didnt support attachEvent",el,e);
} else {
throw e11902;

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
var seq__11906 = cljs.core.seq(shadow.dom.query.cljs$core$IFn$_invoke$arity$2(selector,root_el));
var chunk__11907 = null;
var count__11908 = (0);
var i__11909 = (0);
while(true){
if((i__11909 < count__11908)){
var el = chunk__11907.cljs$core$IIndexed$_nth$arity$2(null,i__11909);
var handler_12908__$1 = ((function (seq__11906,chunk__11907,count__11908,i__11909,el){
return (function (e){
return (handler.cljs$core$IFn$_invoke$arity$2 ? handler.cljs$core$IFn$_invoke$arity$2(e,el) : handler.call(null,e,el));
});})(seq__11906,chunk__11907,count__11908,i__11909,el))
;
shadow.dom.dom_listen(el,cljs.core.name(ev),handler_12908__$1);


var G__12911 = seq__11906;
var G__12912 = chunk__11907;
var G__12913 = count__11908;
var G__12914 = (i__11909 + (1));
seq__11906 = G__12911;
chunk__11907 = G__12912;
count__11908 = G__12913;
i__11909 = G__12914;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__11906);
if(temp__5823__auto__){
var seq__11906__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__11906__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__11906__$1);
var G__12917 = cljs.core.chunk_rest(seq__11906__$1);
var G__12918 = c__5673__auto__;
var G__12919 = cljs.core.count(c__5673__auto__);
var G__12920 = (0);
seq__11906 = G__12917;
chunk__11907 = G__12918;
count__11908 = G__12919;
i__11909 = G__12920;
continue;
} else {
var el = cljs.core.first(seq__11906__$1);
var handler_12923__$1 = ((function (seq__11906,chunk__11907,count__11908,i__11909,el,seq__11906__$1,temp__5823__auto__){
return (function (e){
return (handler.cljs$core$IFn$_invoke$arity$2 ? handler.cljs$core$IFn$_invoke$arity$2(e,el) : handler.call(null,e,el));
});})(seq__11906,chunk__11907,count__11908,i__11909,el,seq__11906__$1,temp__5823__auto__))
;
shadow.dom.dom_listen(el,cljs.core.name(ev),handler_12923__$1);


var G__12924 = cljs.core.next(seq__11906__$1);
var G__12925 = null;
var G__12926 = (0);
var G__12927 = (0);
seq__11906 = G__12924;
chunk__11907 = G__12925;
count__11908 = G__12926;
i__11909 = G__12927;
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
var G__11934 = arguments.length;
switch (G__11934) {
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
var seq__11942 = cljs.core.seq(events);
var chunk__11943 = null;
var count__11944 = (0);
var i__11945 = (0);
while(true){
if((i__11945 < count__11944)){
var vec__11962 = chunk__11943.cljs$core$IIndexed$_nth$arity$2(null,i__11945);
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11962,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11962,(1),null);
shadow.dom.on.cljs$core$IFn$_invoke$arity$3(el,k,v);


var G__12948 = seq__11942;
var G__12949 = chunk__11943;
var G__12950 = count__11944;
var G__12951 = (i__11945 + (1));
seq__11942 = G__12948;
chunk__11943 = G__12949;
count__11944 = G__12950;
i__11945 = G__12951;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__11942);
if(temp__5823__auto__){
var seq__11942__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__11942__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__11942__$1);
var G__12956 = cljs.core.chunk_rest(seq__11942__$1);
var G__12957 = c__5673__auto__;
var G__12958 = cljs.core.count(c__5673__auto__);
var G__12959 = (0);
seq__11942 = G__12956;
chunk__11943 = G__12957;
count__11944 = G__12958;
i__11945 = G__12959;
continue;
} else {
var vec__11971 = cljs.core.first(seq__11942__$1);
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11971,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__11971,(1),null);
shadow.dom.on.cljs$core$IFn$_invoke$arity$3(el,k,v);


var G__12962 = cljs.core.next(seq__11942__$1);
var G__12963 = null;
var G__12964 = (0);
var G__12965 = (0);
seq__11942 = G__12962;
chunk__11943 = G__12963;
count__11944 = G__12964;
i__11945 = G__12965;
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
var seq__11981 = cljs.core.seq(styles);
var chunk__11982 = null;
var count__11983 = (0);
var i__11984 = (0);
while(true){
if((i__11984 < count__11983)){
var vec__12011 = chunk__11982.cljs$core$IIndexed$_nth$arity$2(null,i__11984);
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12011,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12011,(1),null);
goog.style.setStyle(dom,cljs.core.name(k),(((v == null))?"":v));


var G__12967 = seq__11981;
var G__12968 = chunk__11982;
var G__12969 = count__11983;
var G__12970 = (i__11984 + (1));
seq__11981 = G__12967;
chunk__11982 = G__12968;
count__11983 = G__12969;
i__11984 = G__12970;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__11981);
if(temp__5823__auto__){
var seq__11981__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__11981__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__11981__$1);
var G__12972 = cljs.core.chunk_rest(seq__11981__$1);
var G__12973 = c__5673__auto__;
var G__12974 = cljs.core.count(c__5673__auto__);
var G__12975 = (0);
seq__11981 = G__12972;
chunk__11982 = G__12973;
count__11983 = G__12974;
i__11984 = G__12975;
continue;
} else {
var vec__12026 = cljs.core.first(seq__11981__$1);
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12026,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12026,(1),null);
goog.style.setStyle(dom,cljs.core.name(k),(((v == null))?"":v));


var G__12977 = cljs.core.next(seq__11981__$1);
var G__12978 = null;
var G__12979 = (0);
var G__12980 = (0);
seq__11981 = G__12977;
chunk__11982 = G__12978;
count__11983 = G__12979;
i__11984 = G__12980;
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
var G__12045_12982 = key;
var G__12045_12983__$1 = (((G__12045_12982 instanceof cljs.core.Keyword))?G__12045_12982.fqn:null);
switch (G__12045_12983__$1) {
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
var ks_12990 = cljs.core.name(key);
if(cljs.core.truth_((function (){var or__5142__auto__ = goog.string.startsWith(ks_12990,"data-");
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return goog.string.startsWith(ks_12990,"aria-");
}
})())){
el.setAttribute(ks_12990,value);
} else {
(el[ks_12990] = value);
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
shadow.dom.create_dom_node = (function shadow$dom$create_dom_node(tag_def,p__12095){
var map__12097 = p__12095;
var map__12097__$1 = cljs.core.__destructure_map(map__12097);
var props = map__12097__$1;
var class$ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__12097__$1,new cljs.core.Keyword(null,"class","class",-2030961996));
var tag_props = ({});
var vec__12098 = shadow.dom.parse_tag(tag_def);
var tag_name = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12098,(0),null);
var tag_id = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12098,(1),null);
var tag_classes = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12098,(2),null);
if(cljs.core.truth_(tag_id)){
(tag_props["id"] = tag_id);
} else {
}

if(cljs.core.truth_(tag_classes)){
(tag_props["class"] = shadow.dom.merge_class_string(class$,tag_classes));
} else {
}

var G__12105 = goog.dom.createDom(tag_name,tag_props);
shadow.dom.set_attrs(G__12105,cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(props,new cljs.core.Keyword(null,"class","class",-2030961996)));

return G__12105;
});
shadow.dom.append = (function shadow$dom$append(var_args){
var G__12116 = arguments.length;
switch (G__12116) {
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

shadow.dom.destructure_node = (function shadow$dom$destructure_node(create_fn,p__12123){
var vec__12124 = p__12123;
var seq__12125 = cljs.core.seq(vec__12124);
var first__12126 = cljs.core.first(seq__12125);
var seq__12125__$1 = cljs.core.next(seq__12125);
var nn = first__12126;
var first__12126__$1 = cljs.core.first(seq__12125__$1);
var seq__12125__$2 = cljs.core.next(seq__12125__$1);
var np = first__12126__$1;
var nc = seq__12125__$2;
var node = vec__12124;
if((nn instanceof cljs.core.Keyword)){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("invalid dom node",new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"node","node",581201198),node], null));
}

if((((np == null)) && ((nc == null)))){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(function (){var G__12131 = nn;
var G__12132 = cljs.core.PersistentArrayMap.EMPTY;
return (create_fn.cljs$core$IFn$_invoke$arity$2 ? create_fn.cljs$core$IFn$_invoke$arity$2(G__12131,G__12132) : create_fn.call(null,G__12131,G__12132));
})(),cljs.core.List.EMPTY], null);
} else {
if(cljs.core.map_QMARK_(np)){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(create_fn.cljs$core$IFn$_invoke$arity$2 ? create_fn.cljs$core$IFn$_invoke$arity$2(nn,np) : create_fn.call(null,nn,np)),nc], null);
} else {
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(function (){var G__12135 = nn;
var G__12136 = cljs.core.PersistentArrayMap.EMPTY;
return (create_fn.cljs$core$IFn$_invoke$arity$2 ? create_fn.cljs$core$IFn$_invoke$arity$2(G__12135,G__12136) : create_fn.call(null,G__12135,G__12136));
})(),cljs.core.conj.cljs$core$IFn$_invoke$arity$2(nc,np)], null);

}
}
});
shadow.dom.make_dom_node = (function shadow$dom$make_dom_node(structure){
var vec__12140 = shadow.dom.destructure_node(shadow.dom.create_dom_node,structure);
var node = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12140,(0),null);
var node_children = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12140,(1),null);
var seq__12143_13034 = cljs.core.seq(node_children);
var chunk__12144_13035 = null;
var count__12145_13036 = (0);
var i__12146_13037 = (0);
while(true){
if((i__12146_13037 < count__12145_13036)){
var child_struct_13040 = chunk__12144_13035.cljs$core$IIndexed$_nth$arity$2(null,i__12146_13037);
var children_13041 = shadow.dom.dom_node(child_struct_13040);
if(cljs.core.seq_QMARK_(children_13041)){
var seq__12196_13043 = cljs.core.seq(cljs.core.map.cljs$core$IFn$_invoke$arity$2(shadow.dom.dom_node,children_13041));
var chunk__12198_13044 = null;
var count__12199_13045 = (0);
var i__12200_13046 = (0);
while(true){
if((i__12200_13046 < count__12199_13045)){
var child_13047 = chunk__12198_13044.cljs$core$IIndexed$_nth$arity$2(null,i__12200_13046);
if(cljs.core.truth_(child_13047)){
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,child_13047);


var G__13049 = seq__12196_13043;
var G__13050 = chunk__12198_13044;
var G__13051 = count__12199_13045;
var G__13052 = (i__12200_13046 + (1));
seq__12196_13043 = G__13049;
chunk__12198_13044 = G__13050;
count__12199_13045 = G__13051;
i__12200_13046 = G__13052;
continue;
} else {
var G__13054 = seq__12196_13043;
var G__13055 = chunk__12198_13044;
var G__13056 = count__12199_13045;
var G__13057 = (i__12200_13046 + (1));
seq__12196_13043 = G__13054;
chunk__12198_13044 = G__13055;
count__12199_13045 = G__13056;
i__12200_13046 = G__13057;
continue;
}
} else {
var temp__5823__auto___13058 = cljs.core.seq(seq__12196_13043);
if(temp__5823__auto___13058){
var seq__12196_13060__$1 = temp__5823__auto___13058;
if(cljs.core.chunked_seq_QMARK_(seq__12196_13060__$1)){
var c__5673__auto___13061 = cljs.core.chunk_first(seq__12196_13060__$1);
var G__13062 = cljs.core.chunk_rest(seq__12196_13060__$1);
var G__13063 = c__5673__auto___13061;
var G__13064 = cljs.core.count(c__5673__auto___13061);
var G__13065 = (0);
seq__12196_13043 = G__13062;
chunk__12198_13044 = G__13063;
count__12199_13045 = G__13064;
i__12200_13046 = G__13065;
continue;
} else {
var child_13067 = cljs.core.first(seq__12196_13060__$1);
if(cljs.core.truth_(child_13067)){
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,child_13067);


var G__13068 = cljs.core.next(seq__12196_13060__$1);
var G__13069 = null;
var G__13072 = (0);
var G__13073 = (0);
seq__12196_13043 = G__13068;
chunk__12198_13044 = G__13069;
count__12199_13045 = G__13072;
i__12200_13046 = G__13073;
continue;
} else {
var G__13075 = cljs.core.next(seq__12196_13060__$1);
var G__13076 = null;
var G__13077 = (0);
var G__13078 = (0);
seq__12196_13043 = G__13075;
chunk__12198_13044 = G__13076;
count__12199_13045 = G__13077;
i__12200_13046 = G__13078;
continue;
}
}
} else {
}
}
break;
}
} else {
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,children_13041);
}


var G__13081 = seq__12143_13034;
var G__13082 = chunk__12144_13035;
var G__13083 = count__12145_13036;
var G__13084 = (i__12146_13037 + (1));
seq__12143_13034 = G__13081;
chunk__12144_13035 = G__13082;
count__12145_13036 = G__13083;
i__12146_13037 = G__13084;
continue;
} else {
var temp__5823__auto___13086 = cljs.core.seq(seq__12143_13034);
if(temp__5823__auto___13086){
var seq__12143_13091__$1 = temp__5823__auto___13086;
if(cljs.core.chunked_seq_QMARK_(seq__12143_13091__$1)){
var c__5673__auto___13092 = cljs.core.chunk_first(seq__12143_13091__$1);
var G__13093 = cljs.core.chunk_rest(seq__12143_13091__$1);
var G__13094 = c__5673__auto___13092;
var G__13095 = cljs.core.count(c__5673__auto___13092);
var G__13096 = (0);
seq__12143_13034 = G__13093;
chunk__12144_13035 = G__13094;
count__12145_13036 = G__13095;
i__12146_13037 = G__13096;
continue;
} else {
var child_struct_13100 = cljs.core.first(seq__12143_13091__$1);
var children_13101 = shadow.dom.dom_node(child_struct_13100);
if(cljs.core.seq_QMARK_(children_13101)){
var seq__12264_13102 = cljs.core.seq(cljs.core.map.cljs$core$IFn$_invoke$arity$2(shadow.dom.dom_node,children_13101));
var chunk__12266_13103 = null;
var count__12267_13104 = (0);
var i__12268_13105 = (0);
while(true){
if((i__12268_13105 < count__12267_13104)){
var child_13107 = chunk__12266_13103.cljs$core$IIndexed$_nth$arity$2(null,i__12268_13105);
if(cljs.core.truth_(child_13107)){
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,child_13107);


var G__13109 = seq__12264_13102;
var G__13110 = chunk__12266_13103;
var G__13111 = count__12267_13104;
var G__13112 = (i__12268_13105 + (1));
seq__12264_13102 = G__13109;
chunk__12266_13103 = G__13110;
count__12267_13104 = G__13111;
i__12268_13105 = G__13112;
continue;
} else {
var G__13114 = seq__12264_13102;
var G__13115 = chunk__12266_13103;
var G__13116 = count__12267_13104;
var G__13117 = (i__12268_13105 + (1));
seq__12264_13102 = G__13114;
chunk__12266_13103 = G__13115;
count__12267_13104 = G__13116;
i__12268_13105 = G__13117;
continue;
}
} else {
var temp__5823__auto___13118__$1 = cljs.core.seq(seq__12264_13102);
if(temp__5823__auto___13118__$1){
var seq__12264_13120__$1 = temp__5823__auto___13118__$1;
if(cljs.core.chunked_seq_QMARK_(seq__12264_13120__$1)){
var c__5673__auto___13121 = cljs.core.chunk_first(seq__12264_13120__$1);
var G__13123 = cljs.core.chunk_rest(seq__12264_13120__$1);
var G__13124 = c__5673__auto___13121;
var G__13125 = cljs.core.count(c__5673__auto___13121);
var G__13126 = (0);
seq__12264_13102 = G__13123;
chunk__12266_13103 = G__13124;
count__12267_13104 = G__13125;
i__12268_13105 = G__13126;
continue;
} else {
var child_13128 = cljs.core.first(seq__12264_13120__$1);
if(cljs.core.truth_(child_13128)){
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,child_13128);


var G__13129 = cljs.core.next(seq__12264_13120__$1);
var G__13130 = null;
var G__13131 = (0);
var G__13132 = (0);
seq__12264_13102 = G__13129;
chunk__12266_13103 = G__13130;
count__12267_13104 = G__13131;
i__12268_13105 = G__13132;
continue;
} else {
var G__13133 = cljs.core.next(seq__12264_13120__$1);
var G__13134 = null;
var G__13135 = (0);
var G__13136 = (0);
seq__12264_13102 = G__13133;
chunk__12266_13103 = G__13134;
count__12267_13104 = G__13135;
i__12268_13105 = G__13136;
continue;
}
}
} else {
}
}
break;
}
} else {
shadow.dom.append.cljs$core$IFn$_invoke$arity$2(node,children_13101);
}


var G__13137 = cljs.core.next(seq__12143_13091__$1);
var G__13138 = null;
var G__13139 = (0);
var G__13140 = (0);
seq__12143_13034 = G__13137;
chunk__12144_13035 = G__13138;
count__12145_13036 = G__13139;
i__12146_13037 = G__13140;
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
var seq__12348 = cljs.core.seq(node);
var chunk__12349 = null;
var count__12350 = (0);
var i__12351 = (0);
while(true){
if((i__12351 < count__12350)){
var n = chunk__12349.cljs$core$IIndexed$_nth$arity$2(null,i__12351);
(shadow.dom.remove.cljs$core$IFn$_invoke$arity$1 ? shadow.dom.remove.cljs$core$IFn$_invoke$arity$1(n) : shadow.dom.remove.call(null,n));


var G__13158 = seq__12348;
var G__13159 = chunk__12349;
var G__13160 = count__12350;
var G__13161 = (i__12351 + (1));
seq__12348 = G__13158;
chunk__12349 = G__13159;
count__12350 = G__13160;
i__12351 = G__13161;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__12348);
if(temp__5823__auto__){
var seq__12348__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__12348__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__12348__$1);
var G__13162 = cljs.core.chunk_rest(seq__12348__$1);
var G__13163 = c__5673__auto__;
var G__13164 = cljs.core.count(c__5673__auto__);
var G__13165 = (0);
seq__12348 = G__13162;
chunk__12349 = G__13163;
count__12350 = G__13164;
i__12351 = G__13165;
continue;
} else {
var n = cljs.core.first(seq__12348__$1);
(shadow.dom.remove.cljs$core$IFn$_invoke$arity$1 ? shadow.dom.remove.cljs$core$IFn$_invoke$arity$1(n) : shadow.dom.remove.call(null,n));


var G__13166 = cljs.core.next(seq__12348__$1);
var G__13167 = null;
var G__13168 = (0);
var G__13169 = (0);
seq__12348 = G__13166;
chunk__12349 = G__13167;
count__12350 = G__13168;
i__12351 = G__13169;
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
var G__12372 = arguments.length;
switch (G__12372) {
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
var G__12381 = arguments.length;
switch (G__12381) {
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
var G__12391 = arguments.length;
switch (G__12391) {
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
var len__5876__auto___13206 = arguments.length;
var i__5877__auto___13207 = (0);
while(true){
if((i__5877__auto___13207 < len__5876__auto___13206)){
args__5882__auto__.push((arguments[i__5877__auto___13207]));

var G__13208 = (i__5877__auto___13207 + (1));
i__5877__auto___13207 = G__13208;
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
var seq__12404_13214 = cljs.core.seq(nodes);
var chunk__12405_13215 = null;
var count__12406_13216 = (0);
var i__12407_13217 = (0);
while(true){
if((i__12407_13217 < count__12406_13216)){
var node_13219 = chunk__12405_13215.cljs$core$IIndexed$_nth$arity$2(null,i__12407_13217);
fragment.appendChild(shadow.dom._to_dom(node_13219));


var G__13221 = seq__12404_13214;
var G__13222 = chunk__12405_13215;
var G__13223 = count__12406_13216;
var G__13224 = (i__12407_13217 + (1));
seq__12404_13214 = G__13221;
chunk__12405_13215 = G__13222;
count__12406_13216 = G__13223;
i__12407_13217 = G__13224;
continue;
} else {
var temp__5823__auto___13231 = cljs.core.seq(seq__12404_13214);
if(temp__5823__auto___13231){
var seq__12404_13232__$1 = temp__5823__auto___13231;
if(cljs.core.chunked_seq_QMARK_(seq__12404_13232__$1)){
var c__5673__auto___13233 = cljs.core.chunk_first(seq__12404_13232__$1);
var G__13236 = cljs.core.chunk_rest(seq__12404_13232__$1);
var G__13237 = c__5673__auto___13233;
var G__13238 = cljs.core.count(c__5673__auto___13233);
var G__13239 = (0);
seq__12404_13214 = G__13236;
chunk__12405_13215 = G__13237;
count__12406_13216 = G__13238;
i__12407_13217 = G__13239;
continue;
} else {
var node_13241 = cljs.core.first(seq__12404_13232__$1);
fragment.appendChild(shadow.dom._to_dom(node_13241));


var G__13242 = cljs.core.next(seq__12404_13232__$1);
var G__13243 = null;
var G__13244 = (0);
var G__13245 = (0);
seq__12404_13214 = G__13242;
chunk__12405_13215 = G__13243;
count__12406_13216 = G__13244;
i__12407_13217 = G__13245;
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
(shadow.dom.fragment.cljs$lang$applyTo = (function (seq12402){
var self__5862__auto__ = this;
return self__5862__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq(seq12402));
}));

/**
 * given a html string, eval all <script> tags and return the html without the scripts
 * don't do this for everything, only content you trust.
 */
shadow.dom.eval_scripts = (function shadow$dom$eval_scripts(s){
var scripts = cljs.core.re_seq(/<script[^>]*?>(.+?)<\/script>/,s);
var seq__12413_13251 = cljs.core.seq(scripts);
var chunk__12414_13252 = null;
var count__12415_13253 = (0);
var i__12416_13254 = (0);
while(true){
if((i__12416_13254 < count__12415_13253)){
var vec__12428_13256 = chunk__12414_13252.cljs$core$IIndexed$_nth$arity$2(null,i__12416_13254);
var script_tag_13257 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12428_13256,(0),null);
var script_body_13258 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12428_13256,(1),null);
eval(script_body_13258);


var G__13259 = seq__12413_13251;
var G__13260 = chunk__12414_13252;
var G__13261 = count__12415_13253;
var G__13262 = (i__12416_13254 + (1));
seq__12413_13251 = G__13259;
chunk__12414_13252 = G__13260;
count__12415_13253 = G__13261;
i__12416_13254 = G__13262;
continue;
} else {
var temp__5823__auto___13265 = cljs.core.seq(seq__12413_13251);
if(temp__5823__auto___13265){
var seq__12413_13266__$1 = temp__5823__auto___13265;
if(cljs.core.chunked_seq_QMARK_(seq__12413_13266__$1)){
var c__5673__auto___13267 = cljs.core.chunk_first(seq__12413_13266__$1);
var G__13268 = cljs.core.chunk_rest(seq__12413_13266__$1);
var G__13269 = c__5673__auto___13267;
var G__13270 = cljs.core.count(c__5673__auto___13267);
var G__13271 = (0);
seq__12413_13251 = G__13268;
chunk__12414_13252 = G__13269;
count__12415_13253 = G__13270;
i__12416_13254 = G__13271;
continue;
} else {
var vec__12437_13275 = cljs.core.first(seq__12413_13266__$1);
var script_tag_13276 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12437_13275,(0),null);
var script_body_13277 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12437_13275,(1),null);
eval(script_body_13277);


var G__13278 = cljs.core.next(seq__12413_13266__$1);
var G__13279 = null;
var G__13280 = (0);
var G__13281 = (0);
seq__12413_13251 = G__13278;
chunk__12414_13252 = G__13279;
count__12415_13253 = G__13280;
i__12416_13254 = G__13281;
continue;
}
} else {
}
}
break;
}

return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3((function (s__$1,p__12445){
var vec__12446 = p__12445;
var script_tag = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12446,(0),null);
var script_body = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12446,(1),null);
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
var G__12465 = arguments.length;
switch (G__12465) {
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
var seq__12494 = cljs.core.seq(style_keys);
var chunk__12495 = null;
var count__12496 = (0);
var i__12497 = (0);
while(true){
if((i__12497 < count__12496)){
var it = chunk__12495.cljs$core$IIndexed$_nth$arity$2(null,i__12497);
shadow.dom.remove_style_STAR_(el__$1,it);


var G__13299 = seq__12494;
var G__13300 = chunk__12495;
var G__13301 = count__12496;
var G__13302 = (i__12497 + (1));
seq__12494 = G__13299;
chunk__12495 = G__13300;
count__12496 = G__13301;
i__12497 = G__13302;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__12494);
if(temp__5823__auto__){
var seq__12494__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__12494__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__12494__$1);
var G__13303 = cljs.core.chunk_rest(seq__12494__$1);
var G__13304 = c__5673__auto__;
var G__13305 = cljs.core.count(c__5673__auto__);
var G__13306 = (0);
seq__12494 = G__13303;
chunk__12495 = G__13304;
count__12496 = G__13305;
i__12497 = G__13306;
continue;
} else {
var it = cljs.core.first(seq__12494__$1);
shadow.dom.remove_style_STAR_(el__$1,it);


var G__13309 = cljs.core.next(seq__12494__$1);
var G__13310 = null;
var G__13312 = (0);
var G__13313 = (0);
seq__12494 = G__13309;
chunk__12495 = G__13310;
count__12496 = G__13312;
i__12497 = G__13313;
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

(shadow.dom.Coordinate.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__5450__auto__,k12504,else__5451__auto__){
var self__ = this;
var this__5450__auto____$1 = this;
var G__12512 = k12504;
var G__12512__$1 = (((G__12512 instanceof cljs.core.Keyword))?G__12512.fqn:null);
switch (G__12512__$1) {
case "x":
return self__.x;

break;
case "y":
return self__.y;

break;
default:
return cljs.core.get.cljs$core$IFn$_invoke$arity$3(self__.__extmap,k12504,else__5451__auto__);

}
}));

(shadow.dom.Coordinate.prototype.cljs$core$IKVReduce$_kv_reduce$arity$3 = (function (this__5468__auto__,f__5469__auto__,init__5470__auto__){
var self__ = this;
var this__5468__auto____$1 = this;
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3((function (ret__5471__auto__,p__12513){
var vec__12514 = p__12513;
var k__5472__auto__ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12514,(0),null);
var v__5473__auto__ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12514,(1),null);
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

(shadow.dom.Coordinate.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__12503){
var self__ = this;
var G__12503__$1 = this;
return (new cljs.core.RecordIter((0),G__12503__$1,2,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"x","x",2099068185),new cljs.core.Keyword(null,"y","y",-1757859776)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator(self__.__extmap):cljs.core.nil_iter())));
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

(shadow.dom.Coordinate.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this12505,other12506){
var self__ = this;
var this12505__$1 = this;
return (((!((other12506 == null)))) && ((((this12505__$1.constructor === other12506.constructor)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this12505__$1.x,other12506.x)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this12505__$1.y,other12506.y)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this12505__$1.__extmap,other12506.__extmap)))))))));
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

(shadow.dom.Coordinate.prototype.cljs$core$IAssociative$_contains_key_QMARK_$arity$2 = (function (this__5455__auto__,k12504){
var self__ = this;
var this__5455__auto____$1 = this;
var G__12522 = k12504;
var G__12522__$1 = (((G__12522 instanceof cljs.core.Keyword))?G__12522.fqn:null);
switch (G__12522__$1) {
case "x":
case "y":
return true;

break;
default:
return cljs.core.contains_QMARK_(self__.__extmap,k12504);

}
}));

(shadow.dom.Coordinate.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__5456__auto__,k__5457__auto__,G__12503){
var self__ = this;
var this__5456__auto____$1 = this;
var pred__12525 = cljs.core.keyword_identical_QMARK_;
var expr__12526 = k__5457__auto__;
if(cljs.core.truth_((pred__12525.cljs$core$IFn$_invoke$arity$2 ? pred__12525.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"x","x",2099068185),expr__12526) : pred__12525.call(null,new cljs.core.Keyword(null,"x","x",2099068185),expr__12526)))){
return (new shadow.dom.Coordinate(G__12503,self__.y,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_((pred__12525.cljs$core$IFn$_invoke$arity$2 ? pred__12525.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"y","y",-1757859776),expr__12526) : pred__12525.call(null,new cljs.core.Keyword(null,"y","y",-1757859776),expr__12526)))){
return (new shadow.dom.Coordinate(self__.x,G__12503,self__.__meta,self__.__extmap,null));
} else {
return (new shadow.dom.Coordinate(self__.x,self__.y,self__.__meta,cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(self__.__extmap,k__5457__auto__,G__12503),null));
}
}
}));

(shadow.dom.Coordinate.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__5461__auto__){
var self__ = this;
var this__5461__auto____$1 = this;
return cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.MapEntry(new cljs.core.Keyword(null,"x","x",2099068185),self__.x,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"y","y",-1757859776),self__.y,null))], null),self__.__extmap));
}));

(shadow.dom.Coordinate.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__5447__auto__,G__12503){
var self__ = this;
var this__5447__auto____$1 = this;
return (new shadow.dom.Coordinate(self__.x,self__.y,G__12503,self__.__extmap,self__.__hash));
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
shadow.dom.map__GT_Coordinate = (function shadow$dom$map__GT_Coordinate(G__12508){
var extmap__5490__auto__ = (function (){var G__12540 = cljs.core.dissoc.cljs$core$IFn$_invoke$arity$variadic(G__12508,new cljs.core.Keyword(null,"x","x",2099068185),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"y","y",-1757859776)], 0));
if(cljs.core.record_QMARK_(G__12508)){
return cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,G__12540);
} else {
return G__12540;
}
})();
return (new shadow.dom.Coordinate(new cljs.core.Keyword(null,"x","x",2099068185).cljs$core$IFn$_invoke$arity$1(G__12508),new cljs.core.Keyword(null,"y","y",-1757859776).cljs$core$IFn$_invoke$arity$1(G__12508),null,cljs.core.not_empty(extmap__5490__auto__),null));
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

(shadow.dom.Size.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__5450__auto__,k12554,else__5451__auto__){
var self__ = this;
var this__5450__auto____$1 = this;
var G__12563 = k12554;
var G__12563__$1 = (((G__12563 instanceof cljs.core.Keyword))?G__12563.fqn:null);
switch (G__12563__$1) {
case "w":
return self__.w;

break;
case "h":
return self__.h;

break;
default:
return cljs.core.get.cljs$core$IFn$_invoke$arity$3(self__.__extmap,k12554,else__5451__auto__);

}
}));

(shadow.dom.Size.prototype.cljs$core$IKVReduce$_kv_reduce$arity$3 = (function (this__5468__auto__,f__5469__auto__,init__5470__auto__){
var self__ = this;
var this__5468__auto____$1 = this;
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3((function (ret__5471__auto__,p__12565){
var vec__12566 = p__12565;
var k__5472__auto__ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12566,(0),null);
var v__5473__auto__ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12566,(1),null);
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

(shadow.dom.Size.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__12553){
var self__ = this;
var G__12553__$1 = this;
return (new cljs.core.RecordIter((0),G__12553__$1,2,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"w","w",354169001),new cljs.core.Keyword(null,"h","h",1109658740)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator(self__.__extmap):cljs.core.nil_iter())));
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

(shadow.dom.Size.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this12555,other12556){
var self__ = this;
var this12555__$1 = this;
return (((!((other12556 == null)))) && ((((this12555__$1.constructor === other12556.constructor)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this12555__$1.w,other12556.w)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this12555__$1.h,other12556.h)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this12555__$1.__extmap,other12556.__extmap)))))))));
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

(shadow.dom.Size.prototype.cljs$core$IAssociative$_contains_key_QMARK_$arity$2 = (function (this__5455__auto__,k12554){
var self__ = this;
var this__5455__auto____$1 = this;
var G__12584 = k12554;
var G__12584__$1 = (((G__12584 instanceof cljs.core.Keyword))?G__12584.fqn:null);
switch (G__12584__$1) {
case "w":
case "h":
return true;

break;
default:
return cljs.core.contains_QMARK_(self__.__extmap,k12554);

}
}));

(shadow.dom.Size.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__5456__auto__,k__5457__auto__,G__12553){
var self__ = this;
var this__5456__auto____$1 = this;
var pred__12585 = cljs.core.keyword_identical_QMARK_;
var expr__12586 = k__5457__auto__;
if(cljs.core.truth_((pred__12585.cljs$core$IFn$_invoke$arity$2 ? pred__12585.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"w","w",354169001),expr__12586) : pred__12585.call(null,new cljs.core.Keyword(null,"w","w",354169001),expr__12586)))){
return (new shadow.dom.Size(G__12553,self__.h,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_((pred__12585.cljs$core$IFn$_invoke$arity$2 ? pred__12585.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"h","h",1109658740),expr__12586) : pred__12585.call(null,new cljs.core.Keyword(null,"h","h",1109658740),expr__12586)))){
return (new shadow.dom.Size(self__.w,G__12553,self__.__meta,self__.__extmap,null));
} else {
return (new shadow.dom.Size(self__.w,self__.h,self__.__meta,cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(self__.__extmap,k__5457__auto__,G__12553),null));
}
}
}));

(shadow.dom.Size.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__5461__auto__){
var self__ = this;
var this__5461__auto____$1 = this;
return cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.MapEntry(new cljs.core.Keyword(null,"w","w",354169001),self__.w,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"h","h",1109658740),self__.h,null))], null),self__.__extmap));
}));

(shadow.dom.Size.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__5447__auto__,G__12553){
var self__ = this;
var this__5447__auto____$1 = this;
return (new shadow.dom.Size(self__.w,self__.h,G__12553,self__.__extmap,self__.__hash));
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
shadow.dom.map__GT_Size = (function shadow$dom$map__GT_Size(G__12559){
var extmap__5490__auto__ = (function (){var G__12611 = cljs.core.dissoc.cljs$core$IFn$_invoke$arity$variadic(G__12559,new cljs.core.Keyword(null,"w","w",354169001),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"h","h",1109658740)], 0));
if(cljs.core.record_QMARK_(G__12559)){
return cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,G__12611);
} else {
return G__12611;
}
})();
return (new shadow.dom.Size(new cljs.core.Keyword(null,"w","w",354169001).cljs$core$IFn$_invoke$arity$1(G__12559),new cljs.core.Keyword(null,"h","h",1109658740).cljs$core$IFn$_invoke$arity$1(G__12559),null,cljs.core.not_empty(extmap__5490__auto__),null));
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
var G__13563 = (i + (1));
var G__13564 = cljs.core.conj.cljs$core$IFn$_invoke$arity$2(ret,(opts[i]["value"]));
i = G__13563;
ret = G__13564;
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
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(path)+"?"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(clojure.string.join.cljs$core$IFn$_invoke$arity$2("&",cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p__12661){
var vec__12662 = p__12661;
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12662,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12662,(1),null);
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name(k))+"="+cljs.core.str.cljs$core$IFn$_invoke$arity$1(encodeURIComponent((""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)))));
}),query_params))));
}
});
shadow.dom.redirect = (function shadow$dom$redirect(var_args){
var G__12672 = arguments.length;
switch (G__12672) {
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
var G__13581 = ps;
var G__13582 = (i + (1));
el__$1 = G__13581;
i = G__13582;
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
var vec__12701 = shadow.dom.parse_tag(tag_def);
var tag_name = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12701,(0),null);
var tag_id = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12701,(1),null);
var tag_classes = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12701,(2),null);
var el = document.createElementNS("http://www.w3.org/2000/svg",tag_name);
if(cljs.core.truth_(tag_id)){
el.setAttribute("id",tag_id);
} else {
}

if(cljs.core.truth_(tag_classes)){
el.setAttribute("class",shadow.dom.merge_class_string(new cljs.core.Keyword(null,"class","class",-2030961996).cljs$core$IFn$_invoke$arity$1(props),tag_classes));
} else {
}

var seq__12709_13673 = cljs.core.seq(props);
var chunk__12710_13674 = null;
var count__12711_13675 = (0);
var i__12712_13676 = (0);
while(true){
if((i__12712_13676 < count__12711_13675)){
var vec__12725_13677 = chunk__12710_13674.cljs$core$IIndexed$_nth$arity$2(null,i__12712_13676);
var k_13678 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12725_13677,(0),null);
var v_13679 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12725_13677,(1),null);
el.setAttributeNS((function (){var temp__5823__auto__ = cljs.core.namespace(k_13678);
if(cljs.core.truth_(temp__5823__auto__)){
var ns = temp__5823__auto__;
return cljs.core.get.cljs$core$IFn$_invoke$arity$2(cljs.core.deref(shadow.dom.xmlns),ns);
} else {
return null;
}
})(),cljs.core.name(k_13678),v_13679);


var G__13692 = seq__12709_13673;
var G__13693 = chunk__12710_13674;
var G__13694 = count__12711_13675;
var G__13695 = (i__12712_13676 + (1));
seq__12709_13673 = G__13692;
chunk__12710_13674 = G__13693;
count__12711_13675 = G__13694;
i__12712_13676 = G__13695;
continue;
} else {
var temp__5823__auto___13696 = cljs.core.seq(seq__12709_13673);
if(temp__5823__auto___13696){
var seq__12709_13724__$1 = temp__5823__auto___13696;
if(cljs.core.chunked_seq_QMARK_(seq__12709_13724__$1)){
var c__5673__auto___13725 = cljs.core.chunk_first(seq__12709_13724__$1);
var G__13727 = cljs.core.chunk_rest(seq__12709_13724__$1);
var G__13728 = c__5673__auto___13725;
var G__13729 = cljs.core.count(c__5673__auto___13725);
var G__13730 = (0);
seq__12709_13673 = G__13727;
chunk__12710_13674 = G__13728;
count__12711_13675 = G__13729;
i__12712_13676 = G__13730;
continue;
} else {
var vec__12732_13731 = cljs.core.first(seq__12709_13724__$1);
var k_13732 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12732_13731,(0),null);
var v_13733 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12732_13731,(1),null);
el.setAttributeNS((function (){var temp__5823__auto____$1 = cljs.core.namespace(k_13732);
if(cljs.core.truth_(temp__5823__auto____$1)){
var ns = temp__5823__auto____$1;
return cljs.core.get.cljs$core$IFn$_invoke$arity$2(cljs.core.deref(shadow.dom.xmlns),ns);
} else {
return null;
}
})(),cljs.core.name(k_13732),v_13733);


var G__13734 = cljs.core.next(seq__12709_13724__$1);
var G__13735 = null;
var G__13736 = (0);
var G__13737 = (0);
seq__12709_13673 = G__13734;
chunk__12710_13674 = G__13735;
count__12711_13675 = G__13736;
i__12712_13676 = G__13737;
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
var vec__12751 = shadow.dom.destructure_node(shadow.dom.create_svg_node,structure);
var node = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12751,(0),null);
var node_children = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__12751,(1),null);
var seq__12755_13744 = cljs.core.seq(node_children);
var chunk__12757_13745 = null;
var count__12758_13746 = (0);
var i__12759_13747 = (0);
while(true){
if((i__12759_13747 < count__12758_13746)){
var child_struct_13775 = chunk__12757_13745.cljs$core$IIndexed$_nth$arity$2(null,i__12759_13747);
if((!((child_struct_13775 == null)))){
if(typeof child_struct_13775 === 'string'){
var text_13776 = (node["textContent"]);
(node["textContent"] = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(text_13776)+cljs.core.str.cljs$core$IFn$_invoke$arity$1(child_struct_13775)));
} else {
var children_13777 = shadow.dom.svg_node(child_struct_13775);
if(cljs.core.seq_QMARK_(children_13777)){
var seq__12799_13778 = cljs.core.seq(children_13777);
var chunk__12801_13779 = null;
var count__12802_13780 = (0);
var i__12803_13781 = (0);
while(true){
if((i__12803_13781 < count__12802_13780)){
var child_13783 = chunk__12801_13779.cljs$core$IIndexed$_nth$arity$2(null,i__12803_13781);
if(cljs.core.truth_(child_13783)){
node.appendChild(child_13783);


var G__13784 = seq__12799_13778;
var G__13785 = chunk__12801_13779;
var G__13786 = count__12802_13780;
var G__13787 = (i__12803_13781 + (1));
seq__12799_13778 = G__13784;
chunk__12801_13779 = G__13785;
count__12802_13780 = G__13786;
i__12803_13781 = G__13787;
continue;
} else {
var G__13788 = seq__12799_13778;
var G__13789 = chunk__12801_13779;
var G__13790 = count__12802_13780;
var G__13791 = (i__12803_13781 + (1));
seq__12799_13778 = G__13788;
chunk__12801_13779 = G__13789;
count__12802_13780 = G__13790;
i__12803_13781 = G__13791;
continue;
}
} else {
var temp__5823__auto___13793 = cljs.core.seq(seq__12799_13778);
if(temp__5823__auto___13793){
var seq__12799_13796__$1 = temp__5823__auto___13793;
if(cljs.core.chunked_seq_QMARK_(seq__12799_13796__$1)){
var c__5673__auto___13798 = cljs.core.chunk_first(seq__12799_13796__$1);
var G__13799 = cljs.core.chunk_rest(seq__12799_13796__$1);
var G__13800 = c__5673__auto___13798;
var G__13801 = cljs.core.count(c__5673__auto___13798);
var G__13802 = (0);
seq__12799_13778 = G__13799;
chunk__12801_13779 = G__13800;
count__12802_13780 = G__13801;
i__12803_13781 = G__13802;
continue;
} else {
var child_13804 = cljs.core.first(seq__12799_13796__$1);
if(cljs.core.truth_(child_13804)){
node.appendChild(child_13804);


var G__13805 = cljs.core.next(seq__12799_13796__$1);
var G__13806 = null;
var G__13807 = (0);
var G__13808 = (0);
seq__12799_13778 = G__13805;
chunk__12801_13779 = G__13806;
count__12802_13780 = G__13807;
i__12803_13781 = G__13808;
continue;
} else {
var G__13809 = cljs.core.next(seq__12799_13796__$1);
var G__13810 = null;
var G__13811 = (0);
var G__13812 = (0);
seq__12799_13778 = G__13809;
chunk__12801_13779 = G__13810;
count__12802_13780 = G__13811;
i__12803_13781 = G__13812;
continue;
}
}
} else {
}
}
break;
}
} else {
node.appendChild(children_13777);
}
}


var G__13813 = seq__12755_13744;
var G__13814 = chunk__12757_13745;
var G__13815 = count__12758_13746;
var G__13816 = (i__12759_13747 + (1));
seq__12755_13744 = G__13813;
chunk__12757_13745 = G__13814;
count__12758_13746 = G__13815;
i__12759_13747 = G__13816;
continue;
} else {
var G__13817 = seq__12755_13744;
var G__13818 = chunk__12757_13745;
var G__13819 = count__12758_13746;
var G__13820 = (i__12759_13747 + (1));
seq__12755_13744 = G__13817;
chunk__12757_13745 = G__13818;
count__12758_13746 = G__13819;
i__12759_13747 = G__13820;
continue;
}
} else {
var temp__5823__auto___13821 = cljs.core.seq(seq__12755_13744);
if(temp__5823__auto___13821){
var seq__12755_13822__$1 = temp__5823__auto___13821;
if(cljs.core.chunked_seq_QMARK_(seq__12755_13822__$1)){
var c__5673__auto___13823 = cljs.core.chunk_first(seq__12755_13822__$1);
var G__13825 = cljs.core.chunk_rest(seq__12755_13822__$1);
var G__13826 = c__5673__auto___13823;
var G__13827 = cljs.core.count(c__5673__auto___13823);
var G__13828 = (0);
seq__12755_13744 = G__13825;
chunk__12757_13745 = G__13826;
count__12758_13746 = G__13827;
i__12759_13747 = G__13828;
continue;
} else {
var child_struct_13830 = cljs.core.first(seq__12755_13822__$1);
if((!((child_struct_13830 == null)))){
if(typeof child_struct_13830 === 'string'){
var text_13831 = (node["textContent"]);
(node["textContent"] = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(text_13831)+cljs.core.str.cljs$core$IFn$_invoke$arity$1(child_struct_13830)));
} else {
var children_13832 = shadow.dom.svg_node(child_struct_13830);
if(cljs.core.seq_QMARK_(children_13832)){
var seq__12813_13833 = cljs.core.seq(children_13832);
var chunk__12815_13834 = null;
var count__12816_13835 = (0);
var i__12817_13836 = (0);
while(true){
if((i__12817_13836 < count__12816_13835)){
var child_13839 = chunk__12815_13834.cljs$core$IIndexed$_nth$arity$2(null,i__12817_13836);
if(cljs.core.truth_(child_13839)){
node.appendChild(child_13839);


var G__13843 = seq__12813_13833;
var G__13844 = chunk__12815_13834;
var G__13845 = count__12816_13835;
var G__13846 = (i__12817_13836 + (1));
seq__12813_13833 = G__13843;
chunk__12815_13834 = G__13844;
count__12816_13835 = G__13845;
i__12817_13836 = G__13846;
continue;
} else {
var G__13848 = seq__12813_13833;
var G__13849 = chunk__12815_13834;
var G__13850 = count__12816_13835;
var G__13851 = (i__12817_13836 + (1));
seq__12813_13833 = G__13848;
chunk__12815_13834 = G__13849;
count__12816_13835 = G__13850;
i__12817_13836 = G__13851;
continue;
}
} else {
var temp__5823__auto___13852__$1 = cljs.core.seq(seq__12813_13833);
if(temp__5823__auto___13852__$1){
var seq__12813_13853__$1 = temp__5823__auto___13852__$1;
if(cljs.core.chunked_seq_QMARK_(seq__12813_13853__$1)){
var c__5673__auto___13854 = cljs.core.chunk_first(seq__12813_13853__$1);
var G__13855 = cljs.core.chunk_rest(seq__12813_13853__$1);
var G__13856 = c__5673__auto___13854;
var G__13857 = cljs.core.count(c__5673__auto___13854);
var G__13858 = (0);
seq__12813_13833 = G__13855;
chunk__12815_13834 = G__13856;
count__12816_13835 = G__13857;
i__12817_13836 = G__13858;
continue;
} else {
var child_13860 = cljs.core.first(seq__12813_13853__$1);
if(cljs.core.truth_(child_13860)){
node.appendChild(child_13860);


var G__13861 = cljs.core.next(seq__12813_13853__$1);
var G__13862 = null;
var G__13863 = (0);
var G__13864 = (0);
seq__12813_13833 = G__13861;
chunk__12815_13834 = G__13862;
count__12816_13835 = G__13863;
i__12817_13836 = G__13864;
continue;
} else {
var G__13865 = cljs.core.next(seq__12813_13853__$1);
var G__13866 = null;
var G__13867 = (0);
var G__13868 = (0);
seq__12813_13833 = G__13865;
chunk__12815_13834 = G__13866;
count__12816_13835 = G__13867;
i__12817_13836 = G__13868;
continue;
}
}
} else {
}
}
break;
}
} else {
node.appendChild(children_13832);
}
}


var G__13874 = cljs.core.next(seq__12755_13822__$1);
var G__13875 = null;
var G__13876 = (0);
var G__13877 = (0);
seq__12755_13744 = G__13874;
chunk__12757_13745 = G__13875;
count__12758_13746 = G__13876;
i__12759_13747 = G__13877;
continue;
} else {
var G__13882 = cljs.core.next(seq__12755_13822__$1);
var G__13883 = null;
var G__13884 = (0);
var G__13885 = (0);
seq__12755_13744 = G__13882;
chunk__12757_13745 = G__13883;
count__12758_13746 = G__13884;
i__12759_13747 = G__13885;
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
var len__5876__auto___13908 = arguments.length;
var i__5877__auto___13909 = (0);
while(true){
if((i__5877__auto___13909 < len__5876__auto___13908)){
args__5882__auto__.push((arguments[i__5877__auto___13909]));

var G__13910 = (i__5877__auto___13909 + (1));
i__5877__auto___13909 = G__13910;
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
(shadow.dom.svg.cljs$lang$applyTo = (function (seq12837){
var G__12839 = cljs.core.first(seq12837);
var seq12837__$1 = cljs.core.next(seq12837);
var self__5861__auto__ = this;
return self__5861__auto__.cljs$core$IFn$_invoke$arity$variadic(G__12839,seq12837__$1);
}));


//# sourceMappingURL=shadow.dom.js.map
