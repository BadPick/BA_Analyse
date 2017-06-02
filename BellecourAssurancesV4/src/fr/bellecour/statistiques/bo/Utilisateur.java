/**
 * 
 */
package fr.bellecour.statistiques.bo;

/**
 * Classe définissant un utilisateur
 * @author mrault
 *
 */
public class Utilisateur
{
	private int codeUtilisateur;
	private String nom;
	private String email;
	private String password;
	private Niveau niveau;
	
	public Utilisateur()
	{
		this.setCodeUtilisateur(0);
		this.setNom("");
		this.setEmail("");
		this.setPassword("");
		this.setNiveau(new Niveau(0, ""));
		
	}

	/**
	 * @return the codeUtilisateur
	 */
	public int getCodeUtilisateur()
	{
		return codeUtilisateur;
	}

	/**
	 * @param codeUtilisateur the codeUtilisateur to set
	 */
	public void setCodeUtilisateur(int codeUtilisateur)
	{
		this.codeUtilisateur = codeUtilisateur;
	}

	/**
	 * @return the nom
	 */
	public String getNom()
	{
		return nom;
	}

	/**
	 * @param nom the nom to set
	 */
	public void setNom(String nom)
	{
		this.nom = nom;
	}

	/**
	 * @return the email
	 */
	public String getEmail()
	{
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * @return the niveau
	 */
	public Niveau getNiveau()
	{
		return niveau;
	}

	/**
	 * @param niveau the niveau to set
	 */
	public void setNiveau(Niveau niveau)
	{
		this.niveau = niveau;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Utilisateur [codeUtilisateur=" + codeUtilisateur + ", nom=" + nom + ", email=" + email + ", password="
				+ password + ", niveau=" + niveau + "]";
	}
	
	
	
	
	
}
