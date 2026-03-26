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
cljs.core.async.t_cljs$core$async14533 = (function (f,blockable,meta14534){
this.f = f;
this.blockable = blockable;
this.meta14534 = meta14534;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async14533.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_14535,meta14534__$1){
var self__ = this;
var _14535__$1 = this;
return (new cljs.core.async.t_cljs$core$async14533(self__.f,self__.blockable,meta14534__$1));
}));

(cljs.core.async.t_cljs$core$async14533.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_14535){
var self__ = this;
var _14535__$1 = this;
return self__.meta14534;
}));

(cljs.core.async.t_cljs$core$async14533.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async14533.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return true;
}));

(cljs.core.async.t_cljs$core$async14533.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.blockable;
}));

(cljs.core.async.t_cljs$core$async14533.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.f;
}));

(cljs.core.async.t_cljs$core$async14533.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"blockable","blockable",-28395259,null),new cljs.core.Symbol(null,"meta14534","meta14534",-340587295,null)], null);
}));

(cljs.core.async.t_cljs$core$async14533.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async14533.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async14533");

(cljs.core.async.t_cljs$core$async14533.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async14533");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async14533.
 */
cljs.core.async.__GT_t_cljs$core$async14533 = (function cljs$core$async$__GT_t_cljs$core$async14533(f,blockable,meta14534){
return (new cljs.core.async.t_cljs$core$async14533(f,blockable,meta14534));
});


cljs.core.async.fn_handler = (function cljs$core$async$fn_handler(var_args){
var G__14528 = arguments.length;
switch (G__14528) {
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
return (new cljs.core.async.t_cljs$core$async14533(f,blockable,cljs.core.PersistentArrayMap.EMPTY));
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
var G__14558 = arguments.length;
switch (G__14558) {
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
var G__14571 = arguments.length;
switch (G__14571) {
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
var G__14598 = arguments.length;
switch (G__14598) {
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
var val_17901 = cljs.core.deref(ret);
if(cljs.core.truth_(on_caller_QMARK_)){
(fn1.cljs$core$IFn$_invoke$arity$1 ? fn1.cljs$core$IFn$_invoke$arity$1(val_17901) : fn1.call(null,val_17901));
} else {
cljs.core.async.impl.dispatch.run((function (){
return (fn1.cljs$core$IFn$_invoke$arity$1 ? fn1.cljs$core$IFn$_invoke$arity$1(val_17901) : fn1.call(null,val_17901));
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
var G__14626 = arguments.length;
switch (G__14626) {
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
var n__5741__auto___17925 = n;
var x_17926 = (0);
while(true){
if((x_17926 < n__5741__auto___17925)){
(a[x_17926] = x_17926);

var G__17927 = (x_17926 + (1));
x_17926 = G__17927;
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
cljs.core.async.t_cljs$core$async14650 = (function (flag,meta14651){
this.flag = flag;
this.meta14651 = meta14651;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async14650.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_14652,meta14651__$1){
var self__ = this;
var _14652__$1 = this;
return (new cljs.core.async.t_cljs$core$async14650(self__.flag,meta14651__$1));
}));

(cljs.core.async.t_cljs$core$async14650.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_14652){
var self__ = this;
var _14652__$1 = this;
return self__.meta14651;
}));

(cljs.core.async.t_cljs$core$async14650.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async14650.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.deref(self__.flag);
}));

(cljs.core.async.t_cljs$core$async14650.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return true;
}));

(cljs.core.async.t_cljs$core$async14650.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_(self__.flag,null);

return true;
}));

(cljs.core.async.t_cljs$core$async14650.getBasis = (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"flag","flag",-1565787888,null),new cljs.core.Symbol(null,"meta14651","meta14651",-492588648,null)], null);
}));

(cljs.core.async.t_cljs$core$async14650.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async14650.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async14650");

(cljs.core.async.t_cljs$core$async14650.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async14650");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async14650.
 */
cljs.core.async.__GT_t_cljs$core$async14650 = (function cljs$core$async$__GT_t_cljs$core$async14650(flag,meta14651){
return (new cljs.core.async.t_cljs$core$async14650(flag,meta14651));
});


cljs.core.async.alt_flag = (function cljs$core$async$alt_flag(){
var flag = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(true);
return (new cljs.core.async.t_cljs$core$async14650(flag,cljs.core.PersistentArrayMap.EMPTY));
});

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async14682 = (function (flag,cb,meta14683){
this.flag = flag;
this.cb = cb;
this.meta14683 = meta14683;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async14682.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_14684,meta14683__$1){
var self__ = this;
var _14684__$1 = this;
return (new cljs.core.async.t_cljs$core$async14682(self__.flag,self__.cb,meta14683__$1));
}));

(cljs.core.async.t_cljs$core$async14682.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_14684){
var self__ = this;
var _14684__$1 = this;
return self__.meta14683;
}));

(cljs.core.async.t_cljs$core$async14682.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async14682.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.active_QMARK_(self__.flag);
}));

(cljs.core.async.t_cljs$core$async14682.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return true;
}));

(cljs.core.async.t_cljs$core$async14682.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.async.impl.protocols.commit(self__.flag);

return self__.cb;
}));

(cljs.core.async.t_cljs$core$async14682.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"flag","flag",-1565787888,null),new cljs.core.Symbol(null,"cb","cb",-2064487928,null),new cljs.core.Symbol(null,"meta14683","meta14683",2124930370,null)], null);
}));

(cljs.core.async.t_cljs$core$async14682.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async14682.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async14682");

(cljs.core.async.t_cljs$core$async14682.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async14682");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async14682.
 */
cljs.core.async.__GT_t_cljs$core$async14682 = (function cljs$core$async$__GT_t_cljs$core$async14682(flag,cb,meta14683){
return (new cljs.core.async.t_cljs$core$async14682(flag,cb,meta14683));
});


cljs.core.async.alt_handler = (function cljs$core$async$alt_handler(flag,cb){
return (new cljs.core.async.t_cljs$core$async14682(flag,cb,cljs.core.PersistentArrayMap.EMPTY));
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
var port_17942 = cljs.core.nth.cljs$core$IFn$_invoke$arity$2(ports__$1,i);
if(cljs.core.vector_QMARK_(port_17942)){
if((!(((port_17942.cljs$core$IFn$_invoke$arity$1 ? port_17942.cljs$core$IFn$_invoke$arity$1((1)) : port_17942.call(null,(1))) == null)))){
} else {
throw (new Error((""+"Assert failed: "+"can't put nil on channel"+"\n"+"(some? (port 1))")));
}
} else {
}

var G__17943 = (i + (1));
i = G__17943;
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
return (function (p1__14704_SHARP_){
var G__14719 = new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__14704_SHARP_,wport], null);
return (fret.cljs$core$IFn$_invoke$arity$1 ? fret.cljs$core$IFn$_invoke$arity$1(G__14719) : fret.call(null,G__14719));
});})(i,val,idx,port,wport,flag,ports__$1,n,_,idxs,priority))
));
})():cljs.core.async.impl.protocols.take_BANG_(port,cljs.core.async.alt_handler(flag,((function (i,idx,port,wport,flag,ports__$1,n,_,idxs,priority){
return (function (p1__14705_SHARP_){
var G__14720 = new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__14705_SHARP_,port], null);
return (fret.cljs$core$IFn$_invoke$arity$1 ? fret.cljs$core$IFn$_invoke$arity$1(G__14720) : fret.call(null,G__14720));
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
var G__17946 = (i + (1));
i = G__17946;
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
var len__5876__auto___17949 = arguments.length;
var i__5877__auto___17950 = (0);
while(true){
if((i__5877__auto___17950 < len__5876__auto___17949)){
args__5882__auto__.push((arguments[i__5877__auto___17950]));

var G__17951 = (i__5877__auto___17950 + (1));
i__5877__auto___17950 = G__17951;
continue;
} else {
}
break;
}

var argseq__5883__auto__ = ((((1) < args__5882__auto__.length))?(new cljs.core.IndexedSeq(args__5882__auto__.slice((1)),(0),null)):null);
return cljs.core.async.alts_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__5883__auto__);
});

(cljs.core.async.alts_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (ports,p__14813){
var map__14821 = p__14813;
var map__14821__$1 = cljs.core.__destructure_map(map__14821);
var opts = map__14821__$1;
throw (new Error("alts! used not in (go ...) block"));
}));

(cljs.core.async.alts_BANG_.cljs$lang$maxFixedArity = (1));

/** @this {Function} */
(cljs.core.async.alts_BANG_.cljs$lang$applyTo = (function (seq14772){
var G__14773 = cljs.core.first(seq14772);
var seq14772__$1 = cljs.core.next(seq14772);
var self__5861__auto__ = this;
return self__5861__auto__.cljs$core$IFn$_invoke$arity$variadic(G__14773,seq14772__$1);
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
var G__14857 = arguments.length;
switch (G__14857) {
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
var c__14427__auto___17953 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = (function (state_14990){
var state_val_14991 = (state_14990[(1)]);
if((state_val_14991 === (7))){
var inst_14986 = (state_14990[(2)]);
var state_14990__$1 = state_14990;
var statearr_15008_17958 = state_14990__$1;
(statearr_15008_17958[(2)] = inst_14986);

(statearr_15008_17958[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14991 === (1))){
var state_14990__$1 = state_14990;
var statearr_15009_17959 = state_14990__$1;
(statearr_15009_17959[(2)] = null);

(statearr_15009_17959[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14991 === (4))){
var inst_14953 = (state_14990[(7)]);
var inst_14953__$1 = (state_14990[(2)]);
var inst_14960 = (inst_14953__$1 == null);
var state_14990__$1 = (function (){var statearr_15013 = state_14990;
(statearr_15013[(7)] = inst_14953__$1);

return statearr_15013;
})();
if(cljs.core.truth_(inst_14960)){
var statearr_15017_17964 = state_14990__$1;
(statearr_15017_17964[(1)] = (5));

} else {
var statearr_15019_17965 = state_14990__$1;
(statearr_15019_17965[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14991 === (13))){
var state_14990__$1 = state_14990;
var statearr_15025_17967 = state_14990__$1;
(statearr_15025_17967[(2)] = null);

(statearr_15025_17967[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14991 === (6))){
var inst_14953 = (state_14990[(7)]);
var state_14990__$1 = state_14990;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_14990__$1,(11),to,inst_14953);
} else {
if((state_val_14991 === (3))){
var inst_14988 = (state_14990[(2)]);
var state_14990__$1 = state_14990;
return cljs.core.async.impl.ioc_helpers.return_chan(state_14990__$1,inst_14988);
} else {
if((state_val_14991 === (12))){
var state_14990__$1 = state_14990;
var statearr_15035_17972 = state_14990__$1;
(statearr_15035_17972[(2)] = null);

(statearr_15035_17972[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14991 === (2))){
var state_14990__$1 = state_14990;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_14990__$1,(4),from);
} else {
if((state_val_14991 === (11))){
var inst_14970 = (state_14990[(2)]);
var state_14990__$1 = state_14990;
if(cljs.core.truth_(inst_14970)){
var statearr_15044_17973 = state_14990__$1;
(statearr_15044_17973[(1)] = (12));

} else {
var statearr_15046_17974 = state_14990__$1;
(statearr_15046_17974[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14991 === (9))){
var state_14990__$1 = state_14990;
var statearr_15047_17976 = state_14990__$1;
(statearr_15047_17976[(2)] = null);

(statearr_15047_17976[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14991 === (5))){
var state_14990__$1 = state_14990;
if(cljs.core.truth_(close_QMARK_)){
var statearr_15051_17977 = state_14990__$1;
(statearr_15051_17977[(1)] = (8));

} else {
var statearr_15052_17978 = state_14990__$1;
(statearr_15052_17978[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14991 === (14))){
var inst_14984 = (state_14990[(2)]);
var state_14990__$1 = state_14990;
var statearr_15056_17980 = state_14990__$1;
(statearr_15056_17980[(2)] = inst_14984);

(statearr_15056_17980[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14991 === (10))){
var inst_14967 = (state_14990[(2)]);
var state_14990__$1 = state_14990;
var statearr_15063_17981 = state_14990__$1;
(statearr_15063_17981[(2)] = inst_14967);

(statearr_15063_17981[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_14991 === (8))){
var inst_14964 = cljs.core.async.close_BANG_(to);
var state_14990__$1 = state_14990;
var statearr_15065_17982 = state_14990__$1;
(statearr_15065_17982[(2)] = inst_14964);

(statearr_15065_17982[(1)] = (10));


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
var cljs$core$async$state_machine__14162__auto__ = null;
var cljs$core$async$state_machine__14162__auto____0 = (function (){
var statearr_15066 = [null,null,null,null,null,null,null,null];
(statearr_15066[(0)] = cljs$core$async$state_machine__14162__auto__);

(statearr_15066[(1)] = (1));

return statearr_15066;
});
var cljs$core$async$state_machine__14162__auto____1 = (function (state_14990){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_14990);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e15072){var ex__14165__auto__ = e15072;
var statearr_15074_17985 = state_14990;
(statearr_15074_17985[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_14990[(4)]))){
var statearr_15076_17986 = state_14990;
(statearr_15076_17986[(1)] = cljs.core.first((state_14990[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__17988 = state_14990;
state_14990 = G__17988;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$state_machine__14162__auto__ = function(state_14990){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14162__auto____1.call(this,state_14990);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14162__auto____0;
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14162__auto____1;
return cljs$core$async$state_machine__14162__auto__;
})()
})();
var state__14429__auto__ = (function (){var statearr_15082 = f__14428__auto__();
(statearr_15082[(6)] = c__14427__auto___17953);

return statearr_15082;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
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
var process__$1 = (function (p__15107){
var vec__15108 = p__15107;
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__15108,(0),null);
var p = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__15108,(1),null);
var job = vec__15108;
if((job == null)){
cljs.core.async.close_BANG_(results);

return null;
} else {
var res = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3((1),xf,ex_handler);
var c__14427__auto___17995 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = (function (state_15118){
var state_val_15119 = (state_15118[(1)]);
if((state_val_15119 === (1))){
var state_15118__$1 = state_15118;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_15118__$1,(2),res,v);
} else {
if((state_val_15119 === (2))){
var inst_15113 = (state_15118[(2)]);
var inst_15114 = cljs.core.async.close_BANG_(res);
var state_15118__$1 = (function (){var statearr_15127 = state_15118;
(statearr_15127[(7)] = inst_15113);

return statearr_15127;
})();
return cljs.core.async.impl.ioc_helpers.return_chan(state_15118__$1,inst_15114);
} else {
return null;
}
}
});
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____0 = (function (){
var statearr_15129 = [null,null,null,null,null,null,null,null];
(statearr_15129[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__);

(statearr_15129[(1)] = (1));

return statearr_15129;
});
var cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____1 = (function (state_15118){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_15118);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e15133){var ex__14165__auto__ = e15133;
var statearr_15137_18000 = state_15118;
(statearr_15137_18000[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_15118[(4)]))){
var statearr_15138_18001 = state_15118;
(statearr_15138_18001[(1)] = cljs.core.first((state_15118[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18002 = state_15118;
state_15118 = G__18002;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__ = function(state_15118){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____1.call(this,state_15118);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__;
})()
})();
var state__14429__auto__ = (function (){var statearr_15140 = f__14428__auto__();
(statearr_15140[(6)] = c__14427__auto___17995);

return statearr_15140;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
}));


cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2(p,res);

return true;
}
});
var async = (function (p__15141){
var vec__15142 = p__15141;
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__15142,(0),null);
var p = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__15142,(1),null);
var job = vec__15142;
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
var n__5741__auto___18006 = n;
var __18008 = (0);
while(true){
if((__18008 < n__5741__auto___18006)){
var G__15148_18009 = type;
var G__15148_18010__$1 = (((G__15148_18009 instanceof cljs.core.Keyword))?G__15148_18009.fqn:null);
switch (G__15148_18010__$1) {
case "compute":
var c__14427__auto___18013 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run(((function (__18008,c__14427__auto___18013,G__15148_18009,G__15148_18010__$1,n__5741__auto___18006,jobs,results,process__$1,async){
return (function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = ((function (__18008,c__14427__auto___18013,G__15148_18009,G__15148_18010__$1,n__5741__auto___18006,jobs,results,process__$1,async){
return (function (state_15165){
var state_val_15166 = (state_15165[(1)]);
if((state_val_15166 === (1))){
var state_15165__$1 = state_15165;
var statearr_15168_18014 = state_15165__$1;
(statearr_15168_18014[(2)] = null);

(statearr_15168_18014[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15166 === (2))){
var state_15165__$1 = state_15165;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15165__$1,(4),jobs);
} else {
if((state_val_15166 === (3))){
var inst_15163 = (state_15165[(2)]);
var state_15165__$1 = state_15165;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15165__$1,inst_15163);
} else {
if((state_val_15166 === (4))){
var inst_15155 = (state_15165[(2)]);
var inst_15156 = process__$1(inst_15155);
var state_15165__$1 = state_15165;
if(cljs.core.truth_(inst_15156)){
var statearr_15172_18015 = state_15165__$1;
(statearr_15172_18015[(1)] = (5));

} else {
var statearr_15173_18016 = state_15165__$1;
(statearr_15173_18016[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15166 === (5))){
var state_15165__$1 = state_15165;
var statearr_15174_18018 = state_15165__$1;
(statearr_15174_18018[(2)] = null);

(statearr_15174_18018[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15166 === (6))){
var state_15165__$1 = state_15165;
var statearr_15175_18019 = state_15165__$1;
(statearr_15175_18019[(2)] = null);

(statearr_15175_18019[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15166 === (7))){
var inst_15161 = (state_15165[(2)]);
var state_15165__$1 = state_15165;
var statearr_15176_18020 = state_15165__$1;
(statearr_15176_18020[(2)] = inst_15161);

(statearr_15176_18020[(1)] = (3));


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
});})(__18008,c__14427__auto___18013,G__15148_18009,G__15148_18010__$1,n__5741__auto___18006,jobs,results,process__$1,async))
;
return ((function (__18008,switch__14161__auto__,c__14427__auto___18013,G__15148_18009,G__15148_18010__$1,n__5741__auto___18006,jobs,results,process__$1,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____0 = (function (){
var statearr_15180 = [null,null,null,null,null,null,null];
(statearr_15180[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__);

(statearr_15180[(1)] = (1));

return statearr_15180;
});
var cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____1 = (function (state_15165){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_15165);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e15182){var ex__14165__auto__ = e15182;
var statearr_15183_18024 = state_15165;
(statearr_15183_18024[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_15165[(4)]))){
var statearr_15185_18025 = state_15165;
(statearr_15185_18025[(1)] = cljs.core.first((state_15165[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18026 = state_15165;
state_15165 = G__18026;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__ = function(state_15165){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____1.call(this,state_15165);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__;
})()
;})(__18008,switch__14161__auto__,c__14427__auto___18013,G__15148_18009,G__15148_18010__$1,n__5741__auto___18006,jobs,results,process__$1,async))
})();
var state__14429__auto__ = (function (){var statearr_15189 = f__14428__auto__();
(statearr_15189[(6)] = c__14427__auto___18013);

return statearr_15189;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
});})(__18008,c__14427__auto___18013,G__15148_18009,G__15148_18010__$1,n__5741__auto___18006,jobs,results,process__$1,async))
);


break;
case "async":
var c__14427__auto___18027 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run(((function (__18008,c__14427__auto___18027,G__15148_18009,G__15148_18010__$1,n__5741__auto___18006,jobs,results,process__$1,async){
return (function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = ((function (__18008,c__14427__auto___18027,G__15148_18009,G__15148_18010__$1,n__5741__auto___18006,jobs,results,process__$1,async){
return (function (state_15202){
var state_val_15203 = (state_15202[(1)]);
if((state_val_15203 === (1))){
var state_15202__$1 = state_15202;
var statearr_15207_18031 = state_15202__$1;
(statearr_15207_18031[(2)] = null);

(statearr_15207_18031[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15203 === (2))){
var state_15202__$1 = state_15202;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15202__$1,(4),jobs);
} else {
if((state_val_15203 === (3))){
var inst_15200 = (state_15202[(2)]);
var state_15202__$1 = state_15202;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15202__$1,inst_15200);
} else {
if((state_val_15203 === (4))){
var inst_15192 = (state_15202[(2)]);
var inst_15193 = async(inst_15192);
var state_15202__$1 = state_15202;
if(cljs.core.truth_(inst_15193)){
var statearr_15213_18035 = state_15202__$1;
(statearr_15213_18035[(1)] = (5));

} else {
var statearr_15214_18036 = state_15202__$1;
(statearr_15214_18036[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15203 === (5))){
var state_15202__$1 = state_15202;
var statearr_15219_18037 = state_15202__$1;
(statearr_15219_18037[(2)] = null);

(statearr_15219_18037[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15203 === (6))){
var state_15202__$1 = state_15202;
var statearr_15220_18038 = state_15202__$1;
(statearr_15220_18038[(2)] = null);

(statearr_15220_18038[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15203 === (7))){
var inst_15198 = (state_15202[(2)]);
var state_15202__$1 = state_15202;
var statearr_15222_18039 = state_15202__$1;
(statearr_15222_18039[(2)] = inst_15198);

(statearr_15222_18039[(1)] = (3));


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
});})(__18008,c__14427__auto___18027,G__15148_18009,G__15148_18010__$1,n__5741__auto___18006,jobs,results,process__$1,async))
;
return ((function (__18008,switch__14161__auto__,c__14427__auto___18027,G__15148_18009,G__15148_18010__$1,n__5741__auto___18006,jobs,results,process__$1,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____0 = (function (){
var statearr_15226 = [null,null,null,null,null,null,null];
(statearr_15226[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__);

(statearr_15226[(1)] = (1));

return statearr_15226;
});
var cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____1 = (function (state_15202){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_15202);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e15227){var ex__14165__auto__ = e15227;
var statearr_15231_18044 = state_15202;
(statearr_15231_18044[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_15202[(4)]))){
var statearr_15232_18045 = state_15202;
(statearr_15232_18045[(1)] = cljs.core.first((state_15202[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18046 = state_15202;
state_15202 = G__18046;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__ = function(state_15202){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____1.call(this,state_15202);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__;
})()
;})(__18008,switch__14161__auto__,c__14427__auto___18027,G__15148_18009,G__15148_18010__$1,n__5741__auto___18006,jobs,results,process__$1,async))
})();
var state__14429__auto__ = (function (){var statearr_15235 = f__14428__auto__();
(statearr_15235[(6)] = c__14427__auto___18027);

return statearr_15235;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
});})(__18008,c__14427__auto___18027,G__15148_18009,G__15148_18010__$1,n__5741__auto___18006,jobs,results,process__$1,async))
);


break;
default:
throw (new Error((""+"No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__15148_18010__$1))));

}

var G__18047 = (__18008 + (1));
__18008 = G__18047;
continue;
} else {
}
break;
}

var c__14427__auto___18048 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = (function (state_15263){
var state_val_15264 = (state_15263[(1)]);
if((state_val_15264 === (7))){
var inst_15259 = (state_15263[(2)]);
var state_15263__$1 = state_15263;
var statearr_15267_18051 = state_15263__$1;
(statearr_15267_18051[(2)] = inst_15259);

(statearr_15267_18051[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15264 === (1))){
var state_15263__$1 = state_15263;
var statearr_15268_18052 = state_15263__$1;
(statearr_15268_18052[(2)] = null);

(statearr_15268_18052[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15264 === (4))){
var inst_15244 = (state_15263[(7)]);
var inst_15244__$1 = (state_15263[(2)]);
var inst_15245 = (inst_15244__$1 == null);
var state_15263__$1 = (function (){var statearr_15269 = state_15263;
(statearr_15269[(7)] = inst_15244__$1);

return statearr_15269;
})();
if(cljs.core.truth_(inst_15245)){
var statearr_15271_18058 = state_15263__$1;
(statearr_15271_18058[(1)] = (5));

} else {
var statearr_15272_18060 = state_15263__$1;
(statearr_15272_18060[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15264 === (6))){
var inst_15244 = (state_15263[(7)]);
var inst_15249 = (state_15263[(8)]);
var inst_15249__$1 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
var inst_15250 = cljs.core.PersistentVector.EMPTY_NODE;
var inst_15251 = [inst_15244,inst_15249__$1];
var inst_15252 = (new cljs.core.PersistentVector(null,2,(5),inst_15250,inst_15251,null));
var state_15263__$1 = (function (){var statearr_15274 = state_15263;
(statearr_15274[(8)] = inst_15249__$1);

return statearr_15274;
})();
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_15263__$1,(8),jobs,inst_15252);
} else {
if((state_val_15264 === (3))){
var inst_15261 = (state_15263[(2)]);
var state_15263__$1 = state_15263;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15263__$1,inst_15261);
} else {
if((state_val_15264 === (2))){
var state_15263__$1 = state_15263;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15263__$1,(4),from);
} else {
if((state_val_15264 === (9))){
var inst_15256 = (state_15263[(2)]);
var state_15263__$1 = (function (){var statearr_15276 = state_15263;
(statearr_15276[(9)] = inst_15256);

return statearr_15276;
})();
var statearr_15278_18066 = state_15263__$1;
(statearr_15278_18066[(2)] = null);

(statearr_15278_18066[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15264 === (5))){
var inst_15247 = cljs.core.async.close_BANG_(jobs);
var state_15263__$1 = state_15263;
var statearr_15279_18067 = state_15263__$1;
(statearr_15279_18067[(2)] = inst_15247);

(statearr_15279_18067[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15264 === (8))){
var inst_15249 = (state_15263[(8)]);
var inst_15254 = (state_15263[(2)]);
var state_15263__$1 = (function (){var statearr_15280 = state_15263;
(statearr_15280[(10)] = inst_15254);

return statearr_15280;
})();
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_15263__$1,(9),results,inst_15249);
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
var cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____0 = (function (){
var statearr_15283 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_15283[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__);

(statearr_15283[(1)] = (1));

return statearr_15283;
});
var cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____1 = (function (state_15263){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_15263);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e15284){var ex__14165__auto__ = e15284;
var statearr_15285_18077 = state_15263;
(statearr_15285_18077[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_15263[(4)]))){
var statearr_15287_18080 = state_15263;
(statearr_15287_18080[(1)] = cljs.core.first((state_15263[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18081 = state_15263;
state_15263 = G__18081;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__ = function(state_15263){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____1.call(this,state_15263);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__;
})()
})();
var state__14429__auto__ = (function (){var statearr_15288 = f__14428__auto__();
(statearr_15288[(6)] = c__14427__auto___18048);

return statearr_15288;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
}));


var c__14427__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = (function (state_15331){
var state_val_15332 = (state_15331[(1)]);
if((state_val_15332 === (7))){
var inst_15327 = (state_15331[(2)]);
var state_15331__$1 = state_15331;
var statearr_15337_18088 = state_15331__$1;
(statearr_15337_18088[(2)] = inst_15327);

(statearr_15337_18088[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15332 === (20))){
var state_15331__$1 = state_15331;
var statearr_15338_18093 = state_15331__$1;
(statearr_15338_18093[(2)] = null);

(statearr_15338_18093[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15332 === (1))){
var state_15331__$1 = state_15331;
var statearr_15339_18094 = state_15331__$1;
(statearr_15339_18094[(2)] = null);

(statearr_15339_18094[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15332 === (4))){
var inst_15292 = (state_15331[(7)]);
var inst_15292__$1 = (state_15331[(2)]);
var inst_15293 = (inst_15292__$1 == null);
var state_15331__$1 = (function (){var statearr_15341 = state_15331;
(statearr_15341[(7)] = inst_15292__$1);

return statearr_15341;
})();
if(cljs.core.truth_(inst_15293)){
var statearr_15342_18101 = state_15331__$1;
(statearr_15342_18101[(1)] = (5));

} else {
var statearr_15343_18102 = state_15331__$1;
(statearr_15343_18102[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15332 === (15))){
var inst_15305 = (state_15331[(8)]);
var state_15331__$1 = state_15331;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_15331__$1,(18),to,inst_15305);
} else {
if((state_val_15332 === (21))){
var inst_15321 = (state_15331[(2)]);
var state_15331__$1 = state_15331;
var statearr_15344_18105 = state_15331__$1;
(statearr_15344_18105[(2)] = inst_15321);

(statearr_15344_18105[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15332 === (13))){
var inst_15324 = (state_15331[(2)]);
var state_15331__$1 = (function (){var statearr_15348 = state_15331;
(statearr_15348[(9)] = inst_15324);

return statearr_15348;
})();
var statearr_15350_18107 = state_15331__$1;
(statearr_15350_18107[(2)] = null);

(statearr_15350_18107[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15332 === (6))){
var inst_15292 = (state_15331[(7)]);
var state_15331__$1 = state_15331;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15331__$1,(11),inst_15292);
} else {
if((state_val_15332 === (17))){
var inst_15315 = (state_15331[(2)]);
var state_15331__$1 = state_15331;
if(cljs.core.truth_(inst_15315)){
var statearr_15356_18108 = state_15331__$1;
(statearr_15356_18108[(1)] = (19));

} else {
var statearr_15358_18109 = state_15331__$1;
(statearr_15358_18109[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15332 === (3))){
var inst_15329 = (state_15331[(2)]);
var state_15331__$1 = state_15331;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15331__$1,inst_15329);
} else {
if((state_val_15332 === (12))){
var inst_15302 = (state_15331[(10)]);
var state_15331__$1 = state_15331;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15331__$1,(14),inst_15302);
} else {
if((state_val_15332 === (2))){
var state_15331__$1 = state_15331;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15331__$1,(4),results);
} else {
if((state_val_15332 === (19))){
var state_15331__$1 = state_15331;
var statearr_15365_18112 = state_15331__$1;
(statearr_15365_18112[(2)] = null);

(statearr_15365_18112[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15332 === (11))){
var inst_15302 = (state_15331[(2)]);
var state_15331__$1 = (function (){var statearr_15366 = state_15331;
(statearr_15366[(10)] = inst_15302);

return statearr_15366;
})();
var statearr_15367_18117 = state_15331__$1;
(statearr_15367_18117[(2)] = null);

(statearr_15367_18117[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15332 === (9))){
var state_15331__$1 = state_15331;
var statearr_15369_18119 = state_15331__$1;
(statearr_15369_18119[(2)] = null);

(statearr_15369_18119[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15332 === (5))){
var state_15331__$1 = state_15331;
if(cljs.core.truth_(close_QMARK_)){
var statearr_15370_18122 = state_15331__$1;
(statearr_15370_18122[(1)] = (8));

} else {
var statearr_15371_18123 = state_15331__$1;
(statearr_15371_18123[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15332 === (14))){
var inst_15305 = (state_15331[(8)]);
var inst_15307 = (state_15331[(11)]);
var inst_15305__$1 = (state_15331[(2)]);
var inst_15306 = (inst_15305__$1 == null);
var inst_15307__$1 = cljs.core.not(inst_15306);
var state_15331__$1 = (function (){var statearr_15377 = state_15331;
(statearr_15377[(8)] = inst_15305__$1);

(statearr_15377[(11)] = inst_15307__$1);

return statearr_15377;
})();
if(inst_15307__$1){
var statearr_15378_18133 = state_15331__$1;
(statearr_15378_18133[(1)] = (15));

} else {
var statearr_15379_18134 = state_15331__$1;
(statearr_15379_18134[(1)] = (16));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15332 === (16))){
var inst_15307 = (state_15331[(11)]);
var state_15331__$1 = state_15331;
var statearr_15380_18136 = state_15331__$1;
(statearr_15380_18136[(2)] = inst_15307);

(statearr_15380_18136[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15332 === (10))){
var inst_15299 = (state_15331[(2)]);
var state_15331__$1 = state_15331;
var statearr_15381_18137 = state_15331__$1;
(statearr_15381_18137[(2)] = inst_15299);

(statearr_15381_18137[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15332 === (18))){
var inst_15312 = (state_15331[(2)]);
var state_15331__$1 = state_15331;
var statearr_15382_18138 = state_15331__$1;
(statearr_15382_18138[(2)] = inst_15312);

(statearr_15382_18138[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15332 === (8))){
var inst_15296 = cljs.core.async.close_BANG_(to);
var state_15331__$1 = state_15331;
var statearr_15384_18145 = state_15331__$1;
(statearr_15384_18145[(2)] = inst_15296);

(statearr_15384_18145[(1)] = (10));


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
var cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____0 = (function (){
var statearr_15386 = [null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_15386[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__);

(statearr_15386[(1)] = (1));

return statearr_15386;
});
var cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____1 = (function (state_15331){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_15331);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e15387){var ex__14165__auto__ = e15387;
var statearr_15388_18154 = state_15331;
(statearr_15388_18154[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_15331[(4)]))){
var statearr_15392_18155 = state_15331;
(statearr_15392_18155[(1)] = cljs.core.first((state_15331[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18159 = state_15331;
state_15331 = G__18159;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__ = function(state_15331){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____1.call(this,state_15331);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__14162__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__14162__auto__;
})()
})();
var state__14429__auto__ = (function (){var statearr_15394 = f__14428__auto__();
(statearr_15394[(6)] = c__14427__auto__);

return statearr_15394;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
}));

return c__14427__auto__;
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
var G__15397 = arguments.length;
switch (G__15397) {
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
var G__15403 = arguments.length;
switch (G__15403) {
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
var G__15411 = arguments.length;
switch (G__15411) {
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
var c__14427__auto___18196 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = (function (state_15439){
var state_val_15441 = (state_15439[(1)]);
if((state_val_15441 === (7))){
var inst_15435 = (state_15439[(2)]);
var state_15439__$1 = state_15439;
var statearr_15443_18197 = state_15439__$1;
(statearr_15443_18197[(2)] = inst_15435);

(statearr_15443_18197[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15441 === (1))){
var state_15439__$1 = state_15439;
var statearr_15444_18198 = state_15439__$1;
(statearr_15444_18198[(2)] = null);

(statearr_15444_18198[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15441 === (4))){
var inst_15415 = (state_15439[(7)]);
var inst_15415__$1 = (state_15439[(2)]);
var inst_15417 = (inst_15415__$1 == null);
var state_15439__$1 = (function (){var statearr_15451 = state_15439;
(statearr_15451[(7)] = inst_15415__$1);

return statearr_15451;
})();
if(cljs.core.truth_(inst_15417)){
var statearr_15452_18204 = state_15439__$1;
(statearr_15452_18204[(1)] = (5));

} else {
var statearr_15453_18205 = state_15439__$1;
(statearr_15453_18205[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15441 === (13))){
var state_15439__$1 = state_15439;
var statearr_15455_18206 = state_15439__$1;
(statearr_15455_18206[(2)] = null);

(statearr_15455_18206[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15441 === (6))){
var inst_15415 = (state_15439[(7)]);
var inst_15422 = (p.cljs$core$IFn$_invoke$arity$1 ? p.cljs$core$IFn$_invoke$arity$1(inst_15415) : p.call(null,inst_15415));
var state_15439__$1 = state_15439;
if(cljs.core.truth_(inst_15422)){
var statearr_15461_18212 = state_15439__$1;
(statearr_15461_18212[(1)] = (9));

} else {
var statearr_15466_18213 = state_15439__$1;
(statearr_15466_18213[(1)] = (10));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15441 === (3))){
var inst_15437 = (state_15439[(2)]);
var state_15439__$1 = state_15439;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15439__$1,inst_15437);
} else {
if((state_val_15441 === (12))){
var state_15439__$1 = state_15439;
var statearr_15469_18217 = state_15439__$1;
(statearr_15469_18217[(2)] = null);

(statearr_15469_18217[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15441 === (2))){
var state_15439__$1 = state_15439;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15439__$1,(4),ch);
} else {
if((state_val_15441 === (11))){
var inst_15415 = (state_15439[(7)]);
var inst_15426 = (state_15439[(2)]);
var state_15439__$1 = state_15439;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_15439__$1,(8),inst_15426,inst_15415);
} else {
if((state_val_15441 === (9))){
var state_15439__$1 = state_15439;
var statearr_15473_18221 = state_15439__$1;
(statearr_15473_18221[(2)] = tc);

(statearr_15473_18221[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15441 === (5))){
var inst_15419 = cljs.core.async.close_BANG_(tc);
var inst_15420 = cljs.core.async.close_BANG_(fc);
var state_15439__$1 = (function (){var statearr_15477 = state_15439;
(statearr_15477[(8)] = inst_15419);

return statearr_15477;
})();
var statearr_15478_18223 = state_15439__$1;
(statearr_15478_18223[(2)] = inst_15420);

(statearr_15478_18223[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15441 === (14))){
var inst_15433 = (state_15439[(2)]);
var state_15439__$1 = state_15439;
var statearr_15482_18225 = state_15439__$1;
(statearr_15482_18225[(2)] = inst_15433);

(statearr_15482_18225[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15441 === (10))){
var state_15439__$1 = state_15439;
var statearr_15486_18230 = state_15439__$1;
(statearr_15486_18230[(2)] = fc);

(statearr_15486_18230[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15441 === (8))){
var inst_15428 = (state_15439[(2)]);
var state_15439__$1 = state_15439;
if(cljs.core.truth_(inst_15428)){
var statearr_15488_18235 = state_15439__$1;
(statearr_15488_18235[(1)] = (12));

} else {
var statearr_15493_18238 = state_15439__$1;
(statearr_15493_18238[(1)] = (13));

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
var cljs$core$async$state_machine__14162__auto__ = null;
var cljs$core$async$state_machine__14162__auto____0 = (function (){
var statearr_15499 = [null,null,null,null,null,null,null,null,null];
(statearr_15499[(0)] = cljs$core$async$state_machine__14162__auto__);

(statearr_15499[(1)] = (1));

return statearr_15499;
});
var cljs$core$async$state_machine__14162__auto____1 = (function (state_15439){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_15439);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e15500){var ex__14165__auto__ = e15500;
var statearr_15501_18254 = state_15439;
(statearr_15501_18254[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_15439[(4)]))){
var statearr_15505_18256 = state_15439;
(statearr_15505_18256[(1)] = cljs.core.first((state_15439[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18262 = state_15439;
state_15439 = G__18262;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$state_machine__14162__auto__ = function(state_15439){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14162__auto____1.call(this,state_15439);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14162__auto____0;
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14162__auto____1;
return cljs$core$async$state_machine__14162__auto__;
})()
})();
var state__14429__auto__ = (function (){var statearr_15515 = f__14428__auto__();
(statearr_15515[(6)] = c__14427__auto___18196);

return statearr_15515;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
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
var c__14427__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = (function (state_15566){
var state_val_15567 = (state_15566[(1)]);
if((state_val_15567 === (7))){
var inst_15560 = (state_15566[(2)]);
var state_15566__$1 = state_15566;
var statearr_15568_18279 = state_15566__$1;
(statearr_15568_18279[(2)] = inst_15560);

(statearr_15568_18279[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15567 === (1))){
var inst_15539 = init;
var inst_15540 = inst_15539;
var state_15566__$1 = (function (){var statearr_15569 = state_15566;
(statearr_15569[(7)] = inst_15540);

return statearr_15569;
})();
var statearr_15570_18280 = state_15566__$1;
(statearr_15570_18280[(2)] = null);

(statearr_15570_18280[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15567 === (4))){
var inst_15543 = (state_15566[(8)]);
var inst_15543__$1 = (state_15566[(2)]);
var inst_15544 = (inst_15543__$1 == null);
var state_15566__$1 = (function (){var statearr_15572 = state_15566;
(statearr_15572[(8)] = inst_15543__$1);

return statearr_15572;
})();
if(cljs.core.truth_(inst_15544)){
var statearr_15573_18283 = state_15566__$1;
(statearr_15573_18283[(1)] = (5));

} else {
var statearr_15574_18286 = state_15566__$1;
(statearr_15574_18286[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15567 === (6))){
var inst_15540 = (state_15566[(7)]);
var inst_15543 = (state_15566[(8)]);
var inst_15549 = (state_15566[(9)]);
var inst_15549__$1 = (f.cljs$core$IFn$_invoke$arity$2 ? f.cljs$core$IFn$_invoke$arity$2(inst_15540,inst_15543) : f.call(null,inst_15540,inst_15543));
var inst_15550 = cljs.core.reduced_QMARK_(inst_15549__$1);
var state_15566__$1 = (function (){var statearr_15576 = state_15566;
(statearr_15576[(9)] = inst_15549__$1);

return statearr_15576;
})();
if(inst_15550){
var statearr_15578_18294 = state_15566__$1;
(statearr_15578_18294[(1)] = (8));

} else {
var statearr_15580_18295 = state_15566__$1;
(statearr_15580_18295[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15567 === (3))){
var inst_15562 = (state_15566[(2)]);
var state_15566__$1 = state_15566;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15566__$1,inst_15562);
} else {
if((state_val_15567 === (2))){
var state_15566__$1 = state_15566;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15566__$1,(4),ch);
} else {
if((state_val_15567 === (9))){
var inst_15549 = (state_15566[(9)]);
var inst_15540 = inst_15549;
var state_15566__$1 = (function (){var statearr_15583 = state_15566;
(statearr_15583[(7)] = inst_15540);

return statearr_15583;
})();
var statearr_15586_18299 = state_15566__$1;
(statearr_15586_18299[(2)] = null);

(statearr_15586_18299[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15567 === (5))){
var inst_15540 = (state_15566[(7)]);
var state_15566__$1 = state_15566;
var statearr_15596_18300 = state_15566__$1;
(statearr_15596_18300[(2)] = inst_15540);

(statearr_15596_18300[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15567 === (10))){
var inst_15556 = (state_15566[(2)]);
var state_15566__$1 = state_15566;
var statearr_15602_18315 = state_15566__$1;
(statearr_15602_18315[(2)] = inst_15556);

(statearr_15602_18315[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15567 === (8))){
var inst_15549 = (state_15566[(9)]);
var inst_15552 = cljs.core.deref(inst_15549);
var state_15566__$1 = state_15566;
var statearr_15607_18321 = state_15566__$1;
(statearr_15607_18321[(2)] = inst_15552);

(statearr_15607_18321[(1)] = (10));


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
var cljs$core$async$reduce_$_state_machine__14162__auto__ = null;
var cljs$core$async$reduce_$_state_machine__14162__auto____0 = (function (){
var statearr_15617 = [null,null,null,null,null,null,null,null,null,null];
(statearr_15617[(0)] = cljs$core$async$reduce_$_state_machine__14162__auto__);

(statearr_15617[(1)] = (1));

return statearr_15617;
});
var cljs$core$async$reduce_$_state_machine__14162__auto____1 = (function (state_15566){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_15566);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e15623){var ex__14165__auto__ = e15623;
var statearr_15625_18325 = state_15566;
(statearr_15625_18325[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_15566[(4)]))){
var statearr_15628_18326 = state_15566;
(statearr_15628_18326[(1)] = cljs.core.first((state_15566[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18327 = state_15566;
state_15566 = G__18327;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$reduce_$_state_machine__14162__auto__ = function(state_15566){
switch(arguments.length){
case 0:
return cljs$core$async$reduce_$_state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$reduce_$_state_machine__14162__auto____1.call(this,state_15566);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$reduce_$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$reduce_$_state_machine__14162__auto____0;
cljs$core$async$reduce_$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$reduce_$_state_machine__14162__auto____1;
return cljs$core$async$reduce_$_state_machine__14162__auto__;
})()
})();
var state__14429__auto__ = (function (){var statearr_15635 = f__14428__auto__();
(statearr_15635[(6)] = c__14427__auto__);

return statearr_15635;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
}));

return c__14427__auto__;
});
/**
 * async/reduces a channel with a transformation (xform f).
 *   Returns a channel containing the result.  ch must close before
 *   transduce produces a result.
 */
cljs.core.async.transduce = (function cljs$core$async$transduce(xform,f,init,ch){
var f__$1 = (xform.cljs$core$IFn$_invoke$arity$1 ? xform.cljs$core$IFn$_invoke$arity$1(f) : xform.call(null,f));
var c__14427__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = (function (state_15664){
var state_val_15666 = (state_15664[(1)]);
if((state_val_15666 === (1))){
var inst_15659 = cljs.core.async.reduce(f__$1,init,ch);
var state_15664__$1 = state_15664;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_15664__$1,(2),inst_15659);
} else {
if((state_val_15666 === (2))){
var inst_15661 = (state_15664[(2)]);
var inst_15662 = (f__$1.cljs$core$IFn$_invoke$arity$1 ? f__$1.cljs$core$IFn$_invoke$arity$1(inst_15661) : f__$1.call(null,inst_15661));
var state_15664__$1 = state_15664;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15664__$1,inst_15662);
} else {
return null;
}
}
});
return (function() {
var cljs$core$async$transduce_$_state_machine__14162__auto__ = null;
var cljs$core$async$transduce_$_state_machine__14162__auto____0 = (function (){
var statearr_15668 = [null,null,null,null,null,null,null];
(statearr_15668[(0)] = cljs$core$async$transduce_$_state_machine__14162__auto__);

(statearr_15668[(1)] = (1));

return statearr_15668;
});
var cljs$core$async$transduce_$_state_machine__14162__auto____1 = (function (state_15664){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_15664);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e15670){var ex__14165__auto__ = e15670;
var statearr_15671_18338 = state_15664;
(statearr_15671_18338[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_15664[(4)]))){
var statearr_15672_18339 = state_15664;
(statearr_15672_18339[(1)] = cljs.core.first((state_15664[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18340 = state_15664;
state_15664 = G__18340;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$transduce_$_state_machine__14162__auto__ = function(state_15664){
switch(arguments.length){
case 0:
return cljs$core$async$transduce_$_state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$transduce_$_state_machine__14162__auto____1.call(this,state_15664);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$transduce_$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$transduce_$_state_machine__14162__auto____0;
cljs$core$async$transduce_$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$transduce_$_state_machine__14162__auto____1;
return cljs$core$async$transduce_$_state_machine__14162__auto__;
})()
})();
var state__14429__auto__ = (function (){var statearr_15676 = f__14428__auto__();
(statearr_15676[(6)] = c__14427__auto__);

return statearr_15676;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
}));

return c__14427__auto__;
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
var G__15682 = arguments.length;
switch (G__15682) {
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
var c__14427__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = (function (state_15721){
var state_val_15722 = (state_15721[(1)]);
if((state_val_15722 === (7))){
var inst_15701 = (state_15721[(2)]);
var state_15721__$1 = state_15721;
var statearr_15727_18352 = state_15721__$1;
(statearr_15727_18352[(2)] = inst_15701);

(statearr_15727_18352[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15722 === (1))){
var inst_15694 = cljs.core.seq(coll);
var inst_15695 = inst_15694;
var state_15721__$1 = (function (){var statearr_15730 = state_15721;
(statearr_15730[(7)] = inst_15695);

return statearr_15730;
})();
var statearr_15732_18362 = state_15721__$1;
(statearr_15732_18362[(2)] = null);

(statearr_15732_18362[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15722 === (4))){
var inst_15695 = (state_15721[(7)]);
var inst_15699 = cljs.core.first(inst_15695);
var state_15721__$1 = state_15721;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_15721__$1,(7),ch,inst_15699);
} else {
if((state_val_15722 === (13))){
var inst_15714 = (state_15721[(2)]);
var state_15721__$1 = state_15721;
var statearr_15736_18373 = state_15721__$1;
(statearr_15736_18373[(2)] = inst_15714);

(statearr_15736_18373[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15722 === (6))){
var inst_15704 = (state_15721[(2)]);
var state_15721__$1 = state_15721;
if(cljs.core.truth_(inst_15704)){
var statearr_15739_18375 = state_15721__$1;
(statearr_15739_18375[(1)] = (8));

} else {
var statearr_15741_18388 = state_15721__$1;
(statearr_15741_18388[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15722 === (3))){
var inst_15718 = (state_15721[(2)]);
var state_15721__$1 = state_15721;
return cljs.core.async.impl.ioc_helpers.return_chan(state_15721__$1,inst_15718);
} else {
if((state_val_15722 === (12))){
var state_15721__$1 = state_15721;
var statearr_15745_18390 = state_15721__$1;
(statearr_15745_18390[(2)] = null);

(statearr_15745_18390[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15722 === (2))){
var inst_15695 = (state_15721[(7)]);
var state_15721__$1 = state_15721;
if(cljs.core.truth_(inst_15695)){
var statearr_15749_18398 = state_15721__$1;
(statearr_15749_18398[(1)] = (4));

} else {
var statearr_15750_18400 = state_15721__$1;
(statearr_15750_18400[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15722 === (11))){
var inst_15711 = cljs.core.async.close_BANG_(ch);
var state_15721__$1 = state_15721;
var statearr_15757_18402 = state_15721__$1;
(statearr_15757_18402[(2)] = inst_15711);

(statearr_15757_18402[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15722 === (9))){
var state_15721__$1 = state_15721;
if(cljs.core.truth_(close_QMARK_)){
var statearr_15764_18403 = state_15721__$1;
(statearr_15764_18403[(1)] = (11));

} else {
var statearr_15765_18404 = state_15721__$1;
(statearr_15765_18404[(1)] = (12));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15722 === (5))){
var inst_15695 = (state_15721[(7)]);
var state_15721__$1 = state_15721;
var statearr_15767_18409 = state_15721__$1;
(statearr_15767_18409[(2)] = inst_15695);

(statearr_15767_18409[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15722 === (10))){
var inst_15716 = (state_15721[(2)]);
var state_15721__$1 = state_15721;
var statearr_15773_18418 = state_15721__$1;
(statearr_15773_18418[(2)] = inst_15716);

(statearr_15773_18418[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_15722 === (8))){
var inst_15695 = (state_15721[(7)]);
var inst_15707 = cljs.core.next(inst_15695);
var inst_15695__$1 = inst_15707;
var state_15721__$1 = (function (){var statearr_15774 = state_15721;
(statearr_15774[(7)] = inst_15695__$1);

return statearr_15774;
})();
var statearr_15776_18430 = state_15721__$1;
(statearr_15776_18430[(2)] = null);

(statearr_15776_18430[(1)] = (2));


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
var cljs$core$async$state_machine__14162__auto__ = null;
var cljs$core$async$state_machine__14162__auto____0 = (function (){
var statearr_15779 = [null,null,null,null,null,null,null,null];
(statearr_15779[(0)] = cljs$core$async$state_machine__14162__auto__);

(statearr_15779[(1)] = (1));

return statearr_15779;
});
var cljs$core$async$state_machine__14162__auto____1 = (function (state_15721){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_15721);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e15780){var ex__14165__auto__ = e15780;
var statearr_15781_18434 = state_15721;
(statearr_15781_18434[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_15721[(4)]))){
var statearr_15786_18435 = state_15721;
(statearr_15786_18435[(1)] = cljs.core.first((state_15721[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18437 = state_15721;
state_15721 = G__18437;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$state_machine__14162__auto__ = function(state_15721){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14162__auto____1.call(this,state_15721);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14162__auto____0;
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14162__auto____1;
return cljs$core$async$state_machine__14162__auto__;
})()
})();
var state__14429__auto__ = (function (){var statearr_15795 = f__14428__auto__();
(statearr_15795[(6)] = c__14427__auto__);

return statearr_15795;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
}));

return c__14427__auto__;
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
var G__15808 = arguments.length;
switch (G__15808) {
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

var cljs$core$async$Mux$muxch_STAR_$dyn_18457 = (function (_){
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
return cljs$core$async$Mux$muxch_STAR_$dyn_18457(_);
}
});


/**
 * @interface
 */
cljs.core.async.Mult = function(){};

var cljs$core$async$Mult$tap_STAR_$dyn_18473 = (function (m,ch,close_QMARK_){
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
return cljs$core$async$Mult$tap_STAR_$dyn_18473(m,ch,close_QMARK_);
}
});

var cljs$core$async$Mult$untap_STAR_$dyn_18479 = (function (m,ch){
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
return cljs$core$async$Mult$untap_STAR_$dyn_18479(m,ch);
}
});

var cljs$core$async$Mult$untap_all_STAR_$dyn_18489 = (function (m){
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
return cljs$core$async$Mult$untap_all_STAR_$dyn_18489(m);
}
});


/**
* @constructor
 * @implements {cljs.core.async.Mult}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.async.Mux}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async15852 = (function (ch,cs,meta15853){
this.ch = ch;
this.cs = cs;
this.meta15853 = meta15853;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async15852.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_15854,meta15853__$1){
var self__ = this;
var _15854__$1 = this;
return (new cljs.core.async.t_cljs$core$async15852(self__.ch,self__.cs,meta15853__$1));
}));

(cljs.core.async.t_cljs$core$async15852.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_15854){
var self__ = this;
var _15854__$1 = this;
return self__.meta15853;
}));

(cljs.core.async.t_cljs$core$async15852.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async15852.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.ch;
}));

(cljs.core.async.t_cljs$core$async15852.prototype.cljs$core$async$Mult$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async15852.prototype.cljs$core$async$Mult$tap_STAR_$arity$3 = (function (_,ch__$1,close_QMARK_){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(self__.cs,cljs.core.assoc,ch__$1,close_QMARK_);

return null;
}));

(cljs.core.async.t_cljs$core$async15852.prototype.cljs$core$async$Mult$untap_STAR_$arity$2 = (function (_,ch__$1){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(self__.cs,cljs.core.dissoc,ch__$1);

return null;
}));

(cljs.core.async.t_cljs$core$async15852.prototype.cljs$core$async$Mult$untap_all_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_(self__.cs,cljs.core.PersistentArrayMap.EMPTY);

return null;
}));

(cljs.core.async.t_cljs$core$async15852.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"cs","cs",-117024463,null),new cljs.core.Symbol(null,"meta15853","meta15853",1033338802,null)], null);
}));

(cljs.core.async.t_cljs$core$async15852.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async15852.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async15852");

(cljs.core.async.t_cljs$core$async15852.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async15852");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async15852.
 */
cljs.core.async.__GT_t_cljs$core$async15852 = (function cljs$core$async$__GT_t_cljs$core$async15852(ch,cs,meta15853){
return (new cljs.core.async.t_cljs$core$async15852(ch,cs,meta15853));
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
var m = (new cljs.core.async.t_cljs$core$async15852(ch,cs,cljs.core.PersistentArrayMap.EMPTY));
var dchan = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
var dctr = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(null);
var done = (function (_){
if((cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$2(dctr,cljs.core.dec) === (0))){
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2(dchan,true);
} else {
return null;
}
});
var c__14427__auto___18535 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = (function (state_16023){
var state_val_16025 = (state_16023[(1)]);
if((state_val_16025 === (7))){
var inst_16015 = (state_16023[(2)]);
var state_16023__$1 = state_16023;
var statearr_16034_18544 = state_16023__$1;
(statearr_16034_18544[(2)] = inst_16015);

(statearr_16034_18544[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (20))){
var inst_15910 = (state_16023[(7)]);
var inst_15922 = cljs.core.first(inst_15910);
var inst_15923 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_15922,(0),null);
var inst_15924 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_15922,(1),null);
var state_16023__$1 = (function (){var statearr_16041 = state_16023;
(statearr_16041[(8)] = inst_15923);

return statearr_16041;
})();
if(cljs.core.truth_(inst_15924)){
var statearr_16043_18555 = state_16023__$1;
(statearr_16043_18555[(1)] = (22));

} else {
var statearr_16044_18557 = state_16023__$1;
(statearr_16044_18557[(1)] = (23));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (27))){
var inst_15956 = (state_16023[(9)]);
var inst_15958 = (state_16023[(10)]);
var inst_15964 = (state_16023[(11)]);
var inst_15875 = (state_16023[(12)]);
var inst_15964__$1 = cljs.core._nth(inst_15956,inst_15958);
var inst_15965 = cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$3(inst_15964__$1,inst_15875,done);
var state_16023__$1 = (function (){var statearr_16045 = state_16023;
(statearr_16045[(11)] = inst_15964__$1);

return statearr_16045;
})();
if(cljs.core.truth_(inst_15965)){
var statearr_16046_18559 = state_16023__$1;
(statearr_16046_18559[(1)] = (30));

} else {
var statearr_16048_18560 = state_16023__$1;
(statearr_16048_18560[(1)] = (31));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (1))){
var state_16023__$1 = state_16023;
var statearr_16052_18565 = state_16023__$1;
(statearr_16052_18565[(2)] = null);

(statearr_16052_18565[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (24))){
var inst_15910 = (state_16023[(7)]);
var inst_15932 = (state_16023[(2)]);
var inst_15933 = cljs.core.next(inst_15910);
var inst_15885 = inst_15933;
var inst_15886 = null;
var inst_15887 = (0);
var inst_15888 = (0);
var state_16023__$1 = (function (){var statearr_16058 = state_16023;
(statearr_16058[(13)] = inst_15932);

(statearr_16058[(14)] = inst_15885);

(statearr_16058[(15)] = inst_15886);

(statearr_16058[(16)] = inst_15887);

(statearr_16058[(17)] = inst_15888);

return statearr_16058;
})();
var statearr_16059_18574 = state_16023__$1;
(statearr_16059_18574[(2)] = null);

(statearr_16059_18574[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (39))){
var state_16023__$1 = state_16023;
var statearr_16068_18575 = state_16023__$1;
(statearr_16068_18575[(2)] = null);

(statearr_16068_18575[(1)] = (41));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (4))){
var inst_15875 = (state_16023[(12)]);
var inst_15875__$1 = (state_16023[(2)]);
var inst_15876 = (inst_15875__$1 == null);
var state_16023__$1 = (function (){var statearr_16070 = state_16023;
(statearr_16070[(12)] = inst_15875__$1);

return statearr_16070;
})();
if(cljs.core.truth_(inst_15876)){
var statearr_16071_18576 = state_16023__$1;
(statearr_16071_18576[(1)] = (5));

} else {
var statearr_16073_18577 = state_16023__$1;
(statearr_16073_18577[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (15))){
var inst_15888 = (state_16023[(17)]);
var inst_15885 = (state_16023[(14)]);
var inst_15886 = (state_16023[(15)]);
var inst_15887 = (state_16023[(16)]);
var inst_15906 = (state_16023[(2)]);
var inst_15907 = (inst_15888 + (1));
var tmp16064 = inst_15887;
var tmp16065 = inst_15886;
var tmp16066 = inst_15885;
var inst_15885__$1 = tmp16066;
var inst_15886__$1 = tmp16065;
var inst_15887__$1 = tmp16064;
var inst_15888__$1 = inst_15907;
var state_16023__$1 = (function (){var statearr_16077 = state_16023;
(statearr_16077[(18)] = inst_15906);

(statearr_16077[(14)] = inst_15885__$1);

(statearr_16077[(15)] = inst_15886__$1);

(statearr_16077[(16)] = inst_15887__$1);

(statearr_16077[(17)] = inst_15888__$1);

return statearr_16077;
})();
var statearr_16083_18586 = state_16023__$1;
(statearr_16083_18586[(2)] = null);

(statearr_16083_18586[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (21))){
var inst_15936 = (state_16023[(2)]);
var state_16023__$1 = state_16023;
var statearr_16089_18587 = state_16023__$1;
(statearr_16089_18587[(2)] = inst_15936);

(statearr_16089_18587[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (31))){
var inst_15964 = (state_16023[(11)]);
var inst_15968 = m.cljs$core$async$Mult$untap_STAR_$arity$2(null,inst_15964);
var state_16023__$1 = state_16023;
var statearr_16096_18592 = state_16023__$1;
(statearr_16096_18592[(2)] = inst_15968);

(statearr_16096_18592[(1)] = (32));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (32))){
var inst_15958 = (state_16023[(10)]);
var inst_15955 = (state_16023[(19)]);
var inst_15956 = (state_16023[(9)]);
var inst_15957 = (state_16023[(20)]);
var inst_15971 = (state_16023[(2)]);
var inst_15973 = (inst_15958 + (1));
var tmp16084 = inst_15955;
var tmp16085 = inst_15956;
var tmp16086 = inst_15957;
var inst_15955__$1 = tmp16084;
var inst_15956__$1 = tmp16085;
var inst_15957__$1 = tmp16086;
var inst_15958__$1 = inst_15973;
var state_16023__$1 = (function (){var statearr_16102 = state_16023;
(statearr_16102[(21)] = inst_15971);

(statearr_16102[(19)] = inst_15955__$1);

(statearr_16102[(9)] = inst_15956__$1);

(statearr_16102[(20)] = inst_15957__$1);

(statearr_16102[(10)] = inst_15958__$1);

return statearr_16102;
})();
var statearr_16106_18616 = state_16023__$1;
(statearr_16106_18616[(2)] = null);

(statearr_16106_18616[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (40))){
var inst_15987 = (state_16023[(22)]);
var inst_15991 = m.cljs$core$async$Mult$untap_STAR_$arity$2(null,inst_15987);
var state_16023__$1 = state_16023;
var statearr_16108_18622 = state_16023__$1;
(statearr_16108_18622[(2)] = inst_15991);

(statearr_16108_18622[(1)] = (41));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (33))){
var inst_15976 = (state_16023[(23)]);
var inst_15978 = cljs.core.chunked_seq_QMARK_(inst_15976);
var state_16023__$1 = state_16023;
if(inst_15978){
var statearr_16114_18624 = state_16023__$1;
(statearr_16114_18624[(1)] = (36));

} else {
var statearr_16115_18626 = state_16023__$1;
(statearr_16115_18626[(1)] = (37));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (13))){
var inst_15899 = (state_16023[(24)]);
var inst_15903 = cljs.core.async.close_BANG_(inst_15899);
var state_16023__$1 = state_16023;
var statearr_16119_18627 = state_16023__$1;
(statearr_16119_18627[(2)] = inst_15903);

(statearr_16119_18627[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (22))){
var inst_15923 = (state_16023[(8)]);
var inst_15929 = cljs.core.async.close_BANG_(inst_15923);
var state_16023__$1 = state_16023;
var statearr_16121_18628 = state_16023__$1;
(statearr_16121_18628[(2)] = inst_15929);

(statearr_16121_18628[(1)] = (24));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (36))){
var inst_15976 = (state_16023[(23)]);
var inst_15981 = cljs.core.chunk_first(inst_15976);
var inst_15982 = cljs.core.chunk_rest(inst_15976);
var inst_15983 = cljs.core.count(inst_15981);
var inst_15955 = inst_15982;
var inst_15956 = inst_15981;
var inst_15957 = inst_15983;
var inst_15958 = (0);
var state_16023__$1 = (function (){var statearr_16127 = state_16023;
(statearr_16127[(19)] = inst_15955);

(statearr_16127[(9)] = inst_15956);

(statearr_16127[(20)] = inst_15957);

(statearr_16127[(10)] = inst_15958);

return statearr_16127;
})();
var statearr_16128_18638 = state_16023__$1;
(statearr_16128_18638[(2)] = null);

(statearr_16128_18638[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (41))){
var inst_15976 = (state_16023[(23)]);
var inst_15993 = (state_16023[(2)]);
var inst_15994 = cljs.core.next(inst_15976);
var inst_15955 = inst_15994;
var inst_15956 = null;
var inst_15957 = (0);
var inst_15958 = (0);
var state_16023__$1 = (function (){var statearr_16132 = state_16023;
(statearr_16132[(25)] = inst_15993);

(statearr_16132[(19)] = inst_15955);

(statearr_16132[(9)] = inst_15956);

(statearr_16132[(20)] = inst_15957);

(statearr_16132[(10)] = inst_15958);

return statearr_16132;
})();
var statearr_16133_18641 = state_16023__$1;
(statearr_16133_18641[(2)] = null);

(statearr_16133_18641[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (43))){
var state_16023__$1 = state_16023;
var statearr_16134_18644 = state_16023__$1;
(statearr_16134_18644[(2)] = null);

(statearr_16134_18644[(1)] = (44));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (29))){
var inst_16003 = (state_16023[(2)]);
var state_16023__$1 = state_16023;
var statearr_16138_18653 = state_16023__$1;
(statearr_16138_18653[(2)] = inst_16003);

(statearr_16138_18653[(1)] = (26));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (44))){
var inst_16012 = (state_16023[(2)]);
var state_16023__$1 = (function (){var statearr_16143 = state_16023;
(statearr_16143[(26)] = inst_16012);

return statearr_16143;
})();
var statearr_16147_18654 = state_16023__$1;
(statearr_16147_18654[(2)] = null);

(statearr_16147_18654[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (6))){
var inst_15947 = (state_16023[(27)]);
var inst_15946 = cljs.core.deref(cs);
var inst_15947__$1 = cljs.core.keys(inst_15946);
var inst_15948 = cljs.core.count(inst_15947__$1);
var inst_15949 = cljs.core.reset_BANG_(dctr,inst_15948);
var inst_15954 = cljs.core.seq(inst_15947__$1);
var inst_15955 = inst_15954;
var inst_15956 = null;
var inst_15957 = (0);
var inst_15958 = (0);
var state_16023__$1 = (function (){var statearr_16152 = state_16023;
(statearr_16152[(27)] = inst_15947__$1);

(statearr_16152[(28)] = inst_15949);

(statearr_16152[(19)] = inst_15955);

(statearr_16152[(9)] = inst_15956);

(statearr_16152[(20)] = inst_15957);

(statearr_16152[(10)] = inst_15958);

return statearr_16152;
})();
var statearr_16155_18663 = state_16023__$1;
(statearr_16155_18663[(2)] = null);

(statearr_16155_18663[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (28))){
var inst_15955 = (state_16023[(19)]);
var inst_15976 = (state_16023[(23)]);
var inst_15976__$1 = cljs.core.seq(inst_15955);
var state_16023__$1 = (function (){var statearr_16159 = state_16023;
(statearr_16159[(23)] = inst_15976__$1);

return statearr_16159;
})();
if(inst_15976__$1){
var statearr_16161_18677 = state_16023__$1;
(statearr_16161_18677[(1)] = (33));

} else {
var statearr_16162_18678 = state_16023__$1;
(statearr_16162_18678[(1)] = (34));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (25))){
var inst_15958 = (state_16023[(10)]);
var inst_15957 = (state_16023[(20)]);
var inst_15961 = (inst_15958 < inst_15957);
var inst_15962 = inst_15961;
var state_16023__$1 = state_16023;
if(cljs.core.truth_(inst_15962)){
var statearr_16167_18679 = state_16023__$1;
(statearr_16167_18679[(1)] = (27));

} else {
var statearr_16169_18684 = state_16023__$1;
(statearr_16169_18684[(1)] = (28));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (34))){
var state_16023__$1 = state_16023;
var statearr_16173_18685 = state_16023__$1;
(statearr_16173_18685[(2)] = null);

(statearr_16173_18685[(1)] = (35));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (17))){
var state_16023__$1 = state_16023;
var statearr_16178_18689 = state_16023__$1;
(statearr_16178_18689[(2)] = null);

(statearr_16178_18689[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (3))){
var inst_16017 = (state_16023[(2)]);
var state_16023__$1 = state_16023;
return cljs.core.async.impl.ioc_helpers.return_chan(state_16023__$1,inst_16017);
} else {
if((state_val_16025 === (12))){
var inst_15941 = (state_16023[(2)]);
var state_16023__$1 = state_16023;
var statearr_16180_18692 = state_16023__$1;
(statearr_16180_18692[(2)] = inst_15941);

(statearr_16180_18692[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (2))){
var state_16023__$1 = state_16023;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_16023__$1,(4),ch);
} else {
if((state_val_16025 === (23))){
var state_16023__$1 = state_16023;
var statearr_16186_18695 = state_16023__$1;
(statearr_16186_18695[(2)] = null);

(statearr_16186_18695[(1)] = (24));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (35))){
var inst_16001 = (state_16023[(2)]);
var state_16023__$1 = state_16023;
var statearr_16193_18701 = state_16023__$1;
(statearr_16193_18701[(2)] = inst_16001);

(statearr_16193_18701[(1)] = (29));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (19))){
var inst_15910 = (state_16023[(7)]);
var inst_15914 = cljs.core.chunk_first(inst_15910);
var inst_15915 = cljs.core.chunk_rest(inst_15910);
var inst_15916 = cljs.core.count(inst_15914);
var inst_15885 = inst_15915;
var inst_15886 = inst_15914;
var inst_15887 = inst_15916;
var inst_15888 = (0);
var state_16023__$1 = (function (){var statearr_16196 = state_16023;
(statearr_16196[(14)] = inst_15885);

(statearr_16196[(15)] = inst_15886);

(statearr_16196[(16)] = inst_15887);

(statearr_16196[(17)] = inst_15888);

return statearr_16196;
})();
var statearr_16198_18713 = state_16023__$1;
(statearr_16198_18713[(2)] = null);

(statearr_16198_18713[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (11))){
var inst_15885 = (state_16023[(14)]);
var inst_15910 = (state_16023[(7)]);
var inst_15910__$1 = cljs.core.seq(inst_15885);
var state_16023__$1 = (function (){var statearr_16202 = state_16023;
(statearr_16202[(7)] = inst_15910__$1);

return statearr_16202;
})();
if(inst_15910__$1){
var statearr_16203_18724 = state_16023__$1;
(statearr_16203_18724[(1)] = (16));

} else {
var statearr_16204_18726 = state_16023__$1;
(statearr_16204_18726[(1)] = (17));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (9))){
var inst_15943 = (state_16023[(2)]);
var state_16023__$1 = state_16023;
var statearr_16206_18729 = state_16023__$1;
(statearr_16206_18729[(2)] = inst_15943);

(statearr_16206_18729[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (5))){
var inst_15882 = cljs.core.deref(cs);
var inst_15883 = cljs.core.seq(inst_15882);
var inst_15885 = inst_15883;
var inst_15886 = null;
var inst_15887 = (0);
var inst_15888 = (0);
var state_16023__$1 = (function (){var statearr_16208 = state_16023;
(statearr_16208[(14)] = inst_15885);

(statearr_16208[(15)] = inst_15886);

(statearr_16208[(16)] = inst_15887);

(statearr_16208[(17)] = inst_15888);

return statearr_16208;
})();
var statearr_16215_18736 = state_16023__$1;
(statearr_16215_18736[(2)] = null);

(statearr_16215_18736[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (14))){
var state_16023__$1 = state_16023;
var statearr_16219_18737 = state_16023__$1;
(statearr_16219_18737[(2)] = null);

(statearr_16219_18737[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (45))){
var inst_16009 = (state_16023[(2)]);
var state_16023__$1 = state_16023;
var statearr_16230_18738 = state_16023__$1;
(statearr_16230_18738[(2)] = inst_16009);

(statearr_16230_18738[(1)] = (44));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (26))){
var inst_15947 = (state_16023[(27)]);
var inst_16005 = (state_16023[(2)]);
var inst_16006 = cljs.core.seq(inst_15947);
var state_16023__$1 = (function (){var statearr_16231 = state_16023;
(statearr_16231[(29)] = inst_16005);

return statearr_16231;
})();
if(inst_16006){
var statearr_16232_18741 = state_16023__$1;
(statearr_16232_18741[(1)] = (42));

} else {
var statearr_16233_18742 = state_16023__$1;
(statearr_16233_18742[(1)] = (43));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (16))){
var inst_15910 = (state_16023[(7)]);
var inst_15912 = cljs.core.chunked_seq_QMARK_(inst_15910);
var state_16023__$1 = state_16023;
if(inst_15912){
var statearr_16245_18747 = state_16023__$1;
(statearr_16245_18747[(1)] = (19));

} else {
var statearr_16246_18748 = state_16023__$1;
(statearr_16246_18748[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (38))){
var inst_15998 = (state_16023[(2)]);
var state_16023__$1 = state_16023;
var statearr_16248_18749 = state_16023__$1;
(statearr_16248_18749[(2)] = inst_15998);

(statearr_16248_18749[(1)] = (35));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (30))){
var state_16023__$1 = state_16023;
var statearr_16255_18750 = state_16023__$1;
(statearr_16255_18750[(2)] = null);

(statearr_16255_18750[(1)] = (32));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (10))){
var inst_15886 = (state_16023[(15)]);
var inst_15888 = (state_16023[(17)]);
var inst_15897 = cljs.core._nth(inst_15886,inst_15888);
var inst_15899 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_15897,(0),null);
var inst_15901 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_15897,(1),null);
var state_16023__$1 = (function (){var statearr_16262 = state_16023;
(statearr_16262[(24)] = inst_15899);

return statearr_16262;
})();
if(cljs.core.truth_(inst_15901)){
var statearr_16264_18751 = state_16023__$1;
(statearr_16264_18751[(1)] = (13));

} else {
var statearr_16268_18752 = state_16023__$1;
(statearr_16268_18752[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (18))){
var inst_15939 = (state_16023[(2)]);
var state_16023__$1 = state_16023;
var statearr_16270_18753 = state_16023__$1;
(statearr_16270_18753[(2)] = inst_15939);

(statearr_16270_18753[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (42))){
var state_16023__$1 = state_16023;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_16023__$1,(45),dchan);
} else {
if((state_val_16025 === (37))){
var inst_15976 = (state_16023[(23)]);
var inst_15987 = (state_16023[(22)]);
var inst_15875 = (state_16023[(12)]);
var inst_15987__$1 = cljs.core.first(inst_15976);
var inst_15988 = cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$3(inst_15987__$1,inst_15875,done);
var state_16023__$1 = (function (){var statearr_16273 = state_16023;
(statearr_16273[(22)] = inst_15987__$1);

return statearr_16273;
})();
if(cljs.core.truth_(inst_15988)){
var statearr_16277_18756 = state_16023__$1;
(statearr_16277_18756[(1)] = (39));

} else {
var statearr_16278_18758 = state_16023__$1;
(statearr_16278_18758[(1)] = (40));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16025 === (8))){
var inst_15888 = (state_16023[(17)]);
var inst_15887 = (state_16023[(16)]);
var inst_15891 = (inst_15888 < inst_15887);
var inst_15892 = inst_15891;
var state_16023__$1 = state_16023;
if(cljs.core.truth_(inst_15892)){
var statearr_16280_18760 = state_16023__$1;
(statearr_16280_18760[(1)] = (10));

} else {
var statearr_16282_18761 = state_16023__$1;
(statearr_16282_18761[(1)] = (11));

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
var cljs$core$async$mult_$_state_machine__14162__auto__ = null;
var cljs$core$async$mult_$_state_machine__14162__auto____0 = (function (){
var statearr_16287 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_16287[(0)] = cljs$core$async$mult_$_state_machine__14162__auto__);

(statearr_16287[(1)] = (1));

return statearr_16287;
});
var cljs$core$async$mult_$_state_machine__14162__auto____1 = (function (state_16023){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_16023);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e16290){var ex__14165__auto__ = e16290;
var statearr_16292_18763 = state_16023;
(statearr_16292_18763[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_16023[(4)]))){
var statearr_16293_18764 = state_16023;
(statearr_16293_18764[(1)] = cljs.core.first((state_16023[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18765 = state_16023;
state_16023 = G__18765;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$mult_$_state_machine__14162__auto__ = function(state_16023){
switch(arguments.length){
case 0:
return cljs$core$async$mult_$_state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$mult_$_state_machine__14162__auto____1.call(this,state_16023);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$mult_$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mult_$_state_machine__14162__auto____0;
cljs$core$async$mult_$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mult_$_state_machine__14162__auto____1;
return cljs$core$async$mult_$_state_machine__14162__auto__;
})()
})();
var state__14429__auto__ = (function (){var statearr_16295 = f__14428__auto__();
(statearr_16295[(6)] = c__14427__auto___18535);

return statearr_16295;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
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
var G__16304 = arguments.length;
switch (G__16304) {
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

var cljs$core$async$Mix$admix_STAR_$dyn_18769 = (function (m,ch){
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
return cljs$core$async$Mix$admix_STAR_$dyn_18769(m,ch);
}
});

var cljs$core$async$Mix$unmix_STAR_$dyn_18771 = (function (m,ch){
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
return cljs$core$async$Mix$unmix_STAR_$dyn_18771(m,ch);
}
});

var cljs$core$async$Mix$unmix_all_STAR_$dyn_18778 = (function (m){
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
return cljs$core$async$Mix$unmix_all_STAR_$dyn_18778(m);
}
});

var cljs$core$async$Mix$toggle_STAR_$dyn_18780 = (function (m,state_map){
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
return cljs$core$async$Mix$toggle_STAR_$dyn_18780(m,state_map);
}
});

var cljs$core$async$Mix$solo_mode_STAR_$dyn_18785 = (function (m,mode){
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
return cljs$core$async$Mix$solo_mode_STAR_$dyn_18785(m,mode);
}
});

cljs.core.async.ioc_alts_BANG_ = (function cljs$core$async$ioc_alts_BANG_(var_args){
var args__5882__auto__ = [];
var len__5876__auto___18787 = arguments.length;
var i__5877__auto___18788 = (0);
while(true){
if((i__5877__auto___18788 < len__5876__auto___18787)){
args__5882__auto__.push((arguments[i__5877__auto___18788]));

var G__18789 = (i__5877__auto___18788 + (1));
i__5877__auto___18788 = G__18789;
continue;
} else {
}
break;
}

var argseq__5883__auto__ = ((((3) < args__5882__auto__.length))?(new cljs.core.IndexedSeq(args__5882__auto__.slice((3)),(0),null)):null);
return cljs.core.async.ioc_alts_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__5883__auto__);
});

(cljs.core.async.ioc_alts_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (state,cont_block,ports,p__16373){
var map__16378 = p__16373;
var map__16378__$1 = cljs.core.__destructure_map(map__16378);
var opts = map__16378__$1;
var statearr_16380_18797 = state;
(statearr_16380_18797[(1)] = cont_block);


var temp__5823__auto__ = cljs.core.async.do_alts((function (val){
var statearr_16381_18798 = state;
(statearr_16381_18798[(2)] = val);


return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state);
}),ports,opts);
if(cljs.core.truth_(temp__5823__auto__)){
var cb = temp__5823__auto__;
var statearr_16382_18799 = state;
(statearr_16382_18799[(2)] = cljs.core.deref(cb));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}));

(cljs.core.async.ioc_alts_BANG_.cljs$lang$maxFixedArity = (3));

/** @this {Function} */
(cljs.core.async.ioc_alts_BANG_.cljs$lang$applyTo = (function (seq16362){
var G__16363 = cljs.core.first(seq16362);
var seq16362__$1 = cljs.core.next(seq16362);
var G__16364 = cljs.core.first(seq16362__$1);
var seq16362__$2 = cljs.core.next(seq16362__$1);
var G__16365 = cljs.core.first(seq16362__$2);
var seq16362__$3 = cljs.core.next(seq16362__$2);
var self__5861__auto__ = this;
return self__5861__auto__.cljs$core$IFn$_invoke$arity$variadic(G__16363,G__16364,G__16365,seq16362__$3);
}));


/**
* @constructor
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.async.Mix}
 * @implements {cljs.core.async.Mux}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async16437 = (function (change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,meta16438){
this.change = change;
this.solo_mode = solo_mode;
this.pick = pick;
this.cs = cs;
this.calc_state = calc_state;
this.out = out;
this.changed = changed;
this.solo_modes = solo_modes;
this.attrs = attrs;
this.meta16438 = meta16438;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async16437.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_16439,meta16438__$1){
var self__ = this;
var _16439__$1 = this;
return (new cljs.core.async.t_cljs$core$async16437(self__.change,self__.solo_mode,self__.pick,self__.cs,self__.calc_state,self__.out,self__.changed,self__.solo_modes,self__.attrs,meta16438__$1));
}));

(cljs.core.async.t_cljs$core$async16437.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_16439){
var self__ = this;
var _16439__$1 = this;
return self__.meta16438;
}));

(cljs.core.async.t_cljs$core$async16437.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16437.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.out;
}));

(cljs.core.async.t_cljs$core$async16437.prototype.cljs$core$async$Mix$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16437.prototype.cljs$core$async$Mix$admix_STAR_$arity$2 = (function (_,ch){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(self__.cs,cljs.core.assoc,ch,cljs.core.PersistentArrayMap.EMPTY);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async16437.prototype.cljs$core$async$Mix$unmix_STAR_$arity$2 = (function (_,ch){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(self__.cs,cljs.core.dissoc,ch);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async16437.prototype.cljs$core$async$Mix$unmix_all_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_(self__.cs,cljs.core.PersistentArrayMap.EMPTY);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async16437.prototype.cljs$core$async$Mix$toggle_STAR_$arity$2 = (function (_,state_map){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(self__.cs,cljs.core.partial.cljs$core$IFn$_invoke$arity$2(cljs.core.merge_with,cljs.core.merge),state_map);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async16437.prototype.cljs$core$async$Mix$solo_mode_STAR_$arity$2 = (function (_,mode){
var self__ = this;
var ___$1 = this;
if(cljs.core.truth_((self__.solo_modes.cljs$core$IFn$_invoke$arity$1 ? self__.solo_modes.cljs$core$IFn$_invoke$arity$1(mode) : self__.solo_modes.call(null,mode)))){
} else {
throw (new Error((""+"Assert failed: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1((""+"mode must be one of: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(self__.solo_modes)))+"\n"+"(solo-modes mode)")));
}

cljs.core.reset_BANG_(self__.solo_mode,mode);

return (self__.changed.cljs$core$IFn$_invoke$arity$0 ? self__.changed.cljs$core$IFn$_invoke$arity$0() : self__.changed.call(null));
}));

(cljs.core.async.t_cljs$core$async16437.getBasis = (function (){
return new cljs.core.PersistentVector(null, 10, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"change","change",477485025,null),new cljs.core.Symbol(null,"solo-mode","solo-mode",2031788074,null),new cljs.core.Symbol(null,"pick","pick",1300068175,null),new cljs.core.Symbol(null,"cs","cs",-117024463,null),new cljs.core.Symbol(null,"calc-state","calc-state",-349968968,null),new cljs.core.Symbol(null,"out","out",729986010,null),new cljs.core.Symbol(null,"changed","changed",-2083710852,null),new cljs.core.Symbol(null,"solo-modes","solo-modes",882180540,null),new cljs.core.Symbol(null,"attrs","attrs",-450137186,null),new cljs.core.Symbol(null,"meta16438","meta16438",1857156467,null)], null);
}));

(cljs.core.async.t_cljs$core$async16437.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async16437.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async16437");

(cljs.core.async.t_cljs$core$async16437.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async16437");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async16437.
 */
cljs.core.async.__GT_t_cljs$core$async16437 = (function cljs$core$async$__GT_t_cljs$core$async16437(change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,meta16438){
return (new cljs.core.async.t_cljs$core$async16437(change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,meta16438));
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
var m = (new cljs.core.async.t_cljs$core$async16437(change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,cljs.core.PersistentArrayMap.EMPTY));
var c__14427__auto___18823 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = (function (state_16519){
var state_val_16520 = (state_16519[(1)]);
if((state_val_16520 === (7))){
var inst_16475 = (state_16519[(2)]);
var state_16519__$1 = state_16519;
if(cljs.core.truth_(inst_16475)){
var statearr_16521_18824 = state_16519__$1;
(statearr_16521_18824[(1)] = (8));

} else {
var statearr_16522_18825 = state_16519__$1;
(statearr_16522_18825[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (20))){
var inst_16466 = (state_16519[(7)]);
var state_16519__$1 = state_16519;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_16519__$1,(23),out,inst_16466);
} else {
if((state_val_16520 === (1))){
var inst_16449 = calc_state();
var inst_16450 = cljs.core.__destructure_map(inst_16449);
var inst_16451 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_16450,new cljs.core.Keyword(null,"solos","solos",1441458643));
var inst_16452 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_16450,new cljs.core.Keyword(null,"mutes","mutes",1068806309));
var inst_16453 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_16450,new cljs.core.Keyword(null,"reads","reads",-1215067361));
var inst_16454 = inst_16449;
var state_16519__$1 = (function (){var statearr_16525 = state_16519;
(statearr_16525[(8)] = inst_16451);

(statearr_16525[(9)] = inst_16452);

(statearr_16525[(10)] = inst_16453);

(statearr_16525[(11)] = inst_16454);

return statearr_16525;
})();
var statearr_16528_18831 = state_16519__$1;
(statearr_16528_18831[(2)] = null);

(statearr_16528_18831[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (24))){
var inst_16457 = (state_16519[(12)]);
var inst_16454 = inst_16457;
var state_16519__$1 = (function (){var statearr_16529 = state_16519;
(statearr_16529[(11)] = inst_16454);

return statearr_16529;
})();
var statearr_16530_18837 = state_16519__$1;
(statearr_16530_18837[(2)] = null);

(statearr_16530_18837[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (4))){
var inst_16466 = (state_16519[(7)]);
var inst_16470 = (state_16519[(13)]);
var inst_16465 = (state_16519[(2)]);
var inst_16466__$1 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_16465,(0),null);
var inst_16469 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_16465,(1),null);
var inst_16470__$1 = (inst_16466__$1 == null);
var state_16519__$1 = (function (){var statearr_16531 = state_16519;
(statearr_16531[(7)] = inst_16466__$1);

(statearr_16531[(14)] = inst_16469);

(statearr_16531[(13)] = inst_16470__$1);

return statearr_16531;
})();
if(cljs.core.truth_(inst_16470__$1)){
var statearr_16532_18839 = state_16519__$1;
(statearr_16532_18839[(1)] = (5));

} else {
var statearr_16533_18840 = state_16519__$1;
(statearr_16533_18840[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (15))){
var inst_16458 = (state_16519[(15)]);
var inst_16489 = (state_16519[(16)]);
var inst_16489__$1 = cljs.core.empty_QMARK_(inst_16458);
var state_16519__$1 = (function (){var statearr_16534 = state_16519;
(statearr_16534[(16)] = inst_16489__$1);

return statearr_16534;
})();
if(inst_16489__$1){
var statearr_16535_18842 = state_16519__$1;
(statearr_16535_18842[(1)] = (17));

} else {
var statearr_16536_18843 = state_16519__$1;
(statearr_16536_18843[(1)] = (18));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (21))){
var inst_16457 = (state_16519[(12)]);
var inst_16454 = inst_16457;
var state_16519__$1 = (function (){var statearr_16537 = state_16519;
(statearr_16537[(11)] = inst_16454);

return statearr_16537;
})();
var statearr_16541_18848 = state_16519__$1;
(statearr_16541_18848[(2)] = null);

(statearr_16541_18848[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (13))){
var inst_16482 = (state_16519[(2)]);
var inst_16483 = calc_state();
var inst_16454 = inst_16483;
var state_16519__$1 = (function (){var statearr_16544 = state_16519;
(statearr_16544[(17)] = inst_16482);

(statearr_16544[(11)] = inst_16454);

return statearr_16544;
})();
var statearr_16546_18861 = state_16519__$1;
(statearr_16546_18861[(2)] = null);

(statearr_16546_18861[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (22))){
var inst_16513 = (state_16519[(2)]);
var state_16519__$1 = state_16519;
var statearr_16547_18865 = state_16519__$1;
(statearr_16547_18865[(2)] = inst_16513);

(statearr_16547_18865[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (6))){
var inst_16469 = (state_16519[(14)]);
var inst_16473 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(inst_16469,change);
var state_16519__$1 = state_16519;
var statearr_16548_18866 = state_16519__$1;
(statearr_16548_18866[(2)] = inst_16473);

(statearr_16548_18866[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (25))){
var state_16519__$1 = state_16519;
var statearr_16549_18867 = state_16519__$1;
(statearr_16549_18867[(2)] = null);

(statearr_16549_18867[(1)] = (26));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (17))){
var inst_16459 = (state_16519[(18)]);
var inst_16469 = (state_16519[(14)]);
var inst_16491 = (inst_16459.cljs$core$IFn$_invoke$arity$1 ? inst_16459.cljs$core$IFn$_invoke$arity$1(inst_16469) : inst_16459.call(null,inst_16469));
var inst_16492 = cljs.core.not(inst_16491);
var state_16519__$1 = state_16519;
var statearr_16554_18868 = state_16519__$1;
(statearr_16554_18868[(2)] = inst_16492);

(statearr_16554_18868[(1)] = (19));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (3))){
var inst_16517 = (state_16519[(2)]);
var state_16519__$1 = state_16519;
return cljs.core.async.impl.ioc_helpers.return_chan(state_16519__$1,inst_16517);
} else {
if((state_val_16520 === (12))){
var state_16519__$1 = state_16519;
var statearr_16559_18869 = state_16519__$1;
(statearr_16559_18869[(2)] = null);

(statearr_16559_18869[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (2))){
var inst_16454 = (state_16519[(11)]);
var inst_16457 = (state_16519[(12)]);
var inst_16457__$1 = cljs.core.__destructure_map(inst_16454);
var inst_16458 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_16457__$1,new cljs.core.Keyword(null,"solos","solos",1441458643));
var inst_16459 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_16457__$1,new cljs.core.Keyword(null,"mutes","mutes",1068806309));
var inst_16460 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_16457__$1,new cljs.core.Keyword(null,"reads","reads",-1215067361));
var state_16519__$1 = (function (){var statearr_16560 = state_16519;
(statearr_16560[(12)] = inst_16457__$1);

(statearr_16560[(15)] = inst_16458);

(statearr_16560[(18)] = inst_16459);

return statearr_16560;
})();
return cljs.core.async.ioc_alts_BANG_(state_16519__$1,(4),inst_16460);
} else {
if((state_val_16520 === (23))){
var inst_16502 = (state_16519[(2)]);
var state_16519__$1 = state_16519;
if(cljs.core.truth_(inst_16502)){
var statearr_16567_18873 = state_16519__$1;
(statearr_16567_18873[(1)] = (24));

} else {
var statearr_16568_18874 = state_16519__$1;
(statearr_16568_18874[(1)] = (25));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (19))){
var inst_16495 = (state_16519[(2)]);
var state_16519__$1 = state_16519;
var statearr_16569_18875 = state_16519__$1;
(statearr_16569_18875[(2)] = inst_16495);

(statearr_16569_18875[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (11))){
var inst_16469 = (state_16519[(14)]);
var inst_16479 = cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(cs,cljs.core.dissoc,inst_16469);
var state_16519__$1 = state_16519;
var statearr_16570_18876 = state_16519__$1;
(statearr_16570_18876[(2)] = inst_16479);

(statearr_16570_18876[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (9))){
var inst_16458 = (state_16519[(15)]);
var inst_16469 = (state_16519[(14)]);
var inst_16486 = (state_16519[(19)]);
var inst_16486__$1 = (inst_16458.cljs$core$IFn$_invoke$arity$1 ? inst_16458.cljs$core$IFn$_invoke$arity$1(inst_16469) : inst_16458.call(null,inst_16469));
var state_16519__$1 = (function (){var statearr_16571 = state_16519;
(statearr_16571[(19)] = inst_16486__$1);

return statearr_16571;
})();
if(cljs.core.truth_(inst_16486__$1)){
var statearr_16572_18880 = state_16519__$1;
(statearr_16572_18880[(1)] = (14));

} else {
var statearr_16573_18881 = state_16519__$1;
(statearr_16573_18881[(1)] = (15));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (5))){
var inst_16470 = (state_16519[(13)]);
var state_16519__$1 = state_16519;
var statearr_16574_18882 = state_16519__$1;
(statearr_16574_18882[(2)] = inst_16470);

(statearr_16574_18882[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (14))){
var inst_16486 = (state_16519[(19)]);
var state_16519__$1 = state_16519;
var statearr_16575_18883 = state_16519__$1;
(statearr_16575_18883[(2)] = inst_16486);

(statearr_16575_18883[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (26))){
var inst_16507 = (state_16519[(2)]);
var state_16519__$1 = state_16519;
var statearr_16576_18884 = state_16519__$1;
(statearr_16576_18884[(2)] = inst_16507);

(statearr_16576_18884[(1)] = (22));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (16))){
var inst_16497 = (state_16519[(2)]);
var state_16519__$1 = state_16519;
if(cljs.core.truth_(inst_16497)){
var statearr_16577_18885 = state_16519__$1;
(statearr_16577_18885[(1)] = (20));

} else {
var statearr_16578_18886 = state_16519__$1;
(statearr_16578_18886[(1)] = (21));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (10))){
var inst_16515 = (state_16519[(2)]);
var state_16519__$1 = state_16519;
var statearr_16579_18887 = state_16519__$1;
(statearr_16579_18887[(2)] = inst_16515);

(statearr_16579_18887[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (18))){
var inst_16489 = (state_16519[(16)]);
var state_16519__$1 = state_16519;
var statearr_16580_18891 = state_16519__$1;
(statearr_16580_18891[(2)] = inst_16489);

(statearr_16580_18891[(1)] = (19));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16520 === (8))){
var inst_16466 = (state_16519[(7)]);
var inst_16477 = (inst_16466 == null);
var state_16519__$1 = state_16519;
if(cljs.core.truth_(inst_16477)){
var statearr_16581_18892 = state_16519__$1;
(statearr_16581_18892[(1)] = (11));

} else {
var statearr_16582_18893 = state_16519__$1;
(statearr_16582_18893[(1)] = (12));

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
var cljs$core$async$mix_$_state_machine__14162__auto__ = null;
var cljs$core$async$mix_$_state_machine__14162__auto____0 = (function (){
var statearr_16583 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_16583[(0)] = cljs$core$async$mix_$_state_machine__14162__auto__);

(statearr_16583[(1)] = (1));

return statearr_16583;
});
var cljs$core$async$mix_$_state_machine__14162__auto____1 = (function (state_16519){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_16519);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e16584){var ex__14165__auto__ = e16584;
var statearr_16585_18894 = state_16519;
(statearr_16585_18894[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_16519[(4)]))){
var statearr_16586_18895 = state_16519;
(statearr_16586_18895[(1)] = cljs.core.first((state_16519[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18896 = state_16519;
state_16519 = G__18896;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$mix_$_state_machine__14162__auto__ = function(state_16519){
switch(arguments.length){
case 0:
return cljs$core$async$mix_$_state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$mix_$_state_machine__14162__auto____1.call(this,state_16519);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$mix_$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mix_$_state_machine__14162__auto____0;
cljs$core$async$mix_$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mix_$_state_machine__14162__auto____1;
return cljs$core$async$mix_$_state_machine__14162__auto__;
})()
})();
var state__14429__auto__ = (function (){var statearr_16587 = f__14428__auto__();
(statearr_16587[(6)] = c__14427__auto___18823);

return statearr_16587;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
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

var cljs$core$async$Pub$sub_STAR_$dyn_18899 = (function (p,v,ch,close_QMARK_){
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
return cljs$core$async$Pub$sub_STAR_$dyn_18899(p,v,ch,close_QMARK_);
}
});

var cljs$core$async$Pub$unsub_STAR_$dyn_18903 = (function (p,v,ch){
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
return cljs$core$async$Pub$unsub_STAR_$dyn_18903(p,v,ch);
}
});

var cljs$core$async$Pub$unsub_all_STAR_$dyn_18905 = (function() {
var G__18906 = null;
var G__18906__1 = (function (p){
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
var G__18906__2 = (function (p,v){
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
G__18906 = function(p,v){
switch(arguments.length){
case 1:
return G__18906__1.call(this,p);
case 2:
return G__18906__2.call(this,p,v);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
G__18906.cljs$core$IFn$_invoke$arity$1 = G__18906__1;
G__18906.cljs$core$IFn$_invoke$arity$2 = G__18906__2;
return G__18906;
})()
;
cljs.core.async.unsub_all_STAR_ = (function cljs$core$async$unsub_all_STAR_(var_args){
var G__16633 = arguments.length;
switch (G__16633) {
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
return cljs$core$async$Pub$unsub_all_STAR_$dyn_18905(p);
}
}));

(cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$2 = (function (p,v){
if((((!((p == null)))) && ((!((p.cljs$core$async$Pub$unsub_all_STAR_$arity$2 == null)))))){
return p.cljs$core$async$Pub$unsub_all_STAR_$arity$2(p,v);
} else {
return cljs$core$async$Pub$unsub_all_STAR_$dyn_18905(p,v);
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
cljs.core.async.t_cljs$core$async16655 = (function (ch,topic_fn,buf_fn,mults,ensure_mult,meta16656){
this.ch = ch;
this.topic_fn = topic_fn;
this.buf_fn = buf_fn;
this.mults = mults;
this.ensure_mult = ensure_mult;
this.meta16656 = meta16656;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async16655.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_16657,meta16656__$1){
var self__ = this;
var _16657__$1 = this;
return (new cljs.core.async.t_cljs$core$async16655(self__.ch,self__.topic_fn,self__.buf_fn,self__.mults,self__.ensure_mult,meta16656__$1));
}));

(cljs.core.async.t_cljs$core$async16655.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_16657){
var self__ = this;
var _16657__$1 = this;
return self__.meta16656;
}));

(cljs.core.async.t_cljs$core$async16655.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16655.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.ch;
}));

(cljs.core.async.t_cljs$core$async16655.prototype.cljs$core$async$Pub$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async16655.prototype.cljs$core$async$Pub$sub_STAR_$arity$4 = (function (p,topic,ch__$1,close_QMARK_){
var self__ = this;
var p__$1 = this;
var m = (self__.ensure_mult.cljs$core$IFn$_invoke$arity$1 ? self__.ensure_mult.cljs$core$IFn$_invoke$arity$1(topic) : self__.ensure_mult.call(null,topic));
return cljs.core.async.tap.cljs$core$IFn$_invoke$arity$3(m,ch__$1,close_QMARK_);
}));

(cljs.core.async.t_cljs$core$async16655.prototype.cljs$core$async$Pub$unsub_STAR_$arity$3 = (function (p,topic,ch__$1){
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

(cljs.core.async.t_cljs$core$async16655.prototype.cljs$core$async$Pub$unsub_all_STAR_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.reset_BANG_(self__.mults,cljs.core.PersistentArrayMap.EMPTY);
}));

(cljs.core.async.t_cljs$core$async16655.prototype.cljs$core$async$Pub$unsub_all_STAR_$arity$2 = (function (_,topic){
var self__ = this;
var ___$1 = this;
return cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(self__.mults,cljs.core.dissoc,topic);
}));

(cljs.core.async.t_cljs$core$async16655.getBasis = (function (){
return new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"topic-fn","topic-fn",-862449736,null),new cljs.core.Symbol(null,"buf-fn","buf-fn",-1200281591,null),new cljs.core.Symbol(null,"mults","mults",-461114485,null),new cljs.core.Symbol(null,"ensure-mult","ensure-mult",1796584816,null),new cljs.core.Symbol(null,"meta16656","meta16656",730352065,null)], null);
}));

(cljs.core.async.t_cljs$core$async16655.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async16655.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async16655");

(cljs.core.async.t_cljs$core$async16655.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async16655");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async16655.
 */
cljs.core.async.__GT_t_cljs$core$async16655 = (function cljs$core$async$__GT_t_cljs$core$async16655(ch,topic_fn,buf_fn,mults,ensure_mult,meta16656){
return (new cljs.core.async.t_cljs$core$async16655(ch,topic_fn,buf_fn,mults,ensure_mult,meta16656));
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
var G__16652 = arguments.length;
switch (G__16652) {
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
return cljs.core.get.cljs$core$IFn$_invoke$arity$2(cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$2(mults,(function (p1__16643_SHARP_){
if(cljs.core.truth_((p1__16643_SHARP_.cljs$core$IFn$_invoke$arity$1 ? p1__16643_SHARP_.cljs$core$IFn$_invoke$arity$1(topic) : p1__16643_SHARP_.call(null,topic)))){
return p1__16643_SHARP_;
} else {
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(p1__16643_SHARP_,topic,cljs.core.async.mult(cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((buf_fn.cljs$core$IFn$_invoke$arity$1 ? buf_fn.cljs$core$IFn$_invoke$arity$1(topic) : buf_fn.call(null,topic)))));
}
})),topic);
}
});
var p = (new cljs.core.async.t_cljs$core$async16655(ch,topic_fn,buf_fn,mults,ensure_mult,cljs.core.PersistentArrayMap.EMPTY));
var c__14427__auto___18922 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = (function (state_16744){
var state_val_16745 = (state_16744[(1)]);
if((state_val_16745 === (7))){
var inst_16740 = (state_16744[(2)]);
var state_16744__$1 = state_16744;
var statearr_16746_18923 = state_16744__$1;
(statearr_16746_18923[(2)] = inst_16740);

(statearr_16746_18923[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (20))){
var state_16744__$1 = state_16744;
var statearr_16747_18924 = state_16744__$1;
(statearr_16747_18924[(2)] = null);

(statearr_16747_18924[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (1))){
var state_16744__$1 = state_16744;
var statearr_16748_18927 = state_16744__$1;
(statearr_16748_18927[(2)] = null);

(statearr_16748_18927[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (24))){
var inst_16723 = (state_16744[(7)]);
var inst_16732 = cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$3(mults,cljs.core.dissoc,inst_16723);
var state_16744__$1 = state_16744;
var statearr_16751_18928 = state_16744__$1;
(statearr_16751_18928[(2)] = inst_16732);

(statearr_16751_18928[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (4))){
var inst_16671 = (state_16744[(8)]);
var inst_16671__$1 = (state_16744[(2)]);
var inst_16672 = (inst_16671__$1 == null);
var state_16744__$1 = (function (){var statearr_16752 = state_16744;
(statearr_16752[(8)] = inst_16671__$1);

return statearr_16752;
})();
if(cljs.core.truth_(inst_16672)){
var statearr_16754_18933 = state_16744__$1;
(statearr_16754_18933[(1)] = (5));

} else {
var statearr_16755_18934 = state_16744__$1;
(statearr_16755_18934[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (15))){
var inst_16717 = (state_16744[(2)]);
var state_16744__$1 = state_16744;
var statearr_16760_18935 = state_16744__$1;
(statearr_16760_18935[(2)] = inst_16717);

(statearr_16760_18935[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (21))){
var inst_16737 = (state_16744[(2)]);
var state_16744__$1 = (function (){var statearr_16761 = state_16744;
(statearr_16761[(9)] = inst_16737);

return statearr_16761;
})();
var statearr_16762_18936 = state_16744__$1;
(statearr_16762_18936[(2)] = null);

(statearr_16762_18936[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (13))){
var inst_16695 = (state_16744[(10)]);
var inst_16699 = cljs.core.chunked_seq_QMARK_(inst_16695);
var state_16744__$1 = state_16744;
if(inst_16699){
var statearr_16764_18937 = state_16744__$1;
(statearr_16764_18937[(1)] = (16));

} else {
var statearr_16765_18938 = state_16744__$1;
(statearr_16765_18938[(1)] = (17));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (22))){
var inst_16729 = (state_16744[(2)]);
var state_16744__$1 = state_16744;
if(cljs.core.truth_(inst_16729)){
var statearr_16766_18939 = state_16744__$1;
(statearr_16766_18939[(1)] = (23));

} else {
var statearr_16767_18940 = state_16744__$1;
(statearr_16767_18940[(1)] = (24));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (6))){
var inst_16671 = (state_16744[(8)]);
var inst_16723 = (state_16744[(7)]);
var inst_16725 = (state_16744[(11)]);
var inst_16723__$1 = (topic_fn.cljs$core$IFn$_invoke$arity$1 ? topic_fn.cljs$core$IFn$_invoke$arity$1(inst_16671) : topic_fn.call(null,inst_16671));
var inst_16724 = cljs.core.deref(mults);
var inst_16725__$1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(inst_16724,inst_16723__$1);
var state_16744__$1 = (function (){var statearr_16773 = state_16744;
(statearr_16773[(7)] = inst_16723__$1);

(statearr_16773[(11)] = inst_16725__$1);

return statearr_16773;
})();
if(cljs.core.truth_(inst_16725__$1)){
var statearr_16775_18941 = state_16744__$1;
(statearr_16775_18941[(1)] = (19));

} else {
var statearr_16776_18945 = state_16744__$1;
(statearr_16776_18945[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (25))){
var inst_16734 = (state_16744[(2)]);
var state_16744__$1 = state_16744;
var statearr_16778_18947 = state_16744__$1;
(statearr_16778_18947[(2)] = inst_16734);

(statearr_16778_18947[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (17))){
var inst_16695 = (state_16744[(10)]);
var inst_16708 = cljs.core.first(inst_16695);
var inst_16709 = cljs.core.async.muxch_STAR_(inst_16708);
var inst_16710 = cljs.core.async.close_BANG_(inst_16709);
var inst_16711 = cljs.core.next(inst_16695);
var inst_16681 = inst_16711;
var inst_16682 = null;
var inst_16683 = (0);
var inst_16684 = (0);
var state_16744__$1 = (function (){var statearr_16781 = state_16744;
(statearr_16781[(12)] = inst_16710);

(statearr_16781[(13)] = inst_16681);

(statearr_16781[(14)] = inst_16682);

(statearr_16781[(15)] = inst_16683);

(statearr_16781[(16)] = inst_16684);

return statearr_16781;
})();
var statearr_16787_18948 = state_16744__$1;
(statearr_16787_18948[(2)] = null);

(statearr_16787_18948[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (3))){
var inst_16742 = (state_16744[(2)]);
var state_16744__$1 = state_16744;
return cljs.core.async.impl.ioc_helpers.return_chan(state_16744__$1,inst_16742);
} else {
if((state_val_16745 === (12))){
var inst_16719 = (state_16744[(2)]);
var state_16744__$1 = state_16744;
var statearr_16790_18952 = state_16744__$1;
(statearr_16790_18952[(2)] = inst_16719);

(statearr_16790_18952[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (2))){
var state_16744__$1 = state_16744;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_16744__$1,(4),ch);
} else {
if((state_val_16745 === (23))){
var state_16744__$1 = state_16744;
var statearr_16795_18953 = state_16744__$1;
(statearr_16795_18953[(2)] = null);

(statearr_16795_18953[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (19))){
var inst_16725 = (state_16744[(11)]);
var inst_16671 = (state_16744[(8)]);
var inst_16727 = cljs.core.async.muxch_STAR_(inst_16725);
var state_16744__$1 = state_16744;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_16744__$1,(22),inst_16727,inst_16671);
} else {
if((state_val_16745 === (11))){
var inst_16681 = (state_16744[(13)]);
var inst_16695 = (state_16744[(10)]);
var inst_16695__$1 = cljs.core.seq(inst_16681);
var state_16744__$1 = (function (){var statearr_16796 = state_16744;
(statearr_16796[(10)] = inst_16695__$1);

return statearr_16796;
})();
if(inst_16695__$1){
var statearr_16797_18956 = state_16744__$1;
(statearr_16797_18956[(1)] = (13));

} else {
var statearr_16798_18957 = state_16744__$1;
(statearr_16798_18957[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (9))){
var inst_16721 = (state_16744[(2)]);
var state_16744__$1 = state_16744;
var statearr_16799_18958 = state_16744__$1;
(statearr_16799_18958[(2)] = inst_16721);

(statearr_16799_18958[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (5))){
var inst_16678 = cljs.core.deref(mults);
var inst_16679 = cljs.core.vals(inst_16678);
var inst_16680 = cljs.core.seq(inst_16679);
var inst_16681 = inst_16680;
var inst_16682 = null;
var inst_16683 = (0);
var inst_16684 = (0);
var state_16744__$1 = (function (){var statearr_16804 = state_16744;
(statearr_16804[(13)] = inst_16681);

(statearr_16804[(14)] = inst_16682);

(statearr_16804[(15)] = inst_16683);

(statearr_16804[(16)] = inst_16684);

return statearr_16804;
})();
var statearr_16810_18962 = state_16744__$1;
(statearr_16810_18962[(2)] = null);

(statearr_16810_18962[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (14))){
var state_16744__$1 = state_16744;
var statearr_16814_18966 = state_16744__$1;
(statearr_16814_18966[(2)] = null);

(statearr_16814_18966[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (16))){
var inst_16695 = (state_16744[(10)]);
var inst_16702 = cljs.core.chunk_first(inst_16695);
var inst_16704 = cljs.core.chunk_rest(inst_16695);
var inst_16705 = cljs.core.count(inst_16702);
var inst_16681 = inst_16704;
var inst_16682 = inst_16702;
var inst_16683 = inst_16705;
var inst_16684 = (0);
var state_16744__$1 = (function (){var statearr_16815 = state_16744;
(statearr_16815[(13)] = inst_16681);

(statearr_16815[(14)] = inst_16682);

(statearr_16815[(15)] = inst_16683);

(statearr_16815[(16)] = inst_16684);

return statearr_16815;
})();
var statearr_16816_18971 = state_16744__$1;
(statearr_16816_18971[(2)] = null);

(statearr_16816_18971[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (10))){
var inst_16682 = (state_16744[(14)]);
var inst_16684 = (state_16744[(16)]);
var inst_16681 = (state_16744[(13)]);
var inst_16683 = (state_16744[(15)]);
var inst_16689 = cljs.core._nth(inst_16682,inst_16684);
var inst_16690 = cljs.core.async.muxch_STAR_(inst_16689);
var inst_16691 = cljs.core.async.close_BANG_(inst_16690);
var inst_16692 = (inst_16684 + (1));
var tmp16811 = inst_16682;
var tmp16812 = inst_16681;
var tmp16813 = inst_16683;
var inst_16681__$1 = tmp16812;
var inst_16682__$1 = tmp16811;
var inst_16683__$1 = tmp16813;
var inst_16684__$1 = inst_16692;
var state_16744__$1 = (function (){var statearr_16817 = state_16744;
(statearr_16817[(17)] = inst_16691);

(statearr_16817[(13)] = inst_16681__$1);

(statearr_16817[(14)] = inst_16682__$1);

(statearr_16817[(15)] = inst_16683__$1);

(statearr_16817[(16)] = inst_16684__$1);

return statearr_16817;
})();
var statearr_16818_18975 = state_16744__$1;
(statearr_16818_18975[(2)] = null);

(statearr_16818_18975[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (18))){
var inst_16714 = (state_16744[(2)]);
var state_16744__$1 = state_16744;
var statearr_16821_18976 = state_16744__$1;
(statearr_16821_18976[(2)] = inst_16714);

(statearr_16821_18976[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16745 === (8))){
var inst_16684 = (state_16744[(16)]);
var inst_16683 = (state_16744[(15)]);
var inst_16686 = (inst_16684 < inst_16683);
var inst_16687 = inst_16686;
var state_16744__$1 = state_16744;
if(cljs.core.truth_(inst_16687)){
var statearr_16822_18978 = state_16744__$1;
(statearr_16822_18978[(1)] = (10));

} else {
var statearr_16823_18979 = state_16744__$1;
(statearr_16823_18979[(1)] = (11));

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
var cljs$core$async$state_machine__14162__auto__ = null;
var cljs$core$async$state_machine__14162__auto____0 = (function (){
var statearr_16824 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_16824[(0)] = cljs$core$async$state_machine__14162__auto__);

(statearr_16824[(1)] = (1));

return statearr_16824;
});
var cljs$core$async$state_machine__14162__auto____1 = (function (state_16744){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_16744);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e16825){var ex__14165__auto__ = e16825;
var statearr_16826_18981 = state_16744;
(statearr_16826_18981[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_16744[(4)]))){
var statearr_16827_18982 = state_16744;
(statearr_16827_18982[(1)] = cljs.core.first((state_16744[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18983 = state_16744;
state_16744 = G__18983;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$state_machine__14162__auto__ = function(state_16744){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14162__auto____1.call(this,state_16744);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14162__auto____0;
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14162__auto____1;
return cljs$core$async$state_machine__14162__auto__;
})()
})();
var state__14429__auto__ = (function (){var statearr_16829 = f__14428__auto__();
(statearr_16829[(6)] = c__14427__auto___18922);

return statearr_16829;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
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
var G__16835 = arguments.length;
switch (G__16835) {
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
var G__16843 = arguments.length;
switch (G__16843) {
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
var G__16845 = arguments.length;
switch (G__16845) {
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
var c__14427__auto___18997 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = (function (state_16889){
var state_val_16890 = (state_16889[(1)]);
if((state_val_16890 === (7))){
var state_16889__$1 = state_16889;
var statearr_16891_18998 = state_16889__$1;
(statearr_16891_18998[(2)] = null);

(statearr_16891_18998[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16890 === (1))){
var state_16889__$1 = state_16889;
var statearr_16892_18999 = state_16889__$1;
(statearr_16892_18999[(2)] = null);

(statearr_16892_18999[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16890 === (4))){
var inst_16849 = (state_16889[(7)]);
var inst_16848 = (state_16889[(8)]);
var inst_16851 = (inst_16849 < inst_16848);
var state_16889__$1 = state_16889;
if(cljs.core.truth_(inst_16851)){
var statearr_16893_19000 = state_16889__$1;
(statearr_16893_19000[(1)] = (6));

} else {
var statearr_16894_19001 = state_16889__$1;
(statearr_16894_19001[(1)] = (7));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16890 === (15))){
var inst_16875 = (state_16889[(9)]);
var inst_16880 = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(f,inst_16875);
var state_16889__$1 = state_16889;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_16889__$1,(17),out,inst_16880);
} else {
if((state_val_16890 === (13))){
var inst_16875 = (state_16889[(9)]);
var inst_16875__$1 = (state_16889[(2)]);
var inst_16876 = cljs.core.some(cljs.core.nil_QMARK_,inst_16875__$1);
var state_16889__$1 = (function (){var statearr_16896 = state_16889;
(statearr_16896[(9)] = inst_16875__$1);

return statearr_16896;
})();
if(cljs.core.truth_(inst_16876)){
var statearr_16898_19002 = state_16889__$1;
(statearr_16898_19002[(1)] = (14));

} else {
var statearr_16899_19003 = state_16889__$1;
(statearr_16899_19003[(1)] = (15));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16890 === (6))){
var state_16889__$1 = state_16889;
var statearr_16900_19004 = state_16889__$1;
(statearr_16900_19004[(2)] = null);

(statearr_16900_19004[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16890 === (17))){
var inst_16882 = (state_16889[(2)]);
var state_16889__$1 = (function (){var statearr_16902 = state_16889;
(statearr_16902[(10)] = inst_16882);

return statearr_16902;
})();
var statearr_16903_19005 = state_16889__$1;
(statearr_16903_19005[(2)] = null);

(statearr_16903_19005[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16890 === (3))){
var inst_16887 = (state_16889[(2)]);
var state_16889__$1 = state_16889;
return cljs.core.async.impl.ioc_helpers.return_chan(state_16889__$1,inst_16887);
} else {
if((state_val_16890 === (12))){
var _ = (function (){var statearr_16904 = state_16889;
(statearr_16904[(4)] = cljs.core.rest((state_16889[(4)])));

return statearr_16904;
})();
var state_16889__$1 = state_16889;
var ex16901 = (state_16889__$1[(2)]);
var statearr_16905_19015 = state_16889__$1;
(statearr_16905_19015[(5)] = ex16901);


if((ex16901 instanceof Object)){
var statearr_16907_19016 = state_16889__$1;
(statearr_16907_19016[(1)] = (11));

(statearr_16907_19016[(5)] = null);

} else {
throw ex16901;

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16890 === (2))){
var inst_16847 = cljs.core.reset_BANG_(dctr,cnt);
var inst_16848 = cnt;
var inst_16849 = (0);
var state_16889__$1 = (function (){var statearr_16910 = state_16889;
(statearr_16910[(11)] = inst_16847);

(statearr_16910[(8)] = inst_16848);

(statearr_16910[(7)] = inst_16849);

return statearr_16910;
})();
var statearr_16911_19017 = state_16889__$1;
(statearr_16911_19017[(2)] = null);

(statearr_16911_19017[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16890 === (11))){
var inst_16853 = (state_16889[(2)]);
var inst_16854 = cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$2(dctr,cljs.core.dec);
var state_16889__$1 = (function (){var statearr_16912 = state_16889;
(statearr_16912[(12)] = inst_16853);

return statearr_16912;
})();
var statearr_16913_19022 = state_16889__$1;
(statearr_16913_19022[(2)] = inst_16854);

(statearr_16913_19022[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16890 === (9))){
var inst_16849 = (state_16889[(7)]);
var _ = (function (){var statearr_16914 = state_16889;
(statearr_16914[(4)] = cljs.core.cons((12),(state_16889[(4)])));

return statearr_16914;
})();
var inst_16860 = (chs__$1.cljs$core$IFn$_invoke$arity$1 ? chs__$1.cljs$core$IFn$_invoke$arity$1(inst_16849) : chs__$1.call(null,inst_16849));
var inst_16861 = (done.cljs$core$IFn$_invoke$arity$1 ? done.cljs$core$IFn$_invoke$arity$1(inst_16849) : done.call(null,inst_16849));
var inst_16862 = cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$2(inst_16860,inst_16861);
var ___$1 = (function (){var statearr_16915 = state_16889;
(statearr_16915[(4)] = cljs.core.rest((state_16889[(4)])));

return statearr_16915;
})();
var state_16889__$1 = state_16889;
var statearr_16916_19025 = state_16889__$1;
(statearr_16916_19025[(2)] = inst_16862);

(statearr_16916_19025[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16890 === (5))){
var inst_16873 = (state_16889[(2)]);
var state_16889__$1 = (function (){var statearr_16917 = state_16889;
(statearr_16917[(13)] = inst_16873);

return statearr_16917;
})();
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_16889__$1,(13),dchan);
} else {
if((state_val_16890 === (14))){
var inst_16878 = cljs.core.async.close_BANG_(out);
var state_16889__$1 = state_16889;
var statearr_16918_19027 = state_16889__$1;
(statearr_16918_19027[(2)] = inst_16878);

(statearr_16918_19027[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16890 === (16))){
var inst_16885 = (state_16889[(2)]);
var state_16889__$1 = state_16889;
var statearr_16919_19028 = state_16889__$1;
(statearr_16919_19028[(2)] = inst_16885);

(statearr_16919_19028[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16890 === (10))){
var inst_16849 = (state_16889[(7)]);
var inst_16865 = (state_16889[(2)]);
var inst_16867 = (inst_16849 + (1));
var inst_16849__$1 = inst_16867;
var state_16889__$1 = (function (){var statearr_16923 = state_16889;
(statearr_16923[(14)] = inst_16865);

(statearr_16923[(7)] = inst_16849__$1);

return statearr_16923;
})();
var statearr_16924_19032 = state_16889__$1;
(statearr_16924_19032[(2)] = null);

(statearr_16924_19032[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_16890 === (8))){
var inst_16871 = (state_16889[(2)]);
var state_16889__$1 = state_16889;
var statearr_16925_19033 = state_16889__$1;
(statearr_16925_19033[(2)] = inst_16871);

(statearr_16925_19033[(1)] = (5));


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
var cljs$core$async$state_machine__14162__auto__ = null;
var cljs$core$async$state_machine__14162__auto____0 = (function (){
var statearr_16926 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_16926[(0)] = cljs$core$async$state_machine__14162__auto__);

(statearr_16926[(1)] = (1));

return statearr_16926;
});
var cljs$core$async$state_machine__14162__auto____1 = (function (state_16889){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_16889);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e16927){var ex__14165__auto__ = e16927;
var statearr_16928_19035 = state_16889;
(statearr_16928_19035[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_16889[(4)]))){
var statearr_16929_19036 = state_16889;
(statearr_16929_19036[(1)] = cljs.core.first((state_16889[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__19040 = state_16889;
state_16889 = G__19040;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$state_machine__14162__auto__ = function(state_16889){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14162__auto____1.call(this,state_16889);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14162__auto____0;
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14162__auto____1;
return cljs$core$async$state_machine__14162__auto__;
})()
})();
var state__14429__auto__ = (function (){var statearr_16934 = f__14428__auto__();
(statearr_16934[(6)] = c__14427__auto___18997);

return statearr_16934;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
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
var G__16937 = arguments.length;
switch (G__16937) {
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
var c__14427__auto___19055 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = (function (state_17018){
var state_val_17020 = (state_17018[(1)]);
if((state_val_17020 === (7))){
var inst_16972 = (state_17018[(7)]);
var inst_16974 = (state_17018[(8)]);
var inst_16972__$1 = (state_17018[(2)]);
var inst_16974__$1 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_16972__$1,(0),null);
var inst_16975 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(inst_16972__$1,(1),null);
var inst_16982 = (inst_16974__$1 == null);
var state_17018__$1 = (function (){var statearr_17027 = state_17018;
(statearr_17027[(7)] = inst_16972__$1);

(statearr_17027[(8)] = inst_16974__$1);

(statearr_17027[(9)] = inst_16975);

return statearr_17027;
})();
if(cljs.core.truth_(inst_16982)){
var statearr_17028_19071 = state_17018__$1;
(statearr_17028_19071[(1)] = (8));

} else {
var statearr_17029_19075 = state_17018__$1;
(statearr_17029_19075[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17020 === (1))){
var inst_16947 = cljs.core.vec(chs);
var inst_16948 = inst_16947;
var state_17018__$1 = (function (){var statearr_17030 = state_17018;
(statearr_17030[(10)] = inst_16948);

return statearr_17030;
})();
var statearr_17032_19079 = state_17018__$1;
(statearr_17032_19079[(2)] = null);

(statearr_17032_19079[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17020 === (4))){
var inst_16948 = (state_17018[(10)]);
var state_17018__$1 = state_17018;
return cljs.core.async.ioc_alts_BANG_(state_17018__$1,(7),inst_16948);
} else {
if((state_val_17020 === (6))){
var inst_16999 = (state_17018[(2)]);
var state_17018__$1 = state_17018;
var statearr_17036_19080 = state_17018__$1;
(statearr_17036_19080[(2)] = inst_16999);

(statearr_17036_19080[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17020 === (3))){
var inst_17001 = (state_17018[(2)]);
var state_17018__$1 = state_17018;
return cljs.core.async.impl.ioc_helpers.return_chan(state_17018__$1,inst_17001);
} else {
if((state_val_17020 === (2))){
var inst_16948 = (state_17018[(10)]);
var inst_16950 = cljs.core.count(inst_16948);
var inst_16951 = (inst_16950 > (0));
var state_17018__$1 = state_17018;
if(cljs.core.truth_(inst_16951)){
var statearr_17038_19086 = state_17018__$1;
(statearr_17038_19086[(1)] = (4));

} else {
var statearr_17040_19087 = state_17018__$1;
(statearr_17040_19087[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17020 === (11))){
var inst_16948 = (state_17018[(10)]);
var inst_16992 = (state_17018[(2)]);
var tmp17037 = inst_16948;
var inst_16948__$1 = tmp17037;
var state_17018__$1 = (function (){var statearr_17044 = state_17018;
(statearr_17044[(11)] = inst_16992);

(statearr_17044[(10)] = inst_16948__$1);

return statearr_17044;
})();
var statearr_17045_19089 = state_17018__$1;
(statearr_17045_19089[(2)] = null);

(statearr_17045_19089[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17020 === (9))){
var inst_16974 = (state_17018[(8)]);
var state_17018__$1 = state_17018;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17018__$1,(11),out,inst_16974);
} else {
if((state_val_17020 === (5))){
var inst_16997 = cljs.core.async.close_BANG_(out);
var state_17018__$1 = state_17018;
var statearr_17048_19092 = state_17018__$1;
(statearr_17048_19092[(2)] = inst_16997);

(statearr_17048_19092[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17020 === (10))){
var inst_16995 = (state_17018[(2)]);
var state_17018__$1 = state_17018;
var statearr_17052_19093 = state_17018__$1;
(statearr_17052_19093[(2)] = inst_16995);

(statearr_17052_19093[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17020 === (8))){
var inst_16948 = (state_17018[(10)]);
var inst_16972 = (state_17018[(7)]);
var inst_16974 = (state_17018[(8)]);
var inst_16975 = (state_17018[(9)]);
var inst_16986 = (function (){var cs = inst_16948;
var vec__16961 = inst_16972;
var v = inst_16974;
var c = inst_16975;
return (function (p1__16935_SHARP_){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(c,p1__16935_SHARP_);
});
})();
var inst_16988 = cljs.core.filterv(inst_16986,inst_16948);
var inst_16948__$1 = inst_16988;
var state_17018__$1 = (function (){var statearr_17056 = state_17018;
(statearr_17056[(10)] = inst_16948__$1);

return statearr_17056;
})();
var statearr_17057_19104 = state_17018__$1;
(statearr_17057_19104[(2)] = null);

(statearr_17057_19104[(1)] = (2));


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
var cljs$core$async$state_machine__14162__auto__ = null;
var cljs$core$async$state_machine__14162__auto____0 = (function (){
var statearr_17058 = [null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_17058[(0)] = cljs$core$async$state_machine__14162__auto__);

(statearr_17058[(1)] = (1));

return statearr_17058;
});
var cljs$core$async$state_machine__14162__auto____1 = (function (state_17018){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_17018);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e17059){var ex__14165__auto__ = e17059;
var statearr_17060_19105 = state_17018;
(statearr_17060_19105[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_17018[(4)]))){
var statearr_17061_19106 = state_17018;
(statearr_17061_19106[(1)] = cljs.core.first((state_17018[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__19107 = state_17018;
state_17018 = G__19107;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$state_machine__14162__auto__ = function(state_17018){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14162__auto____1.call(this,state_17018);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14162__auto____0;
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14162__auto____1;
return cljs$core$async$state_machine__14162__auto__;
})()
})();
var state__14429__auto__ = (function (){var statearr_17062 = f__14428__auto__();
(statearr_17062[(6)] = c__14427__auto___19055);

return statearr_17062;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
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
var G__17064 = arguments.length;
switch (G__17064) {
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
var c__14427__auto___19112 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = (function (state_17096){
var state_val_17097 = (state_17096[(1)]);
if((state_val_17097 === (7))){
var inst_17070 = (state_17096[(7)]);
var inst_17070__$1 = (state_17096[(2)]);
var inst_17071 = (inst_17070__$1 == null);
var inst_17072 = cljs.core.not(inst_17071);
var state_17096__$1 = (function (){var statearr_17102 = state_17096;
(statearr_17102[(7)] = inst_17070__$1);

return statearr_17102;
})();
if(inst_17072){
var statearr_17103_19113 = state_17096__$1;
(statearr_17103_19113[(1)] = (8));

} else {
var statearr_17104_19114 = state_17096__$1;
(statearr_17104_19114[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17097 === (1))){
var inst_17065 = (0);
var state_17096__$1 = (function (){var statearr_17105 = state_17096;
(statearr_17105[(8)] = inst_17065);

return statearr_17105;
})();
var statearr_17108_19116 = state_17096__$1;
(statearr_17108_19116[(2)] = null);

(statearr_17108_19116[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17097 === (4))){
var state_17096__$1 = state_17096;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_17096__$1,(7),ch);
} else {
if((state_val_17097 === (6))){
var inst_17087 = (state_17096[(2)]);
var state_17096__$1 = state_17096;
var statearr_17109_19117 = state_17096__$1;
(statearr_17109_19117[(2)] = inst_17087);

(statearr_17109_19117[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17097 === (3))){
var inst_17089 = (state_17096[(2)]);
var inst_17090 = cljs.core.async.close_BANG_(out);
var state_17096__$1 = (function (){var statearr_17110 = state_17096;
(statearr_17110[(9)] = inst_17089);

return statearr_17110;
})();
return cljs.core.async.impl.ioc_helpers.return_chan(state_17096__$1,inst_17090);
} else {
if((state_val_17097 === (2))){
var inst_17065 = (state_17096[(8)]);
var inst_17067 = (inst_17065 < n);
var state_17096__$1 = state_17096;
if(cljs.core.truth_(inst_17067)){
var statearr_17111_19118 = state_17096__$1;
(statearr_17111_19118[(1)] = (4));

} else {
var statearr_17112_19119 = state_17096__$1;
(statearr_17112_19119[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17097 === (11))){
var inst_17065 = (state_17096[(8)]);
var inst_17079 = (state_17096[(2)]);
var inst_17080 = (inst_17065 + (1));
var inst_17065__$1 = inst_17080;
var state_17096__$1 = (function (){var statearr_17114 = state_17096;
(statearr_17114[(10)] = inst_17079);

(statearr_17114[(8)] = inst_17065__$1);

return statearr_17114;
})();
var statearr_17115_19120 = state_17096__$1;
(statearr_17115_19120[(2)] = null);

(statearr_17115_19120[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17097 === (9))){
var state_17096__$1 = state_17096;
var statearr_17116_19121 = state_17096__$1;
(statearr_17116_19121[(2)] = null);

(statearr_17116_19121[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17097 === (5))){
var state_17096__$1 = state_17096;
var statearr_17117_19126 = state_17096__$1;
(statearr_17117_19126[(2)] = null);

(statearr_17117_19126[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17097 === (10))){
var inst_17084 = (state_17096[(2)]);
var state_17096__$1 = state_17096;
var statearr_17118_19127 = state_17096__$1;
(statearr_17118_19127[(2)] = inst_17084);

(statearr_17118_19127[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17097 === (8))){
var inst_17070 = (state_17096[(7)]);
var state_17096__$1 = state_17096;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17096__$1,(11),out,inst_17070);
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
var cljs$core$async$state_machine__14162__auto__ = null;
var cljs$core$async$state_machine__14162__auto____0 = (function (){
var statearr_17129 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_17129[(0)] = cljs$core$async$state_machine__14162__auto__);

(statearr_17129[(1)] = (1));

return statearr_17129;
});
var cljs$core$async$state_machine__14162__auto____1 = (function (state_17096){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_17096);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e17132){var ex__14165__auto__ = e17132;
var statearr_17133_19140 = state_17096;
(statearr_17133_19140[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_17096[(4)]))){
var statearr_17137_19141 = state_17096;
(statearr_17137_19141[(1)] = cljs.core.first((state_17096[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__19144 = state_17096;
state_17096 = G__19144;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$state_machine__14162__auto__ = function(state_17096){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14162__auto____1.call(this,state_17096);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14162__auto____0;
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14162__auto____1;
return cljs$core$async$state_machine__14162__auto__;
})()
})();
var state__14429__auto__ = (function (){var statearr_17138 = f__14428__auto__();
(statearr_17138[(6)] = c__14427__auto___19112);

return statearr_17138;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
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
cljs.core.async.t_cljs$core$async17163 = (function (f,ch,meta17147,_,fn1,meta17164){
this.f = f;
this.ch = ch;
this.meta17147 = meta17147;
this._ = _;
this.fn1 = fn1;
this.meta17164 = meta17164;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async17163.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_17165,meta17164__$1){
var self__ = this;
var _17165__$1 = this;
return (new cljs.core.async.t_cljs$core$async17163(self__.f,self__.ch,self__.meta17147,self__._,self__.fn1,meta17164__$1));
}));

(cljs.core.async.t_cljs$core$async17163.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_17165){
var self__ = this;
var _17165__$1 = this;
return self__.meta17164;
}));

(cljs.core.async.t_cljs$core$async17163.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17163.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (___$1){
var self__ = this;
var ___$2 = this;
return cljs.core.async.impl.protocols.active_QMARK_(self__.fn1);
}));

(cljs.core.async.t_cljs$core$async17163.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (___$1){
var self__ = this;
var ___$2 = this;
return true;
}));

(cljs.core.async.t_cljs$core$async17163.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (___$1){
var self__ = this;
var ___$2 = this;
var f1 = cljs.core.async.impl.protocols.commit(self__.fn1);
return (function (p1__17144_SHARP_){
var G__17184 = (((p1__17144_SHARP_ == null))?null:(self__.f.cljs$core$IFn$_invoke$arity$1 ? self__.f.cljs$core$IFn$_invoke$arity$1(p1__17144_SHARP_) : self__.f.call(null,p1__17144_SHARP_)));
return (f1.cljs$core$IFn$_invoke$arity$1 ? f1.cljs$core$IFn$_invoke$arity$1(G__17184) : f1.call(null,G__17184));
});
}));

(cljs.core.async.t_cljs$core$async17163.getBasis = (function (){
return new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta17147","meta17147",910783522,null),cljs.core.with_meta(new cljs.core.Symbol(null,"_","_",-1201019570,null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Symbol("cljs.core.async","t_cljs$core$async17146","cljs.core.async/t_cljs$core$async17146",-2088858710,null)], null)),new cljs.core.Symbol(null,"fn1","fn1",895834444,null),new cljs.core.Symbol(null,"meta17164","meta17164",-1362604818,null)], null);
}));

(cljs.core.async.t_cljs$core$async17163.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async17163.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async17163");

(cljs.core.async.t_cljs$core$async17163.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async17163");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async17163.
 */
cljs.core.async.__GT_t_cljs$core$async17163 = (function cljs$core$async$__GT_t_cljs$core$async17163(f,ch,meta17147,_,fn1,meta17164){
return (new cljs.core.async.t_cljs$core$async17163(f,ch,meta17147,_,fn1,meta17164));
});



/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async17146 = (function (f,ch,meta17147){
this.f = f;
this.ch = ch;
this.meta17147 = meta17147;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async17146.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_17148,meta17147__$1){
var self__ = this;
var _17148__$1 = this;
return (new cljs.core.async.t_cljs$core$async17146(self__.f,self__.ch,meta17147__$1));
}));

(cljs.core.async.t_cljs$core$async17146.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_17148){
var self__ = this;
var _17148__$1 = this;
return self__.meta17147;
}));

(cljs.core.async.t_cljs$core$async17146.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17146.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async17146.prototype.cljs$core$async$impl$protocols$Channel$closed_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.closed_QMARK_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async17146.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17146.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
var ret = cljs.core.async.impl.protocols.take_BANG_(self__.ch,(new cljs.core.async.t_cljs$core$async17163(self__.f,self__.ch,self__.meta17147,___$1,fn1,cljs.core.PersistentArrayMap.EMPTY)));
if(cljs.core.truth_((function (){var and__5140__auto__ = ret;
if(cljs.core.truth_(and__5140__auto__)){
return (!((cljs.core.deref(ret) == null)));
} else {
return and__5140__auto__;
}
})())){
return cljs.core.async.impl.channels.box((function (){var G__17189 = cljs.core.deref(ret);
return (self__.f.cljs$core$IFn$_invoke$arity$1 ? self__.f.cljs$core$IFn$_invoke$arity$1(G__17189) : self__.f.call(null,G__17189));
})());
} else {
return ret;
}
}));

(cljs.core.async.t_cljs$core$async17146.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17146.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.put_BANG_(self__.ch,val,fn1);
}));

(cljs.core.async.t_cljs$core$async17146.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta17147","meta17147",910783522,null)], null);
}));

(cljs.core.async.t_cljs$core$async17146.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async17146.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async17146");

(cljs.core.async.t_cljs$core$async17146.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async17146");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async17146.
 */
cljs.core.async.__GT_t_cljs$core$async17146 = (function cljs$core$async$__GT_t_cljs$core$async17146(f,ch,meta17147){
return (new cljs.core.async.t_cljs$core$async17146(f,ch,meta17147));
});


/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.map_LT_ = (function cljs$core$async$map_LT_(f,ch){
return (new cljs.core.async.t_cljs$core$async17146(f,ch,cljs.core.PersistentArrayMap.EMPTY));
});

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async17196 = (function (f,ch,meta17197){
this.f = f;
this.ch = ch;
this.meta17197 = meta17197;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async17196.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_17198,meta17197__$1){
var self__ = this;
var _17198__$1 = this;
return (new cljs.core.async.t_cljs$core$async17196(self__.f,self__.ch,meta17197__$1));
}));

(cljs.core.async.t_cljs$core$async17196.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_17198){
var self__ = this;
var _17198__$1 = this;
return self__.meta17197;
}));

(cljs.core.async.t_cljs$core$async17196.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17196.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async17196.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17196.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.take_BANG_(self__.ch,fn1);
}));

(cljs.core.async.t_cljs$core$async17196.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17196.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.put_BANG_(self__.ch,(self__.f.cljs$core$IFn$_invoke$arity$1 ? self__.f.cljs$core$IFn$_invoke$arity$1(val) : self__.f.call(null,val)),fn1);
}));

(cljs.core.async.t_cljs$core$async17196.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta17197","meta17197",512835304,null)], null);
}));

(cljs.core.async.t_cljs$core$async17196.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async17196.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async17196");

(cljs.core.async.t_cljs$core$async17196.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async17196");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async17196.
 */
cljs.core.async.__GT_t_cljs$core$async17196 = (function cljs$core$async$__GT_t_cljs$core$async17196(f,ch,meta17197){
return (new cljs.core.async.t_cljs$core$async17196(f,ch,meta17197));
});


/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.map_GT_ = (function cljs$core$async$map_GT_(f,ch){
return (new cljs.core.async.t_cljs$core$async17196(f,ch,cljs.core.PersistentArrayMap.EMPTY));
});

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async17205 = (function (p,ch,meta17206){
this.p = p;
this.ch = ch;
this.meta17206 = meta17206;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
(cljs.core.async.t_cljs$core$async17205.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_17207,meta17206__$1){
var self__ = this;
var _17207__$1 = this;
return (new cljs.core.async.t_cljs$core$async17205(self__.p,self__.ch,meta17206__$1));
}));

(cljs.core.async.t_cljs$core$async17205.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_17207){
var self__ = this;
var _17207__$1 = this;
return self__.meta17206;
}));

(cljs.core.async.t_cljs$core$async17205.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17205.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async17205.prototype.cljs$core$async$impl$protocols$Channel$closed_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.closed_QMARK_(self__.ch);
}));

(cljs.core.async.t_cljs$core$async17205.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17205.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.take_BANG_(self__.ch,fn1);
}));

(cljs.core.async.t_cljs$core$async17205.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL);

(cljs.core.async.t_cljs$core$async17205.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
if(cljs.core.truth_((self__.p.cljs$core$IFn$_invoke$arity$1 ? self__.p.cljs$core$IFn$_invoke$arity$1(val) : self__.p.call(null,val)))){
return cljs.core.async.impl.protocols.put_BANG_(self__.ch,val,fn1);
} else {
return cljs.core.async.impl.channels.box(cljs.core.not(cljs.core.async.impl.protocols.closed_QMARK_(self__.ch)));
}
}));

(cljs.core.async.t_cljs$core$async17205.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"p","p",1791580836,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta17206","meta17206",1741429334,null)], null);
}));

(cljs.core.async.t_cljs$core$async17205.cljs$lang$type = true);

(cljs.core.async.t_cljs$core$async17205.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async17205");

(cljs.core.async.t_cljs$core$async17205.cljs$lang$ctorPrWriter = (function (this__5434__auto__,writer__5435__auto__,opt__5436__auto__){
return cljs.core._write(writer__5435__auto__,"cljs.core.async/t_cljs$core$async17205");
}));

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async17205.
 */
cljs.core.async.__GT_t_cljs$core$async17205 = (function cljs$core$async$__GT_t_cljs$core$async17205(p,ch,meta17206){
return (new cljs.core.async.t_cljs$core$async17205(p,ch,meta17206));
});


/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.filter_GT_ = (function cljs$core$async$filter_GT_(p,ch){
return (new cljs.core.async.t_cljs$core$async17205(p,ch,cljs.core.PersistentArrayMap.EMPTY));
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
var G__17248 = arguments.length;
switch (G__17248) {
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
var c__14427__auto___19185 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = (function (state_17279){
var state_val_17280 = (state_17279[(1)]);
if((state_val_17280 === (7))){
var inst_17272 = (state_17279[(2)]);
var state_17279__$1 = state_17279;
var statearr_17284_19187 = state_17279__$1;
(statearr_17284_19187[(2)] = inst_17272);

(statearr_17284_19187[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17280 === (1))){
var state_17279__$1 = state_17279;
var statearr_17285_19193 = state_17279__$1;
(statearr_17285_19193[(2)] = null);

(statearr_17285_19193[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17280 === (4))){
var inst_17254 = (state_17279[(7)]);
var inst_17254__$1 = (state_17279[(2)]);
var inst_17255 = (inst_17254__$1 == null);
var state_17279__$1 = (function (){var statearr_17293 = state_17279;
(statearr_17293[(7)] = inst_17254__$1);

return statearr_17293;
})();
if(cljs.core.truth_(inst_17255)){
var statearr_17301_19197 = state_17279__$1;
(statearr_17301_19197[(1)] = (5));

} else {
var statearr_17302_19198 = state_17279__$1;
(statearr_17302_19198[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17280 === (6))){
var inst_17254 = (state_17279[(7)]);
var inst_17259 = (p.cljs$core$IFn$_invoke$arity$1 ? p.cljs$core$IFn$_invoke$arity$1(inst_17254) : p.call(null,inst_17254));
var state_17279__$1 = state_17279;
if(cljs.core.truth_(inst_17259)){
var statearr_17308_19202 = state_17279__$1;
(statearr_17308_19202[(1)] = (8));

} else {
var statearr_17309_19203 = state_17279__$1;
(statearr_17309_19203[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17280 === (3))){
var inst_17277 = (state_17279[(2)]);
var state_17279__$1 = state_17279;
return cljs.core.async.impl.ioc_helpers.return_chan(state_17279__$1,inst_17277);
} else {
if((state_val_17280 === (2))){
var state_17279__$1 = state_17279;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_17279__$1,(4),ch);
} else {
if((state_val_17280 === (11))){
var inst_17266 = (state_17279[(2)]);
var state_17279__$1 = state_17279;
var statearr_17316_19205 = state_17279__$1;
(statearr_17316_19205[(2)] = inst_17266);

(statearr_17316_19205[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17280 === (9))){
var state_17279__$1 = state_17279;
var statearr_17317_19206 = state_17279__$1;
(statearr_17317_19206[(2)] = null);

(statearr_17317_19206[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17280 === (5))){
var inst_17257 = cljs.core.async.close_BANG_(out);
var state_17279__$1 = state_17279;
var statearr_17320_19207 = state_17279__$1;
(statearr_17320_19207[(2)] = inst_17257);

(statearr_17320_19207[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17280 === (10))){
var inst_17269 = (state_17279[(2)]);
var state_17279__$1 = (function (){var statearr_17321 = state_17279;
(statearr_17321[(8)] = inst_17269);

return statearr_17321;
})();
var statearr_17322_19208 = state_17279__$1;
(statearr_17322_19208[(2)] = null);

(statearr_17322_19208[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17280 === (8))){
var inst_17254 = (state_17279[(7)]);
var state_17279__$1 = state_17279;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17279__$1,(11),out,inst_17254);
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
var cljs$core$async$state_machine__14162__auto__ = null;
var cljs$core$async$state_machine__14162__auto____0 = (function (){
var statearr_17325 = [null,null,null,null,null,null,null,null,null];
(statearr_17325[(0)] = cljs$core$async$state_machine__14162__auto__);

(statearr_17325[(1)] = (1));

return statearr_17325;
});
var cljs$core$async$state_machine__14162__auto____1 = (function (state_17279){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_17279);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e17330){var ex__14165__auto__ = e17330;
var statearr_17331_19210 = state_17279;
(statearr_17331_19210[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_17279[(4)]))){
var statearr_17332_19211 = state_17279;
(statearr_17332_19211[(1)] = cljs.core.first((state_17279[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__19213 = state_17279;
state_17279 = G__19213;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$state_machine__14162__auto__ = function(state_17279){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14162__auto____1.call(this,state_17279);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14162__auto____0;
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14162__auto____1;
return cljs$core$async$state_machine__14162__auto__;
})()
})();
var state__14429__auto__ = (function (){var statearr_17335 = f__14428__auto__();
(statearr_17335[(6)] = c__14427__auto___19185);

return statearr_17335;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
}));


return out;
}));

(cljs.core.async.filter_LT_.cljs$lang$maxFixedArity = 3);

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.remove_LT_ = (function cljs$core$async$remove_LT_(var_args){
var G__17337 = arguments.length;
switch (G__17337) {
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
var c__14427__auto__ = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = (function (state_17409){
var state_val_17410 = (state_17409[(1)]);
if((state_val_17410 === (7))){
var inst_17405 = (state_17409[(2)]);
var state_17409__$1 = state_17409;
var statearr_17413_19219 = state_17409__$1;
(statearr_17413_19219[(2)] = inst_17405);

(statearr_17413_19219[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17410 === (20))){
var inst_17375 = (state_17409[(7)]);
var inst_17386 = (state_17409[(2)]);
var inst_17387 = cljs.core.next(inst_17375);
var inst_17356 = inst_17387;
var inst_17357 = null;
var inst_17359 = (0);
var inst_17360 = (0);
var state_17409__$1 = (function (){var statearr_17414 = state_17409;
(statearr_17414[(8)] = inst_17386);

(statearr_17414[(9)] = inst_17356);

(statearr_17414[(10)] = inst_17357);

(statearr_17414[(11)] = inst_17359);

(statearr_17414[(12)] = inst_17360);

return statearr_17414;
})();
var statearr_17421_19223 = state_17409__$1;
(statearr_17421_19223[(2)] = null);

(statearr_17421_19223[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17410 === (1))){
var state_17409__$1 = state_17409;
var statearr_17422_19224 = state_17409__$1;
(statearr_17422_19224[(2)] = null);

(statearr_17422_19224[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17410 === (4))){
var inst_17342 = (state_17409[(13)]);
var inst_17342__$1 = (state_17409[(2)]);
var inst_17343 = (inst_17342__$1 == null);
var state_17409__$1 = (function (){var statearr_17423 = state_17409;
(statearr_17423[(13)] = inst_17342__$1);

return statearr_17423;
})();
if(cljs.core.truth_(inst_17343)){
var statearr_17424_19226 = state_17409__$1;
(statearr_17424_19226[(1)] = (5));

} else {
var statearr_17425_19227 = state_17409__$1;
(statearr_17425_19227[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17410 === (15))){
var state_17409__$1 = state_17409;
var statearr_17430_19228 = state_17409__$1;
(statearr_17430_19228[(2)] = null);

(statearr_17430_19228[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17410 === (21))){
var state_17409__$1 = state_17409;
var statearr_17432_19232 = state_17409__$1;
(statearr_17432_19232[(2)] = null);

(statearr_17432_19232[(1)] = (23));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17410 === (13))){
var inst_17360 = (state_17409[(12)]);
var inst_17356 = (state_17409[(9)]);
var inst_17357 = (state_17409[(10)]);
var inst_17359 = (state_17409[(11)]);
var inst_17371 = (state_17409[(2)]);
var inst_17372 = (inst_17360 + (1));
var tmp17426 = inst_17357;
var tmp17427 = inst_17359;
var tmp17428 = inst_17356;
var inst_17356__$1 = tmp17428;
var inst_17357__$1 = tmp17426;
var inst_17359__$1 = tmp17427;
var inst_17360__$1 = inst_17372;
var state_17409__$1 = (function (){var statearr_17433 = state_17409;
(statearr_17433[(14)] = inst_17371);

(statearr_17433[(9)] = inst_17356__$1);

(statearr_17433[(10)] = inst_17357__$1);

(statearr_17433[(11)] = inst_17359__$1);

(statearr_17433[(12)] = inst_17360__$1);

return statearr_17433;
})();
var statearr_17434_19237 = state_17409__$1;
(statearr_17434_19237[(2)] = null);

(statearr_17434_19237[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17410 === (22))){
var state_17409__$1 = state_17409;
var statearr_17435_19240 = state_17409__$1;
(statearr_17435_19240[(2)] = null);

(statearr_17435_19240[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17410 === (6))){
var inst_17342 = (state_17409[(13)]);
var inst_17354 = (f.cljs$core$IFn$_invoke$arity$1 ? f.cljs$core$IFn$_invoke$arity$1(inst_17342) : f.call(null,inst_17342));
var inst_17355 = cljs.core.seq(inst_17354);
var inst_17356 = inst_17355;
var inst_17357 = null;
var inst_17359 = (0);
var inst_17360 = (0);
var state_17409__$1 = (function (){var statearr_17436 = state_17409;
(statearr_17436[(9)] = inst_17356);

(statearr_17436[(10)] = inst_17357);

(statearr_17436[(11)] = inst_17359);

(statearr_17436[(12)] = inst_17360);

return statearr_17436;
})();
var statearr_17437_19244 = state_17409__$1;
(statearr_17437_19244[(2)] = null);

(statearr_17437_19244[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17410 === (17))){
var inst_17375 = (state_17409[(7)]);
var inst_17379 = cljs.core.chunk_first(inst_17375);
var inst_17380 = cljs.core.chunk_rest(inst_17375);
var inst_17381 = cljs.core.count(inst_17379);
var inst_17356 = inst_17380;
var inst_17357 = inst_17379;
var inst_17359 = inst_17381;
var inst_17360 = (0);
var state_17409__$1 = (function (){var statearr_17450 = state_17409;
(statearr_17450[(9)] = inst_17356);

(statearr_17450[(10)] = inst_17357);

(statearr_17450[(11)] = inst_17359);

(statearr_17450[(12)] = inst_17360);

return statearr_17450;
})();
var statearr_17451_19247 = state_17409__$1;
(statearr_17451_19247[(2)] = null);

(statearr_17451_19247[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17410 === (3))){
var inst_17407 = (state_17409[(2)]);
var state_17409__$1 = state_17409;
return cljs.core.async.impl.ioc_helpers.return_chan(state_17409__$1,inst_17407);
} else {
if((state_val_17410 === (12))){
var inst_17395 = (state_17409[(2)]);
var state_17409__$1 = state_17409;
var statearr_17452_19248 = state_17409__$1;
(statearr_17452_19248[(2)] = inst_17395);

(statearr_17452_19248[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17410 === (2))){
var state_17409__$1 = state_17409;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_17409__$1,(4),in$);
} else {
if((state_val_17410 === (23))){
var inst_17403 = (state_17409[(2)]);
var state_17409__$1 = state_17409;
var statearr_17454_19251 = state_17409__$1;
(statearr_17454_19251[(2)] = inst_17403);

(statearr_17454_19251[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17410 === (19))){
var inst_17390 = (state_17409[(2)]);
var state_17409__$1 = state_17409;
var statearr_17455_19254 = state_17409__$1;
(statearr_17455_19254[(2)] = inst_17390);

(statearr_17455_19254[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17410 === (11))){
var inst_17356 = (state_17409[(9)]);
var inst_17375 = (state_17409[(7)]);
var inst_17375__$1 = cljs.core.seq(inst_17356);
var state_17409__$1 = (function (){var statearr_17457 = state_17409;
(statearr_17457[(7)] = inst_17375__$1);

return statearr_17457;
})();
if(inst_17375__$1){
var statearr_17458_19257 = state_17409__$1;
(statearr_17458_19257[(1)] = (14));

} else {
var statearr_17459_19258 = state_17409__$1;
(statearr_17459_19258[(1)] = (15));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17410 === (9))){
var inst_17397 = (state_17409[(2)]);
var inst_17398 = cljs.core.async.impl.protocols.closed_QMARK_(out);
var state_17409__$1 = (function (){var statearr_17460 = state_17409;
(statearr_17460[(15)] = inst_17397);

return statearr_17460;
})();
if(cljs.core.truth_(inst_17398)){
var statearr_17461_19259 = state_17409__$1;
(statearr_17461_19259[(1)] = (21));

} else {
var statearr_17462_19262 = state_17409__$1;
(statearr_17462_19262[(1)] = (22));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17410 === (5))){
var inst_17345 = cljs.core.async.close_BANG_(out);
var state_17409__$1 = state_17409;
var statearr_17463_19267 = state_17409__$1;
(statearr_17463_19267[(2)] = inst_17345);

(statearr_17463_19267[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17410 === (14))){
var inst_17375 = (state_17409[(7)]);
var inst_17377 = cljs.core.chunked_seq_QMARK_(inst_17375);
var state_17409__$1 = state_17409;
if(inst_17377){
var statearr_17464_19275 = state_17409__$1;
(statearr_17464_19275[(1)] = (17));

} else {
var statearr_17465_19276 = state_17409__$1;
(statearr_17465_19276[(1)] = (18));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17410 === (16))){
var inst_17393 = (state_17409[(2)]);
var state_17409__$1 = state_17409;
var statearr_17466_19286 = state_17409__$1;
(statearr_17466_19286[(2)] = inst_17393);

(statearr_17466_19286[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17410 === (10))){
var inst_17357 = (state_17409[(10)]);
var inst_17360 = (state_17409[(12)]);
var inst_17369 = cljs.core._nth(inst_17357,inst_17360);
var state_17409__$1 = state_17409;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17409__$1,(13),out,inst_17369);
} else {
if((state_val_17410 === (18))){
var inst_17375 = (state_17409[(7)]);
var inst_17384 = cljs.core.first(inst_17375);
var state_17409__$1 = state_17409;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17409__$1,(20),out,inst_17384);
} else {
if((state_val_17410 === (8))){
var inst_17360 = (state_17409[(12)]);
var inst_17359 = (state_17409[(11)]);
var inst_17366 = (inst_17360 < inst_17359);
var inst_17367 = inst_17366;
var state_17409__$1 = state_17409;
if(cljs.core.truth_(inst_17367)){
var statearr_17467_19310 = state_17409__$1;
(statearr_17467_19310[(1)] = (10));

} else {
var statearr_17468_19311 = state_17409__$1;
(statearr_17468_19311[(1)] = (11));

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
var cljs$core$async$mapcat_STAR__$_state_machine__14162__auto__ = null;
var cljs$core$async$mapcat_STAR__$_state_machine__14162__auto____0 = (function (){
var statearr_17469 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_17469[(0)] = cljs$core$async$mapcat_STAR__$_state_machine__14162__auto__);

(statearr_17469[(1)] = (1));

return statearr_17469;
});
var cljs$core$async$mapcat_STAR__$_state_machine__14162__auto____1 = (function (state_17409){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_17409);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e17470){var ex__14165__auto__ = e17470;
var statearr_17471_19326 = state_17409;
(statearr_17471_19326[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_17409[(4)]))){
var statearr_17472_19329 = state_17409;
(statearr_17472_19329[(1)] = cljs.core.first((state_17409[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__19335 = state_17409;
state_17409 = G__19335;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$mapcat_STAR__$_state_machine__14162__auto__ = function(state_17409){
switch(arguments.length){
case 0:
return cljs$core$async$mapcat_STAR__$_state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$mapcat_STAR__$_state_machine__14162__auto____1.call(this,state_17409);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$mapcat_STAR__$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mapcat_STAR__$_state_machine__14162__auto____0;
cljs$core$async$mapcat_STAR__$_state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mapcat_STAR__$_state_machine__14162__auto____1;
return cljs$core$async$mapcat_STAR__$_state_machine__14162__auto__;
})()
})();
var state__14429__auto__ = (function (){var statearr_17474 = f__14428__auto__();
(statearr_17474[(6)] = c__14427__auto__);

return statearr_17474;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
}));

return c__14427__auto__;
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.mapcat_LT_ = (function cljs$core$async$mapcat_LT_(var_args){
var G__17481 = arguments.length;
switch (G__17481) {
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
var G__17484 = arguments.length;
switch (G__17484) {
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
var G__17495 = arguments.length;
switch (G__17495) {
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
var c__14427__auto___19358 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = (function (state_17522){
var state_val_17523 = (state_17522[(1)]);
if((state_val_17523 === (7))){
var inst_17517 = (state_17522[(2)]);
var state_17522__$1 = state_17522;
var statearr_17524_19359 = state_17522__$1;
(statearr_17524_19359[(2)] = inst_17517);

(statearr_17524_19359[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17523 === (1))){
var inst_17499 = null;
var state_17522__$1 = (function (){var statearr_17529 = state_17522;
(statearr_17529[(7)] = inst_17499);

return statearr_17529;
})();
var statearr_17530_19360 = state_17522__$1;
(statearr_17530_19360[(2)] = null);

(statearr_17530_19360[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17523 === (4))){
var inst_17502 = (state_17522[(8)]);
var inst_17502__$1 = (state_17522[(2)]);
var inst_17503 = (inst_17502__$1 == null);
var inst_17504 = cljs.core.not(inst_17503);
var state_17522__$1 = (function (){var statearr_17531 = state_17522;
(statearr_17531[(8)] = inst_17502__$1);

return statearr_17531;
})();
if(inst_17504){
var statearr_17535_19362 = state_17522__$1;
(statearr_17535_19362[(1)] = (5));

} else {
var statearr_17536_19363 = state_17522__$1;
(statearr_17536_19363[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17523 === (6))){
var state_17522__$1 = state_17522;
var statearr_17539_19364 = state_17522__$1;
(statearr_17539_19364[(2)] = null);

(statearr_17539_19364[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17523 === (3))){
var inst_17519 = (state_17522[(2)]);
var inst_17520 = cljs.core.async.close_BANG_(out);
var state_17522__$1 = (function (){var statearr_17544 = state_17522;
(statearr_17544[(9)] = inst_17519);

return statearr_17544;
})();
return cljs.core.async.impl.ioc_helpers.return_chan(state_17522__$1,inst_17520);
} else {
if((state_val_17523 === (2))){
var state_17522__$1 = state_17522;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_17522__$1,(4),ch);
} else {
if((state_val_17523 === (11))){
var inst_17502 = (state_17522[(8)]);
var inst_17511 = (state_17522[(2)]);
var inst_17499 = inst_17502;
var state_17522__$1 = (function (){var statearr_17546 = state_17522;
(statearr_17546[(10)] = inst_17511);

(statearr_17546[(7)] = inst_17499);

return statearr_17546;
})();
var statearr_17548_19368 = state_17522__$1;
(statearr_17548_19368[(2)] = null);

(statearr_17548_19368[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17523 === (9))){
var inst_17502 = (state_17522[(8)]);
var state_17522__$1 = state_17522;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17522__$1,(11),out,inst_17502);
} else {
if((state_val_17523 === (5))){
var inst_17502 = (state_17522[(8)]);
var inst_17499 = (state_17522[(7)]);
var inst_17506 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(inst_17502,inst_17499);
var state_17522__$1 = state_17522;
if(inst_17506){
var statearr_17554_19370 = state_17522__$1;
(statearr_17554_19370[(1)] = (8));

} else {
var statearr_17555_19371 = state_17522__$1;
(statearr_17555_19371[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17523 === (10))){
var inst_17514 = (state_17522[(2)]);
var state_17522__$1 = state_17522;
var statearr_17556_19372 = state_17522__$1;
(statearr_17556_19372[(2)] = inst_17514);

(statearr_17556_19372[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17523 === (8))){
var inst_17499 = (state_17522[(7)]);
var tmp17553 = inst_17499;
var inst_17499__$1 = tmp17553;
var state_17522__$1 = (function (){var statearr_17557 = state_17522;
(statearr_17557[(7)] = inst_17499__$1);

return statearr_17557;
})();
var statearr_17558_19373 = state_17522__$1;
(statearr_17558_19373[(2)] = null);

(statearr_17558_19373[(1)] = (2));


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
var cljs$core$async$state_machine__14162__auto__ = null;
var cljs$core$async$state_machine__14162__auto____0 = (function (){
var statearr_17559 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_17559[(0)] = cljs$core$async$state_machine__14162__auto__);

(statearr_17559[(1)] = (1));

return statearr_17559;
});
var cljs$core$async$state_machine__14162__auto____1 = (function (state_17522){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_17522);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e17560){var ex__14165__auto__ = e17560;
var statearr_17565_19381 = state_17522;
(statearr_17565_19381[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_17522[(4)]))){
var statearr_17570_19382 = state_17522;
(statearr_17570_19382[(1)] = cljs.core.first((state_17522[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__19384 = state_17522;
state_17522 = G__19384;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$state_machine__14162__auto__ = function(state_17522){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14162__auto____1.call(this,state_17522);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14162__auto____0;
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14162__auto____1;
return cljs$core$async$state_machine__14162__auto__;
})()
})();
var state__14429__auto__ = (function (){var statearr_17572 = f__14428__auto__();
(statearr_17572[(6)] = c__14427__auto___19358);

return statearr_17572;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
}));


return out;
}));

(cljs.core.async.unique.cljs$lang$maxFixedArity = 2);

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.partition = (function cljs$core$async$partition(var_args){
var G__17585 = arguments.length;
switch (G__17585) {
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
var c__14427__auto___19390 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = (function (state_17643){
var state_val_17644 = (state_17643[(1)]);
if((state_val_17644 === (7))){
var inst_17639 = (state_17643[(2)]);
var state_17643__$1 = state_17643;
var statearr_17651_19391 = state_17643__$1;
(statearr_17651_19391[(2)] = inst_17639);

(statearr_17651_19391[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17644 === (1))){
var inst_17597 = (new Array(n));
var inst_17598 = inst_17597;
var inst_17599 = (0);
var state_17643__$1 = (function (){var statearr_17655 = state_17643;
(statearr_17655[(7)] = inst_17598);

(statearr_17655[(8)] = inst_17599);

return statearr_17655;
})();
var statearr_17658_19393 = state_17643__$1;
(statearr_17658_19393[(2)] = null);

(statearr_17658_19393[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17644 === (4))){
var inst_17602 = (state_17643[(9)]);
var inst_17602__$1 = (state_17643[(2)]);
var inst_17603 = (inst_17602__$1 == null);
var inst_17604 = cljs.core.not(inst_17603);
var state_17643__$1 = (function (){var statearr_17659 = state_17643;
(statearr_17659[(9)] = inst_17602__$1);

return statearr_17659;
})();
if(inst_17604){
var statearr_17660_19398 = state_17643__$1;
(statearr_17660_19398[(1)] = (5));

} else {
var statearr_17661_19399 = state_17643__$1;
(statearr_17661_19399[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17644 === (15))){
var inst_17633 = (state_17643[(2)]);
var state_17643__$1 = state_17643;
var statearr_17662_19404 = state_17643__$1;
(statearr_17662_19404[(2)] = inst_17633);

(statearr_17662_19404[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17644 === (13))){
var state_17643__$1 = state_17643;
var statearr_17664_19405 = state_17643__$1;
(statearr_17664_19405[(2)] = null);

(statearr_17664_19405[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17644 === (6))){
var inst_17599 = (state_17643[(8)]);
var inst_17625 = (inst_17599 > (0));
var state_17643__$1 = state_17643;
if(cljs.core.truth_(inst_17625)){
var statearr_17665_19406 = state_17643__$1;
(statearr_17665_19406[(1)] = (12));

} else {
var statearr_17669_19407 = state_17643__$1;
(statearr_17669_19407[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17644 === (3))){
var inst_17641 = (state_17643[(2)]);
var state_17643__$1 = state_17643;
return cljs.core.async.impl.ioc_helpers.return_chan(state_17643__$1,inst_17641);
} else {
if((state_val_17644 === (12))){
var inst_17598 = (state_17643[(7)]);
var inst_17631 = cljs.core.vec(inst_17598);
var state_17643__$1 = state_17643;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17643__$1,(15),out,inst_17631);
} else {
if((state_val_17644 === (2))){
var state_17643__$1 = state_17643;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_17643__$1,(4),ch);
} else {
if((state_val_17644 === (11))){
var inst_17619 = (state_17643[(2)]);
var inst_17620 = (new Array(n));
var inst_17598 = inst_17620;
var inst_17599 = (0);
var state_17643__$1 = (function (){var statearr_17671 = state_17643;
(statearr_17671[(10)] = inst_17619);

(statearr_17671[(7)] = inst_17598);

(statearr_17671[(8)] = inst_17599);

return statearr_17671;
})();
var statearr_17672_19409 = state_17643__$1;
(statearr_17672_19409[(2)] = null);

(statearr_17672_19409[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17644 === (9))){
var inst_17598 = (state_17643[(7)]);
var inst_17617 = cljs.core.vec(inst_17598);
var state_17643__$1 = state_17643;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17643__$1,(11),out,inst_17617);
} else {
if((state_val_17644 === (5))){
var inst_17598 = (state_17643[(7)]);
var inst_17599 = (state_17643[(8)]);
var inst_17602 = (state_17643[(9)]);
var inst_17608 = (state_17643[(11)]);
var inst_17606 = (inst_17598[inst_17599] = inst_17602);
var inst_17608__$1 = (inst_17599 + (1));
var inst_17611 = (inst_17608__$1 < n);
var state_17643__$1 = (function (){var statearr_17673 = state_17643;
(statearr_17673[(12)] = inst_17606);

(statearr_17673[(11)] = inst_17608__$1);

return statearr_17673;
})();
if(cljs.core.truth_(inst_17611)){
var statearr_17676_19416 = state_17643__$1;
(statearr_17676_19416[(1)] = (8));

} else {
var statearr_17677_19417 = state_17643__$1;
(statearr_17677_19417[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17644 === (14))){
var inst_17636 = (state_17643[(2)]);
var inst_17637 = cljs.core.async.close_BANG_(out);
var state_17643__$1 = (function (){var statearr_17681 = state_17643;
(statearr_17681[(13)] = inst_17636);

return statearr_17681;
})();
var statearr_17682_19419 = state_17643__$1;
(statearr_17682_19419[(2)] = inst_17637);

(statearr_17682_19419[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17644 === (10))){
var inst_17623 = (state_17643[(2)]);
var state_17643__$1 = state_17643;
var statearr_17683_19420 = state_17643__$1;
(statearr_17683_19420[(2)] = inst_17623);

(statearr_17683_19420[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17644 === (8))){
var inst_17598 = (state_17643[(7)]);
var inst_17608 = (state_17643[(11)]);
var tmp17679 = inst_17598;
var inst_17598__$1 = tmp17679;
var inst_17599 = inst_17608;
var state_17643__$1 = (function (){var statearr_17692 = state_17643;
(statearr_17692[(7)] = inst_17598__$1);

(statearr_17692[(8)] = inst_17599);

return statearr_17692;
})();
var statearr_17693_19421 = state_17643__$1;
(statearr_17693_19421[(2)] = null);

(statearr_17693_19421[(1)] = (2));


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
var cljs$core$async$state_machine__14162__auto__ = null;
var cljs$core$async$state_machine__14162__auto____0 = (function (){
var statearr_17697 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_17697[(0)] = cljs$core$async$state_machine__14162__auto__);

(statearr_17697[(1)] = (1));

return statearr_17697;
});
var cljs$core$async$state_machine__14162__auto____1 = (function (state_17643){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_17643);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e17698){var ex__14165__auto__ = e17698;
var statearr_17701_19424 = state_17643;
(statearr_17701_19424[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_17643[(4)]))){
var statearr_17702_19425 = state_17643;
(statearr_17702_19425[(1)] = cljs.core.first((state_17643[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__19426 = state_17643;
state_17643 = G__19426;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$state_machine__14162__auto__ = function(state_17643){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14162__auto____1.call(this,state_17643);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14162__auto____0;
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14162__auto____1;
return cljs$core$async$state_machine__14162__auto__;
})()
})();
var state__14429__auto__ = (function (){var statearr_17708 = f__14428__auto__();
(statearr_17708[(6)] = c__14427__auto___19390);

return statearr_17708;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
}));


return out;
}));

(cljs.core.async.partition.cljs$lang$maxFixedArity = 3);

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.partition_by = (function cljs$core$async$partition_by(var_args){
var G__17714 = arguments.length;
switch (G__17714) {
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
var c__14427__auto___19436 = cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((1));
cljs.core.async.impl.dispatch.run((function (){
var f__14428__auto__ = (function (){var switch__14161__auto__ = (function (state_17763){
var state_val_17764 = (state_17763[(1)]);
if((state_val_17764 === (7))){
var inst_17759 = (state_17763[(2)]);
var state_17763__$1 = state_17763;
var statearr_17766_19440 = state_17763__$1;
(statearr_17766_19440[(2)] = inst_17759);

(statearr_17766_19440[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17764 === (1))){
var inst_17718 = [];
var inst_17719 = inst_17718;
var inst_17720 = new cljs.core.Keyword("cljs.core.async","nothing","cljs.core.async/nothing",-69252123);
var state_17763__$1 = (function (){var statearr_17767 = state_17763;
(statearr_17767[(7)] = inst_17719);

(statearr_17767[(8)] = inst_17720);

return statearr_17767;
})();
var statearr_17768_19441 = state_17763__$1;
(statearr_17768_19441[(2)] = null);

(statearr_17768_19441[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17764 === (4))){
var inst_17723 = (state_17763[(9)]);
var inst_17723__$1 = (state_17763[(2)]);
var inst_17724 = (inst_17723__$1 == null);
var inst_17725 = cljs.core.not(inst_17724);
var state_17763__$1 = (function (){var statearr_17771 = state_17763;
(statearr_17771[(9)] = inst_17723__$1);

return statearr_17771;
})();
if(inst_17725){
var statearr_17772_19445 = state_17763__$1;
(statearr_17772_19445[(1)] = (5));

} else {
var statearr_17773_19446 = state_17763__$1;
(statearr_17773_19446[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17764 === (15))){
var inst_17719 = (state_17763[(7)]);
var inst_17751 = cljs.core.vec(inst_17719);
var state_17763__$1 = state_17763;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17763__$1,(18),out,inst_17751);
} else {
if((state_val_17764 === (13))){
var inst_17746 = (state_17763[(2)]);
var state_17763__$1 = state_17763;
var statearr_17776_19453 = state_17763__$1;
(statearr_17776_19453[(2)] = inst_17746);

(statearr_17776_19453[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17764 === (6))){
var inst_17719 = (state_17763[(7)]);
var inst_17748 = inst_17719.length;
var inst_17749 = (inst_17748 > (0));
var state_17763__$1 = state_17763;
if(cljs.core.truth_(inst_17749)){
var statearr_17781_19461 = state_17763__$1;
(statearr_17781_19461[(1)] = (15));

} else {
var statearr_17782_19468 = state_17763__$1;
(statearr_17782_19468[(1)] = (16));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17764 === (17))){
var inst_17756 = (state_17763[(2)]);
var inst_17757 = cljs.core.async.close_BANG_(out);
var state_17763__$1 = (function (){var statearr_17783 = state_17763;
(statearr_17783[(10)] = inst_17756);

return statearr_17783;
})();
var statearr_17784_19472 = state_17763__$1;
(statearr_17784_19472[(2)] = inst_17757);

(statearr_17784_19472[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17764 === (3))){
var inst_17761 = (state_17763[(2)]);
var state_17763__$1 = state_17763;
return cljs.core.async.impl.ioc_helpers.return_chan(state_17763__$1,inst_17761);
} else {
if((state_val_17764 === (12))){
var inst_17719 = (state_17763[(7)]);
var inst_17739 = cljs.core.vec(inst_17719);
var state_17763__$1 = state_17763;
return cljs.core.async.impl.ioc_helpers.put_BANG_(state_17763__$1,(14),out,inst_17739);
} else {
if((state_val_17764 === (2))){
var state_17763__$1 = state_17763;
return cljs.core.async.impl.ioc_helpers.take_BANG_(state_17763__$1,(4),ch);
} else {
if((state_val_17764 === (11))){
var inst_17719 = (state_17763[(7)]);
var inst_17723 = (state_17763[(9)]);
var inst_17727 = (state_17763[(11)]);
var inst_17736 = inst_17719.push(inst_17723);
var tmp17785 = inst_17719;
var inst_17719__$1 = tmp17785;
var inst_17720 = inst_17727;
var state_17763__$1 = (function (){var statearr_17788 = state_17763;
(statearr_17788[(12)] = inst_17736);

(statearr_17788[(7)] = inst_17719__$1);

(statearr_17788[(8)] = inst_17720);

return statearr_17788;
})();
var statearr_17789_19491 = state_17763__$1;
(statearr_17789_19491[(2)] = null);

(statearr_17789_19491[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17764 === (9))){
var inst_17720 = (state_17763[(8)]);
var inst_17732 = cljs.core.keyword_identical_QMARK_(inst_17720,new cljs.core.Keyword("cljs.core.async","nothing","cljs.core.async/nothing",-69252123));
var state_17763__$1 = state_17763;
var statearr_17796_19498 = state_17763__$1;
(statearr_17796_19498[(2)] = inst_17732);

(statearr_17796_19498[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17764 === (5))){
var inst_17723 = (state_17763[(9)]);
var inst_17727 = (state_17763[(11)]);
var inst_17720 = (state_17763[(8)]);
var inst_17729 = (state_17763[(13)]);
var inst_17727__$1 = (f.cljs$core$IFn$_invoke$arity$1 ? f.cljs$core$IFn$_invoke$arity$1(inst_17723) : f.call(null,inst_17723));
var inst_17729__$1 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(inst_17727__$1,inst_17720);
var state_17763__$1 = (function (){var statearr_17800 = state_17763;
(statearr_17800[(11)] = inst_17727__$1);

(statearr_17800[(13)] = inst_17729__$1);

return statearr_17800;
})();
if(inst_17729__$1){
var statearr_17801_19505 = state_17763__$1;
(statearr_17801_19505[(1)] = (8));

} else {
var statearr_17802_19506 = state_17763__$1;
(statearr_17802_19506[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17764 === (14))){
var inst_17723 = (state_17763[(9)]);
var inst_17727 = (state_17763[(11)]);
var inst_17741 = (state_17763[(2)]);
var inst_17742 = [];
var inst_17743 = inst_17742.push(inst_17723);
var inst_17719 = inst_17742;
var inst_17720 = inst_17727;
var state_17763__$1 = (function (){var statearr_17807 = state_17763;
(statearr_17807[(14)] = inst_17741);

(statearr_17807[(15)] = inst_17743);

(statearr_17807[(7)] = inst_17719);

(statearr_17807[(8)] = inst_17720);

return statearr_17807;
})();
var statearr_17808_19510 = state_17763__$1;
(statearr_17808_19510[(2)] = null);

(statearr_17808_19510[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17764 === (16))){
var state_17763__$1 = state_17763;
var statearr_17815_19523 = state_17763__$1;
(statearr_17815_19523[(2)] = null);

(statearr_17815_19523[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17764 === (10))){
var inst_17734 = (state_17763[(2)]);
var state_17763__$1 = state_17763;
if(cljs.core.truth_(inst_17734)){
var statearr_17816_19525 = state_17763__$1;
(statearr_17816_19525[(1)] = (11));

} else {
var statearr_17821_19526 = state_17763__$1;
(statearr_17821_19526[(1)] = (12));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17764 === (18))){
var inst_17753 = (state_17763[(2)]);
var state_17763__$1 = state_17763;
var statearr_17824_19527 = state_17763__$1;
(statearr_17824_19527[(2)] = inst_17753);

(statearr_17824_19527[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_17764 === (8))){
var inst_17729 = (state_17763[(13)]);
var state_17763__$1 = state_17763;
var statearr_17825_19532 = state_17763__$1;
(statearr_17825_19532[(2)] = inst_17729);

(statearr_17825_19532[(1)] = (10));


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
var cljs$core$async$state_machine__14162__auto__ = null;
var cljs$core$async$state_machine__14162__auto____0 = (function (){
var statearr_17829 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_17829[(0)] = cljs$core$async$state_machine__14162__auto__);

(statearr_17829[(1)] = (1));

return statearr_17829;
});
var cljs$core$async$state_machine__14162__auto____1 = (function (state_17763){
while(true){
var ret_value__14163__auto__ = (function (){try{while(true){
var result__14164__auto__ = switch__14161__auto__(state_17763);
if(cljs.core.keyword_identical_QMARK_(result__14164__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__14164__auto__;
}
break;
}
}catch (e17831){var ex__14165__auto__ = e17831;
var statearr_17832_19533 = state_17763;
(statearr_17832_19533[(2)] = ex__14165__auto__);


if(cljs.core.seq((state_17763[(4)]))){
var statearr_17834_19534 = state_17763;
(statearr_17834_19534[(1)] = cljs.core.first((state_17763[(4)])));

} else {
throw ex__14165__auto__;
}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
}})();
if(cljs.core.keyword_identical_QMARK_(ret_value__14163__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__19536 = state_17763;
state_17763 = G__19536;
continue;
} else {
return ret_value__14163__auto__;
}
break;
}
});
cljs$core$async$state_machine__14162__auto__ = function(state_17763){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__14162__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__14162__auto____1.call(this,state_17763);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__14162__auto____0;
cljs$core$async$state_machine__14162__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__14162__auto____1;
return cljs$core$async$state_machine__14162__auto__;
})()
})();
var state__14429__auto__ = (function (){var statearr_17837 = f__14428__auto__();
(statearr_17837[(6)] = c__14427__auto___19436);

return statearr_17837;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped(state__14429__auto__);
}));


return out;
}));

(cljs.core.async.partition_by.cljs$lang$maxFixedArity = 3);


//# sourceMappingURL=cljs.core.async.js.map
