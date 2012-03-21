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

import org.apache.log4j.Logger;
import org.cocktail.agrhum.serveur.Application;
import org.cocktail.agrhum.serveur.metier.TicketService;
import org.cocktail.fwkcktlajaxwebext.serveur.component.CktlAjaxWindow;
import org.cocktail.fwkcktlajaxwebext.serveur.component.tableview.column.CktlAjaxTableViewColumn;
import org.cocktail.fwkcktlajaxwebext.serveur.component.tableview.column.CktlAjaxTableViewColumnAssociation;
import org.cocktail.fwkcktlpersonne.common.metier.EOCompte;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSValidation;

import er.ajax.AjaxUpdateContainer;
import er.extensions.appserver.ERXWOContext;
import er.extensions.eof.ERXQ;

public class TicketsAdmin extends MyWOComponent {
    
    private static final long serialVersionUID = 3259725569283081330L;
    private static final String BINDING_EDC = "editingContext";
    private static final Logger LOG = Logger.getLogger(TicketsAdmin.class);

    private String filtre;
    private WODisplayGroup displayGroup;
    private NSArray<CktlAjaxTableViewColumn> colonnes;
    private EOCompte ticket;
    private String containerId;
    private boolean willRefreshEditor=false;
    
    public TicketsAdmin(WOContext context) {
        super(context);
    }
    
    public EOEditingContext editingContext() {
        return (EOEditingContext)valueForBinding(BINDING_EDC);
    }
    
    public WOActionResults filtrer() {
        EOQualifier qual = ERXQ.contains(EOCompte.CPT_LOGIN_KEY, filtre);
        displayGroup().setObjectArray(ERXQ.filtered(allTickets(), qual));
        return null;
    }
    
    
    
    public WOActionResults onSelection() {
    	setWillRefreshEditor(true);
        return null;
    }
    
    public WOActionResults creerTicket() {
    	setIsTicketCreationMode(true);
    	setTicketCreation(null);
        displayGroup().setSelectedObject(null);
        setWillRefreshEditor(true);
        return null;
    }
    
    public WOActionResults refreshAfterCreation() {
		setTicketCreation(null);
		setWillRefreshEditor(true);
		return afficherTous();
	}
    
    public WOActionResults afficherTous() {
        displayGroup().setObjectArray(allTickets());
        return null;
    }
    
    public WODisplayGroup displayGroup() {
        if (displayGroup == null) {
            if (displayGroup == null) {
                displayGroup = new WODisplayGroup();
                displayGroup.setObjectArray(allTickets());
            }
        }
        return displayGroup;
    }
    
    private NSArray<EOCompte> allTickets() {
        NSArray<EOCompte> tickets = NSArray.EmptyArray;
        try {
            tickets = TicketService.findAllTickets(editingContext());
        } catch (Exception e) {
            session().addSimpleErrorMessage(e.getLocalizedMessage(), e);
        }
        return tickets;
    }
    
    public NSArray<CktlAjaxTableViewColumn> colonnes() {
        if (colonnes == null) {
            CktlAjaxTableViewColumn col0 = new CktlAjaxTableViewColumn();
            CktlAjaxTableViewColumnAssociation asso0 = new CktlAjaxTableViewColumnAssociation("ticket."+EOCompte.CPT_LOGIN_KEY, null);
            col0.setAssociations(asso0);
            col0.setLibelle("Login");
            CktlAjaxTableViewColumn col1 = new CktlAjaxTableViewColumn();
            CktlAjaxTableViewColumnAssociation asso1 = new CktlAjaxTableViewColumnAssociation("ticket."+EOCompte.CPT_FIN_VALIDE_KEY, "Jamais");
            asso1.setDateformat(Application.DEFAULT_TIMESTAMP_FORMATTER);
            col1.setAssociations(asso1);
            col1.setLibelle("Date expiration");
            col1.setAssociations(asso1);
            colonnes = new NSArray<CktlAjaxTableViewColumn>(col0, col1);
        }
        return colonnes;
    }
    
    public boolean hasTickets() {
        return displayGroup().displayedObjects().count() > 0;
    }
    
    public String containerId() {
        if (containerId == null)
            containerId = "TicketAdmin"+ERXWOContext.safeIdentifierName(context(), true);
       return containerId;
    }
    
    public String getFiltre() {
        return filtre;
    }
    
    public void setFiltre(String filtre) {
        this.filtre = filtre;
    }

    public EOCompte getTicket() {
        return ticket;
    }

    public void setTicket(EOCompte ticket) {
        this.ticket = ticket;
    }

	private boolean isTicketCreationMode;

	/**
	 * @return the isTicketCreationMode
	 */
	public boolean isTicketCreationMode() {
		return isTicketCreationMode;
	}

	/**
	 * @param isTicketCreationMode the isTicketCreationMode to set
	 */
	public void setIsTicketCreationMode(boolean isTicketCreationMode) {
		this.isTicketCreationMode = isTicketCreationMode;
	}

	private EOCompte ticketCreation;

	/**
	 * @return the ticketCreation
	 */
	public EOCompte getTicketCreation() {
		return ticketCreation;
	}

	/**
	 * @param ticketCreation the ticketCreation to set
	 */
	public void setTicketCreation(EOCompte ticketCreation) {
		this.ticketCreation = ticketCreation;
	}

	/**
	 * @return the willRefreshEditor
	 */
	public boolean isWillRefreshEditor() {
		return willRefreshEditor;
	}

	/**
	 * @param willRefreshEditor the willRefreshEditor to set
	 */
	public void setWillRefreshEditor(boolean willRefreshEditor) {
		this.willRefreshEditor = willRefreshEditor;
	}

	public WOActionResults supprimerTicket() {
		try {
			TicketService.deleteTicket(editingContext(), (EOCompte)displayGroup().selectedObject());
			//// Save changes de l'EDC 
        	//editingContext().saveChanges();

			session().addSimpleInfoMessage(session().localizer().localizedStringForKey("confirmation"), null);
			afficherTous();
		} catch (NSValidation.ValidationException e) {
			logger().info(e.getMessage(), e);
			session().addSimpleErrorMessage(e.getLocalizedMessage(), e);
			context().response().setStatus(500);
		} catch (Exception e) {
			editingContext().revert();
			logger().warn(e.getMessage(), e);
			context().response().setStatus(500);
			session().addSimpleErrorMessage("Une erreur est survenue lors de l'enregistrement en base", e);
		} 
		
		return null;
	}

	public boolean isDelTicketEnable() {
		return (displayGroup().selectedObject()!=null);
		//return false ;
	}
	
}