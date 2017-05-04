app.controller('autosuggestController', ['$scope', '$rootScope', '$http', 'Notification', '$location', '$state', function($scope, $rootScope, $http, Notification, $location, $state) {
        $scope.contact = {};
        if (!$rootScope.validUser) {
            $location.path('/login');
            window.location.reload();
        }
        var uriPrefix = _appName_+"/rest/config";
        $scope.autosuggest=true;
        $http.get(_appName_+"/rest/autosuggestdetails/"+$rootScope.companyid).success(function(data) {
            $scope.autosuggest = data['autosuggest'];
            $scope.topqueries = data['topqueries'];
            $scope.keywordsuggestions = data['keywordsuggestions'];
            $scope.searchscope = data['searchscope'];
        });

        $scope.saveAutoSuggestConfigs = function() {

           var autoSuggestValues = {};
           autoSuggestValues['autosuggest'] = $scope.autosuggest;
           autoSuggestValues['topqueries'] = $scope.topqueries;
           autoSuggestValues['keywordsuggestions'] = $scope.keywordsuggestions;
           autoSuggestValues['searchscope'] = $scope.searchscope;

            var res = $http.post(uriPrefix + '/update/autosuggestconfig/'+$rootScope.companyid, autoSuggestValues);
            res.success(function(data, status, headers, config) {
                $scope.message = data;
                $scope.openAddSynonym = false;
                $scope.registeredLinks = data;
                $state.go($state.current, {}, { reload: true });
                Notification.success('Auto suggest config updated successfully');
            });
            res.error(function(data, status, headers, config) {
                Notification.error('Error in deleting synonym');
            });
        }
}]);

