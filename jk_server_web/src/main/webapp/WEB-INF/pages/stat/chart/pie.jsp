<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <title>amCharts examples</title>
        <link rel="stylesheet" href="${ pageContext.request.contextPath }/components/newAmcharts/style.css" type="text/css">
        <script src="${ pageContext.request.contextPath }/components/newAmcharts/amcharts/amcharts.js" type="text/javascript"></script>
        <script src="${ pageContext.request.contextPath }/components/newAmcharts/amcharts/pie.js" type="text/javascript"></script>
	
		<%-- 引入jq --%>
		<script type="text/javascript" src="${ pageContext.request.contextPath }/components/zTree/js/jquery-1.4.4.min.js"></script>

        <script>
            var chart;
            var legend;
            
			/* 
            // 数据，json的数据
            var chartData = [
                {
                    "country": "中国",
                    "value": 260
                },
                {
                    "country": "爱尔兰",
                    "value": 201
                },
                {
                    "country": "Germany",
                    "value": 65
                },
                {
                    "country": "Australia",
                    "value": 39
                },
                {
                    "country": "UK",
                    "value": 19
                },
                {
                    "country": "Latvia",
                    "value": 10
                }
            ];
             */
            
            // JQ的页面加载
            $(function(){
            	var url = "${pageContext.request.contextPath}/stat/statChartAction_getFactorysale.action";
            	$.post(url,function(data){
            		// 生成饼图
            		createPie(data);
            	},"json");
            });
            
            // 生成饼图的
            function createPie(data){
            	// PIE CHART
                chart = new AmCharts.AmPieChart();
                
                // chart.dataProvider = chartData;
                chart.dataProvider = data;
                
                chart.titleField = "factoryName";
                chart.valueField = "value";
                chart.outlineColor = "#FFFFFF";
                chart.outlineAlpha = 0.8;
                chart.outlineThickness = 2;
                chart.balloonText = "[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>";
                // this makes the chart 3D
                chart.depth3D = 15;
                chart.angle = 30;

                // WRITE
                chart.write("chartdiv");
            }

            /* 
            // new 对象 .xxx .方法
            // 页面加载
            AmCharts.ready(function () {
                // PIE CHART
                chart = new AmCharts.AmPieChart();
                
                chart.dataProvider = chartData;
                chart.titleField = "country";
                chart.valueField = "value";
                chart.outlineColor = "#FFFFFF";
                chart.outlineAlpha = 0.8;
                chart.outlineThickness = 2;
                chart.balloonText = "[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>";
                // this makes the chart 3D
                chart.depth3D = 15;
                chart.angle = 30;

                // WRITE
                chart.write("chartdiv");
            });
             */
        </script>
    </head>
	
    <body>
    	<%-- 容器 --%>
        <div id="chartdiv" style="width: 100%; height: 400px;"></div>
    </body>
	
</html>