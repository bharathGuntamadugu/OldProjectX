function ManageUsersController($scope,$rootScope,$http,$filter){
    $scope.showActiveUsers = true;
    $scope.showInviteNewUser = false;
    $scope.activeUsers = [];
    $scope.userTypes= ['Admin','Manager','Employee'];
    $scope.userType = "";
    $scope.search = {};
     $scope.activeUsersFiltered = [];
     $scope.invitedUserCount = 0;
     $scope.registeredUserCount = 0;
     $scope.inviteExpiredUserCount = 0;
    if($rootScope.activeUsers.length == 0){
        showActiveUsers();
    }else{
        $scope.activeUsers = $rootScope.activeUsers;
         $scope.invitedUserCount = 0;
         $scope.registeredUserCount = 0;
         $scope.inviteExpiredUserCount = 0;
        angular.forEach($scope.activeUsers, function (tuple) {
            (tuple.status == "Registered")? $scope.registeredUserCount++ : ((tuple.status == "Invited")? $scope.invitedUserCount++ : $scope.inviteExpiredUserCount++);
        });
        filterData();
    }

    $scope.showDiv = function(name){
        switch(name){
            case "activeUsers":
                 hideAllContentDivs();
                 $scope.showActiveUsers = true;
                 showActiveUsers();
            break;
            case "inviteNewUser":
                hideAllContentDivs();
                $scope.showInviteNewUser = true;
            break;
            default:
                console.error("Invalid showDiv command: " + name);
            break;
        }
    }

    $scope.userTypeSel = function(value) {
        $scope.userType = value;
    }

    $scope.inviteUser = function () {
        console.log($scope.userType);
        $http.post("/inviteNewUser/"+$scope.email+"/"+$scope.userType)
        .success(function(data){
            removeClassTagFromFormOutput();
            switch(data.status){
                case "Exists":
                    $(".formOutput").text("Error! - User already exists..!!")
                    $(".formOutput").addClass("alert-danger");
                break;
                case "Success":
                    $(".formOutput").text("Success! - Invitation successfully send to user..!!")
                    $(".formOutput").addClass("alert-success");
                break;
                case "Failure":
                     $(".formOutput").text("Error! - Failed to invite user..!!")
                     $(".formOutput").addClass("alert-danger");
                break;
                default:
                    console.error("unknown status " + data.status);
                break;
            }
            $scope.email = '';
            setTimeout( function(){
                removeClassTagFromFormOutput();
              }
             , 5000 );
        })
        .error(function(data,status){
            console.error("POST /inviteNewUser failed "+ status);
        });
        }

    $scope.sortColumn  = function(name,value){
        $scope.activeUsersFiltered = $filter('orderBy')($scope.activeUsersFiltered,name,value);
    };

    $scope.filterBy = function(name, value){
       if (!$scope.search) {
            $scope.search = {};
        }
        $scope.search[name] = value;
        filterData();
    }

    $scope.clearFilters = function(){
        $scope.search = {};
        $(".activeUsersFilters").find("input").val("");
        filterData();
    }

    //local functions
    function hideAllContentDivs(){
        $scope.showActiveUsers = false;
        $scope.showInviteNewUser = false;
    }

    function removeClassTagFromFormOutput(){
        $(".formOutput").removeClass("alert-success");
        $(".formOutput").removeClass("alert-danger");
        $(".formOutput").text("");

    }

    function showActiveUsers() {
     $http.get("/getActiveUsers")
        .success(function(data){
             $scope.invitedUserCount = 0;
             $scope.registeredUserCount = 0;
             $scope.inviteExpiredUserCount = 0;
            angular.forEach(data, function (tuple) {
                tuple.firstName = (tuple.firstName == null)?"DNA":tuple.firstName;
                tuple.lastName = (tuple.lastName == null)?"DNA":tuple.lastName;
                (tuple.status == "Registered")? $scope.registeredUserCount++ : ((tuple.status == "Invited")? $scope.invitedUserCount++ : $scope.inviteExpiredUserCount++);
            });
            $rootScope.activeUsers = data;
            $scope.activeUsers = data;
            filterData();
        })
        .error(function(data,status){
            console.error("GET /getActiveUsers Failed "+ status);
         });
    }

    function filterData() {
        $scope.activeUsersFiltered = $filter('filter')($scope.activeUsers,$scope.search)
        export2csv();
    }

    function export2csv(){
        var text = '"Organization","Dealer Code","Role","Email","First Name","Last Name","Status"' + "\n";
        angular.forEach($scope.activeUsers,function(tuple){
            text += (angular.isDefined(tuple.orgName)?tuple.orgName:"Not Specified")+",";
            text += (angular.isDefined(tuple.dealerCode)?tuple.dealerCode:"Not Specified")+",";
            text += (angular.isDefined(tuple.role)?tuple.role:"Not Specified")+",";
            text += (angular.isDefined(tuple.email)?tuple.email:"Not Specified")+",";
            text += (angular.isDefined(tuple.firstName)?tuple.firstName:"Not Specified")+",";
            text += (angular.isDefined(tuple.lastName)?tuple.lastName:"Not Specified")+",";
            text += (angular.isDefined(tuple.status)?tuple.status:"Not Specified")+"\n";
        });
        var exportLink = document.getElementById('export2scv');
        exportLink.setAttribute('href', 'data:text/csv;base64,' + window.btoa(text));
    }
}