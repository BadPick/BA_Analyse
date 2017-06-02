package fr.bellecour.statistiques.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import fr.bellecour.statistiques.bo.Circonstance;
import fr.bellecour.statistiques.bo.Nature;
import fr.bellecour.statistiques.bo.Produit;

public class NatureDao
{

	private static final String GET_ALL_UTIL = "{call NATURE_GET_ALL_UTIL(?)}";
	private static final String GET_ALL = "{call NATURE_GET_ALL()}";
	private static final String GET_ALL_CODE_ANALYSE = "{call NATURE_GET_ALL_CODE_ANALYSE(?)}";
	private static final String GET_BY_ID = "{call NATURE_GET_BY_ID(?)}";
	private static final String ADD = "{call NATURE_ADD(?,?,?,?)}";
	private static final String UPDATE = "{call NATURE_UPDATE(?,?,?,?)}";
	private static final String DELETE = "{call NATURE_DELETE(?)}";
	
	/**
	 * méthode permettant d'obtenir la liste de toutes les natures de sinistre
	 * @return ArrayList<Nature>
	 * @throws Exception
	 */
	public static ArrayList<Nature> getAll() throws Exception
	{
		ArrayList<Nature> liste = new ArrayList<Nature>();
		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(GET_ALL);
			ResultSet rs = rqt.executeQuery();
			while (rs.next())
			{
				liste.add(natureBuilder(rs));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return liste;
	}
	
	/**
	 * méthode permettant d'obtenir la liste de toutes les natures de sinistre ayant un code analyse
	 * pour une branche renseignée en paramètre
	 * @return ArrayList<Nature>
	 * @throws Exception
	 */
	public static ArrayList<Nature> getAllUtil(String branche) throws Exception
	{
		ArrayList<Nature> liste = new ArrayList<Nature>();
		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(GET_ALL_UTIL);
			rqt.setString(1, branche);
			ResultSet rs = rqt.executeQuery();
			while (rs.next())
			{
				liste.add(natureBuilder(rs));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return liste;
	}
	
	/**
	 * méthode permettant d'obtenir la liste de tous les codes analyse
	 * pour la branche renseignée en paramètre
	 * @return ArrayList<Nature>
	 * @throws Exception
	 */
	public static ArrayList<String> getAllCodeAnalyse(String branche) throws Exception
	{
		ArrayList<String> liste = new ArrayList<String>();
		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(GET_ALL_CODE_ANALYSE);
			rqt.setString(1, branche);
			ResultSet rs = rqt.executeQuery();
			while (rs.next())
			{
				liste.add(rs.getString("codeAnalyse"));
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return liste;
	}

	
	private static Nature natureBuilder(ResultSet rs) throws Exception
	{
		Nature nature = new Nature();
		try
		{
			nature.setCodeNature(rs.getString("codeNature"));
			nature.setCodeAnalyse(rs.getString("codeAnalyse"));
			nature.setLibelle(rs.getString("libelleNature"));
			nature.setBranche(rs.getString("branche"));
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			nature = null;
			throw e;
		}
		return nature;
	}

	/**
	 * fonction permettant d'ajouter une nature
	 * 
	 * @param nature à ajouter
	 * @return boolean
	 * @throws Exception 
	 */
	public static boolean add(Nature nouvelleNature) throws Exception
	{
		boolean ajoutOk = false;

		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(ADD);

			rqt.setString(1, nouvelleNature.getCodeNature());
			rqt.setString(2, nouvelleNature.getCodeAnalyse());
			rqt.setString(3, nouvelleNature.getLibelle());
			rqt.setString(4, nouvelleNature.getBranche());

			if (rqt.executeUpdate() == 1)
			{
				ajoutOk = true;
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}

		return ajoutOk;
	}

	/**
	 * fonction permettant de modifier une nature
	 * @param nature à modifier
	 * @return boolean
	 * @throws Exception 
	 */
	public static boolean update(Nature natureAModifier) throws Exception
	{
		boolean updateOk = false;

		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(UPDATE);

			rqt.setString(1, natureAModifier.getCodeNature());
			rqt.setString(2, natureAModifier.getCodeAnalyse());
			rqt.setString(3, natureAModifier.getLibelle());
			rqt.setString(4, natureAModifier.getBranche());
			
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

	public static Nature getById(String codeNature) throws Exception
	{
		Nature nature = new Nature();
		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(GET_BY_ID);
			rqt.setString(1, codeNature);

			ResultSet rs = rqt.executeQuery();
			if (rs.next())
			{
				nature = (natureBuilder(rs));
			}
			else
			{
				nature = null;
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			nature = null;
			throw e;
		}
		return nature;
	}

	public static boolean delete(String codeNature) throws Exception
	{
		boolean suppressionOk = false;
		
		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(DELETE);

			rqt.setString(1, codeNature);			
			
			if (rqt.executeUpdate() == 1)
			{
				suppressionOk = true;
			}		
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		
		return suppressionOk;
	}

	

}
