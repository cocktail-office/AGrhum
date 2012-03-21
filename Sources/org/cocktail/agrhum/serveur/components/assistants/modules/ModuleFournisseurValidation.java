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

import org.cocktail.fwkcktldroitsutils.common.metier.EOUtilisateur;
import org.cocktail.fwkcktlpersonne.common.metier.EOFournis;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.appserver.ERXWOContext;

/**
 * 
 * Module de validation d'un fournisseur.
 * 
 * @author Alexis TUAL <alexis.tual at cocktail.org>
 *
 */
public class ModuleFournisseurValidation extends ModuleAssistant implements IModuleAssistant {

    private static final long serialVersionUID = 5474793934979128796L;
    private static final String BINDING_FOURNISSEUR = "fournisseur";
    private String containerId;

    public ModuleFournisseurValidation(WOContext context) {
        super(context);
    }

    public String getContainerId() {
        if (containerId != null)
            containerId = ERXWOContext.safeIdentifierName(context(), true);
        return containerId;
    }

    public EOFournis getFournisseur() {
        return (EOFournis)valueForBinding(BINDING_FOURNISSEUR);
    }
    
//    public Boolean fouValideEncoursSelected() {
//        return Boolean.valueOf(EOFournis.FOU_VALIDE_NON.equals(getFournisseur().fouValide()));
//    }
//
//    public void setFouValideEncoursSelected(Boolean val) {
//        if (val.booleanValue()) {
//            getFournisseur().setFouValide(EOFournis.FOU_VALIDE_NON);
//        }
//    }
//
//    public Boolean fouValideValideSelected() {
//        return Boolean.valueOf(EOFournis.FOU_VALIDE_OUI.equals(getFournisseur().fouValide()));
//    }
//
//    public void setFouValideValideSelected(Boolean val) {
//        if (val.booleanValue()) {
//            getFournisseur().setFouValide(EOFournis.FOU_VALIDE_OUI);
//        }
//    }
//
//    public Boolean fouValideAnnuleSelected() {
//        return Boolean.valueOf(EOFournis.FOU_VALIDE_ANNULE.equals(getFournisseur().fouValide()));
//    }
//
//    public void setFouValideAnnuleSelected(Boolean val) {
//        if (val.booleanValue()) {
//            getFournisseur().setFouValide(EOFournis.FOU_VALIDE_ANNULE);
//        }
//    }
//
//    public String getFournisCreateur() {
//        if (getFournisseur() != null && getFournisseur().toValideFournis() != null) {
//            Integer utlOrdre = getFournisseur().toValideFournis().valCreation();
//            if (utlOrdre != null) {
//                EOUtilisateur util = EOUtilisateur.fetchByKeyValue(edc(), EOUtilisateur.UTL_ORDRE_KEY, utlOrdre);
//                if (util != null) {
//                    return util.getPrenomAndNom();
//                }
//            }
//        }
//        return null;
//    }
//
//    public String getFournisValideur() {
//        if (getFournisseur() != null && getFournisseur().toValideFournis() != null) {
//            Integer utlOrdre = getFournisseur().toValideFournis().valValidation();
//            if (utlOrdre != null) {
//                EOUtilisateur util = EOUtilisateur.fetchByKeyValue(edc(), EOUtilisateur.UTL_ORDRE_KEY, utlOrdre);
//                if (util != null) {
//                    return util.getPrenomAndNom();
//                }
//            }
//        }
//        return null;
//    }
//
//    public NSTimestamp getFournisValidationDateCreation() {
//        if (getFournisseur() != null && getFournisseur().toValideFournis() != null) {
//            return getFournisseur().toValideFournis().valDateCreate();
//        }
//        return null;
//    }
//
//    public NSTimestamp getFournisValidationDateValidation() {
//        if (getFournisseur() != null && getFournisseur().toValideFournis() != null) {
//            return getFournisseur().toValideFournis().valDateVal();
//        }
//        return null;
//    }
    
}