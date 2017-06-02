/**
 * 
 */
package fr.bellecour.statistiques.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

/**
 * Classe fournissant des méthodes simplifiant l'utilisation du format csv
 * @author mrault
 *
 */
public class CsvHelper
{
	private final static Logger LOGGER = Logger.getLogger(CsvHelper.class.getName());
	
	private static BufferedReader br = null;
	private static String line = "";
	private static String cvsSplitBy = ";";
	
	/**
	 * méthode permettant de parcourir un fichier csv et de le convertir en liste de tableau de string
	 * @param fileContent le fichier à parcourir
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<String[]> getCSV(InputStream fileContent) throws Exception
	{
		ArrayList<String[]> liste =  new ArrayList<String[]>();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(fileContent, "UTF8"));
		while ((line = reader.readLine()) != null) 
		{
			String[] tabLigne = line.split(cvsSplitBy);
			liste.add(tabLigne);
		}
		
		return liste;
	}
	/**
	 * méthode permettant de parcourir un fichier csv et de le convertir en liste de tableau de string
	 * en réspectant un nombre de colonne définit.(évite les problèmes de retours à la ligne intemédiaires)
	 * @param fileContent le fichier à parcourir
	 * @param numberOfColumns le nombre de colonne du fichier à parcourir
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<String[]> getCSV(InputStream fileContent, int numberOfColumns) throws Exception
	{
		String[] tabTemp = new String[numberOfColumns];
		String[] tabLigne = null;
		int compteur = 0;
		int nextPlace = 0;
		int nbLine = 0;
		int i = 0;
		ArrayList<String[]> liste =  new ArrayList<String[]>();	
		BufferedReader reader = new BufferedReader(new InputStreamReader(fileContent, "UTF8"));
	
		try
		{
			while ((line = reader.readLine()) != null) 
			{
				nbLine++;
				tabLigne = line.split(cvsSplitBy);

				if (tabLigne.length == numberOfColumns)
				{
					liste.add(tabLigne);
				}
				else
				{			
					compteur = 0;			
					for (String strings : tabLigne)
					{		
						if (nextPlace != 0 && compteur == 0)
						{
							String concat = tabTemp[nextPlace-1] + " " + strings;
							tabTemp[nextPlace-1] = concat.trim();
						}
						else 
						{
							tabTemp[nextPlace]=strings;
							nextPlace++;
						}	
						compteur++;
						
					}

					if (nextPlace == numberOfColumns)
					{
						liste.add(tabTemp);
						tabTemp = new String[numberOfColumns];
						nextPlace = 0;
					}
				}
			}
		} 
		catch (Exception e)
		{		
			e.printStackTrace();
			throw e;
		}
	
		return liste;
	}
	/**
	 * méthode permettant d'ouvrir un file chooser en java se
	 * @return File le fichier choisi
	 */
	public static File chooseFile()
	{
		File selectedFile = null;
		JFileChooser fileChooser = new JFileChooser();
		
		try
		{
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			int result = fileChooser.showOpenDialog(fileChooser);
			if (result == JFileChooser.APPROVE_OPTION) 
			{
			    selectedFile = fileChooser.getSelectedFile();
			    LOGGER.log(Level.INFO, "Selected file: " + selectedFile.getAbsolutePath());
			}	
		} 
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, e.toString(),e);
			throw e;
		}		
		return selectedFile;		
	}

	/**
	 * méthode permettant de parser un string en double
	 * @param stringToParse
	 * @return
	 */
	public static String formatForDoubleParse(String stringToParse)
	{
		String resultat = stringToParse.replace(" ", "");
		resultat = resultat.replace(",", ".");
		return resultat;
	}
}
