'use strict';

/**
 * @ngdoc overview
 * @name ExpertFinderApp
 * @description
 * # ExpertFinderApp
 *
 * Main module of the application.
 */
angular
  .module('ExpertFinderApp', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch',
    'config',
    'angular-md5',
    'LocalStorageModule',
    'fsm',
    'wu.staticGmap'
  ])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/main.html',
        controller: 'MainCtrl',
        controllerAs: 'main',
        reloadOnSearch: false
      })
      .otherwise({
        redirectTo: '/'
      });
  });
