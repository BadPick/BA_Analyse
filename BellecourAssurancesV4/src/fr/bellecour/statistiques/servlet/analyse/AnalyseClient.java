package fr.bellecour.statistiques.servlet.analyse;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.annotation.security.RolesAllowed;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jasper.tagplugins.jstl.core.ForEach;

import fr.bellecour.statistiques.bo.Client;
import fr.bellecour.statistiques.bo.Contrat;
import fr.bellecour.statistiques.bo.Sinistre;
import fr.bellecour.statistiques.dao.ClientDao;
import fr.bellecour.statistiques.dao.ContratDao;
import fr.bellecour.statistiques.dao.SinistreDao;
import fr.bellecour.statistiques.util.DateHelper;
import fr.bellecour.statistiques.util.ErrorHelper;

/**
 * Servlet implementation class AnalyseClient
 */
@WebServlet("/AnalyseClient")
public class AnalyseClient extends HttpServlet
{
	private String message;
	private String detailMessage;
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AnalyseClient()
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
		String codeContrat = null;
		HashMap<String, String> listeAnneesTecn = null;
		String branche = null;
		String analyse = null;
		String typeAction = null;
		HashMap<String, String> listeCodeContratAAnalyser = null;
		
		if (request.getParameter("typeAction") == null && request.getParameter("nomClient")!=null)
		{
			typeAction = "validerClient";
		}
		else 
		{
			typeAction = request.getParameter("typeAction");
		}
		
		if (typeAction != null)
		{
			switch (typeAction)
			{
				case "affichage":
					rd = getServletContext().getRequestDispatcher("/jsp/clientAnalyse.jsp");
					break;	
					
				case "modifierClient":
					clientAAnalyser = null;
					rd = getServletContext().getRequestDispatcher("/jsp/clientAnalyse.jsp");
					break;
				
				case "lancerAnalyse":	
					branche = request.getParameter("branche");
					if ("flotte automobile".equalsIgnoreCase(branche))
					{
						if ("generaleU".equalsIgnoreCase(request.getParameter("analyse"))||"generaleC".equalsIgnoreCase(request.getParameter("analyse")))
						{
							rd = getServletContext().getRequestDispatcher("/TraitementAnalyseClientFlotteGenerale");
						}						
						if ("graphique".equalsIgnoreCase(request.getParameter("analyse")))
						{
							rd = getServletContext().getRequestDispatcher("/TraitementAnalyseClientFlotteGraphique");
						}
						
					}
					else if ("RC".equalsIgnoreCase(branche))
					{
						rd = getServletContext().getRequestDispatcher("/TraitementAnalyseClientRC");
					}
					else if ("dommage".equalsIgnoreCase(branche))
					{
						rd = getServletContext().getRequestDispatcher("/TraitementAnalyseClientDommage");
					}
					else if ("autres".equalsIgnoreCase(branche))
					{
						rd = getServletContext().getRequestDispatcher("/TraitementAnalyseClientAutres");
					}
					
					break;
					
				case "validerClient":
					clientAAnalyser = chargerCLient(request);
					
					if (request.getParameter("branche")!=null)
					{
						branche = request.getParameter("branche");
					}
					else 
					{
						branche = "Flotte automobile";
					}
					if (request.getParameter("analyse")==null)
					{
						analyse = "generaleU";
					}
					else 
					{
						analyse = request.getParameter("analyse");
					}
					
					if (clientAAnalyser==null)
					{
						message ="ce client n'existe pas dans la base de données, ";
					}	
					else
					{	
						// récupération des listes de contrats à analyser
						listeCodeContratAAnalyser = recuperationContratsAAnalyser(request);
						
						if (request.getParameter("contrat") != null && !(request.getParameter("contrat")).isEmpty())
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
						else if (listeCodeContratAAnalyser.size() > 0)
						{
							String codeContratPourAnneesTechn = null;
							try
							{
								int i = 1;
								for (Entry<String, String> entry : listeCodeContratAAnalyser.entrySet())
								{
									String contrat = "contrat_" + i;
									request.setAttribute(contrat,entry.getKey());
									codeContratPourAnneesTechn = entry.getKey();
									i++;
								}
								contratAAnalyser=ContratDao.getById(codeContratPourAnneesTechn);								
							} 
							catch (Exception e)
							{
								detailMessage = e.getMessage();
								message = "erreur pendant la récupération des contrats, ";
								message += ErrorHelper.traitementDuMessageDErreur(e);
							}
							
							try
							{
								listeAnneesTecn = DateHelper.getAnneesTechniques(contratAAnalyser.getDateEcheance());
								for (Entry<String, String> entry : listeAnneesTecn.entrySet())
								{
									request.setAttribute(entry.getKey(), entry.getValue());
								}
							} 
							catch (Exception e)
							{
								detailMessage = e.getMessage();
								message = "erreur dans le format de la date d'échéance, ";
								message += ErrorHelper.traitementDuMessageDErreur(e);
							}
							
						}
					}
					
					rd = getServletContext().getRequestDispatcher("/jsp/clientAnalyse.jsp");
					break;
					
				default:
					rd = getServletContext().getRequestDispatcher("/jsp/clientAnalyse.jsp");
					break;
			}
		}
		request.setAttribute("analyse", analyse);
		request.setAttribute("branche", branche);
		request.setAttribute("clientAAnalyser", clientAAnalyser);
		request.setAttribute("detailMessage", detailMessage);
		request.setAttribute("message", message);
		rd.forward(request, response);
	}

	/**
	 * méthode permettant de récupérer le client demandé dans la request
	 * @param request
	 * @return Client
	 */
	private Client chargerCLient(HttpServletRequest request)
	{
		Client clientAAnalyser = null;
		if (request.getParameter("nomClient")!=null)
		{
			try
			{
				clientAAnalyser = ClientDao.getByNameWithContrats(request.getParameter("nomClient"));			
			} 
			catch (Exception e)
			{
				detailMessage = e.getMessage();
				message = "echec du chargement du client, ";
				message += ErrorHelper.traitementDuMessageDErreur(e);
			}
		}
		return clientAAnalyser;
	}

	/**
	 * méthode permettant de récupérer dans la request les numéros de contrats à
	 * analyser
	 * 
	 * @param request
	 * @return HashMap<String, String>
	 */
	private HashMap<String, String> recuperationContratsAAnalyser(HttpServletRequest request)
	{
		HashMap<String, String> listeCodeContratAAnalyser = new HashMap<String, String>();
		ArrayList<String> listeParametres = new ArrayList<String>();

		// liste des paramètres reçus
		for (Entry<String, String[]> entry : request.getParameterMap().entrySet())
		{
			listeParametres.add(entry.getKey());
		}

		// tri des paramètres à la recherche de contrats à analyser
		for (String param : listeParametres)
		{
			if (param.contains("contrat_"))
			{
				String codeContrat = param.replace("contrat_", "");
				listeCodeContratAAnalyser.put(codeContrat, request.getParameter(param));
			}
		}

		return listeCodeContratAAnalyser;
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
