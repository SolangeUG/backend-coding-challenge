var app = angular.module("alchemytec.config", []);

// Some environmental specific config options
var gulpEnvConfig = { /*{{gulp-env-config}}*/ };

app.constant("config", {
	apiroot: gulpEnvConfig.apiroot,
	staticRoot: gulpEnvConfig.staticRoot,
	vatRate: gulpEnvConfig.vatRate
});
