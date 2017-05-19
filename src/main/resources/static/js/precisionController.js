app.controller('precisionController', ['$scope', '$rootScope', '$http', 'Notification', '$state', '$location','$cookies', function($scope, $rootScope, $http, Notification, $state, $location,$cookies) {
    $('.sidenav').removeClass('hidden');
    $('.main-page').removeClass('col-sm-12').addClass('col-sm-9');
    $('.main-page').addClass('white-background');


    if (!$cookies.get('fgt45hi7hfturtyrfgh')) {
        $location.path('/login');
        window.location.reload();
    }
$http.get(_appName_+"/rest/logourl/"+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.logourl = data['logo'];});
$http.get(_appName_ + '/rest/config/panel/'+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.panelConfig = data;});
    var uriPrefix = _appName_+"/rest/config";

    $http.get(_appName_+"/rest/categories/"+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) {
        $scope.categories = data;
    });


    $http.get(uriPrefix + '/precision/'+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) {
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
        var res = $http.post(uriPrefix + '/update/precision/'+$cookies.get('fgt45hi7hfturtyrfgh')+'/'+category +'/'+ precision);
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