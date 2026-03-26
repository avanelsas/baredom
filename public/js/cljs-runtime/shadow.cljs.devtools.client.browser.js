goog.provide('shadow.cljs.devtools.client.browser');
shadow.cljs.devtools.client.browser.devtools_msg = (function shadow$cljs$devtools$client$browser$devtools_msg(var_args){
var args__5882__auto__ = [];
var len__5876__auto___21387 = arguments.length;
var i__5877__auto___21388 = (0);
while(true){
if((i__5877__auto___21388 < len__5876__auto___21387)){
args__5882__auto__.push((arguments[i__5877__auto___21388]));

var G__21389 = (i__5877__auto___21388 + (1));
i__5877__auto___21388 = G__21389;
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
(shadow.cljs.devtools.client.browser.devtools_msg.cljs$lang$applyTo = (function (seq20839){
var G__20840 = cljs.core.first(seq20839);
var seq20839__$1 = cljs.core.next(seq20839);
var self__5861__auto__ = this;
return self__5861__auto__.cljs$core$IFn$_invoke$arity$variadic(G__20840,seq20839__$1);
}));

shadow.cljs.devtools.client.browser.script_eval = (function shadow$cljs$devtools$client$browser$script_eval(code){
return goog.globalEval(code);
});
shadow.cljs.devtools.client.browser.do_js_load = (function shadow$cljs$devtools$client$browser$do_js_load(sources){
var seq__20846 = cljs.core.seq(sources);
var chunk__20847 = null;
var count__20848 = (0);
var i__20849 = (0);
while(true){
if((i__20849 < count__20848)){
var map__20860 = chunk__20847.cljs$core$IIndexed$_nth$arity$2(null,i__20849);
var map__20860__$1 = cljs.core.__destructure_map(map__20860);
var src = map__20860__$1;
var resource_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20860__$1,new cljs.core.Keyword(null,"resource-id","resource-id",-1308422582));
var output_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20860__$1,new cljs.core.Keyword(null,"output-name","output-name",-1769107767));
var resource_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20860__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
var js = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20860__$1,new cljs.core.Keyword(null,"js","js",1768080579));
$CLJS.SHADOW_ENV.setLoaded(output_name);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load JS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([resource_name], 0));

shadow.cljs.devtools.client.env.before_load_src(src);

try{shadow.cljs.devtools.client.browser.script_eval((""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(js)+"\n//# sourceURL="+cljs.core.str.cljs$core$IFn$_invoke$arity$1($CLJS.SHADOW_ENV.scriptBase)+cljs.core.str.cljs$core$IFn$_invoke$arity$1(output_name)));
}catch (e20861){var e_21416 = e20861;
if(shadow.cljs.devtools.client.env.log){
console.error((""+"Failed to load "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name)),e_21416);
} else {
}

throw (new Error((""+"Failed to load "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name)+": "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(e_21416.message))));
}

var G__21417 = seq__20846;
var G__21418 = chunk__20847;
var G__21419 = count__20848;
var G__21420 = (i__20849 + (1));
seq__20846 = G__21417;
chunk__20847 = G__21418;
count__20848 = G__21419;
i__20849 = G__21420;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__20846);
if(temp__5823__auto__){
var seq__20846__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__20846__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__20846__$1);
var G__21422 = cljs.core.chunk_rest(seq__20846__$1);
var G__21423 = c__5673__auto__;
var G__21424 = cljs.core.count(c__5673__auto__);
var G__21425 = (0);
seq__20846 = G__21422;
chunk__20847 = G__21423;
count__20848 = G__21424;
i__20849 = G__21425;
continue;
} else {
var map__20863 = cljs.core.first(seq__20846__$1);
var map__20863__$1 = cljs.core.__destructure_map(map__20863);
var src = map__20863__$1;
var resource_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20863__$1,new cljs.core.Keyword(null,"resource-id","resource-id",-1308422582));
var output_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20863__$1,new cljs.core.Keyword(null,"output-name","output-name",-1769107767));
var resource_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20863__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
var js = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20863__$1,new cljs.core.Keyword(null,"js","js",1768080579));
$CLJS.SHADOW_ENV.setLoaded(output_name);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load JS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([resource_name], 0));

shadow.cljs.devtools.client.env.before_load_src(src);

try{shadow.cljs.devtools.client.browser.script_eval((""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(js)+"\n//# sourceURL="+cljs.core.str.cljs$core$IFn$_invoke$arity$1($CLJS.SHADOW_ENV.scriptBase)+cljs.core.str.cljs$core$IFn$_invoke$arity$1(output_name)));
}catch (e20866){var e_21430 = e20866;
if(shadow.cljs.devtools.client.env.log){
console.error((""+"Failed to load "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name)),e_21430);
} else {
}

throw (new Error((""+"Failed to load "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name)+": "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(e_21430.message))));
}

var G__21431 = cljs.core.next(seq__20846__$1);
var G__21432 = null;
var G__21433 = (0);
var G__21434 = (0);
seq__20846 = G__21431;
chunk__20847 = G__21432;
count__20848 = G__21433;
i__20849 = G__21434;
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
var seq__20869 = cljs.core.seq(js_requires);
var chunk__20870 = null;
var count__20871 = (0);
var i__20872 = (0);
while(true){
if((i__20872 < count__20871)){
var js_ns = chunk__20870.cljs$core$IIndexed$_nth$arity$2(null,i__20872);
var require_str_21435 = (""+"var "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(js_ns)+" = shadow.js.require(\""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(js_ns)+"\");");
shadow.cljs.devtools.client.browser.script_eval(require_str_21435);


var G__21436 = seq__20869;
var G__21437 = chunk__20870;
var G__21438 = count__20871;
var G__21439 = (i__20872 + (1));
seq__20869 = G__21436;
chunk__20870 = G__21437;
count__20871 = G__21438;
i__20872 = G__21439;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__20869);
if(temp__5823__auto__){
var seq__20869__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__20869__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__20869__$1);
var G__21441 = cljs.core.chunk_rest(seq__20869__$1);
var G__21442 = c__5673__auto__;
var G__21443 = cljs.core.count(c__5673__auto__);
var G__21444 = (0);
seq__20869 = G__21441;
chunk__20870 = G__21442;
count__20871 = G__21443;
i__20872 = G__21444;
continue;
} else {
var js_ns = cljs.core.first(seq__20869__$1);
var require_str_21446 = (""+"var "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(js_ns)+" = shadow.js.require(\""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(js_ns)+"\");");
shadow.cljs.devtools.client.browser.script_eval(require_str_21446);


var G__21447 = cljs.core.next(seq__20869__$1);
var G__21448 = null;
var G__21449 = (0);
var G__21450 = (0);
seq__20869 = G__21447;
chunk__20870 = G__21448;
count__20871 = G__21449;
i__20872 = G__21450;
continue;
}
} else {
return null;
}
}
break;
}
});
shadow.cljs.devtools.client.browser.handle_build_complete = (function shadow$cljs$devtools$client$browser$handle_build_complete(runtime,p__20888){
var map__20891 = p__20888;
var map__20891__$1 = cljs.core.__destructure_map(map__20891);
var msg = map__20891__$1;
var info = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20891__$1,new cljs.core.Keyword(null,"info","info",-317069002));
var reload_info = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20891__$1,new cljs.core.Keyword(null,"reload-info","reload-info",1648088086));
var warnings = cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentVector.EMPTY,cljs.core.distinct.cljs$core$IFn$_invoke$arity$1((function (){var iter__5628__auto__ = (function shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__20893(s__20894){
return (new cljs.core.LazySeq(null,(function (){
var s__20894__$1 = s__20894;
while(true){
var temp__5823__auto__ = cljs.core.seq(s__20894__$1);
if(temp__5823__auto__){
var xs__6383__auto__ = temp__5823__auto__;
var map__20901 = cljs.core.first(xs__6383__auto__);
var map__20901__$1 = cljs.core.__destructure_map(map__20901);
var src = map__20901__$1;
var resource_name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20901__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
var warnings = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20901__$1,new cljs.core.Keyword(null,"warnings","warnings",-735437651));
if(cljs.core.not(new cljs.core.Keyword(null,"from-jar","from-jar",1050932827).cljs$core$IFn$_invoke$arity$1(src))){
var iterys__5624__auto__ = ((function (s__20894__$1,map__20901,map__20901__$1,src,resource_name,warnings,xs__6383__auto__,temp__5823__auto__,map__20891,map__20891__$1,msg,info,reload_info){
return (function shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__20893_$_iter__20895(s__20896){
return (new cljs.core.LazySeq(null,((function (s__20894__$1,map__20901,map__20901__$1,src,resource_name,warnings,xs__6383__auto__,temp__5823__auto__,map__20891,map__20891__$1,msg,info,reload_info){
return (function (){
var s__20896__$1 = s__20896;
while(true){
var temp__5823__auto____$1 = cljs.core.seq(s__20896__$1);
if(temp__5823__auto____$1){
var s__20896__$2 = temp__5823__auto____$1;
if(cljs.core.chunked_seq_QMARK_(s__20896__$2)){
var c__5626__auto__ = cljs.core.chunk_first(s__20896__$2);
var size__5627__auto__ = cljs.core.count(c__5626__auto__);
var b__20898 = cljs.core.chunk_buffer(size__5627__auto__);
if((function (){var i__20897 = (0);
while(true){
if((i__20897 < size__5627__auto__)){
var warning = cljs.core._nth(c__5626__auto__,i__20897);
cljs.core.chunk_append(b__20898,cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(warning,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100),resource_name));

var G__21452 = (i__20897 + (1));
i__20897 = G__21452;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons(cljs.core.chunk(b__20898),shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__20893_$_iter__20895(cljs.core.chunk_rest(s__20896__$2)));
} else {
return cljs.core.chunk_cons(cljs.core.chunk(b__20898),null);
}
} else {
var warning = cljs.core.first(s__20896__$2);
return cljs.core.cons(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(warning,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100),resource_name),shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__20893_$_iter__20895(cljs.core.rest(s__20896__$2)));
}
} else {
return null;
}
break;
}
});})(s__20894__$1,map__20901,map__20901__$1,src,resource_name,warnings,xs__6383__auto__,temp__5823__auto__,map__20891,map__20891__$1,msg,info,reload_info))
,null,null));
});})(s__20894__$1,map__20901,map__20901__$1,src,resource_name,warnings,xs__6383__auto__,temp__5823__auto__,map__20891,map__20891__$1,msg,info,reload_info))
;
var fs__5625__auto__ = cljs.core.seq(iterys__5624__auto__(warnings));
if(fs__5625__auto__){
return cljs.core.concat.cljs$core$IFn$_invoke$arity$2(fs__5625__auto__,shadow$cljs$devtools$client$browser$handle_build_complete_$_iter__20893(cljs.core.rest(s__20894__$1)));
} else {
var G__21453 = cljs.core.rest(s__20894__$1);
s__20894__$1 = G__21453;
continue;
}
} else {
var G__21454 = cljs.core.rest(s__20894__$1);
s__20894__$1 = G__21454;
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
var seq__20912_21455 = cljs.core.seq(warnings);
var chunk__20913_21456 = null;
var count__20914_21457 = (0);
var i__20915_21458 = (0);
while(true){
if((i__20915_21458 < count__20914_21457)){
var map__20927_21459 = chunk__20913_21456.cljs$core$IIndexed$_nth$arity$2(null,i__20915_21458);
var map__20927_21460__$1 = cljs.core.__destructure_map(map__20927_21459);
var w_21461 = map__20927_21460__$1;
var msg_21462__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20927_21460__$1,new cljs.core.Keyword(null,"msg","msg",-1386103444));
var line_21463 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20927_21460__$1,new cljs.core.Keyword(null,"line","line",212345235));
var column_21464 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20927_21460__$1,new cljs.core.Keyword(null,"column","column",2078222095));
var resource_name_21465 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20927_21460__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
console.warn((""+"BUILD-WARNING in "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name_21465)+" at ["+cljs.core.str.cljs$core$IFn$_invoke$arity$1(line_21463)+":"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(column_21464)+"]\n\t"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(msg_21462__$1)));


var G__21466 = seq__20912_21455;
var G__21467 = chunk__20913_21456;
var G__21468 = count__20914_21457;
var G__21469 = (i__20915_21458 + (1));
seq__20912_21455 = G__21466;
chunk__20913_21456 = G__21467;
count__20914_21457 = G__21468;
i__20915_21458 = G__21469;
continue;
} else {
var temp__5823__auto___21470 = cljs.core.seq(seq__20912_21455);
if(temp__5823__auto___21470){
var seq__20912_21471__$1 = temp__5823__auto___21470;
if(cljs.core.chunked_seq_QMARK_(seq__20912_21471__$1)){
var c__5673__auto___21472 = cljs.core.chunk_first(seq__20912_21471__$1);
var G__21473 = cljs.core.chunk_rest(seq__20912_21471__$1);
var G__21474 = c__5673__auto___21472;
var G__21475 = cljs.core.count(c__5673__auto___21472);
var G__21476 = (0);
seq__20912_21455 = G__21473;
chunk__20913_21456 = G__21474;
count__20914_21457 = G__21475;
i__20915_21458 = G__21476;
continue;
} else {
var map__20928_21477 = cljs.core.first(seq__20912_21471__$1);
var map__20928_21478__$1 = cljs.core.__destructure_map(map__20928_21477);
var w_21479 = map__20928_21478__$1;
var msg_21480__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20928_21478__$1,new cljs.core.Keyword(null,"msg","msg",-1386103444));
var line_21481 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20928_21478__$1,new cljs.core.Keyword(null,"line","line",212345235));
var column_21482 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20928_21478__$1,new cljs.core.Keyword(null,"column","column",2078222095));
var resource_name_21483 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20928_21478__$1,new cljs.core.Keyword(null,"resource-name","resource-name",2001617100));
console.warn((""+"BUILD-WARNING in "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(resource_name_21483)+" at ["+cljs.core.str.cljs$core$IFn$_invoke$arity$1(line_21481)+":"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(column_21482)+"]\n\t"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(msg_21480__$1)));


var G__21484 = cljs.core.next(seq__20912_21471__$1);
var G__21485 = null;
var G__21486 = (0);
var G__21487 = (0);
seq__20912_21455 = G__21484;
chunk__20913_21456 = G__21485;
count__20914_21457 = G__21486;
i__20915_21458 = G__21487;
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

return shadow.cljs.devtools.client.shared.load_sources(runtime,sources_to_get,(function (p1__20885_SHARP_){
return shadow.cljs.devtools.client.browser.do_js_reload(msg,p1__20885_SHARP_,shadow.cljs.devtools.client.hud.load_end_success,shadow.cljs.devtools.client.hud.load_failure);
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
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var G__20933 = node_uri;
G__20933.setQuery(null);

G__20933.setPath(new$);

return G__20933;
})()));
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
}
});
shadow.cljs.devtools.client.browser.handle_asset_update = (function shadow$cljs$devtools$client$browser$handle_asset_update(p__20934){
var map__20935 = p__20934;
var map__20935__$1 = cljs.core.__destructure_map(map__20935);
var msg = map__20935__$1;
var updates = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20935__$1,new cljs.core.Keyword(null,"updates","updates",2013983452));
var reload_info = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20935__$1,new cljs.core.Keyword(null,"reload-info","reload-info",1648088086));
var seq__20936 = cljs.core.seq(updates);
var chunk__20938 = null;
var count__20939 = (0);
var i__20940 = (0);
while(true){
if((i__20940 < count__20939)){
var path = chunk__20938.cljs$core$IIndexed$_nth$arity$2(null,i__20940);
if(clojure.string.ends_with_QMARK_(path,"css")){
var seq__21219_21488 = cljs.core.seq(cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(document.querySelectorAll("link[rel=\"stylesheet\"]")));
var chunk__21223_21489 = null;
var count__21224_21490 = (0);
var i__21225_21491 = (0);
while(true){
if((i__21225_21491 < count__21224_21490)){
var node_21492 = chunk__21223_21489.cljs$core$IIndexed$_nth$arity$2(null,i__21225_21491);
if(cljs.core.not(node_21492.shadow$old)){
var path_match_21493 = shadow.cljs.devtools.client.browser.match_paths(node_21492.getAttribute("href"),path);
if(cljs.core.truth_(path_match_21493)){
var new_link_21494 = (function (){var G__21270 = node_21492.cloneNode(true);
G__21270.setAttribute("href",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(path_match_21493)+"?r="+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.rand.cljs$core$IFn$_invoke$arity$0())));

return G__21270;
})();
(node_21492.shadow$old = true);

(new_link_21494.onload = ((function (seq__21219_21488,chunk__21223_21489,count__21224_21490,i__21225_21491,seq__20936,chunk__20938,count__20939,i__20940,new_link_21494,path_match_21493,node_21492,path,map__20935,map__20935__$1,msg,updates,reload_info){
return (function (e){
var seq__21271_21495 = cljs.core.seq(cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(msg,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"reload-info","reload-info",1648088086),new cljs.core.Keyword(null,"asset-load","asset-load",-1925902322)], null)));
var chunk__21273_21496 = null;
var count__21274_21497 = (0);
var i__21275_21498 = (0);
while(true){
if((i__21275_21498 < count__21274_21497)){
var map__21280_21499 = chunk__21273_21496.cljs$core$IIndexed$_nth$arity$2(null,i__21275_21498);
var map__21280_21500__$1 = cljs.core.__destructure_map(map__21280_21499);
var task_21501 = map__21280_21500__$1;
var fn_str_21502 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21280_21500__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21503 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21280_21500__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21504 = goog.getObjectByName(fn_str_21502,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21503)));

(fn_obj_21504.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21504.cljs$core$IFn$_invoke$arity$2(path,new_link_21494) : fn_obj_21504.call(null,path,new_link_21494));


var G__21505 = seq__21271_21495;
var G__21506 = chunk__21273_21496;
var G__21507 = count__21274_21497;
var G__21508 = (i__21275_21498 + (1));
seq__21271_21495 = G__21505;
chunk__21273_21496 = G__21506;
count__21274_21497 = G__21507;
i__21275_21498 = G__21508;
continue;
} else {
var temp__5823__auto___21509 = cljs.core.seq(seq__21271_21495);
if(temp__5823__auto___21509){
var seq__21271_21510__$1 = temp__5823__auto___21509;
if(cljs.core.chunked_seq_QMARK_(seq__21271_21510__$1)){
var c__5673__auto___21511 = cljs.core.chunk_first(seq__21271_21510__$1);
var G__21512 = cljs.core.chunk_rest(seq__21271_21510__$1);
var G__21513 = c__5673__auto___21511;
var G__21514 = cljs.core.count(c__5673__auto___21511);
var G__21515 = (0);
seq__21271_21495 = G__21512;
chunk__21273_21496 = G__21513;
count__21274_21497 = G__21514;
i__21275_21498 = G__21515;
continue;
} else {
var map__21281_21516 = cljs.core.first(seq__21271_21510__$1);
var map__21281_21517__$1 = cljs.core.__destructure_map(map__21281_21516);
var task_21518 = map__21281_21517__$1;
var fn_str_21519 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21281_21517__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21520 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21281_21517__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21521 = goog.getObjectByName(fn_str_21519,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21520)));

(fn_obj_21521.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21521.cljs$core$IFn$_invoke$arity$2(path,new_link_21494) : fn_obj_21521.call(null,path,new_link_21494));


var G__21522 = cljs.core.next(seq__21271_21510__$1);
var G__21523 = null;
var G__21524 = (0);
var G__21525 = (0);
seq__21271_21495 = G__21522;
chunk__21273_21496 = G__21523;
count__21274_21497 = G__21524;
i__21275_21498 = G__21525;
continue;
}
} else {
}
}
break;
}

return goog.dom.removeNode(node_21492);
});})(seq__21219_21488,chunk__21223_21489,count__21224_21490,i__21225_21491,seq__20936,chunk__20938,count__20939,i__20940,new_link_21494,path_match_21493,node_21492,path,map__20935,map__20935__$1,msg,updates,reload_info))
);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load CSS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([path_match_21493], 0));

goog.dom.insertSiblingAfter(new_link_21494,node_21492);


var G__21526 = seq__21219_21488;
var G__21527 = chunk__21223_21489;
var G__21528 = count__21224_21490;
var G__21529 = (i__21225_21491 + (1));
seq__21219_21488 = G__21526;
chunk__21223_21489 = G__21527;
count__21224_21490 = G__21528;
i__21225_21491 = G__21529;
continue;
} else {
var G__21530 = seq__21219_21488;
var G__21531 = chunk__21223_21489;
var G__21532 = count__21224_21490;
var G__21533 = (i__21225_21491 + (1));
seq__21219_21488 = G__21530;
chunk__21223_21489 = G__21531;
count__21224_21490 = G__21532;
i__21225_21491 = G__21533;
continue;
}
} else {
var G__21534 = seq__21219_21488;
var G__21535 = chunk__21223_21489;
var G__21536 = count__21224_21490;
var G__21537 = (i__21225_21491 + (1));
seq__21219_21488 = G__21534;
chunk__21223_21489 = G__21535;
count__21224_21490 = G__21536;
i__21225_21491 = G__21537;
continue;
}
} else {
var temp__5823__auto___21538 = cljs.core.seq(seq__21219_21488);
if(temp__5823__auto___21538){
var seq__21219_21539__$1 = temp__5823__auto___21538;
if(cljs.core.chunked_seq_QMARK_(seq__21219_21539__$1)){
var c__5673__auto___21540 = cljs.core.chunk_first(seq__21219_21539__$1);
var G__21541 = cljs.core.chunk_rest(seq__21219_21539__$1);
var G__21542 = c__5673__auto___21540;
var G__21543 = cljs.core.count(c__5673__auto___21540);
var G__21544 = (0);
seq__21219_21488 = G__21541;
chunk__21223_21489 = G__21542;
count__21224_21490 = G__21543;
i__21225_21491 = G__21544;
continue;
} else {
var node_21545 = cljs.core.first(seq__21219_21539__$1);
if(cljs.core.not(node_21545.shadow$old)){
var path_match_21546 = shadow.cljs.devtools.client.browser.match_paths(node_21545.getAttribute("href"),path);
if(cljs.core.truth_(path_match_21546)){
var new_link_21547 = (function (){var G__21282 = node_21545.cloneNode(true);
G__21282.setAttribute("href",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(path_match_21546)+"?r="+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.rand.cljs$core$IFn$_invoke$arity$0())));

return G__21282;
})();
(node_21545.shadow$old = true);

(new_link_21547.onload = ((function (seq__21219_21488,chunk__21223_21489,count__21224_21490,i__21225_21491,seq__20936,chunk__20938,count__20939,i__20940,new_link_21547,path_match_21546,node_21545,seq__21219_21539__$1,temp__5823__auto___21538,path,map__20935,map__20935__$1,msg,updates,reload_info){
return (function (e){
var seq__21283_21548 = cljs.core.seq(cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(msg,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"reload-info","reload-info",1648088086),new cljs.core.Keyword(null,"asset-load","asset-load",-1925902322)], null)));
var chunk__21285_21549 = null;
var count__21286_21550 = (0);
var i__21287_21551 = (0);
while(true){
if((i__21287_21551 < count__21286_21550)){
var map__21291_21552 = chunk__21285_21549.cljs$core$IIndexed$_nth$arity$2(null,i__21287_21551);
var map__21291_21553__$1 = cljs.core.__destructure_map(map__21291_21552);
var task_21554 = map__21291_21553__$1;
var fn_str_21555 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21291_21553__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21556 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21291_21553__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21557 = goog.getObjectByName(fn_str_21555,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21556)));

(fn_obj_21557.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21557.cljs$core$IFn$_invoke$arity$2(path,new_link_21547) : fn_obj_21557.call(null,path,new_link_21547));


var G__21558 = seq__21283_21548;
var G__21559 = chunk__21285_21549;
var G__21560 = count__21286_21550;
var G__21561 = (i__21287_21551 + (1));
seq__21283_21548 = G__21558;
chunk__21285_21549 = G__21559;
count__21286_21550 = G__21560;
i__21287_21551 = G__21561;
continue;
} else {
var temp__5823__auto___21562__$1 = cljs.core.seq(seq__21283_21548);
if(temp__5823__auto___21562__$1){
var seq__21283_21563__$1 = temp__5823__auto___21562__$1;
if(cljs.core.chunked_seq_QMARK_(seq__21283_21563__$1)){
var c__5673__auto___21564 = cljs.core.chunk_first(seq__21283_21563__$1);
var G__21565 = cljs.core.chunk_rest(seq__21283_21563__$1);
var G__21566 = c__5673__auto___21564;
var G__21567 = cljs.core.count(c__5673__auto___21564);
var G__21568 = (0);
seq__21283_21548 = G__21565;
chunk__21285_21549 = G__21566;
count__21286_21550 = G__21567;
i__21287_21551 = G__21568;
continue;
} else {
var map__21292_21569 = cljs.core.first(seq__21283_21563__$1);
var map__21292_21570__$1 = cljs.core.__destructure_map(map__21292_21569);
var task_21571 = map__21292_21570__$1;
var fn_str_21572 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21292_21570__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21573 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21292_21570__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21574 = goog.getObjectByName(fn_str_21572,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21573)));

(fn_obj_21574.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21574.cljs$core$IFn$_invoke$arity$2(path,new_link_21547) : fn_obj_21574.call(null,path,new_link_21547));


var G__21575 = cljs.core.next(seq__21283_21563__$1);
var G__21576 = null;
var G__21577 = (0);
var G__21578 = (0);
seq__21283_21548 = G__21575;
chunk__21285_21549 = G__21576;
count__21286_21550 = G__21577;
i__21287_21551 = G__21578;
continue;
}
} else {
}
}
break;
}

return goog.dom.removeNode(node_21545);
});})(seq__21219_21488,chunk__21223_21489,count__21224_21490,i__21225_21491,seq__20936,chunk__20938,count__20939,i__20940,new_link_21547,path_match_21546,node_21545,seq__21219_21539__$1,temp__5823__auto___21538,path,map__20935,map__20935__$1,msg,updates,reload_info))
);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load CSS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([path_match_21546], 0));

goog.dom.insertSiblingAfter(new_link_21547,node_21545);


var G__21579 = cljs.core.next(seq__21219_21539__$1);
var G__21580 = null;
var G__21581 = (0);
var G__21582 = (0);
seq__21219_21488 = G__21579;
chunk__21223_21489 = G__21580;
count__21224_21490 = G__21581;
i__21225_21491 = G__21582;
continue;
} else {
var G__21583 = cljs.core.next(seq__21219_21539__$1);
var G__21584 = null;
var G__21585 = (0);
var G__21586 = (0);
seq__21219_21488 = G__21583;
chunk__21223_21489 = G__21584;
count__21224_21490 = G__21585;
i__21225_21491 = G__21586;
continue;
}
} else {
var G__21587 = cljs.core.next(seq__21219_21539__$1);
var G__21588 = null;
var G__21589 = (0);
var G__21590 = (0);
seq__21219_21488 = G__21587;
chunk__21223_21489 = G__21588;
count__21224_21490 = G__21589;
i__21225_21491 = G__21590;
continue;
}
}
} else {
}
}
break;
}


var G__21591 = seq__20936;
var G__21592 = chunk__20938;
var G__21593 = count__20939;
var G__21594 = (i__20940 + (1));
seq__20936 = G__21591;
chunk__20938 = G__21592;
count__20939 = G__21593;
i__20940 = G__21594;
continue;
} else {
var G__21595 = seq__20936;
var G__21596 = chunk__20938;
var G__21597 = count__20939;
var G__21598 = (i__20940 + (1));
seq__20936 = G__21595;
chunk__20938 = G__21596;
count__20939 = G__21597;
i__20940 = G__21598;
continue;
}
} else {
var temp__5823__auto__ = cljs.core.seq(seq__20936);
if(temp__5823__auto__){
var seq__20936__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__20936__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__20936__$1);
var G__21599 = cljs.core.chunk_rest(seq__20936__$1);
var G__21600 = c__5673__auto__;
var G__21601 = cljs.core.count(c__5673__auto__);
var G__21602 = (0);
seq__20936 = G__21599;
chunk__20938 = G__21600;
count__20939 = G__21601;
i__20940 = G__21602;
continue;
} else {
var path = cljs.core.first(seq__20936__$1);
if(clojure.string.ends_with_QMARK_(path,"css")){
var seq__21293_21603 = cljs.core.seq(cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(document.querySelectorAll("link[rel=\"stylesheet\"]")));
var chunk__21297_21604 = null;
var count__21298_21605 = (0);
var i__21299_21606 = (0);
while(true){
if((i__21299_21606 < count__21298_21605)){
var node_21608 = chunk__21297_21604.cljs$core$IIndexed$_nth$arity$2(null,i__21299_21606);
if(cljs.core.not(node_21608.shadow$old)){
var path_match_21609 = shadow.cljs.devtools.client.browser.match_paths(node_21608.getAttribute("href"),path);
if(cljs.core.truth_(path_match_21609)){
var new_link_21610 = (function (){var G__21329 = node_21608.cloneNode(true);
G__21329.setAttribute("href",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(path_match_21609)+"?r="+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.rand.cljs$core$IFn$_invoke$arity$0())));

return G__21329;
})();
(node_21608.shadow$old = true);

(new_link_21610.onload = ((function (seq__21293_21603,chunk__21297_21604,count__21298_21605,i__21299_21606,seq__20936,chunk__20938,count__20939,i__20940,new_link_21610,path_match_21609,node_21608,path,seq__20936__$1,temp__5823__auto__,map__20935,map__20935__$1,msg,updates,reload_info){
return (function (e){
var seq__21330_21611 = cljs.core.seq(cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(msg,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"reload-info","reload-info",1648088086),new cljs.core.Keyword(null,"asset-load","asset-load",-1925902322)], null)));
var chunk__21332_21612 = null;
var count__21333_21613 = (0);
var i__21334_21614 = (0);
while(true){
if((i__21334_21614 < count__21333_21613)){
var map__21342_21615 = chunk__21332_21612.cljs$core$IIndexed$_nth$arity$2(null,i__21334_21614);
var map__21342_21616__$1 = cljs.core.__destructure_map(map__21342_21615);
var task_21617 = map__21342_21616__$1;
var fn_str_21618 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21342_21616__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21619 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21342_21616__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21620 = goog.getObjectByName(fn_str_21618,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21619)));

(fn_obj_21620.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21620.cljs$core$IFn$_invoke$arity$2(path,new_link_21610) : fn_obj_21620.call(null,path,new_link_21610));


var G__21621 = seq__21330_21611;
var G__21622 = chunk__21332_21612;
var G__21623 = count__21333_21613;
var G__21624 = (i__21334_21614 + (1));
seq__21330_21611 = G__21621;
chunk__21332_21612 = G__21622;
count__21333_21613 = G__21623;
i__21334_21614 = G__21624;
continue;
} else {
var temp__5823__auto___21625__$1 = cljs.core.seq(seq__21330_21611);
if(temp__5823__auto___21625__$1){
var seq__21330_21626__$1 = temp__5823__auto___21625__$1;
if(cljs.core.chunked_seq_QMARK_(seq__21330_21626__$1)){
var c__5673__auto___21627 = cljs.core.chunk_first(seq__21330_21626__$1);
var G__21628 = cljs.core.chunk_rest(seq__21330_21626__$1);
var G__21629 = c__5673__auto___21627;
var G__21630 = cljs.core.count(c__5673__auto___21627);
var G__21631 = (0);
seq__21330_21611 = G__21628;
chunk__21332_21612 = G__21629;
count__21333_21613 = G__21630;
i__21334_21614 = G__21631;
continue;
} else {
var map__21343_21632 = cljs.core.first(seq__21330_21626__$1);
var map__21343_21633__$1 = cljs.core.__destructure_map(map__21343_21632);
var task_21634 = map__21343_21633__$1;
var fn_str_21635 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21343_21633__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21636 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21343_21633__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21637 = goog.getObjectByName(fn_str_21635,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21636)));

(fn_obj_21637.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21637.cljs$core$IFn$_invoke$arity$2(path,new_link_21610) : fn_obj_21637.call(null,path,new_link_21610));


var G__21638 = cljs.core.next(seq__21330_21626__$1);
var G__21639 = null;
var G__21640 = (0);
var G__21641 = (0);
seq__21330_21611 = G__21638;
chunk__21332_21612 = G__21639;
count__21333_21613 = G__21640;
i__21334_21614 = G__21641;
continue;
}
} else {
}
}
break;
}

return goog.dom.removeNode(node_21608);
});})(seq__21293_21603,chunk__21297_21604,count__21298_21605,i__21299_21606,seq__20936,chunk__20938,count__20939,i__20940,new_link_21610,path_match_21609,node_21608,path,seq__20936__$1,temp__5823__auto__,map__20935,map__20935__$1,msg,updates,reload_info))
);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load CSS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([path_match_21609], 0));

goog.dom.insertSiblingAfter(new_link_21610,node_21608);


var G__21642 = seq__21293_21603;
var G__21643 = chunk__21297_21604;
var G__21644 = count__21298_21605;
var G__21645 = (i__21299_21606 + (1));
seq__21293_21603 = G__21642;
chunk__21297_21604 = G__21643;
count__21298_21605 = G__21644;
i__21299_21606 = G__21645;
continue;
} else {
var G__21646 = seq__21293_21603;
var G__21647 = chunk__21297_21604;
var G__21648 = count__21298_21605;
var G__21649 = (i__21299_21606 + (1));
seq__21293_21603 = G__21646;
chunk__21297_21604 = G__21647;
count__21298_21605 = G__21648;
i__21299_21606 = G__21649;
continue;
}
} else {
var G__21650 = seq__21293_21603;
var G__21651 = chunk__21297_21604;
var G__21652 = count__21298_21605;
var G__21653 = (i__21299_21606 + (1));
seq__21293_21603 = G__21650;
chunk__21297_21604 = G__21651;
count__21298_21605 = G__21652;
i__21299_21606 = G__21653;
continue;
}
} else {
var temp__5823__auto___21655__$1 = cljs.core.seq(seq__21293_21603);
if(temp__5823__auto___21655__$1){
var seq__21293_21656__$1 = temp__5823__auto___21655__$1;
if(cljs.core.chunked_seq_QMARK_(seq__21293_21656__$1)){
var c__5673__auto___21657 = cljs.core.chunk_first(seq__21293_21656__$1);
var G__21658 = cljs.core.chunk_rest(seq__21293_21656__$1);
var G__21659 = c__5673__auto___21657;
var G__21660 = cljs.core.count(c__5673__auto___21657);
var G__21661 = (0);
seq__21293_21603 = G__21658;
chunk__21297_21604 = G__21659;
count__21298_21605 = G__21660;
i__21299_21606 = G__21661;
continue;
} else {
var node_21662 = cljs.core.first(seq__21293_21656__$1);
if(cljs.core.not(node_21662.shadow$old)){
var path_match_21663 = shadow.cljs.devtools.client.browser.match_paths(node_21662.getAttribute("href"),path);
if(cljs.core.truth_(path_match_21663)){
var new_link_21664 = (function (){var G__21344 = node_21662.cloneNode(true);
G__21344.setAttribute("href",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(path_match_21663)+"?r="+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.rand.cljs$core$IFn$_invoke$arity$0())));

return G__21344;
})();
(node_21662.shadow$old = true);

(new_link_21664.onload = ((function (seq__21293_21603,chunk__21297_21604,count__21298_21605,i__21299_21606,seq__20936,chunk__20938,count__20939,i__20940,new_link_21664,path_match_21663,node_21662,seq__21293_21656__$1,temp__5823__auto___21655__$1,path,seq__20936__$1,temp__5823__auto__,map__20935,map__20935__$1,msg,updates,reload_info){
return (function (e){
var seq__21345_21665 = cljs.core.seq(cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(msg,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"reload-info","reload-info",1648088086),new cljs.core.Keyword(null,"asset-load","asset-load",-1925902322)], null)));
var chunk__21347_21666 = null;
var count__21348_21667 = (0);
var i__21349_21668 = (0);
while(true){
if((i__21349_21668 < count__21348_21667)){
var map__21359_21669 = chunk__21347_21666.cljs$core$IIndexed$_nth$arity$2(null,i__21349_21668);
var map__21359_21670__$1 = cljs.core.__destructure_map(map__21359_21669);
var task_21671 = map__21359_21670__$1;
var fn_str_21672 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21359_21670__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21673 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21359_21670__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21674 = goog.getObjectByName(fn_str_21672,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21673)));

(fn_obj_21674.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21674.cljs$core$IFn$_invoke$arity$2(path,new_link_21664) : fn_obj_21674.call(null,path,new_link_21664));


var G__21675 = seq__21345_21665;
var G__21676 = chunk__21347_21666;
var G__21677 = count__21348_21667;
var G__21678 = (i__21349_21668 + (1));
seq__21345_21665 = G__21675;
chunk__21347_21666 = G__21676;
count__21348_21667 = G__21677;
i__21349_21668 = G__21678;
continue;
} else {
var temp__5823__auto___21679__$2 = cljs.core.seq(seq__21345_21665);
if(temp__5823__auto___21679__$2){
var seq__21345_21680__$1 = temp__5823__auto___21679__$2;
if(cljs.core.chunked_seq_QMARK_(seq__21345_21680__$1)){
var c__5673__auto___21681 = cljs.core.chunk_first(seq__21345_21680__$1);
var G__21682 = cljs.core.chunk_rest(seq__21345_21680__$1);
var G__21683 = c__5673__auto___21681;
var G__21684 = cljs.core.count(c__5673__auto___21681);
var G__21685 = (0);
seq__21345_21665 = G__21682;
chunk__21347_21666 = G__21683;
count__21348_21667 = G__21684;
i__21349_21668 = G__21685;
continue;
} else {
var map__21360_21687 = cljs.core.first(seq__21345_21680__$1);
var map__21360_21688__$1 = cljs.core.__destructure_map(map__21360_21687);
var task_21689 = map__21360_21688__$1;
var fn_str_21690 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21360_21688__$1,new cljs.core.Keyword(null,"fn-str","fn-str",-1348506402));
var fn_sym_21691 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21360_21688__$1,new cljs.core.Keyword(null,"fn-sym","fn-sym",1423988510));
var fn_obj_21692 = goog.getObjectByName(fn_str_21690,$CLJS);
shadow.cljs.devtools.client.browser.devtools_msg((""+"call "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fn_sym_21691)));

(fn_obj_21692.cljs$core$IFn$_invoke$arity$2 ? fn_obj_21692.cljs$core$IFn$_invoke$arity$2(path,new_link_21664) : fn_obj_21692.call(null,path,new_link_21664));


var G__21693 = cljs.core.next(seq__21345_21680__$1);
var G__21694 = null;
var G__21695 = (0);
var G__21696 = (0);
seq__21345_21665 = G__21693;
chunk__21347_21666 = G__21694;
count__21348_21667 = G__21695;
i__21349_21668 = G__21696;
continue;
}
} else {
}
}
break;
}

return goog.dom.removeNode(node_21662);
});})(seq__21293_21603,chunk__21297_21604,count__21298_21605,i__21299_21606,seq__20936,chunk__20938,count__20939,i__20940,new_link_21664,path_match_21663,node_21662,seq__21293_21656__$1,temp__5823__auto___21655__$1,path,seq__20936__$1,temp__5823__auto__,map__20935,map__20935__$1,msg,updates,reload_info))
);

shadow.cljs.devtools.client.browser.devtools_msg.cljs$core$IFn$_invoke$arity$variadic("load CSS",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([path_match_21663], 0));

goog.dom.insertSiblingAfter(new_link_21664,node_21662);


var G__21697 = cljs.core.next(seq__21293_21656__$1);
var G__21698 = null;
var G__21699 = (0);
var G__21700 = (0);
seq__21293_21603 = G__21697;
chunk__21297_21604 = G__21698;
count__21298_21605 = G__21699;
i__21299_21606 = G__21700;
continue;
} else {
var G__21701 = cljs.core.next(seq__21293_21656__$1);
var G__21702 = null;
var G__21703 = (0);
var G__21704 = (0);
seq__21293_21603 = G__21701;
chunk__21297_21604 = G__21702;
count__21298_21605 = G__21703;
i__21299_21606 = G__21704;
continue;
}
} else {
var G__21705 = cljs.core.next(seq__21293_21656__$1);
var G__21706 = null;
var G__21707 = (0);
var G__21708 = (0);
seq__21293_21603 = G__21705;
chunk__21297_21604 = G__21706;
count__21298_21605 = G__21707;
i__21299_21606 = G__21708;
continue;
}
}
} else {
}
}
break;
}


var G__21709 = cljs.core.next(seq__20936__$1);
var G__21710 = null;
var G__21711 = (0);
var G__21712 = (0);
seq__20936 = G__21709;
chunk__20938 = G__21710;
count__20939 = G__21711;
i__20940 = G__21712;
continue;
} else {
var G__21713 = cljs.core.next(seq__20936__$1);
var G__21714 = null;
var G__21715 = (0);
var G__21716 = (0);
seq__20936 = G__21713;
chunk__20938 = G__21714;
count__20939 = G__21715;
i__20940 = G__21716;
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
try{var G__21366 = shadow.cljs.devtools.client.browser.global_eval(code);
return (success.cljs$core$IFn$_invoke$arity$1 ? success.cljs$core$IFn$_invoke$arity$1(G__21366) : success.call(null,G__21366));
}catch (e21365){var e = e21365;
return (fail.cljs$core$IFn$_invoke$arity$1 ? fail.cljs$core$IFn$_invoke$arity$1(e) : fail.call(null,e));
}}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IHostSpecific$ = cljs.core.PROTOCOL_SENTINEL);

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IHostSpecific$do_invoke$arity$5 = (function (this$,ns,p__21367,success,fail){
var map__21368 = p__21367;
var map__21368__$1 = cljs.core.__destructure_map(map__21368);
var js = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21368__$1,new cljs.core.Keyword(null,"js","js",1768080579));
var this$__$1 = this;
try{var G__21370 = shadow.cljs.devtools.client.browser.global_eval(js);
return (success.cljs$core$IFn$_invoke$arity$1 ? success.cljs$core$IFn$_invoke$arity$1(G__21370) : success.call(null,G__21370));
}catch (e21369){var e = e21369;
return (fail.cljs$core$IFn$_invoke$arity$1 ? fail.cljs$core$IFn$_invoke$arity$1(e) : fail.call(null,e));
}}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IHostSpecific$do_repl_init$arity$4 = (function (runtime,p__21371,done,error){
var map__21372 = p__21371;
var map__21372__$1 = cljs.core.__destructure_map(map__21372);
var repl_sources = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21372__$1,new cljs.core.Keyword(null,"repl-sources","repl-sources",723867535));
var runtime__$1 = this;
return shadow.cljs.devtools.client.shared.load_sources(runtime__$1,cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentVector.EMPTY,cljs.core.remove.cljs$core$IFn$_invoke$arity$2(shadow.cljs.devtools.client.env.src_is_loaded_QMARK_,repl_sources)),(function (sources){
shadow.cljs.devtools.client.browser.do_js_load(sources);

return (done.cljs$core$IFn$_invoke$arity$0 ? done.cljs$core$IFn$_invoke$arity$0() : done.call(null));
}));
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IHostSpecific$do_repl_require$arity$4 = (function (runtime,p__21373,done,error){
var map__21374 = p__21373;
var map__21374__$1 = cljs.core.__destructure_map(map__21374);
var msg = map__21374__$1;
var sources = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21374__$1,new cljs.core.Keyword(null,"sources","sources",-321166424));
var reload_namespaces = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21374__$1,new cljs.core.Keyword(null,"reload-namespaces","reload-namespaces",250210134));
var js_requires = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21374__$1,new cljs.core.Keyword(null,"js-requires","js-requires",-1311472051));
var runtime__$1 = this;
var sources_to_load = cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentVector.EMPTY,cljs.core.remove.cljs$core$IFn$_invoke$arity$2((function (p__21375){
var map__21376 = p__21375;
var map__21376__$1 = cljs.core.__destructure_map(map__21376);
var src = map__21376__$1;
var provides = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21376__$1,new cljs.core.Keyword(null,"provides","provides",-1634397992));
var and__5140__auto__ = shadow.cljs.devtools.client.env.src_is_loaded_QMARK_(src);
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(cljs.core.some(reload_namespaces,provides));
} else {
return and__5140__auto__;
}
}),sources));
if(cljs.core.not(cljs.core.seq(sources_to_load))){
var G__21377 = cljs.core.PersistentVector.EMPTY;
return (done.cljs$core$IFn$_invoke$arity$1 ? done.cljs$core$IFn$_invoke$arity$1(G__21377) : done.call(null,G__21377));
} else {
return shadow.remote.runtime.shared.call.cljs$core$IFn$_invoke$arity$3(runtime__$1,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"op","op",-1882987955),new cljs.core.Keyword(null,"cljs-load-sources","cljs-load-sources",-1458295962),new cljs.core.Keyword(null,"to","to",192099007),shadow.cljs.devtools.client.env.worker_client_id,new cljs.core.Keyword(null,"sources","sources",-321166424),cljs.core.into.cljs$core$IFn$_invoke$arity$3(cljs.core.PersistentVector.EMPTY,cljs.core.map.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"resource-id","resource-id",-1308422582)),sources_to_load)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"cljs-sources","cljs-sources",31121610),(function (p__21378){
var map__21379 = p__21378;
var map__21379__$1 = cljs.core.__destructure_map(map__21379);
var msg__$1 = map__21379__$1;
var sources__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21379__$1,new cljs.core.Keyword(null,"sources","sources",-321166424));
try{shadow.cljs.devtools.client.browser.do_js_load(sources__$1);

if(cljs.core.seq(js_requires)){
shadow.cljs.devtools.client.browser.do_js_requires(js_requires);
} else {
}

return (done.cljs$core$IFn$_invoke$arity$1 ? done.cljs$core$IFn$_invoke$arity$1(sources_to_load) : done.call(null,sources_to_load));
}catch (e21380){var ex = e21380;
return (error.cljs$core$IFn$_invoke$arity$1 ? error.cljs$core$IFn$_invoke$arity$1(ex) : error.call(null,ex));
}})], null));
}
}));

shadow.cljs.devtools.client.shared.add_plugin_BANG_(new cljs.core.Keyword("shadow.cljs.devtools.client.browser","client","shadow.cljs.devtools.client.browser/client",-1461019282),cljs.core.PersistentHashSet.EMPTY,(function (p__21381){
var map__21382 = p__21381;
var map__21382__$1 = cljs.core.__destructure_map(map__21382);
var env = map__21382__$1;
var runtime = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21382__$1,new cljs.core.Keyword(null,"runtime","runtime",-1331573996));
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
}),new cljs.core.Keyword("shadow.cljs.devtools.client.env","worker-notify","shadow.cljs.devtools.client.env/worker-notify",-1456820670),(function (p__21383){
var map__21384 = p__21383;
var map__21384__$1 = cljs.core.__destructure_map(map__21384);
var event_op = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21384__$1,new cljs.core.Keyword(null,"event-op","event-op",200358057));
var client_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21384__$1,new cljs.core.Keyword(null,"client-id","client-id",-464622140));
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
}),(function (p__21385){
var map__21386 = p__21385;
var map__21386__$1 = cljs.core.__destructure_map(map__21386);
var svc = map__21386__$1;
var runtime = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21386__$1,new cljs.core.Keyword(null,"runtime","runtime",-1331573996));
return shadow.remote.runtime.api.del_extension(runtime,new cljs.core.Keyword("shadow.cljs.devtools.client.browser","client","shadow.cljs.devtools.client.browser/client",-1461019282));
}));

shadow.cljs.devtools.client.shared.init_runtime_BANG_(shadow.cljs.devtools.client.browser.client_info,shadow.cljs.devtools.client.websocket.start,shadow.cljs.devtools.client.websocket.send,shadow.cljs.devtools.client.websocket.stop);
} else {
}

//# sourceMappingURL=shadow.cljs.devtools.client.browser.js.map
