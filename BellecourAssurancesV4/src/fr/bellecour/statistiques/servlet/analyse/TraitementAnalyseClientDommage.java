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
import fr.bellecour.statistiques.bo.Nature;
import fr.bellecour.statistiques.bo.Sinistre;
import fr.bellecour.statistiques.dao.ClientDao;
import fr.bellecour.statistiques.dao.ContratDao;
import fr.bellecour.statistiques.dao.NatureDao;
import fr.bellecour.statistiques.dao.SinistreDao;
import fr.bellecour.statistiques.util.DateHelper;
import fr.bellecour.statistiques.util.ErrorHelper;

/**
 * Servlet implementation class TraitementAnalyseClientDommage
 */
@WebServlet("/TraitementAnalyseClientDommage")
public class TraitementAnalyseClientDommage extends HttpServlet
{
	private String message;
	private String detailMessage;
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TraitementAnalyseClientDommage()
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
		Date dateDebut = null;
		Date dateFin = null;
		String periodeAnalyse = null;
		HashMap<String, String> listeAnneesTecn = null;
		String userAgent = request.getHeader("User-Agent");
		SimpleDateFormat sdfEn = null;
		SimpleDateFormat sdfFr = null;
		ArrayList<Sinistre> listeSinistres = null;
		String[] recap = null;
		Client clientAAnalyser = null;
		Contrat contratAAnalyser = null;
		String[] ligneTotalGenerale = null;
		// nature (garantie impactée)
		ArrayList<String[]> resultatNature = null;
		String jsonNatureN = null;
		String jsonNatureCumul = null;
		String jsonNatureColumn = null;
		ArrayList<Nature> listeNatures = null;
		String branche = null;
		
		branche = request.getParameter("branche");

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
			} catch (Exception e)
			{
				detailMessage = e.getMessage();
				message = "erreur dans le format de la date d'échéance, ";
				message += ErrorHelper.traitementDuMessageDErreur(e);
				e.printStackTrace();
			}
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
			listeSinistres = SinistreDao.getSinistresPourAnalyse(contratAAnalyser.getCodeContrat(), dateDebut, dateFin);
			recap = getRecap(contratAAnalyser, periodeAnalyse, clientAAnalyser);
			ligneTotalGenerale = getTotalGenerale(listeSinistres);

		} catch (Exception e)
		{
			detailMessage = e.getMessage();
			message = "un problème est survenue pendant le chargement de la liste des sinistres, ";
			message += ErrorHelper.traitementDuMessageDErreur(e);
			rd = getServletContext().getRequestDispatcher("/jsp/clientAnalyse.jsp");
			e.printStackTrace();
		}

		// traitelent de l'analyse
		try
		{
			// récupération de la liste des natures
			listeNatures = NatureDao.getAllUtil("dommage");
			request.setAttribute("listeNatures", listeNatures);

			// création des différentes listes de tableaux de string
			// pour
			// alimenter les tableau et graph (1 liste = 1 tableau + 2
			// graph)
			if (listeSinistres != null && listeSinistres.size() > 0)
			{
				// analyse des natures de sinistres
				resultatNature = analyseNature(listeSinistres, listeAnneesTecn, dateDebut, dateFin);
				jsonNatureN = getJsonPieChartN(resultatNature, "Natures");
				jsonNatureCumul = getJsonPieChartCumul(resultatNature, "Natures");
				jsonNatureColumn = getJsonColumnChart(resultatNature, "Natures", true);

				request.setAttribute("ligneTotalGenerale", ligneTotalGenerale);
				request.setAttribute("listeSinistres", listeSinistres);
				request.setAttribute("jsonNatureColumn", jsonNatureColumn);
				request.setAttribute("JsonNatureN", jsonNatureN);
				request.setAttribute("JsonNatureCumul", jsonNatureCumul);
				request.setAttribute("resultatNature", resultatNature);
			}
			request.setAttribute("recap", recap);
			rd = getServletContext().getRequestDispatcher("/jsp/clientResultatAnalyseDommage.jsp");
		} catch (Exception e)
		{
			e.printStackTrace();
			detailMessage = e.getMessage();
			message = "un problème est survenue pendant l'analyse des sinistres, ";
			message += ErrorHelper.traitementDuMessageDErreur(e);
			rd = getServletContext().getRequestDispatcher("/jsp/clientAnalyse.jsp");
			e.printStackTrace();
		}

		request.setAttribute("branche", branche);
		request.setAttribute("clientAAnalyser", clientAAnalyser);
		request.setAttribute("detailMessage", detailMessage);
		request.setAttribute("message", message);
		rd.forward(request, response);
	}

	/**
	 * méthode permettant de préparer les données au format attendu par le
	 * columnChart
	 * 
	 * @param resultatNatureFlotte
	 * @param libelle
	 * @return String
	 */
	private String getJsonColumnChart(ArrayList<String[]> resultat, String libelle, boolean cout)
	{
		int i = 0;
		String N3 = "['N-3',";
		String N2 = "['N-2',";
		String N1 = "['N-1',";
		String N = "['N',";
		String json = "[['Année'";
		for (String[] strings : resultat)
		{
			if (!"Total".equalsIgnoreCase(strings[0]) /*
													 * && !"Non renseigné".
													 * equalsIgnoreCase
													 * (strings[0])&&
													 * !"Non renseignée"
													 * .equalsIgnoreCase
													 * (strings[0])
													 */)
			{
				json += ",'" + strings[0] + "'";
			}
		}
		json += "],";

		for (String[] s : resultat)
		{
			if (!"Total".equalsIgnoreCase(s[0]) /*
												 * &&
												 * !"Non renseigné".equalsIgnoreCase
												 * (s[0])&& !"Non renseignée".
												 * equalsIgnoreCase(s[0])
												 */)
			{
				if (!"-".equalsIgnoreCase(s[2]))
				{
					N3 += s[2] + ",";
				}
				else
				{
					N3 += "0,";
				}
				if (!"-".equalsIgnoreCase(s[4]))
				{
					N2 += s[4] + ",";
				}
				else
				{
					N2 += "0,";
				}
				if (!"-".equalsIgnoreCase(s[6]))
				{
					N1 += s[6] + ",";
				}
				else
				{
					N1 += "0,";
				}
				if (!"-".equalsIgnoreCase(s[8]))
				{
					N += s[8] + ",";
				}
				else
				{
					N += "0,";
				}
			}
		}
		N3 += "],";
		N2 += "],";
		N1 += "],";
		N += "]";
		json += N3 + N2 + N1 + N + "]";
		return json;
	}

	/**
	 * méthode permettant de préparer les données au format attendu par le
	 * pieChart représentant l'année N
	 * 
	 * @param resultatNatureFlotte
	 * @param libelle
	 * @return String
	 */
	private String getJsonPieChartN(ArrayList<String[]> resultat, String libelle)
	{
		int i = 0;
		String json = "[['" + libelle + "','nombre']";

		for (String[] s : resultat)
		{
			if (!"Total".equalsIgnoreCase(s[0]))
			{
				json += ",";
				json += "['" + s[0] + "'," + s[7] + "]";
				i++;
			}
		}
		json += "]";

		return json;
	}

	/**
	 * méthode permettant de préparer les données au format attendu par le
	 * pieChart représentant le cumul
	 * 
	 * @param resultatNatureFlotte
	 * @param libelle
	 * @return String
	 */
	private String getJsonPieChartCumul(ArrayList<String[]> resultat, String libelle)
	{
		int i = 0;
		String json = "[['" + libelle + "','nombre']";

		for (String[] s : resultat)
		{
			if (!"Total".equalsIgnoreCase(s[0]))
			{
				json += ",";
				json += "['" + s[0] + "'," + s[11] + "]";
				i++;
			}
		}
		json += "]";

		return json;
	}

	/**
	 * méthode permettant l'analyse de la nature des sinistres
	 * 
	 * @param listeSinistresFlotte
	 * @param listeAnneesTecn
	 * @param dateDebut
	 * @param dateFin
	 * @param contratFlotte
	 * @return ArrayList<String[]>
	 * @throws ParseException
	 */
	private ArrayList<String[]> analyseNature(ArrayList<Sinistre> listeSinistresFlotte,
			HashMap<String, String> listeAnneesTecn, Date dateDebut, Date dateFin) throws ParseException
	{
		ArrayList<String[]> resultatNatureFlotte = new ArrayList<String[]>();
		ArrayList<String> listeNatureBdd = null;
		ArrayList<String> listeNatureSinistres = null;
		try
		{
			listeNatureBdd = NatureDao.getAllCodeAnalyse("dommage");
			listeNatureSinistres = new ArrayList<String>();
			for (Sinistre sinistre : listeSinistresFlotte)
			{	
					if (sinistre.getNature()!=null && sinistre.getNature().getCodeAnalyse() != null
							&& listeNatureBdd.contains(sinistre.getNature().getCodeAnalyse()) && !listeNatureSinistres.contains(sinistre.getNature().getCodeAnalyse()))
					{
						listeNatureSinistres.add(sinistre.getNature().getCodeAnalyse());
					}
			}
		} 
		catch (Exception e)
		{
			detailMessage = e.getMessage();
			message = "une erreur est survenue pendant le chargement de la liste des natures de sinistres, ";
			message += ErrorHelper.traitementDuMessageDErreur(e);
			e.printStackTrace();
		}

		String anneeTechnDepart = DateHelper.getAnneeTechn(listeAnneesTecn, dateDebut);
		String charValeurIgnoree = "-";
		int totalNombreN = 0;
		double totalCoutN = 0;
		int totalNombreN1 = 0;
		double totalCoutN1 = 0;
		int totalNombreN2 = 0;
		double totalCoutN2 = 0;
		int totalNombreN3 = 0;
		double totalCoutN3 = 0;

		// analyse des sinistres par nature de sinistres
		for (String codeAnalyse : listeNatureSinistres)
		{
			int nombreN = 0;
			double coutN = 0;
			int nombreN1 = 0;
			double coutN1 = 0;
			int nombreN2 = 0;
			double coutN2 = 0;
			int nombreN3 = 0;
			double coutN3 = 0;

			for (Sinistre sinistre : listeSinistresFlotte)
			{
				String anneeTechn = DateHelper.getAnneeTechn(listeAnneesTecn, sinistre.getDateSurvenance());
				// si le code nature est le code en cour d'analyse et que la
				// date de survenance est dans la fourchette de l'analyse
				if (codeAnalyse.equalsIgnoreCase(sinistre.getNature().getCodeAnalyse())
						&& !sinistre.getDateSurvenance().before(dateDebut)
						&& !sinistre.getDateSurvenance().after(dateFin))
				{
					switch (anneeTechn)
					{
						case "N":
							nombreN++;
							coutN = coutN + sinistre.getCoutSinistre();
							break;

						case "N1":
							nombreN1++;
							coutN1 = coutN1 + sinistre.getCoutSinistre();
							break;

						case "N2":
							nombreN2++;
							coutN2 = coutN2 + sinistre.getCoutSinistre();
							break;

						case "N3":
							nombreN3++;
							coutN3 = coutN3 + sinistre.getCoutSinistre();
							break;
					}
				}
			}

			String[] ligneResultat = new String[13];
			ligneResultat[0] = codeAnalyse;
			if ("N3".equals(anneeTechnDepart))
			{
				ligneResultat[1] = String.valueOf(nombreN3);
				ligneResultat[2] = String.valueOf(coutN3);
			}
			else
			{
				nombreN3 = 0;
				coutN3 = 0;
				ligneResultat[1] = charValeurIgnoree;
				ligneResultat[2] = charValeurIgnoree;
			}
			if ("N2".equals(anneeTechnDepart) || "N3".equals(anneeTechnDepart))
			{
				ligneResultat[3] = String.valueOf(nombreN2);
				ligneResultat[4] = doubleFormateurArrondi(coutN2);
			}
			else
			{
				nombreN2 = 0;
				coutN2 = 0;
				ligneResultat[3] = charValeurIgnoree;
				ligneResultat[4] = charValeurIgnoree;
			}
			if ("N1".equals(anneeTechnDepart) || "N2".equals(anneeTechnDepart) || "N3".equals(anneeTechnDepart))
			{
				ligneResultat[5] = String.valueOf(nombreN1);
				ligneResultat[6] = doubleFormateurArrondi(coutN1);
			}
			else
			{
				nombreN1 = 0;
				coutN1 = 0;
				ligneResultat[5] = charValeurIgnoree;
				ligneResultat[6] = charValeurIgnoree;
			}

			ligneResultat[7] = String.valueOf(nombreN);
			ligneResultat[8] = doubleFormateurArrondi(coutN);

			ligneResultat[9] = String.valueOf(nombreN + nombreN1 + nombreN2 + nombreN3);
			ligneResultat[10] = doubleFormateurArrondi(coutN + coutN1 + coutN2 + coutN3);
			ligneResultat[11] = String.valueOf(nombreN1 + nombreN2 + nombreN3);
			ligneResultat[12] = doubleFormateurArrondi(coutN1 + coutN2 + coutN3);

			resultatNatureFlotte.add(ligneResultat);

			totalNombreN += nombreN;
			totalCoutN += coutN;
			totalNombreN1 += nombreN1;
			totalCoutN1 += coutN1;
			totalNombreN2 += nombreN2;
			totalCoutN2 += coutN2;
			totalNombreN3 += nombreN3;
			totalCoutN3 += coutN3;
		}

		// analyse des nonrenseignés
		String[] ligneNR = new String[13];
		int NRnombreN = 0;
		double NRcoutN = 0;
		int NRnombreN1 = 0;
		double NRcoutN1 = 0;
		int NRnombreN2 = 0;
		double NRcoutN2 = 0;
		int NRnombreN3 = 0;
		double NRcoutN3 = 0;
		for (Sinistre sinistre : listeSinistresFlotte)
		{
			boolean contenuDansListe = natureExist(sinistre.getNature().getCodeAnalyse(), listeNatureBdd);
			String anneeTechn = DateHelper.getAnneeTechn(listeAnneesTecn, sinistre.getDateSurvenance());

			if (sinistre.getNature() == null || sinistre.getNature().getCodeAnalyse() == null
					|| contenuDansListe == false)
			{

				if (!sinistre.getDateSurvenance().before(dateDebut) && !sinistre.getDateSurvenance().after(dateFin))
				{
					switch (anneeTechn)
					{
						case "N":
							NRnombreN++;
							NRcoutN = NRcoutN + sinistre.getCoutSinistre();
							break;

						case "N1":
							NRnombreN1++;
							NRcoutN1 = NRcoutN1 + sinistre.getCoutSinistre();
							break;

						case "N2":
							NRnombreN2++;
							NRcoutN2 = NRcoutN2 + sinistre.getCoutSinistre();
							break;

						case "N3":
							NRnombreN3++;
							NRcoutN3 = NRcoutN3 + sinistre.getCoutSinistre();
							break;
					}
				}
			}
		}
		// création de la ligne non renseigné
		ligneNR[0] = "Non renseignée";
		if ("N3".equals(anneeTechnDepart))
		{
			ligneNR[1] = String.valueOf(NRnombreN3);
			ligneNR[2] = doubleFormateurArrondi(NRcoutN3);
		}
		else
		{
			NRnombreN3 = 0;
			NRcoutN3 = 0;
			ligneNR[1] = charValeurIgnoree;
			ligneNR[2] = charValeurIgnoree;
		}
		if ("N2".equals(anneeTechnDepart) || "N3".equals(anneeTechnDepart))
		{
			ligneNR[3] = String.valueOf(NRnombreN2);
			ligneNR[4] = doubleFormateurArrondi(NRcoutN2);
		}
		else
		{
			NRnombreN2 = 0;
			NRcoutN2 = 0;
			ligneNR[3] = charValeurIgnoree;
			ligneNR[4] = charValeurIgnoree;
		}
		if ("N1".equals(anneeTechnDepart) || "N2".equals(anneeTechnDepart) || "N3".equals(anneeTechnDepart))
		{
			ligneNR[5] = String.valueOf(NRnombreN1);
			ligneNR[6] = doubleFormateurArrondi(NRcoutN1);
		}
		else
		{
			NRnombreN1 = 0;
			NRcoutN1 = 0;
			ligneNR[5] = charValeurIgnoree;
			ligneNR[6] = charValeurIgnoree;
		}

		ligneNR[7] = String.valueOf(NRnombreN);
		ligneNR[8] = doubleFormateurArrondi(NRcoutN);

		ligneNR[9] = String.valueOf(NRnombreN + NRnombreN1 + NRnombreN2 + NRnombreN3);
		ligneNR[10] = doubleFormateurArrondi(NRcoutN + NRcoutN1 + NRcoutN2 + NRcoutN3);
		ligneNR[11] = String.valueOf(NRnombreN1 + NRnombreN2 + NRnombreN3);
		ligneNR[12] = doubleFormateurArrondi(NRcoutN1 + NRcoutN2 + NRcoutN3);

		totalNombreN += NRnombreN;
		totalCoutN += NRcoutN;
		totalNombreN1 += NRnombreN1;
		totalCoutN1 += NRcoutN1;
		totalNombreN2 += NRnombreN2;
		totalCoutN2 += NRcoutN2;
		totalNombreN3 += NRnombreN3;
		totalCoutN3 += NRcoutN3;

		// insertion de la ligne non renseignée
		boolean contientNonRenseigne = false;
		for (int i = 1; i < ligneNR.length; i++)
		{
			if (!"0".equals(ligneNR[i]) && !"-".equals(ligneNR[i]))
			{
				contientNonRenseigne = true;
			}
		}
		if (contientNonRenseigne)
		{
			resultatNatureFlotte.add(ligneNR);
		}

		// création de la ligne total
		String[] ligneTotal = new String[13];
		ligneTotal[0] = "Total";
		ligneTotal[1] = String.valueOf(totalNombreN3);
		ligneTotal[2] = doubleFormateurArrondi(totalCoutN3);
		ligneTotal[3] = String.valueOf(totalNombreN2);
		ligneTotal[4] = doubleFormateurArrondi(totalCoutN2);
		ligneTotal[5] = String.valueOf(totalNombreN1);
		ligneTotal[6] = doubleFormateurArrondi(totalCoutN1);
		ligneTotal[7] = String.valueOf(totalNombreN);
		ligneTotal[8] = doubleFormateurArrondi(totalCoutN);
		ligneTotal[9] = String.valueOf(totalNombreN + totalNombreN1 + totalNombreN2 + totalNombreN3);
		ligneTotal[10] = doubleFormateurArrondi(totalCoutN + totalCoutN1 + totalCoutN2 + totalCoutN3);
		ligneTotal[11] = String.valueOf(totalNombreN1 + totalNombreN2 + totalNombreN3);
		ligneTotal[12] = doubleFormateurArrondi(totalCoutN1 + totalCoutN2 + totalCoutN3);
		resultatNatureFlotte.add(ligneTotal);

		return resultatNatureFlotte;
	}

	private boolean natureExist(String codeAnalyse, ArrayList<String> listeNature)
	{
		boolean exist = false;
		if (codeAnalyse != null)
		{
			for (String string : listeNature)
			{
				if (codeAnalyse.equalsIgnoreCase(string))
				{
					exist = true;
				}
			}
		}

		return exist;
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
		recap[4] = contrat.getLibelleProduit();

		return recap;
	}

	private String doubleFormateurArrondi(double monDoubleAParser)
	{
		String resultat = String.valueOf(monDoubleAParser);

		resultat = resultat.replace(".", ",");
		String[] tab = resultat.split(",");

		return tab[0];
	}
	
	private String[] getTotalGenerale(ArrayList<Sinistre> listeSinistresFlotte)
	{
		String[] ligneTotal = null;
		if (listeSinistresFlotte != null)
		{
			ligneTotal = new String[6];
			double total1 = 0;
			double total2 = 0;
			double total3 = 0;
			double total4 = 0;
			
			for (Sinistre s : listeSinistresFlotte)
			{
				total1 += s.getAssureReglementMatCorp();
				total2 += s.getAssureProvisions();
				total3 += s.getAssureFranchise();
				total4 += s.getCoutSinistre();
				
			}
			ligneTotal[0] = "TOTAL";
			ligneTotal[1] = doubleFormateurArrondi(total1);
			ligneTotal[2] = doubleFormateurArrondi(total2);
			ligneTotal[3] = doubleFormateurArrondi(total3);
			ligneTotal[4] = doubleFormateurArrondi(total4);
			ligneTotal[5] = "";
		}

		return ligneTotal;
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
