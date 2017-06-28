app.controller('autocompleteController', ['$scope', '$rootScope', '$http', 'Notification', '$location', '$state','$cookies','filterFilter',
function($scope, $rootScope, $http, Notification, $location, $state,$cookies,filterFilter) {

 $scope.selectedUser = '';

  var users = [{
      name: 'Test user1',
      group: 1
    }, {
      name: 'Test user2',
      group: 1
    }, {
      name: 'Test user3',
      group: 1
    }, {
      name: 'Test user1',
      group: 2
    }, {
      name: 'Test user3',
      group: 2
    },
  ];

  $scope.getUsers = function (search) {
    var filtered = filterFilter(users, search);

    var results = _(filtered)
      .groupBy('group')
      .map(function (g) {
        g[0].firstInGroup = true;  // the first item in each group
        return g;
      })
      .flatten()
      .value();

    console.log(results);

    return results;
  }

}]);
