goog.provide('app.components.x_container.model');
app.components.x_container.model.tag_name = "x-container";
app.components.x_container.model.attr_as = "as";
app.components.x_container.model.attr_size = "size";
app.components.x_container.model.attr_padding = "padding";
app.components.x_container.model.attr_center = "center";
app.components.x_container.model.attr_fluid = "fluid";
app.components.x_container.model.attr_label = "label";
app.components.x_container.model.default_as = "div";
app.components.x_container.model.default_size = "lg";
app.components.x_container.model.default_padding = "md";
app.components.x_container.model.allowed_as = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 8, ["article",null,"footer",null,"section",null,"main",null,"nav",null,"div",null,"aside",null,"header",null], null), null);
app.components.x_container.model.allowed_sizes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 6, ["xs",null,"md",null,"lg",null,"full",null,"xl",null,"sm",null], null), null);
app.components.x_container.model.allowed_padding = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, ["none",null,"md",null,"lg",null,"sm",null], null), null);
app.components.x_container.model.observed_attributes = [app.components.x_container.model.attr_as,app.components.x_container.model.attr_size,app.components.x_container.model.attr_padding,app.components.x_container.model.attr_center,app.components.x_container.model.attr_fluid,app.components.x_container.model.attr_label];
app.components.x_container.model.property_api = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"center","center",-748944368),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_container.model.attr_center], null),new cljs.core.Keyword(null,"fluid","fluid",-1823657759),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_container.model.attr_fluid], null)], null);
app.components.x_container.model.event_schema = cljs.core.PersistentArrayMap.EMPTY;
app.components.x_container.model.normalize_enum = (function app$components$x_container$model$normalize_enum(value,allowed,default_value){
if(((typeof value === 'string') && (cljs.core.contains_QMARK_(allowed,value)))){
return value;
} else {
return default_value;
}
});
app.components.x_container.model.normalize_as = (function app$components$x_container$model$normalize_as(value){
return app.components.x_container.model.normalize_enum(value,app.components.x_container.model.allowed_as,app.components.x_container.model.default_as);
});
app.components.x_container.model.normalize_size = (function app$components$x_container$model$normalize_size(value){
return app.components.x_container.model.normalize_enum(value,app.components.x_container.model.allowed_sizes,app.components.x_container.model.default_size);
});
app.components.x_container.model.normalize_padding = (function app$components$x_container$model$normalize_padding(value){
return app.components.x_container.model.normalize_enum(value,app.components.x_container.model.allowed_padding,app.components.x_container.model.default_padding);
});
app.components.x_container.model.non_empty_string_QMARK_ = (function app$components$x_container$model$non_empty_string_QMARK_(value){
return ((typeof value === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(value,"")));
});
app.components.x_container.model.public_state = (function app$components$x_container$model$public_state(p__20811){
var map__20813 = p__20811;
var map__20813__$1 = cljs.core.__destructure_map(map__20813);
var as = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20813__$1,new cljs.core.Keyword(null,"as","as",1148689641));
var size = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20813__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var padding = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20813__$1,new cljs.core.Keyword(null,"padding","padding",1660304693));
var center = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20813__$1,new cljs.core.Keyword(null,"center","center",-748944368));
var fluid = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20813__$1,new cljs.core.Keyword(null,"fluid","fluid",-1823657759));
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20813__$1,new cljs.core.Keyword(null,"label","label",1718410804));
return new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"as","as",1148689641),app.components.x_container.model.normalize_as(as),new cljs.core.Keyword(null,"size","size",1098693007),app.components.x_container.model.normalize_size(size),new cljs.core.Keyword(null,"padding","padding",1660304693),app.components.x_container.model.normalize_padding(padding),new cljs.core.Keyword(null,"center","center",-748944368),cljs.core.boolean$(center),new cljs.core.Keyword(null,"fluid","fluid",-1823657759),cljs.core.boolean$(fluid),new cljs.core.Keyword(null,"label","label",1718410804),((app.components.x_container.model.non_empty_string_QMARK_(label))?label:null)], null);
});

//# sourceMappingURL=app.components.x_container.model.js.map
