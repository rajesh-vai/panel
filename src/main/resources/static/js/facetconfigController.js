app.controller('facetconfigController', ['$scope', '$rootScope', '$http', 'Notification', '$location', '$state', function($scope, $rootScope, $http, Notification, $location, $state) {
        $scope.contact = {};
if (!$rootScope.validUser) {
    $location.path('/login');
    window.location.reload();
}

 $scope.models = {
        selected: null,
        lists: {"Available": [], "Selected": []}
    };

$scope.models.lists.Available = [];
//$scope.models.lists.Available = [{'label': 'primary_camera'}, {'label': 'internal_memory'}, { 'label': 'display'}, {'label' : 'title' }, {'label': 'secondary_camera' }, {'label': 'battery'}, {'label': 'model'}, {'label': 'specs'}, {'label': 'category'}, {'label': 'brand'},{'label': 'ram'}];
$scope.models.lists.Selected = [];


$scope.updateFacetConfig = function(){
    console.log($scope.models.lists.Selected );
}
}]);

