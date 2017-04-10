  'use strict';
function LoginController ($scope,$http){
    $scope.loginData = {
        "userName": "",
        "password":""
    }
    $scope.signUpData = {
        "firstName": "",
        "lastName":"",
        "userName":"",
        "password":"",
        "confirmPassword":"",
        "dealerCode":""
    }

    $scope.loginUser = function () {
        console.log($scope.loginData);
    }
}