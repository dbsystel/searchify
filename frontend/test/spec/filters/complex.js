'use strict';

describe('Filter: complex', function () {

  // load the filter's module
  beforeEach(module('ExpertFinderApp'));

  // initialize a new instance of the filter before each test
  var complex;
  beforeEach(inject(function ($filter) {
    complex = $filter('complex');
  }));

  it('should return the input prefixed with "complex filter:"', function () {
    var text = 'angularjs';
    expect(complex(text)).toBe('complex filter: ' + text);
  });

});
