'use strict';

/**
 * @ngdoc directive
 * @name ExpertFinderApp.directive:highlightText
 * @description
 * # highlightText
 */
angular.module('ExpertFinderApp')
  .directive('highlightText', function ($filter) {
    return {
      restrict: 'E',
      link: function postLink(scope, element, attrs) {
        var em = scope.highlight[scope.item.id];

        var elem = angular.element("<div class='page'></div>");
        for(var i in em) {
          elem.append("<div>... " + $filter('sanitize')(em[i],true) + " ...</div>");
        }
        element.empty().append(elem);
      },
      scope: {
        item: '=',
        highlight: '='
      }
    };
  });
