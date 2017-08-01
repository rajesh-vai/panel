app.controller('autocomplete2Controller', ['$scope', '$rootScope', '$http', 'Notification', '$location', '$state','$cookies','filterFilter',
function($scope, $rootScope, $http, Notification, $location, $state,$cookies,filterFilter) {

$scope.users = [];
 $scope.selectedUser = '';
  var uriPrefix = _appName_+"/rest/config";
        $http.get(uriPrefix + '/autocomplete/').success(function(data) {
            $scope.users = data;
        });


  $scope.getUsers = function (search) {
    var filtered = filterFilter($scope.users, search);

    var results = _(filtered)
      .groupBy('dept')
      .map(function (g) {
        g[0].firstInGroup = true;  // the first item in each group
        return g;
      })
      .flatten()
      .value();

    return results;
  }


}]);