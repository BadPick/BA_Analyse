<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="recap">
<div class="entete">
	<div class="row">	
		<div class="col-sm-12">	
		<div id="titreRecap">Votre bilan sinistres</div>
		</div>
		<br>
		<br>
		<br>
	</div>
</div>
	<div class="row">
		<div class="col-sm-6 print-only">
			<img alt="BELLECOUR Statistiques" src="images/logoBellecour.png" width="370" height="90">
		</div>
		<div class="col-sm-6">
	<table class="tableauRecap">
		<tr>
			<th>Nom client :</th>
			<th>${recap[0]}</th>
		</tr>
		<tr>
			<th>Date d'effet : </th>
			<td>${recap[2]}</td>
		</tr>
		<tr>
			<th>période d'analyse : </th>
			<td>${recap[3]}</td>
		</tr>
		<tr>
			<th>Branche d'analyse : </th>
			<td>${recap[4]}</td>
		</tr>
	</table>
	</div>
	<!-- TABLEAU ANNEES TECHNIQUES -->
	</div>
	<h2 class="orangeBleu"><c:out value="${recap[1]}"/></h2>
</div>