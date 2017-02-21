'use strict';

/**
 * @ngdoc filter
 * @name ExpertFinderApp.filter:complex
 * @function
 * @description
 * # complex
 * Filter in the ExpertFinderApp.
 */
angular.module('ExpertFinderApp')
  .filter('complex', function () {
    return function (input, value) {

      if(!input) return input;

      if(input instanceof Array) input = input[0];

      var val = input.split(" ::: ");

      return value == 'name' ? (val[1] == "null" ? val[0] : val[1]) : val[0];
    };
  });
