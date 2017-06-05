app.controller('facetconfigController', ['$scope', '$rootScope', '$http', 'Notification', '$location', '$state','$cookies', function($scope, $rootScope, $http, Notification, $location, $state,$cookies) {
        $scope.contact = {};
if (!$cookies.get('fgt45hi7hfturtyrfgh')) {
    $location.path('/login');
    window.location.reload();
}

$http.get(_appName_+"/rest/logourl/"+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.logourl = data['logo'];});
$http.get(_appName_ + '/rest/config/panel/'+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.panelConfig = data;});

var uriPrefix = _appName_+"/rest/config";
    $http.get(_appName_+"/rest/categories/"+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) {
        $scope.categories = data;
    });
 $scope.models = {
        selected: null,
        lists: {"Available": [], "Selected": []}
    };

$scope.models.lists.Available = [];
//$scope.models.lists.Available = [{'label': 'primary_camera'}, {'label': 'internal_memory'}, { 'label': 'display'}, {'label' : 'title' }, {'label': 'secondary_camera' }, {'label': 'battery'}, {'label': 'model'}, {'label': 'specs'}, {'label': 'category'}, {'label': 'brand'},{'label': 'ram'}];
$scope.models.lists.Available = [{'label': 'category'}, {'label': 'vendor'}, { 'label': 'title'}, {'label' : 'brand' }, {'label': 'price'}, {'label': 'COD'},  {'label': 'customizable'},{'label': 'in stock'}];
$scope.models.lists.Selected = [];


$scope.updateFacetConfig = function(){
    console.log($scope.models.lists.Selected );
}
}]);

