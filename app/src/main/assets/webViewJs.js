var chartContainer = "container";
var apiUrl = "http://shuang.us-east-1.elasticbeanstalk.com/";
var data_PRICE;
var chartDataSet = [];
var curr_symb;
var data_History;
function load_price(symbol) {
	curr_symb = symbol;
	$.ajax({
		url: apiUrl+'price/'+ symbol,
		dataType: 'json'
	})
	.done(function(data) {
				//console.log(data);
				if(isDataOk(data)) {
					data_PRICE = data;
					showPrice(data);
					getChartUrl();
				}
			})
}


function showPrice(data_PRICE) {
	var topkeys = Object.keys(data_PRICE);
	var title = data_PRICE[topkeys[0]]["Title"];
	var dates = Object.keys(data_PRICE[topkeys[1]]);
	var names = Object.keys(data_PRICE[topkeys[1]][dates[0]]);
	var prices = [], volumes = [];
	dates.forEach(function(date){
		prices.push(parseFloat(data_PRICE[topkeys[1]][date]["Stock Price"]));
		volumes.push(parseInt(data_PRICE[topkeys[1]][date]["Volume"]));
	});
	chartOption= {
		chart: {
			type: 'area',
			pinchType: 'x',
			zoomType: 'x'
		},
		title: {
			text:  title
		},
		subtitle: {
			text: 'Source: <a href="https://www.alphavantage.co/" target="_blank">' +
			'Alpha Vantage</a>',
			style: {
				color : '#0000EE'
			}
		},
		legend: {
			align: 'center',
			verticalAlign: 'bottom',
			x: 0,
			y: 0
		},
		xAxis: {
			"categories": dates,
			tickInterval: 30
		},
		yAxis:[
		{
			title:{
				text:"Stock Price"
			},
			labels:{
				"format":"{value:,.2f}"
			},
						         // tickInterval: 5,
						         labels: {
						         	format: '{value:.0f}'
						         },
						         // max: 1.1*maxPri,
						         //min: 0.9*minPri
						     },
						     {
						     	title:{
						     		text:"Volume"
						     	},
						     	opposite:true,
						         // max: 4*maxVol
						     }
						     ],
						     tooltip: {
						     	pointFormat: '{series.name} :<b>{point.y:,.2f}</b>'
						     },
						     series: [
						     {
						     	type:"area",
						     	name: names[0],
						     	data: prices,
					         // threshold:null,
					         yAxis:0,
					         // tooltip:{
					         //    "pointFormat":"MSFT: {point.y:,..2f}"
					         // },
					         color:"#0000ff",
					         fillOpacity: 0.1,
					         marker:{
					         	"enabled":false
					         }
					     },
					     {
					     	type:"column",
					     	name: names[1],
					     	data: volumes,
					     	yAxis:1,
					     	color:"red",
					     	tooltip: {
					     		pointFormat: '{series.name} :<b>{point.y:,.0f}</b>'
					     	}
					     }
					]
				};
		mychart = Highcharts.chart('container', chartOption);
};

function isDataOk(data){
	if(data["Error Message"]){
		return false;
	} else {
		return true;
	}
}

function load_Chart(type,symbol) {
	curr_symb = symbol;
	type = type.toUpperCase();
	$.ajax({
				url: apiUrl+'chart/' + type + '/' + symbol,
				dataType: 'json'
			})
			.done(function(data) {
				//console.log(data);
				chartDataSet[type] = data;
				showChart(type);
				getChartUrl();
				
			})
			.fail(function() {
				// console.log("Ema error");
			})
			.always(function() {
			});
}

function showChart(type) {
	if(isDataOk(chartDataSet[type])){
		var jsonObj = chartDataSet[type.toUpperCase()];
		var topkeys = Object.keys(jsonObj);
		var title = jsonObj[topkeys[0]]["Title"];
		var dates = Object.keys(jsonObj[topkeys[1]]);
		var dataTitles = Object.keys(jsonObj[topkeys[1]][dates[0]]);
		// dataSets contains several series of properties, such as low, high.
		var dataSets = new Array(Object.keys(jsonObj[topkeys[1]][dates[0]]).length);
		for(var i = 0; i < dataTitles.length; i++) {
			dataSets[i] = [];
			dates.forEach(function(date) {
				dataSets[i].push(parseFloat(jsonObj[topkeys[1]][date][dataTitles[i]]));
			});
		}
		myChart = createEmptyChart(type);
		myChart.setTitle({ text: jsonObj["Meta Data"]["Title"]});
		myChart.xAxis[0].setCategories(
				dates
			);
		myChart.xAxis[0].setTickInterval(30);
		myChart.yAxis[0].setTitle({text: type.toUpperCase()});
		for (var i = 0; i < dataTitles.length; i++) {
			myChart.addSeries({
				name: curr_symb+ " "+dataTitles[i],
				data: dataSets[i],
				threshold:null,
				yAxis:0,
				marker:{
					"enabled":true,
					radius: 1
				},
			})
		}
		//saveLineURL(type);
	}
}


function createEmptyChart() {
	var topkeys = Object.keys(data_PRICE);
	var datesArr = Object.keys(data_PRICE[topkeys[1]]);
	var chartVar = Highcharts.chart(chartContainer, {
						chart: {
					        type: 'line',
					        zoomType: 'x'
					    },
					    subtitle: {
					        text: 'Source: <a href="https://www.alphavantage.co/">' +
					            'Alpha Vantage</a>',
					        style: {
	                            color : '#0000EE'
	                        }
					    },
					    
					    legend: {
					    	align: 'center',
		                    verticalAlign: 'bottom',
		                    x: 0,
		                    y: 0
					    },
					    tooltip: {
					    	pointFormat: '{series.name}: {point.y}',
					    },
					    responsive: {
					        rules: [{
					            condition: {
					                maxWidth: 500
					            },
					            chartOptions: {
					                legend: {
					                    layout: 'horizontal',
					                    align: 'center',
					                    verticalAlign: 'bottom'
					                }
					            }
					        }]
					    }

					});
	return chartVar;
}

function loadHistory(symb){
	curr_symb = symb;
	$.ajax({
				url: apiUrl+"history/" + symb,
				dataType: 'json'
			})
			.done(function(data) {
				//console.log(data);
				if(data["Error Message"]) {
					console.log("ajax error")
				} else {
					data_History = data;
					showHistory();
				}
			})
			.fail(function() {
				// console.log("history error");
				console.log("ajax error");
			})
			.always(function() {
			});
}

function showHistory() {

    // var dates = Object.keys(data_History["Time Series (Daily)"]);
    // dates.reverse();
    // var price = [];
    // dates.forEach(function(date){
    //     price.push(parseFloat(data_History["Time Series (Daily)"][date]["4. close"]));
    // });
    // console.log(document.getElementById("currentStock").offsetWidth);
    var chart = Highcharts.stockChart('history_container', {
        chart: {
            // height: 400,
            // width: document.getElementById("currentStock").offsetWidth-20
        },

        title: {
            text: curr_symb + " Stock Value"
        },
        subtitle: {
            text: 'Source: <a href="https://www.alphavantage.co/" target="_blank">' +
                        'Alpha Vantage</a>'
        },

        rangeSelector: {
            buttons: [{
                        type: 'week',
                        count: 1,
                        text: '1w'
                    },{
                        type: 'month',
                        count: 1,
                        text: '1m'
                    }, {
                        type: 'month',
                        count: 3,
                        text: '3m'
                    }, {
                        type: 'month',
                        count: 6,
                        text: '6m'
                    }, {
                        type: 'ytd',
                        text: 'YTD'
                    }, {
                        type: 'year',
                        count: 1,
                        text: '1y'
                    }, {
                        type: 'all',
                        text: 'All'
                    }],
            selected: 0
        },
        tooltip: {
            xDateFormat: '%A,%b %d,%Y',
            shared: true,
            split: false,
        },

        series: [{
            name: curr_symb,
            data: data_History,
            type: 'area',
            threshold: null,
            tooltip: {
                valueDecimals: 2
            }
        }],

        responsive: {
            rules: [{
                condition: {
                    maxWidth: 500
                },
                chartOptions: {
                    
                    rangeSelector : {
                       inputEnabled:false
                    },
                }
            }]
        }
    });
    chart.reflow();
}

function getChartUrl() {
	var chart = $("#container").highcharts();
	var obj = {};
    obj.svg = chart.getSVG();
    obj.type = 'image/png';
    obj.async = true;
    var exportUrl = 'http://export.highcharts.com/';
    var imgURL;
    $.ajax({
            type: "POST",
            url: exportUrl,
            data: obj,
            cache:false,
            async:true,
            crossDomain:true,
            success: function (data) {
            	console.log("success: ");
            	imgURL = exportUrl+data;
            	window.myMainHandler.sendUrlToJava(imgURL);
            },
            error: function(data) {
            	console.log("error");
            }
        });
    }