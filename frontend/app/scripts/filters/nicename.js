'use strict';

/**
 * @ngdoc filter
 * @name ExpertFinderApp.filter:niceName
 * @function
 * @description
 * # niceName
 * Filter in the ExpertFinderApp.
 */
angular.module('ExpertFinderApp')
  .filter('niceName', function () {

    var map = {
      "en":"English",
      "de":"German",
      "ja":"Japanese",
      "cs":"Czech"
    };

    return function (input) {
      if(map[input]) return map[input];

      var i = input.lastIndexOf("/") + 1;
      var j = input.lastIndexOf(".") + 1;

      return input.substring(Math.max(i,j));
    };
  });
