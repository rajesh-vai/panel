app.controller('loginController', [ '$scope', '$rootScope' , '$http', '$location', function($scope,$rootScope, $http, $location) {
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




                $rootScope.validUser = true;
                $rootScope.companyid=data['companyid'];

                $http.get(_appName_+"/rest/logourl/"+$rootScope.companyid).success(function(data) {
                                        $rootScope.logourl = data['logo'];
                                    });
                $location.path( "/panel/dashboard" );
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