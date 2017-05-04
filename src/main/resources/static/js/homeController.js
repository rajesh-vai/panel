app.controller('homeController', [ '$scope', '$rootScope','$http','$location', function($scope,$rootScope, $http,$location) {
	console.log("Home Controller");

	  if(!$rootScope.validUser){
           $location.path('/login');
           window.location.reload();
        }
}]);