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


import org.cocktail.agrhum.serveur.AGrhumApplicationUser;
import org.cocktail.fwkcktlajaxwebext.serveur.component.CktlAjaxWindow;
import org.cocktail.fwkcktlpersonne.common.metier.EORepartStructure;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSValidation;

import er.extensions.appserver.ERXDisplayGroup;
import com.webobjects.foundation.NSMutableArray;
import org.cocktail.fwkcktlpersonne.common.metier.EORepartAssociation;


public class PersonneMultipleSrchPage extends MyWOComponent {
    
    private static final long serialVersionUID = -809334119825966152L;
    private boolean resetAjoutMembresDialog;
	private EOEditingContext editingContext;
	private ERXDisplayGroup<IPersonne> selectedPersonnesDisplayGroup;
	private EOStructure structure;
	private NSMutableArray<EORepartAssociation> selectedRepartAssociationsList;
	private boolean isSearchMode = true;
	private boolean resetAjoutRolesDialog;
    public PersonneMultipleSrchPage(WOContext context) {
        super(context);
    }

    public boolean isResetAjoutMembresDialog() {
        return resetAjoutMembresDialog;
    }
    
    public void setResetAjoutMembresDialog(boolean resetAjoutMembresDialog) {
        this.resetAjoutMembresDialog = resetAjoutMembresDialog;
    }

	public WOActionResults ajouterPersonnes() {
	    NSArray<IPersonne> selectedPersonnes = selectedPersonnesDisplayGroup().displayedObjects();
	    // On créer les repartStructures si necessaires pour chacune des personnes
	    for (IPersonne personne : selectedPersonnes) {
	    	EORepartStructure reparStructurePersonne = EORepartStructure.creerInstanceSiNecessaire(
	                getEditingContext(), personne, getStructure(), session().applicationUser().getPersId());
	    	
	    	// On créer les repartAssociations si necessaires pour chaque association selectionnée et pour chacune des personnes
		    if (getSelectedRepartAssociationsList()!=null && !getSelectedRepartAssociationsList().isEmpty()) {
				for (EORepartAssociation selectedRepartAsso : getSelectedRepartAssociationsList()) {
					
					EORepartAssociation repartAssociation = EORepartAssociation.creerInstance(getEditingContext());
					repartAssociation.setPersIdModification(getApplicationUser().getPersId());
					repartAssociation.setToAssociationRelationship(selectedRepartAsso.toAssociation().localInstanceIn(getEditingContext()));
					repartAssociation.setRasDOuverture(selectedRepartAsso.rasDOuverture());
					repartAssociation.setRasDFermeture(selectedRepartAsso.rasDFermeture());
					repartAssociation.setRasQuotite(selectedRepartAsso.rasQuotite());
					repartAssociation.setRasCommentaire(selectedRepartAsso.rasCommentaire());
					repartAssociation.setRasRang(selectedRepartAsso.rasRang());
					
					reparStructurePersonne.addToToRepartAssociationsRelationship(repartAssociation);
					
					//nettoyer les repartAssociation vides
					reparStructurePersonne.nettoieRepartAssociationVides();
				}
				// On enregistre le fait que l'on ait manipulé la personne
				// FIX JULIEN on le fait plus finalement
				// personne.setPersIdModification(getApplicationUser().getPersId());
			}
		    
	    }
	    // enregistrer l'ajout 
	    
	    try {
	    	getEditingContext().saveChanges();
			session().addSimpleInfoMessage(session().localizer().localizedStringForKey("confirmation"), null);
//			updateDisplayGroup();
			CktlAjaxWindow.close(context());
			
		} catch (NSValidation.ValidationException e) {
			logger().info(e.getMessage(), e);
			session().addSimpleErrorMessage(e.getLocalizedMessage(), e);
			context().response().setStatus(500);
		} catch (EOGeneralAdaptorException e) {
			getEditingContext().revert();
			logger().warn(e.getMessage(), e);
			context().response().setStatus(500);
			session().addSimpleErrorMessage("Une erreur est survenue lors de l'enregistrement en base", e);
		}
        
	    return null;
	}

	public WOActionResults annulerAjout() {
	    CktlAjaxWindow.close(context());
	    return null;
	}

	public WOActionResults choisirRoles() {
			setSearchMode(false);
		    return null;
	}
	
	public WOActionResults retournerSelectionMode() {
		setSelectedRepartAssociationsList(null);
		setSearchMode(true);
		setResetAjoutRolesDialog(true);
		return null;
	}

	public String ajouterPersonnesLabel() {
	    return "Ajouter ces personnes au groupe " + getStructure().libelle();
	}

	public EOEditingContext getEditingContext() {
	    return editingContext;
	}

	public void setEditingContext(EOEditingContext editingContext) {
	    this.editingContext = editingContext;
	}

	public ERXDisplayGroup<IPersonne> selectedPersonnesDisplayGroup() {
	    return selectedPersonnesDisplayGroup;
	}

	public void setSelectedPersonnesDisplayGroup(ERXDisplayGroup<IPersonne> selectedPersonnesDisplayGroup) {
	    this.selectedPersonnesDisplayGroup = selectedPersonnesDisplayGroup;
	}

	public EOStructure getStructure() {
	    return structure;
	}

	public void setStructure(EOStructure structure) {
	    this.structure = structure;
	}

	/**
	 * @return the selectedRepartAssociationsList
	 */
	public NSMutableArray<EORepartAssociation> getSelectedRepartAssociationsList() {
		if (selectedRepartAssociationsList==null) {
			setSelectedRepartAssociationsList(new NSMutableArray<EORepartAssociation>());
		}
		return selectedRepartAssociationsList;
	}

	/**
	 * @param selectedRepartAssociationsList the selectedRepartAssociationsList to set
	 */
	public void setSelectedRepartAssociationsList(NSMutableArray<EORepartAssociation> selectedRepartAssociationsList) {
		this.selectedRepartAssociationsList = selectedRepartAssociationsList;
	}

	/**
	 * @return the isSearchMode
	 */
	public boolean isSearchMode() {
		return isSearchMode;
	}

	/**
	 * @param isSearchMode the isSearchMode to set
	 */
	public void setSearchMode(boolean isSearchMode) {
		this.isSearchMode = isSearchMode;
	}

	public String getPersonneMultipleUpdateContainerId() {
		return getComponentId() + "_PersonneMultipleSearchPage";
	}

	public boolean isResetAjoutRolesDialog() {
		return resetAjoutRolesDialog;
	}

	public void setResetAjoutRolesDialog(boolean resetAjoutRolesDialog) {
		this.resetAjoutRolesDialog = resetAjoutRolesDialog;
	}
	
	public AGrhumApplicationUser getApplicationUser() {
		return session().applicationUser();
	}

}