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

package org.cocktail.agrhum.serveur.components;

import org.cocktail.agrhum.serveur.metier.TicketService;
import org.cocktail.fwkcktlpersonne.common.metier.EOCompte;
import org.cocktail.fwkcktlpersonne.common.metier.EOVlans;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;
import org.cocktail.sync.ref.SynchroComptePersonne;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSForwardException;

import er.extensions.eof.ERXEC;

public class GestionTickets extends MyWOComponent {

    private static final long serialVersionUID = -900353089569764789L;
    private EOEditingContext editingContextInvites;
    private EOEditingContext editingContextTickets;
    private EOCompte ticket;
    private String[] vlanExterieur;
    private IPersonne selectedPersonne;

    public GestionTickets(WOContext context) {
        super(context);
        TicketService.ticketManagementEnabled(editingContextTickets());
        //TicketService.invitesManagementEnabled(editingContextInvites());
    }

    /**
     * @return the editingContext
     */
    public EOEditingContext editingContextInvites() {
        if (editingContextInvites == null) {
            editingContextInvites = ERXEC.newEditingContext();
        }
        return editingContextInvites;
    }

    /**
     * @return the editingContext
     */
    public EOEditingContext editingContextTickets() {
        if (editingContextTickets == null) {
            editingContextTickets = ERXEC.newEditingContext();
        }
        return editingContextTickets;
    }
    
    /**
     * Appelé lors de l'annulation de l'enregistrement d'un invité WIFI.
     */
    public WOActionResults annuler() {
        if (editingContextInvites() != null) {
            editingContextInvites().revert();
        }
        session().setIndexModuleActifGestionTicket(null);
        return null;
    }

    /**
     * Appelé lors de l'enregistrement d'un invité WIFI.
     */
    public WOActionResults terminer() {
        try {
            if (editingContextInvites().hasChanges()){
                TicketService.ajouterInviteWifi(
                        editingContextInvites(), session().applicationUser().getPersId(), selectedPersonne);
                editingContextInvites().saveChanges();
                SynchroComptePersonne.synchroComptePersonne(selectedPersonne.persId().toString());
                session().addSimpleInfoMessage(localizer().localizedTemplateStringForKeyWithObject("invite.valid", 
                        selectedPersonne), null);
            }
        }  catch (ValidationException e2) {
            logger().info(e2.getMessage(), e2);
            session().addSimpleErrorMessage(e2.getLocalizedMessage(), e2);
        } catch (Exception e) {
            logger().info(e.getMessage(), e);
            throw new NSForwardException(e, "Une erreur est survenue lors de l'enregistrement en base");
        }
        return null;
    }

    public String[] getVlanExterieur() {
        if (vlanExterieur == null) {
            vlanExterieur = new String[] { EOVlans.VLAN_X };
        }
        return vlanExterieur;
    }
    
    /**
     * @return the modules
     */
    public NSArray<String> modules() {
        NSArray<String> modules = new NSArray<String>(new String[]{
                "ModulePersonneSearch",
                "ModuleContactAdmin",
                "ModulePersonneCompte"});
        session().setModulesGestionPersonne(modules);
        return modules;
    }

    public NSArray<String> etapes() {
        NSArray<String> etapes = new NSArray<String>(new String[]{
                "Recherche",
                "Informations",
                "Comptes"});
        return etapes;
    }

    public EOCompte getTicket() {
        return ticket;
    }
    
    public void setTicket(EOCompte ticket) {
        this.ticket = ticket;
    }
    
    public IPersonne getSelectedPersonne() {
        return selectedPersonne;
    }
    
    public void setSelectedPersonne(IPersonne selectedPersonne) {
        this.selectedPersonne = selectedPersonne;
    }
    
}