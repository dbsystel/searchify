'use strict';

/**
 * @ngdoc directive
 * @name ExpertFinderApp.directive:gravatar
 * @description
 * # gravatar
 */
angular.module('ExpertFinderApp')
  .directive('gravatar', function (md5) {
    return {
      restrict: 'E',
      scope: {
        item: '='
      },
      link: function postLink(scope, element, attrs) {
        var email = scope.item.dbsearch_author_email_s;

        if(email) {
          var hash = md5.createHash(email[0]);
          element.html('<img class="gravatar" src="https://secure.gravatar.com/avatar/'+hash+'?d=mm&s=50">');
        }
      }
    };
  });
