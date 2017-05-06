app.controller('analyticsController', ['$scope', '$rootScope',
'$http', 'Notification', '$location', '$state', function($scope, $rootScope, $http, Notification, $location, $state) {

if (!$rootScope.validUser) {
    $location.path('/login');
    window.location.reload();
}
// Radialize the colors
Highcharts.getOptions().colors = Highcharts.map(Highcharts.getOptions().colors, function (color) {
    return {
        radialGradient: {
            cx: 0.5,
            cy: 0.3,
            r: 0.7
        },
        stops: [
            [0, color],
            [1, Highcharts.Color(color).brighten(-0.3).get('rgb')] // darken
        ]
    };
});

// Build the chart
Highcharts.chart('container1', {
    chart: {
        plotBackgroundColor: null,
        plotBorderWidth: null,
        plotShadow: false,
        type: 'pie',
        backgroundColor:'#f5f5f0'
    },
    title: {
        text: 'Browser market shares. January, 2015 to May, 2015'
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
        text: 'Historic and Estimated Worldwide Population Distribution by Region'
    },
    subtitle: {
        text: 'Source: Wikipedia.org'
    },
    xAxis: {
        categories: ['1750', '1800', '1850', '1900', '1950', '1999', '2050'],
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



Highcharts.chart('container3', {

    chart: {
        type: 'gauge',
        plotBackgroundColor: null,
        plotBackgroundImage: null,
        plotBorderWidth: 0,
        plotShadow: false,
           backgroundColor:'#f5f5f0'
    },

    title: {
        text: 'Speedometer'
    },

    pane: {
        startAngle: -150,
        endAngle: 150,
        background: [{
            backgroundColor: {
                linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
                stops: [
                    [0, '#f5f5f0'],
                    [1, '#333']
                ]
            },
            borderWidth: 0,
            outerRadius: '109%'
        }, {
            backgroundColor: {
                linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
                stops: [
                    [0, '#333'],
                    [1, '#f5f5f0']
                ]
            },
            borderWidth: 1,
            outerRadius: '107%'
        }, {
            // default background
        }, {
            backgroundColor: '#DDD',
            borderWidth: 0,
            outerRadius: '105%',
            innerRadius: '103%'
        }]
    },

    // the value axis
    yAxis: {
        min: 0,
        max: 200,

        minorTickInterval: 'auto',
        minorTickWidth: 1,
        minorTickLength: 10,
        minorTickPosition: 'inside',
        minorTickColor: '#666',

        tickPixelInterval: 30,
        tickWidth: 2,
        tickPosition: 'inside',
        tickLength: 10,
        tickColor: '#666',
        labels: {
            step: 2,
            rotation: 'auto'
        },
        title: {
            text: 'km/h'
        },
        plotBands: [{
            from: 0,
            to: 120,
            color: '#55BF3B' // green
        }, {
            from: 120,
            to: 160,
            color: '#DDDF0D' // yellow
        }, {
            from: 160,
            to: 200,
            color: '#DF5353' // red
        }]
    },

    series: [{
        name: 'Speed',
        data: [80],
        tooltip: {
            valueSuffix: ' km/h'
        }
    }]

},
// Add some life
function (chart) {
    if (!chart.renderer.forExport) {
        setInterval(function () {
            var point = chart.series[0].points[0],
                newVal,
                inc = Math.round((Math.random() - 0.5) * 20);

            newVal = point.y + inc;
            if (newVal < 0 || newVal > 200) {
                newVal = point.y - inc;
            }

            point.update(newVal);

        }, 3000);
    }
});




Highcharts.chart('container4', {

    chart: {
        type: 'solidgauge',
        marginTop: 50,
        backgroundColor:'#f5f5f0'
    },

    title: {
        text: 'Activity',
        style: {
            fontSize: '24px'
        }
    },

    tooltip: {
        borderWidth: 0,
        backgroundColor: 'none',
        shadow: false,
        style: {
            fontSize: '16px'
        },
        pointFormat: '{series.name}<br><span style="font-size:2em; color: {point.color}; font-weight: bold">{point.y}%</span>',
        positioner: function (labelWidth) {
            return {
                x: 200 - labelWidth / 2,
                y: 180
            };
        }
    },

    pane: {
        startAngle: 0,
        endAngle: 360,
        background: [{ // Track for Move
            outerRadius: '112%',
            innerRadius: '88%',
            backgroundColor: Highcharts.Color(Highcharts.getOptions().colors[0])
                .setOpacity(0.3)
                .get(),
            borderWidth: 0
        }, { // Track for Exercise
            outerRadius: '87%',
            innerRadius: '63%',
            backgroundColor: Highcharts.Color(Highcharts.getOptions().colors[1])
                .setOpacity(0.3)
                .get(),
            borderWidth: 0
        }, { // Track for Stand
            outerRadius: '62%',
            innerRadius: '38%',
            backgroundColor: Highcharts.Color(Highcharts.getOptions().colors[2])
                .setOpacity(0.3)
                .get(),
            borderWidth: 0
        }]
    },

    yAxis: {
        min: 0,
        max: 100,
        lineWidth: 0,
        tickPositions: []
    },

    plotOptions: {
        solidgauge: {
            dataLabels: {
                enabled: false
            },
            linecap: 'round',
            stickyTracking: false,
            rounded: true
        }
    },

    series: [{
        name: 'Move',
        data: [{
            color: Highcharts.getOptions().colors[0],
            radius: '112%',
            innerRadius: '88%',
            y: 80
        }]
    }, {
        name: 'Exercise',
        data: [{
            color: Highcharts.getOptions().colors[1],
            radius: '87%',
            innerRadius: '63%',
            y: 65
        }]
    }, {
        name: 'Stand',
        data: [{
            color: Highcharts.getOptions().colors[2],
            radius: '62%',
            innerRadius: '38%',
            y: 50
        }]
    }]
},

/**
 * In the chart load callback, add icons on top of the circular shapes
 */
function callback() {

    // Move icon
    this.renderer.path(['M', -8, 0, 'L', 8, 0, 'M', 0, -8, 'L', 8, 0, 0, 8])
        .attr({
            'stroke': '#303030',
            'stroke-linecap': 'round',
            'stroke-linejoin': 'round',
            'stroke-width': 2,
            'zIndex': 10
        })
        .translate(190, 26)
        .add(this.series[2].group);

    // Exercise icon
    this.renderer.path(['M', -8, 0, 'L', 8, 0, 'M', 0, -8, 'L', 8, 0, 0, 8,
            'M', 8, -8, 'L', 16, 0, 8, 8])
        .attr({
            'stroke': '#ffffff',
            'stroke-linecap': 'round',
            'stroke-linejoin': 'round',
            'stroke-width': 2,
            'zIndex': 10
        })
        .translate(190, 61)
        .add(this.series[2].group);

    // Stand icon
    this.renderer.path(['M', 0, 8, 'L', 0, -8, 'M', -8, 0, 'L', 0, -8, 8, 0])
        .attr({
            'stroke': '#303030',
            'stroke-linecap': 'round',
            'stroke-linejoin': 'round',
            'stroke-width': 2,
            'zIndex': 10
        })
        .translate(190, 96)
        .add(this.series[2].group);
});
}]);