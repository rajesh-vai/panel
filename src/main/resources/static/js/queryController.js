app.controller('queryController', ['$scope', '$rootScope', '$http', 'Notification', '$location', '$state','$cookies','$sce',
 function($scope, $rootScope, $http, Notification, $location, $state,$cookies,$sce) {
    var search_url="http://52.163.118.40:8080";
    $scope.parentName = 'queryController';

    $scope.executeSearch = function(searchText){
        var config = {
         headers : {'Accept' : 'application/text'}
        };

        $http.get(search_url+_appName_+"/search/ui?query="+searchText, config).then(function(response) {
               $scope.searchResults =  $sce.trustAsHtml(response.data);
         }, function(response) {
            console.log("");
        });
    };

    $scope.go = function(route) {
    $state.go(route);
    };

}]);
