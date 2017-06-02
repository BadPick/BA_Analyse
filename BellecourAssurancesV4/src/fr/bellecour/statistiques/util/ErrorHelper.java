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
			message += "impossible de se connecter à la base de données";
		}
		if (e instanceof SQLIntegrityConstraintViolationException)
		{
			if (1451 == ((SQLIntegrityConstraintViolationException) e).getErrorCode())
			{
				message += "impossible de supprimer cette référence, elle est utilisée par une autre référence";
			}
			else if (1062 == ((SQLIntegrityConstraintViolationException) e).getErrorCode())
			{
				message += "impossible d'insérer cette référence, ce code existe déja";
			}
			else if (1452 == ((SQLIntegrityConstraintViolationException) e).getErrorCode())
			{
				if (e.getMessage().contains("FK_CONTRATS_SINISTRES"))
				{
					message += "tentative d'insertion d'un sinistre pour un contrat inconnu,"
							+ " veuillez mettre à jour le portefeuille clients ou vérifier le n° de police";
				}
				else
				{
					message += "tentative d'insertion ou de modification d'une référence dont le code n'existe pas "
							+ "(code produit, code circonstance, code nature du sinistre,n° de police, etc...)";
				}
			}
			else
			{
				message += "erreur d'intégrité de référence" + " (code : "
						+ ((SQLIntegrityConstraintViolationException) e).getErrorCode() + ")";
			}
		}

		if (e instanceof ArrayIndexOutOfBoundsException)
		{
			message += "veuillez vérifier que votre fichier à importer est conforme (format CSV) ou qu'il ne contient pas de point virgule";
		}

		if ("".equals(message))
		{

			message += e.getMessage();
		}

		return message;
	}

}
