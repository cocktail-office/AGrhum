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

package org.cocktail.agrhum.serveur.components.controlers;

import org.cocktail.agrhum.serveur.AGrhumApplicationUser;
import org.cocktail.agrhum.serveur.components.Accueil;
import org.cocktail.agrhum.serveur.components.GestionContacts;
import org.cocktail.agrhum.serveur.components.GestionDroits;
import org.cocktail.agrhum.serveur.components.GestionFournisseurs;
import org.cocktail.agrhum.serveur.components.GestionFournisseursInternes;
import org.cocktail.agrhum.serveur.components.GestionGroupes;
import org.cocktail.agrhum.serveur.components.GestionHeberges;
import org.cocktail.agrhum.serveur.components.GestionParametres;
import org.cocktail.agrhum.serveur.components.GestionRolesEtFonctions;
import org.cocktail.agrhum.serveur.components.GestionStructures;
import org.cocktail.agrhum.serveur.components.GestionTickets;
import org.cocktail.agrhum.serveur.components.GestionTypeAssociation;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.eocontrol.EOEditingContext;

public class AccueilCtrl {
    
	public Accueil wocomponent;
	public EOEditingContext edc;
	
	public AccueilCtrl(Accueil aWOComponent) {
		wocomponent = aWOComponent;
		edc = wocomponent.session().defaultEditingContext();
    }

//	public WOActionResults creerUnePersonne() {
//	    resetSessionState();
//		GestionIndividu nextPage = (GestionIndividu)(wocomponent.pageWithName(
//											  GestionIndividu.class.getName()));
//		wocomponent.session().setIndexModuleActifGestionPersonne(null);
//		return nextPage;
//	}
	
	public WOActionResults gererHeberges() {
        GestionHeberges page = (GestionHeberges) wocomponent.pageWithName(GestionHeberges.class
                .getName());
        page.setSectionHeberge(wocomponent.localizer().localizedStringForKey("indiv.heberge"));
        return page;
    }
	
	public WOActionResults gererFournisseurs() {
		resetSessionState();
        GestionFournisseurs page = (GestionFournisseurs) wocomponent.pageWithName(GestionFournisseurs.class.getName());
        page.setSectionFournisseur(wocomponent.localizer().localizedStringForKey("indiv.fournisseur"));
        page.setModeIndividu(true);
        return page;
    }
	
	public WOActionResults gererTicketWifi() {
	    resetSessionState();
	    GestionTickets nextPage = (GestionTickets)wocomponent.pageWithName(
	                                            GestionTickets.class.getName());
	    wocomponent.session().setIndexModuleActifGestionTicket(null);
	    return nextPage;
	}
	

	public WOActionResults gererContact() {
	    resetSessionState();
	    GestionContacts nextPage = (GestionContacts)wocomponent.pageWithName(
	            GestionContacts.class.getName());
	    wocomponent.session().setIndexModuleActifGestionTicket(null);
	    return nextPage;
	}
	
	/**
	 * Vers la gestion des groupes.
	 */
	public WOActionResults gererGroupes() {
	    resetSessionState();
		return wocomponent.pageWithName(GestionGroupes.class.getName());
	}

	
	/**
	 * Vers la gestion des structures de type fournisseurs.
	 */
	public WOActionResults gererFournisseursStr() {
	    resetSessionState();
	    GestionFournisseurs page = (GestionFournisseurs) wocomponent.pageWithName(GestionFournisseurs.class.getName());
        page.setSectionFournisseur(wocomponent.localizer().localizedStringForKey("str.fourniss"));
        page.setModeIndividu(false);
		return page;
	}
	
	/**
	 * Vers la gestion des structures de type fournisseurs internes, ie: rattachées à au moins un enregistrement de la table organe.
	 */
	public WOActionResults gererFournisseursStrInternes() {
	    resetSessionState();
	    GestionFournisseursInternes page = (GestionFournisseursInternes) wocomponent.pageWithName(GestionFournisseursInternes.class.getName());
        page.setSectionFournisseurInterne(wocomponent.localizer().localizedStringForKey("str.fourniss.internes"));
//        page.setModeIndividu(false);
		return page;
	}
	
	
	/**
	 * Vers la gestion des structures autres (tout type).
	 */
	public WOActionResults gererAutresStr() {
	    resetSessionState();
		GestionStructures nextPage = 
			(GestionStructures)wocomponent.pageWithName(GestionStructures.class.getName());
		nextPage.setSectionStructure(wocomponent.localizer().localizedStringForKey("str.autres"));
		return nextPage;
	}
	
	public WOActionResults gererDroits() {
	    resetSessionState();
	    GestionDroits nextPage = 
	        (GestionDroits)wocomponent.pageWithName(GestionDroits.class.getName());
	    return nextPage;
	}
	
	/*
	 ****************************************************************************************************** 
	 */
	/**
	 * Vers la gestion des paramètres du référentiel
	 * @author Alain Malaplate
	 * Modification du 28/01/2011
	 */
	
	public WOActionResults gererParametres() {
		resetSessionState();
		GestionParametres page = (GestionParametres)wocomponent.pageWithName(GestionParametres.class.getName());
        page.setSectionParametres(wocomponent.localizer().localizedStringForKey("gestion.parametres"));

		return page;
	}
	
	
	/**
	 * Vers la gestion des rôles et fonction du référentiel
	 * @author Alain Malaplate
	 * Modification du 18/07/2011
	 */
	public WOActionResults gererRoles() {
		resetSessionState();
		GestionRolesEtFonctions page = (GestionRolesEtFonctions)wocomponent.pageWithName(GestionRolesEtFonctions.class.getName());
        page.setSectionRolesFonctions(wocomponent.localizer().localizedStringForKey("gestion.rolesEtFonctions"));

		return page;
	}	
		/**
		 * Vers la gestion des types d'Association du référentiel
		 * @author Alain Malaplate
		 * Modification du 18/07/2011
		 */
		public WOActionResults gererTypesAssociation() {
			resetSessionState();
			GestionTypeAssociation page = (GestionTypeAssociation)wocomponent.pageWithName(GestionTypeAssociation.class.getName());
	        page.setSectionTypeAssociation(wocomponent.localizer().localizedStringForKey("gestion.typeAssociation"));

			return page;
	}
		
	
	public boolean isGrhumManager() {
		AGrhumApplicationUser user = wocomponent.session().applicationUser();
//		System.out.println("AccueilCtrl.isGrhumManager() " + user.hasDroitGrhumCreateur());
		return user.hasDroitGrhumCreateur();
	}
	
	/*
	 ****************************************************************************************************** 
	 */
	
	
	private void resetSessionState() {
	    wocomponent.session().setIndexModuleActifGestionPersonne(0);
	    wocomponent.session().setIndexModuleActifGestionTicket(0);
	}
	
	public boolean fournisseursDisabled() {
	    AGrhumApplicationUser user = wocomponent.session().applicationUser();
//	    return !user.peutConsulterGrhum() && !user.peutCreerFournisseur() && !user.peutValiderFournisseur() && !user.peutModifierFournisseur();
	    return !user.peutCreerFournisseur() && !user.peutValiderFournisseur(); // && !user.peutModifierFournisseur(null);
	}
	
	public boolean fournisseursInternesDisabled() {
	    AGrhumApplicationUser user = wocomponent.session().applicationUser();
	    return !user.peutGererFournisseursInternes();
	}
	
	public boolean hebergesDisabled() {
//	    return !wocomponent.session().applicationUser().hasDroitCreerIndividu(null);
	    return !wocomponent.session().applicationUser().getGrhApplicationUser().peutGererHeberges();
	}
	
	public boolean ticketsDisabled() {
	    return !wocomponent.session().applicationUser().hasDroitCompteTempoVisualisation();
	}

	public boolean contactsDisabled() {
	    return !wocomponent.session().applicationUser().hasDroitCreerIndividu(null);
	}
	
}