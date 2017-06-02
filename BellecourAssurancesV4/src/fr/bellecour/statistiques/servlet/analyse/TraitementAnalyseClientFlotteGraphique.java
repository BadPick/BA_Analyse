package fr.bellecour.statistiques.servlet.analyse;

import java.io.IOException;
import java.security.Principal;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;

import fr.bellecour.statistiques.bo.Circonstance;
import fr.bellecour.statistiques.bo.Client;
import fr.bellecour.statistiques.bo.Contrat;
import fr.bellecour.statistiques.bo.Nature;
import fr.bellecour.statistiques.bo.Sinistre;
import fr.bellecour.statistiques.dao.CirconstanceDao;
import fr.bellecour.statistiques.dao.ClientDao;
import fr.bellecour.statistiques.dao.ContratDao;
import fr.bellecour.statistiques.dao.NatureDao;
import fr.bellecour.statistiques.dao.SinistreDao;
import fr.bellecour.statistiques.util.CsvHelper;
import fr.bellecour.statistiques.util.DateHelper;
import fr.bellecour.statistiques.util.ErrorHelper;

/**
 * Servlet implementation class TraitementAnalyseClient
 */
@WebServlet("/TraitementAnalyseClientFlotteGraphique")
public class TraitementAnalyseClientFlotteGraphique extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	private String message;
	private String detailMessage;
	Date dateDebut = null;
	Date dateFin = null;
	String periodeAnalyse = null;
	HashMap<String, String> listeCodeContratAAnalyser = null;
	HashMap<String, String> listeAnneesTecn = null;

	SimpleDateFormat sdfEn = null;
	SimpleDateFormat sdfFr = null;
	// -----------------------flotte général-----------------------
	ArrayList<Sinistre> listeSinistresFlotte = null;
	ArrayList<Sinistre> listeSinistresCroisees = null;
	String[] ligneTotalGenerale = null;
	String[] recapFlotte = null;
	// flotte nature
	ArrayList<String[]> resultatNatureFlotte = null;
	String jsonNatureN = null;
	String jsonNatureCumul = null;
	ArrayList<Nature> listeNatures = null;
	// flotte circonstances
	ArrayList<String[]> resultatCirconstanceFlotte = null;
	ArrayList<Circonstance> listeCirconstances = null;
	String jsonCirconstance = null;
	// flotte taux responsabilités
	ArrayList<String[]> resultatTauxResponsabiliteFlotte = null;
	String jsonTauxResponsabiliteN = null;
	String jsonTauxResponsabiliteCumul = null;
	String jsonGaugeTauxN = null;
	String jsonGaugeTauxCumul = null;
	// flotte MAT/CORP
	ArrayList<String[]> resultatMatCorpFlotte = null;
	String jsonMatCorpN = null;
	String jsonMatCorpCumul = null;
	String jsonGaugeCorpN = null;
	String jsonGaugeCorpCumul = null;
	// flotte salarie
	ArrayList<String[]> resultatSalarieFlotte = null;
	String JsonSalarieFlotte = null;
	boolean affichageSalariesFlotte = true;
	// flotte heures
	ArrayList<String[]> resultatHeureFlotte = null;
	String JsonHeureFlotte = null;
	// flotte jours
	ArrayList<String[]> resultatJourFlotte = null;
	String JsonJourFlotte = null;
	// flotte mois
	ArrayList<String[]> resultatMoisFlotte = null;
	String JsonMoisFlotte = null;
	boolean croisee = false;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TraitementAnalyseClientFlotteGraphique()
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
		listeCodeContratAAnalyser = null;
		listeAnneesTecn = null;
		sdfEn = null;
		sdfFr = null;

		// -----------------------flotte général-----------------------
		listeSinistresFlotte = null;
		ligneTotalGenerale = null;
		recapFlotte = null;
		// flotte nature
		resultatNatureFlotte = null;
		jsonNatureN = null;
		jsonNatureCumul = null;
		listeNatures = null;
		// flotte circonstances
		resultatCirconstanceFlotte = null;
		listeCirconstances = null;
		jsonCirconstance = null;
		// flotte taux responsabilités
		resultatTauxResponsabiliteFlotte = null;
		jsonTauxResponsabiliteN = null;
		jsonTauxResponsabiliteCumul = null;
		jsonGaugeTauxN = null;
		jsonGaugeTauxCumul = null;
		// flotte MAT/CORP
		resultatMatCorpFlotte = null;
		jsonMatCorpN = null;
		jsonMatCorpCumul = null;
		jsonGaugeCorpN = null;
		jsonGaugeCorpCumul = null;
		// flotte salarie
		resultatSalarieFlotte = null;
		JsonSalarieFlotte = null;
		affichageSalariesFlotte = true;
		// flotte heures
		resultatHeureFlotte = null;
		JsonHeureFlotte = null;
		// flotte jours
		resultatJourFlotte = null;
		JsonJourFlotte = null;
		// flotte mois
		resultatMoisFlotte = null;
		JsonMoisFlotte = null;

		Client clientAAnalyser = null;

		request.setAttribute("analyse", "graphique");

		// récupération du client à afficher
		if (request.getParameter("nomClient") != null)
		{
			clientAAnalyser = chargerCLient(request);			
		}

		// récupération des listes de contrats à analyser
		listeCodeContratAAnalyser = recuperationContratsAAnalyser(request);
		
		if (listeCodeContratAAnalyser.size() > 0)
		{
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
			} 
			catch (Exception e)
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
				Contrat contratAAnalyser = null;
				if (listeCodeContratAAnalyser.size() > 1)
				{
					// contrats croisées
					for (Entry<String, String> entry : listeCodeContratAAnalyser.entrySet())
					{
						listeSinistresCroisees = SinistreDao
								.getSinistresPourAnalyse(entry.getKey(), dateDebut, dateFin);
						ajouterALaListe(listeSinistresCroisees);
						contratAAnalyser = ContratDao.getById(entry.getKey());
						if (request.getParameter("dateEcheanceAdded")!=null) {
							listeAnneesTecn = DateHelper.getAnneesTechniques(convertDateEcheanceAdded(request.getParameter("dateEcheanceAdded")));
						}else{
							listeAnneesTecn = DateHelper.getAnneesTechniques(contratAAnalyser.getDateEcheance());
						}
						
					}
					croisee = true;
					recapFlotte = getRecap(listeCodeContratAAnalyser, periodeAnalyse, clientAAnalyser);
					for (Entry<String, String> entry : listeAnneesTecn.entrySet())
					{
						request.setAttribute(entry.getKey(), entry.getValue());
					}
				}
				else
				{
					// contrat unique
					for (Entry<String, String> entry : listeCodeContratAAnalyser.entrySet())
					{
						listeSinistresFlotte = SinistreDao.getSinistresPourAnalyse(entry.getKey(), dateDebut, dateFin);
						recapFlotte = getRecap(entry.getKey(), periodeAnalyse, clientAAnalyser);
						contratAAnalyser = ContratDao.getById(entry.getKey());
						listeAnneesTecn = DateHelper.getAnneesTechniques(contratAAnalyser.getDateEcheance());
					}
					for (Entry<String, String> entry : listeAnneesTecn.entrySet())
					{
						request.setAttribute(entry.getKey(), entry.getValue());
					}
					croisee = false;
				}
			} catch (Exception e)
			{
				detailMessage = e.getMessage();
				message = "un problème est survenue pendant le chargement de la liste des sinistres, ";
				message += ErrorHelper.traitementDuMessageDErreur(e);
				rd = getServletContext().getRequestDispatcher("/jsp/clientAnalyse.jsp");
				e.printStackTrace();
			}

			try
			{
				request.setAttribute("croisee", croisee);
				// récupération de la liste des natures
				listeNatures = NatureDao.getAllUtil("flotte");
				request.setAttribute("listeNatures", listeNatures);
				// récupération de la liste des circonstances
				listeCirconstances = CirconstanceDao.getAll();
				request.setAttribute("listeCirconstances", listeCirconstances);

				// création des différentes listes de tableaux de string pour
				// alimenter les tableau et graph (1 liste = 1 tableau + 2
				// graph)
				// traitement de l'analyse graphique
				if (listeSinistresFlotte != null)
				{
					creationAnalyseFlotte(request);
				}

				rd = getServletContext().getRequestDispatcher("/jsp/clientResultatAnalyse.jsp");
			} catch (Exception e)
			{
				e.printStackTrace();
				detailMessage = e.getMessage();
				message = "un problème est survenue pendant l'analyse des sinistres, ";
				message += ErrorHelper.traitementDuMessageDErreur(e);
				rd = getServletContext().getRequestDispatcher("/jsp/clientAnalyse.jsp");
				e.printStackTrace();
			}
		}
		else
		{
			message = "Aucun contrat selectionné pour l'analyse, ";
			rd = getServletContext().getRequestDispatcher("/jsp/clientAnalyse.jsp");
		}

		request.setAttribute("clientAAnalyser", clientAAnalyser);
		request.setAttribute("detailMessage", detailMessage);
		request.setAttribute("message", message);
		rd.forward(request, response);
	}

	private void ajouterALaListe(ArrayList<Sinistre> listeAAjouter)
	{
		if (listeSinistresFlotte == null)
		{
			listeSinistresFlotte = new ArrayList<Sinistre>();
		}
		for (Sinistre sinistre : listeAAjouter)
		{
			listeSinistresFlotte.add(sinistre);
		}
	}

	private void creationAnalyseFlotte(HttpServletRequest request)
	{
		// analyse des natures de sinistres
		try
		{
			resultatNatureFlotte = analyseNatureFlotte(listeSinistresFlotte, listeAnneesTecn, dateDebut, dateFin);
			jsonNatureN = getJsonPieChartN(resultatNatureFlotte, "Natures");
			jsonNatureCumul = getJsonPieChartCumul(resultatNatureFlotte, "Natures");
			// analyse des circonstances de sinistres
			resultatCirconstanceFlotte = analyseCirconstancesFlotte(listeSinistresFlotte, listeAnneesTecn,
					dateDebut, dateFin);
			jsonCirconstance = getJsonColumnChartCirconstances(resultatCirconstanceFlotte, "Circonstances",
					0, true);
			// analyse des taux de responsabilités
			resultatTauxResponsabiliteFlotte = analyseTauxResponsabiliteFlotte(listeSinistresFlotte,
					listeAnneesTecn, dateDebut, dateFin);
			jsonTauxResponsabiliteN = getJsonPieChartN(resultatTauxResponsabiliteFlotte,
					"Taux de responsabilité");
			jsonTauxResponsabiliteCumul = getJsonPieChartCumul(resultatTauxResponsabiliteFlotte,
					"Taux de responsabilité");
			jsonGaugeTauxN = getJsonGaugeTauxRespN(resultatTauxResponsabiliteFlotte, "% resp.");
			jsonGaugeTauxCumul = getJsonGaugeTauxRespCumul(resultatTauxResponsabiliteFlotte, "% resp.");
			// analyse des MAT/CORP
			resultatMatCorpFlotte = analyseMatCorpFlotte(listeSinistresFlotte, listeAnneesTecn, dateDebut,
					dateFin);
			jsonMatCorpN = getJsonPieChartN(resultatMatCorpFlotte, "type de dommage");
			jsonMatCorpCumul = getJsonPieChartCumul(resultatMatCorpFlotte, "type de dommage");
			jsonGaugeCorpN = getJsonGaugeN(resultatMatCorpFlotte, "nb CORP", 1);
			jsonGaugeCorpCumul = getJsonGaugeCumul(resultatMatCorpFlotte, "nb CORP", 1);
			// analyse par salarié
			resultatSalarieFlotte = analyseSalarieFlotte(listeSinistresFlotte, listeAnneesTecn, dateDebut,
					dateFin);
			JsonSalarieFlotte = getJsonColumnChart(resultatSalarieFlotte, "salariés", 5000, true);
			if (JsonSalarieFlotte.length() <= 33)
			{
				affichageSalariesFlotte = false;
			}
			// analyse par heures de survenance
			resultatHeureFlotte = analyseHeureFlotte(listeSinistresFlotte, listeAnneesTecn, dateDebut,
					dateFin);
			JsonHeureFlotte = getJsonColumnChart(resultatHeureFlotte, "heures", 0, false);
			// analyse par jours de survenance
			resultatJourFlotte = analyseJourFlotte(listeSinistresFlotte, listeAnneesTecn, dateDebut,
					dateFin);
			JsonJourFlotte = getJsonColumnChart(resultatJourFlotte, "jours", 0, false);
			// analyse par mois de survenance
			resultatMoisFlotte = analyseMoisFlotte(listeSinistresFlotte, listeAnneesTecn, dateDebut,
					dateFin);
			JsonMoisFlotte = getJsonColumnChart(resultatMoisFlotte, "mois", 0, false);

			request.setAttribute("JsonMoisFlotte", JsonMoisFlotte);
			request.setAttribute("resultatMoisFlotte", resultatMoisFlotte);
			request.setAttribute("JsonJourFlotte", JsonJourFlotte);
			request.setAttribute("resultatJourFlotte", resultatJourFlotte);
			request.setAttribute("JsonHeureFlotte", JsonHeureFlotte);
			request.setAttribute("resultatHeureFlotte", resultatHeureFlotte);
			request.setAttribute("JsonSalarieFlotte", JsonSalarieFlotte);
			request.setAttribute("affichageSalariesFlotte", affichageSalariesFlotte);
			request.setAttribute("resultatSalarieFlotte", resultatSalarieFlotte);
			request.setAttribute("JsonMatCorpFlotteN", jsonMatCorpN);
			request.setAttribute("JsonMatCorpFlotteCumul", jsonMatCorpCumul);
			request.setAttribute("jsonGaugeCorpN", jsonGaugeCorpN);
			request.setAttribute("jsonGaugeCorpCumul", jsonGaugeCorpCumul);
			request.setAttribute("resultatMatCorpFlotte", resultatMatCorpFlotte);
			request.setAttribute("JsonTauxResponsabiliteFlotteN", jsonTauxResponsabiliteN);
			request.setAttribute("JsonTauxResponsabiliteFlotteCumul", jsonTauxResponsabiliteCumul);
			request.setAttribute("jsonGaugeTauxN", jsonGaugeTauxN);
			request.setAttribute("jsonGaugeTauxCumul", jsonGaugeTauxCumul);
			request.setAttribute("resultatTauxResponsabiliteFlotte", resultatTauxResponsabiliteFlotte);
			request.setAttribute("JsonCirconstanceFlotte", jsonCirconstance);
			request.setAttribute("resultatCirconstanceFlotte", resultatCirconstanceFlotte);
			request.setAttribute("JsonNatureFlotteN", jsonNatureN);
			request.setAttribute("JsonNatureFlotteCumul", jsonNatureCumul);
			request.setAttribute("resultatNatureFlotte", resultatNatureFlotte);
			request.setAttribute("recapFlotte", recapFlotte);
		} catch (ParseException e)
		{
			e.printStackTrace();
			detailMessage = e.getMessage();
			message = "un problème est survenue pendant l'analyse des sinistres, ";
			message += ErrorHelper.traitementDuMessageDErreur(e);
		}

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
	private ArrayList<String[]> analyseNatureFlotte(ArrayList<Sinistre> listeSinistresFlotte,
			HashMap<String, String> listeAnneesTecn, Date dateDebut, Date dateFin) throws ParseException
	{
		ArrayList<String[]> resultatNatureFlotte = new ArrayList<String[]>();
		ArrayList<String> listeNature = null;
		try
		{
			listeNature = NatureDao.getAllCodeAnalyse("flotte");
		} catch (Exception e)
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
		for (String codeAnalyse : listeNature)
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
			boolean contenuDansListe = natureExist(sinistre.getNature().getCodeAnalyse(), listeNature);
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
		// création de la ligne 
		ligneNR[0] = "non renseigné";
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

		// insertion de la ligne 
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
	 * méthode permettant l'analyse de la circonstance des sinistres
	 * 
	 * @param listeSinistresFlotte
	 * @param listeAnneesTecn
	 * @param dateDebut
	 * @param dateFin
	 * @param contratFlotte
	 * @return ArrayList<String[]>
	 * @throws ParseException
	 */
	private ArrayList<String[]> analyseCirconstancesFlotte(ArrayList<Sinistre> listeSinistresFlotte,
			HashMap<String, String> listeAnneesTecn, Date dateDebut, Date dateFin) throws ParseException
	{
		ArrayList<String[]> resultatCirconstancesFlotte = new ArrayList<String[]>();
		ArrayList<Circonstance> listeCirconstancesAAnalyser = new ArrayList<Circonstance>();
		ArrayList<String> listeCirconstancesString = new ArrayList<String>();
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

		// création de la liste des Circonstances de sinistres à analyser
		for (Sinistre s : listeSinistresFlotte)
		{
			if (s.getCirconstance() != null && s.getCirconstance().getCodeCirconstance() != null
					&& !listeCirconstancesString.contains(s.getCirconstance().getCodeCirconstance()))
			{
				listeCirconstancesString.add(s.getCirconstance().getCodeCirconstance());
				Circonstance c = s.getCirconstance();
				c.setLibelle(s.getCirconstance().getLibelle());
				listeCirconstancesAAnalyser.add(c);
			}
		}
		Collections.sort(listeCirconstancesAAnalyser);

		// analyse des sinistres par nature de sinistres
		for (Circonstance circonstance : listeCirconstancesAAnalyser)
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
				if (circonstance.getCodeCirconstance().equalsIgnoreCase(
						sinistre.getCirconstance().getCodeCirconstance())
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

			String[] ligneResultat = new String[14];
			ligneResultat[0] = circonstance.getLibelle();
			ligneResultat[13] = circonstance.getCodeCirconstance();
			if ("N3".equals(anneeTechnDepart))
			{
				ligneResultat[1] = String.valueOf(nombreN3);
				ligneResultat[2] = doubleFormateurArrondi(coutN3);
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

			resultatCirconstancesFlotte.add(ligneResultat);

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
		String[] ligneNR = new String[14];
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

			String anneeTechn = DateHelper.getAnneeTechn(listeAnneesTecn, sinistre.getDateSurvenance());

			if (sinistre.getCirconstance() == null || sinistre.getCirconstance().getCodeCirconstance() == null)
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
		ligneNR[0] = "non renseigné";
		ligneNR[13] = "NR";
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

		// insertion de la ligne 
		boolean contientNonRenseigne = false;
		for (int i = 1; i < ligneNR.length - 1; i++)
		{
			if (!"0".equals(ligneNR[i]) && !"-".equals(ligneNR[i]))
			{
				contientNonRenseigne = true;
			}
		}
		if (contientNonRenseigne)
		{
			resultatCirconstancesFlotte.add(ligneNR);
		}

		// création de la ligne totale
		String[] ligneTotal = new String[14];
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
		resultatCirconstancesFlotte.add(ligneTotal);

		return resultatCirconstancesFlotte;
	}

	/**
	 * méthode permettant l'analyse de la circonstance des sinistres
	 * 
	 * @param listeSinistresFlotte
	 * @param listeAnneesTecn
	 * @param dateDebut
	 * @param dateFin
	 * @param contratFlotte
	 * @return ArrayList<String[]>
	 * @throws ParseException
	 */
	private ArrayList<String[]> analyseTauxResponsabiliteFlotte(ArrayList<Sinistre> listeSinistresFlotte,
			HashMap<String, String> listeAnneesTecn, Date dateDebut, Date dateFin) throws ParseException
	{
		ArrayList<String[]> resultatTauxResponsabiliteFlotte = new ArrayList<String[]>();
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

		// création de la liste des taux de responsabilitées de sinistres à
		// analyser
		ArrayList<Integer> listeTauxResponsabilites = new ArrayList<Integer>();
		listeTauxResponsabilites.add(0);
		listeTauxResponsabilites.add(50);
		listeTauxResponsabilites.add(100);

		// analyse des sinistres par nature de sinistres
		for (Integer taux : listeTauxResponsabilites)
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
				if (sinistre.getTauxResponsabilite() == taux && !sinistre.getDateSurvenance().before(dateDebut)
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
			ligneResultat[0] = taux.toString() + "%";
			if ("N3".equals(anneeTechnDepart))
			{
				ligneResultat[1] = String.valueOf(nombreN3);
				ligneResultat[2] = doubleFormateurArrondi(coutN3);
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

			resultatTauxResponsabiliteFlotte.add(ligneResultat);

			totalNombreN += nombreN;
			totalCoutN += coutN;
			totalNombreN1 += nombreN1;
			totalCoutN1 += coutN1;
			totalNombreN2 += nombreN2;
			totalCoutN2 += coutN2;
			totalNombreN3 += nombreN3;
			totalCoutN3 += coutN3;
		}

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
		resultatTauxResponsabiliteFlotte.add(ligneTotal);

		return resultatTauxResponsabiliteFlotte;
	}

	/**
	 * méthode permettant l'analyse de la circonstance des sinistres
	 * 
	 * @param listeSinistresFlotte
	 * @param listeAnneesTecn
	 * @param dateDebut
	 * @param dateFin
	 * @param contratFlotte
	 * @return ArrayList<String[]>
	 * @throws ParseException
	 */
	private ArrayList<String[]> analyseMatCorpFlotte(ArrayList<Sinistre> listeSinistresFlotte,
			HashMap<String, String> listeAnneesTecn, Date dateDebut, Date dateFin) throws ParseException
	{
		ArrayList<String[]> resultatMatCorpFlotte = new ArrayList<String[]>();
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

		// création de la liste des taux de responsabilitées de sinistres à
		// analyser
		ArrayList<String> listeMatCorp = new ArrayList<String>();
		listeMatCorp.add("MAT");
		listeMatCorp.add("CORP");

		// analyse des sinistres
		for (String type : listeMatCorp)
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
				// si le type est le code en cour d'analyse et que la date de
				// survenance est dans la fourchette de l'analyse
				if (type.equalsIgnoreCase(sinistre.getTypeDommage()) && !sinistre.getDateSurvenance().before(dateDebut)
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
			ligneResultat[0] = type;
			if ("N3".equals(anneeTechnDepart))
			{
				ligneResultat[1] = String.valueOf(nombreN3);
				ligneResultat[2] = doubleFormateurArrondi(coutN3);
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

			resultatMatCorpFlotte.add(ligneResultat);

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

			String anneeTechn = DateHelper.getAnneeTechn(listeAnneesTecn, sinistre.getDateSurvenance());

			if (sinistre.getTypeDommage() == null
					|| (!"MAT".equalsIgnoreCase(sinistre.getTypeDommage()) && !"CORP".equalsIgnoreCase(sinistre
							.getTypeDommage())))
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
		// création de la ligne 
		ligneNR[0] = "non renseigné";
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

		// insertion de la ligne 
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
			resultatMatCorpFlotte.add(ligneNR);
		}

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
		resultatMatCorpFlotte.add(ligneTotal);

		return resultatMatCorpFlotte;
	}

	/**
	 * méthode permettant l'analyse des sinistres par salariés
	 * 
	 * @param listeSinistresFlotte
	 * @param listeAnneesTecn
	 * @param dateDebut
	 * @param dateFin
	 * @param contratFlotte
	 * @return ArrayList<String[]>
	 * @throws ParseException
	 */
	private ArrayList<String[]> analyseSalarieFlotte(ArrayList<Sinistre> listeSinistresFlotte,
			HashMap<String, String> listeAnneesTecn, Date dateDebut, Date dateFin) throws ParseException
	{
		ArrayList<String[]> resultatSalarieFlotte = new ArrayList<String[]>();
		ArrayList<String> listeSalaries = new ArrayList<String>();
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

		// création de la liste des salariés de sinistres à analyser
		for (Sinistre s : listeSinistresFlotte)
		{
			if (s.getNomSalarie() != null && !listeSalaries.contains(s.getNomSalarie()))
			{
				listeSalaries.add(s.getNomSalarie());
			}
		}
		Collections.sort(listeSalaries);

		// analyse des sinistres
		for (String salarie : listeSalaries)
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
				// si le type est le code en cour d'analyse et que la date de
				// survenance est dans la fourchette de l'analyse
				if (salarie.equalsIgnoreCase(sinistre.getNomSalarie())
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

			String[] ligneResultat = new String[14];
			ligneResultat[0] = salarie;
			if ("N3".equals(anneeTechnDepart))
			{
				ligneResultat[1] = String.valueOf(nombreN3);
				ligneResultat[2] = doubleFormateurArrondi(coutN3);
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
			// si 3 sinistres en tout ou 2 sinsistres sur 2 années concécutives
			if ((nombreN + nombreN1) > 1 || (nombreN1 + nombreN2) > 1 || (nombreN2 + nombreN3) > 1
					|| (nombreN + nombreN1 + nombreN2 + nombreN3) > 2)
			{
				ligneResultat[13] = "G";
			}
			else
			{
				ligneResultat[13] = "N";
			}

			totalNombreN += nombreN;
			totalCoutN += coutN;
			totalNombreN1 += nombreN1;
			totalCoutN1 += coutN1;
			totalNombreN2 += nombreN2;
			totalCoutN2 += coutN2;
			totalNombreN3 += nombreN3;
			totalCoutN3 += coutN3;

			resultatSalarieFlotte.add(ligneResultat);
		}

		// analyse des nonrenseignés
		String[] ligneNR = new String[14];
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

			String anneeTechn = DateHelper.getAnneeTechn(listeAnneesTecn, sinistre.getDateSurvenance());

			if (sinistre.getNomSalarie() == null)
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
		// création de la ligne 
		ligneNR[0] = "non renseigné";
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

		// insertion de la ligne 
		boolean contientNonRenseigne = false;
		for (int i = 1; i < ligneNR.length - 1; i++)
		{
			if (!"0".equals(ligneNR[i]) && !"-".equals(ligneNR[i]))
			{
				contientNonRenseigne = true;
			}
		}
		if (contientNonRenseigne)
		{
			resultatSalarieFlotte.add(ligneNR);
		}

		// création de la ligne total
		String[] ligneTotal = new String[14];
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
		resultatSalarieFlotte.add(ligneTotal);

		return resultatSalarieFlotte;
	}

	/**
	 * méthode permettant l'analyse des sinistres par mois
	 * 
	 * @param listeSinistresFlotte
	 * @param listeAnneesTecn
	 * @param dateDebut
	 * @param dateFin
	 * @param contratFlotte
	 * @return ArrayList<String[]>
	 * @throws ParseException
	 */
	private ArrayList<String[]> analyseMoisFlotte(ArrayList<Sinistre> listeSinistresFlotte,
			HashMap<String, String> listeAnneesTecn, Date dateDebut, Date dateFin) throws ParseException
	{
		ArrayList<String[]> resultatHeureFlotte = new ArrayList<String[]>();
		ArrayList<String> listeMois = new ArrayList<String>();
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

		// création de la liste des jours à analyser
		listeMois.add("Janvier");
		listeMois.add("Février");
		listeMois.add("Mars");
		listeMois.add("Avril");
		listeMois.add("Mai");
		listeMois.add("Juin");
		listeMois.add("Juillet");
		listeMois.add("Août");
		listeMois.add("Septembre");
		listeMois.add("Octobre");
		listeMois.add("Novembre");
		listeMois.add("Décembre");

		// analyse des sinistres par heure de survenance
		for (String mois : listeMois)
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

				// si le jour est en cour d'analyse et que la date de
				// survenance est dans la fourchette de l'analyse
				if (mois.equalsIgnoreCase(sinistre.getMoisDeSurvenance())
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
			ligneResultat[0] = mois;
			if ("N3".equals(anneeTechnDepart))
			{
				ligneResultat[1] = String.valueOf(nombreN3);
				ligneResultat[2] = doubleFormateurArrondi(coutN3);
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

			resultatHeureFlotte.add(ligneResultat);

			totalNombreN += nombreN;
			totalCoutN += coutN;
			totalNombreN1 += nombreN1;
			totalCoutN1 += coutN1;
			totalNombreN2 += nombreN2;
			totalCoutN2 += coutN2;
			totalNombreN3 += nombreN3;
			totalCoutN3 += coutN3;
		}

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
		resultatHeureFlotte.add(ligneTotal);

		return resultatHeureFlotte;
	}

	/**
	 * méthode permettant l'analyse des sinistres par jours
	 * 
	 * @param listeSinistresFlotte
	 * @param listeAnneesTecn
	 * @param dateDebut
	 * @param dateFin
	 * @param contratFlotte
	 * @return ArrayList<String[]>
	 * @throws ParseException
	 */
	private ArrayList<String[]> analyseJourFlotte(ArrayList<Sinistre> listeSinistresFlotte,
			HashMap<String, String> listeAnneesTecn, Date dateDebut, Date dateFin) throws ParseException
	{
		ArrayList<String[]> resultatHeureFlotte = new ArrayList<String[]>();
		ArrayList<String> listeJours = new ArrayList<String>();
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

		// création de la liste des jours à analyser
		listeJours.add("Lundi");
		listeJours.add("Mardi");
		listeJours.add("Mercredi");
		listeJours.add("Jeudi");
		listeJours.add("Vendredi");
		listeJours.add("Samedi");
		listeJours.add("Dimanche");

		// analyse des sinistres par heure de survenance
		for (String jour : listeJours)
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

				// si le jour est en cour d'analyse et que la date de
				// survenance est dans la fourchette de l'analyse
				if (jour.equalsIgnoreCase(sinistre.getJourDeSurvenance())
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
			ligneResultat[0] = jour;
			if ("N3".equals(anneeTechnDepart))
			{
				ligneResultat[1] = String.valueOf(nombreN3);
				ligneResultat[2] = doubleFormateurArrondi(coutN3);
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

			resultatHeureFlotte.add(ligneResultat);

			totalNombreN += nombreN;
			totalCoutN += coutN;
			totalNombreN1 += nombreN1;
			totalCoutN1 += coutN1;
			totalNombreN2 += nombreN2;
			totalCoutN2 += coutN2;
			totalNombreN3 += nombreN3;
			totalCoutN3 += coutN3;
		}

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
		resultatHeureFlotte.add(ligneTotal);

		return resultatHeureFlotte;
	}

	/**
	 * méthode permettant l'analyse des sinistres par heures
	 * 
	 * @param listeSinistresFlotte
	 * @param listeAnneesTecn
	 * @param dateDebut
	 * @param dateFin
	 * @param contratFlotte
	 * @return ArrayList<String[]>
	 * @throws ParseException
	 */
	private ArrayList<String[]> analyseHeureFlotte(ArrayList<Sinistre> listeSinistresFlotte,
			HashMap<String, String> listeAnneesTecn, Date dateDebut, Date dateFin) throws ParseException
	{
		ArrayList<String[]> resultatHeureFlotte = new ArrayList<String[]>();
		ArrayList<String> listeHeures = new ArrayList<String>();
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
		int NRnombreN = 0;
		double NRcoutN = 0;
		int NRnombreN1 = 0;
		double NRcoutN1 = 0;
		int NRnombreN2 = 0;
		double NRcoutN2 = 0;
		int NRnombreN3 = 0;
		double NRcoutN3 = 0;
		String[] ligneNR = new String[13];

		// création de la liste des heures à analyser
		listeHeures.add("00");
		listeHeures.add("01");
		listeHeures.add("02");
		listeHeures.add("03");
		listeHeures.add("04");
		listeHeures.add("05");
		listeHeures.add("06");
		listeHeures.add("07");
		listeHeures.add("08");
		listeHeures.add("09");
		listeHeures.add("10");
		listeHeures.add("11");
		listeHeures.add("12");
		listeHeures.add("13");
		listeHeures.add("14");
		listeHeures.add("15");
		listeHeures.add("16");
		listeHeures.add("17");
		listeHeures.add("18");
		listeHeures.add("19");
		listeHeures.add("20");
		listeHeures.add("21");
		listeHeures.add("22");
		listeHeures.add("23");

		// analyse des sinistres par heure de survenance
		for (String heure : listeHeures)
		{
			String heureSinistre = null;
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

				if (sinistre.getHeureSurvenance() != null)
				{
					// récupération de l'heure du sinistre (sans les minutes)
					heureSinistre = sinistre.getHeureSurvenance().substring(0, 2);

					// si l'heure est en cour d'analyse et que la date de
					// survenance est dans la fourchette de l'analyse
					if (heure.equalsIgnoreCase(heureSinistre) && !sinistre.getDateSurvenance().before(dateDebut)
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
			}

			String[] ligneResultat = new String[13];
			ligneResultat[0] = heure + "H";
			if ("N3".equals(anneeTechnDepart))
			{
				ligneResultat[1] = String.valueOf(nombreN3);
				ligneResultat[2] = doubleFormateurArrondi(coutN3);
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

			resultatHeureFlotte.add(ligneResultat);

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
		for (Sinistre sinistre : listeSinistresFlotte)
		{

			String anneeTechn = DateHelper.getAnneeTechn(listeAnneesTecn, sinistre.getDateSurvenance());

			if (sinistre.getHeureSurvenance() == null)
			{
				// si la l'heure de survenance n'est pas renseignée
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
		// création de la ligne 
		ligneNR[0] = "non renseigné";
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
		// insertion de la ligne 
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
			resultatHeureFlotte.add(ligneNR);
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
		resultatHeureFlotte.add(ligneTotal);

		return resultatHeureFlotte;
	}

	private String[] getRecap(String codeContrat, String periodeAnalyse, Client clientAAnalyser) throws ParseException
	{
		String[] recap = new String[5];
		Contrat contrat = null;
		// nom client
		recap[0] = clientAAnalyser.getNom();

		// contrat
		for (Contrat c : clientAAnalyser.getListeContrats())
		{
			if (c.getCodeContrat().equals(codeContrat))
			{
				contrat = c;
			}
		}
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

	private String[] getRecap(HashMap<String, String> listeCodeContratAAnalyser, String periodeAnalyse,
			Client clientAAnalyser) throws ParseException
	{
		String[] recap = new String[5];
		Contrat contrat = null;
		// nom client
		recap[0] = clientAAnalyser.getNom();
		int i = 0;
		
		for (Entry<String, String> entry : listeCodeContratAAnalyser.entrySet())
		{
			// contrat
			for (Contrat c : clientAAnalyser.getListeContrats())
			{
				if (c.getCodeContrat().equals(entry.getKey()))
				{
					contrat = c;
				}
			}
			if (i==0)
			{
				recap[1] = contrat.getAssureur() + " - n°" + contrat.getCodeContrat();
			}
			else 
			{
				recap[1] += " / " + contrat.getAssureur() + " - n°" + contrat.getCodeContrat();
			}
			
			// date effet
			recap[2] = contrat.getDateEcheanceString();

			// période analyse
			recap[3] = periodeAnalyse;

			// branche
			recap[4] = contrat.getBrancheProduit() + " (" + contrat.getLibelleProduit() + ")";
			i++;
		}
		return recap;
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
	 * méthode permettant de préparer les données au format attendu par la gauge
	 * représentant l'année N
	 * 
	 * @param resultat
	 * @param libelle
	 * @return String
	 */
	private String getJsonGaugeN(ArrayList<String[]> resultat, String libelle, int ligne)
	{
		int i = 0;
		String[] ligneRecherchee = resultat.get(ligne);

		String json = "[['Label', 'Value'],[" + "'" + libelle + "', " + ligneRecherchee[7] + "],]";
		return json;
	}

	/**
	 * méthode permettant de préparer les données au format attendu par la gauge
	 * représentant les années N-1, N-2, N-3
	 * 
	 * @param resultat
	 * @param libelle
	 * @return String
	 */
	private String getJsonGaugeCumul(ArrayList<String[]> resultat, String libelle, int ligne)
	{
		int i = 0;
		String[] ligneRecherchee = resultat.get(ligne);

		String json = "[['Label', 'Value'],[" + "'" + libelle + "', " + ligneRecherchee[11] + "],]";
		return json;
	}

	private String getJsonGaugeTauxRespCumul(ArrayList<String[]> resultat, String libelle)
	{
		int i = 0;
		double produitCroix = 0;
		String[] lignetotal = resultat.get(3);
		String[] ligneCent = resultat.get(2);
		int total = Integer.parseInt(lignetotal[11]);
		int nbCent = Integer.parseInt(ligneCent[11]);
		if (total != 0 && nbCent != 0)
		{
			produitCroix = (nbCent * 100) / total;
		}

		String json = "[['Label', 'Value'],[" + "'" + libelle + "', " + doubleFormateurArrondi(produitCroix) + "],]";
		return json;
	}

	private String getJsonGaugeTauxRespN(ArrayList<String[]> resultat, String libelle)
	{
		int i = 0;
		double produitCroix = 0;
		String[] lignetotal = resultat.get(3);
		String[] ligneCent = resultat.get(2);
		int total = Integer.parseInt(lignetotal[7]);
		int nbCent = Integer.parseInt(ligneCent[7]);
		if (total != 0 && nbCent != 0)
		{
			produitCroix = (nbCent * 100) / total;
		}

		String json = "[['Label', 'Value'],[" + "'" + libelle + "', " + doubleFormateurArrondi(produitCroix) + "],]";
		return json;
	}

	/**
	 * méthode permettant de préparer les données au format attendu par le
	 * columnChart
	 * 
	 * @param resultatNatureFlotte
	 * @param libelle
	 * @return String
	 */
	private String getJsonColumnChart(ArrayList<String[]> resultat, String libelle, double seuil, boolean cout)
	{
		int i = 0;
		String json = "[['" + libelle + "','N-3,N-2,N-1', 'N']";

		for (String[] s : resultat)
		{
			if (!"Total".equalsIgnoreCase(s[0]) && !"non renseigné".equalsIgnoreCase(s[0]))
			{
				if (seuil != 0)
				{
					if (Integer.parseInt(s[12]) > seuil || Integer.parseInt(s[8]) > seuil)
					{
						json += ",";
						if (cout)
						{
							json += "['" + s[0] + "'," + s[12] + "," + s[8] + "]";
						}
						else
						{
							json += "['" + s[0] + "'," + s[11] + "," + s[7] + "]";
						}

						i++;
					}
				}
				else
				{
					json += ",";
					if (cout)
					{
						json += "['" + s[0] + "'," + s[12] + "," + s[8] + "]";
					}
					else
					{
						json += "['" + s[0] + "'," + s[11] + "," + s[7] + "]";
					}
					i++;
				}

			}
		}
		json += "]";
		return json;
	}

	/**
	 * méthode permettant de préparer les données au format attendu par le
	 * columnChart (spécifique circonstances)
	 * 
	 * @param resultat
	 * @param libelle
	 * @return String
	 */
	private String getJsonColumnChartCirconstances(ArrayList<String[]> resultat, String libelle, double seuil,
			boolean cout)
	{
		int i = 0;
		String json = "[['" + libelle + "','N-3,N-2,N-1', 'N']";

		for (String[] s : resultat)
		{
			if (!"Total".equalsIgnoreCase(s[0])&&!"non renseigné".equalsIgnoreCase(s[0]))
			{
				if (seuil != 0)
				{
					if (Integer.parseInt(s[12]) > seuil || Integer.parseInt(s[8]) > seuil)
					{
						json += ",";
						if (cout)
						{
							json += "['" + s[13] + "'," + s[12] + "," + s[8] + "]";
						}
						else
						{
							json += "['" + s[13] + "'," + s[11] + "," + s[7] + "]";
						}

						i++;
					}
				}
				else
				{
					json += ",";
					if (cout)
					{
						json += "['" + s[13] + "'," + s[12] + "," + s[8] + "]";
					}
					else
					{
						json += "['" + s[13] + "'," + s[11] + "," + s[7] + "]";
					}
					i++;
				}

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

	private String doubleFormateurArrondi(double monDoubleAParser)
	{
		String resultat = String.valueOf(monDoubleAParser);

		resultat = resultat.replace(".", ",");
		String[] tab = resultat.split(",");

		return tab[0];
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
	
	private String convertDateEcheanceAdded(String dateEcheanceAdded) throws ParseException{
		SimpleDateFormat sdfIn = new SimpleDateFormat("dd/MM");
		SimpleDateFormat sdfOut = new SimpleDateFormat("Mdd");
		return sdfOut.format(sdfIn.parse(dateEcheanceAdded));
	}
}
