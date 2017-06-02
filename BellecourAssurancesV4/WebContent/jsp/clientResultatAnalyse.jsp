<%@page import="fr.bellecour.statistiques.bo.Client"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<c:set var="contratList" value="${client.listeContrats}" scope="page" />
<c:set var="JsonNatureFlotteN"
	value="${requestScope['JsonNatureFlotteN']}" scope="page" />

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css" />
<link rel="stylesheet" type="text/css" href="css/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="css/style.css" />
<link rel="stylesheet" type="text/css" href="css/print.css">
<script src="js/jquery-1.12.1.min.js"></script>
<script src="js/jquery-ui-1.11.4.js"></script>
<!--  <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>-->
<script type="text/javascript" src="js/pieChartLoader.js"></script>
<script type="text/javascript">
	google.charts.load('current', {
		'packages' : [ 'corechart', 'bar', 'gauge', 'scatter' ]
	});
</script>

<title>Bellecour statistiques</title>
</head>

<body>
	<%@include file="/jspf/header.jspf"%>
	<%@include file="/jspf/boutonsBar.jspf"%>
	<div class="container">
		<%@include file="/jspf/message.jspf"%>
		<c:set var="recap" value="${requestScope['recapFlotte']}" scope="page" />
		<h1 id="titreScreen">Résultat de l'analyse</h1>

		<!-- --------------------ANALYSE GENERALE ANNEES CONFONDUES-------------------------- -->
		<c:if test="${requestScope['analyse'] == 'generaleC' }">
			<div class="container">
				<c:set var="recap" value="${requestScope['recapFlotte']}" scope="page" />
				<c:set var="total" value="${requestScope['ligneTotalGenerale']}" scope="page" />
				<div class="row">
					<%@include file="/jspf/bandeauRecapGenerale.jsp"%>
				</div>
				<c:set var="listeSinistres" value="${requestScope['listeSinistresFlotte']}" scope="page" />
				<%@include file="/jspf/resultatsStatistiques/tabGenerale.jspf"%>
			</div>
		</c:if>

		<!-- --------------------ANALYSE GENERALE PAR ANNEES-------------------------- -->
		<c:if test="${requestScope['analyse'] == 'generaleU' }">
			<c:set var="recap" value="${requestScope['recapFlotte']}" scope="page" />

			<c:if test="${requestScope['listeSinistresFlotteN3'] != null}">
				<div class="container">
					<c:set var="total" value="${requestScope['ligneTotalGeneraleN3']}" scope="page" />
					<c:set var="listeSinistres" value="${requestScope['listeSinistresFlotteN3']}" scope="page" />
					<c:set var="periode" value="${requestScope['periodeN3']}" scope="page" />
					<div class="row">
						<%@include file="/jspf/bandeauRecapGeneraleParAnnee.jsp"%>
					</div>

					<!-- Tableau générale -->
					<%@include file="/jspf/resultatsStatistiques/tabGenerale.jspf"%>
				</div>
			</c:if>

			
			<c:if test="${requestScope['listeSinistresFlotteN2'] != null}">
				<div class="container">
					<c:set var="total" value="${requestScope['ligneTotalGeneraleN2']}" scope="page" />
					<c:set var="listeSinistres"
						value="${requestScope['listeSinistresFlotteN2']}" scope="page" />
					<c:set var="periode" value="${requestScope['periodeN2']}" scope="page" />

					<!-- récap analyse -->
					<c:if test="${requestScope['firstAnalyse']!= 'N2'}">
						<div class="newpage"></div>
					</c:if>

					<div class="row">
						<%@include file="/jspf/bandeauRecapGeneraleParAnnee.jsp"%>
					</div>
					<!-- Tableau générale -->
					<%@include file="/jspf/resultatsStatistiques/tabGenerale.jspf"%>
				</div>

			</c:if>

			<!-- ANALYSE N1 -->
			<c:if test="${requestScope['listeSinistresFlotteN1'] != null}">
				<div class="container">
					<c:set var="total" value="${requestScope['ligneTotalGeneraleN1']}" scope="page" />
					<c:set var="listeSinistres"
						value="${requestScope['listeSinistresFlotteN1']}" scope="page" />
					<c:set var="periode" value="${requestScope['periodeN1']}" scope="page" />

					<!-- récap analyse -->
					<c:if test="${requestScope['firstAnalyse']!= 'N1'}">
						<div class="newpage"></div>
					</c:if>
					<div class="row">
						<%@include file="/jspf/bandeauRecapGeneraleParAnnee.jsp"%>
					</div>
					<!-- Tableau générale -->
					<%@include file="/jspf/resultatsStatistiques/tabGenerale.jspf"%>
				</div>

			</c:if>

			<!-- ANALYSE N -->
			<c:if test="${requestScope['listeSinistresFlotteN'] != null}">
				<div class="container">
					<c:set var="total" value="${requestScope['ligneTotalGeneraleN']}" scope="page" />
					<c:set var="listeSinistres"
						value="${requestScope['listeSinistresFlotteN']}" scope="page" />
					<c:set var="periode" value="${requestScope['periodeN']}" scope="page" />

					<!-- récap analyse -->
					<c:if test="${requestScope['firstAnalyse']!= 'N'}">
						<div class="newpage"></div>
					</c:if>
					<div class="row">
						<%@include file="/jspf/bandeauRecapGeneraleParAnnee.jsp"%>
					</div>
					<!-- Tableau générale -->
					<%@include file="/jspf/resultatsStatistiques/tabGenerale.jspf"%>
				</div>

			</c:if>

		</c:if>


		<!-- --------------------ANALYSE DETAILLE-------------------------- -->
		<c:if test="${requestScope['analyse'] == 'graphique' }">
			<%@include file="/jspf/resultatsStatistiques/chargementGraphiqueFlotte.jspf"%>
			<%@include file="/jspf/resultatsStatistiques/analyseFlotteDetaille.jspf"%>
		</c:if>
	</div>
</body>