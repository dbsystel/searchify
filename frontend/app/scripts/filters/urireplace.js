'use strict';

/**
 * @ngdoc filter
 * @name ExpertFinderApp.filter:urireplace
 * @function
 * @description
 * # urireplace
 * Filter in the ExpertFinderApp.
 */
angular.module('ExpertFinderApp')
  .filter('urireplace', function () {
    return function (input) {
      return input ? input.replace("rest/prototype/1/user/non-system/","display/~") : input;
    };
  });
