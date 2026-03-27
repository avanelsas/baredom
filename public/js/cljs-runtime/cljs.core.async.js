goog.provide('cljs.core.async');
goog.scope(function(){
  cljs.core.async.goog$module$goog$array = goog.module.get('goog.array');
});

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async14370 = (function (f,blockable,meta14371){
this.f = f;
this.blockable = blockable;
this.meta14371 = meta14371;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async14370.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_14372,meta14371__$1){
var self__ = this;
var _14372__$1 = this;
return (new cljs.core.async.t_cljs$core$async14370(self__.f,self__.blockable,meta14371__$1));
}));

(cljs.core.async.t_cljs$core$async14370.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_14372){
var self__ = this;
var _14372__$1 = this;
return self__.meta14371;
}));

(cljs.core.async.t_cljs$core$async14370.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async14370.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return true;
}));

(cljs.core.async.t_cljs$core$async14370.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.blockable;
}));

(cljs.core.async.t_cljs$core$async14370.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.f;
}));

(cljs.core.async.t_cljs$core$async14370.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"blockable","blockable",-28395259,null),new cljs.core.Symbol(null,"meta14371","meta14371",1740552408,null)], null);
}));

(cljs.core.async.t_cljs$core$async14370.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async14370.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async14370");

(cljs.core.async.t_cljs$core$async14370.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async14370");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async14370.
 */
cljs.core.async.__GT_t_cljs$core$async14370 = (function cljs$core$async$__GT_t_cljs$core$async14370(f,blockable,meta14371){
return (new cljs.core.async.t_cljs$core$async14370(f,blockable,meta14371));
});


cljs.core.async.fn_handler = (function cljs$core$async$fn_handler(var_args){
var G__14366 = arguments.length;
switch (G__14366) {
case 1:
return cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$1 = (function (f){
return cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$2(f,true);
}));

(cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$2 = (function (f,blockable){
return (new cljs.core.async.t_cljs$core$async14370(f,blockable,cljs.core.PersistentArrayMap.EMPTY));
}));

(cljs.core.async.fn_handler.cljs$lang$maxFixedArity = 2);

/**
 * Returns a fixed buffer of size n. When full, puts will block/park.
 */
cljs.core.async.buffer = (function cljs$core$async$buffer(n){
return cljs.core.async.impl.buffers.fixed_buffer(n);
});
/**
 * Returns a buffer of size n. When full, puts will complete but
 *   val will be dropped (no transfer).
 */
cljs.core.async.dropping_buffer = (function cljs$core$async$dropping_buffer(n){
return cljs.core.async.impl.buffers.dropping_buffer(n);
});
/**
 * Returns a buffer of size n. When full, puts will complete, and be
 *   buffered, but oldest elements in buffer will be dropped (not
 *   transferred).
 */
cljs.core.async.sliding_buffer = (function cljs$core$async$sliding_buffer(n){
return cljs.core.async.impl.buffers.sliding_buffer(n);
});
/**
 * Returns true if a channel created with buff will never block. That is to say,
 * puts into this buffer will never cause the buffer to be full. 
 */
cljs.core.async.unblocking_buffer_QMARK_ = (function cljs$core$async$unblocking_buffer_QMARK_(buff){
if((!((buff == null)))){
if(((false) || ((cljs.core.PROTOCOL_SENTINEL === buff.cljs$core$async$impl$protocols$UnblockingBuffer$)))){
return true;
} else {
if((!buff.cljs$lang$protocol_mask$partition$)){
return cljs.core.native_satisfies_QMARK_(cljs.core.async.impl.protocols.UnblockingBuffer,buff);
} else {
return false;
}
}
} else {
return cljs.core.native_satisfies_QMARK_(cljs.core.async.impl.protocols.UnblockingBuffer,buff);
}
});
/**
 * Creates a channel with an optional buffer, an optional transducer (like (map f),
 *   (filter p) etc or a composition thereof), and an optional exception handler.
 *   If buf-or-n is a number, will create and use a fixed buffer of that size. If a
 *   transducer is supplied a buffer must be specified. ex-handler must be a
 *   fn of one argument - if an exception occurs during transformation it will be called
 *   with the thrown value as an argument, and any non-nil return value will be placed
 *   in the channel.
 */
cljs.core.async.chan = (function cljs$core$async$chan(var_args){
var G__14387 = arguments.length;
switch (G__14387) {
case 0:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$0();

break;
case 1:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.chan.cljs$core$IFn$_invoke$arity$0 = (function (){
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(null);
}));

(cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1 = (function (buf_or_n){
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3(buf_or_n,null,null);
}));

(cljs.core.async.chan.cljs$core$IFn$_invoke$arity$2 = (function (buf_or_n,xform){
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3(buf_or_n,xform,null);
}));

(cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3 = (function (buf_or_n,xform,ex_handler){
var buf_or_n__$1 = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(buf_or_n,(0)))?null:buf_or_n);
if(cljs.core.truth_(xform)){
if(cljs.core.truth_(buf_or_n__$1)){
} else {
throw (new Error((""+"Assert failed: "+"buffer must be supplied when transducer is"+"\n"+"buf-or-n")));
}
} else {
}

return cljs.core.async.impl.channels.chan.cljs$core$IFn$_invoke$arity$3(((typeof buf_or_n__$1 === 'number')?cljs.core.async.buffer(buf_or_n__$1):buf_or_n__$1),xform,ex_handler);
}));

(cljs.core.async.chan.cljs$lang$maxFixedArity = 3);

/**
 * Creates a promise channel with an optional transducer, and an optional
 *   exception-handler. A promise channel can take exactly one value that consumers
 *   will receive. Once full, puts complete but val is dropped (no transfer).
 *   Consumers will block until either a value is placed in the channel or the
 *   channel is closed, then return the value (or nil) forever. See chan for the
 *   semantics of xform and ex-handler.
 */
cljs.core.async.promise_chan = (function cljs$core$async$promise_chan(var_args){
var G__14389 = arguments.length;
switch (G__14389) {
case 0:
return cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$0();

break;
case 1:
return cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$0 = (function (){
return cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$1(null);
}));

(cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$1 = (function (xform){
return cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$2(xform,null);
}));

(cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$2 = (function (xform,ex_handler){
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3(cljs.core.async.impl.buffers.promise_buffer(),xform,ex_handler);
}));

(cljs.core.async.promise_chan.cljs$lang$maxFixedArity = 2);

/**
 * Returns a channel that will close after msecs
 */
cljs.core.async.timeout = (function cljs$core$async$timeout(msecs){
return cljs.core.async.impl.timers.timeout(msecs);
});
/**
 * takes a val from port. Must be called inside a (go ...) block. Will
 *   return nil if closed. Will park if nothing is available.
 *   Returns true unless port is already closed
 */
cljs.core.async._LT__BANG_ = (function cljs$core$async$_LT__BANG_(port){
throw (new Error("<! used not in (go ...) block"));
});
/**
 * Asynchronously takes a val from port, passing to fn1. Will pass nil
 * if closed. If on-caller? (default true) is true, and value is
 * immediately available, will call fn1 on calling thread.
 * Returns nil.
 */
cljs.core.async.take_BANG_ = (function cljs$core$async$take_BANG_(var_args){
var G__14395 = arguments.length;
switch (G__14395) {
case 2:
return cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$2 = (function (port,fn1){
return cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$3(port,fn1,true);
}));

(cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$3 = (function (port,fn1,on_caller_QMARK_){
var ret = cljs.core.async.impl.protocols.take_BANG_(port,cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$1(fn1));
if(cljs.core.truth_(ret)){
var val_17576 = cljs.core.deref(ret);
if(cljs.core.truth_(on_caller_QMARK_)){
(fn1.cljs$core$IFn$_invoke$arity$1 ? fn1.cljs$core$IFn$_invoke$arity$1(val_17576) : fn1.call(null,val_17576));
} else {
cljs.core.async.impl.dispatch.run((function (){
return (fn1.cljs$core$IFn$_invoke$arity$1 ? fn1.cljs$core$IFn$_invoke$arity$1(val_17576) : fn1.call(null,val_17576));
}));
}
} else {
}

return null;
}));

(cljs.core.async.take_BANG_.cljs$lang$maxFixedArity = 3);

cljs.core.async.nop = (function cljs$core$async$nop(_){
return null;
});
cljs.core.async.fhnop = cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$1(cljs.core.async.nop);
/**
 * puts a val into port. nil values are not allowed. Must be called
 *   inside a (go ...) block. Will park if no buffer space is available.
 *   Returns true unless port is already closed.
 */
cljs.core.async._GT__BANG_ = (function cljs$core$async$_GT__BANG_(port,val){
throw (new Error(">! used not in (go ...) block"));
});
/**
 * Asynchronously puts a val into port, calling fn1 (if supplied) when
 * complete. nil values are not allowed. Will throw if closed. If
 * on-caller? (default true) is true, and the put is immediately
 * accepted, will call fn1 on calling thread.  Returns nil.
 */
cljs.core.async.put_BANG_ = (function cljs$core$async$put_BANG_(var_args){
var G__14425 = arguments.length;
switch (G__14425) {
case 2:
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2 = (function (port,val){
var temp__5821__auto__ = cljs.core.async.impl.protocols.put_BANG_(port,val,cljs.core.async.fhnop);
if(cljs.core.truth_(temp__5821__auto__)){
var ret = temp__5821__auto__;
return cljs.core.deref(ret);
} else {
return true;
}
}));

(cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$3 = (function (port,val,fn1){
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$4(port,val,fn1,true);
}));

(cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$4 = (function (port,val,fn1,on_caller_QMARK_){
var temp__5821__auto__ = cljs.core.async.impl.protocols.put_BANG_(port,val,cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$1(fn1));
if(cljs.core.truth_(temp__5821__auto__)){
var retb = temp__5821__auto__;
var ret = cljs.core.deref(retb);
if(cljs.core.truth_(on_caller_QMARK_)){
(fn1.cljs$core$IFn$_invoke$arity$1 ? fn1.cljs$core$IFn$_invoke$arity$1(ret) : fn1.call(null,ret));
} else {
cljs.core.async.impl.dispatch.run((function (){
return (fn1.cljs$core$IFn$_invoke$arity$1 ? fn1.cljs$core$IFn$_invoke$arity$1(ret) : fn1.call(null,ret));
}));
}

return ret;
} else {
return true;
}
}));

(cljs.core.async.put_BANG_.cljs$lang$maxFixedArity = 4);

cljs.core.async.close_BANG_ = (function cljs$core$async$close_BANG_(port){
return cljs.core.async.impl.protocols.close_BANG_(port);
});
cljs.core.async.random_array = (function cljs$core$async$random_array(n){
var a = (new Array(n));
var n__5741__auto___17604 = n;
var x_17605 = (0);
while(true){
if((x_17605 < n__5741__auto___17604)){
(a[x_17605] = x_17605);

var G__17606 = (x_17605 + (1));
x_17605 = G__17606;
continue;
} else {
}
break;
}

cljs.core.async.goog$module$goog$array.shuffle(a);

return a;
});

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async14436 = (function (flag,meta14437){
this.flag = flag;
this.meta14437 = meta14437;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async14436.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_14438,meta14437__$1){
var self__ = this;
var _14438__$1 = this;
return (new cljs.core.async.t_cljs$core$async14436(self__.flag,meta14437__$1));
}));

(cljs.core.async.t_cljs$core$async14436.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_14438){
var self__ = this;
var _14438__$1 = this;
return self__.meta14437;
}));

(cljs.core.async.t_cljs$core$async14436.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async14436.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.deref(self__.flag);
}));

(cljs.core.async.t_cljs$core$async14436.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return true;
}));

(cljs.core.async.t_cljs$core$async14436.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_(self__.flag,null);

return true;
}));

(cljs.core.async.t_cljs$core$async14436.getBasis = (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"flag","flag",-1565787888,null),new cljs.core.Symbol(null,"meta14437","meta14437",-1627874931,null)], null);
}));

(cljs.core.async.t_cljs$core$async14436.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async14436.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async14436");

(cljs.core.async.t_cljs$core$async14436.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async14436");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async14436.
 */
cljs.core.async.__GT_t_cljs$core$async14436 = (function cljs$core$async$__GT_t_cljs$core$async14436(flag,meta14437){
return (new cljs.core.async.t_cljs$core$async14436(flag,meta14437));
});


cljs.core.async.alt_flag = (function cljs$core$async$alt_flag(){
var flag = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(true);
return (new cljs.core.async.t_cljs$core$async14436(flag,cljs.core.PersistentArrayMap.EMPTY));
});

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async14446 = (function (flag,cb,meta14447){
this.flag = flag;
this.cb = cb;
this.meta14447 = meta14447;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async14446.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_14448,meta14447__$1){
var self__ = this;
var _14448__$1 = this;
return (new cljs.core.async.t_cljs$core$async14446(self__.flag,self__.cb,meta14447__$1));
}));

(cljs.core.async.t_cljs$core$async14446.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_14448){
var self__ = this;
var _14448__$1 = this;
return self__.meta14447;
}));

(cljs.core.async.t_cljs$core$async14446.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async14446.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.active_QMARK_(self__.flag);
}));

(cljs.core.async.t_cljs$core$async14446.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return true;
}));

(cljs.core.async.t_cljs$core$async14446.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.async.impl.protocols.commit(self__.flag);

return self__.cb;
}));

(cljs.core.async.t_cljs$core$async14446.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"flag","flag",-1565787888,null),new cljs.core.Symbol(null,"cb","cb",-2064487928,null),new cljs.core.Symbol(null,"meta14447","meta14447",951442901,null)], null);
}));

(cljs.core.async.t_cljs$core$async14446.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async14446.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async14446");

(cljs.core.async.t_cljs$core$async14446.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async14446");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async14446.
 */
cljs.core.async.__GT_t_cljs$core$async14446 = (function cljs$core$async$__GT_t_cljs$core$async14446(flag,cb,meta14447){
return (new cljs.core.async.t_cljs$core$async14446(flag,cb,meta14447));
});


cljs.core.async.alt_handler = (function cljs$core$async$alt_handler(flag,cb){
return (new cljs.core.async.t_cljs$core$async14446(flag,cb,cljs.core.PersistentArrayMap.EMPTY));
});
/**
 * returns derefable [val port] if immediate, nil if enqueued
 */
cljs.core.async.do_alts = (function cljs$core$async$do_alts(fret,ports,opts){
if((cljs.core.count(ports) > (0))){
} else {
throw (new Error((""+"Assert failed: "+"alts must have at least one channel operation"+"\n"+"(pos? (count ports))")));
}

var flag = cljs.core.async.alt_flag();
var ports__$1 = cljs.core.vec(ports);
var n = cljs.core.count(ports__$1);
var _ = (function (){var i = (0);
while(true){
if((i < n)){
var port_17611 = cljs.core.nth.cljs$core$IFn$_invoke$arity$2(ports__$1,i);
if(cljs.core.vector_QMARK_(port_17611)){
if((!(((port_17611.cljs$core$IFn$_invoke$arity$1 ? port_17611.cljs$core$IFn$_invoke$arity$1((1)) : port_17611.call(null,(1))) == null)))){
} else {
throw (new Error((""+"Assert failed: "+"can't put nil on channel"+"\n"+"(some? (port 1))")));
}
} else {
}

var G__17612 = (i + (1));
i = G__17612;
continue;
} else {
return null;
}
break;
}
})();
var idxs = cljs.core.async.random_array(n);
var priority = new cljs.core.Keyword(null,"priority","priority",1431093715).cljs$core$IFn$_invoke$arity$1(opts);
var ret = (function (){var i = (0);
while(true){
if((i < n)){
var idx = (cljs.core.truth_(priority)?i:(idxs[i]));
var port = cljs.core.nth.cljs$core$IFn$_invoke$arity$2(ports__$1,idx);
var wport = ((cljs.core.vector_QMARK_(port))?(port.cljs$core$IFn$_invoke$arity$1 ? port.cljs$core$IFn$_invoke$arity$1((0)) : port.call(null,(0))):null);
var vbox = (cljs.core.truth_(wport)?(function (){var val = (port.cljs$core$IFn$_invoke$arity$1 ? port.cljs$core$IFn$_invoke$arity$1((1)) : port.call(null,(1)));
return cljs.core.async.impl.protocols.put_BANG_(wport,val,cljs.core.async.alt_handler(flag,((function (i,val,idx,port,wport,flag,ports__$1,n,_,idxs,priority){
return (function (p1__14460_SHARP_){
var G__14481 = new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__14460_SHARP_,wport], null);
return (fret.cljs$core$IFn$_invoke$arity$1 ? fret.cljs$core$IFn$_invoke$arity$1(G__14481) : fret.call(null,G__14481));
});})(i,val,idx,port,wport,flag,ports__$1,n,_,idxs,priority))
));
})():cljs.core.async.impl.protocols.take_BANG_(port,cljs.core.async.alt_handler(flag,((function (i,idx,port,wport,flag,ports__$1,n,_,idxs,priority){
return (function (p1__14462_SHARP_){
var G__14485 = new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__14462_SHARP_,port], null);
return (fret.cljs$core$IFn$_invoke$arity$1 ? fret.cljs$core$IFn$_invoke$arity$1(G__14485) : fret.call(null,G__14485));
});})(i,idx,port,wport,flag,ports__$1,n,_,idxs,priority))
)));
if(cljs.core.truth_(vbox)){
return cljs.core.async.impl.channels.box(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.deref(vbox),(function (){var or__5142__auto__ = wport;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return port;
}
})()], null));
} else {
var G__17617 = (i + (1));
i = G__17617;
continue;
}
} else {
return null;
}
break;
}
})();
var or__5142__auto__ = ret;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
if(cljs.core.contains_QMARK_(opts,new cljs.core.Keyword(null,"default","default",-1987822328))){
var temp__5823__auto__ = (function (){var and__5140__auto__ = flag.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1(null);
if(cljs.core.truth_(and__5140__auto__)){
return flag.cljs$core$async$impl$protocols$Handler$commit$arity$1(null);
} else {
return and__5140__auto__;
}
})();
if(cljs.core.truth_(temp__5823__auto__)){
var got = temp__5823__auto__;
return cljs.core.async.impl.channels.box(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"default","default",-1987822328).cljs$core$IFn$_invoke$arity$1(opts),new cljs.core.Keyword(null,"default","default",-1987822328)], null));
} else {
return null;
}
} else {
return null;
}
}
});
/**
 * Completes at most one of several channel operations. Must be called
 * inside a (go ...) block. ports is a vector of channel endpoints,
 * which can be either a channel to take from or a vector of
 *   [channel-to-put-to val-to-put], in any combination. Takes will be
 *   made as if by <!, and puts will be made as if by >!. Unless
 *   the :priority option is true, if more than one port operation is
 *   ready a non-deterministic choice will be made. If no operation is
 *   ready and a :default value is supplied, [default-val :default] will
 *   be returned, otherwise alts! will park until the first operation to
 *   become ready completes. Returns [val port] of the completed
 *   operation, where val is the value taken for takes, and a
 *   boolean (true unless already closed, as per put!) for puts.
 * 
 *   opts are passed as :key val ... Supported options:
 * 
 *   :default val - the value to use if none of the operations are immediately ready
 *   :priority true - (default nil) when true, the operations will be tried in order.
 * 
 *   Note: there is no guarantee that the port exps or val exprs will be
 *   used, nor in what order should they be, so they should not be
 *   depended upon for side effects.
 */
cljs.core.async.alts_BANG_ = (function cljs$core$async$alts_BANG_(var_args){
var args__5882__auto__ = [];
var len__5876__auto___17618 = arguments.length;
var i__5877__auto___17619 = (0);
while(true){
if((i__5877__auto___17619 < len__5876__auto___17618)){
args__5882__auto__.push((arguments[i__5877__auto___17619]));

var G__17621 = (i__5877__auto___17619 + (1));
i__5877__auto___17619 = G__17621;
continue;
} else {
}
break;
}

var argseq__5883__auto__ = ((((1) < args__5882__auto__.length))?(new cljs.core.IndexedSeq(args__5882__auto__.slice((1)),(0),null)):null);
return cljs.core.async.alts_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__5883__auto__);
});

(cljs.core.async.alts_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (ports,p__14502){
var map__14503 = p__14502;
var map__14503__$1 = cljs.core.__destructure_map(map__14503);
var opts = map__14503__$1;
throw (new Error("alts! used not in (go ...) block"));
}));

(cljs.core.async.alts_BANG_.cljs$lang$maxFixedArity = (1));

/** @this {Function} */
(cljs.core.async.alts_BANG_.cljs$lang$applyTo = (function (seq14497){
var G__14498 = cljs.core.first(seq14497);
var seq14497__$1 = cljs.core.next(seq14497);
var self__5861__auto__ = this;
return self__5861__auto__.cljs$core$IFn$_invoke$arity$variadic(G__14498,seq14497__$1);
}));

/**
 * Puts a val into port if it's possible to do so immediately.
 *   nil values are not allowed. Never blocks. Returns true if offer succeeds.
 */
cljs.core.async.offer_BANG_ = (function cljs$core$async$offer_BANG_(port,val){
var ret = cljs.core.async.impl.protocols.put_BANG_(port,val,cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$2(cljs.core.async.nop,false));
if(cljs.core.truth_(ret)){
return cljs.core.deref(ret);
} else {
return null;
}
});
/**
 * Takes a val from port if it's possible to do so immediately.
 *   Never blocks. Returns value if successful, nil otherwise.
 */
cljs.core.async.poll_BANG_ = (function cljs$core$async$poll_BANG_(port){
var ret = cljs.core.async.impl.protocols.take_BANG_(port,cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$2(cljs.core.async.nop,false));
if(cljs.core.truth_(ret)){
return cljs.core.deref(ret);
} else {
return null;
}
});
/**
 * Takes elements from the from channel and supplies them to the to
 * channel. By default, the to channel will be closed when the from
 * channel closes, but can be determined by the close?  parameter. Will
 * stop consuming the from channel if the to channel closes
 */
cljs.core.async.pipe = (function cljs$core$async$pipe(var_args){
var G__14533 = arguments.length;
switch (G__14533) {
case 2:
return cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$2 = (function (from,to){
return cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$3(from,to,true);
}));

(cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$3 = (function (from,to,close_QMARK_){
var c__14286__auto___17630 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = (function (state_14612){
var state_val_14613 = (state_14612[(1)]);
if((state_val_14613 === (7))){
var inst_14605 = (state_14612[(2)]);
var state_14612__$1 = state_14612;
var statearr_14626_17635 = state_14612__$1;
(statearr_14626_17635[(2)] = inst_14605);

(statearr_14626_17635[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14613 === (1))){
var state_14612__$1 = state_14612;
var statearr_14630_17636 = state_14612__$1;
(statearr_14630_17636[(2)] = null);

(statearr_14630_17636[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14613 === (4))){
var inst_14571 = (state_14612[(7)]);
var inst_14571__$1 = (state_14612[(2)]);
var inst_14580 = (inst_14571__$1 == null);
var state_14612__$1 = (function (){var statearr_14637 = state_14612;
(statearr_14637[(7)] = inst_14571__$1);

return statearr_14637;
})();
if(cljs.core.truth_(inst_14580)){
var statearr_14639_17648 = state_14612__$1;
(statearr_14639_17648[(1)] = (5));

} else {
var statearr_14640_17649 = state_14612__$1;
(statearr_14640_17649[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14613 === (13))){
var state_14612__$1 = state_14612;
var statearr_14644_17653 = state_14612__$1;
(statearr_14644_17653[(2)] = null);

(statearr_14644_17653[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14613 === (6))){
var inst_14571 = (state_14612[(7)]);
var state_14612__$1 = state_14612;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_14612__$1,(11),to,inst_14571);
} else {
if((state_val_14613 === (3))){
var inst_14607 = (state_14612[(2)]);
var state_14612__$1 = state_14612;
return cljs.core.async.impl.ioc_helpers.return_chan(state_14612__$1,inst_14607);
} else {
if((state_val_14613 === (12))){
var state_14612__$1 = state_14612;
var statearr_14652_17658 = state_14612__$1;
(statearr_14652_17658[(2)] = null);

(statearr_14652_17658[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14613 === (2))){
var state_14612__$1 = state_14612;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_14612__$1,(4),from);
} else {
if((state_val_14613 === (11))){
var inst_14594 = (state_14612[(2)]);
var state_14612__$1 = state_14612;
if(cljs.core.truth_(inst_14594)){
var statearr_14654_17659 = state_14612__$1;
(statearr_14654_17659[(1)] = (12));

} else {
var statearr_14658_17660 = state_14612__$1;
(statearr_14658_17660[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14613 === (9))){
var state_14612__$1 = state_14612;
var statearr_14660_17663 = state_14612__$1;
(statearr_14660_17663[(2)] = null);

(statearr_14660_17663[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14613 === (5))){
var state_14612__$1 = state_14612;
if(cljs.core.truth_(close_QMARK_)){
var statearr_14693_17677 = state_14612__$1;
(statearr_14693_17677[(1)] = (8));

} else {
var statearr_14695_17680 = state_14612__$1;
(statearr_14695_17680[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14613 === (14))){
var inst_14603 = (state_14612[(2)]);
var state_14612__$1 = state_14612;
var statearr_14706_17682 = state_14612__$1;
(statearr_14706_17682[(2)] = inst_14603);

(statearr_14706_17682[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14613 === (10))){
var inst_14590 = (state_14612[(2)]);
var state_14612__$1 = state_14612;
var statearr_14707_17683 = state_14612__$1;
(statearr_14707_17683[(2)] = inst_14590);

(statearr_14707_17683[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14613 === (8))){
var inst_14587 = cljs.core.async.close_BANG_(to);
var state_14612__$1 = state_14612;
var statearr_14712_17685 = state_14612__$1;
(statearr_14712_17685[(2)] = inst_14587);

(statearr_14712_17685[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__14092__auto__ = null;
var cljs$core$async$state_machine__14092__auto____0 = (function (){
var statearr_14714 = [null,null,null,null,null,null,null,null];
(statearr_14714[(0)] = cljs$core$async$state_machine__14092__auto__);

(statearr_14714[(1)] = (1));

return statearr_14714;
});
var cljs$core$async$state_machine__14092__auto____1 = (function (state_14612){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_14612);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e14721){var ex__14095__auto__ = e14721;
var statearr_14722_17688 = state_14612;
(statearr_14722_17688[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_14612[(4)]))){
var statearr_14731_17691 = state_14612;
(statearr_14731_17691[(1)] = cljs.core.first((state_14612[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__17701 = state_14612;
state_14612 = G__17701;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$state_machine__14092__auto__ = function(state_14612){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14092__auto____1.call(this,state_14612);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14092__auto____0;
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14092__auto____1;
return cljs$core$async$state_machine__14092__auto__;
})()
})();
var state__14288__auto__ = (function (){var statearr_14735 = f__14287__auto__();
(statearr_14735[(6)] = c__14286__auto___17630);

return statearr_14735;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
}));


return to;
}));

(cljs.core.async.pipe.cljs$lang$maxFixedArity = 3);

cljs.core.async.pipeline_STAR_ = (function cljs$core$async$pipeline_STAR_(n,to,xf,from,close_QMARK_,ex_handler,type){
if((n > (0))){
} else {
throw (new Error("Assert failed: (pos? n)"));
}

var jobs = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(n);
var results = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(n);
var process__$1 = (function (p__14760){
var vec__14762 = p__14760;
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__14762,(0),null);
var p = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__14762,(1),null);
var job = vec__14762;
if((job == null)){
cljs.core.async.close_BANG_(results);

return null;
} else {
var res = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3((1),xf,ex_handler);
var c__14286__auto___17717 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = (function (state_14770){
var state_val_14771 = (state_14770[(1)]);
if((state_val_14771 === (1))){
var state_14770__$1 = state_14770;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_14770__$1,(2),res,v);
} else {
if((state_val_14771 === (2))){
var inst_14767 = (state_14770[(2)]);
var inst_14768 = cljs.core.async.close_BANG_(res);
var state_14770__$1 = (function (){var statearr_14776 = state_14770;
(statearr_14776[(7)] = inst_14767);

return statearr_14776;
})();
return cljs.core.async.impl.ioc_helpers.return_chan(state_14770__$1,inst_14768);
} else {
return null;
}
}
});
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____0 = (function (){
var statearr_14777 = [null,null,null,null,null,null,null,null];
(statearr_14777[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__);

(statearr_14777[(1)] = (1));

return statearr_14777;
});
var cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____1 = (function (state_14770){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_14770);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e14778){var ex__14095__auto__ = e14778;
var statearr_14779_17730 = state_14770;
(statearr_14779_17730[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_14770[(4)]))){
var statearr_14781_17731 = state_14770;
(statearr_14781_17731[(1)] = cljs.core.first((state_14770[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__17742 = state_14770;
state_14770 = G__17742;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__ = function(state_14770){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____1.call(this,state_14770);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__;
})()
})();
var state__14288__auto__ = (function (){var statearr_14786 = f__14287__auto__();
(statearr_14786[(6)] = c__14286__auto___17717);

return statearr_14786;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
}));


cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2(p,res);

return true;
}
});
var async = (function (p__14792){
var vec__14793 = p__14792;
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__14793,(0),null);
var p = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__14793,(1),null);
var job = vec__14793;
if((job == null)){
cljs.core.async.close_BANG_(results);

return null;
} else {
var res = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
(xf.cljs$core$IFn$_invoke$arity$2 ? xf.cljs$core$IFn$_invoke$arity$2(v,res) : xf.call(null,v,res));

cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2(p,res);

return true;
}
});
var n__5741__auto___17767 = n;
var __17768 = (0);
while(true){
if((__17768 < n__5741__auto___17767)){
var G__14803_17772 = type;
var G__14803_17773__$1 = (((G__14803_17772 instanceof cljs.core.Keyword))?G__14803_17772.fqn:null);
switch (G__14803_17773__$1) {
case "compute":
var c__14286__auto___17775 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run(((function (__17768,c__14286__auto___17775,G__14803_17772,G__14803_17773__$1,n__5741__auto___17767,jobs,results,process__$1,async){
return (function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = ((function (__17768,c__14286__auto___17775,G__14803_17772,G__14803_17773__$1,n__5741__auto___17767,jobs,results,process__$1,async){
return (function (state_14825){
var state_val_14826 = (state_14825[(1)]);
if((state_val_14826 === (1))){
var state_14825__$1 = state_14825;
var statearr_14829_17780 = state_14825__$1;
(statearr_14829_17780[(2)] = null);

(statearr_14829_17780[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14826 === (2))){
var state_14825__$1 = state_14825;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_14825__$1,(4),jobs);
} else {
if((state_val_14826 === (3))){
var inst_14823 = (state_14825[(2)]);
var state_14825__$1 = state_14825;
return cljs.core.async.impl.ioc_helpers.return_chan(state_14825__$1,inst_14823);
} else {
if((state_val_14826 === (4))){
var inst_14814 = (state_14825[(2)]);
var inst_14816 = process__$1(inst_14814);
var state_14825__$1 = state_14825;
if(cljs.core.truth_(inst_14816)){
var statearr_14836_17785 = state_14825__$1;
(statearr_14836_17785[(1)] = (5));

} else {
var statearr_14837_17786 = state_14825__$1;
(statearr_14837_17786[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14826 === (5))){
var state_14825__$1 = state_14825;
var statearr_14841_17787 = state_14825__$1;
(statearr_14841_17787[(2)] = null);

(statearr_14841_17787[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14826 === (6))){
var state_14825__$1 = state_14825;
var statearr_14843_17788 = state_14825__$1;
(statearr_14843_17788[(2)] = null);

(statearr_14843_17788[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14826 === (7))){
var inst_14821 = (state_14825[(2)]);
var state_14825__$1 = state_14825;
var statearr_14845_17789 = state_14825__$1;
(statearr_14845_17789[(2)] = inst_14821);

(statearr_14845_17789[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
});})(__17768,c__14286__auto___17775,G__14803_17772,G__14803_17773__$1,n__5741__auto___17767,jobs,results,process__$1,async))
;
return ((function (__17768,switch__14091__auto__,c__14286__auto___17775,G__14803_17772,G__14803_17773__$1,n__5741__auto___17767,jobs,results,process__$1,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____0 = (function (){
var statearr_14846 = [null,null,null,null,null,null,null];
(statearr_14846[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__);

(statearr_14846[(1)] = (1));

return statearr_14846;
});
var cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____1 = (function (state_14825){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_14825);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e14848){var ex__14095__auto__ = e14848;
var statearr_14849_17794 = state_14825;
(statearr_14849_17794[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_14825[(4)]))){
var statearr_14852_17795 = state_14825;
(statearr_14852_17795[(1)] = cljs.core.first((state_14825[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__17796 = state_14825;
state_14825 = G__17796;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__ = function(state_14825){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____1.call(this,state_14825);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__;
})()
;})(__17768,switch__14091__auto__,c__14286__auto___17775,G__14803_17772,G__14803_17773__$1,n__5741__auto___17767,jobs,results,process__$1,async))
})();
var state__14288__auto__ = (function (){var statearr_14857 = f__14287__auto__();
(statearr_14857[(6)] = c__14286__auto___17775);

return statearr_14857;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
});})(__17768,c__14286__auto___17775,G__14803_17772,G__14803_17773__$1,n__5741__auto___17767,jobs,results,process__$1,async))
);


break;
case "async":
var c__14286__auto___17798 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run(((function (__17768,c__14286__auto___17798,G__14803_17772,G__14803_17773__$1,n__5741__auto___17767,jobs,results,process__$1,async){
return (function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = ((function (__17768,c__14286__auto___17798,G__14803_17772,G__14803_17773__$1,n__5741__auto___17767,jobs,results,process__$1,async){
return (function (state_14878){
var state_val_14879 = (state_14878[(1)]);
if((state_val_14879 === (1))){
var state_14878__$1 = state_14878;
var statearr_14882_17800 = state_14878__$1;
(statearr_14882_17800[(2)] = null);

(statearr_14882_17800[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14879 === (2))){
var state_14878__$1 = state_14878;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_14878__$1,(4),jobs);
} else {
if((state_val_14879 === (3))){
var inst_14875 = (state_14878[(2)]);
var state_14878__$1 = state_14878;
return cljs.core.async.impl.ioc_helpers.return_chan(state_14878__$1,inst_14875);
} else {
if((state_val_14879 === (4))){
var inst_14865 = (state_14878[(2)]);
var inst_14867 = async(inst_14865);
var state_14878__$1 = state_14878;
if(cljs.core.truth_(inst_14867)){
var statearr_14887_17801 = state_14878__$1;
(statearr_14887_17801[(1)] = (5));

} else {
var statearr_14888_17802 = state_14878__$1;
(statearr_14888_17802[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14879 === (5))){
var state_14878__$1 = state_14878;
var statearr_14890_17803 = state_14878__$1;
(statearr_14890_17803[(2)] = null);

(statearr_14890_17803[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14879 === (6))){
var state_14878__$1 = state_14878;
var statearr_14899_17804 = state_14878__$1;
(statearr_14899_17804[(2)] = null);

(statearr_14899_17804[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14879 === (7))){
var inst_14872 = (state_14878[(2)]);
var state_14878__$1 = state_14878;
var statearr_14900_17808 = state_14878__$1;
(statearr_14900_17808[(2)] = inst_14872);

(statearr_14900_17808[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
});})(__17768,c__14286__auto___17798,G__14803_17772,G__14803_17773__$1,n__5741__auto___17767,jobs,results,process__$1,async))
;
return ((function (__17768,switch__14091__auto__,c__14286__auto___17798,G__14803_17772,G__14803_17773__$1,n__5741__auto___17767,jobs,results,process__$1,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____0 = (function (){
var statearr_14902 = [null,null,null,null,null,null,null];
(statearr_14902[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__);

(statearr_14902[(1)] = (1));

return statearr_14902;
});
var cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____1 = (function (state_14878){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_14878);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e14906){var ex__14095__auto__ = e14906;
var statearr_14909_17811 = state_14878;
(statearr_14909_17811[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_14878[(4)]))){
var statearr_14913_17812 = state_14878;
(statearr_14913_17812[(1)] = cljs.core.first((state_14878[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__17813 = state_14878;
state_14878 = G__17813;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__ = function(state_14878){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____1.call(this,state_14878);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__;
})()
;})(__17768,switch__14091__auto__,c__14286__auto___17798,G__14803_17772,G__14803_17773__$1,n__5741__auto___17767,jobs,results,process__$1,async))
})();
var state__14288__auto__ = (function (){var statearr_14918 = f__14287__auto__();
(statearr_14918[(6)] = c__14286__auto___17798);

return statearr_14918;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
});})(__17768,c__14286__auto___17798,G__14803_17772,G__14803_17773__$1,n__5741__auto___17767,jobs,results,process__$1,async))
);


break;
default:
throw (new Error((""+"No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__14803_17773__$1))));

}

var G__17814 = (__17768 + (1));
__17768 = G__17814;
continue;
} else {
}
break;
}

var c__14286__auto___17815 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = (function (state_14949){
var state_val_14950 = (state_14949[(1)]);
if((state_val_14950 === (7))){
var inst_14945 = (state_14949[(2)]);
var state_14949__$1 = state_14949;
var statearr_14972_17816 = state_14949__$1;
(statearr_14972_17816[(2)] = inst_14945);

(statearr_14972_17816[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14950 === (1))){
var state_14949__$1 = state_14949;
var statearr_14977_17824 = state_14949__$1;
(statearr_14977_17824[(2)] = null);

(statearr_14977_17824[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14950 === (4))){
var inst_14928 = (state_14949[(7)]);
var inst_14928__$1 = (state_14949[(2)]);
var inst_14929 = (inst_14928__$1 == null);
var state_14949__$1 = (function (){var statearr_14986 = state_14949;
(statearr_14986[(7)] = inst_14928__$1);

return statearr_14986;
})();
if(cljs.core.truth_(inst_14929)){
var statearr_14993_17825 = state_14949__$1;
(statearr_14993_17825[(1)] = (5));

} else {
var statearr_15001_17827 = state_14949__$1;
(statearr_15001_17827[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14950 === (6))){
var inst_14928 = (state_14949[(7)]);
var inst_14934 = (state_14949[(8)]);
var inst_14934__$1 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
var inst_14936 = cljs.core.PersistentVector.EMPTY_NODE;
var inst_14937 = [inst_14928,inst_14934__$1];
var inst_14938 = (new cljs.core.PersistentVector(null,2,(5),inst_14936,inst_14937,null));
var state_14949__$1 = (function (){var statearr_15002 = state_14949;
(statearr_15002[(8)] = inst_14934__$1);

return statearr_15002;
})();
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_14949__$1,(8),jobs,inst_14938);
} else {
if((state_val_14950 === (3))){
var inst_14947 = (state_14949[(2)]);
var state_14949__$1 = state_14949;
return cljs.core.async.impl.ioc_helpers.return_chan(state_14949__$1,inst_14947);
} else {
if((state_val_14950 === (2))){
var state_14949__$1 = state_14949;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_14949__$1,(4),from);
} else {
if((state_val_14950 === (9))){
var inst_14942 = (state_14949[(2)]);
var state_14949__$1 = (function (){var statearr_15008 = state_14949;
(statearr_15008[(9)] = inst_14942);

return statearr_15008;
})();
var statearr_15010_17836 = state_14949__$1;
(statearr_15010_17836[(2)] = null);

(statearr_15010_17836[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14950 === (5))){
var inst_14931 = cljs.core.async.close_BANG_(jobs);
var state_14949__$1 = state_14949;
var statearr_15011_17837 = state_14949__$1;
(statearr_15011_17837[(2)] = inst_14931);

(statearr_15011_17837[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14950 === (8))){
var inst_14934 = (state_14949[(8)]);
var inst_14940 = (state_14949[(2)]);
var state_14949__$1 = (function (){var statearr_15015 = state_14949;
(statearr_15015[(10)] = inst_14940);

return statearr_15015;
})();
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_14949__$1,(9),results,inst_14934);
} else {
return null;
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____0 = (function (){
var statearr_15024 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_15024[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__);

(statearr_15024[(1)] = (1));

return statearr_15024;
});
var cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____1 = (function (state_14949){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_14949);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e15026){var ex__14095__auto__ = e15026;
var statearr_15027_17840 = state_14949;
(statearr_15027_17840[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_14949[(4)]))){
var statearr_15042_17841 = state_14949;
(statearr_15042_17841[(1)] = cljs.core.first((state_14949[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__17842 = state_14949;
state_14949 = G__17842;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__ = function(state_14949){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____1.call(this,state_14949);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__;
})()
})();
var state__14288__auto__ = (function (){var statearr_15044 = f__14287__auto__();
(statearr_15044[(6)] = c__14286__auto___17815);

return statearr_15044;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
}));


var c__14286__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = (function (state_15090){
var state_val_15093 = (state_15090[(1)]);
if((state_val_15093 === (7))){
var inst_15085 = (state_15090[(2)]);
var state_15090__$1 = state_15090;
var statearr_15098_17854 = state_15090__$1;
(statearr_15098_17854[(2)] = inst_15085);

(statearr_15098_17854[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15093 === (20))){
var state_15090__$1 = state_15090;
var statearr_15099_17855 = state_15090__$1;
(statearr_15099_17855[(2)] = null);

(statearr_15099_17855[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15093 === (1))){
var state_15090__$1 = state_15090;
var statearr_15102_17873 = state_15090__$1;
(statearr_15102_17873[(2)] = null);

(statearr_15102_17873[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15093 === (4))){
var inst_15048 = (state_15090[(7)]);
var inst_15048__$1 = (state_15090[(2)]);
var inst_15049 = (inst_15048__$1 == null);
var state_15090__$1 = (function (){var statearr_15106 = state_15090;
(statearr_15106[(7)] = inst_15048__$1);

return statearr_15106;
})();
if(cljs.core.truth_(inst_15049)){
var statearr_15109_17874 = state_15090__$1;
(statearr_15109_17874[(1)] = (5));

} else {
var statearr_15110_17875 = state_15090__$1;
(statearr_15110_17875[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15093 === (15))){
var inst_15065 = (state_15090[(8)]);
var state_15090__$1 = state_15090;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_15090__$1,(18),to,inst_15065);
} else {
if((state_val_15093 === (21))){
var inst_15080 = (state_15090[(2)]);
var state_15090__$1 = state_15090;
var statearr_15114_17877 = state_15090__$1;
(statearr_15114_17877[(2)] = inst_15080);

(statearr_15114_17877[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15093 === (13))){
var inst_15082 = (state_15090[(2)]);
var state_15090__$1 = (function (){var statearr_15115 = state_15090;
(statearr_15115[(9)] = inst_15082);

return statearr_15115;
})();
var statearr_15116_17878 = state_15090__$1;
(statearr_15116_17878[(2)] = null);

(statearr_15116_17878[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15093 === (6))){
var inst_15048 = (state_15090[(7)]);
var state_15090__$1 = state_15090;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15090__$1,(11),inst_15048);
} else {
if((state_val_15093 === (17))){
var inst_15073 = (state_15090[(2)]);
var state_15090__$1 = state_15090;
if(cljs.core.truth_(inst_15073)){
var statearr_15117_17879 = state_15090__$1;
(statearr_15117_17879[(1)] = (19));

} else {
var statearr_15119_17880 = state_15090__$1;
(statearr_15119_17880[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15093 === (3))){
var inst_15087 = (state_15090[(2)]);
var state_15090__$1 = state_15090;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15090__$1,inst_15087);
} else {
if((state_val_15093 === (12))){
var inst_15061 = (state_15090[(10)]);
var state_15090__$1 = state_15090;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15090__$1,(14),inst_15061);
} else {
if((state_val_15093 === (2))){
var state_15090__$1 = state_15090;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15090__$1,(4),results);
} else {
if((state_val_15093 === (19))){
var state_15090__$1 = state_15090;
var statearr_15121_17885 = state_15090__$1;
(statearr_15121_17885[(2)] = null);

(statearr_15121_17885[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15093 === (11))){
var inst_15061 = (state_15090[(2)]);
var state_15090__$1 = (function (){var statearr_15122 = state_15090;
(statearr_15122[(10)] = inst_15061);

return statearr_15122;
})();
var statearr_15123_17886 = state_15090__$1;
(statearr_15123_17886[(2)] = null);

(statearr_15123_17886[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15093 === (9))){
var state_15090__$1 = state_15090;
var statearr_15124_17890 = state_15090__$1;
(statearr_15124_17890[(2)] = null);

(statearr_15124_17890[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15093 === (5))){
var state_15090__$1 = state_15090;
if(cljs.core.truth_(close_QMARK_)){
var statearr_15125_17891 = state_15090__$1;
(statearr_15125_17891[(1)] = (8));

} else {
var statearr_15126_17892 = state_15090__$1;
(statearr_15126_17892[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15093 === (14))){
var inst_15065 = (state_15090[(8)]);
var inst_15067 = (state_15090[(11)]);
var inst_15065__$1 = (state_15090[(2)]);
var inst_15066 = (inst_15065__$1 == null);
var inst_15067__$1 = cljs.core.not(inst_15066);
var state_15090__$1 = (function (){var statearr_15128 = state_15090;
(statearr_15128[(8)] = inst_15065__$1);

(statearr_15128[(11)] = inst_15067__$1);

return statearr_15128;
})();
if(inst_15067__$1){
var statearr_15131_17893 = state_15090__$1;
(statearr_15131_17893[(1)] = (15));

} else {
var statearr_15132_17894 = state_15090__$1;
(statearr_15132_17894[(1)] = (16));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15093 === (16))){
var inst_15067 = (state_15090[(11)]);
var state_15090__$1 = state_15090;
var statearr_15134_17895 = state_15090__$1;
(statearr_15134_17895[(2)] = inst_15067);

(statearr_15134_17895[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15093 === (10))){
var inst_15055 = (state_15090[(2)]);
var state_15090__$1 = state_15090;
var statearr_15135_17896 = state_15090__$1;
(statearr_15135_17896[(2)] = inst_15055);

(statearr_15135_17896[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15093 === (18))){
var inst_15070 = (state_15090[(2)]);
var state_15090__$1 = state_15090;
var statearr_15136_17899 = state_15090__$1;
(statearr_15136_17899[(2)] = inst_15070);

(statearr_15136_17899[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15093 === (8))){
var inst_15052 = cljs.core.async.close_BANG_(to);
var state_15090__$1 = state_15090;
var statearr_15140_17900 = state_15090__$1;
(statearr_15140_17900[(2)] = inst_15052);

(statearr_15140_17900[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____0 = (function (){
var statearr_15141 = [null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_15141[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__);

(statearr_15141[(1)] = (1));

return statearr_15141;
});
var cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____1 = (function (state_15090){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_15090);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e15142){var ex__14095__auto__ = e15142;
var statearr_15143_17903 = state_15090;
(statearr_15143_17903[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_15090[(4)]))){
var statearr_15144_17904 = state_15090;
(statearr_15144_17904[(1)] = cljs.core.first((state_15090[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__17907 = state_15090;
state_15090 = G__17907;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__ = function(state_15090){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____1.call(this,state_15090);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__14092__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__14092__auto__;
})()
})();
var state__14288__auto__ = (function (){var statearr_15145 = f__14287__auto__();
(statearr_15145[(6)] = c__14286__auto__);

return statearr_15145;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
}));

return c__14286__auto__;
});
/**
 * Takes elements from the from channel and supplies them to the to
 *   channel, subject to the async function af, with parallelism n. af
 *   must be a function of two arguments, the first an input value and
 *   the second a channel on which to place the result(s). The
 *   presumption is that af will return immediately, having launched some
 *   asynchronous operation whose completion/callback will put results on
 *   the channel, then close! it. Outputs will be returned in order
 *   relative to the inputs. By default, the to channel will be closed
 *   when the from channel closes, but can be determined by the close?
 *   parameter. Will stop consuming the from channel if the to channel
 *   closes. See also pipeline, pipeline-blocking.
 */
cljs.core.async.pipeline_async = (function cljs$core$async$pipeline_async(var_args){
var G__15150 = arguments.length;
switch (G__15150) {
case 4:
return cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
case 5:
return cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$5((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$4 = (function (n,to,af,from){
return cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$5(n,to,af,from,true);
}));

(cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$5 = (function (n,to,af,from,close_QMARK_){
return cljs.core.async.pipeline_STAR_(n,to,af,from,close_QMARK_,null,new cljs.core.Keyword(null,"async","async",1050769601));
}));

(cljs.core.async.pipeline_async.cljs$lang$maxFixedArity = 5);

/**
 * Takes elements from the from channel and supplies them to the to
 *   channel, subject to the transducer xf, with parallelism n. Because
 *   it is parallel, the transducer will be applied independently to each
 *   element, not across elements, and may produce zero or more outputs
 *   per input.  Outputs will be returned in order relative to the
 *   inputs. By default, the to channel will be closed when the from
 *   channel closes, but can be determined by the close?  parameter. Will
 *   stop consuming the from channel if the to channel closes.
 * 
 *   Note this is supplied for API compatibility with the Clojure version.
 *   Values of N > 1 will not result in actual concurrency in a
 *   single-threaded runtime.
 */
cljs.core.async.pipeline = (function cljs$core$async$pipeline(var_args){
var G__15160 = arguments.length;
switch (G__15160) {
case 4:
return cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
case 5:
return cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$5((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]));

break;
case 6:
return cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$6((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]),(arguments[(5)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$4 = (function (n,to,xf,from){
return cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$5(n,to,xf,from,true);
}));

(cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$5 = (function (n,to,xf,from,close_QMARK_){
return cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$6(n,to,xf,from,close_QMARK_,null);
}));

(cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$6 = (function (n,to,xf,from,close_QMARK_,ex_handler){
return cljs.core.async.pipeline_STAR_(n,to,xf,from,close_QMARK_,ex_handler,new cljs.core.Keyword(null,"compute","compute",1555393130));
}));

(cljs.core.async.pipeline.cljs$lang$maxFixedArity = 6);

/**
 * Takes a predicate and a source channel and returns a vector of two
 *   channels, the first of which will contain the values for which the
 *   predicate returned true, the second those for which it returned
 *   false.
 * 
 *   The out channels will be unbuffered by default, or two buf-or-ns can
 *   be supplied. The channels will close after the source channel has
 *   closed.
 */
cljs.core.async.split = (function cljs$core$async$split(var_args){
var G__15169 = arguments.length;
switch (G__15169) {
case 2:
return cljs.core.async.split.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 4:
return cljs.core.async.split.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.split.cljs$core$IFn$_invoke$arity$2 = (function (p,ch){
return cljs.core.async.split.cljs$core$IFn$_invoke$arity$4(p,ch,null,null);
}));

(cljs.core.async.split.cljs$core$IFn$_invoke$arity$4 = (function (p,ch,t_buf_or_n,f_buf_or_n){
var tc = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(t_buf_or_n);
var fc = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(f_buf_or_n);
var c__14286__auto___17933 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = (function (state_15236){
var state_val_15237 = (state_15236[(1)]);
if((state_val_15237 === (7))){
var inst_15232 = (state_15236[(2)]);
var state_15236__$1 = state_15236;
var statearr_15239_17934 = state_15236__$1;
(statearr_15239_17934[(2)] = inst_15232);

(statearr_15239_17934[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15237 === (1))){
var state_15236__$1 = state_15236;
var statearr_15243_17935 = state_15236__$1;
(statearr_15243_17935[(2)] = null);

(statearr_15243_17935[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15237 === (4))){
var inst_15207 = (state_15236[(7)]);
var inst_15207__$1 = (state_15236[(2)]);
var inst_15212 = (inst_15207__$1 == null);
var state_15236__$1 = (function (){var statearr_15244 = state_15236;
(statearr_15244[(7)] = inst_15207__$1);

return statearr_15244;
})();
if(cljs.core.truth_(inst_15212)){
var statearr_15245_17941 = state_15236__$1;
(statearr_15245_17941[(1)] = (5));

} else {
var statearr_15246_17942 = state_15236__$1;
(statearr_15246_17942[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15237 === (13))){
var state_15236__$1 = state_15236;
var statearr_15250_17943 = state_15236__$1;
(statearr_15250_17943[(2)] = null);

(statearr_15250_17943[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15237 === (6))){
var inst_15207 = (state_15236[(7)]);
var inst_15218 = (p.cljs$core$IFn$_invoke$arity$1 ? p.cljs$core$IFn$_invoke$arity$1(inst_15207) : p.call(null,inst_15207));
var state_15236__$1 = state_15236;
if(cljs.core.truth_(inst_15218)){
var statearr_15251_17952 = state_15236__$1;
(statearr_15251_17952[(1)] = (9));

} else {
var statearr_15253_17955 = state_15236__$1;
(statearr_15253_17955[(1)] = (10));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15237 === (3))){
var inst_15234 = (state_15236[(2)]);
var state_15236__$1 = state_15236;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15236__$1,inst_15234);
} else {
if((state_val_15237 === (12))){
var state_15236__$1 = state_15236;
var statearr_15262_17956 = state_15236__$1;
(statearr_15262_17956[(2)] = null);

(statearr_15262_17956[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15237 === (2))){
var state_15236__$1 = state_15236;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15236__$1,(4),ch);
} else {
if((state_val_15237 === (11))){
var inst_15207 = (state_15236[(7)]);
var inst_15222 = (state_15236[(2)]);
var state_15236__$1 = state_15236;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_15236__$1,(8),inst_15222,inst_15207);
} else {
if((state_val_15237 === (9))){
var state_15236__$1 = state_15236;
var statearr_15275_17958 = state_15236__$1;
(statearr_15275_17958[(2)] = tc);

(statearr_15275_17958[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15237 === (5))){
var inst_15214 = cljs.core.async.close_BANG_(tc);
var inst_15215 = cljs.core.async.close_BANG_(fc);
var state_15236__$1 = (function (){var statearr_15284 = state_15236;
(statearr_15284[(8)] = inst_15214);

return statearr_15284;
})();
var statearr_15285_17959 = state_15236__$1;
(statearr_15285_17959[(2)] = inst_15215);

(statearr_15285_17959[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15237 === (14))){
var inst_15230 = (state_15236[(2)]);
var state_15236__$1 = state_15236;
var statearr_15291_17975 = state_15236__$1;
(statearr_15291_17975[(2)] = inst_15230);

(statearr_15291_17975[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15237 === (10))){
var state_15236__$1 = state_15236;
var statearr_15292_17976 = state_15236__$1;
(statearr_15292_17976[(2)] = fc);

(statearr_15292_17976[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15237 === (8))){
var inst_15224 = (state_15236[(2)]);
var state_15236__$1 = state_15236;
if(cljs.core.truth_(inst_15224)){
var statearr_15293_17977 = state_15236__$1;
(statearr_15293_17977[(1)] = (12));

} else {
var statearr_15294_17978 = state_15236__$1;
(statearr_15294_17978[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__14092__auto__ = null;
var cljs$core$async$state_machine__14092__auto____0 = (function (){
var statearr_15302 = [null,null,null,null,null,null,null,null,null];
(statearr_15302[(0)] = cljs$core$async$state_machine__14092__auto__);

(statearr_15302[(1)] = (1));

return statearr_15302;
});
var cljs$core$async$state_machine__14092__auto____1 = (function (state_15236){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_15236);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e15303){var ex__14095__auto__ = e15303;
var statearr_15310_17980 = state_15236;
(statearr_15310_17980[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_15236[(4)]))){
var statearr_15311_17981 = state_15236;
(statearr_15311_17981[(1)] = cljs.core.first((state_15236[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__17982 = state_15236;
state_15236 = G__17982;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$state_machine__14092__auto__ = function(state_15236){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14092__auto____1.call(this,state_15236);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14092__auto____0;
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14092__auto____1;
return cljs$core$async$state_machine__14092__auto__;
})()
})();
var state__14288__auto__ = (function (){var statearr_15313 = f__14287__auto__();
(statearr_15313[(6)] = c__14286__auto___17933);

return statearr_15313;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
}));


return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [tc,fc], null);
}));

(cljs.core.async.split.cljs$lang$maxFixedArity = 4);

/**
 * f should be a function of 2 arguments. Returns a channel containing
 *   the single result of applying f to init and the first item from the
 *   channel, then applying f to that result and the 2nd item, etc. If
 *   the channel closes without yielding items, returns init and f is not
 *   called. ch must close before reduce produces a result.
 */
cljs.core.async.reduce = (function cljs$core$async$reduce(f,init,ch){
var c__14286__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = (function (state_15345){
var state_val_15346 = (state_15345[(1)]);
if((state_val_15346 === (7))){
var inst_15338 = (state_15345[(2)]);
var state_15345__$1 = state_15345;
var statearr_15347_17994 = state_15345__$1;
(statearr_15347_17994[(2)] = inst_15338);

(statearr_15347_17994[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15346 === (1))){
var inst_15318 = init;
var inst_15319 = inst_15318;
var state_15345__$1 = (function (){var statearr_15349 = state_15345;
(statearr_15349[(7)] = inst_15319);

return statearr_15349;
})();
var statearr_15350_17998 = state_15345__$1;
(statearr_15350_17998[(2)] = null);

(statearr_15350_17998[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15346 === (4))){
var inst_15325 = (state_15345[(8)]);
var inst_15325__$1 = (state_15345[(2)]);
var inst_15326 = (inst_15325__$1 == null);
var state_15345__$1 = (function (){var statearr_15351 = state_15345;
(statearr_15351[(8)] = inst_15325__$1);

return statearr_15351;
})();
if(cljs.core.truth_(inst_15326)){
var statearr_15352_18002 = state_15345__$1;
(statearr_15352_18002[(1)] = (5));

} else {
var statearr_15353_18003 = state_15345__$1;
(statearr_15353_18003[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15346 === (6))){
var inst_15319 = (state_15345[(7)]);
var inst_15325 = (state_15345[(8)]);
var inst_15329 = (state_15345[(9)]);
var inst_15329__$1 = (f.cljs$core$IFn$_invoke$arity$2 ? f.cljs$core$IFn$_invoke$arity$2(inst_15319,inst_15325) : f.call(null,inst_15319,inst_15325));
var inst_15330 = cljs.core.reduced_QMARK_(inst_15329__$1);
var state_15345__$1 = (function (){var statearr_15354 = state_15345;
(statearr_15354[(9)] = inst_15329__$1);

return statearr_15354;
})();
if(inst_15330){
var statearr_15355_18004 = state_15345__$1;
(statearr_15355_18004[(1)] = (8));

} else {
var statearr_15356_18005 = state_15345__$1;
(statearr_15356_18005[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15346 === (3))){
var inst_15340 = (state_15345[(2)]);
var state_15345__$1 = state_15345;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15345__$1,inst_15340);
} else {
if((state_val_15346 === (2))){
var state_15345__$1 = state_15345;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15345__$1,(4),ch);
} else {
if((state_val_15346 === (9))){
var inst_15329 = (state_15345[(9)]);
var inst_15319 = inst_15329;
var state_15345__$1 = (function (){var statearr_15360 = state_15345;
(statearr_15360[(7)] = inst_15319);

return statearr_15360;
})();
var statearr_15361_18006 = state_15345__$1;
(statearr_15361_18006[(2)] = null);

(statearr_15361_18006[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15346 === (5))){
var inst_15319 = (state_15345[(7)]);
var state_15345__$1 = state_15345;
var statearr_15362_18008 = state_15345__$1;
(statearr_15362_18008[(2)] = inst_15319);

(statearr_15362_18008[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15346 === (10))){
var inst_15336 = (state_15345[(2)]);
var state_15345__$1 = state_15345;
var statearr_15364_18009 = state_15345__$1;
(statearr_15364_18009[(2)] = inst_15336);

(statearr_15364_18009[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15346 === (8))){
var inst_15329 = (state_15345[(9)]);
var inst_15332 = cljs.core.deref(inst_15329);
var state_15345__$1 = state_15345;
var statearr_15365_18012 = state_15345__$1;
(statearr_15365_18012[(2)] = inst_15332);

(statearr_15365_18012[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$reduce_$_state_machine__14092__auto__ = null;
var cljs$core$async$reduce_$_state_machine__14092__auto____0 = (function (){
var statearr_15368 = [null,null,null,null,null,null,null,null,null,null];
(statearr_15368[(0)] = cljs$core$async$reduce_$_state_machine__14092__auto__);

(statearr_15368[(1)] = (1));

return statearr_15368;
});
var cljs$core$async$reduce_$_state_machine__14092__auto____1 = (function (state_15345){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_15345);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e15369){var ex__14095__auto__ = e15369;
var statearr_15370_18017 = state_15345;
(statearr_15370_18017[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_15345[(4)]))){
var statearr_15371_18019 = state_15345;
(statearr_15371_18019[(1)] = cljs.core.first((state_15345[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18020 = state_15345;
state_15345 = G__18020;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$reduce_$_state_machine__14092__auto__ = function(state_15345){
switch(arguments.length){
case 0:
return cljs$core$async$reduce_$_state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$reduce_$_state_machine__14092__auto____1.call(this,state_15345);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$reduce_$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$reduce_$_state_machine__14092__auto____0;
cljs$core$async$reduce_$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$reduce_$_state_machine__14092__auto____1;
return cljs$core$async$reduce_$_state_machine__14092__auto__;
})()
})();
var state__14288__auto__ = (function (){var statearr_15375 = f__14287__auto__();
(statearr_15375[(6)] = c__14286__auto__);

return statearr_15375;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
}));

return c__14286__auto__;
});
/**
 * async/reduces a channel with a transformation (xform f).
 *   Returns a channel containing the result.  ch must close before
 *   transduce produces a result.
 */
cljs.core.async.transduce = (function cljs$core$async$transduce(xform,f,init,ch){
var f__$1 = (xform.cljs$core$IFn$_invoke$arity$1 ? xform.cljs$core$IFn$_invoke$arity$1(f) : xform.call(null,f));
var c__14286__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = (function (state_15385){
var state_val_15386 = (state_15385[(1)]);
if((state_val_15386 === (1))){
var inst_15380 = cljs.core.async.reduce(f__$1,init,ch);
var state_15385__$1 = state_15385;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15385__$1,(2),inst_15380);
} else {
if((state_val_15386 === (2))){
var inst_15382 = (state_15385[(2)]);
var inst_15383 = (f__$1.cljs$core$IFn$_invoke$arity$1 ? f__$1.cljs$core$IFn$_invoke$arity$1(inst_15382) : f__$1.call(null,inst_15382));
var state_15385__$1 = state_15385;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15385__$1,inst_15383);
} else {
return null;
}
}
});
return (function() {
var cljs$core$async$transduce_$_state_machine__14092__auto__ = null;
var cljs$core$async$transduce_$_state_machine__14092__auto____0 = (function (){
var statearr_15392 = [null,null,null,null,null,null,null];
(statearr_15392[(0)] = cljs$core$async$transduce_$_state_machine__14092__auto__);

(statearr_15392[(1)] = (1));

return statearr_15392;
});
var cljs$core$async$transduce_$_state_machine__14092__auto____1 = (function (state_15385){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_15385);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e15394){var ex__14095__auto__ = e15394;
var statearr_15396_18049 = state_15385;
(statearr_15396_18049[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_15385[(4)]))){
var statearr_15398_18054 = state_15385;
(statearr_15398_18054[(1)] = cljs.core.first((state_15385[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18055 = state_15385;
state_15385 = G__18055;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$transduce_$_state_machine__14092__auto__ = function(state_15385){
switch(arguments.length){
case 0:
return cljs$core$async$transduce_$_state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$transduce_$_state_machine__14092__auto____1.call(this,state_15385);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$transduce_$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$transduce_$_state_machine__14092__auto____0;
cljs$core$async$transduce_$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$transduce_$_state_machine__14092__auto____1;
return cljs$core$async$transduce_$_state_machine__14092__auto__;
})()
})();
var state__14288__auto__ = (function (){var statearr_15399 = f__14287__auto__();
(statearr_15399[(6)] = c__14286__auto__);

return statearr_15399;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
}));

return c__14286__auto__;
});
/**
 * Puts the contents of coll into the supplied channel.
 * 
 *   By default the channel will be closed after the items are copied,
 *   but can be determined by the close? parameter.
 * 
 *   Returns a channel which will close after the items are copied.
 */
cljs.core.async.onto_chan_BANG_ = (function cljs$core$async$onto_chan_BANG_(var_args){
var G__15404 = arguments.length;
switch (G__15404) {
case 2:
return cljs.core.async.onto_chan_BANG_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.onto_chan_BANG_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.onto_chan_BANG_.cljs$core$IFn$_invoke$arity$2 = (function (ch,coll){
return cljs.core.async.onto_chan_BANG_.cljs$core$IFn$_invoke$arity$3(ch,coll,true);
}));

(cljs.core.async.onto_chan_BANG_.cljs$core$IFn$_invoke$arity$3 = (function (ch,coll,close_QMARK_){
var c__14286__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = (function (state_15453){
var state_val_15454 = (state_15453[(1)]);
if((state_val_15454 === (7))){
var inst_15434 = (state_15453[(2)]);
var state_15453__$1 = state_15453;
var statearr_15455_18071 = state_15453__$1;
(statearr_15455_18071[(2)] = inst_15434);

(statearr_15455_18071[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15454 === (1))){
var inst_15422 = cljs.core.seq(coll);
var inst_15423 = inst_15422;
var state_15453__$1 = (function (){var statearr_15456 = state_15453;
(statearr_15456[(7)] = inst_15423);

return statearr_15456;
})();
var statearr_15457_18073 = state_15453__$1;
(statearr_15457_18073[(2)] = null);

(statearr_15457_18073[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15454 === (4))){
var inst_15423 = (state_15453[(7)]);
var inst_15432 = cljs.core.first(inst_15423);
var state_15453__$1 = state_15453;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_15453__$1,(7),ch,inst_15432);
} else {
if((state_val_15454 === (13))){
var inst_15447 = (state_15453[(2)]);
var state_15453__$1 = state_15453;
var statearr_15462_18081 = state_15453__$1;
(statearr_15462_18081[(2)] = inst_15447);

(statearr_15462_18081[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15454 === (6))){
var inst_15437 = (state_15453[(2)]);
var state_15453__$1 = state_15453;
if(cljs.core.truth_(inst_15437)){
var statearr_15466_18086 = state_15453__$1;
(statearr_15466_18086[(1)] = (8));

} else {
var statearr_15467_18087 = state_15453__$1;
(statearr_15467_18087[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15454 === (3))){
var inst_15451 = (state_15453[(2)]);
var state_15453__$1 = state_15453;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15453__$1,inst_15451);
} else {
if((state_val_15454 === (12))){
var state_15453__$1 = state_15453;
var statearr_15469_18088 = state_15453__$1;
(statearr_15469_18088[(2)] = null);

(statearr_15469_18088[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15454 === (2))){
var inst_15423 = (state_15453[(7)]);
var state_15453__$1 = state_15453;
if(cljs.core.truth_(inst_15423)){
var statearr_15470_18090 = state_15453__$1;
(statearr_15470_18090[(1)] = (4));

} else {
var statearr_15471_18091 = state_15453__$1;
(statearr_15471_18091[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15454 === (11))){
var inst_15444 = cljs.core.async.close_BANG_(ch);
var state_15453__$1 = state_15453;
var statearr_15472_18092 = state_15453__$1;
(statearr_15472_18092[(2)] = inst_15444);

(statearr_15472_18092[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15454 === (9))){
var state_15453__$1 = state_15453;
if(cljs.core.truth_(close_QMARK_)){
var statearr_15474_18098 = state_15453__$1;
(statearr_15474_18098[(1)] = (11));

} else {
var statearr_15475_18101 = state_15453__$1;
(statearr_15475_18101[(1)] = (12));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15454 === (5))){
var inst_15423 = (state_15453[(7)]);
var state_15453__$1 = state_15453;
var statearr_15479_18102 = state_15453__$1;
(statearr_15479_18102[(2)] = inst_15423);

(statearr_15479_18102[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15454 === (10))){
var inst_15449 = (state_15453[(2)]);
var state_15453__$1 = state_15453;
var statearr_15485_18103 = state_15453__$1;
(statearr_15485_18103[(2)] = inst_15449);

(statearr_15485_18103[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15454 === (8))){
var inst_15423 = (state_15453[(7)]);
var inst_15439 = cljs.core.next(inst_15423);
var inst_15423__$1 = inst_15439;
var state_15453__$1 = (function (){var statearr_15486 = state_15453;
(statearr_15486[(7)] = inst_15423__$1);

return statearr_15486;
})();
var statearr_15487_18105 = state_15453__$1;
(statearr_15487_18105[(2)] = null);

(statearr_15487_18105[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__14092__auto__ = null;
var cljs$core$async$state_machine__14092__auto____0 = (function (){
var statearr_15490 = [null,null,null,null,null,null,null,null];
(statearr_15490[(0)] = cljs$core$async$state_machine__14092__auto__);

(statearr_15490[(1)] = (1));

return statearr_15490;
});
var cljs$core$async$state_machine__14092__auto____1 = (function (state_15453){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_15453);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e15492){var ex__14095__auto__ = e15492;
var statearr_15494_18111 = state_15453;
(statearr_15494_18111[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_15453[(4)]))){
var statearr_15497_18118 = state_15453;
(statearr_15497_18118[(1)] = cljs.core.first((state_15453[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18119 = state_15453;
state_15453 = G__18119;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$state_machine__14092__auto__ = function(state_15453){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14092__auto____1.call(this,state_15453);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14092__auto____0;
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14092__auto____1;
return cljs$core$async$state_machine__14092__auto__;
})()
})();
var state__14288__auto__ = (function (){var statearr_15498 = f__14287__auto__();
(statearr_15498[(6)] = c__14286__auto__);

return statearr_15498;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
}));

return c__14286__auto__;
}));

(cljs.core.async.onto_chan_BANG_.cljs$lang$maxFixedArity = 3);

/**
 * Creates and returns a channel which contains the contents of coll,
 *   closing when exhausted.
 */
cljs.core.async.to_chan_BANG_ = (function cljs$core$async$to_chan_BANG_(coll){
var ch = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(cljs.core.bounded_count((100),coll));
cljs.core.async.onto_chan_BANG_.cljs$core$IFn$_invoke$arity$2(ch,coll);

return ch;
});
/**
 * Deprecated - use onto-chan!
 */
cljs.core.async.onto_chan = (function cljs$core$async$onto_chan(var_args){
var G__15501 = arguments.length;
switch (G__15501) {
case 2:
return cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$2 = (function (ch,coll){
return cljs.core.async.onto_chan_BANG_.cljs$core$IFn$_invoke$arity$3(ch,coll,true);
}));

(cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$3 = (function (ch,coll,close_QMARK_){
return cljs.core.async.onto_chan_BANG_.cljs$core$IFn$_invoke$arity$3(ch,coll,close_QMARK_);
}));

(cljs.core.async.onto_chan.cljs$lang$maxFixedArity = 3);

/**
 * Deprecated - use to-chan!
 */
cljs.core.async.to_chan = (function cljs$core$async$to_chan(coll){
return cljs.core.async.to_chan_BANG_(coll);
});

/**
 * @interface
 */
cljs.core.async.Mux = function(){};

var cljs$core$async$Mux$muxch_STAR_$dyn_18131 = (function (_){
var x__5498__auto__ = (((_ == null))?null:_);
var m__5499__auto__ = (cljs.core.async.muxch_STAR_[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$1(_) : m__5499__auto__.call(null,_));
} else {
var m__5497__auto__ = (cljs.core.async.muxch_STAR_["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$1(_) : m__5497__auto__.call(null,_));
} else {
throw cljs.core.missing_protocol("Mux.muxch*",_);
}
}
});
cljs.core.async.muxch_STAR_ = (function cljs$core$async$muxch_STAR_(_){
if((((!((_ == null)))) && ((!((_.cljs$core$async$Mux$muxch_STAR_$arity$1 == null)))))){
return _.cljs$core$async$Mux$muxch_STAR_$arity$1(_);
} else {
return cljs$core$async$Mux$muxch_STAR_$dyn_18131(_);
}
});


/**
 * @interface
 */
cljs.core.async.Mult = function(){};

var cljs$core$async$Mult$tap_STAR_$dyn_18133 = (function (m,ch,close_QMARK_){
var x__5498__auto__ = (((m == null))?null:m);
var m__5499__auto__ = (cljs.core.async.tap_STAR_[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$3 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$3(m,ch,close_QMARK_) : m__5499__auto__.call(null,m,ch,close_QMARK_));
} else {
var m__5497__auto__ = (cljs.core.async.tap_STAR_["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$3 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$3(m,ch,close_QMARK_) : m__5497__auto__.call(null,m,ch,close_QMARK_));
} else {
throw cljs.core.missing_protocol("Mult.tap*",m);
}
}
});
cljs.core.async.tap_STAR_ = (function cljs$core$async$tap_STAR_(m,ch,close_QMARK_){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mult$tap_STAR_$arity$3 == null)))))){
return m.cljs$core$async$Mult$tap_STAR_$arity$3(m,ch,close_QMARK_);
} else {
return cljs$core$async$Mult$tap_STAR_$dyn_18133(m,ch,close_QMARK_);
}
});

var cljs$core$async$Mult$untap_STAR_$dyn_18140 = (function (m,ch){
var x__5498__auto__ = (((m == null))?null:m);
var m__5499__auto__ = (cljs.core.async.untap_STAR_[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$2(m,ch) : m__5499__auto__.call(null,m,ch));
} else {
var m__5497__auto__ = (cljs.core.async.untap_STAR_["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$2(m,ch) : m__5497__auto__.call(null,m,ch));
} else {
throw cljs.core.missing_protocol("Mult.untap*",m);
}
}
});
cljs.core.async.untap_STAR_ = (function cljs$core$async$untap_STAR_(m,ch){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mult$untap_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mult$untap_STAR_$arity$2(m,ch);
} else {
return cljs$core$async$Mult$untap_STAR_$dyn_18140(m,ch);
}
});

var cljs$core$async$Mult$untap_all_STAR_$dyn_18146 = (function (m){
var x__5498__auto__ = (((m == null))?null:m);
var m__5499__auto__ = (cljs.core.async.untap_all_STAR_[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$1(m) : m__5499__auto__.call(null,m));
} else {
var m__5497__auto__ = (cljs.core.async.untap_all_STAR_["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$1(m) : m__5497__auto__.call(null,m));
} else {
throw cljs.core.missing_protocol("Mult.untap-all*",m);
}
}
});
cljs.core.async.untap_all_STAR_ = (function cljs$core$async$untap_all_STAR_(m){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mult$untap_all_STAR_$arity$1 == null)))))){
return m.cljs$core$async$Mult$untap_all_STAR_$arity$1(m);
} else {
return cljs$core$async$Mult$untap_all_STAR_$dyn_18146(m);
}
});


/**
* @constructor
 * @implements {cljs.core.async.Mult}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.async.Mux}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async15543 = (function (ch,cs,meta15544){
this.ch = ch;
this.cs = cs;
this.meta15544 = meta15544;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async15543.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_15545,meta15544__$1){
var self__ = this;
var _15545__$1 = this;
return (new cljs.core.async.t_cljs$core$async15543(self__.ch,self__.cs,meta15544__$1));
}));

(cljs.core.async.t_cljs$core$async15543.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_15545){
var self__ = this;
var _15545__$1 = this;
return self__.meta15544;
}));

(cljs.core.async.t_cljs$core$async15543.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async15543.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.ch;
}));

(cljs.core.async.t_cljs$core$async15543.prototype.cljs$core$async$Mult$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async15543.prototype.cljs$core$async$Mult$tap_STAR_$arity$3 = (function (_,ch__$1,close_QMARK_){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(self__.cs,cljs.core.assoc,ch__$1,close_QMARK_);

return null;
}));

(cljs.core.async.t_cljs$core$async15543.prototype.cljs$core$async$Mult$untap_STAR_$arity$2 = (function (_,ch__$1){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(self__.cs,cljs.core.dissoc,ch__$1);

return null;
}));

(cljs.core.async.t_cljs$core$async15543.prototype.cljs$core$async$Mult$untap_all_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_(self__.cs,cljs.core.PersistentArrayMap.EMPTY);

return null;
}));

(cljs.core.async.t_cljs$core$async15543.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"cs","cs",-117024463,null),new cljs.core.Symbol(null,"meta15544","meta15544",-19173186,null)], null);
}));

(cljs.core.async.t_cljs$core$async15543.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async15543.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async15543");

(cljs.core.async.t_cljs$core$async15543.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async15543");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async15543.
 */
cljs.core.async.__GT_t_cljs$core$async15543 = (function cljs$core$async$__GT_t_cljs$core$async15543(ch,cs,meta15544){
return (new cljs.core.async.t_cljs$core$async15543(ch,cs,meta15544));
});


/**
 * Creates and returns a mult(iple) of the supplied channel. Channels
 *   containing copies of the channel can be created with 'tap', and
 *   detached with 'untap'.
 * 
 *   Each item is distributed to all taps in parallel and synchronously,
 *   i.e. each tap must accept before the next item is distributed. Use
 *   buffering/windowing to prevent slow taps from holding up the mult.
 * 
 *   Items received when there are no taps get dropped.
 * 
 *   If a tap puts to a closed channel, it will be removed from the mult.
 */
cljs.core.async.mult = (function cljs$core$async$mult(ch){
var cs = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(cljs.core.PersistentArrayMap.EMPTY);
var m = (new cljs.core.async.t_cljs$core$async15543(ch,cs,cljs.core.PersistentArrayMap.EMPTY));
var dchan = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
var dctr = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(null);
var done = (function (_){
if((cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$2(dctr,cljs.core.dec) === (0))){
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2(dchan,true);
} else {
return null;
}
});
var c__14286__auto___18153 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = (function (state_15694){
var state_val_15696 = (state_15694[(1)]);
if((state_val_15696 === (7))){
var inst_15690 = (state_15694[(2)]);
var state_15694__$1 = state_15694;
var statearr_15702_18154 = state_15694__$1;
(statearr_15702_18154[(2)] = inst_15690);

(statearr_15702_18154[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (20))){
var inst_15594 = (state_15694[(7)]);
var inst_15606 = cljs.core.first(inst_15594);
var inst_15607 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_15606,(0),null);
var inst_15608 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_15606,(1),null);
var state_15694__$1 = (function (){var statearr_15704 = state_15694;
(statearr_15704[(8)] = inst_15607);

return statearr_15704;
})();
if(cljs.core.truth_(inst_15608)){
var statearr_15705_18155 = state_15694__$1;
(statearr_15705_18155[(1)] = (22));

} else {
var statearr_15706_18156 = state_15694__$1;
(statearr_15706_18156[(1)] = (23));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (27))){
var inst_15637 = (state_15694[(9)]);
var inst_15639 = (state_15694[(10)]);
var inst_15644 = (state_15694[(11)]);
var inst_15555 = (state_15694[(12)]);
var inst_15644__$1 = cljs.core._nth(inst_15637,inst_15639);
var inst_15645 = cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$3(inst_15644__$1,inst_15555,done);
var state_15694__$1 = (function (){var statearr_15708 = state_15694;
(statearr_15708[(11)] = inst_15644__$1);

return statearr_15708;
})();
if(cljs.core.truth_(inst_15645)){
var statearr_15709_18157 = state_15694__$1;
(statearr_15709_18157[(1)] = (30));

} else {
var statearr_15710_18158 = state_15694__$1;
(statearr_15710_18158[(1)] = (31));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (1))){
var state_15694__$1 = state_15694;
var statearr_15711_18159 = state_15694__$1;
(statearr_15711_18159[(2)] = null);

(statearr_15711_18159[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (24))){
var inst_15594 = (state_15694[(7)]);
var inst_15613 = (state_15694[(2)]);
var inst_15614 = cljs.core.next(inst_15594);
var inst_15565 = inst_15614;
var inst_15567 = null;
var inst_15568 = (0);
var inst_15569 = (0);
var state_15694__$1 = (function (){var statearr_15716 = state_15694;
(statearr_15716[(13)] = inst_15613);

(statearr_15716[(14)] = inst_15565);

(statearr_15716[(15)] = inst_15567);

(statearr_15716[(16)] = inst_15568);

(statearr_15716[(17)] = inst_15569);

return statearr_15716;
})();
var statearr_15717_18161 = state_15694__$1;
(statearr_15717_18161[(2)] = null);

(statearr_15717_18161[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (39))){
var state_15694__$1 = state_15694;
var statearr_15721_18162 = state_15694__$1;
(statearr_15721_18162[(2)] = null);

(statearr_15721_18162[(1)] = (41));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (4))){
var inst_15555 = (state_15694[(12)]);
var inst_15555__$1 = (state_15694[(2)]);
var inst_15557 = (inst_15555__$1 == null);
var state_15694__$1 = (function (){var statearr_15724 = state_15694;
(statearr_15724[(12)] = inst_15555__$1);

return statearr_15724;
})();
if(cljs.core.truth_(inst_15557)){
var statearr_15725_18163 = state_15694__$1;
(statearr_15725_18163[(1)] = (5));

} else {
var statearr_15726_18164 = state_15694__$1;
(statearr_15726_18164[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (15))){
var inst_15569 = (state_15694[(17)]);
var inst_15565 = (state_15694[(14)]);
var inst_15567 = (state_15694[(15)]);
var inst_15568 = (state_15694[(16)]);
var inst_15585 = (state_15694[(2)]);
var inst_15586 = (inst_15569 + (1));
var tmp15718 = inst_15568;
var tmp15719 = inst_15567;
var tmp15720 = inst_15565;
var inst_15565__$1 = tmp15720;
var inst_15567__$1 = tmp15719;
var inst_15568__$1 = tmp15718;
var inst_15569__$1 = inst_15586;
var state_15694__$1 = (function (){var statearr_15727 = state_15694;
(statearr_15727[(18)] = inst_15585);

(statearr_15727[(14)] = inst_15565__$1);

(statearr_15727[(15)] = inst_15567__$1);

(statearr_15727[(16)] = inst_15568__$1);

(statearr_15727[(17)] = inst_15569__$1);

return statearr_15727;
})();
var statearr_15728_18170 = state_15694__$1;
(statearr_15728_18170[(2)] = null);

(statearr_15728_18170[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (21))){
var inst_15617 = (state_15694[(2)]);
var state_15694__$1 = state_15694;
var statearr_15735_18171 = state_15694__$1;
(statearr_15735_18171[(2)] = inst_15617);

(statearr_15735_18171[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (31))){
var inst_15644 = (state_15694[(11)]);
var inst_15648 = m.cljs$core$async$Mult$untap_STAR_$arity$2(null,inst_15644);
var state_15694__$1 = state_15694;
var statearr_15738_18172 = state_15694__$1;
(statearr_15738_18172[(2)] = inst_15648);

(statearr_15738_18172[(1)] = (32));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (32))){
var inst_15639 = (state_15694[(10)]);
var inst_15636 = (state_15694[(19)]);
var inst_15637 = (state_15694[(9)]);
var inst_15638 = (state_15694[(20)]);
var inst_15650 = (state_15694[(2)]);
var inst_15651 = (inst_15639 + (1));
var tmp15732 = inst_15637;
var tmp15733 = inst_15638;
var tmp15734 = inst_15636;
var inst_15636__$1 = tmp15734;
var inst_15637__$1 = tmp15732;
var inst_15638__$1 = tmp15733;
var inst_15639__$1 = inst_15651;
var state_15694__$1 = (function (){var statearr_15746 = state_15694;
(statearr_15746[(21)] = inst_15650);

(statearr_15746[(19)] = inst_15636__$1);

(statearr_15746[(9)] = inst_15637__$1);

(statearr_15746[(20)] = inst_15638__$1);

(statearr_15746[(10)] = inst_15639__$1);

return statearr_15746;
})();
var statearr_15747_18177 = state_15694__$1;
(statearr_15747_18177[(2)] = null);

(statearr_15747_18177[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (40))){
var inst_15663 = (state_15694[(22)]);
var inst_15667 = m.cljs$core$async$Mult$untap_STAR_$arity$2(null,inst_15663);
var state_15694__$1 = state_15694;
var statearr_15748_18178 = state_15694__$1;
(statearr_15748_18178[(2)] = inst_15667);

(statearr_15748_18178[(1)] = (41));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (33))){
var inst_15654 = (state_15694[(23)]);
var inst_15656 = cljs.core.chunked_seq_QMARK_(inst_15654);
var state_15694__$1 = state_15694;
if(inst_15656){
var statearr_15749_18179 = state_15694__$1;
(statearr_15749_18179[(1)] = (36));

} else {
var statearr_15750_18180 = state_15694__$1;
(statearr_15750_18180[(1)] = (37));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (13))){
var inst_15579 = (state_15694[(24)]);
var inst_15582 = cljs.core.async.close_BANG_(inst_15579);
var state_15694__$1 = state_15694;
var statearr_15751_18185 = state_15694__$1;
(statearr_15751_18185[(2)] = inst_15582);

(statearr_15751_18185[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (22))){
var inst_15607 = (state_15694[(8)]);
var inst_15610 = cljs.core.async.close_BANG_(inst_15607);
var state_15694__$1 = state_15694;
var statearr_15752_18186 = state_15694__$1;
(statearr_15752_18186[(2)] = inst_15610);

(statearr_15752_18186[(1)] = (24));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (36))){
var inst_15654 = (state_15694[(23)]);
var inst_15658 = cljs.core.chunk_first(inst_15654);
var inst_15659 = cljs.core.chunk_rest(inst_15654);
var inst_15660 = cljs.core.count(inst_15658);
var inst_15636 = inst_15659;
var inst_15637 = inst_15658;
var inst_15638 = inst_15660;
var inst_15639 = (0);
var state_15694__$1 = (function (){var statearr_15753 = state_15694;
(statearr_15753[(19)] = inst_15636);

(statearr_15753[(9)] = inst_15637);

(statearr_15753[(20)] = inst_15638);

(statearr_15753[(10)] = inst_15639);

return statearr_15753;
})();
var statearr_15754_18196 = state_15694__$1;
(statearr_15754_18196[(2)] = null);

(statearr_15754_18196[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (41))){
var inst_15654 = (state_15694[(23)]);
var inst_15669 = (state_15694[(2)]);
var inst_15670 = cljs.core.next(inst_15654);
var inst_15636 = inst_15670;
var inst_15637 = null;
var inst_15638 = (0);
var inst_15639 = (0);
var state_15694__$1 = (function (){var statearr_15755 = state_15694;
(statearr_15755[(25)] = inst_15669);

(statearr_15755[(19)] = inst_15636);

(statearr_15755[(9)] = inst_15637);

(statearr_15755[(20)] = inst_15638);

(statearr_15755[(10)] = inst_15639);

return statearr_15755;
})();
var statearr_15757_18200 = state_15694__$1;
(statearr_15757_18200[(2)] = null);

(statearr_15757_18200[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (43))){
var state_15694__$1 = state_15694;
var statearr_15758_18201 = state_15694__$1;
(statearr_15758_18201[(2)] = null);

(statearr_15758_18201[(1)] = (44));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (29))){
var inst_15678 = (state_15694[(2)]);
var state_15694__$1 = state_15694;
var statearr_15760_18206 = state_15694__$1;
(statearr_15760_18206[(2)] = inst_15678);

(statearr_15760_18206[(1)] = (26));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (44))){
var inst_15687 = (state_15694[(2)]);
var state_15694__$1 = (function (){var statearr_15761 = state_15694;
(statearr_15761[(26)] = inst_15687);

return statearr_15761;
})();
var statearr_15762_18208 = state_15694__$1;
(statearr_15762_18208[(2)] = null);

(statearr_15762_18208[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (6))){
var inst_15628 = (state_15694[(27)]);
var inst_15627 = cljs.core.deref(cs);
var inst_15628__$1 = cljs.core.keys(inst_15627);
var inst_15629 = cljs.core.count(inst_15628__$1);
var inst_15630 = cljs.core.reset_BANG_(dctr,inst_15629);
var inst_15635 = cljs.core.seq(inst_15628__$1);
var inst_15636 = inst_15635;
var inst_15637 = null;
var inst_15638 = (0);
var inst_15639 = (0);
var state_15694__$1 = (function (){var statearr_15766 = state_15694;
(statearr_15766[(27)] = inst_15628__$1);

(statearr_15766[(28)] = inst_15630);

(statearr_15766[(19)] = inst_15636);

(statearr_15766[(9)] = inst_15637);

(statearr_15766[(20)] = inst_15638);

(statearr_15766[(10)] = inst_15639);

return statearr_15766;
})();
var statearr_15767_18222 = state_15694__$1;
(statearr_15767_18222[(2)] = null);

(statearr_15767_18222[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (28))){
var inst_15636 = (state_15694[(19)]);
var inst_15654 = (state_15694[(23)]);
var inst_15654__$1 = cljs.core.seq(inst_15636);
var state_15694__$1 = (function (){var statearr_15769 = state_15694;
(statearr_15769[(23)] = inst_15654__$1);

return statearr_15769;
})();
if(inst_15654__$1){
var statearr_15770_18234 = state_15694__$1;
(statearr_15770_18234[(1)] = (33));

} else {
var statearr_15771_18235 = state_15694__$1;
(statearr_15771_18235[(1)] = (34));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (25))){
var inst_15639 = (state_15694[(10)]);
var inst_15638 = (state_15694[(20)]);
var inst_15641 = (inst_15639 < inst_15638);
var inst_15642 = inst_15641;
var state_15694__$1 = state_15694;
if(cljs.core.truth_(inst_15642)){
var statearr_15772_18236 = state_15694__$1;
(statearr_15772_18236[(1)] = (27));

} else {
var statearr_15774_18237 = state_15694__$1;
(statearr_15774_18237[(1)] = (28));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (34))){
var state_15694__$1 = state_15694;
var statearr_15775_18238 = state_15694__$1;
(statearr_15775_18238[(2)] = null);

(statearr_15775_18238[(1)] = (35));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (17))){
var state_15694__$1 = state_15694;
var statearr_15780_18240 = state_15694__$1;
(statearr_15780_18240[(2)] = null);

(statearr_15780_18240[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (3))){
var inst_15692 = (state_15694[(2)]);
var state_15694__$1 = state_15694;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15694__$1,inst_15692);
} else {
if((state_val_15696 === (12))){
var inst_15622 = (state_15694[(2)]);
var state_15694__$1 = state_15694;
var statearr_15782_18242 = state_15694__$1;
(statearr_15782_18242[(2)] = inst_15622);

(statearr_15782_18242[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (2))){
var state_15694__$1 = state_15694;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15694__$1,(4),ch);
} else {
if((state_val_15696 === (23))){
var state_15694__$1 = state_15694;
var statearr_15783_18243 = state_15694__$1;
(statearr_15783_18243[(2)] = null);

(statearr_15783_18243[(1)] = (24));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (35))){
var inst_15676 = (state_15694[(2)]);
var state_15694__$1 = state_15694;
var statearr_15784_18244 = state_15694__$1;
(statearr_15784_18244[(2)] = inst_15676);

(statearr_15784_18244[(1)] = (29));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (19))){
var inst_15594 = (state_15694[(7)]);
var inst_15598 = cljs.core.chunk_first(inst_15594);
var inst_15599 = cljs.core.chunk_rest(inst_15594);
var inst_15600 = cljs.core.count(inst_15598);
var inst_15565 = inst_15599;
var inst_15567 = inst_15598;
var inst_15568 = inst_15600;
var inst_15569 = (0);
var state_15694__$1 = (function (){var statearr_15785 = state_15694;
(statearr_15785[(14)] = inst_15565);

(statearr_15785[(15)] = inst_15567);

(statearr_15785[(16)] = inst_15568);

(statearr_15785[(17)] = inst_15569);

return statearr_15785;
})();
var statearr_15786_18249 = state_15694__$1;
(statearr_15786_18249[(2)] = null);

(statearr_15786_18249[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (11))){
var inst_15565 = (state_15694[(14)]);
var inst_15594 = (state_15694[(7)]);
var inst_15594__$1 = cljs.core.seq(inst_15565);
var state_15694__$1 = (function (){var statearr_15788 = state_15694;
(statearr_15788[(7)] = inst_15594__$1);

return statearr_15788;
})();
if(inst_15594__$1){
var statearr_15789_18271 = state_15694__$1;
(statearr_15789_18271[(1)] = (16));

} else {
var statearr_15790_18272 = state_15694__$1;
(statearr_15790_18272[(1)] = (17));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (9))){
var inst_15624 = (state_15694[(2)]);
var state_15694__$1 = state_15694;
var statearr_15791_18282 = state_15694__$1;
(statearr_15791_18282[(2)] = inst_15624);

(statearr_15791_18282[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (5))){
var inst_15563 = cljs.core.deref(cs);
var inst_15564 = cljs.core.seq(inst_15563);
var inst_15565 = inst_15564;
var inst_15567 = null;
var inst_15568 = (0);
var inst_15569 = (0);
var state_15694__$1 = (function (){var statearr_15792 = state_15694;
(statearr_15792[(14)] = inst_15565);

(statearr_15792[(15)] = inst_15567);

(statearr_15792[(16)] = inst_15568);

(statearr_15792[(17)] = inst_15569);

return statearr_15792;
})();
var statearr_15793_18289 = state_15694__$1;
(statearr_15793_18289[(2)] = null);

(statearr_15793_18289[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (14))){
var state_15694__$1 = state_15694;
var statearr_15794_18290 = state_15694__$1;
(statearr_15794_18290[(2)] = null);

(statearr_15794_18290[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (45))){
var inst_15684 = (state_15694[(2)]);
var state_15694__$1 = state_15694;
var statearr_15796_18292 = state_15694__$1;
(statearr_15796_18292[(2)] = inst_15684);

(statearr_15796_18292[(1)] = (44));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (26))){
var inst_15628 = (state_15694[(27)]);
var inst_15680 = (state_15694[(2)]);
var inst_15681 = cljs.core.seq(inst_15628);
var state_15694__$1 = (function (){var statearr_15797 = state_15694;
(statearr_15797[(29)] = inst_15680);

return statearr_15797;
})();
if(inst_15681){
var statearr_15798_18293 = state_15694__$1;
(statearr_15798_18293[(1)] = (42));

} else {
var statearr_15799_18294 = state_15694__$1;
(statearr_15799_18294[(1)] = (43));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (16))){
var inst_15594 = (state_15694[(7)]);
var inst_15596 = cljs.core.chunked_seq_QMARK_(inst_15594);
var state_15694__$1 = state_15694;
if(inst_15596){
var statearr_15800_18300 = state_15694__$1;
(statearr_15800_18300[(1)] = (19));

} else {
var statearr_15801_18301 = state_15694__$1;
(statearr_15801_18301[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (38))){
var inst_15673 = (state_15694[(2)]);
var state_15694__$1 = state_15694;
var statearr_15802_18302 = state_15694__$1;
(statearr_15802_18302[(2)] = inst_15673);

(statearr_15802_18302[(1)] = (35));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (30))){
var state_15694__$1 = state_15694;
var statearr_15803_18304 = state_15694__$1;
(statearr_15803_18304[(2)] = null);

(statearr_15803_18304[(1)] = (32));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (10))){
var inst_15567 = (state_15694[(15)]);
var inst_15569 = (state_15694[(17)]);
var inst_15578 = cljs.core._nth(inst_15567,inst_15569);
var inst_15579 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_15578,(0),null);
var inst_15580 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_15578,(1),null);
var state_15694__$1 = (function (){var statearr_15805 = state_15694;
(statearr_15805[(24)] = inst_15579);

return statearr_15805;
})();
if(cljs.core.truth_(inst_15580)){
var statearr_15806_18305 = state_15694__$1;
(statearr_15806_18305[(1)] = (13));

} else {
var statearr_15807_18306 = state_15694__$1;
(statearr_15807_18306[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (18))){
var inst_15620 = (state_15694[(2)]);
var state_15694__$1 = state_15694;
var statearr_15808_18308 = state_15694__$1;
(statearr_15808_18308[(2)] = inst_15620);

(statearr_15808_18308[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (42))){
var state_15694__$1 = state_15694;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15694__$1,(45),dchan);
} else {
if((state_val_15696 === (37))){
var inst_15654 = (state_15694[(23)]);
var inst_15663 = (state_15694[(22)]);
var inst_15555 = (state_15694[(12)]);
var inst_15663__$1 = cljs.core.first(inst_15654);
var inst_15664 = cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$3(inst_15663__$1,inst_15555,done);
var state_15694__$1 = (function (){var statearr_15809 = state_15694;
(statearr_15809[(22)] = inst_15663__$1);

return statearr_15809;
})();
if(cljs.core.truth_(inst_15664)){
var statearr_15811_18313 = state_15694__$1;
(statearr_15811_18313[(1)] = (39));

} else {
var statearr_15812_18314 = state_15694__$1;
(statearr_15812_18314[(1)] = (40));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15696 === (8))){
var inst_15569 = (state_15694[(17)]);
var inst_15568 = (state_15694[(16)]);
var inst_15571 = (inst_15569 < inst_15568);
var inst_15572 = inst_15571;
var state_15694__$1 = state_15694;
if(cljs.core.truth_(inst_15572)){
var statearr_15814_18315 = state_15694__$1;
(statearr_15814_18315[(1)] = (10));

} else {
var statearr_15818_18320 = state_15694__$1;
(statearr_15818_18320[(1)] = (11));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$mult_$_state_machine__14092__auto__ = null;
var cljs$core$async$mult_$_state_machine__14092__auto____0 = (function (){
var statearr_15820 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_15820[(0)] = cljs$core$async$mult_$_state_machine__14092__auto__);

(statearr_15820[(1)] = (1));

return statearr_15820;
});
var cljs$core$async$mult_$_state_machine__14092__auto____1 = (function (state_15694){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_15694);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e15821){var ex__14095__auto__ = e15821;
var statearr_15823_18331 = state_15694;
(statearr_15823_18331[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_15694[(4)]))){
var statearr_15825_18332 = state_15694;
(statearr_15825_18332[(1)] = cljs.core.first((state_15694[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18333 = state_15694;
state_15694 = G__18333;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$mult_$_state_machine__14092__auto__ = function(state_15694){
switch(arguments.length){
case 0:
return cljs$core$async$mult_$_state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$mult_$_state_machine__14092__auto____1.call(this,state_15694);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$mult_$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mult_$_state_machine__14092__auto____0;
cljs$core$async$mult_$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mult_$_state_machine__14092__auto____1;
return cljs$core$async$mult_$_state_machine__14092__auto__;
})()
})();
var state__14288__auto__ = (function (){var statearr_15828 = f__14287__auto__();
(statearr_15828[(6)] = c__14286__auto___18153);

return statearr_15828;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
}));


return m;
});
/**
 * Copies the mult source onto the supplied channel.
 * 
 *   By default the channel will be closed when the source closes,
 *   but can be determined by the close? parameter.
 */
cljs.core.async.tap = (function cljs$core$async$tap(var_args){
var G__15831 = arguments.length;
switch (G__15831) {
case 2:
return cljs.core.async.tap.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.tap.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.tap.cljs$core$IFn$_invoke$arity$2 = (function (mult,ch){
return cljs.core.async.tap.cljs$core$IFn$_invoke$arity$3(mult,ch,true);
}));

(cljs.core.async.tap.cljs$core$IFn$_invoke$arity$3 = (function (mult,ch,close_QMARK_){
cljs.core.async.tap_STAR_(mult,ch,close_QMARK_);

return ch;
}));

(cljs.core.async.tap.cljs$lang$maxFixedArity = 3);

/**
 * Disconnects a target channel from a mult
 */
cljs.core.async.untap = (function cljs$core$async$untap(mult,ch){
return cljs.core.async.untap_STAR_(mult,ch);
});
/**
 * Disconnects all target channels from a mult
 */
cljs.core.async.untap_all = (function cljs$core$async$untap_all(mult){
return cljs.core.async.untap_all_STAR_(mult);
});

/**
 * @interface
 */
cljs.core.async.Mix = function(){};

var cljs$core$async$Mix$admix_STAR_$dyn_18347 = (function (m,ch){
var x__5498__auto__ = (((m == null))?null:m);
var m__5499__auto__ = (cljs.core.async.admix_STAR_[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$2(m,ch) : m__5499__auto__.call(null,m,ch));
} else {
var m__5497__auto__ = (cljs.core.async.admix_STAR_["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$2(m,ch) : m__5497__auto__.call(null,m,ch));
} else {
throw cljs.core.missing_protocol("Mix.admix*",m);
}
}
});
cljs.core.async.admix_STAR_ = (function cljs$core$async$admix_STAR_(m,ch){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$admix_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mix$admix_STAR_$arity$2(m,ch);
} else {
return cljs$core$async$Mix$admix_STAR_$dyn_18347(m,ch);
}
});

var cljs$core$async$Mix$unmix_STAR_$dyn_18351 = (function (m,ch){
var x__5498__auto__ = (((m == null))?null:m);
var m__5499__auto__ = (cljs.core.async.unmix_STAR_[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$2(m,ch) : m__5499__auto__.call(null,m,ch));
} else {
var m__5497__auto__ = (cljs.core.async.unmix_STAR_["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$2(m,ch) : m__5497__auto__.call(null,m,ch));
} else {
throw cljs.core.missing_protocol("Mix.unmix*",m);
}
}
});
cljs.core.async.unmix_STAR_ = (function cljs$core$async$unmix_STAR_(m,ch){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$unmix_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mix$unmix_STAR_$arity$2(m,ch);
} else {
return cljs$core$async$Mix$unmix_STAR_$dyn_18351(m,ch);
}
});

var cljs$core$async$Mix$unmix_all_STAR_$dyn_18363 = (function (m){
var x__5498__auto__ = (((m == null))?null:m);
var m__5499__auto__ = (cljs.core.async.unmix_all_STAR_[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$1(m) : m__5499__auto__.call(null,m));
} else {
var m__5497__auto__ = (cljs.core.async.unmix_all_STAR_["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$1(m) : m__5497__auto__.call(null,m));
} else {
throw cljs.core.missing_protocol("Mix.unmix-all*",m);
}
}
});
cljs.core.async.unmix_all_STAR_ = (function cljs$core$async$unmix_all_STAR_(m){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$unmix_all_STAR_$arity$1 == null)))))){
return m.cljs$core$async$Mix$unmix_all_STAR_$arity$1(m);
} else {
return cljs$core$async$Mix$unmix_all_STAR_$dyn_18363(m);
}
});

var cljs$core$async$Mix$toggle_STAR_$dyn_18368 = (function (m,state_map){
var x__5498__auto__ = (((m == null))?null:m);
var m__5499__auto__ = (cljs.core.async.toggle_STAR_[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$2(m,state_map) : m__5499__auto__.call(null,m,state_map));
} else {
var m__5497__auto__ = (cljs.core.async.toggle_STAR_["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$2(m,state_map) : m__5497__auto__.call(null,m,state_map));
} else {
throw cljs.core.missing_protocol("Mix.toggle*",m);
}
}
});
cljs.core.async.toggle_STAR_ = (function cljs$core$async$toggle_STAR_(m,state_map){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$toggle_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mix$toggle_STAR_$arity$2(m,state_map);
} else {
return cljs$core$async$Mix$toggle_STAR_$dyn_18368(m,state_map);
}
});

var cljs$core$async$Mix$solo_mode_STAR_$dyn_18370 = (function (m,mode){
var x__5498__auto__ = (((m == null))?null:m);
var m__5499__auto__ = (cljs.core.async.solo_mode_STAR_[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$2(m,mode) : m__5499__auto__.call(null,m,mode));
} else {
var m__5497__auto__ = (cljs.core.async.solo_mode_STAR_["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$2(m,mode) : m__5497__auto__.call(null,m,mode));
} else {
throw cljs.core.missing_protocol("Mix.solo-mode*",m);
}
}
});
cljs.core.async.solo_mode_STAR_ = (function cljs$core$async$solo_mode_STAR_(m,mode){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$solo_mode_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mix$solo_mode_STAR_$arity$2(m,mode);
} else {
return cljs$core$async$Mix$solo_mode_STAR_$dyn_18370(m,mode);
}
});

cljs.core.async.ioc_alts_BANG_ = (function cljs$core$async$ioc_alts_BANG_(var_args){
var args__5882__auto__ = [];
var len__5876__auto___18371 = arguments.length;
var i__5877__auto___18372 = (0);
while(true){
if((i__5877__auto___18372 < len__5876__auto___18371)){
args__5882__auto__.push((arguments[i__5877__auto___18372]));

var G__18373 = (i__5877__auto___18372 + (1));
i__5877__auto___18372 = G__18373;
continue;
} else {
}
break;
}

var argseq__5883__auto__ = ((((3) < args__5882__auto__.length))?(new cljs.core.IndexedSeq(args__5882__auto__.slice((3)),(0),null)):null);
return cljs.core.async.ioc_alts_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__5883__auto__);
});

(cljs.core.async.ioc_alts_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (state,cont_block,ports,p__15864){
var map__15865 = p__15864;
var map__15865__$1 = cljs.core.__destructure_map(map__15865);
var opts = map__15865__$1;
var statearr_15868_18374 = state;
(statearr_15868_18374[(1)] = cont_block);


var temp__5823__auto__ = cljs.core.async.do_alts((function (val){
var statearr_15869_18375 = state;
(statearr_15869_18375[(2)] = val);


return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state);
}),ports,opts);
if(cljs.core.truth_(temp__5823__auto__)){
var cb = temp__5823__auto__;
var statearr_15872_18377 = state;
(statearr_15872_18377[(2)] = cljs.core.deref(cb));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}));

(cljs.core.async.ioc_alts_BANG_.cljs$lang$maxFixedArity = (3));

/** @this {Function} */
(cljs.core.async.ioc_alts_BANG_.cljs$lang$applyTo = (function (seq15855){
var G__15856 = cljs.core.first(seq15855);
var seq15855__$1 = cljs.core.next(seq15855);
var G__15857 = cljs.core.first(seq15855__$1);
var seq15855__$2 = cljs.core.next(seq15855__$1);
var G__15858 = cljs.core.first(seq15855__$2);
var seq15855__$3 = cljs.core.next(seq15855__$2);
var self__5861__auto__ = this;
return self__5861__auto__.cljs$core$IFn$_invoke$arity$variadic(G__15856,G__15857,G__15858,seq15855__$3);
}));


/**
* @constructor
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.async.Mix}
 * @implements {cljs.core.async.Mux}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async15882 = (function (change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,meta15883){
this.change = change;
this.solo_mode = solo_mode;
this.pick = pick;
this.cs = cs;
this.calc_state = calc_state;
this.out = out;
this.changed = changed;
this.solo_modes = solo_modes;
this.attrs = attrs;
this.meta15883 = meta15883;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async15882.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_15884,meta15883__$1){
var self__ = this;
var _15884__$1 = this;
return (new cljs.core.async.t_cljs$core$async15882(self__.change,self__.solo_mode,self__.pick,self__.cs,self__.calc_state,self__.out,self__.changed,self__.solo_modes,self__.attrs,meta15883__$1));
}));

(cljs.core.async.t_cljs$core$async15882.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_15884){
var self__ = this;
var _15884__$1 = this;
return self__.meta15883;
}));

(cljs.core.async.t_cljs$core$async15882.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async15882.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.out;
}));

(cljs.core.async.t_cljs$core$async15882.prototype.cljs$core$async$Mix$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async15882.prototype.cljs$core$async$Mix$admix_STAR_$arity$2 = (function (_,ch){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(self__.cs,cljs.core.assoc,ch,cljs.core.PersistentArrayMap.EMPTY);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async15882.prototype.cljs$core$async$Mix$unmix_STAR_$arity$2 = (function (_,ch){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(self__.cs,cljs.core.dissoc,ch);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async15882.prototype.cljs$core$async$Mix$unmix_all_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_(self__.cs,cljs.core.PersistentArrayMap.EMPTY);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async15882.prototype.cljs$core$async$Mix$toggle_STAR_$arity$2 = (function (_,state_map){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(self__.cs,cljs.core.partial.cljs$core$IFn$_invoke$arity$2(cljs.core.merge_with,cljs.core.merge),state_map);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async15882.prototype.cljs$core$async$Mix$solo_mode_STAR_$arity$2 = (function (_,mode){
var self__ = this;
var ___$1 = this;
if(cljs.core.truth_((self__.solo_modes.cljs$core$IFn$_invoke$arity$1 ? self__.solo_modes.cljs$core$IFn$_invoke$arity$1(mode) : self__.solo_modes.call(null,mode)))){
} else {
throw (new Error((""+"Assert failed: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1((""+"mode must be one of: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(self__.solo_modes)))+"\n"+"(solo-modes mode)")));
}

cljs.core.reset_BANG_(self__.solo_mode,mode);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async15882.getBasis = (function (){
return new cljs.core.PersistentVector(null, 10, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"change","change",477485025,null),new cljs.core.Symbol(null,"solo-mode","solo-mode",2031788074,null),new cljs.core.Symbol(null,"pick","pick",1300068175,null),new cljs.core.Symbol(null,"cs","cs",-117024463,null),new cljs.core.Symbol(null,"calc-state","calc-state",-349968968,null),new cljs.core.Symbol(null,"out","out",729986010,null),new cljs.core.Symbol(null,"changed","changed",-2083710852,null),new cljs.core.Symbol(null,"solo-modes","solo-modes",882180540,null),new cljs.core.Symbol(null,"attrs","attrs",-450137186,null),new cljs.core.Symbol(null,"meta15883","meta15883",1797290796,null)], null);
}));

(cljs.core.async.t_cljs$core$async15882.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async15882.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async15882");

(cljs.core.async.t_cljs$core$async15882.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async15882");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async15882.
 */
cljs.core.async.__GT_t_cljs$core$async15882 = (function cljs$core$async$__GT_t_cljs$core$async15882(change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,meta15883){
return (new cljs.core.async.t_cljs$core$async15882(change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,meta15883));
});


/**
 * Creates and returns a mix of one or more input channels which will
 *   be put on the supplied out channel. Input sources can be added to
 *   the mix with 'admix', and removed with 'unmix'. A mix supports
 *   soloing, muting and pausing multiple inputs atomically using
 *   'toggle', and can solo using either muting or pausing as determined
 *   by 'solo-mode'.
 * 
 *   Each channel can have zero or more boolean modes set via 'toggle':
 * 
 *   :solo - when true, only this (ond other soloed) channel(s) will appear
 *        in the mix output channel. :mute and :pause states of soloed
 *        channels are ignored. If solo-mode is :mute, non-soloed
 *        channels are muted, if :pause, non-soloed channels are
 *        paused.
 * 
 *   :mute - muted channels will have their contents consumed but not included in the mix
 *   :pause - paused channels will not have their contents consumed (and thus also not included in the mix)
 */
cljs.core.async.mix = (function cljs$core$async$mix(out){
var cs = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(cljs.core.PersistentArrayMap.EMPTY);
var solo_modes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"pause","pause",-2095325672),null,new cljs.core.Keyword(null,"mute","mute",1151223646),null], null), null);
var attrs = cljs.core.conj.cljs$core$IFn$_invoke$arity$2(solo_modes,new cljs.core.Keyword(null,"solo","solo",-316350075));
var solo_mode = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"mute","mute",1151223646));
var change = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(cljs.core.async.sliding_buffer((1)));
var changed = (function (){
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2(change,true);
});
var pick = (function (attr,chs){
return cljs.core.reduce_kv((function (ret,c,v){
if(cljs.core.truth_((attr.cljs$core$IFn$_invoke$arity$1 ? attr.cljs$core$IFn$_invoke$arity$1(v) : attr.call(null,v)))){
return cljs.core.conj.cljs$core$IFn$_invoke$arity$2(ret,c);
} else {
return ret;
}
}),cljs.core.PersistentHashSet.EMPTY,chs);
});
var calc_state = (function (){
var chs = cljs.core.deref(cs);
var mode = cljs.core.deref(solo_mode);
var solos = pick(new cljs.core.Keyword(null,"solo","solo",-316350075),chs);
var pauses = pick(new cljs.core.Keyword(null,"pause","pause",-2095325672),chs);
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"solos","solos",1441458643),solos,new cljs.core.Keyword(null,"mutes","mutes",1068806309),pick(new cljs.core.Keyword(null,"mute","mute",1151223646),chs),new cljs.core.Keyword(null,"reads","reads",-1215067361),cljs.core.conj.cljs$core$IFn$_invoke$arity$2(((((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(mode,new cljs.core.Keyword(null,"pause","pause",-2095325672))) && (cljs.core.seq(solos))))?cljs.core.vec(solos):cljs.core.vec(cljs.core.remove.cljs$core$IFn$_invoke$arity$2(pauses,cljs.core.keys(chs)))),change)], null);
});
var m = (new cljs.core.async.t_cljs$core$async15882(change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,cljs.core.PersistentArrayMap.EMPTY));
var c__14286__auto___18399 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = (function (state_15980){
var state_val_15981 = (state_15980[(1)]);
if((state_val_15981 === (7))){
var inst_15929 = (state_15980[(2)]);
var state_15980__$1 = state_15980;
if(cljs.core.truth_(inst_15929)){
var statearr_15984_18400 = state_15980__$1;
(statearr_15984_18400[(1)] = (8));

} else {
var statearr_15985_18401 = state_15980__$1;
(statearr_15985_18401[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (20))){
var inst_15920 = (state_15980[(7)]);
var state_15980__$1 = state_15980;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_15980__$1,(23),out,inst_15920);
} else {
if((state_val_15981 === (1))){
var inst_15900 = calc_state();
var inst_15901 = cljs.core.__destructure_map(inst_15900);
var inst_15903 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_15901,new cljs.core.Keyword(null,"solos","solos",1441458643));
var inst_15904 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_15901,new cljs.core.Keyword(null,"mutes","mutes",1068806309));
var inst_15905 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_15901,new cljs.core.Keyword(null,"reads","reads",-1215067361));
var inst_15906 = inst_15900;
var state_15980__$1 = (function (){var statearr_15998 = state_15980;
(statearr_15998[(8)] = inst_15903);

(statearr_15998[(9)] = inst_15904);

(statearr_15998[(10)] = inst_15905);

(statearr_15998[(11)] = inst_15906);

return statearr_15998;
})();
var statearr_16004_18403 = state_15980__$1;
(statearr_16004_18403[(2)] = null);

(statearr_16004_18403[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (24))){
var inst_15910 = (state_15980[(12)]);
var inst_15906 = inst_15910;
var state_15980__$1 = (function (){var statearr_16010 = state_15980;
(statearr_16010[(11)] = inst_15906);

return statearr_16010;
})();
var statearr_16013_18406 = state_15980__$1;
(statearr_16013_18406[(2)] = null);

(statearr_16013_18406[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (4))){
var inst_15920 = (state_15980[(7)]);
var inst_15924 = (state_15980[(13)]);
var inst_15919 = (state_15980[(2)]);
var inst_15920__$1 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_15919,(0),null);
var inst_15921 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_15919,(1),null);
var inst_15924__$1 = (inst_15920__$1 == null);
var state_15980__$1 = (function (){var statearr_16026 = state_15980;
(statearr_16026[(7)] = inst_15920__$1);

(statearr_16026[(14)] = inst_15921);

(statearr_16026[(13)] = inst_15924__$1);

return statearr_16026;
})();
if(cljs.core.truth_(inst_15924__$1)){
var statearr_16032_18419 = state_15980__$1;
(statearr_16032_18419[(1)] = (5));

} else {
var statearr_16034_18427 = state_15980__$1;
(statearr_16034_18427[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (15))){
var inst_15912 = (state_15980[(15)]);
var inst_15944 = (state_15980[(16)]);
var inst_15944__$1 = cljs.core.empty_QMARK_(inst_15912);
var state_15980__$1 = (function (){var statearr_16038 = state_15980;
(statearr_16038[(16)] = inst_15944__$1);

return statearr_16038;
})();
if(inst_15944__$1){
var statearr_16043_18435 = state_15980__$1;
(statearr_16043_18435[(1)] = (17));

} else {
var statearr_16046_18436 = state_15980__$1;
(statearr_16046_18436[(1)] = (18));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (21))){
var inst_15910 = (state_15980[(12)]);
var inst_15906 = inst_15910;
var state_15980__$1 = (function (){var statearr_16048 = state_15980;
(statearr_16048[(11)] = inst_15906);

return statearr_16048;
})();
var statearr_16049_18440 = state_15980__$1;
(statearr_16049_18440[(2)] = null);

(statearr_16049_18440[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (13))){
var inst_15936 = (state_15980[(2)]);
var inst_15937 = calc_state();
var inst_15906 = inst_15937;
var state_15980__$1 = (function (){var statearr_16051 = state_15980;
(statearr_16051[(17)] = inst_15936);

(statearr_16051[(11)] = inst_15906);

return statearr_16051;
})();
var statearr_16053_18446 = state_15980__$1;
(statearr_16053_18446[(2)] = null);

(statearr_16053_18446[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (22))){
var inst_15968 = (state_15980[(2)]);
var state_15980__$1 = state_15980;
var statearr_16054_18448 = state_15980__$1;
(statearr_16054_18448[(2)] = inst_15968);

(statearr_16054_18448[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (6))){
var inst_15921 = (state_15980[(14)]);
var inst_15927 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(inst_15921,change);
var state_15980__$1 = state_15980;
var statearr_16059_18449 = state_15980__$1;
(statearr_16059_18449[(2)] = inst_15927);

(statearr_16059_18449[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (25))){
var state_15980__$1 = state_15980;
var statearr_16061_18450 = state_15980__$1;
(statearr_16061_18450[(2)] = null);

(statearr_16061_18450[(1)] = (26));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (17))){
var inst_15913 = (state_15980[(18)]);
var inst_15921 = (state_15980[(14)]);
var inst_15946 = (inst_15913.cljs$core$IFn$_invoke$arity$1 ? inst_15913.cljs$core$IFn$_invoke$arity$1(inst_15921) : inst_15913.call(null,inst_15921));
var inst_15947 = cljs.core.not(inst_15946);
var state_15980__$1 = state_15980;
var statearr_16068_18453 = state_15980__$1;
(statearr_16068_18453[(2)] = inst_15947);

(statearr_16068_18453[(1)] = (19));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (3))){
var inst_15973 = (state_15980[(2)]);
var state_15980__$1 = state_15980;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15980__$1,inst_15973);
} else {
if((state_val_15981 === (12))){
var state_15980__$1 = state_15980;
var statearr_16080_18454 = state_15980__$1;
(statearr_16080_18454[(2)] = null);

(statearr_16080_18454[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (2))){
var inst_15906 = (state_15980[(11)]);
var inst_15910 = (state_15980[(12)]);
var inst_15910__$1 = cljs.core.__destructure_map(inst_15906);
var inst_15912 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_15910__$1,new cljs.core.Keyword(null,"solos","solos",1441458643));
var inst_15913 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_15910__$1,new cljs.core.Keyword(null,"mutes","mutes",1068806309));
var inst_15914 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_15910__$1,new cljs.core.Keyword(null,"reads","reads",-1215067361));
var state_15980__$1 = (function (){var statearr_16090 = state_15980;
(statearr_16090[(12)] = inst_15910__$1);

(statearr_16090[(15)] = inst_15912);

(statearr_16090[(18)] = inst_15913);

return statearr_16090;
})();
return cljs.core.async.ioc_alts_BANG_(state_15980__$1,(4),inst_15914);
} else {
if((state_val_15981 === (23))){
var inst_15959 = (state_15980[(2)]);
var state_15980__$1 = state_15980;
if(cljs.core.truth_(inst_15959)){
var statearr_16094_18457 = state_15980__$1;
(statearr_16094_18457[(1)] = (24));

} else {
var statearr_16098_18458 = state_15980__$1;
(statearr_16098_18458[(1)] = (25));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (19))){
var inst_15950 = (state_15980[(2)]);
var state_15980__$1 = state_15980;
var statearr_16100_18459 = state_15980__$1;
(statearr_16100_18459[(2)] = inst_15950);

(statearr_16100_18459[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (11))){
var inst_15921 = (state_15980[(14)]);
var inst_15933 = cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(cs,cljs.core.dissoc,inst_15921);
var state_15980__$1 = state_15980;
var statearr_16105_18460 = state_15980__$1;
(statearr_16105_18460[(2)] = inst_15933);

(statearr_16105_18460[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (9))){
var inst_15912 = (state_15980[(15)]);
var inst_15921 = (state_15980[(14)]);
var inst_15941 = (state_15980[(19)]);
var inst_15941__$1 = (inst_15912.cljs$core$IFn$_invoke$arity$1 ? inst_15912.cljs$core$IFn$_invoke$arity$1(inst_15921) : inst_15912.call(null,inst_15921));
var state_15980__$1 = (function (){var statearr_16107 = state_15980;
(statearr_16107[(19)] = inst_15941__$1);

return statearr_16107;
})();
if(cljs.core.truth_(inst_15941__$1)){
var statearr_16108_18461 = state_15980__$1;
(statearr_16108_18461[(1)] = (14));

} else {
var statearr_16110_18463 = state_15980__$1;
(statearr_16110_18463[(1)] = (15));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (5))){
var inst_15924 = (state_15980[(13)]);
var state_15980__$1 = state_15980;
var statearr_16116_18465 = state_15980__$1;
(statearr_16116_18465[(2)] = inst_15924);

(statearr_16116_18465[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (14))){
var inst_15941 = (state_15980[(19)]);
var state_15980__$1 = state_15980;
var statearr_16118_18466 = state_15980__$1;
(statearr_16118_18466[(2)] = inst_15941);

(statearr_16118_18466[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (26))){
var inst_15964 = (state_15980[(2)]);
var state_15980__$1 = state_15980;
var statearr_16119_18467 = state_15980__$1;
(statearr_16119_18467[(2)] = inst_15964);

(statearr_16119_18467[(1)] = (22));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (16))){
var inst_15952 = (state_15980[(2)]);
var state_15980__$1 = state_15980;
if(cljs.core.truth_(inst_15952)){
var statearr_16122_18468 = state_15980__$1;
(statearr_16122_18468[(1)] = (20));

} else {
var statearr_16123_18469 = state_15980__$1;
(statearr_16123_18469[(1)] = (21));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (10))){
var inst_15970 = (state_15980[(2)]);
var state_15980__$1 = state_15980;
var statearr_16125_18470 = state_15980__$1;
(statearr_16125_18470[(2)] = inst_15970);

(statearr_16125_18470[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (18))){
var inst_15944 = (state_15980[(16)]);
var state_15980__$1 = state_15980;
var statearr_16128_18472 = state_15980__$1;
(statearr_16128_18472[(2)] = inst_15944);

(statearr_16128_18472[(1)] = (19));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15981 === (8))){
var inst_15920 = (state_15980[(7)]);
var inst_15931 = (inst_15920 == null);
var state_15980__$1 = state_15980;
if(cljs.core.truth_(inst_15931)){
var statearr_16129_18473 = state_15980__$1;
(statearr_16129_18473[(1)] = (11));

} else {
var statearr_16130_18474 = state_15980__$1;
(statearr_16130_18474[(1)] = (12));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$mix_$_state_machine__14092__auto__ = null;
var cljs$core$async$mix_$_state_machine__14092__auto____0 = (function (){
var statearr_16136 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_16136[(0)] = cljs$core$async$mix_$_state_machine__14092__auto__);

(statearr_16136[(1)] = (1));

return statearr_16136;
});
var cljs$core$async$mix_$_state_machine__14092__auto____1 = (function (state_15980){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_15980);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e16138){var ex__14095__auto__ = e16138;
var statearr_16139_18475 = state_15980;
(statearr_16139_18475[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_15980[(4)]))){
var statearr_16140_18476 = state_15980;
(statearr_16140_18476[(1)] = cljs.core.first((state_15980[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18479 = state_15980;
state_15980 = G__18479;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$mix_$_state_machine__14092__auto__ = function(state_15980){
switch(arguments.length){
case 0:
return cljs$core$async$mix_$_state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$mix_$_state_machine__14092__auto____1.call(this,state_15980);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$mix_$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mix_$_state_machine__14092__auto____0;
cljs$core$async$mix_$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mix_$_state_machine__14092__auto____1;
return cljs$core$async$mix_$_state_machine__14092__auto__;
})()
})();
var state__14288__auto__ = (function (){var statearr_16143 = f__14287__auto__();
(statearr_16143[(6)] = c__14286__auto___18399);

return statearr_16143;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
}));


return m;
});
/**
 * Adds ch as an input to the mix
 */
cljs.core.async.admix = (function cljs$core$async$admix(mix,ch){
return cljs.core.async.admix_STAR_(mix,ch);
});
/**
 * Removes ch as an input to the mix
 */
cljs.core.async.unmix = (function cljs$core$async$unmix(mix,ch){
return cljs.core.async.unmix_STAR_(mix,ch);
});
/**
 * removes all inputs from the mix
 */
cljs.core.async.unmix_all = (function cljs$core$async$unmix_all(mix){
return cljs.core.async.unmix_all_STAR_(mix);
});
/**
 * Atomically sets the state(s) of one or more channels in a mix. The
 *   state map is a map of channels -> channel-state-map. A
 *   channel-state-map is a map of attrs -> boolean, where attr is one or
 *   more of :mute, :pause or :solo. Any states supplied are merged with
 *   the current state.
 * 
 *   Note that channels can be added to a mix via toggle, which can be
 *   used to add channels in a particular (e.g. paused) state.
 */
cljs.core.async.toggle = (function cljs$core$async$toggle(mix,state_map){
return cljs.core.async.toggle_STAR_(mix,state_map);
});
/**
 * Sets the solo mode of the mix. mode must be one of :mute or :pause
 */
cljs.core.async.solo_mode = (function cljs$core$async$solo_mode(mix,mode){
return cljs.core.async.solo_mode_STAR_(mix,mode);
});

/**
 * @interface
 */
cljs.core.async.Pub = function(){};

var cljs$core$async$Pub$sub_STAR_$dyn_18489 = (function (p,v,ch,close_QMARK_){
var x__5498__auto__ = (((p == null))?null:p);
var m__5499__auto__ = (cljs.core.async.sub_STAR_[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$4 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$4(p,v,ch,close_QMARK_) : m__5499__auto__.call(null,p,v,ch,close_QMARK_));
} else {
var m__5497__auto__ = (cljs.core.async.sub_STAR_["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$4 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$4(p,v,ch,close_QMARK_) : m__5497__auto__.call(null,p,v,ch,close_QMARK_));
} else {
throw cljs.core.missing_protocol("Pub.sub*",p);
}
}
});
cljs.core.async.sub_STAR_ = (function cljs$core$async$sub_STAR_(p,v,ch,close_QMARK_){
if((((!((p == null)))) && ((!((p.cljs$core$async$Pub$sub_STAR_$arity$4 == null)))))){
return p.cljs$core$async$Pub$sub_STAR_$arity$4(p,v,ch,close_QMARK_);
} else {
return cljs$core$async$Pub$sub_STAR_$dyn_18489(p,v,ch,close_QMARK_);
}
});

var cljs$core$async$Pub$unsub_STAR_$dyn_18497 = (function (p,v,ch){
var x__5498__auto__ = (((p == null))?null:p);
var m__5499__auto__ = (cljs.core.async.unsub_STAR_[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$3 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$3(p,v,ch) : m__5499__auto__.call(null,p,v,ch));
} else {
var m__5497__auto__ = (cljs.core.async.unsub_STAR_["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$3 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$3(p,v,ch) : m__5497__auto__.call(null,p,v,ch));
} else {
throw cljs.core.missing_protocol("Pub.unsub*",p);
}
}
});
cljs.core.async.unsub_STAR_ = (function cljs$core$async$unsub_STAR_(p,v,ch){
if((((!((p == null)))) && ((!((p.cljs$core$async$Pub$unsub_STAR_$arity$3 == null)))))){
return p.cljs$core$async$Pub$unsub_STAR_$arity$3(p,v,ch);
} else {
return cljs$core$async$Pub$unsub_STAR_$dyn_18497(p,v,ch);
}
});

var cljs$core$async$Pub$unsub_all_STAR_$dyn_18500 = (function() {
var G__18501 = null;
var G__18501__1 = (function (p){
var x__5498__auto__ = (((p == null))?null:p);
var m__5499__auto__ = (cljs.core.async.unsub_all_STAR_[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$1(p) : m__5499__auto__.call(null,p));
} else {
var m__5497__auto__ = (cljs.core.async.unsub_all_STAR_["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$1 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$1(p) : m__5497__auto__.call(null,p));
} else {
throw cljs.core.missing_protocol("Pub.unsub-all*",p);
}
}
});
var G__18501__2 = (function (p,v){
var x__5498__auto__ = (((p == null))?null:p);
var m__5499__auto__ = (cljs.core.async.unsub_all_STAR_[goog.typeOf(x__5498__auto__)]);
if((!((m__5499__auto__ == null)))){
return (m__5499__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5499__auto__.cljs$core$IFn$_invoke$arity$2(p,v) : m__5499__auto__.call(null,p,v));
} else {
var m__5497__auto__ = (cljs.core.async.unsub_all_STAR_["_"]);
if((!((m__5497__auto__ == null)))){
return (m__5497__auto__.cljs$core$IFn$_invoke$arity$2 ? m__5497__auto__.cljs$core$IFn$_invoke$arity$2(p,v) : m__5497__auto__.call(null,p,v));
} else {
throw cljs.core.missing_protocol("Pub.unsub-all*",p);
}
}
});
G__18501 = function(p,v){
switch(arguments.length){
case 1:
return G__18501__1.call(this,p);
case 2:
return G__18501__2.call(this,p,v);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
G__18501.cljs$core$IFn$_invoke$arity$1 = G__18501__1;
G__18501.cljs$core$IFn$_invoke$arity$2 = G__18501__2;
return G__18501;
})()
;
cljs.core.async.unsub_all_STAR_ = (function cljs$core$async$unsub_all_STAR_(var_args){
var G__16197 = arguments.length;
switch (G__16197) {
case 1:
return cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$1 = (function (p){
if((((!((p == null)))) && ((!((p.cljs$core$async$Pub$unsub_all_STAR_$arity$1 == null)))))){
return p.cljs$core$async$Pub$unsub_all_STAR_$arity$1(p);
} else {
return cljs$core$async$Pub$unsub_all_STAR_$dyn_18500(p);
}
}));

(cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$2 = (function (p,v){
if((((!((p == null)))) && ((!((p.cljs$core$async$Pub$unsub_all_STAR_$arity$2 == null)))))){
return p.cljs$core$async$Pub$unsub_all_STAR_$arity$2(p,v);
} else {
return cljs$core$async$Pub$unsub_all_STAR_$dyn_18500(p,v);
}
}));

(cljs.core.async.unsub_all_STAR_.cljs$lang$maxFixedArity = 2);



/**
* @constructor
 * @implements {cljs.core.async.Pub}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.async.Mux}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async16233 = (function (ch,topic_fn,buf_fn,mults,ensure_mult,meta16234){
this.ch = ch;
this.topic_fn = topic_fn;
this.buf_fn = buf_fn;
this.mults = mults;
this.ensure_mult = ensure_mult;
this.meta16234 = meta16234;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async16233.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_16235,meta16234__$1){
var self__ = this;
var _16235__$1 = this;
return (new cljs.core.async.t_cljs$core$async16233(self__.ch,self__.topic_fn,self__.buf_fn,self__.mults,self__.ensure_mult,meta16234__$1));
}));

(cljs.core.async.t_cljs$core$async16233.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_16235){
var self__ = this;
var _16235__$1 = this;
return self__.meta16234;
}));

(cljs.core.async.t_cljs$core$async16233.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16233.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.ch;
}));

(cljs.core.async.t_cljs$core$async16233.prototype.cljs$core$async$Pub$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16233.prototype.cljs$core$async$Pub$sub_STAR_$arity$4 = (function (p,topic,ch__$1,close_QMARK_){
var self__ = this;
var p__$1 = this;
var m = (self__.ensure_mult.cljs$core$IFn$_invoke$arity$1 ? self__.ensure_mult.cljs$core$IFn$_invoke$arity$1(topic) : self__.ensure_mult.call(null,topic));
return cljs.core.async.tap.cljs$core$IFn$_invoke$arity$3(m,ch__$1,close_QMARK_);
}));

(cljs.core.async.t_cljs$core$async16233.prototype.cljs$core$async$Pub$unsub_STAR_$arity$3 = (function (p,topic,ch__$1){
var self__ = this;
var p__$1 = this;
var temp__5823__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(cljs.core.deref(self__.mults),topic);
if(cljs.core.truth_(temp__5823__auto__)){
var m = temp__5823__auto__;
return cljs.core.async.untap(m,ch__$1);
} else {
return null;
}
}));

(cljs.core.async.t_cljs$core$async16233.prototype.cljs$core$async$Pub$unsub_all_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.reset_BANG_(self__.mults,cljs.core.PersistentArrayMap.EMPTY);
}));

(cljs.core.async.t_cljs$core$async16233.prototype.cljs$core$async$Pub$unsub_all_STAR_$arity$2 = (function (_,topic){
var self__ = this;
var ___$1 = this;
return cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(self__.mults,cljs.core.dissoc,topic);
}));

(cljs.core.async.t_cljs$core$async16233.getBasis = (function (){
return new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"topic-fn","topic-fn",-862449736,null),new cljs.core.Symbol(null,"buf-fn","buf-fn",-1200281591,null),new cljs.core.Symbol(null,"mults","mults",-461114485,null),new cljs.core.Symbol(null,"ensure-mult","ensure-mult",1796584816,null),new cljs.core.Symbol(null,"meta16234","meta16234",1153363806,null)], null);
}));

(cljs.core.async.t_cljs$core$async16233.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async16233.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async16233");

(cljs.core.async.t_cljs$core$async16233.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async16233");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async16233.
 */
cljs.core.async.__GT_t_cljs$core$async16233 = (function cljs$core$async$__GT_t_cljs$core$async16233(ch,topic_fn,buf_fn,mults,ensure_mult,meta16234){
return (new cljs.core.async.t_cljs$core$async16233(ch,topic_fn,buf_fn,mults,ensure_mult,meta16234));
});


/**
 * Creates and returns a pub(lication) of the supplied channel,
 *   partitioned into topics by the topic-fn. topic-fn will be applied to
 *   each value on the channel and the result will determine the 'topic'
 *   on which that value will be put. Channels can be subscribed to
 *   receive copies of topics using 'sub', and unsubscribed using
 *   'unsub'. Each topic will be handled by an internal mult on a
 *   dedicated channel. By default these internal channels are
 *   unbuffered, but a buf-fn can be supplied which, given a topic,
 *   creates a buffer with desired properties.
 * 
 *   Each item is distributed to all subs in parallel and synchronously,
 *   i.e. each sub must accept before the next item is distributed. Use
 *   buffering/windowing to prevent slow subs from holding up the pub.
 * 
 *   Items received when there are no matching subs get dropped.
 * 
 *   Note that if buf-fns are used then each topic is handled
 *   asynchronously, i.e. if a channel is subscribed to more than one
 *   topic it should not expect them to be interleaved identically with
 *   the source.
 */
cljs.core.async.pub = (function cljs$core$async$pub(var_args){
var G__16230 = arguments.length;
switch (G__16230) {
case 2:
return cljs.core.async.pub.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.pub.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.pub.cljs$core$IFn$_invoke$arity$2 = (function (ch,topic_fn){
return cljs.core.async.pub.cljs$core$IFn$_invoke$arity$3(ch,topic_fn,cljs.core.constantly(null));
}));

(cljs.core.async.pub.cljs$core$IFn$_invoke$arity$3 = (function (ch,topic_fn,buf_fn){
var mults = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(cljs.core.PersistentArrayMap.EMPTY);
var ensure_mult = (function (topic){
var or__5142__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(cljs.core.deref(mults),topic);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return cljs.core.get.cljs$core$IFn$_invoke$arity$2(cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$2(mults,(function (p1__16223_SHARP_){
if(cljs.core.truth_((p1__16223_SHARP_.cljs$core$IFn$_invoke$arity$1 ? p1__16223_SHARP_.cljs$core$IFn$_invoke$arity$1(topic) : p1__16223_SHARP_.call(null,topic)))){
return p1__16223_SHARP_;
} else {
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(p1__16223_SHARP_,topic,cljs.core.async.mult(cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((buf_fn.cljs$core$IFn$_invoke$arity$1 ? buf_fn.cljs$core$IFn$_invoke$arity$1(topic) : buf_fn.call(null,topic)))));
}
})),topic);
}
});
var p = (new cljs.core.async.t_cljs$core$async16233(ch,topic_fn,buf_fn,mults,ensure_mult,cljs.core.PersistentArrayMap.EMPTY));
var c__14286__auto___18562 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = (function (state_16326){
var state_val_16327 = (state_16326[(1)]);
if((state_val_16327 === (7))){
var inst_16318 = (state_16326[(2)]);
var state_16326__$1 = state_16326;
var statearr_16335_18566 = state_16326__$1;
(statearr_16335_18566[(2)] = inst_16318);

(statearr_16335_18566[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (20))){
var state_16326__$1 = state_16326;
var statearr_16337_18567 = state_16326__$1;
(statearr_16337_18567[(2)] = null);

(statearr_16337_18567[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (1))){
var state_16326__$1 = state_16326;
var statearr_16340_18568 = state_16326__$1;
(statearr_16340_18568[(2)] = null);

(statearr_16340_18568[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (24))){
var inst_16301 = (state_16326[(7)]);
var inst_16310 = cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(mults,cljs.core.dissoc,inst_16301);
var state_16326__$1 = state_16326;
var statearr_16342_18569 = state_16326__$1;
(statearr_16342_18569[(2)] = inst_16310);

(statearr_16342_18569[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (4))){
var inst_16250 = (state_16326[(8)]);
var inst_16250__$1 = (state_16326[(2)]);
var inst_16251 = (inst_16250__$1 == null);
var state_16326__$1 = (function (){var statearr_16344 = state_16326;
(statearr_16344[(8)] = inst_16250__$1);

return statearr_16344;
})();
if(cljs.core.truth_(inst_16251)){
var statearr_16345_18570 = state_16326__$1;
(statearr_16345_18570[(1)] = (5));

} else {
var statearr_16346_18571 = state_16326__$1;
(statearr_16346_18571[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (15))){
var inst_16294 = (state_16326[(2)]);
var state_16326__$1 = state_16326;
var statearr_16347_18572 = state_16326__$1;
(statearr_16347_18572[(2)] = inst_16294);

(statearr_16347_18572[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (21))){
var inst_16315 = (state_16326[(2)]);
var state_16326__$1 = (function (){var statearr_16348 = state_16326;
(statearr_16348[(9)] = inst_16315);

return statearr_16348;
})();
var statearr_16349_18573 = state_16326__$1;
(statearr_16349_18573[(2)] = null);

(statearr_16349_18573[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (13))){
var inst_16274 = (state_16326[(10)]);
var inst_16277 = cljs.core.chunked_seq_QMARK_(inst_16274);
var state_16326__$1 = state_16326;
if(inst_16277){
var statearr_16352_18574 = state_16326__$1;
(statearr_16352_18574[(1)] = (16));

} else {
var statearr_16353_18575 = state_16326__$1;
(statearr_16353_18575[(1)] = (17));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (22))){
var inst_16307 = (state_16326[(2)]);
var state_16326__$1 = state_16326;
if(cljs.core.truth_(inst_16307)){
var statearr_16355_18576 = state_16326__$1;
(statearr_16355_18576[(1)] = (23));

} else {
var statearr_16357_18577 = state_16326__$1;
(statearr_16357_18577[(1)] = (24));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (6))){
var inst_16250 = (state_16326[(8)]);
var inst_16301 = (state_16326[(7)]);
var inst_16303 = (state_16326[(11)]);
var inst_16301__$1 = (topic_fn.cljs$core$IFn$_invoke$arity$1 ? topic_fn.cljs$core$IFn$_invoke$arity$1(inst_16250) : topic_fn.call(null,inst_16250));
var inst_16302 = cljs.core.deref(mults);
var inst_16303__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_16302,inst_16301__$1);
var state_16326__$1 = (function (){var statearr_16360 = state_16326;
(statearr_16360[(7)] = inst_16301__$1);

(statearr_16360[(11)] = inst_16303__$1);

return statearr_16360;
})();
if(cljs.core.truth_(inst_16303__$1)){
var statearr_16361_18578 = state_16326__$1;
(statearr_16361_18578[(1)] = (19));

} else {
var statearr_16363_18579 = state_16326__$1;
(statearr_16363_18579[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (25))){
var inst_16312 = (state_16326[(2)]);
var state_16326__$1 = state_16326;
var statearr_16367_18580 = state_16326__$1;
(statearr_16367_18580[(2)] = inst_16312);

(statearr_16367_18580[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (17))){
var inst_16274 = (state_16326[(10)]);
var inst_16285 = cljs.core.first(inst_16274);
var inst_16286 = cljs.core.async.muxch_STAR_(inst_16285);
var inst_16287 = cljs.core.async.close_BANG_(inst_16286);
var inst_16288 = cljs.core.next(inst_16274);
var inst_16260 = inst_16288;
var inst_16261 = null;
var inst_16262 = (0);
var inst_16263 = (0);
var state_16326__$1 = (function (){var statearr_16371 = state_16326;
(statearr_16371[(12)] = inst_16287);

(statearr_16371[(13)] = inst_16260);

(statearr_16371[(14)] = inst_16261);

(statearr_16371[(15)] = inst_16262);

(statearr_16371[(16)] = inst_16263);

return statearr_16371;
})();
var statearr_16373_18581 = state_16326__$1;
(statearr_16373_18581[(2)] = null);

(statearr_16373_18581[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (3))){
var inst_16320 = (state_16326[(2)]);
var state_16326__$1 = state_16326;
return cljs.core.async.impl.ioc_helpers.return_chan(state_16326__$1,inst_16320);
} else {
if((state_val_16327 === (12))){
var inst_16296 = (state_16326[(2)]);
var state_16326__$1 = state_16326;
var statearr_16377_18585 = state_16326__$1;
(statearr_16377_18585[(2)] = inst_16296);

(statearr_16377_18585[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (2))){
var state_16326__$1 = state_16326;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_16326__$1,(4),ch);
} else {
if((state_val_16327 === (23))){
var state_16326__$1 = state_16326;
var statearr_16379_18586 = state_16326__$1;
(statearr_16379_18586[(2)] = null);

(statearr_16379_18586[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (19))){
var inst_16303 = (state_16326[(11)]);
var inst_16250 = (state_16326[(8)]);
var inst_16305 = cljs.core.async.muxch_STAR_(inst_16303);
var state_16326__$1 = state_16326;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_16326__$1,(22),inst_16305,inst_16250);
} else {
if((state_val_16327 === (11))){
var inst_16260 = (state_16326[(13)]);
var inst_16274 = (state_16326[(10)]);
var inst_16274__$1 = cljs.core.seq(inst_16260);
var state_16326__$1 = (function (){var statearr_16383 = state_16326;
(statearr_16383[(10)] = inst_16274__$1);

return statearr_16383;
})();
if(inst_16274__$1){
var statearr_16385_18591 = state_16326__$1;
(statearr_16385_18591[(1)] = (13));

} else {
var statearr_16386_18592 = state_16326__$1;
(statearr_16386_18592[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (9))){
var inst_16298 = (state_16326[(2)]);
var state_16326__$1 = state_16326;
var statearr_16387_18593 = state_16326__$1;
(statearr_16387_18593[(2)] = inst_16298);

(statearr_16387_18593[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (5))){
var inst_16257 = cljs.core.deref(mults);
var inst_16258 = cljs.core.vals(inst_16257);
var inst_16259 = cljs.core.seq(inst_16258);
var inst_16260 = inst_16259;
var inst_16261 = null;
var inst_16262 = (0);
var inst_16263 = (0);
var state_16326__$1 = (function (){var statearr_16389 = state_16326;
(statearr_16389[(13)] = inst_16260);

(statearr_16389[(14)] = inst_16261);

(statearr_16389[(15)] = inst_16262);

(statearr_16389[(16)] = inst_16263);

return statearr_16389;
})();
var statearr_16390_18594 = state_16326__$1;
(statearr_16390_18594[(2)] = null);

(statearr_16390_18594[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (14))){
var state_16326__$1 = state_16326;
var statearr_16397_18595 = state_16326__$1;
(statearr_16397_18595[(2)] = null);

(statearr_16397_18595[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (16))){
var inst_16274 = (state_16326[(10)]);
var inst_16280 = cljs.core.chunk_first(inst_16274);
var inst_16281 = cljs.core.chunk_rest(inst_16274);
var inst_16282 = cljs.core.count(inst_16280);
var inst_16260 = inst_16281;
var inst_16261 = inst_16280;
var inst_16262 = inst_16282;
var inst_16263 = (0);
var state_16326__$1 = (function (){var statearr_16399 = state_16326;
(statearr_16399[(13)] = inst_16260);

(statearr_16399[(14)] = inst_16261);

(statearr_16399[(15)] = inst_16262);

(statearr_16399[(16)] = inst_16263);

return statearr_16399;
})();
var statearr_16400_18596 = state_16326__$1;
(statearr_16400_18596[(2)] = null);

(statearr_16400_18596[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (10))){
var inst_16261 = (state_16326[(14)]);
var inst_16263 = (state_16326[(16)]);
var inst_16260 = (state_16326[(13)]);
var inst_16262 = (state_16326[(15)]);
var inst_16268 = cljs.core._nth(inst_16261,inst_16263);
var inst_16269 = cljs.core.async.muxch_STAR_(inst_16268);
var inst_16270 = cljs.core.async.close_BANG_(inst_16269);
var inst_16271 = (inst_16263 + (1));
var tmp16394 = inst_16261;
var tmp16395 = inst_16262;
var tmp16396 = inst_16260;
var inst_16260__$1 = tmp16396;
var inst_16261__$1 = tmp16394;
var inst_16262__$1 = tmp16395;
var inst_16263__$1 = inst_16271;
var state_16326__$1 = (function (){var statearr_16408 = state_16326;
(statearr_16408[(17)] = inst_16270);

(statearr_16408[(13)] = inst_16260__$1);

(statearr_16408[(14)] = inst_16261__$1);

(statearr_16408[(15)] = inst_16262__$1);

(statearr_16408[(16)] = inst_16263__$1);

return statearr_16408;
})();
var statearr_16409_18597 = state_16326__$1;
(statearr_16409_18597[(2)] = null);

(statearr_16409_18597[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (18))){
var inst_16291 = (state_16326[(2)]);
var state_16326__$1 = state_16326;
var statearr_16412_18598 = state_16326__$1;
(statearr_16412_18598[(2)] = inst_16291);

(statearr_16412_18598[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16327 === (8))){
var inst_16263 = (state_16326[(16)]);
var inst_16262 = (state_16326[(15)]);
var inst_16265 = (inst_16263 < inst_16262);
var inst_16266 = inst_16265;
var state_16326__$1 = state_16326;
if(cljs.core.truth_(inst_16266)){
var statearr_16413_18599 = state_16326__$1;
(statearr_16413_18599[(1)] = (10));

} else {
var statearr_16416_18600 = state_16326__$1;
(statearr_16416_18600[(1)] = (11));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__14092__auto__ = null;
var cljs$core$async$state_machine__14092__auto____0 = (function (){
var statearr_16419 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_16419[(0)] = cljs$core$async$state_machine__14092__auto__);

(statearr_16419[(1)] = (1));

return statearr_16419;
});
var cljs$core$async$state_machine__14092__auto____1 = (function (state_16326){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_16326);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e16422){var ex__14095__auto__ = e16422;
var statearr_16423_18602 = state_16326;
(statearr_16423_18602[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_16326[(4)]))){
var statearr_16424_18603 = state_16326;
(statearr_16424_18603[(1)] = cljs.core.first((state_16326[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18605 = state_16326;
state_16326 = G__18605;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$state_machine__14092__auto__ = function(state_16326){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14092__auto____1.call(this,state_16326);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14092__auto____0;
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14092__auto____1;
return cljs$core$async$state_machine__14092__auto__;
})()
})();
var state__14288__auto__ = (function (){var statearr_16430 = f__14287__auto__();
(statearr_16430[(6)] = c__14286__auto___18562);

return statearr_16430;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
}));


return p;
}));

(cljs.core.async.pub.cljs$lang$maxFixedArity = 3);

/**
 * Subscribes a channel to a topic of a pub.
 * 
 *   By default the channel will be closed when the source closes,
 *   but can be determined by the close? parameter.
 */
cljs.core.async.sub = (function cljs$core$async$sub(var_args){
var G__16433 = arguments.length;
switch (G__16433) {
case 3:
return cljs.core.async.sub.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core.async.sub.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.sub.cljs$core$IFn$_invoke$arity$3 = (function (p,topic,ch){
return cljs.core.async.sub.cljs$core$IFn$_invoke$arity$4(p,topic,ch,true);
}));

(cljs.core.async.sub.cljs$core$IFn$_invoke$arity$4 = (function (p,topic,ch,close_QMARK_){
return cljs.core.async.sub_STAR_(p,topic,ch,close_QMARK_);
}));

(cljs.core.async.sub.cljs$lang$maxFixedArity = 4);

/**
 * Unsubscribes a channel from a topic of a pub
 */
cljs.core.async.unsub = (function cljs$core$async$unsub(p,topic,ch){
return cljs.core.async.unsub_STAR_(p,topic,ch);
});
/**
 * Unsubscribes all channels from a pub, or a topic of a pub
 */
cljs.core.async.unsub_all = (function cljs$core$async$unsub_all(var_args){
var G__16438 = arguments.length;
switch (G__16438) {
case 1:
return cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$1 = (function (p){
return cljs.core.async.unsub_all_STAR_(p);
}));

(cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$2 = (function (p,topic){
return cljs.core.async.unsub_all_STAR_(p,topic);
}));

(cljs.core.async.unsub_all.cljs$lang$maxFixedArity = 2);

/**
 * Takes a function and a collection of source channels, and returns a
 *   channel which contains the values produced by applying f to the set
 *   of first items taken from each source channel, followed by applying
 *   f to the set of second items from each channel, until any one of the
 *   channels is closed, at which point the output channel will be
 *   closed. The returned channel will be unbuffered by default, or a
 *   buf-or-n can be supplied
 */
cljs.core.async.map = (function cljs$core$async$map(var_args){
var G__16445 = arguments.length;
switch (G__16445) {
case 2:
return cljs.core.async.map.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.map.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.map.cljs$core$IFn$_invoke$arity$2 = (function (f,chs){
return cljs.core.async.map.cljs$core$IFn$_invoke$arity$3(f,chs,null);
}));

(cljs.core.async.map.cljs$core$IFn$_invoke$arity$3 = (function (f,chs,buf_or_n){
var chs__$1 = cljs.core.vec(chs);
var out = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(buf_or_n);
var cnt = cljs.core.count(chs__$1);
var rets = cljs.core.object_array.cljs$core$IFn$_invoke$arity$1(cnt);
var dchan = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
var dctr = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(null);
var done = cljs.core.mapv.cljs$core$IFn$_invoke$arity$2((function (i){
return (function (ret){
(rets[i] = ret);

if((cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$2(dctr,cljs.core.dec) === (0))){
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2(dchan,rets.slice((0)));
} else {
return null;
}
});
}),cljs.core.range.cljs$core$IFn$_invoke$arity$1(cnt));
if((cnt === (0))){
cljs.core.async.close_BANG_(out);
} else {
var c__14286__auto___18635 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = (function (state_16508){
var state_val_16509 = (state_16508[(1)]);
if((state_val_16509 === (7))){
var state_16508__$1 = state_16508;
var statearr_16514_18643 = state_16508__$1;
(statearr_16514_18643[(2)] = null);

(statearr_16514_18643[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16509 === (1))){
var state_16508__$1 = state_16508;
var statearr_16516_18644 = state_16508__$1;
(statearr_16516_18644[(2)] = null);

(statearr_16516_18644[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16509 === (4))){
var inst_16458 = (state_16508[(7)]);
var inst_16457 = (state_16508[(8)]);
var inst_16461 = (inst_16458 < inst_16457);
var state_16508__$1 = state_16508;
if(cljs.core.truth_(inst_16461)){
var statearr_16518_18645 = state_16508__$1;
(statearr_16518_18645[(1)] = (6));

} else {
var statearr_16519_18646 = state_16508__$1;
(statearr_16519_18646[(1)] = (7));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16509 === (15))){
var inst_16493 = (state_16508[(9)]);
var inst_16499 = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(f,inst_16493);
var state_16508__$1 = state_16508;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_16508__$1,(17),out,inst_16499);
} else {
if((state_val_16509 === (13))){
var inst_16493 = (state_16508[(9)]);
var inst_16493__$1 = (state_16508[(2)]);
var inst_16494 = cljs.core.some(cljs.core.nil_QMARK_,inst_16493__$1);
var state_16508__$1 = (function (){var statearr_16523 = state_16508;
(statearr_16523[(9)] = inst_16493__$1);

return statearr_16523;
})();
if(cljs.core.truth_(inst_16494)){
var statearr_16524_18654 = state_16508__$1;
(statearr_16524_18654[(1)] = (14));

} else {
var statearr_16525_18655 = state_16508__$1;
(statearr_16525_18655[(1)] = (15));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16509 === (6))){
var state_16508__$1 = state_16508;
var statearr_16526_18656 = state_16508__$1;
(statearr_16526_18656[(2)] = null);

(statearr_16526_18656[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16509 === (17))){
var inst_16501 = (state_16508[(2)]);
var state_16508__$1 = (function (){var statearr_16548 = state_16508;
(statearr_16548[(10)] = inst_16501);

return statearr_16548;
})();
var statearr_16549_18664 = state_16508__$1;
(statearr_16549_18664[(2)] = null);

(statearr_16549_18664[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16509 === (3))){
var inst_16506 = (state_16508[(2)]);
var state_16508__$1 = state_16508;
return cljs.core.async.impl.ioc_helpers.return_chan(state_16508__$1,inst_16506);
} else {
if((state_val_16509 === (12))){
var _ = (function (){var statearr_16561 = state_16508;
(statearr_16561[(4)] = cljs.core.rest((state_16508[(4)])));

return statearr_16561;
})();
var state_16508__$1 = state_16508;
var ex16545 = (state_16508__$1[(2)]);
var statearr_16570_18665 = state_16508__$1;
(statearr_16570_18665[(5)] = ex16545);


if((ex16545 instanceof Object)){
var statearr_16573_18666 = state_16508__$1;
(statearr_16573_18666[(1)] = (11));

(statearr_16573_18666[(5)] = null);

} else {
throw ex16545;

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16509 === (2))){
var inst_16456 = cljs.core.reset_BANG_(dctr,cnt);
var inst_16457 = cnt;
var inst_16458 = (0);
var state_16508__$1 = (function (){var statearr_16599 = state_16508;
(statearr_16599[(11)] = inst_16456);

(statearr_16599[(8)] = inst_16457);

(statearr_16599[(7)] = inst_16458);

return statearr_16599;
})();
var statearr_16600_18674 = state_16508__$1;
(statearr_16600_18674[(2)] = null);

(statearr_16600_18674[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16509 === (11))){
var inst_16468 = (state_16508[(2)]);
var inst_16469 = cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$2(dctr,cljs.core.dec);
var state_16508__$1 = (function (){var statearr_16602 = state_16508;
(statearr_16602[(12)] = inst_16468);

return statearr_16602;
})();
var statearr_16608_18675 = state_16508__$1;
(statearr_16608_18675[(2)] = inst_16469);

(statearr_16608_18675[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16509 === (9))){
var inst_16458 = (state_16508[(7)]);
var _ = (function (){var statearr_16615 = state_16508;
(statearr_16615[(4)] = cljs.core.cons((12),(state_16508[(4)])));

return statearr_16615;
})();
var inst_16477 = (chs__$1.cljs$core$IFn$_invoke$arity$1 ? chs__$1.cljs$core$IFn$_invoke$arity$1(inst_16458) : chs__$1.call(null,inst_16458));
var inst_16478 = (done.cljs$core$IFn$_invoke$arity$1 ? done.cljs$core$IFn$_invoke$arity$1(inst_16458) : done.call(null,inst_16458));
var inst_16479 = cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$2(inst_16477,inst_16478);
var ___$1 = (function (){var statearr_16618 = state_16508;
(statearr_16618[(4)] = cljs.core.rest((state_16508[(4)])));

return statearr_16618;
})();
var state_16508__$1 = state_16508;
var statearr_16624_18676 = state_16508__$1;
(statearr_16624_18676[(2)] = inst_16479);

(statearr_16624_18676[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16509 === (5))){
var inst_16489 = (state_16508[(2)]);
var state_16508__$1 = (function (){var statearr_16629 = state_16508;
(statearr_16629[(13)] = inst_16489);

return statearr_16629;
})();
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_16508__$1,(13),dchan);
} else {
if((state_val_16509 === (14))){
var inst_16497 = cljs.core.async.close_BANG_(out);
var state_16508__$1 = state_16508;
var statearr_16635_18677 = state_16508__$1;
(statearr_16635_18677[(2)] = inst_16497);

(statearr_16635_18677[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16509 === (16))){
var inst_16504 = (state_16508[(2)]);
var state_16508__$1 = state_16508;
var statearr_16644_18678 = state_16508__$1;
(statearr_16644_18678[(2)] = inst_16504);

(statearr_16644_18678[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16509 === (10))){
var inst_16458 = (state_16508[(7)]);
var inst_16482 = (state_16508[(2)]);
var inst_16483 = (inst_16458 + (1));
var inst_16458__$1 = inst_16483;
var state_16508__$1 = (function (){var statearr_16646 = state_16508;
(statearr_16646[(14)] = inst_16482);

(statearr_16646[(7)] = inst_16458__$1);

return statearr_16646;
})();
var statearr_16648_18679 = state_16508__$1;
(statearr_16648_18679[(2)] = null);

(statearr_16648_18679[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16509 === (8))){
var inst_16487 = (state_16508[(2)]);
var state_16508__$1 = state_16508;
var statearr_16652_18680 = state_16508__$1;
(statearr_16652_18680[(2)] = inst_16487);

(statearr_16652_18680[(1)] = (5));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__14092__auto__ = null;
var cljs$core$async$state_machine__14092__auto____0 = (function (){
var statearr_16659 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_16659[(0)] = cljs$core$async$state_machine__14092__auto__);

(statearr_16659[(1)] = (1));

return statearr_16659;
});
var cljs$core$async$state_machine__14092__auto____1 = (function (state_16508){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_16508);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e16661){var ex__14095__auto__ = e16661;
var statearr_16662_18683 = state_16508;
(statearr_16662_18683[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_16508[(4)]))){
var statearr_16663_18684 = state_16508;
(statearr_16663_18684[(1)] = cljs.core.first((state_16508[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18687 = state_16508;
state_16508 = G__18687;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$state_machine__14092__auto__ = function(state_16508){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14092__auto____1.call(this,state_16508);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14092__auto____0;
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14092__auto____1;
return cljs$core$async$state_machine__14092__auto__;
})()
})();
var state__14288__auto__ = (function (){var statearr_16665 = f__14287__auto__();
(statearr_16665[(6)] = c__14286__auto___18635);

return statearr_16665;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
}));

}

return out;
}));

(cljs.core.async.map.cljs$lang$maxFixedArity = 3);

/**
 * Takes a collection of source channels and returns a channel which
 *   contains all values taken from them. The returned channel will be
 *   unbuffered by default, or a buf-or-n can be supplied. The channel
 *   will close after all the source channels have closed.
 */
cljs.core.async.merge = (function cljs$core$async$merge(var_args){
var G__16670 = arguments.length;
switch (G__16670) {
case 1:
return cljs.core.async.merge.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.merge.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.merge.cljs$core$IFn$_invoke$arity$1 = (function (chs){
return cljs.core.async.merge.cljs$core$IFn$_invoke$arity$2(chs,null);
}));

(cljs.core.async.merge.cljs$core$IFn$_invoke$arity$2 = (function (chs,buf_or_n){
var out = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(buf_or_n);
var c__14286__auto___18707 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = (function (state_16715){
var state_val_16716 = (state_16715[(1)]);
if((state_val_16716 === (7))){
var inst_16682 = (state_16715[(7)]);
var inst_16683 = (state_16715[(8)]);
var inst_16682__$1 = (state_16715[(2)]);
var inst_16683__$1 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_16682__$1,(0),null);
var inst_16684 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_16682__$1,(1),null);
var inst_16686 = (inst_16683__$1 == null);
var state_16715__$1 = (function (){var statearr_16730 = state_16715;
(statearr_16730[(7)] = inst_16682__$1);

(statearr_16730[(8)] = inst_16683__$1);

(statearr_16730[(9)] = inst_16684);

return statearr_16730;
})();
if(cljs.core.truth_(inst_16686)){
var statearr_16732_18719 = state_16715__$1;
(statearr_16732_18719[(1)] = (8));

} else {
var statearr_16733_18720 = state_16715__$1;
(statearr_16733_18720[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16716 === (1))){
var inst_16671 = cljs.core.vec(chs);
var inst_16672 = inst_16671;
var state_16715__$1 = (function (){var statearr_16737 = state_16715;
(statearr_16737[(10)] = inst_16672);

return statearr_16737;
})();
var statearr_16739_18725 = state_16715__$1;
(statearr_16739_18725[(2)] = null);

(statearr_16739_18725[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16716 === (4))){
var inst_16672 = (state_16715[(10)]);
var state_16715__$1 = state_16715;
return cljs.core.async.ioc_alts_BANG_(state_16715__$1,(7),inst_16672);
} else {
if((state_val_16716 === (6))){
var inst_16711 = (state_16715[(2)]);
var state_16715__$1 = state_16715;
var statearr_16743_18730 = state_16715__$1;
(statearr_16743_18730[(2)] = inst_16711);

(statearr_16743_18730[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16716 === (3))){
var inst_16713 = (state_16715[(2)]);
var state_16715__$1 = state_16715;
return cljs.core.async.impl.ioc_helpers.return_chan(state_16715__$1,inst_16713);
} else {
if((state_val_16716 === (2))){
var inst_16672 = (state_16715[(10)]);
var inst_16674 = cljs.core.count(inst_16672);
var inst_16675 = (inst_16674 > (0));
var state_16715__$1 = state_16715;
if(cljs.core.truth_(inst_16675)){
var statearr_16746_18733 = state_16715__$1;
(statearr_16746_18733[(1)] = (4));

} else {
var statearr_16747_18734 = state_16715__$1;
(statearr_16747_18734[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16716 === (11))){
var inst_16672 = (state_16715[(10)]);
var inst_16704 = (state_16715[(2)]);
var tmp16745 = inst_16672;
var inst_16672__$1 = tmp16745;
var state_16715__$1 = (function (){var statearr_16750 = state_16715;
(statearr_16750[(11)] = inst_16704);

(statearr_16750[(10)] = inst_16672__$1);

return statearr_16750;
})();
var statearr_16751_18738 = state_16715__$1;
(statearr_16751_18738[(2)] = null);

(statearr_16751_18738[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16716 === (9))){
var inst_16683 = (state_16715[(8)]);
var state_16715__$1 = state_16715;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_16715__$1,(11),out,inst_16683);
} else {
if((state_val_16716 === (5))){
var inst_16709 = cljs.core.async.close_BANG_(out);
var state_16715__$1 = state_16715;
var statearr_16754_18744 = state_16715__$1;
(statearr_16754_18744[(2)] = inst_16709);

(statearr_16754_18744[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16716 === (10))){
var inst_16707 = (state_16715[(2)]);
var state_16715__$1 = state_16715;
var statearr_16755_18748 = state_16715__$1;
(statearr_16755_18748[(2)] = inst_16707);

(statearr_16755_18748[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16716 === (8))){
var inst_16672 = (state_16715[(10)]);
var inst_16682 = (state_16715[(7)]);
var inst_16683 = (state_16715[(8)]);
var inst_16684 = (state_16715[(9)]);
var inst_16698 = (function (){var cs = inst_16672;
var vec__16678 = inst_16682;
var v = inst_16683;
var c = inst_16684;
return (function (p1__16666_SHARP_){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(c,p1__16666_SHARP_);
});
})();
var inst_16699 = cljs.core.filterv(inst_16698,inst_16672);
var inst_16672__$1 = inst_16699;
var state_16715__$1 = (function (){var statearr_16757 = state_16715;
(statearr_16757[(10)] = inst_16672__$1);

return statearr_16757;
})();
var statearr_16758_18761 = state_16715__$1;
(statearr_16758_18761[(2)] = null);

(statearr_16758_18761[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__14092__auto__ = null;
var cljs$core$async$state_machine__14092__auto____0 = (function (){
var statearr_16760 = [null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_16760[(0)] = cljs$core$async$state_machine__14092__auto__);

(statearr_16760[(1)] = (1));

return statearr_16760;
});
var cljs$core$async$state_machine__14092__auto____1 = (function (state_16715){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_16715);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e16770){var ex__14095__auto__ = e16770;
var statearr_16772_18767 = state_16715;
(statearr_16772_18767[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_16715[(4)]))){
var statearr_16773_18774 = state_16715;
(statearr_16773_18774[(1)] = cljs.core.first((state_16715[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18777 = state_16715;
state_16715 = G__18777;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$state_machine__14092__auto__ = function(state_16715){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14092__auto____1.call(this,state_16715);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14092__auto____0;
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14092__auto____1;
return cljs$core$async$state_machine__14092__auto__;
})()
})();
var state__14288__auto__ = (function (){var statearr_16783 = f__14287__auto__();
(statearr_16783[(6)] = c__14286__auto___18707);

return statearr_16783;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
}));


return out;
}));

(cljs.core.async.merge.cljs$lang$maxFixedArity = 2);

/**
 * Returns a channel containing the single (collection) result of the
 *   items taken from the channel conjoined to the supplied
 *   collection. ch must close before into produces a result.
 */
cljs.core.async.into = (function cljs$core$async$into(coll,ch){
return cljs.core.async.reduce(cljs.core.conj,coll,ch);
});
/**
 * Returns a channel that will return, at most, n items from ch. After n items
 * have been returned, or ch has been closed, the return chanel will close.
 * 
 *   The output channel is unbuffered by default, unless buf-or-n is given.
 */
cljs.core.async.take = (function cljs$core$async$take(var_args){
var G__16804 = arguments.length;
switch (G__16804) {
case 2:
return cljs.core.async.take.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.take.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.take.cljs$core$IFn$_invoke$arity$2 = (function (n,ch){
return cljs.core.async.take.cljs$core$IFn$_invoke$arity$3(n,ch,null);
}));

(cljs.core.async.take.cljs$core$IFn$_invoke$arity$3 = (function (n,ch,buf_or_n){
var out = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(buf_or_n);
var c__14286__auto___18787 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = (function (state_16834){
var state_val_16835 = (state_16834[(1)]);
if((state_val_16835 === (7))){
var inst_16816 = (state_16834[(7)]);
var inst_16816__$1 = (state_16834[(2)]);
var inst_16817 = (inst_16816__$1 == null);
var inst_16818 = cljs.core.not(inst_16817);
var state_16834__$1 = (function (){var statearr_16836 = state_16834;
(statearr_16836[(7)] = inst_16816__$1);

return statearr_16836;
})();
if(inst_16818){
var statearr_16837_18794 = state_16834__$1;
(statearr_16837_18794[(1)] = (8));

} else {
var statearr_16838_18795 = state_16834__$1;
(statearr_16838_18795[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16835 === (1))){
var inst_16811 = (0);
var state_16834__$1 = (function (){var statearr_16840 = state_16834;
(statearr_16840[(8)] = inst_16811);

return statearr_16840;
})();
var statearr_16841_18797 = state_16834__$1;
(statearr_16841_18797[(2)] = null);

(statearr_16841_18797[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16835 === (4))){
var state_16834__$1 = state_16834;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_16834__$1,(7),ch);
} else {
if((state_val_16835 === (6))){
var inst_16829 = (state_16834[(2)]);
var state_16834__$1 = state_16834;
var statearr_16845_18800 = state_16834__$1;
(statearr_16845_18800[(2)] = inst_16829);

(statearr_16845_18800[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16835 === (3))){
var inst_16831 = (state_16834[(2)]);
var inst_16832 = cljs.core.async.close_BANG_(out);
var state_16834__$1 = (function (){var statearr_16846 = state_16834;
(statearr_16846[(9)] = inst_16831);

return statearr_16846;
})();
return cljs.core.async.impl.ioc_helpers.return_chan(state_16834__$1,inst_16832);
} else {
if((state_val_16835 === (2))){
var inst_16811 = (state_16834[(8)]);
var inst_16813 = (inst_16811 < n);
var state_16834__$1 = state_16834;
if(cljs.core.truth_(inst_16813)){
var statearr_16847_18819 = state_16834__$1;
(statearr_16847_18819[(1)] = (4));

} else {
var statearr_16849_18821 = state_16834__$1;
(statearr_16849_18821[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16835 === (11))){
var inst_16811 = (state_16834[(8)]);
var inst_16821 = (state_16834[(2)]);
var inst_16822 = (inst_16811 + (1));
var inst_16811__$1 = inst_16822;
var state_16834__$1 = (function (){var statearr_16853 = state_16834;
(statearr_16853[(10)] = inst_16821);

(statearr_16853[(8)] = inst_16811__$1);

return statearr_16853;
})();
var statearr_16854_18829 = state_16834__$1;
(statearr_16854_18829[(2)] = null);

(statearr_16854_18829[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16835 === (9))){
var state_16834__$1 = state_16834;
var statearr_16856_18837 = state_16834__$1;
(statearr_16856_18837[(2)] = null);

(statearr_16856_18837[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16835 === (5))){
var state_16834__$1 = state_16834;
var statearr_16857_18838 = state_16834__$1;
(statearr_16857_18838[(2)] = null);

(statearr_16857_18838[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16835 === (10))){
var inst_16826 = (state_16834[(2)]);
var state_16834__$1 = state_16834;
var statearr_16858_18845 = state_16834__$1;
(statearr_16858_18845[(2)] = inst_16826);

(statearr_16858_18845[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16835 === (8))){
var inst_16816 = (state_16834[(7)]);
var state_16834__$1 = state_16834;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_16834__$1,(11),out,inst_16816);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__14092__auto__ = null;
var cljs$core$async$state_machine__14092__auto____0 = (function (){
var statearr_16859 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_16859[(0)] = cljs$core$async$state_machine__14092__auto__);

(statearr_16859[(1)] = (1));

return statearr_16859;
});
var cljs$core$async$state_machine__14092__auto____1 = (function (state_16834){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_16834);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e16860){var ex__14095__auto__ = e16860;
var statearr_16861_18883 = state_16834;
(statearr_16861_18883[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_16834[(4)]))){
var statearr_16863_18885 = state_16834;
(statearr_16863_18885[(1)] = cljs.core.first((state_16834[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18886 = state_16834;
state_16834 = G__18886;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$state_machine__14092__auto__ = function(state_16834){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14092__auto____1.call(this,state_16834);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14092__auto____0;
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14092__auto____1;
return cljs$core$async$state_machine__14092__auto__;
})()
})();
var state__14288__auto__ = (function (){var statearr_16866 = f__14287__auto__();
(statearr_16866[(6)] = c__14286__auto___18787);

return statearr_16866;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
}));


return out;
}));

(cljs.core.async.take.cljs$lang$maxFixedArity = 3);


/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async16878 = (function (f,ch,meta16872,_,fn1,meta16879){
this.f = f;
this.ch = ch;
this.meta16872 = meta16872;
this._ = _;
this.fn1 = fn1;
this.meta16879 = meta16879;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async16878.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_16880,meta16879__$1){
var self__ = this;
var _16880__$1 = this;
return (new cljs.core.async.t_cljs$core$async16878(self__.f,self__.ch,self__.meta16872,self__._,self__.fn1,meta16879__$1));
}));

(cljs.core.async.t_cljs$core$async16878.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_16880){
var self__ = this;
var _16880__$1 = this;
return self__.meta16879;
}));

(cljs.core.async.t_cljs$core$async16878.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16878.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (___$1){
var self__ = this;
var ___$2 = this;
return cljs.core.async.impl.protocols.active_QMARK_(self__.fn1);
}));

(cljs.core.async.t_cljs$core$async16878.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (___$1){
var self__ = this;
var ___$2 = this;
return true;
}));

(cljs.core.async.t_cljs$core$async16878.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (___$1){
var self__ = this;
var ___$2 = this;
var f1 = cljs.core.async.impl.protocols.commit(self__.fn1);
return (function (p1__16869_SHARP_){
var G__16883 = (((p1__16869_SHARP_ == null))?null:(self__.f.cljs$core$IFn$_invoke$arity$1 ? self__.f.cljs$core$IFn$_invoke$arity$1(p1__16869_SHARP_) : self__.f.call(null,p1__16869_SHARP_)));
return (f1.cljs$core$IFn$_invoke$arity$1 ? f1.cljs$core$IFn$_invoke$arity$1(G__16883) : f1.call(null,G__16883));
});
}));

(cljs.core.async.t_cljs$core$async16878.getBasis = (function (){
return new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta16872","meta16872",-16679898,null),cljs.core.with_meta(new cljs.core.Symbol(null,"_","_",-1201019570,null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Symbol("cljs.core.async","t_cljs$core$async16871","cljs.core.async/t_cljs$core$async16871",-1810096062,null)], null)),new cljs.core.Symbol(null,"fn1","fn1",895834444,null),new cljs.core.Symbol(null,"meta16879","meta16879",1164843332,null)], null);
}));

(cljs.core.async.t_cljs$core$async16878.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async16878.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async16878");

(cljs.core.async.t_cljs$core$async16878.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async16878");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async16878.
 */
cljs.core.async.__GT_t_cljs$core$async16878 = (function cljs$core$async$__GT_t_cljs$core$async16878(f,ch,meta16872,_,fn1,meta16879){
return (new cljs.core.async.t_cljs$core$async16878(f,ch,meta16872,_,fn1,meta16879));
});



/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async16871 = (function (f,ch,meta16872){
this.f = f;
this.ch = ch;
this.meta16872 = meta16872;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async16871.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_16873,meta16872__$1){
var self__ = this;
var _16873__$1 = this;
return (new cljs.core.async.t_cljs$core$async16871(self__.f,self__.ch,meta16872__$1));
}));

(cljs.core.async.t_cljs$core$async16871.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_16873){
var self__ = this;
var _16873__$1 = this;
return self__.meta16872;
}));

(cljs.core.async.t_cljs$core$async16871.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16871.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async16871.prototype.cljs$core$async$impl$protocols$Channel$closed_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.closed_QMARK_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async16871.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16871.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
var ret = cljs.core.async.impl.protocols.take_BANG_(self__.ch,(new cljs.core.async.t_cljs$core$async16878(self__.f,self__.ch,self__.meta16872,___$1,fn1,cljs.core.PersistentArrayMap.EMPTY)));
if(cljs.core.truth_((function (){var and__5140__auto__ = ret;
if(cljs.core.truth_(and__5140__auto__)){
return (!((cljs.core.deref(ret) == null)));
} else {
return and__5140__auto__;
}
})())){
return cljs.core.async.impl.channels.box((function (){var G__16885 = cljs.core.deref(ret);
return (self__.f.cljs$core$IFn$_invoke$arity$1 ? self__.f.cljs$core$IFn$_invoke$arity$1(G__16885) : self__.f.call(null,G__16885));
})());
} else {
return ret;
}
}));

(cljs.core.async.t_cljs$core$async16871.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16871.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.put_BANG_(self__.ch,val,fn1);
}));

(cljs.core.async.t_cljs$core$async16871.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta16872","meta16872",-16679898,null)], null);
}));

(cljs.core.async.t_cljs$core$async16871.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async16871.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async16871");

(cljs.core.async.t_cljs$core$async16871.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async16871");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async16871.
 */
cljs.core.async.__GT_t_cljs$core$async16871 = (function cljs$core$async$__GT_t_cljs$core$async16871(f,ch,meta16872){
return (new cljs.core.async.t_cljs$core$async16871(f,ch,meta16872));
});


/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.map_LT_ = (function cljs$core$async$map_LT_(f,ch){
return (new cljs.core.async.t_cljs$core$async16871(f,ch,cljs.core.PersistentArrayMap.EMPTY));
});

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async16891 = (function (f,ch,meta16892){
this.f = f;
this.ch = ch;
this.meta16892 = meta16892;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async16891.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_16893,meta16892__$1){
var self__ = this;
var _16893__$1 = this;
return (new cljs.core.async.t_cljs$core$async16891(self__.f,self__.ch,meta16892__$1));
}));

(cljs.core.async.t_cljs$core$async16891.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_16893){
var self__ = this;
var _16893__$1 = this;
return self__.meta16892;
}));

(cljs.core.async.t_cljs$core$async16891.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16891.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async16891.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16891.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.take_BANG_(self__.ch,fn1);
}));

(cljs.core.async.t_cljs$core$async16891.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16891.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.put_BANG_(self__.ch,(self__.f.cljs$core$IFn$_invoke$arity$1 ? self__.f.cljs$core$IFn$_invoke$arity$1(val) : self__.f.call(null,val)),fn1);
}));

(cljs.core.async.t_cljs$core$async16891.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta16892","meta16892",-1082381537,null)], null);
}));

(cljs.core.async.t_cljs$core$async16891.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async16891.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async16891");

(cljs.core.async.t_cljs$core$async16891.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async16891");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async16891.
 */
cljs.core.async.__GT_t_cljs$core$async16891 = (function cljs$core$async$__GT_t_cljs$core$async16891(f,ch,meta16892){
return (new cljs.core.async.t_cljs$core$async16891(f,ch,meta16892));
});


/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.map_GT_ = (function cljs$core$async$map_GT_(f,ch){
return (new cljs.core.async.t_cljs$core$async16891(f,ch,cljs.core.PersistentArrayMap.EMPTY));
});

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async16937 = (function (p,ch,meta16938){
this.p = p;
this.ch = ch;
this.meta16938 = meta16938;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async16937.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_16939,meta16938__$1){
var self__ = this;
var _16939__$1 = this;
return (new cljs.core.async.t_cljs$core$async16937(self__.p,self__.ch,meta16938__$1));
}));

(cljs.core.async.t_cljs$core$async16937.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_16939){
var self__ = this;
var _16939__$1 = this;
return self__.meta16938;
}));

(cljs.core.async.t_cljs$core$async16937.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16937.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async16937.prototype.cljs$core$async$impl$protocols$Channel$closed_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.closed_QMARK_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async16937.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16937.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.take_BANG_(self__.ch,fn1);
}));

(cljs.core.async.t_cljs$core$async16937.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16937.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
if(cljs.core.truth_((self__.p.cljs$core$IFn$_invoke$arity$1 ? self__.p.cljs$core$IFn$_invoke$arity$1(val) : self__.p.call(null,val)))){
return cljs.core.async.impl.protocols.put_BANG_(self__.ch,val,fn1);
} else {
return cljs.core.async.impl.channels.box(cljs.core.not(cljs.core.async.impl.protocols.closed_QMARK_(self__.ch)));
}
}));

(cljs.core.async.t_cljs$core$async16937.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"p","p",1791580836,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta16938","meta16938",1450141281,null)], null);
}));

(cljs.core.async.t_cljs$core$async16937.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async16937.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async16937");

(cljs.core.async.t_cljs$core$async16937.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async16937");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async16937.
 */
cljs.core.async.__GT_t_cljs$core$async16937 = (function cljs$core$async$__GT_t_cljs$core$async16937(p,ch,meta16938){
return (new cljs.core.async.t_cljs$core$async16937(p,ch,meta16938));
});


/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.filter_GT_ = (function cljs$core$async$filter_GT_(p,ch){
return (new cljs.core.async.t_cljs$core$async16937(p,ch,cljs.core.PersistentArrayMap.EMPTY));
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.remove_GT_ = (function cljs$core$async$remove_GT_(p,ch){
return cljs.core.async.filter_GT_(cljs.core.complement(p),ch);
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.filter_LT_ = (function cljs$core$async$filter_LT_(var_args){
var G__16948 = arguments.length;
switch (G__16948) {
case 2:
return cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$2 = (function (p,ch){
return cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$3(p,ch,null);
}));

(cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$3 = (function (p,ch,buf_or_n){
var out = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(buf_or_n);
var c__14286__auto___18973 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = (function (state_16980){
var state_val_16981 = (state_16980[(1)]);
if((state_val_16981 === (7))){
var inst_16976 = (state_16980[(2)]);
var state_16980__$1 = state_16980;
var statearr_16991_18974 = state_16980__$1;
(statearr_16991_18974[(2)] = inst_16976);

(statearr_16991_18974[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16981 === (1))){
var state_16980__$1 = state_16980;
var statearr_16992_18975 = state_16980__$1;
(statearr_16992_18975[(2)] = null);

(statearr_16992_18975[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16981 === (4))){
var inst_16962 = (state_16980[(7)]);
var inst_16962__$1 = (state_16980[(2)]);
var inst_16963 = (inst_16962__$1 == null);
var state_16980__$1 = (function (){var statearr_16995 = state_16980;
(statearr_16995[(7)] = inst_16962__$1);

return statearr_16995;
})();
if(cljs.core.truth_(inst_16963)){
var statearr_16999_18976 = state_16980__$1;
(statearr_16999_18976[(1)] = (5));

} else {
var statearr_17000_18977 = state_16980__$1;
(statearr_17000_18977[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16981 === (6))){
var inst_16962 = (state_16980[(7)]);
var inst_16967 = (p.cljs$core$IFn$_invoke$arity$1 ? p.cljs$core$IFn$_invoke$arity$1(inst_16962) : p.call(null,inst_16962));
var state_16980__$1 = state_16980;
if(cljs.core.truth_(inst_16967)){
var statearr_17003_18978 = state_16980__$1;
(statearr_17003_18978[(1)] = (8));

} else {
var statearr_17004_18979 = state_16980__$1;
(statearr_17004_18979[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16981 === (3))){
var inst_16978 = (state_16980[(2)]);
var state_16980__$1 = state_16980;
return cljs.core.async.impl.ioc_helpers.return_chan(state_16980__$1,inst_16978);
} else {
if((state_val_16981 === (2))){
var state_16980__$1 = state_16980;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_16980__$1,(4),ch);
} else {
if((state_val_16981 === (11))){
var inst_16970 = (state_16980[(2)]);
var state_16980__$1 = state_16980;
var statearr_17007_18980 = state_16980__$1;
(statearr_17007_18980[(2)] = inst_16970);

(statearr_17007_18980[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16981 === (9))){
var state_16980__$1 = state_16980;
var statearr_17008_18981 = state_16980__$1;
(statearr_17008_18981[(2)] = null);

(statearr_17008_18981[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16981 === (5))){
var inst_16965 = cljs.core.async.close_BANG_(out);
var state_16980__$1 = state_16980;
var statearr_17009_18982 = state_16980__$1;
(statearr_17009_18982[(2)] = inst_16965);

(statearr_17009_18982[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16981 === (10))){
var inst_16973 = (state_16980[(2)]);
var state_16980__$1 = (function (){var statearr_17029 = state_16980;
(statearr_17029[(8)] = inst_16973);

return statearr_17029;
})();
var statearr_17030_18983 = state_16980__$1;
(statearr_17030_18983[(2)] = null);

(statearr_17030_18983[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16981 === (8))){
var inst_16962 = (state_16980[(7)]);
var state_16980__$1 = state_16980;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_16980__$1,(11),out,inst_16962);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__14092__auto__ = null;
var cljs$core$async$state_machine__14092__auto____0 = (function (){
var statearr_17031 = [null,null,null,null,null,null,null,null,null];
(statearr_17031[(0)] = cljs$core$async$state_machine__14092__auto__);

(statearr_17031[(1)] = (1));

return statearr_17031;
});
var cljs$core$async$state_machine__14092__auto____1 = (function (state_16980){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_16980);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e17032){var ex__14095__auto__ = e17032;
var statearr_17033_18997 = state_16980;
(statearr_17033_18997[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_16980[(4)]))){
var statearr_17034_19000 = state_16980;
(statearr_17034_19000[(1)] = cljs.core.first((state_16980[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__19006 = state_16980;
state_16980 = G__19006;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$state_machine__14092__auto__ = function(state_16980){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14092__auto____1.call(this,state_16980);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14092__auto____0;
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14092__auto____1;
return cljs$core$async$state_machine__14092__auto__;
})()
})();
var state__14288__auto__ = (function (){var statearr_17035 = f__14287__auto__();
(statearr_17035[(6)] = c__14286__auto___18973);

return statearr_17035;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
}));


return out;
}));

(cljs.core.async.filter_LT_.cljs$lang$maxFixedArity = 3);

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.remove_LT_ = (function cljs$core$async$remove_LT_(var_args){
var G__17037 = arguments.length;
switch (G__17037) {
case 2:
return cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$2 = (function (p,ch){
return cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$3(p,ch,null);
}));

(cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$3 = (function (p,ch,buf_or_n){
return cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$3(cljs.core.complement(p),ch,buf_or_n);
}));

(cljs.core.async.remove_LT_.cljs$lang$maxFixedArity = 3);

cljs.core.async.mapcat_STAR_ = (function cljs$core$async$mapcat_STAR_(f,in$,out){
var c__14286__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = (function (state_17120){
var state_val_17121 = (state_17120[(1)]);
if((state_val_17121 === (7))){
var inst_17116 = (state_17120[(2)]);
var state_17120__$1 = state_17120;
var statearr_17123_19069 = state_17120__$1;
(statearr_17123_19069[(2)] = inst_17116);

(statearr_17123_19069[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17121 === (20))){
var inst_17086 = (state_17120[(7)]);
var inst_17097 = (state_17120[(2)]);
var inst_17098 = cljs.core.next(inst_17086);
var inst_17071 = inst_17098;
var inst_17072 = null;
var inst_17073 = (0);
var inst_17074 = (0);
var state_17120__$1 = (function (){var statearr_17127 = state_17120;
(statearr_17127[(8)] = inst_17097);

(statearr_17127[(9)] = inst_17071);

(statearr_17127[(10)] = inst_17072);

(statearr_17127[(11)] = inst_17073);

(statearr_17127[(12)] = inst_17074);

return statearr_17127;
})();
var statearr_17128_19072 = state_17120__$1;
(statearr_17128_19072[(2)] = null);

(statearr_17128_19072[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17121 === (1))){
var state_17120__$1 = state_17120;
var statearr_17129_19076 = state_17120__$1;
(statearr_17129_19076[(2)] = null);

(statearr_17129_19076[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17121 === (4))){
var inst_17060 = (state_17120[(13)]);
var inst_17060__$1 = (state_17120[(2)]);
var inst_17061 = (inst_17060__$1 == null);
var state_17120__$1 = (function (){var statearr_17130 = state_17120;
(statearr_17130[(13)] = inst_17060__$1);

return statearr_17130;
})();
if(cljs.core.truth_(inst_17061)){
var statearr_17131_19087 = state_17120__$1;
(statearr_17131_19087[(1)] = (5));

} else {
var statearr_17132_19090 = state_17120__$1;
(statearr_17132_19090[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17121 === (15))){
var state_17120__$1 = state_17120;
var statearr_17136_19094 = state_17120__$1;
(statearr_17136_19094[(2)] = null);

(statearr_17136_19094[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17121 === (21))){
var state_17120__$1 = state_17120;
var statearr_17138_19099 = state_17120__$1;
(statearr_17138_19099[(2)] = null);

(statearr_17138_19099[(1)] = (23));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17121 === (13))){
var inst_17074 = (state_17120[(12)]);
var inst_17071 = (state_17120[(9)]);
var inst_17072 = (state_17120[(10)]);
var inst_17073 = (state_17120[(11)]);
var inst_17082 = (state_17120[(2)]);
var inst_17083 = (inst_17074 + (1));
var tmp17133 = inst_17073;
var tmp17134 = inst_17072;
var tmp17135 = inst_17071;
var inst_17071__$1 = tmp17135;
var inst_17072__$1 = tmp17134;
var inst_17073__$1 = tmp17133;
var inst_17074__$1 = inst_17083;
var state_17120__$1 = (function (){var statearr_17144 = state_17120;
(statearr_17144[(14)] = inst_17082);

(statearr_17144[(9)] = inst_17071__$1);

(statearr_17144[(10)] = inst_17072__$1);

(statearr_17144[(11)] = inst_17073__$1);

(statearr_17144[(12)] = inst_17074__$1);

return statearr_17144;
})();
var statearr_17145_19104 = state_17120__$1;
(statearr_17145_19104[(2)] = null);

(statearr_17145_19104[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17121 === (22))){
var state_17120__$1 = state_17120;
var statearr_17151_19105 = state_17120__$1;
(statearr_17151_19105[(2)] = null);

(statearr_17151_19105[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17121 === (6))){
var inst_17060 = (state_17120[(13)]);
var inst_17069 = (f.cljs$core$IFn$_invoke$arity$1 ? f.cljs$core$IFn$_invoke$arity$1(inst_17060) : f.call(null,inst_17060));
var inst_17070 = cljs.core.seq(inst_17069);
var inst_17071 = inst_17070;
var inst_17072 = null;
var inst_17073 = (0);
var inst_17074 = (0);
var state_17120__$1 = (function (){var statearr_17156 = state_17120;
(statearr_17156[(9)] = inst_17071);

(statearr_17156[(10)] = inst_17072);

(statearr_17156[(11)] = inst_17073);

(statearr_17156[(12)] = inst_17074);

return statearr_17156;
})();
var statearr_17164_19135 = state_17120__$1;
(statearr_17164_19135[(2)] = null);

(statearr_17164_19135[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17121 === (17))){
var inst_17086 = (state_17120[(7)]);
var inst_17090 = cljs.core.chunk_first(inst_17086);
var inst_17091 = cljs.core.chunk_rest(inst_17086);
var inst_17092 = cljs.core.count(inst_17090);
var inst_17071 = inst_17091;
var inst_17072 = inst_17090;
var inst_17073 = inst_17092;
var inst_17074 = (0);
var state_17120__$1 = (function (){var statearr_17165 = state_17120;
(statearr_17165[(9)] = inst_17071);

(statearr_17165[(10)] = inst_17072);

(statearr_17165[(11)] = inst_17073);

(statearr_17165[(12)] = inst_17074);

return statearr_17165;
})();
var statearr_17171_19161 = state_17120__$1;
(statearr_17171_19161[(2)] = null);

(statearr_17171_19161[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17121 === (3))){
var inst_17118 = (state_17120[(2)]);
var state_17120__$1 = state_17120;
return cljs.core.async.impl.ioc_helpers.return_chan(state_17120__$1,inst_17118);
} else {
if((state_val_17121 === (12))){
var inst_17106 = (state_17120[(2)]);
var state_17120__$1 = state_17120;
var statearr_17172_19174 = state_17120__$1;
(statearr_17172_19174[(2)] = inst_17106);

(statearr_17172_19174[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17121 === (2))){
var state_17120__$1 = state_17120;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_17120__$1,(4),in$);
} else {
if((state_val_17121 === (23))){
var inst_17114 = (state_17120[(2)]);
var state_17120__$1 = state_17120;
var statearr_17173_19196 = state_17120__$1;
(statearr_17173_19196[(2)] = inst_17114);

(statearr_17173_19196[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17121 === (19))){
var inst_17101 = (state_17120[(2)]);
var state_17120__$1 = state_17120;
var statearr_17174_19203 = state_17120__$1;
(statearr_17174_19203[(2)] = inst_17101);

(statearr_17174_19203[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17121 === (11))){
var inst_17071 = (state_17120[(9)]);
var inst_17086 = (state_17120[(7)]);
var inst_17086__$1 = cljs.core.seq(inst_17071);
var state_17120__$1 = (function (){var statearr_17178 = state_17120;
(statearr_17178[(7)] = inst_17086__$1);

return statearr_17178;
})();
if(inst_17086__$1){
var statearr_17181_19216 = state_17120__$1;
(statearr_17181_19216[(1)] = (14));

} else {
var statearr_17186_19221 = state_17120__$1;
(statearr_17186_19221[(1)] = (15));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17121 === (9))){
var inst_17108 = (state_17120[(2)]);
var inst_17109 = cljs.core.async.impl.protocols.closed_QMARK_(out);
var state_17120__$1 = (function (){var statearr_17188 = state_17120;
(statearr_17188[(15)] = inst_17108);

return statearr_17188;
})();
if(cljs.core.truth_(inst_17109)){
var statearr_17190_19223 = state_17120__$1;
(statearr_17190_19223[(1)] = (21));

} else {
var statearr_17191_19224 = state_17120__$1;
(statearr_17191_19224[(1)] = (22));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17121 === (5))){
var inst_17063 = cljs.core.async.close_BANG_(out);
var state_17120__$1 = state_17120;
var statearr_17195_19225 = state_17120__$1;
(statearr_17195_19225[(2)] = inst_17063);

(statearr_17195_19225[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17121 === (14))){
var inst_17086 = (state_17120[(7)]);
var inst_17088 = cljs.core.chunked_seq_QMARK_(inst_17086);
var state_17120__$1 = state_17120;
if(inst_17088){
var statearr_17197_19233 = state_17120__$1;
(statearr_17197_19233[(1)] = (17));

} else {
var statearr_17198_19237 = state_17120__$1;
(statearr_17198_19237[(1)] = (18));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17121 === (16))){
var inst_17104 = (state_17120[(2)]);
var state_17120__$1 = state_17120;
var statearr_17199_19245 = state_17120__$1;
(statearr_17199_19245[(2)] = inst_17104);

(statearr_17199_19245[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17121 === (10))){
var inst_17072 = (state_17120[(10)]);
var inst_17074 = (state_17120[(12)]);
var inst_17080 = cljs.core._nth(inst_17072,inst_17074);
var state_17120__$1 = state_17120;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17120__$1,(13),out,inst_17080);
} else {
if((state_val_17121 === (18))){
var inst_17086 = (state_17120[(7)]);
var inst_17095 = cljs.core.first(inst_17086);
var state_17120__$1 = state_17120;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17120__$1,(20),out,inst_17095);
} else {
if((state_val_17121 === (8))){
var inst_17074 = (state_17120[(12)]);
var inst_17073 = (state_17120[(11)]);
var inst_17077 = (inst_17074 < inst_17073);
var inst_17078 = inst_17077;
var state_17120__$1 = state_17120;
if(cljs.core.truth_(inst_17078)){
var statearr_17203_19257 = state_17120__$1;
(statearr_17203_19257[(1)] = (10));

} else {
var statearr_17204_19262 = state_17120__$1;
(statearr_17204_19262[(1)] = (11));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$mapcat_STAR__$_state_machine__14092__auto__ = null;
var cljs$core$async$mapcat_STAR__$_state_machine__14092__auto____0 = (function (){
var statearr_17205 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_17205[(0)] = cljs$core$async$mapcat_STAR__$_state_machine__14092__auto__);

(statearr_17205[(1)] = (1));

return statearr_17205;
});
var cljs$core$async$mapcat_STAR__$_state_machine__14092__auto____1 = (function (state_17120){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_17120);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e17206){var ex__14095__auto__ = e17206;
var statearr_17207_19269 = state_17120;
(statearr_17207_19269[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_17120[(4)]))){
var statearr_17208_19275 = state_17120;
(statearr_17208_19275[(1)] = cljs.core.first((state_17120[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__19282 = state_17120;
state_17120 = G__19282;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$mapcat_STAR__$_state_machine__14092__auto__ = function(state_17120){
switch(arguments.length){
case 0:
return cljs$core$async$mapcat_STAR__$_state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$mapcat_STAR__$_state_machine__14092__auto____1.call(this,state_17120);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$mapcat_STAR__$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mapcat_STAR__$_state_machine__14092__auto____0;
cljs$core$async$mapcat_STAR__$_state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mapcat_STAR__$_state_machine__14092__auto____1;
return cljs$core$async$mapcat_STAR__$_state_machine__14092__auto__;
})()
})();
var state__14288__auto__ = (function (){var statearr_17216 = f__14287__auto__();
(statearr_17216[(6)] = c__14286__auto__);

return statearr_17216;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
}));

return c__14286__auto__;
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.mapcat_LT_ = (function cljs$core$async$mapcat_LT_(var_args){
var G__17220 = arguments.length;
switch (G__17220) {
case 2:
return cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$2 = (function (f,in$){
return cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$3(f,in$,null);
}));

(cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$3 = (function (f,in$,buf_or_n){
var out = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(buf_or_n);
cljs.core.async.mapcat_STAR_(f,in$,out);

return out;
}));

(cljs.core.async.mapcat_LT_.cljs$lang$maxFixedArity = 3);

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.mapcat_GT_ = (function cljs$core$async$mapcat_GT_(var_args){
var G__17228 = arguments.length;
switch (G__17228) {
case 2:
return cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$2 = (function (f,out){
return cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$3(f,out,null);
}));

(cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$3 = (function (f,out,buf_or_n){
var in$ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(buf_or_n);
cljs.core.async.mapcat_STAR_(f,in$,out);

return in$;
}));

(cljs.core.async.mapcat_GT_.cljs$lang$maxFixedArity = 3);

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.unique = (function cljs$core$async$unique(var_args){
var G__17242 = arguments.length;
switch (G__17242) {
case 1:
return cljs.core.async.unique.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.unique.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.unique.cljs$core$IFn$_invoke$arity$1 = (function (ch){
return cljs.core.async.unique.cljs$core$IFn$_invoke$arity$2(ch,null);
}));

(cljs.core.async.unique.cljs$core$IFn$_invoke$arity$2 = (function (ch,buf_or_n){
var out = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(buf_or_n);
var c__14286__auto___19299 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = (function (state_17267){
var state_val_17268 = (state_17267[(1)]);
if((state_val_17268 === (7))){
var inst_17262 = (state_17267[(2)]);
var state_17267__$1 = state_17267;
var statearr_17274_19301 = state_17267__$1;
(statearr_17274_19301[(2)] = inst_17262);

(statearr_17274_19301[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17268 === (1))){
var inst_17244 = null;
var state_17267__$1 = (function (){var statearr_17275 = state_17267;
(statearr_17275[(7)] = inst_17244);

return statearr_17275;
})();
var statearr_17276_19303 = state_17267__$1;
(statearr_17276_19303[(2)] = null);

(statearr_17276_19303[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17268 === (4))){
var inst_17247 = (state_17267[(8)]);
var inst_17247__$1 = (state_17267[(2)]);
var inst_17248 = (inst_17247__$1 == null);
var inst_17249 = cljs.core.not(inst_17248);
var state_17267__$1 = (function (){var statearr_17277 = state_17267;
(statearr_17277[(8)] = inst_17247__$1);

return statearr_17277;
})();
if(inst_17249){
var statearr_17278_19305 = state_17267__$1;
(statearr_17278_19305[(1)] = (5));

} else {
var statearr_17279_19307 = state_17267__$1;
(statearr_17279_19307[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17268 === (6))){
var state_17267__$1 = state_17267;
var statearr_17280_19313 = state_17267__$1;
(statearr_17280_19313[(2)] = null);

(statearr_17280_19313[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17268 === (3))){
var inst_17264 = (state_17267[(2)]);
var inst_17265 = cljs.core.async.close_BANG_(out);
var state_17267__$1 = (function (){var statearr_17282 = state_17267;
(statearr_17282[(9)] = inst_17264);

return statearr_17282;
})();
return cljs.core.async.impl.ioc_helpers.return_chan(state_17267__$1,inst_17265);
} else {
if((state_val_17268 === (2))){
var state_17267__$1 = state_17267;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_17267__$1,(4),ch);
} else {
if((state_val_17268 === (11))){
var inst_17247 = (state_17267[(8)]);
var inst_17256 = (state_17267[(2)]);
var inst_17244 = inst_17247;
var state_17267__$1 = (function (){var statearr_17288 = state_17267;
(statearr_17288[(10)] = inst_17256);

(statearr_17288[(7)] = inst_17244);

return statearr_17288;
})();
var statearr_17289_19321 = state_17267__$1;
(statearr_17289_19321[(2)] = null);

(statearr_17289_19321[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17268 === (9))){
var inst_17247 = (state_17267[(8)]);
var state_17267__$1 = state_17267;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17267__$1,(11),out,inst_17247);
} else {
if((state_val_17268 === (5))){
var inst_17247 = (state_17267[(8)]);
var inst_17244 = (state_17267[(7)]);
var inst_17251 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(inst_17247,inst_17244);
var state_17267__$1 = state_17267;
if(inst_17251){
var statearr_17295_19329 = state_17267__$1;
(statearr_17295_19329[(1)] = (8));

} else {
var statearr_17299_19330 = state_17267__$1;
(statearr_17299_19330[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17268 === (10))){
var inst_17259 = (state_17267[(2)]);
var state_17267__$1 = state_17267;
var statearr_17300_19332 = state_17267__$1;
(statearr_17300_19332[(2)] = inst_17259);

(statearr_17300_19332[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17268 === (8))){
var inst_17244 = (state_17267[(7)]);
var tmp17290 = inst_17244;
var inst_17244__$1 = tmp17290;
var state_17267__$1 = (function (){var statearr_17301 = state_17267;
(statearr_17301[(7)] = inst_17244__$1);

return statearr_17301;
})();
var statearr_17302_19342 = state_17267__$1;
(statearr_17302_19342[(2)] = null);

(statearr_17302_19342[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__14092__auto__ = null;
var cljs$core$async$state_machine__14092__auto____0 = (function (){
var statearr_17303 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_17303[(0)] = cljs$core$async$state_machine__14092__auto__);

(statearr_17303[(1)] = (1));

return statearr_17303;
});
var cljs$core$async$state_machine__14092__auto____1 = (function (state_17267){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_17267);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e17304){var ex__14095__auto__ = e17304;
var statearr_17305_19347 = state_17267;
(statearr_17305_19347[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_17267[(4)]))){
var statearr_17306_19349 = state_17267;
(statearr_17306_19349[(1)] = cljs.core.first((state_17267[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__19350 = state_17267;
state_17267 = G__19350;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$state_machine__14092__auto__ = function(state_17267){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14092__auto____1.call(this,state_17267);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14092__auto____0;
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14092__auto____1;
return cljs$core$async$state_machine__14092__auto__;
})()
})();
var state__14288__auto__ = (function (){var statearr_17308 = f__14287__auto__();
(statearr_17308[(6)] = c__14286__auto___19299);

return statearr_17308;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
}));


return out;
}));

(cljs.core.async.unique.cljs$lang$maxFixedArity = 2);

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.partition = (function cljs$core$async$partition(var_args){
var G__17313 = arguments.length;
switch (G__17313) {
case 2:
return cljs.core.async.partition.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.partition.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.partition.cljs$core$IFn$_invoke$arity$2 = (function (n,ch){
return cljs.core.async.partition.cljs$core$IFn$_invoke$arity$3(n,ch,null);
}));

(cljs.core.async.partition.cljs$core$IFn$_invoke$arity$3 = (function (n,ch,buf_or_n){
var out = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(buf_or_n);
var c__14286__auto___19368 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = (function (state_17360){
var state_val_17361 = (state_17360[(1)]);
if((state_val_17361 === (7))){
var inst_17355 = (state_17360[(2)]);
var state_17360__$1 = state_17360;
var statearr_17365_19369 = state_17360__$1;
(statearr_17365_19369[(2)] = inst_17355);

(statearr_17365_19369[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17361 === (1))){
var inst_17315 = (new Array(n));
var inst_17316 = inst_17315;
var inst_17317 = (0);
var state_17360__$1 = (function (){var statearr_17367 = state_17360;
(statearr_17367[(7)] = inst_17316);

(statearr_17367[(8)] = inst_17317);

return statearr_17367;
})();
var statearr_17368_19383 = state_17360__$1;
(statearr_17368_19383[(2)] = null);

(statearr_17368_19383[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17361 === (4))){
var inst_17321 = (state_17360[(9)]);
var inst_17321__$1 = (state_17360[(2)]);
var inst_17322 = (inst_17321__$1 == null);
var inst_17323 = cljs.core.not(inst_17322);
var state_17360__$1 = (function (){var statearr_17369 = state_17360;
(statearr_17369[(9)] = inst_17321__$1);

return statearr_17369;
})();
if(inst_17323){
var statearr_17370_19391 = state_17360__$1;
(statearr_17370_19391[(1)] = (5));

} else {
var statearr_17371_19399 = state_17360__$1;
(statearr_17371_19399[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17361 === (15))){
var inst_17349 = (state_17360[(2)]);
var state_17360__$1 = state_17360;
var statearr_17372_19405 = state_17360__$1;
(statearr_17372_19405[(2)] = inst_17349);

(statearr_17372_19405[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17361 === (13))){
var state_17360__$1 = state_17360;
var statearr_17373_19412 = state_17360__$1;
(statearr_17373_19412[(2)] = null);

(statearr_17373_19412[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17361 === (6))){
var inst_17317 = (state_17360[(8)]);
var inst_17345 = (inst_17317 > (0));
var state_17360__$1 = state_17360;
if(cljs.core.truth_(inst_17345)){
var statearr_17381_19415 = state_17360__$1;
(statearr_17381_19415[(1)] = (12));

} else {
var statearr_17382_19416 = state_17360__$1;
(statearr_17382_19416[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17361 === (3))){
var inst_17357 = (state_17360[(2)]);
var state_17360__$1 = state_17360;
return cljs.core.async.impl.ioc_helpers.return_chan(state_17360__$1,inst_17357);
} else {
if((state_val_17361 === (12))){
var inst_17316 = (state_17360[(7)]);
var inst_17347 = cljs.core.vec(inst_17316);
var state_17360__$1 = state_17360;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17360__$1,(15),out,inst_17347);
} else {
if((state_val_17361 === (2))){
var state_17360__$1 = state_17360;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_17360__$1,(4),ch);
} else {
if((state_val_17361 === (11))){
var inst_17339 = (state_17360[(2)]);
var inst_17340 = (new Array(n));
var inst_17316 = inst_17340;
var inst_17317 = (0);
var state_17360__$1 = (function (){var statearr_17386 = state_17360;
(statearr_17386[(10)] = inst_17339);

(statearr_17386[(7)] = inst_17316);

(statearr_17386[(8)] = inst_17317);

return statearr_17386;
})();
var statearr_17387_19441 = state_17360__$1;
(statearr_17387_19441[(2)] = null);

(statearr_17387_19441[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17361 === (9))){
var inst_17316 = (state_17360[(7)]);
var inst_17337 = cljs.core.vec(inst_17316);
var state_17360__$1 = state_17360;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17360__$1,(11),out,inst_17337);
} else {
if((state_val_17361 === (5))){
var inst_17316 = (state_17360[(7)]);
var inst_17317 = (state_17360[(8)]);
var inst_17321 = (state_17360[(9)]);
var inst_17332 = (state_17360[(11)]);
var inst_17326 = (inst_17316[inst_17317] = inst_17321);
var inst_17332__$1 = (inst_17317 + (1));
var inst_17333 = (inst_17332__$1 < n);
var state_17360__$1 = (function (){var statearr_17390 = state_17360;
(statearr_17390[(12)] = inst_17326);

(statearr_17390[(11)] = inst_17332__$1);

return statearr_17390;
})();
if(cljs.core.truth_(inst_17333)){
var statearr_17391_19453 = state_17360__$1;
(statearr_17391_19453[(1)] = (8));

} else {
var statearr_17392_19455 = state_17360__$1;
(statearr_17392_19455[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17361 === (14))){
var inst_17352 = (state_17360[(2)]);
var inst_17353 = cljs.core.async.close_BANG_(out);
var state_17360__$1 = (function (){var statearr_17397 = state_17360;
(statearr_17397[(13)] = inst_17352);

return statearr_17397;
})();
var statearr_17398_19472 = state_17360__$1;
(statearr_17398_19472[(2)] = inst_17353);

(statearr_17398_19472[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17361 === (10))){
var inst_17343 = (state_17360[(2)]);
var state_17360__$1 = state_17360;
var statearr_17399_19482 = state_17360__$1;
(statearr_17399_19482[(2)] = inst_17343);

(statearr_17399_19482[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17361 === (8))){
var inst_17316 = (state_17360[(7)]);
var inst_17332 = (state_17360[(11)]);
var tmp17393 = inst_17316;
var inst_17316__$1 = tmp17393;
var inst_17317 = inst_17332;
var state_17360__$1 = (function (){var statearr_17400 = state_17360;
(statearr_17400[(7)] = inst_17316__$1);

(statearr_17400[(8)] = inst_17317);

return statearr_17400;
})();
var statearr_17401_19491 = state_17360__$1;
(statearr_17401_19491[(2)] = null);

(statearr_17401_19491[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__14092__auto__ = null;
var cljs$core$async$state_machine__14092__auto____0 = (function (){
var statearr_17402 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_17402[(0)] = cljs$core$async$state_machine__14092__auto__);

(statearr_17402[(1)] = (1));

return statearr_17402;
});
var cljs$core$async$state_machine__14092__auto____1 = (function (state_17360){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_17360);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e17403){var ex__14095__auto__ = e17403;
var statearr_17404_19496 = state_17360;
(statearr_17404_19496[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_17360[(4)]))){
var statearr_17405_19497 = state_17360;
(statearr_17405_19497[(1)] = cljs.core.first((state_17360[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__19498 = state_17360;
state_17360 = G__19498;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$state_machine__14092__auto__ = function(state_17360){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14092__auto____1.call(this,state_17360);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14092__auto____0;
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14092__auto____1;
return cljs$core$async$state_machine__14092__auto__;
})()
})();
var state__14288__auto__ = (function (){var statearr_17409 = f__14287__auto__();
(statearr_17409[(6)] = c__14286__auto___19368);

return statearr_17409;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
}));


return out;
}));

(cljs.core.async.partition.cljs$lang$maxFixedArity = 3);

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.partition_by = (function cljs$core$async$partition_by(var_args){
var G__17411 = arguments.length;
switch (G__17411) {
case 2:
return cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",arguments.length].join("")));

}
});

(cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$2 = (function (f,ch){
return cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$3(f,ch,null);
}));

(cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$3 = (function (f,ch,buf_or_n){
var out = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1(buf_or_n);
var c__14286__auto___19501 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14287__auto__ = (function (){var switch__14091__auto__ = (function (state_17460){
var state_val_17461 = (state_17460[(1)]);
if((state_val_17461 === (7))){
var inst_17456 = (state_17460[(2)]);
var state_17460__$1 = state_17460;
var statearr_17464_19502 = state_17460__$1;
(statearr_17464_19502[(2)] = inst_17456);

(statearr_17464_19502[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17461 === (1))){
var inst_17415 = [];
var inst_17416 = inst_17415;
var inst_17417 = new cljs.core.Keyword("cljs.core.async","nothing","cljs.core.async/nothing",-69252123);
var state_17460__$1 = (function (){var statearr_17465 = state_17460;
(statearr_17465[(7)] = inst_17416);

(statearr_17465[(8)] = inst_17417);

return statearr_17465;
})();
var statearr_17466_19506 = state_17460__$1;
(statearr_17466_19506[(2)] = null);

(statearr_17466_19506[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17461 === (4))){
var inst_17420 = (state_17460[(9)]);
var inst_17420__$1 = (state_17460[(2)]);
var inst_17421 = (inst_17420__$1 == null);
var inst_17422 = cljs.core.not(inst_17421);
var state_17460__$1 = (function (){var statearr_17468 = state_17460;
(statearr_17468[(9)] = inst_17420__$1);

return statearr_17468;
})();
if(inst_17422){
var statearr_17469_19508 = state_17460__$1;
(statearr_17469_19508[(1)] = (5));

} else {
var statearr_17470_19509 = state_17460__$1;
(statearr_17470_19509[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17461 === (15))){
var inst_17416 = (state_17460[(7)]);
var inst_17448 = cljs.core.vec(inst_17416);
var state_17460__$1 = state_17460;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17460__$1,(18),out,inst_17448);
} else {
if((state_val_17461 === (13))){
var inst_17443 = (state_17460[(2)]);
var state_17460__$1 = state_17460;
var statearr_17471_19514 = state_17460__$1;
(statearr_17471_19514[(2)] = inst_17443);

(statearr_17471_19514[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17461 === (6))){
var inst_17416 = (state_17460[(7)]);
var inst_17445 = inst_17416.length;
var inst_17446 = (inst_17445 > (0));
var state_17460__$1 = state_17460;
if(cljs.core.truth_(inst_17446)){
var statearr_17475_19518 = state_17460__$1;
(statearr_17475_19518[(1)] = (15));

} else {
var statearr_17476_19519 = state_17460__$1;
(statearr_17476_19519[(1)] = (16));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17461 === (17))){
var inst_17453 = (state_17460[(2)]);
var inst_17454 = cljs.core.async.close_BANG_(out);
var state_17460__$1 = (function (){var statearr_17480 = state_17460;
(statearr_17480[(10)] = inst_17453);

return statearr_17480;
})();
var statearr_17481_19520 = state_17460__$1;
(statearr_17481_19520[(2)] = inst_17454);

(statearr_17481_19520[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17461 === (3))){
var inst_17458 = (state_17460[(2)]);
var state_17460__$1 = state_17460;
return cljs.core.async.impl.ioc_helpers.return_chan(state_17460__$1,inst_17458);
} else {
if((state_val_17461 === (12))){
var inst_17416 = (state_17460[(7)]);
var inst_17436 = cljs.core.vec(inst_17416);
var state_17460__$1 = state_17460;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17460__$1,(14),out,inst_17436);
} else {
if((state_val_17461 === (2))){
var state_17460__$1 = state_17460;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_17460__$1,(4),ch);
} else {
if((state_val_17461 === (11))){
var inst_17416 = (state_17460[(7)]);
var inst_17420 = (state_17460[(9)]);
var inst_17425 = (state_17460[(11)]);
var inst_17433 = inst_17416.push(inst_17420);
var tmp17483 = inst_17416;
var inst_17416__$1 = tmp17483;
var inst_17417 = inst_17425;
var state_17460__$1 = (function (){var statearr_17485 = state_17460;
(statearr_17485[(12)] = inst_17433);

(statearr_17485[(7)] = inst_17416__$1);

(statearr_17485[(8)] = inst_17417);

return statearr_17485;
})();
var statearr_17490_19522 = state_17460__$1;
(statearr_17490_19522[(2)] = null);

(statearr_17490_19522[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17461 === (9))){
var inst_17417 = (state_17460[(8)]);
var inst_17429 = cljs.core.keyword_identical_QMARK_(inst_17417,new cljs.core.Keyword("cljs.core.async","nothing","cljs.core.async/nothing",-69252123));
var state_17460__$1 = state_17460;
var statearr_17491_19523 = state_17460__$1;
(statearr_17491_19523[(2)] = inst_17429);

(statearr_17491_19523[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17461 === (5))){
var inst_17420 = (state_17460[(9)]);
var inst_17425 = (state_17460[(11)]);
var inst_17417 = (state_17460[(8)]);
var inst_17426 = (state_17460[(13)]);
var inst_17425__$1 = (f.cljs$core$IFn$_invoke$arity$1 ? f.cljs$core$IFn$_invoke$arity$1(inst_17420) : f.call(null,inst_17420));
var inst_17426__$1 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(inst_17425__$1,inst_17417);
var state_17460__$1 = (function (){var statearr_17492 = state_17460;
(statearr_17492[(11)] = inst_17425__$1);

(statearr_17492[(13)] = inst_17426__$1);

return statearr_17492;
})();
if(inst_17426__$1){
var statearr_17493_19527 = state_17460__$1;
(statearr_17493_19527[(1)] = (8));

} else {
var statearr_17494_19529 = state_17460__$1;
(statearr_17494_19529[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17461 === (14))){
var inst_17420 = (state_17460[(9)]);
var inst_17425 = (state_17460[(11)]);
var inst_17438 = (state_17460[(2)]);
var inst_17439 = [];
var inst_17440 = inst_17439.push(inst_17420);
var inst_17416 = inst_17439;
var inst_17417 = inst_17425;
var state_17460__$1 = (function (){var statearr_17495 = state_17460;
(statearr_17495[(14)] = inst_17438);

(statearr_17495[(15)] = inst_17440);

(statearr_17495[(7)] = inst_17416);

(statearr_17495[(8)] = inst_17417);

return statearr_17495;
})();
var statearr_17496_19531 = state_17460__$1;
(statearr_17496_19531[(2)] = null);

(statearr_17496_19531[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17461 === (16))){
var state_17460__$1 = state_17460;
var statearr_17497_19533 = state_17460__$1;
(statearr_17497_19533[(2)] = null);

(statearr_17497_19533[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17461 === (10))){
var inst_17431 = (state_17460[(2)]);
var state_17460__$1 = state_17460;
if(cljs.core.truth_(inst_17431)){
var statearr_17498_19536 = state_17460__$1;
(statearr_17498_19536[(1)] = (11));

} else {
var statearr_17499_19537 = state_17460__$1;
(statearr_17499_19537[(1)] = (12));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17461 === (18))){
var inst_17450 = (state_17460[(2)]);
var state_17460__$1 = state_17460;
var statearr_17500_19538 = state_17460__$1;
(statearr_17500_19538[(2)] = inst_17450);

(statearr_17500_19538[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17461 === (8))){
var inst_17426 = (state_17460[(13)]);
var state_17460__$1 = state_17460;
var statearr_17501_19546 = state_17460__$1;
(statearr_17501_19546[(2)] = inst_17426);

(statearr_17501_19546[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});
return (function() {
var cljs$core$async$state_machine__14092__auto__ = null;
var cljs$core$async$state_machine__14092__auto____0 = (function (){
var statearr_17502 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_17502[(0)] = cljs$core$async$state_machine__14092__auto__);

(statearr_17502[(1)] = (1));

return statearr_17502;
});
var cljs$core$async$state_machine__14092__auto____1 = (function (state_17460){
while(true){
var ret_value__14093__auto__ = (function (){try{while(true){
var result__14094__auto__ = switch__14091__auto__(state_17460);
if(cljs.core.keyword_identical_QMARK_(result__14094__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14094__auto__;
}
break;
}
}catch (e17503){var ex__14095__auto__ = e17503;
var statearr_17504_19554 = state_17460;
(statearr_17504_19554[(2)] = ex__14095__auto__);


if(cljs.core.seq((state_17460[(4)]))){
var statearr_17505_19556 = state_17460;
(statearr_17505_19556[(1)] = cljs.core.first((state_17460[(4)])));

} else {
throw ex__14095__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14093__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__19563 = state_17460;
state_17460 = G__19563;
continue;
} else {
return ret_value__14093__auto__;
}
break;
}
});
cljs$core$async$state_machine__14092__auto__ = function(state_17460){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14092__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14092__auto____1.call(this,state_17460);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14092__auto____0;
cljs$core$async$state_machine__14092__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14092__auto____1;
return cljs$core$async$state_machine__14092__auto__;
})()
})();
var state__14288__auto__ = (function (){var statearr_17506 = f__14287__auto__();
(statearr_17506[(6)] = c__14286__auto___19501);

return statearr_17506;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14288__auto__);
}));


return out;
}));

(cljs.core.async.partition_by.cljs$lang$maxFixedArity = 3);


//# sourceMappingURL=cljs.core.async.js.map
