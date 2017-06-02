/**
 * 
 */
package fr.bellecour.statistiques.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import fr.bellecour.statistiques.bo.Niveau;
import fr.bellecour.statistiques.bo.Utilisateur;

/**
 * Classe permettant l'accès aux données concernant les utilisateurs et leurs niveaux
 * @author mrault
 *
 */
public class UtilisateurDao
{
	
	private static final String GET_USER = "{call UTILISATEUR_GET(?,?)}";
	private static final String GET_USER_BY_ID = "{call UTILISATEUR_GET_BY_ID(?)}";
	private static final String GET_ALL_USER = "{call UTILISATEUR_GET_ALL()}";
	private static final String GET_ALL_LEVEL = "{call NIVEAU_GET_ALL()}";
	private static final String ADD_USER = "{call UTILISATEUR_ADD(?,?,?,?)}";
	private static final String UPDATE_USER = "{call UTILISATEUR_UPDATE(?,?,?,?,?)}";
	private static final String DELETE_USER = "{call UTILISATEUR_DELETE(?)}";
	
	/**
	 * fonction qui permettant d'authentifier un utilisateur
	 * @param email
	 * @param password
	 * @return Utilisateur (null si non authentifié)
	 * @throws Exception 
	 */
	public static Utilisateur getUser(String email, String password) throws Exception
	{
		Utilisateur user = new Utilisateur();
		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(GET_USER);
			rqt.setString(1, email);
			rqt.setString(2, password);
			ResultSet rs = rqt.executeQuery();
			if (rs.next()) 
			{
				user = (userBuilder(rs));
			}
			else
			{
				user=null;
			}		
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			user = null;
			throw e;		
		}
		return user;
	}

	/**
	 * fonction permettant d'obtenir un utilisateur à partir de son codeUtilisateur
	 * @param codeUtilisateur
	 * @return Utilisateur (null si il n'existe pas)
	 * @throws Exception 
	 */
	public static Utilisateur geUserById(int codeUtilisateur) throws Exception
	{
		Utilisateur user = new Utilisateur();
		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(GET_USER_BY_ID);
			rqt.setInt(1, codeUtilisateur);

			ResultSet rs = rqt.executeQuery();
			if (rs.next()) 
			{
				user = (userBuilder(rs));
			}
			else
			{
				user=null;
			}			
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			user = null;
			throw e;
		}
		return user;
	}
	
	/**
	 * fonction permettant d'obtenir la liste des utilisateurs
	 * @return ArrayList<Utilisateur>
	 * @throws Exception 
	 */
	public static ArrayList<Utilisateur> getAllUser() throws Exception
	{
		ArrayList<Utilisateur> userList = new ArrayList<Utilisateur>();
		try(Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(GET_ALL_USER);
			ResultSet rs = rqt.executeQuery();
			while (rs.next())
			{
				userList.add(userBuilder(rs));
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return userList;
	}
	
	/**
	 * fonction permettant d'obtenir la liste des niveaux de droits d'accès
	 * @return ArrayList<Niveau>
	 * @throws Exception 
	 */
	public static ArrayList<Niveau> getNiveaux() throws Exception
	{
		ArrayList<Niveau> levelList = new ArrayList<Niveau>();
		try(Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(GET_ALL_LEVEL);
			ResultSet rs = rqt.executeQuery();
			while (rs.next())
			{
				Niveau n = new Niveau(rs.getInt("codeNiveau"), rs.getString("descriptif"));
				levelList.add(n);
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return levelList;
	}

	/**
	 * fonction permettant d'ajouter un utilisateur
	 * @param utilisateur à ajouter
	 * @return boolean
	 * @throws Exception 
	 */
	public static boolean addUser(Utilisateur userNouveau) throws Exception
	{
		boolean ajoutOk = false;
		
		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(ADD_USER);
			
			rqt.setString(1,userNouveau.getNom() );
			rqt.setString(2, userNouveau.getEmail());
			rqt.setString(3, userNouveau.getPassword());
			rqt.setInt(4, userNouveau.getNiveau().getCodeNiveau());

			
			
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
	 * fonction permettant de modifier un utilisateur
	 * @param utilisateur à modifier
	 * @return boolean
	 * @throws Exception 
	 */
	public static boolean updateUser(Utilisateur userAModifier) throws Exception
	{
		boolean updateOk = false;
		
		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(UPDATE_USER);
			
			rqt.setString(1,userAModifier.getNom() );
			rqt.setString(2, userAModifier.getEmail());
			rqt.setString(3, userAModifier.getPassword());
			rqt.setInt(4, userAModifier.getNiveau().getCodeNiveau());
			rqt.setInt(5, userAModifier.getCodeUtilisateur());			
			
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
	 * fonction permettant de supprimer un utilisateur
	 * @param codeUtilisateur
	 * @return boolean
	 * @throws Exception 
	 */
	public static boolean deleteUser(int codeUtilisateur) throws Exception
	{
		boolean suppressionOk = false;
		
		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(DELETE_USER);

			rqt.setInt(1, codeUtilisateur);			
			
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
	 * fonction permettant de construire l'objet utilisateur à partir d'un resulSet
	 * @param resulSet
	 * @return Utilisateur
	 * @throws Exception 
	 */
	private static Utilisateur userBuilder(ResultSet rs) throws Exception
	{
		Utilisateur user = new Utilisateur();
		try 
		{
			user.setCodeUtilisateur(rs.getInt("codeUtilisateur"));
			user.setNom(rs.getString("nomUtilisateur"));
			user.setEmail(rs.getString("email"));
			user.setPassword(rs.getString("password"));
			user.setNiveau(new Niveau(rs.getInt("codeNiveau"), rs.getString("descriptif")));			
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			user = null;
			throw e;
		}
		return user;
	}


	
	
	

}
