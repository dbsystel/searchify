'use strict';

/**
 * @ngdoc directive
 * @name ExpertFinderApp.directive:resultItem
 * @description
 * # resultItem
 */
angular.module('ExpertFinderApp')
  .directive('resultItem', function ($templateRequest, $compile, ENV, $rootScope,localStorageService) {

    var basePath = 'views/templates/';

    function getTemplateURL(item) {
      return basePath + item.dbsearch_doctype_s.toLowerCase() + '.html';
    }

    var linker = function(scope, element, attrs) {

      scope.imgBaseURL = ENV.imageEndpoint;

      function render() {
        var templateUrl = getTemplateURL(scope.item);

        $templateRequest(templateUrl).then(function(html){
          element.html(html);
          $compile(element.contents())(scope);
          element.show();
        });
      }

      scope.$watch('item',function(n){
        if(n) render();
      });

      scope.search = function(text,id) {
        scope.query({text:text,id:id})
      };

      /*scope.setUser = function() {
        localStorageService.set("ExpertFinderuser",scope.item.title);
        $rootScope.user = scope.item.title;
      };*/

      scope.getImage = function(item,default_icon) {
        return default_icon;
      };
    };

    return {
      link: linker,
      template:'<div class="single"></div>',
      scope: {
        item:'=',
        query:'&',
        highlight:'='
      },
      replace:true
    };
  });
