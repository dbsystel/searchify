'use strict';

describe('Directive: autosize', function () {

  // load the directive's module
  beforeEach(module('ExpertFinderApp'));

  var element,
    scope;

  beforeEach(inject(function ($rootScope) {
    scope = $rootScope.$new();
  }));

  it('should make hidden element visible', inject(function ($compile) {
    element = angular.element('<autosize></autosize>');
    element = $compile(element)(scope);
    expect(element.text()).toBe('this is the autosize directive');
  }));
});
