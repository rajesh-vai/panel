app.controller('rankByKeyController', ['$scope', '$rootScope', '$http', 'Notification', '$location','$cookies', function($scope, $rootScope, $http, Notification, $location,$cookies) {
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
            $scope.showSpinner = false;
        });
    };

    $scope.updateDetails = function(updatedProductId, rank) {
        if(!rank){
            Notification.error("Please specify the rank to be updated");
            return false;
        }
        if(isNaN(rank)){
            Notification.error("Only number is allowed for rank field");
            return false;
        }

        $scope.showSpinner = true;
        var res = $http.post(uriPrefix + '/update/rankbykey/'+$cookies.get('fgt45hi7hfturtyrfgh')+'/'+rank, updatedProductId);
        res.success(function(data, status, headers, config) {
            Notification.success('Rank has been updated successfully');
            $scope.showSpinner = false;
        });
        res.error(function(data, status, headers, config) {
            Notification.error("Error in updating the rank");
            $scope.showSpinner = false;
        });
    };
}]);