/**
 * 
 */
package fr.bellecour.statistiques.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import fr.bellecour.statistiques.bo.Circonstance;
import fr.bellecour.statistiques.bo.Client;
import fr.bellecour.statistiques.bo.Nature;
import fr.bellecour.statistiques.bo.Produit;

/**
 * Classe permettant l'accès aux données concernant les circonstances
 * 
 * @author mrault
 *
 */
public class CirconstanceDao
{

	private static final String GET_ALL = "{call CIRCONSTANCE_GET_ALL()}";
	private static final String GET_BY_ID = "{call CIRCONSTANCE_GET_BY_ID(?)}";
	private static final String DELETE = "{call CIRCONSTANCE_DELETE(?)}";
	private static final String ADD = "{call CIRCONSTANCE_ADD(?,?,?)}";
	private static final String UPDATE = "{call CIRCONSTANCE_UPDATE(?,?,?)}";
	
	/**
	 * méthode permettant d'obtenir la liste de toutes les circonstances
	 * @return ArrayList<Circonstance>
	 * @throws Exception
	 */
	public static ArrayList<Circonstance> getAll() throws Exception
	{
		ArrayList<Circonstance> liste = new ArrayList<Circonstance>();
		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(GET_ALL);
			ResultSet rs = rqt.executeQuery();
			while (rs.next())
			{
				liste.add(circonstanceBuilder(rs));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return liste;
	}

	/**
	 * méthode permettant de construire un objet circonstance à partir d'un resultSet
	 * @param ResultSet
	 * @return Circonstance
	 * @throws Exception
	 */
	private static Circonstance circonstanceBuilder(ResultSet rs) throws Exception
	{
		Circonstance circonstance = new Circonstance();
		try
		{
			circonstance.setCodeCirconstance(rs.getString("codeCirconstance"));
			circonstance.setLibelle(rs.getString("libelleCirconstance"));
			circonstance.setIda(rs.getBoolean("ida"));
		} catch (Exception e)
		{
			e.printStackTrace();
			circonstance = null;
			throw e;
		}
		return circonstance;
	}

	public static boolean add(Circonstance nouvelleCirconstance) throws Exception
	{
		boolean ajoutOk = false;

		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(ADD);

			rqt.setString(1, nouvelleCirconstance.getCodeCirconstance());
			rqt.setString(2, nouvelleCirconstance.getLibelle());
			rqt.setBoolean(3, nouvelleCirconstance.isIda());

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

	public static boolean update(Circonstance circonstanceAModifier) throws Exception
	{
		boolean updateOk = false;

		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(UPDATE);

			rqt.setString(1, circonstanceAModifier.getCodeCirconstance());
			rqt.setString(2, circonstanceAModifier.getLibelle());
			rqt.setBoolean(3, circonstanceAModifier.isIda());
			
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

	public static Circonstance getById(String codeCirconstance) throws Exception
	{
		Circonstance circonstance = new Circonstance();
		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(GET_BY_ID);
			rqt.setString(1, codeCirconstance);

			ResultSet rs = rqt.executeQuery();
			if (rs.next())
			{
				circonstance = (circonstanceBuilder(rs));
			}
			else
			{
				circonstance = null;
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			circonstance = null;
			throw e;
		}
		return circonstance;
	}

	public static boolean delete(String parameter)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
}
