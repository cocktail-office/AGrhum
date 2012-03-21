/*
 * Copyright COCKTAIL (www.cockta​il.org), 1995, 2010 This software 
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

import org.cocktail.fwkcktlajaxwebext.serveur.component.CktlAjaxWindow;
import org.cocktail.fwkcktlpersonne.common.metier.EORepartAssociation;
import org.cocktail.fwkcktlpersonne.common.metier.EORepartStructure;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSValidation;

import er.extensions.appserver.ERXDisplayGroup;
import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXEOControlUtilities;
import er.extensions.eof.ERXS;

/**
 * 
 * Composant d'édition des membres d'un groupe :
 * ajout, suppression, déplacement.
 * 
 * @binding structure le groupe (structure) dont on veut éditer les membres
 * @binding editingContext 
 * 
 * @author Alexis TUAL <alexis.tual at cocktail.org>
 *
 */
public class EditMembresGroupe extends MyWOComponent {

    private static final long serialVersionUID = 6700595852450866652L;
    private static final String EDITING_CONTEXT_BDG = "editingContext";
    private static final String STRUCTURE_BDG = "structure";

    private ERXDisplayGroup<IPersonne> selectedPersonnesDisplayGroup = new ERXDisplayGroup<IPersonne>();
    private ERXDisplayGroup<EORepartStructure> membresDisplayGroup = new ERXDisplayGroup<EORepartStructure>();
    private EOStructure structure;
    private EOStructure structureDestination;
    
    private boolean moveMode;
    private boolean isResetAjoutMembresDialog;
    
    private Boolean selectedPersonneHasChanged = false;

    public EditMembresGroupe(WOContext context) {
        super(context);
        membresDisplayGroup.setDelegate(new MembresDisplayGroupDelegate());
    }
    
	public class MembresDisplayGroupDelegate {
		public void displayGroupDidChangeSelection(WODisplayGroup group) {
			setSelectedPersonneHasChanged(true);
		}
    }
    
    @Override
    public boolean synchronizesVariablesWithBindings() {
        return false;
    }
    
    @Override
    public void appendToResponse(WOResponse response, WOContext context) {
        updateDisplayGroup();
        super.appendToResponse(response, context);
    }
    
    public WOActionResults openAjouterMembres() {
        setResetAjoutMembresDialog(true);
        PersonneMultipleSrchPage detailPers = (PersonneMultipleSrchPage)pageWithName(PersonneMultipleSrchPage.class.getName());
//        detailPers.setEditingContext(editingContext());
        detailPers.setEditingContext(getEditingContextForAddMembers());
        detailPers.setSelectedPersonnesDisplayGroup(selectedPersonnesDisplayGroup());
        detailPers.setResetAjoutMembresDialog(true);
//        detailPers.setStructure(structure());
        detailPers.setStructure(structure().localInstanceIn(getEditingContextForAddMembers()));
        return detailPers;
    }
    
    public WOActionResults deleteMembres() {
        NSArray<EORepartStructure> reparts = getMembresDisplayGroup().selectedObjects();
        NSArray<EORepartStructure> localReparts = ERXEOControlUtilities.localInstancesOfObjects(getEditingContextForDelete(), reparts);
        EOStructure localStructure = structure().localInstanceIn(getEditingContextForDelete());
        
        for (EORepartStructure repart : localReparts) {
//            structure().getPersonneDelegate().supprimerAffectationAUnGroupe(editingContext(), session().applicationUser().getPersId(), repart);
        	localStructure.getPersonneDelegate().supprimerAffectationAUnGroupe(getEditingContextForDelete(), session().applicationUser().getPersId(), repart);
        }
        
        // Save changes du EDC specifique
        try {
        	getEditingContextForDelete().saveChanges();
			session().addSimpleInfoMessage(session().localizer().localizedStringForKey("confirmation"), null);
			updateDisplayGroup();
			setEditingContextForDelete(null);
		} catch (NSValidation.ValidationException e) {
			logger().info(e.getMessage(), e);
			session().addSimpleErrorMessage(e.getLocalizedMessage(), e);
			context().response().setStatus(500);
		} catch (EOGeneralAdaptorException e) {
			getEditingContextForDelete().revert();
			logger().warn(e.getMessage(), e);
			context().response().setStatus(500);
			session().addSimpleErrorMessage("Une erreur est survenue lors de l'enregistrement en base", e);
		} 
        
         return refreshMembres();
    }
    
    public WOActionResults refreshMembres() {
        updateDisplayGroup();
        
        return null;
    }
    
    public WOActionResults openDialogForMove() {
        CktlAjaxWindow.open(context(), selectPersDialogId());
        setMoveMode(true);
        return null;
    }
    
    public WOActionResults openDialogForCopy() {
        CktlAjaxWindow.open(context(), selectPersDialogId());
        setMoveMode(false);
        return null;
    }
    
    public WOActionResults moveOrCopyMembres() {
        NSArray<EORepartStructure> reparts = getMembresDisplayGroup().selectedObjects();
        NSArray<EORepartStructure> localReparts = ERXEOControlUtilities.localInstancesOfObjects(getEditingContextForSelectGroup(), reparts);
        EOStructure localStructureDestination = getStructureDestination().localInstanceIn(getEditingContextForSelectGroup());
        
        for (EORepartStructure repart : localReparts) {
        	EORepartStructure.creerInstanceSiNecessaire(getEditingContextForSelectGroup(), repart.toPersonneElt(), localStructureDestination, session().applicationUser().getPersId());
            if (moveMode){
            	EOStructure localStructure = structure().localInstanceIn(getEditingContextForSelectGroup());
            	localStructure.getPersonneDelegate().supprimerAffectationAUnGroupe(getEditingContextForSelectGroup(), session().applicationUser().getPersId(), repart);
            }
                
        }
        
        // Save changes du EDC specifique
        try {
        	getEditingContextForSelectGroup().saveChanges();
			session().addSimpleInfoMessage(session().localizer().localizedStringForKey("confirmation"), null);
			updateDisplayGroup();
			setEditingContextForSelectGroup(null);
	        CktlAjaxWindow.close(context(), selectPersDialogId());
		} catch (NSValidation.ValidationException e) {
			logger().info(e.getMessage(), e);
			session().addSimpleErrorMessage(e.getLocalizedMessage(), e);
			context().response().setStatus(500);
		} catch (EOGeneralAdaptorException e) {
			getEditingContextForSelectGroup().revert();
			throw NSForwardException._runtimeExceptionForThrowable(e);
		}
        return null;
    }
    
    public boolean peutModifierMembreSelectionne() {
        return !aucunMembreSelectionne() && structure() != null && peutModifierGroupeSelectionne();
    }
    
    public boolean peutModifierGroupeSelectionne() {
        return session().applicationUser().peutModifierGroupe(structure());
    }
    
    public boolean hasMembres() {
        return getMembresDisplayGroup().allObjects().count() > 0;
    }
    
    public String membresContId() {
        return "MemCont_" + uniqueDomId();
    }
    
    public EOEditingContext editingContext() {
        return (EOEditingContext) valueForBinding(EDITING_CONTEXT_BDG);
    }
    
    public String ajouterPersonnesLabel() {
        return "Ajouter ces personnes au groupe " + structure().libelle();
    }
    
    public String selectionLabel() {
        if (unSeulMembreSelectionne()) {
            return getMembresDisplayGroup().selectedObject().toPersonneElt().getNomPrenomAffichage();
        } else {
            return getMembresDisplayGroup().selectedObjects().count() + " personnes sélectionnées";
        }
    }
    
    public ERXDisplayGroup<IPersonne> selectedPersonnesDisplayGroup() {
        return selectedPersonnesDisplayGroup;
    }
    
    public ERXDisplayGroup<EORepartStructure> getMembresDisplayGroup() {
        return membresDisplayGroup;
    }
    
    private EOStructure structure() {
        EOStructure structureTmp = (EOStructure)valueForBinding(STRUCTURE_BDG);
        if (!ERXEOControlUtilities.eoEquals(structure, structureTmp)) {
            structure = structureTmp;
        }
        return structure;
    }
    
    public boolean unSeulMembreSelectionne() {
        return getMembresDisplayGroup().selectedObjects().count() == 1;
    }
    
    public boolean aucunMembreSelectionne() {
        return getMembresDisplayGroup().selectedObjects().count() == 0;
    }
    
    public IPersonne personneSelectionnee() {
        return getMembresDisplayGroup().selectedObject().toPersonneElt();
    }
    
    public void updateDisplayGroup() {
        getMembresDisplayGroup().setSelectsFirstObjectAfterFetch(false);
        getMembresDisplayGroup().setObjectArray(structure().toRepartStructuresElts(
                null, ERXS.ascInsensitives(ViewMembresGroupe.LIBELLE_STRUCTURE_KP), false));
        getMembresDisplayGroup().setSelectedObjects(NSArray.EmptyArray);
    }
    
    public String groupesDialogId() {
        return "GrpDiag_" + uniqueDomId();
    }
    
    public String membresDetailId() {
        return "MemDetails_" + uniqueDomId();
    }
    
    public String selectPersDialogId() {
        return "SelectPersDiag_" + uniqueDomId();
    }
    
    public String selectGrpId() {
        return "SelectGrp_" + uniqueDomId();
    }
    
    public EOStructure getStructureDestination() {
        return structureDestination;
    }
    
    public void setStructureDestination(EOStructure structureDestination) {
        this.structureDestination = structureDestination;
    }
    
    public boolean isMoveMode() {
        return moveMode;
    }
    
    public void setMoveMode(boolean moveMode) {
        this.moveMode = moveMode;
    }
    
    public boolean isResetAjoutMembresDialog() {
        return isResetAjoutMembresDialog;
    }
    
    public void setResetAjoutMembresDialog(boolean isResetAjoutMembresDialog) {
        this.isResetAjoutMembresDialog = isResetAjoutMembresDialog;
    }

	private EOEditingContext editingContextForSelectGroup;

	/**
	 * @return the editingContextForSelectGroup
	 */
	public EOEditingContext getEditingContextForSelectGroup() {
		if (editingContextForSelectGroup==null) {
			editingContextForSelectGroup = ERXEC.newEditingContext();
		}
		return editingContextForSelectGroup;
	}
	
	private EOEditingContext editingContextForAddMembers;

	/**
	 * @return the editingContextForSelectGroup
	 */
	public EOEditingContext getEditingContextForAddMembers() {
		if (editingContextForAddMembers==null) {
			editingContextForAddMembers = ERXEC.newEditingContext();
		}
		return editingContextForAddMembers;
	}

	/**
	 * @param editingContextForSelectGroup the editingContextForSelectGroup to set
	 */
	public void setEditingContextForSelectGroup(EOEditingContext editingContextForSelectGroup) {
		this.editingContextForSelectGroup = editingContextForSelectGroup;
	}
	
	private EOEditingContext editingContextForDelete;

	/**
	 * @return the editingContextForSelectGroup
	 */
	public EOEditingContext getEditingContextForDelete() {
		if (editingContextForDelete==null) {
			editingContextForDelete = ERXEC.newEditingContext();
		}
		return editingContextForDelete;
	}

	/**
	 * @param editingContextForSelectGroup the editingContextForSelectGroup to set
	 */
	public void setEditingContextForDelete(EOEditingContext editingContextForDelete) {
		this.editingContextForDelete = editingContextForDelete;
	}

	public Boolean getSelectedPersonneHasChanged() {
		return selectedPersonneHasChanged;
	}

	public void setSelectedPersonneHasChanged(Boolean selectedPersonneHasChanged) {
		this.selectedPersonneHasChanged = selectedPersonneHasChanged;
	}
    
}