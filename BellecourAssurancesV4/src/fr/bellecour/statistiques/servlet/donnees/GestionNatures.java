package fr.bellecour.statistiques.servlet.donnees;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.bellecour.statistiques.bo.Circonstance;
import fr.bellecour.statistiques.bo.Nature;
import fr.bellecour.statistiques.dao.CirconstanceDao;
import fr.bellecour.statistiques.dao.NatureDao;
import fr.bellecour.statistiques.util.ErrorHelper;

/**
 * Servlet implementation class GestionNatures
 */
@WebServlet("/GestionNatures")
public class GestionNatures extends HttpServlet 
{
	private String message;
	private String detailMessage;
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GestionNatures() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		javax.servlet.RequestDispatcher rd = null;
		javax.servlet.http.HttpSession session = request.getSession();
		Nature natureAModifier = null;
		Nature nouvelleNature = null;
		String affichage = "";
		message = null;
		detailMessage = null;
		ArrayList<Nature> listeNatures = null;
		

		if (request.getParameter("typeAction") != null)
		{
			switch (request.getParameter("typeAction"))
			{
				case "visualiser":
					affichage = "nomenclature";
					try
					{
						
						listeNatures = NatureDao.getAllUtil("");
					}
					catch (Exception e1)
					{
						detailMessage = e1.getMessage();
						message = "erreur pendant le chargement de la liste des natures, ";
						message += ErrorHelper.traitementDuMessageDErreur(e1);
					}
					request.setAttribute("listeNatures", listeNatures);
					request.setAttribute("affichage", affichage);					
					break;
			
				case "gererNatures":
					affichage = "liste";
					listeNatures = chargementDeLaListe();
					request.setAttribute("listeNatures", listeNatures);
					request.setAttribute("affichage", affichage);					
					break;
					
				case "validerAjout":
					// insert
					try
					{
						nouvelleNature = recuperationNature(request);
						boolean ajoutOk = NatureDao.add(nouvelleNature);
						if (ajoutOk)
						{
							message = "succès de l'ajout de la nature";
						}
						else
						{
							message = "echec de l'ajout de la nature, ";
						}
					} 
					catch (Exception e)
					{
						detailMessage = e.getMessage();
						message = "echec de l'ajout de la nature, ";
						message += ErrorHelper.traitementDuMessageDErreur(e);
					}

					affichage = "liste";
					listeNatures = chargementDeLaListe();

					break;

				case "validerModif":
					// update
					if (request.getParameter("codeNature") != null)
					{
						boolean updateOk;
						try
						{
							natureAModifier = recuperationNature(request);
							updateOk = NatureDao.update(natureAModifier);
							if (updateOk)
							{
								message = "succès de la modification de la nature";
							}
							else
							{
								message = "echec de la modification de la nature, ";
							}
						} catch (Exception e)
						{
							detailMessage = e.getMessage();
							message = "echec de la modification de la nature, ";
							message += ErrorHelper.traitementDuMessageDErreur(e);
						}

					}
					affichage = "liste";
					listeNatures = chargementDeLaListe();					

					break;

				case "ajouter":
					affichage = "formulaire";

					break;

				case "modifier":
					affichage = "formulaire";
					try
					{
						natureAModifier = NatureDao.getById(request.getParameter("codeNature"));
					} 
					catch (Exception e)
					{
						detailMessage = e.getMessage();
						message = "echec du chargement de la nature à modifier, ";
						message += ErrorHelper.traitementDuMessageDErreur(e);
					}

					break;

				case "supprimer":
					if (request.getParameter("codeNature") != null)
					{
						boolean suppressionOk;
						try
						{
							suppressionOk = NatureDao.delete(request.getParameter("codeNature"));
							if (suppressionOk)
							{
								message = "succès de la suppression de la nature";
							}
							else
							{
								message = "echec de la suppression de la nature, ";
							}
						} 
						catch (Exception e)
						{
							detailMessage = e.getMessage();
							message = "echec de la suppression de la nature, ";
							message += ErrorHelper.traitementDuMessageDErreur(e);
						}
						
					}
					else
					{
						message = "echec de la suppression de la nature, ";
					}
					affichage = "liste";
					listeNatures = chargementDeLaListe();
					break;

				case "annuler":
					affichage = "liste";
					listeNatures = chargementDeLaListe();
					break;

				default:

					break;
			}
		}

		request.setAttribute("affichage", affichage);
		request.setAttribute("natureAModifier", natureAModifier);
		request.setAttribute("listeNatures", listeNatures);
		request.setAttribute("message", message);
		request.setAttribute("detailMessage", detailMessage);
		rd = getServletContext().getRequestDispatcher("/jsp/naturesGestion.jsp");
		rd.forward(request, response);
	}

	/**
	 * méthode permettant de récupérer la liste des natures
	 * @return ArrayList<Nature>
	 */
	private ArrayList<Nature> chargementDeLaListe()
	{
		ArrayList<Nature> liste = null;
		try
		{
			liste = NatureDao.getAll();
		} 
		catch (Exception e)
		{
			detailMessage = e.getMessage();
			message = "echec du chargement de la liste des natures, ";
			message += ErrorHelper.traitementDuMessageDErreur(e);
		}
		return liste;
	}
	
	/**
	 * méthode permettant de construire une Nature à partir de la request
	 * @param request
	 * @return Nature
	 */
	private Nature recuperationNature(HttpServletRequest request)
	{
		Nature nature = new Nature();

		nature.setCodeNature(request.getParameter("codeNature"));
		nature.setLibelle(request.getParameter("libelle"));
		nature.setCodeAnalyse(request.getParameter("codeAnalyse"));
		nature.setBranche(request.getParameter("branche"));

		return nature;
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
