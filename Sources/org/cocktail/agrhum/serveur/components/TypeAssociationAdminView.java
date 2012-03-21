package org.cocktail.agrhum.serveur.components;

import org.cocktail.fwkcktlajaxwebext.serveur.component.CktlAjaxWindow;
import org.cocktail.fwkcktlpersonne.common.metier.EOTypeAssociation;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXSortOrdering;

public class TypeAssociationAdminView extends MyWOComponent {
	
	private static final long serialVersionUID = 6866121400601842593L;
	
//	private EOEditingContext editingContext;
	
//	private EOTypeAssociation selectedTypeAssociation;
	public EOTypeAssociation unTypeAssociation;
	
	private NSMutableArray<EOTypeAssociation> lesTypesAssociationsTemp;
	private String sectionTypeAssociation;
//	private String interimTasCode;
//	private String interimTasLibelle;
	private String code = "";
	private String libelle = "";
	
	/**
	 * selectedTypeAssociation est un binding obligatoire dans l'API
	 */
	public static final String SELECTED_TYPE_ASSOCIATION_BDG = "selectedTypeAssociation";
	private static final String EDITING_CONTEXT_BDG = "editingContext";
	
	
    public TypeAssociationAdminView(WOContext context) {
        super(context);
    }
    
    @Override
    public void appendToResponse(WOResponse response, WOContext context) {
        if (getSelectedTypeAssociation() == null) {
        	setSelectedTypeAssociation(getSelectedTypeAssociationsList().objectAtIndex(0));
        }
        super.appendToResponse(response, context);
    }
    
    public EOEditingContext getEditingContext() {
		if (valueForBinding(EDITING_CONTEXT_BDG) == null) {
			setValueForBinding(ERXEC.newEditingContext(), EDITING_CONTEXT_BDG);
		}
		return (EOEditingContext) valueForBinding(EDITING_CONTEXT_BDG);
	}

//	public void setEditingContext(EOEditingContext editingContext) {
//		this.editingContext = editingContext;
//	}
    
    public void setSectionTypeAssociation(String sectionTypeAssociation) {
		this.sectionTypeAssociation = sectionTypeAssociation;
	}

	public String getSectionTypeAssociation() {
		return sectionTypeAssociation;
	}

	public void setSelectedTypeAssociation(EOTypeAssociation selectedTypeAssociation) {
		setValueForBinding(selectedTypeAssociation, SELECTED_TYPE_ASSOCIATION_BDG);
	}
	
	public EOTypeAssociation getSelectedTypeAssociation() {
		return (EOTypeAssociation)valueForBinding(SELECTED_TYPE_ASSOCIATION_BDG);
	}
	
	public EOTypeAssociation getUnTypeAssociation() {
		return unTypeAssociation;
	}

	public void setUnTypeAssociation(EOTypeAssociation unTypeAssociation) {
		this.unTypeAssociation = unTypeAssociation;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getPopUpTypeAssoID() {
		return "popUpTypeAsso_"+uniqueDomId();
	}
	
	public String getAssociationContainerID() {
		return "associationContainer_"+uniqueDomId();
	}
	
	public String getTypeAssoEditModalID() {
		return getComponentId() + "_" + "TypeAssoEditModal";
	}
	
	public String getTypeAssoAjoutModalID() {
		return getComponentId() + "_" + "TypeAssoAjoutModal";
	}
	
	public String getModalWindowContainerID() {
		return getComponentId() + "_" + "ModalWindowContainer";
	}
	
	public String getTypeAssoSupprModalID() {
		return getComponentId() + "_" + "TypeAssoSupprModal";
	}
	
	
	public WOActionResults onCloseResultModal() {
        return null;
    }
	
	
	public void setInterimTasCode(String interimTasCode) {
		getSelectedTypeAssociation().setTasCode(interimTasCode);
	}

	public String getInterimTasCode() {
		return getSelectedTypeAssociation().tasCode();
	}

	public void setInterimTasLibelle(String interimTasLibelle) {
		getSelectedTypeAssociation().setTasLibelle(interimTasLibelle);
	}

	public String getInterimTasLibelle() {
		return getSelectedTypeAssociation().tasLibelle();
	}


	@SuppressWarnings("unchecked")
	public NSMutableArray<EOTypeAssociation> getSelectedTypeAssociationsList() {
		if (lesTypesAssociationsTemp == null ) {
			//lesTypesAssociationsTemp = new NSMutableArray<EOTypeAssociation>();
			NSArray sortOrderings = new NSArray(ERXSortOrdering.sortOrderingWithKey(EOTypeAssociation.TAS_ID_KEY, EOSortOrdering.CompareAscending));
			lesTypesAssociationsTemp = new NSMutableArray<EOTypeAssociation>(EOTypeAssociation.fetchAll(getEditingContext(), sortOrderings));
			if ( !lesTypesAssociationsTemp.isEmpty()){
				setSelectedTypeAssociation(lesTypesAssociationsTemp.objectAtIndex(0));
			}
		} else {
			//lesTypesAssociationsTemp = new NSMutableArray<EOTypeAssociation>();
			NSArray sortOrderings = new NSArray(ERXSortOrdering.sortOrderingWithKey(EOTypeAssociation.TAS_ID_KEY, EOSortOrdering.CompareAscending));
			lesTypesAssociationsTemp = new NSMutableArray<EOTypeAssociation>(EOTypeAssociation.fetchAll(getEditingContext(), sortOrderings));
		}
		
		return lesTypesAssociationsTemp;
	}

	
	public void ouvrirAjoutModalW() {
		// Pour un ajout, on a besoin d'un editing context propre
		// Donc on annule toutes les modifications qui pourraient avoir été faites et non enregistrées.
		getEditingContext().revert();
		// Création d'une instance de type d'association
		unTypeAssociation = EOTypeAssociation.creerInstance(getEditingContext());
		mySession().addSimpleInfoMessage("AGRHUM", "Création d'une instance.");
	}
	
	
	public WOActionResults onTypeAssociationCreer() {
		getUnTypeAssociation().setTasCode(getCode());
		getUnTypeAssociation().setTasLibelle(getLibelle());
		getUnTypeAssociation().setDCreation(new NSTimestamp());
		getUnTypeAssociation().setDModification(new NSTimestamp());
		setSelectedTypeAssociation(getUnTypeAssociation());
		sauvegardeEnBase();
//		setSelectedTypeAssociation(getUnTypeAssociation());
		CktlAjaxWindow.close(context());
		return null;
	}
	
	public void sauvegardeEnBase() {
		
		String message;
		
		// Récupération de l'editingContext de la session
		 EOEditingContext edc = getEditingContext();
//		 EOEntity entityParameter = ERXEOAccessUtilities.entityForEo(getSelectedTypeAssociation());
		 try {
			 if (getSelectedTypeAssociation() != null) {
				 edc.saveChanges();
				 mySession().addSimpleInfoMessage("AGRHUM", "Le type d'association a été supprimé avec succès.");
//			 	CktlAjaxWindow.close(context());
			 }
			 
		 }
		 catch (EOGeneralAdaptorException e)
		 {
			 edc.invalidateAllObjects();
			 throw new RuntimeException(e);
		 }
		 catch (ValidationException e)
		 {
			 // Récupération du message d'erreur de validation pour affichage
			 message = e.getMessage();
			 session().addSimpleErrorMessage("AGRHUM", message);
//			 AjaxUpdateContainer.updateContainerWithID(getModalWindowContainerID(), context());
		 }
		 catch (Exception e) {
			e.printStackTrace();
		 }
	}
	
	public WOActionResults onTypeAssociationModifier() {
		getSelectedTypeAssociation().setDModification(new NSTimestamp());
		sauvegardeEnBase();
		CktlAjaxWindow.close(context());
		return null;
	}
	
	public WOActionResults onTypeAssociationSupprimer() {
		String message;
		if (getSelectedTypeAssociation() != null) {
			try {
				getEditingContext().deleteObject(getSelectedTypeAssociation());
				getEditingContext().saveChanges();
				mySession().addSimpleInfoMessage("AGRHUM", "Le type d'association a été modifié avec succès.");
				setSelectedTypeAssociation(getSelectedTypeAssociationsList().objectAtIndex(0));
				CktlAjaxWindow.close(context());
			}
			catch (EOGeneralAdaptorException e)
			{
				// Logger l'exception + Annuler les changements et les caches
				getEditingContext().invalidateAllObjects();
				// Affichage de l'exception
				message = "Une erreur est survenue lors de l'enregistrement en Base";
				System.out.println(message);
			}
			catch (ValidationException e)
			{
				// Récupération du message d'erreur de validation pour affichage
				message = e.getMessage();
				System.out.println(message);
			}
		}
		
		return null;
	}
	
	public WOActionResults onTypeAssociationAnnuler(){
		if (getEditingContext() != null) {
			 getEditingContext().revert();
		 }
		// On ne change pas la valeur actuelle du type d'association sélectionné pour revenir directement sur lui dans la page
		
		CktlAjaxWindow.close(context());
		return null;
	}
	
	
	/* ********************************************************************************************* */
	 public WOActionResults reload() {
		 return null;
	 }
}