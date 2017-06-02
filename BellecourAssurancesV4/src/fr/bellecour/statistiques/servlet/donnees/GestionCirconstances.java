package fr.bellecour.statistiques.servlet.donnees;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.bellecour.statistiques.bo.Circonstance;
import fr.bellecour.statistiques.bo.Produit;
import fr.bellecour.statistiques.dao.CirconstanceDao;
import fr.bellecour.statistiques.dao.ProduitDao;
import fr.bellecour.statistiques.util.ErrorHelper;

/**
 * Servlet implementation class GestionCirconstances
 */
@WebServlet("/GestionCirconstances")
public class GestionCirconstances extends HttpServlet 
{
	private String message;
	private String detailMessage;
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GestionCirconstances() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		javax.servlet.RequestDispatcher rd = null;
		javax.servlet.http.HttpSession session = request.getSession();
		Circonstance circonstanceAModifier = null;
		Circonstance nouvelleCirconstance = null;
		String affichage = "";
		message = null;
		detailMessage = null;
		ArrayList<Circonstance> circonstanceList = null;
		

		if (request.getParameter("typeAction") != null)
		{
			switch (request.getParameter("typeAction"))
			{
				case "gererCirconstances":
					affichage = "liste";
					circonstanceList = chargementDeLaListe();
					request.setAttribute("circonstanceList", circonstanceList);
					request.setAttribute("affichage", affichage);					
					break;
					
				case "validerAjout":
					// insert
					try
					{
						nouvelleCirconstance = recuperationCirconstance(request);
						boolean ajoutOk = CirconstanceDao.add(nouvelleCirconstance);
						if (ajoutOk)
						{
							message = "succès de l'ajout de la circonstance";
						}
						else
						{
							message = "echec de l'ajout de la circonstance, ";
						}
					} 
					catch (Exception e)
					{
						detailMessage = e.getMessage();
						message = "echec de l'ajout de la circonstance, ";
						message += ErrorHelper.traitementDuMessageDErreur(e);
					}

					affichage = "liste";
					circonstanceList = chargementDeLaListe();

					break;

				case "validerModif":
					// update
					if (request.getParameter("codeCirconstance") != null)
					{
						boolean updateOk;
						try
						{
							circonstanceAModifier = recuperationCirconstance(request);
							updateOk = CirconstanceDao.update(circonstanceAModifier);
							if (updateOk)
							{
								message = "succès de la modification de la circonstance";
							}
							else
							{
								message = "echec de la modification de la circonstance, ";
							}
						} catch (Exception e)
						{
							detailMessage = e.getMessage();
							message = "echec de la modification de la circonstance, ";
							message += ErrorHelper.traitementDuMessageDErreur(e);
						}

					}
					affichage = "liste";
					circonstanceList = chargementDeLaListe();					

					break;

				case "ajouter":
					affichage = "formulaire";

					break;

				case "modifier":
					affichage = "formulaire";
					try
					{
						circonstanceAModifier = CirconstanceDao.getById(request.getParameter("codeCirconstance"));
					} 
					catch (Exception e)
					{
						detailMessage = e.getMessage();
						message = "echec du chargement de la circonstance à modifier, ";
						message += ErrorHelper.traitementDuMessageDErreur(e);
					}

					break;

				case "supprimer":
					if (request.getParameter("codeCirconstance") != null)
					{
						boolean suppressionOk;
						try
						{
							suppressionOk = CirconstanceDao.delete(request.getParameter("codeCirconstance"));
							if (suppressionOk)
							{
								message = "succès de la suppression de la circonstance";
							}
							else
							{
								message = "echec de la suppression de la circonstance, ";
							}
						} 
						catch (Exception e)
						{
							detailMessage = e.getMessage();
							message = "echec de la suppression de la circonstance, ";
							message += ErrorHelper.traitementDuMessageDErreur(e);
						}
						
					}
					else
					{
						message = "echec de la suppression de la circonstance, ";
					}
					affichage = "liste";
					circonstanceList = chargementDeLaListe();
					break;

				case "annuler":
					affichage = "liste";
					circonstanceList = chargementDeLaListe();
					break;

				default:

					break;
			}
		}

		request.setAttribute("affichage", affichage);
		request.setAttribute("circonstanceAModifier", circonstanceAModifier);
		request.setAttribute("circonstanceList", circonstanceList);
		request.setAttribute("message", message);
		request.setAttribute("detailMessage", detailMessage);
		rd = getServletContext().getRequestDispatcher("/jsp/circonstancesGestion.jsp");
		rd.forward(request, response);
	}

	/**
	 * méthode permettant de récupérer la liste des circonstances
	 * @return ArrayList<Circonstance>
	 */
	private ArrayList<Circonstance> chargementDeLaListe()
	{
		ArrayList<Circonstance> liste = null;
		try
		{
			liste = CirconstanceDao.getAll();
		} 
		catch (Exception e)
		{
			detailMessage = e.getMessage();
			message = "echec du chargement de la liste des circonstances, ";
			message += ErrorHelper.traitementDuMessageDErreur(e);
		}
		return liste;
	}
	
	/**
	 * méthode permettant de construire une Circonstance à partir de la request
	 * @param request
	 * @return Circonstance
	 */
	private Circonstance recuperationCirconstance(HttpServletRequest request)
	{
		Circonstance circonstance = new Circonstance();

		circonstance.setCodeCirconstance(request.getParameter("codeCirconstance"));
		circonstance.setLibelle(request.getParameter("libelle"));
		circonstance.setIda("oui".equals(request.getParameter("ida")));

		return circonstance;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
