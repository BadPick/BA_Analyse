/**
 * 
 */
package fr.bellecour.statistiques.bo;

/**
 * classe définissant une circonstance
 * @author mrault
 *
 */
public class Circonstance implements Comparable<Circonstance>
{
	private String codeCirconstance;
	private boolean ida;
	private String libelle;
	
	public Circonstance()
	{
		this.codeCirconstance = codeCirconstance;
	}
	public Circonstance(String codeCirconstance)
	{
		this.codeCirconstance = codeCirconstance;
	}



	/**
	 * @return the codeCirconstance
	 */
	public String getCodeCirconstance()
	{
		return codeCirconstance;
	}

	/**
	 * @param codeCirconstance the codeCirconstance to set
	 */
	public void setCodeCirconstance(String codeCirconstance)
	{
		this.codeCirconstance = codeCirconstance;
	}

	/**
	 * @return the ida
	 */
	public boolean isIda()
	{
		return ida;
	}

	/**
	 * @param ida the ida to set
	 */
	public void setIda(boolean ida)
	{
		this.ida = ida;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return codeCirconstance + " : " + libelle;
	}
	@Override
	public int compareTo(Circonstance c)
	{
        return this.getCodeCirconstance().compareToIgnoreCase(c.getCodeCirconstance());
	}
	
	
	
}
