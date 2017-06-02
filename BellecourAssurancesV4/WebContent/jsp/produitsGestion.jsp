<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<c:set var="produitList" value="${requestScope['produitList']}" scope="page" />

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"/>
		<link rel="stylesheet" type="text/css" href="css/style.css"/>
		<title>Bellecour statistiques</title>
	</head>
	
	<body>
		<%@include file="/jspf/header.jspf" %>
		<div class="container">
		<h1 id="titreScreen">Gestion des produits</h1>
			<%@include file="/jspf/message.jspf" %>		
			
				<c:if test="${requestScope['affichage']== 'liste'}">
					<%@include file="/jspf/produitsListe.jspf" %>
				</c:if>
				
				<c:if test="${requestScope['affichage']== 'formulaire'}">
					<%@include file="/jspf/produitsFormulaire.jspf" %>
				</c:if>
								
		</div>
	</body>
</html>