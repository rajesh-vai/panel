app.controller('linkController', ['$scope', '$rootScope', '$http', 'Notification', '$state', '$location','$cookies', function($scope, $rootScope, $http, Notification, $state, $location,$cookies) {

    $scope.key = '';
    $scope.links = '';
    $('.sidenav').removeClass('hidden');
    $('.main-page').removeClass('col-sm-12').addClass('col-sm-9');
    $('.main-page').addClass('white-background');

    if (!$cookies.get('fgt45hi7hfturtyrfgh')) {
        $location.path('/login');
        window.location.reload();
    }

    $http.get(_appName_+"/rest/logourl/"+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.logourl = data['logo'];});
    $http.get(_appName_ + '/rest/config/panel/'+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.panelConfig = data;});

    var uriPrefix = _appName_+"/rest/config";
    $http.get(uriPrefix + '/links/'+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) {
        $scope.concatenatedLinks = data;
    });

    $scope.addLinks = function() {
        if(!$scope.key){
            Notification.error('Keyword cannot be empty.');
            return false;
        }
        var updatedValue = {};
        updatedValue[$scope.key] = $scope.links;
      /*  var letterNumber = /^[a-zA-Z0-9 .,@:/|\?]+$/;
        if(!$scope.links.match(letterNumber) || !$scope.key.match(letterNumber) ){
            Notification.error('Only numbers and alpha letters are allowed');
            return false;
        }*/
        var res = $http.post(uriPrefix + '/add/links/'+$cookies.get('fgt45hi7hfturtyrfgh'), updatedValue);
        res.success(function(data, status, headers, config) {
            $scope.message = data;
            $scope.openAddLinks = false;
            $scope.registeredLinks = data;
            $state.go($state.current, {}, { reload: true });
            Notification.success('Links saved successfully');
        });
        res.error(function(data, status, headers, config) {
            console.log("Could not update links");
            Notification.error('Error in saving links');
        });
    }

    $scope.updateLink = function(linkKey, linksValue) {

        var updatedValue = {};
        updatedValue[linkKey] = linksValue;
        /*var letterNumber = /^[a-zA-Z0-9 .,@]+$/;
        if(!linksValue.match(letterNumber)  ){
            Notification.error('Only numbers and alpha letters are allowed');
            return false;
        }*/
        var res = $http.post(uriPrefix + '/update/links/'+$cookies.get('fgt45hi7hfturtyrfgh'), updatedValue);
        res.success(function(data, status, headers, config) {
            $scope.message = data;
            $scope.openAddLinks = false;
            $scope.registeredLinks = data;
            $state.go($state.current, {}, { reload: true });
            Notification.success('Links saved successfully');
        });
        res.error(function(data, status, headers, config) {
            console.log("Could not update links");
            Notification.error('Error in saving links');
        });
    }

    $scope.deleteLink = function(linkKey) {
        var res = $http.post(uriPrefix + '/delete/links/'+$cookies.get('fgt45hi7hfturtyrfgh'), linkKey);
        res.success(function(data, status, headers, config) {
            $scope.message = data;
            $scope.openAddLinks = false;
            $scope.registeredLinks = data;
            $state.go($state.current, {}, { reload: true });
            Notification.success('Links deleted successfully');
        });
        res.error(function(data, status, headers, config) {
            console.log("Could not delete links");
            Notification.error('Error in deleting links');
        });
    }

}]);