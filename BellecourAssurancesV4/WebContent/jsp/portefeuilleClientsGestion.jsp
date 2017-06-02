<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<c:set var="client" value="${requestScope['client']}" scope="page" />
<c:set var="contratList" value="${client.listeContrats}" scope="page" />

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"/>
		<link rel="stylesheet" type="text/css" href="css/jquery-ui.css"/>
		<link rel="stylesheet" type="text/css" href="css/style.css"/>
		<script src="js/jquery-1.12.1.min.js"></script>
		<script src="js/jquery-ui-1.11.4.js"></script>
		<title>Bellecour statistiques</title>
	</head>
	
	<body>
		<%@include file="/jspf/header.jspf" %>
		<div class="container">
			<%@include file="/jspf/message.jspf" %>	
				
			<h1 id="titreScreen">Gestion du portefeuille clients</h1>
			<c:if test="${requestScope['affichage']== 'liste'}">
				<%@include file="/jspf/clientRechercheBox.jspf" %>
	
				<c:if test="${client != null }">
				<h2>Client :</h2>
				<div class="clientChoisi">
					
					<div class="row">
						<div class="col-sm-4 col-sm-offset-3">Nom : ${client.nom}</div>
						<div class="col-sm-2">
						<form action="<%= request.getContextPath() %>/GestionPortefeuilleClients" method="post">
							<button type="submit" class="btn btn-default" name="typeAction" value="modifierClient">Modifier</button>
							<input type="hidden" name="codeClient" value="${client.codeClient}"/>
							<input type="hidden" name="nomClient" value="${client.nom}"/>
						</form>
						</div>
					</div>
				</div>
				</c:if>
				
				<c:if test="${contratList != null }">
					<h2>Contrats :</h2>
					<%@include file="/jspf/contratsListe.jspf" %>
				</c:if>
			</c:if>
			
			<c:if test="${requestScope['affichage']== 'formulaire'}">
				<c:if test="${requestScope['clientModif']!= null}">
					<%@include file="/jspf/clientFormulaire.jspf" %>
				</c:if>
				<c:if test="${requestScope['contratModif']!= null}">
					<%@include file="/jspf/contratsFormulaire.jspf" %>
				</c:if>
			</c:if>
								
		</div>
	</body>
</html>