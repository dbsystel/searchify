angular.module('config', [])

  .factory('ENV', ['$window', function ENVFactory($window) {
    var base = '';
    if ($window.location.port == 9000) { // when started with grunt serve
      base = '//' + $window.location.hostname + ':8983/';
    }

    return {
      solrEndpoint:base + 'solr/main/redlink',
      selectEndpoint:base + 'solr/main/select',
      imageEndpoint:base + 'solr/main/tn/',
      analysisEndpoint:base + 'solr/analytics/select',
      storeEndpoint:base + 'solr/main/store'
    };
  }])

;