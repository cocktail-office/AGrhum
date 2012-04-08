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

import org.cocktail.agrhum.serveur.AgrhumParamManager;
import org.cocktail.fwkcktlpersonne.common.eospecificites.EOIndividuForFournisseurSpec;
import org.cocktail.fwkcktlpersonne.common.eospecificites.EOStructureForFournisseurSpec;
import org.cocktail.fwkcktlpersonne.common.metier.AfwkPersRecord;
import org.cocktail.fwkcktlpersonne.common.metier.EOCompte;
import org.cocktail.fwkcktlpersonne.common.metier.EOIndividu;
import org.cocktail.fwkcktlpersonne.common.metier.EOVlans;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;
import org.cocktail.fwkcktlpersonneguiajax.serveur.FwkCktlPersonneGuiAjaxParamManager;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSTimestamp;

/**
 * Composant d'affichage et d'édition des principales caractéristiques (nom, adresses, téléphones...) d'une personne.
 * @binding personne la personne dont on veut éditer les caractéristiques
 * @binding editingContext l'editing context (pas de nested crée dans ce composant)
 * @binding utilisateurPersId le persId de l'utilisateur
 */
public class PersonneAdminView extends MyWOComponent {
    
    private static final long serialVersionUID = 2001993614889312288L;
    public static String BINDING_PERSONNE = "personne";
    public static String BINDING_EDITING_CONTEXT = "editingContext";
    public static String BINDING_FORM_ID = "formID";
    private boolean isAdresseEditing = false;
    private boolean isNomEditing = false;
    private boolean isPersonneAdminEditing = true;
    private boolean isGroupeAdminEditing = true;
    private boolean isEffectifEditing = false;
    private boolean isCaEditing = false;
    private boolean isPersonneGroupesEditing = false;
	private boolean isRibEditing=false;
    private EOCompte selectedCompte;

    private NSArray<EOVlans> vlansAutorises;

    private boolean isCompteTabSelected;
    private boolean isDroitsTabSelected;
    private boolean isCoordonneesTabSelected;
    private boolean isGroupesTabSelected;
    private boolean isInfosTabSelected = true;
    private boolean isDocumentsTabSelected;
	private boolean isDiplomesTabSelected;
	private boolean wantReset = false;
	private boolean wantResetTelephones = false;
	private boolean wantResetBureaux = false;
	private boolean wantResetCalendriers = false;
    
    public PersonneAdminView(WOContext context) {
        super(context);
    }
    
    @Override
    public void appendToResponse(WOResponse response, WOContext context) {
        resetTabs();
        super.appendToResponse(response, context);
    }
    
    private void resetTabs() {
        // Si rien n'est sélectionné alors qu'on visualise une structure, on sélectionne le premier
        if (!isInfosTabSelected && !isCoordonneesTabSelected && !isCompteTabSelected 
                && !isGroupesTabSelected && personne().isStructure()){
        	isInfosTabSelected = true;
        }
    	setWantReset(true);

        // Si on a le tab infos et le tab droits sélectionnés (peut arriver) on déselectionne les droits...
        if (isInfosTabSelected && isDroitsTabSelected)
            isDroitsTabSelected = false;
    }
    
    private boolean isSuperUser() {
        return session().applicationUser().hasDroitTous();
    }
    
    public EOEditingContext editingContext() {
        return (EOEditingContext)valueForBinding(BINDING_EDITING_CONTEXT);
    }
    
    public IPersonne personne() {
        return (IPersonne)valueForBinding(BINDING_PERSONNE);
    }
    
    public void setPersonne( IPersonne personne){
    	setWantReset(true);
    	setValueForBinding(personne, BINDING_PERSONNE);
    }
    
    public NSArray<EOVlans> vlansAutorises() {
        if (vlansAutorises == null) {
            vlansAutorises = EOVlans.fetchAllVlansPourPersonne(personne(), editingContext());
        }
        return vlansAutorises;
    }
    
    public boolean canEditDroits() {
        return isSuperUser() && personne().isIndividu();
    }
    
    public boolean canEditResponsabilites() {
        return isSuperUser() && personne().isIndividu();
    }
    
    
    public EOCompte getSelectedCompte() {
        return selectedCompte;
    }
    
    public void setSelectedCompte(EOCompte selectedCompte) {
        this.selectedCompte = selectedCompte;
    }

    public boolean isAdresseEditing() {
        return isAdresseEditing;
    }

    public void setAdresseEditing(boolean isAdresseEditing) {
        this.isAdresseEditing = isAdresseEditing;
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

    public boolean isGroupeAdminEditing() {
        return isGroupeAdminEditing;
    }

    public void setGroupeAdminEditing(boolean isGroupeAdminEditing) {
        this.isGroupeAdminEditing = isGroupeAdminEditing;
    }

    public boolean isEffectifEditing() {
        return isEffectifEditing;
    }

    public void setEffectifEditing(boolean isEffectifEditing) {
        this.isEffectifEditing = isEffectifEditing;
    }

    public boolean isCaEditing() {
        return isCaEditing;
    }

    public void setCaEditing(boolean isCaEditing) {
        this.isCaEditing = isCaEditing;
    }
    
    public boolean isPersonneGroupesEditing() {
        return isPersonneGroupesEditing;
    }
    
    public void setPersonneGroupesEditing(boolean isPersonneGroupesEditing) {
        this.isPersonneGroupesEditing = isPersonneGroupesEditing;
    }
    
    public boolean isCompteTabSelected() {
        return isCompteTabSelected;
    }
    
    public void setCompteTabSelected(boolean isCompteSelected) {
        this.isCompteTabSelected = isCompteSelected;
    }

    public boolean isDroitsTabSelected() {
        return isDroitsTabSelected;
    }

    public void setDroitsTabSelected(boolean isDroitsTabSelected) {
        this.isDroitsTabSelected = isDroitsTabSelected;
    }
    
    public boolean isCoordonneesTabSelected() {
        return isCoordonneesTabSelected;
    }

    public void setCoordonneesTabSelected(boolean isCoordonneesTabSelected) {
        this.isCoordonneesTabSelected = isCoordonneesTabSelected;
    }

    public boolean isGroupesTabSelected() {
        return isGroupesTabSelected;
    }

    public void setGroupesTabSelected(boolean isGroupesTabSelected) {
        this.isGroupesTabSelected = isGroupesTabSelected;
    }

    public boolean isInfosTabSelected() {
        return isInfosTabSelected;
    }

    public void setInfosTabSelected(boolean isInfosTabSelected) {
        this.isInfosTabSelected = isInfosTabSelected;
    }

	/**
	 * @return the documentsTabSelected
	 */
	public boolean isDocumentsTabSelected() {
		return isDocumentsTabSelected;
	}

	/**
	 * @param documentsTabSelected the documentsTabSelected to set
	 */
	public void setDocumentsTabSelected(boolean documentsTabSelected) {
		this.isDocumentsTabSelected = documentsTabSelected;
	}
	
	/**
	 * @return the isDiplomesTabSelected
	 */
	public boolean isDiplomesTabSelected() {
		return isDiplomesTabSelected;
	}

	/**
	 * @param isDiplomesTabSelected the isDiplomesTabSelected to set
	 */
	public void setDiplomesTabSelected(boolean isDiplomesTabSelected) {
		this.isDiplomesTabSelected = isDiplomesTabSelected;
	}
	
	
	
	public boolean isPersonneFournisseur() {
		boolean isPersonneFournisseur = false;
		if (personne()!=null) {
			if (personne().isIndividu()) {
				isPersonneFournisseur = EOIndividuForFournisseurSpec.sharedInstance().isSpecificite((AfwkPersRecord) personne());
			} else {
				isPersonneFournisseur = EOStructureForFournisseurSpec.sharedInstance().isSpecificite((AfwkPersRecord) personne());
			}
		}
		return isPersonneFournisseur;
	}
	
	public boolean isFournisValidationEnabled(){
		return isPersonneFournisseur() && session().applicationUser().peutValiderFournisseur();
	}
	
	public boolean showFournisInfos(){
		return isPersonneFournisseur() && session().applicationUser().peutVoirFournisseur();
	}
	
	public boolean isFournisEditionEnabled() {
		return isPersonneFournisseur() && session().applicationUser().peutModifierFournisseur(personne().toFournis());
	}

	public boolean isPersonneHeberge() {
		boolean isPersonneHeberge = false;
		// TODO : rajouter spec pour les invités/hébergés
//		if (personne()!=null) {
//			if (personne().isIndividu()) {
//				isPersonneHeberge = EOIndividuForPersonnelSpec.sharedInstance().isSpecificite((AfwkPersRecord) personne());
//			} 
//		}
		return isPersonneHeberge;
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
	
	
	/**
	 * @return the wantReset
	 */
	public boolean isWantReset() {
		return wantReset;
	}
	
	/**
	 * @param wantReset the wantReset to set
	 */
	public void setWantReset(boolean wantReset) {
		this.wantReset = wantReset;
		setWantResetTelephones(wantReset);
		setWantResetBureaux(wantReset);
		setWantResetCalendriers(wantReset);
	}

	public boolean isWantResetTelephones() {
		return wantResetTelephones;
	}

	public void setWantResetTelephones(boolean wantResetTelephones) {
		this.wantResetTelephones = wantResetTelephones;
	}

	public boolean isWantResetBureaux() {
		return wantResetBureaux;
	}

	public void setWantResetBureaux(boolean wantResetBureaux) {
		this.wantResetBureaux = wantResetBureaux;
	}

	public boolean isWantResetCalendriers() {
		return wantResetCalendriers;
	}

	public void setWantResetCalendriers(boolean wantResetCalendriers) {
		this.wantResetCalendriers = wantResetCalendriers;
	}

	/**
	 * @return la valeur 'Oui' pour l'attribut "ListeRouge" d'un individu
	 * 
	 * @author Pierre-Yves MARIE <pierre-yves.marie at cocktail.org>
	 */
	public String getListeRougeOui() {
		return EOIndividu.LISTE_ROUGE_O;
	}

	/**
	 * @return la valeur 'Non' pour l'attribut "ListeRouge" d'un individu
	 * 
	 * @author Pierre-Yves MARIE <pierre-yves.marie at cocktail.org>
	 */
	public String getListeRougeNon() {
		return EOIndividu.LISTE_ROUGE_N;
	}
	
	public EOIndividu getIndividu() {
		if (personne().isIndividu()) {
			return (EOIndividu) personne();
		}
		return null;
	}

	public boolean peutVoirInfoPerso() {
		return session().applicationUser().hasDroitVoirInfosPerso(personne()); // || session().applicationUser().hasDroitModificationIPersonne(personne());
	}

	public boolean showRibUI() {
		// si utilisateur est la personne, ou s'il a tous les droits, ou s'il peut modifier un fournisseur
		return (session().applicationUser().isSamePersonne(personne()) 
				|| session().applicationUser().hasDroitTous()
				|| isFournisEditionEnabled()) ;
	}
	
	public boolean isRibReadOnly() {
		return !session().applicationUser().peutModifierFournisseur(personne().toFournis()); // session().applicationUser().hasDroitModificationIPersonne(personne()) ???
	}
	
	public String retourneCreateur (){
		String createur = ((EOIndividu)personne()).retourneCreateur();
		return createur;
	}

	public String retourneModificateur (){
		String modificateur = ((EOIndividu)personne()).retourneModificateur();
		return modificateur;
	}
	
	public NSTimestamp retourneDateCreation (){
		NSTimestamp dCreation = ((EOIndividu)personne()).retourneDateCreation();
		return dCreation;
	}
	
	public NSTimestamp retourneDateModification (){
		NSTimestamp dModification = ((EOIndividu)personne()).retourneDateModification();
		return dModification;
	}
	
	public String getTimestampFormatter() {
		return DateCtrl.DEFAULT_FORMAT;
	}
    
	public Boolean allowEditNoInsee() {
		if (personne().isIndividu()) {
			return !(((EOIndividu)personne()).isPersonnel() || ((EOIndividu)personne()).isEtudiant());
		}
		return false;
	}

	public Boolean allowEditEtatCivil() {
		if (personne().isIndividu()) {
			return !(((EOIndividu)personne()).isPersonnel() || ((EOIndividu)personne()).isEtudiant());
		}
		return false;
	}

	public Boolean showEtatCivil() {
		return myApp().config().booleanForKey(FwkCktlPersonneGuiAjaxParamManager.PARAM_FOURNIS_FORM_SHOWNAISSANCE);
	}

	public Boolean showNoInsee() {
		return myApp().config().booleanForKey(FwkCktlPersonneGuiAjaxParamManager.PARAM_FOURNIS_FORM_SHOWINSEE);
	}
	
	public boolean isEditing(){
		return applicationUser().hasDroitModificationIPersonne(getIndividu());
	}
	
	public boolean isReadOnly(){
		return !isEditing();
	}
	
	/**
	 * AJout d'un paramètre dans la table Grhum.paramètre pour que les noms et prénoms des individus ou
	 * les noms des structures soient seulement en mode affichage si l'utilisateur n'est pas GrhumCreateur.
	 */
	public boolean isNomReadOnlyEnabled(){
		if (myApp().config().booleanForKey(AgrhumParamManager.AGRHUM_PERSONNE_NOM_READONLY_ACTIVE) && applicationUser().hasDroitGrhumCreateur() ){
			return true;
		}
		return false;
	}
}