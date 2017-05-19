app.controller('spellingController', ['$scope', '$rootScope', '$http', 'Notification', '$state', '$location','$cookies', function($scope, $rootScope, $http, Notification, $state, $location,$cookies) {
    $scope.headingTitle = "User List";
    $scope.key = '';
    $scope.spellings = '';
   if (!$cookies.get('fgt45hi7hfturtyrfgh')) {
        $location.path('/login');
        window.location.reload();
    }

    $http.get(_appName_+"/rest/logourl/"+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.logourl = data['logo'];});
    $http.get(_appName_ + '/rest/config/panel/'+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.panelConfig = data;});

    $('.sidenav').removeClass('hidden');
    $('.main-page').removeClass('col-sm-12').addClass('col-sm-9');
    $('.main-page').addClass('white-background');

    var uriPrefix = _appName_+"/rest/config";
    $http.get(uriPrefix + '/spellings/'+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) {
        $scope.concatenatedSpellings = data;
    });

    $scope.addSpellings = function() {

        if(!$scope.key){
                    Notification.error('Keyword cannot be empty.');
                    return false;
                }
                var letterNumber = /^[a-zA-Z0-9 .,]+$/;
                                if(!$scope.spellings.match(letterNumber) || !$scope.key.match(letterNumber) ){
                                    Notification.error('Only numbers and alpha letters are allowed');
                                    return false;
                                }
        var updatedValue = {};
                        updatedValue[$scope.key] = $scope.spellings;
            var res = $http.post(uriPrefix + '/add/spellings/'+$cookies.get('fgt45hi7hfturtyrfgh'), updatedValue);
            res.success(function(data, status, headers, config) {
                $scope.message = data;
                $scope.openAddSpelling = false;
                $scope.registeredLinks = data;
                $state.go('panel.settings.spelling');
                Notification.success('Spellings saved successfully');
            });
            res.error(function(data, status, headers, config) {
                console.log("Could not update spellings");
                Notification.error('Error in saving synonyns');
            });
        }

    $scope.updateSpelling = function(spellingKey, concatenatedValues) {
        var letterNumber = /^[a-zA-Z0-9 .,]+$/;
        if(!concatenatedValues.match(letterNumber)){
            Notification.error('Only numbers and alpha letters are allowed');
            return false;
        }
        var updatedValue = {};
        updatedValue[spellingKey] = concatenatedValues;
        var res = $http.post(uriPrefix + '/update/spellings/'+$cookies.get('fgt45hi7hfturtyrfgh'), updatedValue);
        res.success(function(data, status, headers, config) {
            $scope.message = data;
            $scope.openAddSpelling = false;
            $scope.registeredLinks = data;
            $state.go('panel.settings.spelling');
            Notification.success('Spellings saved successfully');
        });
        res.error(function(data, status, headers, config) {
            console.log("Could not update spellings");
            Notification.error('Error in saving synonyns');
        });
    }

    $scope.deleteSpelling = function(spellingKey) {

            var res = $http.post(uriPrefix + '/delete/spellings/'+$cookies.get('fgt45hi7hfturtyrfgh'),spellingKey);
            res.success(function(data, status, headers, config) {
                $scope.message = data;
                $scope.openAddSpelling = false;
                $scope.registeredLinks = data;
                $state.go('panel.settings.spelling');
                Notification.success('Spelling deleted successfully');
            });
            res.error(function(data, status, headers, config) {
                console.log("Could not delete spelling");
                Notification.error('Error in deleting spelling');
            });
        }
}]);