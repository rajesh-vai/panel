app.controller('autocompleteController', ['$scope', '$rootScope', '$http', 'Notification', '$location', '$state','$cookies','filterFilter',

function($scope, $rootScope, $http, Notification, $location, $state,$cookies,filterFilter) {

 $scope.selectedUser = '';


     var uriPrefix = _appName_+"/rest/config";
        $http.get(uriPrefix + '/autocomplete/').success(function(data) {
            $scope.users = data;
        });

}]);
