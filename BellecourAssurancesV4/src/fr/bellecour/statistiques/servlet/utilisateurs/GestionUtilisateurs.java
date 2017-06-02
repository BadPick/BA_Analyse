package fr.bellecour.statistiques.servlet.utilisateurs;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.bellecour.statistiques.bo.Niveau;
import fr.bellecour.statistiques.bo.Utilisateur;
import fr.bellecour.statistiques.dao.UtilisateurDao;
import fr.bellecour.statistiques.util.ErrorHelper;

/**
 * Servlet implementation class GestionUtilisateurs
 */
@WebServlet("/GestionUtilisateurs")
public class GestionUtilisateurs extends HttpServlet
{
	private String message;
	private String detailMessage;
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GestionUtilisateurs()
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
		javax.servlet.RequestDispatcher rd = null;
		javax.servlet.http.HttpSession session = request.getSession();
		message = null;
		detailMessage = null;
		String affichage = "";
		Utilisateur userAModifier = new Utilisateur();
		Utilisateur userNouveau = null;
		ArrayList<Utilisateur> userList = null;
		ArrayList<Niveau> levelList = null;

		if (request.getParameter("typeAction") != null)
		{
			switch (request.getParameter("typeAction"))
			{
				case "affichage":
					affichage = "liste";
					userList = chargementDeLaListeDUtilisateur();
					

					break;

				case "annuler":
					affichage = "liste";
					userList = chargementDeLaListeDUtilisateur();

					break;

				case "ajouter":
					affichage = "formulaire";
					levelList = chargementDeLaListeDesNiveaux();
					break;

				case "modifier":
					affichage = "formulaire";
					levelList = chargementDeLaListeDesNiveaux();
					try
					{
						userAModifier = UtilisateurDao.geUserById(Integer.parseInt(request.getParameter("codeUtilisateur")));
					} 
					catch (Exception e)
					{
						detailMessage = e.getMessage();
						message = "échec du chargement de l'utilisateur à modifier";
						message += ErrorHelper.traitementDuMessageDErreur(e);
					}

					break;

				case "valider":
					// insert
					if (request.getParameter("codeUtilisateur") != null
							&& "0".equals(request.getParameter("codeUtilisateur")))
					{
						
						boolean ajoutOk;
						try
						{
							userNouveau = recuperationUser(request);
							ajoutOk = UtilisateurDao.addUser(userNouveau);
							if (ajoutOk)
							{
								message = "succès de l'ajout utilisateur";
							}
							else
							{
								message = "échec de l'ajout utilisateur";
							}
						} 
						catch (Exception e)
						{
							detailMessage = e.getMessage();
							message = "échec de l'ajout utilisateur";
							message += ErrorHelper.traitementDuMessageDErreur(e);
						}
						
					}
					// update
					if (request.getParameter("codeUtilisateur") != null
							&& !"0".equals(request.getParameter("codeUtilisateur")))
					{
						
						boolean updateOk;
						try
						{
							userAModifier = recuperationUser(request);
							updateOk = UtilisateurDao.updateUser(userAModifier);
							if (updateOk)
							{
								message = "succès de la modification de l'utilisateur";
							}
							else
							{
								message = "échec de la modification de l'utilisateur";
							}
						} 
						catch (Exception e)
						{
							detailMessage = e.getMessage();
							message = "échec de la modification de l'utilisateur";
							message += ErrorHelper.traitementDuMessageDErreur(e);
						}
						
					}
					affichage = "liste";
					userList = chargementDeLaListeDUtilisateur();
					break;

				case "supprimer":
					if (request.getParameter("codeUtilisateur") != null
							&& !"0".equals(request.getParameter("codeUtilisateur")))
					{
						boolean suppressionOk;
						try
						{
							
							suppressionOk = UtilisateurDao.deleteUser(Integer.parseInt(request.getParameter("codeUtilisateur")));
							if (suppressionOk)
							{
								message = "succès de la suppression de l'utilisateur";
							}
							else
							{
								message = "échec de la suppression de l'utilisateur";
							}
						}
						catch (Exception e)
						{
							detailMessage = e.getMessage();
							message = "échec de la suppression de l'utilisateur";
							message += ErrorHelper.traitementDuMessageDErreur(e);
						}
						
					}
					else
					{
						message = "échec de la suppression de l'utilisateur";
					}
					affichage = "liste";
					userList = chargementDeLaListeDUtilisateur();
					break;

				default:

					break;
			}
		}

		request.setAttribute("userAModifier", userAModifier);
		request.setAttribute("levelList", levelList);
		request.setAttribute("userList", userList);
		request.setAttribute("affichage", affichage);
		request.setAttribute("message", message);
		rd = getServletContext().getRequestDispatcher("/jsp/utilisateurGestion.jsp");
		rd.forward(request, response);
	}

	/**
	 * méthode permettant de récupérer la liste des niveaux
	 * @return ArrayList<Niveau>
	 */
	private ArrayList<Niveau> chargementDeLaListeDesNiveaux()
	{
		ArrayList<Niveau> liste = null;
		try
		{
			liste = UtilisateurDao.getNiveaux();
		} 
		catch (Exception e)
		{
			detailMessage = e.getMessage();
			message = "échec du chargement de la liste des niveaux";
			message += ErrorHelper.traitementDuMessageDErreur(e);
		}
		return liste;
	}

	/**
	 * méthode permettant de récupérer la liste des utilisateurs
	 * @return ArrayList<Utilisateur>
	 */
	private ArrayList<Utilisateur> chargementDeLaListeDUtilisateur()
	{
		ArrayList<Utilisateur> liste = null;
		try
		{
			liste = UtilisateurDao.getAllUser();
		} 
		catch (Exception e)
		{
			detailMessage = e.getMessage();
			message = "échec du chargement de la liste des utilisateurs";
			message += ErrorHelper.traitementDuMessageDErreur(e);
		}
		return liste;
	}

	/**
	 * méthode permettant de construire un utilisateur à partir de la request
	 * @param request
	 * @return Utilisateur
	 */
	private Utilisateur recuperationUser(HttpServletRequest request)
	{
		Utilisateur user = new Utilisateur();

		user.setCodeUtilisateur(Integer.parseInt(request.getParameter("codeUtilisateur")));
		user.setNom(request.getParameter("nom"));
		user.setEmail(request.getParameter("email"));
		user.setPassword(request.getParameter("password"));
		user.setNiveau(new Niveau(Integer.parseInt(request.getParameter("codeNiveau")), ""));

		return user;
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

}
