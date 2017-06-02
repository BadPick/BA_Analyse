<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"/>
		<link rel="stylesheet" type="text/css" href="css/jquery-ui.css"/>
		<link rel="stylesheet" type="text/css" href="css/style.css"/>
		<link rel="stylesheet" type="text/css" href="css/print.css">
		<script src="js/jquery-1.12.1.min.js"></script>
		<script src="js/jquery-ui-1.11.4.js"></script>
		<!--  <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>-->
		<script type="text/javascript" src="js/pieChartLoader.js"></script>
		<script type="text/javascript">google.charts.load('current', {'packages':['corechart', 'bar', 'gauge','scatter']});</script>
		<script type="text/javascript">
		google.charts.setOnLoadCallback(drawChartNatureCumul);
		google.charts.setOnLoadCallback(drawChartNatureN);
		google.charts.setOnLoadCallback(drawChartNatureColumn);
		
		function drawChartNatureColumn() 
	      {
	        var data = google.visualization.arrayToDataTable(<%=request.getAttribute("jsonNatureColumn")%>);

	        var options = {	
	        	fontSize:16,
		        chartArea: {top:40, width: '80%', height: '80%'},
		        legend: {position: 'top',maxlines:2},
		        titlePosition: 'out', axisTitlesPosition: 'out',
		        hAxis: {textPosition: 'out'}, vAxis: {textPosition: 'out'},
		        seriesType: 'bars',
		        vAxis: {title: 'Coût en €uros'},
		        hAxis: {title: 'Nature de sinistre'},
		        slices: {
	                0: { color: '#FFCC33' },
	                1: { color: '#00CC66' },
	                2: { color: '#DEEBF7' },
	                3: { color: '#234CA5' },
	                4: { color: '#6633FF' },
	                5: { color: '#990066' },
	                6: { color: '#CC6633' },
	                7: { color: '#CC0000' }
	             
	              }};

	        var chart = new google.visualization.ComboChart(document.getElementById('columnchartNatureColumn'));

	        chart.draw(data, options);
	      }
		
		function drawChartNatureCumul() 
	      {	
	    	var data = google.visualization.arrayToDataTable(<%=request.getAttribute("JsonNatureCumul")%>);
	        	        
	        var options = 
	        {
	          chartArea:{left:0,top:30,width:'70%',height:'90%'},
	          fontSize: 14,
	          is3D: true,
	          slices: {
	                0: { color: '#FFCC33' },
	                1: { color: '#00CC66' },
	                2: { color: '#DEEBF7' },
	                3: { color: '#234CA5' },
	                4: { color: '#6633FF' },
	                5: { color: '#990066' },
	                6: { color: '#CC6633' },
	                7: { color: '#CC0000' }
	             
	              }
	          
	        };

	        var chartNatureCumul = new google.visualization.PieChart(document.getElementById('piechartNatureCumul'));

	        chartNatureCumul.draw(data, options);
	      }
	      
	      function drawChartNatureN() 
	      {

	          var data = google.visualization.arrayToDataTable(<%=request.getAttribute("JsonNatureN")%>);

	          var options = {
	            chartArea:{left:0,top:30,width:'70%',height:'90%'},
	            fontSize: 14,
	            is3D: true,
	            slices: {
	            	0: { color: '#FFCC33' },
	                1: { color: '#00CC66' },
	                2: { color: '#DEEBF7' },
	                3: { color: '#234CA5' },
	                4: { color: '#6633FF' },
	                5: { color: '#990066' },
	                6: { color: '#CC6633' },
	                7: { color: '#CC0000' }
	              }
	          };

	          var chartNatureN = new google.visualization.PieChart(document.getElementById('piechartNatureN'));

	          chartNatureN.draw(data, options);
	        }
		</script>
		<title>Bellecour statistiques</title>
	</head>
	
	<body>
		<%@include file="/jspf/header.jspf" %>
		<%@include file="/jspf/boutonsBar.jspf" %>
		<div class="container">
			<%@include file="/jspf/message.jspf" %>	
			
			<h1 id="titreScreen">Résultat de l'analyse</h1>
		
		</div>
			<!-- --------------------ANALYSE-------------------------- -->
			<div class="container">
			<!-- récap analyse -->
				<c:set var="recap" value="${requestScope['recap']}" scope="page" />
				<div class="row">
					<%@include file="/jspf/bandeauRecapAnalyseDetaillee.jspf" %>
				</div>
			
			<!-- tableau general -->
			<c:set var="listeSinistres" value="${requestScope['listeSinistres']}" scope="page" />
			<c:if test="${fn:length(listeSinistres)>0}">		
				<%@include file="/jspf/resultatsStatistiques/tabGeneraleAutres.jspf" %>

			
			
			
			
			<div class="blockAnalyse">
						<div class="titreRepresentation"><img class="carre" src="images/carreOrange.png">Nomenclature des natures de sinistres</div>
					<%@include file="/jspf/resultatsStatistiques/nomenclatureNatures.jspf" %>
			</div>
			<!-- tableau resultats natures -->
			<!-- Analyse par nature -->
			
			<%@include file="/jspf/entete.jspf" %>

				<div class="blockAnalyse">
					<div class="titreRepresentation"><img class="carre" src="images/carreOrange.png">Ventilation par nature de sinistres</div>
					<c:set var="listeResultats" value="${requestScope['resultatNature']}" scope="page" />
					<%@include file="/jspf/resultatsStatistiques/tabResultats.jspf" %>	
				</div>
				<div class="blockAnalyse">
					<div class="row">
						<div class="col-sm-6">
							<div class="sousTitreRepresentation">Nature de sinistre en nombre (N-3, N-2, N-1)</div>
							<div class="graph" id="piechartNatureCumul"></div>
						</div>
						<div class="col-sm-6">
							<div class="sousTitreRepresentation">Nature de sinistre en nombre (N)</div>
							<div class="graph" id="piechartNatureN"></div>
						</div>
					</div>
					</div>
					
					<div class="blockAnalyse">
						<div class="sousTitreRepresentation">Nature de sinistre en coût (Nature/€)</div>
						<div class="col-sm-12">
							<div class="columnGraph" id="columnchartNatureColumn"></div>
						</div>
					</div>
									
				
			</c:if>
			<!-- pieCharts -->
			
			<!-- columnCHart -->
			<c:if test="${fn:length(listeSinistres)==0}">
				<div>Aucun sinistre à analyser</div>
			</c:if>

		</div>
	</body>
