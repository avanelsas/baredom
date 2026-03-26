goog.provide('cljs.repl');
cljs.repl.print_doc = (function cljs$repl$print_doc(p__18710){
var map__18711 = p__18710;
var map__18711__$1 = cljs.core.__destructure_map(map__18711);
var m = map__18711__$1;
var n = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18711__$1,new cljs.core.Keyword(null,"ns","ns",441598760));
var nm = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18711__$1,new cljs.core.Keyword(null,"name","name",1843675177));
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
var seq__18720_18969 = cljs.core.seq(new cljs.core.Keyword(null,"forms","forms",2045992350).cljs$core$IFn$_invoke$arity$1(m));
var chunk__18721_18970 = null;
var count__18722_18971 = (0);
var i__18723_18972 = (0);
while(true){
if((i__18723_18972 < count__18722_18971)){
var f_18973 = chunk__18721_18970.cljs$core$IIndexed$_nth$arity$2(null,i__18723_18972);
cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2(["  ",f_18973], 0));


var G__18975 = seq__18720_18969;
var G__18976 = chunk__18721_18970;
var G__18977 = count__18722_18971;
var G__18978 = (i__18723_18972 + (1));
seq__18720_18969 = G__18975;
chunk__18721_18970 = G__18976;
count__18722_18971 = G__18977;
i__18723_18972 = G__18978;
continue;
} else {
var temp__5823__auto___18979 = cljs.core.seq(seq__18720_18969);
if(temp__5823__auto___18979){
var seq__18720_18980__$1 = temp__5823__auto___18979;
if(cljs.core.chunked_seq_QMARK_(seq__18720_18980__$1)){
var c__5673__auto___18982 = cljs.core.chunk_first(seq__18720_18980__$1);
var G__18984 = cljs.core.chunk_rest(seq__18720_18980__$1);
var G__18985 = c__5673__auto___18982;
var G__18986 = cljs.core.count(c__5673__auto___18982);
var G__18987 = (0);
seq__18720_18969 = G__18984;
chunk__18721_18970 = G__18985;
count__18722_18971 = G__18986;
i__18723_18972 = G__18987;
continue;
} else {
var f_18993 = cljs.core.first(seq__18720_18980__$1);
cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2(["  ",f_18993], 0));


var G__18994 = cljs.core.next(seq__18720_18980__$1);
var G__18995 = null;
var G__18996 = (0);
var G__18997 = (0);
seq__18720_18969 = G__18994;
chunk__18721_18970 = G__18995;
count__18722_18971 = G__18996;
i__18723_18972 = G__18997;
continue;
}
} else {
}
}
break;
}
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m))){
var arglists_19004 = new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_((function (){var or__5142__auto__ = new cljs.core.Keyword(null,"macro","macro",-867863404).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return new cljs.core.Keyword(null,"repl-special-function","repl-special-function",1262603725).cljs$core$IFn$_invoke$arity$1(m);
}
})())){
cljs.core.prn.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([arglists_19004], 0));
} else {
cljs.core.prn.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Symbol(null,"quote","quote",1377916282,null),cljs.core.first(arglists_19004)))?cljs.core.second(arglists_19004):arglists_19004)], 0));
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
var seq__18742_19021 = cljs.core.seq(new cljs.core.Keyword(null,"methods","methods",453930866).cljs$core$IFn$_invoke$arity$1(m));
var chunk__18743_19022 = null;
var count__18744_19024 = (0);
var i__18745_19025 = (0);
while(true){
if((i__18745_19025 < count__18744_19024)){
var vec__18762_19028 = chunk__18743_19022.cljs$core$IIndexed$_nth$arity$2(null,i__18745_19025);
var name_19030 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18762_19028,(0),null);
var map__18765_19031 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18762_19028,(1),null);
var map__18765_19032__$1 = cljs.core.__destructure_map(map__18765_19031);
var doc_19033 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18765_19032__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists_19034 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18765_19032__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println();

cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",name_19030], 0));

cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",arglists_19034], 0));

if(cljs.core.truth_(doc_19033)){
cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",doc_19033], 0));
} else {
}


var G__19037 = seq__18742_19021;
var G__19038 = chunk__18743_19022;
var G__19039 = count__18744_19024;
var G__19040 = (i__18745_19025 + (1));
seq__18742_19021 = G__19037;
chunk__18743_19022 = G__19038;
count__18744_19024 = G__19039;
i__18745_19025 = G__19040;
continue;
} else {
var temp__5823__auto___19041 = cljs.core.seq(seq__18742_19021);
if(temp__5823__auto___19041){
var seq__18742_19042__$1 = temp__5823__auto___19041;
if(cljs.core.chunked_seq_QMARK_(seq__18742_19042__$1)){
var c__5673__auto___19043 = cljs.core.chunk_first(seq__18742_19042__$1);
var G__19044 = cljs.core.chunk_rest(seq__18742_19042__$1);
var G__19045 = c__5673__auto___19043;
var G__19046 = cljs.core.count(c__5673__auto___19043);
var G__19047 = (0);
seq__18742_19021 = G__19044;
chunk__18743_19022 = G__19045;
count__18744_19024 = G__19046;
i__18745_19025 = G__19047;
continue;
} else {
var vec__18773_19052 = cljs.core.first(seq__18742_19042__$1);
var name_19054 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18773_19052,(0),null);
var map__18776_19055 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18773_19052,(1),null);
var map__18776_19056__$1 = cljs.core.__destructure_map(map__18776_19055);
var doc_19057 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18776_19056__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists_19058 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18776_19056__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println();

cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",name_19054], 0));

cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",arglists_19058], 0));

if(cljs.core.truth_(doc_19057)){
cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",doc_19057], 0));
} else {
}


var G__19068 = cljs.core.next(seq__18742_19042__$1);
var G__19069 = null;
var G__19070 = (0);
var G__19071 = (0);
seq__18742_19021 = G__19068;
chunk__18743_19022 = G__19069;
count__18744_19024 = G__19070;
i__18745_19025 = G__19071;
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

var seq__18784 = cljs.core.seq(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"args","args",1315556576),new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Keyword(null,"fn","fn",-1175266204)], null));
var chunk__18786 = null;
var count__18787 = (0);
var i__18788 = (0);
while(true){
if((i__18788 < count__18787)){
var role = chunk__18786.cljs$core$IIndexed$_nth$arity$2(null,i__18788);
var temp__5823__auto___19086__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(fnspec,role);
if(cljs.core.truth_(temp__5823__auto___19086__$1)){
var spec_19088 = temp__5823__auto___19086__$1;
cljs.core.print.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(""+"\n "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name(role))+":"),cljs.spec.alpha.describe(spec_19088)], 0));
} else {
}


var G__19099 = seq__18784;
var G__19102 = chunk__18786;
var G__19105 = count__18787;
var G__19107 = (i__18788 + (1));
seq__18784 = G__19099;
chunk__18786 = G__19102;
count__18787 = G__19105;
i__18788 = G__19107;
continue;
} else {
var temp__5823__auto____$1 = cljs.core.seq(seq__18784);
if(temp__5823__auto____$1){
var seq__18784__$1 = temp__5823__auto____$1;
if(cljs.core.chunked_seq_QMARK_(seq__18784__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__18784__$1);
var G__19115 = cljs.core.chunk_rest(seq__18784__$1);
var G__19116 = c__5673__auto__;
var G__19117 = cljs.core.count(c__5673__auto__);
var G__19118 = (0);
seq__18784 = G__19115;
chunk__18786 = G__19116;
count__18787 = G__19117;
i__18788 = G__19118;
continue;
} else {
var role = cljs.core.first(seq__18784__$1);
var temp__5823__auto___19119__$2 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(fnspec,role);
if(cljs.core.truth_(temp__5823__auto___19119__$2)){
var spec_19120 = temp__5823__auto___19119__$2;
cljs.core.print.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(""+"\n "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name(role))+":"),cljs.spec.alpha.describe(spec_19120)], 0));
} else {
}


var G__19121 = cljs.core.next(seq__18784__$1);
var G__19122 = null;
var G__19123 = (0);
var G__19124 = (0);
seq__18784 = G__19121;
chunk__18786 = G__19122;
count__18787 = G__19123;
i__18788 = G__19124;
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
var map__18815 = datafied_throwable;
var map__18815__$1 = cljs.core.__destructure_map(map__18815);
var via = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18815__$1,new cljs.core.Keyword(null,"via","via",-1904457336));
var trace = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18815__$1,new cljs.core.Keyword(null,"trace","trace",-1082747415));
var phase = cljs.core.get.cljs$core$IFn$_invoke$arity$3(map__18815__$1,new cljs.core.Keyword(null,"phase","phase",575722892),new cljs.core.Keyword(null,"execution","execution",253283524));
var map__18817 = cljs.core.last(via);
var map__18817__$1 = cljs.core.__destructure_map(map__18817);
var type = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18817__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var message = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18817__$1,new cljs.core.Keyword(null,"message","message",-406056002));
var data = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18817__$1,new cljs.core.Keyword(null,"data","data",-232669377));
var map__18818 = data;
var map__18818__$1 = cljs.core.__destructure_map(map__18818);
var problems = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18818__$1,new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814));
var fn = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18818__$1,new cljs.core.Keyword("cljs.spec.alpha","fn","cljs.spec.alpha/fn",408600443));
var caller = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18818__$1,new cljs.core.Keyword("cljs.spec.test.alpha","caller","cljs.spec.test.alpha/caller",-398302390));
var map__18820 = new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(cljs.core.first(via));
var map__18820__$1 = cljs.core.__destructure_map(map__18820);
var top_data = map__18820__$1;
var source = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18820__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397));
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3((function (){var G__18824 = phase;
var G__18824__$1 = (((G__18824 instanceof cljs.core.Keyword))?G__18824.fqn:null);
switch (G__18824__$1) {
case "read-source":
var map__18829 = data;
var map__18829__$1 = cljs.core.__destructure_map(map__18829);
var line = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18829__$1,new cljs.core.Keyword("clojure.error","line","clojure.error/line",-1816287471));
var column = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18829__$1,new cljs.core.Keyword("clojure.error","column","clojure.error/column",304721553));
var G__18831 = cljs.core.merge.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(cljs.core.second(via)),top_data], 0));
var G__18831__$1 = (cljs.core.truth_(source)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18831,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397),source):G__18831);
var G__18831__$2 = (cljs.core.truth_((function (){var fexpr__18832 = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["NO_SOURCE_PATH",null,"NO_SOURCE_FILE",null], null), null);
return (fexpr__18832.cljs$core$IFn$_invoke$arity$1 ? fexpr__18832.cljs$core$IFn$_invoke$arity$1(source) : fexpr__18832.call(null,source));
})())?cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(G__18831__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397)):G__18831__$1);
if(cljs.core.truth_(message)){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18831__$2,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742),message);
} else {
return G__18831__$2;
}

break;
case "compile-syntax-check":
case "compilation":
case "macro-syntax-check":
case "macroexpansion":
var G__18833 = top_data;
var G__18833__$1 = (cljs.core.truth_(source)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18833,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397),source):G__18833);
var G__18833__$2 = (cljs.core.truth_((function (){var fexpr__18834 = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["NO_SOURCE_PATH",null,"NO_SOURCE_FILE",null], null), null);
return (fexpr__18834.cljs$core$IFn$_invoke$arity$1 ? fexpr__18834.cljs$core$IFn$_invoke$arity$1(source) : fexpr__18834.call(null,source));
})())?cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(G__18833__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397)):G__18833__$1);
var G__18833__$3 = (cljs.core.truth_(type)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18833__$2,new cljs.core.Keyword("clojure.error","class","clojure.error/class",278435890),type):G__18833__$2);
var G__18833__$4 = (cljs.core.truth_(message)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18833__$3,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742),message):G__18833__$3);
if(cljs.core.truth_(problems)){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18833__$4,new cljs.core.Keyword("clojure.error","spec","clojure.error/spec",2055032595),data);
} else {
return G__18833__$4;
}

break;
case "read-eval-result":
case "print-eval-result":
var vec__18836 = cljs.core.first(trace);
var source__$1 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18836,(0),null);
var method = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18836,(1),null);
var file = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18836,(2),null);
var line = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18836,(3),null);
var G__18839 = top_data;
var G__18839__$1 = (cljs.core.truth_(line)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18839,new cljs.core.Keyword("clojure.error","line","clojure.error/line",-1816287471),line):G__18839);
var G__18839__$2 = (cljs.core.truth_(file)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18839__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397),file):G__18839__$1);
var G__18839__$3 = (cljs.core.truth_((function (){var and__5140__auto__ = source__$1;
if(cljs.core.truth_(and__5140__auto__)){
return method;
} else {
return and__5140__auto__;
}
})())?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18839__$2,new cljs.core.Keyword("clojure.error","symbol","clojure.error/symbol",1544821994),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[source__$1,method],null))):G__18839__$2);
var G__18839__$4 = (cljs.core.truth_(type)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18839__$3,new cljs.core.Keyword("clojure.error","class","clojure.error/class",278435890),type):G__18839__$3);
if(cljs.core.truth_(message)){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18839__$4,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742),message);
} else {
return G__18839__$4;
}

break;
case "execution":
var vec__18848 = cljs.core.first(trace);
var source__$1 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18848,(0),null);
var method = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18848,(1),null);
var file = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18848,(2),null);
var line = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18848,(3),null);
var file__$1 = cljs.core.first(cljs.core.remove.cljs$core$IFn$_invoke$arity$2((function (p1__18813_SHARP_){
var or__5142__auto__ = (p1__18813_SHARP_ == null);
if(or__5142__auto__){
return or__5142__auto__;
} else {
var fexpr__18855 = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["NO_SOURCE_PATH",null,"NO_SOURCE_FILE",null], null), null);
return (fexpr__18855.cljs$core$IFn$_invoke$arity$1 ? fexpr__18855.cljs$core$IFn$_invoke$arity$1(p1__18813_SHARP_) : fexpr__18855.call(null,p1__18813_SHARP_));
}
}),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"file","file",-1269645878).cljs$core$IFn$_invoke$arity$1(caller),file], null)));
var err_line = (function (){var or__5142__auto__ = new cljs.core.Keyword(null,"line","line",212345235).cljs$core$IFn$_invoke$arity$1(caller);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return line;
}
})();
var G__18861 = new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword("clojure.error","class","clojure.error/class",278435890),type], null);
var G__18861__$1 = (cljs.core.truth_(err_line)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18861,new cljs.core.Keyword("clojure.error","line","clojure.error/line",-1816287471),err_line):G__18861);
var G__18861__$2 = (cljs.core.truth_(message)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18861__$1,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742),message):G__18861__$1);
var G__18861__$3 = (cljs.core.truth_((function (){var or__5142__auto__ = fn;
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
})())?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18861__$2,new cljs.core.Keyword("clojure.error","symbol","clojure.error/symbol",1544821994),(function (){var or__5142__auto__ = fn;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[source__$1,method],null));
}
})()):G__18861__$2);
var G__18861__$4 = (cljs.core.truth_(file__$1)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18861__$3,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397),file__$1):G__18861__$3);
if(cljs.core.truth_(problems)){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18861__$4,new cljs.core.Keyword("clojure.error","spec","clojure.error/spec",2055032595),data);
} else {
return G__18861__$4;
}

break;
default:
throw (new Error((""+"No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__18824__$1))));

}
})(),new cljs.core.Keyword("clojure.error","phase","clojure.error/phase",275140358),phase);
});
/**
 * Returns a string from exception data, as produced by ex-triage.
 *   The first line summarizes the exception phase and location.
 *   The subsequent lines describe the cause.
 */
cljs.repl.ex_str = (function cljs$repl$ex_str(p__18880){
var map__18882 = p__18880;
var map__18882__$1 = cljs.core.__destructure_map(map__18882);
var triage_data = map__18882__$1;
var phase = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18882__$1,new cljs.core.Keyword("clojure.error","phase","clojure.error/phase",275140358));
var source = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18882__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397));
var line = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18882__$1,new cljs.core.Keyword("clojure.error","line","clojure.error/line",-1816287471));
var column = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18882__$1,new cljs.core.Keyword("clojure.error","column","clojure.error/column",304721553));
var symbol = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18882__$1,new cljs.core.Keyword("clojure.error","symbol","clojure.error/symbol",1544821994));
var class$ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18882__$1,new cljs.core.Keyword("clojure.error","class","clojure.error/class",278435890));
var cause = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18882__$1,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742));
var spec = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18882__$1,new cljs.core.Keyword("clojure.error","spec","clojure.error/spec",2055032595));
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
var G__18888 = phase;
var G__18888__$1 = (((G__18888 instanceof cljs.core.Keyword))?G__18888.fqn:null);
switch (G__18888__$1) {
case "read-source":
return (format.cljs$core$IFn$_invoke$arity$3 ? format.cljs$core$IFn$_invoke$arity$3("Syntax error reading source at (%s).\n%s\n",loc,cause) : format.call(null,"Syntax error reading source at (%s).\n%s\n",loc,cause));

break;
case "macro-syntax-check":
var G__18889 = "Syntax error macroexpanding %sat (%s).\n%s";
var G__18890 = (cljs.core.truth_(symbol)?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)+" "):"");
var G__18891 = loc;
var G__18892 = (cljs.core.truth_(spec)?(function (){var sb__5795__auto__ = (new goog.string.StringBuffer());
var _STAR_print_newline_STAR__orig_val__18893_19201 = cljs.core._STAR_print_newline_STAR_;
var _STAR_print_fn_STAR__orig_val__18894_19202 = cljs.core._STAR_print_fn_STAR_;
var _STAR_print_newline_STAR__temp_val__18895_19203 = true;
var _STAR_print_fn_STAR__temp_val__18896_19204 = (function (x__5796__auto__){
return sb__5795__auto__.append(x__5796__auto__);
});
(cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__temp_val__18895_19203);

(cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__temp_val__18896_19204);

try{cljs.spec.alpha.explain_out(cljs.core.update.cljs$core$IFn$_invoke$arity$3(spec,new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814),(function (probs){
return cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p1__18873_SHARP_){
return cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(p1__18873_SHARP_,new cljs.core.Keyword(null,"in","in",-1531184865));
}),probs);
}))
);
}finally {(cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__orig_val__18894_19202);

(cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__orig_val__18893_19201);
}
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(sb__5795__auto__));
})():(format.cljs$core$IFn$_invoke$arity$2 ? format.cljs$core$IFn$_invoke$arity$2("%s\n",cause) : format.call(null,"%s\n",cause)));
return (format.cljs$core$IFn$_invoke$arity$4 ? format.cljs$core$IFn$_invoke$arity$4(G__18889,G__18890,G__18891,G__18892) : format.call(null,G__18889,G__18890,G__18891,G__18892));

break;
case "macroexpansion":
var G__18904 = "Unexpected error%s macroexpanding %sat (%s).\n%s\n";
var G__18905 = cause_type;
var G__18906 = (cljs.core.truth_(symbol)?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)+" "):"");
var G__18907 = loc;
var G__18908 = cause;
return (format.cljs$core$IFn$_invoke$arity$5 ? format.cljs$core$IFn$_invoke$arity$5(G__18904,G__18905,G__18906,G__18907,G__18908) : format.call(null,G__18904,G__18905,G__18906,G__18907,G__18908));

break;
case "compile-syntax-check":
var G__18911 = "Syntax error%s compiling %sat (%s).\n%s\n";
var G__18912 = cause_type;
var G__18913 = (cljs.core.truth_(symbol)?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)+" "):"");
var G__18914 = loc;
var G__18915 = cause;
return (format.cljs$core$IFn$_invoke$arity$5 ? format.cljs$core$IFn$_invoke$arity$5(G__18911,G__18912,G__18913,G__18914,G__18915) : format.call(null,G__18911,G__18912,G__18913,G__18914,G__18915));

break;
case "compilation":
var G__18917 = "Unexpected error%s compiling %sat (%s).\n%s\n";
var G__18918 = cause_type;
var G__18919 = (cljs.core.truth_(symbol)?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)+" "):"");
var G__18920 = loc;
var G__18921 = cause;
return (format.cljs$core$IFn$_invoke$arity$5 ? format.cljs$core$IFn$_invoke$arity$5(G__18917,G__18918,G__18919,G__18920,G__18921) : format.call(null,G__18917,G__18918,G__18919,G__18920,G__18921));

break;
case "read-eval-result":
return (format.cljs$core$IFn$_invoke$arity$5 ? format.cljs$core$IFn$_invoke$arity$5("Error reading eval result%s at %s (%s).\n%s\n",cause_type,symbol,loc,cause) : format.call(null,"Error reading eval result%s at %s (%s).\n%s\n",cause_type,symbol,loc,cause));

break;
case "print-eval-result":
return (format.cljs$core$IFn$_invoke$arity$5 ? format.cljs$core$IFn$_invoke$arity$5("Error printing return value%s at %s (%s).\n%s\n",cause_type,symbol,loc,cause) : format.call(null,"Error printing return value%s at %s (%s).\n%s\n",cause_type,symbol,loc,cause));

break;
case "execution":
if(cljs.core.truth_(spec)){
var G__18922 = "Execution error - invalid arguments to %s at (%s).\n%s";
var G__18923 = symbol;
var G__18924 = loc;
var G__18925 = (function (){var sb__5795__auto__ = (new goog.string.StringBuffer());
var _STAR_print_newline_STAR__orig_val__18928_19218 = cljs.core._STAR_print_newline_STAR_;
var _STAR_print_fn_STAR__orig_val__18929_19219 = cljs.core._STAR_print_fn_STAR_;
var _STAR_print_newline_STAR__temp_val__18930_19220 = true;
var _STAR_print_fn_STAR__temp_val__18931_19221 = (function (x__5796__auto__){
return sb__5795__auto__.append(x__5796__auto__);
});
(cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__temp_val__18930_19220);

(cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__temp_val__18931_19221);

try{cljs.spec.alpha.explain_out(cljs.core.update.cljs$core$IFn$_invoke$arity$3(spec,new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814),(function (probs){
return cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p1__18878_SHARP_){
return cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(p1__18878_SHARP_,new cljs.core.Keyword(null,"in","in",-1531184865));
}),probs);
}))
);
}finally {(cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__orig_val__18929_19219);

(cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__orig_val__18928_19218);
}
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(sb__5795__auto__));
})();
return (format.cljs$core$IFn$_invoke$arity$4 ? format.cljs$core$IFn$_invoke$arity$4(G__18922,G__18923,G__18924,G__18925) : format.call(null,G__18922,G__18923,G__18924,G__18925));
} else {
var G__18941 = "Execution error%s at %s(%s).\n%s\n";
var G__18942 = cause_type;
var G__18943 = (cljs.core.truth_(symbol)?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)+" "):"");
var G__18944 = loc;
var G__18945 = cause;
return (format.cljs$core$IFn$_invoke$arity$5 ? format.cljs$core$IFn$_invoke$arity$5(G__18941,G__18942,G__18943,G__18944,G__18945) : format.call(null,G__18941,G__18942,G__18943,G__18944,G__18945));
}

break;
default:
throw (new Error((""+"No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__18888__$1))));

}
});
cljs.repl.error__GT_str = (function cljs$repl$error__GT_str(error){
return cljs.repl.ex_str(cljs.repl.ex_triage(cljs.repl.Error__GT_map(error)));
});

//# sourceMappingURL=cljs.repl.js.map
