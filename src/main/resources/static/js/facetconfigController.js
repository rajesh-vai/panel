app.controller('facetconfigController', ['$scope', '$rootScope', '$http', 'Notification', '$location', '$state', function($scope, $rootScope, $http, Notification, $location, $state) {
        $scope.contact = {};
if (!$rootScope.validUser) {
    $location.path('/login');
    window.location.reload();
}
var uriPrefix = _appName_+"/rest/config";
    $http.get(_appName_+"/rest/categories/"+$rootScope.companyid).success(function(data) {
        $scope.categories = data;
    });
 $scope.models = {
        selected: null,
        lists: {"Available": [], "Selected": []}
    };

$scope.models.lists.Available = [];
//$scope.models.lists.Available = [{'label': 'primary_camera'}, {'label': 'internal_memory'}, { 'label': 'display'}, {'label' : 'title' }, {'label': 'secondary_camera' }, {'label': 'battery'}, {'label': 'model'}, {'label': 'specs'}, {'label': 'category'}, {'label': 'brand'},{'label': 'ram'}];
$scope.models.lists.Available = [{'label': 'category'}, {'label': 'vendor'}, { 'label': 'title'}, {'label' : 'brand' }, {'label': 'currency' }, {'label': 'price'}, {'label': 'shipping cost'}, {'label': 'COD'}, {'label': 'category'}, {'label': 'season'},{'label': 'in stock'}];
$scope.models.lists.Selected = [];


$scope.updateFacetConfig = function(){
    console.log($scope.models.lists.Selected );
}
}]);

