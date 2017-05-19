app.controller('homeController', [ '$scope', '$rootScope','$http','$location','$cookies', function($scope,$rootScope, $http,$location,$cookies) {
	console.log("Home Controller");

	  if(!$cookies.get('fgt45hi7hfturtyrfgh')){
           $location.path('/login');
           window.location.reload();
        }

        $http.get(_appName_+"/rest/logourl/"+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.logourl = data['logo'];});
        $http.get(_appName_ + '/rest/config/panel/'+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.panelConfig = data;});
}]);