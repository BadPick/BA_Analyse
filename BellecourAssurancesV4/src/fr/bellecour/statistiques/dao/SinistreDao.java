/**
 * 
 */
package fr.bellecour.statistiques.dao;

import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;

import fr.bellecour.statistiques.bo.Circonstance;
import fr.bellecour.statistiques.bo.Contrat;
import fr.bellecour.statistiques.bo.Nature;
import fr.bellecour.statistiques.bo.Produit;
import fr.bellecour.statistiques.bo.Sinistre;
import fr.bellecour.statistiques.util.CsvHelper;
import fr.bellecour.statistiques.util.ErrorHelper;

/**
 * Classe permettant l'accès aux données concernant les sinistres
 * 
 * @author mrault
 *
 */
public class SinistreDao
{
	private static final String DELETE = "{call SINISTRE_DELETE(?)}";
	private static final String GET_BY_DATE_BY_CONTRAT = "{call SINISTRE_GET_BY_DATE_BY_CONTRAT(?,?,?)}";
	private static final String GET_ALL_BY_CONTRAT = "{call SINISTRE_GET_ALL_BY_CONTRAT(?)}";
	private static final String ADD = "{call SINISTRE_ADD(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	private static final String UPDATE = "{call SINISTRE_UPDATE(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	private static final String GET_RECHERCHE = "{call SINISTRE_RECHERCHE(?)}";
	private static final String GET_BY_ID = "{call SINISTRE_GET_BY_ID(?)}";
	private static final int NUM_COL_CODE_PRODUIT = 48;
	private static final int NUM_COL_CODE_SINISTRE = 10;
	private static final int NUM_COL_NOM_SALARIE = 11;
	private static final int NUM_COL_DATE_SURVENANCE = 8;
	private static final int NUM_COL_HEURE_SURVENANCE = 21;
	private static final int NUM_COL_IMMATRICULATION = 9;
	private static final int NUM_COL_NON_TIERS = 12;
	private static final int NUM_COL_TYPE_DOMMAGES = 13;
	private static final int NUM_COL_TAUX_RESP = 18;
	private static final int NUM_COL_CODE_CIRCONSTANCE = 19;
	private static final int NUM_COL_NATURE_DOMMAGES = 49;
	private static final int NUM_COL_NATURE_SINISTRE = 47;
	private static final int NUM_COL_AGGLO_HORS_AGGLO = 52;
	private static final int NUM_COL_TIERS_REGT_MAT = 22;
	private static final int NUM_COL_TIERS_REGT_CORP = 23;
	private static final int NUM_COL_TIERS_PROVISIONS = 32;
	private static final int NUM_COL_TIERS_RECOURS = 34;
	private static final int NUM_COL_ASSURE_REGT_MAT = 35;
	private static final int NUM_COL_ASSURE_REGT_CORP = 36;
	private static final int NUM_COL_ASSURE_PROVISIONS = 38;
	private static final int NUM_COL_ASSURE_FRANCHISE = 39;

	private static final int NUM_COL_CODE_CONTRAT = 44;
	private static final int NUM_COL_ETAT = 42;
	private static final int NUM_COL_OBSERVATIONS = 43;
	private static final int NUM_COL_LIEU = 51;
	private static final int NUM_COL_DONNEUR_ORDRE = 52;
	private static final int NUM_COL_POIDS = 53;
	private static final int NUM_COL_REF_COMPAGNIE = 54;

	private static int compteur;

	/**
	 * méthode permettant la mise à jour des sinistres
	 * 
	 * @param fileContent
	 *            le fichier à parcourir
	 * @return String message détaillé de l'import
	 * @throws Exception
	 */
	public static String majSinistres(InputStream fileContent) throws Exception
	{
		String message = "";
		int nbAjout = 0;
		int nbModif = 0;

		// récupération du csv d'import en liste de string[]
		ArrayList<String[]> liste = CsvHelper.getCSV(fileContent, 57);

		// convertion de la liste de string[] en liste d'objets sinistres
		ArrayList<Sinistre> listeSinistre = listeCSVtoListeSinistres(liste);

		nbAjout = 0;
		nbModif = 0;
		for (Sinistre sinistre : listeSinistre)
		{
			// delete sinistre
			int nbRow = delete(sinistre.getCodeSinistre());

			// insert sinistre
			boolean ajoutOk = add(sinistre);

			if (ajoutOk)
			{
				if (nbRow == 0)
				{
					nbAjout++;
				}
				else
				{
					nbModif++;
				}
			}
		}
		message += ", " + nbAjout + " nouveaux sinistres, " + nbModif + " remplacements";
		return message;
	}

	/**
	 * méthode permettant l'ajout de sinistres
	 * 
	 * @param sinistre
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean add(Sinistre sinistre) throws Exception
	{
		boolean ajoutOk = false;

		try (Connection cnx = BddAccess.getConnection())
		{

			CallableStatement rqt = cnx.prepareCall(ADD);

			rqt.setString(1, sinistre.getCodeSinistre());
			rqt.setDate(2, new java.sql.Date(sinistre.getDateSurvenance().getTime()));
			rqt.setString(3, sinistre.getHeureSurvenance());
			rqt.setString(4, sinistre.getTypeDommage());
			if (sinistre.getCirconstance() != null)
			{
				rqt.setString(5, sinistre.getCirconstance().getCodeCirconstance());
			}
			else
			{
				rqt.setNull(5, Types.VARCHAR);
			}

			rqt.setInt(6, sinistre.getTauxResponsabilite());
			rqt.setString(7, sinistre.getNomSalarie());
			rqt.setString(8, sinistre.getNomTiers());
			rqt.setString(9, sinistre.getImmatriculation());
			rqt.setInt(10, sinistre.getTiersReglementMat());
			rqt.setInt(11, sinistre.getTiersReglementCorp());
			rqt.setInt(12, sinistre.getTiersProvisions());
			rqt.setInt(13, sinistre.getTiersRecours());
			rqt.setInt(14, sinistre.getAssureReglementMat());
			rqt.setInt(15, sinistre.getAssureReglementCorp());
			rqt.setInt(16, sinistre.getAssureProvisions());
			rqt.setInt(17, sinistre.getAssureFranchise());
			rqt.setString(18, sinistre.getEtat());
			rqt.setString(19, sinistre.getObservation());
			rqt.setString(20, sinistre.getNatureDommage());
			if (sinistre.getNature() != null)
			{
				rqt.setString(21, sinistre.getNature().getCodeNature());
			}
			else
			{
				rqt.setNull(21, Types.VARCHAR);
			}
			rqt.setString(22, sinistre.getAggloHorsAgglo());
			rqt.setString(23, sinistre.getCodeContrat());
			rqt.setString(24, sinistre.getLieu());
			rqt.setString(25, sinistre.getPoids());
			rqt.setString(26, sinistre.getDonneurOrdre());
			rqt.setString(27, sinistre.getRefCompagnie());

			if (rqt.executeUpdate() == 1)
			{
				ajoutOk = true;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception("erreur dans le sinistre n° " + sinistre.getCodeSinistre() + " (n°police : " + sinistre.getCodeContrat() + ") : "
					+ ErrorHelper.traitementDuMessageDErreur(e));
		}
		return ajoutOk;
	}

	/**
	 * méthode permettant la supression d'un sinistre
	 * 
	 * @param codeSinistre
	 * @return int nombre de ligne affectées
	 * @throws Exception
	 */
	public static int delete(String codeSinistre) throws Exception
	{
		int resultat = 0;

		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(DELETE);
			rqt.setString(1, codeSinistre);
			resultat = rqt.executeUpdate();
		} catch (Exception e)
		{
			e.printStackTrace();
			System.out.println(codeSinistre);
			throw e;
		}
		return resultat;
	}

	/**
	 * méthode permettant de convertir la liste de tableau de string importée en
	 * liste d'objets sinistre
	 * 
	 * @param liste
	 *            de tableau de string importée
	 * @return ArrayList<Sinistre> liste de sinistres
	 * @throws Exception
	 */
	private static ArrayList<Sinistre> listeCSVtoListeSinistres(ArrayList<String[]> liste) throws Exception
	{
		// récupération de la liste des codes produits
		ArrayList<Produit> listeProduit = ProduitDao.getAll();

		ArrayList<Sinistre> listeSinistres = new ArrayList<Sinistre>();
		compteur = 1;
		try
		{
			for (String[] tab : liste)
			{
				// si le code produit exist
				if (ProduitDao.produitExist(tab[NUM_COL_CODE_PRODUIT], listeProduit))
				{
					Sinistre nouveauSinistre = new Sinistre();

					nouveauSinistre.setAssureFranchise(tab[NUM_COL_ASSURE_FRANCHISE]);
					nouveauSinistre.setAssureProvisions(tab[NUM_COL_ASSURE_PROVISIONS]);
					nouveauSinistre.setAssureReglementCorp(tab[NUM_COL_ASSURE_REGT_CORP]);
					nouveauSinistre.setAssureReglementMat(tab[NUM_COL_ASSURE_REGT_MAT]);
					if (tab[NUM_COL_CODE_CIRCONSTANCE] != null && !"".equals(tab[NUM_COL_CODE_CIRCONSTANCE]))
					{
						nouveauSinistre.setCirconstance(new Circonstance(tab[NUM_COL_CODE_CIRCONSTANCE]));
					}
					else
					{
						nouveauSinistre.setCirconstance(null);
					}
					nouveauSinistre.setCodeSinistre(tab[NUM_COL_CODE_SINISTRE]);
					nouveauSinistre.setDateSurvenance(tab[NUM_COL_DATE_SURVENANCE]);
					if (tab[NUM_COL_HEURE_SURVENANCE] != null && !"".equals(tab[NUM_COL_HEURE_SURVENANCE]))
					{
						nouveauSinistre.setHeureSurvenanceString(tab[NUM_COL_HEURE_SURVENANCE]);
					}
					else
					{
						nouveauSinistre.setHeureSurvenanceString(null);
					}
					if (tab[NUM_COL_IMMATRICULATION] != null && !"".equals(tab[NUM_COL_IMMATRICULATION]))
					{
						nouveauSinistre.setImmatriculation(tab[NUM_COL_IMMATRICULATION]);
					}
					else
					{
						nouveauSinistre.setImmatriculation(null);
					}
					if (tab[NUM_COL_NOM_SALARIE] != null && !"".equals(tab[NUM_COL_NOM_SALARIE]))
					{
						nouveauSinistre.setNomSalarie(tab[NUM_COL_NOM_SALARIE]);
					}
					else
					{
						nouveauSinistre.setNomSalarie(null);
					}
					if (tab[NUM_COL_NON_TIERS] != null && !"".equals(tab[NUM_COL_NON_TIERS]))
					{
						nouveauSinistre.setNomTiers(tab[NUM_COL_NON_TIERS]);
					}
					else
					{
						nouveauSinistre.setNomTiers(null);
					}
					nouveauSinistre.setTauxResponsabilite(tab[NUM_COL_TAUX_RESP]);
					nouveauSinistre.setTiersProvisions(tab[NUM_COL_TIERS_PROVISIONS]);
					nouveauSinistre.setTiersRecours(tab[NUM_COL_TIERS_RECOURS]);
					nouveauSinistre.setTiersReglementCorp(tab[NUM_COL_TIERS_REGT_CORP]);
					nouveauSinistre.setTiersReglementMat(tab[NUM_COL_TIERS_REGT_MAT]);
					nouveauSinistre.setTypeDommage(tab[NUM_COL_TYPE_DOMMAGES]);
					if (tab[NUM_COL_NATURE_DOMMAGES] != null && !"".equals(tab[NUM_COL_NATURE_DOMMAGES]))
					{
						nouveauSinistre.setNatureDommage(tab[NUM_COL_NATURE_DOMMAGES]);
					}
					else
					{
						nouveauSinistre.setNatureDommage(null);
					}
					if (tab[NUM_COL_NATURE_SINISTRE] != null && !"".equals(tab[NUM_COL_NATURE_SINISTRE]))
					{
						nouveauSinistre.setNature(new Nature(tab[NUM_COL_NATURE_SINISTRE]));
					}
					else
					{
						nouveauSinistre.setNature(null);
					}
					if (tab[NUM_COL_AGGLO_HORS_AGGLO] != null && !"".equals(tab[NUM_COL_AGGLO_HORS_AGGLO]))
					{
						nouveauSinistre.setAggloHorsAgglo(tab[NUM_COL_AGGLO_HORS_AGGLO]);
					}
					else
					{
						nouveauSinistre.setAggloHorsAgglo(null);
					}
					nouveauSinistre.setCodeContrat(tab[NUM_COL_CODE_CONTRAT]);
					if (tab[NUM_COL_ETAT] != null && !"".equals(tab[NUM_COL_ETAT]))
					{
						nouveauSinistre.setEtat(tab[NUM_COL_ETAT]);
					}
					else
					{
						nouveauSinistre.setEtat(null);
					}
					if (tab[NUM_COL_OBSERVATIONS] != null && !"".equals(tab[NUM_COL_OBSERVATIONS]))
					{
						nouveauSinistre.setObservation(tab[NUM_COL_OBSERVATIONS]);
					}
					else
					{
						nouveauSinistre.setObservation(null);
					}

					if (tab[NUM_COL_LIEU] != null && !"".equals(tab[NUM_COL_LIEU]))
					{
						nouveauSinistre.setLieu(tab[NUM_COL_LIEU]);
					}
					else
					{
						nouveauSinistre.setLieu(null);
					}
					if (tab[NUM_COL_DONNEUR_ORDRE] != null && !"".equals(tab[NUM_COL_DONNEUR_ORDRE]))
					{
						nouveauSinistre.setDonneurOrdre(tab[NUM_COL_DONNEUR_ORDRE]);
					}
					else
					{
						nouveauSinistre.setDonneurOrdre(null);
					}
					if (tab[NUM_COL_POIDS] != null && !"".equals(tab[NUM_COL_POIDS]))
					{
						nouveauSinistre.setPoids(tab[NUM_COL_POIDS]);
					}
					else
					{
						nouveauSinistre.setPoids(null);
					}
					if (tab[NUM_COL_REF_COMPAGNIE] != null && !tab[NUM_COL_REF_COMPAGNIE].isEmpty())
					{
						nouveauSinistre.setRefCompagnie(tab[NUM_COL_REF_COMPAGNIE]);
					}
					else
					{
						nouveauSinistre.setRefCompagnie(null);
					}

					listeSinistres.add(nouveauSinistre);
				}
				compteur++;
			}
		} catch (Exception e)
		{
			throw new Exception("erreur dans l'import à la ligne " + compteur + ", "
					+ ErrorHelper.traitementDuMessageDErreur(e));
		}

		return listeSinistres;
	}

	/**
	 * méthode permettant d'obtenir la liste des sinistres pour un contrat
	 * 
	 * @param codeContrat
	 * @return ArrayList<Sinistre>
	 * @throws Exception
	 */
	public static ArrayList<Sinistre> getSinistresPourAnalyse(String codeContrat, Date dateDebut, Date dateFin)
			throws Exception
	{
		ArrayList<Sinistre> listeSinistres = new ArrayList<Sinistre>();

		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(GET_BY_DATE_BY_CONTRAT);

			rqt.setString(1, codeContrat);
			rqt.setDate(2, new java.sql.Date(dateDebut.getTime()));
			rqt.setDate(3, new java.sql.Date(dateFin.getTime()));

			ResultSet rs = rqt.executeQuery();

			while (rs.next())
			{
				listeSinistres.add(sinistreBuilder(rs));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return listeSinistres;
	}

	/**
	 * méthode permettant de construire un sinistre à partir d'un ResultSet
	 * 
	 * @param ResultSet
	 * @return Sinistre
	 * @throws Exception
	 */
	private static Sinistre sinistreBuilder(ResultSet rs) throws Exception
	{
		Sinistre sinistre = new Sinistre();
		try
		{
			sinistre.setAggloHorsAgglo(rs.getString("aggloHorsAgglo"));
			sinistre.setAssureFranchise(rs.getInt("assureFranchise"));
			sinistre.setAssureProvisions(rs.getInt("assureProvisions"));
			sinistre.setAssureReglementCorp(rs.getInt("assureReglementCorp"));
			sinistre.setAssureReglementMat(rs.getInt("assureReglementMat"));
			sinistre.setCodeContrat(rs.getString("codeContrat"));
			sinistre.setCodeSinistre(rs.getString("codeSinistre"));
			sinistre.setDateSurvenance(new java.util.Date(rs.getDate("dateSurvenance").getTime()));
			sinistre.setEtat(rs.getString("etat"));
			sinistre.setHeureSurvenance(rs.getString("heureSurvenance"));
			sinistre.setImmatriculation(rs.getString("immatriculation"));
			sinistre.setNomSalarie(rs.getString("nomSalarie"));
			sinistre.setNomTiers(rs.getString("nomTiers"));
			sinistre.setObservation(rs.getString("observations"));
			sinistre.setTauxResponsabilite(rs.getInt("tauxResponsabilite"));
			sinistre.setTiersProvisions(rs.getInt("tiersProvisions"));
			sinistre.setTiersRecours(rs.getInt("tiersRecours"));
			sinistre.setTiersReglementCorp(rs.getInt("tiersReglementCorp"));
			sinistre.setTiersReglementMat(rs.getInt("tiersReglementMat"));
			sinistre.setTypeDommage(rs.getString("typeDommage"));
			sinistre.setLieu(rs.getString("lieu"));
			sinistre.setPoids(rs.getString("poids"));
			sinistre.setDonneurOrdre(rs.getString("donneurOrdre"));
			sinistre.setRefCompagnie(rs.getString("referenceCompagnie"));
			sinistre.setNatureDommage(rs.getString("natureDommages"));

			Circonstance circonstance = new Circonstance();
			circonstance.setCodeCirconstance(rs.getString("codeCirconstance"));
			circonstance.setLibelle(rs.getString("libelleCirconstance"));
			circonstance.setIda(rs.getBoolean("ida"));
			sinistre.setCirconstance(circonstance);

			Nature nature = new Nature();
			nature.setCodeNature(rs.getString("codeNature"));
			nature.setCodeAnalyse(rs.getString("codeAnalyse"));
			nature.setLibelle(rs.getString("libelleNature"));
			sinistre.setNature(nature);
		} catch (Exception e)
		{
			e.printStackTrace();
			sinistre = null;
			throw e;
		}
		return sinistre;
	}

	/**
	 * méthode permettant d'obtenir la liste des codes sinistres correspondant
	 * aux caratères de la recherche
	 * 
	 * @param param
	 * @return ArrayList<String>
	 * @throws Exception
	 */
	public static ArrayList<String> getListeRecherche(String param) throws Exception
	{
		ArrayList<String> liste = new ArrayList<String>();

		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(GET_RECHERCHE);

			rqt.setString(1, param);

			ResultSet rs = rqt.executeQuery();

			while (rs.next())
			{
				liste.add(rs.getString("codeSinistre"));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return liste;
	}

	public static ArrayList<Sinistre> getAllByContrat(String codeContrat) throws Exception
	{
		ArrayList<Sinistre> listeSinistres = new ArrayList<Sinistre>();

		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(GET_ALL_BY_CONTRAT);

			rqt.setString(1, codeContrat);

			ResultSet rs = rqt.executeQuery();

			while (rs.next())
			{
				listeSinistres.add(sinistreBuilder(rs));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return listeSinistres;
	}


	/**
	 * méthode permettant d'obtenir un sinistre par son identifiant
	 * @param codeSinistre
	 * @return Sinistre
	 * @throws Exception
	 */
	public static Sinistre getById(String codeSinistre) throws Exception
	{
		Sinistre sinistre = null;
		try (Connection cnx = BddAccess.getConnection())
		{

			CallableStatement rqt = cnx.prepareCall(GET_BY_ID);

			rqt.setString(1, codeSinistre);

			ResultSet rs = rqt.executeQuery();

			while (rs.next())
			{
				sinistre = sinistreBuilder(rs);
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return sinistre;
	}

	public static boolean update(Sinistre sinistre)
	{
		boolean modifOk = false;

		try (Connection cnx = BddAccess.getConnection())
		{

			CallableStatement rqt = cnx.prepareCall(UPDATE);

			rqt.setString(1, sinistre.getCodeSinistre());
			rqt.setDate(2, new java.sql.Date(sinistre.getDateSurvenance().getTime()));
			rqt.setString(3, sinistre.getHeureSurvenance());
			rqt.setString(4, sinistre.getTypeDommage());
			if (sinistre.getCirconstance() != null && sinistre.getCirconstance().getCodeCirconstance()!=null && !sinistre.getCirconstance().getCodeCirconstance().isEmpty())
			{
				rqt.setString(5, sinistre.getCirconstance().getCodeCirconstance());
			}
			else
			{
				rqt.setNull(5, Types.VARCHAR);
			}

			rqt.setInt(6, sinistre.getTauxResponsabilite());
			rqt.setString(7, sinistre.getNomSalarie());
			rqt.setString(8, sinistre.getNomTiers());
			rqt.setString(9, sinistre.getImmatriculation());
			rqt.setInt(10, sinistre.getTiersReglementMat());
			rqt.setInt(11, sinistre.getTiersReglementCorp());
			rqt.setInt(12, sinistre.getTiersProvisions());
			rqt.setInt(13, sinistre.getTiersRecours());
			rqt.setInt(14, sinistre.getAssureReglementMat());
			rqt.setInt(15, sinistre.getAssureReglementCorp());
			rqt.setInt(16, sinistre.getAssureProvisions());
			rqt.setInt(17, sinistre.getAssureFranchise());
			rqt.setString(18, sinistre.getEtat());
			rqt.setString(19, sinistre.getObservation());
			rqt.setString(20, sinistre.getNatureDommage());
			if (sinistre.getNature() != null && sinistre.getNature().getCodeNature()!=null && !sinistre.getNature().getCodeNature().isEmpty() )
			{
				rqt.setString(21, sinistre.getNature().getCodeNature());
			}
			else
			{
				rqt.setNull(21, Types.VARCHAR);
			}
			rqt.setString(22, sinistre.getAggloHorsAgglo());
			rqt.setString(23, sinistre.getCodeContrat());
			rqt.setString(24, sinistre.getLieu());
			rqt.setString(25, sinistre.getPoids());
			rqt.setString(26, sinistre.getDonneurOrdre());
			rqt.setString(27, sinistre.getRefCompagnie());

			if (rqt.executeUpdate() == 1)
			{
				modifOk = true;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return modifOk;
	}

}
