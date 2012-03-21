/*
 * Copyright COCKTAIL (www.cocktail.org), 1995, 2010 This software 
 * is governed by the CeCILL license under French law and abiding by the
 * rules of distribution of free software. You can use, modify and/or 
 * redistribute the software under the terms of the CeCILL license as 
 * circulated by CEA, CNRS and INRIA at the following URL 
 * "http://www.cecill.info". 
 * As a counterpart to the access to the source code and rights to copy, modify 
 * and redistribute granted by the license, users are provided only with a 
 * limited warranty and the software's author, the holder of the economic 
 * rights, and the successive licensors have only limited liability. In this 
 * respect, the user's attention is drawn to the risks associated with loading,
 * using, modifying and/or developing or reproducing the software by the user 
 * in light of its specific status of free software, that may mean that it
 * is complicated to manipulate, and that also therefore means that it is 
 * reserved for developers and experienced professionals having in-depth
 * computer knowledge. Users are therefore encouraged to load and test the 
 * software's suitability as regards their requirements in conditions enabling
 * the security of their systems and/or data to be ensured and, more generally, 
 * to use and operate it in the same conditions as regards security. The
 * fact that you are presently reading this means that you have had knowledge 
 * of the CeCILL license and that you accept its terms.
 */

package org.cocktail.agrhum.serveur.metier;

import org.cocktail.fwkcktldroitsutils.common.CktlCallEOUtilities;
import org.cocktail.fwkcktldroitsutils.common.util.MyStringCtrl;
import org.cocktail.fwkcktlpersonne.common.PersonneApplicationUser;
import org.cocktail.fwkcktlpersonne.common.eospecificites.EOStructureForGroupeSpec;
import org.cocktail.fwkcktlpersonne.common.metier.EOCompte;
import org.cocktail.fwkcktlpersonne.common.metier.EOGrhumParametres;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;
import org.cocktail.fwkcktlpersonne.common.metier.EOVlans;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;
import org.cocktail.fwkcktlpersonneguiajax.serveur.controleurs.MyCRIMailBus;
import org.cocktail.fwkcktlwebapp.server.CktlConfig;
import org.cocktail.fwkcktlwebapp.server.CktlWebApplication;
import org.cocktail.fwkcktlwebapp.server.database.CktlDataBus;

import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSValidation.ValidationException;

import er.extensions.eof.ERXQ;
import er.extensions.eof.ERXSortOrdering;

public class TicketService {

    public static void ticketManagementEnabled(EOEditingContext ec) 
    throws CktlConfigurationException {
        try {
            EOStructureForGroupeSpec.getGroupeForParamKey(
                    ec, EOStructureForGroupeSpec.ANNUAIRE_TICKET_WIFI_KEY);
        } catch (Exception e) {
            throw new CktlConfigurationException(
                    "L'application n'est pas encore configurée pour gérer les tickets WIFI.\n"+
                    "Contactez l'administrateur de cette application.\n" +
                    "Informations techniques :\n" +
                    "Le paramètre ANNUAIRE_TICKET_WIFI n'est pas renseignée ou ne pointe pas " +
                    "sur une structure existante.", e);
        }
    }

    public static void invitesManagementEnabled(EOEditingContext ec) 
    throws CktlConfigurationException {
        try {
            EOStructureForGroupeSpec.getGroupeForParamKey(
                    ec, EOStructureForGroupeSpec.ANNUAIRE_INVITE_WIFI_KEY);
        } catch (Exception e) {
            throw new CktlConfigurationException(
                    "L'application n'est pas encore configurée pour gérer les invités WIFI.\n"+
                    "Contactez l'administrateur de cette application.\n" +
                    "Informations techniques :\n" +
                    "Le paramètre ANNUAIRE_INVITE_WIFI n'est pas renseignée ou ne pointe pas " +
                    "sur une structure existante.", e);
        }
    }
    
    
    @SuppressWarnings("unchecked")
    public static NSArray<EOCompte> findAllTickets(EOEditingContext ec) 
    throws Exception {
        // On récupère tous les comptes dont la structure appartient au groupe
        // des tickets wifi
        EOStructure groupeTicketsWifi = EOStructureForGroupeSpec.getGroupeForParamKey(
                ec, EOStructureForGroupeSpec.ANNUAIRE_TICKET_WIFI_KEY);
        NSArray<IPersonne> personnes = EOStructureForGroupeSpec.getPersonnesInGroupe(groupeTicketsWifi);
        NSArray<Integer> ids = (NSArray<Integer>)personnes.valueForKey("persId");
        EOQualifier qual = ERXQ.in(EOCompte.PERS_ID_KEY, ids);
        return EOCompte.fetchAll(ec, qual, ERXSortOrdering.sortOrderingWithKey(EOCompte.CPT_LOGIN_KEY, ERXSortOrdering.CompareAscending).array());
    }

    public static EOCompte createTicket(EOEditingContext ec, 
            String login,
            NSTimestamp finValidite, 
            String passwordClair, 
            Integer persId,
            PersonneApplicationUser user) 
    throws ValidationException, Exception {
        String strAffichage = "TICKET-" + login;
        EOStructure groupeTicketsWifi = EOStructureForGroupeSpec.getGroupeForParamKey(
                ec, EOStructureForGroupeSpec.ANNUAIRE_TICKET_WIFI_KEY);
        EOStructure structure = 
            EOStructureForGroupeSpec.creerGroupe(
                    ec, 
                    persId, 
                    strAffichage, null, null, null);
        EOStructureForGroupeSpec.affecterPersonneDansGroupe(ec, structure, groupeTicketsWifi, persId, null);
        
        EOCompte compte = EOCompte.creerInstance(ec);
        NSTimestamp debut = new NSTimestamp();
        
        String vlanExterne = EOGrhumParametres.parametrePourCle(ec, EOGrhumParametres.PARAM_GRHUM_VLAN_EXTERNE_KEY, EOVlans.VLAN_X);
        compte.initialiseCompte(ec,user, structure, vlanExterne, login, passwordClair, true, null, debut, finValidite);
        
//        compte.setCptUid(structure.persId());
//        compte.setCptGid(structure.persId());
//        compte.setCptHome("None");
//        compte.setCptShell("None");
        return compte;
    }

    public static void ajouterInviteWifi(EOEditingContext ec, Integer persId, IPersonne invite) 
        throws ValidationException, Exception {
        EOStructure groupeInvitesWifi = EOStructureForGroupeSpec.getGroupeForParamKey(
                ec, EOStructureForGroupeSpec.ANNUAIRE_INVITE_WIFI_KEY);
        EOStructureForGroupeSpec.affecterPersonneDansGroupe(ec, invite, groupeInvitesWifi, persId, null);
    }

    public static void deleteTicket(EOEditingContext ec, EOCompte ticket) throws ValidationException, EOGeneralAdaptorException, Exception {
    	boolean isTicketStructValid = (ticket.toStructure()!=null);
    	// delete de la structure du Ticket
    	if (isTicketStructValid) {
    		EOStructure ticketStructure = ticket.toStructure();
//            CktlDataBus databus = new CktlDataBus(ec);
            CktlDataBus databus = ((CktlWebApplication) CktlWebApplication.application()).dataBus();
//            databus.beginTransaction();
            try {
            	String exp = "ALTER TRIGGER GRHUM.LOG_DELETE_PERSONNE DISABLE";
                databus.executeSQLQuery(exp, NSArray.EmptyArray);
                NSMutableDictionary<String, Integer> procStockParams = new NSMutableDictionary<String, Integer>();
                procStockParams.put("persid", ticketStructure.persId());
//                databus.executeProcedure("FwkPers_prcDetruirePersonne", procStockParams);
                CktlCallEOUtilities.executeStoredProcedureNamed(ec, "FwkPers_prcDetruirePersonne", procStockParams);
                exp = "ALTER TRIGGER GRHUM.LOG_DELETE_PERSONNE ENABLE";
                databus.executeSQLQuery(exp, NSArray.EmptyArray);
			} catch (Throwable e) {
				databus.rollbackTransaction();
				throw new Exception(e);
			} 
//            databus.commitTransaction();
    	}
    	
    }

	public static void sendMailInfoToValidAlias(EOCompte ticket, CktlConfig cktlConfig) throws Exception {
		String validTicketWifiAliasCstructure = cktlConfig.stringForKey(EOStructureForGroupeSpec.ANNUAIRE_VALID_TICKET_WIFI_KEY);
		String grhumDomainePrincipal = cktlConfig.stringForKey(EOGrhumParametres.PARAM_GRHUM_DOMAINE_PRINCIPAL);
		if (!MyStringCtrl.isEmpty(validTicketWifiAliasCstructure) && ticket != null) {
			// Recuperation du groupe alias de validation des tickets wifi
			EOStructure validWifiGroupe = EOStructureForGroupeSpec.getGroupeValidTicketWifi(ticket.editingContext());
			// Recupération de l'adresse mail de l'alias
			String alias = null;
			if (validWifiGroupe != null) {
				alias = validWifiGroupe.grpAlias();
			}
			
			if (!MyStringCtrl.isEmpty(alias) && !MyStringCtrl.isEmpty(grhumDomainePrincipal) ) {
				String toAlias = alias+"@"+grhumDomainePrincipal;
				if (MyStringCtrl.isEmailValid(toAlias)) {
					// Construction du message d'information sur la creation du nouveau ticket
					String message = "Bonjour, un ticket wifi vient d'être crée avec le login "+ ticket.cptLogin();
					// Envoie du mail à l'alias
					new MyCRIMailBus(cktlConfig).sendMail(
							"noreply@"+grhumDomainePrincipal,
							alias,
							null,
							"[AGrhum]Notification de la création d'un nouveau ticket Wifi",
							message);
				}
			}
		}
	}
}
