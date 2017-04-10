
  'use strict';
function IndexController($scope,$route,$location,$rootScope,$http) {

    $scope.$location = $location;

       $scope.accountInfo = [];
       $scope.showManageUsers = false;
        if($rootScope.accountInfo.length == 0){
          $http.get("/getUserInfo")
          .success(function(data){
             $scope.accountInfo = data;
             $rootScope.accountInfo = data;
             showManageUsersMenu();
          })
          .error(function(data,status){
            console.error("GET /getUserInfo Failed " + status)
          });
        }else {
            $scope.accountInfo = $rootScope.accountInfo;
            showManageUsersMenu();
        }


function showManageUsersMenu(){
    if($scope.accountInfo.role == "Admin"){
        $scope.showManageUsers = true;
    }
}

};