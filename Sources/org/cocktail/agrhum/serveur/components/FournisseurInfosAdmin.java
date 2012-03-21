package org.cocktail.agrhum.serveur.components;

import com.webobjects.appserver.WOContext;

import org.cocktail.fwkcktldroitsutils.common.util.AUtils;
import org.cocktail.fwkcktlpersonne.common.metier.EOFournis;
import org.cocktail.fwkcktlpersonneguiajax.serveur.FwkCktlPersonneGuiAjaxParamManager;

public class FournisseurInfosAdmin extends MyWOComponent {
	private static final long serialVersionUID = 5084193737050877657L;
	
    public static final String BINDING_FOURNISSEUR = "fournisseur";


	public FournisseurInfosAdmin(WOContext context) {
        super(context);
    }
    
    public String getFouCode() {
        return getFournisseur().fouCode() == null ? "n/a" : getFournisseur().fouCode();
    }
    
    private EOFournis getFournisseur() {
        return (EOFournis)valueForBinding(BINDING_FOURNISSEUR);
    }
    
    public boolean fouTypeFournisseurSelected() {
        return getFournisseur().isTypeFournisseur();
    }
    
    public void setFouTypeFournisseurSelected(Boolean val) {
        if (val.booleanValue()) {
            getFournisseur().setFouType(EOFournis.FOU_TYPE_FOURNISSEUR);
        }
    }
    
    public boolean fouTypeClientSelected() {
        return getFournisseur().isTypeClient();
    }
    
    public void setFouTypeClientSelected(Boolean val) {
        if (val.booleanValue()) {
            getFournisseur().setFouType(EOFournis.FOU_TYPE_CLIENT);
        }
    }
    
    public boolean fouTypeTiersSelected() {
        return getFournisseur().isTypeTiers();
    }
    
    public void setFouTypeTiersSelected(Boolean val) {
        if (val.booleanValue()) {
            getFournisseur().setFouType(EOFournis.FOU_TYPE_TIERS);
        }
    }
    
    public boolean getIsEtranger() {
        return getFournisseur().isEtranger();
    }
    
    public void setIsEtranger(Boolean isEtranger) {
        getFournisseur().setIsEtranger(isEtranger);
    }
    
    public Boolean canEditFournisseur() {
    	return applicationUser().hasDroitModificationEOFournis(getFournisseur(), AUtils.currentExercice()) 
    			|| applicationUser().hasDroitCreationEOFournis(null, AUtils.currentExercice());
    }
    
    public boolean cannotEditFournisseur() {
    	return ! canEditFournisseur();
    }
    
    public Boolean allowEditNoInsee() {
		if (getFournisseur().isIndividu()) {
			return !(getFournisseur().toIndividu().isPersonnel() || getFournisseur().toIndividu().isEtudiant());
		}
		return false;
	}

	public Boolean allowEditEtatCivil() {
		if (getFournisseur().isIndividu()) {
			return !(getFournisseur().toIndividu().isPersonnel() || getFournisseur().toIndividu().isEtudiant());
		}
		return false;
	}

	public Boolean showEtatCivil() {
		return myApp().config().booleanForKey(FwkCktlPersonneGuiAjaxParamManager.PARAM_FOURNIS_FORM_SHOWNAISSANCE);
	}

	public Boolean showNoInsee() {
		return myApp().config().booleanForKey(FwkCktlPersonneGuiAjaxParamManager.PARAM_FOURNIS_FORM_SHOWINSEE);
	}
}