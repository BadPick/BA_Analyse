package fr.bellecour.statistiques.servlet.analyse;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.bellecour.statistiques.bo.Client;
import fr.bellecour.statistiques.bo.Contrat;
import fr.bellecour.statistiques.bo.Sinistre;
import fr.bellecour.statistiques.dao.ClientDao;
import fr.bellecour.statistiques.dao.ContratDao;
import fr.bellecour.statistiques.dao.SinistreDao;
import fr.bellecour.statistiques.util.DateHelper;
import fr.bellecour.statistiques.util.ErrorHelper;

/**
 * Servlet implementation class TraitementAnalyseClientFlotteGenerale
 */
@WebServlet("/TraitementAnalyseClientFlotteGenerale")
public class TraitementAnalyseClientFlotteGenerale extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private String message;
	private String detailMessage;
	private Date dateDebut;
	private Date dateFin;
	private String periodeAnalyse;
	private HashMap<String, String> listeAnneesTecn;

	private SimpleDateFormat sdfEn;
	private SimpleDateFormat sdfFr;

	private ArrayList<Sinistre> listeSinistresFlotte;
	private ArrayList<Sinistre> listeSinistresFlotteN3;
	private ArrayList<Sinistre> listeSinistresFlotteN2;
	private ArrayList<Sinistre> listeSinistresFlotteN1;
	private ArrayList<Sinistre> listeSinistresFlotteN;
	private String[] ligneTotalGenerale;
	private String[] ligneTotalGeneraleN3;
	private String[] ligneTotalGeneraleN2;
	private String[] ligneTotalGeneraleN1;
	private String[] ligneTotalGeneraleN;
	private String[] recapFlotte;
	private String codeContratAAnalyser;
	private String firstAnalyse;
	private String periodeN3;
	private String periodeN2;
	private String periodeN1;
	private String periodeN;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TraitementAnalyseClientFlotteGenerale()
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
		String userAgent = request.getHeader("User-Agent");
		javax.servlet.RequestDispatcher rd = null;
		javax.servlet.http.HttpSession session = request.getSession();
		message = null;
		detailMessage = null;
		dateDebut = null;
		dateFin = null;
		periodeAnalyse = null;
		listeAnneesTecn = null;
		sdfEn = null;
		sdfFr = null;
		firstAnalyse = "N3";
		
		// -----------------------flotte général-----------------------
		listeSinistresFlotte = null;
		listeSinistresFlotteN3 = null;
		listeSinistresFlotteN2 = null;
		listeSinistresFlotteN1 = null;
		listeSinistresFlotteN = null;
		ligneTotalGenerale = null;
		ligneTotalGeneraleN3 = null;
		ligneTotalGeneraleN2 = null;
		ligneTotalGeneraleN1 = null;
		ligneTotalGeneraleN = null;
		periodeN3 = null;
		periodeN2 = null;
		periodeN1 = null;
		periodeN = null;
		recapFlotte = null;

		Client clientAAnalyser = null;
		Contrat contratAAnalyser = null;

		// récupération du client à afficher
		if (request.getParameter("nomClient") != null)
		{
			clientAAnalyser = chargerCLient(request);			
		}

		if (request.getParameter("contrat") != null)
		{
			
			try
			{
				// récupération du contrat à analyser
				contratAAnalyser = ContratDao.getById(request.getParameter("contrat"));
				listeAnneesTecn = DateHelper.getAnneesTechniques(contratAAnalyser.getDateEcheance());
				for (Entry<String, String> entry : listeAnneesTecn.entrySet())
				{
					request.setAttribute(entry.getKey(), entry.getValue());
				}
			} 
			catch ( Exception e)
			{
				detailMessage = e.getMessage();
				message = "erreur dans le format de la date d'échéance, ";
				message += ErrorHelper.traitementDuMessageDErreur(e);
				e.printStackTrace();
			}

			// récupération des dates
			try
			{
				sdfFr = new SimpleDateFormat("dd/MM/yyyy");
				// test du navigateur pour adapter le format de date
				if (userAgent.contains("Chrome") || userAgent.contains("Edge"))
				{
					sdfEn = new SimpleDateFormat("yyyy-MM-dd");
					dateDebut = sdfEn.parse(request.getParameter("dateDebut"));
					dateFin = sdfEn.parse(request.getParameter("dateFin"));
				}
				else
				{
					dateDebut = sdfFr.parse(request.getParameter("dateDebut"));
					dateFin = sdfFr.parse(request.getParameter("dateFin"));
				}
				periodeAnalyse = "du " + sdfFr.format(dateDebut) + " au " + sdfFr.format(dateFin);
				

			} catch (Exception e)
			{
				detailMessage = e.getMessage();
				message = "le format de la date de début ou de fin d'analyse est incorecte, ";
				message += ErrorHelper.traitementDuMessageDErreur(e);
				rd = getServletContext().getRequestDispatcher("/jsp/clientAnalyse.jsp");
				e.printStackTrace();
			}

			// récupération des sinistres
			try
			{
				if ("generaleU".equalsIgnoreCase(request.getParameter("analyse")))
				{
					//création des périodes d'analyses
					String dateFinAnalyse = DateHelper.getAnneeTechn(listeAnneesTecn, dateFin);
					periodeN3 = "du " + listeAnneesTecn.get("dateDebutN3") + " au " + listeAnneesTecn.get("dateFinN3");
					periodeN2 = "du " + listeAnneesTecn.get("dateDebutN2") + " au " + listeAnneesTecn.get("dateFinN2");
					periodeN1 = "du " + listeAnneesTecn.get("dateDebutN1") + " au " + listeAnneesTecn.get("dateFinN1");
					periodeN = "du " + listeAnneesTecn.get("dateDebutN") + " au " + listeAnneesTecn.get("dateFinN");
					switch (dateFinAnalyse)
					{
						case "N3":
							periodeN3 = "du " + listeAnneesTecn.get("dateDebutN3") + " au " + sdfFr.format(dateFin);
							break;

						case "N2":
							periodeN2 = "du " + listeAnneesTecn.get("dateDebutN2") + " au " + sdfFr.format(dateFin);
							break;

						case "N1":
							periodeN1 = "du " + listeAnneesTecn.get("dateDebutN1") + " au " + sdfFr.format(dateFin);
							break;

						case "N":
							periodeN = "du " + listeAnneesTecn.get("dateDebutN") + " au " + sdfFr.format(dateFin);
							break;

						default:
							break;
					}
					listeSinistresFlotte = SinistreDao.getSinistresPourAnalyse(contratAAnalyser.getCodeContrat(), dateDebut, dateFin);
					recapFlotte = getRecap(contratAAnalyser, periodeAnalyse, clientAAnalyser);
					repartitionDesSinistresParAnnee(listeSinistresFlotte, listeAnneesTecn);

					if (listeSinistresFlotteN3 == null)
					{
						firstAnalyse = "N2";
					}
					if (listeSinistresFlotteN2 == null)
					{
						firstAnalyse = "N1";
					}
					if (listeSinistresFlotteN1 == null)
					{
						firstAnalyse = "N";
					}

					ligneTotalGeneraleN3 = getTotalGenerale(listeSinistresFlotteN3);
					ligneTotalGeneraleN2 = getTotalGenerale(listeSinistresFlotteN2);
					ligneTotalGeneraleN1 = getTotalGenerale(listeSinistresFlotteN1);
					ligneTotalGeneraleN = getTotalGenerale(listeSinistresFlotteN);

					request.setAttribute("periodeN3", periodeN3);
					request.setAttribute("periodeN2", periodeN2);
					request.setAttribute("periodeN1", periodeN1);
					request.setAttribute("periodeN", periodeN);
					request.setAttribute("ligneTotalGeneraleN3", ligneTotalGeneraleN3);
					request.setAttribute("ligneTotalGeneraleN2", ligneTotalGeneraleN2);
					request.setAttribute("ligneTotalGeneraleN1", ligneTotalGeneraleN1);
					request.setAttribute("ligneTotalGeneraleN", ligneTotalGeneraleN);
					request.setAttribute("analyse", "generaleU");
					request.setAttribute("firstAnalyse", firstAnalyse);
					request.setAttribute("listeSinistresFlotteN3", listeSinistresFlotteN3);
					request.setAttribute("listeSinistresFlotteN2", listeSinistresFlotteN2);
					request.setAttribute("listeSinistresFlotteN1", listeSinistresFlotteN1);
					request.setAttribute("listeSinistresFlotteN", listeSinistresFlotteN);
				}
				else if ("generaleC".equalsIgnoreCase(request.getParameter("analyse")))
				{
					listeSinistresFlotte = SinistreDao
							.getSinistresPourAnalyse(contratAAnalyser.getCodeContrat(), dateDebut, dateFin);
					recapFlotte = getRecap(contratAAnalyser, periodeAnalyse, clientAAnalyser);
					ligneTotalGenerale = getTotalGenerale(listeSinistresFlotte);
					request.setAttribute("ligneTotalGenerale", ligneTotalGenerale);
					request.setAttribute("analyse", "generaleC");
					request.setAttribute("listeSinistresFlotte", listeSinistresFlotte);

				}
			} catch (Exception e)
			{
				detailMessage = e.getMessage();
				message = "un problème est survenue pendant le chargement de la liste des sinistres, ";
				message += ErrorHelper.traitementDuMessageDErreur(e);
				rd = getServletContext().getRequestDispatcher("/jsp/clientAnalyse.jsp");
				e.printStackTrace();
			}

			// traitement de l'analyse generale
			request.setAttribute("recapFlotte", recapFlotte);
			rd = getServletContext().getRequestDispatcher("/jsp/clientResultatAnalyse.jsp");

		}
		else
		{
			message = "Aucun contrat selectionné pour l'analyse, ";
			rd = getServletContext().getRequestDispatcher("/jsp/clientAnalyse.jsp");
		}

		request.setAttribute("clientAAnalyser", clientAAnalyser);
		request.setAttribute("detailMessage", detailMessage);
		request.setAttribute("message", message);
		request.setAttribute("clientAAnalyser", clientAAnalyser);
		rd.forward(request, response);
	}

	private void repartitionDesSinistresParAnnee(ArrayList<Sinistre> listeSinistres,
			HashMap<String, String> listeAnneesTecn) throws ParseException
	{
		for (Sinistre sinistre : listeSinistres)
		{

			if ("N3".equalsIgnoreCase(sinistre.getAnneeTechn(listeAnneesTecn)))
			{
				if (listeSinistresFlotteN3 == null)
				{
					listeSinistresFlotteN3 = new ArrayList<Sinistre>();
				}
				listeSinistresFlotteN3.add(sinistre);
			}
			else if ("N2".equalsIgnoreCase(sinistre.getAnneeTechn(listeAnneesTecn)))
			{

				if (listeSinistresFlotteN2 == null)
				{
					listeSinistresFlotteN2 = new ArrayList<Sinistre>();
				}
				listeSinistresFlotteN2.add(sinistre);
			}
			else if ("N1".equalsIgnoreCase(sinistre.getAnneeTechn(listeAnneesTecn)))
			{

				if (listeSinistresFlotteN1 == null)
				{
					listeSinistresFlotteN1 = new ArrayList<Sinistre>();
				}
				listeSinistresFlotteN1.add(sinistre);
			}
			else if ("N".equalsIgnoreCase(sinistre.getAnneeTechn(listeAnneesTecn)))
			{

				if (listeSinistresFlotteN == null)
				{
					listeSinistresFlotteN = new ArrayList<Sinistre>();
				}
				listeSinistresFlotteN.add(sinistre);
			}
		}

	}

	private String[] getTotalGenerale(ArrayList<Sinistre> listeSinistresFlotte)
	{
		String[] ligneTotal = null;
		if (listeSinistresFlotte != null)
		{
			ligneTotal = new String[16];
			double total1 = 0;
			double total2 = 0;
			double total3 = 0;
			double total4 = 0;
			double total5 = 0;
			double total6 = 0;
			double total7 = 0;
			double total8 = 0;
			double total9 = 0;
			double total10 = 0;
			double total11 = 0;
			double total12 = 0;
			double total13 = 0;
			double total15 = 0;
			for (Sinistre s : listeSinistresFlotte)
			{
				total1 += s.getTiersReglementMat();
				total2 += s.getTiersReglementCorp();
				total3 += s.getTiersReglementMatCorp();
				total4 += s.getTiersProvisions();
				total5 += s.getTiersRecours();
				total6 += s.getTiersCout();
				total7 += s.getAssureReglementMat();
				total8 += s.getAssureReglementCorp();
				total9 += s.getAssureReglementMatCorp();
				total10 += s.getAssureProvisions();
				total11 += s.getAssureFranchise();
				total12 += s.getAssureCout();
				total13 += s.getCoutSinistre();
				if (s.getTauxResponsabilite()==100) {
					total15++;
				}

			}
			ligneTotal[0] = "TOTAL";
			ligneTotal[1] = doubleFormateurArrondi(total1);
			ligneTotal[2] = doubleFormateurArrondi(total2);
			ligneTotal[3] = doubleFormateurArrondi(total3);
			ligneTotal[4] = doubleFormateurArrondi(total4);
			ligneTotal[5] = doubleFormateurArrondi(total5);
			ligneTotal[6] = doubleFormateurArrondi(total6);
			ligneTotal[7] = doubleFormateurArrondi(total7);
			ligneTotal[8] = doubleFormateurArrondi(total8);
			ligneTotal[9] = doubleFormateurArrondi(total9);
			ligneTotal[10] = doubleFormateurArrondi(total10);
			ligneTotal[11] = doubleFormateurArrondi(total11);
			ligneTotal[12] = doubleFormateurArrondi(total12);
			ligneTotal[13] = doubleFormateurArrondi(total13);
			ligneTotal[14] = "";
			ligneTotal[15] = doubleFormateurArrondi(total15);
		}

		return ligneTotal;
	}

	private String doubleFormateurArrondi(double monDoubleAParser)
	{
		String resultat = String.valueOf(monDoubleAParser);

		resultat = resultat.replace(".", ",");
		String[] tab = resultat.split(",");

		return tab[0];
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
				e.printStackTrace();
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

	private String[] getRecap(Contrat contrat, String periodeAnalyse, Client clientAAnalyser) throws ParseException
	{
		String[] recap = new String[5];
		// nom client
		recap[0] = clientAAnalyser.getNom();

		recap[1] = contrat.getAssureur() + " - n°" + contrat.getCodeContrat();

		// date effet
		recap[2] = contrat.getDateEffetString() + " (échéance : " + contrat.getDateEcheanceString() + ")";

		// période analyse
		recap[3] = periodeAnalyse;

		// branche
		//recap[4] = contrat.getBrancheProduit() + " (" + contrat.getLibelleProduit() + ")";
		recap[4] = contrat.getLibelleProduit();
		
		return recap;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{
		// TODO Auto-generated method stub
	}

}
