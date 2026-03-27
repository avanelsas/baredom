goog.provide('shadow.cljs.devtools.client.browser');
shadow.cljs.devtools.client.browser.devtools_msg = (function shadow$cljs$devtools$client$browser$devtools_msg(var_args){
var args__5882__auto__ = [];
var len__5876__auto___21404 = arguments.length;
var i__5877__auto___21405 = (0);
while(true){
if((i__5877__auto___21405 < len__5876__auto___21404)){
args__5882__auto__.push((arguments[i__5877__auto___21405]));

var G__21406 = (i__5877__auto___21405 + (1));
i__5877__auto___21405 = G__21406;
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
(shadow.cljs.devtools.client.browser.devtools_msg.cljs$lang$applyTo = (function (seq20778){
var G__20779 = cljs.core.first(seq20778);
var seq20778__$1 = cljs.core.next(seq20778);
var self__5861__auto__ = this;
return self__5861__auto__.cljs$core$IFn$_invoke$arity$variadic(G__20779,seq20778__$1);
}));

shadow.cljs.devtools.client.browser.script_eval = (function shadow$cljs$devtools$client$browser$script_eval(code){
return goog.globalEval(code);
});
shadow.cljs.devtools.client.browser.do_js_load = (function shadow$cljs$devtools$client$browser$do_js_load(sources){
var seq__20788 = cljs.core.seq(sources);
var chunk__20789 = null;
var count__20790 = (0);
var i__20791 = (0);
while(true){
if((i__20791 < count__20790)){
var map__20798 = chunk__20789.cljs$core$IIndexed$_nth$arity$2(null,i__20791);
var map__20798__$1 = cljs.core.__destructure_map(map__20798);
var src = map__20798__$1;
var resource_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20798__$1,new cljs.core.Keyword(null,"resource-id","resource-id",-1308422582));
var output_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20798__$1,new cljs.core.Keyword(null,"output-name","output-name",-1769107767));
var resource_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20798__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
var js = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20798__$1,new cljs.core.Keyword(null,"js","js",1768080579));
$CLJS.SHADOW_ENV.setLoaded(output_name);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load JS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([resource_name], 0));

shadow.cljs.devtools.client.env.before_load_src(src);

try{shadow.cljs.devtools.client.browser.script_eval((""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(js)+"\n//# sourceURL="+cljs.core.str.cljs$core$IFn$_invoke$arity$1($CLJS.SHADOW_ENV.scriptBase)+cljs.core.str.cljs$core$IFn$_invoke$arity$1(output_name)));
}catch (e20799){var e_21409 = e20799;
if(shadow.cljs.devtools.client.env.log){
console.error((""+"Failed to load "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name)),e_21409);
} else {
}

throw (new Error((""+"Failed to load "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name)+": "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(e_21409.message))));
}

var G__21414 = seq__20788;
var G__21415 = chunk__20789;
var G__21416 = count__20790;
var G__21417 = (i__20791 + (1));
seq__20788 = G__21414;
chunk__20789 = G__21415;
count__20790 = G__21416;
i__20791 = G__21417;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__20788);
if(temp__5823__auto__){
var seq__20788__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__20788__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__20788__$1);
var G__21422 = cljs.core.chunk_rest(seq__20788__$1);
var G__21423 = c__5673__auto__;
var G__21424 = cljs.core.count(c__5673__auto__);
var G__21425 = (0);
seq__20788 = G__21422;
chunk__20789 = G__21423;
count__20790 = G__21424;
i__20791 = G__21425;
continue;
} else {
var map__20800 = cljs.core.first(seq__20788__$1);
var map__20800__$1 = cljs.core.__destructure_map(map__20800);
var src = map__20800__$1;
var resource_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20800__$1,new cljs.core.Keyword(null,"resource-id","resource-id",-1308422582));
var output_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20800__$1,new cljs.core.Keyword(null,"output-name","output-name",-1769107767));
var resource_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20800__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
var js = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20800__$1,new cljs.core.Keyword(null,"js","js",1768080579));
$CLJS.SHADOW_ENV.setLoaded(output_name);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load JS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([resource_name], 0));

shadow.cljs.devtools.client.env.before_load_src(src);

try{shadow.cljs.devtools.client.browser.script_eval((""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(js)+"\n//# sourceURL="+cljs.core.str.cljs$core$IFn$_invoke$arity$1($CLJS.SHADOW_ENV.scriptBase)+cljs.core.str.cljs$core$IFn$_invoke$arity$1(output_name)));
}catch (e20802){var e_21431 = e20802;
if(shadow.cljs.devtools.client.env.log){
console.error((""+"Failed to load "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name)),e_21431);
} else {
}

throw (new Error((""+"Failed to load "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name)+": "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(e_21431.message))));
}

var G__21432 = cljs.core.next(seq__20788__$1);
var G__21433 = null;
var G__21434 = (0);
var G__21435 = (0);
seq__20788 = G__21432;
chunk__20789 = G__21433;
count__20790 = G__21434;
i__20791 = G__21435;
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
var seq__20820 = cljs.core.seq(js_requires);
var chunk__20821 = null;
var count__20822 = (0);
var i__20823 = (0);
while(true){
if((i__20823 < count__20822)){
var js_ns = chunk__20821.cljs$core$IIndexed$_nth$arity$2(null,i__20823);
var require_str_21448 = (""+"var "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(js_ns)+" = shadow.js.require(\""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(js_ns)+"\");");
shadow.cljs.devtools.client.browser.script_eval(require_str_21448);


var G__21449 = seq__20820;
var G__21450 = chunk__20821;
var G__21451 = count__20822;
var G__21452 = (i__20823 + (1));
seq__20820 = G__21449;
chunk__20821 = G__21450;
count__20822 = G__21451;
i__20823 = G__21452;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__20820);
if(temp__5823__auto__){
var seq__20820__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__20820__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__20820__$1);
var G__21453 = cljs.core.chunk_rest(seq__20820__$1);
var G__21454 = c__5673__auto__;
var G__21455 = cljs.core.count(c__5673__auto__);
var G__21456 = (0);
seq__20820 = G__21453;
chunk__20821 = G__21454;
count__20822 = G__21455;
i__20823 = G__21456;
continue;
} else {
var js_ns = cljs.core.first(seq__20820__$1);
var require_str_21457 = (""+"var "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(js_ns)+" = shadow.js.require(\""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(js_ns)+"\");");
shadow.cljs.devtools.client.browser.script_eval(require_str_21457);


var G__21458 = cljs.core.next(seq__20820__$1);
var G__21459 = null;
var G__21460 = (0);
var G__21461 = (0);
seq__20820 = G__21458;
chunk__20821 = G__21459;
count__20822 = G__21460;
i__20823 = G__21461;
continue;
}
} else {
return null;
}
}
break;
}
});
shadow.cljs.devtools.client.browser.handle_build_complete = (function shadow$cljs$devtools$client$browser$handle_build_complete(runtime,p__20839){
var map__20840 = p__20839;
var map__20840__$1 = cljs.core.__destructure_map(map__20840);
var msg = map__20840__$1;
var info = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20840__$1,new cljs.core.Keyword(null,"info","info",-317069002));
var reload_info = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20840__$1,new cljs.core.Keyword(null,"reload-info","reload-info",1648088086));
var warnings = cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentVector.EMPTY,cljs.core.distinct.cljs$core$IFn$_invoke$arity$1((function (){var iter__5628__auto__ = (function shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__20842(s__20843){
return (new cljs.core.LazySeq(null,(function (){
var s__20843__$1 = s__20843;
while(true){
var temp__5823__auto__ = cljs.core.seq(s__20843__$1);
if(temp__5823__auto__){
var xs__6383__auto__ = temp__5823__auto__;
var map__20850 = cljs.core.first(xs__6383__auto__);
var map__20850__$1 = cljs.core.__destructure_map(map__20850);
var src = map__20850__$1;
var resource_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20850__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
var warnings = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20850__$1,new cljs.core.Keyword(null,"warnings","warnings",-735437651));
if(cljs.core.not(new cljs.core.Keyword(null,"from-jar","from-jar",1050932827).cljs$core$IFn$_invoke$arity$1(src))){
var iterys__5624__auto__ = ((function (s__20843__$1,map__20850,map__20850__$1,src,resource_name,warnings,xs__6383__auto__,temp__5823__auto__,map__20840,map__20840__$1,msg,info,reload_info){
return (function shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__20842_$_iter__20844(s__20845){
return (new cljs.core.LazySeq(null,((function (s__20843__$1,map__20850,map__20850__$1,src,resource_name,warnings,xs__6383__auto__,temp__5823__auto__,map__20840,map__20840__$1,msg,info,reload_info){
return (function (){
var s__20845__$1 = s__20845;
while(true){
var temp__5823__auto____$1 = cljs.core.seq(s__20845__$1);
if(temp__5823__auto____$1){
var s__20845__$2 = temp__5823__auto____$1;
if(cljs.core.chunked_seq_QMARK_(s__20845__$2)){
var c__5626__auto__ = cljs.core.chunk_first(s__20845__$2);
var size__5627__auto__ = cljs.core.count(c__5626__auto__);
var b__20847 = cljs.core.chunk_buffer(size__5627__auto__);
if((function (){var i__20846 = (0);
while(true){
if((i__20846 < size__5627__auto__)){
var warning = cljs.core._nth(c__5626__auto__,i__20846);
cljs.core.chunk_append(b__20847,cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(warning,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100),resource_name));

var G__21462 = (i__20846 + (1));
i__20846 = G__21462;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons(cljs.core.chunk(b__20847),shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__20842_$_iter__20844(cljs.core.chunk_rest(s__20845__$2)));
} else {
return cljs.core.chunk_cons(cljs.core.chunk(b__20847),null);
}
} else {
var warning = cljs.core.first(s__20845__$2);
return cljs.core.cons(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(warning,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100),resource_name),shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__20842_$_iter__20844(cljs.core.rest(s__20845__$2)));
}
} else {
return null;
}
break;
}
});})(s__20843__$1,map__20850,map__20850__$1,src,resource_name,warnings,xs__6383__auto__,temp__5823__auto__,map__20840,map__20840__$1,msg,info,reload_info))
,null,null));
});})(s__20843__$1,map__20850,map__20850__$1,src,resource_name,warnings,xs__6383__auto__,temp__5823__auto__,map__20840,map__20840__$1,msg,info,reload_info))
;
var fs__5625__auto__ = cljs.core.seq(iterys__5624__auto__(warnings));
if(fs__5625__auto__){
return cljs.core.concat.cljs$core$IFn$_invoke$arity$2(fs__5625__auto__,shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__20842(cljs.core.rest(s__20843__$1)));
} else {
var G__21496 = cljs.core.rest(s__20843__$1);
s__20843__$1 = G__21496;
continue;
}
} else {
var G__21499 = cljs.core.rest(s__20843__$1);
s__20843__$1 = G__21499;
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
var seq__20855_21500 = cljs.core.seq(warnings);
var chunk__20856_21501 = null;
var count__20857_21502 = (0);
var i__20858_21503 = (0);
while(true){
if((i__20858_21503 < count__20857_21502)){
var map__20863_21504 = chunk__20856_21501.cljs$core$IIndexed$_nth$arity$2(null,i__20858_21503);
var map__20863_21505__$1 = cljs.core.__destructure_map(map__20863_21504);
var w_21506 = map__20863_21505__$1;
var msg_21507__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20863_21505__$1,new cljs.core.Keyword(null,"msg","msg",-1386103444));
var line_21508 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20863_21505__$1,new cljs.core.Keyword(null,"line","line",212345235));
var column_21509 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20863_21505__$1,new cljs.core.Keyword(null,"column","column",2078222095));
var resource_name_21510 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20863_21505__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
console.warn((""+"BUILD-WARNING in "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name_21510)+" at ["+cljs.core.str.cljs$core$IFn$_invoke$arity$1(line_21508)+":"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(column_21509)+"]\n\t"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(msg_21507__$1)));


var G__21511 = seq__20855_21500;
var G__21512 = chunk__20856_21501;
var G__21513 = count__20857_21502;
var G__21514 = (i__20858_21503 + (1));
seq__20855_21500 = G__21511;
chunk__20856_21501 = G__21512;
count__20857_21502 = G__21513;
i__20858_21503 = G__21514;
continue;
} else {
var temp__5823__auto___21515 = cljs.core.seq(seq__20855_21500);
if(temp__5823__auto___21515){
var seq__20855_21516__$1 = temp__5823__auto___21515;
if(cljs.core.chunked_seq_QMARK_(seq__20855_21516__$1)){
var c__5673__auto___21517 = cljs.core.chunk_first(seq__20855_21516__$1);
var G__21518 = cljs.core.chunk_rest(seq__20855_21516__$1);
var G__21519 = c__5673__auto___21517;
var G__21520 = cljs.core.count(c__5673__auto___21517);
var G__21521 = (0);
seq__20855_21500 = G__21518;
chunk__20856_21501 = G__21519;
count__20857_21502 = G__21520;
i__20858_21503 = G__21521;
continue;
} else {
var map__20864_21522 = cljs.core.first(seq__20855_21516__$1);
var map__20864_21523__$1 = cljs.core.__destructure_map(map__20864_21522);
var w_21524 = map__20864_21523__$1;
var msg_21525__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20864_21523__$1,new cljs.core.Keyword(null,"msg","msg",-1386103444));
var line_21526 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20864_21523__$1,new cljs.core.Keyword(null,"line","line",212345235));
var column_21527 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20864_21523__$1,new cljs.core.Keyword(null,"column","column",2078222095));
var resource_name_21528 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20864_21523__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
console.warn((""+"BUILD-WARNING in "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name_21528)+" at ["+cljs.core.str.cljs$core$IFn$_invoke$arity$1(line_21526)+":"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(column_21527)+"]\n\t"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(msg_21525__$1)));


var G__21532 = cljs.core.next(seq__20855_21516__$1);
var G__21533 = null;
var G__21534 = (0);
var G__21535 = (0);
seq__20855_21500 = G__21532;
chunk__20856_21501 = G__21533;
count__20857_21502 = G__21534;
i__20858_21503 = G__21535;
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

return shadow.cljs.devtools.client.shared.load_sources(runtime,sources_to_get,(function (p1__20838_SHARP_){
return shadow.cljs.devtools.client.browser.do_js_reload(msg,p1__20838_SHARP_,shadow.cljs.devtools.client.hud.load_end_success,shadow.cljs.devtools.client.hud.load_failure);
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
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var G__20879 = node_uri;
G__20879.setQuery(null);

G__20879.setPath(new$);

return G__20879;
})()));
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
}
});
shadow.cljs.devtools.client.browser.handle_asset_update = (function shadow$cljs$devtools$client$browser$handle_asset_update(p__20880){
var map__20883 = p__20880;
var map__20883__$1 = cljs.core.__destructure_map(map__20883);
var msg = map__20883__$1;
var updates = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20883__$1,new cljs.core.Keyword(null,"updates","updates",2013983452));
var reload_info = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20883__$1,new cljs.core.Keyword(null,"reload-info","reload-info",1648088086));
var seq__20886 = cljs.core.seq(updates);
var chunk__20888 = null;
var count__20889 = (0);
var i__20890 = (0);
while(true){
if((i__20890 < count__20889)){
var path = chunk__20888.cljs$core$IIndexed$_nth$arity$2(null,i__20890);
if(clojure.string.ends_with_QMARK_(path,"css")){
var seq__21065_21541 = cljs.core.seq(cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(document.querySelectorAll("link[rel=\"stylesheet\"]")));
var chunk__21069_21542 = null;
var count__21070_21543 = (0);
var i__21071_21544 = (0);
while(true){
if((i__21071_21544 < count__21070_21543)){
var node_21551 = chunk__21069_21542.cljs$core$IIndexed$_nth$arity$2(null,i__21071_21544);
if(cljs.core.not(node_21551.shadow$old)){
var path_match_21552 = shadow.cljs.devtools.client.browser.match_paths(node_21551.getAttribute("href"),path);
if(cljs.core.truth_(path_match_21552)){
var new_link_21553 = (function (){var G__21138 = node_21551.cloneNode(true);
G__21138.setAttribute("href",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(path_match_21552)+"?r="+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.rand.cljs$core$IFn$_invoke$arity$0())));

return G__21138;
})();
(node_21551.shadow$old = true);

(new_link_21553.onload = ((function (seq__21065_21541,chunk__21069_21542,count__21070_21543,i__21071_21544,seq__20886,chunk__20888,count__20889,i__20890,new_link_21553,path_match_21552,node_21551,path,map__20883,map__20883__$1,msg,updates,reload_info){
return (function (e){
var seq__21140_21554 = cljs.core.seq(cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(msg,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"reload-info","reload-info",1648088086),new cljs.core.Keyword(null,"asset-load","asset-load",-1925902322)], null)));
var chunk__21142_21555 = null;
var count__21143_21556 = (0);
var i__21144_21557 = (0);
while(true){
if((i__21144_21557 < count__21143_21556)){
var map__21152_21558 = chunk__21142_21555.cljs$core$IIndexed$_nth$arity$2(null,i__21144_21557);
var map__21152_21559__$1 = cljs.core.__destructure_map(map__21152_21558);
var task_21560 = map__21152_21559__$1;
var fn_str_21561 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21152_21559__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21562 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21152_21559__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21564 = goog.getObjectByName(fn_str_21561,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21562)));

(fn_obj_21564.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21564.cljs$core$IFn$_invoke$arity$2(path,new_link_21553) : fn_obj_21564.call(null,path,new_link_21553));


var G__21566 = seq__21140_21554;
var G__21567 = chunk__21142_21555;
var G__21568 = count__21143_21556;
var G__21569 = (i__21144_21557 + (1));
seq__21140_21554 = G__21566;
chunk__21142_21555 = G__21567;
count__21143_21556 = G__21568;
i__21144_21557 = G__21569;
continue;
} else {
var temp__5823__auto___21570 = cljs.core.seq(seq__21140_21554);
if(temp__5823__auto___21570){
var seq__21140_21577__$1 = temp__5823__auto___21570;
if(cljs.core.chunked_seq_QMARK_(seq__21140_21577__$1)){
var c__5673__auto___21578 = cljs.core.chunk_first(seq__21140_21577__$1);
var G__21579 = cljs.core.chunk_rest(seq__21140_21577__$1);
var G__21580 = c__5673__auto___21578;
var G__21581 = cljs.core.count(c__5673__auto___21578);
var G__21582 = (0);
seq__21140_21554 = G__21579;
chunk__21142_21555 = G__21580;
count__21143_21556 = G__21581;
i__21144_21557 = G__21582;
continue;
} else {
var map__21154_21583 = cljs.core.first(seq__21140_21577__$1);
var map__21154_21584__$1 = cljs.core.__destructure_map(map__21154_21583);
var task_21585 = map__21154_21584__$1;
var fn_str_21586 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21154_21584__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21587 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21154_21584__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21588 = goog.getObjectByName(fn_str_21586,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21587)));

(fn_obj_21588.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21588.cljs$core$IFn$_invoke$arity$2(path,new_link_21553) : fn_obj_21588.call(null,path,new_link_21553));


var G__21589 = cljs.core.next(seq__21140_21577__$1);
var G__21590 = null;
var G__21591 = (0);
var G__21592 = (0);
seq__21140_21554 = G__21589;
chunk__21142_21555 = G__21590;
count__21143_21556 = G__21591;
i__21144_21557 = G__21592;
continue;
}
} else {
}
}
break;
}

return goog.dom.removeNode(node_21551);
});})(seq__21065_21541,chunk__21069_21542,count__21070_21543,i__21071_21544,seq__20886,chunk__20888,count__20889,i__20890,new_link_21553,path_match_21552,node_21551,path,map__20883,map__20883__$1,msg,updates,reload_info))
);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load CSS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([path_match_21552], 0));

goog.dom.insertSiblingAfter(new_link_21553,node_21551);


var G__21595 = seq__21065_21541;
var G__21596 = chunk__21069_21542;
var G__21597 = count__21070_21543;
var G__21598 = (i__21071_21544 + (1));
seq__21065_21541 = G__21595;
chunk__21069_21542 = G__21596;
count__21070_21543 = G__21597;
i__21071_21544 = G__21598;
continue;
} else {
var G__21600 = seq__21065_21541;
var G__21602 = chunk__21069_21542;
var G__21603 = count__21070_21543;
var G__21604 = (i__21071_21544 + (1));
seq__21065_21541 = G__21600;
chunk__21069_21542 = G__21602;
count__21070_21543 = G__21603;
i__21071_21544 = G__21604;
continue;
}
} else {
var G__21605 = seq__21065_21541;
var G__21606 = chunk__21069_21542;
var G__21607 = count__21070_21543;
var G__21608 = (i__21071_21544 + (1));
seq__21065_21541 = G__21605;
chunk__21069_21542 = G__21606;
count__21070_21543 = G__21607;
i__21071_21544 = G__21608;
continue;
}
} else {
var temp__5823__auto___21609 = cljs.core.seq(seq__21065_21541);
if(temp__5823__auto___21609){
var seq__21065_21613__$1 = temp__5823__auto___21609;
if(cljs.core.chunked_seq_QMARK_(seq__21065_21613__$1)){
var c__5673__auto___21614 = cljs.core.chunk_first(seq__21065_21613__$1);
var G__21624 = cljs.core.chunk_rest(seq__21065_21613__$1);
var G__21625 = c__5673__auto___21614;
var G__21626 = cljs.core.count(c__5673__auto___21614);
var G__21627 = (0);
seq__21065_21541 = G__21624;
chunk__21069_21542 = G__21625;
count__21070_21543 = G__21626;
i__21071_21544 = G__21627;
continue;
} else {
var node_21628 = cljs.core.first(seq__21065_21613__$1);
if(cljs.core.not(node_21628.shadow$old)){
var path_match_21629 = shadow.cljs.devtools.client.browser.match_paths(node_21628.getAttribute("href"),path);
if(cljs.core.truth_(path_match_21629)){
var new_link_21630 = (function (){var G__21162 = node_21628.cloneNode(true);
G__21162.setAttribute("href",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(path_match_21629)+"?r="+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.rand.cljs$core$IFn$_invoke$arity$0())));

return G__21162;
})();
(node_21628.shadow$old = true);

(new_link_21630.onload = ((function (seq__21065_21541,chunk__21069_21542,count__21070_21543,i__21071_21544,seq__20886,chunk__20888,count__20889,i__20890,new_link_21630,path_match_21629,node_21628,seq__21065_21613__$1,temp__5823__auto___21609,path,map__20883,map__20883__$1,msg,updates,reload_info){
return (function (e){
var seq__21163_21631 = cljs.core.seq(cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(msg,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"reload-info","reload-info",1648088086),new cljs.core.Keyword(null,"asset-load","asset-load",-1925902322)], null)));
var chunk__21165_21632 = null;
var count__21166_21633 = (0);
var i__21167_21634 = (0);
while(true){
if((i__21167_21634 < count__21166_21633)){
var map__21176_21635 = chunk__21165_21632.cljs$core$IIndexed$_nth$arity$2(null,i__21167_21634);
var map__21176_21636__$1 = cljs.core.__destructure_map(map__21176_21635);
var task_21637 = map__21176_21636__$1;
var fn_str_21638 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21176_21636__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21639 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21176_21636__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21640 = goog.getObjectByName(fn_str_21638,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21639)));

(fn_obj_21640.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21640.cljs$core$IFn$_invoke$arity$2(path,new_link_21630) : fn_obj_21640.call(null,path,new_link_21630));


var G__21641 = seq__21163_21631;
var G__21642 = chunk__21165_21632;
var G__21643 = count__21166_21633;
var G__21644 = (i__21167_21634 + (1));
seq__21163_21631 = G__21641;
chunk__21165_21632 = G__21642;
count__21166_21633 = G__21643;
i__21167_21634 = G__21644;
continue;
} else {
var temp__5823__auto___21645__$1 = cljs.core.seq(seq__21163_21631);
if(temp__5823__auto___21645__$1){
var seq__21163_21646__$1 = temp__5823__auto___21645__$1;
if(cljs.core.chunked_seq_QMARK_(seq__21163_21646__$1)){
var c__5673__auto___21647 = cljs.core.chunk_first(seq__21163_21646__$1);
var G__21648 = cljs.core.chunk_rest(seq__21163_21646__$1);
var G__21649 = c__5673__auto___21647;
var G__21650 = cljs.core.count(c__5673__auto___21647);
var G__21651 = (0);
seq__21163_21631 = G__21648;
chunk__21165_21632 = G__21649;
count__21166_21633 = G__21650;
i__21167_21634 = G__21651;
continue;
} else {
var map__21208_21652 = cljs.core.first(seq__21163_21646__$1);
var map__21208_21653__$1 = cljs.core.__destructure_map(map__21208_21652);
var task_21654 = map__21208_21653__$1;
var fn_str_21655 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21208_21653__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21656 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21208_21653__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21657 = goog.getObjectByName(fn_str_21655,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21656)));

(fn_obj_21657.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21657.cljs$core$IFn$_invoke$arity$2(path,new_link_21630) : fn_obj_21657.call(null,path,new_link_21630));


var G__21658 = cljs.core.next(seq__21163_21646__$1);
var G__21659 = null;
var G__21660 = (0);
var G__21661 = (0);
seq__21163_21631 = G__21658;
chunk__21165_21632 = G__21659;
count__21166_21633 = G__21660;
i__21167_21634 = G__21661;
continue;
}
} else {
}
}
break;
}

return goog.dom.removeNode(node_21628);
});})(seq__21065_21541,chunk__21069_21542,count__21070_21543,i__21071_21544,seq__20886,chunk__20888,count__20889,i__20890,new_link_21630,path_match_21629,node_21628,seq__21065_21613__$1,temp__5823__auto___21609,path,map__20883,map__20883__$1,msg,updates,reload_info))
);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load CSS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([path_match_21629], 0));

goog.dom.insertSiblingAfter(new_link_21630,node_21628);


var G__21662 = cljs.core.next(seq__21065_21613__$1);
var G__21663 = null;
var G__21664 = (0);
var G__21665 = (0);
seq__21065_21541 = G__21662;
chunk__21069_21542 = G__21663;
count__21070_21543 = G__21664;
i__21071_21544 = G__21665;
continue;
} else {
var G__21666 = cljs.core.next(seq__21065_21613__$1);
var G__21667 = null;
var G__21668 = (0);
var G__21669 = (0);
seq__21065_21541 = G__21666;
chunk__21069_21542 = G__21667;
count__21070_21543 = G__21668;
i__21071_21544 = G__21669;
continue;
}
} else {
var G__21670 = cljs.core.next(seq__21065_21613__$1);
var G__21671 = null;
var G__21672 = (0);
var G__21673 = (0);
seq__21065_21541 = G__21670;
chunk__21069_21542 = G__21671;
count__21070_21543 = G__21672;
i__21071_21544 = G__21673;
continue;
}
}
} else {
}
}
break;
}


var G__21674 = seq__20886;
var G__21675 = chunk__20888;
var G__21676 = count__20889;
var G__21677 = (i__20890 + (1));
seq__20886 = G__21674;
chunk__20888 = G__21675;
count__20889 = G__21676;
i__20890 = G__21677;
continue;
} else {
var G__21678 = seq__20886;
var G__21679 = chunk__20888;
var G__21680 = count__20889;
var G__21681 = (i__20890 + (1));
seq__20886 = G__21678;
chunk__20888 = G__21679;
count__20889 = G__21680;
i__20890 = G__21681;
continue;
}
} else {
var temp__5823__auto__ = cljs.core.seq(seq__20886);
if(temp__5823__auto__){
var seq__20886__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__20886__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__20886__$1);
var G__21682 = cljs.core.chunk_rest(seq__20886__$1);
var G__21683 = c__5673__auto__;
var G__21684 = cljs.core.count(c__5673__auto__);
var G__21685 = (0);
seq__20886 = G__21682;
chunk__20888 = G__21683;
count__20889 = G__21684;
i__20890 = G__21685;
continue;
} else {
var path = cljs.core.first(seq__20886__$1);
if(clojure.string.ends_with_QMARK_(path,"css")){
var seq__21214_21686 = cljs.core.seq(cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(document.querySelectorAll("link[rel=\"stylesheet\"]")));
var chunk__21218_21687 = null;
var count__21219_21688 = (0);
var i__21220_21689 = (0);
while(true){
if((i__21220_21689 < count__21219_21688)){
var node_21690 = chunk__21218_21687.cljs$core$IIndexed$_nth$arity$2(null,i__21220_21689);
if(cljs.core.not(node_21690.shadow$old)){
var path_match_21691 = shadow.cljs.devtools.client.browser.match_paths(node_21690.getAttribute("href"),path);
if(cljs.core.truth_(path_match_21691)){
var new_link_21694 = (function (){var G__21277 = node_21690.cloneNode(true);
G__21277.setAttribute("href",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(path_match_21691)+"?r="+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.rand.cljs$core$IFn$_invoke$arity$0())));

return G__21277;
})();
(node_21690.shadow$old = true);

(new_link_21694.onload = ((function (seq__21214_21686,chunk__21218_21687,count__21219_21688,i__21220_21689,seq__20886,chunk__20888,count__20889,i__20890,new_link_21694,path_match_21691,node_21690,path,seq__20886__$1,temp__5823__auto__,map__20883,map__20883__$1,msg,updates,reload_info){
return (function (e){
var seq__21280_21715 = cljs.core.seq(cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(msg,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"reload-info","reload-info",1648088086),new cljs.core.Keyword(null,"asset-load","asset-load",-1925902322)], null)));
var chunk__21282_21716 = null;
var count__21283_21717 = (0);
var i__21284_21718 = (0);
while(true){
if((i__21284_21718 < count__21283_21717)){
var map__21295_21719 = chunk__21282_21716.cljs$core$IIndexed$_nth$arity$2(null,i__21284_21718);
var map__21295_21720__$1 = cljs.core.__destructure_map(map__21295_21719);
var task_21721 = map__21295_21720__$1;
var fn_str_21722 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21295_21720__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21723 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21295_21720__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21724 = goog.getObjectByName(fn_str_21722,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21723)));

(fn_obj_21724.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21724.cljs$core$IFn$_invoke$arity$2(path,new_link_21694) : fn_obj_21724.call(null,path,new_link_21694));


var G__21725 = seq__21280_21715;
var G__21726 = chunk__21282_21716;
var G__21727 = count__21283_21717;
var G__21728 = (i__21284_21718 + (1));
seq__21280_21715 = G__21725;
chunk__21282_21716 = G__21726;
count__21283_21717 = G__21727;
i__21284_21718 = G__21728;
continue;
} else {
var temp__5823__auto___21729__$1 = cljs.core.seq(seq__21280_21715);
if(temp__5823__auto___21729__$1){
var seq__21280_21730__$1 = temp__5823__auto___21729__$1;
if(cljs.core.chunked_seq_QMARK_(seq__21280_21730__$1)){
var c__5673__auto___21731 = cljs.core.chunk_first(seq__21280_21730__$1);
var G__21732 = cljs.core.chunk_rest(seq__21280_21730__$1);
var G__21733 = c__5673__auto___21731;
var G__21734 = cljs.core.count(c__5673__auto___21731);
var G__21735 = (0);
seq__21280_21715 = G__21732;
chunk__21282_21716 = G__21733;
count__21283_21717 = G__21734;
i__21284_21718 = G__21735;
continue;
} else {
var map__21306_21738 = cljs.core.first(seq__21280_21730__$1);
var map__21306_21739__$1 = cljs.core.__destructure_map(map__21306_21738);
var task_21740 = map__21306_21739__$1;
var fn_str_21741 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21306_21739__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21742 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21306_21739__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21743 = goog.getObjectByName(fn_str_21741,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21742)));

(fn_obj_21743.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21743.cljs$core$IFn$_invoke$arity$2(path,new_link_21694) : fn_obj_21743.call(null,path,new_link_21694));


var G__21744 = cljs.core.next(seq__21280_21730__$1);
var G__21745 = null;
var G__21746 = (0);
var G__21747 = (0);
seq__21280_21715 = G__21744;
chunk__21282_21716 = G__21745;
count__21283_21717 = G__21746;
i__21284_21718 = G__21747;
continue;
}
} else {
}
}
break;
}

return goog.dom.removeNode(node_21690);
});})(seq__21214_21686,chunk__21218_21687,count__21219_21688,i__21220_21689,seq__20886,chunk__20888,count__20889,i__20890,new_link_21694,path_match_21691,node_21690,path,seq__20886__$1,temp__5823__auto__,map__20883,map__20883__$1,msg,updates,reload_info))
);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load CSS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([path_match_21691], 0));

goog.dom.insertSiblingAfter(new_link_21694,node_21690);


var G__21748 = seq__21214_21686;
var G__21749 = chunk__21218_21687;
var G__21750 = count__21219_21688;
var G__21751 = (i__21220_21689 + (1));
seq__21214_21686 = G__21748;
chunk__21218_21687 = G__21749;
count__21219_21688 = G__21750;
i__21220_21689 = G__21751;
continue;
} else {
var G__21753 = seq__21214_21686;
var G__21754 = chunk__21218_21687;
var G__21755 = count__21219_21688;
var G__21756 = (i__21220_21689 + (1));
seq__21214_21686 = G__21753;
chunk__21218_21687 = G__21754;
count__21219_21688 = G__21755;
i__21220_21689 = G__21756;
continue;
}
} else {
var G__21757 = seq__21214_21686;
var G__21758 = chunk__21218_21687;
var G__21759 = count__21219_21688;
var G__21760 = (i__21220_21689 + (1));
seq__21214_21686 = G__21757;
chunk__21218_21687 = G__21758;
count__21219_21688 = G__21759;
i__21220_21689 = G__21760;
continue;
}
} else {
var temp__5823__auto___21761__$1 = cljs.core.seq(seq__21214_21686);
if(temp__5823__auto___21761__$1){
var seq__21214_21762__$1 = temp__5823__auto___21761__$1;
if(cljs.core.chunked_seq_QMARK_(seq__21214_21762__$1)){
var c__5673__auto___21765 = cljs.core.chunk_first(seq__21214_21762__$1);
var G__21766 = cljs.core.chunk_rest(seq__21214_21762__$1);
var G__21767 = c__5673__auto___21765;
var G__21768 = cljs.core.count(c__5673__auto___21765);
var G__21769 = (0);
seq__21214_21686 = G__21766;
chunk__21218_21687 = G__21767;
count__21219_21688 = G__21768;
i__21220_21689 = G__21769;
continue;
} else {
var node_21770 = cljs.core.first(seq__21214_21762__$1);
if(cljs.core.not(node_21770.shadow$old)){
var path_match_21773 = shadow.cljs.devtools.client.browser.match_paths(node_21770.getAttribute("href"),path);
if(cljs.core.truth_(path_match_21773)){
var new_link_21782 = (function (){var G__21340 = node_21770.cloneNode(true);
G__21340.setAttribute("href",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(path_match_21773)+"?r="+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.rand.cljs$core$IFn$_invoke$arity$0())));

return G__21340;
})();
(node_21770.shadow$old = true);

(new_link_21782.onload = ((function (seq__21214_21686,chunk__21218_21687,count__21219_21688,i__21220_21689,seq__20886,chunk__20888,count__20889,i__20890,new_link_21782,path_match_21773,node_21770,seq__21214_21762__$1,temp__5823__auto___21761__$1,path,seq__20886__$1,temp__5823__auto__,map__20883,map__20883__$1,msg,updates,reload_info){
return (function (e){
var seq__21343_21784 = cljs.core.seq(cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(msg,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"reload-info","reload-info",1648088086),new cljs.core.Keyword(null,"asset-load","asset-load",-1925902322)], null)));
var chunk__21345_21785 = null;
var count__21346_21786 = (0);
var i__21347_21787 = (0);
while(true){
if((i__21347_21787 < count__21346_21786)){
var map__21357_21788 = chunk__21345_21785.cljs$core$IIndexed$_nth$arity$2(null,i__21347_21787);
var map__21357_21789__$1 = cljs.core.__destructure_map(map__21357_21788);
var task_21790 = map__21357_21789__$1;
var fn_str_21791 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21357_21789__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21792 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21357_21789__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21795 = goog.getObjectByName(fn_str_21791,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21792)));

(fn_obj_21795.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21795.cljs$core$IFn$_invoke$arity$2(path,new_link_21782) : fn_obj_21795.call(null,path,new_link_21782));


var G__21803 = seq__21343_21784;
var G__21804 = chunk__21345_21785;
var G__21805 = count__21346_21786;
var G__21806 = (i__21347_21787 + (1));
seq__21343_21784 = G__21803;
chunk__21345_21785 = G__21804;
count__21346_21786 = G__21805;
i__21347_21787 = G__21806;
continue;
} else {
var temp__5823__auto___21807__$2 = cljs.core.seq(seq__21343_21784);
if(temp__5823__auto___21807__$2){
var seq__21343_21808__$1 = temp__5823__auto___21807__$2;
if(cljs.core.chunked_seq_QMARK_(seq__21343_21808__$1)){
var c__5673__auto___21809 = cljs.core.chunk_first(seq__21343_21808__$1);
var G__21810 = cljs.core.chunk_rest(seq__21343_21808__$1);
var G__21811 = c__5673__auto___21809;
var G__21812 = cljs.core.count(c__5673__auto___21809);
var G__21813 = (0);
seq__21343_21784 = G__21810;
chunk__21345_21785 = G__21811;
count__21346_21786 = G__21812;
i__21347_21787 = G__21813;
continue;
} else {
var map__21359_21818 = cljs.core.first(seq__21343_21808__$1);
var map__21359_21819__$1 = cljs.core.__destructure_map(map__21359_21818);
var task_21820 = map__21359_21819__$1;
var fn_str_21821 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21359_21819__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21822 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21359_21819__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21823 = goog.getObjectByName(fn_str_21821,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21822)));

(fn_obj_21823.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21823.cljs$core$IFn$_invoke$arity$2(path,new_link_21782) : fn_obj_21823.call(null,path,new_link_21782));


var G__21824 = cljs.core.next(seq__21343_21808__$1);
var G__21825 = null;
var G__21826 = (0);
var G__21827 = (0);
seq__21343_21784 = G__21824;
chunk__21345_21785 = G__21825;
count__21346_21786 = G__21826;
i__21347_21787 = G__21827;
continue;
}
} else {
}
}
break;
}

return goog.dom.removeNode(node_21770);
});})(seq__21214_21686,chunk__21218_21687,count__21219_21688,i__21220_21689,seq__20886,chunk__20888,count__20889,i__20890,new_link_21782,path_match_21773,node_21770,seq__21214_21762__$1,temp__5823__auto___21761__$1,path,seq__20886__$1,temp__5823__auto__,map__20883,map__20883__$1,msg,updates,reload_info))
);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load CSS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([path_match_21773], 0));

goog.dom.insertSiblingAfter(new_link_21782,node_21770);


var G__21828 = cljs.core.next(seq__21214_21762__$1);
var G__21829 = null;
var G__21830 = (0);
var G__21831 = (0);
seq__21214_21686 = G__21828;
chunk__21218_21687 = G__21829;
count__21219_21688 = G__21830;
i__21220_21689 = G__21831;
continue;
} else {
var G__21832 = cljs.core.next(seq__21214_21762__$1);
var G__21833 = null;
var G__21834 = (0);
var G__21835 = (0);
seq__21214_21686 = G__21832;
chunk__21218_21687 = G__21833;
count__21219_21688 = G__21834;
i__21220_21689 = G__21835;
continue;
}
} else {
var G__21836 = cljs.core.next(seq__21214_21762__$1);
var G__21837 = null;
var G__21838 = (0);
var G__21839 = (0);
seq__21214_21686 = G__21836;
chunk__21218_21687 = G__21837;
count__21219_21688 = G__21838;
i__21220_21689 = G__21839;
continue;
}
}
} else {
}
}
break;
}


var G__21840 = cljs.core.next(seq__20886__$1);
var G__21841 = null;
var G__21842 = (0);
var G__21843 = (0);
seq__20886 = G__21840;
chunk__20888 = G__21841;
count__20889 = G__21842;
i__20890 = G__21843;
continue;
} else {
var G__21844 = cljs.core.next(seq__20886__$1);
var G__21845 = null;
var G__21846 = (0);
var G__21847 = (0);
seq__20886 = G__21844;
chunk__20888 = G__21845;
count__20889 = G__21846;
i__20890 = G__21847;
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
try{var G__21376 = shadow.cljs.devtools.client.browser.global_eval(code);
return (success.cljs$core$IFn$_invoke$arity$1 ? success.cljs$core$IFn$_invoke$arity$1(G__21376) : success.call(null,G__21376));
}catch (e21375){var e = e21375;
return (fail.cljs$core$IFn$_invoke$arity$1 ? fail.cljs$core$IFn$_invoke$arity$1(e) : fail.call(null,e));
}}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IHostSpecific$ = cljs.core.PROTOCOL_SENTINEL);

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IHostSpecific$do_invoke$arity$5 = (function (this$,ns,p__21377,success,fail){
var map__21378 = p__21377;
var map__21378__$1 = cljs.core.__destructure_map(map__21378);
var js = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21378__$1,new cljs.core.Keyword(null,"js","js",1768080579));
var this$__$1 = this;
try{var G__21380 = shadow.cljs.devtools.client.browser.global_eval(js);
return (success.cljs$core$IFn$_invoke$arity$1 ? success.cljs$core$IFn$_invoke$arity$1(G__21380) : success.call(null,G__21380));
}catch (e21379){var e = e21379;
return (fail.cljs$core$IFn$_invoke$arity$1 ? fail.cljs$core$IFn$_invoke$arity$1(e) : fail.call(null,e));
}}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IHostSpecific$do_repl_init$arity$4 = (function (runtime,p__21381,done,error){
var map__21382 = p__21381;
var map__21382__$1 = cljs.core.__destructure_map(map__21382);
var repl_sources = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21382__$1,new cljs.core.Keyword(null,"repl-sources","repl-sources",723867535));
var runtime__$1 = this;
return shadow.cljs.devtools.client.shared.load_sources(runtime__$1,cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentVector.EMPTY,cljs.core.remove.cljs$core$IFn$_invoke$arity$2(shadow.cljs.devtools.client.env.src_is_loaded_QMARK_,repl_sources)),(function (sources){
shadow.cljs.devtools.client.browser.do_js_load(sources);

return (done.cljs$core$IFn$_invoke$arity$0 ? done.cljs$core$IFn$_invoke$arity$0() : done.call(null));
}));
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IHostSpecific$do_repl_require$arity$4 = (function (runtime,p__21383,done,error){
var map__21384 = p__21383;
var map__21384__$1 = cljs.core.__destructure_map(map__21384);
var msg = map__21384__$1;
var sources = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21384__$1,new cljs.core.Keyword(null,"sources","sources",-321166424));
var reload_namespaces = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21384__$1,new cljs.core.Keyword(null,"reload-namespaces","reload-namespaces",250210134));
var js_requires = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21384__$1,new cljs.core.Keyword(null,"js-requires","js-requires",-1311472051));
var runtime__$1 = this;
var sources_to_load = cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentVector.EMPTY,cljs.core.remove.cljs$core$IFn$_invoke$arity$2((function (p__21387){
var map__21388 = p__21387;
var map__21388__$1 = cljs.core.__destructure_map(map__21388);
var src = map__21388__$1;
var provides = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21388__$1,new cljs.core.Keyword(null,"provides","provides",-1634397992));
var and__5140__auto__ = shadow.cljs.devtools.client.env.src_is_loaded_QMARK_(src);
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(cljs.core.some(reload_namespaces,provides));
} else {
return and__5140__auto__;
}
}),sources));
if(cljs.core.not(cljs.core.seq(sources_to_load))){
var G__21389 = cljs.core.PersistentVector.EMPTY;
return (done.cljs$core$IFn$_invoke$arity$1 ? done.cljs$core$IFn$_invoke$arity$1(G__21389) : done.call(null,G__21389));
} else {
return shadow.remote.runtime.shared.call.cljs$core$IFn$_invoke$arity$3(runtime__$1,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"op","op",-1882987955),new cljs.core.Keyword(null,"cljs-load-sources","cljs-load-sources",-1458295962),new cljs.core.Keyword(null,"to","to",192099007),shadow.cljs.devtools.client.env.worker_client_id,new cljs.core.Keyword(null,"sources","sources",-321166424),cljs.core.into.cljs$core$IFn$_invoke$arity$3(cljs.core.PersistentVector.EMPTY,cljs.core.map.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"resource-id","resource-id",-1308422582)),sources_to_load)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"cljs-sources","cljs-sources",31121610),(function (p__21391){
var map__21392 = p__21391;
var map__21392__$1 = cljs.core.__destructure_map(map__21392);
var msg__$1 = map__21392__$1;
var sources__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21392__$1,new cljs.core.Keyword(null,"sources","sources",-321166424));
try{shadow.cljs.devtools.client.browser.do_js_load(sources__$1);

if(cljs.core.seq(js_requires)){
shadow.cljs.devtools.client.browser.do_js_requires(js_requires);
} else {
}

return (done.cljs$core$IFn$_invoke$arity$1 ? done.cljs$core$IFn$_invoke$arity$1(sources_to_load) : done.call(null,sources_to_load));
}catch (e21393){var ex = e21393;
return (error.cljs$core$IFn$_invoke$arity$1 ? error.cljs$core$IFn$_invoke$arity$1(ex) : error.call(null,ex));
}})], null));
}
}));

shadow.cljs.devtools.client.shared.add_plugin_BANG_(new cljs.core.Keyword("shadow.cljs.devtools.client.browser","client","shadow.cljs.devtools.client.browser/client",-1461019282),cljs.core.PersistentHashSet.EMPTY,(function (p__21394){
var map__21395 = p__21394;
var map__21395__$1 = cljs.core.__destructure_map(map__21395);
var env = map__21395__$1;
var runtime = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21395__$1,new cljs.core.Keyword(null,"runtime","runtime",-1331573996));
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
}),new cljs.core.Keyword("shadow.cljs.devtools.client.env","worker-notify","shadow.cljs.devtools.client.env/worker-notify",-1456820670),(function (p__21399){
var map__21400 = p__21399;
var map__21400__$1 = cljs.core.__destructure_map(map__21400);
var event_op = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21400__$1,new cljs.core.Keyword(null,"event-op","event-op",200358057));
var client_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21400__$1,new cljs.core.Keyword(null,"client-id","client-id",-464622140));
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
}),(function (p__21402){
var map__21403 = p__21402;
var map__21403__$1 = cljs.core.__destructure_map(map__21403);
var svc = map__21403__$1;
var runtime = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21403__$1,new cljs.core.Keyword(null,"runtime","runtime",-1331573996));
return shadow.remote.runtime.api.del_extension(runtime,new cljs.core.Keyword("shadow.cljs.devtools.client.browser","client","shadow.cljs.devtools.client.browser/client",-1461019282));
}));

shadow.cljs.devtools.client.shared.init_runtime_BANG_(shadow.cljs.devtools.client.browser.client_info,shadow.cljs.devtools.client.websocket.start,shadow.cljs.devtools.client.websocket.send,shadow.cljs.devtools.client.websocket.stop);
} else {
}

//# sourceMappingURL=shadow.cljs.devtools.client.browser.js.map
