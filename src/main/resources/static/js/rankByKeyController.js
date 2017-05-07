app.controller('rankByKeyController', ['$scope', '$rootScope', '$http', 'Notification', '$location', function($scope, $rootScope, $http, Notification, $location) {
    $scope.key = '';
    $scope.synonyms = '';
    $scope.showSpinner = false;
if (!$rootScope.validUser) {
    $location.path('/login');
    window.location.reload();
}

    var uriPrefix = _appName_+"/rest/config";
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

    $scope.updateDetails = function(updatedProductId, rank) {
        $scope.showSpinner = true;
        var res = $http.post(uriPrefix + '/update/rankbykey/'+$rootScope.companyid+'/'+rank, updatedProductId);
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