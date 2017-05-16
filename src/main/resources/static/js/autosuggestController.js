app.controller('autosuggestController', ['$scope', '$rootScope', '$http', 'Notification', '$location', '$state', function($scope, $rootScope, $http, Notification, $location, $state) {
        $scope.contact = {};
        if (!$rootScope.validUser) {
            $location.path('/login');
            window.location.reload();
        }
        var uriPrefix = _appName_+"/rest/config";
        $scope.autosuggest=true;
        $http.get(uriPrefix+"/autosuggestdetails/"+$rootScope.companyid).success(function(data) {
            $scope.autosuggest = data['autosuggest'];
            $scope.topqueries = data['topqueries'];
            $scope.keywordsuggestions = data['keywordsuggestions'];
            $scope.searchscope = data['searchscope'];
//            $scope.selectedtemplate.name = data['template'];
        });

        $scope.saveAutoSuggestConfigs = function() {

           var autoSuggestValues = {};

           var autosuggest = $scope.autosuggest ? 1 : 0;
           var topqueries = $scope.topqueries ? 1 : 0;
           var keywordsuggestions  = $scope.keywordsuggestions ? 1 : 0;
           var searchscope  = $scope.searchscope ? 1 : 0;
           var selectedtemplate  = 'template1';
           if($scope.selectedtemplate){
                var selectedtemplate = $scope.selectedtemplate.name;
           }

            var res = $http.post(uriPrefix + '/update/autosuggestconfig/'+$rootScope.companyid +'/'+autosuggest +'/'+topqueries +'/'+keywordsuggestions +'/'+searchscope +'/'+selectedtemplate);
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

