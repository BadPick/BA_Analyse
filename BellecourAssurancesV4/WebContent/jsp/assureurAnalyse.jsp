<%@page import="fr.bellecour.statistiques.bo.Client"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<c:set var="client" value="${requestScope['clientAAnalyser']}"
	scope="page" />
<c:set var="contratList" value="${client.listeContrats}" scope="page" />
<c:set var="anneesList" value="${requestScope['listeAnneesTecn']}"
	scope="page" />

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css" />
<link rel="stylesheet" type="text/css" href="css/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="css/style.css" />
<script src="js/jquery-1.12.1.min.js"></script>
<script src="js/jquery-ui-1.11.4.js"></script>
<title>Bellecour statistiques</title>
</head>

<body>
	<%@include file="/jspf/header.jspf"%>
	<div class="container">
		<h1 id="titreScreen">Analyse assureur</h1>
		<%@include file="/jspf/message.jspf"%>

		<c:if test="${client == null}">
			<script>
				function loadDoc(value) {
					//déclaration
					var xhttp = new XMLHttpRequest();
					var url = "http://localhost:8080/BellecourAssurances/RechercheBox?type=client&rechercheBox="
							+ value;

					xhttp.onreadystatechange = function() {

						//récupération de la liste quand changement dans l'input 
						if (xhttp.readyState == 4 && xhttp.status == 200) {
							var data = xhttp.responseText;

							var parsed = JSON.parse(data);

							var items = [];

							for ( var x in parsed) {
								items.push(parsed[x]);
							}

							$("#rechercheBox").autocomplete({
								source : items
							});
						}
					};
					xhttp.open("GET", url, true);
					xhttp.send();
				}
			</script>
			<h2>Sélection du client :</h2>
			<div class="recherche">
				<form action="<%=request.getContextPath()%>/AnalyseAssureur"
					method="post">
					<div class="labelTexte">Veuillez choisir le client puis
						appuyez sur valider :</div>
					<div class="row">
						<div class="col-sm-2 labelTexte">Client :</div>
						<div class="col-sm-4">
							<input type="text" class="form-control" id="rechercheBox"
								name="nomClient" autocomplete="off" placeholder="rechercher..."
								autofocus onkeyup="loadDoc(this.value)" />
						</div>
						<div class="col-sm-2">
							<button type="submit" class="btn btn-default" name="typeAction"
								value="validerClient">Valider</button>
						</div>
					</div>
				</form>
			</div>
		</c:if>
		<!-- CLIENT -->
		<c:if test="${client != null }">
			<form action="<%=request.getContextPath()%>/AnalyseAssureur"
				method="post">
				<h2>Client :</h2>
				<div class="clientChoisi">
					<div class="row">
						<div class="col-sm-4 col-sm-offset-3">Nom : ${client.nom}</div>
						<div class="col-sm-2">

							<button type="submit" class="btn btn-default"
								title="modifier le client sélectionné pour l'analyse"
								name="typeAction" value="modifierClient">Modifier</button>
						</div>
					</div>
				</div>
			</form>

			<form action="<%=request.getContextPath()%>/AnalyseAssureur"
				id="contratAssureur">
				<input type="hidden" name="nomClient" value="${client.nom}">
				<!-- CONTRAT -->
				<c:if test="${contratList != null }">
					<h2>Contrats :</h2>
					<%@include file="/jspf/contratsListeAnalyseAssureur.jspf"%>
				</c:if>

				<c:if test="${requestScope['contrat'] != null }">
					<!-- PARAMETRE -->
					<h2>Paramètres de l'Analyse sinistres à primes :</h2>

					<!-- tableau primes -->
					<div class="row">
						<div class="col-sm-8 col-sm-offset-2">
							<table class="tableauARenseigner">
								<tr>
									<th colspan="4">Primes :</th>
								</tr>
								<tr>
									<th>N-3</th>
									<th>N-2</th>
									<th>N-1</th>
									<th>N</th>
								</tr>
								<tr>
									<td><input type="text" class="form-control"
										title="renseignez le cumul des primes pour l'année technique concernée"
										name="primeN-3" autocomplete="off"></td>
									<td><input type="text" class="form-control"
										title="renseignez le cumul des primes pour l'année technique concernée"
										name="primeN-2" autocomplete="off"></td>
									<td><input type="text" class="form-control"
										title="renseignez le cumul des primes pour l'année technique concernée"
										name="primeN-1" autocomplete="off"></td>
									<td><input type="text" class="form-control"
										title="renseignez le cumul des primes pour l'année technique concernée"
										name="primeN" autocomplete="off"></td>
								</tr>
							</table>
						</div>
					</div>
					<h2>Paramètres de l'Analyse de la fréquence :</h2>

					<!-- tableau nbre véhicules -->
					<div class="row">
						<div class="col-sm-8 col-sm-offset-2">
							<table class="tableauARenseigner">
								<tr>
									<th colspan="4">Nombre de véhicules :</th>
								</tr>
								<tr>
									<th>N-3</th>
									<th>N-2</th>
									<th>N-1</th>
									<th>N</th>
								</tr>
								<tr>
									<td><input type="text" class="form-control"
										title="renseignez le nombre de véhicule pour l'année technique concernée"
										name="vehiculesN-3" autocomplete="off"></td>
									<td><input type="text" class="form-control"
										title="renseignez le nombre de véhicule pour l'année technique concernée"
										name="vehiculesN-2" autocomplete="off"></td>
									<td><input type="text" class="form-control"
										title="renseignez le nombre de véhicule pour l'année technique concernée"
										name="vehiculesN-1" autocomplete="off"></td>
									<td><input type="text" class="form-control"
										title="renseignez le nombre de véhicule pour l'année technique concernée"
										name="vehiculesN" autocomplete="off"></td>
								</tr>
							</table>
						</div>
					</div>
					<div class="col-sm-6 col-sm-offset-3">
						<button type="submit" id="btnLancerAnalyse"
							class="btn btn-default" name="typeAction" value="lancerAnalyse">Lancer
							l'analyse</button>
					</div>
				</c:if>
			</form>
		</c:if>



	</div>
</body>
</html>