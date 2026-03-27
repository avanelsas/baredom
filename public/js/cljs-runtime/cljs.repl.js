goog.provide('cljs.repl');
cljs.repl.print_doc = (function cljs$repl$print_doc(p__18798){
var map__18801 = p__18798;
var map__18801__$1 = cljs.core.__destructure_map(map__18801);
var m = map__18801__$1;
var n = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18801__$1,new cljs.core.Keyword(null,"ns","ns",441598760));
var nm = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18801__$1,new cljs.core.Keyword(null,"name","name",1843675177));
cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2(["-------------------------"], 0));

cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var or__5142__auto__ = new cljs.core.Keyword(null,"spec","spec",347520401).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var temp__5823__auto__ = new cljs.core.Keyword(null,"ns","ns",441598760).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__5823__auto__)){
var ns = temp__5823__auto__;
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(ns)+"/");
} else {
return null;
}
})())+cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m)));
}
})()], 0));

if(cljs.core.truth_(new cljs.core.Keyword(null,"protocol","protocol",652470118).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2(["Protocol"], 0));
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"forms","forms",2045992350).cljs$core$IFn$_invoke$arity$1(m))){
var seq__18809_19309 = cljs.core.seq(new cljs.core.Keyword(null,"forms","forms",2045992350).cljs$core$IFn$_invoke$arity$1(m));
var chunk__18810_19310 = null;
var count__18811_19311 = (0);
var i__18812_19312 = (0);
while(true){
if((i__18812_19312 < count__18811_19311)){
var f_19315 = chunk__18810_19310.cljs$core$IIndexed$_nth$arity$2(null,i__18812_19312);
cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2(["  ",f_19315], 0));


var G__19316 = seq__18809_19309;
var G__19317 = chunk__18810_19310;
var G__19318 = count__18811_19311;
var G__19319 = (i__18812_19312 + (1));
seq__18809_19309 = G__19316;
chunk__18810_19310 = G__19317;
count__18811_19311 = G__19318;
i__18812_19312 = G__19319;
continue;
} else {
var temp__5823__auto___19320 = cljs.core.seq(seq__18809_19309);
if(temp__5823__auto___19320){
var seq__18809_19322__$1 = temp__5823__auto___19320;
if(cljs.core.chunked_seq_QMARK_(seq__18809_19322__$1)){
var c__5673__auto___19323 = cljs.core.chunk_first(seq__18809_19322__$1);
var G__19324 = cljs.core.chunk_rest(seq__18809_19322__$1);
var G__19325 = c__5673__auto___19323;
var G__19326 = cljs.core.count(c__5673__auto___19323);
var G__19327 = (0);
seq__18809_19309 = G__19324;
chunk__18810_19310 = G__19325;
count__18811_19311 = G__19326;
i__18812_19312 = G__19327;
continue;
} else {
var f_19331 = cljs.core.first(seq__18809_19322__$1);
cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2(["  ",f_19331], 0));


var G__19333 = cljs.core.next(seq__18809_19322__$1);
var G__19334 = null;
var G__19335 = (0);
var G__19336 = (0);
seq__18809_19309 = G__19333;
chunk__18810_19310 = G__19334;
count__18811_19311 = G__19335;
i__18812_19312 = G__19336;
continue;
}
} else {
}
}
break;
}
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m))){
var arglists_19344 = new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_((function (){var or__5142__auto__ = new cljs.core.Keyword(null,"macro","macro",-867863404).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return new cljs.core.Keyword(null,"repl-special-function","repl-special-function",1262603725).cljs$core$IFn$_invoke$arity$1(m);
}
})())){
cljs.core.prn.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([arglists_19344], 0));
} else {
cljs.core.prn.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Symbol(null,"quote","quote",1377916282,null),cljs.core.first(arglists_19344)))?cljs.core.second(arglists_19344):arglists_19344)], 0));
}
} else {
}
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"special-form","special-form",-1326536374).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2(["Special Form"], 0));

cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",new cljs.core.Keyword(null,"doc","doc",1913296891).cljs$core$IFn$_invoke$arity$1(m)], 0));

if(cljs.core.contains_QMARK_(m,new cljs.core.Keyword(null,"url","url",276297046))){
if(cljs.core.truth_(new cljs.core.Keyword(null,"url","url",276297046).cljs$core$IFn$_invoke$arity$1(m))){
return cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(""+"\n  Please see http://clojure.org/"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"url","url",276297046).cljs$core$IFn$_invoke$arity$1(m)))], 0));
} else {
return null;
}
} else {
return cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(""+"\n  Please see http://clojure.org/special_forms#"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m)))], 0));
}
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"macro","macro",-867863404).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2(["Macro"], 0));
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"spec","spec",347520401).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2(["Spec"], 0));
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"repl-special-function","repl-special-function",1262603725).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2(["REPL Special Function"], 0));
} else {
}

cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",new cljs.core.Keyword(null,"doc","doc",1913296891).cljs$core$IFn$_invoke$arity$1(m)], 0));

if(cljs.core.truth_(new cljs.core.Keyword(null,"protocol","protocol",652470118).cljs$core$IFn$_invoke$arity$1(m))){
var seq__18872_19375 = cljs.core.seq(new cljs.core.Keyword(null,"methods","methods",453930866).cljs$core$IFn$_invoke$arity$1(m));
var chunk__18873_19376 = null;
var count__18874_19377 = (0);
var i__18875_19378 = (0);
while(true){
if((i__18875_19378 < count__18874_19377)){
var vec__18895_19393 = chunk__18873_19376.cljs$core$IIndexed$_nth$arity$2(null,i__18875_19378);
var name_19394 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18895_19393,(0),null);
var map__18898_19395 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18895_19393,(1),null);
var map__18898_19396__$1 = cljs.core.__destructure_map(map__18898_19395);
var doc_19397 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18898_19396__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists_19398 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18898_19396__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println();

cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",name_19394], 0));

cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",arglists_19398], 0));

if(cljs.core.truth_(doc_19397)){
cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",doc_19397], 0));
} else {
}


var G__19419 = seq__18872_19375;
var G__19420 = chunk__18873_19376;
var G__19421 = count__18874_19377;
var G__19422 = (i__18875_19378 + (1));
seq__18872_19375 = G__19419;
chunk__18873_19376 = G__19420;
count__18874_19377 = G__19421;
i__18875_19378 = G__19422;
continue;
} else {
var temp__5823__auto___19424 = cljs.core.seq(seq__18872_19375);
if(temp__5823__auto___19424){
var seq__18872_19425__$1 = temp__5823__auto___19424;
if(cljs.core.chunked_seq_QMARK_(seq__18872_19425__$1)){
var c__5673__auto___19427 = cljs.core.chunk_first(seq__18872_19425__$1);
var G__19428 = cljs.core.chunk_rest(seq__18872_19425__$1);
var G__19429 = c__5673__auto___19427;
var G__19430 = cljs.core.count(c__5673__auto___19427);
var G__19431 = (0);
seq__18872_19375 = G__19428;
chunk__18873_19376 = G__19429;
count__18874_19377 = G__19430;
i__18875_19378 = G__19431;
continue;
} else {
var vec__18905_19435 = cljs.core.first(seq__18872_19425__$1);
var name_19436 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18905_19435,(0),null);
var map__18908_19437 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18905_19435,(1),null);
var map__18908_19438__$1 = cljs.core.__destructure_map(map__18908_19437);
var doc_19439 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18908_19438__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists_19440 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18908_19438__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println();

cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",name_19436], 0));

cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",arglists_19440], 0));

if(cljs.core.truth_(doc_19439)){
cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",doc_19439], 0));
} else {
}


var G__19443 = cljs.core.next(seq__18872_19425__$1);
var G__19444 = null;
var G__19445 = (0);
var G__19446 = (0);
seq__18872_19375 = G__19443;
chunk__18873_19376 = G__19444;
count__18874_19377 = G__19445;
i__18875_19378 = G__19446;
continue;
}
} else {
}
}
break;
}
} else {
}

if(cljs.core.truth_(n)){
var temp__5823__auto__ = cljs.spec.alpha.get_spec(cljs.core.symbol.cljs$core$IFn$_invoke$arity$2((""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.ns_name(n))),cljs.core.name(nm)));
if(cljs.core.truth_(temp__5823__auto__)){
var fnspec = temp__5823__auto__;
cljs.core.print.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2(["Spec"], 0));

var seq__18917 = cljs.core.seq(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"args","args",1315556576),new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Keyword(null,"fn","fn",-1175266204)], null));
var chunk__18918 = null;
var count__18919 = (0);
var i__18920 = (0);
while(true){
if((i__18920 < count__18919)){
var role = chunk__18918.cljs$core$IIndexed$_nth$arity$2(null,i__18920);
var temp__5823__auto___19456__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(fnspec,role);
if(cljs.core.truth_(temp__5823__auto___19456__$1)){
var spec_19461 = temp__5823__auto___19456__$1;
cljs.core.print.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(""+"\n "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name(role))+":"),cljs.spec.alpha.describe(spec_19461)], 0));
} else {
}


var G__19466 = seq__18917;
var G__19467 = chunk__18918;
var G__19468 = count__18919;
var G__19469 = (i__18920 + (1));
seq__18917 = G__19466;
chunk__18918 = G__19467;
count__18919 = G__19468;
i__18920 = G__19469;
continue;
} else {
var temp__5823__auto____$1 = cljs.core.seq(seq__18917);
if(temp__5823__auto____$1){
var seq__18917__$1 = temp__5823__auto____$1;
if(cljs.core.chunked_seq_QMARK_(seq__18917__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__18917__$1);
var G__19475 = cljs.core.chunk_rest(seq__18917__$1);
var G__19476 = c__5673__auto__;
var G__19477 = cljs.core.count(c__5673__auto__);
var G__19478 = (0);
seq__18917 = G__19475;
chunk__18918 = G__19476;
count__18919 = G__19477;
i__18920 = G__19478;
continue;
} else {
var role = cljs.core.first(seq__18917__$1);
var temp__5823__auto___19480__$2 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(fnspec,role);
if(cljs.core.truth_(temp__5823__auto___19480__$2)){
var spec_19481 = temp__5823__auto___19480__$2;
cljs.core.print.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(""+"\n "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name(role))+":"),cljs.spec.alpha.describe(spec_19481)], 0));
} else {
}


var G__19483 = cljs.core.next(seq__18917__$1);
var G__19484 = null;
var G__19485 = (0);
var G__19486 = (0);
seq__18917 = G__19483;
chunk__18918 = G__19484;
count__18919 = G__19485;
i__18920 = G__19486;
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
} else {
return null;
}
}
});
/**
 * Constructs a data representation for a Error with keys:
 *  :cause - root cause message
 *  :phase - error phase
 *  :via - cause chain, with cause keys:
 *           :type - exception class symbol
 *           :message - exception message
 *           :data - ex-data
 *           :at - top stack element
 *  :trace - root cause stack elements
 */
cljs.repl.Error__GT_map = (function cljs$repl$Error__GT_map(o){
return cljs.core.Throwable__GT_map(o);
});
/**
 * Returns an analysis of the phase, error, cause, and location of an error that occurred
 *   based on Throwable data, as returned by Throwable->map. All attributes other than phase
 *   are optional:
 *  :clojure.error/phase - keyword phase indicator, one of:
 *    :read-source :compile-syntax-check :compilation :macro-syntax-check :macroexpansion
 *    :execution :read-eval-result :print-eval-result
 *  :clojure.error/source - file name (no path)
 *  :clojure.error/line - integer line number
 *  :clojure.error/column - integer column number
 *  :clojure.error/symbol - symbol being expanded/compiled/invoked
 *  :clojure.error/class - cause exception class symbol
 *  :clojure.error/cause - cause exception message
 *  :clojure.error/spec - explain-data for spec error
 */
cljs.repl.ex_triage = (function cljs$repl$ex_triage(datafied_throwable){
var map__19008 = datafied_throwable;
var map__19008__$1 = cljs.core.__destructure_map(map__19008);
var via = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__19008__$1,new cljs.core.Keyword(null,"via","via",-1904457336));
var trace = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__19008__$1,new cljs.core.Keyword(null,"trace","trace",-1082747415));
var phase = cljs.core.get.cljs$core$IFn$_invoke$arity$3(map__19008__$1,new cljs.core.Keyword(null,"phase","phase",575722892),new cljs.core.Keyword(null,"execution","execution",253283524));
var map__19009 = cljs.core.last(via);
var map__19009__$1 = cljs.core.__destructure_map(map__19009);
var type = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__19009__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var message = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__19009__$1,new cljs.core.Keyword(null,"message","message",-406056002));
var data = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__19009__$1,new cljs.core.Keyword(null,"data","data",-232669377));
var map__19010 = data;
var map__19010__$1 = cljs.core.__destructure_map(map__19010);
var problems = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__19010__$1,new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814));
var fn = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__19010__$1,new cljs.core.Keyword("cljs.spec.alpha","fn","cljs.spec.alpha/fn",408600443));
var caller = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__19010__$1,new cljs.core.Keyword("cljs.spec.test.alpha","caller","cljs.spec.test.alpha/caller",-398302390));
var map__19011 = new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(cljs.core.first(via));
var map__19011__$1 = cljs.core.__destructure_map(map__19011);
var top_data = map__19011__$1;
var source = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__19011__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397));
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3((function (){var G__19024 = phase;
var G__19024__$1 = (((G__19024 instanceof cljs.core.Keyword))?G__19024.fqn:null);
switch (G__19024__$1) {
case "read-source":
var map__19029 = data;
var map__19029__$1 = cljs.core.__destructure_map(map__19029);
var line = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__19029__$1,new cljs.core.Keyword("clojure.error","line","clojure.error/line",-1816287471));
var column = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__19029__$1,new cljs.core.Keyword("clojure.error","column","clojure.error/column",304721553));
var G__19031 = cljs.core.merge.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(cljs.core.second(via)),top_data], 0));
var G__19031__$1 = (cljs.core.truth_(source)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__19031,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397),source):G__19031);
var G__19031__$2 = (cljs.core.truth_((function (){var fexpr__19033 = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["NO_SOURCE_PATH",null,"NO_SOURCE_FILE",null], null), null);
return (fexpr__19033.cljs$core$IFn$_invoke$arity$1 ? fexpr__19033.cljs$core$IFn$_invoke$arity$1(source) : fexpr__19033.call(null,source));
})())?cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(G__19031__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397)):G__19031__$1);
if(cljs.core.truth_(message)){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__19031__$2,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742),message);
} else {
return G__19031__$2;
}

break;
case "compile-syntax-check":
case "compilation":
case "macro-syntax-check":
case "macroexpansion":
var G__19034 = top_data;
var G__19034__$1 = (cljs.core.truth_(source)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__19034,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397),source):G__19034);
var G__19034__$2 = (cljs.core.truth_((function (){var fexpr__19035 = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["NO_SOURCE_PATH",null,"NO_SOURCE_FILE",null], null), null);
return (fexpr__19035.cljs$core$IFn$_invoke$arity$1 ? fexpr__19035.cljs$core$IFn$_invoke$arity$1(source) : fexpr__19035.call(null,source));
})())?cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(G__19034__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397)):G__19034__$1);
var G__19034__$3 = (cljs.core.truth_(type)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__19034__$2,new cljs.core.Keyword("clojure.error","class","clojure.error/class",278435890),type):G__19034__$2);
var G__19034__$4 = (cljs.core.truth_(message)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__19034__$3,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742),message):G__19034__$3);
if(cljs.core.truth_(problems)){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__19034__$4,new cljs.core.Keyword("clojure.error","spec","clojure.error/spec",2055032595),data);
} else {
return G__19034__$4;
}

break;
case "read-eval-result":
case "print-eval-result":
var vec__19044 = cljs.core.first(trace);
var source__$1 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__19044,(0),null);
var method = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__19044,(1),null);
var file = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__19044,(2),null);
var line = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__19044,(3),null);
var G__19051 = top_data;
var G__19051__$1 = (cljs.core.truth_(line)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__19051,new cljs.core.Keyword("clojure.error","line","clojure.error/line",-1816287471),line):G__19051);
var G__19051__$2 = (cljs.core.truth_(file)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__19051__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397),file):G__19051__$1);
var G__19051__$3 = (cljs.core.truth_((function (){var and__5140__auto__ = source__$1;
if(cljs.core.truth_(and__5140__auto__)){
return method;
} else {
return and__5140__auto__;
}
})())?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__19051__$2,new cljs.core.Keyword("clojure.error","symbol","clojure.error/symbol",1544821994),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[source__$1,method],null))):G__19051__$2);
var G__19051__$4 = (cljs.core.truth_(type)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__19051__$3,new cljs.core.Keyword("clojure.error","class","clojure.error/class",278435890),type):G__19051__$3);
if(cljs.core.truth_(message)){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__19051__$4,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742),message);
} else {
return G__19051__$4;
}

break;
case "execution":
var vec__19056 = cljs.core.first(trace);
var source__$1 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__19056,(0),null);
var method = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__19056,(1),null);
var file = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__19056,(2),null);
var line = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__19056,(3),null);
var file__$1 = cljs.core.first(cljs.core.remove.cljs$core$IFn$_invoke$arity$2((function (p1__18999_SHARP_){
var or__5142__auto__ = (p1__18999_SHARP_ == null);
if(or__5142__auto__){
return or__5142__auto__;
} else {
var fexpr__19070 = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["NO_SOURCE_PATH",null,"NO_SOURCE_FILE",null], null), null);
return (fexpr__19070.cljs$core$IFn$_invoke$arity$1 ? fexpr__19070.cljs$core$IFn$_invoke$arity$1(p1__18999_SHARP_) : fexpr__19070.call(null,p1__18999_SHARP_));
}
}),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"file","file",-1269645878).cljs$core$IFn$_invoke$arity$1(caller),file], null)));
var err_line = (function (){var or__5142__auto__ = new cljs.core.Keyword(null,"line","line",212345235).cljs$core$IFn$_invoke$arity$1(caller);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return line;
}
})();
var G__19071 = new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword("clojure.error","class","clojure.error/class",278435890),type], null);
var G__19071__$1 = (cljs.core.truth_(err_line)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__19071,new cljs.core.Keyword("clojure.error","line","clojure.error/line",-1816287471),err_line):G__19071);
var G__19071__$2 = (cljs.core.truth_(message)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__19071__$1,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742),message):G__19071__$1);
var G__19071__$3 = (cljs.core.truth_((function (){var or__5142__auto__ = fn;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
var and__5140__auto__ = source__$1;
if(cljs.core.truth_(and__5140__auto__)){
return method;
} else {
return and__5140__auto__;
}
}
})())?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__19071__$2,new cljs.core.Keyword("clojure.error","symbol","clojure.error/symbol",1544821994),(function (){var or__5142__auto__ = fn;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[source__$1,method],null));
}
})()):G__19071__$2);
var G__19071__$4 = (cljs.core.truth_(file__$1)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__19071__$3,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397),file__$1):G__19071__$3);
if(cljs.core.truth_(problems)){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__19071__$4,new cljs.core.Keyword("clojure.error","spec","clojure.error/spec",2055032595),data);
} else {
return G__19071__$4;
}

break;
default:
throw (new Error((""+"No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__19024__$1))));

}
})(),new cljs.core.Keyword("clojure.error","phase","clojure.error/phase",275140358),phase);
});
/**
 * Returns a string from exception data, as produced by ex-triage.
 *   The first line summarizes the exception phase and location.
 *   The subsequent lines describe the cause.
 */
cljs.repl.ex_str = (function cljs$repl$ex_str(p__19088){
var map__19091 = p__19088;
var map__19091__$1 = cljs.core.__destructure_map(map__19091);
var triage_data = map__19091__$1;
var phase = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__19091__$1,new cljs.core.Keyword("clojure.error","phase","clojure.error/phase",275140358));
var source = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__19091__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397));
var line = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__19091__$1,new cljs.core.Keyword("clojure.error","line","clojure.error/line",-1816287471));
var column = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__19091__$1,new cljs.core.Keyword("clojure.error","column","clojure.error/column",304721553));
var symbol = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__19091__$1,new cljs.core.Keyword("clojure.error","symbol","clojure.error/symbol",1544821994));
var class$ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__19091__$1,new cljs.core.Keyword("clojure.error","class","clojure.error/class",278435890));
var cause = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__19091__$1,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742));
var spec = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__19091__$1,new cljs.core.Keyword("clojure.error","spec","clojure.error/spec",2055032595));
var loc = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var or__5142__auto__ = source;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "<cljs repl>";
}
})())+":"+cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var or__5142__auto__ = line;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (1);
}
})())+cljs.core.str.cljs$core$IFn$_invoke$arity$1((cljs.core.truth_(column)?(""+":"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(column)):"")));
var class_name = cljs.core.name((function (){var or__5142__auto__ = class$;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})());
var simple_class = class_name;
var cause_type = ((cljs.core.contains_QMARK_(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["RuntimeException",null,"Exception",null], null), null),simple_class))?"":(""+" ("+cljs.core.str.cljs$core$IFn$_invoke$arity$1(simple_class)+")"));
var format = goog.string.format;
var G__19108 = phase;
var G__19108__$1 = (((G__19108 instanceof cljs.core.Keyword))?G__19108.fqn:null);
switch (G__19108__$1) {
case "read-source":
return (format.cljs$core$IFn$_invoke$arity$3 ? format.cljs$core$IFn$_invoke$arity$3("Syntax error reading source at (%s).\n%s\n",loc,cause) : format.call(null,"Syntax error reading source at (%s).\n%s\n",loc,cause));

break;
case "macro-syntax-check":
var G__19117 = "Syntax error macroexpanding %sat (%s).\n%s";
var G__19121 = (cljs.core.truth_(symbol)?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)+" "):"");
var G__19123 = loc;
var G__19124 = (cljs.core.truth_(spec)?(function (){var sb__5795__auto__ = (new goog.string.StringBuffer());
var _STAR_print_newline_STAR__orig_val__19137_19634 = cljs.core._STAR_print_newline_STAR_;
var _STAR_print_fn_STAR__orig_val__19138_19635 = cljs.core._STAR_print_fn_STAR_;
var _STAR_print_newline_STAR__temp_val__19139_19636 = true;
var _STAR_print_fn_STAR__temp_val__19140_19637 = (function (x__5796__auto__){
return sb__5795__auto__.append(x__5796__auto__);
});
(cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__temp_val__19139_19636);

(cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__temp_val__19140_19637);

try{cljs.spec.alpha.explain_out(cljs.core.update.cljs$core$IFn$_invoke$arity$3(spec,new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814),(function (probs){
return cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p1__19077_SHARP_){
return cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(p1__19077_SHARP_,new cljs.core.Keyword(null,"in","in",-1531184865));
}),probs);
}))
);
}finally {(cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__orig_val__19138_19635);

(cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__orig_val__19137_19634);
}
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(sb__5795__auto__));
})():(format.cljs$core$IFn$_invoke$arity$2 ? format.cljs$core$IFn$_invoke$arity$2("%s\n",cause) : format.call(null,"%s\n",cause)));
return (format.cljs$core$IFn$_invoke$arity$4 ? format.cljs$core$IFn$_invoke$arity$4(G__19117,G__19121,G__19123,G__19124) : format.call(null,G__19117,G__19121,G__19123,G__19124));

break;
case "macroexpansion":
var G__19175 = "Unexpected error%s macroexpanding %sat (%s).\n%s\n";
var G__19176 = cause_type;
var G__19177 = (cljs.core.truth_(symbol)?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)+" "):"");
var G__19178 = loc;
var G__19181 = cause;
return (format.cljs$core$IFn$_invoke$arity$5 ? format.cljs$core$IFn$_invoke$arity$5(G__19175,G__19176,G__19177,G__19178,G__19181) : format.call(null,G__19175,G__19176,G__19177,G__19178,G__19181));

break;
case "compile-syntax-check":
var G__19188 = "Syntax error%s compiling %sat (%s).\n%s\n";
var G__19189 = cause_type;
var G__19190 = (cljs.core.truth_(symbol)?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)+" "):"");
var G__19191 = loc;
var G__19192 = cause;
return (format.cljs$core$IFn$_invoke$arity$5 ? format.cljs$core$IFn$_invoke$arity$5(G__19188,G__19189,G__19190,G__19191,G__19192) : format.call(null,G__19188,G__19189,G__19190,G__19191,G__19192));

break;
case "compilation":
var G__19198 = "Unexpected error%s compiling %sat (%s).\n%s\n";
var G__19199 = cause_type;
var G__19200 = (cljs.core.truth_(symbol)?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)+" "):"");
var G__19201 = loc;
var G__19202 = cause;
return (format.cljs$core$IFn$_invoke$arity$5 ? format.cljs$core$IFn$_invoke$arity$5(G__19198,G__19199,G__19200,G__19201,G__19202) : format.call(null,G__19198,G__19199,G__19200,G__19201,G__19202));

break;
case "read-eval-result":
return (format.cljs$core$IFn$_invoke$arity$5 ? format.cljs$core$IFn$_invoke$arity$5("Error reading eval result%s at %s (%s).\n%s\n",cause_type,symbol,loc,cause) : format.call(null,"Error reading eval result%s at %s (%s).\n%s\n",cause_type,symbol,loc,cause));

break;
case "print-eval-result":
return (format.cljs$core$IFn$_invoke$arity$5 ? format.cljs$core$IFn$_invoke$arity$5("Error printing return value%s at %s (%s).\n%s\n",cause_type,symbol,loc,cause) : format.call(null,"Error printing return value%s at %s (%s).\n%s\n",cause_type,symbol,loc,cause));

break;
case "execution":
if(cljs.core.truth_(spec)){
var G__19208 = "Execution error - invalid arguments to %s at (%s).\n%s";
var G__19209 = symbol;
var G__19210 = loc;
var G__19211 = (function (){var sb__5795__auto__ = (new goog.string.StringBuffer());
var _STAR_print_newline_STAR__orig_val__19215_19680 = cljs.core._STAR_print_newline_STAR_;
var _STAR_print_fn_STAR__orig_val__19217_19681 = cljs.core._STAR_print_fn_STAR_;
var _STAR_print_newline_STAR__temp_val__19218_19682 = true;
var _STAR_print_fn_STAR__temp_val__19219_19683 = (function (x__5796__auto__){
return sb__5795__auto__.append(x__5796__auto__);
});
(cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__temp_val__19218_19682);

(cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__temp_val__19219_19683);

try{cljs.spec.alpha.explain_out(cljs.core.update.cljs$core$IFn$_invoke$arity$3(spec,new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814),(function (probs){
return cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p1__19083_SHARP_){
return cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(p1__19083_SHARP_,new cljs.core.Keyword(null,"in","in",-1531184865));
}),probs);
}))
);
}finally {(cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__orig_val__19217_19681);

(cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__orig_val__19215_19680);
}
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(sb__5795__auto__));
})();
return (format.cljs$core$IFn$_invoke$arity$4 ? format.cljs$core$IFn$_invoke$arity$4(G__19208,G__19209,G__19210,G__19211) : format.call(null,G__19208,G__19209,G__19210,G__19211));
} else {
var G__19248 = "Execution error%s at %s(%s).\n%s\n";
var G__19249 = cause_type;
var G__19250 = (cljs.core.truth_(symbol)?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)+" "):"");
var G__19251 = loc;
var G__19252 = cause;
return (format.cljs$core$IFn$_invoke$arity$5 ? format.cljs$core$IFn$_invoke$arity$5(G__19248,G__19249,G__19250,G__19251,G__19252) : format.call(null,G__19248,G__19249,G__19250,G__19251,G__19252));
}

break;
default:
throw (new Error((""+"No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__19108__$1))));

}
});
cljs.repl.error__GT_str = (function cljs$repl$error__GT_str(error){
return cljs.repl.ex_str(cljs.repl.ex_triage(cljs.repl.Error__GT_map(error)));
});

//# sourceMappingURL=cljs.repl.js.map
