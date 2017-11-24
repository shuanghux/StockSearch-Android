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
	chartOptions['PRICE'] = {
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
			        tickInterval: 10
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





	mychart = Highcharts.chart('highchart_PRICE', chartOptions['PRICE']);




};