/**
 * 
 */
package fr.bellecour.statistiques.bo;

/**
 * Classe définissant un niveau
 * @author mrault
 *
 */
public class Niveau
{
	private int codeNiveau;
	private String descriptif;
	
	public Niveau()
	{
		
	}
	
	

	public Niveau(int codeNiveau, String descriptif)
	{
		this.codeNiveau = codeNiveau;
		this.descriptif = descriptif;
	}



	/**
	 * @return the code
	 */
	public int getCodeNiveau()
	{
		return codeNiveau;
	}

	/**
	 * @param code the code to set
	 */
	public void setCodeNiveau(int codeNiveau)
	{
		this.codeNiveau = codeNiveau;
	}

	/**
	 * @return the descriptif
	 */
	public String getDescriptif()
	{
		return descriptif;
	}

	/**
	 * @param descriptif the descriptif to set
	 */
	public void setDescriptif(String descriptif)
	{
		this.descriptif = descriptif;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return codeNiveau + " - " + descriptif;
	}
	
	
	
	
}
