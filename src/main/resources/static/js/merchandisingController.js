app.controller('merchandisingController', ['$scope', '$rootScope', '$http', 'Notification', '$location', '$state','$cookies',
 function($scope, $rootScope, $http, Notification, $location, $state,$cookies) {
if (!$cookies.get('fgt45hi7hfturtyrfgh')) {
    $location.path('/login');
    window.location.reload();
}

$http.get(_appName_+"/rest/logourl/"+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.logourl = data['logo'];});
$http.get(_appName_ + '/rest/config/panel/'+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.panelConfig = data;});

  $scope.tabs = [
    { title:'Query to URL Redirect', route: 'panel.merchandising.link' },
    { title:'Rank By Category', route: 'panel.merchandising.rank' },
    { title:'Rank by Keyword', route: 'panel.merchandising.rankByKey' },
    { title:'Facet Config', route: 'panel.merchandising.facetconfig' },
    { title:'Sort Config', route: 'panel.merchandising.sortingConfig' }
  ];


  $scope.parentName = 'merchandisingController';

  $scope.go = function(route) {
$state.go(route);
  };

}]);
