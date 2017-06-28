app.controller('sortingConfigController', ['$scope','$state', '$rootScope', '$http', 'Notification', '$state', '$location','$cookies', function($scope,$state, $rootScope, $http, Notification, $state, $location, $cookies) {
    $('.sidenav').removeClass('hidden');
    $('.main-page').removeClass('col-sm-12').addClass('col-sm-9');
    $('.main-page').addClass('white-background');

    /*if (!$cookies.get('fgt45hi7hfturtyrfgh')) {
        $location.path('/login');
        window.location.reload();
    }*/
    $scope.names = ["Relevance", "Price low to high", "Price high to low", "New arrival", "Best selling", "Popular", "Discount Ascending", "Discount Descending"];

    var uriPrefix = _appName_+"/rest/config";

    $http.get(uriPrefix+"/sorting/"+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) {
        $scope.sortRanks = data;
    });

    $scope.getSortRank = function(){
        $scope.rankValue = $scope.sortRanks[$scope.selectedSortItem];
    }

    $scope.saveSortRankConfigs = function() {
        var sort = $scope.selectedSortItem;
        var rank = $scope.rankValue;
        if(!sort){
            Notification.error('Please select a sort order');
            return false;
        }
        if(!rank){
            Notification.error('Please enter rank and save the changes');
            return false;
        }
        if(isNaN(rank)){
            Notification.error('Please enter a valid number');
            return false;
        }
        var res = $http.post(uriPrefix + '/update/sortorder/'+$cookies.get('fgt45hi7hfturtyrfgh')+'/'+sort +'/'+ rank);
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