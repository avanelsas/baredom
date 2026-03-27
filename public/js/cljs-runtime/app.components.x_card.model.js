goog.provide('app.components.x_card.model');
app.components.x_card.model.tag_name = "x-card";
app.components.x_card.model.attr_variant = "variant";
app.components.x_card.model.attr_padding = "padding";
app.components.x_card.model.attr_radius = "radius";
app.components.x_card.model.attr_interactive = "interactive";
app.components.x_card.model.attr_disabled = "disabled";
app.components.x_card.model.attr_label = "label";
app.components.x_card.model.observed_attributes = [app.components.x_card.model.attr_variant,app.components.x_card.model.attr_padding,app.components.x_card.model.attr_radius,app.components.x_card.model.attr_interactive,app.components.x_card.model.attr_disabled,app.components.x_card.model.attr_label];
app.components.x_card.model.variant_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, ["outlined",null,"elevated",null,"filled",null,"ghost",null], null), null);
app.components.x_card.model.padding_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, ["none",null,"md",null,"lg",null,"sm",null], null), null);
app.components.x_card.model.radius_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 5, ["none",null,"md",null,"lg",null,"xl",null,"sm",null], null), null);
app.components.x_card.model.default_variant = "elevated";
app.components.x_card.model.default_padding = "md";
app.components.x_card.model.default_radius = "lg";
app.components.x_card.model.property_api = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"interactive","interactive",-2024078362),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"attribute","attribute",-2074029119),app.components.x_card.model.attr_interactive,new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"default","default",-1987822328),false,new cljs.core.Keyword(null,"reflects-to-attribute","reflects-to-attribute",1726736839),true], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"attribute","attribute",-2074029119),app.components.x_card.model.attr_disabled,new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"default","default",-1987822328),false,new cljs.core.Keyword(null,"reflects-to-attribute","reflects-to-attribute",1726736839),true], null)], null);
app.components.x_card.model.event_press = "press";
app.components.x_card.model.event_schema = new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"press","press",-1963096513),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"event-name","event-name",927259778),"press",new cljs.core.Keyword(null,"detail","detail",-1545345025),cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"bubbles","bubbles",1049634589),true,new cljs.core.Keyword(null,"composed","composed",-1510693384),true], null)], null);
app.components.x_card.model.valid_enum = (function app$components$x_card$model$valid_enum(value,allowed,fallback){
if(cljs.core.contains_QMARK_(allowed,value)){
return value;
} else {
return fallback;
}
});
app.components.x_card.model.normalize_variant = (function app$components$x_card$model$normalize_variant(value){
return app.components.x_card.model.valid_enum(value,app.components.x_card.model.variant_values,app.components.x_card.model.default_variant);
});
app.components.x_card.model.normalize_padding = (function app$components$x_card$model$normalize_padding(value){
return app.components.x_card.model.valid_enum(value,app.components.x_card.model.padding_values,app.components.x_card.model.default_padding);
});
app.components.x_card.model.normalize_radius = (function app$components$x_card$model$normalize_radius(value){
return app.components.x_card.model.valid_enum(value,app.components.x_card.model.radius_values,app.components.x_card.model.default_radius);
});
app.components.x_card.model.normalize_bool = (function app$components$x_card$model$normalize_bool(value){
return cljs.core.boolean$(value);
});
app.components.x_card.model.normalize_label = (function app$components$x_card$model$normalize_label(value){
if(((typeof value === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",value)))){
return value;
} else {
return null;
}
});
app.components.x_card.model.derive_state = (function app$components$x_card$model$derive_state(p__20571){
var map__20572 = p__20571;
var map__20572__$1 = cljs.core.__destructure_map(map__20572);
var variant = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20572__$1,new cljs.core.Keyword(null,"variant","variant",-424354234));
var padding = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20572__$1,new cljs.core.Keyword(null,"padding","padding",1660304693));
var radius = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20572__$1,new cljs.core.Keyword(null,"radius","radius",-2073122258));
var interactive = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20572__$1,new cljs.core.Keyword(null,"interactive","interactive",-2024078362));
var disabled = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20572__$1,new cljs.core.Keyword(null,"disabled","disabled",-1529784218));
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20572__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var variant_STAR_ = app.components.x_card.model.normalize_variant(variant);
var padding_STAR_ = app.components.x_card.model.normalize_padding(padding);
var radius_STAR_ = app.components.x_card.model.normalize_radius(radius);
var interactive_STAR_ = app.components.x_card.model.normalize_bool(interactive);
var disabled_STAR_ = app.components.x_card.model.normalize_bool(disabled);
var role = ((interactive_STAR_)?"button":null);
var tabindex = ((((interactive_STAR_) && (disabled_STAR_)))?"-1":((interactive_STAR_)?"0":null
));
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"role","role",-736691072),new cljs.core.Keyword(null,"tabindex","tabindex",338877510),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.Keyword(null,"variant","variant",-424354234),new cljs.core.Keyword(null,"interactive","interactive",-2024078362),new cljs.core.Keyword(null,"radius","radius",-2073122258),new cljs.core.Keyword(null,"padding","padding",1660304693),new cljs.core.Keyword(null,"aria-disabled","aria-disabled",-667357160),new cljs.core.Keyword(null,"aria-label","aria-label",455891514)],[role,tabindex,disabled_STAR_,variant_STAR_,interactive_STAR_,radius_STAR_,padding_STAR_,((disabled_STAR_)?"true":null),app.components.x_card.model.normalize_label(label)]);
});

//# sourceMappingURL=app.components.x_card.model.js.map
