/**
 * 
 */
package fr.bellecour.statistiques.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import fr.bellecour.statistiques.bo.Produit;

/**
 * Classe permettant l'accès aux données concernant les produits
 * 
 * @author mrault
 *
 */
public class ProduitDao
{
	private static final String GET_ALL = "{call PRODUIT_GET_ALL()}";
	private static final String GET_BY_ID = "{call PRODUIT_GET_BY_ID(?)}";
	private static final String ADD = "{call PRODUIT_ADD(?,?,?)}";
	private static final String UPDATE = "{call PRODUIT_UPDATE(?,?,?)}";
	private static final String DELETE = "{call PRODUIT_DELETE(?)}";

	/**
	 * fonction permettant d'obtenir la liste des produits
	 * 
	 * @return ArrayList<Utilisateur>
	 * @throws Exception 
	 */
	public static ArrayList<Produit> getAll() throws Exception
	{
		ArrayList<Produit> produitList = new ArrayList<Produit>();
		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(GET_ALL);
			ResultSet rs = rqt.executeQuery();
			while (rs.next())
			{
				produitList.add(produitBuilder(rs));
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return produitList;
	}

	/**
	 * fonction permettant d'obtenir un produit à partir de son code roduit
	 * 
	 * @param codeProduit
	 * @return Produit (null si il n'existe pas)
	 * @throws Exception 
	 */
	public static Produit getById(String codeProduit) throws Exception
	{
		Produit produit = new Produit();
		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(GET_BY_ID);
			rqt.setString(1, codeProduit);

			ResultSet rs = rqt.executeQuery();
			if (rs.next())
			{
				produit = (produitBuilder(rs));
			}
			else
			{
				produit = null;
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			produit = null;
			throw e;
		}
		return produit;
	}

	/**
	 * fonction permettant d'ajouter un produit
	 * 
	 * @param produit
	 *            à ajouter
	 * @return boolean
	 * @throws Exception 
	 */
	public static boolean add(Produit nouveauProduit) throws Exception
	{
		boolean ajoutOk = false;

		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(ADD);

			rqt.setString(1, nouveauProduit.getCodeProduit());
			rqt.setString(2, nouveauProduit.getLibelle());
			rqt.setString(3, nouveauProduit.getBranche());

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
	 * fonction permettant de modifier un produit
	 * @param produit à modifier
	 * @return boolean
	 * @throws Exception 
	 */
	public static boolean update(Produit produitAModifier) throws Exception
	{
		boolean updateOk = false;

		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(UPDATE);

			rqt.setString(1, produitAModifier.getCodeProduit());
			rqt.setString(2, produitAModifier.getLibelle());
			rqt.setString(3, produitAModifier.getBranche());

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
	 * fonction permettant de supprimer un produit
	 * @param codeProduit
	 * @return boolean
	 * @throws Exception 
	 */
	public static boolean delete(String codeProduit) throws Exception
	{
		boolean suppressionOk = false;
		
		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(DELETE);

			rqt.setString(1, codeProduit);			
			
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
	
	/**
	 * fonction permettant de construire l'objet Produit à partir d'un resulSet
	 * @param resulSet
	 * @return Produit
	 * @throws Exception 
	 */
	private static Produit produitBuilder(ResultSet rs) throws Exception
	{
		Produit produit = new Produit();
		try
		{
			produit.setCodeProduit(rs.getString("codeProduit"));
			produit.setLibelle(rs.getString("libelle"));
			produit.setBranche(rs.getString("branche"));
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			produit = null;
			throw e;
		}
		return produit;
	}

	/**
	 * méthode permettant de vérifier si un produit existe
	 * @param codeProduit
	 * @param listeProduits
	 * @return boolean
	 */
	public static boolean produitExist(String codeProduit, ArrayList<Produit> listeProduits)
	{
		boolean exist = false;
		for (Produit produit : listeProduits)
		{
			if (codeProduit.equals(produit.getCodeProduit()))
			{
				exist = true;
			}
		}
		return exist;
	}

}
