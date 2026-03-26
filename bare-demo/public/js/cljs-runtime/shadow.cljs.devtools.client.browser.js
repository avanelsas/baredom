goog.provide('shadow.cljs.devtools.client.browser');
shadow.cljs.devtools.client.browser.devtools_msg = (function shadow$cljs$devtools$client$browser$devtools_msg(var_args){
var args__5882__auto__ = [];
var len__5876__auto___21488 = arguments.length;
var i__5877__auto___21489 = (0);
while(true){
if((i__5877__auto___21489 < len__5876__auto___21488)){
args__5882__auto__.push((arguments[i__5877__auto___21489]));

var G__21490 = (i__5877__auto___21489 + (1));
i__5877__auto___21489 = G__21490;
continue;
} else {
}
break;
}

var argseq__5883__auto__ = ((((1) < args__5882__auto__.length))?(new cljs.core.IndexedSeq(args__5882__auto__.slice((1)),(0),null)):null);
return shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__5883__auto__);
});

(shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic = (function (msg,args){
if(shadow.cljs.devtools.client.env.log){
if(cljs.core.seq(shadow.cljs.devtools.client.env.log_style)){
return console.log.apply(console,cljs.core.into_array.cljs$core$IFn$_invoke$arity$1(cljs.core.into.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(""+"%cshadow-cljs: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(msg)),shadow.cljs.devtools.client.env.log_style], null),args)));
} else {
return console.log.apply(console,cljs.core.into_array.cljs$core$IFn$_invoke$arity$1(cljs.core.into.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(""+"shadow-cljs: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(msg))], null),args)));
}
} else {
return null;
}
}));

(shadow.cljs.devtools.client.browser.devtools_msg.cljs$lang$maxFixedArity = (1));

/** @this {Function} */
(shadow.cljs.devtools.client.browser.devtools_msg.cljs$lang$applyTo = (function (seq20889){
var G__20890 = cljs.core.first(seq20889);
var seq20889__$1 = cljs.core.next(seq20889);
var self__5861__auto__ = this;
return self__5861__auto__.cljs$core$IFn$_invoke$arity$variadic(G__20890,seq20889__$1);
}));

shadow.cljs.devtools.client.browser.script_eval = (function shadow$cljs$devtools$client$browser$script_eval(code){
return goog.globalEval(code);
});
shadow.cljs.devtools.client.browser.do_js_load = (function shadow$cljs$devtools$client$browser$do_js_load(sources){
var seq__20907 = cljs.core.seq(sources);
var chunk__20908 = null;
var count__20909 = (0);
var i__20910 = (0);
while(true){
if((i__20910 < count__20909)){
var map__20948 = chunk__20908.cljs$core$IIndexed$_nth$arity$2(null,i__20910);
var map__20948__$1 = cljs.core.__destructure_map(map__20948);
var src = map__20948__$1;
var resource_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20948__$1,new cljs.core.Keyword(null,"resource-id","resource-id",-1308422582));
var output_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20948__$1,new cljs.core.Keyword(null,"output-name","output-name",-1769107767));
var resource_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20948__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
var js = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20948__$1,new cljs.core.Keyword(null,"js","js",1768080579));
$CLJS.SHADOW_ENV.setLoaded(output_name);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load JS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([resource_name], 0));

shadow.cljs.devtools.client.env.before_load_src(src);

try{shadow.cljs.devtools.client.browser.script_eval((""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(js)+"\n//# sourceURL="+cljs.core.str.cljs$core$IFn$_invoke$arity$1($CLJS.SHADOW_ENV.scriptBase)+cljs.core.str.cljs$core$IFn$_invoke$arity$1(output_name)));
}catch (e20953){var e_21491 = e20953;
if(shadow.cljs.devtools.client.env.log){
console.error((""+"Failed to load "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name)),e_21491);
} else {
}

throw (new Error((""+"Failed to load "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name)+": "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(e_21491.message))));
}

var G__21492 = seq__20907;
var G__21493 = chunk__20908;
var G__21494 = count__20909;
var G__21495 = (i__20910 + (1));
seq__20907 = G__21492;
chunk__20908 = G__21493;
count__20909 = G__21494;
i__20910 = G__21495;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__20907);
if(temp__5823__auto__){
var seq__20907__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__20907__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__20907__$1);
var G__21496 = cljs.core.chunk_rest(seq__20907__$1);
var G__21497 = c__5673__auto__;
var G__21498 = cljs.core.count(c__5673__auto__);
var G__21499 = (0);
seq__20907 = G__21496;
chunk__20908 = G__21497;
count__20909 = G__21498;
i__20910 = G__21499;
continue;
} else {
var map__20961 = cljs.core.first(seq__20907__$1);
var map__20961__$1 = cljs.core.__destructure_map(map__20961);
var src = map__20961__$1;
var resource_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20961__$1,new cljs.core.Keyword(null,"resource-id","resource-id",-1308422582));
var output_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20961__$1,new cljs.core.Keyword(null,"output-name","output-name",-1769107767));
var resource_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20961__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
var js = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20961__$1,new cljs.core.Keyword(null,"js","js",1768080579));
$CLJS.SHADOW_ENV.setLoaded(output_name);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load JS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([resource_name], 0));

shadow.cljs.devtools.client.env.before_load_src(src);

try{shadow.cljs.devtools.client.browser.script_eval((""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(js)+"\n//# sourceURL="+cljs.core.str.cljs$core$IFn$_invoke$arity$1($CLJS.SHADOW_ENV.scriptBase)+cljs.core.str.cljs$core$IFn$_invoke$arity$1(output_name)));
}catch (e20965){var e_21500 = e20965;
if(shadow.cljs.devtools.client.env.log){
console.error((""+"Failed to load "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name)),e_21500);
} else {
}

throw (new Error((""+"Failed to load "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name)+": "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(e_21500.message))));
}

var G__21501 = cljs.core.next(seq__20907__$1);
var G__21502 = null;
var G__21503 = (0);
var G__21504 = (0);
seq__20907 = G__21501;
chunk__20908 = G__21502;
count__20909 = G__21503;
i__20910 = G__21504;
continue;
}
} else {
return null;
}
}
break;
}
});
shadow.cljs.devtools.client.browser.do_js_reload = (function shadow$cljs$devtools$client$browser$do_js_reload(msg,sources,complete_fn,failure_fn){
return shadow.cljs.devtools.client.env.do_js_reload.cljs$core$IFn$_invoke$arity$4(cljs.core.assoc.cljs$core$IFn$_invoke$arity$variadic(msg,new cljs.core.Keyword(null,"log-missing-fn","log-missing-fn",732676765),(function (fn_sym){
return null;
}),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"log-call-async","log-call-async",183826192),(function (fn_sym){
return shadow.cljs.devtools.client.browser.devtools_msg((""+"call async "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym)));
}),new cljs.core.Keyword(null,"log-call","log-call",412404391),(function (fn_sym){
return shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym)));
})], 0)),(function (next){
shadow.cljs.devtools.client.browser.do_js_load(sources);

return (next.cljs$core$IFn$_invoke$arity$0 ? next.cljs$core$IFn$_invoke$arity$0() : next.call(null));
}),complete_fn,failure_fn);
});
/**
 * when (require '["some-str" :as x]) is done at the REPL we need to manually call the shadow.js.require for it
 * since the file only adds the shadow$provide. only need to do this for shadow-js.
 */
shadow.cljs.devtools.client.browser.do_js_requires = (function shadow$cljs$devtools$client$browser$do_js_requires(js_requires){
var seq__20985 = cljs.core.seq(js_requires);
var chunk__20986 = null;
var count__20987 = (0);
var i__20988 = (0);
while(true){
if((i__20988 < count__20987)){
var js_ns = chunk__20986.cljs$core$IIndexed$_nth$arity$2(null,i__20988);
var require_str_21505 = (""+"var "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(js_ns)+" = shadow.js.require(\""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(js_ns)+"\");");
shadow.cljs.devtools.client.browser.script_eval(require_str_21505);


var G__21506 = seq__20985;
var G__21507 = chunk__20986;
var G__21508 = count__20987;
var G__21509 = (i__20988 + (1));
seq__20985 = G__21506;
chunk__20986 = G__21507;
count__20987 = G__21508;
i__20988 = G__21509;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__20985);
if(temp__5823__auto__){
var seq__20985__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__20985__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__20985__$1);
var G__21510 = cljs.core.chunk_rest(seq__20985__$1);
var G__21511 = c__5673__auto__;
var G__21512 = cljs.core.count(c__5673__auto__);
var G__21513 = (0);
seq__20985 = G__21510;
chunk__20986 = G__21511;
count__20987 = G__21512;
i__20988 = G__21513;
continue;
} else {
var js_ns = cljs.core.first(seq__20985__$1);
var require_str_21514 = (""+"var "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(js_ns)+" = shadow.js.require(\""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(js_ns)+"\");");
shadow.cljs.devtools.client.browser.script_eval(require_str_21514);


var G__21515 = cljs.core.next(seq__20985__$1);
var G__21516 = null;
var G__21517 = (0);
var G__21518 = (0);
seq__20985 = G__21515;
chunk__20986 = G__21516;
count__20987 = G__21517;
i__20988 = G__21518;
continue;
}
} else {
return null;
}
}
break;
}
});
shadow.cljs.devtools.client.browser.handle_build_complete = (function shadow$cljs$devtools$client$browser$handle_build_complete(runtime,p__21018){
var map__21019 = p__21018;
var map__21019__$1 = cljs.core.__destructure_map(map__21019);
var msg = map__21019__$1;
var info = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21019__$1,new cljs.core.Keyword(null,"info","info",-317069002));
var reload_info = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21019__$1,new cljs.core.Keyword(null,"reload-info","reload-info",1648088086));
var warnings = cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentVector.EMPTY,cljs.core.distinct.cljs$core$IFn$_invoke$arity$1((function (){var iter__5628__auto__ = (function shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__21022(s__21023){
return (new cljs.core.LazySeq(null,(function (){
var s__21023__$1 = s__21023;
while(true){
var temp__5823__auto__ = cljs.core.seq(s__21023__$1);
if(temp__5823__auto__){
var xs__6383__auto__ = temp__5823__auto__;
var map__21033 = cljs.core.first(xs__6383__auto__);
var map__21033__$1 = cljs.core.__destructure_map(map__21033);
var src = map__21033__$1;
var resource_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21033__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
var warnings = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21033__$1,new cljs.core.Keyword(null,"warnings","warnings",-735437651));
if(cljs.core.not(new cljs.core.Keyword(null,"from-jar","from-jar",1050932827).cljs$core$IFn$_invoke$arity$1(src))){
var iterys__5624__auto__ = ((function (s__21023__$1,map__21033,map__21033__$1,src,resource_name,warnings,xs__6383__auto__,temp__5823__auto__,map__21019,map__21019__$1,msg,info,reload_info){
return (function shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__21022_$_iter__21024(s__21025){
return (new cljs.core.LazySeq(null,((function (s__21023__$1,map__21033,map__21033__$1,src,resource_name,warnings,xs__6383__auto__,temp__5823__auto__,map__21019,map__21019__$1,msg,info,reload_info){
return (function (){
var s__21025__$1 = s__21025;
while(true){
var temp__5823__auto____$1 = cljs.core.seq(s__21025__$1);
if(temp__5823__auto____$1){
var s__21025__$2 = temp__5823__auto____$1;
if(cljs.core.chunked_seq_QMARK_(s__21025__$2)){
var c__5626__auto__ = cljs.core.chunk_first(s__21025__$2);
var size__5627__auto__ = cljs.core.count(c__5626__auto__);
var b__21027 = cljs.core.chunk_buffer(size__5627__auto__);
if((function (){var i__21026 = (0);
while(true){
if((i__21026 < size__5627__auto__)){
var warning = cljs.core._nth(c__5626__auto__,i__21026);
cljs.core.chunk_append(b__21027,cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(warning,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100),resource_name));

var G__21519 = (i__21026 + (1));
i__21026 = G__21519;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons(cljs.core.chunk(b__21027),shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__21022_$_iter__21024(cljs.core.chunk_rest(s__21025__$2)));
} else {
return cljs.core.chunk_cons(cljs.core.chunk(b__21027),null);
}
} else {
var warning = cljs.core.first(s__21025__$2);
return cljs.core.cons(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(warning,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100),resource_name),shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__21022_$_iter__21024(cljs.core.rest(s__21025__$2)));
}
} else {
return null;
}
break;
}
});})(s__21023__$1,map__21033,map__21033__$1,src,resource_name,warnings,xs__6383__auto__,temp__5823__auto__,map__21019,map__21019__$1,msg,info,reload_info))
,null,null));
});})(s__21023__$1,map__21033,map__21033__$1,src,resource_name,warnings,xs__6383__auto__,temp__5823__auto__,map__21019,map__21019__$1,msg,info,reload_info))
;
var fs__5625__auto__ = cljs.core.seq(iterys__5624__auto__(warnings));
if(fs__5625__auto__){
return cljs.core.concat.cljs$core$IFn$_invoke$arity$2(fs__5625__auto__,shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__21022(cljs.core.rest(s__21023__$1)));
} else {
var G__21520 = cljs.core.rest(s__21023__$1);
s__21023__$1 = G__21520;
continue;
}
} else {
var G__21521 = cljs.core.rest(s__21023__$1);
s__21023__$1 = G__21521;
continue;
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__5628__auto__(new cljs.core.Keyword(null,"sources","sources",-321166424).cljs$core$IFn$_invoke$arity$1(info));
})()));
if(shadow.cljs.devtools.client.env.log){
var seq__21058_21522 = cljs.core.seq(warnings);
var chunk__21059_21523 = null;
var count__21060_21524 = (0);
var i__21061_21525 = (0);
while(true){
if((i__21061_21525 < count__21060_21524)){
var map__21073_21526 = chunk__21059_21523.cljs$core$IIndexed$_nth$arity$2(null,i__21061_21525);
var map__21073_21527__$1 = cljs.core.__destructure_map(map__21073_21526);
var w_21528 = map__21073_21527__$1;
var msg_21529__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21073_21527__$1,new cljs.core.Keyword(null,"msg","msg",-1386103444));
var line_21530 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21073_21527__$1,new cljs.core.Keyword(null,"line","line",212345235));
var column_21531 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21073_21527__$1,new cljs.core.Keyword(null,"column","column",2078222095));
var resource_name_21532 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21073_21527__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
console.warn((""+"BUILD-WARNING in "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name_21532)+" at ["+cljs.core.str.cljs$core$IFn$_invoke$arity$1(line_21530)+":"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(column_21531)+"]\n\t"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(msg_21529__$1)));


var G__21533 = seq__21058_21522;
var G__21534 = chunk__21059_21523;
var G__21535 = count__21060_21524;
var G__21536 = (i__21061_21525 + (1));
seq__21058_21522 = G__21533;
chunk__21059_21523 = G__21534;
count__21060_21524 = G__21535;
i__21061_21525 = G__21536;
continue;
} else {
var temp__5823__auto___21537 = cljs.core.seq(seq__21058_21522);
if(temp__5823__auto___21537){
var seq__21058_21538__$1 = temp__5823__auto___21537;
if(cljs.core.chunked_seq_QMARK_(seq__21058_21538__$1)){
var c__5673__auto___21539 = cljs.core.chunk_first(seq__21058_21538__$1);
var G__21540 = cljs.core.chunk_rest(seq__21058_21538__$1);
var G__21541 = c__5673__auto___21539;
var G__21542 = cljs.core.count(c__5673__auto___21539);
var G__21543 = (0);
seq__21058_21522 = G__21540;
chunk__21059_21523 = G__21541;
count__21060_21524 = G__21542;
i__21061_21525 = G__21543;
continue;
} else {
var map__21083_21544 = cljs.core.first(seq__21058_21538__$1);
var map__21083_21545__$1 = cljs.core.__destructure_map(map__21083_21544);
var w_21546 = map__21083_21545__$1;
var msg_21547__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21083_21545__$1,new cljs.core.Keyword(null,"msg","msg",-1386103444));
var line_21548 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21083_21545__$1,new cljs.core.Keyword(null,"line","line",212345235));
var column_21549 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21083_21545__$1,new cljs.core.Keyword(null,"column","column",2078222095));
var resource_name_21550 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21083_21545__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
console.warn((""+"BUILD-WARNING in "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name_21550)+" at ["+cljs.core.str.cljs$core$IFn$_invoke$arity$1(line_21548)+":"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(column_21549)+"]\n\t"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(msg_21547__$1)));


var G__21551 = cljs.core.next(seq__21058_21538__$1);
var G__21552 = null;
var G__21553 = (0);
var G__21554 = (0);
seq__21058_21522 = G__21551;
chunk__21059_21523 = G__21552;
count__21060_21524 = G__21553;
i__21061_21525 = G__21554;
continue;
}
} else {
}
}
break;
}
} else {
}

if((!(shadow.cljs.devtools.client.env.autoload))){
return shadow.cljs.devtools.client.hud.load_end_success();
} else {
if(((cljs.core.empty_QMARK_(warnings)) || (shadow.cljs.devtools.client.env.ignore_warnings))){
var sources_to_get = shadow.cljs.devtools.client.env.filter_reload_sources(info,reload_info);
if(cljs.core.not(cljs.core.seq(sources_to_get))){
return shadow.cljs.devtools.client.hud.load_end_success();
} else {
if(cljs.core.seq(cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(msg,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"reload-info","reload-info",1648088086),new cljs.core.Keyword(null,"after-load","after-load",-1278503285)], null)))){
} else {
shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("reloading code but no :after-load hooks are configured!",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2(["https://shadow-cljs.github.io/docs/UsersGuide.html#_lifecycle_hooks"], 0));
}

return shadow.cljs.devtools.client.shared.load_sources(runtime,sources_to_get,(function (p1__21017_SHARP_){
return shadow.cljs.devtools.client.browser.do_js_reload(msg,p1__21017_SHARP_,shadow.cljs.devtools.client.hud.load_end_success,shadow.cljs.devtools.client.hud.load_failure);
}));
}
} else {
return null;
}
}
});
shadow.cljs.devtools.client.browser.page_load_uri = (cljs.core.truth_(goog.global.document)?goog.Uri.parse(document.location.href):null);
shadow.cljs.devtools.client.browser.match_paths = (function shadow$cljs$devtools$client$browser$match_paths(old,new$){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2("file",shadow.cljs.devtools.client.browser.page_load_uri.getScheme())){
var rel_new = cljs.core.subs.cljs$core$IFn$_invoke$arity$2(new$,(1));
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(old,rel_new)) || (clojure.string.starts_with_QMARK_(old,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(rel_new)+"?"))))){
return rel_new;
} else {
return null;
}
} else {
var node_uri = goog.Uri.parse(old);
var node_uri_resolved = shadow.cljs.devtools.client.browser.page_load_uri.resolve(node_uri);
var node_abs = node_uri_resolved.getPath();
var and__5140__auto__ = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$1(shadow.cljs.devtools.client.browser.page_load_uri.hasSameDomainAs(node_uri))) || (cljs.core.not(node_uri.hasDomain())));
if(and__5140__auto__){
var and__5140__auto____$1 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(node_abs,new$);
if(and__5140__auto____$1){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var G__21092 = node_uri;
G__21092.setQuery(null);

G__21092.setPath(new$);

return G__21092;
})()));
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
}
});
shadow.cljs.devtools.client.browser.handle_asset_update = (function shadow$cljs$devtools$client$browser$handle_asset_update(p__21094){
var map__21095 = p__21094;
var map__21095__$1 = cljs.core.__destructure_map(map__21095);
var msg = map__21095__$1;
var updates = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21095__$1,new cljs.core.Keyword(null,"updates","updates",2013983452));
var reload_info = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21095__$1,new cljs.core.Keyword(null,"reload-info","reload-info",1648088086));
var seq__21096 = cljs.core.seq(updates);
var chunk__21098 = null;
var count__21099 = (0);
var i__21100 = (0);
while(true){
if((i__21100 < count__21099)){
var path = chunk__21098.cljs$core$IIndexed$_nth$arity$2(null,i__21100);
if(clojure.string.ends_with_QMARK_(path,"css")){
var seq__21357_21555 = cljs.core.seq(cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(document.querySelectorAll("link[rel=\"stylesheet\"]")));
var chunk__21361_21556 = null;
var count__21362_21557 = (0);
var i__21363_21558 = (0);
while(true){
if((i__21363_21558 < count__21362_21557)){
var node_21559 = chunk__21361_21556.cljs$core$IIndexed$_nth$arity$2(null,i__21363_21558);
if(cljs.core.not(node_21559.shadow$old)){
var path_match_21560 = shadow.cljs.devtools.client.browser.match_paths(node_21559.getAttribute("href"),path);
if(cljs.core.truth_(path_match_21560)){
var new_link_21561 = (function (){var G__21390 = node_21559.cloneNode(true);
G__21390.setAttribute("href",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(path_match_21560)+"?r="+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.rand.cljs$core$IFn$_invoke$arity$0())));

return G__21390;
})();
(node_21559.shadow$old = true);

(new_link_21561.onload = ((function (seq__21357_21555,chunk__21361_21556,count__21362_21557,i__21363_21558,seq__21096,chunk__21098,count__21099,i__21100,new_link_21561,path_match_21560,node_21559,path,map__21095,map__21095__$1,msg,updates,reload_info){
return (function (e){
var seq__21391_21562 = cljs.core.seq(cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(msg,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"reload-info","reload-info",1648088086),new cljs.core.Keyword(null,"asset-load","asset-load",-1925902322)], null)));
var chunk__21393_21563 = null;
var count__21394_21564 = (0);
var i__21395_21565 = (0);
while(true){
if((i__21395_21565 < count__21394_21564)){
var map__21399_21566 = chunk__21393_21563.cljs$core$IIndexed$_nth$arity$2(null,i__21395_21565);
var map__21399_21567__$1 = cljs.core.__destructure_map(map__21399_21566);
var task_21568 = map__21399_21567__$1;
var fn_str_21569 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21399_21567__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21570 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21399_21567__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21571 = goog.getObjectByName(fn_str_21569,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21570)));

(fn_obj_21571.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21571.cljs$core$IFn$_invoke$arity$2(path,new_link_21561) : fn_obj_21571.call(null,path,new_link_21561));


var G__21572 = seq__21391_21562;
var G__21573 = chunk__21393_21563;
var G__21574 = count__21394_21564;
var G__21575 = (i__21395_21565 + (1));
seq__21391_21562 = G__21572;
chunk__21393_21563 = G__21573;
count__21394_21564 = G__21574;
i__21395_21565 = G__21575;
continue;
} else {
var temp__5823__auto___21576 = cljs.core.seq(seq__21391_21562);
if(temp__5823__auto___21576){
var seq__21391_21577__$1 = temp__5823__auto___21576;
if(cljs.core.chunked_seq_QMARK_(seq__21391_21577__$1)){
var c__5673__auto___21578 = cljs.core.chunk_first(seq__21391_21577__$1);
var G__21579 = cljs.core.chunk_rest(seq__21391_21577__$1);
var G__21580 = c__5673__auto___21578;
var G__21581 = cljs.core.count(c__5673__auto___21578);
var G__21582 = (0);
seq__21391_21562 = G__21579;
chunk__21393_21563 = G__21580;
count__21394_21564 = G__21581;
i__21395_21565 = G__21582;
continue;
} else {
var map__21400_21583 = cljs.core.first(seq__21391_21577__$1);
var map__21400_21584__$1 = cljs.core.__destructure_map(map__21400_21583);
var task_21585 = map__21400_21584__$1;
var fn_str_21586 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21400_21584__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21587 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21400_21584__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21588 = goog.getObjectByName(fn_str_21586,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21587)));

(fn_obj_21588.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21588.cljs$core$IFn$_invoke$arity$2(path,new_link_21561) : fn_obj_21588.call(null,path,new_link_21561));


var G__21589 = cljs.core.next(seq__21391_21577__$1);
var G__21590 = null;
var G__21591 = (0);
var G__21592 = (0);
seq__21391_21562 = G__21589;
chunk__21393_21563 = G__21590;
count__21394_21564 = G__21591;
i__21395_21565 = G__21592;
continue;
}
} else {
}
}
break;
}

return goog.dom.removeNode(node_21559);
});})(seq__21357_21555,chunk__21361_21556,count__21362_21557,i__21363_21558,seq__21096,chunk__21098,count__21099,i__21100,new_link_21561,path_match_21560,node_21559,path,map__21095,map__21095__$1,msg,updates,reload_info))
);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load CSS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([path_match_21560], 0));

goog.dom.insertSiblingAfter(new_link_21561,node_21559);


var G__21593 = seq__21357_21555;
var G__21594 = chunk__21361_21556;
var G__21595 = count__21362_21557;
var G__21596 = (i__21363_21558 + (1));
seq__21357_21555 = G__21593;
chunk__21361_21556 = G__21594;
count__21362_21557 = G__21595;
i__21363_21558 = G__21596;
continue;
} else {
var G__21597 = seq__21357_21555;
var G__21598 = chunk__21361_21556;
var G__21599 = count__21362_21557;
var G__21600 = (i__21363_21558 + (1));
seq__21357_21555 = G__21597;
chunk__21361_21556 = G__21598;
count__21362_21557 = G__21599;
i__21363_21558 = G__21600;
continue;
}
} else {
var G__21601 = seq__21357_21555;
var G__21602 = chunk__21361_21556;
var G__21603 = count__21362_21557;
var G__21604 = (i__21363_21558 + (1));
seq__21357_21555 = G__21601;
chunk__21361_21556 = G__21602;
count__21362_21557 = G__21603;
i__21363_21558 = G__21604;
continue;
}
} else {
var temp__5823__auto___21605 = cljs.core.seq(seq__21357_21555);
if(temp__5823__auto___21605){
var seq__21357_21606__$1 = temp__5823__auto___21605;
if(cljs.core.chunked_seq_QMARK_(seq__21357_21606__$1)){
var c__5673__auto___21607 = cljs.core.chunk_first(seq__21357_21606__$1);
var G__21608 = cljs.core.chunk_rest(seq__21357_21606__$1);
var G__21609 = c__5673__auto___21607;
var G__21610 = cljs.core.count(c__5673__auto___21607);
var G__21611 = (0);
seq__21357_21555 = G__21608;
chunk__21361_21556 = G__21609;
count__21362_21557 = G__21610;
i__21363_21558 = G__21611;
continue;
} else {
var node_21612 = cljs.core.first(seq__21357_21606__$1);
if(cljs.core.not(node_21612.shadow$old)){
var path_match_21613 = shadow.cljs.devtools.client.browser.match_paths(node_21612.getAttribute("href"),path);
if(cljs.core.truth_(path_match_21613)){
var new_link_21614 = (function (){var G__21401 = node_21612.cloneNode(true);
G__21401.setAttribute("href",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(path_match_21613)+"?r="+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.rand.cljs$core$IFn$_invoke$arity$0())));

return G__21401;
})();
(node_21612.shadow$old = true);

(new_link_21614.onload = ((function (seq__21357_21555,chunk__21361_21556,count__21362_21557,i__21363_21558,seq__21096,chunk__21098,count__21099,i__21100,new_link_21614,path_match_21613,node_21612,seq__21357_21606__$1,temp__5823__auto___21605,path,map__21095,map__21095__$1,msg,updates,reload_info){
return (function (e){
var seq__21402_21615 = cljs.core.seq(cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(msg,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"reload-info","reload-info",1648088086),new cljs.core.Keyword(null,"asset-load","asset-load",-1925902322)], null)));
var chunk__21404_21616 = null;
var count__21405_21617 = (0);
var i__21406_21618 = (0);
while(true){
if((i__21406_21618 < count__21405_21617)){
var map__21410_21619 = chunk__21404_21616.cljs$core$IIndexed$_nth$arity$2(null,i__21406_21618);
var map__21410_21620__$1 = cljs.core.__destructure_map(map__21410_21619);
var task_21621 = map__21410_21620__$1;
var fn_str_21622 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21410_21620__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21623 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21410_21620__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21624 = goog.getObjectByName(fn_str_21622,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21623)));

(fn_obj_21624.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21624.cljs$core$IFn$_invoke$arity$2(path,new_link_21614) : fn_obj_21624.call(null,path,new_link_21614));


var G__21625 = seq__21402_21615;
var G__21626 = chunk__21404_21616;
var G__21627 = count__21405_21617;
var G__21628 = (i__21406_21618 + (1));
seq__21402_21615 = G__21625;
chunk__21404_21616 = G__21626;
count__21405_21617 = G__21627;
i__21406_21618 = G__21628;
continue;
} else {
var temp__5823__auto___21629__$1 = cljs.core.seq(seq__21402_21615);
if(temp__5823__auto___21629__$1){
var seq__21402_21630__$1 = temp__5823__auto___21629__$1;
if(cljs.core.chunked_seq_QMARK_(seq__21402_21630__$1)){
var c__5673__auto___21631 = cljs.core.chunk_first(seq__21402_21630__$1);
var G__21632 = cljs.core.chunk_rest(seq__21402_21630__$1);
var G__21633 = c__5673__auto___21631;
var G__21634 = cljs.core.count(c__5673__auto___21631);
var G__21635 = (0);
seq__21402_21615 = G__21632;
chunk__21404_21616 = G__21633;
count__21405_21617 = G__21634;
i__21406_21618 = G__21635;
continue;
} else {
var map__21411_21636 = cljs.core.first(seq__21402_21630__$1);
var map__21411_21637__$1 = cljs.core.__destructure_map(map__21411_21636);
var task_21638 = map__21411_21637__$1;
var fn_str_21639 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21411_21637__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21640 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21411_21637__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21641 = goog.getObjectByName(fn_str_21639,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21640)));

(fn_obj_21641.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21641.cljs$core$IFn$_invoke$arity$2(path,new_link_21614) : fn_obj_21641.call(null,path,new_link_21614));


var G__21642 = cljs.core.next(seq__21402_21630__$1);
var G__21643 = null;
var G__21644 = (0);
var G__21645 = (0);
seq__21402_21615 = G__21642;
chunk__21404_21616 = G__21643;
count__21405_21617 = G__21644;
i__21406_21618 = G__21645;
continue;
}
} else {
}
}
break;
}

return goog.dom.removeNode(node_21612);
});})(seq__21357_21555,chunk__21361_21556,count__21362_21557,i__21363_21558,seq__21096,chunk__21098,count__21099,i__21100,new_link_21614,path_match_21613,node_21612,seq__21357_21606__$1,temp__5823__auto___21605,path,map__21095,map__21095__$1,msg,updates,reload_info))
);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load CSS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([path_match_21613], 0));

goog.dom.insertSiblingAfter(new_link_21614,node_21612);


var G__21646 = cljs.core.next(seq__21357_21606__$1);
var G__21647 = null;
var G__21648 = (0);
var G__21649 = (0);
seq__21357_21555 = G__21646;
chunk__21361_21556 = G__21647;
count__21362_21557 = G__21648;
i__21363_21558 = G__21649;
continue;
} else {
var G__21650 = cljs.core.next(seq__21357_21606__$1);
var G__21651 = null;
var G__21652 = (0);
var G__21653 = (0);
seq__21357_21555 = G__21650;
chunk__21361_21556 = G__21651;
count__21362_21557 = G__21652;
i__21363_21558 = G__21653;
continue;
}
} else {
var G__21654 = cljs.core.next(seq__21357_21606__$1);
var G__21655 = null;
var G__21656 = (0);
var G__21657 = (0);
seq__21357_21555 = G__21654;
chunk__21361_21556 = G__21655;
count__21362_21557 = G__21656;
i__21363_21558 = G__21657;
continue;
}
}
} else {
}
}
break;
}


var G__21658 = seq__21096;
var G__21659 = chunk__21098;
var G__21660 = count__21099;
var G__21661 = (i__21100 + (1));
seq__21096 = G__21658;
chunk__21098 = G__21659;
count__21099 = G__21660;
i__21100 = G__21661;
continue;
} else {
var G__21662 = seq__21096;
var G__21663 = chunk__21098;
var G__21664 = count__21099;
var G__21665 = (i__21100 + (1));
seq__21096 = G__21662;
chunk__21098 = G__21663;
count__21099 = G__21664;
i__21100 = G__21665;
continue;
}
} else {
var temp__5823__auto__ = cljs.core.seq(seq__21096);
if(temp__5823__auto__){
var seq__21096__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__21096__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__21096__$1);
var G__21666 = cljs.core.chunk_rest(seq__21096__$1);
var G__21667 = c__5673__auto__;
var G__21668 = cljs.core.count(c__5673__auto__);
var G__21669 = (0);
seq__21096 = G__21666;
chunk__21098 = G__21667;
count__21099 = G__21668;
i__21100 = G__21669;
continue;
} else {
var path = cljs.core.first(seq__21096__$1);
if(clojure.string.ends_with_QMARK_(path,"css")){
var seq__21412_21670 = cljs.core.seq(cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(document.querySelectorAll("link[rel=\"stylesheet\"]")));
var chunk__21416_21671 = null;
var count__21417_21672 = (0);
var i__21418_21673 = (0);
while(true){
if((i__21418_21673 < count__21417_21672)){
var node_21674 = chunk__21416_21671.cljs$core$IIndexed$_nth$arity$2(null,i__21418_21673);
if(cljs.core.not(node_21674.shadow$old)){
var path_match_21675 = shadow.cljs.devtools.client.browser.match_paths(node_21674.getAttribute("href"),path);
if(cljs.core.truth_(path_match_21675)){
var new_link_21676 = (function (){var G__21444 = node_21674.cloneNode(true);
G__21444.setAttribute("href",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(path_match_21675)+"?r="+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.rand.cljs$core$IFn$_invoke$arity$0())));

return G__21444;
})();
(node_21674.shadow$old = true);

(new_link_21676.onload = ((function (seq__21412_21670,chunk__21416_21671,count__21417_21672,i__21418_21673,seq__21096,chunk__21098,count__21099,i__21100,new_link_21676,path_match_21675,node_21674,path,seq__21096__$1,temp__5823__auto__,map__21095,map__21095__$1,msg,updates,reload_info){
return (function (e){
var seq__21445_21677 = cljs.core.seq(cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(msg,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"reload-info","reload-info",1648088086),new cljs.core.Keyword(null,"asset-load","asset-load",-1925902322)], null)));
var chunk__21447_21678 = null;
var count__21448_21679 = (0);
var i__21449_21680 = (0);
while(true){
if((i__21449_21680 < count__21448_21679)){
var map__21453_21681 = chunk__21447_21678.cljs$core$IIndexed$_nth$arity$2(null,i__21449_21680);
var map__21453_21682__$1 = cljs.core.__destructure_map(map__21453_21681);
var task_21683 = map__21453_21682__$1;
var fn_str_21684 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21453_21682__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21685 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21453_21682__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21686 = goog.getObjectByName(fn_str_21684,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21685)));

(fn_obj_21686.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21686.cljs$core$IFn$_invoke$arity$2(path,new_link_21676) : fn_obj_21686.call(null,path,new_link_21676));


var G__21687 = seq__21445_21677;
var G__21688 = chunk__21447_21678;
var G__21689 = count__21448_21679;
var G__21690 = (i__21449_21680 + (1));
seq__21445_21677 = G__21687;
chunk__21447_21678 = G__21688;
count__21448_21679 = G__21689;
i__21449_21680 = G__21690;
continue;
} else {
var temp__5823__auto___21691__$1 = cljs.core.seq(seq__21445_21677);
if(temp__5823__auto___21691__$1){
var seq__21445_21692__$1 = temp__5823__auto___21691__$1;
if(cljs.core.chunked_seq_QMARK_(seq__21445_21692__$1)){
var c__5673__auto___21693 = cljs.core.chunk_first(seq__21445_21692__$1);
var G__21694 = cljs.core.chunk_rest(seq__21445_21692__$1);
var G__21695 = c__5673__auto___21693;
var G__21696 = cljs.core.count(c__5673__auto___21693);
var G__21697 = (0);
seq__21445_21677 = G__21694;
chunk__21447_21678 = G__21695;
count__21448_21679 = G__21696;
i__21449_21680 = G__21697;
continue;
} else {
var map__21454_21698 = cljs.core.first(seq__21445_21692__$1);
var map__21454_21699__$1 = cljs.core.__destructure_map(map__21454_21698);
var task_21700 = map__21454_21699__$1;
var fn_str_21701 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21454_21699__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21702 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21454_21699__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21703 = goog.getObjectByName(fn_str_21701,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21702)));

(fn_obj_21703.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21703.cljs$core$IFn$_invoke$arity$2(path,new_link_21676) : fn_obj_21703.call(null,path,new_link_21676));


var G__21704 = cljs.core.next(seq__21445_21692__$1);
var G__21705 = null;
var G__21706 = (0);
var G__21707 = (0);
seq__21445_21677 = G__21704;
chunk__21447_21678 = G__21705;
count__21448_21679 = G__21706;
i__21449_21680 = G__21707;
continue;
}
} else {
}
}
break;
}

return goog.dom.removeNode(node_21674);
});})(seq__21412_21670,chunk__21416_21671,count__21417_21672,i__21418_21673,seq__21096,chunk__21098,count__21099,i__21100,new_link_21676,path_match_21675,node_21674,path,seq__21096__$1,temp__5823__auto__,map__21095,map__21095__$1,msg,updates,reload_info))
);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load CSS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([path_match_21675], 0));

goog.dom.insertSiblingAfter(new_link_21676,node_21674);


var G__21708 = seq__21412_21670;
var G__21709 = chunk__21416_21671;
var G__21710 = count__21417_21672;
var G__21711 = (i__21418_21673 + (1));
seq__21412_21670 = G__21708;
chunk__21416_21671 = G__21709;
count__21417_21672 = G__21710;
i__21418_21673 = G__21711;
continue;
} else {
var G__21712 = seq__21412_21670;
var G__21713 = chunk__21416_21671;
var G__21714 = count__21417_21672;
var G__21715 = (i__21418_21673 + (1));
seq__21412_21670 = G__21712;
chunk__21416_21671 = G__21713;
count__21417_21672 = G__21714;
i__21418_21673 = G__21715;
continue;
}
} else {
var G__21716 = seq__21412_21670;
var G__21717 = chunk__21416_21671;
var G__21718 = count__21417_21672;
var G__21719 = (i__21418_21673 + (1));
seq__21412_21670 = G__21716;
chunk__21416_21671 = G__21717;
count__21417_21672 = G__21718;
i__21418_21673 = G__21719;
continue;
}
} else {
var temp__5823__auto___21720__$1 = cljs.core.seq(seq__21412_21670);
if(temp__5823__auto___21720__$1){
var seq__21412_21721__$1 = temp__5823__auto___21720__$1;
if(cljs.core.chunked_seq_QMARK_(seq__21412_21721__$1)){
var c__5673__auto___21722 = cljs.core.chunk_first(seq__21412_21721__$1);
var G__21723 = cljs.core.chunk_rest(seq__21412_21721__$1);
var G__21724 = c__5673__auto___21722;
var G__21725 = cljs.core.count(c__5673__auto___21722);
var G__21726 = (0);
seq__21412_21670 = G__21723;
chunk__21416_21671 = G__21724;
count__21417_21672 = G__21725;
i__21418_21673 = G__21726;
continue;
} else {
var node_21727 = cljs.core.first(seq__21412_21721__$1);
if(cljs.core.not(node_21727.shadow$old)){
var path_match_21728 = shadow.cljs.devtools.client.browser.match_paths(node_21727.getAttribute("href"),path);
if(cljs.core.truth_(path_match_21728)){
var new_link_21729 = (function (){var G__21455 = node_21727.cloneNode(true);
G__21455.setAttribute("href",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(path_match_21728)+"?r="+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.rand.cljs$core$IFn$_invoke$arity$0())));

return G__21455;
})();
(node_21727.shadow$old = true);

(new_link_21729.onload = ((function (seq__21412_21670,chunk__21416_21671,count__21417_21672,i__21418_21673,seq__21096,chunk__21098,count__21099,i__21100,new_link_21729,path_match_21728,node_21727,seq__21412_21721__$1,temp__5823__auto___21720__$1,path,seq__21096__$1,temp__5823__auto__,map__21095,map__21095__$1,msg,updates,reload_info){
return (function (e){
var seq__21456_21730 = cljs.core.seq(cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(msg,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"reload-info","reload-info",1648088086),new cljs.core.Keyword(null,"asset-load","asset-load",-1925902322)], null)));
var chunk__21458_21731 = null;
var count__21459_21732 = (0);
var i__21460_21733 = (0);
while(true){
if((i__21460_21733 < count__21459_21732)){
var map__21464_21734 = chunk__21458_21731.cljs$core$IIndexed$_nth$arity$2(null,i__21460_21733);
var map__21464_21735__$1 = cljs.core.__destructure_map(map__21464_21734);
var task_21736 = map__21464_21735__$1;
var fn_str_21737 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21464_21735__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21738 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21464_21735__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21739 = goog.getObjectByName(fn_str_21737,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21738)));

(fn_obj_21739.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21739.cljs$core$IFn$_invoke$arity$2(path,new_link_21729) : fn_obj_21739.call(null,path,new_link_21729));


var G__21740 = seq__21456_21730;
var G__21741 = chunk__21458_21731;
var G__21742 = count__21459_21732;
var G__21743 = (i__21460_21733 + (1));
seq__21456_21730 = G__21740;
chunk__21458_21731 = G__21741;
count__21459_21732 = G__21742;
i__21460_21733 = G__21743;
continue;
} else {
var temp__5823__auto___21744__$2 = cljs.core.seq(seq__21456_21730);
if(temp__5823__auto___21744__$2){
var seq__21456_21745__$1 = temp__5823__auto___21744__$2;
if(cljs.core.chunked_seq_QMARK_(seq__21456_21745__$1)){
var c__5673__auto___21746 = cljs.core.chunk_first(seq__21456_21745__$1);
var G__21747 = cljs.core.chunk_rest(seq__21456_21745__$1);
var G__21748 = c__5673__auto___21746;
var G__21749 = cljs.core.count(c__5673__auto___21746);
var G__21750 = (0);
seq__21456_21730 = G__21747;
chunk__21458_21731 = G__21748;
count__21459_21732 = G__21749;
i__21460_21733 = G__21750;
continue;
} else {
var map__21465_21751 = cljs.core.first(seq__21456_21745__$1);
var map__21465_21752__$1 = cljs.core.__destructure_map(map__21465_21751);
var task_21753 = map__21465_21752__$1;
var fn_str_21754 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21465_21752__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21755 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21465_21752__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21756 = goog.getObjectByName(fn_str_21754,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21755)));

(fn_obj_21756.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21756.cljs$core$IFn$_invoke$arity$2(path,new_link_21729) : fn_obj_21756.call(null,path,new_link_21729));


var G__21757 = cljs.core.next(seq__21456_21745__$1);
var G__21758 = null;
var G__21759 = (0);
var G__21760 = (0);
seq__21456_21730 = G__21757;
chunk__21458_21731 = G__21758;
count__21459_21732 = G__21759;
i__21460_21733 = G__21760;
continue;
}
} else {
}
}
break;
}

return goog.dom.removeNode(node_21727);
});})(seq__21412_21670,chunk__21416_21671,count__21417_21672,i__21418_21673,seq__21096,chunk__21098,count__21099,i__21100,new_link_21729,path_match_21728,node_21727,seq__21412_21721__$1,temp__5823__auto___21720__$1,path,seq__21096__$1,temp__5823__auto__,map__21095,map__21095__$1,msg,updates,reload_info))
);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load CSS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([path_match_21728], 0));

goog.dom.insertSiblingAfter(new_link_21729,node_21727);


var G__21761 = cljs.core.next(seq__21412_21721__$1);
var G__21762 = null;
var G__21763 = (0);
var G__21764 = (0);
seq__21412_21670 = G__21761;
chunk__21416_21671 = G__21762;
count__21417_21672 = G__21763;
i__21418_21673 = G__21764;
continue;
} else {
var G__21765 = cljs.core.next(seq__21412_21721__$1);
var G__21766 = null;
var G__21767 = (0);
var G__21768 = (0);
seq__21412_21670 = G__21765;
chunk__21416_21671 = G__21766;
count__21417_21672 = G__21767;
i__21418_21673 = G__21768;
continue;
}
} else {
var G__21769 = cljs.core.next(seq__21412_21721__$1);
var G__21770 = null;
var G__21771 = (0);
var G__21772 = (0);
seq__21412_21670 = G__21769;
chunk__21416_21671 = G__21770;
count__21417_21672 = G__21771;
i__21418_21673 = G__21772;
continue;
}
}
} else {
}
}
break;
}


var G__21773 = cljs.core.next(seq__21096__$1);
var G__21774 = null;
var G__21775 = (0);
var G__21776 = (0);
seq__21096 = G__21773;
chunk__21098 = G__21774;
count__21099 = G__21775;
i__21100 = G__21776;
continue;
} else {
var G__21777 = cljs.core.next(seq__21096__$1);
var G__21778 = null;
var G__21779 = (0);
var G__21780 = (0);
seq__21096 = G__21777;
chunk__21098 = G__21778;
count__21099 = G__21779;
i__21100 = G__21780;
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
shadow.cljs.devtools.client.browser.global_eval = (function shadow$cljs$devtools$client$browser$global_eval(js){
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("undefined",typeof(module))){
return eval(js);
} else {
return (0,eval)(js);;
}
});
shadow.cljs.devtools.client.browser.runtime_info = (((typeof SHADOW_CONFIG !== 'undefined'))?shadow.json.to_clj.cljs$core$IFn$_invoke$arity$1(SHADOW_CONFIG):null);
shadow.cljs.devtools.client.browser.client_info = cljs.core.merge.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([shadow.cljs.devtools.client.browser.runtime_info,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"host","host",-1558485167),(cljs.core.truth_(goog.global.document)?new cljs.core.Keyword(null,"browser","browser",828191719):new cljs.core.Keyword(null,"browser-worker","browser-worker",1638998282)),new cljs.core.Keyword(null,"user-agent","user-agent",1220426212),(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((cljs.core.truth_(goog.userAgent.OPERA)?"Opera":(cljs.core.truth_(goog.userAgent.product.CHROME)?"Chrome":(cljs.core.truth_(goog.userAgent.IE)?"MSIE":(cljs.core.truth_(goog.userAgent.EDGE)?"Edge":(cljs.core.truth_(goog.userAgent.GECKO)?"Firefox":(cljs.core.truth_(goog.userAgent.SAFARI)?"Safari":(cljs.core.truth_(goog.userAgent.WEBKIT)?"Webkit":null))))))))+" "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(goog.userAgent.VERSION)+" ["+cljs.core.str.cljs$core$IFn$_invoke$arity$1(goog.userAgent.PLATFORM)+"]"),new cljs.core.Keyword(null,"dom","dom",-1236537922),(!((goog.global.document == null)))], null)], 0));
if((typeof shadow !== 'undefined') && (typeof shadow.cljs !== 'undefined') && (typeof shadow.cljs.devtools !== 'undefined') && (typeof shadow.cljs.devtools.client !== 'undefined') && (typeof shadow.cljs.devtools.client.browser !== 'undefined') && (typeof shadow.cljs.devtools.client.browser.ws_was_welcome_ref !== 'undefined')){
} else {
shadow.cljs.devtools.client.browser.ws_was_welcome_ref = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(false);
}
if(((shadow.cljs.devtools.client.env.enabled) && ((shadow.cljs.devtools.client.env.worker_client_id > (0))))){
(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$remote$runtime$api$IEvalJS$ = cljs.core.PROTOCOL_SENTINEL);

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$remote$runtime$api$IEvalJS$_js_eval$arity$4 = (function (this$,code,success,fail){
var this$__$1 = this;
try{var G__21467 = shadow.cljs.devtools.client.browser.global_eval(code);
return (success.cljs$core$IFn$_invoke$arity$1 ? success.cljs$core$IFn$_invoke$arity$1(G__21467) : success.call(null,G__21467));
}catch (e21466){var e = e21466;
return (fail.cljs$core$IFn$_invoke$arity$1 ? fail.cljs$core$IFn$_invoke$arity$1(e) : fail.call(null,e));
}}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IHostSpecific$ = cljs.core.PROTOCOL_SENTINEL);

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IHostSpecific$do_invoke$arity$5 = (function (this$,ns,p__21468,success,fail){
var map__21469 = p__21468;
var map__21469__$1 = cljs.core.__destructure_map(map__21469);
var js = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21469__$1,new cljs.core.Keyword(null,"js","js",1768080579));
var this$__$1 = this;
try{var G__21471 = shadow.cljs.devtools.client.browser.global_eval(js);
return (success.cljs$core$IFn$_invoke$arity$1 ? success.cljs$core$IFn$_invoke$arity$1(G__21471) : success.call(null,G__21471));
}catch (e21470){var e = e21470;
return (fail.cljs$core$IFn$_invoke$arity$1 ? fail.cljs$core$IFn$_invoke$arity$1(e) : fail.call(null,e));
}}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IHostSpecific$do_repl_init$arity$4 = (function (runtime,p__21472,done,error){
var map__21473 = p__21472;
var map__21473__$1 = cljs.core.__destructure_map(map__21473);
var repl_sources = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21473__$1,new cljs.core.Keyword(null,"repl-sources","repl-sources",723867535));
var runtime__$1 = this;
return shadow.cljs.devtools.client.shared.load_sources(runtime__$1,cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentVector.EMPTY,cljs.core.remove.cljs$core$IFn$_invoke$arity$2(shadow.cljs.devtools.client.env.src_is_loaded_QMARK_,repl_sources)),(function (sources){
shadow.cljs.devtools.client.browser.do_js_load(sources);

return (done.cljs$core$IFn$_invoke$arity$0 ? done.cljs$core$IFn$_invoke$arity$0() : done.call(null));
}));
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IHostSpecific$do_repl_require$arity$4 = (function (runtime,p__21474,done,error){
var map__21475 = p__21474;
var map__21475__$1 = cljs.core.__destructure_map(map__21475);
var msg = map__21475__$1;
var sources = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21475__$1,new cljs.core.Keyword(null,"sources","sources",-321166424));
var reload_namespaces = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21475__$1,new cljs.core.Keyword(null,"reload-namespaces","reload-namespaces",250210134));
var js_requires = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21475__$1,new cljs.core.Keyword(null,"js-requires","js-requires",-1311472051));
var runtime__$1 = this;
var sources_to_load = cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentVector.EMPTY,cljs.core.remove.cljs$core$IFn$_invoke$arity$2((function (p__21476){
var map__21477 = p__21476;
var map__21477__$1 = cljs.core.__destructure_map(map__21477);
var src = map__21477__$1;
var provides = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21477__$1,new cljs.core.Keyword(null,"provides","provides",-1634397992));
var and__5140__auto__ = shadow.cljs.devtools.client.env.src_is_loaded_QMARK_(src);
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(cljs.core.some(reload_namespaces,provides));
} else {
return and__5140__auto__;
}
}),sources));
if(cljs.core.not(cljs.core.seq(sources_to_load))){
var G__21478 = cljs.core.PersistentVector.EMPTY;
return (done.cljs$core$IFn$_invoke$arity$1 ? done.cljs$core$IFn$_invoke$arity$1(G__21478) : done.call(null,G__21478));
} else {
return shadow.remote.runtime.shared.call.cljs$core$IFn$_invoke$arity$3(runtime__$1,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"op","op",-1882987955),new cljs.core.Keyword(null,"cljs-load-sources","cljs-load-sources",-1458295962),new cljs.core.Keyword(null,"to","to",192099007),shadow.cljs.devtools.client.env.worker_client_id,new cljs.core.Keyword(null,"sources","sources",-321166424),cljs.core.into.cljs$core$IFn$_invoke$arity$3(cljs.core.PersistentVector.EMPTY,cljs.core.map.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"resource-id","resource-id",-1308422582)),sources_to_load)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"cljs-sources","cljs-sources",31121610),(function (p__21479){
var map__21480 = p__21479;
var map__21480__$1 = cljs.core.__destructure_map(map__21480);
var msg__$1 = map__21480__$1;
var sources__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21480__$1,new cljs.core.Keyword(null,"sources","sources",-321166424));
try{shadow.cljs.devtools.client.browser.do_js_load(sources__$1);

if(cljs.core.seq(js_requires)){
shadow.cljs.devtools.client.browser.do_js_requires(js_requires);
} else {
}

return (done.cljs$core$IFn$_invoke$arity$1 ? done.cljs$core$IFn$_invoke$arity$1(sources_to_load) : done.call(null,sources_to_load));
}catch (e21481){var ex = e21481;
return (error.cljs$core$IFn$_invoke$arity$1 ? error.cljs$core$IFn$_invoke$arity$1(ex) : error.call(null,ex));
}})], null));
}
}));

shadow.cljs.devtools.client.shared.add_plugin_BANG_(new cljs.core.Keyword("shadow.cljs.devtools.client.browser","client","shadow.cljs.devtools.client.browser/client",-1461019282),cljs.core.PersistentHashSet.EMPTY,(function (p__21482){
var map__21483 = p__21482;
var map__21483__$1 = cljs.core.__destructure_map(map__21483);
var env = map__21483__$1;
var runtime = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21483__$1,new cljs.core.Keyword(null,"runtime","runtime",-1331573996));
var svc = new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"runtime","runtime",-1331573996),runtime], null);
shadow.remote.runtime.api.add_extension(runtime,new cljs.core.Keyword("shadow.cljs.devtools.client.browser","client","shadow.cljs.devtools.client.browser/client",-1461019282),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"on-welcome","on-welcome",1895317125),(function (){
cljs.core.reset_BANG_(shadow.cljs.devtools.client.browser.ws_was_welcome_ref,true);

shadow.cljs.devtools.client.hud.connection_error_clear_BANG_();

shadow.cljs.devtools.client.env.patch_goog_BANG_();

return shadow.cljs.devtools.client.browser.devtools_msg((""+"#"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"client-id","client-id",-464622140).cljs$core$IFn$_invoke$arity$1(cljs.core.deref(new cljs.core.Keyword(null,"state-ref","state-ref",2127874952).cljs$core$IFn$_invoke$arity$1(runtime))))+" ready!"));
}),new cljs.core.Keyword(null,"on-disconnect","on-disconnect",-809021814),(function (e){
if(cljs.core.truth_(cljs.core.deref(shadow.cljs.devtools.client.browser.ws_was_welcome_ref))){
shadow.cljs.devtools.client.hud.connection_error("The Websocket connection was closed!");

return cljs.core.reset_BANG_(shadow.cljs.devtools.client.browser.ws_was_welcome_ref,false);
} else {
return null;
}
}),new cljs.core.Keyword(null,"on-reconnect","on-reconnect",1239988702),(function (e){
return shadow.cljs.devtools.client.hud.connection_error("Reconnecting ...");
}),new cljs.core.Keyword(null,"ops","ops",1237330063),new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"access-denied","access-denied",959449406),(function (msg){
cljs.core.reset_BANG_(shadow.cljs.devtools.client.browser.ws_was_welcome_ref,false);

return shadow.cljs.devtools.client.hud.connection_error((""+"Stale Output! Your loaded JS was not produced by the running shadow-cljs instance."+" Is the watch for this build running?"));
}),new cljs.core.Keyword(null,"cljs-asset-update","cljs-asset-update",1224093028),(function (msg){
return shadow.cljs.devtools.client.browser.handle_asset_update(msg);
}),new cljs.core.Keyword(null,"cljs-build-configure","cljs-build-configure",-2089891268),(function (msg){
return null;
}),new cljs.core.Keyword(null,"cljs-build-start","cljs-build-start",-725781241),(function (msg){
shadow.cljs.devtools.client.hud.hud_hide();

shadow.cljs.devtools.client.hud.load_start();

return shadow.cljs.devtools.client.env.run_custom_notify_BANG_(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(msg,new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"build-start","build-start",-959649480)));
}),new cljs.core.Keyword(null,"cljs-build-complete","cljs-build-complete",273626153),(function (msg){
var msg__$1 = shadow.cljs.devtools.client.env.add_warnings_to_info(msg);
shadow.cljs.devtools.client.hud.connection_error_clear_BANG_();

shadow.cljs.devtools.client.hud.hud_warnings(msg__$1);

shadow.cljs.devtools.client.browser.handle_build_complete(runtime,msg__$1);

return shadow.cljs.devtools.client.env.run_custom_notify_BANG_(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(msg__$1,new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"build-complete","build-complete",-501868472)));
}),new cljs.core.Keyword(null,"cljs-build-failure","cljs-build-failure",1718154990),(function (msg){
shadow.cljs.devtools.client.hud.load_end();

shadow.cljs.devtools.client.hud.hud_error(msg);

return shadow.cljs.devtools.client.env.run_custom_notify_BANG_(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(msg,new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"build-failure","build-failure",-2107487466)));
}),new cljs.core.Keyword("shadow.cljs.devtools.client.env","worker-notify","shadow.cljs.devtools.client.env/worker-notify",-1456820670),(function (p__21484){
var map__21485 = p__21484;
var map__21485__$1 = cljs.core.__destructure_map(map__21485);
var event_op = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21485__$1,new cljs.core.Keyword(null,"event-op","event-op",200358057));
var client_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21485__$1,new cljs.core.Keyword(null,"client-id","client-id",-464622140));
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"client-disconnect","client-disconnect",640227957),event_op)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(client_id,shadow.cljs.devtools.client.env.worker_client_id)))){
shadow.cljs.devtools.client.hud.connection_error_clear_BANG_();

return shadow.cljs.devtools.client.hud.connection_error("The watch for this build was stopped!");
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"client-connect","client-connect",-1113973888),event_op)){
shadow.cljs.devtools.client.hud.connection_error_clear_BANG_();

return shadow.cljs.devtools.client.hud.connection_error("The watch for this build was restarted. Reload required!");
} else {
return null;
}
}
})], null)], null));

return svc;
}),(function (p__21486){
var map__21487 = p__21486;
var map__21487__$1 = cljs.core.__destructure_map(map__21487);
var svc = map__21487__$1;
var runtime = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21487__$1,new cljs.core.Keyword(null,"runtime","runtime",-1331573996));
return shadow.remote.runtime.api.del_extension(runtime,new cljs.core.Keyword("shadow.cljs.devtools.client.browser","client","shadow.cljs.devtools.client.browser/client",-1461019282));
}));

shadow.cljs.devtools.client.shared.init_runtime_BANG_(shadow.cljs.devtools.client.browser.client_info,shadow.cljs.devtools.client.websocket.start,shadow.cljs.devtools.client.websocket.send,shadow.cljs.devtools.client.websocket.stop);
} else {
}

//# sourceMappingURL=shadow.cljs.devtools.client.browser.js.map
