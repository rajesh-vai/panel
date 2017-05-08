app.controller('analyticsController', ['$scope', '$rootScope',
'$http', 'Notification', '$location', '$state', function($scope, $rootScope, $http, Notification, $location, $state) {

if (!$rootScope.validUser) {
    $location.path('/login');
    window.location.reload();
}

Highcharts.chart('container1', {
    chart: {
        type: 'pie',
        backgroundColor:'#f5f5f0'
    },
    title: {
        text: 'Queries from different browsers'
    },
    tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
    },
    plotOptions: {
        pie: {
            allowPointSelect: true,
            cursor: 'pointer',
            dataLabels: {
                enabled: true,
                format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                style: {
                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                },
                connectorColor: 'silver'
            }
        }
    },
    series: [{
        name: 'Brands',
        data: [
            { name: 'Microsoft Internet Explorer', y: 56.33 },
            {
                name: 'Chrome',
                y: 24.03,
                sliced: true,
                selected: true
            },
            { name: 'Firefox', y: 10.38 },
            { name: 'Safari', y: 4.77 }, { name: 'Opera', y: 0.91 },
            { name: 'Proprietary or Undetectable', y: 0.2 }
        ]
    }]
});


//Maps

// Prepare demo data
var data = [{
    'hc-key': 'us',
    value: 3
}, {
    'hc-key': 'ca',
    value: 5
}, {
    'hc-key': 'mx',
    value: 20
}];

Highcharts.chart('container2', {
    chart: {
        type: 'area',
        backgroundColor:'#f5f5f0'
    },
    title: {
        text: 'Queries from different parts of the world'
    },
    subtitle: {
//        text: 'Source: Wikipedia.org'
    },
    xAxis: {
        categories: ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
        tickmarkPlacement: 'on',
        title: {
            enabled: false
        }
    },
    yAxis: {
        title: {
            text: 'Percent'
        }
    },
    tooltip: {
        pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.percentage:.1f}%</b> ({point.y:,.0f} millions)<br/>',
        split: true
    },
    plotOptions: {
        area: {
            stacking: 'percent',
            lineColor: '#ffffff',
            lineWidth: 1,
            marker: {
                lineWidth: 1,
                lineColor: '#ffffff'
            }
        }
    },
    series: [{
        name: 'Asia',
        data: [502, 635, 809, 947, 1402, 3634, 5268]
    }, {
        name: 'Africa',
        data: [106, 107, 111, 133, 221, 767, 1766]
    }, {
        name: 'Europe',
        data: [163, 203, 276, 408, 547, 729, 628]
    }, {
        name: 'America',
        data: [18, 31, 54, 156, 339, 818, 1201]
    }, {
        name: 'Oceania',
        data: [2, 2, 2, 6, 13, 30, 46]
    }]
});




}]);