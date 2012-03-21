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
import org.cocktail.fwkcktlajaxwebext.serveur.CocktailAjaxSession;
import org.cocktail.fwkcktldroitsutils.common.util.MyStringCtrl;
import org.cocktail.fwkcktlpersonne.common.metier.EOAssociation;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSValidation;

import er.ajax.AjaxUpdateContainer;
import er.extensions.validation.ERXValidationException;

/**
 * Ce composant est basé sur le composant EditGroupe d'Alexis TUAL.
 * 
 * @binding onValider callback appelé lorsque l'on souhaite valider l'enregistrement des données.
 * @binding onAnnuler callback appelé lorsque l'on souhaite retourner à la fenêtre principale sans conserver les données.
 * @binding edc récupère l'editingcontext de la fenêtre principale.
 * @binding role est le binding sur le rôle ou la fonction qui est sélectionnée.
 * 
 * @binding wantReset flag pour indiquer à ce composant de reset son état interne 
 * 
 * @author Alain MALAPLATE
 *
 */

public class EditRole extends MyWOComponent {
	
	private static final long serialVersionUID = -1941683715892047068L;
	private static final String ONVALIDER_BDG = "onValider";
//	private static final String ONANNULER_BDG = "onAnnuler";
	private static final String EDITING_CONTEXT_BDG = "edc";
	private static final String ROLE_BDG = "role";
	private static final String WANT_RESET = "wantReset";
	private static final String BDG_FCT_PARENT = "associationPere";
	
	
	private boolean isRoleSelected;
	private boolean isEditingMode = false;
	private int roleIndex;
	
	private static final Logger LOG = Logger
	.getLogger(EditParametres.class);
	
    public EditRole(WOContext context) {
        super(context);
    }

    @Override
    public boolean synchronizesVariablesWithBindings() {
    	return false;
    }
    
    @Override
    public void appendToResponse(WOResponse woresponse, WOContext wocontext) {
    	if (wantReset()) {
    		setWantReset(false);
    	}
    	super.appendToResponse(woresponse, wocontext);
    }
    
    public EOEditingContext editingContext() {
		return (EOEditingContext) valueForBinding(EDITING_CONTEXT_BDG);
	}
    
    
    
	public String affichageParent(){
		if (getSelectedRole() != null){
			NSArray<String> libelles = (NSArray<String>)getSelectedRole().getPeres(edc()).valueForKey(EOAssociation.ASS_LIBELLE_KEY);
			return libelles.componentsJoinedByString(",");
		}
		return null;
	}
    
	public EOAssociation getRoleParent() {
		return (EOAssociation)valueForBinding(BDG_FCT_PARENT);
	}

	public void setRoleParent(EOAssociation roleParent) {
		setValueForBinding(roleParent, EditRole.BDG_FCT_PARENT);
	}
	
    
    public WOActionResults onRoleCancelEdit() {
    	/* On annule les changements dans l'editingContext.
		   On revient à l'affichage du rôle sélectionné. */
    	editingContext().revert();
    	return null;
    }
    
	public WOActionResults onRoleEdit() {
		AjaxUpdateContainer.updateContainerWithID(updateContainerID(), context());
		return null;
	}
	
	public WOActionResults onRoleEnregistrer() {
		String message;
		
    	try {
    		if( getSelectedRole().assCode() == null ) {
    			editingContext().invalidateAllObjects();
    			throw new NSValidation.ValidationException("Le code du rôle sélectionné n'est pas renseigné.");
    		}
    		getSelectedRole().setDModification(new NSTimestamp());
    		editingContext().saveChanges();
    	} catch (EOGeneralAdaptorException e) {
			// Logger l'exception + Annuler les changements et les caches
    		editingContext().invalidateAllObjects();
			// Affichage de l'exception
			message = "Une erreur est survenue lors de l'enregistrement en Base";
			LOG.error(message, e);
			mySession().addSimpleErrorMessage("Erreur", message);
		} catch (ValidationException e) {
			// Récupération du message d'erreur de validation pour affichage
			message = e.getMessage();
			LOG.error(message, e);
			mySession().addSimpleErrorMessage("Erreur", message);
		}
				
		return null;
	}
	
	public WOActionResults reload() {
		return null;
	}
    
    public WOActionResults valider() {
    	// On rajoute un test pour être sûr que l'utilisateur renseigne le code et le libellé.
    	if (!isRoleNotEmpty()){
    		mySession().addSimpleErrorMessage("AGRHUM", "Le code ou le libellé du rôle sélectionné n'est pas renseigné.");
    	}
		return (WOActionResults)valueForBinding(ONVALIDER_BDG);
	}
    
    @Override
	public void validationFailedWithException(Throwable e, Object obj, String keyPath) {
		if (e instanceof ERXValidationException) {
			((CocktailAjaxSession) session()).addSimpleErrorMessage("AGRHUM", e.getMessage());
		}
		super.validationFailedWithException(e, obj, keyPath);
	}
    
    public String getTimestampFormatter() {
		return "%d/%m/%Y";
	}
	
	public String getRoleInPlaceID() {
		return getComponentId()+"_RoleInPlaceID_"+getRoleIndex();
	}

	private Boolean wantReset() {
		return hasBinding(WANT_RESET) && (Boolean)valueForBinding(WANT_RESET);
	}
	
	private void setWantReset(Boolean value) {
		setEditingMode(false);
		setValueForBinding(value, WANT_RESET);
	}
	
    
	public boolean isRoleNotEmpty() {
		return ( !MyStringCtrl.isEmpty(getSelectedRole().assCode()) && !MyStringCtrl.isEmpty(getSelectedRole().assLibelle()) );
	}
	
	
	public EOAssociation getSelectedRole() {
		return (EOAssociation)valueForBinding(ROLE_BDG);
	}

	public void setSelectedRole(EOAssociation selectedRole) {
		setValueForBinding(selectedRole, ROLE_BDG);
	}

	/**
	 * @return the isRoleSelected
	 */
	public boolean isRoleSelected() {
		if (getSelectedRole() != null){
			setRoleSelected(true);
		}
		return isRoleSelected;
	}
	
	/**
	 * @param isRoleSelected the isRoleSelected to set
	 */
	public void setRoleSelected(boolean isRoleSelected) {
		//getSelectedRole().toAssociationReseauPeres().valueForKey(EOAssociationReseau.TO_ASSOCIATION_PERE_KEY);
		this.isRoleSelected = isRoleSelected;
	}
	
	
	/**
	 * @return the isLocal
	 */
	public boolean isLocal() {
		if (getSelectedRole().assLocale().equals("O")){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @return the roleIndex
	 */
	public int getRoleIndex() {
		return roleIndex;
	}

	/**
	 * @param compteEmailIndex the roleIndex to set
	 */
	public void setRoleIndex(int roleIndex) {
		this.roleIndex = roleIndex;
	}
	

	public String getInPlaceManualEditFunctionName() {
		return getRoleInPlaceID()+"Edit()";
	}

	public String getRoleInPlaceFieldID() {
		return "FRoleInPlace_"+getRoleInPlaceID();
	}

	public String getInPlaceManualCancelEditFunctionName() {
		return getRoleInPlaceID()+"Cancel()";
	}

	public String getInPlaceManualValidEditFunctionName() {
		return getRoleInPlaceID()+"Save()";
	}
	
	/**
	 * Test si la fonction locale est close
	 */
	public boolean isClosed(){
		return getSelectedRole().isClosed();
	}
	
	/**
	 * Ré-ouverture 
	 */
	public void rouvrir(){
		getSelectedRole().cascadeOuverture();
		getSelectedRole().setDFermeture(null);
	}
	/**
	 * Message affiché avant la Ré-ouverture d'une fonction locale qui a été fermée.
	 * @return
	 */
	public String beforeClickOnOpenAgain() {
		if ( !getRoleParent().isClosed() ){
			return "confirm('Voulez-vous réouvrir la fonction locale sélectionnée (" + getSelectedRole().assLibelle() + ") ? ATTENTION : les fonctions filles vont aussi être réouvertes')";
		} else {
			return "alert('Vous voulez réouvrir une fonction mais son rôle père est encore fermé !')";
		}
	}

	public boolean isEditingMode() {
		return isEditingMode;
	}

	public void setEditingMode(boolean isEditingMode) {
		this.isEditingMode = isEditingMode;
	}
	
}