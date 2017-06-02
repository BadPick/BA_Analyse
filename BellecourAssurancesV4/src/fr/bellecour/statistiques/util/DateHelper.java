/**
 * 
 */
package fr.bellecour.statistiques.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Classe fournissant des méthodes simplifiant l'utilisation des dates
 * 
 * @author mrault
 *
 */
public class DateHelper
{

	/**
	 * méthode permettant d'ajouter ou de soustraire des années à une date
	 * 
	 * @param date
	 * @param nbAnnees
	 * @return
	 * @throws ParseException
	 */
	public static String addYears(Date date, int nbAnnees) throws ParseException
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, nbAnnees);	
		SimpleDateFormat sdfDateComplete = new SimpleDateFormat("dd/MM/yyyy");
		String dateRetour = sdfDateComplete.format(cal.getTime());
		return dateRetour;
	}
	
	public static String addDays(Date date, int nbJours) throws ParseException
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, nbJours);	
		SimpleDateFormat sdfDateComplete = new SimpleDateFormat("dd/MM/yyyy");
		String dateRetour = sdfDateComplete.format(cal.getTime());
		return dateRetour;
	}

	public static HashMap<String, String> getAnneesTechniques(String dateEcheance) throws ParseException
	{
		String dateFinN;
		SimpleDateFormat sdf;
		if (dateEcheance.length()==3)
		{
			sdf = new SimpleDateFormat("Mdd");
		}
		else 
		{
			sdf = new SimpleDateFormat("MMdd");
		}
		SimpleDateFormat sdfFr = new SimpleDateFormat("dd/MM");
		Date dateEchFormat = sdf.parse(dateEcheance);
		String dateFr = sdfFr.format(dateEchFormat);

		SimpleDateFormat sdfAnnee = new SimpleDateFormat("yyyy");
		String annees = sdfAnnee.format(new Date());

		SimpleDateFormat sdfDateComplete = new SimpleDateFormat("dd/MM/yyyy");
		Date dateProchaineEcheance = sdfDateComplete.parse(dateFr + "/" + annees);

		if (dateProchaineEcheance.before(new Date()))
		{
			dateProchaineEcheance = sdfDateComplete.parse(addYears(dateProchaineEcheance, 1));
		}

			dateFinN = sdfDateComplete.format(sdfDateComplete.parse(addDays(dateProchaineEcheance, -1)));
			String dateDebutN = addYears(dateProchaineEcheance, -1);

			String dateFinN1 = addYears(sdfDateComplete.parse(addDays(dateProchaineEcheance, -1)), -1);
			String dateDebutN1 = addYears(dateProchaineEcheance, -2);

			String dateFinN2 = addYears(sdfDateComplete.parse(addDays(dateProchaineEcheance, -1)), -2);
			String dateDebutN2 = addYears(dateProchaineEcheance, -3);

			String dateFinN3 = addYears(sdfDateComplete.parse(addDays(dateProchaineEcheance, -1)), -3);
			String dateDebutN3 = addYears(dateProchaineEcheance, -4);
		


		

		HashMap<String, String> liste = new HashMap<String, String>();
		liste.put("dateFinN",dateFinN);
		liste.put("dateDebutN",dateDebutN);
		liste.put("dateFinN1",dateFinN1);
		liste.put("dateDebutN1",dateDebutN1);
		liste.put("dateFinN2",dateFinN2);
		liste.put("dateDebutN2",dateDebutN2);
		liste.put("dateFinN3",dateFinN3);
		liste.put("dateDebutN3",dateDebutN3);

		return liste;
	}
	
	public static String getAnneeTechn(HashMap<String,String> listeAnneesTechn, Date date) throws ParseException
	{
		String anneeTechn = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		if (date.after(sdf.parse(listeAnneesTechn.get("dateFinN1"))))
		{
			anneeTechn = "N";
		}
		else if (date.after(sdf.parse(listeAnneesTechn.get("dateFinN2"))))
		{
			anneeTechn = "N1";
		}
		else if (date.after(sdf.parse(listeAnneesTechn.get("dateFinN3"))))
		{
			anneeTechn = "N2";
		}
		else if (!date.before(sdf.parse(listeAnneesTechn.get("dateDebutN3")))) 
		{
			anneeTechn = "N3";
		}
		
		return anneeTechn;
	}

}
