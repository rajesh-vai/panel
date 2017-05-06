app.controller('contactusController', ['$scope', '$rootScope', '$http', 'Notification', '$location', '$state', function($scope, $rootScope, $http, Notification, $location, $state) {
        $scope.contact = {};
        if (!$rootScope.validUser) {
            $location.path('/login');
            window.location.reload();
        }
}]);

