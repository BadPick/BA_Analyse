/**
 * 
 */
package fr.bellecour.statistiques.bo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Classe définissant un client
 * 
 * @author mrault
 *
 */
public class Client
{
	private int codeClient;
	private String nom;
		
	private ArrayList<Contrat> listeContrats;

	public Client()
	{
		this.listeContrats = new ArrayList<Contrat>();
	}

	/**
	 * @return the codeClient
	 */
	public int getCodeClient()
	{
		return codeClient;
	}

	/**
	 * @param codeClient
	 *            the codeClient to set
	 */
	public void setCodeClient(int codeClient)
	{
		this.codeClient = codeClient;
	}

	/**
	 * @return the nom
	 */
	public String getNom()
	{
		return nom;
	}

	/**
	 * @param nom
	 *            the nom to set
	 * @throws Exception 
	 */
	public void setNom(String nom) throws Exception
	{
		if (nom.trim().length()>0)
		{
			this.nom = nom.trim();
		}
		else 
		{
			throw new Exception("le nom du client est obligatoire");
		}
		
	}

	/**
	 * @return the listeContrats
	 */
	public ArrayList<Contrat> getListeContrats()
	{
		return listeContrats;
	}

	/**
	 * @param listeContrats
	 *            the listeContrats to set
	 */
	public void setListeContrats(ArrayList<Contrat> listeContrats)
	{
		this.listeContrats = listeContrats;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Client [codeClient=" + codeClient + ", nom=" + nom + ", listeContrats=" + listeContrats + "]";
	}

}
