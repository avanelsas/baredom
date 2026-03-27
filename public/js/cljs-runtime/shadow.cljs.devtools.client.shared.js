goog.provide('shadow.cljs.devtools.client.shared');
goog.scope(function(){
  shadow.cljs.devtools.client.shared.goog$module$goog$object = goog.module.get('goog.object');
});

/**
 * @interface
 */
shadow.cljs.devtools.client.shared.IRemote = function(){};

var shadow$cljs$devtools$client$shared$IRemote$remote_open$dyn_20562 = (function (this$,e){
var x__5498__auto__ = (((this$ == null))?null:this$);
var m__5499__auto__ = (shadow.cljs.devtools.client.shared.remote_open[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$2(this$,e) : m__5499__auto__.call(null,this$,e));
} else {
var m__5497__auto__ = (shadow.cljs.devtools.client.shared.remote_open["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$2(this$,e) : m__5497__auto__.call(null,this$,e));
} else {
throw cljs.core.missing_protocol("IRemote.remote-open",this$);
}
}
});
shadow.cljs.devtools.client.shared.remote_open = (function shadow$cljs$devtools$client$shared$remote_open(this$,e){
if((((!((this$ == null)))) && ((!((this$.shadow$cljs$devtools$client$shared$IRemote$remote_open$arity$2 == null)))))){
return this$.shadow$cljs$devtools$client$shared$IRemote$remote_open$arity$2(this$,e);
} else {
return shadow$cljs$devtools$client$shared$IRemote$remote_open$dyn_20562(this$,e);
}
});

var shadow$cljs$devtools$client$shared$IRemote$remote_msg$dyn_20564 = (function (this$,msg){
var x__5498__auto__ = (((this$ == null))?null:this$);
var m__5499__auto__ = (shadow.cljs.devtools.client.shared.remote_msg[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$2(this$,msg) : m__5499__auto__.call(null,this$,msg));
} else {
var m__5497__auto__ = (shadow.cljs.devtools.client.shared.remote_msg["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$2(this$,msg) : m__5497__auto__.call(null,this$,msg));
} else {
throw cljs.core.missing_protocol("IRemote.remote-msg",this$);
}
}
});
shadow.cljs.devtools.client.shared.remote_msg = (function shadow$cljs$devtools$client$shared$remote_msg(this$,msg){
if((((!((this$ == null)))) && ((!((this$.shadow$cljs$devtools$client$shared$IRemote$remote_msg$arity$2 == null)))))){
return this$.shadow$cljs$devtools$client$shared$IRemote$remote_msg$arity$2(this$,msg);
} else {
return shadow$cljs$devtools$client$shared$IRemote$remote_msg$dyn_20564(this$,msg);
}
});

var shadow$cljs$devtools$client$shared$IRemote$remote_close$dyn_20568 = (function (this$,e,info){
var x__5498__auto__ = (((this$ == null))?null:this$);
var m__5499__auto__ = (shadow.cljs.devtools.client.shared.remote_close[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$3 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$3(this$,e,info) : m__5499__auto__.call(null,this$,e,info));
} else {
var m__5497__auto__ = (shadow.cljs.devtools.client.shared.remote_close["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$3 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$3(this$,e,info) : m__5497__auto__.call(null,this$,e,info));
} else {
throw cljs.core.missing_protocol("IRemote.remote-close",this$);
}
}
});
shadow.cljs.devtools.client.shared.remote_close = (function shadow$cljs$devtools$client$shared$remote_close(this$,e,info){
if((((!((this$ == null)))) && ((!((this$.shadow$cljs$devtools$client$shared$IRemote$remote_close$arity$3 == null)))))){
return this$.shadow$cljs$devtools$client$shared$IRemote$remote_close$arity$3(this$,e,info);
} else {
return shadow$cljs$devtools$client$shared$IRemote$remote_close$dyn_20568(this$,e,info);
}
});

var shadow$cljs$devtools$client$shared$IRemote$remote_error$dyn_20570 = (function (this$,e){
var x__5498__auto__ = (((this$ == null))?null:this$);
var m__5499__auto__ = (shadow.cljs.devtools.client.shared.remote_error[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$2(this$,e) : m__5499__auto__.call(null,this$,e));
} else {
var m__5497__auto__ = (shadow.cljs.devtools.client.shared.remote_error["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$2(this$,e) : m__5497__auto__.call(null,this$,e));
} else {
throw cljs.core.missing_protocol("IRemote.remote-error",this$);
}
}
});
shadow.cljs.devtools.client.shared.remote_error = (function shadow$cljs$devtools$client$shared$remote_error(this$,e){
if((((!((this$ == null)))) && ((!((this$.shadow$cljs$devtools$client$shared$IRemote$remote_error$arity$2 == null)))))){
return this$.shadow$cljs$devtools$client$shared$IRemote$remote_error$arity$2(this$,e);
} else {
return shadow$cljs$devtools$client$shared$IRemote$remote_error$dyn_20570(this$,e);
}
});


/**
 * @interface
 */
shadow.cljs.devtools.client.shared.IHostSpecific = function(){};

var shadow$cljs$devtools$client$shared$IHostSpecific$do_repl_init$dyn_20573 = (function (this$,action,done,error){
var x__5498__auto__ = (((this$ == null))?null:this$);
var m__5499__auto__ = (shadow.cljs.devtools.client.shared.do_repl_init[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$4 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$4(this$,action,done,error) : m__5499__auto__.call(null,this$,action,done,error));
} else {
var m__5497__auto__ = (shadow.cljs.devtools.client.shared.do_repl_init["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$4 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$4(this$,action,done,error) : m__5497__auto__.call(null,this$,action,done,error));
} else {
throw cljs.core.missing_protocol("IHostSpecific.do-repl-init",this$);
}
}
});
shadow.cljs.devtools.client.shared.do_repl_init = (function shadow$cljs$devtools$client$shared$do_repl_init(this$,action,done,error){
if((((!((this$ == null)))) && ((!((this$.shadow$cljs$devtools$client$shared$IHostSpecific$do_repl_init$arity$4 == null)))))){
return this$.shadow$cljs$devtools$client$shared$IHostSpecific$do_repl_init$arity$4(this$,action,done,error);
} else {
return shadow$cljs$devtools$client$shared$IHostSpecific$do_repl_init$dyn_20573(this$,action,done,error);
}
});

var shadow$cljs$devtools$client$shared$IHostSpecific$do_repl_require$dyn_20586 = (function (this$,require_msg,done,error){
var x__5498__auto__ = (((this$ == null))?null:this$);
var m__5499__auto__ = (shadow.cljs.devtools.client.shared.do_repl_require[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$4 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$4(this$,require_msg,done,error) : m__5499__auto__.call(null,this$,require_msg,done,error));
} else {
var m__5497__auto__ = (shadow.cljs.devtools.client.shared.do_repl_require["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$4 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$4(this$,require_msg,done,error) : m__5497__auto__.call(null,this$,require_msg,done,error));
} else {
throw cljs.core.missing_protocol("IHostSpecific.do-repl-require",this$);
}
}
});
shadow.cljs.devtools.client.shared.do_repl_require = (function shadow$cljs$devtools$client$shared$do_repl_require(this$,require_msg,done,error){
if((((!((this$ == null)))) && ((!((this$.shadow$cljs$devtools$client$shared$IHostSpecific$do_repl_require$arity$4 == null)))))){
return this$.shadow$cljs$devtools$client$shared$IHostSpecific$do_repl_require$arity$4(this$,require_msg,done,error);
} else {
return shadow$cljs$devtools$client$shared$IHostSpecific$do_repl_require$dyn_20586(this$,require_msg,done,error);
}
});

var shadow$cljs$devtools$client$shared$IHostSpecific$do_invoke$dyn_20596 = (function (this$,ns,invoke_msg,success,fail){
var x__5498__auto__ = (((this$ == null))?null:this$);
var m__5499__auto__ = (shadow.cljs.devtools.client.shared.do_invoke[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$5 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$5(this$,ns,invoke_msg,success,fail) : m__5499__auto__.call(null,this$,ns,invoke_msg,success,fail));
} else {
var m__5497__auto__ = (shadow.cljs.devtools.client.shared.do_invoke["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$5 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$5(this$,ns,invoke_msg,success,fail) : m__5497__auto__.call(null,this$,ns,invoke_msg,success,fail));
} else {
throw cljs.core.missing_protocol("IHostSpecific.do-invoke",this$);
}
}
});
shadow.cljs.devtools.client.shared.do_invoke = (function shadow$cljs$devtools$client$shared$do_invoke(this$,ns,invoke_msg,success,fail){
if((((!((this$ == null)))) && ((!((this$.shadow$cljs$devtools$client$shared$IHostSpecific$do_invoke$arity$5 == null)))))){
return this$.shadow$cljs$devtools$client$shared$IHostSpecific$do_invoke$arity$5(this$,ns,invoke_msg,success,fail);
} else {
return shadow$cljs$devtools$client$shared$IHostSpecific$do_invoke$dyn_20596(this$,ns,invoke_msg,success,fail);
}
});

shadow.cljs.devtools.client.shared.load_sources = (function shadow$cljs$devtools$client$shared$load_sources(runtime,sources,callback){
return shadow.remote.runtime.shared.call.cljs$core$IFn$_invoke$arity$3(runtime,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"op","op",-1882987955),new cljs.core.Keyword(null,"cljs-load-sources","cljs-load-sources",-1458295962),new cljs.core.Keyword(null,"to","to",192099007),shadow.cljs.devtools.client.env.worker_client_id,new cljs.core.Keyword(null,"sources","sources",-321166424),cljs.core.into.cljs$core$IFn$_invoke$arity$3(cljs.core.PersistentVector.EMPTY,cljs.core.map.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"resource-id","resource-id",-1308422582)),sources)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"cljs-sources","cljs-sources",31121610),(function (p__20367){
var map__20368 = p__20367;
var map__20368__$1 = cljs.core.__destructure_map(map__20368);
var msg = map__20368__$1;
var sources__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20368__$1,new cljs.core.Keyword(null,"sources","sources",-321166424));
return (callback.cljs$core$IFn$_invoke$arity$1 ? callback.cljs$core$IFn$_invoke$arity$1(sources__$1) : callback.call(null,sources__$1));
})], null));
});
if((typeof shadow !== 'undefined') && (typeof shadow.cljs !== 'undefined') && (typeof shadow.cljs.devtools !== 'undefined') && (typeof shadow.cljs.devtools.client !== 'undefined') && (typeof shadow.cljs.devtools.client.shared !== 'undefined') && (typeof shadow.cljs.devtools.client.shared.runtime_ref !== 'undefined')){
} else {
shadow.cljs.devtools.client.shared.runtime_ref = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(null);
}
if((typeof shadow !== 'undefined') && (typeof shadow.cljs !== 'undefined') && (typeof shadow.cljs.devtools !== 'undefined') && (typeof shadow.cljs.devtools.client !== 'undefined') && (typeof shadow.cljs.devtools.client.shared !== 'undefined') && (typeof shadow.cljs.devtools.client.shared.plugins_ref !== 'undefined')){
} else {
shadow.cljs.devtools.client.shared.plugins_ref = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(cljs.core.PersistentArrayMap.EMPTY);
}
shadow.cljs.devtools.client.shared.start_all_plugins_BANG_ = (function shadow$cljs$devtools$client$shared$start_all_plugins_BANG_(p__20369){
var map__20370 = p__20369;
var map__20370__$1 = cljs.core.__destructure_map(map__20370);
var runtime = map__20370__$1;
var state_ref = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20370__$1,new cljs.core.Keyword(null,"state-ref","state-ref",2127874952));
var started_set = cljs.core.set(cljs.core.keys(new cljs.core.Keyword("shadow.cljs.devtools.client.shared","plugins","shadow.cljs.devtools.client.shared/plugins",1565262855).cljs$core$IFn$_invoke$arity$1(cljs.core.deref(state_ref))));
var plugins = cljs.core.deref(shadow.cljs.devtools.client.shared.plugins_ref);
var plugins_set = cljs.core.set(cljs.core.keys(plugins));
var pending_set = clojure.set.difference.cljs$core$IFn$_invoke$arity$2(plugins_set,started_set);
var pending_set__$1 = pending_set;
while(true){
if(cljs.core.empty_QMARK_(pending_set__$1)){
return new cljs.core.Keyword("shadow.cljs.devtools.client.shared","done!","shadow.cljs.devtools.client.shared/done!",-748632664);
} else {
var G__20600 = cljs.core.reduce.cljs$core$IFn$_invoke$arity$3(((function (pending_set__$1,started_set,plugins,plugins_set,pending_set,map__20370,map__20370__$1,runtime,state_ref){
return (function (pending_set__$2,plugin_id){
var map__20373 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(plugins,plugin_id);
var map__20373__$1 = cljs.core.__destructure_map(map__20373);
var plugin = map__20373__$1;
var depends_on = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20373__$1,new cljs.core.Keyword(null,"depends-on","depends-on",-1448442022));
var init_fn = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20373__$1,new cljs.core.Keyword(null,"init-fn","init-fn",777257971));
if(cljs.core.truth_(cljs.core.some(pending_set__$2,depends_on))){
return pending_set__$2;
} else {
var start_arg = cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(cljs.core.select_keys(new cljs.core.Keyword("shadow.cljs.devtools.client.shared","plugins","shadow.cljs.devtools.client.shared/plugins",1565262855).cljs$core$IFn$_invoke$arity$1(cljs.core.deref(state_ref)),depends_on),new cljs.core.Keyword(null,"runtime","runtime",-1331573996),runtime);
var started = (init_fn.cljs$core$IFn$_invoke$arity$1 ? init_fn.cljs$core$IFn$_invoke$arity$1(start_arg) : init_fn.call(null,start_arg));
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(state_ref,cljs.core.assoc_in,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("shadow.cljs.devtools.client.shared","plugins","shadow.cljs.devtools.client.shared/plugins",1565262855),plugin_id], null),started);

return cljs.core.disj.cljs$core$IFn$_invoke$arity$2(pending_set__$2,plugin_id);
}
});})(pending_set__$1,started_set,plugins,plugins_set,pending_set,map__20370,map__20370__$1,runtime,state_ref))
,pending_set__$1,pending_set__$1);
pending_set__$1 = G__20600;
continue;

}
break;
}
});
shadow.cljs.devtools.client.shared.add_plugin_BANG_ = (function shadow$cljs$devtools$client$shared$add_plugin_BANG_(plugin_id,depends_on,init_fn,stop_fn){
if((plugin_id instanceof cljs.core.Keyword)){
} else {
throw (new Error("Assert failed: (keyword? plugin-id)"));
}

if(cljs.core.set_QMARK_(depends_on)){
} else {
throw (new Error("Assert failed: (set? depends-on)"));
}

if(cljs.core.fn_QMARK_(init_fn)){
} else {
throw (new Error("Assert failed: (fn? init-fn)"));
}

if(cljs.core.fn_QMARK_(stop_fn)){
} else {
throw (new Error("Assert failed: (fn? stop-fn)"));
}

var temp__5827__auto___20601 = cljs.core.deref(shadow.cljs.devtools.client.shared.runtime_ref);
if((temp__5827__auto___20601 == null)){
} else {
var runtime_20602 = temp__5827__auto___20601;
var temp__5827__auto___20603__$1 = cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(runtime_20602,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("shadow.cljs.devtools.client.shared","plugins","shadow.cljs.devtools.client.shared/plugins",1565262855),plugin_id], null));
if((temp__5827__auto___20603__$1 == null)){
} else {
var started_20604 = temp__5827__auto___20603__$1;
var map__20376_20607 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(cljs.core.deref(shadow.cljs.devtools.client.shared.plugins_ref),plugin_id);
var map__20376_20608__$1 = cljs.core.__destructure_map(map__20376_20607);
var old_20609 = map__20376_20608__$1;
var stop_fn_20610__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20376_20608__$1,new cljs.core.Keyword(null,"stop-fn","stop-fn",-348472246));
(stop_fn_20610__$1.cljs$core$IFn$_invoke$arity$1 ? stop_fn_20610__$1.cljs$core$IFn$_invoke$arity$1(started_20604) : stop_fn_20610__$1.call(null,started_20604));

cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$variadic(shadow.cljs.devtools.client.shared.runtime_ref,cljs.core.update,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","plugins","shadow.cljs.devtools.client.shared/plugins",1565262855),cljs.core.dissoc,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([plugin_id], 0));
}
}

cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(shadow.cljs.devtools.client.shared.plugins_ref,cljs.core.assoc,plugin_id,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"ext-id","ext-id",-653860474),plugin_id,new cljs.core.Keyword(null,"depends-on","depends-on",-1448442022),depends_on,new cljs.core.Keyword(null,"init-fn","init-fn",777257971),init_fn,new cljs.core.Keyword(null,"stop-fn","stop-fn",-348472246),stop_fn], null));

var temp__5827__auto__ = cljs.core.deref(shadow.cljs.devtools.client.shared.runtime_ref);
if((temp__5827__auto__ == null)){
return null;
} else {
var runtime = temp__5827__auto__;
return shadow.cljs.devtools.client.shared.start_all_plugins_BANG_(runtime);
}
});
shadow.cljs.devtools.client.shared.transit_read = (function shadow$cljs$devtools$client$shared$transit_read(p__20377,data){
var map__20378 = p__20377;
var map__20378__$1 = cljs.core.__destructure_map(map__20378);
var state = map__20378__$1;
var transit_readers = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20378__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","transit-readers","shadow.cljs.devtools.client.shared/transit-readers",-1866893537));
var t = cognitect.transit.reader.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"json","json",1279968570),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"handlers","handlers",79528781),transit_readers], null));
return cognitect.transit.read(t,data);
});
shadow.cljs.devtools.client.shared.transit_str = (function shadow$cljs$devtools$client$shared$transit_str(p__20379,obj){
var map__20380 = p__20379;
var map__20380__$1 = cljs.core.__destructure_map(map__20380);
var state = map__20380__$1;
var transit_writers = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20380__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","transit-writers","shadow.cljs.devtools.client.shared/transit-writers",-776127897));
var w = cognitect.transit.writer.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"json","json",1279968570),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"handlers","handlers",79528781),transit_writers], null));
return cognitect.transit.write(w,obj);
});
shadow.cljs.devtools.client.shared.add_transit_writers_BANG_ = (function shadow$cljs$devtools$client$shared$add_transit_writers_BANG_(p__20381,writers){
var map__20382 = p__20381;
var map__20382__$1 = cljs.core.__destructure_map(map__20382);
var runtime = map__20382__$1;
var state_ref = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20382__$1,new cljs.core.Keyword(null,"state-ref","state-ref",2127874952));
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$variadic(state_ref,cljs.core.update,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","transit-writers","shadow.cljs.devtools.client.shared/transit-writers",-776127897),cljs.core.merge,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([writers], 0));

return runtime;
});
shadow.cljs.devtools.client.shared.continue_BANG_ = (function shadow$cljs$devtools$client$shared$continue_BANG_(state){
return (shadow.cljs.devtools.client.shared.interpret_actions.cljs$core$IFn$_invoke$arity$1 ? shadow.cljs.devtools.client.shared.interpret_actions.cljs$core$IFn$_invoke$arity$1(state) : shadow.cljs.devtools.client.shared.interpret_actions.call(null,state));
});
shadow.cljs.devtools.client.shared.abort_BANG_ = (function shadow$cljs$devtools$client$shared$abort_BANG_(p__20383,action,ex){
var map__20384 = p__20383;
var map__20384__$1 = cljs.core.__destructure_map(map__20384);
var state = map__20384__$1;
var callback = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20384__$1,new cljs.core.Keyword(null,"callback","callback",-705136228));
var G__20386 = cljs.core.dissoc.cljs$core$IFn$_invoke$arity$variadic(cljs.core.assoc.cljs$core$IFn$_invoke$arity$variadic(state,new cljs.core.Keyword(null,"result","result",1415092211),new cljs.core.Keyword(null,"runtime-error","runtime-error",-2116843646),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"ex","ex",-1413771341),ex,new cljs.core.Keyword(null,"ex-action","ex-action",672251553),action], 0)),new cljs.core.Keyword(null,"runtime","runtime",-1331573996),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"callback","callback",-705136228)], 0));
return (callback.cljs$core$IFn$_invoke$arity$1 ? callback.cljs$core$IFn$_invoke$arity$1(G__20386) : callback.call(null,G__20386));
});
shadow.cljs.devtools.client.shared.handle_invoke = (function shadow$cljs$devtools$client$shared$handle_invoke(state,runtime,action){
return shadow.cljs.devtools.client.shared.do_invoke(runtime,new cljs.core.Keyword(null,"ns","ns",441598760).cljs$core$IFn$_invoke$arity$1(state),action,(function shadow$cljs$devtools$client$shared$handle_invoke_$_invoke_success(res){
return shadow.cljs.devtools.client.shared.continue_BANG_((function (){var G__20391 = state;
if(cljs.core.not(new cljs.core.Keyword(null,"internal","internal",-854870097).cljs$core$IFn$_invoke$arity$1(action))){
return cljs.core.update.cljs$core$IFn$_invoke$arity$4(G__20391,new cljs.core.Keyword(null,"results","results",-1134170113),cljs.core.conj,res);
} else {
return G__20391;
}
})());
}),(function shadow$cljs$devtools$client$shared$handle_invoke_$_invoke_fail(ex){
console.error("REPL Invoke Exception",ex);

return shadow.cljs.devtools.client.shared.abort_BANG_(state,action,ex);
}));
});
shadow.cljs.devtools.client.shared.handle_repl_invoke = (function shadow$cljs$devtools$client$shared$handle_repl_invoke(state,runtime,action){
return shadow.cljs.devtools.client.shared.do_invoke(runtime,new cljs.core.Keyword(null,"ns","ns",441598760).cljs$core$IFn$_invoke$arity$1(state),action,(function shadow$cljs$devtools$client$shared$handle_repl_invoke_$_repl_invoke_success(ret){
(cljs.core._STAR_3 = cljs.core._STAR_2);

(cljs.core._STAR_2 = cljs.core._STAR_1);

(cljs.core._STAR_1 = ret);

return shadow.cljs.devtools.client.shared.continue_BANG_((function (){var G__20401 = state;
if(cljs.core.not(new cljs.core.Keyword(null,"internal","internal",-854870097).cljs$core$IFn$_invoke$arity$1(action))){
return cljs.core.update.cljs$core$IFn$_invoke$arity$4(G__20401,new cljs.core.Keyword(null,"results","results",-1134170113),cljs.core.conj,ret);
} else {
return G__20401;
}
})());
}),(function shadow$cljs$devtools$client$shared$handle_repl_invoke_$_repl_invoke_fail(ex){
(cljs.core._STAR_e = ex);

console.error("REPL Invoke Exception",ex);

return shadow.cljs.devtools.client.shared.abort_BANG_(state,action,ex);
}));
});
shadow.cljs.devtools.client.shared.interpret_action = (function shadow$cljs$devtools$client$shared$interpret_action(p__20407,p__20408){
var map__20410 = p__20407;
var map__20410__$1 = cljs.core.__destructure_map(map__20410);
var state = map__20410__$1;
var runtime = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20410__$1,new cljs.core.Keyword(null,"runtime","runtime",-1331573996));
var map__20411 = p__20408;
var map__20411__$1 = cljs.core.__destructure_map(map__20411);
var action = map__20411__$1;
var type = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20411__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var G__20413 = type;
var G__20413__$1 = (((G__20413 instanceof cljs.core.Keyword))?G__20413.fqn:null);
switch (G__20413__$1) {
case "repl/init":
return shadow.cljs.devtools.client.shared.do_repl_init(runtime,action,(function (){
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(new cljs.core.Keyword(null,"state-ref","state-ref",2127874952).cljs$core$IFn$_invoke$arity$1(runtime),cljs.core.assoc,new cljs.core.Keyword(null,"init-complete","init-complete",-157163484),true);

return shadow.cljs.devtools.client.shared.continue_BANG_(state);
}),(function (ex){
return shadow.cljs.devtools.client.shared.abort_BANG_(state,action,ex);
}));

break;
case "repl/set-ns":
var map__20425 = action;
var map__20425__$1 = cljs.core.__destructure_map(map__20425);
var ns = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20425__$1,new cljs.core.Keyword(null,"ns","ns",441598760));
var internal = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20425__$1,new cljs.core.Keyword(null,"internal","internal",-854870097));
return shadow.cljs.devtools.client.shared.continue_BANG_((function (){var G__20427 = cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(state,new cljs.core.Keyword(null,"ns","ns",441598760),ns);
if(cljs.core.not(internal)){
return cljs.core.update.cljs$core$IFn$_invoke$arity$4(G__20427,new cljs.core.Keyword(null,"results","results",-1134170113),cljs.core.conj,null);
} else {
return G__20427;
}
})());

break;
case "repl/require":
var map__20432 = action;
var map__20432__$1 = cljs.core.__destructure_map(map__20432);
var internal = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20432__$1,new cljs.core.Keyword(null,"internal","internal",-854870097));
return shadow.cljs.devtools.client.shared.do_repl_require(runtime,action,(function (sources){
return shadow.cljs.devtools.client.shared.continue_BANG_((function (){var G__20433 = cljs.core.update.cljs$core$IFn$_invoke$arity$4(state,new cljs.core.Keyword(null,"loaded-sources","loaded-sources",-300252436),cljs.core.into,sources);
if(cljs.core.not(internal)){
return cljs.core.update.cljs$core$IFn$_invoke$arity$4(G__20433,new cljs.core.Keyword(null,"results","results",-1134170113),cljs.core.conj,null);
} else {
return G__20433;
}
})());
}),(function (ex){
return shadow.cljs.devtools.client.shared.abort_BANG_(state,action,ex);
}));

break;
case "repl/invoke":
var repl = cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(state,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input","input",556931961),new cljs.core.Keyword(null,"repl","repl",-35398667)], null));
var temp__5827__auto___20644 = cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(state,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input","input",556931961),new cljs.core.Keyword(null,"obj-refs","obj-refs",-2086642078)], null));
if((temp__5827__auto___20644 == null)){
} else {
var obj_refs_20645 = temp__5827__auto___20644;
var obj_support_20646 = new cljs.core.Keyword(null,"obj-support","obj-support",1522559229).cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword("shadow.cljs.devtools.client.shared","plugins","shadow.cljs.devtools.client.shared/plugins",1565262855).cljs$core$IFn$_invoke$arity$1(cljs.core.deref(new cljs.core.Keyword(null,"state-ref","state-ref",2127874952).cljs$core$IFn$_invoke$arity$1(runtime))));
var vec__20434_20647 = obj_refs_20645;
var a_20648 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__20434_20647,(0),null);
var b_20649 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__20434_20647,(1),null);
var c_20650 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__20434_20647,(2),null);
if(cljs.core.truth_(a_20648)){
(cljs.core._STAR_1 = new cljs.core.Keyword(null,"obj","obj",981763962).cljs$core$IFn$_invoke$arity$1(shadow.remote.runtime.obj_support.get_ref(obj_support_20646,a_20648)));
} else {
}

if(cljs.core.truth_(b_20649)){
(cljs.core._STAR_2 = new cljs.core.Keyword(null,"obj","obj",981763962).cljs$core$IFn$_invoke$arity$1(shadow.remote.runtime.obj_support.get_ref(obj_support_20646,b_20649)));
} else {
}

if(cljs.core.truth_(c_20650)){
(cljs.core._STAR_3 = new cljs.core.Keyword(null,"obj","obj",981763962).cljs$core$IFn$_invoke$arity$1(shadow.remote.runtime.obj_support.get_ref(obj_support_20646,c_20650)));
} else {
}
}

var invoke_fn = (cljs.core.truth_((function (){var and__5140__auto__ = repl;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(new cljs.core.Keyword(null,"internal","internal",-854870097).cljs$core$IFn$_invoke$arity$1(action));
} else {
return and__5140__auto__;
}
})())?shadow.cljs.devtools.client.shared.handle_repl_invoke:shadow.cljs.devtools.client.shared.handle_invoke);
return (invoke_fn.cljs$core$IFn$_invoke$arity$3 ? invoke_fn.cljs$core$IFn$_invoke$arity$3(state,runtime,action) : invoke_fn.call(null,state,runtime,action));

break;
default:
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("unhandled repl action",new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"state","state",-1988618099),state,new cljs.core.Keyword(null,"action","action",-811238024),action], null));

}
});
shadow.cljs.devtools.client.shared.interpret_actions = (function shadow$cljs$devtools$client$shared$interpret_actions(p__20438){
var map__20440 = p__20438;
var map__20440__$1 = cljs.core.__destructure_map(map__20440);
var state = map__20440__$1;
var queue = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20440__$1,new cljs.core.Keyword(null,"queue","queue",1455835879));
var warnings = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20440__$1,new cljs.core.Keyword(null,"warnings","warnings",-735437651));
if(cljs.core.empty_QMARK_(queue)){
var map__20441 = state;
var map__20441__$1 = cljs.core.__destructure_map(map__20441);
var callback = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20441__$1,new cljs.core.Keyword(null,"callback","callback",-705136228));
var G__20442 = cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(cljs.core.dissoc.cljs$core$IFn$_invoke$arity$variadic(state,new cljs.core.Keyword(null,"runtime","runtime",-1331573996),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"callback","callback",-705136228),new cljs.core.Keyword(null,"queue","queue",1455835879)], 0)),new cljs.core.Keyword(null,"time-finish","time-finish",-1456032653),Date.now());
return (callback.cljs$core$IFn$_invoke$arity$1 ? callback.cljs$core$IFn$_invoke$arity$1(G__20442) : callback.call(null,G__20442));
} else {
if(((cljs.core.seq(warnings)) && (shadow.cljs.devtools.client.env.ignore_warnings === false))){
var map__20443 = state;
var map__20443__$1 = cljs.core.__destructure_map(map__20443);
var callback = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20443__$1,new cljs.core.Keyword(null,"callback","callback",-705136228));
var G__20444 = cljs.core.assoc.cljs$core$IFn$_invoke$arity$variadic(cljs.core.dissoc.cljs$core$IFn$_invoke$arity$variadic(state,new cljs.core.Keyword(null,"runtime","runtime",-1331573996),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"callback","callback",-705136228),new cljs.core.Keyword(null,"queue","queue",1455835879)], 0)),new cljs.core.Keyword(null,"result","result",1415092211),new cljs.core.Keyword(null,"warnings","warnings",-735437651),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"warnings","warnings",-735437651),warnings,new cljs.core.Keyword(null,"time-finish","time-finish",-1456032653),Date.now()], 0));
return (callback.cljs$core$IFn$_invoke$arity$1 ? callback.cljs$core$IFn$_invoke$arity$1(G__20444) : callback.call(null,G__20444));
} else {
var action = cljs.core.first(queue);
var state__$1 = cljs.core.update.cljs$core$IFn$_invoke$arity$3(state,new cljs.core.Keyword(null,"queue","queue",1455835879),cljs.core.rest);
return shadow.cljs.devtools.client.shared.interpret_action(state__$1,action);

}
}
});
shadow.cljs.devtools.client.shared.setup_actions = (function shadow$cljs$devtools$client$shared$setup_actions(runtime,input,p__20446,callback){
var map__20447 = p__20446;
var map__20447__$1 = cljs.core.__destructure_map(map__20447);
var msg = map__20447__$1;
var actions = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20447__$1,new cljs.core.Keyword(null,"actions","actions",-812656882));
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"queue","queue",1455835879),new cljs.core.Keyword(null,"ns","ns",441598760),new cljs.core.Keyword(null,"msg","msg",-1386103444),new cljs.core.Keyword(null,"loaded-sources","loaded-sources",-300252436),new cljs.core.Keyword(null,"warnings","warnings",-735437651),new cljs.core.Keyword(null,"result","result",1415092211),new cljs.core.Keyword(null,"runtime","runtime",-1331573996),new cljs.core.Keyword(null,"input","input",556931961),new cljs.core.Keyword(null,"callback","callback",-705136228),new cljs.core.Keyword(null,"time-start","time-start",-590811745),new cljs.core.Keyword(null,"results","results",-1134170113)],[actions,new cljs.core.Keyword(null,"ns","ns",441598760).cljs$core$IFn$_invoke$arity$1(input),msg,cljs.core.PersistentVector.EMPTY,cljs.core.vec(cljs.core.mapcat.cljs$core$IFn$_invoke$arity$variadic(new cljs.core.Keyword(null,"warnings","warnings",-735437651),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([actions], 0))),new cljs.core.Keyword(null,"ok","ok",967785236),runtime,input,callback,Date.now(),cljs.core.PersistentVector.EMPTY]);
});

/**
* @constructor
 * @implements {cljs.core.IRecord}
 * @implements {shadow.cljs.devtools.client.shared.IRemote}
 * @implements {shadow.remote.runtime.api.IRuntime}
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
 * @implements {shadow.remote.runtime.api.IEvalCLJS}
*/
shadow.cljs.devtools.client.shared.Runtime = (function (state_ref,__meta,__extmap,__hash){
this.state_ref = state_ref;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2230716170;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
(shadow.cljs.devtools.client.shared.Runtime.prototype.attempt_connect_BANG_ = (function (){
var self__ = this;
var this$ = this;
var map__20484 = cljs.core.deref(self__.state_ref);
var map__20484__$1 = cljs.core.__destructure_map(map__20484);
var state = map__20484__$1;
var ws_connecting = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20484__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-connecting","shadow.cljs.devtools.client.shared/ws-connecting",-847036450));
var ws_connect_timeout = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20484__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-connect-timeout","shadow.cljs.devtools.client.shared/ws-connect-timeout",1477702043));
var shutdown = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20484__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","shutdown","shadow.cljs.devtools.client.shared/shutdown",1947481008));
var stale = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20484__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","stale","shadow.cljs.devtools.client.shared/stale",-1017562082));
var ws_state = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20484__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-state","shadow.cljs.devtools.client.shared/ws-state",1653373656));
var ws_stop_fn = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20484__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-stop-fn","shadow.cljs.devtools.client.shared/ws-stop-fn",88454255));
var ws_start_fn = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20484__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-start-fn","shadow.cljs.devtools.client.shared/ws-start-fn",-49893682));
if(((cljs.core.not(shutdown)) && (((cljs.core.not(stale)) && (cljs.core.not(ws_connecting)))))){
if(cljs.core.truth_(ws_connect_timeout)){
clearTimeout(ws_connect_timeout);
} else {
}

if((!((ws_state == null)))){
(ws_stop_fn.cljs$core$IFn$_invoke$arity$1 ? ws_stop_fn.cljs$core$IFn$_invoke$arity$1(ws_state) : ws_stop_fn.call(null,ws_state));
} else {
}

var ws_state__$1 = (ws_start_fn.cljs$core$IFn$_invoke$arity$1 ? ws_start_fn.cljs$core$IFn$_invoke$arity$1(this$) : ws_start_fn.call(null,this$));
return cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$variadic(self__.state_ref,cljs.core.assoc,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-connecting","shadow.cljs.devtools.client.shared/ws-connecting",-847036450),true,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-connected","shadow.cljs.devtools.client.shared/ws-connected",-1476385853),false,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-state","shadow.cljs.devtools.client.shared/ws-state",1653373656),ws_state__$1], 0));
} else {
return null;
}
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.schedule_connect_BANG_ = (function (after){
var self__ = this;
var this$ = this;
var map__20485 = cljs.core.deref(self__.state_ref);
var map__20485__$1 = cljs.core.__destructure_map(map__20485);
var ws_connect_timeout = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20485__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-connect-timeout","shadow.cljs.devtools.client.shared/ws-connect-timeout",1477702043));
var stale = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20485__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","stale","shadow.cljs.devtools.client.shared/stale",-1017562082));
var shutdown = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20485__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","shutdown","shadow.cljs.devtools.client.shared/shutdown",1947481008));
if(cljs.core.truth_(ws_connect_timeout)){
clearTimeout(ws_connect_timeout);
} else {
}

if(((cljs.core.not(stale)) && (cljs.core.not(shutdown)))){
shadow.remote.runtime.shared.trigger_BANG_(this$,new cljs.core.Keyword(null,"on-reconnect","on-reconnect",1239988702));

return cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(self__.state_ref,cljs.core.assoc,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-connect-timeout","shadow.cljs.devtools.client.shared/ws-connect-timeout",1477702043),setTimeout((function (){
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(self__.state_ref,cljs.core.dissoc,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-connect-timeout","shadow.cljs.devtools.client.shared/ws-connect-timeout",1477702043));

return this$.attempt_connect_BANG_();
}),after));
} else {
return null;
}
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__5448__auto__,k__5449__auto__){
var self__ = this;
var this__5448__auto____$1 = this;
return this__5448__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__5449__auto__,null);
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__5450__auto__,k20453,else__5451__auto__){
var self__ = this;
var this__5450__auto____$1 = this;
var G__20486 = k20453;
var G__20486__$1 = (((G__20486 instanceof cljs.core.Keyword))?G__20486.fqn:null);
switch (G__20486__$1) {
case "state-ref":
return self__.state_ref;

break;
default:
return cljs.core.get.cljs$core$IFn$_invoke$arity$3(self__.__extmap,k20453,else__5451__auto__);

}
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.cljs$core$IKVReduce$_kv_reduce$arity$3 = (function (this__5468__auto__,f__5469__auto__,init__5470__auto__){
var self__ = this;
var this__5468__auto____$1 = this;
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3((function (ret__5471__auto__,p__20487){
var vec__20488 = p__20487;
var k__5472__auto__ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__20488,(0),null);
var v__5473__auto__ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__20488,(1),null);
return (f__5469__auto__.cljs$core$IFn$_invoke$arity$3 ? f__5469__auto__.cljs$core$IFn$_invoke$arity$3(ret__5471__auto__,k__5472__auto__,v__5473__auto__) : f__5469__auto__.call(null,ret__5471__auto__,k__5472__auto__,v__5473__auto__));
}),init__5470__auto__,this__5468__auto____$1);
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__5463__auto__,writer__5464__auto__,opts__5465__auto__){
var self__ = this;
var this__5463__auto____$1 = this;
var pr_pair__5466__auto__ = (function (keyval__5467__auto__){
return cljs.core.pr_sequential_writer(writer__5464__auto__,cljs.core.pr_writer,""," ","",opts__5465__auto__,keyval__5467__auto__);
});
return cljs.core.pr_sequential_writer(writer__5464__auto__,pr_pair__5466__auto__,"#shadow.cljs.devtools.client.shared.Runtime{",", ","}",opts__5465__auto__,cljs.core.concat.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"state-ref","state-ref",2127874952),self__.state_ref],null))], null),self__.__extmap));
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__20452){
var self__ = this;
var G__20452__$1 = this;
return (new cljs.core.RecordIter((0),G__20452__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"state-ref","state-ref",2127874952)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator(self__.__extmap):cljs.core.nil_iter())));
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__5446__auto__){
var self__ = this;
var this__5446__auto____$1 = this;
return self__.__meta;
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__5443__auto__){
var self__ = this;
var this__5443__auto____$1 = this;
return (new shadow.cljs.devtools.client.shared.Runtime(self__.state_ref,self__.__meta,self__.__extmap,self__.__hash));
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__5452__auto__){
var self__ = this;
var this__5452__auto____$1 = this;
return (1 + cljs.core.count(self__.__extmap));
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__5444__auto__){
var self__ = this;
var this__5444__auto____$1 = this;
var h__5251__auto__ = self__.__hash;
if((!((h__5251__auto__ == null)))){
return h__5251__auto__;
} else {
var h__5251__auto____$1 = (function (coll__5445__auto__){
return (229736719 ^ cljs.core.hash_unordered_coll(coll__5445__auto__));
})(this__5444__auto____$1);
(self__.__hash = h__5251__auto____$1);

return h__5251__auto____$1;
}
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this20454,other20455){
var self__ = this;
var this20454__$1 = this;
return (((!((other20455 == null)))) && ((((this20454__$1.constructor === other20455.constructor)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this20454__$1.state_ref,other20455.state_ref)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(this20454__$1.__extmap,other20455.__extmap)))))));
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$remote$runtime$api$IEvalCLJS$ = cljs.core.PROTOCOL_SENTINEL);

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$remote$runtime$api$IEvalCLJS$_cljs_eval$arity$3 = (function (this$,input,callback){
var self__ = this;
var this$__$1 = this;
return shadow.remote.runtime.shared.call.cljs$core$IFn$_invoke$arity$3(this$__$1,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"op","op",-1882987955),new cljs.core.Keyword(null,"cljs-compile","cljs-compile",-1735075279),new cljs.core.Keyword(null,"to","to",192099007),shadow.cljs.devtools.client.env.worker_client_id,new cljs.core.Keyword(null,"input","input",556931961),input,new cljs.core.Keyword(null,"include-init","include-init",1061115199),cljs.core.not(new cljs.core.Keyword(null,"init-complete","init-complete",-157163484).cljs$core$IFn$_invoke$arity$1(cljs.core.deref(self__.state_ref)))], null),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"cljs-compile-result","cljs-compile-result",-1137644713),(function (msg){
return shadow.cljs.devtools.client.shared.interpret_actions(shadow.cljs.devtools.client.shared.setup_actions(this$__$1,input,msg,callback));
}),new cljs.core.Keyword(null,"cljs-compile-error","cljs-compile-error",-371665768),(function (p__20493){
var map__20494 = p__20493;
var map__20494__$1 = cljs.core.__destructure_map(map__20494);
var ex_oid = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20494__$1,new cljs.core.Keyword(null,"ex-oid","ex-oid",-650256737));
var ex_client_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20494__$1,new cljs.core.Keyword(null,"ex-client-id","ex-client-id",-446123984));
var report = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20494__$1,new cljs.core.Keyword(null,"report","report",1394055010));
var G__20495 = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"result","result",1415092211),new cljs.core.Keyword(null,"compile-error","compile-error",-1313154526),new cljs.core.Keyword(null,"ex-oid","ex-oid",-650256737),ex_oid,new cljs.core.Keyword(null,"ex-client-id","ex-client-id",-446123984),ex_client_id,new cljs.core.Keyword(null,"report","report",1394055010),report], null);
return (callback.cljs$core$IFn$_invoke$arity$1 ? callback.cljs$core$IFn$_invoke$arity$1(G__20495) : callback.call(null,G__20495));
}),new cljs.core.Keyword(null,"client-not-found","client-not-found",-1754042614),(function (msg){
var G__20496 = new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"result","result",1415092211),new cljs.core.Keyword(null,"worker-not-found","worker-not-found",-1848657608)], null);
return (callback.cljs$core$IFn$_invoke$arity$1 ? callback.cljs$core$IFn$_invoke$arity$1(G__20496) : callback.call(null,G__20496));
})], null));
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IRemote$ = cljs.core.PROTOCOL_SENTINEL);

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IRemote$remote_open$arity$2 = (function (this$,e){
var self__ = this;
var this$__$1 = this;
return cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$variadic(self__.state_ref,cljs.core.assoc,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-errors","shadow.cljs.devtools.client.shared/ws-errors",105464879),(0),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-connecting","shadow.cljs.devtools.client.shared/ws-connecting",-847036450),false,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-connected","shadow.cljs.devtools.client.shared/ws-connected",-1476385853),true,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-last-msg","shadow.cljs.devtools.client.shared/ws-last-msg",-1438906782),shadow.remote.runtime.shared.now()], 0));
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IRemote$remote_msg$arity$2 = (function (this$,text){
var self__ = this;
var this$__$1 = this;
var msg = shadow.cljs.devtools.client.shared.transit_read(cljs.core.deref(self__.state_ref),text);
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(self__.state_ref,cljs.core.assoc,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-last-msg","shadow.cljs.devtools.client.shared/ws-last-msg",-1438906782),shadow.remote.runtime.shared.now());

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"access-denied","access-denied",959449406),new cljs.core.Keyword(null,"op","op",-1882987955).cljs$core$IFn$_invoke$arity$1(msg))){
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(self__.state_ref,cljs.core.assoc,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","stale","shadow.cljs.devtools.client.shared/stale",-1017562082),true);
} else {
}

return shadow.remote.runtime.shared.process(this$__$1,msg);
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IRemote$remote_close$arity$3 = (function (this$,e,info){
var self__ = this;
var this$__$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(self__.state_ref,cljs.core.dissoc,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-connected","shadow.cljs.devtools.client.shared/ws-connected",-1476385853),new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-connecting","shadow.cljs.devtools.client.shared/ws-connecting",-847036450));

if(((3) >= new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-errors","shadow.cljs.devtools.client.shared/ws-errors",105464879).cljs$core$IFn$_invoke$arity$1(cljs.core.deref(self__.state_ref)))){
return this$__$1.schedule_connect_BANG_((5000));
} else {
return console.warn("shadow-cljs: giving up trying to connect to ",info);
}
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$cljs$devtools$client$shared$IRemote$remote_error$arity$2 = (function (this$,e){
var self__ = this;
var this$__$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(self__.state_ref,cljs.core.update,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-errors","shadow.cljs.devtools.client.shared/ws-errors",105464879),cljs.core.inc);

shadow.remote.runtime.shared.trigger_BANG_(this$__$1,new cljs.core.Keyword(null,"on-disconnect","on-disconnect",-809021814));

return console.error("shadow-cljs - remote-error",e);
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$remote$runtime$api$IRuntime$ = cljs.core.PROTOCOL_SENTINEL);

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$remote$runtime$api$IRuntime$relay_msg$arity$2 = (function (this$,msg){
var self__ = this;
var this$__$1 = this;
var map__20504 = cljs.core.deref(self__.state_ref);
var map__20504__$1 = cljs.core.__destructure_map(map__20504);
var state = map__20504__$1;
var ws_state = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20504__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-state","shadow.cljs.devtools.client.shared/ws-state",1653373656));
var ws_connected = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20504__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-connected","shadow.cljs.devtools.client.shared/ws-connected",-1476385853));
var ws_send_fn = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20504__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-send-fn","shadow.cljs.devtools.client.shared/ws-send-fn",-1986889866));
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(self__.state_ref,cljs.core.assoc,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-last-msg","shadow.cljs.devtools.client.shared/ws-last-msg",-1438906782),shadow.remote.runtime.shared.now());

if(cljs.core.not(ws_connected)){
return console.warn("shadow-cljs - dropped ws message, not connected",msg,state);
} else {
var s = (function (){try{return shadow.cljs.devtools.client.shared.transit_str(state,msg);
}catch (e20505){var e = e20505;
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("failed to encode relay msg",new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"msg","msg",-1386103444),msg], null));
}})();
return (ws_send_fn.cljs$core$IFn$_invoke$arity$2 ? ws_send_fn.cljs$core$IFn$_invoke$arity$2(ws_state,s) : ws_send_fn.call(null,ws_state,s));
}
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$remote$runtime$api$IRuntime$add_extension$arity$3 = (function (runtime,key,spec){
var self__ = this;
var runtime__$1 = this;
return shadow.remote.runtime.shared.add_extension(runtime__$1,key,spec);
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.shadow$remote$runtime$api$IRuntime$del_extension$arity$2 = (function (runtime,key){
var self__ = this;
var runtime__$1 = this;
return shadow.remote.runtime.shared.del_extension(runtime__$1,key);
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__5458__auto__,k__5459__auto__){
var self__ = this;
var this__5458__auto____$1 = this;
if(cljs.core.contains_QMARK_(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"state-ref","state-ref",2127874952),null], null), null),k__5459__auto__)){
return cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(cljs.core._with_meta(cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,this__5458__auto____$1),self__.__meta),k__5459__auto__);
} else {
return (new shadow.cljs.devtools.client.shared.Runtime(self__.state_ref,self__.__meta,cljs.core.not_empty(cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(self__.__extmap,k__5459__auto__)),null));
}
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.cljs$core$IAssociative$_contains_key_QMARK_$arity$2 = (function (this__5455__auto__,k20453){
var self__ = this;
var this__5455__auto____$1 = this;
var G__20513 = k20453;
var G__20513__$1 = (((G__20513 instanceof cljs.core.Keyword))?G__20513.fqn:null);
switch (G__20513__$1) {
case "state-ref":
return true;

break;
default:
return cljs.core.contains_QMARK_(self__.__extmap,k20453);

}
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__5456__auto__,k__5457__auto__,G__20452){
var self__ = this;
var this__5456__auto____$1 = this;
var pred__20514 = cljs.core.keyword_identical_QMARK_;
var expr__20515 = k__5457__auto__;
if(cljs.core.truth_((pred__20514.cljs$core$IFn$_invoke$arity$2 ? pred__20514.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"state-ref","state-ref",2127874952),expr__20515) : pred__20514.call(null,new cljs.core.Keyword(null,"state-ref","state-ref",2127874952),expr__20515)))){
return (new shadow.cljs.devtools.client.shared.Runtime(G__20452,self__.__meta,self__.__extmap,null));
} else {
return (new shadow.cljs.devtools.client.shared.Runtime(self__.state_ref,self__.__meta,cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(self__.__extmap,k__5457__auto__,G__20452),null));
}
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__5461__auto__){
var self__ = this;
var this__5461__auto____$1 = this;
return cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.MapEntry(new cljs.core.Keyword(null,"state-ref","state-ref",2127874952),self__.state_ref,null))], null),self__.__extmap));
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__5447__auto__,G__20452){
var self__ = this;
var this__5447__auto____$1 = this;
return (new shadow.cljs.devtools.client.shared.Runtime(self__.state_ref,G__20452,self__.__extmap,self__.__hash));
}));

(shadow.cljs.devtools.client.shared.Runtime.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__5453__auto__,entry__5454__auto__){
var self__ = this;
var this__5453__auto____$1 = this;
if(cljs.core.vector_QMARK_(entry__5454__auto__)){
return this__5453__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth(entry__5454__auto__,(0)),cljs.core._nth(entry__5454__auto__,(1)));
} else {
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3(cljs.core._conj,this__5453__auto____$1,entry__5454__auto__);
}
}));

(shadow.cljs.devtools.client.shared.Runtime.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"state-ref","state-ref",-526560817,null)], null);
}));

(shadow.cljs.devtools.client.shared.Runtime.cljs$lang$type = true);

(shadow.cljs.devtools.client.shared.Runtime.cljs$lang$ctorPrSeq = (function (this__5494__auto__){
return (new cljs.core.List(null,"shadow.cljs.devtools.client.shared/Runtime",null,(1),null));
}));

(shadow.cljs.devtools.client.shared.Runtime.cljs$lang$ctorPrWriter = (function (this__5494__auto__,writer__5495__auto__){
return cljs.core._write(writer__5495__auto__,"shadow.cljs.devtools.client.shared/Runtime");
}));

/**
 * Positional factory function for shadow.cljs.devtools.client.shared/Runtime.
 */
shadow.cljs.devtools.client.shared.__GT_Runtime = (function shadow$cljs$devtools$client$shared$__GT_Runtime(state_ref){
return (new shadow.cljs.devtools.client.shared.Runtime(state_ref,null,null,null));
});

/**
 * Factory function for shadow.cljs.devtools.client.shared/Runtime, taking a map of keywords to field values.
 */
shadow.cljs.devtools.client.shared.map__GT_Runtime = (function shadow$cljs$devtools$client$shared$map__GT_Runtime(G__20466){
var extmap__5490__auto__ = (function (){var G__20523 = cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(G__20466,new cljs.core.Keyword(null,"state-ref","state-ref",2127874952));
if(cljs.core.record_QMARK_(G__20466)){
return cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,G__20523);
} else {
return G__20523;
}
})();
return (new shadow.cljs.devtools.client.shared.Runtime(new cljs.core.Keyword(null,"state-ref","state-ref",2127874952).cljs$core$IFn$_invoke$arity$1(G__20466),null,cljs.core.not_empty(extmap__5490__auto__),null));
});

if((typeof shadow !== 'undefined') && (typeof shadow.cljs !== 'undefined') && (typeof shadow.cljs.devtools !== 'undefined') && (typeof shadow.cljs.devtools.client !== 'undefined') && (typeof shadow.cljs.devtools.client.shared !== 'undefined') && (typeof shadow.cljs.devtools.client.shared.print_subs !== 'undefined')){
} else {
shadow.cljs.devtools.client.shared.print_subs = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(cljs.core.PersistentHashSet.EMPTY);
}
shadow.cljs.devtools.client.shared.stop_runtime_BANG_ = (function shadow$cljs$devtools$client$shared$stop_runtime_BANG_(p__20524){
var map__20525 = p__20524;
var map__20525__$1 = cljs.core.__destructure_map(map__20525);
var runtime = map__20525__$1;
var state_ref = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20525__$1,new cljs.core.Keyword(null,"state-ref","state-ref",2127874952));
var map__20526 = cljs.core.deref(state_ref);
var map__20526__$1 = cljs.core.__destructure_map(map__20526);
var ws_state = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20526__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-state","shadow.cljs.devtools.client.shared/ws-state",1653373656));
var ws_stop_fn = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20526__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-stop-fn","shadow.cljs.devtools.client.shared/ws-stop-fn",88454255));
var interval = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20526__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","interval","shadow.cljs.devtools.client.shared/interval",-2112273585));
var plugins = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20526__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","plugins","shadow.cljs.devtools.client.shared/plugins",1565262855));
clearInterval(interval);

if((!((ws_state == null)))){
(ws_stop_fn.cljs$core$IFn$_invoke$arity$1 ? ws_stop_fn.cljs$core$IFn$_invoke$arity$1(ws_state) : ws_stop_fn.call(null,ws_state));
} else {
}

cljs.core.reduce_kv((function (_,plugin_id,started){
var map__20527 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(cljs.core.deref(shadow.cljs.devtools.client.shared.plugins_ref),plugin_id);
var map__20527__$1 = cljs.core.__destructure_map(map__20527);
var stop_fn = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20527__$1,new cljs.core.Keyword(null,"stop-fn","stop-fn",-348472246));
return (stop_fn.cljs$core$IFn$_invoke$arity$1 ? stop_fn.cljs$core$IFn$_invoke$arity$1(started) : stop_fn.call(null,started));
}),null,plugins);

return cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(state_ref,cljs.core.assoc,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","shutdown","shadow.cljs.devtools.client.shared/shutdown",1947481008),true);
});
shadow.cljs.devtools.client.shared.init_runtime_BANG_ = (function shadow$cljs$devtools$client$shared$init_runtime_BANG_(client_info,ws_start_fn,ws_send_fn,ws_stop_fn){
var temp__5827__auto___20702 = cljs.core.deref(shadow.cljs.devtools.client.shared.runtime_ref);
if((temp__5827__auto___20702 == null)){
} else {
var runtime_20703 = temp__5827__auto___20702;
shadow.cljs.devtools.client.shared.stop_runtime_BANG_(runtime_20703);

cljs.core.reset_BANG_(shadow.cljs.devtools.client.shared.runtime_ref,null);
}

shadow.cljs.devtools.client.shared.add_plugin_BANG_(new cljs.core.Keyword(null,"obj-support","obj-support",1522559229),cljs.core.PersistentHashSet.EMPTY,(function (p1__20529_SHARP_){
return shadow.remote.runtime.obj_support.start(new cljs.core.Keyword(null,"runtime","runtime",-1331573996).cljs$core$IFn$_invoke$arity$1(p1__20529_SHARP_));
}),shadow.remote.runtime.obj_support.stop);

shadow.cljs.devtools.client.shared.add_plugin_BANG_(new cljs.core.Keyword(null,"tap-support","tap-support",80346439),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"obj-support","obj-support",1522559229),null], null), null),(function (p__20532){
var map__20533 = p__20532;
var map__20533__$1 = cljs.core.__destructure_map(map__20533);
var runtime = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20533__$1,new cljs.core.Keyword(null,"runtime","runtime",-1331573996));
var obj_support = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20533__$1,new cljs.core.Keyword(null,"obj-support","obj-support",1522559229));
return shadow.remote.runtime.tap_support.start(runtime,obj_support);
}),shadow.remote.runtime.tap_support.stop);

shadow.cljs.devtools.client.shared.add_plugin_BANG_(new cljs.core.Keyword(null,"eval-support","eval-support",-1581799533),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"obj-support","obj-support",1522559229),null], null), null),(function (p__20534){
var map__20535 = p__20534;
var map__20535__$1 = cljs.core.__destructure_map(map__20535);
var runtime = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20535__$1,new cljs.core.Keyword(null,"runtime","runtime",-1331573996));
var obj_support = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20535__$1,new cljs.core.Keyword(null,"obj-support","obj-support",1522559229));
return shadow.remote.runtime.eval_support.start(runtime,obj_support);
}),shadow.remote.runtime.eval_support.stop);

var state_ref = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(cljs.core.assoc.cljs$core$IFn$_invoke$arity$variadic(shadow.remote.runtime.shared.init_state(cljs.core.assoc.cljs$core$IFn$_invoke$arity$variadic(client_info,new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"runtime","runtime",-1331573996),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"lang","lang",-1819677104),new cljs.core.Keyword(null,"cljs","cljs",1492417629),new cljs.core.Keyword(null,"build-id","build-id",1642831089),cljs.core.keyword.cljs$core$IFn$_invoke$arity$1(shadow.cljs.devtools.client.env.build_id),new cljs.core.Keyword(null,"worker-id","worker-id",644510040),shadow.cljs.devtools.client.env.worker_client_id,new cljs.core.Keyword(null,"proc-id","proc-id",-1856109278),shadow.cljs.devtools.client.env.proc_id], 0))),new cljs.core.Keyword("shadow.cljs.devtools.client.shared","shutdown","shadow.cljs.devtools.client.shared/shutdown",1947481008),false,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword("shadow.cljs.devtools.client.shared","stale","shadow.cljs.devtools.client.shared/stale",-1017562082),false,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","plugins","shadow.cljs.devtools.client.shared/plugins",1565262855),cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-errors","shadow.cljs.devtools.client.shared/ws-errors",105464879),(0),new cljs.core.Keyword("shadow.cljs.devtools.client.shared","transit-writers","shadow.cljs.devtools.client.shared/transit-writers",-776127897),cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","transit-readers","shadow.cljs.devtools.client.shared/transit-readers",-1866893537),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"default","default",-1987822328),cljs.core.tagged_literal], null),new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-start-fn","shadow.cljs.devtools.client.shared/ws-start-fn",-49893682),ws_start_fn,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-send-fn","shadow.cljs.devtools.client.shared/ws-send-fn",-1986889866),ws_send_fn,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-stop-fn","shadow.cljs.devtools.client.shared/ws-stop-fn",88454255),ws_stop_fn], 0)));
var runtime = (function (){var G__20538 = shadow.cljs.devtools.client.shared.__GT_Runtime(state_ref);
shadow.remote.runtime.shared.add_defaults(G__20538);

return G__20538;
})();
var idle_fn = (function (){
var map__20539 = cljs.core.deref(state_ref);
var map__20539__$1 = cljs.core.__destructure_map(map__20539);
var state = map__20539__$1;
var shutdown = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20539__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","shutdown","shadow.cljs.devtools.client.shared/shutdown",1947481008));
var ws_connected = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20539__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-connected","shadow.cljs.devtools.client.shared/ws-connected",-1476385853));
var ws_last_msg = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20539__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-last-msg","shadow.cljs.devtools.client.shared/ws-last-msg",-1438906782));
var ws_connect_timeout = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20539__$1,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-connect-timeout","shadow.cljs.devtools.client.shared/ws-connect-timeout",1477702043));
if(cljs.core.truth_((function (){var and__5140__auto__ = cljs.core.not(ws_connect_timeout);
if(and__5140__auto__){
var and__5140__auto____$1 = cljs.core.not(shutdown);
if(and__5140__auto____$1){
var and__5140__auto____$2 = ws_connected;
if(cljs.core.truth_(and__5140__auto____$2)){
return (shadow.remote.runtime.shared.now() > (ws_last_msg + (20000)));
} else {
return and__5140__auto____$2;
}
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
})())){
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(state_ref,cljs.core.dissoc,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","ws-connected","shadow.cljs.devtools.client.shared/ws-connected",-1476385853));

runtime.schedule_connect_BANG_((2000));
} else {
}

return shadow.remote.runtime.shared.run_on_idle(state_ref);
});
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(state_ref,cljs.core.assoc,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","interval","shadow.cljs.devtools.client.shared/interval",-2112273585),setInterval(idle_fn,(1000)));

cljs.core.reset_BANG_(shadow.cljs.devtools.client.shared.runtime_ref,runtime);

(goog.global.cljs_eval = (function (input,opts){
var input__$1 = ((cljs.core.map_QMARK_(input))?input:((((typeof input === 'string') && (cljs.core.not(opts))))?new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"code","code",1586293142),input,new cljs.core.Keyword(null,"ns","ns",441598760),new cljs.core.Symbol(null,"cljs.user","cljs.user",877795071,null)], null):((((typeof input === 'string') && (cljs.core.object_QMARK_(opts))))?new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"code","code",1586293142),input,new cljs.core.Keyword(null,"ns","ns",441598760),cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(shadow.cljs.devtools.client.shared.goog$module$goog$object.get(opts,"ns"))], null):(function(){throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("invalid arguments, call cljs_eval(string, opts-obj) or cljs_eval(map)",new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"input","input",556931961),input,new cljs.core.Keyword(null,"opts","opts",155075701),opts], null))})()
)));
return (new Promise((function (resolve,reject){
return shadow.remote.runtime.api.cljs_eval(runtime,input__$1,(function (p__20542){
var map__20543 = p__20542;
var map__20543__$1 = cljs.core.__destructure_map(map__20543);
var info = map__20543__$1;
var result = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20543__$1,new cljs.core.Keyword(null,"result","result",1415092211));
var results = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20543__$1,new cljs.core.Keyword(null,"results","results",-1134170113));
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"ok","ok",967785236),result)){
var G__20544 = cljs.core.last(results);
return (resolve.cljs$core$IFn$_invoke$arity$1 ? resolve.cljs$core$IFn$_invoke$arity$1(G__20544) : resolve.call(null,G__20544));
} else {
return (reject.cljs$core$IFn$_invoke$arity$1 ? reject.cljs$core$IFn$_invoke$arity$1(info) : reject.call(null,info));
}
}));
})));
}));

runtime.shadow$remote$runtime$api$IRuntime$add_extension$arity$3(null,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","print-support","shadow.cljs.devtools.client.shared/print-support",-1090548637),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"ops","ops",1237330063),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"runtime-print-sub","runtime-print-sub",1832904759),(function (p__20545){
var map__20546 = p__20545;
var map__20546__$1 = cljs.core.__destructure_map(map__20546);
var msg = map__20546__$1;
var from = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20546__$1,new cljs.core.Keyword(null,"from","from",1815293044));
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(shadow.cljs.devtools.client.shared.print_subs,cljs.core.conj,from);

return shadow.remote.runtime.shared.relay_msg(runtime,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"op","op",-1882987955),new cljs.core.Keyword(null,"request-notify","request-notify",351029173),new cljs.core.Keyword(null,"notify-op","notify-op",1568109361),new cljs.core.Keyword("shadow.cljs.devtools.client.shared","runtime-print-disconnect","shadow.cljs.devtools.client.shared/runtime-print-disconnect",2030167412),new cljs.core.Keyword(null,"query","query",-1288509510),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"eq","eq",-618539067),new cljs.core.Keyword(null,"client-id","client-id",-464622140),from], null)], null));
}),new cljs.core.Keyword(null,"runtime-print-unsub","runtime-print-unsub",2055442536),(function (p__20547){
var map__20548 = p__20547;
var map__20548__$1 = cljs.core.__destructure_map(map__20548);
var msg = map__20548__$1;
var from = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20548__$1,new cljs.core.Keyword(null,"from","from",1815293044));
return cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(shadow.cljs.devtools.client.shared.print_subs,cljs.core.disj,from);
}),new cljs.core.Keyword("shadow.cljs.devtools.client.shared","runtime-print-disconnect","shadow.cljs.devtools.client.shared/runtime-print-disconnect",2030167412),(function (p__20550){
var map__20551 = p__20550;
var map__20551__$1 = cljs.core.__destructure_map(map__20551);
var event_op = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20551__$1,new cljs.core.Keyword(null,"event-op","event-op",200358057));
var client_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20551__$1,new cljs.core.Keyword(null,"client-id","client-id",-464622140));
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"client-disconnect","client-disconnect",640227957),event_op)){
return cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(shadow.cljs.devtools.client.shared.print_subs,cljs.core.disj,client_id);
} else {
return null;
}
})], null),new cljs.core.Keyword(null,"on-client-not-found","on-client-not-found",-642452849),(function (p__20552){
var map__20553 = p__20552;
var map__20553__$1 = cljs.core.__destructure_map(map__20553);
var client_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20553__$1,new cljs.core.Keyword(null,"client-id","client-id",-464622140));
return cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(shadow.cljs.devtools.client.shared.print_subs,cljs.core.disj,client_id);
})], null));

shadow.cljs.devtools.client.env.reset_print_fns_BANG_();

shadow.cljs.devtools.client.env.set_print_fns_BANG_((function (stream,text){
var subs = cljs.core.deref(shadow.cljs.devtools.client.shared.print_subs);
if(cljs.core.seq(subs)){
return shadow.remote.runtime.shared.relay_msg(runtime,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"op","op",-1882987955),new cljs.core.Keyword(null,"runtime-print","runtime-print",-1950604603),new cljs.core.Keyword(null,"to","to",192099007),subs,new cljs.core.Keyword(null,"stream","stream",1534941648),stream,new cljs.core.Keyword(null,"text","text",-1790561697),text], null));
} else {
return null;
}
}));

runtime.shadow$remote$runtime$api$IRuntime$add_extension$arity$3(null,new cljs.core.Keyword("shadow.cljs.devtools.client.shared","shared","shadow.cljs.devtools.client.shared/shared",85374117),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-welcome","on-welcome",1895317125),(function (){
return shadow.remote.runtime.shared.call.cljs$core$IFn$_invoke$arity$3(runtime,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"op","op",-1882987955),new cljs.core.Keyword(null,"request-clients","request-clients",13229886),new cljs.core.Keyword(null,"notify","notify",-1256867814),true,new cljs.core.Keyword(null,"notify-op","notify-op",1568109361),new cljs.core.Keyword("shadow.cljs.devtools.client.env","worker-notify","shadow.cljs.devtools.client.env/worker-notify",-1456820670),new cljs.core.Keyword(null,"query","query",-1288509510),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"eq","eq",-618539067),new cljs.core.Keyword("shadow.cljs","worker-for","shadow.cljs/worker-for",-574470432),cljs.core.keyword.cljs$core$IFn$_invoke$arity$1(shadow.cljs.devtools.client.env.build_id)], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"clients","clients",1436018090),(function (p__20554){
var map__20555 = p__20554;
var map__20555__$1 = cljs.core.__destructure_map(map__20555);
var msg = map__20555__$1;
var clients = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20555__$1,new cljs.core.Keyword(null,"clients","clients",1436018090));
if(cljs.core.not(cljs.core.seq(clients))){
return console.error((""+"shadow-cljs watch for build :"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(shadow.cljs.devtools.client.env.build_id)+" not running!"));
} else {
var worker = cljs.core.first(clients);
var map__20556 = worker;
var map__20556__$1 = cljs.core.__destructure_map(map__20556);
var client_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20556__$1,new cljs.core.Keyword(null,"client-id","client-id",-464622140));
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(client_id,shadow.cljs.devtools.client.env.worker_client_id)){
console.warn("shadow-cljs worker id outdated! This can mean that the JS loaded is outdated! Ensure that the shadow-cljs output is not cached. If other tools process this output make sure they update correctly. The code may continue to run fine, but if unexplained things happen make sure to fix this first.");

return (shadow.cljs.devtools.client.env.worker_client_id = client_id);
} else {
return null;
}
}
})], null));
})], null));

shadow.cljs.devtools.client.shared.start_all_plugins_BANG_(runtime);

return runtime.attempt_connect_BANG_();
});

//# sourceMappingURL=shadow.cljs.devtools.client.shared.js.map
