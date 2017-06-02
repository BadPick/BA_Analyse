<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<c:set var="listeNatures" value="${requestScope['listeNatures']}" scope="page" />

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"/>
		<link rel="stylesheet" type="text/css" href="css/style.css"/>
		<link rel="stylesheet" type="text/css" href="css/print.css">
		<title>Bellecour statistiques</title>
	</head>
	
	<body>
		<%@include file="/jspf/header.jspf" %>
		<div class="container">
		<c:if test="${requestScope['affichage']== 'nomenclature'}">
			<div class="menuBar">
			<div class="row">
				<form action="<%= request.getContextPath() %>/GestionNatures" method="post">
					<div class="col-sm-3"><button type="submit" class="btn btn-default" name="typeAction" value="gererNatures">Retour</button></div>
				</form>
			<br><br><div class="col-sm-3"><button class="btn btn-default" onclick="window.print()">Imprimer</button></div>
			</div>
		</div>
		</c:if>
			<h1 id="titreScreen">Gestion des natures</h1>
			<%@include file="/jspf/message.jspf" %>		
			
				<c:if test="${requestScope['affichage']== 'liste'}">
					<%@include file="/jspf/naturesListe.jspf" %>
				</c:if>
				
				<c:if test="${requestScope['affichage']== 'formulaire'}">
					<%@include file="/jspf/naturesFormulaire.jspf" %>
				</c:if>
				
				<c:if test="${requestScope['affichage']== 'nomenclature'}">
					<div class="col-sm-12">
						<%@include file="/jspf/nomenclatureNaturesGlobale.jspf" %>
					</div>
				</c:if>
								
		</div>
	</body>
</html>