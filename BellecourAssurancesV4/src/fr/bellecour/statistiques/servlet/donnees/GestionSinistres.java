package fr.bellecour.statistiques.servlet.donnees;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import fr.bellecour.statistiques.bo.Circonstance;
import fr.bellecour.statistiques.bo.Contrat;
import fr.bellecour.statistiques.bo.Nature;
import fr.bellecour.statistiques.bo.Sinistre;
import fr.bellecour.statistiques.dao.ClientDao;
import fr.bellecour.statistiques.dao.ContratDao;
import fr.bellecour.statistiques.dao.ProduitDao;
import fr.bellecour.statistiques.dao.SinistreDao;
import fr.bellecour.statistiques.util.ErrorHelper;

/**
 * Servlet implementation class GestionSinistres
 */
@WebServlet("/GestionSinistres")
public class GestionSinistres extends HttpServlet
{
	private String message;
	private String detailMessage;
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GestionSinistres()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//déclaration des variables
		javax.servlet.RequestDispatcher rd = null;
		javax.servlet.http.HttpSession session = request.getSession();
		message = null;
		detailMessage = null;
		String affichage = "";
		Contrat contratAAfficher = null;
		Sinistre sinistreAAfficher = null;
		ArrayList<Sinistre> sinistreList = null;
		
		if (request.getParameter("typeAction")!=null)
		{
			switch (request.getParameter("typeAction"))
			{
				//affiche la page par défaut de la gestion des sinistres
				case "gererSinistres":
					rd = getServletContext().getRequestDispatcher("/jsp/sinistresGestion.jsp");
					affichage = "liste";
					break;
				
				case "modifierContrat":
					rd = getServletContext().getRequestDispatcher("/jsp/sinistresGestion.jsp");
					affichage = "liste";
					break;
					
				case "validerSinistre":				
					affichage = "formulaire";
					if (request.getParameter("codeSinistre")!=null)
					{
						try
						{
							sinistreAAfficher = SinistreDao.getById(request.getParameter("codeSinistre"));
							if (sinistreAAfficher==null)
							{
								message ="ce sinistre n'existe pas dans la base de données, ";
							}
							else 
							{
								request.setAttribute("sinistreAAfficher", sinistreAAfficher);
							}
						} 
						catch (Exception e)
						{
							detailMessage = e.getMessage();
							message = "echec du chargement du sinistre, ";
							message += ErrorHelper.traitementDuMessageDErreur(e);
						}
					}
					rd = getServletContext().getRequestDispatcher("/jsp/sinistresGestion.jsp");
					break;
				
				case "validerContrat":
					affichage = "liste";
					if (request.getParameter("codeContrat")!=null)
					{
						try
						{
							contratAAfficher = ContratDao.getById(request.getParameter("codeContrat"));
							if (contratAAfficher==null)
							{
								message ="ce contrat n'existe pas dans la base de données, ";
							}
							else 
							{
								sinistreList = SinistreDao.getAllByContrat(contratAAfficher.getCodeContrat());
								request.setAttribute("contratAAfficher", contratAAfficher);
								request.setAttribute("sinistreList", sinistreList);
							}
						} 
						catch (Exception e)
						{
							detailMessage = e.getMessage();
							message = "echec du chargement du contrat, ";
							message += ErrorHelper.traitementDuMessageDErreur(e);
						}
					}
					rd = getServletContext().getRequestDispatcher("/jsp/sinistresGestion.jsp");					
					break;
				
				case "majSinistres":
					
					if (request.getPart("file")!=null)
					{						
						try
						{
							Part filePart = request.getPart("file");
							if (filePart.getSize()>0)
							{
								String fileName = getSubmittedFileName(filePart);
								InputStream fileContent = filePart.getInputStream();
								
								String messageRetour = SinistreDao.majSinistres(fileContent);
								message = "succès de la mise à jour des sinistres" + messageRetour;
							}
							else 
							{
								message = "aucun fichier sélectionné pour la mise à jour";
							}

							
						}
						catch (Exception e)
						{
							detailMessage =  e.getMessage();
							message = "echec de la mise à jour des sinistres, ";
							message += ErrorHelper.traitementDuMessageDErreur(e);
						}
					}
					else
					{
						message = "impossible d'accéder au fichier de mise à jour";
					}
					
					rd = getServletContext().getRequestDispatcher("/jsp/donneesGestion.jsp");
					break;	
					
				//persiste le sinistre modifié puis réaffiche la liste des sinistres
				case "validerModif":
					// update
					if (request.getParameter("codeSinistre") != null)
					{
						boolean updateOk;
						try
						{
							sinistreAAfficher = recuperationSinistre(request);
							updateOk = SinistreDao.update(sinistreAAfficher);
							if (updateOk)
							{
								message = "succès de la modification du sinistre";
							}
							else
							{
								message = "echec de la modification du sinistre, ";
							}
							rd = getServletContext().getRequestDispatcher("/jsp/sinistresGestion.jsp");
						} 
						catch (Exception e)
						{
							detailMessage = e.getMessage();
							message = "echec de la modification du sinistre, ";
							message += ErrorHelper.traitementDuMessageDErreur(e);
						}

					}
					affichage = "liste";					
					
					break;							

			}
		}
		
		request.setAttribute("affichage", affichage);
		request.setAttribute("message", message);
		request.setAttribute("detailMessage", detailMessage);
		rd.forward(request, response);
	}

	private Sinistre recuperationSinistre(HttpServletRequest request) throws Exception
	{
		Sinistre sinistre = new Sinistre();
		
		sinistre.setAssureFranchise(request.getParameter("assureFranchise"));
		sinistre.setAssureProvisions(request.getParameter("assureProvisions"));
		sinistre.setAssureReglementCorp(request.getParameter("assureReglementCorp"));
		sinistre.setAssureReglementMat(request.getParameter("assureReglementMat"));
		sinistre.setCodeContrat(request.getParameter("codeContrat"));
		sinistre.setCodeSinistre(request.getParameter("codeSinistre"));
		sinistre.setDateSurvenance(request.getParameter("dateSurvenance"));
		sinistre.setDonneurOrdre(request.getParameter("donneurOrdre"));
		sinistre.setEtat(request.getParameter("etat"));
		sinistre.setHeureSurvenance(request.getParameter("heureSurvenance"));
		sinistre.setImmatriculation(request.getParameter("immatriculation"));
		sinistre.setLieu(request.getParameter("lieu"));
		sinistre.setNomSalarie(request.getParameter("nomSalarie"));
		sinistre.setNomTiers(request.getParameter("nomTiers"));
		sinistre.setObservation(request.getParameter("observation"));
		sinistre.setPoids(request.getParameter("poids"));
		sinistre.setRefCompagnie(request.getParameter("refCompagnie"));
		sinistre.setTauxResponsabilite(request.getParameter("tauxResponsabilite"));
		sinistre.setTiersProvisions(request.getParameter("tiersProvisions"));
		sinistre.setTiersRecours(request.getParameter("tiersRecours"));
		sinistre.setTiersReglementCorp(request.getParameter("tiersReglementCorp"));
		sinistre.setTiersReglementMat(request.getParameter("tiersReglementMat"));
		sinistre.setTypeDommage(request.getParameter("typeDommage"));
		sinistre.setNatureDommage(request.getParameter("natureDommage"));
		
		Circonstance c = new Circonstance();
		c.setCodeCirconstance(request.getParameter("codeCirconstance"));
		
		Nature n = new Nature();
		n.setCodeNature(request.getParameter("codeNature"));
		
		sinistre.setCirconstance(c);
		sinistre.setNature(n);
		
		
		return sinistre;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{
		doGet(request, response);
	}
	
	/**
	 * méthode permettant de récupérer le nom du fichier selectionné
	 * @param part
	 * @return String
	 */
	private static String getSubmittedFileName(Part part)
	{
		for (String cd : part.getHeader("content-disposition").split(";"))
		{
			if (cd.trim().startsWith("filename"))
			{
				String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1); // MSIE
																													// fix.
			}
		}
		return null;
	}

}
