app.controller('stopwordController', ['$scope', '$rootScope', '$http', 'Notification', '$state', '$location', '$cookies',function($scope, $rootScope, $http, Notification, $state, $location,$cookies) {


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
    $http.get(uriPrefix + '/stopwords/'+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) {
        $scope.registeredStopwords = data;
        $scope.concatenatedStopwords = data.join();
    });

    $scope.saveStopWords = function() {
        var letterNumber = /^[a-zA-Z0-9 .,]+$/;
        if(!$scope.concatenatedStopwords.match(letterNumber)){
            Notification.error('Only numbers and alpha letters are allowed');
            return false;
        }
        var stopWordsList = $scope.concatenatedStopwords.split(",");
        var res = $http.post(uriPrefix + '/update/stopwords/'+$cookies.get('fgt45hi7hfturtyrfgh'), $scope.concatenatedStopwords);
        res.success(function(data, status, headers, config) {
            $scope.message = data;
            $state.go('panel.settings.stopword');
            Notification.success('Stopwords saved successfully');
        });
        res.error(function(data, status, headers, config) {
            console.log("Could not save stopwords");
            Notification.error('Error in saving stopwords');
        });
    }

}]);