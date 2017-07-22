app.controller('loginController', [ '$scope', '$rootScope' , '$http', '$location' ,'$cookies', function($scope,$rootScope, $http, $location,$cookies) {
	console.log("Login Controller");
    $scope.showlogin = true;
    $scope.companyName = undefined;
	$scope.validateLogin = function(){
	    var url = _appName_+"/rest/validate/companyName/"+$scope.companyName;
	    $rootScope.password = $scope.password;

	    $http.get(url).success(function(data) {

           if(data['isValid'] == "true"){
                if($rootScope.password != data['securitykey']){
                alert("invalid password");
                return false;
                }


                $rootScope.companyid=data['companyid'];

                $cookies.put('fgt45hi7hfturtyrfgh', data['companyid']);

                $http.get(_appName_+"/rest/logourl/"+$rootScope.companyid).success(function(data) { $rootScope.logourl = data['logo'];});
                $http.get(_appName_ + '/rest/config/panel/'+$rootScope.companyid).success(function(data) { $rootScope.panelConfig = data;});

                $location.path( "/panel/home" );
            }
            else{
            alert("invalid company name");
            return false;
            }
        });


	}


   $scope.keyPress = function(keyEvent) {
    if (keyEvent.which === 13)
        $scope.validateLogin();
    }

}]);