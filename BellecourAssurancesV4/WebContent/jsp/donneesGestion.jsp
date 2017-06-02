<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

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
		<h1 id="titreScreen">Gestion des données</h1>
			<%@include file="/jspf/message.jspf" %>		
			
			
			<h2>Gestion des données :</h2>
			
			<div class="blockDonnees">
				<div class="row">
					<div class="col-sm-5"><div class="titreSection">Portefeuille clients</div>
				</div>
					<form action="<%= request.getContextPath() %>/GestionDonnees" method="post" enctype="multipart/form-data">
						<div class="col-sm-4">		
								<input type="file" name="file" class="form-control btn btn-default" title="Choisir le fichier à importer pour la mise à jour"/>
							</div>
							<div class="col-sm-2">
								<button class="btn btn-default" type="submit" name="typeAction" value="majPortefeuilleClients" title="Lancer la mise à jour">Mise à jour</button>
							</div>
							<div class="col-sm-1">
								<button class="btn btn-default" type="submit" name="typeAction" value="gererPortefeuilleClients" title="Passer sur l'écran de gestion (modification, suppression)">Gérer</button>
							</div>
					</form>
				</div>
			</div>
			
			<div class="blockDonnees">
				<div class="row">
				<div class="col-sm-5"><div class="titreSection">Sinistres</div></div>
					<form action="<%= request.getContextPath() %>/GestionDonnees" method="post" enctype="multipart/form-data">
					<div class="col-sm-4">		
						<input type="file" name="file" class="form-control btn btn-default"  title="Choisir le fichier à importer pour la mise à jour"/>
					</div>
					<div class="col-sm-2">
						<button class="btn btn-default" type="submit" name="typeAction" value="majSinistres" title="Lancer la mise à jour">Mise à jour</button>
						</div>
						<div class="col-sm-1">					
						<button class="btn btn-default" type="submit" name="typeAction" value="gererSinistres" title="Passer sur l'écran de gestion (modification, suppression)">Gérer</button>
						</div>
					</form>
				</div>

			</div>
			
			<div class="blockDonnees">
				<div class="row">
				<div class="col-sm-11"><div class="titreSection">Produits</div></div>

					<form>
					<div class="col-sm-1">
						<button class="btn btn-default" type="submit" name="typeAction" value="gererProduits" title="Passer sur l'écran de gestion (ajout, modification, suppression)">Gérer</button>
						</div>
					</form>
				</div>

			</div>
			
			<div class="blockDonnees">
				<div class="row">
				<div class="col-sm-11"><div class="titreSection">Circonstances de sinistre</div></div>

					<form>
					<div class="col-sm-1">
						<button class="btn btn-default" type="submit" name="typeAction" value="gererCirconstances" title="Passer sur l'écran de gestion (ajout, modification, suppression)">Gérer</button>
						</div>
					</form>
				</div>
			</div>
			
			<div class="blockDonnees">
				<div class="row">
				<div class="col-sm-11"><div class="titreSection">Natures de sinistre</div></div>

					<form>
					<div class="col-sm-1">
						<button class="btn btn-default" type="submit" name="typeAction" value="gererNatures" title="Passer sur l'écran de gestion (ajout, modification, suppression)">Gérer</button>
						</div>
					</form>
				</div>

			</div>
								
		</div>
	</body>
</html>