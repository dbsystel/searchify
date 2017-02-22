'use strict';

/**
 * @ngdoc function
 * @name ExpertFinderApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the ExpertFinderApp
 */
angular.module('ExpertFinderApp')
  .controller('MainCtrl', function ($scope,$http,ENV,$timeout,$rootScope,localStorageService,$location,$window,$q) {

    $scope.loaded = false;
    $scope.filterLoaded = false;

    $scope.showExamples = false;

    var lang = "en";

    var currentSearch = undefined;

    //$rootScope.user = localStorageService.get("ExpertFinderuser");

    var groupLimit = 6;

    var params = {
      "defType": "edismax",
      "qf": "relation_expert_of_txt^20.0 relation_application_txt^3.0 relation_expert_txt^3.0 relation_project_txt^3.0 relation_qualification_txt^3.0 relation_parent_of_txt^3.0 relation_technology_txt^3.0 relation_knowledge_txt^3.0 dbsearch_doctype_s^10.0 dbsearch_title_t^4.0 dbsearch_content_t^0.4",
      "group": true,
      "group.field": "dbsearch_doctype_s",
      "group.limit": 6,
      "bq":"dbsearch_doctype_s:Person^10.0",
      "fl":"*,score",
      //"pf":"title_en~4^10.0 keyword_en~4^3.0 type^50.0 content_en^2.0 text", TODO
      //"qs":"10",
      "bf":"product(recip(ms(NOW/DAY,dbsearch_update_tdt),3.16e-11,20,10),dbsearch_pagerank_d,10)",
      "spellcheck":true,
      "spellcheck.dictionary":"spellcheck",
      "spellcheck.maxResultsForSuggest":1,
      "json.nl":"map",
      "q.alt":"*",
      "hl":true,
      "hl.fl":"content",
      "hl.snippets":2,
      "hl.mergeContiguous":true,
      "hl.alternateField":"description",
      "hl.fragsize":300,
      "hl.maxAlternateFieldLength":500,
      "autocomplete":true,
      "q.op":"OR",
      "mm":"67%"
    };

    var timeout;

    var canceler = $q.defer();

    function setTypeahead(autocomplete,input,title) {
      if(input && title) {
        if(autocomplete && autocomplete.length > 0) {
          if(input.length == autocomplete[0].length && autocomplete.length > 1) {
            $scope.typeahead = input + autocomplete[1].substring(input.length);
          } else {
            $scope.typeahead = input + autocomplete[0].substring(input.length);
          }
        } else if(title.toLowerCase().indexOf(input.toLowerCase()) == 0) {
          $scope.typeahead = input + title.substring(input.length);
        } else {
          $scope.suggestion = title;
        }
      } else {
        if(autocomplete && autocomplete.length > 0) {
          $scope.typeahead = autocomplete[0];
        }
      }
    }

    $scope.moreResults = undefined;

    $scope.search = function(input,elevation,spellcheck){

      //reset filters
      if(elevation) {
        for(var i in $scope.filters) {
          $scope.selectedFilters[i] =  $scope.filters[i][0];
        }
      }

      var facets = [];

      var setGroupLimit = false;

      for(var i in $scope.selectedFilters) {
        if ($scope.selectedFilters[i].facet) {
          if($scope.selectedFilters[i].field == 'dbsearch_doctype_s') {
            setGroupLimit = true;
          }
          facets.push($scope.selectedFilters[i].facet);
        }
      }
      //set params
      if(facets.length > 0) {
        params.fq = facets;
      } else {
        params.fq = undefined;
      }

      //HACK^3
      if(setGroupLimit) {
        params["group.limit"] = 50;
      } else {
        params["group.limit"] = 6;
      }

      $scope.typeahead = undefined;
      $scope.suggestion = undefined;
      $scope.moreResult = undefined;

      canceler.resolve();
      canceler = $q.defer();

      $scope.spellcheckResult = undefined;

      if(input && !spellcheck) $scope.input = input;

      if(facets.length == 0 && $scope.input == "") {
        $scope.result = undefined;
        $scope.selected = undefined;
        return;
      }

      if(elevation) {
        params.elevateIds = elevation;
      } else {
        params.elevateIds = undefined;
      }

      if(!spellcheck) {
        params.q = $scope.input;
        $location.search('q',params.q);
        $location.search('e',params.elevateIds);
        currentSearch = $location.search();
      } else {
        params.q = input;
      }

      //if($rootScope.user) params.user = $rootScope.user;

      $http.get(ENV.solrEndpoint,{params:params,timeout: canceler.promise}).then(
        function(data) {
          $scope.result = data.data;

          $timeout.cancel(timeout);

          $scope.loaded = true;
          $scope.filterLoaded = true;

          if($scope.result.grouped.dbsearch_doctype_s.matches) {
            $scope.selected = $scope.result.grouped.dbsearch_doctype_s.groups[0].doclist.docs[0];
            setTypeahead($scope.result.autocomplete, $scope.input,$scope.selected.title ? $scope.selected.title : $scope.selected.url);
          } else {
            $scope.selected = undefined;
            setTypeahead($scope.result.autocomplete);
          }

          if(spellcheck && $scope.result.grouped.dbsearch_doctype_s.matches) {
            $scope.spellcheckResult = input;
            $scope.moreResult = undefined;
          }

          if($scope.result.spellcheck && !angular.equals($scope.result.spellcheck.suggestions,{}) && !spellcheck) {
            var inp = params.q.toLowerCase();

            angular.forEach($scope.result.spellcheck.suggestions,function(suggestion,key){
              inp = inp.replace(new RegExp(key, "ig"),suggestion.suggestion[0]);
            });

            if(!$scope.result.grouped.dbsearch_doctype_s.matches) {
              $scope.search(inp,undefined,true);
            }
            $scope.moreResult = inp;
          }
        }
      )
    };

    $scope.key = function($event) {
      if ($event.keyCode == 39) {
        if($scope.typeahead) $scope.input = $scope.typeahead;
      } else if ($event.keyCode != 38 && $event.keyCode != 40) {
        $scope.typeahead = undefined;
        $scope.suggestion = undefined;
      }
    };


    $scope.select = function(doc) {
      $scope.selected = doc;
    };


    $scope.recentSearches = [];

    var aparams = {q:"*:*",rows:4,sort:"datetime desc",wt:"json"};

    //if($rootScope.user) {
    //  aparams.fq="user:\""+$rootScope.user+"\"";
    //}

    $rootScope.$on('$locationChangeSuccess', function() {
      if(currentSearch !== $location.search()) {
        currentSearch = $location.search();
        $timeout.cancel(timeout);
        $window.location.reload();
      }
    });

    $scope.logout = function() {
      //$rootScope.user = undefined;
      localStorageService.set("ExpertFinderuser", undefined);
      $window.location.reload();
    };

    //load on pageload
    var sparams = $location.search();

    if(sparams.q) {
      $scope.search(sparams.q,sparams.e);
    }

    $scope.showExamples = sparams.examples ? true : false;

    $scope.getClassForType = function(type) {
      var t = type.toLowerCase().substring(type.indexOf('/')+1);
      if(t==='container') return 'container-item';
      return t;
    };

    var typeParams = {
      "q": "*:*",
      "facet.field": [
        //"type",
        "dbsearch_doctype_s",
        "dbsearch_language_s"
      ],
      "indent": "true",
      "f.type.facet.mincount": "10",
      "rows": "0",
      "wt": "json",
      "facet": "true",
      "json.nl":"map",
      "f.language.facet.mincount": "10"
    };

    $scope.filters = [
      [
        {name:"Any date"},
        {name:"Past hour",facet:"modified:[NOW-1HOUR TO NOW]"},
        {name:"Past 24 hours",facet:"modified:[NOW-1DAY TO NOW]"},
        {name:"Past week",facet:"modified:[NOW-7DAYS TO NOW]"},
        {name:"Past month",facet:"modified:[NOW-1MONTH TO NOW]"},
        {name:"Past year",facet:"modified:[NOW-1YEAR TO NOW]"}
      ]
    ];

    $scope.selectedFilters = {};

    $http.get(ENV.selectEndpoint,{params:typeParams}).then(
      function(data) {
        var facets = data.data.facet_counts.facet_fields;

        for(var i in facets) {
          var filter = [{name:"All " + i.charAt(0).toUpperCase() + i.slice(1) + "s"}];
          for(var j in facets[i]) {
            filter.push({name:j,facet:i+":\""+j+"\"",field:i});
          }
          $scope.filters.push(filter);
        }
        for(var i in $scope.filters) {
          $scope.selectedFilters[i] =  $scope.filters[i][0];
        }
        $scope.filterLoaded = true;
      }
    );

    $scope.selectFacets = function() {
      $scope.input = null;
      $scope.search();
    }

  });
