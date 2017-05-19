app.controller('rankController', ['$scope', '$rootScope', '$http', 'Notification', '$location','$cookies', function($scope, $rootScope, $http, Notification, $location,$cookies) {
    $scope.key = '';
    $scope.synonyms = '';
    $scope.showSpinner = false;
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
    $scope.filter = function(filterText) {
        $scope.showSpinner = true;
        var url = uriPrefix + "/filter/" + filterText;

        $http.get(url).success(function(data) {
            console.log(data);
            $scope.filteredResults = [];

            $.each(data, function(index, value) {
                $scope.filteredResults.push(JSON.parse(value));
            });
            $scope.showSpinner = false;
        });
    };


    $scope.filterByKey = function(filterText) {
        $scope.showSpinner = true;
        var url = uriPrefix + "/filterByKey/" + filterText;

        $http.get(url).success(function(data) {
            console.log(data);
            $scope.filteredResults = [];

            $.each(data, function(index, value) {
                $scope.filteredResults.push(JSON.parse(value));
            });
            $scope.showSpinner = false;
        });
    };

    $scope.updateDetails = function(updatedProductId,rank,category) {
        $scope.showSpinner = true;
        var res = $http.post(uriPrefix + '/update/rankbyproduct/'+$cookies.get('fgt45hi7hfturtyrfgh')+'/'+rank+'/'+updatedProductId , category);
        res.success(function(data, status, headers, config) {
            Notification.success('Update successful');
            $scope.showSpinner = false;
        });
        res.error(function(data, status, headers, config) {
            Notification.log("Could not update");
            $scope.showSpinner = false;
        });
    };
}]);