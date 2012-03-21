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

package org.cocktail.agrhum.serveur.components.assistants.modules;

import org.cocktail.fwkcktlpersonne.common.metier.EOCivilite;
import org.cocktail.fwkcktlpersonne.common.metier.EOIndividu;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.foundation.NSArray;

public class ModulePersonneSearch extends ModuleAssistant implements IModuleAssistant {

	private static final long serialVersionUID = -7622396899954356481L;
	private WODisplayGroup displayGroup;
//	private Boolean wantReset = Boolean.TRUE;

	private static final String MODE_INDIVIDU_BDG = "modeIndividu";
	
	public ModulePersonneSearch(WOContext context) {
        super(context,null);
    }
	
	public boolean showIndividus() {
		return hasBinding(MODE_INDIVIDU_BDG) && 
			   (Boolean)valueForBinding(MODE_INDIVIDU_BDG);
	}
	
	public boolean showStructure() {
		return hasBinding(MODE_INDIVIDU_BDG) && 
		   	   !(Boolean)valueForBinding(MODE_INDIVIDU_BDG);
	}
	
	public boolean isTerminerDisabled() {
		return true;
	}

	public void onSuivant() {
//		personne().editingContext().
	}

	public boolean isSuivantDisabled() {
		if (displayGroup() != null && displayGroup().selectedObject() != null) {
			return false;
		}
		return true;
	}

	public void onPrecedent() {
	}

	public boolean valider() {
		return true;
	}


	/**
	 * @return the displayGroup
	 */
	public WODisplayGroup displayGroup() {
		return displayGroup;
	}

	/**
	 * @param displayGroup the displayGroup to set
	 */
	public void setDisplayGroup(WODisplayGroup displayGroup) {
		this.displayGroup = displayGroup;
	}

	@SuppressWarnings("unchecked")
	public WOActionResults onCreerPersonne() {
		if (personne() instanceof EOStructure) {
			//EOStructure structure = (EOStructure) personne();
			// structure.setValidationEditingContext(editingContext());
		} else if (personne() instanceof EOIndividu) {
			EOIndividu individu = (EOIndividu) personne();
			// individu.setValidationEditingContext(editingContext());
			if (individu.toCivilite() == null) {
				NSArray<EOCivilite> civilites = EOCivilite.fetchAll(editingContext());
				individu.setToCiviliteRelationship((EOCivilite) civilites.objectAtIndex(0));
			}
		}
		
		if (personne().persIdCreation() == null) {
			personne().setPersIdCreation(utilisateurPersId());
		}
		if (personne().persIdModification() == null) {
			personne().setPersIdModification(utilisateurPersId());
		}
		return performParentAction("suivant");
	}

//	/**
//	 * @return the wantReset
//	 */
//	public Boolean wantReset() {
//		return wantReset;
//	}
//
//	/**
//	 * @param wantReset the wantReset to set
//	 */
//	public void setWantReset(Boolean wantReset) {
//		this.wantReset = wantReset;
//	}

}
