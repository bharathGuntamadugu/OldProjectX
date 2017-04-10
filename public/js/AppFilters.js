'use strict';

var appFilters = angular.module('appFilters', []);

appFilters.filter('showStatus',function(){
    return function(status){
        if(status == 1)
            return "Registered"
        return "Invited"
    }
});

appFilters.filter('sortDirectionIcon', function () {
  return function (columnName, sortPredicate, sortDirection) {
    if (columnName === sortPredicate) {
      if (sortDirection) {
        return "assets/images/down.png";
      }
      else {
        return "assets/images/up.png";
      }
    }
    else {
      return "assets/images/empty.png";
    }
  }
});

appFilters.filter('checkIfNull',function(){
    return function(name){
        if(name == null)
            return "DNA"
        return name;
    }
});