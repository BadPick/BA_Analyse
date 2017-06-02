<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<c:set var="totalGenerale" value="${requestScope['totalGenerale']}" scope="page" />
<c:set var="resultatPrimes" value="${requestScope['resultatPrimes']}" scope="page" />
<c:set var="resultatVehicules" value="${requestScope['resultatVehicules']}" scope="page" />
<c:set var="recap" value="${requestScope['recap']}" scope="page" />
<c:set var="client" value="${requestScope['clientAAnalyser']}" scope="page" />

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
		<title>Bellecour statistiques</title>
		<c:if test="${requestScope['analysePrimes'] == true}">
			<script type="text/javascript">
			google.charts.setOnLoadCallback(drawChartPrimes);
			
			function drawChartPrimes() 
		      {
		        var data = google.visualization.arrayToDataTable(<%=request.getAttribute("jsonPrimes")%>);

		        var options = 
		        {	        	      
		        		fontSize:15,
				        chartArea: {top:40, width: '90%', height: '70%'},
				        legend: {position: 'top'},
				        titlePosition: 'out', axisTitlesPosition: 'out',
				        hAxis: {textPosition: 'out'}, vAxis: {textPosition: 'out'},
				        seriesType: 'bars',
				        series: {1: {type: 'line'}},
				        vAxis: {title: 'Ratio sinistres/primes'},
				        hAxis: {title: 'Années techniques'},
				        colors: ['#034381', '#fa6816']};

		        var chartPrimes = new google.visualization.ComboChart(document.getElementById('columnchartPrimes'));

		        chartPrimes.draw(data, options);
		      }
			</script>
		</c:if>
		
		<c:if test="${requestScope['analyseVehicules'] == true}">
			<script type="text/javascript">
			google.charts.setOnLoadCallback(drawChartVehicules);
			
			function drawChartVehicules() 
		      {
		        var data = google.visualization.arrayToDataTable(<%=request.getAttribute("jsonVehicules")%>);

		        var options = 
		        {	        	      
		        		fontSize:15,
				        chartArea: {top:40, width: '90%', height: '70%'},
				        legend: {position: 'top'},
				        titlePosition: 'out', axisTitlesPosition: 'out',
				        hAxis: {textPosition: 'out'}, vAxis: {textPosition: 'out'},
				        seriesType: 'bars',
				        series: {1: {type: 'line'}},
				        vAxis: {title: 'Ratio nbre sinistres/nbre véhicules'},
				        hAxis: {title: 'Années techniques'},
				        colors: ['#034381', '#fa6816']};

		        var chartVehicules = new google.visualization.ComboChart(document.getElementById('columnchartVehicules'));

		        chartVehicules.draw(data, options);
		      }
			</script>
		</c:if>
	</head>
	
	<body>
		<%@include file="/jspf/header.jspf" %>
			<div class="menuBar">
			<div class="row">
				<form action="<%= request.getContextPath() %>/AnalyseAssureur" method="post">
					<div class="col-sm-3"><button type="submit" class="btn btn-default" name="typeAction" value="validerClient">Retour</button></div>
					<input type="hidden" name="nomClient" value="${client.nom}">
				</form>
			<br><br><div class="col-sm-3"><button class="btn btn-default" onclick="window.print()">Imprimer</button></div>
			</div>
		</div>
		<div class="container">
			<%@include file="/jspf/message.jspf" %>				
			<h1 id="titreScreen">Résultat de l'analyse</h1>
		
		<!-- --------------------ANALYSE ASSUREUR-------------------------- -->
		
				<!-- récap analyse -->
				<c:set var="recap" value="${requestScope['recap']}" scope="page" />
				<div class="row">
					<%@include file="/jspf/bandeauRecapAnalyseAssureur.jspf" %>
				</div>
				
				<!-- PRIMES -->
				<c:if test="${requestScope['analysePrimes'] == true}">
				<div class="blockAnalyse">

					<div class="titreRepresentation"><img class="carre" src="images/carreOrange.png">Le rapport sinistres à primes</div>

					
					<table class="tableauResultat">
					<col width="25%">
					<col width="15%">
					<col width="15%">
					<col width="15%">
					<col width="15%">
					<col width="15%">
						<tr class="premiereLigne bordGrasBas">
							<th><img src="images/troisCarres.png" width="53" height="20"></th>
							<th>N-3</th>
							<th>N-2</th>
							<th>N-1</th>
							<th>N</th>
							<th>TOTAL</th>
						</tr>
						<tr>
							<td class="ecrireGras">La charge sinistre</td>
							<td>${totalGenerale[1]}</td>
							<td>${totalGenerale[3]}</td>
							<td>${totalGenerale[5]}</td>
							<td>${totalGenerale[7]}</td>
							<td>${totalGenerale[9]}</td>
						</tr>
						<tr>
							<td class="ecrireGras">Les primes</td>
							<td>${resultatPrimes[0]}</td>
							<td>${resultatPrimes[2]}</td>
							<td>${resultatPrimes[4]}</td>
							<td>${resultatPrimes[6]}</td>
							<td>${resultatPrimes[8]}</td>
						</tr>
						<tr class="total">
							<td>Les ratios sinistres/primes</td>
							<td>${resultatPrimes[1]}</td>
							<td>${resultatPrimes[3]}</td>
							<td>${resultatPrimes[5]}</td>
							<td>${resultatPrimes[7]}</td>
							<td>${resultatPrimes[9]}</td>
						</tr>
					</table>
								

					<div class="sousTitreRepresentation">Rapport sinistres à primes (années techniques/ratios)</div>					
						<div class="col-sm-12">
							<div class="columnGraph" id="columnchartPrimes"></div>
						</div>							
				</div>
				</c:if>
				
				<!-- VEHICULES -->
				<c:if test="${requestScope['analyseVehicules'] == true}">
				<div class="blockAnalyse">
				
				<div class="titreRepresentation"><img class="carre" src="images/carreOrange.png">La fréquence</div>
				
					<table class="tableauResultat">
					<col width="25%">
					<col width="15%">
					<col width="15%">
					<col width="15%">
					<col width="15%">
					<col width="15%">
						<tr class="premiereLigne bordGrasBas">
							<th><img src="images/troisCarres.png" width="53" height="20"></th>
							<th>N-3</th>
							<th>N-2</th>
							<th>N-1</th>
							<th>N</th>
							<th>TOTAL</th>
						</tr>
						<tr>
							<td class="ecrireGras">Le nombre de sinistres</td>
							<td>${totalGenerale[0]}</td>
							<td>${totalGenerale[2]}</td>
							<td>${totalGenerale[4]}</td>
							<td>${totalGenerale[6]}</td>
							<td>${totalGenerale[8]}</td>
						</tr>
						<tr class="ecrireGras">
							<td>Le nombre de véhicules</td>
							<td>${resultatVehicules[0]}</td>
							<td>${resultatVehicules[2]}</td>
							<td>${resultatVehicules[4]}</td>
							<td>${resultatVehicules[6]}</td>
							<td>${resultatVehicules[8]}</td>
						</tr>
						<tr class="total">
							<td>La fréquence</td>
							<td>${resultatVehicules[1]}</td>
							<td>${resultatVehicules[3]}</td>
							<td>${resultatVehicules[5]}</td>
							<td>${resultatVehicules[7]}</td>
							<td>${resultatVehicules[9]}</td>
						</tr>
					</table>		
		
				<div class="sousTitreRepresentation">Fréquence (années techniques/ratios)</div>					
						<div class="col-sm-12">
							<div class="columnGraph" id="columnchartVehicules"></div>
						</div>
				</div>
				</c:if>
		</div>		
	</body>
