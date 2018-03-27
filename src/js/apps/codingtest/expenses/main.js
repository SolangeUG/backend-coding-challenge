"use strict";

/******************************************************************************************

Tasks main

******************************************************************************************/

require("./expenses-controller.js");

var app = angular.module("expenses.controllers", [
	"expenses.controller", ["ngCookies"]
]);

app.config(['$httpProvider', function ($httpProvider) {
    // Configure $http requests to allow session based authentication
	$httpProvider.defaults.withCredentials = true;
}]);

app.config(["$routeProvider", function($routeProvider) {
	// Labour analysis routes
	$routeProvider.when("/expenses", { templateUrl: "codingtest/expenses-content.html" });

	$routeProvider.otherwise({redirectTo: "/expenses"});
}]);

app.run(["$rootScope", function($rootScope) {
	// Add app button
	$rootScope.appSections = $rootScope.appSections || [];
	$rootScope.appSections.push({ title: "Expenses", image: "img/icon-generic.png", app: "expenses" });

	// Configure tab sections
	$rootScope.tabSections = $rootScope.tabSections || {};
	$rootScope.tabSections.expenses = [
		{ title: "Expenses", app: "expenses" }
	];
}]);
