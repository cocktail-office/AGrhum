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

package org.cocktail.agrhum.serveur;

import org.cocktail.fwkcktlajaxwebext.serveur.CocktailAjaxSession;
import org.cocktail.fwkcktlajaxwebext.serveur.component.CktlAjaxWindow;
import org.cocktail.fwkcktlajaxwebext.serveur.uimessages.CktlUIMessage;
import org.cocktail.fwkcktlpersonne.common.PersonneApplicationUser;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

import er.extensions.foundation.ERXThreadStorage;

public class Session extends CocktailAjaxSession {
	
	private static final long serialVersionUID = 1L;
	
	private AGrhumApplicationUser applicationUser=null;
	
	public Integer indexModuleActifGestionPersonne = null;
	private Integer indexModuleActifGestionTicket = null;
	private Integer indexModuleActifAssistantContactPartenaire = null;
	private Integer indexModuleActifAssistantPartenaire = null;
	
	private NSArray modulesGestionPersonne;
	
	private String generalErrorMessage;

	private NSArray lesEtablissementsAffectation;

	

	

	public Session() {
		super();
		// Initialisation du theme applique a toutes les fenetres gerees via CktlAjaxModalDialog
		setWindowsClassName(CktlAjaxWindow.WINDOWS_CLASS_NAME_BLUELIGHTING);
	}

	public void setApplicationUser(AGrhumApplicationUser appUser) {
		this.applicationUser=appUser;
	}
	public AGrhumApplicationUser applicationUser() {
		return applicationUser;
	}
	
	@Override
	public void awake() {
	    super.awake();
	    if (applicationUser != null)
	        ERXThreadStorage.takeValueForKey(
	                applicationUser.getPersId(), PersonneApplicationUser.PERS_ID_CURRENT_USER_STORAGE_KEY);
	}
	
	public WOActionResults onQuitter() {
		return goToMainSite();
	}

	public void reset() {
		if (defaultEditingContext() != null) {
			defaultEditingContext().revert();
		}
		setIndexModuleActifGestionPersonne(null);
		setIndexModuleActifGestionTicket(null);
	}
	public NSDictionary exceptionInfos() {
		return null;
	}

	/**
	 * @return the indexModuleActifGestionPersonne
	 */
	public Integer indexModuleActifGestionPersonne() {
		if (indexModuleActifGestionPersonne == null) {
			indexModuleActifGestionPersonne = new Integer(0);
		}
		return indexModuleActifGestionPersonne;
	}

	/**
	 * @param indexModuleActifGestionPersonne the indexModuleActifGestionPersonne to set
	 */
	public void setIndexModuleActifGestionPersonne(Integer indexModuleActifGestionPersonne) {
		this.indexModuleActifGestionPersonne = indexModuleActifGestionPersonne;
	}
	
	public Integer indexModuleActifGestionTicket() {
        if (indexModuleActifGestionTicket == null)
            indexModuleActifGestionTicket = Integer.valueOf(0);
        return indexModuleActifGestionTicket;
    }

    public void setIndexModuleActifGestionTicket(
            Integer indexModuleActifGestionTicket) {
        this.indexModuleActifGestionTicket = indexModuleActifGestionTicket;
    }

    /**
	 * @return the modulesGestionPersonne
	 */
	public NSArray modulesGestionPersonne() {
		return modulesGestionPersonne;
	}

	/**
	 * @param modulesGestionPersonne the modulesGestionPersonne to set
	 */
	public void setModulesGestionPersonne(NSArray modulesGestionPersonne) {
		this.modulesGestionPersonne = modulesGestionPersonne;
	}
	
	public String getGeneralErrorMessage() {
        return generalErrorMessage;
    }
	
	public void setGeneralErrorMessage(String generalErrorMessage) {
        this.generalErrorMessage = generalErrorMessage;
    }

	public void setIndexModuleActifAssistantContactPartenaire(
			Integer indexModuleActifAssistantContactPartenaire) {
		this.indexModuleActifAssistantContactPartenaire  = indexModuleActifAssistantContactPartenaire;
	}

	public Integer indexModuleActifAssistantContactPartenaire() {
		if (indexModuleActifAssistantContactPartenaire == null)
			indexModuleActifAssistantContactPartenaire = Integer.valueOf(0);
		return indexModuleActifAssistantContactPartenaire;
	}

	public void setIndexModuleActifAssistantPartenaire(
			Integer indexModuleActifAssistantPartenaire) {
		this.indexModuleActifAssistantPartenaire  = indexModuleActifAssistantPartenaire;
	}

	public Integer indexModuleActifAssistantPartenaire() {
		if (indexModuleActifAssistantPartenaire == null)
			indexModuleActifAssistantPartenaire = Integer.valueOf(0);
		return indexModuleActifAssistantPartenaire;
	}
	
	public NSArray lesEtablissementsAffectation() {
		if (lesEtablissementsAffectation == null) {
			PersonneApplicationUser persAppUser = new PersonneApplicationUser(defaultEditingContext(),applicationUser().getPersId());
			lesEtablissementsAffectation = persAppUser.getEtablissementsAffectation();
		}
		return lesEtablissementsAffectation;
		
	}
	
}
