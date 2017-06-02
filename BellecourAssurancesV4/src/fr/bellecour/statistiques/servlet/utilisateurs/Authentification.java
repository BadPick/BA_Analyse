package fr.bellecour.statistiques.servlet.utilisateurs;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.bellecour.statistiques.bo.Utilisateur;
import fr.bellecour.statistiques.dao.UtilisateurDao;
import fr.bellecour.statistiques.util.ErrorHelper;

/**
 * Servlet implementation class Authentification
 */
/**
 * @author mrault
 *
 */
@WebServlet(description = "Servlet permettant l'authentification des utilisateurs", urlPatterns =
{ "/Authentification" })
public class Authentification extends HttpServlet
{
	private String message;
	private String detailMessage;
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Authentification()
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

		if (request.getParameter("typeAction") != null)
		{
			switch (request.getParameter("typeAction"))
			{
				case "connexion":
					Utilisateur user;
					try
					{
						user = UtilisateurDao.getUser(request.getParameter("email"),
								request.getParameter("password"));
						if (user != null)
						{
							session.setAttribute("user", user);
							session.setAttribute("level", user.getNiveau().getCodeNiveau());
						}
						else
						{
							message = "combinaison email password inconnue";
						}
					} 
					catch (Exception e)
					{
						detailMessage = e.getMessage();
						message += ErrorHelper.traitementDuMessageDErreur(e);
					}
					
					break;

				case "deconnexion":
					session.invalidate();
					break;

				default:

					break;
			}
		}

		request.setAttribute("detailMessage", detailMessage);
		request.setAttribute("message", message);
		rd = getServletContext().getRequestDispatcher("/index.jsp");
		rd.forward(request, response);
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
