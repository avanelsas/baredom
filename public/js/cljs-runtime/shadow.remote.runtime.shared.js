goog.provide('shadow.remote.runtime.shared');
shadow.remote.runtime.shared.init_state = (function shadow$remote$runtime$shared$init_state(client_info){
return new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"extensions","extensions",-1103629196),cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"ops","ops",1237330063),cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"client-info","client-info",1958982504),client_info,new cljs.core.Keyword(null,"call-id-seq","call-id-seq",-1679248218),(0),new cljs.core.Keyword(null,"call-handlers","call-handlers",386605551),cljs.core.PersistentArrayMap.EMPTY], null);
});
shadow.remote.runtime.shared.now = (function shadow$remote$runtime$shared$now(){
return Date.now();
});
shadow.remote.runtime.shared.get_client_id = (function shadow$remote$runtime$shared$get_client_id(p__14443){
var map__14444 = p__14443;
var map__14444__$1 = cljs.core.__destructure_map(map__14444);
var runtime = map__14444__$1;
var state_ref = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14444__$1,new cljs.core.Keyword(null,"state-ref","state-ref",2127874952));
var or__5142__auto__ = new cljs.core.Keyword(null,"client-id","client-id",-464622140).cljs$core$IFn$_invoke$arity$1(cljs.core.deref(state_ref));
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("runtime has no assigned runtime-id",new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"runtime","runtime",-1331573996),runtime], null));
}
});
shadow.remote.runtime.shared.relay_msg = (function shadow$remote$runtime$shared$relay_msg(runtime,msg){
var self_id_14619 = shadow.remote.runtime.shared.get_client_id(runtime);
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"to","to",192099007).cljs$core$IFn$_invoke$arity$1(msg),self_id_14619)){
shadow.remote.runtime.api.relay_msg(runtime,msg);
} else {
Promise.resolve((1)).then((function (){
var G__14453 = runtime;
var G__14454 = cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(msg,new cljs.core.Keyword(null,"from","from",1815293044),self_id_14619);
return (shadow.remote.runtime.shared.process.cljs$core$IFn$_invoke$arity$2 ? shadow.remote.runtime.shared.process.cljs$core$IFn$_invoke$arity$2(G__14453,G__14454) : shadow.remote.runtime.shared.process.call(null,G__14453,G__14454));
}));
}

return msg;
});
shadow.remote.runtime.shared.reply = (function shadow$remote$runtime$shared$reply(runtime,p__14463,res){
var map__14464 = p__14463;
var map__14464__$1 = cljs.core.__destructure_map(map__14464);
var call_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14464__$1,new cljs.core.Keyword(null,"call-id","call-id",1043012968));
var from = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14464__$1,new cljs.core.Keyword(null,"from","from",1815293044));
var res__$1 = (function (){var G__14465 = res;
var G__14465__$1 = (cljs.core.truth_(call_id)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__14465,new cljs.core.Keyword(null,"call-id","call-id",1043012968),call_id):G__14465);
if(cljs.core.truth_(from)){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__14465__$1,new cljs.core.Keyword(null,"to","to",192099007),from);
} else {
return G__14465__$1;
}
})();
return shadow.remote.runtime.api.relay_msg(runtime,res__$1);
});
shadow.remote.runtime.shared.call = (function shadow$remote$runtime$shared$call(var_args){
var G__14468 = arguments.length;
switch (G__14468) {
case 3:
return shadow.remote.runtime.shared.call.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return shadow.remote.runtime.shared.call.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(shadow.remote.runtime.shared.call.cljs$core$IFn$_invoke$arity$3 = (function (runtime,msg,handlers){
return shadow.remote.runtime.shared.call.cljs$core$IFn$_invoke$arity$4(runtime,msg,handlers,(0));
}));

(shadow.remote.runtime.shared.call.cljs$core$IFn$_invoke$arity$4 = (function (p__14472,msg,handlers,timeout_after_ms){
var map__14473 = p__14472;
var map__14473__$1 = cljs.core.__destructure_map(map__14473);
var runtime = map__14473__$1;
var state_ref = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14473__$1,new cljs.core.Keyword(null,"state-ref","state-ref",2127874952));
if(cljs.core.map_QMARK_(msg)){
} else {
throw (new Error("Assert failed: (map? msg)"));
}

if(cljs.core.map_QMARK_(handlers)){
} else {
throw (new Error("Assert failed: (map? handlers)"));
}

if(cljs.core.nat_int_QMARK_(timeout_after_ms)){
} else {
throw (new Error("Assert failed: (nat-int? timeout-after-ms)"));
}

var call_id = new cljs.core.Keyword(null,"call-id-seq","call-id-seq",-1679248218).cljs$core$IFn$_invoke$arity$1(cljs.core.deref(state_ref));
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(state_ref,cljs.core.update,new cljs.core.Keyword(null,"call-id-seq","call-id-seq",-1679248218),cljs.core.inc);

cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(state_ref,cljs.core.assoc_in,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"call-handlers","call-handlers",386605551),call_id], null),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"handlers","handlers",79528781),handlers,new cljs.core.Keyword(null,"called-at","called-at",607081160),shadow.remote.runtime.shared.now(),new cljs.core.Keyword(null,"msg","msg",-1386103444),msg,new cljs.core.Keyword(null,"timeout","timeout",-318625318),timeout_after_ms], null));

return shadow.remote.runtime.api.relay_msg(runtime,cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(msg,new cljs.core.Keyword(null,"call-id","call-id",1043012968),call_id));
}));

(shadow.remote.runtime.shared.call.cljs$lang$maxFixedArity = 4);

shadow.remote.runtime.shared.trigger_BANG_ = (function shadow$remote$runtime$shared$trigger_BANG_(var_args){
var args__5882__auto__ = [];
var len__5876__auto___14653 = arguments.length;
var i__5877__auto___14657 = (0);
while(true){
if((i__5877__auto___14657 < len__5876__auto___14653)){
args__5882__auto__.push((arguments[i__5877__auto___14657]));

var G__14659 = (i__5877__auto___14657 + (1));
i__5877__auto___14657 = G__14659;
continue;
} else {
}
break;
}

var argseq__5883__auto__ = ((((2) < args__5882__auto__.length))?(new cljs.core.IndexedSeq(args__5882__auto__.slice((2)),(0),null)):null);
return shadow.remote.runtime.shared.trigger_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__5883__auto__);
});

(shadow.remote.runtime.shared.trigger_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (p__14484,ev,args){
var map__14487 = p__14484;
var map__14487__$1 = cljs.core.__destructure_map(map__14487);
var runtime = map__14487__$1;
var state_ref = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14487__$1,new cljs.core.Keyword(null,"state-ref","state-ref",2127874952));
var seq__14488 = cljs.core.seq(cljs.core.vals(new cljs.core.Keyword(null,"extensions","extensions",-1103629196).cljs$core$IFn$_invoke$arity$1(cljs.core.deref(state_ref))));
var chunk__14491 = null;
var count__14492 = (0);
var i__14493 = (0);
while(true){
if((i__14493 < count__14492)){
var ext = chunk__14491.cljs$core$IIndexed$_nth$arity$2(null,i__14493);
var ev_fn = cljs.core.get.cljs$core$IFn$_invoke$arity$2(ext,ev);
if(cljs.core.truth_(ev_fn)){
cljs.core.apply.cljs$core$IFn$_invoke$arity$2(ev_fn,args);


var G__14687 = seq__14488;
var G__14688 = chunk__14491;
var G__14689 = count__14492;
var G__14690 = (i__14493 + (1));
seq__14488 = G__14687;
chunk__14491 = G__14688;
count__14492 = G__14689;
i__14493 = G__14690;
continue;
} else {
var G__14696 = seq__14488;
var G__14697 = chunk__14491;
var G__14698 = count__14492;
var G__14699 = (i__14493 + (1));
seq__14488 = G__14696;
chunk__14491 = G__14697;
count__14492 = G__14698;
i__14493 = G__14699;
continue;
}
} else {
var temp__5823__auto__ = cljs.core.seq(seq__14488);
if(temp__5823__auto__){
var seq__14488__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__14488__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__14488__$1);
var G__14708 = cljs.core.chunk_rest(seq__14488__$1);
var G__14709 = c__5673__auto__;
var G__14710 = cljs.core.count(c__5673__auto__);
var G__14711 = (0);
seq__14488 = G__14708;
chunk__14491 = G__14709;
count__14492 = G__14710;
i__14493 = G__14711;
continue;
} else {
var ext = cljs.core.first(seq__14488__$1);
var ev_fn = cljs.core.get.cljs$core$IFn$_invoke$arity$2(ext,ev);
if(cljs.core.truth_(ev_fn)){
cljs.core.apply.cljs$core$IFn$_invoke$arity$2(ev_fn,args);


var G__14715 = cljs.core.next(seq__14488__$1);
var G__14716 = null;
var G__14717 = (0);
var G__14718 = (0);
seq__14488 = G__14715;
chunk__14491 = G__14716;
count__14492 = G__14717;
i__14493 = G__14718;
continue;
} else {
var G__14723 = cljs.core.next(seq__14488__$1);
var G__14724 = null;
var G__14725 = (0);
var G__14726 = (0);
seq__14488 = G__14723;
chunk__14491 = G__14724;
count__14492 = G__14725;
i__14493 = G__14726;
continue;
}
}
} else {
return null;
}
}
break;
}
}));

(shadow.remote.runtime.shared.trigger_BANG_.cljs$lang$maxFixedArity = (2));

/** @this {Function} */
(shadow.remote.runtime.shared.trigger_BANG_.cljs$lang$applyTo = (function (seq14477){
var G__14478 = cljs.core.first(seq14477);
var seq14477__$1 = cljs.core.next(seq14477);
var G__14479 = cljs.core.first(seq14477__$1);
var seq14477__$2 = cljs.core.next(seq14477__$1);
var self__5861__auto__ = this;
return self__5861__auto__.cljs$core$IFn$_invoke$arity$variadic(G__14478,G__14479,seq14477__$2);
}));

shadow.remote.runtime.shared.welcome = (function shadow$remote$runtime$shared$welcome(p__14504,p__14505){
var map__14506 = p__14504;
var map__14506__$1 = cljs.core.__destructure_map(map__14506);
var runtime = map__14506__$1;
var state_ref = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14506__$1,new cljs.core.Keyword(null,"state-ref","state-ref",2127874952));
var map__14507 = p__14505;
var map__14507__$1 = cljs.core.__destructure_map(map__14507);
var msg = map__14507__$1;
var client_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14507__$1,new cljs.core.Keyword(null,"client-id","client-id",-464622140));
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$variadic(state_ref,cljs.core.assoc,new cljs.core.Keyword(null,"client-id","client-id",-464622140),client_id,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"welcome","welcome",-578152123),true], 0));

var map__14509 = cljs.core.deref(state_ref);
var map__14509__$1 = cljs.core.__destructure_map(map__14509);
var client_info = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14509__$1,new cljs.core.Keyword(null,"client-info","client-info",1958982504));
var extensions = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14509__$1,new cljs.core.Keyword(null,"extensions","extensions",-1103629196));
shadow.remote.runtime.shared.relay_msg(runtime,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"op","op",-1882987955),new cljs.core.Keyword(null,"hello","hello",-245025397),new cljs.core.Keyword(null,"client-info","client-info",1958982504),client_info], null));

return shadow.remote.runtime.shared.trigger_BANG_(runtime,new cljs.core.Keyword(null,"on-welcome","on-welcome",1895317125));
});
shadow.remote.runtime.shared.ping = (function shadow$remote$runtime$shared$ping(runtime,msg){
return shadow.remote.runtime.shared.reply(runtime,msg,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"op","op",-1882987955),new cljs.core.Keyword(null,"pong","pong",-172484958)], null));
});
shadow.remote.runtime.shared.request_supported_ops = (function shadow$remote$runtime$shared$request_supported_ops(p__14513,msg){
var map__14514 = p__14513;
var map__14514__$1 = cljs.core.__destructure_map(map__14514);
var runtime = map__14514__$1;
var state_ref = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14514__$1,new cljs.core.Keyword(null,"state-ref","state-ref",2127874952));
return shadow.remote.runtime.shared.reply(runtime,msg,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"op","op",-1882987955),new cljs.core.Keyword(null,"supported-ops","supported-ops",337914702),new cljs.core.Keyword(null,"ops","ops",1237330063),cljs.core.disj.cljs$core$IFn$_invoke$arity$variadic(cljs.core.set(cljs.core.keys(new cljs.core.Keyword(null,"ops","ops",1237330063).cljs$core$IFn$_invoke$arity$1(cljs.core.deref(state_ref)))),new cljs.core.Keyword(null,"welcome","welcome",-578152123),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"unknown-relay-op","unknown-relay-op",170832753),new cljs.core.Keyword(null,"unknown-op","unknown-op",1900385996),new cljs.core.Keyword(null,"request-supported-ops","request-supported-ops",-1034994502),new cljs.core.Keyword(null,"tool-disconnect","tool-disconnect",189103996)], 0))], null));
});
shadow.remote.runtime.shared.unknown_relay_op = (function shadow$remote$runtime$shared$unknown_relay_op(msg){
return console.warn("unknown-relay-op",msg);
});
shadow.remote.runtime.shared.unknown_op = (function shadow$remote$runtime$shared$unknown_op(msg){
return console.warn("unknown-op",msg);
});
shadow.remote.runtime.shared.add_extension_STAR_ = (function shadow$remote$runtime$shared$add_extension_STAR_(p__14518,key,p__14519){
var map__14520 = p__14518;
var map__14520__$1 = cljs.core.__destructure_map(map__14520);
var state = map__14520__$1;
var extensions = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14520__$1,new cljs.core.Keyword(null,"extensions","extensions",-1103629196));
var map__14521 = p__14519;
var map__14521__$1 = cljs.core.__destructure_map(map__14521);
var spec = map__14521__$1;
var ops = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14521__$1,new cljs.core.Keyword(null,"ops","ops",1237330063));
var transit_write_handlers = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14521__$1,new cljs.core.Keyword(null,"transit-write-handlers","transit-write-handlers",1886308716));
if(cljs.core.contains_QMARK_(extensions,key)){
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("extension already registered",new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"key","key",-1516042587),key,new cljs.core.Keyword(null,"spec","spec",347520401),spec], null));
} else {
}

return cljs.core.reduce_kv((function (state__$1,op_kw,op_handler){
if(cljs.core.truth_(cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(state__$1,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"ops","ops",1237330063),op_kw], null)))){
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("op already registered",new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"key","key",-1516042587),key,new cljs.core.Keyword(null,"op","op",-1882987955),op_kw], null));
} else {
}

return cljs.core.assoc_in(state__$1,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"ops","ops",1237330063),op_kw], null),op_handler);
}),cljs.core.assoc_in(state,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"extensions","extensions",-1103629196),key], null),spec),ops);
});
shadow.remote.runtime.shared.add_extension = (function shadow$remote$runtime$shared$add_extension(p__14527,key,spec){
var map__14528 = p__14527;
var map__14528__$1 = cljs.core.__destructure_map(map__14528);
var runtime = map__14528__$1;
var state_ref = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14528__$1,new cljs.core.Keyword(null,"state-ref","state-ref",2127874952));
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(state_ref,shadow.remote.runtime.shared.add_extension_STAR_,key,spec);

var temp__5827__auto___14855 = new cljs.core.Keyword(null,"on-welcome","on-welcome",1895317125).cljs$core$IFn$_invoke$arity$1(spec);
if((temp__5827__auto___14855 == null)){
} else {
var on_welcome_14856 = temp__5827__auto___14855;
if(cljs.core.truth_(new cljs.core.Keyword(null,"welcome","welcome",-578152123).cljs$core$IFn$_invoke$arity$1(cljs.core.deref(state_ref)))){
(on_welcome_14856.cljs$core$IFn$_invoke$arity$0 ? on_welcome_14856.cljs$core$IFn$_invoke$arity$0() : on_welcome_14856.call(null));
} else {
}
}

return runtime;
});
shadow.remote.runtime.shared.add_defaults = (function shadow$remote$runtime$shared$add_defaults(runtime){
return shadow.remote.runtime.shared.add_extension(runtime,new cljs.core.Keyword("shadow.remote.runtime.shared","defaults","shadow.remote.runtime.shared/defaults",-1821257543),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"ops","ops",1237330063),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"welcome","welcome",-578152123),(function (p1__14531_SHARP_){
return shadow.remote.runtime.shared.welcome(runtime,p1__14531_SHARP_);
}),new cljs.core.Keyword(null,"unknown-relay-op","unknown-relay-op",170832753),(function (p1__14532_SHARP_){
return shadow.remote.runtime.shared.unknown_relay_op(p1__14532_SHARP_);
}),new cljs.core.Keyword(null,"unknown-op","unknown-op",1900385996),(function (p1__14534_SHARP_){
return shadow.remote.runtime.shared.unknown_op(p1__14534_SHARP_);
}),new cljs.core.Keyword(null,"ping","ping",-1670114784),(function (p1__14535_SHARP_){
return shadow.remote.runtime.shared.ping(runtime,p1__14535_SHARP_);
}),new cljs.core.Keyword(null,"request-supported-ops","request-supported-ops",-1034994502),(function (p1__14536_SHARP_){
return shadow.remote.runtime.shared.request_supported_ops(runtime,p1__14536_SHARP_);
})], null)], null));
});
shadow.remote.runtime.shared.del_extension_STAR_ = (function shadow$remote$runtime$shared$del_extension_STAR_(state,key){
var ext = cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(state,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"extensions","extensions",-1103629196),key], null));
if(cljs.core.not(ext)){
return state;
} else {
return cljs.core.reduce_kv((function (state__$1,op_kw,op_handler){
return cljs.core.update_in.cljs$core$IFn$_invoke$arity$4(state__$1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"ops","ops",1237330063)], null),cljs.core.dissoc,op_kw);
}),cljs.core.update.cljs$core$IFn$_invoke$arity$4(state,new cljs.core.Keyword(null,"extensions","extensions",-1103629196),cljs.core.dissoc,key),new cljs.core.Keyword(null,"ops","ops",1237330063).cljs$core$IFn$_invoke$arity$1(ext));
}
});
shadow.remote.runtime.shared.del_extension = (function shadow$remote$runtime$shared$del_extension(p__14538,key){
var map__14539 = p__14538;
var map__14539__$1 = cljs.core.__destructure_map(map__14539);
var state_ref = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14539__$1,new cljs.core.Keyword(null,"state-ref","state-ref",2127874952));
return cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(state_ref,shadow.remote.runtime.shared.del_extension_STAR_,key);
});
shadow.remote.runtime.shared.unhandled_call_result = (function shadow$remote$runtime$shared$unhandled_call_result(call_config,msg){
return console.warn("unhandled call result",msg,call_config);
});
shadow.remote.runtime.shared.unhandled_client_not_found = (function shadow$remote$runtime$shared$unhandled_client_not_found(p__14540,msg){
var map__14541 = p__14540;
var map__14541__$1 = cljs.core.__destructure_map(map__14541);
var runtime = map__14541__$1;
var state_ref = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14541__$1,new cljs.core.Keyword(null,"state-ref","state-ref",2127874952));
return shadow.remote.runtime.shared.trigger_BANG_.cljs$core$IFn$_invoke$arity$variadic(runtime,new cljs.core.Keyword(null,"on-client-not-found","on-client-not-found",-642452849),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([msg], 0));
});
shadow.remote.runtime.shared.reply_unknown_op = (function shadow$remote$runtime$shared$reply_unknown_op(runtime,msg){
return shadow.remote.runtime.shared.reply(runtime,msg,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"op","op",-1882987955),new cljs.core.Keyword(null,"unknown-op","unknown-op",1900385996),new cljs.core.Keyword(null,"msg","msg",-1386103444),msg], null));
});
shadow.remote.runtime.shared.process = (function shadow$remote$runtime$shared$process(p__14543,p__14544){
var map__14545 = p__14543;
var map__14545__$1 = cljs.core.__destructure_map(map__14545);
var runtime = map__14545__$1;
var state_ref = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14545__$1,new cljs.core.Keyword(null,"state-ref","state-ref",2127874952));
var map__14546 = p__14544;
var map__14546__$1 = cljs.core.__destructure_map(map__14546);
var msg = map__14546__$1;
var op = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14546__$1,new cljs.core.Keyword(null,"op","op",-1882987955));
var call_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14546__$1,new cljs.core.Keyword(null,"call-id","call-id",1043012968));
var state = cljs.core.deref(state_ref);
var op_handler = cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(state,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"ops","ops",1237330063),op], null));
if(cljs.core.truth_(call_id)){
var cfg = cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(state,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"call-handlers","call-handlers",386605551),call_id], null));
var call_handler = cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(cfg,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"handlers","handlers",79528781),op], null));
if(cljs.core.truth_(call_handler)){
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$variadic(state_ref,cljs.core.update,new cljs.core.Keyword(null,"call-handlers","call-handlers",386605551),cljs.core.dissoc,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([call_id], 0));

return (call_handler.cljs$core$IFn$_invoke$arity$1 ? call_handler.cljs$core$IFn$_invoke$arity$1(msg) : call_handler.call(null,msg));
} else {
if(cljs.core.truth_(op_handler)){
return (op_handler.cljs$core$IFn$_invoke$arity$1 ? op_handler.cljs$core$IFn$_invoke$arity$1(msg) : op_handler.call(null,msg));
} else {
return shadow.remote.runtime.shared.unhandled_call_result(cfg,msg);

}
}
} else {
if(cljs.core.truth_(op_handler)){
return (op_handler.cljs$core$IFn$_invoke$arity$1 ? op_handler.cljs$core$IFn$_invoke$arity$1(msg) : op_handler.call(null,msg));
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"client-not-found","client-not-found",-1754042614),op)){
return shadow.remote.runtime.shared.unhandled_client_not_found(runtime,msg);
} else {
return shadow.remote.runtime.shared.reply_unknown_op(runtime,msg);

}
}
}
});
shadow.remote.runtime.shared.run_on_idle = (function shadow$remote$runtime$shared$run_on_idle(state_ref){
var seq__14572 = cljs.core.seq(cljs.core.vals(new cljs.core.Keyword(null,"extensions","extensions",-1103629196).cljs$core$IFn$_invoke$arity$1(cljs.core.deref(state_ref))));
var chunk__14574 = null;
var count__14575 = (0);
var i__14576 = (0);
while(true){
if((i__14576 < count__14575)){
var map__14585 = chunk__14574.cljs$core$IIndexed$_nth$arity$2(null,i__14576);
var map__14585__$1 = cljs.core.__destructure_map(map__14585);
var on_idle = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14585__$1,new cljs.core.Keyword(null,"on-idle","on-idle",2044706602));
if(cljs.core.truth_(on_idle)){
(on_idle.cljs$core$IFn$_invoke$arity$0 ? on_idle.cljs$core$IFn$_invoke$arity$0() : on_idle.call(null));


var G__14964 = seq__14572;
var G__14965 = chunk__14574;
var G__14966 = count__14575;
var G__14967 = (i__14576 + (1));
seq__14572 = G__14964;
chunk__14574 = G__14965;
count__14575 = G__14966;
i__14576 = G__14967;
continue;
} else {
var G__14968 = seq__14572;
var G__14969 = chunk__14574;
var G__14970 = count__14575;
var G__14971 = (i__14576 + (1));
seq__14572 = G__14968;
chunk__14574 = G__14969;
count__14575 = G__14970;
i__14576 = G__14971;
continue;
}
} else {
var temp__5823__auto__ = cljs.core.seq(seq__14572);
if(temp__5823__auto__){
var seq__14572__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__14572__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__14572__$1);
var G__14978 = cljs.core.chunk_rest(seq__14572__$1);
var G__14979 = c__5673__auto__;
var G__14980 = cljs.core.count(c__5673__auto__);
var G__14981 = (0);
seq__14572 = G__14978;
chunk__14574 = G__14979;
count__14575 = G__14980;
i__14576 = G__14981;
continue;
} else {
var map__14592 = cljs.core.first(seq__14572__$1);
var map__14592__$1 = cljs.core.__destructure_map(map__14592);
var on_idle = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__14592__$1,new cljs.core.Keyword(null,"on-idle","on-idle",2044706602));
if(cljs.core.truth_(on_idle)){
(on_idle.cljs$core$IFn$_invoke$arity$0 ? on_idle.cljs$core$IFn$_invoke$arity$0() : on_idle.call(null));


var G__14989 = cljs.core.next(seq__14572__$1);
var G__14990 = null;
var G__14991 = (0);
var G__14992 = (0);
seq__14572 = G__14989;
chunk__14574 = G__14990;
count__14575 = G__14991;
i__14576 = G__14992;
continue;
} else {
var G__14995 = cljs.core.next(seq__14572__$1);
var G__14996 = null;
var G__14997 = (0);
var G__14998 = (0);
seq__14572 = G__14995;
chunk__14574 = G__14996;
count__14575 = G__14997;
i__14576 = G__14998;
continue;
}
}
} else {
return null;
}
}
break;
}
});

//# sourceMappingURL=shadow.remote.runtime.shared.js.map
