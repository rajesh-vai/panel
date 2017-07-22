app.controller('homeController', [ '$scope', '$rootScope','$http','$location','$cookies', function($scope,$rootScope, $http,$location,$cookies) {
	console.log("Home Controller");

	  if(!$cookies.get('fgt45hi7hfturtyrfgh')){
           $location.path('/login');
           window.location.reload();
        }

        $http.get(_appName_+"/rest/logourl/"+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.logourl = data['logo'];});
        $http.get(_appName_ + '/rest/config/panel/'+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) { $rootScope.panelConfig = data;});
        $http.get(_appName_ + '/rest/config/home/'+$cookies.get('fgt45hi7hfturtyrfgh')).success(function(data) {

         var groups = _.pluck(data, 'groupName');
         var uniqueGroups = _.uniq(groups);
         $scope.displayItems = [];
         uniqueGroups.forEach(function(entry) {
             var displaydata = {};
             displaydata.name = entry;
             displaydata.details = [];
             var matchingGroup = _.filter(data, function(d){ return d.groupName == entry; });


             _.each(matchingGroup, function(item){
                 var itemDisplay = {};
                 itemDisplay.screenName = item.screenName;
                 itemDisplay.description = item.description;
                 displaydata.details.push(itemDisplay);
             });


            $scope.displayItems.push(displaydata);
         });

         console.log($scope.displayItems);
        });
}]);