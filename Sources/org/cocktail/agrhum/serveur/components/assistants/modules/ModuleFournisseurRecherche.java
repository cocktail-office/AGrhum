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

import org.cocktail.agrhum.serveur.components.assistants.Assistant;
import org.cocktail.fwkcktlpersonne.common.eospecificites.EOStructureForFournisseurSpec;
import org.cocktail.fwkcktlpersonne.common.metier.EOCivilite;
import org.cocktail.fwkcktlpersonne.common.metier.EOFournis;
import org.cocktail.fwkcktlpersonne.common.metier.EOIndividu;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;
import org.cocktail.fwkcktlpersonneguiajax.serveur.fournis.controleurs.FournisseurSrchCtrl;
import org.cocktail.fwkcktlwebapp.server.CktlWebApplication;
import org.cocktail.fwkcktlwebapp.server.database.CktlDataBus;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSValidation;

import er.extensions.appserver.ERXWOContext;
import er.extensions.eof.ERXEOControlUtilities;

/**
 * Module de recherche d'un fournisseur.
 * 
 * @author Alexis TUAL <alexis.tual at cocktail.org>
 *
 */
public class ModuleFournisseurRecherche extends ModuleAssistant implements IModuleAssistant {

    private static final long serialVersionUID = 2741622698091187190L;
    private static final String BINDING_FOURNISSEUR = "fournisseur";
    private static final String BINDING_MODE_INDIVIDU = "modeIndividu";

    private WODisplayGroup displayGroup;
    private FournisseurSrchCtrl fournisseurSrchCtrl = new FournisseurSrchCtrl();
    private String containerId;

    public ModuleFournisseurRecherche(WOContext context) {
        super(context);
    }
    
    public WODisplayGroup getDisplayGroup() {
        return displayGroup;
    }
    
    public void setDisplayGroup(WODisplayGroup displayGroup) {
        this.displayGroup = displayGroup;
    }
    
    public FournisseurSrchCtrl getFournisseurSrchCtrl() {
        return fournisseurSrchCtrl;
    }

    public WOActionResults modifierFournisseur() {
    	try {
    		personne().validateForSave();
		} catch (NSValidation.ValidationException e2) {
			logger().info(e2.getMessage(), e2);
			String messageAlerte = "Attention le fournisseur selectionné ne remplit pas certaines règles du référentiel : \n"
				+ e2.getLocalizedMessage();
            session().addSimpleInfoMessage(messageAlerte, null);
		}
		parent().takeValueForKey(true, Assistant.ASSISTANT_EDIT_MODE_VALUE);
        return performParentAction("suivant");
    }
    
    public WOActionResults creerFournisseur() {
        initPersonne();
        EOFournis fournis = initFournisseur();
        setValueForBinding(fournis, BINDING_FOURNISSEUR);
        try {
    		personne().validateForSave();
		} catch (NSValidation.ValidationException e2) {
			logger().info(e2.getMessage(), e2);
			String messageAlerte = "Attention le fournisseur créé ne remplit pas certaines règles du référentiel : \n"
				+ e2.getLocalizedMessage();
//            session().addSimpleErrorMessage(messageAlerte, e2);
            session().addSimpleInfoMessage(messageAlerte, null);
		}

        return performParentAction("suivant");
    }
    
    public String getContainerId() {
        if (containerId == null)
            containerId = ERXWOContext.safeIdentifierName(context(), true);
        return containerId;
    }
    
    private void initPersonne() {
        if (personne() != null && ERXEOControlUtilities.isNewObject(personne())) {
            if (personne() instanceof EOStructure) {
                //EOStructure structure = (EOStructure) personne();
                // structure.setValidationEditingContext(editingContext());
            } else if (personne() instanceof EOIndividu) {
                EOIndividu individu = (EOIndividu) personne();
                // individu.setValidationEditingContext(editingContext());
                if (individu.toCivilite() == null) {
                    NSArray<EOCivilite> civilites = EOCivilite.fetchAll(editingContext());
                    individu.setToCiviliteRelationship((EOCivilite) civilites.objectAtIndex(0));
                }
            }
            if (personne().persIdCreation() == null) {
                personne().setPersIdCreation(utilisateurPersId());
            }
            if (personne().persIdModification() == null) {
                personne().setPersIdModification(utilisateurPersId());
            }
        }
    }
    
    private EOFournis initFournisseur() {
        EOFournis fournis = EOFournis.creerInstance(editingContext());
        fournis.setToPersonne(personne());
        if (personne() instanceof EOStructure) {
            EOStructure structure = (EOStructure) personne();
            structure.registerSpecificite(EOStructureForFournisseurSpec.sharedInstance());
        }
        try {
            fournis.initialise(session().applicationUser().getPersId(), null, false, EOFournis.FOU_TYPE_FOURNISSEUR);
        } catch (Exception e) {
            throw new NSForwardException(e);
        }
        return fournis;
    }
    
    @Override
    public boolean isSuivantDisabled() {
        return true;
    }
    
    public boolean modeIndividu() {
        return (Boolean)valueForBinding(BINDING_MODE_INDIVIDU);
    }
    
    public boolean modeStructure() {
        return !modeIndividu();
    }
    
}