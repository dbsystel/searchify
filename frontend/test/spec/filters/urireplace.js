'use strict';

describe('Filter: urireplace', function () {

  // load the filter's module
  beforeEach(module('ExpertFinderApp'));

  // initialize a new instance of the filter before each test
  var urireplace;
  beforeEach(inject(function ($filter) {
    urireplace = $filter('urireplace');
  }));

  it('should return the input prefixed with "urireplace filter:"', function () {
    var text = 'angularjs';
    expect(urireplace(text)).toBe('urireplace filter: ' + text);
  });

});
