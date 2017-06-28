//This should be same as the WAR to be generated name
_appName_ = "/flyrobe2";
//

var app = angular.module('app', ['ui.bootstrap','ui.toggle','ui.router','ui.bootstrap.typeahead','appDirectives','AngularChart','ui-notification','dndLists','ngCookies','ngSanitize']);


app.config(function($stateProvider, $urlRouterProvider) {
$urlRouterProvider.otherwise('/login');
    $stateProvider
        .state('panel', {
            url: '/panel',
            templateUrl: 'views/panel.html'
        })
        .state('panel.settings', {
            url: '/settings',
            templateUrl: 'views/settings.html',
            controller: 'settingsController'
        })

        .state('query', {
            url: '/query',
            templateUrl: 'views/query.html',
            controller: 'queryController'
        })

        .state('panel.merchandising', {
            url: '/merchandising',
            templateUrl: 'views/merchandising.html',
            controller: 'merchandisingController'
        })

        .state('panel.merchandising.facetconfig', {
            url: '/facetconfig',
            templateUrl: 'views/facetconfig.html',
            controller: 'facetconfigController'
        })
        .state('panel.merchandising.rank', {
            url: '/rank',
            templateUrl: 'views/rank.html',
            controller: 'rankController'
        })
        .state('panel.merchandising.rankByKey', {
            url: '/rankbykey',
            templateUrl: 'views/rankByKey.html',
            controller: 'rankByKeyController'
        })
        .state('panel.merchandising.link', {
            url: '/link',
            templateUrl: 'views/link.html',
            controller: 'linkController'
        })
        .state('panel.settings.synonym', {
            url: '/synonym',
            templateUrl: 'views/synonym.html',
            controller: 'synonymController'
        })
        .state('panel.dashboard', {
            url: '/dashboard',
            templateUrl: 'views/dashboard.html',
            controller: 'dashboardController'
        })
        .state('panel.contactus', {
            url: '/contactus',
            templateUrl: 'views/contactus.html',
            controller: 'contactusController'
        })
        .state('panel.autocomplete', {
            url: '/autocomplete',
            templateUrl: 'views/autocomplete.html',
            controller: 'autocompleteController'
        })
        .state('panel.analytics', {
            url: '/analytics',
            templateUrl: 'views/analytics.html',
            controller: 'analyticsController'
        })
        .state('panel.viewnotifications', {
            url: '/notifications',
            templateUrl: 'views/viewnotifications.html',
            controller: 'viewNotificationsController'
        })
        .state('panel.autosuggest', {
            url: '/autosuggest',
            templateUrl: 'views/autosuggest.html',
            controller: 'autosuggestController'
        })
        .state('panel.settings.spelling', {
            url: '/spelling',
            templateUrl: 'views/spellcheck.html',
            controller: 'spellingController'
        })
        .state('panel.settings.stopword', {
            url: '/stopword',
            templateUrl: 'views/stopword.html',
            controller: 'stopwordController'
        })
        .state('panel.merchandising.sortingConfig', {
            url: '/sortingConfig',
            templateUrl: 'views/sortingConfig.html',
            controller: 'sortingConfigController'
        })
        .state('panel.settings.precision', {
            url: '/precision',
            templateUrl: 'views/precisionConfig.html',
            controller: 'precisionController'
        })
        .state('panel.settings.fliter', {
            url: '/fliter',
            templateUrl: 'views/fliterConfig.html',
            controller: 'filterConfigController'
        })
        .state('panel.home', {
            url: '/home',
            templateUrl: 'views/home.html',
            controller: 'homeController'
        })
        .state('login', {
            url: '/login',
            templateUrl: 'views/login.html',
            controller: 'loginController'
        });

});



