angular.module('myApp', ['ngRoute','appFilters'])
.config(['$routeProvider', function ($routeProvider){
      $routeProvider.when('/home', {
          templateUrl: 'assets/templates/Home.html',
          controller: 'IndexController'});
      $routeProvider.when('/forms', {
          templateUrl: 'assets/templates/Forms.html',
          controller: 'FormsController'});
      $routeProvider.when('/settings', {
          templateUrl: 'assets/templates/Settings.html',
          controller: 'SettingsController'});
    $routeProvider.when('/manageUsers',{
        templateUrl: 'assets/templates/ManageUsers.html',
        controller:'ManageUsersController'
    });
      $routeProvider.otherwise({redirectTo: '/'})
}]).run(function ($rootScope, $http) {
    $rootScope.accountInfo = [];
    $rootScope.activeUsers = [];
    $rootScope.invitedUsers = [];
});