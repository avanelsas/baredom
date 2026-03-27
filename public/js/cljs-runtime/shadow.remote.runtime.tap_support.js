goog.provide('shadow.remote.runtime.tap_support');
shadow.remote.runtime.tap_support.tap_subscribe = (function shadow$remote$runtime$tap_support$tap_subscribe(p__20236,p__20237){
var map__20243 = p__20236;
var map__20243__$1 = cljs.core.__destructure_map(map__20243);
var svc = map__20243__$1;
var subs_ref = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20243__$1,new cljs.core.Keyword(null,"subs-ref","subs-ref",-1355989911));
var obj_support = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20243__$1,new cljs.core.Keyword(null,"obj-support","obj-support",1522559229));
var runtime = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20243__$1,new cljs.core.Keyword(null,"runtime","runtime",-1331573996));
var map__20244 = p__20237;
var map__20244__$1 = cljs.core.__destructure_map(map__20244);
var msg = map__20244__$1;
var from = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20244__$1,new cljs.core.Keyword(null,"from","from",1815293044));
var summary = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20244__$1,new cljs.core.Keyword(null,"summary","summary",380847952));
var history__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20244__$1,new cljs.core.Keyword(null,"history","history",-247395220));
var num = cljs.core.get.cljs$core$IFn$_invoke$arity$3(map__20244__$1,new cljs.core.Keyword(null,"num","num",1985240673),(10));
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(subs_ref,cljs.core.assoc,from,msg);

if(cljs.core.truth_(history__$1)){
return shadow.remote.runtime.shared.reply(runtime,msg,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"op","op",-1882987955),new cljs.core.Keyword(null,"tap-subscribed","tap-subscribed",-1882247432),new cljs.core.Keyword(null,"history","history",-247395220),cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentVector.EMPTY,cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (oid){
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"oid","oid",-768692334),oid,new cljs.core.Keyword(null,"summary","summary",380847952),shadow.remote.runtime.obj_support.obj_describe_STAR_(obj_support,oid)], null);
}),shadow.remote.runtime.obj_support.get_tap_history(obj_support,num)))], null));
} else {
return null;
}
});
shadow.remote.runtime.tap_support.tap_unsubscribe = (function shadow$remote$runtime$tap_support$tap_unsubscribe(p__20264,p__20265){
var map__20268 = p__20264;
var map__20268__$1 = cljs.core.__destructure_map(map__20268);
var subs_ref = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20268__$1,new cljs.core.Keyword(null,"subs-ref","subs-ref",-1355989911));
var map__20269 = p__20265;
var map__20269__$1 = cljs.core.__destructure_map(map__20269);
var from = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20269__$1,new cljs.core.Keyword(null,"from","from",1815293044));
return cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(subs_ref,cljs.core.dissoc,from);
});
shadow.remote.runtime.tap_support.request_tap_history = (function shadow$remote$runtime$tap_support$request_tap_history(p__20275,p__20276){
var map__20277 = p__20275;
var map__20277__$1 = cljs.core.__destructure_map(map__20277);
var obj_support = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20277__$1,new cljs.core.Keyword(null,"obj-support","obj-support",1522559229));
var runtime = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20277__$1,new cljs.core.Keyword(null,"runtime","runtime",-1331573996));
var map__20278 = p__20276;
var map__20278__$1 = cljs.core.__destructure_map(map__20278);
var msg = map__20278__$1;
var num = cljs.core.get.cljs$core$IFn$_invoke$arity$3(map__20278__$1,new cljs.core.Keyword(null,"num","num",1985240673),(10));
var tap_ids = shadow.remote.runtime.obj_support.get_tap_history(obj_support,num);
return shadow.remote.runtime.shared.reply(runtime,msg,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"op","op",-1882987955),new cljs.core.Keyword(null,"tap-history","tap-history",-282803347),new cljs.core.Keyword(null,"oids","oids",-1580877688),tap_ids], null));
});
shadow.remote.runtime.tap_support.tool_disconnect = (function shadow$remote$runtime$tap_support$tool_disconnect(p__20290,tid){
var map__20292 = p__20290;
var map__20292__$1 = cljs.core.__destructure_map(map__20292);
var svc = map__20292__$1;
var subs_ref = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20292__$1,new cljs.core.Keyword(null,"subs-ref","subs-ref",-1355989911));
return cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(subs_ref,cljs.core.dissoc,tid);
});
shadow.remote.runtime.tap_support.start = (function shadow$remote$runtime$tap_support$start(runtime,obj_support){
var subs_ref = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(cljs.core.PersistentArrayMap.EMPTY);
var tap_fn = (function shadow$remote$runtime$tap_support$start_$_runtime_tap(obj){
if((!((obj == null)))){
var oid = shadow.remote.runtime.obj_support.register(obj_support,obj,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"from","from",1815293044),new cljs.core.Keyword(null,"tap","tap",-1086702463)], null));
var seq__20308 = cljs.core.seq(cljs.core.deref(subs_ref));
var chunk__20309 = null;
var count__20310 = (0);
var i__20311 = (0);
while(true){
if((i__20311 < count__20310)){
var vec__20330 = chunk__20309.cljs$core$IIndexed$_nth$arity$2(null,i__20311);
var tid = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__20330,(0),null);
var tap_config = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__20330,(1),null);
shadow.remote.runtime.api.relay_msg(runtime,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"op","op",-1882987955),new cljs.core.Keyword(null,"tap","tap",-1086702463),new cljs.core.Keyword(null,"to","to",192099007),tid,new cljs.core.Keyword(null,"oid","oid",-768692334),oid], null));


var G__20349 = seq__20308;
var G__20350 = chunk__20309;
var G__20351 = count__20310;
var G__20352 = (i__20311 + (1));
seq__20308 = G__20349;
chunk__20309 = G__20350;
count__20310 = G__20351;
i__20311 = G__20352;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__20308);
if(temp__5823__auto__){
var seq__20308__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__20308__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__20308__$1);
var G__20353 = cljs.core.chunk_rest(seq__20308__$1);
var G__20354 = c__5673__auto__;
var G__20355 = cljs.core.count(c__5673__auto__);
var G__20356 = (0);
seq__20308 = G__20353;
chunk__20309 = G__20354;
count__20310 = G__20355;
i__20311 = G__20356;
continue;
} else {
var vec__20336 = cljs.core.first(seq__20308__$1);
var tid = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__20336,(0),null);
var tap_config = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__20336,(1),null);
shadow.remote.runtime.api.relay_msg(runtime,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"op","op",-1882987955),new cljs.core.Keyword(null,"tap","tap",-1086702463),new cljs.core.Keyword(null,"to","to",192099007),tid,new cljs.core.Keyword(null,"oid","oid",-768692334),oid], null));


var G__20357 = cljs.core.next(seq__20308__$1);
var G__20358 = null;
var G__20359 = (0);
var G__20360 = (0);
seq__20308 = G__20357;
chunk__20309 = G__20358;
count__20310 = G__20359;
i__20311 = G__20360;
continue;
}
} else {
return null;
}
}
break;
}
} else {
return null;
}
});
var svc = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"runtime","runtime",-1331573996),runtime,new cljs.core.Keyword(null,"obj-support","obj-support",1522559229),obj_support,new cljs.core.Keyword(null,"tap-fn","tap-fn",1573556461),tap_fn,new cljs.core.Keyword(null,"subs-ref","subs-ref",-1355989911),subs_ref], null);
shadow.remote.runtime.api.add_extension(runtime,new cljs.core.Keyword("shadow.remote.runtime.tap-support","ext","shadow.remote.runtime.tap-support/ext",1019069674),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"ops","ops",1237330063),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"tap-subscribe","tap-subscribe",411179050),(function (p1__20295_SHARP_){
return shadow.remote.runtime.tap_support.tap_subscribe(svc,p1__20295_SHARP_);
}),new cljs.core.Keyword(null,"tap-unsubscribe","tap-unsubscribe",1183890755),(function (p1__20296_SHARP_){
return shadow.remote.runtime.tap_support.tap_unsubscribe(svc,p1__20296_SHARP_);
}),new cljs.core.Keyword(null,"request-tap-history","request-tap-history",-670837812),(function (p1__20297_SHARP_){
return shadow.remote.runtime.tap_support.request_tap_history(svc,p1__20297_SHARP_);
})], null),new cljs.core.Keyword(null,"on-tool-disconnect","on-tool-disconnect",693464366),(function (p1__20298_SHARP_){
return shadow.remote.runtime.tap_support.tool_disconnect(svc,p1__20298_SHARP_);
})], null));

cljs.core.add_tap(tap_fn);

return svc;
});
shadow.remote.runtime.tap_support.stop = (function shadow$remote$runtime$tap_support$stop(p__20342){
var map__20345 = p__20342;
var map__20345__$1 = cljs.core.__destructure_map(map__20345);
var svc = map__20345__$1;
var tap_fn = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20345__$1,new cljs.core.Keyword(null,"tap-fn","tap-fn",1573556461));
var runtime = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20345__$1,new cljs.core.Keyword(null,"runtime","runtime",-1331573996));
cljs.core.remove_tap(tap_fn);

return shadow.remote.runtime.api.del_extension(runtime,new cljs.core.Keyword("shadow.remote.runtime.tap-support","ext","shadow.remote.runtime.tap-support/ext",1019069674));
});

//# sourceMappingURL=shadow.remote.runtime.tap_support.js.map
