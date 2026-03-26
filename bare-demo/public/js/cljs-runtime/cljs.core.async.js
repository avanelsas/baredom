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
cljs.core.async.t_cljs$core$async14529 = (function (f,blockable,meta14530){
this.f = f;
this.blockable = blockable;
this.meta14530 = meta14530;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async14529.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_14531,meta14530__$1){
var self__ = this;
var _14531__$1 = this;
return (new cljs.core.async.t_cljs$core$async14529(self__.f,self__.blockable,meta14530__$1));
}));

(cljs.core.async.t_cljs$core$async14529.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_14531){
var self__ = this;
var _14531__$1 = this;
return self__.meta14530;
}));

(cljs.core.async.t_cljs$core$async14529.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async14529.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return true;
}));

(cljs.core.async.t_cljs$core$async14529.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.blockable;
}));

(cljs.core.async.t_cljs$core$async14529.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.f;
}));

(cljs.core.async.t_cljs$core$async14529.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"blockable","blockable",-28395259,null),new cljs.core.Symbol(null,"meta14530","meta14530",798723556,null)], null);
}));

(cljs.core.async.t_cljs$core$async14529.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async14529.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async14529");

(cljs.core.async.t_cljs$core$async14529.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async14529");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async14529.
 */
cljs.core.async.__GT_t_cljs$core$async14529 = (function cljs$core$async$__GT_t_cljs$core$async14529(f,blockable,meta14530){
return (new cljs.core.async.t_cljs$core$async14529(f,blockable,meta14530));
});


cljs.core.async.fn_handler = (function cljs$core$async$fn_handler(var_args){
var G__14504 = arguments.length;
switch (G__14504) {
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
return (new cljs.core.async.t_cljs$core$async14529(f,blockable,cljs.core.PersistentArrayMap.EMPTY));
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
var G__14560 = arguments.length;
switch (G__14560) {
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
var G__14573 = arguments.length;
switch (G__14573) {
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
var G__14582 = arguments.length;
switch (G__14582) {
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
var val_17985 = cljs.core.deref(ret);
if(cljs.core.truth_(on_caller_QMARK_)){
(fn1.cljs$core$IFn$_invoke$arity$1 ? fn1.cljs$core$IFn$_invoke$arity$1(val_17985) : fn1.call(null,val_17985));
} else {
cljs.core.async.impl.dispatch.run((function (){
return (fn1.cljs$core$IFn$_invoke$arity$1 ? fn1.cljs$core$IFn$_invoke$arity$1(val_17985) : fn1.call(null,val_17985));
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
var G__14595 = arguments.length;
switch (G__14595) {
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
var n__5741__auto___17991 = n;
var x_17992 = (0);
while(true){
if((x_17992 < n__5741__auto___17991)){
(a[x_17992] = x_17992);

var G__17993 = (x_17992 + (1));
x_17992 = G__17993;
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
cljs.core.async.t_cljs$core$async14627 = (function (flag,meta14628){
this.flag = flag;
this.meta14628 = meta14628;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async14627.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_14629,meta14628__$1){
var self__ = this;
var _14629__$1 = this;
return (new cljs.core.async.t_cljs$core$async14627(self__.flag,meta14628__$1));
}));

(cljs.core.async.t_cljs$core$async14627.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_14629){
var self__ = this;
var _14629__$1 = this;
return self__.meta14628;
}));

(cljs.core.async.t_cljs$core$async14627.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async14627.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.deref(self__.flag);
}));

(cljs.core.async.t_cljs$core$async14627.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return true;
}));

(cljs.core.async.t_cljs$core$async14627.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_(self__.flag,null);

return true;
}));

(cljs.core.async.t_cljs$core$async14627.getBasis = (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"flag","flag",-1565787888,null),new cljs.core.Symbol(null,"meta14628","meta14628",1530968827,null)], null);
}));

(cljs.core.async.t_cljs$core$async14627.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async14627.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async14627");

(cljs.core.async.t_cljs$core$async14627.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async14627");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async14627.
 */
cljs.core.async.__GT_t_cljs$core$async14627 = (function cljs$core$async$__GT_t_cljs$core$async14627(flag,meta14628){
return (new cljs.core.async.t_cljs$core$async14627(flag,meta14628));
});


cljs.core.async.alt_flag = (function cljs$core$async$alt_flag(){
var flag = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(true);
return (new cljs.core.async.t_cljs$core$async14627(flag,cljs.core.PersistentArrayMap.EMPTY));
});

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async14643 = (function (flag,cb,meta14644){
this.flag = flag;
this.cb = cb;
this.meta14644 = meta14644;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async14643.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_14645,meta14644__$1){
var self__ = this;
var _14645__$1 = this;
return (new cljs.core.async.t_cljs$core$async14643(self__.flag,self__.cb,meta14644__$1));
}));

(cljs.core.async.t_cljs$core$async14643.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_14645){
var self__ = this;
var _14645__$1 = this;
return self__.meta14644;
}));

(cljs.core.async.t_cljs$core$async14643.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async14643.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.active_QMARK_(self__.flag);
}));

(cljs.core.async.t_cljs$core$async14643.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return true;
}));

(cljs.core.async.t_cljs$core$async14643.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.async.impl.protocols.commit(self__.flag);

return self__.cb;
}));

(cljs.core.async.t_cljs$core$async14643.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"flag","flag",-1565787888,null),new cljs.core.Symbol(null,"cb","cb",-2064487928,null),new cljs.core.Symbol(null,"meta14644","meta14644",1780462782,null)], null);
}));

(cljs.core.async.t_cljs$core$async14643.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async14643.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async14643");

(cljs.core.async.t_cljs$core$async14643.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async14643");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async14643.
 */
cljs.core.async.__GT_t_cljs$core$async14643 = (function cljs$core$async$__GT_t_cljs$core$async14643(flag,cb,meta14644){
return (new cljs.core.async.t_cljs$core$async14643(flag,cb,meta14644));
});


cljs.core.async.alt_handler = (function cljs$core$async$alt_handler(flag,cb){
return (new cljs.core.async.t_cljs$core$async14643(flag,cb,cljs.core.PersistentArrayMap.EMPTY));
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
var port_18007 = cljs.core.nth.cljs$core$IFn$_invoke$arity$2(ports__$1,i);
if(cljs.core.vector_QMARK_(port_18007)){
if((!(((port_18007.cljs$core$IFn$_invoke$arity$1 ? port_18007.cljs$core$IFn$_invoke$arity$1((1)) : port_18007.call(null,(1))) == null)))){
} else {
throw (new Error((""+"Assert failed: "+"can't put nil on channel"+"\n"+"(some? (port 1))")));
}
} else {
}

var G__18009 = (i + (1));
i = G__18009;
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
return (function (p1__14661_SHARP_){
var G__14713 = new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__14661_SHARP_,wport], null);
return (fret.cljs$core$IFn$_invoke$arity$1 ? fret.cljs$core$IFn$_invoke$arity$1(G__14713) : fret.call(null,G__14713));
});})(i,val,idx,port,wport,flag,ports__$1,n,_,idxs,priority))
));
})():cljs.core.async.impl.protocols.take_BANG_(port,cljs.core.async.alt_handler(flag,((function (i,idx,port,wport,flag,ports__$1,n,_,idxs,priority){
return (function (p1__14662_SHARP_){
var G__14714 = new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__14662_SHARP_,port], null);
return (fret.cljs$core$IFn$_invoke$arity$1 ? fret.cljs$core$IFn$_invoke$arity$1(G__14714) : fret.call(null,G__14714));
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
var G__18022 = (i + (1));
i = G__18022;
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
var len__5876__auto___18023 = arguments.length;
var i__5877__auto___18024 = (0);
while(true){
if((i__5877__auto___18024 < len__5876__auto___18023)){
args__5882__auto__.push((arguments[i__5877__auto___18024]));

var G__18025 = (i__5877__auto___18024 + (1));
i__5877__auto___18024 = G__18025;
continue;
} else {
}
break;
}

var argseq__5883__auto__ = ((((1) < args__5882__auto__.length))?(new cljs.core.IndexedSeq(args__5882__auto__.slice((1)),(0),null)):null);
return cljs.core.async.alts_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__5883__auto__);
});

(cljs.core.async.alts_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (ports,p__14737){
var map__14739 = p__14737;
var map__14739__$1 = cljs.core.__destructure_map(map__14739);
var opts = map__14739__$1;
throw (new Error("alts! used not in (go ...) block"));
}));

(cljs.core.async.alts_BANG_.cljs$lang$maxFixedArity = (1));

/** @this {Function} */
(cljs.core.async.alts_BANG_.cljs$lang$applyTo = (function (seq14726){
var G__14728 = cljs.core.first(seq14726);
var seq14726__$1 = cljs.core.next(seq14726);
var self__5861__auto__ = this;
return self__5861__auto__.cljs$core$IFn$_invoke$arity$variadic(G__14728,seq14726__$1);
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
var G__14771 = arguments.length;
switch (G__14771) {
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
var c__14373__auto___18037 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = (function (state_14834){
var state_val_14836 = (state_14834[(1)]);
if((state_val_14836 === (7))){
var inst_14826 = (state_14834[(2)]);
var state_14834__$1 = state_14834;
var statearr_14865_18039 = state_14834__$1;
(statearr_14865_18039[(2)] = inst_14826);

(statearr_14865_18039[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14836 === (1))){
var state_14834__$1 = state_14834;
var statearr_14867_18041 = state_14834__$1;
(statearr_14867_18041[(2)] = null);

(statearr_14867_18041[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14836 === (4))){
var inst_14800 = (state_14834[(7)]);
var inst_14800__$1 = (state_14834[(2)]);
var inst_14806 = (inst_14800__$1 == null);
var state_14834__$1 = (function (){var statearr_14869 = state_14834;
(statearr_14869[(7)] = inst_14800__$1);

return statearr_14869;
})();
if(cljs.core.truth_(inst_14806)){
var statearr_14872_18044 = state_14834__$1;
(statearr_14872_18044[(1)] = (5));

} else {
var statearr_14874_18045 = state_14834__$1;
(statearr_14874_18045[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14836 === (13))){
var state_14834__$1 = state_14834;
var statearr_14877_18047 = state_14834__$1;
(statearr_14877_18047[(2)] = null);

(statearr_14877_18047[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14836 === (6))){
var inst_14800 = (state_14834[(7)]);
var state_14834__$1 = state_14834;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_14834__$1,(11),to,inst_14800);
} else {
if((state_val_14836 === (3))){
var inst_14830 = (state_14834[(2)]);
var state_14834__$1 = state_14834;
return cljs.core.async.impl.ioc_helpers.return_chan(state_14834__$1,inst_14830);
} else {
if((state_val_14836 === (12))){
var state_14834__$1 = state_14834;
var statearr_14888_18053 = state_14834__$1;
(statearr_14888_18053[(2)] = null);

(statearr_14888_18053[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14836 === (2))){
var state_14834__$1 = state_14834;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_14834__$1,(4),from);
} else {
if((state_val_14836 === (11))){
var inst_14817 = (state_14834[(2)]);
var state_14834__$1 = state_14834;
if(cljs.core.truth_(inst_14817)){
var statearr_14901_18059 = state_14834__$1;
(statearr_14901_18059[(1)] = (12));

} else {
var statearr_14902_18060 = state_14834__$1;
(statearr_14902_18060[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14836 === (9))){
var state_14834__$1 = state_14834;
var statearr_14903_18064 = state_14834__$1;
(statearr_14903_18064[(2)] = null);

(statearr_14903_18064[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14836 === (5))){
var state_14834__$1 = state_14834;
if(cljs.core.truth_(close_QMARK_)){
var statearr_14904_18066 = state_14834__$1;
(statearr_14904_18066[(1)] = (8));

} else {
var statearr_14905_18067 = state_14834__$1;
(statearr_14905_18067[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14836 === (14))){
var inst_14823 = (state_14834[(2)]);
var state_14834__$1 = state_14834;
var statearr_14906_18069 = state_14834__$1;
(statearr_14906_18069[(2)] = inst_14823);

(statearr_14906_18069[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14836 === (10))){
var inst_14814 = (state_14834[(2)]);
var state_14834__$1 = state_14834;
var statearr_14907_18070 = state_14834__$1;
(statearr_14907_18070[(2)] = inst_14814);

(statearr_14907_18070[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14836 === (8))){
var inst_14810 = cljs.core.async.close_BANG_(to);
var state_14834__$1 = state_14834;
var statearr_14908_18071 = state_14834__$1;
(statearr_14908_18071[(2)] = inst_14810);

(statearr_14908_18071[(1)] = (10));


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
var cljs$core$async$state_machine__14014__auto__ = null;
var cljs$core$async$state_machine__14014__auto____0 = (function (){
var statearr_14912 = [null,null,null,null,null,null,null,null];
(statearr_14912[(0)] = cljs$core$async$state_machine__14014__auto__);

(statearr_14912[(1)] = (1));

return statearr_14912;
});
var cljs$core$async$state_machine__14014__auto____1 = (function (state_14834){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_14834);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e14916){var ex__14017__auto__ = e14916;
var statearr_14917_18079 = state_14834;
(statearr_14917_18079[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_14834[(4)]))){
var statearr_14918_18080 = state_14834;
(statearr_14918_18080[(1)] = cljs.core.first((state_14834[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18081 = state_14834;
state_14834 = G__18081;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$state_machine__14014__auto__ = function(state_14834){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14014__auto____1.call(this,state_14834);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14014__auto____0;
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14014__auto____1;
return cljs$core$async$state_machine__14014__auto__;
})()
})();
var state__14375__auto__ = (function (){var statearr_14920 = f__14374__auto__();
(statearr_14920[(6)] = c__14373__auto___18037);

return statearr_14920;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
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
var process__$1 = (function (p__14948){
var vec__14949 = p__14948;
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__14949,(0),null);
var p = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__14949,(1),null);
var job = vec__14949;
if((job == null)){
cljs.core.async.close_BANG_(results);

return null;
} else {
var res = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3((1),xf,ex_handler);
var c__14373__auto___18083 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = (function (state_14958){
var state_val_14959 = (state_14958[(1)]);
if((state_val_14959 === (1))){
var state_14958__$1 = state_14958;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_14958__$1,(2),res,v);
} else {
if((state_val_14959 === (2))){
var inst_14955 = (state_14958[(2)]);
var inst_14956 = cljs.core.async.close_BANG_(res);
var state_14958__$1 = (function (){var statearr_14974 = state_14958;
(statearr_14974[(7)] = inst_14955);

return statearr_14974;
})();
return cljs.core.async.impl.ioc_helpers.return_chan(state_14958__$1,inst_14956);
} else {
return null;
}
}
});
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____0 = (function (){
var statearr_14980 = [null,null,null,null,null,null,null,null];
(statearr_14980[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__);

(statearr_14980[(1)] = (1));

return statearr_14980;
});
var cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____1 = (function (state_14958){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_14958);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e14983){var ex__14017__auto__ = e14983;
var statearr_14984_18087 = state_14958;
(statearr_14984_18087[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_14958[(4)]))){
var statearr_14989_18088 = state_14958;
(statearr_14989_18088[(1)] = cljs.core.first((state_14958[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18093 = state_14958;
state_14958 = G__18093;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__ = function(state_14958){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____1.call(this,state_14958);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__;
})()
})();
var state__14375__auto__ = (function (){var statearr_15001 = f__14374__auto__();
(statearr_15001[(6)] = c__14373__auto___18083);

return statearr_15001;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
}));


cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2(p,res);

return true;
}
});
var async = (function (p__15006){
var vec__15007 = p__15006;
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__15007,(0),null);
var p = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__15007,(1),null);
var job = vec__15007;
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
var n__5741__auto___18094 = n;
var __18095 = (0);
while(true){
if((__18095 < n__5741__auto___18094)){
var G__15011_18096 = type;
var G__15011_18097__$1 = (((G__15011_18096 instanceof cljs.core.Keyword))?G__15011_18096.fqn:null);
switch (G__15011_18097__$1) {
case "compute":
var c__14373__auto___18099 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run(((function (__18095,c__14373__auto___18099,G__15011_18096,G__15011_18097__$1,n__5741__auto___18094,jobs,results,process__$1,async){
return (function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = ((function (__18095,c__14373__auto___18099,G__15011_18096,G__15011_18097__$1,n__5741__auto___18094,jobs,results,process__$1,async){
return (function (state_15049){
var state_val_15050 = (state_15049[(1)]);
if((state_val_15050 === (1))){
var state_15049__$1 = state_15049;
var statearr_15060_18100 = state_15049__$1;
(statearr_15060_18100[(2)] = null);

(statearr_15060_18100[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15050 === (2))){
var state_15049__$1 = state_15049;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15049__$1,(4),jobs);
} else {
if((state_val_15050 === (3))){
var inst_15046 = (state_15049[(2)]);
var state_15049__$1 = state_15049;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15049__$1,inst_15046);
} else {
if((state_val_15050 === (4))){
var inst_15031 = (state_15049[(2)]);
var inst_15032 = process__$1(inst_15031);
var state_15049__$1 = state_15049;
if(cljs.core.truth_(inst_15032)){
var statearr_15078_18108 = state_15049__$1;
(statearr_15078_18108[(1)] = (5));

} else {
var statearr_15082_18109 = state_15049__$1;
(statearr_15082_18109[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15050 === (5))){
var state_15049__$1 = state_15049;
var statearr_15083_18110 = state_15049__$1;
(statearr_15083_18110[(2)] = null);

(statearr_15083_18110[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15050 === (6))){
var state_15049__$1 = state_15049;
var statearr_15084_18114 = state_15049__$1;
(statearr_15084_18114[(2)] = null);

(statearr_15084_18114[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15050 === (7))){
var inst_15040 = (state_15049[(2)]);
var state_15049__$1 = state_15049;
var statearr_15089_18116 = state_15049__$1;
(statearr_15089_18116[(2)] = inst_15040);

(statearr_15089_18116[(1)] = (3));


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
});})(__18095,c__14373__auto___18099,G__15011_18096,G__15011_18097__$1,n__5741__auto___18094,jobs,results,process__$1,async))
;
return ((function (__18095,switch__14013__auto__,c__14373__auto___18099,G__15011_18096,G__15011_18097__$1,n__5741__auto___18094,jobs,results,process__$1,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____0 = (function (){
var statearr_15094 = [null,null,null,null,null,null,null];
(statearr_15094[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__);

(statearr_15094[(1)] = (1));

return statearr_15094;
});
var cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____1 = (function (state_15049){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_15049);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e15097){var ex__14017__auto__ = e15097;
var statearr_15098_18117 = state_15049;
(statearr_15098_18117[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_15049[(4)]))){
var statearr_15102_18118 = state_15049;
(statearr_15102_18118[(1)] = cljs.core.first((state_15049[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18119 = state_15049;
state_15049 = G__18119;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__ = function(state_15049){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____1.call(this,state_15049);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__;
})()
;})(__18095,switch__14013__auto__,c__14373__auto___18099,G__15011_18096,G__15011_18097__$1,n__5741__auto___18094,jobs,results,process__$1,async))
})();
var state__14375__auto__ = (function (){var statearr_15110 = f__14374__auto__();
(statearr_15110[(6)] = c__14373__auto___18099);

return statearr_15110;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
});})(__18095,c__14373__auto___18099,G__15011_18096,G__15011_18097__$1,n__5741__auto___18094,jobs,results,process__$1,async))
);


break;
case "async":
var c__14373__auto___18121 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run(((function (__18095,c__14373__auto___18121,G__15011_18096,G__15011_18097__$1,n__5741__auto___18094,jobs,results,process__$1,async){
return (function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = ((function (__18095,c__14373__auto___18121,G__15011_18096,G__15011_18097__$1,n__5741__auto___18094,jobs,results,process__$1,async){
return (function (state_15126){
var state_val_15127 = (state_15126[(1)]);
if((state_val_15127 === (1))){
var state_15126__$1 = state_15126;
var statearr_15132_18122 = state_15126__$1;
(statearr_15132_18122[(2)] = null);

(statearr_15132_18122[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15127 === (2))){
var state_15126__$1 = state_15126;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15126__$1,(4),jobs);
} else {
if((state_val_15127 === (3))){
var inst_15123 = (state_15126[(2)]);
var state_15126__$1 = state_15126;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15126__$1,inst_15123);
} else {
if((state_val_15127 === (4))){
var inst_15115 = (state_15126[(2)]);
var inst_15116 = async(inst_15115);
var state_15126__$1 = state_15126;
if(cljs.core.truth_(inst_15116)){
var statearr_15139_18124 = state_15126__$1;
(statearr_15139_18124[(1)] = (5));

} else {
var statearr_15140_18125 = state_15126__$1;
(statearr_15140_18125[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15127 === (5))){
var state_15126__$1 = state_15126;
var statearr_15142_18126 = state_15126__$1;
(statearr_15142_18126[(2)] = null);

(statearr_15142_18126[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15127 === (6))){
var state_15126__$1 = state_15126;
var statearr_15147_18130 = state_15126__$1;
(statearr_15147_18130[(2)] = null);

(statearr_15147_18130[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15127 === (7))){
var inst_15121 = (state_15126[(2)]);
var state_15126__$1 = state_15126;
var statearr_15158_18131 = state_15126__$1;
(statearr_15158_18131[(2)] = inst_15121);

(statearr_15158_18131[(1)] = (3));


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
});})(__18095,c__14373__auto___18121,G__15011_18096,G__15011_18097__$1,n__5741__auto___18094,jobs,results,process__$1,async))
;
return ((function (__18095,switch__14013__auto__,c__14373__auto___18121,G__15011_18096,G__15011_18097__$1,n__5741__auto___18094,jobs,results,process__$1,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____0 = (function (){
var statearr_15169 = [null,null,null,null,null,null,null];
(statearr_15169[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__);

(statearr_15169[(1)] = (1));

return statearr_15169;
});
var cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____1 = (function (state_15126){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_15126);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e15177){var ex__14017__auto__ = e15177;
var statearr_15179_18132 = state_15126;
(statearr_15179_18132[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_15126[(4)]))){
var statearr_15186_18133 = state_15126;
(statearr_15186_18133[(1)] = cljs.core.first((state_15126[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18134 = state_15126;
state_15126 = G__18134;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__ = function(state_15126){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____1.call(this,state_15126);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__;
})()
;})(__18095,switch__14013__auto__,c__14373__auto___18121,G__15011_18096,G__15011_18097__$1,n__5741__auto___18094,jobs,results,process__$1,async))
})();
var state__14375__auto__ = (function (){var statearr_15193 = f__14374__auto__();
(statearr_15193[(6)] = c__14373__auto___18121);

return statearr_15193;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
});})(__18095,c__14373__auto___18121,G__15011_18096,G__15011_18097__$1,n__5741__auto___18094,jobs,results,process__$1,async))
);


break;
default:
throw (new Error((""+"No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__15011_18097__$1))));

}

var G__18135 = (__18095 + (1));
__18095 = G__18135;
continue;
} else {
}
break;
}

var c__14373__auto___18136 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = (function (state_15236){
var state_val_15237 = (state_15236[(1)]);
if((state_val_15237 === (7))){
var inst_15228 = (state_15236[(2)]);
var state_15236__$1 = state_15236;
var statearr_15246_18137 = state_15236__$1;
(statearr_15246_18137[(2)] = inst_15228);

(statearr_15246_18137[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15237 === (1))){
var state_15236__$1 = state_15236;
var statearr_15247_18138 = state_15236__$1;
(statearr_15247_18138[(2)] = null);

(statearr_15247_18138[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15237 === (4))){
var inst_15204 = (state_15236[(7)]);
var inst_15204__$1 = (state_15236[(2)]);
var inst_15208 = (inst_15204__$1 == null);
var state_15236__$1 = (function (){var statearr_15249 = state_15236;
(statearr_15249[(7)] = inst_15204__$1);

return statearr_15249;
})();
if(cljs.core.truth_(inst_15208)){
var statearr_15250_18150 = state_15236__$1;
(statearr_15250_18150[(1)] = (5));

} else {
var statearr_15251_18151 = state_15236__$1;
(statearr_15251_18151[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15237 === (6))){
var inst_15204 = (state_15236[(7)]);
var inst_15213 = (state_15236[(8)]);
var inst_15213__$1 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
var inst_15214 = cljs.core.PersistentVector.EMPTY_NODE;
var inst_15219 = [inst_15204,inst_15213__$1];
var inst_15220 = (new cljs.core.PersistentVector(null,2,(5),inst_15214,inst_15219,null));
var state_15236__$1 = (function (){var statearr_15253 = state_15236;
(statearr_15253[(8)] = inst_15213__$1);

return statearr_15253;
})();
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_15236__$1,(8),jobs,inst_15220);
} else {
if((state_val_15237 === (3))){
var inst_15230 = (state_15236[(2)]);
var state_15236__$1 = state_15236;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15236__$1,inst_15230);
} else {
if((state_val_15237 === (2))){
var state_15236__$1 = state_15236;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15236__$1,(4),from);
} else {
if((state_val_15237 === (9))){
var inst_15225 = (state_15236[(2)]);
var state_15236__$1 = (function (){var statearr_15256 = state_15236;
(statearr_15256[(9)] = inst_15225);

return statearr_15256;
})();
var statearr_15257_18159 = state_15236__$1;
(statearr_15257_18159[(2)] = null);

(statearr_15257_18159[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15237 === (5))){
var inst_15211 = cljs.core.async.close_BANG_(jobs);
var state_15236__$1 = state_15236;
var statearr_15258_18160 = state_15236__$1;
(statearr_15258_18160[(2)] = inst_15211);

(statearr_15258_18160[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15237 === (8))){
var inst_15213 = (state_15236[(8)]);
var inst_15222 = (state_15236[(2)]);
var state_15236__$1 = (function (){var statearr_15259 = state_15236;
(statearr_15259[(10)] = inst_15222);

return statearr_15259;
})();
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_15236__$1,(9),results,inst_15213);
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
var cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____0 = (function (){
var statearr_15261 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_15261[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__);

(statearr_15261[(1)] = (1));

return statearr_15261;
});
var cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____1 = (function (state_15236){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_15236);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e15262){var ex__14017__auto__ = e15262;
var statearr_15263_18163 = state_15236;
(statearr_15263_18163[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_15236[(4)]))){
var statearr_15268_18164 = state_15236;
(statearr_15268_18164[(1)] = cljs.core.first((state_15236[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18165 = state_15236;
state_15236 = G__18165;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__ = function(state_15236){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____1.call(this,state_15236);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__;
})()
})();
var state__14375__auto__ = (function (){var statearr_15270 = f__14374__auto__();
(statearr_15270[(6)] = c__14373__auto___18136);

return statearr_15270;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
}));


var c__14373__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = (function (state_15328){
var state_val_15329 = (state_15328[(1)]);
if((state_val_15329 === (7))){
var inst_15317 = (state_15328[(2)]);
var state_15328__$1 = state_15328;
var statearr_15334_18166 = state_15328__$1;
(statearr_15334_18166[(2)] = inst_15317);

(statearr_15334_18166[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15329 === (20))){
var state_15328__$1 = state_15328;
var statearr_15335_18167 = state_15328__$1;
(statearr_15335_18167[(2)] = null);

(statearr_15335_18167[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15329 === (1))){
var state_15328__$1 = state_15328;
var statearr_15336_18168 = state_15328__$1;
(statearr_15336_18168[(2)] = null);

(statearr_15336_18168[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15329 === (4))){
var inst_15279 = (state_15328[(7)]);
var inst_15279__$1 = (state_15328[(2)]);
var inst_15281 = (inst_15279__$1 == null);
var state_15328__$1 = (function (){var statearr_15337 = state_15328;
(statearr_15337[(7)] = inst_15279__$1);

return statearr_15337;
})();
if(cljs.core.truth_(inst_15281)){
var statearr_15338_18169 = state_15328__$1;
(statearr_15338_18169[(1)] = (5));

} else {
var statearr_15339_18170 = state_15328__$1;
(statearr_15339_18170[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15329 === (15))){
var inst_15298 = (state_15328[(8)]);
var state_15328__$1 = state_15328;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_15328__$1,(18),to,inst_15298);
} else {
if((state_val_15329 === (21))){
var inst_15312 = (state_15328[(2)]);
var state_15328__$1 = state_15328;
var statearr_15343_18172 = state_15328__$1;
(statearr_15343_18172[(2)] = inst_15312);

(statearr_15343_18172[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15329 === (13))){
var inst_15314 = (state_15328[(2)]);
var state_15328__$1 = (function (){var statearr_15344 = state_15328;
(statearr_15344[(9)] = inst_15314);

return statearr_15344;
})();
var statearr_15345_18173 = state_15328__$1;
(statearr_15345_18173[(2)] = null);

(statearr_15345_18173[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15329 === (6))){
var inst_15279 = (state_15328[(7)]);
var state_15328__$1 = state_15328;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15328__$1,(11),inst_15279);
} else {
if((state_val_15329 === (17))){
var inst_15306 = (state_15328[(2)]);
var state_15328__$1 = state_15328;
if(cljs.core.truth_(inst_15306)){
var statearr_15347_18174 = state_15328__$1;
(statearr_15347_18174[(1)] = (19));

} else {
var statearr_15348_18175 = state_15328__$1;
(statearr_15348_18175[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15329 === (3))){
var inst_15320 = (state_15328[(2)]);
var state_15328__$1 = state_15328;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15328__$1,inst_15320);
} else {
if((state_val_15329 === (12))){
var inst_15290 = (state_15328[(10)]);
var state_15328__$1 = state_15328;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15328__$1,(14),inst_15290);
} else {
if((state_val_15329 === (2))){
var state_15328__$1 = state_15328;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15328__$1,(4),results);
} else {
if((state_val_15329 === (19))){
var state_15328__$1 = state_15328;
var statearr_15349_18176 = state_15328__$1;
(statearr_15349_18176[(2)] = null);

(statearr_15349_18176[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15329 === (11))){
var inst_15290 = (state_15328[(2)]);
var state_15328__$1 = (function (){var statearr_15350 = state_15328;
(statearr_15350[(10)] = inst_15290);

return statearr_15350;
})();
var statearr_15351_18177 = state_15328__$1;
(statearr_15351_18177[(2)] = null);

(statearr_15351_18177[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15329 === (9))){
var state_15328__$1 = state_15328;
var statearr_15352_18178 = state_15328__$1;
(statearr_15352_18178[(2)] = null);

(statearr_15352_18178[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15329 === (5))){
var state_15328__$1 = state_15328;
if(cljs.core.truth_(close_QMARK_)){
var statearr_15354_18179 = state_15328__$1;
(statearr_15354_18179[(1)] = (8));

} else {
var statearr_15355_18180 = state_15328__$1;
(statearr_15355_18180[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15329 === (14))){
var inst_15298 = (state_15328[(8)]);
var inst_15300 = (state_15328[(11)]);
var inst_15298__$1 = (state_15328[(2)]);
var inst_15299 = (inst_15298__$1 == null);
var inst_15300__$1 = cljs.core.not(inst_15299);
var state_15328__$1 = (function (){var statearr_15360 = state_15328;
(statearr_15360[(8)] = inst_15298__$1);

(statearr_15360[(11)] = inst_15300__$1);

return statearr_15360;
})();
if(inst_15300__$1){
var statearr_15361_18183 = state_15328__$1;
(statearr_15361_18183[(1)] = (15));

} else {
var statearr_15362_18184 = state_15328__$1;
(statearr_15362_18184[(1)] = (16));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15329 === (16))){
var inst_15300 = (state_15328[(11)]);
var state_15328__$1 = state_15328;
var statearr_15364_18185 = state_15328__$1;
(statearr_15364_18185[(2)] = inst_15300);

(statearr_15364_18185[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15329 === (10))){
var inst_15287 = (state_15328[(2)]);
var state_15328__$1 = state_15328;
var statearr_15365_18188 = state_15328__$1;
(statearr_15365_18188[(2)] = inst_15287);

(statearr_15365_18188[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15329 === (18))){
var inst_15303 = (state_15328[(2)]);
var state_15328__$1 = state_15328;
var statearr_15366_18189 = state_15328__$1;
(statearr_15366_18189[(2)] = inst_15303);

(statearr_15366_18189[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15329 === (8))){
var inst_15284 = cljs.core.async.close_BANG_(to);
var state_15328__$1 = state_15328;
var statearr_15367_18190 = state_15328__$1;
(statearr_15367_18190[(2)] = inst_15284);

(statearr_15367_18190[(1)] = (10));


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
var cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____0 = (function (){
var statearr_15370 = [null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_15370[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__);

(statearr_15370[(1)] = (1));

return statearr_15370;
});
var cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____1 = (function (state_15328){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_15328);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e15371){var ex__14017__auto__ = e15371;
var statearr_15372_18195 = state_15328;
(statearr_15372_18195[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_15328[(4)]))){
var statearr_15373_18196 = state_15328;
(statearr_15373_18196[(1)] = cljs.core.first((state_15328[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18197 = state_15328;
state_15328 = G__18197;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__ = function(state_15328){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____1.call(this,state_15328);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__14014__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__14014__auto__;
})()
})();
var state__14375__auto__ = (function (){var statearr_15375 = f__14374__auto__();
(statearr_15375[(6)] = c__14373__auto__);

return statearr_15375;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
}));

return c__14373__auto__;
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
var G__15380 = arguments.length;
switch (G__15380) {
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
var G__15384 = arguments.length;
switch (G__15384) {
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
var G__15388 = arguments.length;
switch (G__15388) {
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
var c__14373__auto___18213 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = (function (state_15415){
var state_val_15416 = (state_15415[(1)]);
if((state_val_15416 === (7))){
var inst_15411 = (state_15415[(2)]);
var state_15415__$1 = state_15415;
var statearr_15419_18214 = state_15415__$1;
(statearr_15419_18214[(2)] = inst_15411);

(statearr_15419_18214[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15416 === (1))){
var state_15415__$1 = state_15415;
var statearr_15420_18217 = state_15415__$1;
(statearr_15420_18217[(2)] = null);

(statearr_15420_18217[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15416 === (4))){
var inst_15391 = (state_15415[(7)]);
var inst_15391__$1 = (state_15415[(2)]);
var inst_15392 = (inst_15391__$1 == null);
var state_15415__$1 = (function (){var statearr_15427 = state_15415;
(statearr_15427[(7)] = inst_15391__$1);

return statearr_15427;
})();
if(cljs.core.truth_(inst_15392)){
var statearr_15430_18219 = state_15415__$1;
(statearr_15430_18219[(1)] = (5));

} else {
var statearr_15432_18220 = state_15415__$1;
(statearr_15432_18220[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15416 === (13))){
var state_15415__$1 = state_15415;
var statearr_15434_18221 = state_15415__$1;
(statearr_15434_18221[(2)] = null);

(statearr_15434_18221[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15416 === (6))){
var inst_15391 = (state_15415[(7)]);
var inst_15397 = (p.cljs$core$IFn$_invoke$arity$1 ? p.cljs$core$IFn$_invoke$arity$1(inst_15391) : p.call(null,inst_15391));
var state_15415__$1 = state_15415;
if(cljs.core.truth_(inst_15397)){
var statearr_15435_18222 = state_15415__$1;
(statearr_15435_18222[(1)] = (9));

} else {
var statearr_15436_18223 = state_15415__$1;
(statearr_15436_18223[(1)] = (10));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15416 === (3))){
var inst_15413 = (state_15415[(2)]);
var state_15415__$1 = state_15415;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15415__$1,inst_15413);
} else {
if((state_val_15416 === (12))){
var state_15415__$1 = state_15415;
var statearr_15438_18224 = state_15415__$1;
(statearr_15438_18224[(2)] = null);

(statearr_15438_18224[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15416 === (2))){
var state_15415__$1 = state_15415;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15415__$1,(4),ch);
} else {
if((state_val_15416 === (11))){
var inst_15391 = (state_15415[(7)]);
var inst_15402 = (state_15415[(2)]);
var state_15415__$1 = state_15415;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_15415__$1,(8),inst_15402,inst_15391);
} else {
if((state_val_15416 === (9))){
var state_15415__$1 = state_15415;
var statearr_15440_18229 = state_15415__$1;
(statearr_15440_18229[(2)] = tc);

(statearr_15440_18229[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15416 === (5))){
var inst_15394 = cljs.core.async.close_BANG_(tc);
var inst_15395 = cljs.core.async.close_BANG_(fc);
var state_15415__$1 = (function (){var statearr_15448 = state_15415;
(statearr_15448[(8)] = inst_15394);

return statearr_15448;
})();
var statearr_15450_18239 = state_15415__$1;
(statearr_15450_18239[(2)] = inst_15395);

(statearr_15450_18239[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15416 === (14))){
var inst_15409 = (state_15415[(2)]);
var state_15415__$1 = state_15415;
var statearr_15452_18243 = state_15415__$1;
(statearr_15452_18243[(2)] = inst_15409);

(statearr_15452_18243[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15416 === (10))){
var state_15415__$1 = state_15415;
var statearr_15453_18247 = state_15415__$1;
(statearr_15453_18247[(2)] = fc);

(statearr_15453_18247[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15416 === (8))){
var inst_15404 = (state_15415[(2)]);
var state_15415__$1 = state_15415;
if(cljs.core.truth_(inst_15404)){
var statearr_15456_18250 = state_15415__$1;
(statearr_15456_18250[(1)] = (12));

} else {
var statearr_15459_18252 = state_15415__$1;
(statearr_15459_18252[(1)] = (13));

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
var cljs$core$async$state_machine__14014__auto__ = null;
var cljs$core$async$state_machine__14014__auto____0 = (function (){
var statearr_15465 = [null,null,null,null,null,null,null,null,null];
(statearr_15465[(0)] = cljs$core$async$state_machine__14014__auto__);

(statearr_15465[(1)] = (1));

return statearr_15465;
});
var cljs$core$async$state_machine__14014__auto____1 = (function (state_15415){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_15415);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e15467){var ex__14017__auto__ = e15467;
var statearr_15468_18258 = state_15415;
(statearr_15468_18258[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_15415[(4)]))){
var statearr_15469_18259 = state_15415;
(statearr_15469_18259[(1)] = cljs.core.first((state_15415[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18263 = state_15415;
state_15415 = G__18263;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$state_machine__14014__auto__ = function(state_15415){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14014__auto____1.call(this,state_15415);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14014__auto____0;
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14014__auto____1;
return cljs$core$async$state_machine__14014__auto__;
})()
})();
var state__14375__auto__ = (function (){var statearr_15471 = f__14374__auto__();
(statearr_15471[(6)] = c__14373__auto___18213);

return statearr_15471;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
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
var c__14373__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = (function (state_15501){
var state_val_15502 = (state_15501[(1)]);
if((state_val_15502 === (7))){
var inst_15496 = (state_15501[(2)]);
var state_15501__$1 = state_15501;
var statearr_15507_18272 = state_15501__$1;
(statearr_15507_18272[(2)] = inst_15496);

(statearr_15507_18272[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15502 === (1))){
var inst_15478 = init;
var inst_15479 = inst_15478;
var state_15501__$1 = (function (){var statearr_15508 = state_15501;
(statearr_15508[(7)] = inst_15479);

return statearr_15508;
})();
var statearr_15510_18273 = state_15501__$1;
(statearr_15510_18273[(2)] = null);

(statearr_15510_18273[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15502 === (4))){
var inst_15482 = (state_15501[(8)]);
var inst_15482__$1 = (state_15501[(2)]);
var inst_15483 = (inst_15482__$1 == null);
var state_15501__$1 = (function (){var statearr_15511 = state_15501;
(statearr_15511[(8)] = inst_15482__$1);

return statearr_15511;
})();
if(cljs.core.truth_(inst_15483)){
var statearr_15512_18274 = state_15501__$1;
(statearr_15512_18274[(1)] = (5));

} else {
var statearr_15513_18275 = state_15501__$1;
(statearr_15513_18275[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15502 === (6))){
var inst_15479 = (state_15501[(7)]);
var inst_15482 = (state_15501[(8)]);
var inst_15486 = (state_15501[(9)]);
var inst_15486__$1 = (f.cljs$core$IFn$_invoke$arity$2 ? f.cljs$core$IFn$_invoke$arity$2(inst_15479,inst_15482) : f.call(null,inst_15479,inst_15482));
var inst_15488 = cljs.core.reduced_QMARK_(inst_15486__$1);
var state_15501__$1 = (function (){var statearr_15515 = state_15501;
(statearr_15515[(9)] = inst_15486__$1);

return statearr_15515;
})();
if(inst_15488){
var statearr_15516_18277 = state_15501__$1;
(statearr_15516_18277[(1)] = (8));

} else {
var statearr_15517_18278 = state_15501__$1;
(statearr_15517_18278[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15502 === (3))){
var inst_15498 = (state_15501[(2)]);
var state_15501__$1 = state_15501;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15501__$1,inst_15498);
} else {
if((state_val_15502 === (2))){
var state_15501__$1 = state_15501;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15501__$1,(4),ch);
} else {
if((state_val_15502 === (9))){
var inst_15486 = (state_15501[(9)]);
var inst_15479 = inst_15486;
var state_15501__$1 = (function (){var statearr_15523 = state_15501;
(statearr_15523[(7)] = inst_15479);

return statearr_15523;
})();
var statearr_15524_18280 = state_15501__$1;
(statearr_15524_18280[(2)] = null);

(statearr_15524_18280[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15502 === (5))){
var inst_15479 = (state_15501[(7)]);
var state_15501__$1 = state_15501;
var statearr_15525_18285 = state_15501__$1;
(statearr_15525_18285[(2)] = inst_15479);

(statearr_15525_18285[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15502 === (10))){
var inst_15494 = (state_15501[(2)]);
var state_15501__$1 = state_15501;
var statearr_15526_18286 = state_15501__$1;
(statearr_15526_18286[(2)] = inst_15494);

(statearr_15526_18286[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15502 === (8))){
var inst_15486 = (state_15501[(9)]);
var inst_15490 = cljs.core.deref(inst_15486);
var state_15501__$1 = state_15501;
var statearr_15528_18287 = state_15501__$1;
(statearr_15528_18287[(2)] = inst_15490);

(statearr_15528_18287[(1)] = (10));


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
var cljs$core$async$reduce_$_state_machine__14014__auto__ = null;
var cljs$core$async$reduce_$_state_machine__14014__auto____0 = (function (){
var statearr_15529 = [null,null,null,null,null,null,null,null,null,null];
(statearr_15529[(0)] = cljs$core$async$reduce_$_state_machine__14014__auto__);

(statearr_15529[(1)] = (1));

return statearr_15529;
});
var cljs$core$async$reduce_$_state_machine__14014__auto____1 = (function (state_15501){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_15501);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e15531){var ex__14017__auto__ = e15531;
var statearr_15532_18288 = state_15501;
(statearr_15532_18288[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_15501[(4)]))){
var statearr_15533_18289 = state_15501;
(statearr_15533_18289[(1)] = cljs.core.first((state_15501[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18290 = state_15501;
state_15501 = G__18290;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$reduce_$_state_machine__14014__auto__ = function(state_15501){
switch(arguments.length){
case 0:
return cljs$core$async$reduce_$_state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$reduce_$_state_machine__14014__auto____1.call(this,state_15501);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$reduce_$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$reduce_$_state_machine__14014__auto____0;
cljs$core$async$reduce_$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$reduce_$_state_machine__14014__auto____1;
return cljs$core$async$reduce_$_state_machine__14014__auto__;
})()
})();
var state__14375__auto__ = (function (){var statearr_15536 = f__14374__auto__();
(statearr_15536[(6)] = c__14373__auto__);

return statearr_15536;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
}));

return c__14373__auto__;
});
/**
 * async/reduces a channel with a transformation (xform f).
 *   Returns a channel containing the result.  ch must close before
 *   transduce produces a result.
 */
cljs.core.async.transduce = (function cljs$core$async$transduce(xform,f,init,ch){
var f__$1 = (xform.cljs$core$IFn$_invoke$arity$1 ? xform.cljs$core$IFn$_invoke$arity$1(f) : xform.call(null,f));
var c__14373__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = (function (state_15546){
var state_val_15547 = (state_15546[(1)]);
if((state_val_15547 === (1))){
var inst_15541 = cljs.core.async.reduce(f__$1,init,ch);
var state_15546__$1 = state_15546;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15546__$1,(2),inst_15541);
} else {
if((state_val_15547 === (2))){
var inst_15543 = (state_15546[(2)]);
var inst_15544 = (f__$1.cljs$core$IFn$_invoke$arity$1 ? f__$1.cljs$core$IFn$_invoke$arity$1(inst_15543) : f__$1.call(null,inst_15543));
var state_15546__$1 = state_15546;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15546__$1,inst_15544);
} else {
return null;
}
}
});
return (function() {
var cljs$core$async$transduce_$_state_machine__14014__auto__ = null;
var cljs$core$async$transduce_$_state_machine__14014__auto____0 = (function (){
var statearr_15555 = [null,null,null,null,null,null,null];
(statearr_15555[(0)] = cljs$core$async$transduce_$_state_machine__14014__auto__);

(statearr_15555[(1)] = (1));

return statearr_15555;
});
var cljs$core$async$transduce_$_state_machine__14014__auto____1 = (function (state_15546){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_15546);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e15556){var ex__14017__auto__ = e15556;
var statearr_15557_18305 = state_15546;
(statearr_15557_18305[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_15546[(4)]))){
var statearr_15562_18306 = state_15546;
(statearr_15562_18306[(1)] = cljs.core.first((state_15546[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18311 = state_15546;
state_15546 = G__18311;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$transduce_$_state_machine__14014__auto__ = function(state_15546){
switch(arguments.length){
case 0:
return cljs$core$async$transduce_$_state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$transduce_$_state_machine__14014__auto____1.call(this,state_15546);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$transduce_$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$transduce_$_state_machine__14014__auto____0;
cljs$core$async$transduce_$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$transduce_$_state_machine__14014__auto____1;
return cljs$core$async$transduce_$_state_machine__14014__auto__;
})()
})();
var state__14375__auto__ = (function (){var statearr_15567 = f__14374__auto__();
(statearr_15567[(6)] = c__14373__auto__);

return statearr_15567;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
}));

return c__14373__auto__;
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
var G__15576 = arguments.length;
switch (G__15576) {
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
var c__14373__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = (function (state_15620){
var state_val_15621 = (state_15620[(1)]);
if((state_val_15621 === (7))){
var inst_15596 = (state_15620[(2)]);
var state_15620__$1 = state_15620;
var statearr_15632_18318 = state_15620__$1;
(statearr_15632_18318[(2)] = inst_15596);

(statearr_15632_18318[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15621 === (1))){
var inst_15586 = cljs.core.seq(coll);
var inst_15587 = inst_15586;
var state_15620__$1 = (function (){var statearr_15636 = state_15620;
(statearr_15636[(7)] = inst_15587);

return statearr_15636;
})();
var statearr_15639_18319 = state_15620__$1;
(statearr_15639_18319[(2)] = null);

(statearr_15639_18319[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15621 === (4))){
var inst_15587 = (state_15620[(7)]);
var inst_15594 = cljs.core.first(inst_15587);
var state_15620__$1 = state_15620;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_15620__$1,(7),ch,inst_15594);
} else {
if((state_val_15621 === (13))){
var inst_15611 = (state_15620[(2)]);
var state_15620__$1 = state_15620;
var statearr_15651_18320 = state_15620__$1;
(statearr_15651_18320[(2)] = inst_15611);

(statearr_15651_18320[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15621 === (6))){
var inst_15599 = (state_15620[(2)]);
var state_15620__$1 = state_15620;
if(cljs.core.truth_(inst_15599)){
var statearr_15653_18321 = state_15620__$1;
(statearr_15653_18321[(1)] = (8));

} else {
var statearr_15655_18322 = state_15620__$1;
(statearr_15655_18322[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15621 === (3))){
var inst_15616 = (state_15620[(2)]);
var state_15620__$1 = state_15620;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15620__$1,inst_15616);
} else {
if((state_val_15621 === (12))){
var state_15620__$1 = state_15620;
var statearr_15662_18323 = state_15620__$1;
(statearr_15662_18323[(2)] = null);

(statearr_15662_18323[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15621 === (2))){
var inst_15587 = (state_15620[(7)]);
var state_15620__$1 = state_15620;
if(cljs.core.truth_(inst_15587)){
var statearr_15670_18324 = state_15620__$1;
(statearr_15670_18324[(1)] = (4));

} else {
var statearr_15674_18326 = state_15620__$1;
(statearr_15674_18326[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15621 === (11))){
var inst_15608 = cljs.core.async.close_BANG_(ch);
var state_15620__$1 = state_15620;
var statearr_15675_18328 = state_15620__$1;
(statearr_15675_18328[(2)] = inst_15608);

(statearr_15675_18328[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15621 === (9))){
var state_15620__$1 = state_15620;
if(cljs.core.truth_(close_QMARK_)){
var statearr_15679_18329 = state_15620__$1;
(statearr_15679_18329[(1)] = (11));

} else {
var statearr_15680_18330 = state_15620__$1;
(statearr_15680_18330[(1)] = (12));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15621 === (5))){
var inst_15587 = (state_15620[(7)]);
var state_15620__$1 = state_15620;
var statearr_15684_18334 = state_15620__$1;
(statearr_15684_18334[(2)] = inst_15587);

(statearr_15684_18334[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15621 === (10))){
var inst_15613 = (state_15620[(2)]);
var state_15620__$1 = state_15620;
var statearr_15687_18335 = state_15620__$1;
(statearr_15687_18335[(2)] = inst_15613);

(statearr_15687_18335[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15621 === (8))){
var inst_15587 = (state_15620[(7)]);
var inst_15604 = cljs.core.next(inst_15587);
var inst_15587__$1 = inst_15604;
var state_15620__$1 = (function (){var statearr_15690 = state_15620;
(statearr_15690[(7)] = inst_15587__$1);

return statearr_15690;
})();
var statearr_15692_18336 = state_15620__$1;
(statearr_15692_18336[(2)] = null);

(statearr_15692_18336[(1)] = (2));


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
var cljs$core$async$state_machine__14014__auto__ = null;
var cljs$core$async$state_machine__14014__auto____0 = (function (){
var statearr_15696 = [null,null,null,null,null,null,null,null];
(statearr_15696[(0)] = cljs$core$async$state_machine__14014__auto__);

(statearr_15696[(1)] = (1));

return statearr_15696;
});
var cljs$core$async$state_machine__14014__auto____1 = (function (state_15620){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_15620);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e15699){var ex__14017__auto__ = e15699;
var statearr_15702_18343 = state_15620;
(statearr_15702_18343[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_15620[(4)]))){
var statearr_15703_18344 = state_15620;
(statearr_15703_18344[(1)] = cljs.core.first((state_15620[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18345 = state_15620;
state_15620 = G__18345;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$state_machine__14014__auto__ = function(state_15620){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14014__auto____1.call(this,state_15620);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14014__auto____0;
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14014__auto____1;
return cljs$core$async$state_machine__14014__auto__;
})()
})();
var state__14375__auto__ = (function (){var statearr_15705 = f__14374__auto__();
(statearr_15705[(6)] = c__14373__auto__);

return statearr_15705;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
}));

return c__14373__auto__;
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
var G__15716 = arguments.length;
switch (G__15716) {
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

var cljs$core$async$Mux$muxch_STAR_$dyn_18364 = (function (_){
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
return cljs$core$async$Mux$muxch_STAR_$dyn_18364(_);
}
});


/**
 * @interface
 */
cljs.core.async.Mult = function(){};

var cljs$core$async$Mult$tap_STAR_$dyn_18374 = (function (m,ch,close_QMARK_){
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
return cljs$core$async$Mult$tap_STAR_$dyn_18374(m,ch,close_QMARK_);
}
});

var cljs$core$async$Mult$untap_STAR_$dyn_18378 = (function (m,ch){
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
return cljs$core$async$Mult$untap_STAR_$dyn_18378(m,ch);
}
});

var cljs$core$async$Mult$untap_all_STAR_$dyn_18390 = (function (m){
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
return cljs$core$async$Mult$untap_all_STAR_$dyn_18390(m);
}
});


/**
* @constructor
 * @implements {cljs.core.async.Mult}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.async.Mux}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async15740 = (function (ch,cs,meta15741){
this.ch = ch;
this.cs = cs;
this.meta15741 = meta15741;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async15740.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_15742,meta15741__$1){
var self__ = this;
var _15742__$1 = this;
return (new cljs.core.async.t_cljs$core$async15740(self__.ch,self__.cs,meta15741__$1));
}));

(cljs.core.async.t_cljs$core$async15740.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_15742){
var self__ = this;
var _15742__$1 = this;
return self__.meta15741;
}));

(cljs.core.async.t_cljs$core$async15740.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async15740.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.ch;
}));

(cljs.core.async.t_cljs$core$async15740.prototype.cljs$core$async$Mult$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async15740.prototype.cljs$core$async$Mult$tap_STAR_$arity$3 = (function (_,ch__$1,close_QMARK_){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(self__.cs,cljs.core.assoc,ch__$1,close_QMARK_);

return null;
}));

(cljs.core.async.t_cljs$core$async15740.prototype.cljs$core$async$Mult$untap_STAR_$arity$2 = (function (_,ch__$1){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(self__.cs,cljs.core.dissoc,ch__$1);

return null;
}));

(cljs.core.async.t_cljs$core$async15740.prototype.cljs$core$async$Mult$untap_all_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_(self__.cs,cljs.core.PersistentArrayMap.EMPTY);

return null;
}));

(cljs.core.async.t_cljs$core$async15740.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"cs","cs",-117024463,null),new cljs.core.Symbol(null,"meta15741","meta15741",-2083103209,null)], null);
}));

(cljs.core.async.t_cljs$core$async15740.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async15740.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async15740");

(cljs.core.async.t_cljs$core$async15740.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async15740");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async15740.
 */
cljs.core.async.__GT_t_cljs$core$async15740 = (function cljs$core$async$__GT_t_cljs$core$async15740(ch,cs,meta15741){
return (new cljs.core.async.t_cljs$core$async15740(ch,cs,meta15741));
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
var m = (new cljs.core.async.t_cljs$core$async15740(ch,cs,cljs.core.PersistentArrayMap.EMPTY));
var dchan = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
var dctr = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(null);
var done = (function (_){
if((cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$2(dctr,cljs.core.dec) === (0))){
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2(dchan,true);
} else {
return null;
}
});
var c__14373__auto___18407 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = (function (state_15928){
var state_val_15930 = (state_15928[(1)]);
if((state_val_15930 === (7))){
var inst_15922 = (state_15928[(2)]);
var state_15928__$1 = state_15928;
var statearr_15943_18408 = state_15928__$1;
(statearr_15943_18408[(2)] = inst_15922);

(statearr_15943_18408[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (20))){
var inst_15798 = (state_15928[(7)]);
var inst_15813 = cljs.core.first(inst_15798);
var inst_15814 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_15813,(0),null);
var inst_15815 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_15813,(1),null);
var state_15928__$1 = (function (){var statearr_15949 = state_15928;
(statearr_15949[(8)] = inst_15814);

return statearr_15949;
})();
if(cljs.core.truth_(inst_15815)){
var statearr_15952_18409 = state_15928__$1;
(statearr_15952_18409[(1)] = (22));

} else {
var statearr_15954_18410 = state_15928__$1;
(statearr_15954_18410[(1)] = (23));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (27))){
var inst_15854 = (state_15928[(9)]);
var inst_15856 = (state_15928[(10)]);
var inst_15866 = (state_15928[(11)]);
var inst_15761 = (state_15928[(12)]);
var inst_15866__$1 = cljs.core._nth(inst_15854,inst_15856);
var inst_15867 = cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$3(inst_15866__$1,inst_15761,done);
var state_15928__$1 = (function (){var statearr_15969 = state_15928;
(statearr_15969[(11)] = inst_15866__$1);

return statearr_15969;
})();
if(cljs.core.truth_(inst_15867)){
var statearr_15970_18412 = state_15928__$1;
(statearr_15970_18412[(1)] = (30));

} else {
var statearr_15974_18413 = state_15928__$1;
(statearr_15974_18413[(1)] = (31));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (1))){
var state_15928__$1 = state_15928;
var statearr_15976_18414 = state_15928__$1;
(statearr_15976_18414[(2)] = null);

(statearr_15976_18414[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (24))){
var inst_15798 = (state_15928[(7)]);
var inst_15820 = (state_15928[(2)]);
var inst_15821 = cljs.core.next(inst_15798);
var inst_15771 = inst_15821;
var inst_15772 = null;
var inst_15773 = (0);
var inst_15774 = (0);
var state_15928__$1 = (function (){var statearr_15982 = state_15928;
(statearr_15982[(13)] = inst_15820);

(statearr_15982[(14)] = inst_15771);

(statearr_15982[(15)] = inst_15772);

(statearr_15982[(16)] = inst_15773);

(statearr_15982[(17)] = inst_15774);

return statearr_15982;
})();
var statearr_15989_18415 = state_15928__$1;
(statearr_15989_18415[(2)] = null);

(statearr_15989_18415[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (39))){
var state_15928__$1 = state_15928;
var statearr_16005_18416 = state_15928__$1;
(statearr_16005_18416[(2)] = null);

(statearr_16005_18416[(1)] = (41));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (4))){
var inst_15761 = (state_15928[(12)]);
var inst_15761__$1 = (state_15928[(2)]);
var inst_15762 = (inst_15761__$1 == null);
var state_15928__$1 = (function (){var statearr_16014 = state_15928;
(statearr_16014[(12)] = inst_15761__$1);

return statearr_16014;
})();
if(cljs.core.truth_(inst_15762)){
var statearr_16015_18421 = state_15928__$1;
(statearr_16015_18421[(1)] = (5));

} else {
var statearr_16017_18422 = state_15928__$1;
(statearr_16017_18422[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (15))){
var inst_15774 = (state_15928[(17)]);
var inst_15771 = (state_15928[(14)]);
var inst_15772 = (state_15928[(15)]);
var inst_15773 = (state_15928[(16)]);
var inst_15794 = (state_15928[(2)]);
var inst_15795 = (inst_15774 + (1));
var tmp15993 = inst_15771;
var tmp15994 = inst_15772;
var tmp15995 = inst_15773;
var inst_15771__$1 = tmp15993;
var inst_15772__$1 = tmp15994;
var inst_15773__$1 = tmp15995;
var inst_15774__$1 = inst_15795;
var state_15928__$1 = (function (){var statearr_16018 = state_15928;
(statearr_16018[(18)] = inst_15794);

(statearr_16018[(14)] = inst_15771__$1);

(statearr_16018[(15)] = inst_15772__$1);

(statearr_16018[(16)] = inst_15773__$1);

(statearr_16018[(17)] = inst_15774__$1);

return statearr_16018;
})();
var statearr_16019_18426 = state_15928__$1;
(statearr_16019_18426[(2)] = null);

(statearr_16019_18426[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (21))){
var inst_15825 = (state_15928[(2)]);
var state_15928__$1 = state_15928;
var statearr_16025_18430 = state_15928__$1;
(statearr_16025_18430[(2)] = inst_15825);

(statearr_16025_18430[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (31))){
var inst_15866 = (state_15928[(11)]);
var inst_15870 = m.cljs$core$async$Mult$untap_STAR_$arity$2(null,inst_15866);
var state_15928__$1 = state_15928;
var statearr_16028_18431 = state_15928__$1;
(statearr_16028_18431[(2)] = inst_15870);

(statearr_16028_18431[(1)] = (32));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (32))){
var inst_15856 = (state_15928[(10)]);
var inst_15853 = (state_15928[(19)]);
var inst_15854 = (state_15928[(9)]);
var inst_15855 = (state_15928[(20)]);
var inst_15872 = (state_15928[(2)]);
var inst_15873 = (inst_15856 + (1));
var tmp16020 = inst_15853;
var tmp16021 = inst_15855;
var tmp16022 = inst_15854;
var inst_15853__$1 = tmp16020;
var inst_15854__$1 = tmp16022;
var inst_15855__$1 = tmp16021;
var inst_15856__$1 = inst_15873;
var state_15928__$1 = (function (){var statearr_16029 = state_15928;
(statearr_16029[(21)] = inst_15872);

(statearr_16029[(19)] = inst_15853__$1);

(statearr_16029[(9)] = inst_15854__$1);

(statearr_16029[(20)] = inst_15855__$1);

(statearr_16029[(10)] = inst_15856__$1);

return statearr_16029;
})();
var statearr_16030_18434 = state_15928__$1;
(statearr_16030_18434[(2)] = null);

(statearr_16030_18434[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (40))){
var inst_15890 = (state_15928[(22)]);
var inst_15895 = m.cljs$core$async$Mult$untap_STAR_$arity$2(null,inst_15890);
var state_15928__$1 = state_15928;
var statearr_16033_18435 = state_15928__$1;
(statearr_16033_18435[(2)] = inst_15895);

(statearr_16033_18435[(1)] = (41));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (33))){
var inst_15877 = (state_15928[(23)]);
var inst_15879 = cljs.core.chunked_seq_QMARK_(inst_15877);
var state_15928__$1 = state_15928;
if(inst_15879){
var statearr_16036_18438 = state_15928__$1;
(statearr_16036_18438[(1)] = (36));

} else {
var statearr_16037_18439 = state_15928__$1;
(statearr_16037_18439[(1)] = (37));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (13))){
var inst_15787 = (state_15928[(24)]);
var inst_15791 = cljs.core.async.close_BANG_(inst_15787);
var state_15928__$1 = state_15928;
var statearr_16038_18441 = state_15928__$1;
(statearr_16038_18441[(2)] = inst_15791);

(statearr_16038_18441[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (22))){
var inst_15814 = (state_15928[(8)]);
var inst_15817 = cljs.core.async.close_BANG_(inst_15814);
var state_15928__$1 = state_15928;
var statearr_16039_18442 = state_15928__$1;
(statearr_16039_18442[(2)] = inst_15817);

(statearr_16039_18442[(1)] = (24));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (36))){
var inst_15877 = (state_15928[(23)]);
var inst_15882 = cljs.core.chunk_first(inst_15877);
var inst_15883 = cljs.core.chunk_rest(inst_15877);
var inst_15885 = cljs.core.count(inst_15882);
var inst_15853 = inst_15883;
var inst_15854 = inst_15882;
var inst_15855 = inst_15885;
var inst_15856 = (0);
var state_15928__$1 = (function (){var statearr_16042 = state_15928;
(statearr_16042[(19)] = inst_15853);

(statearr_16042[(9)] = inst_15854);

(statearr_16042[(20)] = inst_15855);

(statearr_16042[(10)] = inst_15856);

return statearr_16042;
})();
var statearr_16044_18443 = state_15928__$1;
(statearr_16044_18443[(2)] = null);

(statearr_16044_18443[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (41))){
var inst_15877 = (state_15928[(23)]);
var inst_15897 = (state_15928[(2)]);
var inst_15899 = cljs.core.next(inst_15877);
var inst_15853 = inst_15899;
var inst_15854 = null;
var inst_15855 = (0);
var inst_15856 = (0);
var state_15928__$1 = (function (){var statearr_16045 = state_15928;
(statearr_16045[(25)] = inst_15897);

(statearr_16045[(19)] = inst_15853);

(statearr_16045[(9)] = inst_15854);

(statearr_16045[(20)] = inst_15855);

(statearr_16045[(10)] = inst_15856);

return statearr_16045;
})();
var statearr_16046_18444 = state_15928__$1;
(statearr_16046_18444[(2)] = null);

(statearr_16046_18444[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (43))){
var state_15928__$1 = state_15928;
var statearr_16047_18445 = state_15928__$1;
(statearr_16047_18445[(2)] = null);

(statearr_16047_18445[(1)] = (44));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (29))){
var inst_15907 = (state_15928[(2)]);
var state_15928__$1 = state_15928;
var statearr_16049_18446 = state_15928__$1;
(statearr_16049_18446[(2)] = inst_15907);

(statearr_16049_18446[(1)] = (26));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (44))){
var inst_15919 = (state_15928[(2)]);
var state_15928__$1 = (function (){var statearr_16053 = state_15928;
(statearr_16053[(26)] = inst_15919);

return statearr_16053;
})();
var statearr_16054_18451 = state_15928__$1;
(statearr_16054_18451[(2)] = null);

(statearr_16054_18451[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (6))){
var inst_15839 = (state_15928[(27)]);
var inst_15838 = cljs.core.deref(cs);
var inst_15839__$1 = cljs.core.keys(inst_15838);
var inst_15840 = cljs.core.count(inst_15839__$1);
var inst_15841 = cljs.core.reset_BANG_(dctr,inst_15840);
var inst_15851 = cljs.core.seq(inst_15839__$1);
var inst_15853 = inst_15851;
var inst_15854 = null;
var inst_15855 = (0);
var inst_15856 = (0);
var state_15928__$1 = (function (){var statearr_16056 = state_15928;
(statearr_16056[(27)] = inst_15839__$1);

(statearr_16056[(28)] = inst_15841);

(statearr_16056[(19)] = inst_15853);

(statearr_16056[(9)] = inst_15854);

(statearr_16056[(20)] = inst_15855);

(statearr_16056[(10)] = inst_15856);

return statearr_16056;
})();
var statearr_16058_18467 = state_15928__$1;
(statearr_16058_18467[(2)] = null);

(statearr_16058_18467[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (28))){
var inst_15853 = (state_15928[(19)]);
var inst_15877 = (state_15928[(23)]);
var inst_15877__$1 = cljs.core.seq(inst_15853);
var state_15928__$1 = (function (){var statearr_16062 = state_15928;
(statearr_16062[(23)] = inst_15877__$1);

return statearr_16062;
})();
if(inst_15877__$1){
var statearr_16063_18468 = state_15928__$1;
(statearr_16063_18468[(1)] = (33));

} else {
var statearr_16065_18469 = state_15928__$1;
(statearr_16065_18469[(1)] = (34));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (25))){
var inst_15856 = (state_15928[(10)]);
var inst_15855 = (state_15928[(20)]);
var inst_15858 = (inst_15856 < inst_15855);
var inst_15860 = inst_15858;
var state_15928__$1 = state_15928;
if(cljs.core.truth_(inst_15860)){
var statearr_16068_18470 = state_15928__$1;
(statearr_16068_18470[(1)] = (27));

} else {
var statearr_16069_18471 = state_15928__$1;
(statearr_16069_18471[(1)] = (28));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (34))){
var state_15928__$1 = state_15928;
var statearr_16070_18472 = state_15928__$1;
(statearr_16070_18472[(2)] = null);

(statearr_16070_18472[(1)] = (35));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (17))){
var state_15928__$1 = state_15928;
var statearr_16071_18473 = state_15928__$1;
(statearr_16071_18473[(2)] = null);

(statearr_16071_18473[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (3))){
var inst_15924 = (state_15928[(2)]);
var state_15928__$1 = state_15928;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15928__$1,inst_15924);
} else {
if((state_val_15930 === (12))){
var inst_15830 = (state_15928[(2)]);
var state_15928__$1 = state_15928;
var statearr_16072_18474 = state_15928__$1;
(statearr_16072_18474[(2)] = inst_15830);

(statearr_16072_18474[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (2))){
var state_15928__$1 = state_15928;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15928__$1,(4),ch);
} else {
if((state_val_15930 === (23))){
var state_15928__$1 = state_15928;
var statearr_16074_18475 = state_15928__$1;
(statearr_16074_18475[(2)] = null);

(statearr_16074_18475[(1)] = (24));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (35))){
var inst_15905 = (state_15928[(2)]);
var state_15928__$1 = state_15928;
var statearr_16075_18479 = state_15928__$1;
(statearr_16075_18479[(2)] = inst_15905);

(statearr_16075_18479[(1)] = (29));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (19))){
var inst_15798 = (state_15928[(7)]);
var inst_15805 = cljs.core.chunk_first(inst_15798);
var inst_15806 = cljs.core.chunk_rest(inst_15798);
var inst_15807 = cljs.core.count(inst_15805);
var inst_15771 = inst_15806;
var inst_15772 = inst_15805;
var inst_15773 = inst_15807;
var inst_15774 = (0);
var state_15928__$1 = (function (){var statearr_16076 = state_15928;
(statearr_16076[(14)] = inst_15771);

(statearr_16076[(15)] = inst_15772);

(statearr_16076[(16)] = inst_15773);

(statearr_16076[(17)] = inst_15774);

return statearr_16076;
})();
var statearr_16077_18484 = state_15928__$1;
(statearr_16077_18484[(2)] = null);

(statearr_16077_18484[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (11))){
var inst_15771 = (state_15928[(14)]);
var inst_15798 = (state_15928[(7)]);
var inst_15798__$1 = cljs.core.seq(inst_15771);
var state_15928__$1 = (function (){var statearr_16080 = state_15928;
(statearr_16080[(7)] = inst_15798__$1);

return statearr_16080;
})();
if(inst_15798__$1){
var statearr_16081_18485 = state_15928__$1;
(statearr_16081_18485[(1)] = (16));

} else {
var statearr_16082_18486 = state_15928__$1;
(statearr_16082_18486[(1)] = (17));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (9))){
var inst_15832 = (state_15928[(2)]);
var state_15928__$1 = state_15928;
var statearr_16084_18487 = state_15928__$1;
(statearr_16084_18487[(2)] = inst_15832);

(statearr_16084_18487[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (5))){
var inst_15769 = cljs.core.deref(cs);
var inst_15770 = cljs.core.seq(inst_15769);
var inst_15771 = inst_15770;
var inst_15772 = null;
var inst_15773 = (0);
var inst_15774 = (0);
var state_15928__$1 = (function (){var statearr_16085 = state_15928;
(statearr_16085[(14)] = inst_15771);

(statearr_16085[(15)] = inst_15772);

(statearr_16085[(16)] = inst_15773);

(statearr_16085[(17)] = inst_15774);

return statearr_16085;
})();
var statearr_16086_18494 = state_15928__$1;
(statearr_16086_18494[(2)] = null);

(statearr_16086_18494[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (14))){
var state_15928__$1 = state_15928;
var statearr_16091_18498 = state_15928__$1;
(statearr_16091_18498[(2)] = null);

(statearr_16091_18498[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (45))){
var inst_15916 = (state_15928[(2)]);
var state_15928__$1 = state_15928;
var statearr_16093_18499 = state_15928__$1;
(statearr_16093_18499[(2)] = inst_15916);

(statearr_16093_18499[(1)] = (44));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (26))){
var inst_15839 = (state_15928[(27)]);
var inst_15909 = (state_15928[(2)]);
var inst_15912 = cljs.core.seq(inst_15839);
var state_15928__$1 = (function (){var statearr_16095 = state_15928;
(statearr_16095[(29)] = inst_15909);

return statearr_16095;
})();
if(inst_15912){
var statearr_16096_18500 = state_15928__$1;
(statearr_16096_18500[(1)] = (42));

} else {
var statearr_16097_18501 = state_15928__$1;
(statearr_16097_18501[(1)] = (43));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (16))){
var inst_15798 = (state_15928[(7)]);
var inst_15802 = cljs.core.chunked_seq_QMARK_(inst_15798);
var state_15928__$1 = state_15928;
if(inst_15802){
var statearr_16098_18502 = state_15928__$1;
(statearr_16098_18502[(1)] = (19));

} else {
var statearr_16100_18503 = state_15928__$1;
(statearr_16100_18503[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (38))){
var inst_15902 = (state_15928[(2)]);
var state_15928__$1 = state_15928;
var statearr_16103_18504 = state_15928__$1;
(statearr_16103_18504[(2)] = inst_15902);

(statearr_16103_18504[(1)] = (35));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (30))){
var state_15928__$1 = state_15928;
var statearr_16106_18505 = state_15928__$1;
(statearr_16106_18505[(2)] = null);

(statearr_16106_18505[(1)] = (32));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (10))){
var inst_15772 = (state_15928[(15)]);
var inst_15774 = (state_15928[(17)]);
var inst_15786 = cljs.core._nth(inst_15772,inst_15774);
var inst_15787 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_15786,(0),null);
var inst_15789 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_15786,(1),null);
var state_15928__$1 = (function (){var statearr_16111 = state_15928;
(statearr_16111[(24)] = inst_15787);

return statearr_16111;
})();
if(cljs.core.truth_(inst_15789)){
var statearr_16120_18507 = state_15928__$1;
(statearr_16120_18507[(1)] = (13));

} else {
var statearr_16124_18508 = state_15928__$1;
(statearr_16124_18508[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (18))){
var inst_15828 = (state_15928[(2)]);
var state_15928__$1 = state_15928;
var statearr_16130_18509 = state_15928__$1;
(statearr_16130_18509[(2)] = inst_15828);

(statearr_16130_18509[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (42))){
var state_15928__$1 = state_15928;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15928__$1,(45),dchan);
} else {
if((state_val_15930 === (37))){
var inst_15877 = (state_15928[(23)]);
var inst_15890 = (state_15928[(22)]);
var inst_15761 = (state_15928[(12)]);
var inst_15890__$1 = cljs.core.first(inst_15877);
var inst_15891 = cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$3(inst_15890__$1,inst_15761,done);
var state_15928__$1 = (function (){var statearr_16138 = state_15928;
(statearr_16138[(22)] = inst_15890__$1);

return statearr_16138;
})();
if(cljs.core.truth_(inst_15891)){
var statearr_16139_18510 = state_15928__$1;
(statearr_16139_18510[(1)] = (39));

} else {
var statearr_16141_18511 = state_15928__$1;
(statearr_16141_18511[(1)] = (40));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15930 === (8))){
var inst_15774 = (state_15928[(17)]);
var inst_15773 = (state_15928[(16)]);
var inst_15777 = (inst_15774 < inst_15773);
var inst_15778 = inst_15777;
var state_15928__$1 = state_15928;
if(cljs.core.truth_(inst_15778)){
var statearr_16142_18512 = state_15928__$1;
(statearr_16142_18512[(1)] = (10));

} else {
var statearr_16143_18513 = state_15928__$1;
(statearr_16143_18513[(1)] = (11));

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
var cljs$core$async$mult_$_state_machine__14014__auto__ = null;
var cljs$core$async$mult_$_state_machine__14014__auto____0 = (function (){
var statearr_16148 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_16148[(0)] = cljs$core$async$mult_$_state_machine__14014__auto__);

(statearr_16148[(1)] = (1));

return statearr_16148;
});
var cljs$core$async$mult_$_state_machine__14014__auto____1 = (function (state_15928){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_15928);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e16152){var ex__14017__auto__ = e16152;
var statearr_16154_18519 = state_15928;
(statearr_16154_18519[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_15928[(4)]))){
var statearr_16155_18520 = state_15928;
(statearr_16155_18520[(1)] = cljs.core.first((state_15928[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18524 = state_15928;
state_15928 = G__18524;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$mult_$_state_machine__14014__auto__ = function(state_15928){
switch(arguments.length){
case 0:
return cljs$core$async$mult_$_state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$mult_$_state_machine__14014__auto____1.call(this,state_15928);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$mult_$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mult_$_state_machine__14014__auto____0;
cljs$core$async$mult_$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mult_$_state_machine__14014__auto____1;
return cljs$core$async$mult_$_state_machine__14014__auto__;
})()
})();
var state__14375__auto__ = (function (){var statearr_16159 = f__14374__auto__();
(statearr_16159[(6)] = c__14373__auto___18407);

return statearr_16159;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
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
var G__16165 = arguments.length;
switch (G__16165) {
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

var cljs$core$async$Mix$admix_STAR_$dyn_18532 = (function (m,ch){
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
return cljs$core$async$Mix$admix_STAR_$dyn_18532(m,ch);
}
});

var cljs$core$async$Mix$unmix_STAR_$dyn_18537 = (function (m,ch){
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
return cljs$core$async$Mix$unmix_STAR_$dyn_18537(m,ch);
}
});

var cljs$core$async$Mix$unmix_all_STAR_$dyn_18538 = (function (m){
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
return cljs$core$async$Mix$unmix_all_STAR_$dyn_18538(m);
}
});

var cljs$core$async$Mix$toggle_STAR_$dyn_18540 = (function (m,state_map){
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
return cljs$core$async$Mix$toggle_STAR_$dyn_18540(m,state_map);
}
});

var cljs$core$async$Mix$solo_mode_STAR_$dyn_18544 = (function (m,mode){
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
return cljs$core$async$Mix$solo_mode_STAR_$dyn_18544(m,mode);
}
});

cljs.core.async.ioc_alts_BANG_ = (function cljs$core$async$ioc_alts_BANG_(var_args){
var args__5882__auto__ = [];
var len__5876__auto___18547 = arguments.length;
var i__5877__auto___18549 = (0);
while(true){
if((i__5877__auto___18549 < len__5876__auto___18547)){
args__5882__auto__.push((arguments[i__5877__auto___18549]));

var G__18550 = (i__5877__auto___18549 + (1));
i__5877__auto___18549 = G__18550;
continue;
} else {
}
break;
}

var argseq__5883__auto__ = ((((3) < args__5882__auto__.length))?(new cljs.core.IndexedSeq(args__5882__auto__.slice((3)),(0),null)):null);
return cljs.core.async.ioc_alts_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__5883__auto__);
});

(cljs.core.async.ioc_alts_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (state,cont_block,ports,p__16258){
var map__16259 = p__16258;
var map__16259__$1 = cljs.core.__destructure_map(map__16259);
var opts = map__16259__$1;
var statearr_16260_18556 = state;
(statearr_16260_18556[(1)] = cont_block);


var temp__5823__auto__ = cljs.core.async.do_alts((function (val){
var statearr_16262_18557 = state;
(statearr_16262_18557[(2)] = val);


return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state);
}),ports,opts);
if(cljs.core.truth_(temp__5823__auto__)){
var cb = temp__5823__auto__;
var statearr_16264_18558 = state;
(statearr_16264_18558[(2)] = cljs.core.deref(cb));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}));

(cljs.core.async.ioc_alts_BANG_.cljs$lang$maxFixedArity = (3));

/** @this {Function} */
(cljs.core.async.ioc_alts_BANG_.cljs$lang$applyTo = (function (seq16246){
var G__16247 = cljs.core.first(seq16246);
var seq16246__$1 = cljs.core.next(seq16246);
var G__16248 = cljs.core.first(seq16246__$1);
var seq16246__$2 = cljs.core.next(seq16246__$1);
var G__16249 = cljs.core.first(seq16246__$2);
var seq16246__$3 = cljs.core.next(seq16246__$2);
var self__5861__auto__ = this;
return self__5861__auto__.cljs$core$IFn$_invoke$arity$variadic(G__16247,G__16248,G__16249,seq16246__$3);
}));


/**
* @constructor
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.async.Mix}
 * @implements {cljs.core.async.Mux}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async16281 = (function (change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,meta16282){
this.change = change;
this.solo_mode = solo_mode;
this.pick = pick;
this.cs = cs;
this.calc_state = calc_state;
this.out = out;
this.changed = changed;
this.solo_modes = solo_modes;
this.attrs = attrs;
this.meta16282 = meta16282;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async16281.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_16283,meta16282__$1){
var self__ = this;
var _16283__$1 = this;
return (new cljs.core.async.t_cljs$core$async16281(self__.change,self__.solo_mode,self__.pick,self__.cs,self__.calc_state,self__.out,self__.changed,self__.solo_modes,self__.attrs,meta16282__$1));
}));

(cljs.core.async.t_cljs$core$async16281.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_16283){
var self__ = this;
var _16283__$1 = this;
return self__.meta16282;
}));

(cljs.core.async.t_cljs$core$async16281.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16281.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.out;
}));

(cljs.core.async.t_cljs$core$async16281.prototype.cljs$core$async$Mix$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16281.prototype.cljs$core$async$Mix$admix_STAR_$arity$2 = (function (_,ch){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(self__.cs,cljs.core.assoc,ch,cljs.core.PersistentArrayMap.EMPTY);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async16281.prototype.cljs$core$async$Mix$unmix_STAR_$arity$2 = (function (_,ch){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(self__.cs,cljs.core.dissoc,ch);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async16281.prototype.cljs$core$async$Mix$unmix_all_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_(self__.cs,cljs.core.PersistentArrayMap.EMPTY);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async16281.prototype.cljs$core$async$Mix$toggle_STAR_$arity$2 = (function (_,state_map){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(self__.cs,cljs.core.partial.cljs$core$IFn$_invoke$arity$2(cljs.core.merge_with,cljs.core.merge),state_map);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async16281.prototype.cljs$core$async$Mix$solo_mode_STAR_$arity$2 = (function (_,mode){
var self__ = this;
var ___$1 = this;
if(cljs.core.truth_((self__.solo_modes.cljs$core$IFn$_invoke$arity$1 ? self__.solo_modes.cljs$core$IFn$_invoke$arity$1(mode) : self__.solo_modes.call(null,mode)))){
} else {
throw (new Error((""+"Assert failed: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1((""+"mode must be one of: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(self__.solo_modes)))+"\n"+"(solo-modes mode)")));
}

cljs.core.reset_BANG_(self__.solo_mode,mode);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async16281.getBasis = (function (){
return new cljs.core.PersistentVector(null, 10, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"change","change",477485025,null),new cljs.core.Symbol(null,"solo-mode","solo-mode",2031788074,null),new cljs.core.Symbol(null,"pick","pick",1300068175,null),new cljs.core.Symbol(null,"cs","cs",-117024463,null),new cljs.core.Symbol(null,"calc-state","calc-state",-349968968,null),new cljs.core.Symbol(null,"out","out",729986010,null),new cljs.core.Symbol(null,"changed","changed",-2083710852,null),new cljs.core.Symbol(null,"solo-modes","solo-modes",882180540,null),new cljs.core.Symbol(null,"attrs","attrs",-450137186,null),new cljs.core.Symbol(null,"meta16282","meta16282",-1775097429,null)], null);
}));

(cljs.core.async.t_cljs$core$async16281.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async16281.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async16281");

(cljs.core.async.t_cljs$core$async16281.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async16281");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async16281.
 */
cljs.core.async.__GT_t_cljs$core$async16281 = (function cljs$core$async$__GT_t_cljs$core$async16281(change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,meta16282){
return (new cljs.core.async.t_cljs$core$async16281(change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,meta16282));
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
var m = (new cljs.core.async.t_cljs$core$async16281(change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,cljs.core.PersistentArrayMap.EMPTY));
var c__14373__auto___18573 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = (function (state_16393){
var state_val_16398 = (state_16393[(1)]);
if((state_val_16398 === (7))){
var inst_16350 = (state_16393[(2)]);
var state_16393__$1 = state_16393;
if(cljs.core.truth_(inst_16350)){
var statearr_16415_18575 = state_16393__$1;
(statearr_16415_18575[(1)] = (8));

} else {
var statearr_16417_18576 = state_16393__$1;
(statearr_16417_18576[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (20))){
var inst_16342 = (state_16393[(7)]);
var state_16393__$1 = state_16393;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_16393__$1,(23),out,inst_16342);
} else {
if((state_val_16398 === (1))){
var inst_16317 = calc_state();
var inst_16318 = cljs.core.__destructure_map(inst_16317);
var inst_16320 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_16318,new cljs.core.Keyword(null,"solos","solos",1441458643));
var inst_16321 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_16318,new cljs.core.Keyword(null,"mutes","mutes",1068806309));
var inst_16322 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_16318,new cljs.core.Keyword(null,"reads","reads",-1215067361));
var inst_16324 = inst_16317;
var state_16393__$1 = (function (){var statearr_16427 = state_16393;
(statearr_16427[(8)] = inst_16320);

(statearr_16427[(9)] = inst_16321);

(statearr_16427[(10)] = inst_16322);

(statearr_16427[(11)] = inst_16324);

return statearr_16427;
})();
var statearr_16431_18577 = state_16393__$1;
(statearr_16431_18577[(2)] = null);

(statearr_16431_18577[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (24))){
var inst_16330 = (state_16393[(12)]);
var inst_16324 = inst_16330;
var state_16393__$1 = (function (){var statearr_16436 = state_16393;
(statearr_16436[(11)] = inst_16324);

return statearr_16436;
})();
var statearr_16439_18578 = state_16393__$1;
(statearr_16439_18578[(2)] = null);

(statearr_16439_18578[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (4))){
var inst_16342 = (state_16393[(7)]);
var inst_16345 = (state_16393[(13)]);
var inst_16341 = (state_16393[(2)]);
var inst_16342__$1 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_16341,(0),null);
var inst_16344 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_16341,(1),null);
var inst_16345__$1 = (inst_16342__$1 == null);
var state_16393__$1 = (function (){var statearr_16445 = state_16393;
(statearr_16445[(7)] = inst_16342__$1);

(statearr_16445[(14)] = inst_16344);

(statearr_16445[(13)] = inst_16345__$1);

return statearr_16445;
})();
if(cljs.core.truth_(inst_16345__$1)){
var statearr_16447_18579 = state_16393__$1;
(statearr_16447_18579[(1)] = (5));

} else {
var statearr_16449_18580 = state_16393__$1;
(statearr_16449_18580[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (15))){
var inst_16333 = (state_16393[(15)]);
var inst_16366 = (state_16393[(16)]);
var inst_16366__$1 = cljs.core.empty_QMARK_(inst_16333);
var state_16393__$1 = (function (){var statearr_16454 = state_16393;
(statearr_16454[(16)] = inst_16366__$1);

return statearr_16454;
})();
if(inst_16366__$1){
var statearr_16458_18581 = state_16393__$1;
(statearr_16458_18581[(1)] = (17));

} else {
var statearr_16463_18583 = state_16393__$1;
(statearr_16463_18583[(1)] = (18));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (21))){
var inst_16330 = (state_16393[(12)]);
var inst_16324 = inst_16330;
var state_16393__$1 = (function (){var statearr_16467 = state_16393;
(statearr_16467[(11)] = inst_16324);

return statearr_16467;
})();
var statearr_16469_18585 = state_16393__$1;
(statearr_16469_18585[(2)] = null);

(statearr_16469_18585[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (13))){
var inst_16359 = (state_16393[(2)]);
var inst_16360 = calc_state();
var inst_16324 = inst_16360;
var state_16393__$1 = (function (){var statearr_16477 = state_16393;
(statearr_16477[(17)] = inst_16359);

(statearr_16477[(11)] = inst_16324);

return statearr_16477;
})();
var statearr_16479_18587 = state_16393__$1;
(statearr_16479_18587[(2)] = null);

(statearr_16479_18587[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (22))){
var inst_16387 = (state_16393[(2)]);
var state_16393__$1 = state_16393;
var statearr_16482_18589 = state_16393__$1;
(statearr_16482_18589[(2)] = inst_16387);

(statearr_16482_18589[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (6))){
var inst_16344 = (state_16393[(14)]);
var inst_16348 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(inst_16344,change);
var state_16393__$1 = state_16393;
var statearr_16483_18591 = state_16393__$1;
(statearr_16483_18591[(2)] = inst_16348);

(statearr_16483_18591[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (25))){
var state_16393__$1 = state_16393;
var statearr_16484_18593 = state_16393__$1;
(statearr_16484_18593[(2)] = null);

(statearr_16484_18593[(1)] = (26));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (17))){
var inst_16334 = (state_16393[(18)]);
var inst_16344 = (state_16393[(14)]);
var inst_16368 = (inst_16334.cljs$core$IFn$_invoke$arity$1 ? inst_16334.cljs$core$IFn$_invoke$arity$1(inst_16344) : inst_16334.call(null,inst_16344));
var inst_16369 = cljs.core.not(inst_16368);
var state_16393__$1 = state_16393;
var statearr_16485_18594 = state_16393__$1;
(statearr_16485_18594[(2)] = inst_16369);

(statearr_16485_18594[(1)] = (19));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (3))){
var inst_16391 = (state_16393[(2)]);
var state_16393__$1 = state_16393;
return cljs.core.async.impl.ioc_helpers.return_chan(state_16393__$1,inst_16391);
} else {
if((state_val_16398 === (12))){
var state_16393__$1 = state_16393;
var statearr_16487_18595 = state_16393__$1;
(statearr_16487_18595[(2)] = null);

(statearr_16487_18595[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (2))){
var inst_16324 = (state_16393[(11)]);
var inst_16330 = (state_16393[(12)]);
var inst_16330__$1 = cljs.core.__destructure_map(inst_16324);
var inst_16333 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_16330__$1,new cljs.core.Keyword(null,"solos","solos",1441458643));
var inst_16334 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_16330__$1,new cljs.core.Keyword(null,"mutes","mutes",1068806309));
var inst_16336 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_16330__$1,new cljs.core.Keyword(null,"reads","reads",-1215067361));
var state_16393__$1 = (function (){var statearr_16489 = state_16393;
(statearr_16489[(12)] = inst_16330__$1);

(statearr_16489[(15)] = inst_16333);

(statearr_16489[(18)] = inst_16334);

return statearr_16489;
})();
return cljs.core.async.ioc_alts_BANG_(state_16393__$1,(4),inst_16336);
} else {
if((state_val_16398 === (23))){
var inst_16378 = (state_16393[(2)]);
var state_16393__$1 = state_16393;
if(cljs.core.truth_(inst_16378)){
var statearr_16493_18597 = state_16393__$1;
(statearr_16493_18597[(1)] = (24));

} else {
var statearr_16494_18598 = state_16393__$1;
(statearr_16494_18598[(1)] = (25));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (19))){
var inst_16372 = (state_16393[(2)]);
var state_16393__$1 = state_16393;
var statearr_16500_18599 = state_16393__$1;
(statearr_16500_18599[(2)] = inst_16372);

(statearr_16500_18599[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (11))){
var inst_16344 = (state_16393[(14)]);
var inst_16356 = cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(cs,cljs.core.dissoc,inst_16344);
var state_16393__$1 = state_16393;
var statearr_16504_18600 = state_16393__$1;
(statearr_16504_18600[(2)] = inst_16356);

(statearr_16504_18600[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (9))){
var inst_16333 = (state_16393[(15)]);
var inst_16344 = (state_16393[(14)]);
var inst_16363 = (state_16393[(19)]);
var inst_16363__$1 = (inst_16333.cljs$core$IFn$_invoke$arity$1 ? inst_16333.cljs$core$IFn$_invoke$arity$1(inst_16344) : inst_16333.call(null,inst_16344));
var state_16393__$1 = (function (){var statearr_16507 = state_16393;
(statearr_16507[(19)] = inst_16363__$1);

return statearr_16507;
})();
if(cljs.core.truth_(inst_16363__$1)){
var statearr_16510_18602 = state_16393__$1;
(statearr_16510_18602[(1)] = (14));

} else {
var statearr_16513_18604 = state_16393__$1;
(statearr_16513_18604[(1)] = (15));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (5))){
var inst_16345 = (state_16393[(13)]);
var state_16393__$1 = state_16393;
var statearr_16514_18605 = state_16393__$1;
(statearr_16514_18605[(2)] = inst_16345);

(statearr_16514_18605[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (14))){
var inst_16363 = (state_16393[(19)]);
var state_16393__$1 = state_16393;
var statearr_16515_18606 = state_16393__$1;
(statearr_16515_18606[(2)] = inst_16363);

(statearr_16515_18606[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (26))){
var inst_16383 = (state_16393[(2)]);
var state_16393__$1 = state_16393;
var statearr_16520_18608 = state_16393__$1;
(statearr_16520_18608[(2)] = inst_16383);

(statearr_16520_18608[(1)] = (22));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (16))){
var inst_16375 = (state_16393[(2)]);
var state_16393__$1 = state_16393;
if(cljs.core.truth_(inst_16375)){
var statearr_16523_18612 = state_16393__$1;
(statearr_16523_18612[(1)] = (20));

} else {
var statearr_16524_18613 = state_16393__$1;
(statearr_16524_18613[(1)] = (21));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (10))){
var inst_16389 = (state_16393[(2)]);
var state_16393__$1 = state_16393;
var statearr_16527_18614 = state_16393__$1;
(statearr_16527_18614[(2)] = inst_16389);

(statearr_16527_18614[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (18))){
var inst_16366 = (state_16393[(16)]);
var state_16393__$1 = state_16393;
var statearr_16528_18615 = state_16393__$1;
(statearr_16528_18615[(2)] = inst_16366);

(statearr_16528_18615[(1)] = (19));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16398 === (8))){
var inst_16342 = (state_16393[(7)]);
var inst_16352 = (inst_16342 == null);
var state_16393__$1 = state_16393;
if(cljs.core.truth_(inst_16352)){
var statearr_16529_18617 = state_16393__$1;
(statearr_16529_18617[(1)] = (11));

} else {
var statearr_16530_18619 = state_16393__$1;
(statearr_16530_18619[(1)] = (12));

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
var cljs$core$async$mix_$_state_machine__14014__auto__ = null;
var cljs$core$async$mix_$_state_machine__14014__auto____0 = (function (){
var statearr_16532 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_16532[(0)] = cljs$core$async$mix_$_state_machine__14014__auto__);

(statearr_16532[(1)] = (1));

return statearr_16532;
});
var cljs$core$async$mix_$_state_machine__14014__auto____1 = (function (state_16393){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_16393);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e16539){var ex__14017__auto__ = e16539;
var statearr_16540_18621 = state_16393;
(statearr_16540_18621[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_16393[(4)]))){
var statearr_16542_18622 = state_16393;
(statearr_16542_18622[(1)] = cljs.core.first((state_16393[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18629 = state_16393;
state_16393 = G__18629;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$mix_$_state_machine__14014__auto__ = function(state_16393){
switch(arguments.length){
case 0:
return cljs$core$async$mix_$_state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$mix_$_state_machine__14014__auto____1.call(this,state_16393);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$mix_$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mix_$_state_machine__14014__auto____0;
cljs$core$async$mix_$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mix_$_state_machine__14014__auto____1;
return cljs$core$async$mix_$_state_machine__14014__auto__;
})()
})();
var state__14375__auto__ = (function (){var statearr_16548 = f__14374__auto__();
(statearr_16548[(6)] = c__14373__auto___18573);

return statearr_16548;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
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

var cljs$core$async$Pub$sub_STAR_$dyn_18636 = (function (p,v,ch,close_QMARK_){
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
return cljs$core$async$Pub$sub_STAR_$dyn_18636(p,v,ch,close_QMARK_);
}
});

var cljs$core$async$Pub$unsub_STAR_$dyn_18637 = (function (p,v,ch){
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
return cljs$core$async$Pub$unsub_STAR_$dyn_18637(p,v,ch);
}
});

var cljs$core$async$Pub$unsub_all_STAR_$dyn_18644 = (function() {
var G__18645 = null;
var G__18645__1 = (function (p){
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
var G__18645__2 = (function (p,v){
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
G__18645 = function(p,v){
switch(arguments.length){
case 1:
return G__18645__1.call(this,p);
case 2:
return G__18645__2.call(this,p,v);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
G__18645.cljs$core$IFn$_invoke$arity$1 = G__18645__1;
G__18645.cljs$core$IFn$_invoke$arity$2 = G__18645__2;
return G__18645;
})()
;
cljs.core.async.unsub_all_STAR_ = (function cljs$core$async$unsub_all_STAR_(var_args){
var G__16595 = arguments.length;
switch (G__16595) {
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
return cljs$core$async$Pub$unsub_all_STAR_$dyn_18644(p);
}
}));

(cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$2 = (function (p,v){
if((((!((p == null)))) && ((!((p.cljs$core$async$Pub$unsub_all_STAR_$arity$2 == null)))))){
return p.cljs$core$async$Pub$unsub_all_STAR_$arity$2(p,v);
} else {
return cljs$core$async$Pub$unsub_all_STAR_$dyn_18644(p,v);
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
cljs.core.async.t_cljs$core$async16632 = (function (ch,topic_fn,buf_fn,mults,ensure_mult,meta16633){
this.ch = ch;
this.topic_fn = topic_fn;
this.buf_fn = buf_fn;
this.mults = mults;
this.ensure_mult = ensure_mult;
this.meta16633 = meta16633;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async16632.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_16634,meta16633__$1){
var self__ = this;
var _16634__$1 = this;
return (new cljs.core.async.t_cljs$core$async16632(self__.ch,self__.topic_fn,self__.buf_fn,self__.mults,self__.ensure_mult,meta16633__$1));
}));

(cljs.core.async.t_cljs$core$async16632.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_16634){
var self__ = this;
var _16634__$1 = this;
return self__.meta16633;
}));

(cljs.core.async.t_cljs$core$async16632.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16632.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.ch;
}));

(cljs.core.async.t_cljs$core$async16632.prototype.cljs$core$async$Pub$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16632.prototype.cljs$core$async$Pub$sub_STAR_$arity$4 = (function (p,topic,ch__$1,close_QMARK_){
var self__ = this;
var p__$1 = this;
var m = (self__.ensure_mult.cljs$core$IFn$_invoke$arity$1 ? self__.ensure_mult.cljs$core$IFn$_invoke$arity$1(topic) : self__.ensure_mult.call(null,topic));
return cljs.core.async.tap.cljs$core$IFn$_invoke$arity$3(m,ch__$1,close_QMARK_);
}));

(cljs.core.async.t_cljs$core$async16632.prototype.cljs$core$async$Pub$unsub_STAR_$arity$3 = (function (p,topic,ch__$1){
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

(cljs.core.async.t_cljs$core$async16632.prototype.cljs$core$async$Pub$unsub_all_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.reset_BANG_(self__.mults,cljs.core.PersistentArrayMap.EMPTY);
}));

(cljs.core.async.t_cljs$core$async16632.prototype.cljs$core$async$Pub$unsub_all_STAR_$arity$2 = (function (_,topic){
var self__ = this;
var ___$1 = this;
return cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(self__.mults,cljs.core.dissoc,topic);
}));

(cljs.core.async.t_cljs$core$async16632.getBasis = (function (){
return new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"topic-fn","topic-fn",-862449736,null),new cljs.core.Symbol(null,"buf-fn","buf-fn",-1200281591,null),new cljs.core.Symbol(null,"mults","mults",-461114485,null),new cljs.core.Symbol(null,"ensure-mult","ensure-mult",1796584816,null),new cljs.core.Symbol(null,"meta16633","meta16633",-426952115,null)], null);
}));

(cljs.core.async.t_cljs$core$async16632.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async16632.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async16632");

(cljs.core.async.t_cljs$core$async16632.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async16632");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async16632.
 */
cljs.core.async.__GT_t_cljs$core$async16632 = (function cljs$core$async$__GT_t_cljs$core$async16632(ch,topic_fn,buf_fn,mults,ensure_mult,meta16633){
return (new cljs.core.async.t_cljs$core$async16632(ch,topic_fn,buf_fn,mults,ensure_mult,meta16633));
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
var G__16623 = arguments.length;
switch (G__16623) {
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
return cljs.core.get.cljs$core$IFn$_invoke$arity$2(cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$2(mults,(function (p1__16612_SHARP_){
if(cljs.core.truth_((p1__16612_SHARP_.cljs$core$IFn$_invoke$arity$1 ? p1__16612_SHARP_.cljs$core$IFn$_invoke$arity$1(topic) : p1__16612_SHARP_.call(null,topic)))){
return p1__16612_SHARP_;
} else {
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(p1__16612_SHARP_,topic,cljs.core.async.mult(cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((buf_fn.cljs$core$IFn$_invoke$arity$1 ? buf_fn.cljs$core$IFn$_invoke$arity$1(topic) : buf_fn.call(null,topic)))));
}
})),topic);
}
});
var p = (new cljs.core.async.t_cljs$core$async16632(ch,topic_fn,buf_fn,mults,ensure_mult,cljs.core.PersistentArrayMap.EMPTY));
var c__14373__auto___18658 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = (function (state_16763){
var state_val_16764 = (state_16763[(1)]);
if((state_val_16764 === (7))){
var inst_16758 = (state_16763[(2)]);
var state_16763__$1 = state_16763;
var statearr_16765_18659 = state_16763__$1;
(statearr_16765_18659[(2)] = inst_16758);

(statearr_16765_18659[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (20))){
var state_16763__$1 = state_16763;
var statearr_16768_18660 = state_16763__$1;
(statearr_16768_18660[(2)] = null);

(statearr_16768_18660[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (1))){
var state_16763__$1 = state_16763;
var statearr_16771_18664 = state_16763__$1;
(statearr_16771_18664[(2)] = null);

(statearr_16771_18664[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (24))){
var inst_16740 = (state_16763[(7)]);
var inst_16750 = cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(mults,cljs.core.dissoc,inst_16740);
var state_16763__$1 = state_16763;
var statearr_16776_18666 = state_16763__$1;
(statearr_16776_18666[(2)] = inst_16750);

(statearr_16776_18666[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (4))){
var inst_16681 = (state_16763[(8)]);
var inst_16681__$1 = (state_16763[(2)]);
var inst_16682 = (inst_16681__$1 == null);
var state_16763__$1 = (function (){var statearr_16780 = state_16763;
(statearr_16780[(8)] = inst_16681__$1);

return statearr_16780;
})();
if(cljs.core.truth_(inst_16682)){
var statearr_16787_18670 = state_16763__$1;
(statearr_16787_18670[(1)] = (5));

} else {
var statearr_16790_18671 = state_16763__$1;
(statearr_16790_18671[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (15))){
var inst_16734 = (state_16763[(2)]);
var state_16763__$1 = state_16763;
var statearr_16791_18675 = state_16763__$1;
(statearr_16791_18675[(2)] = inst_16734);

(statearr_16791_18675[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (21))){
var inst_16755 = (state_16763[(2)]);
var state_16763__$1 = (function (){var statearr_16792 = state_16763;
(statearr_16792[(9)] = inst_16755);

return statearr_16792;
})();
var statearr_16793_18676 = state_16763__$1;
(statearr_16793_18676[(2)] = null);

(statearr_16793_18676[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (13))){
var inst_16714 = (state_16763[(10)]);
var inst_16718 = cljs.core.chunked_seq_QMARK_(inst_16714);
var state_16763__$1 = state_16763;
if(inst_16718){
var statearr_16795_18677 = state_16763__$1;
(statearr_16795_18677[(1)] = (16));

} else {
var statearr_16799_18678 = state_16763__$1;
(statearr_16799_18678[(1)] = (17));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (22))){
var inst_16747 = (state_16763[(2)]);
var state_16763__$1 = state_16763;
if(cljs.core.truth_(inst_16747)){
var statearr_16800_18679 = state_16763__$1;
(statearr_16800_18679[(1)] = (23));

} else {
var statearr_16801_18680 = state_16763__$1;
(statearr_16801_18680[(1)] = (24));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (6))){
var inst_16681 = (state_16763[(8)]);
var inst_16740 = (state_16763[(7)]);
var inst_16742 = (state_16763[(11)]);
var inst_16740__$1 = (topic_fn.cljs$core$IFn$_invoke$arity$1 ? topic_fn.cljs$core$IFn$_invoke$arity$1(inst_16681) : topic_fn.call(null,inst_16681));
var inst_16741 = cljs.core.deref(mults);
var inst_16742__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_16741,inst_16740__$1);
var state_16763__$1 = (function (){var statearr_16804 = state_16763;
(statearr_16804[(7)] = inst_16740__$1);

(statearr_16804[(11)] = inst_16742__$1);

return statearr_16804;
})();
if(cljs.core.truth_(inst_16742__$1)){
var statearr_16805_18683 = state_16763__$1;
(statearr_16805_18683[(1)] = (19));

} else {
var statearr_16806_18684 = state_16763__$1;
(statearr_16806_18684[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (25))){
var inst_16752 = (state_16763[(2)]);
var state_16763__$1 = state_16763;
var statearr_16811_18685 = state_16763__$1;
(statearr_16811_18685[(2)] = inst_16752);

(statearr_16811_18685[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (17))){
var inst_16714 = (state_16763[(10)]);
var inst_16725 = cljs.core.first(inst_16714);
var inst_16726 = cljs.core.async.muxch_STAR_(inst_16725);
var inst_16727 = cljs.core.async.close_BANG_(inst_16726);
var inst_16728 = cljs.core.next(inst_16714);
var inst_16696 = inst_16728;
var inst_16697 = null;
var inst_16698 = (0);
var inst_16699 = (0);
var state_16763__$1 = (function (){var statearr_16812 = state_16763;
(statearr_16812[(12)] = inst_16727);

(statearr_16812[(13)] = inst_16696);

(statearr_16812[(14)] = inst_16697);

(statearr_16812[(15)] = inst_16698);

(statearr_16812[(16)] = inst_16699);

return statearr_16812;
})();
var statearr_16814_18686 = state_16763__$1;
(statearr_16814_18686[(2)] = null);

(statearr_16814_18686[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (3))){
var inst_16760 = (state_16763[(2)]);
var state_16763__$1 = state_16763;
return cljs.core.async.impl.ioc_helpers.return_chan(state_16763__$1,inst_16760);
} else {
if((state_val_16764 === (12))){
var inst_16736 = (state_16763[(2)]);
var state_16763__$1 = state_16763;
var statearr_16818_18687 = state_16763__$1;
(statearr_16818_18687[(2)] = inst_16736);

(statearr_16818_18687[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (2))){
var state_16763__$1 = state_16763;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_16763__$1,(4),ch);
} else {
if((state_val_16764 === (23))){
var state_16763__$1 = state_16763;
var statearr_16819_18688 = state_16763__$1;
(statearr_16819_18688[(2)] = null);

(statearr_16819_18688[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (19))){
var inst_16742 = (state_16763[(11)]);
var inst_16681 = (state_16763[(8)]);
var inst_16745 = cljs.core.async.muxch_STAR_(inst_16742);
var state_16763__$1 = state_16763;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_16763__$1,(22),inst_16745,inst_16681);
} else {
if((state_val_16764 === (11))){
var inst_16696 = (state_16763[(13)]);
var inst_16714 = (state_16763[(10)]);
var inst_16714__$1 = cljs.core.seq(inst_16696);
var state_16763__$1 = (function (){var statearr_16821 = state_16763;
(statearr_16821[(10)] = inst_16714__$1);

return statearr_16821;
})();
if(inst_16714__$1){
var statearr_16825_18689 = state_16763__$1;
(statearr_16825_18689[(1)] = (13));

} else {
var statearr_16826_18690 = state_16763__$1;
(statearr_16826_18690[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (9))){
var inst_16738 = (state_16763[(2)]);
var state_16763__$1 = state_16763;
var statearr_16827_18691 = state_16763__$1;
(statearr_16827_18691[(2)] = inst_16738);

(statearr_16827_18691[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (5))){
var inst_16692 = cljs.core.deref(mults);
var inst_16693 = cljs.core.vals(inst_16692);
var inst_16694 = cljs.core.seq(inst_16693);
var inst_16696 = inst_16694;
var inst_16697 = null;
var inst_16698 = (0);
var inst_16699 = (0);
var state_16763__$1 = (function (){var statearr_16828 = state_16763;
(statearr_16828[(13)] = inst_16696);

(statearr_16828[(14)] = inst_16697);

(statearr_16828[(15)] = inst_16698);

(statearr_16828[(16)] = inst_16699);

return statearr_16828;
})();
var statearr_16830_18692 = state_16763__$1;
(statearr_16830_18692[(2)] = null);

(statearr_16830_18692[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (14))){
var state_16763__$1 = state_16763;
var statearr_16834_18693 = state_16763__$1;
(statearr_16834_18693[(2)] = null);

(statearr_16834_18693[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (16))){
var inst_16714 = (state_16763[(10)]);
var inst_16720 = cljs.core.chunk_first(inst_16714);
var inst_16721 = cljs.core.chunk_rest(inst_16714);
var inst_16722 = cljs.core.count(inst_16720);
var inst_16696 = inst_16721;
var inst_16697 = inst_16720;
var inst_16698 = inst_16722;
var inst_16699 = (0);
var state_16763__$1 = (function (){var statearr_16836 = state_16763;
(statearr_16836[(13)] = inst_16696);

(statearr_16836[(14)] = inst_16697);

(statearr_16836[(15)] = inst_16698);

(statearr_16836[(16)] = inst_16699);

return statearr_16836;
})();
var statearr_16837_18696 = state_16763__$1;
(statearr_16837_18696[(2)] = null);

(statearr_16837_18696[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (10))){
var inst_16697 = (state_16763[(14)]);
var inst_16699 = (state_16763[(16)]);
var inst_16696 = (state_16763[(13)]);
var inst_16698 = (state_16763[(15)]);
var inst_16706 = cljs.core._nth(inst_16697,inst_16699);
var inst_16707 = cljs.core.async.muxch_STAR_(inst_16706);
var inst_16708 = cljs.core.async.close_BANG_(inst_16707);
var inst_16710 = (inst_16699 + (1));
var tmp16831 = inst_16697;
var tmp16832 = inst_16696;
var tmp16833 = inst_16698;
var inst_16696__$1 = tmp16832;
var inst_16697__$1 = tmp16831;
var inst_16698__$1 = tmp16833;
var inst_16699__$1 = inst_16710;
var state_16763__$1 = (function (){var statearr_16838 = state_16763;
(statearr_16838[(17)] = inst_16708);

(statearr_16838[(13)] = inst_16696__$1);

(statearr_16838[(14)] = inst_16697__$1);

(statearr_16838[(15)] = inst_16698__$1);

(statearr_16838[(16)] = inst_16699__$1);

return statearr_16838;
})();
var statearr_16839_18697 = state_16763__$1;
(statearr_16839_18697[(2)] = null);

(statearr_16839_18697[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (18))){
var inst_16731 = (state_16763[(2)]);
var state_16763__$1 = state_16763;
var statearr_16844_18701 = state_16763__$1;
(statearr_16844_18701[(2)] = inst_16731);

(statearr_16844_18701[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16764 === (8))){
var inst_16699 = (state_16763[(16)]);
var inst_16698 = (state_16763[(15)]);
var inst_16702 = (inst_16699 < inst_16698);
var inst_16703 = inst_16702;
var state_16763__$1 = state_16763;
if(cljs.core.truth_(inst_16703)){
var statearr_16845_18702 = state_16763__$1;
(statearr_16845_18702[(1)] = (10));

} else {
var statearr_16846_18703 = state_16763__$1;
(statearr_16846_18703[(1)] = (11));

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
var cljs$core$async$state_machine__14014__auto__ = null;
var cljs$core$async$state_machine__14014__auto____0 = (function (){
var statearr_16848 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_16848[(0)] = cljs$core$async$state_machine__14014__auto__);

(statearr_16848[(1)] = (1));

return statearr_16848;
});
var cljs$core$async$state_machine__14014__auto____1 = (function (state_16763){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_16763);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e16849){var ex__14017__auto__ = e16849;
var statearr_16850_18704 = state_16763;
(statearr_16850_18704[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_16763[(4)]))){
var statearr_16851_18705 = state_16763;
(statearr_16851_18705[(1)] = cljs.core.first((state_16763[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18706 = state_16763;
state_16763 = G__18706;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$state_machine__14014__auto__ = function(state_16763){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14014__auto____1.call(this,state_16763);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14014__auto____0;
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14014__auto____1;
return cljs$core$async$state_machine__14014__auto__;
})()
})();
var state__14375__auto__ = (function (){var statearr_16855 = f__14374__auto__();
(statearr_16855[(6)] = c__14373__auto___18658);

return statearr_16855;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
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
var G__16860 = arguments.length;
switch (G__16860) {
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
var G__16862 = arguments.length;
switch (G__16862) {
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
var G__16874 = arguments.length;
switch (G__16874) {
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
var c__14373__auto___18719 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = (function (state_16939){
var state_val_16940 = (state_16939[(1)]);
if((state_val_16940 === (7))){
var state_16939__$1 = state_16939;
var statearr_16941_18724 = state_16939__$1;
(statearr_16941_18724[(2)] = null);

(statearr_16941_18724[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16940 === (1))){
var state_16939__$1 = state_16939;
var statearr_16942_18725 = state_16939__$1;
(statearr_16942_18725[(2)] = null);

(statearr_16942_18725[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16940 === (4))){
var inst_16885 = (state_16939[(7)]);
var inst_16884 = (state_16939[(8)]);
var inst_16887 = (inst_16885 < inst_16884);
var state_16939__$1 = state_16939;
if(cljs.core.truth_(inst_16887)){
var statearr_16943_18727 = state_16939__$1;
(statearr_16943_18727[(1)] = (6));

} else {
var statearr_16944_18728 = state_16939__$1;
(statearr_16944_18728[(1)] = (7));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16940 === (15))){
var inst_16921 = (state_16939[(9)]);
var inst_16929 = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(f,inst_16921);
var state_16939__$1 = state_16939;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_16939__$1,(17),out,inst_16929);
} else {
if((state_val_16940 === (13))){
var inst_16921 = (state_16939[(9)]);
var inst_16921__$1 = (state_16939[(2)]);
var inst_16922 = cljs.core.some(cljs.core.nil_QMARK_,inst_16921__$1);
var state_16939__$1 = (function (){var statearr_16945 = state_16939;
(statearr_16945[(9)] = inst_16921__$1);

return statearr_16945;
})();
if(cljs.core.truth_(inst_16922)){
var statearr_16946_18729 = state_16939__$1;
(statearr_16946_18729[(1)] = (14));

} else {
var statearr_16947_18730 = state_16939__$1;
(statearr_16947_18730[(1)] = (15));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16940 === (6))){
var state_16939__$1 = state_16939;
var statearr_16948_18732 = state_16939__$1;
(statearr_16948_18732[(2)] = null);

(statearr_16948_18732[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16940 === (17))){
var inst_16931 = (state_16939[(2)]);
var state_16939__$1 = (function (){var statearr_16956 = state_16939;
(statearr_16956[(10)] = inst_16931);

return statearr_16956;
})();
var statearr_16958_18733 = state_16939__$1;
(statearr_16958_18733[(2)] = null);

(statearr_16958_18733[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16940 === (3))){
var inst_16936 = (state_16939[(2)]);
var state_16939__$1 = state_16939;
return cljs.core.async.impl.ioc_helpers.return_chan(state_16939__$1,inst_16936);
} else {
if((state_val_16940 === (12))){
var _ = (function (){var statearr_16960 = state_16939;
(statearr_16960[(4)] = cljs.core.rest((state_16939[(4)])));

return statearr_16960;
})();
var state_16939__$1 = state_16939;
var ex16955 = (state_16939__$1[(2)]);
var statearr_16965_18735 = state_16939__$1;
(statearr_16965_18735[(5)] = ex16955);


if((ex16955 instanceof Object)){
var statearr_16970_18736 = state_16939__$1;
(statearr_16970_18736[(1)] = (11));

(statearr_16970_18736[(5)] = null);

} else {
throw ex16955;

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16940 === (2))){
var inst_16881 = cljs.core.reset_BANG_(dctr,cnt);
var inst_16884 = cnt;
var inst_16885 = (0);
var state_16939__$1 = (function (){var statearr_16976 = state_16939;
(statearr_16976[(11)] = inst_16881);

(statearr_16976[(8)] = inst_16884);

(statearr_16976[(7)] = inst_16885);

return statearr_16976;
})();
var statearr_16977_18737 = state_16939__$1;
(statearr_16977_18737[(2)] = null);

(statearr_16977_18737[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16940 === (11))){
var inst_16892 = (state_16939[(2)]);
var inst_16900 = cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$2(dctr,cljs.core.dec);
var state_16939__$1 = (function (){var statearr_16978 = state_16939;
(statearr_16978[(12)] = inst_16892);

return statearr_16978;
})();
var statearr_16979_18739 = state_16939__$1;
(statearr_16979_18739[(2)] = inst_16900);

(statearr_16979_18739[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16940 === (9))){
var inst_16885 = (state_16939[(7)]);
var _ = (function (){var statearr_16981 = state_16939;
(statearr_16981[(4)] = cljs.core.cons((12),(state_16939[(4)])));

return statearr_16981;
})();
var inst_16906 = (chs__$1.cljs$core$IFn$_invoke$arity$1 ? chs__$1.cljs$core$IFn$_invoke$arity$1(inst_16885) : chs__$1.call(null,inst_16885));
var inst_16907 = (done.cljs$core$IFn$_invoke$arity$1 ? done.cljs$core$IFn$_invoke$arity$1(inst_16885) : done.call(null,inst_16885));
var inst_16908 = cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$2(inst_16906,inst_16907);
var ___$1 = (function (){var statearr_16984 = state_16939;
(statearr_16984[(4)] = cljs.core.rest((state_16939[(4)])));

return statearr_16984;
})();
var state_16939__$1 = state_16939;
var statearr_16985_18741 = state_16939__$1;
(statearr_16985_18741[(2)] = inst_16908);

(statearr_16985_18741[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16940 === (5))){
var inst_16918 = (state_16939[(2)]);
var state_16939__$1 = (function (){var statearr_16986 = state_16939;
(statearr_16986[(13)] = inst_16918);

return statearr_16986;
})();
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_16939__$1,(13),dchan);
} else {
if((state_val_16940 === (14))){
var inst_16924 = cljs.core.async.close_BANG_(out);
var state_16939__$1 = state_16939;
var statearr_16987_18750 = state_16939__$1;
(statearr_16987_18750[(2)] = inst_16924);

(statearr_16987_18750[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16940 === (16))){
var inst_16934 = (state_16939[(2)]);
var state_16939__$1 = state_16939;
var statearr_16991_18751 = state_16939__$1;
(statearr_16991_18751[(2)] = inst_16934);

(statearr_16991_18751[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16940 === (10))){
var inst_16885 = (state_16939[(7)]);
var inst_16911 = (state_16939[(2)]);
var inst_16912 = (inst_16885 + (1));
var inst_16885__$1 = inst_16912;
var state_16939__$1 = (function (){var statearr_16992 = state_16939;
(statearr_16992[(14)] = inst_16911);

(statearr_16992[(7)] = inst_16885__$1);

return statearr_16992;
})();
var statearr_16998_18752 = state_16939__$1;
(statearr_16998_18752[(2)] = null);

(statearr_16998_18752[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16940 === (8))){
var inst_16916 = (state_16939[(2)]);
var state_16939__$1 = state_16939;
var statearr_16999_18753 = state_16939__$1;
(statearr_16999_18753[(2)] = inst_16916);

(statearr_16999_18753[(1)] = (5));


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
var cljs$core$async$state_machine__14014__auto__ = null;
var cljs$core$async$state_machine__14014__auto____0 = (function (){
var statearr_17000 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_17000[(0)] = cljs$core$async$state_machine__14014__auto__);

(statearr_17000[(1)] = (1));

return statearr_17000;
});
var cljs$core$async$state_machine__14014__auto____1 = (function (state_16939){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_16939);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e17001){var ex__14017__auto__ = e17001;
var statearr_17002_18759 = state_16939;
(statearr_17002_18759[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_16939[(4)]))){
var statearr_17003_18760 = state_16939;
(statearr_17003_18760[(1)] = cljs.core.first((state_16939[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18761 = state_16939;
state_16939 = G__18761;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$state_machine__14014__auto__ = function(state_16939){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14014__auto____1.call(this,state_16939);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14014__auto____0;
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14014__auto____1;
return cljs$core$async$state_machine__14014__auto__;
})()
})();
var state__14375__auto__ = (function (){var statearr_17005 = f__14374__auto__();
(statearr_17005[(6)] = c__14373__auto___18719);

return statearr_17005;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
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
var G__17012 = arguments.length;
switch (G__17012) {
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
var c__14373__auto___18769 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = (function (state_17045){
var state_val_17046 = (state_17045[(1)]);
if((state_val_17046 === (7))){
var inst_17023 = (state_17045[(7)]);
var inst_17024 = (state_17045[(8)]);
var inst_17023__$1 = (state_17045[(2)]);
var inst_17024__$1 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_17023__$1,(0),null);
var inst_17025 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_17023__$1,(1),null);
var inst_17026 = (inst_17024__$1 == null);
var state_17045__$1 = (function (){var statearr_17059 = state_17045;
(statearr_17059[(7)] = inst_17023__$1);

(statearr_17059[(8)] = inst_17024__$1);

(statearr_17059[(9)] = inst_17025);

return statearr_17059;
})();
if(cljs.core.truth_(inst_17026)){
var statearr_17063_18777 = state_17045__$1;
(statearr_17063_18777[(1)] = (8));

} else {
var statearr_17064_18778 = state_17045__$1;
(statearr_17064_18778[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17046 === (1))){
var inst_17013 = cljs.core.vec(chs);
var inst_17014 = inst_17013;
var state_17045__$1 = (function (){var statearr_17065 = state_17045;
(statearr_17065[(10)] = inst_17014);

return statearr_17065;
})();
var statearr_17066_18779 = state_17045__$1;
(statearr_17066_18779[(2)] = null);

(statearr_17066_18779[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17046 === (4))){
var inst_17014 = (state_17045[(10)]);
var state_17045__$1 = state_17045;
return cljs.core.async.ioc_alts_BANG_(state_17045__$1,(7),inst_17014);
} else {
if((state_val_17046 === (6))){
var inst_17041 = (state_17045[(2)]);
var state_17045__$1 = state_17045;
var statearr_17067_18781 = state_17045__$1;
(statearr_17067_18781[(2)] = inst_17041);

(statearr_17067_18781[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17046 === (3))){
var inst_17043 = (state_17045[(2)]);
var state_17045__$1 = state_17045;
return cljs.core.async.impl.ioc_helpers.return_chan(state_17045__$1,inst_17043);
} else {
if((state_val_17046 === (2))){
var inst_17014 = (state_17045[(10)]);
var inst_17016 = cljs.core.count(inst_17014);
var inst_17017 = (inst_17016 > (0));
var state_17045__$1 = state_17045;
if(cljs.core.truth_(inst_17017)){
var statearr_17070_18785 = state_17045__$1;
(statearr_17070_18785[(1)] = (4));

} else {
var statearr_17071_18789 = state_17045__$1;
(statearr_17071_18789[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17046 === (11))){
var inst_17014 = (state_17045[(10)]);
var inst_17034 = (state_17045[(2)]);
var tmp17068 = inst_17014;
var inst_17014__$1 = tmp17068;
var state_17045__$1 = (function (){var statearr_17072 = state_17045;
(statearr_17072[(11)] = inst_17034);

(statearr_17072[(10)] = inst_17014__$1);

return statearr_17072;
})();
var statearr_17073_18790 = state_17045__$1;
(statearr_17073_18790[(2)] = null);

(statearr_17073_18790[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17046 === (9))){
var inst_17024 = (state_17045[(8)]);
var state_17045__$1 = state_17045;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17045__$1,(11),out,inst_17024);
} else {
if((state_val_17046 === (5))){
var inst_17039 = cljs.core.async.close_BANG_(out);
var state_17045__$1 = state_17045;
var statearr_17074_18793 = state_17045__$1;
(statearr_17074_18793[(2)] = inst_17039);

(statearr_17074_18793[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17046 === (10))){
var inst_17037 = (state_17045[(2)]);
var state_17045__$1 = state_17045;
var statearr_17078_18794 = state_17045__$1;
(statearr_17078_18794[(2)] = inst_17037);

(statearr_17078_18794[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17046 === (8))){
var inst_17014 = (state_17045[(10)]);
var inst_17023 = (state_17045[(7)]);
var inst_17024 = (state_17045[(8)]);
var inst_17025 = (state_17045[(9)]);
var inst_17028 = (function (){var cs = inst_17014;
var vec__17019 = inst_17023;
var v = inst_17024;
var c = inst_17025;
return (function (p1__17010_SHARP_){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(c,p1__17010_SHARP_);
});
})();
var inst_17030 = cljs.core.filterv(inst_17028,inst_17014);
var inst_17014__$1 = inst_17030;
var state_17045__$1 = (function (){var statearr_17079 = state_17045;
(statearr_17079[(10)] = inst_17014__$1);

return statearr_17079;
})();
var statearr_17081_18828 = state_17045__$1;
(statearr_17081_18828[(2)] = null);

(statearr_17081_18828[(1)] = (2));


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
var cljs$core$async$state_machine__14014__auto__ = null;
var cljs$core$async$state_machine__14014__auto____0 = (function (){
var statearr_17082 = [null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_17082[(0)] = cljs$core$async$state_machine__14014__auto__);

(statearr_17082[(1)] = (1));

return statearr_17082;
});
var cljs$core$async$state_machine__14014__auto____1 = (function (state_17045){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_17045);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e17086){var ex__14017__auto__ = e17086;
var statearr_17087_18835 = state_17045;
(statearr_17087_18835[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_17045[(4)]))){
var statearr_17088_18840 = state_17045;
(statearr_17088_18840[(1)] = cljs.core.first((state_17045[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18842 = state_17045;
state_17045 = G__18842;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$state_machine__14014__auto__ = function(state_17045){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14014__auto____1.call(this,state_17045);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14014__auto____0;
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14014__auto____1;
return cljs$core$async$state_machine__14014__auto__;
})()
})();
var state__14375__auto__ = (function (){var statearr_17090 = f__14374__auto__();
(statearr_17090[(6)] = c__14373__auto___18769);

return statearr_17090;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
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
var G__17093 = arguments.length;
switch (G__17093) {
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
var c__14373__auto___18872 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = (function (state_17119){
var state_val_17120 = (state_17119[(1)]);
if((state_val_17120 === (7))){
var inst_17101 = (state_17119[(7)]);
var inst_17101__$1 = (state_17119[(2)]);
var inst_17102 = (inst_17101__$1 == null);
var inst_17103 = cljs.core.not(inst_17102);
var state_17119__$1 = (function (){var statearr_17123 = state_17119;
(statearr_17123[(7)] = inst_17101__$1);

return statearr_17123;
})();
if(inst_17103){
var statearr_17124_18879 = state_17119__$1;
(statearr_17124_18879[(1)] = (8));

} else {
var statearr_17125_18881 = state_17119__$1;
(statearr_17125_18881[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17120 === (1))){
var inst_17096 = (0);
var state_17119__$1 = (function (){var statearr_17126 = state_17119;
(statearr_17126[(8)] = inst_17096);

return statearr_17126;
})();
var statearr_17127_18883 = state_17119__$1;
(statearr_17127_18883[(2)] = null);

(statearr_17127_18883[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17120 === (4))){
var state_17119__$1 = state_17119;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_17119__$1,(7),ch);
} else {
if((state_val_17120 === (6))){
var inst_17114 = (state_17119[(2)]);
var state_17119__$1 = state_17119;
var statearr_17128_18884 = state_17119__$1;
(statearr_17128_18884[(2)] = inst_17114);

(statearr_17128_18884[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17120 === (3))){
var inst_17116 = (state_17119[(2)]);
var inst_17117 = cljs.core.async.close_BANG_(out);
var state_17119__$1 = (function (){var statearr_17133 = state_17119;
(statearr_17133[(9)] = inst_17116);

return statearr_17133;
})();
return cljs.core.async.impl.ioc_helpers.return_chan(state_17119__$1,inst_17117);
} else {
if((state_val_17120 === (2))){
var inst_17096 = (state_17119[(8)]);
var inst_17098 = (inst_17096 < n);
var state_17119__$1 = state_17119;
if(cljs.core.truth_(inst_17098)){
var statearr_17134_18898 = state_17119__$1;
(statearr_17134_18898[(1)] = (4));

} else {
var statearr_17135_18900 = state_17119__$1;
(statearr_17135_18900[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17120 === (11))){
var inst_17096 = (state_17119[(8)]);
var inst_17106 = (state_17119[(2)]);
var inst_17107 = (inst_17096 + (1));
var inst_17096__$1 = inst_17107;
var state_17119__$1 = (function (){var statearr_17136 = state_17119;
(statearr_17136[(10)] = inst_17106);

(statearr_17136[(8)] = inst_17096__$1);

return statearr_17136;
})();
var statearr_17137_18916 = state_17119__$1;
(statearr_17137_18916[(2)] = null);

(statearr_17137_18916[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17120 === (9))){
var state_17119__$1 = state_17119;
var statearr_17139_18926 = state_17119__$1;
(statearr_17139_18926[(2)] = null);

(statearr_17139_18926[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17120 === (5))){
var state_17119__$1 = state_17119;
var statearr_17143_18933 = state_17119__$1;
(statearr_17143_18933[(2)] = null);

(statearr_17143_18933[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17120 === (10))){
var inst_17111 = (state_17119[(2)]);
var state_17119__$1 = state_17119;
var statearr_17144_18946 = state_17119__$1;
(statearr_17144_18946[(2)] = inst_17111);

(statearr_17144_18946[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17120 === (8))){
var inst_17101 = (state_17119[(7)]);
var state_17119__$1 = state_17119;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17119__$1,(11),out,inst_17101);
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
var cljs$core$async$state_machine__14014__auto__ = null;
var cljs$core$async$state_machine__14014__auto____0 = (function (){
var statearr_17145 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_17145[(0)] = cljs$core$async$state_machine__14014__auto__);

(statearr_17145[(1)] = (1));

return statearr_17145;
});
var cljs$core$async$state_machine__14014__auto____1 = (function (state_17119){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_17119);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e17146){var ex__14017__auto__ = e17146;
var statearr_17147_18958 = state_17119;
(statearr_17147_18958[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_17119[(4)]))){
var statearr_17148_18965 = state_17119;
(statearr_17148_18965[(1)] = cljs.core.first((state_17119[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18974 = state_17119;
state_17119 = G__18974;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$state_machine__14014__auto__ = function(state_17119){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14014__auto____1.call(this,state_17119);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14014__auto____0;
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14014__auto____1;
return cljs$core$async$state_machine__14014__auto__;
})()
})();
var state__14375__auto__ = (function (){var statearr_17166 = f__14374__auto__();
(statearr_17166[(6)] = c__14373__auto___18872);

return statearr_17166;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
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
cljs.core.async.t_cljs$core$async17201 = (function (f,ch,meta17177,_,fn1,meta17202){
this.f = f;
this.ch = ch;
this.meta17177 = meta17177;
this._ = _;
this.fn1 = fn1;
this.meta17202 = meta17202;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async17201.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_17203,meta17202__$1){
var self__ = this;
var _17203__$1 = this;
return (new cljs.core.async.t_cljs$core$async17201(self__.f,self__.ch,self__.meta17177,self__._,self__.fn1,meta17202__$1));
}));

(cljs.core.async.t_cljs$core$async17201.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_17203){
var self__ = this;
var _17203__$1 = this;
return self__.meta17202;
}));

(cljs.core.async.t_cljs$core$async17201.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17201.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (___$1){
var self__ = this;
var ___$2 = this;
return cljs.core.async.impl.protocols.active_QMARK_(self__.fn1);
}));

(cljs.core.async.t_cljs$core$async17201.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (___$1){
var self__ = this;
var ___$2 = this;
return true;
}));

(cljs.core.async.t_cljs$core$async17201.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (___$1){
var self__ = this;
var ___$2 = this;
var f1 = cljs.core.async.impl.protocols.commit(self__.fn1);
return (function (p1__17174_SHARP_){
var G__17208 = (((p1__17174_SHARP_ == null))?null:(self__.f.cljs$core$IFn$_invoke$arity$1 ? self__.f.cljs$core$IFn$_invoke$arity$1(p1__17174_SHARP_) : self__.f.call(null,p1__17174_SHARP_)));
return (f1.cljs$core$IFn$_invoke$arity$1 ? f1.cljs$core$IFn$_invoke$arity$1(G__17208) : f1.call(null,G__17208));
});
}));

(cljs.core.async.t_cljs$core$async17201.getBasis = (function (){
return new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta17177","meta17177",459775275,null),cljs.core.with_meta(new cljs.core.Symbol(null,"_","_",-1201019570,null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Symbol("cljs.core.async","t_cljs$core$async17176","cljs.core.async/t_cljs$core$async17176",-1391932162,null)], null)),new cljs.core.Symbol(null,"fn1","fn1",895834444,null),new cljs.core.Symbol(null,"meta17202","meta17202",-892712162,null)], null);
}));

(cljs.core.async.t_cljs$core$async17201.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async17201.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async17201");

(cljs.core.async.t_cljs$core$async17201.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async17201");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async17201.
 */
cljs.core.async.__GT_t_cljs$core$async17201 = (function cljs$core$async$__GT_t_cljs$core$async17201(f,ch,meta17177,_,fn1,meta17202){
return (new cljs.core.async.t_cljs$core$async17201(f,ch,meta17177,_,fn1,meta17202));
});



/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async17176 = (function (f,ch,meta17177){
this.f = f;
this.ch = ch;
this.meta17177 = meta17177;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async17176.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_17178,meta17177__$1){
var self__ = this;
var _17178__$1 = this;
return (new cljs.core.async.t_cljs$core$async17176(self__.f,self__.ch,meta17177__$1));
}));

(cljs.core.async.t_cljs$core$async17176.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_17178){
var self__ = this;
var _17178__$1 = this;
return self__.meta17177;
}));

(cljs.core.async.t_cljs$core$async17176.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17176.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async17176.prototype.cljs$core$async$impl$protocols$Channel$closed_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.closed_QMARK_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async17176.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17176.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
var ret = cljs.core.async.impl.protocols.take_BANG_(self__.ch,(new cljs.core.async.t_cljs$core$async17201(self__.f,self__.ch,self__.meta17177,___$1,fn1,cljs.core.PersistentArrayMap.EMPTY)));
if(cljs.core.truth_((function (){var and__5140__auto__ = ret;
if(cljs.core.truth_(and__5140__auto__)){
return (!((cljs.core.deref(ret) == null)));
} else {
return and__5140__auto__;
}
})())){
return cljs.core.async.impl.channels.box((function (){var G__17214 = cljs.core.deref(ret);
return (self__.f.cljs$core$IFn$_invoke$arity$1 ? self__.f.cljs$core$IFn$_invoke$arity$1(G__17214) : self__.f.call(null,G__17214));
})());
} else {
return ret;
}
}));

(cljs.core.async.t_cljs$core$async17176.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17176.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.put_BANG_(self__.ch,val,fn1);
}));

(cljs.core.async.t_cljs$core$async17176.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta17177","meta17177",459775275,null)], null);
}));

(cljs.core.async.t_cljs$core$async17176.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async17176.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async17176");

(cljs.core.async.t_cljs$core$async17176.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async17176");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async17176.
 */
cljs.core.async.__GT_t_cljs$core$async17176 = (function cljs$core$async$__GT_t_cljs$core$async17176(f,ch,meta17177){
return (new cljs.core.async.t_cljs$core$async17176(f,ch,meta17177));
});


/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.map_LT_ = (function cljs$core$async$map_LT_(f,ch){
return (new cljs.core.async.t_cljs$core$async17176(f,ch,cljs.core.PersistentArrayMap.EMPTY));
});

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async17217 = (function (f,ch,meta17218){
this.f = f;
this.ch = ch;
this.meta17218 = meta17218;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async17217.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_17219,meta17218__$1){
var self__ = this;
var _17219__$1 = this;
return (new cljs.core.async.t_cljs$core$async17217(self__.f,self__.ch,meta17218__$1));
}));

(cljs.core.async.t_cljs$core$async17217.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_17219){
var self__ = this;
var _17219__$1 = this;
return self__.meta17218;
}));

(cljs.core.async.t_cljs$core$async17217.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17217.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async17217.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17217.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.take_BANG_(self__.ch,fn1);
}));

(cljs.core.async.t_cljs$core$async17217.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17217.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.put_BANG_(self__.ch,(self__.f.cljs$core$IFn$_invoke$arity$1 ? self__.f.cljs$core$IFn$_invoke$arity$1(val) : self__.f.call(null,val)),fn1);
}));

(cljs.core.async.t_cljs$core$async17217.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta17218","meta17218",-186529077,null)], null);
}));

(cljs.core.async.t_cljs$core$async17217.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async17217.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async17217");

(cljs.core.async.t_cljs$core$async17217.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async17217");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async17217.
 */
cljs.core.async.__GT_t_cljs$core$async17217 = (function cljs$core$async$__GT_t_cljs$core$async17217(f,ch,meta17218){
return (new cljs.core.async.t_cljs$core$async17217(f,ch,meta17218));
});


/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.map_GT_ = (function cljs$core$async$map_GT_(f,ch){
return (new cljs.core.async.t_cljs$core$async17217(f,ch,cljs.core.PersistentArrayMap.EMPTY));
});

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async17221 = (function (p,ch,meta17222){
this.p = p;
this.ch = ch;
this.meta17222 = meta17222;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async17221.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_17223,meta17222__$1){
var self__ = this;
var _17223__$1 = this;
return (new cljs.core.async.t_cljs$core$async17221(self__.p,self__.ch,meta17222__$1));
}));

(cljs.core.async.t_cljs$core$async17221.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_17223){
var self__ = this;
var _17223__$1 = this;
return self__.meta17222;
}));

(cljs.core.async.t_cljs$core$async17221.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17221.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async17221.prototype.cljs$core$async$impl$protocols$Channel$closed_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.closed_QMARK_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async17221.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17221.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.take_BANG_(self__.ch,fn1);
}));

(cljs.core.async.t_cljs$core$async17221.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17221.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
if(cljs.core.truth_((self__.p.cljs$core$IFn$_invoke$arity$1 ? self__.p.cljs$core$IFn$_invoke$arity$1(val) : self__.p.call(null,val)))){
return cljs.core.async.impl.protocols.put_BANG_(self__.ch,val,fn1);
} else {
return cljs.core.async.impl.channels.box(cljs.core.not(cljs.core.async.impl.protocols.closed_QMARK_(self__.ch)));
}
}));

(cljs.core.async.t_cljs$core$async17221.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"p","p",1791580836,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta17222","meta17222",-314944593,null)], null);
}));

(cljs.core.async.t_cljs$core$async17221.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async17221.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async17221");

(cljs.core.async.t_cljs$core$async17221.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async17221");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async17221.
 */
cljs.core.async.__GT_t_cljs$core$async17221 = (function cljs$core$async$__GT_t_cljs$core$async17221(p,ch,meta17222){
return (new cljs.core.async.t_cljs$core$async17221(p,ch,meta17222));
});


/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.filter_GT_ = (function cljs$core$async$filter_GT_(p,ch){
return (new cljs.core.async.t_cljs$core$async17221(p,ch,cljs.core.PersistentArrayMap.EMPTY));
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
var G__17239 = arguments.length;
switch (G__17239) {
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
var c__14373__auto___19222 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = (function (state_17263){
var state_val_17264 = (state_17263[(1)]);
if((state_val_17264 === (7))){
var inst_17259 = (state_17263[(2)]);
var state_17263__$1 = state_17263;
var statearr_17265_19227 = state_17263__$1;
(statearr_17265_19227[(2)] = inst_17259);

(statearr_17265_19227[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17264 === (1))){
var state_17263__$1 = state_17263;
var statearr_17266_19232 = state_17263__$1;
(statearr_17266_19232[(2)] = null);

(statearr_17266_19232[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17264 === (4))){
var inst_17245 = (state_17263[(7)]);
var inst_17245__$1 = (state_17263[(2)]);
var inst_17246 = (inst_17245__$1 == null);
var state_17263__$1 = (function (){var statearr_17267 = state_17263;
(statearr_17267[(7)] = inst_17245__$1);

return statearr_17267;
})();
if(cljs.core.truth_(inst_17246)){
var statearr_17268_19242 = state_17263__$1;
(statearr_17268_19242[(1)] = (5));

} else {
var statearr_17269_19243 = state_17263__$1;
(statearr_17269_19243[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17264 === (6))){
var inst_17245 = (state_17263[(7)]);
var inst_17250 = (p.cljs$core$IFn$_invoke$arity$1 ? p.cljs$core$IFn$_invoke$arity$1(inst_17245) : p.call(null,inst_17245));
var state_17263__$1 = state_17263;
if(cljs.core.truth_(inst_17250)){
var statearr_17270_19244 = state_17263__$1;
(statearr_17270_19244[(1)] = (8));

} else {
var statearr_17271_19245 = state_17263__$1;
(statearr_17271_19245[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17264 === (3))){
var inst_17261 = (state_17263[(2)]);
var state_17263__$1 = state_17263;
return cljs.core.async.impl.ioc_helpers.return_chan(state_17263__$1,inst_17261);
} else {
if((state_val_17264 === (2))){
var state_17263__$1 = state_17263;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_17263__$1,(4),ch);
} else {
if((state_val_17264 === (11))){
var inst_17253 = (state_17263[(2)]);
var state_17263__$1 = state_17263;
var statearr_17276_19251 = state_17263__$1;
(statearr_17276_19251[(2)] = inst_17253);

(statearr_17276_19251[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17264 === (9))){
var state_17263__$1 = state_17263;
var statearr_17277_19255 = state_17263__$1;
(statearr_17277_19255[(2)] = null);

(statearr_17277_19255[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17264 === (5))){
var inst_17248 = cljs.core.async.close_BANG_(out);
var state_17263__$1 = state_17263;
var statearr_17282_19259 = state_17263__$1;
(statearr_17282_19259[(2)] = inst_17248);

(statearr_17282_19259[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17264 === (10))){
var inst_17256 = (state_17263[(2)]);
var state_17263__$1 = (function (){var statearr_17283 = state_17263;
(statearr_17283[(8)] = inst_17256);

return statearr_17283;
})();
var statearr_17284_19260 = state_17263__$1;
(statearr_17284_19260[(2)] = null);

(statearr_17284_19260[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17264 === (8))){
var inst_17245 = (state_17263[(7)]);
var state_17263__$1 = state_17263;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17263__$1,(11),out,inst_17245);
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
var cljs$core$async$state_machine__14014__auto__ = null;
var cljs$core$async$state_machine__14014__auto____0 = (function (){
var statearr_17289 = [null,null,null,null,null,null,null,null,null];
(statearr_17289[(0)] = cljs$core$async$state_machine__14014__auto__);

(statearr_17289[(1)] = (1));

return statearr_17289;
});
var cljs$core$async$state_machine__14014__auto____1 = (function (state_17263){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_17263);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e17290){var ex__14017__auto__ = e17290;
var statearr_17291_19265 = state_17263;
(statearr_17291_19265[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_17263[(4)]))){
var statearr_17295_19270 = state_17263;
(statearr_17295_19270[(1)] = cljs.core.first((state_17263[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__19272 = state_17263;
state_17263 = G__19272;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$state_machine__14014__auto__ = function(state_17263){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14014__auto____1.call(this,state_17263);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14014__auto____0;
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14014__auto____1;
return cljs$core$async$state_machine__14014__auto__;
})()
})();
var state__14375__auto__ = (function (){var statearr_17296 = f__14374__auto__();
(statearr_17296[(6)] = c__14373__auto___19222);

return statearr_17296;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
}));


return out;
}));

(cljs.core.async.filter_LT_.cljs$lang$maxFixedArity = 3);

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.remove_LT_ = (function cljs$core$async$remove_LT_(var_args){
var G__17300 = arguments.length;
switch (G__17300) {
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
var c__14373__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = (function (state_17378){
var state_val_17379 = (state_17378[(1)]);
if((state_val_17379 === (7))){
var inst_17374 = (state_17378[(2)]);
var state_17378__$1 = state_17378;
var statearr_17380_19278 = state_17378__$1;
(statearr_17380_19278[(2)] = inst_17374);

(statearr_17380_19278[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17379 === (20))){
var inst_17342 = (state_17378[(7)]);
var inst_17355 = (state_17378[(2)]);
var inst_17356 = cljs.core.next(inst_17342);
var inst_17321 = inst_17356;
var inst_17322 = null;
var inst_17323 = (0);
var inst_17324 = (0);
var state_17378__$1 = (function (){var statearr_17386 = state_17378;
(statearr_17386[(8)] = inst_17355);

(statearr_17386[(9)] = inst_17321);

(statearr_17386[(10)] = inst_17322);

(statearr_17386[(11)] = inst_17323);

(statearr_17386[(12)] = inst_17324);

return statearr_17386;
})();
var statearr_17388_19285 = state_17378__$1;
(statearr_17388_19285[(2)] = null);

(statearr_17388_19285[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17379 === (1))){
var state_17378__$1 = state_17378;
var statearr_17389_19286 = state_17378__$1;
(statearr_17389_19286[(2)] = null);

(statearr_17389_19286[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17379 === (4))){
var inst_17307 = (state_17378[(13)]);
var inst_17307__$1 = (state_17378[(2)]);
var inst_17309 = (inst_17307__$1 == null);
var state_17378__$1 = (function (){var statearr_17390 = state_17378;
(statearr_17390[(13)] = inst_17307__$1);

return statearr_17390;
})();
if(cljs.core.truth_(inst_17309)){
var statearr_17391_19289 = state_17378__$1;
(statearr_17391_19289[(1)] = (5));

} else {
var statearr_17392_19290 = state_17378__$1;
(statearr_17392_19290[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17379 === (15))){
var state_17378__$1 = state_17378;
var statearr_17402_19291 = state_17378__$1;
(statearr_17402_19291[(2)] = null);

(statearr_17402_19291[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17379 === (21))){
var state_17378__$1 = state_17378;
var statearr_17405_19297 = state_17378__$1;
(statearr_17405_19297[(2)] = null);

(statearr_17405_19297[(1)] = (23));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17379 === (13))){
var inst_17324 = (state_17378[(12)]);
var inst_17321 = (state_17378[(9)]);
var inst_17322 = (state_17378[(10)]);
var inst_17323 = (state_17378[(11)]);
var inst_17336 = (state_17378[(2)]);
var inst_17337 = (inst_17324 + (1));
var tmp17396 = inst_17323;
var tmp17397 = inst_17321;
var tmp17398 = inst_17322;
var inst_17321__$1 = tmp17397;
var inst_17322__$1 = tmp17398;
var inst_17323__$1 = tmp17396;
var inst_17324__$1 = inst_17337;
var state_17378__$1 = (function (){var statearr_17412 = state_17378;
(statearr_17412[(14)] = inst_17336);

(statearr_17412[(9)] = inst_17321__$1);

(statearr_17412[(10)] = inst_17322__$1);

(statearr_17412[(11)] = inst_17323__$1);

(statearr_17412[(12)] = inst_17324__$1);

return statearr_17412;
})();
var statearr_17414_19299 = state_17378__$1;
(statearr_17414_19299[(2)] = null);

(statearr_17414_19299[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17379 === (22))){
var state_17378__$1 = state_17378;
var statearr_17419_19300 = state_17378__$1;
(statearr_17419_19300[(2)] = null);

(statearr_17419_19300[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17379 === (6))){
var inst_17307 = (state_17378[(13)]);
var inst_17319 = (f.cljs$core$IFn$_invoke$arity$1 ? f.cljs$core$IFn$_invoke$arity$1(inst_17307) : f.call(null,inst_17307));
var inst_17320 = cljs.core.seq(inst_17319);
var inst_17321 = inst_17320;
var inst_17322 = null;
var inst_17323 = (0);
var inst_17324 = (0);
var state_17378__$1 = (function (){var statearr_17433 = state_17378;
(statearr_17433[(9)] = inst_17321);

(statearr_17433[(10)] = inst_17322);

(statearr_17433[(11)] = inst_17323);

(statearr_17433[(12)] = inst_17324);

return statearr_17433;
})();
var statearr_17434_19306 = state_17378__$1;
(statearr_17434_19306[(2)] = null);

(statearr_17434_19306[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17379 === (17))){
var inst_17342 = (state_17378[(7)]);
var inst_17348 = cljs.core.chunk_first(inst_17342);
var inst_17349 = cljs.core.chunk_rest(inst_17342);
var inst_17350 = cljs.core.count(inst_17348);
var inst_17321 = inst_17349;
var inst_17322 = inst_17348;
var inst_17323 = inst_17350;
var inst_17324 = (0);
var state_17378__$1 = (function (){var statearr_17437 = state_17378;
(statearr_17437[(9)] = inst_17321);

(statearr_17437[(10)] = inst_17322);

(statearr_17437[(11)] = inst_17323);

(statearr_17437[(12)] = inst_17324);

return statearr_17437;
})();
var statearr_17440_19310 = state_17378__$1;
(statearr_17440_19310[(2)] = null);

(statearr_17440_19310[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17379 === (3))){
var inst_17376 = (state_17378[(2)]);
var state_17378__$1 = state_17378;
return cljs.core.async.impl.ioc_helpers.return_chan(state_17378__$1,inst_17376);
} else {
if((state_val_17379 === (12))){
var inst_17364 = (state_17378[(2)]);
var state_17378__$1 = state_17378;
var statearr_17444_19316 = state_17378__$1;
(statearr_17444_19316[(2)] = inst_17364);

(statearr_17444_19316[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17379 === (2))){
var state_17378__$1 = state_17378;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_17378__$1,(4),in$);
} else {
if((state_val_17379 === (23))){
var inst_17372 = (state_17378[(2)]);
var state_17378__$1 = state_17378;
var statearr_17450_19320 = state_17378__$1;
(statearr_17450_19320[(2)] = inst_17372);

(statearr_17450_19320[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17379 === (19))){
var inst_17359 = (state_17378[(2)]);
var state_17378__$1 = state_17378;
var statearr_17451_19321 = state_17378__$1;
(statearr_17451_19321[(2)] = inst_17359);

(statearr_17451_19321[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17379 === (11))){
var inst_17321 = (state_17378[(9)]);
var inst_17342 = (state_17378[(7)]);
var inst_17342__$1 = cljs.core.seq(inst_17321);
var state_17378__$1 = (function (){var statearr_17454 = state_17378;
(statearr_17454[(7)] = inst_17342__$1);

return statearr_17454;
})();
if(inst_17342__$1){
var statearr_17458_19323 = state_17378__$1;
(statearr_17458_19323[(1)] = (14));

} else {
var statearr_17460_19328 = state_17378__$1;
(statearr_17460_19328[(1)] = (15));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17379 === (9))){
var inst_17366 = (state_17378[(2)]);
var inst_17367 = cljs.core.async.impl.protocols.closed_QMARK_(out);
var state_17378__$1 = (function (){var statearr_17482 = state_17378;
(statearr_17482[(15)] = inst_17366);

return statearr_17482;
})();
if(cljs.core.truth_(inst_17367)){
var statearr_17489_19341 = state_17378__$1;
(statearr_17489_19341[(1)] = (21));

} else {
var statearr_17490_19342 = state_17378__$1;
(statearr_17490_19342[(1)] = (22));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17379 === (5))){
var inst_17312 = cljs.core.async.close_BANG_(out);
var state_17378__$1 = state_17378;
var statearr_17494_19343 = state_17378__$1;
(statearr_17494_19343[(2)] = inst_17312);

(statearr_17494_19343[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17379 === (14))){
var inst_17342 = (state_17378[(7)]);
var inst_17344 = cljs.core.chunked_seq_QMARK_(inst_17342);
var state_17378__$1 = state_17378;
if(inst_17344){
var statearr_17513_19344 = state_17378__$1;
(statearr_17513_19344[(1)] = (17));

} else {
var statearr_17514_19345 = state_17378__$1;
(statearr_17514_19345[(1)] = (18));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17379 === (16))){
var inst_17362 = (state_17378[(2)]);
var state_17378__$1 = state_17378;
var statearr_17519_19349 = state_17378__$1;
(statearr_17519_19349[(2)] = inst_17362);

(statearr_17519_19349[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17379 === (10))){
var inst_17322 = (state_17378[(10)]);
var inst_17324 = (state_17378[(12)]);
var inst_17334 = cljs.core._nth(inst_17322,inst_17324);
var state_17378__$1 = state_17378;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17378__$1,(13),out,inst_17334);
} else {
if((state_val_17379 === (18))){
var inst_17342 = (state_17378[(7)]);
var inst_17353 = cljs.core.first(inst_17342);
var state_17378__$1 = state_17378;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17378__$1,(20),out,inst_17353);
} else {
if((state_val_17379 === (8))){
var inst_17324 = (state_17378[(12)]);
var inst_17323 = (state_17378[(11)]);
var inst_17326 = (inst_17324 < inst_17323);
var inst_17327 = inst_17326;
var state_17378__$1 = state_17378;
if(cljs.core.truth_(inst_17327)){
var statearr_17520_19352 = state_17378__$1;
(statearr_17520_19352[(1)] = (10));

} else {
var statearr_17521_19353 = state_17378__$1;
(statearr_17521_19353[(1)] = (11));

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
var cljs$core$async$mapcat_STAR__$_state_machine__14014__auto__ = null;
var cljs$core$async$mapcat_STAR__$_state_machine__14014__auto____0 = (function (){
var statearr_17525 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_17525[(0)] = cljs$core$async$mapcat_STAR__$_state_machine__14014__auto__);

(statearr_17525[(1)] = (1));

return statearr_17525;
});
var cljs$core$async$mapcat_STAR__$_state_machine__14014__auto____1 = (function (state_17378){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_17378);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e17526){var ex__14017__auto__ = e17526;
var statearr_17527_19362 = state_17378;
(statearr_17527_19362[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_17378[(4)]))){
var statearr_17528_19367 = state_17378;
(statearr_17528_19367[(1)] = cljs.core.first((state_17378[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__19368 = state_17378;
state_17378 = G__19368;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$mapcat_STAR__$_state_machine__14014__auto__ = function(state_17378){
switch(arguments.length){
case 0:
return cljs$core$async$mapcat_STAR__$_state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$mapcat_STAR__$_state_machine__14014__auto____1.call(this,state_17378);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$mapcat_STAR__$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mapcat_STAR__$_state_machine__14014__auto____0;
cljs$core$async$mapcat_STAR__$_state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mapcat_STAR__$_state_machine__14014__auto____1;
return cljs$core$async$mapcat_STAR__$_state_machine__14014__auto__;
})()
})();
var state__14375__auto__ = (function (){var statearr_17529 = f__14374__auto__();
(statearr_17529[(6)] = c__14373__auto__);

return statearr_17529;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
}));

return c__14373__auto__;
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.mapcat_LT_ = (function cljs$core$async$mapcat_LT_(var_args){
var G__17535 = arguments.length;
switch (G__17535) {
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
var G__17546 = arguments.length;
switch (G__17546) {
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
var G__17566 = arguments.length;
switch (G__17566) {
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
var c__14373__auto___19389 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = (function (state_17592){
var state_val_17593 = (state_17592[(1)]);
if((state_val_17593 === (7))){
var inst_17587 = (state_17592[(2)]);
var state_17592__$1 = state_17592;
var statearr_17594_19395 = state_17592__$1;
(statearr_17594_19395[(2)] = inst_17587);

(statearr_17594_19395[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17593 === (1))){
var inst_17568 = null;
var state_17592__$1 = (function (){var statearr_17595 = state_17592;
(statearr_17595[(7)] = inst_17568);

return statearr_17595;
})();
var statearr_17596_19410 = state_17592__$1;
(statearr_17596_19410[(2)] = null);

(statearr_17596_19410[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17593 === (4))){
var inst_17571 = (state_17592[(8)]);
var inst_17571__$1 = (state_17592[(2)]);
var inst_17573 = (inst_17571__$1 == null);
var inst_17574 = cljs.core.not(inst_17573);
var state_17592__$1 = (function (){var statearr_17609 = state_17592;
(statearr_17609[(8)] = inst_17571__$1);

return statearr_17609;
})();
if(inst_17574){
var statearr_17610_19422 = state_17592__$1;
(statearr_17610_19422[(1)] = (5));

} else {
var statearr_17611_19426 = state_17592__$1;
(statearr_17611_19426[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17593 === (6))){
var state_17592__$1 = state_17592;
var statearr_17612_19431 = state_17592__$1;
(statearr_17612_19431[(2)] = null);

(statearr_17612_19431[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17593 === (3))){
var inst_17589 = (state_17592[(2)]);
var inst_17590 = cljs.core.async.close_BANG_(out);
var state_17592__$1 = (function (){var statearr_17613 = state_17592;
(statearr_17613[(9)] = inst_17589);

return statearr_17613;
})();
return cljs.core.async.impl.ioc_helpers.return_chan(state_17592__$1,inst_17590);
} else {
if((state_val_17593 === (2))){
var state_17592__$1 = state_17592;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_17592__$1,(4),ch);
} else {
if((state_val_17593 === (11))){
var inst_17571 = (state_17592[(8)]);
var inst_17581 = (state_17592[(2)]);
var inst_17568 = inst_17571;
var state_17592__$1 = (function (){var statearr_17614 = state_17592;
(statearr_17614[(10)] = inst_17581);

(statearr_17614[(7)] = inst_17568);

return statearr_17614;
})();
var statearr_17615_19444 = state_17592__$1;
(statearr_17615_19444[(2)] = null);

(statearr_17615_19444[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17593 === (9))){
var inst_17571 = (state_17592[(8)]);
var state_17592__$1 = state_17592;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17592__$1,(11),out,inst_17571);
} else {
if((state_val_17593 === (5))){
var inst_17571 = (state_17592[(8)]);
var inst_17568 = (state_17592[(7)]);
var inst_17576 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(inst_17571,inst_17568);
var state_17592__$1 = state_17592;
if(inst_17576){
var statearr_17623_19447 = state_17592__$1;
(statearr_17623_19447[(1)] = (8));

} else {
var statearr_17624_19448 = state_17592__$1;
(statearr_17624_19448[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17593 === (10))){
var inst_17584 = (state_17592[(2)]);
var state_17592__$1 = state_17592;
var statearr_17625_19450 = state_17592__$1;
(statearr_17625_19450[(2)] = inst_17584);

(statearr_17625_19450[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17593 === (8))){
var inst_17568 = (state_17592[(7)]);
var tmp17616 = inst_17568;
var inst_17568__$1 = tmp17616;
var state_17592__$1 = (function (){var statearr_17626 = state_17592;
(statearr_17626[(7)] = inst_17568__$1);

return statearr_17626;
})();
var statearr_17627_19452 = state_17592__$1;
(statearr_17627_19452[(2)] = null);

(statearr_17627_19452[(1)] = (2));


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
var cljs$core$async$state_machine__14014__auto__ = null;
var cljs$core$async$state_machine__14014__auto____0 = (function (){
var statearr_17628 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_17628[(0)] = cljs$core$async$state_machine__14014__auto__);

(statearr_17628[(1)] = (1));

return statearr_17628;
});
var cljs$core$async$state_machine__14014__auto____1 = (function (state_17592){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_17592);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e17629){var ex__14017__auto__ = e17629;
var statearr_17630_19471 = state_17592;
(statearr_17630_19471[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_17592[(4)]))){
var statearr_17631_19475 = state_17592;
(statearr_17631_19475[(1)] = cljs.core.first((state_17592[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__19477 = state_17592;
state_17592 = G__19477;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$state_machine__14014__auto__ = function(state_17592){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14014__auto____1.call(this,state_17592);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14014__auto____0;
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14014__auto____1;
return cljs$core$async$state_machine__14014__auto__;
})()
})();
var state__14375__auto__ = (function (){var statearr_17636 = f__14374__auto__();
(statearr_17636[(6)] = c__14373__auto___19389);

return statearr_17636;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
}));


return out;
}));

(cljs.core.async.unique.cljs$lang$maxFixedArity = 2);

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.partition = (function cljs$core$async$partition(var_args){
var G__17642 = arguments.length;
switch (G__17642) {
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
var c__14373__auto___19485 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = (function (state_17695){
var state_val_17696 = (state_17695[(1)]);
if((state_val_17696 === (7))){
var inst_17691 = (state_17695[(2)]);
var state_17695__$1 = state_17695;
var statearr_17699_19488 = state_17695__$1;
(statearr_17699_19488[(2)] = inst_17691);

(statearr_17699_19488[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17696 === (1))){
var inst_17658 = (new Array(n));
var inst_17659 = inst_17658;
var inst_17660 = (0);
var state_17695__$1 = (function (){var statearr_17700 = state_17695;
(statearr_17700[(7)] = inst_17659);

(statearr_17700[(8)] = inst_17660);

return statearr_17700;
})();
var statearr_17701_19491 = state_17695__$1;
(statearr_17701_19491[(2)] = null);

(statearr_17701_19491[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17696 === (4))){
var inst_17663 = (state_17695[(9)]);
var inst_17663__$1 = (state_17695[(2)]);
var inst_17664 = (inst_17663__$1 == null);
var inst_17665 = cljs.core.not(inst_17664);
var state_17695__$1 = (function (){var statearr_17702 = state_17695;
(statearr_17702[(9)] = inst_17663__$1);

return statearr_17702;
})();
if(inst_17665){
var statearr_17704_19502 = state_17695__$1;
(statearr_17704_19502[(1)] = (5));

} else {
var statearr_17705_19504 = state_17695__$1;
(statearr_17705_19504[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17696 === (15))){
var inst_17685 = (state_17695[(2)]);
var state_17695__$1 = state_17695;
var statearr_17706_19505 = state_17695__$1;
(statearr_17706_19505[(2)] = inst_17685);

(statearr_17706_19505[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17696 === (13))){
var state_17695__$1 = state_17695;
var statearr_17708_19511 = state_17695__$1;
(statearr_17708_19511[(2)] = null);

(statearr_17708_19511[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17696 === (6))){
var inst_17660 = (state_17695[(8)]);
var inst_17681 = (inst_17660 > (0));
var state_17695__$1 = state_17695;
if(cljs.core.truth_(inst_17681)){
var statearr_17709_19520 = state_17695__$1;
(statearr_17709_19520[(1)] = (12));

} else {
var statearr_17710_19521 = state_17695__$1;
(statearr_17710_19521[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17696 === (3))){
var inst_17693 = (state_17695[(2)]);
var state_17695__$1 = state_17695;
return cljs.core.async.impl.ioc_helpers.return_chan(state_17695__$1,inst_17693);
} else {
if((state_val_17696 === (12))){
var inst_17659 = (state_17695[(7)]);
var inst_17683 = cljs.core.vec(inst_17659);
var state_17695__$1 = state_17695;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17695__$1,(15),out,inst_17683);
} else {
if((state_val_17696 === (2))){
var state_17695__$1 = state_17695;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_17695__$1,(4),ch);
} else {
if((state_val_17696 === (11))){
var inst_17675 = (state_17695[(2)]);
var inst_17676 = (new Array(n));
var inst_17659 = inst_17676;
var inst_17660 = (0);
var state_17695__$1 = (function (){var statearr_17711 = state_17695;
(statearr_17711[(10)] = inst_17675);

(statearr_17711[(7)] = inst_17659);

(statearr_17711[(8)] = inst_17660);

return statearr_17711;
})();
var statearr_17712_19548 = state_17695__$1;
(statearr_17712_19548[(2)] = null);

(statearr_17712_19548[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17696 === (9))){
var inst_17659 = (state_17695[(7)]);
var inst_17673 = cljs.core.vec(inst_17659);
var state_17695__$1 = state_17695;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17695__$1,(11),out,inst_17673);
} else {
if((state_val_17696 === (5))){
var inst_17659 = (state_17695[(7)]);
var inst_17660 = (state_17695[(8)]);
var inst_17663 = (state_17695[(9)]);
var inst_17668 = (state_17695[(11)]);
var inst_17667 = (inst_17659[inst_17660] = inst_17663);
var inst_17668__$1 = (inst_17660 + (1));
var inst_17669 = (inst_17668__$1 < n);
var state_17695__$1 = (function (){var statearr_17722 = state_17695;
(statearr_17722[(12)] = inst_17667);

(statearr_17722[(11)] = inst_17668__$1);

return statearr_17722;
})();
if(cljs.core.truth_(inst_17669)){
var statearr_17723_19564 = state_17695__$1;
(statearr_17723_19564[(1)] = (8));

} else {
var statearr_17724_19566 = state_17695__$1;
(statearr_17724_19566[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17696 === (14))){
var inst_17688 = (state_17695[(2)]);
var inst_17689 = cljs.core.async.close_BANG_(out);
var state_17695__$1 = (function (){var statearr_17727 = state_17695;
(statearr_17727[(13)] = inst_17688);

return statearr_17727;
})();
var statearr_17728_19578 = state_17695__$1;
(statearr_17728_19578[(2)] = inst_17689);

(statearr_17728_19578[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17696 === (10))){
var inst_17679 = (state_17695[(2)]);
var state_17695__$1 = state_17695;
var statearr_17736_19587 = state_17695__$1;
(statearr_17736_19587[(2)] = inst_17679);

(statearr_17736_19587[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17696 === (8))){
var inst_17659 = (state_17695[(7)]);
var inst_17668 = (state_17695[(11)]);
var tmp17725 = inst_17659;
var inst_17659__$1 = tmp17725;
var inst_17660 = inst_17668;
var state_17695__$1 = (function (){var statearr_17737 = state_17695;
(statearr_17737[(7)] = inst_17659__$1);

(statearr_17737[(8)] = inst_17660);

return statearr_17737;
})();
var statearr_17740_19593 = state_17695__$1;
(statearr_17740_19593[(2)] = null);

(statearr_17740_19593[(1)] = (2));


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
var cljs$core$async$state_machine__14014__auto__ = null;
var cljs$core$async$state_machine__14014__auto____0 = (function (){
var statearr_17742 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_17742[(0)] = cljs$core$async$state_machine__14014__auto__);

(statearr_17742[(1)] = (1));

return statearr_17742;
});
var cljs$core$async$state_machine__14014__auto____1 = (function (state_17695){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_17695);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e17744){var ex__14017__auto__ = e17744;
var statearr_17745_19602 = state_17695;
(statearr_17745_19602[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_17695[(4)]))){
var statearr_17746_19603 = state_17695;
(statearr_17746_19603[(1)] = cljs.core.first((state_17695[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__19613 = state_17695;
state_17695 = G__19613;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$state_machine__14014__auto__ = function(state_17695){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14014__auto____1.call(this,state_17695);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14014__auto____0;
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14014__auto____1;
return cljs$core$async$state_machine__14014__auto__;
})()
})();
var state__14375__auto__ = (function (){var statearr_17747 = f__14374__auto__();
(statearr_17747[(6)] = c__14373__auto___19485);

return statearr_17747;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
}));


return out;
}));

(cljs.core.async.partition.cljs$lang$maxFixedArity = 3);

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.partition_by = (function cljs$core$async$partition_by(var_args){
var G__17749 = arguments.length;
switch (G__17749) {
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
var c__14373__auto___19627 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14374__auto__ = (function (){var switch__14013__auto__ = (function (state_17810){
var state_val_17811 = (state_17810[(1)]);
if((state_val_17811 === (7))){
var inst_17806 = (state_17810[(2)]);
var state_17810__$1 = state_17810;
var statearr_17815_19631 = state_17810__$1;
(statearr_17815_19631[(2)] = inst_17806);

(statearr_17815_19631[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17811 === (1))){
var inst_17764 = [];
var inst_17765 = inst_17764;
var inst_17766 = new cljs.core.Keyword("cljs.core.async","nothing","cljs.core.async/nothing",-69252123);
var state_17810__$1 = (function (){var statearr_17816 = state_17810;
(statearr_17816[(7)] = inst_17765);

(statearr_17816[(8)] = inst_17766);

return statearr_17816;
})();
var statearr_17817_19640 = state_17810__$1;
(statearr_17817_19640[(2)] = null);

(statearr_17817_19640[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17811 === (4))){
var inst_17770 = (state_17810[(9)]);
var inst_17770__$1 = (state_17810[(2)]);
var inst_17772 = (inst_17770__$1 == null);
var inst_17773 = cljs.core.not(inst_17772);
var state_17810__$1 = (function (){var statearr_17821 = state_17810;
(statearr_17821[(9)] = inst_17770__$1);

return statearr_17821;
})();
if(inst_17773){
var statearr_17822_19650 = state_17810__$1;
(statearr_17822_19650[(1)] = (5));

} else {
var statearr_17823_19651 = state_17810__$1;
(statearr_17823_19651[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17811 === (15))){
var inst_17765 = (state_17810[(7)]);
var inst_17798 = cljs.core.vec(inst_17765);
var state_17810__$1 = state_17810;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17810__$1,(18),out,inst_17798);
} else {
if((state_val_17811 === (13))){
var inst_17793 = (state_17810[(2)]);
var state_17810__$1 = state_17810;
var statearr_17824_19668 = state_17810__$1;
(statearr_17824_19668[(2)] = inst_17793);

(statearr_17824_19668[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17811 === (6))){
var inst_17765 = (state_17810[(7)]);
var inst_17795 = inst_17765.length;
var inst_17796 = (inst_17795 > (0));
var state_17810__$1 = state_17810;
if(cljs.core.truth_(inst_17796)){
var statearr_17828_19675 = state_17810__$1;
(statearr_17828_19675[(1)] = (15));

} else {
var statearr_17829_19676 = state_17810__$1;
(statearr_17829_19676[(1)] = (16));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17811 === (17))){
var inst_17803 = (state_17810[(2)]);
var inst_17804 = cljs.core.async.close_BANG_(out);
var state_17810__$1 = (function (){var statearr_17830 = state_17810;
(statearr_17830[(10)] = inst_17803);

return statearr_17830;
})();
var statearr_17831_19682 = state_17810__$1;
(statearr_17831_19682[(2)] = inst_17804);

(statearr_17831_19682[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17811 === (3))){
var inst_17808 = (state_17810[(2)]);
var state_17810__$1 = state_17810;
return cljs.core.async.impl.ioc_helpers.return_chan(state_17810__$1,inst_17808);
} else {
if((state_val_17811 === (12))){
var inst_17765 = (state_17810[(7)]);
var inst_17786 = cljs.core.vec(inst_17765);
var state_17810__$1 = state_17810;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17810__$1,(14),out,inst_17786);
} else {
if((state_val_17811 === (2))){
var state_17810__$1 = state_17810;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_17810__$1,(4),ch);
} else {
if((state_val_17811 === (11))){
var inst_17765 = (state_17810[(7)]);
var inst_17770 = (state_17810[(9)]);
var inst_17775 = (state_17810[(11)]);
var inst_17783 = inst_17765.push(inst_17770);
var tmp17847 = inst_17765;
var inst_17765__$1 = tmp17847;
var inst_17766 = inst_17775;
var state_17810__$1 = (function (){var statearr_17859 = state_17810;
(statearr_17859[(12)] = inst_17783);

(statearr_17859[(7)] = inst_17765__$1);

(statearr_17859[(8)] = inst_17766);

return statearr_17859;
})();
var statearr_17860_19699 = state_17810__$1;
(statearr_17860_19699[(2)] = null);

(statearr_17860_19699[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17811 === (9))){
var inst_17766 = (state_17810[(8)]);
var inst_17779 = cljs.core.keyword_identical_QMARK_(inst_17766,new cljs.core.Keyword("cljs.core.async","nothing","cljs.core.async/nothing",-69252123));
var state_17810__$1 = state_17810;
var statearr_17870_19701 = state_17810__$1;
(statearr_17870_19701[(2)] = inst_17779);

(statearr_17870_19701[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17811 === (5))){
var inst_17770 = (state_17810[(9)]);
var inst_17775 = (state_17810[(11)]);
var inst_17766 = (state_17810[(8)]);
var inst_17776 = (state_17810[(13)]);
var inst_17775__$1 = (f.cljs$core$IFn$_invoke$arity$1 ? f.cljs$core$IFn$_invoke$arity$1(inst_17770) : f.call(null,inst_17770));
var inst_17776__$1 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(inst_17775__$1,inst_17766);
var state_17810__$1 = (function (){var statearr_17871 = state_17810;
(statearr_17871[(11)] = inst_17775__$1);

(statearr_17871[(13)] = inst_17776__$1);

return statearr_17871;
})();
if(inst_17776__$1){
var statearr_17872_19707 = state_17810__$1;
(statearr_17872_19707[(1)] = (8));

} else {
var statearr_17873_19709 = state_17810__$1;
(statearr_17873_19709[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17811 === (14))){
var inst_17770 = (state_17810[(9)]);
var inst_17775 = (state_17810[(11)]);
var inst_17788 = (state_17810[(2)]);
var inst_17789 = [];
var inst_17790 = inst_17789.push(inst_17770);
var inst_17765 = inst_17789;
var inst_17766 = inst_17775;
var state_17810__$1 = (function (){var statearr_17876 = state_17810;
(statearr_17876[(14)] = inst_17788);

(statearr_17876[(15)] = inst_17790);

(statearr_17876[(7)] = inst_17765);

(statearr_17876[(8)] = inst_17766);

return statearr_17876;
})();
var statearr_17879_19714 = state_17810__$1;
(statearr_17879_19714[(2)] = null);

(statearr_17879_19714[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17811 === (16))){
var state_17810__$1 = state_17810;
var statearr_17880_19715 = state_17810__$1;
(statearr_17880_19715[(2)] = null);

(statearr_17880_19715[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17811 === (10))){
var inst_17781 = (state_17810[(2)]);
var state_17810__$1 = state_17810;
if(cljs.core.truth_(inst_17781)){
var statearr_17888_19716 = state_17810__$1;
(statearr_17888_19716[(1)] = (11));

} else {
var statearr_17889_19717 = state_17810__$1;
(statearr_17889_19717[(1)] = (12));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17811 === (18))){
var inst_17800 = (state_17810[(2)]);
var state_17810__$1 = state_17810;
var statearr_17890_19722 = state_17810__$1;
(statearr_17890_19722[(2)] = inst_17800);

(statearr_17890_19722[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17811 === (8))){
var inst_17776 = (state_17810[(13)]);
var state_17810__$1 = state_17810;
var statearr_17891_19723 = state_17810__$1;
(statearr_17891_19723[(2)] = inst_17776);

(statearr_17891_19723[(1)] = (10));


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
var cljs$core$async$state_machine__14014__auto__ = null;
var cljs$core$async$state_machine__14014__auto____0 = (function (){
var statearr_17893 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_17893[(0)] = cljs$core$async$state_machine__14014__auto__);

(statearr_17893[(1)] = (1));

return statearr_17893;
});
var cljs$core$async$state_machine__14014__auto____1 = (function (state_17810){
while(true){
var ret_value__14015__auto__ = (function (){try{while(true){
var result__14016__auto__ = switch__14013__auto__(state_17810);
if(cljs.core.keyword_identical_QMARK_(result__14016__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14016__auto__;
}
break;
}
}catch (e17894){var ex__14017__auto__ = e17894;
var statearr_17895_19724 = state_17810;
(statearr_17895_19724[(2)] = ex__14017__auto__);


if(cljs.core.seq((state_17810[(4)]))){
var statearr_17896_19725 = state_17810;
(statearr_17896_19725[(1)] = cljs.core.first((state_17810[(4)])));

} else {
throw ex__14017__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14015__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__19726 = state_17810;
state_17810 = G__19726;
continue;
} else {
return ret_value__14015__auto__;
}
break;
}
});
cljs$core$async$state_machine__14014__auto__ = function(state_17810){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14014__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14014__auto____1.call(this,state_17810);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14014__auto____0;
cljs$core$async$state_machine__14014__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14014__auto____1;
return cljs$core$async$state_machine__14014__auto__;
})()
})();
var state__14375__auto__ = (function (){var statearr_17903 = f__14374__auto__();
(statearr_17903[(6)] = c__14373__auto___19627);

return statearr_17903;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14375__auto__);
}));


return out;
}));

(cljs.core.async.partition_by.cljs$lang$maxFixedArity = 3);


//# sourceMappingURL=cljs.core.async.js.map
