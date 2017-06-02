/**
 * 
 */


//google.charts.load('current', {'packages':['corechart']});
      

google.charts.setOnLoadCallback(drawChartNatureN);
//google.charts.setOnLoadCallback(drawChartNatureCumul());

      function drawChartNatureCumul(JsonNatureFlotteN) 
      {
    	alert(JsonNatureFlotteN);
        var data = JsonNatureFlotteN;
        
        
        var options = 
        {
          title: 'My Daily Activities',
          chartArea:{left:0,top:30,width:'100%',height:'1000%'}
        };

        var chartNatureCumul = new google.visualization.PieChart(document.getElementById('piechartNatureCumul'));

        chartNatureCumul.draw(JsonNatureFlotteN, options);
      }
      
      function drawChartNatureN() 
      {

          var data = google.visualization.arrayToDataTable([
            ['Task', 'Hours per Day'],
            ['Work',     11],
            ['Eat',      2],
            ['Commute',  2],
            ['Watch TV', 2],
            ['Sleep',    7]
          ]);

          var options = {
            title: 'My Daily Activities',
            chartArea:{left:0,top:30,width:'100%',height:'1000%'}
          };

          var chartNatureN = new google.visualization.PieChart(document.getElementById('piechartNatureN'));

          chartNatureN.draw(data, options);
        }