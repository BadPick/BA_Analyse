package fr.bellecour.statistiques.servlet.donnees;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import fr.bellecour.statistiques.bo.Produit;
import fr.bellecour.statistiques.dao.ClientDao;
import fr.bellecour.statistiques.dao.ProduitDao;
import fr.bellecour.statistiques.util.ErrorHelper;

/**
 * Servlet implementation class GestionDonnees
 */
@WebServlet("/GestionDonnees")
@MultipartConfig
public class GestionDonnees extends HttpServlet
{
	private String message;
	private String detailMessage;
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GestionDonnees()
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
		message = null;
		detailMessage = null;

		if (request.getParameter("typeAction") != null)
		{
			switch (request.getParameter("typeAction"))
			{
				case "affichage":
					rd = getServletContext().getRequestDispatcher("/jsp/donneesGestion.jsp");
					break;

				case "gererPortefeuilleClients":
					rd = getServletContext().getRequestDispatcher("/GestionPortefeuilleClients");
					break;			
				
				case "majPortefeuilleClients":
					rd = getServletContext().getRequestDispatcher("/GestionPortefeuilleClients");
					break;
					
				case "gererSinistres":
					rd = getServletContext().getRequestDispatcher("/GestionSinistres");
					break;

				case "majSinistres":
					rd = getServletContext().getRequestDispatcher("/GestionSinistres");
					break;

				case "gererProduits":
					rd = getServletContext().getRequestDispatcher("/GestionProduits");
					break;
				
				case "gererCirconstances":
					rd = getServletContext().getRequestDispatcher("/GestionCirconstances");
					break;
					
				case "gererNatures":
					rd = getServletContext().getRequestDispatcher("/GestionNatures");
					break;

				default:
					rd = getServletContext().getRequestDispatcher("/jsp/donneesGestion.jsp");
					break;
			}
		}
		
		request.setAttribute("detailMessage", detailMessage);
		request.setAttribute("message", message);
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
