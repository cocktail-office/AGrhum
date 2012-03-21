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

import org.cocktail.fwkcktldroitsutils.common.util.MyStringCtrl;
import org.cocktail.fwkcktlpersonne.common.eospecificites.EOStructureForGroupeSpec;
import org.cocktail.fwkcktlpersonne.common.metier.EOCivilite;
import org.cocktail.fwkcktlpersonne.common.metier.EOIndividu;
import org.cocktail.fwkcktlpersonne.common.metier.EORepartStructure;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;
import org.cocktail.fwkcktlpersonneguiajax.serveur.components.PersonneSrch;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSSet;

import er.extensions.appserver.ERXDisplayGroup;
import er.extensions.eof.ERXS;

/**
 * Composant de sélection de personnes de façon multiples et en plusieurs fois :
 * <ul>
 *      <li>A partir de membres d'un groupe existant</li>
 *      <li>A partir d'une recherche de personnes</li>
 * </ul>
 * 
 * @binding selectionDisplayGroup le dg destiné à contenir la sélection de personnes
 * @binding utilisateurPersId le persId du user courant
 * @binding editingContext l'ec utilisé pour les fetch de personnes
 * @binding wantReset (facultatif) indique au composant de se resetter
 * 
 * @author Alexis TUAL <alexis.tual at cocktail.org>
 *
 */
public class PersonneMultipleSrch extends MyWOComponent {

    private static final long serialVersionUID = -7639728708382601304L;
    private static final String BINDING_SELECTION_DG = "selectionDisplayGroup";
    private static final String BINDING_WANT_RESET = "wantReset";
    private static final String BINDING_EDC = "editingContext";

    private ERXDisplayGroup<IPersonne> displayGroup = new ERXDisplayGroup<IPersonne>();
    private ERXDisplayGroup<EORepartStructure> membresDisplayGroup = new ERXDisplayGroup<EORepartStructure>();
    private IPersonne selectedPersonne;
    private boolean isModeRecherche = false;
    private boolean isModeStructure = false;
    private boolean isModeCreation = false;
    private boolean wantResetPersonneSrch = false;
    private EOStructure selectedStructure;
    private NSMutableDictionary<String, EOQualifier> filters;
    
    public PersonneMultipleSrch(WOContext context) {
        super(context);
    }
    
    @Override
    public void appendToResponse(WOResponse arg0, WOContext arg1) {
        if (wantReset()) {
            displayGroup.setObjectArray(NSArray.EmptyArray);
            membresDisplayGroup.setObjectArray(NSArray.EmptyArray);
            membresDisplayGroup.setSelectedObjects(NSArray.EmptyArray);
            getDisplayGroupSelection().setObjectArray(NSArray.EmptyArray);
            setSelectedStructure(null);
            setModeRecherche(false);
            setModeStructure(false);
            setModeCreation(false);
            wantResetPersonneSrch = true;
        }
        super.appendToResponse(arg0, arg1);
        if (wantReset() && canSetValueForBinding(BINDING_WANT_RESET))
            setValueForBinding(Boolean.FALSE, BINDING_WANT_RESET);
    }
    
    private void updateMembresDisplayGroup() {
        if (getSelectedStructure() != null) {
            getMembresDisplayGroup().setObjectArray(getSelectedStructure().toRepartStructuresElts(
                    null, ERXS.ascInsensitives(ViewMembresGroupe.LIBELLE_STRUCTURE_KP), false));
            getMembresDisplayGroup().setSelectedObjects(NSArray.emptyArray());
        }
    }
    
    private EOEditingContext editingContext() {
        return (EOEditingContext)valueForBinding(BINDING_EDC);
    }
    
    public WOActionResults updateMembres() {
        updateMembresDisplayGroup();
        return null;
    }
    
    public WOActionResults switchToModeCreation() {
        setModeCreation(true);
        if (selectedPersonne.isIndividu()) {
            EOIndividu individu = (EOIndividu)selectedPersonne;
            NSArray<EOCivilite> civilites = EOCivilite.fetchAll(editingContext());
            individu.setToCiviliteRelationship((EOCivilite) civilites.objectAtIndex(0));
        }
        return null;
    }
    
    public WOActionResults annulerCreation() {
        setModeCreation(false);
        wantResetPersonneSrch = true;
        editingContext().revert();
        return null;
    }
    
    public WOActionResults creerPersonne() {
        NSMutableArray<IPersonne> previous = getDisplayGroupSelection().allObjects().mutableClone();
        previous.addObjects(getSelectedPersonne());
        NSSet<IPersonne> newSelection = new NSSet<IPersonne>(previous);
        getDisplayGroupSelection().setObjectArray(newSelection.allObjects());
        setModeCreation(false);
        return showAccueil();
    }
    
    public WOActionResults selectPersonnesRecherche() {
        NSMutableArray<IPersonne> previous = getDisplayGroupSelection().allObjects().mutableClone();
        previous.addObjectsFromArray(getDisplayGroup().selectedObjects());
        NSSet<IPersonne> newSelection = new NSSet<IPersonne>(previous);
        getDisplayGroupSelection().setObjectArray(newSelection.allObjects());
        return showAccueil();
    }

    @SuppressWarnings("unchecked")
    public WOActionResults selectPersonnesStructure() {
        NSMutableArray<IPersonne> previous = getDisplayGroupSelection().allObjects().mutableClone();
        NSArray<IPersonne> selectedPersonnes = 
            (NSArray<IPersonne>) getMembresDisplayGroup().selectedObjects().valueForKey(EORepartStructure.TO_PERSONNE_ELT_KEY);
        previous.addObjectsFromArray(selectedPersonnes);
        NSSet<IPersonne> newSelection = new NSSet<IPersonne>(previous);
        getDisplayGroupSelection().setObjectArray(newSelection.allObjects());
        return showAccueil();
    }
    
    public WOActionResults delSelectedPersonne() {
        getDisplayGroupSelection().deleteSelection();
        return null;
    }

    public WOActionResults showStructures() {
        setModeRecherche(false);
        setModeStructure(true);
        return null;
    }
    
    public WOActionResults showRecherche() {
        setModeRecherche(true);
        setModeStructure(false);
        wantResetPersonneSrch = true;
        return null;
    }
    
    public WOActionResults showAccueil() {
        setModeRecherche(false);
        setModeStructure(false);
        return null;
    }
    
    public boolean wantResetPersonneSrch() {
        if (wantReset()) {
            return true;
        } else if (wantResetPersonneSrch){
            wantResetPersonneSrch = false;
            return true;
        }
        return false;
    }
    
    public NSDictionary<String, EOQualifier> filters() {
        if (filters == null) {
            filters = 
                new NSMutableDictionary<String, EOQualifier>();
            EOQualifier qual = EOStructureForGroupeSpec.QUAL_GROUPES_SERVICES;
            filters.put("Services", qual);
            qual = EOStructureForGroupeSpec.QUAL_GROUPES_LABORATOIRES;
            filters.put("Labos", qual);
            qual = EOStructureForGroupeSpec.QUAL_GROUPE_FORUMS;
            filters.put("Forums", qual);
            qual = EOStructureForGroupeSpec.QUAL_GROUPE_ENTREPRISES;
            filters.put("Entreprises", qual);
        }
        return filters;
    }
    
    public boolean wantReset() {
        return booleanValueForBinding(BINDING_WANT_RESET, false);
    }
    
    public boolean hasResults() {
        return !getDisplayGroup().displayedObjects().isEmpty();
    }
    
    public boolean hasMembres() {
        return !getMembresDisplayGroup().allObjects().isEmpty();
    }
    
    public boolean hasPersonneSelected() {
        return !getDisplayGroupSelection().selectedObjects().isEmpty();
    }

    public boolean isInSelectionMode() {
        return isModeRecherche || isModeStructure;
    }

    @Override
    public boolean synchronizesVariablesWithBindings() {
        return false;
    }

    public ERXDisplayGroup<IPersonne> getDisplayGroup() {
        return displayGroup;
    }

    public IPersonne getSelectedPersonne() {
        return selectedPersonne;
    }

    public void setSelectedPersonne(IPersonne selectedPersonne) {
        this.selectedPersonne = selectedPersonne;
    }

    @SuppressWarnings("unchecked")
    public ERXDisplayGroup<IPersonne> getDisplayGroupSelection() {
        return (ERXDisplayGroup<IPersonne>) valueForBinding(BINDING_SELECTION_DG);
    }
    
    public boolean isModeRecherche() {
        return isModeRecherche;
    }
    
    public void setModeRecherche(boolean isModeRecherche) {
        this.isModeRecherche = isModeRecherche;
    }
    
    public boolean isModeStructure() {
        return isModeStructure;
    }
    
    public void setModeStructure(boolean isModeStructure) {
        this.isModeStructure = isModeStructure;
    }
 
    public EOStructure getSelectedStructure() {
        return selectedStructure;
    }
    
    public void setSelectedStructure(EOStructure selectedStructure) {
        this.selectedStructure = selectedStructure;
    }
    
    public ERXDisplayGroup<EORepartStructure> getMembresDisplayGroup() {
        return membresDisplayGroup;
    }
    
    public boolean isModeCreation() {
        return isModeCreation;
    }
    
    public void setModeCreation(boolean isModeCreation) {
        this.isModeCreation = isModeCreation;
    }

	private String personneSrchType;

	/**
	 * @return the personneSrchType
	 */
	public String getPersonneSrchType() {
		if (MyStringCtrl.isEmpty(this.personneSrchType)) {
			setPersonneSrchType(PersonneSrch.PERS_TYPE_INDIVIDU);
		}
		return personneSrchType;
	}

	/**
	 * @param personneSrchType the personneSrchType to set
	 */
	public void setPersonneSrchType(String personneSrchType) {
		this.personneSrchType = personneSrchType;
	}
    
}