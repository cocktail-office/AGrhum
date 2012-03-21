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
 
package org.cocktail.agrhum.serveur.components.assistants.modules;

import org.cocktail.fwkcktlpersonne.common.metier.EOIndividu;
import org.cocktail.fwkcktlpersonneguiajax.serveur.FwkCktlPersonneGuiAjaxParamManager;

import com.webobjects.appserver.WOContext;

/**
 * Module d'edition des informations generales d'un fournisseur.
 * 
 * @author Alexis TUAL <alexis.tual at cocktail.org>
 *
 */
public class ModuleFournisseurInformations extends ModuleAssistant implements IModuleAssistant {

    private static final long serialVersionUID = -3033071406151768434L;
    public static final String BINDING_FOURNISSEUR = "fournisseur";
    
    private boolean isRibEditing;
    private boolean isNomEditing;
    
    public ModuleFournisseurInformations(WOContext context) {
        super(context);
    }
    
//    public String getFouCode() {
//        return getFournisseur().fouCode() == null ? "n/a" : getFournisseur().fouCode();
//    }
//    
//    private EOFournis getFournisseur() {
//        return (EOFournis)valueForBinding(BINDING_FOURNISSEUR);
//    }
//    
//    public boolean fouTypeFournisseurSelected() {
//        return getFournisseur().isTypeFournisseur();
//    }
//    
//    public void setFouTypeFournisseurSelected(Boolean val) {
//        if (val.booleanValue()) {
//            getFournisseur().setFouType(EOFournis.FOU_TYPE_FOURNISSEUR);
//        }
//    }
//    
//    public boolean fouTypeClientSelected() {
//        return getFournisseur().isTypeClient();
//    }
//    
//    public void setFouTypeClientSelected(Boolean val) {
//        if (val.booleanValue()) {
//            getFournisseur().setFouType(EOFournis.FOU_TYPE_CLIENT);
//        }
//    }
//    
//    public boolean fouTypeTiersSelected() {
//        return getFournisseur().isTypeTiers();
//    }
//    
//    public void setFouTypeTiersSelected(Boolean val) {
//        if (val.booleanValue()) {
//            getFournisseur().setFouType(EOFournis.FOU_TYPE_TIERS);
//        }
//    }
//    
//    public boolean getIsEtranger() {
//        return getFournisseur().isEtranger();
//    }
//    
//    public void setIsEtranger(Boolean isEtranger) {
//        getFournisseur().setIsEtranger(isEtranger);
//    }
 
    public boolean isRibEditing() {
        return isRibEditing;
    }
    
    public void setIsRibEditing(boolean isRibEditing) {
        this.isRibEditing = isRibEditing;
    }

//	TODO PYM : Faire une DT pour voir si l'etat civil et le no insee a lieu d'etre saisie ici...
//    public boolean showNIR() {
//		boolean showNIR = false;
//		if (valueForBinding(BINDING_FOURNISSEUR)!=null) {
//			EOFournis fournis = (EOFournis) valueForBinding(BINDING_FOURNISSEUR);
//			showNIR = fournis.isEtranger();
//		}
//		return showNIR;
//	}
    
    public boolean isNomEditing() {
        return isNomEditing;
    }
    
    public void setNomEditing(boolean isNomEditing) {
        this.isNomEditing = isNomEditing;
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
    
}