app.controller('rankController', ['$scope', '$rootScope', '$http', 'Notification', '$location', function($scope, $rootScope, $http, Notification, $location) {
    $scope.key = '';
    $scope.synonyms = '';
    $scope.showSpinner = false;
if (!$rootScope.validUser) {
    $location.path('/login');
    window.location.reload();
}

    var uriPrefix = _appName_+"/rest/config";
    $http.get(_appName_+"/rest/categories/"+$rootScope.companyid).success(function(data) {
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

    $scope.updateDetails = function(updatedProductId) {
        $scope.showSpinner = true;
        var updatedRecord = $scope.filteredResults.filter(r => r.pid == updatedProductId);
        console.log(updatedRecord);
        var res = $http.post(uriPrefix + '/updateJson', updatedRecord);
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