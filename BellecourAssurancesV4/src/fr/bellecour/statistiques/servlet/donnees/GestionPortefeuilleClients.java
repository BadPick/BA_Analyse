package fr.bellecour.statistiques.servlet.donnees;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import fr.bellecour.statistiques.bo.Client;
import fr.bellecour.statistiques.bo.Contrat;
import fr.bellecour.statistiques.bo.Produit;
import fr.bellecour.statistiques.dao.ClientDao;
import fr.bellecour.statistiques.dao.ContratDao;
import fr.bellecour.statistiques.util.ErrorHelper;

/**
 * Servlet implementation class GestionPortefeuilleClients
 */
@WebServlet("/GestionPortefeuilleClients")
public class GestionPortefeuilleClients extends HttpServlet
{
	private String message;
	private String detailMessage;
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GestionPortefeuilleClients()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//déclaration des variables
		javax.servlet.RequestDispatcher rd = null;
		javax.servlet.http.HttpSession session = request.getSession();
		ArrayList<Produit> produitList = null;
		message = null;
		detailMessage = null;
		String affichage = "";
		Client clientAAfficher = null;
		Client clientModif = null;
		Contrat contratModif = null;
		int codeClient;
		
		if (request.getParameter("typeAction")!=null)
		{
			switch (request.getParameter("typeAction"))
			{
				//affiche la page par défaut de la gestion du portefeuille client
				case "gererPortefeuilleClients":
					affichage = "liste";
					rd = getServletContext().getRequestDispatcher("/jsp/portefeuilleClientsGestion.jsp");
					break;
				
				case "majPortefeuilleClients":
										
					if (request.getPart("file")!=null)
					{						
						try
						{
							Part filePart = request.getPart("file");
							if (filePart.getSize()>0)
							{
								String fileName = getSubmittedFileName(filePart);
								InputStream fileContent = filePart.getInputStream();
								
								String messageRetour = ClientDao.majPorteFeuilleClient(fileContent);
								message = "succès de la mise à jour du portefeuille clients, " + messageRetour;
							}
							else 
							{
								message = "aucun fichier sélectionné pour la mise à jour";
							}

							
						}
						catch (Exception e)
						{
							detailMessage =  e.getMessage();
							message = "echec de la mise à jour du portefeuille clients, ";
							message += ErrorHelper.traitementDuMessageDErreur(e);
						}
					}
					else
					{
						message = "impossible d'accéder au fichier de mise à jour";
					}
					
					rd = getServletContext().getRequestDispatcher("/jsp/donneesGestion.jsp");
					break;	
					
				//persiste le client modifié puis réaffiche le client et sa liste de contrats
				case "validerModifClient":
					affichage = "liste";
					try
					{
						clientModif = new Client();			
						clientModif.setCodeClient(Integer.parseInt(request.getParameter("codeClient")));
						clientModif.setNom(request.getParameter("nomClient"));
						boolean updateOk = ClientDao.update(clientModif);
						clientAAfficher = ClientDao.getById(clientModif.getCodeClient());
						if (updateOk)
						{
							message = "succès de la modification du client, ";
						}
						else 
						{
							message = "echec de la modification du client, ";
						}						
					} 
					catch (Exception e)
					{
						detailMessage = e.getMessage();
						message = "echec de la modification du client, ";
						message += ErrorHelper.traitementDuMessageDErreur(e);
					}
					rd = getServletContext().getRequestDispatcher("/jsp/portefeuilleClientsGestion.jsp");
					break;
					
				//persiste le contrat modifié puis réaffiche le client et sa liste de contrats
				case "validerModifContrat":
					affichage = "liste";
					try
					{
						//récupération des données
						contratModif = new Contrat();
						contratModif.setCodeContrat(request.getParameter("codeContrat"));
						contratModif.setAssureur(request.getParameter("assureur"));
						contratModif.setCodeProduit(request.getParameter("codeProduit"));	
						contratModif.setDateEffet(request.getParameter("dateEffet"));
						contratModif.setDateEcheance(request.getParameter("dateEcheance"));
						if (request.getParameter("dateResiliation")!=null && !request.getParameter("dateResiliation").isEmpty())
						{
							contratModif.setDateResiliation(request.getParameter("dateResiliation"));
						}
						codeClient = Integer.parseInt(request.getParameter("codeClient"));
						request.setAttribute("codeClient", codeClient); 
						
						//persistance
						boolean updateOk = ContratDao.update(contratModif);
						clientAAfficher = ClientDao.getById(codeClient);
						if (updateOk)
						{
							message = "succès de la modification du contrat, ";
						}
						else 
						{
							message = "echec de la modification du contrat, ";
						}						
					} 
					catch (Exception e)
					{
						detailMessage = e.getMessage();
						message = "echec de la modification du client, ";
						message += ErrorHelper.traitementDuMessageDErreur(e);
					}
					rd = getServletContext().getRequestDispatcher("/jsp/portefeuilleClientsGestion.jsp");
					break;
					
				//affiche le formulaire de modification de contrat
				case "modifierContrat":
					affichage = "formulaire";
					
					try
					{
						codeClient = Integer.parseInt(request.getParameter("codeClient"));
						request.setAttribute("codeClient", codeClient);
						contratModif = ContratDao.getById(request.getParameter("codeContrat"));
					} 
					catch (Exception e)
					{
						detailMessage = e.getMessage();
						message = "echec du chargement du contrat, ";
						message += ErrorHelper.traitementDuMessageDErreur(e);
					}
					rd = getServletContext().getRequestDispatcher("/jsp/portefeuilleClientsGestion.jsp");
					break;
				
				//affiche le formulaire de modification de client
				case "modifierClient":
					affichage = "formulaire";
					
					try
					{
						clientModif = new Client();		
						clientModif.setCodeClient(Integer.parseInt(request.getParameter("codeClient")));
						clientModif.setNom(request.getParameter("nomClient"));
					} 
					catch (Exception e)
					{
						detailMessage = e.getMessage();
						message = "echec du chargement du client, ";
						message += ErrorHelper.traitementDuMessageDErreur(e);
					}
					rd = getServletContext().getRequestDispatcher("/jsp/portefeuilleClientsGestion.jsp");
					break;

				//affiche le client sélectionné et sa liste de contrats
				case "validerClient":
					affichage = "liste";
					if (request.getParameter("nomClient")!=null)
					{
						try
						{
							clientAAfficher = ClientDao.getByNameWithContrats(request.getParameter("nomClient"));
							if (clientAAfficher==null)
							{
								message ="ce client n'existe pas dans la base de données, ";
							}				
						} 
						catch (Exception e)
						{
							detailMessage = e.getMessage();
							message = "echec du chargement du client, ";
							message += ErrorHelper.traitementDuMessageDErreur(e);
						}
					}
					if (request.getParameter("codeClient")!=null)
					{
						try
						{
							clientAAfficher = ClientDao.getById(Integer.parseInt(request.getParameter("codeClient")));
							if (clientAAfficher==null)
							{
								message ="ce client n'existe pas dans la base de données, ";
							}				
						} 
						catch (Exception e)
						{
							detailMessage = e.getMessage();
							message = "echec du chargement du client, ";
							message += ErrorHelper.traitementDuMessageDErreur(e);
						}
					}
					rd = getServletContext().getRequestDispatcher("/jsp/portefeuilleClientsGestion.jsp");
					break;
			}
		}
			
		request.setAttribute("contratModif", contratModif);
		request.setAttribute("clientModif", clientModif);
		request.setAttribute("client", clientAAfficher);
		request.setAttribute("affichage", affichage);
		request.setAttribute("message", message);
		request.setAttribute("detailMessage", detailMessage);		
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{
		doGet(request, response);
	}
	
	/**
	 * méthode permettant de récupérer le nom du fichier selectionné
	 * @param part
	 * @return String
	 */
	private static String getSubmittedFileName(Part part)
	{
		for (String cd : part.getHeader("content-disposition").split(";"))
		{
			if (cd.trim().startsWith("filename"))
			{
				String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1); // MSIE
																													// fix.
			}
		}
		return null;
	}

}
