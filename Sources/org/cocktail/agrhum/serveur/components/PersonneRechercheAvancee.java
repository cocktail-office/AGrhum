package org.cocktail.agrhum.serveur.components;

import org.cocktail.agrhum.serveur.components.controlers.PersonneRechercheAvanceeCtrl;
import org.cocktail.fwkcktldroitsutils.common.util.AUtils;
import org.cocktail.fwkcktlpersonne.common.metier.EOFournis;
import org.cocktail.fwkcktlpersonne.common.metier.EOIndividu;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;
import org.cocktail.fwkcktlpersonne.common.metier.EOVlans;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSTimestamp;

import er.ajax.AjaxUpdateContainer;
import er.extensions.appserver.ERXDisplayGroup;
import er.extensions.foundation.ERXStringUtilities;

public class PersonneRechercheAvancee extends MyWOComponent {
	
	public final static String RECHERCHE_INDIVIDUS = "RECHERCHE_INIDVIDUS";
	public final static String RECHERCHE_STRUCTURES = "RECHERCHE_STRUCTURES";
	
	public final static String COULEUR_VALIDE = "#46a546";
	public final static String COULEUR_ATTENTE = "#ffc40d";
	public final static String COULEUR_NON_VALIDE = "#b94a48";
	public final static String COULEUR_NEUTRE = "#ffffff";
	
	private PersonneRechercheAvanceeCtrl personneRechercheAvanceeCtrl;
	
	private ERXDisplayGroup<NSDictionary<String, Object>> displayGroup;
	private NSDictionary<String, Object> currentPersonne;
	private String currentEtatFournisseurCle;
	
	private String selectedRecherche = RECHERCHE_STRUCTURES; 
	
	private NSArray<EOVlans> vlansDisponibles;
	private EOVlans currentVlans;
	
	
    public PersonneRechercheAvancee(WOContext context) {
        super(context);
    }


	public PersonneRechercheAvanceeCtrl getPersonneRechercheAvanceeCtrl() {
		if(personneRechercheAvanceeCtrl == null) {
			personneRechercheAvanceeCtrl = new PersonneRechercheAvanceeCtrl(applicationUser());
		}
		return personneRechercheAvanceeCtrl;
	}

	public WOActionResults rechercheDeStructures() {
		getPersonneRechercheAvanceeCtrl().lancerUneRechercheStructure();
		getDisplayGroup().setObjectArray(getPersonneRechercheAvanceeCtrl().getResultatsDeRecherche());
		getDisplayGroup().updateDisplayedObjects();
		return doNothing();
	}
	
	public WOActionResults resetChampsStructure() {
		getPersonneRechercheAvanceeCtrl().resetChampsStructure();
		getDisplayGroup().setObjectArray(null);
		getDisplayGroup().updateDisplayedObjects();
		AjaxUpdateContainer.updateContainerWithID("tableViewResultatsContainer", context());
		return doNothing();
	}
	
	public WOActionResults rechercheDeIndividus() {
		getPersonneRechercheAvanceeCtrl().lancerUneRechercheIndividu();
		getDisplayGroup().setObjectArray(getPersonneRechercheAvanceeCtrl().getResultatsDeRecherche());
		getDisplayGroup().updateDisplayedObjects();
		return doNothing();
	}
	
	public WOActionResults resetChampsIndividu() {
		getPersonneRechercheAvanceeCtrl().resetChampsIndividu();
		getDisplayGroup().setObjectArray(null);
		getDisplayGroup().updateDisplayedObjects();
		AjaxUpdateContainer.updateContainerWithID("tableViewResultatsContainer", context());
		return doNothing();
	}


	public ERXDisplayGroup<NSDictionary<String, Object>> getDisplayGroup() {
		if(displayGroup == null) {
			displayGroup = new ERXDisplayGroup<NSDictionary<String, Object>>();
		}
		return displayGroup;
	}


	public NSDictionary<String, Object> getCurrentPersonne() {
		return currentPersonne;
	}


	public void setCurrentPersonne(NSDictionary<String, Object> currentPersonne) {
		this.currentPersonne = currentPersonne;
	}


	public String getCurrentEtatFournisseurLibelle() {
		return (String) PersonneRechercheAvanceeCtrl.ETATS_FOURNISSEURS.valueForKey(getCurrentEtatFournisseurCle());
	}
	
	public NSArray<String> etatsFournisseursCles() {
		return PersonneRechercheAvanceeCtrl.ETATS_FOURNISSEURS_CLES;
	}


	public String getCurrentEtatFournisseurCle() {
		return currentEtatFournisseurCle;
	}


	public void setCurrentEtatFournisseurCle(String currentEtatFournisseurCle) {
		this.currentEtatFournisseurCle = currentEtatFournisseurCle;
	}

	
	public String currentStructureLibelleLong() {
		Object value = getCurrentPersonne().valueForKey(getPersonneRechercheAvanceeCtrl().KEY_STRUCTURE_LIBELLE_LONG);
		if(value == NSKeyValueCoding.NullValue) {
			return "";
		} else {
			return (String) value;
		}
	}
	
	public Integer currentStructurePersId() {
		return (Integer) getCurrentPersonne().valueForKey(getPersonneRechercheAvanceeCtrl().KEY_STRUCTURE_PERS_ID);
	}

	public String currentStructureCStructure() {
		Object value = getCurrentPersonne().valueForKey(getPersonneRechercheAvanceeCtrl().KEY_STRUCTURE_C_STRUCTURE);
		if(value == NSKeyValueCoding.NullValue) {
			return "";
		} else {
			return (String) value;
		}
	}	
   
	public String currentStructureFouCode() {
		Object value = getCurrentPersonne().valueForKey(getPersonneRechercheAvanceeCtrl().KEY_STRUCTURE_FOU_CODE);
		if(value == NSKeyValueCoding.NullValue) {
			return "";
		} else {
			return (String) value;
		}
	}
	
	public String currentStructureFouValide() {
		Object value = getCurrentPersonne().valueForKey(getPersonneRechercheAvanceeCtrl().KEY_STRUCTURE_FOU_VALIDE);
		if(value == NSKeyValueCoding.NullValue) {
			return "";
		} else {
			return (String) value;
		}
	}	
    
	public String currentStructureFouValideStyle() {
		Object value = getCurrentPersonne().valueForKey(getPersonneRechercheAvanceeCtrl().KEY_STRUCTURE_FOU_VALIDE);
		String style = "background-color: ";
		if(value == NSKeyValueCoding.NullValue) {
			style += COULEUR_NEUTRE;
		} else {
			String fouValide = (String) value;
			if(ERXStringUtilities.stringEqualsString(fouValide, EOFournis.FOU_VALIDE_OUI)) {
				style += COULEUR_VALIDE;
			}
			if(ERXStringUtilities.stringEqualsString(fouValide, EOFournis.FOU_VALIDE_NON)) {
				style += COULEUR_ATTENTE;
			}
			if(ERXStringUtilities.stringEqualsString(fouValide, EOFournis.FOU_VALIDE_ANNULE)) {
				style += COULEUR_NON_VALIDE;
			}
		}
		style += ";";
		return style;
	}	
	
	public String currentStructureValideStyle() {
		Object value = getCurrentPersonne().valueForKey(getPersonneRechercheAvanceeCtrl().KEY_STRUCTURE_DATE_FERMETURE);
		String style = "background-color: ";
		if(value == NSKeyValueCoding.NullValue) {
			style += COULEUR_VALIDE;
		} else {
			style += COULEUR_NON_VALIDE;
		}
		style += ";";
		return style;
	}
	
	public String currentStructureSiret() {
		Object value = getCurrentPersonne().valueForKey(getPersonneRechercheAvanceeCtrl().KEY_STRUCTURE_SIRET);
		if(value == NSKeyValueCoding.NullValue) {
			return "";
		} else {
			return (String) value;
		}
	}	
	
	public EOStructure getSelectedStructure() {
		Integer persId = (Integer) getDisplayGroup().selectedObject().valueForKey(getPersonneRechercheAvanceeCtrl().KEY_STRUCTURE_PERS_ID);
		return EOStructure.fetchFirstByQualifier(getPersonneRechercheAvanceeCtrl().editingContext(), EOStructure.PERS_ID.eq(persId));
	}
	
	public EOIndividu getSelectedIndividu() {
		Integer persId = (Integer) getDisplayGroup().selectedObject().valueForKey(getPersonneRechercheAvanceeCtrl().KEY_STRUCTURE_PERS_ID);
		return EOIndividu.fetchFirstByQualifier(getPersonneRechercheAvanceeCtrl().editingContext(), EOIndividu.PERS_ID.eq(persId));
	}
	
	public WOActionResults selectionner() {
		if(getIsRechercheStructuresSelected()) {
			EditStructurePage nextPage = (EditStructurePage) pageWithName(EditStructurePage.class.getName());
			nextPage.setStructure(getSelectedStructure());
			nextPage.setResetTabs(true);
			return nextPage;
		}
		if(getIsRechercheIndividusSelected()) {
			EditIndividuPage nextPage = (EditIndividuPage) pageWithName(EditIndividuPage.class.getName());
			nextPage.setIndividu(getSelectedIndividu());
			return nextPage;
		}
		return doNothing();
	}
	
	public Boolean isSelectionnerDisabled() {
		return getDisplayGroup().selectedObject() == null;
	}
	
	
	public Boolean showInfosFournisseurs() {
		return applicationUser().hasDroitVisualisationEOFournis(null, AUtils.currentExercice());
	}


	public String getSelectedRecherche() {
		return selectedRecherche;
	}


	public void setSelectedRecherche(String selectedRecherche) {
		this.selectedRecherche = selectedRecherche;
	}


	public Boolean getIsRechercheStructuresSelected() {
		return ERXStringUtilities.stringEqualsString(getSelectedRecherche(), RECHERCHE_STRUCTURES);
	}


	public void setIsRechercheStructuresSelected(Boolean isRechercheStructuresSelected) {
		if(isRechercheStructuresSelected) {
			setSelectedRecherche(RECHERCHE_STRUCTURES);
		}
	}


	public Boolean getIsRechercheIndividusSelected() {
		return ERXStringUtilities.stringEqualsString(getSelectedRecherche(), RECHERCHE_INDIVIDUS);
	}


	public void setIsRechercheIndividusSelected(Boolean isRechercheIndividusSelected) {
		if(isRechercheIndividusSelected) {
			setSelectedRecherche(RECHERCHE_INDIVIDUS);
		}
	}
	
	
	public WOActionResults changerTypeRecherche() {
		getDisplayGroup().setObjectArray(null);
		getDisplayGroup().updateDisplayedObjects();
		AjaxUpdateContainer.updateContainerWithID("tableViewResultatsContainer", context());
		return doNothing();
	}
	
	public NSArray<EOVlans> vlansDisponibles() {
		if(vlansDisponibles == null) {
			vlansDisponibles = EOVlans.fetchAllVLansAPrendreEnCompte(getPersonneRechercheAvanceeCtrl().editingContext());
		}
		return vlansDisponibles;
	}


	public EOVlans getCurrentVlans() {
		return currentVlans;
	}


	public void setCurrentVlans(EOVlans currentVlans) {
		this.currentVlans = currentVlans;
	}
	
	
	
	
	public String currentIndividuNomUsuel() {
		Object value = getCurrentPersonne().valueForKey(getPersonneRechercheAvanceeCtrl().KEY_INDIVIDU_NOM_USUEL);
		if(value == NSKeyValueCoding.NullValue) {
			return "";
		} else {
			return (String) value;
		}
	}
	
	public String currentIndividuNomPatronymique() {
		Object value = getCurrentPersonne().valueForKey(getPersonneRechercheAvanceeCtrl().KEY_INDIVIDU_NOM_PATRONYMIQUE);
		if(value == NSKeyValueCoding.NullValue) {
			return "";
		} else {
			return (String) value;
		}
	}
	
	public String currentIndividuPrenom() {
		Object value = getCurrentPersonne().valueForKey(getPersonneRechercheAvanceeCtrl().KEY_INDIVIDU_PRENOM);
		if(value == NSKeyValueCoding.NullValue) {
			return "";
		} else {
			return (String) value;
		}
	}
	
	public Integer currentIndividuPersId() {
		return (Integer) getCurrentPersonne().valueForKey(getPersonneRechercheAvanceeCtrl().KEY_INDIVIDU_PERS_ID);
	}

	public Integer currentIndividuNumero() {
		return (Integer) getCurrentPersonne().valueForKey(getPersonneRechercheAvanceeCtrl().KEY_INDIVIDU_NO_INDIVIDU);

	}	
   
	public String currentIndividuFouCode() {
		Object value = getCurrentPersonne().valueForKey(getPersonneRechercheAvanceeCtrl().KEY_INDIVIDU_FOU_CODE);
		if(value == NSKeyValueCoding.NullValue) {
			return "";
		} else {
			return (String) value;
		}
	}
	
	public String currentIndividuFouValide() {
		Object value = getCurrentPersonne().valueForKey(getPersonneRechercheAvanceeCtrl().KEY_INDIVIDU_FOU_VALIDE);
		if(value == NSKeyValueCoding.NullValue) {
			return "";
		} else {
			return (String) value;
		}
	}	
    
	public String currentIndividuFouValideStyle() {
		Object value = getCurrentPersonne().valueForKey(getPersonneRechercheAvanceeCtrl().KEY_INDIVIDU_FOU_VALIDE);
		String style = "background-color: ";
		if(value == NSKeyValueCoding.NullValue) {
			style += COULEUR_NEUTRE;
		} else {
			String fouValide = (String) value;
			if(ERXStringUtilities.stringEqualsString(fouValide, EOFournis.FOU_VALIDE_OUI)) {
				style += COULEUR_VALIDE;
			}
			if(ERXStringUtilities.stringEqualsString(fouValide, EOFournis.FOU_VALIDE_NON)) {
				style += COULEUR_ATTENTE;
			}
			if(ERXStringUtilities.stringEqualsString(fouValide, EOFournis.FOU_VALIDE_ANNULE)) {
				style += COULEUR_NON_VALIDE;
			}
		}
		style += ";";
		return style;
	}
		
	public String currentIndividuValideStyle() {
		Object value = getCurrentPersonne().valueForKey(getPersonneRechercheAvanceeCtrl().KEY_INDIVIDU_VALIDE);
		String style = "background-color: ";
		if(value == NSKeyValueCoding.NullValue) {
			style += COULEUR_NEUTRE;
		} else {
			String valide = (String) value;
			if(ERXStringUtilities.stringEqualsString(valide, EOIndividu.TEM_VALIDE_O)) {
				style += COULEUR_VALIDE;
			} else {
				style += COULEUR_NON_VALIDE;
			}
		}
		style += ";";
		return style;
	}
	
	
	
	
	
}