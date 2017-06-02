/**
 * 
 */
package fr.bellecour.statistiques.dao;

import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import fr.bellecour.statistiques.bo.Client;
import fr.bellecour.statistiques.bo.Contrat;
import fr.bellecour.statistiques.bo.Produit;
import fr.bellecour.statistiques.util.CsvHelper;

/**
 * Classe permettant l'accès aux données concernant les clients et leurs
 * contrats
 * 
 * @author mrault
 *
 */
public class ClientDao
{
	private static final String ADD = "{call CLIENT_INSERT(?)}";
	private static final String UPDATE = "{call CLIENT_UPDATE(?,?)}";
	private static final String DELETE_ALL = "{call CLIENT_DELETE_ALL()}";
	private static final String GET_ALL = "{call CLIENT_GET_ALL()}";
	private static final String GET_RECHERCHE = "{call CLIENT_RECHERCHE(?)}";
	private static final String GET_BY_NAME_WITH_CONTRATS = "{call CLIENT_GET_BY_NAME_WITH_CONTRATS(?)}";
	private static final String GET_BY_NAME = "{call CLIENT_GET_BY_NAME(?)}";
	private static final String GET_BY_ID = "{call CLIENT_GET_BY_ID(?)}";
	private static final int NUM_COL_NOM = 0;
	private static final int NUM_COL_DATE_ECHEANCE = 12;
	private static final int NUM_COL_CODE_PRODUIT = 16;
	private static final int NUM_COL_ASSUREUR = 2;
	private static final int NUM_COL_CODE_CONTRAT = 3;
	private static final int NUM_COL_DATE_EFFET = 5;
	private static final int NUM_COL_DATE_RESILIATION = 10;
	
	private static int compteur;

	/**
	 * méthode permettant la mise à jour complète du portefeuille clients
	 * (clients + contrat) à partir d'un fichier CSV exporté du progiciel
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public static String majPorteFeuilleClient(InputStream fileContent) throws Exception
	{
		String message = "";
		int nbAjout = 0;
		int nbModif = 0;
		boolean operationOk = false;
		Client clientRecupere = null;
		Contrat contratRecupere = null;
		// récupération du csv d'import en liste de string[]
		ArrayList<String[]> liste = CsvHelper.getCSV(fileContent);
		
		//suppression de la ligne d'entête
		liste.remove(0);

		// convertion de la liste de string[] en liste d'objets clients
		ArrayList<Client> listeClients = listeCSVtoListeCLients(liste);

		// update ou ajout des clients
		for (Client client : listeClients)
			{
				clientRecupere = getByName(client.getNom());
				
				//si le client existe déja
				if (clientRecupere != null)
					{
						client.setCodeClient(clientRecupere.getCodeClient());
						operationOk = update(client);
						if (operationOk)
							{
								nbModif++;
							}
					}
				else 
					{
						boolean ajoutOk = addClient(client);
						if (ajoutOk)
						{
							nbAjout++;
						}
				}
			}
		
		message += nbAjout + " client(s) ajouté(s), " + nbModif + " modifié(s)";

		// récupération de la liste de client
		ArrayList<Client> listeCLientRetour = getAll();

		// récupération des codes clients
		for (Client clientRetour : listeCLientRetour)
		{
			for (Client client : listeClients)
			{
				if (client.getNom().equals(clientRetour.getNom()))
				{
					client.setCodeClient(clientRetour.getCodeClient());
				}
			}
		}

		// insertion des contrats si le code produit et contenu dans la liste
		// des codes à analyser
		nbAjout = 0;
		nbModif = 0;
		for (Client client : listeClients)
		{
			for (Contrat contrat : client.getListeContrats())
			{
				contratRecupere = ContratDao.getById(contrat.getCodeContrat());
				//si le contrat existe déja
				if (contratRecupere!=null)
					{
						operationOk = ContratDao.update(contrat);
						if (operationOk)
							{
								nbModif++;
							}
					}
					else 
					{
							boolean ajoutOk = ContratDao.addContrat(client.getCodeClient(), contrat);
							if (ajoutOk)
							{
								nbAjout++;
							}
						
					}
				
			}
		}
		message += ", " + nbAjout + " contrat(s) ajouté(s), " + nbModif + " modifié(s)";

		return message;
	}

	/**
	 * méthode permettant d'obtenir la liste de tous les clients
	 * @return ArrayList<Client>
	 * @throws Exception
	 */
	public static ArrayList<Client> getAll() throws Exception
	{
		ArrayList<Client> liste = new ArrayList<Client>();
		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(GET_ALL);
			ResultSet rs = rqt.executeQuery();
			while (rs.next())
			{
				liste.add(clientBuilder(rs));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return liste;
	}

	/**
	 * méthode permettant de construire un objet client à partir d'un resultSet
	 * @param ResultSet
	 * @return Client
	 * @throws Exception
	 */
	private static Client clientBuilder(ResultSet rs) throws Exception
	{
		Client client = new Client();
		try
		{
			client.setCodeClient(rs.getInt("code"));
			client.setNom(rs.getString("nomClient"));
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			client = null;
			throw e;
		}
		return client;
	}

	/**
	 * méthose permettant de supprimer tous les clients
	 * @throws Exception
	 */
	public static void deleteAllClient() throws Exception
	{
		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(DELETE_ALL);
			rqt.executeUpdate();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * méthode permettant d'ajouter un client
	 * @param client
	 * @return boolean
	 * @throws Exception
	 */
	private static boolean addClient(Client client) throws Exception
	{
		boolean ajoutOk = false;
		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(ADD);

			rqt.setString(1, client.getNom());

			if (rqt.executeUpdate() == 1)
			{
				ajoutOk = true;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return ajoutOk;
	}

	/**
	 * méthode permettant de convertir la liste de String[] récupérer de
	 * l'export csv en liste d'objets client
	 * 
	 * @param liste
	 * @return ArrayList<Client>
	 * @throws Exception
	 */
	private static ArrayList<Client> listeCSVtoListeCLients(ArrayList<String[]> liste) throws Exception
	{
		// récupération de la liste des codes produits
		ArrayList<Produit> listeProduit = ProduitDao.getAll();

		ArrayList<Client> listeClients = new ArrayList<Client>();		
		compteur = 1;
		//création de la liste de clients
		try
		{
			for (String[] tab : liste)
			{
				// si le client n'existe pas déja et que le code produit exist
				if (!clientExist(tab[NUM_COL_NOM], listeClients) && ProduitDao.produitExist(tab[NUM_COL_CODE_PRODUIT], listeProduit))
				{
					Client nouveauClient = new Client();
					nouveauClient.setNom(tab[NUM_COL_NOM]);					
					listeClients.add(nouveauClient);
				}
				compteur++;
			}
		} 
		catch (Exception e)
		{
			throw new Exception("erreur dans l'import à la ligne " + compteur + ", " + e.getMessage());
		}
		
		
		//création de la liste de contrats
		try
		{
			for (Client client : listeClients)
			{
				compteur = 1;
				for (String[] tab : liste)
				{
					//si le code produit exist et que le contrat n'est pas résilié
					if (tab[NUM_COL_NOM].equals(client.getNom()) && ProduitDao.produitExist(tab[NUM_COL_CODE_PRODUIT], listeProduit))// && (tab[NUM_COL_DATE_RESILIATION].trim()).isEmpty())
					{
						Contrat contrat = new Contrat();
						contrat.setAssureur(tab[NUM_COL_ASSUREUR]);
						contrat.setCodeContrat(tab[NUM_COL_CODE_CONTRAT]);
						contrat.setCodeProduit(tab[NUM_COL_CODE_PRODUIT]);
						contrat.setDateEffet(tab[NUM_COL_DATE_EFFET]);
						contrat.setDateEcheance(tab[NUM_COL_DATE_ECHEANCE]);
						
						if (tab[NUM_COL_DATE_RESILIATION]!=null && !tab[NUM_COL_DATE_RESILIATION].isEmpty())
						{
							contrat.setDateResiliation(tab[NUM_COL_DATE_RESILIATION]);
						}
						client.getListeContrats().add(contrat);
					}
					compteur++;
				}
			}
		} 
		catch (Exception e)
		{
			throw new Exception("erreur dans l'import à la ligne " + compteur + ", " + e.getMessage());
		}
		
		return listeClients;
	}

	/**
	 * méthode permettant de vérifier si un client existe
	 * @param nomClient
	 * @param listeClients
	 * @return boolean
	 */
	private static boolean clientExist(String nomClient, ArrayList<Client> listeClients)
	{
		boolean exist = false;
		for (Client client : listeClients)
		{
			if (nomClient.equals(client.getNom()))
			{
				exist = true;
			}
		}
		return exist;
	}

	/**
	 * méthode permettant d'obtenir la liste des nom de clients correspondant aux caratères de la recherche
	 * @param param
	 * @return ArrayList<String>
	 * @throws Exception
	 */
	public static ArrayList<String> getListeRechercheClient(String param) throws Exception
	{
		ArrayList<String> listeNomClient = new ArrayList<String>();

		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(GET_RECHERCHE);

			rqt.setString(1, param);

			ResultSet rs = rqt.executeQuery();

			while (rs.next())
			{
				listeNomClient.add(rs.getString("nomClient"));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return listeNomClient;
	}

	/**
	 * méthode permettant d'obtenir un client par son nom
	 * @param nomClient
	 * @return Client
	 * @throws Exception
	 */
	public static Client getByNameWithContrats(String nomClient) throws Exception
	{
		Client client = null;
		try (Connection cnx = BddAccess.getConnection())
		{

			CallableStatement rqt = cnx.prepareCall(GET_BY_NAME_WITH_CONTRATS);

			rqt.setString(1, nomClient);

			ResultSet rs = rqt.executeQuery();

			while (rs.next())
			{
				if (client == null)
				{
					client = clientBuilder(rs);
				}
				client.getListeContrats().add(ContratDao.contratBuilder(rs));
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return client;
	}
	
	public static Client getByName(String nomClient) throws Exception
	{
		Client client = null;
		try (Connection cnx = BddAccess.getConnection())
		{

			CallableStatement rqt = cnx.prepareCall(GET_BY_NAME);

			rqt.setString(1, nomClient);

			ResultSet rs = rqt.executeQuery();

			while (rs.next())
			{
				if (client == null)
				{
					client = clientBuilder(rs);
				}				
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return client;
	}
	
	/**
	 * méthode permettant d'obtenir un client par son identifiant
	 * @param codeClient
	 * @return Client
	 * @throws Exception
	 */
	public static Client getById(int codeClient) throws Exception
	{
		Client client = null;
		try (Connection cnx = BddAccess.getConnection())
		{

			CallableStatement rqt = cnx.prepareCall(GET_BY_ID);

			rqt.setInt(1, codeClient);

			ResultSet rs = rqt.executeQuery();

			while (rs.next())
			{
				if (client == null)
				{
					client = clientBuilder(rs);
				}
				client.getListeContrats().add(ContratDao.contratBuilder(rs));
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return client;
	}

	/**
	 * méthode permettant modifier un client
	 * @param clientAModifier
	 * @return
	 * @throws Exception
	 */
	public static boolean update(Client clientAModifier) throws Exception
	{
		boolean updateOk = false;

		try (Connection cnx = BddAccess.getConnection())
		{
			CallableStatement rqt = cnx.prepareCall(UPDATE);

			rqt.setInt(1, clientAModifier.getCodeClient());
			rqt.setString(2, clientAModifier.getNom());

			if (rqt.executeUpdate() == 1)
			{
				updateOk = true;
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}

		return updateOk;
	}
}
