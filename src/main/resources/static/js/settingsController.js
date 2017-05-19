app.controller('settingsController', ['$scope', '$rootScope', '$http', 'Notification', '$location', '$state','$cookies',
 function($scope, $rootScope, $http, Notification, $location, $state,$cookies) {
if (!$cookies.get('fgt45hi7hfturtyrfgh')) {
    $location.path('/login');
    window.location.reload();
}

$http.get(_appName_+"/rest/logourl/"+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.logourl = data['logo'];});
$http.get(_appName_ + '/rest/config/panel/'+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.panelConfig = data;});

  $scope.tabs = [
    { title:'Synonyms', route: 'panel.settings.synonym' },
    { title:'Spell Check', route: 'panel.settings.spelling' },
    { title:'Noise Words', route: 'panel.settings.stopword' },
    { title:'Precision Configuration', route: 'panel.settings.precision' }
  ];


  $scope.parentName = 'settingsController';

  $scope.go = function(route) {
    $state.go(route);
  };

}]);
