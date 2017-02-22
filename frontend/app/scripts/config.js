angular.module('config', [])

.constant('ENV', {solrEndpoint:'http://localhost:8983/solr/expertfinder/redlink',selectEndpoint:'http://localhost:8983/solr/expertfinder/select',treemapEndpoint:'http://localhost:8081/rest/expertfinder/treemap'})

;