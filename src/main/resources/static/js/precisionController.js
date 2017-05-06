app.controller('precisionController', ['$scope', '$rootScope', '$http', 'Notification', '$state', '$location', function($scope, $rootScope, $http, Notification, $state, $location) {
    $('.sidenav').removeClass('hidden');
    $('.main-page').removeClass('col-sm-12').addClass('col-sm-9');
    $('.main-page').addClass('white-background');


    if (!$rootScope.validUser) {
        $location.path('/login');
        window.location.reload();
    }

    var uriPrefix = _appName_+"/rest/config";

    $http.get(_appName_+"/rest/categories/"+$rootScope.companyid).success(function(data) {
        $scope.categories = data;
    });


    $http.get(uriPrefix + '/precision/'+$rootScope.companyid).success(function(data) {
        $scope.precisionMap= data;
    });


    $scope.savePrecisionConfigs = function() {
        var precision = $scope.rankValue;
        var number = /^[0-9]+$/;
        if(!precision.match(number)){
            Notification.error('Please enter a valid number');
            return;
        }
        var category = $scope.selectedCategory;
        var updatedValue = {};
        updatedValue[category] = precision ;
        var res = $http.post(uriPrefix + '/update/precision/'+$rootScope.companyid+'/'+category +'/'+ precision);
        res.success(function(data, status, headers, config) {
            $scope.message = data;
            $state.go('panel.settings.precision');
            Notification.success('Precision configuration has been saved successfully');
        });
        res.error(function(data, status, headers, config) {
            console.log("Could not save sort order");
            Notification.error('Error in saving precision settings. Please try after some time');
        });
    }


    $scope.updatePrecision = function(){
        $scope.rankValue = $scope.precisionMap[$scope.selectedCategory];
    }

}]);