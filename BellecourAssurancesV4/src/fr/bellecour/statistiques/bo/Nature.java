/**
 * 
 */
package fr.bellecour.statistiques.bo;

/**
 * @author mrault
 *
 */
public class Nature
{
	private String codeNature;
	private String codeAnalyse;
	private String libelle;
	private String branche;
	
	public Nature()
	{

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


	public Nature(String codeNature)
	{
		super();
		this.codeNature = codeNature;
	}

	/**
	 * @return the codeNature
	 */
	public String getCodeNature()
	{
		return codeNature;
	}

	/**
	 * @param codeNature the codeNature to set
	 */
	public void setCodeNature(String codeNature)
	{
		this.codeNature = codeNature;
	}

	/**
	 * @return the codeAnalyse
	 */
	public String getCodeAnalyse()
	{
		return codeAnalyse;
	}

	/**
	 * @param codeAnalyse the codeAnalyse to set
	 */
	public void setCodeAnalyse(String codeAnalyse)
	{
		this.codeAnalyse = codeAnalyse;
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
	
	

	
	
	
	
}
