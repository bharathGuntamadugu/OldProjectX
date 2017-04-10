  'use strict';
function SettingsController ($scope,$rootScope, $http){
    $scope.showPasswordChange = false;
    $scope.showAccountInfo = true;

    $scope.accountInfo = [];

        if($rootScope.accountInfo.length == 0){
          $http.get("/getUserInfo")
          .success(function(data){
             $scope.accountInfo = data;
             $rootScope.accountInfo = data;
            console.log(data);
          })
          .error(function(data,status){
            console.error("GET /userInfo Failed " + status)
          });
        }else {
            $scope.accountInfo = $rootScope.accountInfo;
        }

        $scope.showDiv = function(type) {
            hideAllContentDiv();
            switch (type) {
                case "accountInfo":
                    $scope.showAccountInfo = true;
                    $scope.showPasswordChange = false;
                break;
                case "passwordChange":
                    $scope.showPasswordChange = true;
                    $scope.showAccountInfo = false;
                    break;
                default:
                break;
            }
        }

        function hideAllContentDiv() {
            $scope.showPasswordChange = false;
            $scope.showAccountInfo = false;
        }

        $scope.changePassword = function(){
            if($scope.newPassword1 != $scope.newPassword2){
                setOutputMessage("Entered Passwords don't match..!!","failureOutput")
            }else{
                $http.post("/password/"+$scope.currentPassword+"/"+$scope.newPassword1)
                .success(function(data){
                    switch(data.status) {
                       case "Success":
                        setOutputMessage("Password Successfully changed..!!","successOutput");
                       break;
                       case "Failure":
                        setOutputMessage("Incorrect current password..!!","failureOutput");
                       break;
                      case "Error":
                       setOutputMessage("Error while updating the password..!!","failureOutput");
                      break;
                       default:
                        console.error("Wrong status message " + data.status);
                       break;
                    }
                })
                .error(function(data,status){
                    console.error("/POST changePassword Failed "+ status);
                });
            }
            $scope.currentPassword = '';
            $scope.newPassword1 = '';
            $scope.newPassword2 = '';
            setTimeout( function(){
                removeClassTagFromFormOutput();
              }
             , 4000 );
        }

        function setOutputMessage(value, className) {
        removeClassTagFromFormOutput();
         $(".formOutput").text(value)
         $(".formOutput").addClass(className);
        }

        function removeClassTagFromFormOutput(){
            $(".formOutput").removeClass("successOutput");
            $(".formOutput").removeClass("failureOutput");
            $(".formOutput").text("");
        }

}