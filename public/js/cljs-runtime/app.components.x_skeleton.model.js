goog.provide('app.components.x_skeleton.model');
app.components.x_skeleton.model.tag_name = "x-skeleton";
app.components.x_skeleton.model.attr_variant = "variant";
app.components.x_skeleton.model.attr_animation = "animation";
app.components.x_skeleton.model.attr_width = "width";
app.components.x_skeleton.model.attr_height = "height";
app.components.x_skeleton.model.default_variant = "rect";
app.components.x_skeleton.model.default_animation = "pulse";
app.components.x_skeleton.model.allowed_variants = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["text",null,"rect",null,"circle",null], null), null);
app.components.x_skeleton.model.allowed_animations = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["none",null,"pulse",null,"wave",null], null), null);
app.components.x_skeleton.model.observed_attributes = [app.components.x_skeleton.model.attr_variant,app.components.x_skeleton.model.attr_animation,app.components.x_skeleton.model.attr_width,app.components.x_skeleton.model.attr_height];
app.components.x_skeleton.model.property_api = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"variant","variant",-424354234),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_skeleton.model.attr_variant], null),new cljs.core.Keyword(null,"animation","animation",-1248293244),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_skeleton.model.attr_animation], null),new cljs.core.Keyword(null,"width","width",-384071477),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_skeleton.model.attr_width], null),new cljs.core.Keyword(null,"height","height",1025178622),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_skeleton.model.attr_height], null)], null);
app.components.x_skeleton.model.normalize_variant = (function app$components$x_skeleton$model$normalize_variant(v){
if(((typeof v === 'string') && (cljs.core.contains_QMARK_(app.components.x_skeleton.model.allowed_variants,v)))){
return v;
} else {
return app.components.x_skeleton.model.default_variant;
}
});
app.components.x_skeleton.model.normalize_animation = (function app$components$x_skeleton$model$normalize_animation(v){
if(((typeof v === 'string') && (cljs.core.contains_QMARK_(app.components.x_skeleton.model.allowed_animations,v)))){
return v;
} else {
return app.components.x_skeleton.model.default_animation;
}
});
app.components.x_skeleton.model.normalize_css_value = (function app$components$x_skeleton$model$normalize_css_value(v){
if(((typeof v === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",v)))){
return v;
} else {
return null;
}
});
app.components.x_skeleton.model.derive_state = (function app$components$x_skeleton$model$derive_state(p__23038){
var map__23039 = p__23038;
var map__23039__$1 = cljs.core.__destructure_map(map__23039);
var variant = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23039__$1,new cljs.core.Keyword(null,"variant","variant",-424354234));
var animation = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23039__$1,new cljs.core.Keyword(null,"animation","animation",-1248293244));
var width = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23039__$1,new cljs.core.Keyword(null,"width","width",-384071477));
var height = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23039__$1,new cljs.core.Keyword(null,"height","height",1025178622));
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"variant","variant",-424354234),app.components.x_skeleton.model.normalize_variant(variant),new cljs.core.Keyword(null,"animation","animation",-1248293244),app.components.x_skeleton.model.normalize_animation(animation),new cljs.core.Keyword(null,"width","width",-384071477),app.components.x_skeleton.model.normalize_css_value(width),new cljs.core.Keyword(null,"height","height",1025178622),app.components.x_skeleton.model.normalize_css_value(height)], null);
});

//# sourceMappingURL=app.components.x_skeleton.model.js.map
