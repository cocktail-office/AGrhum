package org.cocktail.agrhum.serveur.components.assistants.modules;

import org.cocktail.agrhum.serveur.AGrhumHelpers;
import org.cocktail.agrhum.serveur.components.ContactGroupesForm;
import org.cocktail.agrhum.serveur.metier.CktlConfigurationException;
import org.cocktail.fwkcktlpersonne.common.eospecificites.EOStructureForGroupeSpec;
import org.cocktail.fwkcktlpersonne.common.metier.EORepartStructure;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSValidation;

import er.extensions.eof.ERXEC;

/**
 * Module d'édition du EORepartStructure d'un contact.
 * 
 * @see ContactGroupesForm
 * @author Alexis TUAL <alexis.tual at cocktail.org>
 *
 */
public class ModuleContactGroupe extends ModuleAssistant implements IModuleAssistant {

    private static final long serialVersionUID = -1013025682970104288L;
    private EOEditingContext editingContext;
    private boolean isCreatingNewAffectation;
    private EORepartStructure repartStructureContact;

    public ModuleContactGroupe(WOContext context) {
        super(context);
    }

    @Override
    public EOEditingContext editingContext() {
        EOEditingContext ecParent = super.editingContext();
        if (ecParent == null)
            throw new IllegalArgumentException("Un ec parent doit être fourni au composant " + this);
        if (editingContext == null || !AGrhumHelpers.nullSafeEquals(ecParent, editingContext.parentObjectStore()))
            editingContext = ERXEC.newEditingContext(ecParent);
        return editingContext;
    }

    public boolean hasAtLeastOneAssociationOrDeleteChanges() {
        return !editingContext().deletedObjects().isEmpty() ||
               (repartStructureContact().toRepartAssociations() != null && 
               !repartStructureContact().toRepartAssociations().isEmpty());
    }
    
    public WOActionResults enregistrer() {
        try {
                //verifier que l'utilisateur a le droit de selectionner le groupe
                if (!session().applicationUser().hasDroitGererGroupe(repartStructureContact().toStructureGroupe())) {
                    throw new NSValidation.ValidationException("Vous n'avez pas les droit de gerer ce groupe");
                }
                repartStructureContact().toStructureGroupe().setPersIdModification(utilisateurPersId());
                repartStructureContact().toPersonneElt().setPersIdModification(utilisateurPersId());
                //nettoyer les repartAssociation vides
                repartStructureContact().nettoieRepartAssociationVides();
                // push de l'adresse dans l'ec parent
                editingContext().saveChanges();
        } catch (NSValidation.ValidationException e) {
            session().addSimpleErrorMessage(e.getLocalizedMessage(), e);
//            setErreurSaisieMessage(e.getMessage());
        } catch (Exception e) {
            editingContext().revert();
            throw new NSForwardException(e);
        }
        return null;
    }
    
    public WOActionResults annuler() {
        editingContext().revert();
        return null;
    }
    
    public IPersonne personne() {
        return (IPersonne)valueForBinding(BDG_personne);
    }
    
    public EORepartStructure repartStructureContact() {
        if (repartStructureContact == null) {
            try {
                EOStructure groupeContacts = EOStructureForGroupeSpec.getGroupeForParamKey(
                        editingContext(), EOStructureForGroupeSpec.ANNUAIRE_CONTACTS_KEY);
                repartStructureContact = EORepartStructure.creerInstanceSiNecessaire(
                        editingContext(), personne().localInstanceIn(editingContext()), 
                        groupeContacts, utilisateurPersId());
            } catch (Exception e) {
                e.printStackTrace();
                throw new CktlConfigurationException(
                        "L'application n'est pas encore configurée pour gérer les contacts.\n"+
                        "Contactez l'administrateur de cette application.\n" +
                        "Informations techniques :\n" +
                        "Le paramètre ANNUAIRE_CONTACTS n'est pas renseignée ou ne pointe pas " +
                        "sur une structure existante.", e);
            }
        }
        return repartStructureContact;
    }
    
    public boolean isCreatingNewAffectation() {
        return isCreatingNewAffectation;
    }

    public void setCreatingNewAffectation(boolean isCreatingNewAffectation) {
        this.isCreatingNewAffectation = isCreatingNewAffectation;
    }
    
}