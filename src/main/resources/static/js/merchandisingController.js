app.controller('merchandisingController', ['$scope', '$rootScope', '$http', 'Notification', '$location', '$state',
 function($scope, $rootScope, $http, Notification, $location, $state) {
if (!$rootScope.validUser) {
    $location.path('/login');
    window.location.reload();
}
  $scope.tabs = [
    { title:'Query to URL Redirect', route: 'panel.merchandising.link' },
    { title:'Rank By Product', route: 'panel.merchandising.rank' },
    { title:'Rank by Keyword', route: 'panel.merchandising.rankByKey' },
    { title:'Facet Config', route: 'panel.merchandising.facetconfig' },
//    { title:'Sort Config', route: 'panel.merchandising.sortingConfig' }
  ];


  $scope.parentName = 'merchandisingController';

  $scope.go = function(route) {
$state.go(route);
  };

}]);
