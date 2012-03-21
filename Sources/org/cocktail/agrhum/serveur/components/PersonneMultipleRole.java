package org.cocktail.agrhum.serveur.components;

import org.cocktail.fwkcktlpersonne.common.metier.EOAssociation;
import org.cocktail.fwkcktlpersonne.common.metier.EORepartAssociation;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.eof.ERXEC;
import er.extensions.foundation.ERXArrayUtilities;

public class PersonneMultipleRole extends MyWOComponent {
	private static final long serialVersionUID = 6866121400601842517L;
	
	//	public static final String BINDING_personne = "personne"; // ou utilisateurPersId seulement ?
	public static final String BINDING_utilisateurPersId = "utilisateurPersId"; // required (pour la selection des associations)
	public static final String BINDING_structureGroupe = "structureGroupe"; // required
	public static final String BINDING_selectedRepartAssociationsList = "selectedRepartAssociationsList"; // willset (et required ?)
	
//	private static final String BINDING_SELECTION_DG = "selectionDisplayGroup"; // optional

	private static final String BINDING_WANT_RESET = "wantReset"; // optional
    
//    private static final String BINDING_EDC = "editingContext"; // on construit un tableau d'associations en vue de les appliquer dans le composant parent qui possède son edc.
	
//	public static final String BINDING_repartStructure = "repartStructure";
//	public static final String BINDING_isNew = "isNew";
//	public static final String BINDING_filtrerParTypeGroupe = "filtrerParTypeGroupe";
//	public static final String BINDING_filtrerParVisibilite = "filtrerParVisibilite";

//	public static final Boolean DEFAULT_filtrerParTypeGroupe = Boolean.TRUE;
//	public static final Boolean DEFAULT_filtrerParVisibilite = Boolean.FALSE;

//	private String groupe;
	//	private String association;
	//	private String commentaires;
	private EOEditingContext editingContext;
//	private EOStructure selectedStructure;
//	private NSMutableArray<EORepartAssociation> lesRepartAssociationsTemp;
//	private NSMutableDictionary userFiltersDictionary;

//	private NSArray groupeExclus;

	private EORepartAssociation selectedRepartAssociation;
	public EORepartAssociation unRepartAssociation;
//	private EORepartStructure repartStructureCache;

	public PersonneMultipleRole(WOContext context) {
        super(context);
    }

	public void resetForm() {
		selectedRepartAssociation = null;
	}

	public Integer getUtilisateurPersId() {
		return (Integer) valueForBinding(BINDING_utilisateurPersId);
	}

	public String associationSelectID() {
		return getComponentId() + "_associationSelect";
	}

	public String ajaxGroupeSelectId() {
		return getComponentId() + "_ajaxGroupeSelect";
	}


	public EOAssociation getSelectedAssociation() {
		return (getSelectedRepartAssociation() == null ? null : getSelectedRepartAssociation().toAssociation());
	}

	public void setSelectedAssociation(EOAssociation selectedAssociation) {
		getSelectedRepartAssociation().setToAssociationRelationship(selectedAssociation);
	}

	public EORepartAssociation getSelectedRepartAssociation() {
		if (selectedRepartAssociation == null) {
			if (getSelectedRepartAssociationsList().isEmpty()) {
				EORepartAssociation selectedRepartAssociationNew = EORepartAssociation.creerInstance(getEditingContext());
				getSelectedRepartAssociationsList().add(selectedRepartAssociationNew);
				setSelectedRepartAssociation(selectedRepartAssociationNew);
			} else {
				setSelectedRepartAssociation ((EORepartAssociation) ERXArrayUtilities.firstObject(getSelectedRepartAssociationsList()));
			}
		}
		return selectedRepartAssociation;
	}

	public void setSelectedRepartAssociation(EORepartAssociation selectedRepartAssociation) {
		this.selectedRepartAssociation = selectedRepartAssociation;

	}

	public String getUnRepartAssociationDisplayString() {
		return (unRepartAssociation.toAssociation() == null ? "Nouveau role" : (unRepartAssociation.toAssociation().assLibelle().length() > 20 ? unRepartAssociation.toAssociation().assLibelle().substring(0, 20) : unRepartAssociation.toAssociation().assLibelle()));
	}

	public WOActionResults onRepartAssociationCreer() {
		//si le role en cours n'est pas precise, on n'en rajoute pas un nouveau
		EORepartAssociation repartAssociation = EORepartAssociation.creerInstance(getEditingContext());
		//		repartAssociation.setPersIdCreation(getUtilisateurPersId());
//		repartAssociation.setPersIdModification(utilisateurPersId());
		
		// dans le cas de plusieurs personne on construit simplement une liste d'objet non destinés à être complets et sauvegardé en BD
//		repartStructure().addToToRepartAssociationsRelationship(repartAssociation);
		getSelectedRepartAssociationsList().add(repartAssociation);
		
		setSelectedRepartAssociation(repartAssociation);
		//}
		return null;
	}

	public WOActionResults onRepartAssociationSupprimer() {
		if (selectedRepartAssociation != null) {
//			repartStructure().removeFromToRepartAssociationsRelationship(selectedRepartAssociation);
			getSelectedRepartAssociationsList().removeObject(selectedRepartAssociation);
			getEditingContext().deleteObject(selectedRepartAssociation);
		}
		setSelectedRepartAssociation(null);
		return null;
	}

	public String addRepartAssociationTitle() {
		String title = "Affecter un nouveau rôle pour la liste de personnes et pour le groupe ";
		if (getStructureGroupe()!=null) {
			title = title + getStructureGroupe().libelleForGroupe();
		}
		return title;
	}

//	public NSArray getLesRepartAssociations() {
//		if (repartStructure() == null) {
//			return NSArray.EmptyArray;
//		}
//		NSArray res = repartStructure().toRepartAssociations();
//		if (res.count() > 0) {
//			if (selectedRepartAssociation == null || res.indexOf(selectedRepartAssociation) == NSArray.NotFound) {
//				setSelectedRepartAssociation((EORepartAssociation) res.objectAtIndex(0));
//			}
//		}
//		return res;
//	}

//	public Boolean isShowRolesPopup() {
//		return Boolean.valueOf(repartStructure() != null && repartStructure().toRepartAssociations().count() > 1);
//	}

//	public Boolean isShowRoles() {
//		return Boolean.valueOf(repartStructure() != null && repartStructure().toRepartAssociations().count() > 0);
//	}

//	public String getPersonneNom() {
//		return (personne() == null ? "" : " de " + personne().getNomCompletAffichage() + " ");
//	}

//	public Boolean filtrerParTypeGroupe() {
//		return booleanValueForBinding(BINDING_filtrerParTypeGroupe, DEFAULT_filtrerParTypeGroupe);
//	}
//
//	public Boolean filtrerParVisibilite() {
//		return booleanValueForBinding(BINDING_filtrerParVisibilite, DEFAULT_filtrerParVisibilite);
//	}

	@SuppressWarnings("unchecked")
	public NSMutableArray<EORepartAssociation> getSelectedRepartAssociationsList() {
		NSMutableArray<EORepartAssociation> lesRepartAssociationsTemp= 
					(NSMutableArray<EORepartAssociation>) valueForBinding(BINDING_selectedRepartAssociationsList);
		if (lesRepartAssociationsTemp==null) {
			lesRepartAssociationsTemp = new NSMutableArray<EORepartAssociation>();
		}
		// ajouter le premier role
		if (!lesRepartAssociationsTemp.isEmpty()) {
			if (selectedRepartAssociation == null || lesRepartAssociationsTemp.indexOf(selectedRepartAssociation) == NSArray.NotFound) {
				setSelectedRepartAssociation(ERXArrayUtilities.firstObject(lesRepartAssociationsTemp));
			}
		} else {
			EORepartAssociation selectedRepartAssociationNew = EORepartAssociation.creerInstance(getEditingContext());
			lesRepartAssociationsTemp.add(selectedRepartAssociationNew);
			setSelectedRepartAssociation(selectedRepartAssociationNew);
		}
		
		return lesRepartAssociationsTemp;
	}

	public void setSelectedRepartAssociationsList(NSMutableArray<EORepartAssociation> lesRepartAssociationsTemp) {
		if (canSetValueForBinding(BINDING_selectedRepartAssociationsList)) {
			setValueForBinding(lesRepartAssociationsTemp, BINDING_selectedRepartAssociationsList);
		}
	}

	public EOStructure getStructureGroupe() {
		return (EOStructure) valueForBinding(BINDING_structureGroupe);
	}

//	public void setSelectedStructureGroupe(EOStructure selectedStructure) {
//		setValueForBinding(selectedStructure,BINDING_selectedStructureGroupe);
//	}

	public EOEditingContext getEditingContext() {
		if (editingContext==null) {
			editingContext=ERXEC.newEditingContext();
		}
		return editingContext;
	}

	public void setEditingContext(EOEditingContext editingContext) {
		this.editingContext = editingContext;
	}
	
	@Override
    public boolean synchronizesVariablesWithBindings() {
        return false;
    }
	
	public boolean wantReset() {
        return booleanValueForBinding(BINDING_WANT_RESET, false);
    }
	
	@Override
    public void appendToResponse(WOResponse arg0, WOContext arg1) {
        if (wantReset()) {
            setSelectedRepartAssociationsList(null);
            setSelectedAssociation(null);
//            wantResetPersonneSrch = true;
        }
        super.appendToResponse(arg0, arg1);
        if (wantReset() && canSetValueForBinding(BINDING_WANT_RESET))
            setValueForBinding(Boolean.FALSE, BINDING_WANT_RESET);
    }
}