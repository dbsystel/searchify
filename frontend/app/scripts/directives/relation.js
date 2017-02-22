'use strict';

/**
 * @ngdoc directive
 * @name ExpertFinderApp.directive:relation
 * @description
 * # relation
 */
angular.module('ExpertFinderApp')
  .directive('relation', function ($filter,$compile,$http,$window,ENV) {

    var html = '<h2 ng-bind="title"></h2><table><tr><th ng-repeat="head in data.heads" ng-bind="head.name"></th></tr><tr ng-repeat="dataitem in data.dataitems"><td ng-class="{link : head.link, external : head.link.type == \'external\'}" ng-click="click(head.link, dataitem)" ng-repeat="head in data.heads" ng-bind="adaptFilter(dataitem[head.value],head.filter)"></td></tr></table>';

    var data = {
      heads: [
        {name:"A",value:"source"},
        {name:"Title",value:"title"},
        {name:"Last update",value:"modified",filter:{name:'date'}}
      ],
      dataitems: [
        {source:"Jira / Service Hub", title:"Performance problem at loading assets module in workspace with many assets", update:12321321321}
      ]
    };

    var query = {
      'expert_application':{
          params:{defType:"edismax",rowst:3,sort:"modified desc",fqt:"doctype:Application AND dynamic_uri_expert:{id}"},
          heads: [
            {name:"Application",value:"title",link:{field:'id',type:'simple'}},
            {name:"Confluence Link",value:"url",link:{field:'id',type:'external'}}
          ]
        }
        ,'expert_project':{
          params:{defType:"edismax",rowst:3,sort:"modified desc",fqt:"doctype:Project AND dynamic_uri_expert:{id}"},
          heads: [
            {name:"Project",value:"title",link:{field:'id',type:'simple'}},
            {name:"Confluence Link",value:"url",link:{field:'id',type:'external'}}
          ]
        }
        ,'expert_qualification':{
          params:{defType:"edismax",rowst:3,sort:"modified desc",fqt:"doctype:Qualification AND dynamic_uri_expert:{id}"},
          heads: [
            {name:"Qualification",value:"title",link:{field:'id',type:'simple'}},
            {name:"Confluence Link",value:"url",link:{field:'id',type:'external'}}
          ]
        }
        ,'expert_knowledge':{
          params:{defType:"edismax",rowst:3,sort:"modified desc",fqt:"doctype:Knowledge AND dynamic_uri_expert:{id}"},
          heads: [
            {name:"Knowledge",value:"title",link:{field:'id',type:'simple'}},
            {name:"Confluence Link",value:"url",link:{field:'id',type:'external'}}
          ]
        }
        ,'expert_technology':{
          params:{defType:"edismax",rowst:3,sort:"modified desc",fqt:"doctype:Technology AND dynamic_uri_expert:{id}"},
          heads: [
            {name:"Technology",value:"title",link:{field:'id',type:'simple'}},
            {name:"Confluence Link",value:"url",link:{field:'id',type:'external'}}
          ]
        }
        ,'expert':{
          params:{defType:"edismax",rowst:3,sort:"modified desc",fqt:"id:{expert_ids}"},
          heads: [
            {name:"Expert",value:"title",link:{field:'id',type:'simple'}},
            {name:"Confluence Link",value:"url",link:{field:'id',type:'external'},filter:{name:'urireplace'}}
          ]
        }
        ,'parent_of':{
          params:{defType:"edismax",rowst:3,sort:"modified desc",fqt:"id:{narrower_ids}"},
          heads: [
            {name:"Narrower",value:"title",link:{field:'id',type:'simple'}},
            {name:"Confluence Link",value:"url",link:{field:'id',type:'external'}}
          ]
        }
        ,'application':{
          params:{defType:"edismax",rowst:3,sort:"modified desc",fqt:"id:{application_ids}"},
          heads: [
            {name:"Application",value:"title",link:{field:'id',type:'simple'}},
            {name:"Confluence Link",value:"url",link:{field:'id',type:'external'}}
          ]
        }
        ,'project':{
          params:{defType:"edismax",rowst:3,sort:"modified desc",fqt:"id:{project_ids}"},
          heads: [
            {name:"Project",value:"title",link:{field:'id',type:'simple'}},
            {name:"Confluence Link",value:"url",link:{field:'id',type:'external'}}
          ]
        }
        ,'qualification':{
          params:{defType:"edismax",rowst:3,sort:"modified desc",fqt:"id:{qualification_ids}"},
          heads: [
            {name:"Qualification",value:"title",link:{field:'id',type:'simple'}},
            {name:"Confluence Link",value:"url",link:{field:'id',type:'external'}}
          ]
        }
        ,'knowledge':{
          params:{defType:"edismax",rowst:3,sort:"modified desc",fqt:"id:{knowledge_ids}"},
          heads: [
            {name:"Knowledge",value:"title",link:{field:'id',type:'simple'}},
            {name:"Confluence Link",value:"url",link:{field:'id',type:'external'}}
          ]
        }
        ,'technology':{
          params:{defType:"edismax",rowst:3,sort:"modified desc",fqt:"id:{technology_ids}"},
          heads: [
            {name:"Technology",value:"title",link:{field:'id',type:'simple'}},
            {name:"Confluence Link",value:"url",link:{field:'id',type:'external'}}
          ]
        }
        
    };

    return {
      replace: true,
      restrict: 'E',
      scope: {
        item:'=',
        type:'=',
        title:'=',
        search:'&'
      },
      link: function postLink(scope, element, attrs) {

        scope.data = {};

        scope.adaptFilter = function(value, filter) {
          return filter ? $filter(filter.name)(value,filter.expression) : value;
        };

        scope.click = function(link,datatitem) {
          if(!link) return;

          if(link.type === "external") {
            var elink = $filter('urireplace')(datatitem[link.field]);
            $window.open(elink, '_blank');
          } else if(link.type === "simple") {
            scope.search({text:datatitem.title,id:datatitem.id});
          }  else {
              var text = $filter('complex')(datatitem[link.field],'name');
              var id = $filter('complex')(datatitem[link.field],'id');

              scope.search({text:text,id:id});
          }
        };

        var params = query[scope.type].params;

        var id = '"'+scope.item.id+'"';
        
        //Formating narrower relations
        if(scope.item.dynamic_uri_narrower) {
          var narrower_uris = scope.item.dynamic_uri_narrower.map( function(value){
           return '"' + value + '"';
          })

          var narrowerIds = '('+narrower_uris.join(' ')+')';
        }

        //Formating technology relations
        if(scope.item.dynamic_uri_technology) {
          var technology_uris = scope.item.dynamic_uri_technology.map( function(value){
           return '"' + value + '"';
          })

          var technologyIds = '('+technology_uris.join(' ')+')';
        }
        //Formating knowledge relations
        if(scope.item.dynamic_uri_knowledge) {
          var knowledge_uris = scope.item.dynamic_uri_knowledge.map( function(value){
           return '"' + value + '"';
          })

          var knowledgeIds = '('+knowledge_uris.join(' ')+')';
        }

        //Formating project relations
        if(scope.item.dynamic_uri_project) {
          var project_uris = scope.item.dynamic_uri_project.map( function(value){
           return '"' + value + '"';
          })

          var projectIds = '('+project_uris.join(' ')+')';
        }
        //Formating qualification relations
        if(scope.item.dynamic_uri_qualification) {
          var qualification_uris = scope.item.dynamic_uri_qualification.map( function(value){
           return '"' + value + '"';
          })

          var qualificationIds = '('+qualification_uris.join(' ')+')';
        }
        //Formating application relations
        if(scope.item.dynamic_uri_application) {
          var application_uris = scope.item.dynamic_uri_application.map( function(value){
           return '"' + value + '"';
          })

          var applicationIds = '('+application_uris.join(' ')+')';
        }
        //Formating expert relations
        if(scope.item.dynamic_uri_expert) {
          var expert_uris = scope.item.dynamic_uri_expert.map( function(value){
           return '"' + value + '"';
          })

          var expertIds = '('+expert_uris.join(' ')+')';
        }

        params.q = "*";
        params.fq = params.fqt.replace(/\{id\}/g,id)
                      .replace(/\{narrower_ids\}/g,narrowerIds)
                      .replace(/\{technology_ids\}/g,technologyIds)
                      .replace(/\{knowledge_ids\}/g,knowledgeIds)
                      .replace(/\{project_ids\}/g,projectIds)
                      .replace(/\{qualification_ids\}/g,qualificationIds)
                      .replace(/\{application_ids\}/g,applicationIds)
                      .replace(/\{expert_ids\}/g,expertIds);
        params.rows = params.rowst;

        var pagesize = query[scope.type].pageSize ? query[scope.type].pageSize : 25;

        function load(number){

          if(number) {
            params.rows = params.rows + number;
          }

          $http.get(ENV.selectEndpoint,{params:params}).then(

            function(data) {
              scope.data.heads = query[scope.type].heads;
              scope.data.dataitems = data.data.response.docs;

              if(scope.data.dataitems.length == 0) {
                return element.empty();
              }

              element.html(html);

              if(data.data.response.numFound > params.rows) {
                angular.element("<button>Show "+pagesize+" more of overall " + data.data.response.numFound + " results</button>").click(function(){$(this).addClass('loading');load(pagesize);}).appendTo(element);
              }

              $compile(element.contents())(scope);
              element.show();
            }
          );
        }

        load();

      }
    };
  });
