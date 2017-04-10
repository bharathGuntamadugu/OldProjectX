/**
 * Created by bhargav on 3/28/15.
 */
'use strict';
function FormsController($scope,$rootScope, $http) {
    $(".iframe").colorbox({iframe:true, width:"80%", height:"100%"});
    $(".group").colorbox({rel:'group'});
    $("#dobInput").datepicker();
    $(function () {
        $('[data-toggle="tooltip"]').tooltip()
    })
    $scope.cusForm=true;
    $scope.vehForm=false;
    $scope.allForm=false;
    $scope.showHide=function($divName) {
        if ($divName == 1) {
            $scope.cusForm = true;
            $scope.vehForm = false;
            $scope.allForm = false;
        }
        else if ($divName == 2) {
            $scope.cusForm = false;
            $scope.vehForm = true;
            $scope.allForm = false;
        }
        else if ($divName == 3) {
            $scope.cusForm = false;
            $scope.vehForm = false;
            $scope.allForm = true;
        }
    }
    $scope.customerDetailsSubmit = function() {
        var dob=$("#dobInput").val();
        if(dob.length<1)
        {
            dob="a";
        }
        $http.get("/forms/customerDetails/"+JSON.stringify($scope.custForm)+"/"+dob)
            .success(function(data){
                if(data.toLowerCase()== "\"success\"")
                        alert("Success")
                else
                        alert("Data failed to save in DB")

            })
                .error(function() {
                alert("Post Failed")
            }

        )
    };
}
