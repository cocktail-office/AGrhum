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

package org.cocktail.agrhum.serveur.components.assistants;

import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;

import com.webobjects.appserver.WOContext;

public class AssistantPersonne extends Assistant {

	private static final long serialVersionUID = 1862286763294950551L;

	public static final String PERSONNE_BDG = "personne";
	
	private IPersonne personne;
	
	public AssistantPersonne(WOContext context) {
        super(context);
    }
	
	public void reset() {
		personne = null;
	}
	
	public String updateContainerIDOnSelectionnerPersonneInTableview() {
		return "ContainerPersonneGestionMenu";
	}

	/**
	 * @return the personne
	 */
	public IPersonne personne() {
		if (hasBinding(PERSONNE_BDG)) {
			personne = (IPersonne)valueForBinding(PERSONNE_BDG);
		}
		return personne;
	}

	/**
	 * @param personne the personne to set
	 */
	public void setPersonne(IPersonne personne) {
		this.personne = personne;
		if (hasBinding(PERSONNE_BDG)) {
			setValueForBinding(personne, PERSONNE_BDG);
		}
	}

	public boolean isTerminerDisabled() {
		return personne() == null;
	}

	public boolean isPrecedentEnabled() {
		return !super.isPrecedentDisabled();
	}

	public boolean isSuivantEnabled() {
		return !super.isSuivantDisabled();
	}

	public boolean isTerminerEnabled() {
		return !isTerminerDisabled();
	}

	public boolean isInModalWindow() {
		boolean isInModalWin = booleanValueForBinding("isInModalWindow", false);
		return isInModalWin;
	}

}
