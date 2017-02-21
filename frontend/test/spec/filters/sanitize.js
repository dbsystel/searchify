'use strict';

describe('Filter: sanitize', function () {

  // load the filter's module
  beforeEach(module('ExpertFinderApp'));

  // initialize a new instance of the filter before each test
  var sanitize;
  beforeEach(inject(function ($filter) {
    sanitize = $filter('sanitize');
  }));

  it('should return the input prefixed with "sanitize filter:"', function () {
    var text = 'angularjs';
    expect(sanitize(text)).toBe('sanitize filter: ' + text);
  });

});
