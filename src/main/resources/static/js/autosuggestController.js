app.controller('autosuggestController', ['$scope', '$rootScope', '$http', 'Notification', '$location', '$state','$cookies', function($scope, $rootScope, $http, Notification, $location, $state,$cookies) {
        $scope.contact = {};
        if (!$cookies.get('fgt45hi7hfturtyrfgh')) {
            $location.path('/login');
            window.location.reload();
        }
        $http.get(_appName_+"/rest/logourl/"+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.logourl = data['logo'];});
        $http.get(_appName_ + '/rest/config/panel/'+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.panelConfig = data;});

        var uriPrefix = _appName_+"/rest/config";
        $scope.autosuggest=true;
        $http.get(uriPrefix+"/autosuggestdetails/"+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) {
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

            var res = $http.post(uriPrefix + '/update/autosuggestconfig/'+$cookies.get('fgt45hi7hfturtyrfgh') +'/'+autosuggest +'/'+topqueries +'/'+keywordsuggestions +'/'+searchscope +'/'+selectedtemplate);
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
        };


        $scope.updateOtherAutosuggets = function(){
            if(!$scope.autosuggest){
                $scope.topqueries = false;
                $scope.keywordsuggestions = false;
                $scope.searchscope = false;
            }
        }

        $scope.resetAutoSuggest = function(reset){
            if(reset){
                $scope.autosuggest = true;
            }
        }

}]);

