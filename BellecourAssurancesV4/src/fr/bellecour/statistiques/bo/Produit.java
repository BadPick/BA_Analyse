/**
 * 
 */
package fr.bellecour.statistiques.bo;

/**
 * Classe définissant un produit
 * @author mrault
 *
 */
public class Produit
{
	private String codeProduit;
	private String libelle;
	private String branche;
	
	public Produit()
	{
		this.setCodeProduit("");
		this.setLibelle("");
		this.setBranche("");
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
	 */
	public void setCodeProduit(String codeProduit)
	{
		this.codeProduit = codeProduit;
	}

	/**
	 * @return the libelle
	 */
	public String getLibelle()
	{
		return libelle;
	}

	/**
	 * @param libelle the libelle to set
	 */
	public void setLibelle(String libelle)
	{
		this.libelle = libelle;
	}

	/**
	 * @return the branche
	 */
	public String getBranche()
	{
		return branche;
	}

	/**
	 * @param branche the branche to set
	 */
	public void setBranche(String branche)
	{
		this.branche = branche;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Produit [codeProduit=" + codeProduit + ", libelle=" + libelle + ", branche=" + branche + "]";
	}
	
	
	
	
}
