package fr.bellecour.statistiques.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import fr.bellecour.statistiques.util.Settings;

/**
 * classe permettant de fournir une connexion à une base de données
 * @author mrault
 *
 */
public class BddAccess 
{
	static
	{
		try 
		{
			Class.forName(Settings.getPropriete("driver"));
		}
		catch (ClassNotFoundException e) 
		{			
			e.printStackTrace();
		}
	}
	
	/**
	 * méthode permettant d'obtenir une nouvelle connexion
	 * @return objet de connexion
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException
	{
		return DriverManager.getConnection("jdbc:mysql://localhost/BELLECOUR_STATS","root", "");
	}
}
