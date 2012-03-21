package org.cocktail.agrhum.serveur.components;

import org.cocktail.fwkcktlajaxwebext.serveur.component.CktlAjaxWindow;
import org.cocktail.fwkcktlpersonne.common.metier.EOTypeAssociation;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import er.ajax.AjaxUpdateContainer;
import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXEOAccessUtilities;
import er.extensions.eof.ERXSortOrdering;

public class GestionTypeAssociation extends MyWOComponent {
	
	private static final long serialVersionUID = 6866121400601842593L;
	
	private EOEditingContext editingContext;
	
	private EOTypeAssociation selectedTypeAssociation;
	public EOTypeAssociation unTypeAssociation;
	
	private NSMutableArray<EOTypeAssociation> lesTypesAssociationsTemp;
	private String sectionTypeAssociation;
//	private String interimTasCode;
//	private String interimTasLibelle;
	
	
	
    public GestionTypeAssociation(WOContext context) {
        super(context);
    }
    
    public void setSectionTypeAssociation(String sectionTypeAssociation) {
		this.sectionTypeAssociation = sectionTypeAssociation;
	}

	public String getSectionTypeAssociation() {
		return sectionTypeAssociation;
	}

	public void setSelectedTypeAssociation(EOTypeAssociation selectedTypeAssociation) {
		this.selectedTypeAssociation = selectedTypeAssociation;
	}

	public EOTypeAssociation getSelectedTypeAssociation() {
		if ( selectedTypeAssociation == null){
			this.selectedTypeAssociation = new EOTypeAssociation();
		}
		return selectedTypeAssociation;
	}

	public EOTypeAssociation getUnTypeAssociation() {
		return unTypeAssociation;
	}

	public void setUnTypeAssociation(EOTypeAssociation unTypeAssociation) {
		this.unTypeAssociation = unTypeAssociation;
	}
	
	public EOEditingContext getEditingContext() {
		if (editingContext==null) {
			editingContext=ERXEC.newEditingContext();
		}
		return editingContext;
	}

	public void setEditingContext(EOEditingContext editingContext) {
		this.editingContext = editingContext;
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
		this.selectedTypeAssociation.setTasCode(interimTasCode);
	}

	public String getInterimTasCode() {
		return getSelectedTypeAssociation().tasCode();
	}

	public void setInterimTasLibelle(String interimTasLibelle) {
		this.selectedTypeAssociation.setTasLibelle(interimTasLibelle);
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
		EOTypeAssociation typeAssociation = EOTypeAssociation.creerInstance(getEditingContext());
		// On positionne la sélection sur ce type d'association.
		setSelectedTypeAssociation(typeAssociation);
	}
	
	
	public WOActionResults onTypeAssociationCreer() {
		sauvegardeEnBase();
		return null;
	}
	
	public void sauvegardeEnBase() {
		
		String message;
		
		// Récupération de l'editingContext de la session
		 EOEditingContext edc = getEditingContext();

		 // Réalisation d'un try-catch pour éviter les soucis lors de l'écriture en base
		 /*
		  * Avec la ligne ci-dessous, nous accédons aux propriétés du modèle.
		  * Nous envisageons de pouvoir désactiver temporairement et à notre guise
		  * le "Read Only" du modèle
		  */

		 EOEntity entityParameter = ERXEOAccessUtilities.entityForEo(getSelectedTypeAssociation());
		 try {
			 // Avant de sauvegarder les données, nous modifions le modèle
			 // pour que l'on puisse avoir accès aussi en écriture sur les données
			 entityParameter.setReadOnly(false);
			 edc.saveChanges();
			 CktlAjaxWindow.close(context());
		 }
		 catch (EOGeneralAdaptorException e)
		 {
			 // Logger l'exception + Annuler les changements et les caches
			 edc.invalidateAllObjects();
			 // Affichage de l'exception
			 throw new RuntimeException(e);
		 }
		 catch (ValidationException e)
		 {
			 // Récupération du message d'erreur de validation pour affichage
			 message = e.getMessage();
			 session().addSimpleErrorMessage("AGRHUM", message);
			 AjaxUpdateContainer.updateContainerWithID(getModalWindowContainerID(), context());
		 }
		 catch (Exception e) {
			e.printStackTrace();
		} finally {
			 entityParameter.setReadOnly(true);
		 }

	}
	
	public WOActionResults onTypeAssociationModifier() {
		sauvegardeEnBase();
		return null;
	}
	
	public WOActionResults onTypeAssociationSupprimer() {
		if (selectedTypeAssociation != null) {
			getEditingContext().deleteObject(selectedTypeAssociation);
			sauvegardeEnBase();
		}
		setSelectedTypeAssociation(getSelectedTypeAssociationsList().objectAtIndex(0));
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