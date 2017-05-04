app.controller('sortingConfigController', ['$scope', '$rootScope', '$http', 'Notification', '$state', '$location', function($scope, $rootScope, $http, Notification, $state, $location) {
    $('.sidenav').removeClass('hidden');
    $('.main-page').removeClass('col-sm-12').addClass('col-sm-9');
    $('.main-page').addClass('white-background');

    /*if (!$rootScope.validUser) {
        $location.path('/login');
        window.location.reload();
    }*/
    $scope.names = ["Relevance", "Price low to high", "Price high to low", "New arrival", "Best selling", "Popular", "Discount Ascending", "Discount Descending"];

    var uriPrefix = _appName_+"/rest/config";
    $scope.getSortRank = function() {
        $http.get(uriPrefix + '/sortorder').success(function(data) {
            if (data.length && $scope.selectedSortItem && $scope.selectedCategory) {
                $scope.rankValue = "";
                var keepGoing = true;
                angular.forEach(data, function(dataItem) {
                    item = JSON.parse(dataItem);
                    if (keepGoing && item.category && item.sort && item.category == $scope.selectedCategory && item.sort == $scope.selectedSortItem && item.rank) {
                        $scope.rankValue = item.rank;
                        keepGoing = false;
                    }
                });
            }
        });
    }

    $scope.saveSortRankConfigs = function() {
        var sort = $scope.selectedSortItem;
        var rank = $scope.rankValue;
        var category = $scope.selectedCategory;
        var object = { category, sort, rank };
        var res = $http.post(uriPrefix + '/update/sortorder', JSON.stringify(object));
        res.success(function(data, status, headers, config) {
            $scope.message = data;
            $state.go($state.current, {}, { reload: true });
            Notification.success('Sort order ranking has been saved successfully');
        });
        res.error(function(data, status, headers, config) {
            console.log("Could not save sort order");
            Notification.error('Error in saving sort order ranking. Please try after some time');
        });
    }

}]);