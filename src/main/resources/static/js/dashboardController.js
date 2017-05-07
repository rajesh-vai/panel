app.controller('dashboardController', ['$scope', '$rootScope', '$http', 'Notification', '$location', '$state', function($scope, $rootScope, $http, Notification, $location, $state) {

if (!$rootScope.validUser) {
    $location.path('/login');
    window.location.reload();
}

Highcharts.chart('container', {

    chart: {
        type: 'column',
        backgroundColor:'#f5f5f0'
    },

    title: {
        text: 'Top 5 categories, based on Number of queries'
    },

    xAxis: {
        categories: ['Kurtis', 'Tees', 'Leggings', 'Jeans', 'Sandals']
    },

    yAxis: {
        allowDecimals: false,
        min: 0,
        title: {
            text: 'Numbers in 1000s'
        }
    },

    tooltip: {
        formatter: function () {
            return '<b>' + this.x + '</b><br/>' +
                this.series.name + ': ' + this.y + '<br/>' +
                'Total: ' + this.point.stackTotal;
        }
    },

    plotOptions: {
        column: {
            stacking: 'normal'
        }
    },

    series: [{
        name: 'Simple Queries',
        data: [6, 8, 7, 5, 9],
        stack: 'male'
    }, {
        name: 'Queries with Multiple Attributes',
        data: [2, 3, 1, 1, 2],
        stack: 'male'
    }
   /* , {
        name: 'Jane',
        data: [2, 5, 6, 2, 1],
        stack: 'female'
    }, {
        name: 'Janet',
        data: [3, 0, 4, 4, 3],
        stack: 'female'
    }*/
    ]
});

var data = {"xData": ["Jan", "Feb", "Mar", "Apr", "May", "Jun","Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],"yData":[{
                "name": "Tokyo",
                "data": [7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6]
            }, {
                "name": "New York",
                "data": [-0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5]
            }, {
                "name": "Berlin",
                "data": [-0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0]
            }, {
                "name": "London",
                "data": [3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8]
            }]}

            $scope.lineChartYData=data.yData
            $scope.lineChartXData=data.xData
}]);
