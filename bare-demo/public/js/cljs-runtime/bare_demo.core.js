goog.provide('bare_demo.core');
bare_demo.core.register_components_BANG_ = (function bare_demo$core$register_components_BANG_(){
app.exports.x_navbar.register_BANG_();

app.exports.x_sidebar.register_BANG_();

app.exports.x_button.register_BANG_();

app.exports.x_modal.register_BANG_();

return app.exports.x_container.register_BANG_();
});
bare_demo.core.view = (function bare_demo$core$view(){
return bare_demo.views.app.app(cljs.core.deref(bare_demo.state.app));
});
bare_demo.core.reload_BANG_ = (function bare_demo$core$reload_BANG_(){
return bare_demo.renderer.render_BANG_(document.getElementById("app"),bare_demo.core.view);
});
bare_demo.core.init_BANG_ = (function bare_demo$core$init_BANG_(){
bare_demo.core.register_components_BANG_();

return bare_demo.renderer.mount_BANG_(document.getElementById("app"),bare_demo.core.view,bare_demo.state.app);
});

//# sourceMappingURL=bare_demo.core.js.map
