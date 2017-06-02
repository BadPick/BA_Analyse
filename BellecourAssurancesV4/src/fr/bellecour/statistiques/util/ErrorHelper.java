/**
 * 
 */
package fr.bellecour.statistiques.util;

import java.net.ConnectException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLNonTransientConnectionException;

/**
 * @author mrault
 *
 */
public class ErrorHelper
{

	public static String traitementDuMessageDErreur(Exception e)
	{
		String message = "";

		if (e instanceof SQLNonTransientConnectionException || e instanceof ConnectException)
		{
			message += "impossible de se connecter � la base de donn�es";
		}
		if (e instanceof SQLIntegrityConstraintViolationException)
		{
			if (1451 == ((SQLIntegrityConstraintViolationException) e).getErrorCode())
			{
				message += "impossible de supprimer cette r�f�rence, elle est utilis�e par une autre r�f�rence";
			}
			else if (1062 == ((SQLIntegrityConstraintViolationException) e).getErrorCode())
			{
				message += "impossible d'ins�rer cette r�f�rence, ce code existe d�ja";
			}
			else if (1452 == ((SQLIntegrityConstraintViolationException) e).getErrorCode())
			{
				if (e.getMessage().contains("FK_CONTRATS_SINISTRES"))
				{
					message += "tentative d'insertion d'un sinistre pour un contrat inconnu,"
							+ " veuillez mettre � jour le portefeuille clients ou v�rifier le n� de police";
				}
				else
				{
					message += "tentative d'insertion ou de modification d'une r�f�rence dont le code n'existe pas "
							+ "(code produit, code circonstance, code nature du sinistre,n� de police, etc...)";
				}
			}
			else
			{
				message += "erreur d'int�grit� de r�f�rence" + " (code : "
						+ ((SQLIntegrityConstraintViolationException) e).getErrorCode() + ")";
			}
		}

		if (e instanceof ArrayIndexOutOfBoundsException)
		{
			message += "veuillez v�rifier que votre fichier � importer est conforme (format CSV) ou qu'il ne contient pas de point virgule";
		}

		if ("".equals(message))
		{

			message += e.getMessage();
		}

		return message;
	}

}
