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
 * Servlet implementation class TraitementAnalyseAssureur
 */
@WebServlet("/TraitementAnalyseAssureur")
public class TraitementAnalyseAssureur extends HttpServlet
{
	private String message;
	private String detailMessage;
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TraitementAnalyseAssureur()
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
		HashMap<String, String> listeAnneesTecn = null;
		String userAgent = request.getHeader("User-Agent");
		String[] totalGenerale = null;
		Client clientAAnalyser = null;
		Contrat contratAAnalyser = null;
		ArrayList<Sinistre> listeSinistres = null;
		int primeN3 = 0;
		int primeN2 = 0;
		int primeN1 = 0;
		int primeN = 0;
		int vehiculeN3 = 0;
		int vehiculeN2 = 0;
		int vehiculeN1 = 0;
		int vehiculeN = 0;
		String[] resultatPrimes = null;
		String[] resultatVehicules = null;
		String[] recap = null;
		String jsonPrimes = null;
		String jsonVehicules = null;
		boolean analysePrimes = false;
		boolean analyseVehicules = false;
		Date dateDebut = null;
		Date dateFin = null;

		// récupération du client à afficher
		if (request.getParameter("nomClient") != null)
		{
			clientAAnalyser = chargerCLient(request);
			try
			{
				contratAAnalyser=ContratDao.getById(request.getParameter("contrat"));
				listeAnneesTecn = DateHelper.getAnneesTechniques(contratAAnalyser.getDateEcheance());
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				for (Entry<String, String> entry : listeAnneesTecn.entrySet())
				{
					request.setAttribute(entry.getKey(), entry.getValue());
					if ("dateDebutN3".equalsIgnoreCase(entry.getKey()))
					{
						dateDebut = sdf.parse(entry.getValue());
					}
					if ("dateFinN".equalsIgnoreCase(entry.getKey()))
					{
						dateFin = sdf.parse(entry.getValue());
					}
				}
			} 
			catch ( Exception e)
			{
				detailMessage = e.getMessage();
				message = "erreur dans le format de la date d'échéance, ";
				message += ErrorHelper.traitementDuMessageDErreur(e);
				e.printStackTrace();
			}
		}
		//si un contrat est bien sélectionné
		if (contratAAnalyser!=null)
		{
			try
			{
				// récupération des primes
				if (request.getParameter("primeN-3") != null && !request.getParameter("primeN-3").isEmpty())
				{
					primeN3 = Integer.parseInt(request.getParameter("primeN-3"));
					analysePrimes = true;
				}
				if (request.getParameter("primeN-2") != null && !request.getParameter("primeN-2").isEmpty())
				{
					primeN2 = Integer.parseInt(request.getParameter("primeN-2"));
					analysePrimes = true;
				}
				if (request.getParameter("primeN-1") != null && !request.getParameter("primeN-1").isEmpty())
				{
					primeN1 = Integer.parseInt(request.getParameter("primeN-1"));
					analysePrimes = true;
				}
				if (request.getParameter("primeN") != null && !request.getParameter("primeN").isEmpty())
				{
					primeN = Integer.parseInt(request.getParameter("primeN"));
					analysePrimes = true;
				}
			} catch (Exception e)
			{
				detailMessage = e.getMessage();
				message = "erreur dans le format d'une des primes, ";
				message += ErrorHelper.traitementDuMessageDErreur(e);
				e.printStackTrace();
			}

			try
			{
				// récupération des nombres de véhicules
				if (request.getParameter("vehiculesN-3") != null && !request.getParameter("vehiculesN-3").isEmpty())
				{
					vehiculeN3 = Integer.parseInt(request.getParameter("vehiculesN-3"));
					analyseVehicules = true;
				}
				if (request.getParameter("vehiculesN-2") != null && !request.getParameter("vehiculesN-2").isEmpty())
				{
					vehiculeN2 = Integer.parseInt(request.getParameter("vehiculesN-2"));
					analyseVehicules = true;
				}
				if (request.getParameter("vehiculesN-1") != null && !request.getParameter("vehiculesN-1").isEmpty())
				{
					vehiculeN1 = Integer.parseInt(request.getParameter("vehiculesN-1"));
					analyseVehicules = true;
				}
				if (request.getParameter("vehiculesN") != null && !request.getParameter("vehiculesN").isEmpty())
				{
					vehiculeN = Integer.parseInt(request.getParameter("vehiculesN"));
					analyseVehicules = true;
				}
			} catch (Exception e)
			{
				detailMessage = e.getMessage();
				message = "erreur dans le format d'un des nombre de véhicules, ";
				message += ErrorHelper.traitementDuMessageDErreur(e);
				e.printStackTrace();
			}

			if (analyseVehicules || analysePrimes)
			{
				// récupération des sinistres
				try
				{				
						listeSinistres = SinistreDao.getSinistresPourAnalyse(request.getParameter("contrat"), dateDebut, dateFin);			
				} 
				catch (Exception e)
				{
					detailMessage = e.getMessage();
					message = "echec du chargement de la liste des sinistres, ";
					message += ErrorHelper.traitementDuMessageDErreur(e);
					e.printStackTrace();
				}

				// récupération des nombres et coûts de sinistres
				try
				{
					totalGenerale = analyseTotalAssureur(listeSinistres, listeAnneesTecn);
				} 
				catch (ParseException e)
				{
					detailMessage = e.getMessage();
					message = "erreur pendant l'analyse des sinistres, ";
					message += ErrorHelper.traitementDuMessageDErreur(e);
					e.printStackTrace();
				}
			}
			// création du récapitulatif
			try
			{
				recap = getRecap(request.getParameter("contrat"), clientAAnalyser);
			} catch (ParseException e)
			{
				detailMessage = e.getMessage();
				message = "erreur pendant la création du récapitulatis de l'analyse, ";
				message += ErrorHelper.traitementDuMessageDErreur(e);
				e.printStackTrace();
			}
			if (analysePrimes)
			{
				// traitement de l'analyse des primes
				resultatPrimes = analysePrimes(primeN3, primeN2, primeN1, primeN, totalGenerale);
				jsonPrimes = getJsonColumnChart(resultatPrimes, "ratio", 0.5);
			}

			if (analyseVehicules)
			{
				// traitement de l'analyse des nombres de véhicules
				resultatVehicules = analyseVehicules(vehiculeN3, vehiculeN2, vehiculeN1, vehiculeN, totalGenerale);
				jsonVehicules = getJsonColumnChart(resultatVehicules, "ratio", 0.3);
			}

			request.setAttribute("clientAAnalyser", clientAAnalyser);
			request.setAttribute("analysePrimes", analysePrimes);
			request.setAttribute("analyseVehicules", analyseVehicules);
			request.setAttribute("recap", recap);
			request.setAttribute("totalGenerale", totalGenerale);
			request.setAttribute("resultatPrimes", resultatPrimes);
			request.setAttribute("jsonPrimes", jsonPrimes);
			request.setAttribute("resultatVehicules", resultatVehicules);
			request.setAttribute("jsonVehicules", jsonVehicules);
			rd = getServletContext().getRequestDispatcher("/jsp/assureurResultatsAnalyse.jsp");
		}
		else
		{
			message = "aucun contrat sélectionné pour l'analyse, ";
			rd = getServletContext().getRequestDispatcher("/jsp/assureurAnalyse.jsp");
		}
		request.setAttribute("clientAAnalyser", clientAAnalyser);
		request.setAttribute("message", message);
		request.setAttribute("detailMessage", detailMessage);
		rd.forward(request, response);
	}

	private String[] getRecap(String codeContrat, Client clientAAnalyser) throws ParseException
	{
		String[] recap = new String[4];
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

		recap[3] = contrat.getBrancheProduit();
		return recap;
	}

	private String[] analysePrimes(int primeN3, int primeN2, int primeN1, int primeN, String[] totalGenerale)
	{
		String[] resultat = new String[12];
		String charValeurIgnoree = "-";

		resultat[0] = String.valueOf(primeN3);
		if (primeN3 != 0 && !"0".equals(totalGenerale[1]) && !"-".equals(totalGenerale[1]))
		{
			resultat[1] = doubleFormateurArrondi(Double.parseDouble(totalGenerale[1]) / primeN3);
		}
		else
		{
			resultat[1] = "0";
		}

		resultat[2] = String.valueOf(primeN2);
		if (primeN2 != 0 && !"0".equals(totalGenerale[3]) && !"-".equals(totalGenerale[3]))
		{
			resultat[3] = doubleFormateurArrondi(Double.parseDouble(totalGenerale[3]) / primeN2);
		}
		else
		{
			resultat[3] = "0";
		}

		resultat[4] = String.valueOf(primeN1);
		if (primeN1 != 0 && !"0".equals(totalGenerale[5]) && !"-".equals(totalGenerale[5]))
		{
			resultat[5] = doubleFormateurArrondi(Double.parseDouble(totalGenerale[5]) / primeN1);
		}
		else
		{
			resultat[5] = "0";
		}

		resultat[6] = String.valueOf(primeN);
		if (primeN != 0 && !"0".equals(totalGenerale[7]) && !"-".equals(totalGenerale[7]))
		{
			resultat[7] = doubleFormateurArrondi(Double.parseDouble(totalGenerale[7]) / primeN);
		}
		else
		{
			resultat[7] = "0";
		}

		resultat[8] = String.valueOf(Integer.parseInt(resultat[0]) + Integer.parseInt(resultat[2])
				+ Integer.parseInt(resultat[4]) + Integer.parseInt(resultat[6]));
		if (!"0".equals(resultat[8]) && !"-".equals(resultat[8]) && !"0".equals(totalGenerale[9])
				&& !"-".equals(totalGenerale[9]))
		{
			resultat[9] = doubleFormateurArrondi(Double.parseDouble(totalGenerale[9]) / Integer.parseInt(resultat[8]));
		}
		else
		{
			resultat[9] = "0";
		}

		return resultat;
	}

	private String[] analyseVehicules(int vehiculeN3, int vehiculeN2, int vehiculeN1, int vehiculeN,
			String[] totalGenerale)
	{
		String[] resultat = new String[12];
		String charValeurIgnoree = "-";

		resultat[0] = String.valueOf(vehiculeN3);
		if (vehiculeN3 != 0 && !"0".equals(totalGenerale[0]) && !"-".equals(totalGenerale[0]))
		{
			resultat[1] = doubleFormateurArrondi(Double.parseDouble(totalGenerale[0]) / vehiculeN3);
		}
		else
		{
			resultat[1] = "0";
		}

		resultat[2] = String.valueOf(vehiculeN2);
		if (vehiculeN2 != 0 && !"0".equals(totalGenerale[2]) && !"-".equals(totalGenerale[2]))
		{
			resultat[3] = doubleFormateurArrondi(Double.parseDouble(totalGenerale[2]) / vehiculeN2);
		}
		else
		{
			resultat[3] = "0";
		}

		resultat[4] = String.valueOf(vehiculeN1);
		if (vehiculeN1 != 0 && !"0".equals(totalGenerale[4]) && !"-".equals(totalGenerale[4]))
		{
			resultat[5] = doubleFormateurArrondi(Double.parseDouble(totalGenerale[4]) / vehiculeN1);
		}
		else
		{
			resultat[5] = "0";
		}

		resultat[6] = String.valueOf(vehiculeN);
		if (vehiculeN != 0 && !"0".equals(totalGenerale[6]) && !"-".equals(totalGenerale[6]))
		{
			resultat[7] = doubleFormateurArrondi(Double.parseDouble(totalGenerale[6]) / vehiculeN);
		}
		else
		{
			resultat[7] = "0";
		}

		resultat[8] = String.valueOf(Integer.parseInt(resultat[0]) + Integer.parseInt(resultat[2])
				+ Integer.parseInt(resultat[4]) + Integer.parseInt(resultat[6]));
		if (!"0".equals(resultat[8]) && !"-".equals(resultat[8]) && !"0".equals(totalGenerale[8])
				&& !"-".equals(totalGenerale[8]))
		{
			resultat[9] = doubleFormateurArrondi(Double.parseDouble(totalGenerale[8]) / Integer.parseInt(resultat[8]));
		}
		else
		{
			resultat[9] = "0";
		}

		return resultat;
	}

	private String[] analyseTotalAssureur(ArrayList<Sinistre> listeSinistres, HashMap<String, String> listeAnneesTecn)
			throws ParseException
	{
		String[] resultat = new String[12];
		String charValeurIgnoree = "-";
		int totalNombreN = 0;
		int totalCoutN = 0;
		int totalNombreN1 = 0;
		int totalCoutN1 = 0;
		int totalNombreN2 = 0;
		int totalCoutN2 = 0;
		int totalNombreN3 = 0;
		int totalCoutN3 = 0;
		int nombreN = 0;
		int coutN = 0;
		int nombreN1 = 0;
		int coutN1 = 0;
		int nombreN2 = 0;
		int coutN2 = 0;
		int nombreN3 = 0;
		int coutN3 = 0;
		String anneeTechnDepart = "N3";

		for (Sinistre sinistre : listeSinistres)
		{
			String anneeTechn = DateHelper.getAnneeTechn(listeAnneesTecn, sinistre.getDateSurvenance());
			// si le code nature est le code en cour d'analyse et que la
			// date de survenance est dans la fourchette de l'analyse
			switch (anneeTechn)
			{
				case "N":
					if (sinistre.getTauxResponsabilite()!=0)
					{
						nombreN++;
					}				
					coutN = coutN + sinistre.getCoutSinistre();
					break;

				case "N1":
					if (sinistre.getTauxResponsabilite()!=0)
					{
						nombreN1++;
					}					
					coutN1 = coutN1 + sinistre.getCoutSinistre();
					break;

				case "N2":
					if (sinistre.getTauxResponsabilite()!=0)
					{
						nombreN2++;
					}			
					coutN2 = coutN2 + sinistre.getCoutSinistre();
					break;

				case "N3":
					if (sinistre.getTauxResponsabilite()!=0)
					{
						nombreN3++;
					}				
					coutN3 = coutN3 + sinistre.getCoutSinistre();
					break;
			}
		}

		if ("N3".equals(anneeTechnDepart))
		{
			resultat[0] = String.valueOf(nombreN3);
			resultat[1] = String.valueOf(coutN3);
		}
		else
		{
			nombreN3 = 0;
			coutN3 = 0;
			resultat[0] = charValeurIgnoree;
			resultat[1] = charValeurIgnoree;
		}

		if ("N2".equals(anneeTechnDepart) || "N3".equals(anneeTechnDepart))
		{
			resultat[2] = String.valueOf(nombreN2);
			resultat[3] = String.valueOf(coutN2);
		}
		else
		{
			nombreN2 = 0;
			coutN2 = 0;
			resultat[2] = charValeurIgnoree;
			resultat[3] = charValeurIgnoree;
		}

		if ("N1".equals(anneeTechnDepart) || "N2".equals(anneeTechnDepart) || "N3".equals(anneeTechnDepart))
		{
			resultat[4] = String.valueOf(nombreN1);
			resultat[5] = String.valueOf(coutN1);
		}
		else
		{
			nombreN1 = 0;
			coutN1 = 0;
			resultat[4] = charValeurIgnoree;
			resultat[5] = charValeurIgnoree;
		}

		resultat[6] = String.valueOf(nombreN);
		resultat[7] = String.valueOf(coutN);

		resultat[8] = String.valueOf(nombreN + nombreN1 + nombreN2 + nombreN3);
		resultat[9] = String.valueOf(coutN + coutN1 + coutN2 + coutN3);
		resultat[10] = String.valueOf(nombreN1 + nombreN2 + nombreN3);
		resultat[11] = String.valueOf(coutN1 + coutN2 + coutN3);

		return resultat;
	}

	/**
	 * méthode permettant de préparer les données au format attendu par le
	 * columnChart
	 * 
	 * @param resultatNatureFlotte
	 * @param libelle
	 * @return String
	 */
	private String getJsonColumnChart(String[] resultat, String libelle, double seuil)
	{
		String json = "[['Années techniques','" + libelle + "','Seuil'],";
		json += "['N-3'," + resultat[1] + "," + seuil + "],";
		json += "['N-2'," + resultat[3] + "," + seuil + "],";
		json += "['N-1'," + resultat[5] + "," + seuil + "],";
		json += "['N'," + resultat[7] + "," + seuil + "],";
		json += "['TOTAL'," + resultat[9] + "," + seuil + "],";
		json += "]";
		return json;
	}

	private String doubleFormateurArrondi(double d)
	{
		String resultat = String.valueOf(d);
		String decimale = "";
		resultat = resultat.replace(".", ",");
		String[] tab = resultat.split(",");
		if (tab.length > 1)
		{
			if (tab[1].length() > 1)
			{
				decimale = tab[1].substring(0, 2);
			}
			else
			{
				decimale = tab[1].substring(0, 1);
			}

			resultat = tab[0] + "." + decimale;
		}
		else
		{
			resultat = tab[0];
		}

		return resultat;
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{
		doGet(request, response);
	}

}
