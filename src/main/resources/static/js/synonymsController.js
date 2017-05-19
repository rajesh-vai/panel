app.controller('synonymController', ['$scope', '$rootScope', '$http', 'Notification', '$location', '$state','$cookies', function($scope, $rootScope, $http, Notification, $location, $state,$cookies) {
    $scope.headingTitle = "User List";
    $scope.key = '';
    $scope.synonyms = '';
    $scope.regex = /^[a-zA-Z0-9 ._-]+$/;
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
    $http.get(uriPrefix + '/synonyms/'+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) {
        $scope.concatenatedSynonyms = data;
    });

    $scope.addSynonyms = function() {
        var updatedValue = {};
        if(!$scope.key){
            Notification.error('Keyword cannot be empty.');
            return false;
        }
                updatedValue[$scope.key] = $scope.synonyms;
                var letterNumber = /^[a-zA-Z0-9 .,]+$/;
                if(!$scope.synonyms.match(letterNumber) || !$scope.key.match(letterNumber) ){
                    Notification.error('Only numbers and alpha letters are allowed');
                    return false;
                }
            var res = $http.post(uriPrefix + '/add/synonyms/'+$cookies.get('fgt45hi7hfturtyrfgh'), updatedValue);
            res.success(function(data, status, headers, config) {
                $scope.message = data;
                $scope.openAddSynonym = false;
                $scope.registeredLinks = data;
                $state.go($state.current, {}, { reload: true });
                Notification.success('Synonyms saved successfully');
            });
            res.error(function(data, status, headers, config) {
                console.log("Could not update synonyms");
                Notification.error('Error in saving synonyns');
            });
        }

    $scope.updateSynonym = function(synonymKey, concatenatedValues) {
        var updatedValue = {};
        updatedValue[synonymKey] = concatenatedValues;
        var letterNumber = /^[a-zA-Z0-9 .,]+$/;
        if(!concatenatedValues.match(letterNumber)){
            Notification.error('Only numbers and alpha letters are allowed');
            return false;
        }
        var res = $http.post(uriPrefix + '/update/synonyms/'+$cookies.get('fgt45hi7hfturtyrfgh'), updatedValue);
        res.success(function(data, status, headers, config) {
            $scope.message = data;
            $scope.openAddSynonym = false;
            $scope.registeredLinks = data;
            $state.go($state.current, {}, { reload: true });
            Notification.success('Synonyms saved successfully');
        });
        res.error(function(data, status, headers, config) {
            console.log("Could not update synonyms");
            Notification.error('Error in saving synonyns');
        });
    };

    $scope.deleteSynonym = function(synonymKey) {

            var res = $http.post(uriPrefix + '/delete/synonyms/'+$cookies.get('fgt45hi7hfturtyrfgh'), synonymKey);
            res.success(function(data, status, headers, config) {
                $scope.message = data;
                $scope.openAddSynonym = false;
                $scope.registeredLinks = data;
                $state.go($state.current, {}, { reload: true });
                Notification.success('Synonym deleted successfully');
            });
            res.error(function(data, status, headers, config) {
                console.log("Could not delete synonym");
                Notification.error('Error in deleting synonym');
            });
        }
}]);