package fr.bellecour.statistiques.servlet.analyse;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.bellecour.statistiques.bo.Client;
import fr.bellecour.statistiques.bo.Contrat;
import fr.bellecour.statistiques.dao.ClientDao;
import fr.bellecour.statistiques.dao.ContratDao;
import fr.bellecour.statistiques.util.DateHelper;
import fr.bellecour.statistiques.util.ErrorHelper;

/**
 * Servlet implementation class AnalyseAssureur
 */
@WebServlet("/AnalyseAssureur")
public class AnalyseAssureur extends HttpServlet
{
	private String message;
	private String detailMessage;
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AnalyseAssureur()
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
		Client clientAAnalyser = null;
		Contrat contratAAnalyser = null;
		HashMap<String, String> listeAnneesTecn = null;
		String codeContrat = null;
		String typeAction = null;

		if (request.getParameter("typeAction") == null && request.getParameter("nomClient")!=null)
		{
			typeAction = "validerClient";
		}
		else 
		{
			typeAction = request.getParameter("typeAction");
		}
		if (typeAction!= null)
		{
			switch (typeAction)
			{
				case "affichage":
					rd = getServletContext().getRequestDispatcher("/jsp/assureurAnalyse.jsp");
					break;

				case "modifierClient":
					clientAAnalyser = null;
					rd = getServletContext().getRequestDispatcher("/jsp/assureurAnalyse.jsp");
					break;

				case "lancerAnalyse":
					rd = getServletContext().getRequestDispatcher("/TraitementAnalyseAssureur");
					break;

				case "validerClient":
					clientAAnalyser = chargerCLient(request);
					if (clientAAnalyser == null)
					{
						message = "ce client n'existe pas dans la base de données, ";
					}
					else
					{
						if (request.getParameter("contrat") != null)
						{
							try
							{
								contratAAnalyser=ContratDao.getById(request.getParameter("contrat"));
								listeAnneesTecn = DateHelper.getAnneesTechniques(contratAAnalyser.getDateEcheance());
								request.setAttribute("contrat", contratAAnalyser.getCodeContrat());
							} 
							catch (Exception e)
							{
								detailMessage = e.getMessage();
								message = "erreur dans le format de la date d'échéance, ";
								message += ErrorHelper.traitementDuMessageDErreur(e);
							}
							for (Entry<String, String> entry : listeAnneesTecn.entrySet())
							{
								request.setAttribute(entry.getKey(), entry.getValue());
							}
						}
					}

					rd = getServletContext().getRequestDispatcher("/jsp/assureurAnalyse.jsp");
					break;

				default:
					rd = getServletContext().getRequestDispatcher("/jsp/assureurAnalyse.jsp");
					break;
			}
		}

		request.setAttribute("clientAAnalyser", clientAAnalyser);
		request.setAttribute("detailMessage", detailMessage);
		request.setAttribute("message", message);
		rd.forward(request, response);
	}

	/**
	 * méthode permettant de récupérer le client demandé dans la request
	 * 
	 * @param request
	 * @return Client
	 */
	private Client chargerCLient(HttpServletRequest request)
	{
		Client clientAAnalyser = null;
		if (request.getParameter("nomClient") != null)
		{
			try
			{
				clientAAnalyser = ClientDao.getByNameWithContrats(request.getParameter("nomClient"));
			} catch (Exception e)
			{
				detailMessage = e.getMessage();
				message = "echec du chargement du client, ";
				message += ErrorHelper.traitementDuMessageDErreur(e);
			}
		}
		return clientAAnalyser;
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
