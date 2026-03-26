goog.provide('cljs.repl');
cljs.repl.print_doc = (function cljs$repl$print_doc(p__18156){
var map__18158 = p__18156;
var map__18158__$1 = cljs.core.__destructure_map(map__18158);
var m = map__18158__$1;
var n = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18158__$1,new cljs.core.Keyword(null,"ns","ns",441598760));
var nm = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18158__$1,new cljs.core.Keyword(null,"name","name",1843675177));
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
var seq__18165_18588 = cljs.core.seq(new cljs.core.Keyword(null,"forms","forms",2045992350).cljs$core$IFn$_invoke$arity$1(m));
var chunk__18166_18589 = null;
var count__18167_18590 = (0);
var i__18168_18591 = (0);
while(true){
if((i__18168_18591 < count__18167_18590)){
var f_18593 = chunk__18166_18589.cljs$core$IIndexed$_nth$arity$2(null,i__18168_18591);
cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2(["  ",f_18593], 0));


var G__18594 = seq__18165_18588;
var G__18595 = chunk__18166_18589;
var G__18596 = count__18167_18590;
var G__18597 = (i__18168_18591 + (1));
seq__18165_18588 = G__18594;
chunk__18166_18589 = G__18595;
count__18167_18590 = G__18596;
i__18168_18591 = G__18597;
continue;
} else {
var temp__5823__auto___18598 = cljs.core.seq(seq__18165_18588);
if(temp__5823__auto___18598){
var seq__18165_18609__$1 = temp__5823__auto___18598;
if(cljs.core.chunked_seq_QMARK_(seq__18165_18609__$1)){
var c__5673__auto___18610 = cljs.core.chunk_first(seq__18165_18609__$1);
var G__18611 = cljs.core.chunk_rest(seq__18165_18609__$1);
var G__18612 = c__5673__auto___18610;
var G__18613 = cljs.core.count(c__5673__auto___18610);
var G__18614 = (0);
seq__18165_18588 = G__18611;
chunk__18166_18589 = G__18612;
count__18167_18590 = G__18613;
i__18168_18591 = G__18614;
continue;
} else {
var f_18617 = cljs.core.first(seq__18165_18609__$1);
cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2(["  ",f_18617], 0));


var G__18618 = cljs.core.next(seq__18165_18609__$1);
var G__18619 = null;
var G__18620 = (0);
var G__18621 = (0);
seq__18165_18588 = G__18618;
chunk__18166_18589 = G__18619;
count__18167_18590 = G__18620;
i__18168_18591 = G__18621;
continue;
}
} else {
}
}
break;
}
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m))){
var arglists_18623 = new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_((function (){var or__5142__auto__ = new cljs.core.Keyword(null,"macro","macro",-867863404).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return new cljs.core.Keyword(null,"repl-special-function","repl-special-function",1262603725).cljs$core$IFn$_invoke$arity$1(m);
}
})())){
cljs.core.prn.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([arglists_18623], 0));
} else {
cljs.core.prn.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Symbol(null,"quote","quote",1377916282,null),cljs.core.first(arglists_18623)))?cljs.core.second(arglists_18623):arglists_18623)], 0));
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
var seq__18175_18639 = cljs.core.seq(new cljs.core.Keyword(null,"methods","methods",453930866).cljs$core$IFn$_invoke$arity$1(m));
var chunk__18176_18640 = null;
var count__18177_18642 = (0);
var i__18178_18643 = (0);
while(true){
if((i__18178_18643 < count__18177_18642)){
var vec__18192_18645 = chunk__18176_18640.cljs$core$IIndexed$_nth$arity$2(null,i__18178_18643);
var name_18646 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18192_18645,(0),null);
var map__18195_18647 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18192_18645,(1),null);
var map__18195_18648__$1 = cljs.core.__destructure_map(map__18195_18647);
var doc_18649 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18195_18648__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists_18650 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18195_18648__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println();

cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",name_18646], 0));

cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",arglists_18650], 0));

if(cljs.core.truth_(doc_18649)){
cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",doc_18649], 0));
} else {
}


var G__18656 = seq__18175_18639;
var G__18657 = chunk__18176_18640;
var G__18658 = count__18177_18642;
var G__18659 = (i__18178_18643 + (1));
seq__18175_18639 = G__18656;
chunk__18176_18640 = G__18657;
count__18177_18642 = G__18658;
i__18178_18643 = G__18659;
continue;
} else {
var temp__5823__auto___18660 = cljs.core.seq(seq__18175_18639);
if(temp__5823__auto___18660){
var seq__18175_18661__$1 = temp__5823__auto___18660;
if(cljs.core.chunked_seq_QMARK_(seq__18175_18661__$1)){
var c__5673__auto___18662 = cljs.core.chunk_first(seq__18175_18661__$1);
var G__18664 = cljs.core.chunk_rest(seq__18175_18661__$1);
var G__18665 = c__5673__auto___18662;
var G__18666 = cljs.core.count(c__5673__auto___18662);
var G__18667 = (0);
seq__18175_18639 = G__18664;
chunk__18176_18640 = G__18665;
count__18177_18642 = G__18666;
i__18178_18643 = G__18667;
continue;
} else {
var vec__18199_18668 = cljs.core.first(seq__18175_18661__$1);
var name_18669 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18199_18668,(0),null);
var map__18202_18671 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18199_18668,(1),null);
var map__18202_18674__$1 = cljs.core.__destructure_map(map__18202_18671);
var doc_18675 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18202_18674__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists_18676 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18202_18674__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println();

cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",name_18669], 0));

cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",arglists_18676], 0));

if(cljs.core.truth_(doc_18675)){
cljs.core.println.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([" ",doc_18675], 0));
} else {
}


var G__18680 = cljs.core.next(seq__18175_18661__$1);
var G__18681 = null;
var G__18682 = (0);
var G__18683 = (0);
seq__18175_18639 = G__18680;
chunk__18176_18640 = G__18681;
count__18177_18642 = G__18682;
i__18178_18643 = G__18683;
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

var seq__18207 = cljs.core.seq(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"args","args",1315556576),new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Keyword(null,"fn","fn",-1175266204)], null));
var chunk__18208 = null;
var count__18209 = (0);
var i__18210 = (0);
while(true){
if((i__18210 < count__18209)){
var role = chunk__18208.cljs$core$IIndexed$_nth$arity$2(null,i__18210);
var temp__5823__auto___18694__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(fnspec,role);
if(cljs.core.truth_(temp__5823__auto___18694__$1)){
var spec_18699 = temp__5823__auto___18694__$1;
cljs.core.print.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(""+"\n "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name(role))+":"),cljs.spec.alpha.describe(spec_18699)], 0));
} else {
}


var G__18704 = seq__18207;
var G__18705 = chunk__18208;
var G__18706 = count__18209;
var G__18707 = (i__18210 + (1));
seq__18207 = G__18704;
chunk__18208 = G__18705;
count__18209 = G__18706;
i__18210 = G__18707;
continue;
} else {
var temp__5823__auto____$1 = cljs.core.seq(seq__18207);
if(temp__5823__auto____$1){
var seq__18207__$1 = temp__5823__auto____$1;
if(cljs.core.chunked_seq_QMARK_(seq__18207__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__18207__$1);
var G__18709 = cljs.core.chunk_rest(seq__18207__$1);
var G__18710 = c__5673__auto__;
var G__18711 = cljs.core.count(c__5673__auto__);
var G__18712 = (0);
seq__18207 = G__18709;
chunk__18208 = G__18710;
count__18209 = G__18711;
i__18210 = G__18712;
continue;
} else {
var role = cljs.core.first(seq__18207__$1);
var temp__5823__auto___18717__$2 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(fnspec,role);
if(cljs.core.truth_(temp__5823__auto___18717__$2)){
var spec_18718 = temp__5823__auto___18717__$2;
cljs.core.print.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(""+"\n "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name(role))+":"),cljs.spec.alpha.describe(spec_18718)], 0));
} else {
}


var G__18720 = cljs.core.next(seq__18207__$1);
var G__18721 = null;
var G__18722 = (0);
var G__18723 = (0);
seq__18207 = G__18720;
chunk__18208 = G__18721;
count__18209 = G__18722;
i__18210 = G__18723;
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
var map__18266 = datafied_throwable;
var map__18266__$1 = cljs.core.__destructure_map(map__18266);
var via = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18266__$1,new cljs.core.Keyword(null,"via","via",-1904457336));
var trace = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18266__$1,new cljs.core.Keyword(null,"trace","trace",-1082747415));
var phase = cljs.core.get.cljs$core$IFn$_invoke$arity$3(map__18266__$1,new cljs.core.Keyword(null,"phase","phase",575722892),new cljs.core.Keyword(null,"execution","execution",253283524));
var map__18267 = cljs.core.last(via);
var map__18267__$1 = cljs.core.__destructure_map(map__18267);
var type = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18267__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var message = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18267__$1,new cljs.core.Keyword(null,"message","message",-406056002));
var data = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18267__$1,new cljs.core.Keyword(null,"data","data",-232669377));
var map__18268 = data;
var map__18268__$1 = cljs.core.__destructure_map(map__18268);
var problems = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18268__$1,new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814));
var fn = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18268__$1,new cljs.core.Keyword("cljs.spec.alpha","fn","cljs.spec.alpha/fn",408600443));
var caller = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18268__$1,new cljs.core.Keyword("cljs.spec.test.alpha","caller","cljs.spec.test.alpha/caller",-398302390));
var map__18269 = new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(cljs.core.first(via));
var map__18269__$1 = cljs.core.__destructure_map(map__18269);
var top_data = map__18269__$1;
var source = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18269__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397));
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3((function (){var G__18281 = phase;
var G__18281__$1 = (((G__18281 instanceof cljs.core.Keyword))?G__18281.fqn:null);
switch (G__18281__$1) {
case "read-source":
var map__18282 = data;
var map__18282__$1 = cljs.core.__destructure_map(map__18282);
var line = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18282__$1,new cljs.core.Keyword("clojure.error","line","clojure.error/line",-1816287471));
var column = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18282__$1,new cljs.core.Keyword("clojure.error","column","clojure.error/column",304721553));
var G__18287 = cljs.core.merge.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(cljs.core.second(via)),top_data], 0));
var G__18287__$1 = (cljs.core.truth_(source)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18287,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397),source):G__18287);
var G__18287__$2 = (cljs.core.truth_((function (){var fexpr__18289 = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["NO_SOURCE_PATH",null,"NO_SOURCE_FILE",null], null), null);
return (fexpr__18289.cljs$core$IFn$_invoke$arity$1 ? fexpr__18289.cljs$core$IFn$_invoke$arity$1(source) : fexpr__18289.call(null,source));
})())?cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(G__18287__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397)):G__18287__$1);
if(cljs.core.truth_(message)){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18287__$2,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742),message);
} else {
return G__18287__$2;
}

break;
case "compile-syntax-check":
case "compilation":
case "macro-syntax-check":
case "macroexpansion":
var G__18293 = top_data;
var G__18293__$1 = (cljs.core.truth_(source)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18293,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397),source):G__18293);
var G__18293__$2 = (cljs.core.truth_((function (){var fexpr__18296 = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["NO_SOURCE_PATH",null,"NO_SOURCE_FILE",null], null), null);
return (fexpr__18296.cljs$core$IFn$_invoke$arity$1 ? fexpr__18296.cljs$core$IFn$_invoke$arity$1(source) : fexpr__18296.call(null,source));
})())?cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(G__18293__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397)):G__18293__$1);
var G__18293__$3 = (cljs.core.truth_(type)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18293__$2,new cljs.core.Keyword("clojure.error","class","clojure.error/class",278435890),type):G__18293__$2);
var G__18293__$4 = (cljs.core.truth_(message)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18293__$3,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742),message):G__18293__$3);
if(cljs.core.truth_(problems)){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18293__$4,new cljs.core.Keyword("clojure.error","spec","clojure.error/spec",2055032595),data);
} else {
return G__18293__$4;
}

break;
case "read-eval-result":
case "print-eval-result":
var vec__18308 = cljs.core.first(trace);
var source__$1 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18308,(0),null);
var method = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18308,(1),null);
var file = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18308,(2),null);
var line = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18308,(3),null);
var G__18320 = top_data;
var G__18320__$1 = (cljs.core.truth_(line)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18320,new cljs.core.Keyword("clojure.error","line","clojure.error/line",-1816287471),line):G__18320);
var G__18320__$2 = (cljs.core.truth_(file)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18320__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397),file):G__18320__$1);
var G__18320__$3 = (cljs.core.truth_((function (){var and__5140__auto__ = source__$1;
if(cljs.core.truth_(and__5140__auto__)){
return method;
} else {
return and__5140__auto__;
}
})())?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18320__$2,new cljs.core.Keyword("clojure.error","symbol","clojure.error/symbol",1544821994),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[source__$1,method],null))):G__18320__$2);
var G__18320__$4 = (cljs.core.truth_(type)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18320__$3,new cljs.core.Keyword("clojure.error","class","clojure.error/class",278435890),type):G__18320__$3);
if(cljs.core.truth_(message)){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18320__$4,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742),message);
} else {
return G__18320__$4;
}

break;
case "execution":
var vec__18328 = cljs.core.first(trace);
var source__$1 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18328,(0),null);
var method = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18328,(1),null);
var file = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18328,(2),null);
var line = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__18328,(3),null);
var file__$1 = cljs.core.first(cljs.core.remove.cljs$core$IFn$_invoke$arity$2((function (p1__18257_SHARP_){
var or__5142__auto__ = (p1__18257_SHARP_ == null);
if(or__5142__auto__){
return or__5142__auto__;
} else {
var fexpr__18331 = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["NO_SOURCE_PATH",null,"NO_SOURCE_FILE",null], null), null);
return (fexpr__18331.cljs$core$IFn$_invoke$arity$1 ? fexpr__18331.cljs$core$IFn$_invoke$arity$1(p1__18257_SHARP_) : fexpr__18331.call(null,p1__18257_SHARP_));
}
}),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"file","file",-1269645878).cljs$core$IFn$_invoke$arity$1(caller),file], null)));
var err_line = (function (){var or__5142__auto__ = new cljs.core.Keyword(null,"line","line",212345235).cljs$core$IFn$_invoke$arity$1(caller);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return line;
}
})();
var G__18336 = new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword("clojure.error","class","clojure.error/class",278435890),type], null);
var G__18336__$1 = (cljs.core.truth_(err_line)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18336,new cljs.core.Keyword("clojure.error","line","clojure.error/line",-1816287471),err_line):G__18336);
var G__18336__$2 = (cljs.core.truth_(message)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18336__$1,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742),message):G__18336__$1);
var G__18336__$3 = (cljs.core.truth_((function (){var or__5142__auto__ = fn;
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
})())?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18336__$2,new cljs.core.Keyword("clojure.error","symbol","clojure.error/symbol",1544821994),(function (){var or__5142__auto__ = fn;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[source__$1,method],null));
}
})()):G__18336__$2);
var G__18336__$4 = (cljs.core.truth_(file__$1)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18336__$3,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397),file__$1):G__18336__$3);
if(cljs.core.truth_(problems)){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(G__18336__$4,new cljs.core.Keyword("clojure.error","spec","clojure.error/spec",2055032595),data);
} else {
return G__18336__$4;
}

break;
default:
throw (new Error((""+"No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__18281__$1))));

}
})(),new cljs.core.Keyword("clojure.error","phase","clojure.error/phase",275140358),phase);
});
/**
 * Returns a string from exception data, as produced by ex-triage.
 *   The first line summarizes the exception phase and location.
 *   The subsequent lines describe the cause.
 */
cljs.repl.ex_str = (function cljs$repl$ex_str(p__18399){
var map__18401 = p__18399;
var map__18401__$1 = cljs.core.__destructure_map(map__18401);
var triage_data = map__18401__$1;
var phase = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18401__$1,new cljs.core.Keyword("clojure.error","phase","clojure.error/phase",275140358));
var source = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18401__$1,new cljs.core.Keyword("clojure.error","source","clojure.error/source",-2011936397));
var line = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18401__$1,new cljs.core.Keyword("clojure.error","line","clojure.error/line",-1816287471));
var column = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18401__$1,new cljs.core.Keyword("clojure.error","column","clojure.error/column",304721553));
var symbol = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18401__$1,new cljs.core.Keyword("clojure.error","symbol","clojure.error/symbol",1544821994));
var class$ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18401__$1,new cljs.core.Keyword("clojure.error","class","clojure.error/class",278435890));
var cause = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18401__$1,new cljs.core.Keyword("clojure.error","cause","clojure.error/cause",-1879175742));
var spec = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__18401__$1,new cljs.core.Keyword("clojure.error","spec","clojure.error/spec",2055032595));
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
var G__18436 = phase;
var G__18436__$1 = (((G__18436 instanceof cljs.core.Keyword))?G__18436.fqn:null);
switch (G__18436__$1) {
case "read-source":
return (format.cljs$core$IFn$_invoke$arity$3 ? format.cljs$core$IFn$_invoke$arity$3("Syntax error reading source at (%s).\n%s\n",loc,cause) : format.call(null,"Syntax error reading source at (%s).\n%s\n",loc,cause));

break;
case "macro-syntax-check":
var G__18442 = "Syntax error macroexpanding %sat (%s).\n%s";
var G__18443 = (cljs.core.truth_(symbol)?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)+" "):"");
var G__18444 = loc;
var G__18445 = (cljs.core.truth_(spec)?(function (){var sb__5795__auto__ = (new goog.string.StringBuffer());
var _STAR_print_newline_STAR__orig_val__18451_18781 = cljs.core._STAR_print_newline_STAR_;
var _STAR_print_fn_STAR__orig_val__18452_18782 = cljs.core._STAR_print_fn_STAR_;
var _STAR_print_newline_STAR__temp_val__18453_18783 = true;
var _STAR_print_fn_STAR__temp_val__18455_18784 = (function (x__5796__auto__){
return sb__5795__auto__.append(x__5796__auto__);
});
(cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__temp_val__18453_18783);

(cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__temp_val__18455_18784);

try{cljs.spec.alpha.explain_out(cljs.core.update.cljs$core$IFn$_invoke$arity$3(spec,new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814),(function (probs){
return cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p1__18372_SHARP_){
return cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(p1__18372_SHARP_,new cljs.core.Keyword(null,"in","in",-1531184865));
}),probs);
}))
);
}finally {(cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__orig_val__18452_18782);

(cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__orig_val__18451_18781);
}
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(sb__5795__auto__));
})():(format.cljs$core$IFn$_invoke$arity$2 ? format.cljs$core$IFn$_invoke$arity$2("%s\n",cause) : format.call(null,"%s\n",cause)));
return (format.cljs$core$IFn$_invoke$arity$4 ? format.cljs$core$IFn$_invoke$arity$4(G__18442,G__18443,G__18444,G__18445) : format.call(null,G__18442,G__18443,G__18444,G__18445));

break;
case "macroexpansion":
var G__18474 = "Unexpected error%s macroexpanding %sat (%s).\n%s\n";
var G__18475 = cause_type;
var G__18476 = (cljs.core.truth_(symbol)?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)+" "):"");
var G__18477 = loc;
var G__18478 = cause;
return (format.cljs$core$IFn$_invoke$arity$5 ? format.cljs$core$IFn$_invoke$arity$5(G__18474,G__18475,G__18476,G__18477,G__18478) : format.call(null,G__18474,G__18475,G__18476,G__18477,G__18478));

break;
case "compile-syntax-check":
var G__18480 = "Syntax error%s compiling %sat (%s).\n%s\n";
var G__18481 = cause_type;
var G__18482 = (cljs.core.truth_(symbol)?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)+" "):"");
var G__18483 = loc;
var G__18484 = cause;
return (format.cljs$core$IFn$_invoke$arity$5 ? format.cljs$core$IFn$_invoke$arity$5(G__18480,G__18481,G__18482,G__18483,G__18484) : format.call(null,G__18480,G__18481,G__18482,G__18483,G__18484));

break;
case "compilation":
var G__18490 = "Unexpected error%s compiling %sat (%s).\n%s\n";
var G__18491 = cause_type;
var G__18492 = (cljs.core.truth_(symbol)?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)+" "):"");
var G__18493 = loc;
var G__18494 = cause;
return (format.cljs$core$IFn$_invoke$arity$5 ? format.cljs$core$IFn$_invoke$arity$5(G__18490,G__18491,G__18492,G__18493,G__18494) : format.call(null,G__18490,G__18491,G__18492,G__18493,G__18494));

break;
case "read-eval-result":
return (format.cljs$core$IFn$_invoke$arity$5 ? format.cljs$core$IFn$_invoke$arity$5("Error reading eval result%s at %s (%s).\n%s\n",cause_type,symbol,loc,cause) : format.call(null,"Error reading eval result%s at %s (%s).\n%s\n",cause_type,symbol,loc,cause));

break;
case "print-eval-result":
return (format.cljs$core$IFn$_invoke$arity$5 ? format.cljs$core$IFn$_invoke$arity$5("Error printing return value%s at %s (%s).\n%s\n",cause_type,symbol,loc,cause) : format.call(null,"Error printing return value%s at %s (%s).\n%s\n",cause_type,symbol,loc,cause));

break;
case "execution":
if(cljs.core.truth_(spec)){
var G__18508 = "Execution error - invalid arguments to %s at (%s).\n%s";
var G__18509 = symbol;
var G__18510 = loc;
var G__18511 = (function (){var sb__5795__auto__ = (new goog.string.StringBuffer());
var _STAR_print_newline_STAR__orig_val__18517_18809 = cljs.core._STAR_print_newline_STAR_;
var _STAR_print_fn_STAR__orig_val__18518_18810 = cljs.core._STAR_print_fn_STAR_;
var _STAR_print_newline_STAR__temp_val__18519_18811 = true;
var _STAR_print_fn_STAR__temp_val__18520_18812 = (function (x__5796__auto__){
return sb__5795__auto__.append(x__5796__auto__);
});
(cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__temp_val__18519_18811);

(cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__temp_val__18520_18812);

try{cljs.spec.alpha.explain_out(cljs.core.update.cljs$core$IFn$_invoke$arity$3(spec,new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814),(function (probs){
return cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p1__18389_SHARP_){
return cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(p1__18389_SHARP_,new cljs.core.Keyword(null,"in","in",-1531184865));
}),probs);
}))
);
}finally {(cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__orig_val__18518_18810);

(cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__orig_val__18517_18809);
}
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(sb__5795__auto__));
})();
return (format.cljs$core$IFn$_invoke$arity$4 ? format.cljs$core$IFn$_invoke$arity$4(G__18508,G__18509,G__18510,G__18511) : format.call(null,G__18508,G__18509,G__18510,G__18511));
} else {
var G__18550 = "Execution error%s at %s(%s).\n%s\n";
var G__18551 = cause_type;
var G__18552 = (cljs.core.truth_(symbol)?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(symbol)+" "):"");
var G__18553 = loc;
var G__18554 = cause;
return (format.cljs$core$IFn$_invoke$arity$5 ? format.cljs$core$IFn$_invoke$arity$5(G__18550,G__18551,G__18552,G__18553,G__18554) : format.call(null,G__18550,G__18551,G__18552,G__18553,G__18554));
}

break;
default:
throw (new Error((""+"No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__18436__$1))));

}
});
cljs.repl.error__GT_str = (function cljs$repl$error__GT_str(error){
return cljs.repl.ex_str(cljs.repl.ex_triage(cljs.repl.Error__GT_map(error)));
});

//# sourceMappingURL=cljs.repl.js.map
