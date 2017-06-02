package fr.bellecour.statistiques.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;

import fr.bellecour.statistiques.bo.Contrat;

/**
 * Classe permettant l'acc�s aux donn�es concernant les contrats
 * @author mrault
 *
 */
public class ContratDao
{
	private static final String ADD = "{call CONTRAT_INSERT(?,?,?,?,?,?,?)}";
	private static final String UPDATE = "{call CONTRAT_UPDATE(?,?,?,?,?,?)}";
	private static final String GET_BY_ID = "{call CONTRAT_GET_BY_ID(?)}";
	private static final String GET_RECHERCHE = "{call CONTRAT_RECHERCHE(?)}";
	
	/**
	 * m�thode permettant d'ajouter un contrat
	 * @param codeClient
	 * @param contrat
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean addContrat(int codeClient, Contrat contrat) throws Exception
	{
		boolean ajoutOk = false;

		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(ADD);

			rqt.setString(1, contrat.getCodeContrat());
			rqt.setInt(2, codeClient);
			rqt.setString(3, contrat.getCodeProduit());
			rqt.setString(4, contrat.getAssureur());
			rqt.setDate(5, new java.sql.Date(contrat.getDateEffet().getTime()));
			if (contrat.getDateResiliation()!=null)
			{
				rqt.setDate(6, new java.sql.Date(contrat.getDateResiliation().getTime()));
			}
			else
			{
				rqt.setNull(6, Types.DATE);
			}
			rqt.setString(7, contrat.getDateEcheance());
			

			if (rqt.executeUpdate() == 1)
			{
				ajoutOk = true;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return ajoutOk;

	}

	/**
	 * m�thode permettant de construire un contrat � partir d'un resultSet
	 * @param ResultSet
	 * @return Contrat
	 * @throws Exception
	 */
	public static Contrat contratBuilder(ResultSet rs) throws Exception
	{
		Contrat contrat = new Contrat();
		try
		{
			contrat.setAssureur(rs.getString("assureur"));
			contrat.setCodeContrat(rs.getString("codePolice"));
			contrat.setCodeProduit(rs.getString("codeProduit"));
			contrat.setLibelleProduit(rs.getString("libelle"));
			contrat.setBrancheProduit(rs.getString("branche"));
			contrat.setDateEffet(new java.util.Date(rs.getDate("dateEffet").getTime()));
			contrat.setDateEcheance(rs.getString("dateEcheance"));
			if (rs.getDate("dateResiliation")!=null)
			{
				contrat.setDateResiliation(new java.util.Date(rs.getDate("dateResiliation").getTime()));
			}
			
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			contrat = null;
			throw e;
		}
		return contrat;
	}

	/**
	 * m�thode permettant d'obtenir un contrat par son identifiant
	 * @param codeContrat
	 * @return Contrat
	 * @throws Exception
	 */
	public static Contrat getById(String codeContrat) throws Exception
	{
		Contrat contrat = null;
		try (Connection cnx = BddAccess.getConnection())
		{

			CallableStatement rqt = cnx.prepareCall(GET_BY_ID);

			rqt.setString(1, codeContrat);

			ResultSet rs = rqt.executeQuery();

			while (rs.next())
			{
				contrat = contratBuilder(rs);
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return contrat;
	}

	/**
	 * m�thode permettant de modifier un contrat
	 * @param contrat � modifier
	 * @return Contrat
	 * @throws Exception
	 */
	public static boolean update(Contrat contratModif) throws Exception
	{
		boolean updateOk = false;

		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(UPDATE);

			rqt.setString(1, contratModif.getCodeContrat());
			rqt.setString(2, contratModif.getCodeProduit());
			rqt.setString(3, contratModif.getAssureur());
			rqt.setDate(4, new java.sql.Date(contratModif.getDateEffet().getTime()));
			if (contratModif.getDateResiliation()!=null)
			{
				rqt.setDate(5, new java.sql.Date(contratModif.getDateResiliation().getTime()));
			}
			else 
			{
				rqt.setNull(5, Types.DATE);
			}
			rqt.setString(6, contratModif.getDateEcheance());
			
			if (rqt.executeUpdate() == 1)
			{
				updateOk = true;
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}

		return updateOk;
	}

	/**
	 * m�thode permettant d'obtenir la liste des codes contrats correspondant aux carat�res de la recherche
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
				liste.add(rs.getString("codePolice"));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return liste;
	}
	


}
