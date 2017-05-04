app.controller('settingsController', ['$scope', '$rootScope', '$http', 'Notification', '$location', '$state',
 function($scope, $rootScope, $http, Notification, $location, $state) {
if (!$rootScope.validUser) {
    $location.path('/login');
    window.location.reload();
}
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
