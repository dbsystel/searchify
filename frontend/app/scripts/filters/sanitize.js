'use strict';

/**
 * @ngdoc filter
 * @name ExpertFinderApp.filter:sanitize
 * @function
 * @description
 * # sanitize
 * Filter in the ExpertFinderApp.
 */
angular.module('ExpertFinderApp')
  .filter("sanitize", ['$sce', function($sce) {
    return function(htmlCode, escaped){
      if(escaped) {
        var textArea = document.createElement('textarea');
        textArea.innerHTML = htmlCode;
        htmlCode = textArea.value.replace(/\/n/g,"<br>");
      }

      if(htmlCode.indexOf("This is the navigation link for moving toward in this page.") == 0) {
        htmlCode = htmlCode.substring(59);
      }

      return $sce.trustAsHtml(htmlCode);
    }
  }]);
