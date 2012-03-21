package org.cocktail.agrhum.serveur.components;

import org.apache.log4j.Logger;
import org.cocktail.agrhum.serveur.Session;
import org.cocktail.fwkcktlajaxwebext.serveur.component.CktlAjaxWindow;
import org.cocktail.fwkcktlpersonne.common.metier.EOAssociation;
import org.cocktail.fwkcktlpersonne.common.metier.EOAssociationReseau;
import org.cocktail.fwkcktlpersonneguiajax.serveur.components.ATreeViewComponent;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;

//public class MoveRoleForm extends MyWOComponent {
public class MoveRoleForm extends ATreeViewComponent {
	
	private static final long serialVersionUID = 3L;
	private static final Logger LOG = Logger.getLogger(MoveRoleForm.class);
	
	public static final String SELECTION_BDG = "selectionRole";
	public static final String BINDING_PARENT = "newParent";
//	public static final String BINDING_RESET = "reset";
	
	EOAssociation selectedNewParent;
	
    public MoveRoleForm(WOContext context) {
        super(context);
    }
    
//    @Override
//    public void appendToResponse(WOResponse response, WOContext context) {
//    	if (wantReset()){
//			setWantReset(false);
//			setSelectedNewParent(null);
//		}
//        super.appendToResponse(response, context);
//        
//    }
    
    public boolean synchronizesVariablesWithBindings() {
		return false;
	}
    
    public EOEditingContext editingContext() {
        return getSelectionRole().editingContext();
    }
    
    public EOAssociation getSelectionRole() {
		return (EOAssociation)valueForBinding(SELECTION_BDG);
	}

	public void setSelectionRole(EOAssociation selectionRole) {
		setValueForBinding(selectionRole, MoveRoleForm.SELECTION_BDG);
	}
	
	public EOAssociation getRoleParent() {
		return (EOAssociation)valueForBinding(BINDING_PARENT);
	}

	public void setRoleParent(EOAssociation roleParent) {
		setValueForBinding(roleParent, MoveRoleForm.BINDING_PARENT);
	}
    
//	private Boolean wantReset() {
//		return hasBinding(BINDING_RESET) && (Boolean)valueForBinding(BINDING_RESET);
//	}
//	
//	private void setWantReset(Boolean value) {
//		setValueForBinding(value, BINDING_RESET);
//	}
	
    public EOAssociation getSelectedNewParent() {
		return selectedNewParent;
	}

	public void setSelectedNewParent(EOAssociation selectedNewParent) {
		this.selectedNewParent = selectedNewParent;
	}

	public String containerAssociationSelectionneId() {
		return getComponentId() + "_selection";
	}
	
	public boolean hasSelectedNewParent(){
		if (getSelectedNewParent() != null){
			return true;
		}
		return false;
	}
	
	public WOActionResults annuler() {
		if (editingContext() != null) {
			editingContext().revert();
		}
		setSelectedNewParent(null);
		return null;
	}
	
	public WOActionResults quitter() {
		if (editingContext() != null) {
			editingContext().revert();
		}
		CktlAjaxWindow.close(context());
		return null;
	}
	
	public WOActionResults onSelectRoleForMove() {
		try {
			// déplacer le groupe vers le groupe sélectionné
		    if (getSelectedNewParent() != null) {
		    	// TODO : mettre les archives en place & la relation vers l'ID du Père
//		    	if (getSelectionRole().dFermeture().before(new NSTimestamp())) {
//		    		getSelectionRole().setDFermeture(null);
//				}
		    	EOAssociationReseau lienAssoPereFils = EOAssociationReseau.rechercheAssociationReseau(getSelectionRole(),
		    			getRoleParent(), editingContext());
		    	lienAssoPereFils.setToAssociationPereRelationship(getSelectedNewParent());
		    	
				setValueForBinding(getSelectedNewParent(), BINDING_PARENT);
				
		    	editingContext().saveChanges();
		    	((Session)session()).addSimpleInfoMessage("Gestion des Rôles",
	            		"Le changement de fonction parente a été effectué et enregistré avec succès");
	            
	            setSelectedNewParent(null);
	            
		    }
		    CktlAjaxWindow.close(context());
		} catch (ValidationException e) {
            LOG.warn(e);
            ((Session)session()).addSimpleErrorMessage(e.getLocalizedMessage(), e);
        } catch (Exception e) {
            LOG.error(e);
       
        }
	    return null;
	}
}