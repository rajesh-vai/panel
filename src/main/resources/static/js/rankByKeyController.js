app.controller('rankByKeyController', ['$scope','$state', '$rootScope', '$http', 'Notification', '$location','$cookies', function($scope,$state, $rootScope, $http, Notification, $location,$cookies) {
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
    $scope.filter = function(filterText) {
        $scope.showSpinner = true;
        var url = uriPrefix + "/filter/" + filterText;

        $http.get(url).success(function(data) {
            console.log(data);
            $scope.filteredResults = [];

            $.each(data, function(index, value) {
                $scope.filteredResults.push(JSON.parse(value));
            });
            $scope.filteredResults = _.sortBy( $scope.filteredResults, 'rank');
            $scope.showSpinner = false;
        });
    };


    $scope.filterByKey = function(filterText) {
        if(!filterText){
            Notification.error("Please specify a keyword to search");
            return false;
        }
        $scope.showSpinner = true;
        var url = uriPrefix + "/filterByKey/" + filterText;

        $http.get(url).success(function(data) {
            console.log(data);
            $scope.filteredResults = [];

            $.each(data, function(index, value) {
                $scope.filteredResults.push(JSON.parse(value));
            });
//            $scope.filteredResults = _.sortBy( $scope.filteredResults, 'rank');
            if(!_.has($scope.filteredResults[0], "popularity")){
                Notification.error("Feed does not have the popularity field. Rank cannot be updated");
                $scope.filteredResults=[];
                $scope.showSpinner = false;
                return false;
            }
            else {
                $scope.filteredSortedResults = _.sortBy( $scope.filteredResults, 'popularity');
                $scope.filteredSortedResults = $scope.filteredSortedResults.reverse();
                $scope.filteredResults= $scope.filteredSortedResults;
            }
            $scope.showSpinner = false;
        });
    };

    $scope.updateDetails = function(updatedProductId, rank, popularity) {
        if(!rank){
            Notification.error("Please specify the rank to be updated");
            return false;
        }
        if(isNaN(rank)){
            Notification.error("Only number is allowed for rank field");
            return false;
        }
        var popularityToBeUpdated;
        if(rank==1){
            popularityToBeUpdated = $scope.filteredSortedResults[0].popularity + 1;
        }
        else {
            popularityToBeUpdated = Math.floor(($scope.filteredSortedResults[rank-2].popularity + $scope.filteredSortedResults[rank-1].popularity)/2);
        }

        $scope.showSpinner = true;
        var res = $http.post(uriPrefix + '/update/rankbykey/'+$cookies.get('fgt45hi7hfturtyrfgh')+'/'+rank+'/'+popularityToBeUpdated, updatedProductId);
        res.success(function(data, status, headers, config) {
            Notification.success('Rank has been updated successfully');
            $scope.showSpinner = false;
        });
        res.error(function(data, status, headers, config) {
            Notification.error("Error in updating the rank");
            $scope.showSpinner = false;
        });
    };

    $scope.deleteRankingByKey = function(pid) {

            var res = $http.post(uriPrefix + '/delete/rankbykey/'+$cookies.get('fgt45hi7hfturtyrfgh')+'/'+pid);
            res.success(function(data, status, headers, config) {
                $scope.message = data;
                $scope.openAddSynonym = false;
                $scope.registeredLinks = data;
                $state.go($state.current, {}, { reload: true });
                Notification.success('Ranking deleted successfully');
            });
            res.error(function(data, status, headers, config) {
                console.log("Could not delete synonym");
                Notification.error('Error in deleting Rank');
            });
        }
}]);