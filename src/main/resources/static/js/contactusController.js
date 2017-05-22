app.controller('contactusController', ['$scope', '$rootScope', '$http', 'Notification', '$location', '$state','$cookies', function($scope, $rootScope, $http, Notification, $location, $state,$cookies) {
        $scope.contact = {};
        if (!$cookies.get('fgt45hi7hfturtyrfgh')) {
            $location.path('/login');
            window.location.reload();
        }
        var uriPrefix = _appName_+"/rest/";
        $http.get(_appName_+"/rest/logourl/"+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.logourl = data['logo'];});
        $http.get(_appName_ + '/rest/config/panel/'+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.panelConfig = data;});


        $scope.sendMail = function() {
            var updatedValue = {};
            if(!$scope.message){
                Notification.error('Message cannot be empty.');
                return false;
            }
            if(!$scope.subject){
                Notification.error('Subject of the mail cannot be empty.');
                return false;
            }

            /*var letterNumber = /^[a-zA-Z0-9 .,]+$/;
            if(!$scope.message.match(letterNumber) || !$scope.subject.match(letterNumber) ){
                Notification.error('Only numbers and alpha letters are allowed');
                return false;
            }*/
            updatedValue['subject'] = $scope.subject;
            updatedValue['message'] = $scope.message;
            var res = $http.post(uriPrefix + 'send/email/'+$cookies.get('fgt45hi7hfturtyrfgh'), updatedValue);
            res.success(function(data, status, headers, config) {
                $scope.message = data;
                $scope.openAddSynonym = false;
                $scope.registeredLinks = data;
                $state.go($state.current, {}, { reload: true });
                Notification.success('Thanks for contacting SeekNShop support. We will get back to you shortly');
            });
            res.error(function(data, status, headers, config) {
                console.log("Could not update synonyms");
                Notification.error('Error in saving synonyns');
            });
        }

}]);

