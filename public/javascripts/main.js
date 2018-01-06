console.log("rending this page!");

function loadBackTestData() {
    const load = id => document.getElementById(id).value;
    const output = (content, successful) => {
        let el = document.getElementById("reload-output");
        let classList = el.classList;
        classList.remove("text-success", "text-warning");
        el.textContent = content;
        if (successful) classList.add("text-success");
        else classList.add("text-warning");
    };
    const url = new URL('/bot/backtest', window.location.href),
        params = {
            'currency-pair': load('currencyPair'),
            interval: load('interval'),
            'start-date': load('startDate'),
            'end-date': load('endDate'),
        };
    Object.keys(params).forEach(key => url.searchParams.append(key, params[key]))
    console.log("fetching from", url);
    output("");
    fetch(url, {
        method: 'get'
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error(`Network response was not ok. Got ${response.status} instead`);
        })
        .then(data => {
            const prices = data.candlesticks.map(candlestick => [new Date(candlestick.date).getTime(), candlestick.average]);
            renderChart(prices, null);
        })
        .then(() => {
            output('Rendered successfully!', true);
        })
        .catch(function (err) {
            console.log("error", err);
            output(`encountered an error: ${err}`, false);
        });

}

function renderChart(prices, trades) {
    // Create the chart
    Highcharts.stockChart('bot-chart', {
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
document.getElementById("reload")
    .addEventListener("click", loadBackTestData, false);
