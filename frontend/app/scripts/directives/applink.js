'use strict';

/**
 * @ngdoc directive
 * @name ExpertFinderApp.directive:applink
 * @description
 * # applink
 */
angular.module('ExpertFinderApp')
  .directive('applink', function () {
    return {
      template: '<div></div>',
      restrict: 'E',
      scope: {
        value:"=",
        def:"="
      },
      link: function postLink(scope, element, attrs) {

        function createLink(link) {
          if(link.indexOf("http://192.168.56.101:8090")==0) {
            element.append("<a class='link external' href='http://confluence.kmiservicehub.com/display/~" + link.substring(60) + "' target='_blank'>Confluence</a> ")
          } else if(link.indexOf("http://192.168.56.101:8080")==0) {
            element.append("<a class='link external' href='http://jira.kmiservicehub.com/secure/ViewProfile.jspa?name=" + link.substring(52) + "' target='_blank'>Jira</a> ")
          } else if(link.indexOf("http://en.wikipedia")==0) {
            element.append("<a class='link external' href='" + link + "' target='_blank'>Wikipedia</a> ")
          }
        }

        if(scope.value) {
          for(var i in scope.value) {
            createLink(scope.value[i]);
          }
        } else {
          createLink(scope.def)
        }
      }
    };
  });
