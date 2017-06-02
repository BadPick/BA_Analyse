/**
 * 
 */
package fr.bellecour.statistiques.util;

import java.io.IOException;
import java.util.Properties;


/**
 * classe permettant de lire le fichier de config.
 * @author mrault
 *
 */
public class Settings
{
private static Properties properties;
	
	/**
	 * initialiseur static permettant de d�finir le fichier "settings.properties" comme fichier de config
	 */
	static
	{		
		try 
		{
			properties = new Properties();
			properties.load(Settings.class.getResourceAsStream("settings.properties"));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * retourne la chaine de caract�re(ligne) � partir de la cl�(ex : "url=")
	 * @param String key
	 * @return String line
	 */
	public static String getPropriete(String key)
	{
		return properties.getProperty(key);
	}
}
