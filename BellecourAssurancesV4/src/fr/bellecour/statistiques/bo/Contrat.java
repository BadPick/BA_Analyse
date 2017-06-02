/**
 * 
 */
package fr.bellecour.statistiques.bo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Classe définissant un contrat d'assurance
 * @author mrault
 *
 */
public class Contrat
{
	private String codeContrat;
	private String assureur;
	private String codeProduit;
	private String libelleProduit;
	private String brancheProduit;
	private Date dateEffet;
	private Date dateResiliation;
	private String dateEcheance;
	private ArrayList<Sinistre> listeSinistres;
	
	public Contrat()
	{
		this.listeSinistres = new ArrayList<Sinistre>();
	}
	/**
	 * @return the dateEcheanceString
	 * @throws ParseException 
	 */
	public String getDateEcheanceString() throws ParseException
	{
		SimpleDateFormat sdfConvert;
		if (this.dateEcheance.length()==3)
		{
			sdfConvert = new SimpleDateFormat("Mdd");
		}
		else 
		{
			sdfConvert = new SimpleDateFormat("MMdd");
		}
		SimpleDateFormat sdfEcheance = new SimpleDateFormat("dd-MMMM");	
		Date date = sdfConvert.parse(this.dateEcheance);
		String dateEcheanceString = sdfEcheance.format(date);
		return dateEcheanceString;
	}
	


	/**
	 * @return the dateEcheance
	 * @throws ParseException 
	 */
	public String getDateEcheance()
	{		
		return dateEcheance;
	}

	/**
	 * @param dateEcheance
	 *            the dateEcheance to set
	 * @throws Exception 
	 */
	public void setDateEcheance(String dateEcheance) throws Exception
	{
		if (dateEcheance.trim().length() <= 4)
		{
			try
			{
				int test = Integer.parseInt(dateEcheance);

			} 
			catch (Exception e)
			{
				throw new Exception("erreur dans le format de la date d'échéance");
			}
			
			this.dateEcheance = dateEcheance.trim();
		}
		else
		{
			throw new Exception("erreur dans le format de la date d'échéance");
		}
		
	}
	/**
	 * @return the dateResiliationString
	 */
	public String getDateResiliationString()
	{
		String dateResiliationString = null;
		if (this.dateResiliation!=null)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			dateResiliationString = sdf.format(this.dateResiliation);
		}
		else
		{
			dateResiliationString = "";
		}
		
		return dateResiliationString;
	}
	/**
	 * @return the dateResiliation
	 */
	public Date getDateResiliation()
	{
		return dateResiliation;
	}


	/**
	 * @param dateResiliation the dateResiliation to set
	 */
	public void setDateResiliation(Date dateResiliation)
	{
		this.dateResiliation = dateResiliation;
	}
	/**
	 * @param dateResiliation the dateResiliation to set
	 * @throws Exception 
	 */
	public void setDateResiliation(String dateResiliation) throws Exception
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try
		{
			this.dateResiliation = sdf.parse(dateResiliation);
		} catch (Exception e)
		{
			throw new Exception("erreur dans le format de la date de résiliation du contrat");
		}
		
	}

	/**
	 * @return the dateEffetString
	 */
	public String getDateEffetString()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String dateEffetString = sdf.format(this.dateEffet);
		return dateEffetString;
	}

	/**
	 * @return the dateEffet
	 */
	public Date getDateEffet()
	{
		return dateEffet;
	}


	/**
	 * @param dateEffet the dateEffet to set
	 */
	public void setDateEffet(Date dateEffet)
	{
		this.dateEffet = dateEffet;
	}
	
	/**
	 * @param dateEffet the dateEffet to set
	 * @throws Exception 
	 */
	public void setDateEffet(String dateEffet) throws Exception
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try
		{
			this.dateEffet = sdf.parse(dateEffet);
		} catch (Exception e)
		{
			throw new Exception("erreur dans le format de la date d'effet du contrat");
		}
		
	}


	/**
	 * @return the libelleProduit
	 */
	public String getLibelleProduit()
	{
		return libelleProduit;
	}


	/**
	 * @param libelleProduit the libelleProduit to set
	 */
	public void setLibelleProduit(String libelleProduit)
	{
		this.libelleProduit = libelleProduit;
	}


	/**
	 * @return the brancheProduit
	 */
	public String getBrancheProduit()
	{
		return brancheProduit;
	}


	/**
	 * @param brancheProduit the brancheProduit to set
	 */
	public void setBrancheProduit(String brancheProduit)
	{
		this.brancheProduit = brancheProduit;
	}


	/**
	 * @return the codeContrat
	 */
	public String getCodeContrat()
	{
		return codeContrat;
	}

	/**
	 * @param codeContrat the codeContrat to set
	 * @throws Exception 
	 */
	public void setCodeContrat(String codeContrat) throws Exception
	{
		if (codeContrat.trim().length()>0)
		{
			this.codeContrat = codeContrat.trim();
		}
		else 
		{
			throw new Exception("le code contrat est obligatoire");
		}
		
	}

	/**
	 * @return the assureur
	 */
	public String getAssureur()
	{
		return assureur;
	}

	/**
	 * @param assureur the assureur to set
	 * @throws Exception 
	 */
	public void setAssureur(String assureur) throws Exception
	{
		if (assureur.trim().length()>0)
		{
			this.assureur = assureur.trim();
		}
		else 
		{
			throw new Exception("l'assureur est obligatoire");
		}
	}

	/**
	 * @return the codeProduit
	 */
	public String getCodeProduit()
	{
		return codeProduit;
	}

	/**
	 * @param codeProduit the codeProduit to set
	 * @throws Exception 
	 */
	public void setCodeProduit(String codeProduit) throws Exception
	{
		if (codeProduit.trim().length()>0)
		{
			this.codeProduit = codeProduit.trim();
		}
		else 
		{
			throw new Exception("le code produit est obligatoire");
		}
		
	}

	/**
	 * @return the listeSinistres
	 */
	public ArrayList<Sinistre> getListeSinistres()
	{
		return listeSinistres;
	}

	/**
	 * @param listeSinistres the listeSinistres to set
	 */
	public void setListeSinistres(ArrayList<Sinistre> listeSinistres)
	{
		this.listeSinistres = listeSinistres;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Contrat [codeContrat=" + codeContrat + ", assureur=" + assureur + ", codeProduit=" + codeProduit
				+ ", listeSinistres=" + listeSinistres + "]";
	}



	
	
	
	
	
}
