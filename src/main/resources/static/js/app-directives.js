(function(angular){
    "use strict";

    angular.module('appDirectives', [])
        .directive('togglePanel', function(){
            return{
                restrict : 'EA',
                link: function(scope,element){
                    element.bind('click', function(){
                        $(element[0]).parents().find('.panel-heading').css("background-color", "#7ea927");
                      //  $(element[0]).closest('.panel-heading').css({"background-color","#7ea927");
                        $(element[0]).closest('.panel-heading').css("border-left","5px solid #7ea927");
                        //$(element[0]).parents().find('.glyphicon').toggleClass('glyphicon-minus glyphicon-plus');
                        $(element[0]).find('.glyphicon').toggleClass('glyphicon-plus glyphicon-minus');
                    });
                }
            }
        })
        .directive('showSynonymEdit', function(){
            return{
                restrict : 'EA',
                link: function(scope,element){
                    element.bind('click', function(){
                        $(element[0]).closest('tr').find('.editSynonym').css("display","block");
                        $(element[0]).closest('tr').find('.displaySynonym').css("display","none");
                        $(element[0]).closest('tr').find('.saveSynonym').css("display","inline-block");
                    });
                }
            }
        })
        .directive('showSpellingEdit', function(){
            return{
                restrict : 'EA',
                link: function(scope,element){
                    element.bind('click', function(){
                        $(element[0]).closest('tr').find('.editSpelling').css("display","block");
                        $(element[0]).closest('tr').find('.displaySpelling').css("display","none");
                        $(element[0]).closest('tr').find('.saveSpelling').css("display","inline-block");
                    });
                }
            }
        })
        .directive('showLinkEdit', function(){
            return{
                restrict : 'EA',
                link: function(scope,element){
                    element.bind('click', function(){
                        $(element[0]).closest('tr').find('.editLink').css("display","block");
                        $(element[0]).closest('tr').find('.displayLink').css("display","none");
                        $(element[0]).closest('tr').find('.saveLink').css("display","inline-block");
                    });
                }
            }
        })
        .directive('updateLoginView', function(){
            return{
                restrict : 'EA',
                link: function(scope,element){
                    element.bind('click', function(){
                        $('.sidenav').addClass('hidden');
                        $('.main-page').removeClass('col-sm-9');
                        $('.main-page').addClass('col-sm-12');
                    });
                }
            }
        })
        .directive('highlightSelection', function(){
            return{
                restrict : 'EA',
                link: function(scope,element){
                    element.bind('click', function(){
                        $('li').removeClass('active');
                        $(element).addClass('active');
                    });
                }
            }
        })

         .directive('toggleCollapseItem', function(){
            return{
                restrict : 'EA',
                link: function(scope,element){
                    element.bind('click', function(){
                        $('ul.list-group.sub-group').removeClass('in');
                    });
                }
            }
        })
        .directive("dualmultiselect", [function() {
        	return {
        		restrict: 'E',
        		scope: {
        			options: '='
        		},
        		controller: function($scope) {
        			$scope.transfer = function(from, to, index) {
        				if (index >= 0) {
        					to.push(from[index]);
        					from.splice(index, 1);
        				} else {
        					for (var i = 0; i < from.length; i++) {
        						to.push(from[i]);
        					}
        					from.length = 0;
        				}
        			};
        		},
        		template: '<div class="dualmultiselect"> <div class="row"> <div class="col-lg-12 col-md-12 col-sm-12">  <input class="form-control" placeholder="{{options.filterPlaceHolder}}" ng-model="searchTerm"> </div></div><div class="row"> <div class="col-lg-6 col-md-6 col-sm-6"> <label>{{options.labelAll}}</label> <button type="button" class="btn btn-default btn-xs" ng-click="transfer(options.items, options.selectedItems, -1)"> Select All </button> <div class="pool"> <ul> <li ng-repeat="item in options.items | filter: searchTerm | orderBy: options.orderProperty"> <a href="" ng-click="transfer(options.items, options.selectedItems, options.items.indexOf(item))">{{item.name}}&nbsp;&rArr; </a> </li></ul> </div></div><div class="col-lg-6 col-md-6 col-sm-6"> <label>{{options.labelSelected}}</label> <button type="button" class="btn btn-default btn-xs" ng-click="transfer(options.selectedItems, options.items, -1)"> Deselect All </button> <div class="pool"> <ul> <li ng-repeat="item in options.selectedItems | orderBy: options.orderProperty"> <a href="" ng-click="transfer(options.selectedItems, options.items, options.selectedItems.indexOf(item))"> &lArr;&nbsp;{{item.name}}</a> </li></ul> </div></div></div></div>'
        	};
        }])
        .directive('bsHasError', [function() {
          return {
              restrict: "A",
              link: function(scope, element, attrs, ctrl) {
                  var input = element.find('input[ng-model]');
                  if (input) {
                      scope.$watch(function() {
                          return input.hasClass('ng-invalid');
                      }, function(isInvalid) {
                          element.toggleClass('has-error', isInvalid);
                      });
                  }
              }
          };
        }])
        .directive('bootstrapSwitch', [
                function() {
                    return {
                        restrict: 'A',
                        require: '?ngModel',
                        link: function(scope, element, attrs, ngModel) {
                            element.bootstrapSwitch();

                            element.on('switchChange.bootstrapSwitch', function(event, state) {
                                if (ngModel) {
                                    scope.$apply(function() {
                                        ngModel.$setViewValue(state);
                                    });
                                }
                            });

                            scope.$watch(attrs.ngModel, function(newValue, oldValue) {
                                if (newValue) {
                                    element.bootstrapSwitch('state', true, true);
                                } else {
                                    element.bootstrapSwitch('state', false, true);
                                }
                            });
                        }
                    };
                }
            ])
            .directive('appSizer', function() {
                return function (scope, element, attrs) {
                    element.height($('body').height() - 60);
                }
            });


})(angular);
