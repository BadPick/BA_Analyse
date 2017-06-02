<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<c:set var="contratAAfficher" value="${requestScope['contratAAfficher']}" scope="page" />


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
				
			<h1 id="titreScreen">Gestion des sinistres</h1>
			
<c:if test="${contratAAfficher == null && requestScope['affichage']== 'liste'}">
				<script> 
 function loadContrat(value) 
    {	  
	  //déclaration
	  var xhttp = new XMLHttpRequest();
	  var url = "http://localhost:8080/BellecourAssurances/RechercheBox?type=contrat&rechercheBox=" + value;

	  xhttp.onreadystatechange = function() {
		  
		//récupération de la liste quand changement dans l'input 
	    if ( xhttp.readyState == 4 && xhttp.status == 200) 
		{
	      var data = xhttp.responseText;
	      
	      var parsed = JSON.parse(data);

	      var items = [];

	      for(var x in parsed)
		  {
	        items.push(parsed[x]);
	      }

	      $( "#rechercheBoxContrat" ).autocomplete({
		      source: items
		    });	      
	    }
	  };
	  xhttp.open("POST", url, true);
	  xhttp.send();	  
	}
 
 function loadSinistre(value) 
 {	  
	  //déclaration
	  var xhttp = new XMLHttpRequest();
	  var url = "http://localhost:8080/BellecourAssurances/RechercheBox?type=sinistre&rechercheBox=" + value;

	  xhttp.onreadystatechange = function() {
		  
		//récupération de la liste quand changement dans l'input 
	    if ( xhttp.readyState == 4 && xhttp.status == 200) 
		{
	      var data = xhttp.responseText;
	      
	      var parsed = JSON.parse(data);

	      var items = [];

	      for(var x in parsed)
		  {
	        items.push(parsed[x]);
	      }

	      $( "#rechercheBoxSinistre" ).autocomplete({
		      source: items
		    });	      
	    }
	  };
	  xhttp.open("POST", url, true);
	  xhttp.send();	  
	}
  
</script>
<h2>Recherche par n° de contrat :</h2>
<div class="recherche">
	<form action="<%= request.getContextPath() %>/GestionSinistres" method="post">
	<div class="labelTexte">Veuillez choisir le contrat puis appuyez sur valider :</div>
		<div class="row">
			<div class="col-sm-2 labelTexte">n° de contrat : </div>
			<div class="col-sm-4">
		    	<input type="text" class="form-control" id="rechercheBoxContrat" name="codeContrat" autocomplete="off" placeholder="rechercher..." autofocus onkeyup="loadContrat(this.value)"/>
			</div>
			<div class="col-sm-2">
		    	<button type="submit" class="btn btn-default" name="typeAction" value="validerContrat">Valider</button>
			</div>
		</div>
	</form>
</div>

<h2>Recherche par n° de sinistre :</h2>
<div class="recherche">
	<form action="<%= request.getContextPath() %>/GestionSinistres" method="post">
	<div class="labelTexte">Veuillez choisir le sinistre puis appuyez sur valider :</div>
		<div class="row">
			<div class="col-sm-2 labelTexte">n° de sinistre : </div>
			<div class="col-sm-4">
		    	<input type="text" class="form-control" id="rechercheBoxSinistre" name="codeSinistre" autocomplete="off" placeholder="rechercher..." autofocus onkeyup="loadSinistre(this.value)"/>
			</div>
			<div class="col-sm-2">
		    	<button type="submit" class="btn btn-default" name="typeAction" value="validerSinistre">Valider</button>
			</div>
		</div>
	</form>
</div>
			</c:if>
			
			<!-- Contrat -->
			<c:if test="${contratAAfficher != null }">
			<form action="<%= request.getContextPath() %>/GestionSinistres"  method="post">
				<h2>Contrat :</h2>
				<div class="clientChoisi">					
					<div class="row">
						<div class="col-sm-4 ">N° de police : ${contratAAfficher.codeContrat}</div>
						<div class="col-sm-4 ">Assureur : ${contratAAfficher.assureur}</div>
						<div class="col-sm-4 ">Libellé : ${contratAAfficher.libelleProduit}</div>						
					</div>
					<div class="row">
						<div class="col-sm-4 ">Date effet : ${contratAAfficher.dateEffetString}</div>
						<div class="col-sm-4 ">Date échéance : ${contratAAfficher.dateEcheance}</div>
						<div class="col-sm-4">							
							<button type="submit" class="btn btn-default" title="modifier le contrat sélectionné" name="typeAction" value="modifierContrat">Modifier</button>					
						</div>
					</div>
				</div>
				</form>
			</c:if>


			<c:if test="${requestScope['affichage']== 'liste' && sinistreList!=null}">
				<%@include file="/jspf/sinistresListe.jspf" %>
			</c:if>
			
			<c:if test="${requestScope['affichage']== 'formulaire'}">
				<%@include file="/jspf/sinistreFormulaire.jspf" %>			
			</c:if>
								
		</div>
	</body>
</html>