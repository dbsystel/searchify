'use strict';

describe('Filter: niceName', function () {

  // load the filter's module
  beforeEach(module('ExpertFinderApp'));

  // initialize a new instance of the filter before each test
  var niceName;
  beforeEach(inject(function ($filter) {
    niceName = $filter('niceName');
  }));

  it('should return the input prefixed with "niceName filter:"', function () {
    var text = 'angularjs';
    expect(niceName(text)).toBe('niceName filter: ' + text);
  });

});
