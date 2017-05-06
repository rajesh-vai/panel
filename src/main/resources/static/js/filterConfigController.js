app.controller('filterConfigController', ['$scope', '$rootScope', '$http', 'Notification', '$state', '$location', function($scope, $rootScope, $http, Notification, $state, $location) {

    $('.sidenav').removeClass('hidden');
    $('.main-page').removeClass('col-sm-12').addClass('col-sm-9');
    $('.main-page').addClass('white-background');

    /*if (!$rootScope.validUser) {
        $location.path('/login');
        window.location.reload();
    }*/

    var uriPrefix =  _appName_+"/rest/config";
    $http.get(uriPrefix + '/filters').success(function(data) {
        $scope.registeredStopwords = data;
        $scope.names = ["filter"];
        if (data.length) {
            $scope.concatenatedStopwords = data.join();
        }
    });

    $scope.saveStopWords = function() {
        var stopWordsList = $scope.concatenatedStopwords.split(",");
        var res = $http.post(uriPrefix + '/update/filters', JSON.stringify(stopWordsList));
        res.success(function(data, status, headers, config) {
            $scope.message = data;
            $state.go($state.current, {}, { reload: true });
            Notification.success('Sort order saved successfully');
        });
        res.error(function(data, status, headers, config) {
            console.log("Could not save sort order");
            Notification.error('Error in saving sort order');
        });
    }

}]);