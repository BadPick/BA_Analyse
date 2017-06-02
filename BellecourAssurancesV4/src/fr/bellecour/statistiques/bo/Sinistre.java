/**
 * 
 */
package fr.bellecour.statistiques.bo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import fr.bellecour.statistiques.util.CsvHelper;
import fr.bellecour.statistiques.util.DateHelper;

/**
 * Classe définissant un sinistre
 * @author mrault
 *
 */
public class Sinistre
{
	private String codeSinistre;
	private String nomSalarie;
	private Date dateSurvenance;
	private String heureSurvenance;
	private String immatriculation;
	private String nomTiers;
	private String typeDommage;
	private int tauxResponsabilite;
	private Circonstance circonstance;
	private String etat;
	private String observation;
	private String natureDommage;
	private Nature nature;
	private String aggloHorsAgglo;
	private String codeContrat;
	
	private int tiersReglementMat;
	private int tiersReglementCorp;
	private int tiersProvisions;
	private int tiersRecours;
	
	private int assureReglementMat;
	private int assureReglementCorp;
	private int assureProvisions;
	private int assureFranchise;
	
	private String lieu;
	private String poids;
	private String donneurOrdre;
	private String refCompagnie;
	
	public Sinistre()
	{
		
	}
	
	
	/**
	 * @return the lieu
	 */
	public String getLieu()
	{
		return lieu;
	}


	/**
	 * @param lieu the lieu to set
	 */
	public void setLieu(String lieu)
	{
		this.lieu = lieu;
	}


	/**
	 * @return the poids
	 */
	public String getPoids()
	{
		return poids;
	}


	/**
	 * @param poids the poids to set
	 */
	public void setPoids(String poids)
	{
		this.poids = poids;
	}


	/**
	 * @return the donneurOrdre
	 */
	public String getDonneurOrdre()
	{
		return donneurOrdre;
	}


	/**
	 * @param donneurOrdre the donneurOrdre to set
	 */
	public void setDonneurOrdre(String donneurOrdre)
	{
		this.donneurOrdre = donneurOrdre;
	}


	/**
	 * @return the refCompagnie
	 */
	public String getRefCompagnie()
	{
		return refCompagnie;
	}


	/**
	 * @param refCompagnie the refCompagnie to set
	 */
	public void setRefCompagnie(String refCompagnie)
	{
		this.refCompagnie = refCompagnie;
	}


	/**
	 * @return the nature
	 */
	public Nature getNature()
	{
		return nature;
	}
	/**
	 * @param nature the nature to set
	 */
	public void setNature(Nature nature)
	{
		this.nature = nature;
	}
	/**
	 * @return the codeContrat
	 */
	public String getCodeContrat()
	{
		return codeContrat;
	}
	/**
	 * @param codeContrat the codeContrat to set
	 */
	public void setCodeContrat(String codeContrat)
	{
		this.codeContrat = codeContrat;
	}
	/**
	 * @param heureSurvenance the heureSurvenance to set
	 */
	public void setHeureSurvenance(String heureSurvenance)
	{
		this.heureSurvenance = heureSurvenance;
	}
	/**
	 * @return the aggloHorsAgglo
	 */
	public String getAggloHorsAgglo()
	{
		return aggloHorsAgglo;
	}
	/**
	 * @param aggloHorsAgglo the aggloHorsAgglo to set
	 */
	public void setAggloHorsAgglo(String aggloHorsAgglo)
	{
		this.aggloHorsAgglo = aggloHorsAgglo;
	}
	/**
	 * @return the natureDommage
	 */
	public String getNatureDommage()
	{
		return natureDommage;
	}
	/**
	 * @param natureDommage the natureDommage to set
	 */
	public void setNatureDommage(String natureDommage)
	{
		this.natureDommage = natureDommage;
	}
	/**
	 * @param circonstance the circonstance to set
	 */
	public void setCirconstance(Circonstance circonstance)
	{
		this.circonstance = circonstance;
	}
	/**
	 * @return the etat
	 */
	public String getEtat()
	{
		return etat;
	}
	/**
	 * @param etat the etat to set
	 */
	public void setEtat(String etat)
	{
		this.etat = etat;
	}
	/**
	 * @return the observation
	 */
	public String getObservation()
	{
		return observation;
	}
	/**
	 * @param observation the observation to set
	 */
	public void setObservation(String observation)
	{
		this.observation = observation;
	}
	/**
	 * @return the codeSinistre
	 */
	public String getCodeSinistre()
	{
		return codeSinistre;
	}
	/**
	 * @param codeSinistre the codeSinistre to set
	 */
	public void setCodeSinistre(String codeSinistre)
	{
		this.codeSinistre = codeSinistre;
	}
	/**
	 * @return the nomSalarie
	 */
	public String getNomSalarie()
	{
		return nomSalarie;
	}
	/**
	 * @param nomSalarie the nomSalarie to set
	 */
	public void setNomSalarie(String nomSalarie)
	{
		this.nomSalarie = nomSalarie;
	}
	/**
	 * @return the dateSurvenance
	 */
	public Date getDateSurvenance()
	{
		return dateSurvenance;
	}
	/**
	 * @return the dateSurvenance formater "dd/mm/yyyy"
	 */
	public String getDateSurvenanceString()
	{
		SimpleDateFormat formaterDate = new SimpleDateFormat("dd/MM/yyyy");
		return formaterDate.format(this.dateSurvenance);
	}
	/**
	 * @param Date dateSurvenance the dateSurvenance to set
	 */
	public void setDateSurvenance(Date dateSurvenance)
	{
		this.dateSurvenance = dateSurvenance;
	}
	/**
	 * @param String dateSurvenance the dateSurvenance to set
	 */
	public void setDateSurvenance(String dateSurvenance) throws Exception
	{		
		try
		{
			SimpleDateFormat formaterDate = new SimpleDateFormat("dd/MM/yyyy");
			this.dateSurvenance = formaterDate.parse(dateSurvenance);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			throw new Exception("erreur dans le format de la date de survenance");
		}
	}	
	/**
	 * @return Date the heureSurvenance
	 * @throws ParseException 
	 */
	public Date getHeureSurvenanceDate() throws ParseException
	{
		if (this.heureSurvenance != null)
		{
			SimpleDateFormat formaterHeure = new SimpleDateFormat("hh:mm");
			return formaterHeure.parse(this.heureSurvenance);
		}
		else
		{return null;}
	}
	/**
	 * @return String the heureSurvenance
	 */
	public String getHeureSurvenance()
	{
		return this.heureSurvenance;
	}
	/**
	 * @param heureSurvenance the heureSurvenance to set
	 * @throws Exception 
	 */
	public void setHeureSurvenance(Date heureSurvenance) throws Exception
	{	
			try
			{
				SimpleDateFormat formaterHeure = new SimpleDateFormat("hh:mm");
				this.heureSurvenance = formaterHeure.format(heureSurvenance);
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				throw new Exception("erreur dans le format de l'heure de survenance");			
			}	
	}
	/**
	 * @param heureSurvenance the heureSurvenance to set
	 * @throws Exception 
	 */
	public void setHeureSurvenanceString(String heureSurvenance)
	{
		this.heureSurvenance = heureSurvenance;
	}
	/**
	 * @return the immatriculation
	 */
	public String getImmatriculation()
	{
		return immatriculation;
	}
	/**
	 * @param immatriculation the immatriculation to set
	 */
	public void setImmatriculation(String immatriculation)
	{
		this.immatriculation = immatriculation;
	}
	/**
	 * @return the nomTiers
	 */
	public String getNomTiers()
	{
		return nomTiers;
	}
	/**
	 * @param nomTiers the nomTiers to set
	 */
	public void setNomTiers(String nomTiers)
	{
		this.nomTiers = nomTiers;
	}
	/**
	 * @return the typeDommage
	 */
	public String getTypeDommage()
	{
		return typeDommage;
	}
	/**
	 * @param typeDommage the typeDommage to set
	 */
	public void setTypeDommage(String typeDommage)
	{
		this.typeDommage = typeDommage;
	}
	/**
	 * @return the tauxResponsabilite
	 */
	public int getTauxResponsabilite()
	{
		return tauxResponsabilite;
	}
	/**
	 * @param tauxResponsabilite the tauxResponsabilite to set
	 */
	public void setTauxResponsabilite(int tauxResponsabilite)
	{
		this.tauxResponsabilite = tauxResponsabilite;
	}
	/**
	 * @param tauxResponsabilite the tauxResponsabilite to set
	 * @throws Exception 
	 */
	public void setTauxResponsabilite(String tauxResponsabilite) throws Exception
	{
		try
		{
			this.tauxResponsabilite = Integer.parseInt(tauxResponsabilite);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception("erreur dans le format du taux de responsabilité");
		}
		
	}
	/**
	 * @return the Circonstance
	 */
	public Circonstance getCirconstance()
	{
		return circonstance;
	}
	/**
	 * @return the tiersReglementMat
	 */
	public int getTiersReglementMat()
	{
		return tiersReglementMat;
	}
	/**
	 * @param tiersReglementMat the tiersReglementMat to set
	 */
	public void setTiersReglementMat(int tiersReglementMat)
	{
		this.tiersReglementMat = tiersReglementMat;
	}
	/**
	 * @param tiersReglementMat the tiersReglementMat to set
	 * @throws Exception 
	 */
	public void setTiersReglementMat(String tiersReglementMat) throws Exception
	{
		try
		{		
			this.tiersReglementMat = Integer.parseInt(doubleFormateurArrondi(Double.parseDouble(CsvHelper.formatForDoubleParse(tiersReglementMat))));
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception("erreur dans le format du tiers règlement MAT");
		}
	}
	/**
	 * @return the tiersReglementCorp
	 */
	public int getTiersReglementCorp()
	{
		return tiersReglementCorp;
	}
	/**
	 * @param tiersReglementCorp the tiersReglementCorp to set
	 */
	public void setTiersReglementCorp(int tiersReglementCorp)
	{
		this.tiersReglementCorp = tiersReglementCorp;
	}
	/**
	 * @param tiersReglementCorp the tiersReglementCorp to set
	 * @throws Exception 
	 */
	public void setTiersReglementCorp(String tiersReglementCorp) throws Exception
	{
		try
		{
			this.tiersReglementCorp = Integer.parseInt(doubleFormateurArrondi(Double.parseDouble(CsvHelper.formatForDoubleParse(tiersReglementCorp))));
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception("erreur dans le format du tiers règlement CORP");
		}
		
	}
	/**
	 * @return the tiersProvisions
	 */
	public int getTiersProvisions()
	{
		return tiersProvisions;
	}
	/**
	 * @param tiersProvisions the tiersProvisions to set
	 */
	public void setTiersProvisions(int tiersProvisions)
	{
		this.tiersProvisions = tiersProvisions;
	}
	/**
	 * @param tiersProvisions the tiersProvisions to set
	 * @throws Exception 
	 */
	public void setTiersProvisions(String tiersProvisions) throws Exception
	{
		try
		{
			this.tiersProvisions = Integer.parseInt(doubleFormateurArrondi(Double.parseDouble(CsvHelper.formatForDoubleParse(tiersProvisions))));
		} 
		catch (Exception e)
		{
			e.printStackTrace();
 			throw new Exception("erreur dans le format du tiers provisions");
		}
	}
	/**
	 * @return the tiersRecours
	 */
	public int getTiersRecours()
	{
		return tiersRecours;
	}
	/**
	 * @param tiersRecours the tiersRecours to set
	 */
	public void setTiersRecours(int tiersRecours)
	{
		this.tiersRecours = tiersRecours;
	}
	/**
	 * @param tiersRecours the tiersRecours to set
	 * @throws Exception 
	 */
	public void setTiersRecours(String tiersRecours) throws Exception
	{
		try
		{
			this.tiersRecours = Integer.parseInt(doubleFormateurArrondi(Double.parseDouble(CsvHelper.formatForDoubleParse(tiersRecours))));
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception("erreur dans le format du tiers recours");
		}	
	}
	/**
	 * @return the assureReglementMat
	 */
	public int getAssureReglementMat()
	{
		return assureReglementMat;
	}
	/**
	 * @param assureReglementMat the assureReglementMat to set
	 */
	public void setAssureReglementMat(int assureReglementMat)
	{
		this.assureReglementMat = assureReglementMat;
	}
	/**
	 * @param assureReglementMat the assureReglementMat to set
	 * @throws Exception 
	 */
	public void setAssureReglementMat(String assureReglementMat) throws Exception
	{
		try
		{
			this.assureReglementMat = Integer.parseInt(doubleFormateurArrondi(Double.parseDouble(CsvHelper.formatForDoubleParse(assureReglementMat))));
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception("erreur dans le format de l'assuré règlement MAT");
		}
	}
	/**
	 * @return the assureReglementCorp
	 */
	public int getAssureReglementCorp()
	{
		return assureReglementCorp;
	}
	/**
	 * @param assureReglementCorp the assureReglementCorp to set
	 */
	public void setAssureReglementCorp(int assureReglementCorp)
	{
		this.assureReglementCorp = assureReglementCorp;
	}
	/**
	 * @param assureReglementCorp the assureReglementCorp to set
	 * @throws Exception 
	 */
	public void setAssureReglementCorp(String assureReglementCorp) throws Exception
	{
		try
		{
			this.assureReglementCorp = Integer.parseInt(doubleFormateurArrondi(Double.parseDouble(CsvHelper.formatForDoubleParse(assureReglementCorp))));
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception("erreur dans le format de l'assuré règlement CORP");
		}
	}
	/**
	 * @return the assureProvisions
	 */
	public int getAssureProvisions()
	{
		return assureProvisions;
	}
	/**
	 * @param assureProvisions the assureProvisions to set
	 */
	public void setAssureProvisions(int assureProvisions)
	{
		this.assureProvisions = assureProvisions;
	}
	/**
	 * @param assureProvisions the assureProvisions to set
	 * @throws Exception 
	 */
	public void setAssureProvisions(String assureProvisions) throws Exception
	{
		try
		{
			this.assureProvisions = Integer.parseInt(doubleFormateurArrondi(Double.parseDouble(CsvHelper.formatForDoubleParse(assureProvisions))));
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception("erreur dans le format de l'assuré provisions");
		}
	}
	/**
	 * @return the assureFranchise
	 */
	public int getAssureFranchise()
	{
		return assureFranchise;
	}
	/**
	 * @param assureFranchise the assureFranchise to set
	 */
	public void setAssureFranchise(int assureFranchise)
	{
		this.assureFranchise = assureFranchise;
	}
	/**
	 * @param assureFranchise the assureFranchise to set
	 * @throws Exception 
	 */
	public void setAssureFranchise(String assureFranchise) throws Exception
	{
		try
		{
			this.assureFranchise = Integer.parseInt(doubleFormateurArrondi(Double.parseDouble(CsvHelper.formatForDoubleParse(assureFranchise))));
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception("erreur dans le format de l'assuré franchise");
		}
	}
	/**
	 * @return tiers règlement corp + mat
	 */
	public int getTiersReglementMatCorp()
	{
		return this.getTiersReglementCorp() + this.getTiersReglementMat();
	}
	/**
	 * @return tiers règlement corp + mat + provisions
	 */
	public int getTiersCout()
	{
		return this.getTiersReglementCorp() + this.getTiersReglementMat() + this.getTiersProvisions() + this.getTiersRecours();
	}
	/**
	 * @return assuré règlement corp + mat
	 */
	public int getAssureReglementMatCorp()
	{
		return this.getAssureReglementCorp() + this.getAssureReglementMat();
	} 
	/**
	 * @return assuré règlement corp + mat + provisions
	 */
	public int getAssureCout()
	{
		return this.getAssureReglementCorp() + this.getAssureReglementMat() + this.getAssureProvisions() - this.getAssureFranchise();
	}
	/**
	 * @return cout assuré total + cout tiers total
	 */
	public int getCoutSinistre()
	{
		return this.getAssureCout() + this.getTiersCout();
	}
	/**
	 * @return le libellé du jour de survenance
	 */
	public String getJourDeSurvenance()
	{
		SimpleDateFormat formaterJour = new SimpleDateFormat("EEEE");
		return formaterJour.format(this.getDateSurvenance());
	}
	/**
	 * @return le libellé du mois de survenance
	 */
	public String getMoisDeSurvenance()
	{
		SimpleDateFormat formaterMois = new SimpleDateFormat("MMMM");
		return formaterMois.format(this.getDateSurvenance());
	}
	/**
	 * 
	 * @param listeAnneesTechn
	 * @return le libellé de l'année technique concernée par le sinistre
	 * @throws ParseException
	 */
	public String getAnneeTechn(HashMap<String,String> listeAnneesTechn) throws ParseException
	{
		String anneeTechn = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		if (this.getDateSurvenance().after(sdf.parse(listeAnneesTechn.get("dateFinN1"))))
		{
			anneeTechn = "N";
		}
		else if (this.getDateSurvenance().after(sdf.parse(listeAnneesTechn.get("dateFinN2"))))
		{
			anneeTechn = "N1";
		}
		else if (this.getDateSurvenance().after(sdf.parse(listeAnneesTechn.get("dateFinN3"))))
		{
			anneeTechn = "N2";
		}
		else if (!this.getDateSurvenance().before(sdf.parse(listeAnneesTechn.get("dateDebutN3"))))
		{
			anneeTechn = "N3";
		}
		
		return anneeTechn;
	}
	
	private String doubleFormateurArrondi(double monDoubleAParser)
	{
		String resultat = String.valueOf(monDoubleAParser);

		resultat = resultat.replace(".", ",");
		String[] tab = resultat.split(",");

		return tab[0];
	}
	
	/*exemple comparator
	public static Comparator<Sinistre> nomSalarieComparator = new Comparator<Sinistre>() {
		public int compare(Sinistre s1, Sinistre s2) 
		{
		   String nomSalarie1 = s1.getNomSalarie().toUpperCase();
		   String nomSalarie2 = s2.getNomSalarie().toUpperCase();

		   //ordre croissant
		   return nomSalarie1.compareTo(nomSalarie2);
		}};*/


}
