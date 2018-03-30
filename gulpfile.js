/******************************************************************************************

Automated building of JavaScript and LESS files courtesy of Gulp

******************************************************************************************/

require("./sanitycheck.js");

try {
	var package = require("./package.json");
	var config = require("./config.js");
	var path = require("path");
	var fs = require("fs");
	var util = require("util");
	var parseargs = require("minimist");
	var gulp = require("gulp");
	var gulpsync = require("gulp-sync")(gulp);
	var watch = require("gulp-watch");
	var less = require("gulp-less");
	var sourcemaps = require("gulp-sourcemaps");
	var uglify = require("gulp-uglify");
	var gulpif = require("gulp-if");
	var rename = require("gulp-rename");
	var filesize = require("gulp-filesize");
	var iconfont = require("gulp-iconfont");
	var consolidate = require("gulp-consolidate");
	var ngtemplates = require("gulp-ng-templates");
	var htmlmin = require("gulp-htmlmin");
	var buffer = require("vinyl-buffer");
	var source = require("vinyl-source-stream");
	var browserify = require("browserify");
	var watchify = require("watchify");
	var babel = require("babelify");
	var LessPluginCleanCSS = require("less-plugin-clean-css");
	var cleancss = new LessPluginCleanCSS({ advanced: true, verbose: true, debug: true });
	var replace = require("gulp-replace");
	var htmlhint = require("gulp-htmlhint");
	var gutil = require("gulp-util");
	var findparentdir = require("find-parent-dir");
	var mkdirp = require("mkdirp");
	var glob = require("glob")
	var es = require("event-stream");
	var prompt = require("prompt-sync")();
	var settings = require("./settings.js");
	var eslint = require("gulp-eslint");
	var tasksGit = require("./tasks-git.js");
	var tasksMisc = require("./tasks-misc.js");
	var connect = require("gulp-connect");
}
catch (error) {
	console.log(error);
	console.log("\nA required module may be missing, try the following command to fix this:\n   npm install\n");
	process.exit(1);
}

var argv = parseargs(process.argv.slice(2));

var iconList = [];

var watching = false;

// Return root path for minifify
var compressPath = function(p) {
	return p;
	// Despite the docs saying this works, Chrome seems to show it doesn't
	return path.relative(settings.paths.build.source, p);
};

var errorHandler = function(err) {
	if (err instanceof SyntaxError) {
		gutil.log(gutil.colors.red("ERROR:"), err.message);
		console.log(err.codeFrame);
	}
	else if (err instanceof Error)
		gutil.log(gutil.colors.red("ERROR:"), err.message);
	else
		gutil.log(err);

	if (watching)
		this.emit("end");
	else
		process.exit(1);
};

// Build icon font
gulp.task("iconfont", function(){
	return gulp.src([settings.paths.build.source + "font-svgs/iconfont/*.svg"])
	.pipe(iconfont({
		fontName: "iconfont",
		appendCodepoints: false
	}))
	.on("glyphs", function(glyphs, options) {
		iconList = [];

		// Make a list of glyphs we can provide the app version page with
		for (var u = 0; u < glyphs.length; u++)
			iconList.push(glyphs[u].name);

		// Create the CSS template
		return gulp.src(settings.paths.build.source + "less/font.less.template")
			.pipe(consolidate("swig", {
				glyphs: glyphs,
				fontName: "iconfont",
				fontPath: "../fonts/",
				className: "iconfont",
				commithash: settings.githash
			}))
			.pipe(rename("iconfont.less"))
			.pipe(gulp.dest(settings.paths.build.source + "less/"));
	})
	.pipe(gulp.dest(settings.paths.build.target + "fonts/"));
});

// Compile CSS
gulp.task("css", function(done) {
	// List available theme files
	var themePaths = settings.buildPathArray(settings.paths.build.source, settings.lists.css.build);
	var streams = [];

	themePaths.forEach((matchPath, index) => {
		var themeFiles = glob.sync(matchPath);

		themeFiles.forEach((theme, index) => {
			var basename = path.basename(theme, ".less");

			// Process each theme file
			streams.push(gulp.src(settings.buildPathArray(settings.paths.build.source, settings.lists.css.master), { base: settings.paths.build.source })
				.pipe(replace(/{{theme-name}}/ig, "themes/" + basename))
				.pipe(replace(/{{iconfont-path}}/ig, ""))
				.pipe(sourcemaps.init({ loadMaps: true, debug: true }))
				.pipe(less({
					plugins: [
						cleancss
					]
				}))
				.on("error", errorHandler)
				.pipe(rename(basename + ".main.min.css"))
				.pipe(filesize())
				.pipe(sourcemaps.write("../maps"))
				.pipe(gulp.dest(settings.paths.build.target + "css/")));
		});
	});

	es.merge(streams).on("end", done);
});

// Copy static html templates
gulp.task("html", function() {
	return gulp.src(settings.buildPathArray(settings.paths.build.source, settings.lists.html.build))
		.pipe(replace(/{{commithash}}/ig, settings.githash))
		.pipe(replace(/{{branch}}/ig, settings.gitbranch))
		.pipe(replace(/{{fonturl}}/ig, config.fonts))
		.pipe(filesize())
		.pipe(gulp.dest(settings.paths.static.targethtml));
});

// Compile an individual directory's partials
function compilePartials(filepath) {
	if (process.platform === "win32")
		var pathArray = filepath.split("\\");
	else
		var pathArray = filepath.split("/");

	pathArray.pop();
	var basename = pathArray.pop();
	pathArray.push(basename);
	var destpath = pathArray.join("/");

	return gulp.src(filepath + "/**/*.html")
		.pipe(htmlhint({ "doctype-first": false }))
		.pipe(watching ? htmlhint.reporter() : htmlhint.failReporter())
		.pipe(ngtemplates({
			htmlMinifier: settings.minifiy,
			module: basename + ".partials",
			path: function(path, base) {
				if (!base.endsWith("partials/"))
					base += "partials/";

				return basename + "/" + path.replace(base, "");
			}
		}))
		.pipe(uglify(settings.uglify))
		.pipe(rename("partials.js"))
		.pipe(filesize())
		.pipe(gulp.dest(destpath));
};

// Compile all partials
gulp.task("partials", function(done) {
	// List available partial files
	var partialPaths = settings.buildPathArray(settings.paths.build.source, settings.lists.apps.partials);
	var streams = [];

	partialPaths.forEach((matchPath, index) => {
		var partialFiles = glob.sync(matchPath);

		partialFiles.forEach((partial, index) => {
			streams.push(compilePartials(partial));
		});
	});

	es.merge(streams).on("end", done);
});

// Compile app scripts
gulp.task("apps-scripts", ["partials"], function(done) {
	// List available app scripts
	var appPaths = settings.buildPathArray(settings.paths.build.source, settings.lists.apps.build);
	var streams = [];

	appPaths.forEach((matchPath, index) => {
		var appFiles = glob.sync(matchPath);

		appFiles.forEach((app, index) => {
			var basename = path.basename(app, "-main.js");

			if (argv.apps) {
				var appList = argv.apps.split(",");
				var matched = false;

				appList.forEach((appName, index) => {
					if (basename.toLowerCase().indexOf(appName.toLowerCase()) != -1)
						matched = true;
				});

				if (!matched)
					return;
			}

			app = path.normalize(app.replace(settings.paths.build.source, ""));

			var bundler = browserify(settings.browserify);
			if (watching)
				bundler.plugin(watchify);

			bundler.transform(babel, settings.babel);
			bundler.add(app);
			bundler.external("moment");

			var bundle = function() {
				gutil.log("Bundling js module '" + basename + "'");
				return bundler
					.bundle()
					.on("error", errorHandler)
					.pipe(source(basename + ".min.js"))
					.pipe(buffer())
					.pipe(replace(/\/\*{{gulp-build-version}}\*\//ig, "version: '" + package.version + "', commithash: '" + settings.githash + "', gitbranch: '" + settings.gitbranch + "', release: '" + package.release + "', builddate: new Date(" + JSON.stringify(new Date()) + "), iconlist: " + JSON.stringify(iconList)))
					.pipe(replace(/\/\*{{gulp-env-config}}\*\//ig, JSON.stringify(config).slice(1, -1)))
					.pipe(sourcemaps.init({loadMaps: true}))
					.pipe(gulpif(settings.compress, uglify(settings.uglify)))
					.pipe(sourcemaps.write("../maps"))
					.pipe(gulp.dest(settings.paths.build.target + "js/"));
			}

			if (watching) {
				bundler.on("update", function() {
					bundle().on("finish", function() {
						gulp.start("copyto");
					});
				});
				bundler.on("log", function(msg) {
					gutil.log(basename + ": " + msg);
				});
			}

			streams.push(bundle());
		});
	});

	es.merge(streams).on("end", done);
});

// Watch files and trigger minimal builds
gulp.task("watch", function(done) {
	watching = true;
	gulp.start("dev");

	watch(settings.buildPathArray(settings.paths.static.source, settings.lists.css.watch), function(file) {
		var basename = path.basename(file.path);
		var extension = path.extname(file.path);
		var destination = path.normalize(settings.paths.build.source + path.relative(settings.paths.static.source, file.path));

		gutil.log("Watched less file changed: '" + basename + "'");
		// check if the file has been removed
		fs.access(file.path, function(err) {
			if (!err) {
				fs.createReadStream(file.path)
					.pipe(fs.createWriteStream(destination)
						.on("close", function() {
							gulp.start(gulpsync.sync([ "css", "copyto" ]));
						})
					);
			} else {
				// file has been removed, cleanup destination
				fs.unlinkSync(destination);
				gulp.start(gulpsync.sync([ "css", "copyto" ]));
			}
		});
	});

	// Make all eslint options warnings as we are watching
	for (key in settings.eslint.rules) {
		if (Array.isArray(settings.eslint.rules[key]) && (settings.eslint.rules[key][0] == 2))
			settings.eslint.rules[key][0] = 1;
		else if (settings.eslint.rules[key] == 2)
			settings.eslint.rules[key] = 1;
	}

	watch(settings.buildPathArray(settings.paths.static.source, settings.lists.apps.watch), function(file) {
		var basename = path.basename(file.path);
		var extension = path.extname(file.path);
		var destination = path.normalize(settings.paths.build.source + path.relative(settings.paths.static.source, file.path));

		gutil.log("Watched app file changed: '" + basename + "'");

		var compile = function() {
			switch (extension) {
				case ".html":
					// Find the parent partials file so we can rebuild this partial set only
					findparentdir(destination, "partials.js", function(error, directory) {
						if (error)
							gutil.log("Unable to find partial file: " + error);
						else
							compilePartials(directory);
					});
					break;

				case ".js":
					// Do nothing for js files but lint them, watchify will notice the new file has been copied over
					// unfortunately watchify doesn't handle adding or removing files so you will need to restart watchify
					gulp.src(file.path)
						.pipe(eslint(settings.eslint))
						.pipe(eslint.format())
						.pipe(eslint.failAfterError())
						.on("error", (error) => {
							this.emit("end");
						});

					break;
			}
		};

		// check if the file has been removed
		fs.access(file.path, function(err) {
			if (!err) {
				fs.createReadStream(file.path)
					.pipe(fs.createWriteStream(destination)
						.on("close", compile)
					);
			} else {
				// file has been removed, cleanup destination
				fs.unlinkSync(destination);
				compile();
			}
		});

	});

	done();
});

// Main release build chain
gulp.task("build", gulpsync.sync(["gitinfo", "copyfrom", "iconfont", "css", "html", "apps-scripts", "copyto"], "sync release"));
gulp.task("build-lint", gulpsync.sync(["gitinfo", "copyfrom", "eslint", "iconfont", "css", "html", "apps-scripts", "copyto"], "sync release-lint"));

// Uncompressed release build chain
gulp.task("dev", gulpsync.sync(["uncompressed", "build"], "sync dev"));

// Uncompressed es6 release build chain
gulp.task("dev-es6", gulpsync.sync(["es6", "uncompressed", "build"], "sync dev-es6"));

// Default release build chain with clean
gulp.task("clean-build", gulpsync.sync(["clean-all", "build"], "sync default"));

// Font creation build chain
gulp.task("icons", gulpsync.sync(["clean-all", "copyfrom", "iconfont", "copyto"], "sync icons"));

// ESLint test build chain
gulp.task("lint", gulpsync.sync(["clean-all", "copyfrom", "eslint"], "sync lint"));

// Aliases
gulp.task("build-dev", ["dev"]);
gulp.task("debug", ["dev"]);

// Less only build chain
gulp.task("less", gulpsync.sync(["gitinfo", "uncompressed", "copyfrom", "html", "iconfont", "css", "copyto"], "sync css"));

// App scripts only build chain
gulp.task("apps", gulpsync.sync(["gitinfo", "uncompressed", "copyfrom", "html", "iconfont", "apps-scripts", "copyto"], "sync apps"));

gulp.task("server", ["dev"], function() {
	connect.server({ root: config.target });
});

// Present help info
gulp.task("help", function() {
	console.log("options:");
	console.log("build\n  : standard build");
	console.log("build-upload\n  : standard build and upload to artifactory");
	console.log("dev\n  : unminified build");
	console.log("dev-es6\n  : unminified es6 browser build");
	console.log("less\n  : only build CSS files");
	console.log("less --themes prs,ssr\n  : only build themes beginning with prs and ssr");
	console.log("apps\n  : only build app bundles");
	console.log("icons\n  : build just the icon font and matching less file");
	console.log("clean\n  : clean build directories");
	console.log("clean-all\n  : clean build and target static directories");
	console.log("watch\n  : build on changes");
	console.log("watch --apps main\n  : only build matching apps on changes");
	console.log("lint\n  : run eslint tests");
	console.log("lint --fix\n  : run eslint tests and attempt to fix issues");
});

// Default build task
gulp.task("default", ["server"]);
