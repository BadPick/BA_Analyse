package fr.bellecour.statistiques.servlet.ajax;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.bellecour.statistiques.dao.ClientDao;
import fr.bellecour.statistiques.dao.ContratDao;
import fr.bellecour.statistiques.dao.SinistreDao;

/**
 * Servlet implementation class RechercheBox
 */
@WebServlet("/RechercheBox")
public class RechercheBox extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RechercheBox()
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
		ArrayList<String> liste = null;
		
		//récupération du type de recherche
		String type = request.getParameter("type");
		//récupération du paramètre de recherche
		String param = request.getParameter("rechercheBox");
				
		//code pour aller chercher la liste de suggestion
		switch (type)
		{
			case "client":
				try
				{
					liste = ClientDao.getListeRechercheClient(param);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
				break;
				
			case "contrat":
				try
				{
					liste = ContratDao.getListeRecherche(param);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
				break;
			
			case "sinistre":
				try
				{
					liste = SinistreDao.getListeRecherche(param);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
				break;

			default:
				try
				{
					liste = ClientDao.getListeRechercheClient(param);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
				break;
		}
				
		//code pour conversion en Json
		String monObjetJson = listToJson(liste);
		
		//Authorise la reponse
		//response.setHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Origin", "http://192.168.100.200:8080");
        response.addHeader("Access-Control-Allow-Methods", "POST");
        response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        response.addHeader("Access-Control-Max-Age", "1728000");
		
		
		
		//renvoi de l'objet Json dans la réponse
		response.getWriter().println(monObjetJson);
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
	 * méthode permettant de convertir une ArrayList<String> en objet JSON
	 * @param liste
	 * @return JSON
	 */
	public static String listToJson(ArrayList<String> liste)
	{
		String json = "{\"";
		int i = 1;
		
		for (String s : liste) 
		{	
			json += i;
			json += "\": \"";
			json += s;
			json += "\"";
			if (i < liste.size()) 
			{
				json += ",\"";
			}			
			i++;
		}
		json += "}";
		
		return json;		
	}
}
