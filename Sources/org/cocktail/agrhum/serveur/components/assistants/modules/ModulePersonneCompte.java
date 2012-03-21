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

import org.cocktail.fwkcktlpersonne.common.metier.EOCompte;
import org.cocktail.fwkcktlpersonne.common.metier.EOVlans;

import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSValidation;

import er.extensions.eof.ERXQ;

public class ModulePersonneCompte extends ModuleAssistant implements IModuleAssistant {

	private static final long serialVersionUID = -916223179991840931L;
	private static final String BINDING_VLANS_AUTORISES = "vlansAutorises";
	private EOCompte selectedCompte;
	private NSArray vlansAutorises;
	
	public ModulePersonneCompte(WOContext context) {
        super(context);
    }
	
	@Override
	public boolean valider() {
//		if (selectedCompte == null)
//			throw new NSValidation.ValidationException(
//					localizer().localizedStringForKey("compte.oblig"));
		return true;
	}
	
	public EOCompte getSelectedCompte() {
		return selectedCompte;
	}
	
	public void setSelectedCompte(EOCompte selectedCompte) {
		this.selectedCompte = selectedCompte;
	}
	
	@SuppressWarnings("unchecked")
    public NSArray getVlansAutorises() {
		if (vlansAutorises == null) {
			/*
		    String[] vlans = null;
		    if (hasBinding(BINDING_VLANS_AUTORISES)) {
		        vlans = (String[])valueForBinding(BINDING_VLANS_AUTORISES);
		    } else {
		        vlans = new String[] { EOVlans.VLAN_E, EOVlans.VLAN_P, EOVlans.VLAN_R, EOVlans.VLAN_X };
		    }
			EOQualifier qual = ERXQ.inObjects(EOVlans.C_VLAN_KEY, vlans);
			vlansAutorises = EOVlans.fetchAll(editingContext(), qual);
			*/
			return EOVlans.fetchAllVlansPourPersonne(personne(), editingContext());
		}
		return vlansAutorises;
	}
	
}