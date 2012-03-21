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

import org.cocktail.fwkcktlpersonne.common.eospecificites.EOStructureForFournisseurSpec;
import org.cocktail.fwkcktlpersonne.common.metier.EOCompte;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;
import org.cocktail.fwkcktlpersonne.common.metier.EOVlans;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.eof.ERXEC;

public class EditGroupe extends MyWOComponent {
	
	private static final long serialVersionUID = -1941683715892047068L;
	private static final String ONVALIDER_BDG = "onValider";
	private static final String ONANNULER_BDG = "onAnnuler";
	private static final String EDITING_CONTEXT_BDG = "edc";
	private static final String STRUCTURE_BDG = "structure";
	private static final String WANT_RESET = "wantReset";
	
	private boolean isEditing;
	private boolean isCoordonnesTabSelected;
	private boolean isMembresTabSelected;
	private boolean isDetailTabSelected;
	private boolean isGroupesTabSelected;
	
	private boolean isAdminTabEditing;
	private boolean isGroupesTabEditing;
	private boolean isMembresTabEditing;
	private boolean isTelephoneTabEditing;
	private boolean isAdresseTabEditing;
	
	private boolean isPersonneAdminEditing;
	private boolean isNomEditing;
	private EOEditingContext nestedEcForNomEditing;
	
	private EOCompte selectedCompte;
    private NSArray<EOVlans> vlansAutorises;
	private boolean isRibEditing=false;
	private Boolean  wantRefreshGroupeUI;
	
    public EditGroupe(WOContext context) {
        super(context);
    }

    @Override
    public boolean synchronizesVariablesWithBindings() {
    	return false;
    }
    
    @Override
    public void appendToResponse(WOResponse woresponse, WOContext wocontext) {
    	if (wantReset()) {
    		setDetailTabSelected(false);
    		setCoordonnesTabSelected(false);
    		setGroupesTabSelected(false);
    		setMembresTabSelected(true);
    		setWantRefreshGroupeUI(Boolean.TRUE);
    		vlansAutorises = null;
    		setWantReset(false);
    	}
    	super.appendToResponse(woresponse, wocontext);
    }
    
    public WOActionResults valider() {
		return (WOActionResults)valueForBinding(ONVALIDER_BDG);
	}
    
	public WOActionResults annuler() {
		editingContext().revert();
		setStructure(null);
		setDetailTabSelected(false);
		setCoordonnesTabSelected(false);
		setMembresTabSelected(false);
		return (WOActionResults)valueForBinding(ONANNULER_BDG);
	}
	
	public EOEditingContext editingContext() {
		return (EOEditingContext) valueForBinding(EDITING_CONTEXT_BDG);
	}

	public EOEditingContext getNestedEcForNomEditing() {
	    if (nestedEcForNomEditing == null)
	        nestedEcForNomEditing = ERXEC.newEditingContext(editingContext());
        return nestedEcForNomEditing;
    }
	
	public void setStructure(EOStructure structure) {
		setValueForBinding(structure, STRUCTURE_BDG);
	}
	
	public EOStructure structure() {
		return (EOStructure)valueForBinding(STRUCTURE_BDG);
	}
	
	public String structureRattachementId() {
	    return "StrR_" + uniqueDomId();
	}
	
	public String tabsId() {
		return "Tabs_" + uniqueDomId();
	}
	
	public String tabDetailId() {
	    return "Details_" + uniqueDomId();
	}
	
	public String tabCoordonneesId() {
	    return "Coord_" + uniqueDomId();
	}
	
	public String membreTelId() {
	    return "MemTab_" + uniqueDomId();
	}

	public String tabGroupesId() {
	    return "Groups_" + uniqueDomId();
	}
	
	public String tabComptesId() {
	    return "Comptes_" + uniqueDomId();
	}
	
	public boolean isEditing() {
		return isEditing;
	}

	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}

	public Boolean wantReset() {
		return hasBinding(WANT_RESET) && (Boolean)valueForBinding(WANT_RESET);
	}
	
	public void setWantReset(Boolean value) {
		setValueForBinding(value, WANT_RESET);
	}
	
    public Boolean getWantRefreshGroupeUI() {
		return wantRefreshGroupeUI;
	}

	public void setWantRefreshGroupeUI(Boolean wantRefreshGroupeUI) {
		this.wantRefreshGroupeUI = wantRefreshGroupeUI;
	}

	public NSArray<EOVlans> vlansAutorises() {
        if (vlansAutorises == null) {
            vlansAutorises = EOVlans.fetchAllVlansPourPersonne(structure(), editingContext());
        }
        return vlansAutorises;
    }
	
	public boolean isGroupesTabEditing() {
		return isGroupesTabEditing;
	}

	public void setGroupesTabEditing(boolean isGroupesTabEditing) {
		this.isGroupesTabEditing = isGroupesTabEditing;
	}

	public boolean isMembresTabEditing() {
		return isMembresTabEditing;
	}

	public void setMembresTabEditing(boolean isMembresTabEditing) {
		this.isMembresTabEditing = isMembresTabEditing;
	}

	public boolean isTelephoneTabEditing() {
		return isTelephoneTabEditing;
	}

	public void setTelephoneTabEditing(boolean isTelephoneTabEditing) {
		this.isTelephoneTabEditing = isTelephoneTabEditing;
	}

	public boolean isAdresseTabEditing() {
		return isAdresseTabEditing;
	}

	public void setAdresseTabEditing(boolean isAdresseTabEditing) {
		this.isAdresseTabEditing = isAdresseTabEditing;
	}

	public boolean isCoordonnesTabSelected() {
        return isCoordonnesTabSelected;
    }
	
	public void setCoordonnesTabSelected(boolean isCoordonnesTabSelected) {
        this.isCoordonnesTabSelected = isCoordonnesTabSelected;
    }

	public boolean isMembresTabSelected() {
		return isMembresTabSelected;
	}

	public void setMembresTabSelected(boolean isMembresTabSelected) {
		this.isMembresTabSelected = isMembresTabSelected;
	}

	public boolean isDetailTabSelected() {
		return isDetailTabSelected;
	}

	public void setDetailTabSelected(boolean isDetailTabSelected) {
		this.isDetailTabSelected = isDetailTabSelected;
	}

	public boolean isGroupesTabSelected() {
		return isGroupesTabSelected;
	}

	public void setGroupesTabSelected(boolean isGroupesTabSelected) {
		this.isGroupesTabSelected = isGroupesTabSelected;
	}
	
	public boolean isAdminTabEditing() {
        return isAdminTabEditing;
    }
	
	public void setAdminTabEditing(boolean isAdminTabEditing) {
        this.isAdminTabEditing = isAdminTabEditing;
    }
	
	public boolean isNomEditing() {
        return isNomEditing;
    }
	
	public void setNomEditing(boolean isNomEditing) {
        this.isNomEditing = isNomEditing;
    }
	
	public boolean isPersonneAdminEditing() {
        return isPersonneAdminEditing;
    }
	
	public void setPersonneAdminEditing(boolean isPersonneAdminEditing) {
        this.isPersonneAdminEditing = isPersonneAdminEditing;
    }
	
	public EOCompte getSelectedCompte() {
        return selectedCompte;
    }
	
	public void setSelectedCompte(EOCompte selectedCompte) {
        this.selectedCompte = selectedCompte;
    }
	
	public boolean isStructureSelected() {
		return structure()!=null;
	}
	
	public boolean isStructureFournisseur() {
		boolean isStructureFournisseur = false;
		if (isStructureSelected() && structure().isStructure()) {
//			isStructureFournisseur = EOStructureForFournisseurSpec.sharedInstance().isSpecificite((AfwkPersRecord) structure());
			isStructureFournisseur = EOStructureForFournisseurSpec.sharedInstance().isSpecificite(structure());
		}
		return isStructureFournisseur;
	}
	
	/**
	 * @return the isRibEditing
	 */
	public boolean isRibEditing() {
		return isRibEditing;
	}

	/**
	 * @param isRibEditing the isRibEditing to set
	 */
	public void setIsRibEditing(boolean isRibEditing) {
		this.isRibEditing = isRibEditing;
	}
	
	
	public boolean canEditGroupeParent() {
		return applicationUser().hasDroitGererGroupe(structure());
	}
	
	
	public Boolean canEditGroupe() {
		return applicationUser().hasDroitGererGroupe(structure());
	}
	
	/**
	 * Partie concernant les infos de modification de la fiche
	 * @return
	 */
	public String retourneCreateur (){
		String createur = ((EOStructure)structure()).retourneCreateur();
		return createur;
	}

	public String retourneModificateur (){
		String modificateur = ((EOStructure)structure()).retourneModificateur();
		return modificateur;
	}
	
	public NSTimestamp retourneDateCreation (){
		NSTimestamp dCreation = ((EOStructure)structure()).retourneDateCreation();
		return dCreation;
	}
	
	public NSTimestamp retourneDateModification (){
		NSTimestamp dModification = ((EOStructure)structure()).retourneDateModification();
		return dModification;
	}
	
	public String getTimestampFormatter() {
		return DateCtrl.DEFAULT_FORMAT;
	}
}