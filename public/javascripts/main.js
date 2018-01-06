console.log("rending this page!");

function loadBackTestData() {
    console.log("fetching!");
    fetch('/bot/backtest', {
        method: 'get'
    })
        .then(r => r.json())
        .then(data => {
            const prices = data.candlesticks.map(candlestick => [new Date(candlestick.date).getTime(), candlestick.average]);
            renderChart(prices, null);
        })
        .catch(function (err) {
            console.log("error", err)
        });

}

function renderChart(prices, trades) {
    // Create the chart
    Highcharts.stockChart('container', {
        rangeSelector: {
            selected: 5
        },
        title: {
            text: 'USDT_BTC'
        },
        tooltip: {
            style: {
                width: '200px'
            },
            valueDecimals: 2,
            shared: true
        },
        yAxis: {
            title: {
                text: 'USD'
            }
        },
        series: [{
            name: 'USDT_BTC',
            data: prices,
            id: 'dataseries'
        },
            // {
            //     type: 'flags',
            //     data: trades,
            //     onSeries: 'dataseries',
            //     shape: 'circlepin',
            //     width: 10
            // }
        ]
    });
}

window.onload = loadBackTestData();
