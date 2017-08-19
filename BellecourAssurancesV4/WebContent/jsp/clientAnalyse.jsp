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
<c:set var="branche" value="${requestScope['branche']}" scope="page" />

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
		<%@include file="/jspf/message.jspf"%>
		<h1 id="titreScreen">Analyse client</h1>
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
				<form action="<%=request.getContextPath()%>/AnalyseClient"
					method="post">
					<div class="labelTexte">Veuillez choisir le client et
						sélectionner la branche d'analyse puis appuyez sur valider :</div>
					<div class="row">
						<div class="col-sm-2 labelTexte">Client :</div>
						<div class="col-sm-4">
							<input type="text" class="form-control" id="rechercheBox"
								name="nomClient" autocomplete="off" placeholder="rechercher..."
								autofocus onkeyup="loadDoc(this.value)" />
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2 labelTexte">Branche :</div>
						<div class="col-sm-4">
							<select name="branche" class="form-control">
								<option value="Flotte automobile" selected>Flotte
									automobile</option>
								<option value="RC">RC</option>
								<option value="Dommage">Dommage</option>
								<option value="Autres">Autres</option>
							</select>
						</div>

					</div>
					<div class="row">
						<div class="col-sm-4 col-sm-offset-4">
							<button type="submit" class="btn btn-default ajout"
								name="typeAction" value="validerClient">Valider</button>
						</div>
					</div>
				</form>
			</div>
		</c:if>
		<!-- CLIENT -->
		<c:if test="${client != null }">
			<form action="<%=request.getContextPath()%>/AnalyseClient"
				method="post">
				<h2>Client :</h2>
				<div class="clientChoisi">
					<div class="row">
						<div class="col-sm-4 col-sm-offset-3">Nom : ${client.nom}</div>
						<div class="col-sm-2">

							<button type="submit" class="btn btn-default" name="typeAction"
								value="modifierClient">Modifier</button>
						</div>
					</div>
				</div>
			</form>


			<div class="row">
				<form action="<%=request.getContextPath()%>/AnalyseClient"
					id="brancheForm" method="post">
					<input type="hidden" name="nomClient" value="${client.nom}">
					<!-- CONTRAT -->


					<div class="col-sm-4">
						<h2>Contrats de la branche :</h2>
					</div>
					<div class="col-sm-2">
						<select name="branche" class="form-control option"
							onChange="document.getElementById('brancheForm').submit()">
							<c:if test="${branche == 'Flotte automobile' }">
								<option value="Flotte automobile" selected>Flotte
									automobile</option>
							</c:if>
							<c:if test="${branche != 'Flotte automobile' }">
								<option value="Flotte automobile">Flotte automobile</option>
							</c:if>

							<c:if test="${branche == 'RC' }">
								<option value="RC" selected>RC</option>
							</c:if>
							<c:if test="${branche != 'RC' }">
								<option value="RC">RC</option>
							</c:if>

							<c:if test="${branche == 'Dommage' }">
								<option value="Dommage" selected>Dommage</option>
							</c:if>
							<c:if test="${branche != 'Dommage' }">
								<option value="Dommage">Dommage</option>
							</c:if>

							<c:if test="${branche == 'Autres' }">
								<option value="Autres" selected>Autres</option>
							</c:if>
							<c:if test="${branche != 'Autres' }">
								<option value="Autres">Autres</option>
							</c:if>
						</select>
					</div>
				</form>

				<form action="<%=request.getContextPath()%>/AnalyseClient"
					id="analyseForm" method="get">
					<c:if test="${branche == 'Flotte automobile' }">
						<input type="hidden" name="nomClient" value="${client.nom}">
						<input type="hidden" name="branche" value="${branche}">
						<input type="hidden" name="contrat"
							value="${requestScope['contrat']}">
						<div class="col-sm-2 option">
							<div class="labelTexte">Type d'analyse :</div>
						</div>
						<div class="col-sm-4">
							<select name="analyse" class="form-control option"
								onChange="document.getElementById('analyseForm').submit()">
								<c:if test="${analyse == 'generaleU' }">
									<option value="generaleU"
										title="compartimente par années techniques les tableaux à la sortie"
										selected>Générale (par années techniques)</option>
								</c:if>
								<c:if test="${analyse != 'generaleU' }">
									<option value="generaleU"
										title="compartimente par années techniques les tableaux à la sortie">Générale
										(par années techniques)</option>
								</c:if>

								<c:if test="${analyse == 'generaleC' }">
									<option value="generaleC"
										title="un seul tableau, toutes années confondues" selected>Générale
										(toutes années confondues)</option>
								</c:if>
								<c:if test="${analyse != 'generaleC' }">
									<option value="generaleC"
										title="un seul tableau, toutes années confondues">Générale
										(toutes années confondues)</option>
								</c:if>

								<c:if test="${analyse == 'graphique' }">
									<option value="graphique"
										title="alnalyse sous la forme de graphiques" selected>Graphique</option>
								</c:if>
								<c:if test="${analyse != 'graphique' }">
									<option value="graphique"
										title="alnalyse sous la forme de graphiques">Graphique</option>
								</c:if>

							</select>
						</div>
					</c:if>
				</form>
			</div>
			<form action="<%=request.getContextPath()%>/AnalyseClient"
				id="contratForm" method="get">
				<c:if test="${contratList != null }">
					<%@include file="/jspf/contratsListeAnalyseClient.jspf"%>
				</c:if>



				<c:if
					test="${requestScope['contrat'] != null || requestScope['contrat_1'] != null}">

					<input type="hidden" name="nomClient" value="${client.nom}">
					<input type="hidden" name="branche" value="${branche}">
					<input type="hidden" name="analyse" value="${analyse}">
					<!-- PARAMETRE -->
					<h2>Paramètres de l'Analyse :</h2>
					<div class="row">
						<div class="col-sm-6">
							<div id="echeanceAdded">
							<div class="col-sm-8">
								<div class="labelTexte">Date d'échéance :</div>
							</div>		
							<div class="col-sm-4">
								<input type="text" name="dateEcheanceAdded" class="form-control"
									placeholder="jj/mm" autocomplete="off" required disabled>
							</div>
							</div>
							<div class="col-sm-8">
								<div class="labelTexte">Date de départ de l'analyse :</div>
							</div>						
							<div class="col-sm-4">
								<input type="date" name="dateDebut" class="form-control"
									placeholder="jj/mm/aaaa" autocomplete="off" required>
							</div>
							<div class="col-sm-8">
								<div class="labelTexte">Date de fin de l'analyse :</div>
							</div>
							<div class="col-sm-4">
								<input type="date" name="dateFin" class="form-control"
									placeholder="jj/mm/aaaa" autocomplete="off" required>
							</div>
						</div>

						<!-- TABLEAU ANNEES TECHNIQUES -->
						<%@include file="/jspf/tableauAnneesTechniques.jspf"%>
						<div class="col-sm-6 col-sm-offset-3">
							<button type="submit" id="btnLancerAnalyse"
								class="btn btn-default" name="typeAction" value="lancerAnalyse">Lancer
								l'analyse</button>
						</div>
					</div>
				</c:if>
			</form>
		</c:if>



	</div>
	<script type="text/javascript">
		//script de check des dates d'échéances à ajouter ici
		$(document).ready(function() {
			$('#echeanceAdded').addClass('hidden');
			var lastId = null;
			$('input.checkBox').each(function(index) {
				if (this.checked) {
					if (lastId == null) {
						lastId = $(this).attr('id');
					}
					if ($(this).attr('id') != lastId) {
						$('#echeanceAdded').removeClass('hidden');					
					}
					lastId = $(this).attr('id');
				}
				
			})
			$('input[name=dateDebut]').focusout(function(){
				if ($(this).val().length == 10) {
					var values = $(this).val().split("/");
					if (values.length == 1) {
						values = $(this).val().split("-");
					}
					var dateEcheance;
					if (values[0].length == 4) {
						//EN date format
						dateEcheance = values[2] + '/' + values[1];
					}else{
						//FR date format
						dateEcheance = values[0] + '/' + values[1];
					}
					$('input[name=dateEcheanceAdded]').val(dateEcheance);
					$('input[name=dateEcheanceAdded]').attr('value',dateEcheance);
				}else{
					$('input[name=dateEcheanceAdded]').val('');
					$('input[name=dateEcheanceAdded]').attr('value','');
				}
				
			})
			$('#btnLancerAnalyse').click(function(){
				if ($('.hidden').length > 0) {
					$('input[name=dateEcheanceAdded]').val(null);
					$('input[name=dateEcheanceAdded]').attr('value',null);
				}else{
					$('input[name=dateEcheanceAdded]').prop('disabled', false);
				}
				
			})
		});
	</script>
</body>
</html>