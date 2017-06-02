package fr.bellecour.statistiques.servlet.donnees;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.bellecour.statistiques.bo.Produit;
import fr.bellecour.statistiques.dao.ProduitDao;
import fr.bellecour.statistiques.util.ErrorHelper;

/**
 * Servlet implementation class GestionProduits
 */
@WebServlet("/GestionProduits")
public class GestionProduits extends HttpServlet
{
	private String message;
	private String detailMessage;
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GestionProduits()
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
		javax.servlet.RequestDispatcher rd = null;
		javax.servlet.http.HttpSession session = request.getSession();
		Produit produitAModifier = null;
		Produit nouveauProduit = null;
		String affichage = "";
		message = null;
		detailMessage = null;
		ArrayList<Produit> produitList = null;
		

		if (request.getParameter("typeAction") != null)
		{
			switch (request.getParameter("typeAction"))
			{
				case "gererProduits":
					affichage = "liste";
					try
					{
						produitList = ProduitDao.getAll();
					} 
					catch (Exception e)
					{
						detailMessage = e.getMessage();
						message = "echec du chargement de la liste de produit, ";
						message += ErrorHelper.traitementDuMessageDErreur(e);
					}
					request.setAttribute("produitList", produitList);
					request.setAttribute("affichage", affichage);
					rd = getServletContext().getRequestDispatcher("/jsp/produitsGestion.jsp");
					break;
					
				case "validerAjout":
					// insert
					try
					{
						nouveauProduit = recuperationProduit(request);
						boolean ajoutOk = ProduitDao.add(nouveauProduit);
						if (ajoutOk)
						{
							message = "succès de l'ajout produit";
						}
						else
						{
							message = "echec de l'ajout produit, ";
						}
					} 
					catch (Exception e)
					{
						detailMessage = e.getMessage();
						message = "echec de l'ajout produit, ";
						message += ErrorHelper.traitementDuMessageDErreur(e);
					}

					affichage = "liste";
					produitList = chargementDeLaListe();

					break;

				case "validerModif":
					// update
					if (request.getParameter("codeProduit") != null)
					{
						boolean updateOk;
						try
						{
							produitAModifier = recuperationProduit(request);
							updateOk = ProduitDao.update(produitAModifier);
							if (updateOk)
							{
								message = "succès de la modification du produit";
							}
							else
							{
								message = "echec de la modification du produit, ";
							}
						} catch (Exception e)
						{
							detailMessage = e.getMessage();
							message = "echec de la modification du produit, ";
							message += ErrorHelper.traitementDuMessageDErreur(e);
						}

					}
					affichage = "liste";
					produitList = chargementDeLaListe();					

					break;

				case "ajouter":
					affichage = "formulaire";

					break;

				case "modifier":
					affichage = "formulaire";
					try
					{
						produitAModifier = ProduitDao.getById(request.getParameter("codeProduit"));
					} 
					catch (Exception e)
					{
						detailMessage = e.getMessage();
						message = "echec du chargement du produit à modifier, ";
						message += ErrorHelper.traitementDuMessageDErreur(e);
					}

					break;

				case "supprimer":
					if (request.getParameter("codeProduit") != null)
					{
						boolean suppressionOk;
						try
						{
							suppressionOk = ProduitDao.delete(request.getParameter("codeProduit"));
							if (suppressionOk)
							{
								message = "succès de la suppression du produit";
							}
							else
							{
								message = "echec de la suppression du produit, ";
							}
						} 
						catch (Exception e)
						{
							detailMessage = e.getMessage();
							message = "echec de la suppression du produit, ";
							message += ErrorHelper.traitementDuMessageDErreur(e);
						}
						
					}
					else
					{
						message = "echec de la suppression du produit, ";
					}
					affichage = "liste";
					produitList = chargementDeLaListe();
					break;

				case "annuler":
					affichage = "liste";
					produitList = chargementDeLaListe();
					break;

				default:

					break;
			}
		}

		request.setAttribute("affichage", affichage);
		request.setAttribute("produitAModifier", produitAModifier);
		request.setAttribute("produitList", produitList);
		request.setAttribute("message", message);
		request.setAttribute("detailMessage", detailMessage);
		rd = getServletContext().getRequestDispatcher("/jsp/produitsGestion.jsp");
		rd.forward(request, response);
	}

	/**
	 * méthode permettant de récupérer la liste des produits
	 * @return ArrayList<Produit>
	 */
	private ArrayList<Produit> chargementDeLaListe()
	{
		ArrayList<Produit> produitList = null;
		try
		{
			produitList = ProduitDao.getAll();
		} 
		catch (Exception e)
		{
			detailMessage = e.getMessage();
			message = "echec du chargement de la liste de produit, ";
			message += ErrorHelper.traitementDuMessageDErreur(e);
		}
		return produitList;
	}

	/**
	 * méthode permettant de construire un produit à partir de la request
	 * @param request
	 * @return Produit
	 */
	private Produit recuperationProduit(HttpServletRequest request)
	{
		Produit produit = new Produit();

		produit.setCodeProduit(request.getParameter("codeProduit"));
		produit.setLibelle(request.getParameter("libelle"));
		produit.setBranche(request.getParameter("branche"));

		return produit;
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

}
