package org.cocktail.agrhum.serveur.components;

import org.cocktail.fwkcktldroitsutils.common.metier.EOUtilisateur;
import org.cocktail.fwkcktlpersonne.common.metier.EOFournis;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSTimestamp;

public class FournisseurValidationAdmin extends MyWOComponent {
	private static final long serialVersionUID = -4828534560882653447L;

    private static final String BINDING_FOURNISSEUR = "fournisseur";

	public FournisseurValidationAdmin(WOContext context) {
        super(context);
    }
	
	private EOFournis getFournisseur() {
        return (EOFournis)valueForBinding(BINDING_FOURNISSEUR);
    }
    
    public Boolean fouValideEncoursSelected() {
        return Boolean.valueOf(EOFournis.FOU_VALIDE_NON.equals(getFournisseur().fouValide()));
    }

    public void setFouValideEncoursSelected(Boolean val) {
        if (val.booleanValue()) {
            getFournisseur().setFouValide(EOFournis.FOU_VALIDE_NON);
        }
    }

    public Boolean fouValideValideSelected() {
        return Boolean.valueOf(EOFournis.FOU_VALIDE_OUI.equals(getFournisseur().fouValide()));
    }

    public void setFouValideValideSelected(Boolean val) {
        if (val.booleanValue()) {
            getFournisseur().setFouValide(EOFournis.FOU_VALIDE_OUI);
        }
    }

    public Boolean fouValideAnnuleSelected() {
        return Boolean.valueOf(EOFournis.FOU_VALIDE_ANNULE.equals(getFournisseur().fouValide()));
    }

    public void setFouValideAnnuleSelected(Boolean val) {
        if (val.booleanValue()) {
            getFournisseur().setFouValide(EOFournis.FOU_VALIDE_ANNULE);
        }
    }

    public String getFournisCreateur() {
        if (getFournisseur() != null && getFournisseur().toValideFournis() != null) {
            Integer utlOrdre = getFournisseur().toValideFournis().valCreation();
            if (utlOrdre != null) {
                EOUtilisateur util = EOUtilisateur.fetchByKeyValue(edc(), EOUtilisateur.UTL_ORDRE_KEY, utlOrdre);
                if (util != null) {
                    return util.getPrenomAndNom();
                }
            }
        }
        return null;
    }

    public String getFournisValideur() {
        if (getFournisseur() != null && getFournisseur().toValideFournis() != null) {
            Integer utlOrdre = getFournisseur().toValideFournis().valValidation();
            if (utlOrdre != null) {
                EOUtilisateur util = EOUtilisateur.fetchByKeyValue(edc(), EOUtilisateur.UTL_ORDRE_KEY, utlOrdre);
                if (util != null) {
                    return util.getPrenomAndNom();
                }
            }
        }
        return null;
    }

    public NSTimestamp getFournisValidationDateCreation() {
        if (getFournisseur() != null && getFournisseur().toValideFournis() != null) {
            return getFournisseur().toValideFournis().valDateCreate();
        }
        return null;
    }

    public NSTimestamp getFournisValidationDateValidation() {
        if (getFournisseur() != null && getFournisseur().toValideFournis() != null) {
            return getFournisseur().toValideFournis().valDateVal();
        }
        return null;
    }
}