module.exports = function (config) {
  config.set({
    frameworks: ["cljs-test"],
    files: ["out/karma/test.js"],
    browsers: ["ChromeHeadless"],
    singleRun: true,
    pingTimeout: 30000,
    plugins: ["karma-cljs-test", "karma-chrome-launcher"],
    client: { args: ["shadow.test.karma.init"] },
  });
};
