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

import org.cocktail.fwkcktlpersonne.common.metier.EOCompte;
import org.cocktail.fwkcktlpersonne.common.metier.EOFournis;
import org.cocktail.fwkcktlpersonne.common.metier.EOGrhumParametres;
import org.cocktail.fwkcktlpersonne.common.metier.EORepartPersonneAdresse;
import org.cocktail.fwkcktlpersonne.common.metier.EOTypeAdresse;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;
import org.cocktail.fwkcktlpersonneguiajax.serveur.fournis.controleurs.FournisseurFormCtrl;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSForwardException;

import er.extensions.appserver.ERXRedirect;
import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXEOControlUtilities;
import er.extensions.eof.ERXQ;

/**
 * 
 * Composant de gestion des fournisseurs.
 * 
 * @author Alexis TUAL <alexis.tual at cocktail.org>
 *
 */
public class GestionFournisseurs extends MyWOComponent {

    private static final long serialVersionUID = 1377563365934679531L;
    private String sectionFournisseur;
    private NSArray<String> modules;
    private NSArray<String> etapes;
    private EOEditingContext editingContext;
    private EOFournis selectedFournisseur;
    private IPersonne selectedPersonne;
    private EOCompte  selectedCompteForFournis;
    private FournisseurFormCtrl ctrl = new FournisseurFormCtrl();
    private String previousFouValide;
    private boolean modeIndividu;
    
    public GestionFournisseurs(WOContext context) {
        super(context);
    }
    
    public String getSectionFournisseur() {
        return sectionFournisseur;
    }
    
    public void setSectionFournisseur(String sectionFournisseur) {
        this.sectionFournisseur = sectionFournisseur;
    }

    /**
     * @return the editingContext
     */
    public EOEditingContext editingContext() {
        if (editingContext == null) {
            editingContext = ERXEC.newEditingContext();
        }
        return editingContext;
    }
    
    /**
     * @return the modules
     */
    public NSArray<String> modules() {
        modules = new NSArray<String>(new String[] { 
                "ModuleFournisseurRecherche",
                "ModuleFournisseurInformations",
                "ModuleFournisseurAdresse",
                "ModulePersonneTelephone",
                "ModuleFournisseurCompte",
                "ModulePersonneGroupe",
                "ModuleFournisseurValidation"
                });
        if (isModeIndividu()) {
        	modules = modules.arrayByAddingObject("ModulePersonneDocumentGED");
		}
        return modules;
    }

    public NSArray<String> etapes() {
        etapes = new NSArray<String>(new String[] { 
                "Recherche",
                "Informations",
                "Adresses",
                "Telephones",
                "Comptes",
                "Groupes",
                "État de validation"
                });
        if (isModeIndividu()) {
        	etapes = etapes.arrayByAddingObject("Documents");
		}
        return etapes;
    }
    
    public WOActionResults terminer() {
        IPersonne personne = getSelectedPersonne();
        // preparer le fournisseur
        prepareFournis();
        if (personne != null) {
            try {
                if (personne.editingContext().hasChanges()){
                    personne.setPersIdModification(session().applicationUser().getPersonne().persId());
                    personne.editingContext().saveChanges();
                    try {
						envoyerNotifications();
					} catch (Exception e) {
						// Si le mail ne part pas on log et affiche le message 
						// d'erreur mais on ne bloque pas l'enregistrement.
						logger().error(e.getMessage(), e);
						session().addSimpleErrorMessage(e.getLocalizedMessage(), e);
					}
                    String complement = null;
                    if (!getSelectedFournisseur().hasRibsValides())
                        complement = localizer().localizedStringForKey("fournis.rib");
                    session().addSimpleInfoMessage(
                            localizer().localizedTemplateStringForKeyWithObject("fournis.valid", getSelectedPersonne()), complement);
                    session().setIndexModuleActifGestionPersonne(null);
                    ERXRedirect redirect = (ERXRedirect)pageWithName(ERXRedirect.class.getName());
                    WOComponent accueil = pageWithName(Accueil.class.getName());
                    redirect.setComponent(accueil);
                    return redirect;
                }
            }  catch (ValidationException e2) {
                logger().info(e2.getMessage(), e2);
                session().addSimpleErrorMessage(e2.getLocalizedMessage(), e2);
            } catch (Exception e) {
                logger().error(e.getMessage(), e);
                throw new NSForwardException(e, "Une erreur est survenue lors de l'enregistrement en base");
            }

        }
        return null;
    }
    
    private void envoyerNotifications() throws Exception {
        if (ERXEOControlUtilities.isNewObject(getSelectedFournisseur())) {
            //Envoyer le mail aux valideurs si fournisseur pas valide
            if (getSelectedFournisseur().isFouValideEncours() && !session().applicationUser().peutValiderFournisseur()) {
                    ctrl.sendMailDemandeValidationToValideurs(getSelectedFournisseur());
            }
        }
        else {
            if (!getSelectedFournisseur().fouValide().equals(previousFouValide)) {
                //Si le valideur n'est pas le createur
                if (getSelectedFournisseur().toValideFournis().getCreateurAsUtilisateur() != null && 
                        !getSelectedFournisseur().toValideFournis().getValideurAsUtilisateur().equals(
                                getSelectedFournisseur().toValideFournis().getCreateurAsUtilisateur())) {
                    ctrl.sendMailInformationValidation(getSelectedFournisseur());
                }
            }
        }
        
    }

    @SuppressWarnings("unchecked")
    private void prepareFournis() {
        Integer userPersId = session().applicationUser().getPersId();
        NSArray<EORepartPersonneAdresse> repartsFacturation = 
            ERXQ.filtered(getSelectedPersonne().toRepartPersonneAdresses(), 
                    ERXQ.equals(EORepartPersonneAdresse.TADR_CODE_KEY, EOTypeAdresse.TADR_CODE_FACT));
        if (repartsFacturation != null)
            getSelectedFournisseur().setToAdresseRelationship(repartsFacturation.lastObject().toAdresse());
        // Si le compte est null, on le cree (si demandé)
        try {
            if (session().applicationUser().peutValiderFournisseur()) {
                if (getSelectedCompteForFournis() == null && creerCompteAuto()) {
                    getSelectedFournisseur().associerCompte(userPersId);
                } else if (getSelectedCompteForFournis() != null) {
                    getSelectedFournisseur().setToCompteRelationship(getSelectedCompteForFournis().localInstanceIn(editingContext()));
                }
            }
            getSelectedFournisseur().initialise(
                    userPersId, 
                    (repartsFacturation.lastObject() == null ? null : repartsFacturation.lastObject().toAdresse()), 
                    getSelectedFournisseur().isEtranger(), 
                    getSelectedFournisseur().fouType());
        } catch (Exception e) {
            throw new NSForwardException(e, "Une erreur est survenue lors de l'enregistrement du fournisseur");
        }
    }
    
    private boolean creerCompteAuto() {
        return EOGrhumParametres.OUI.equals(
                    EOGrhumParametres.parametrePourCle(edc(), EOGrhumParametres.PARAM_ANNUAIRE_FOU_COMPTE_AUTO));
    }
    
    public WOActionResults annuler() {
        Accueil nextPage = (Accueil)pageWithName(Accueil.class.getName());
        if (editingContext() != null) {
            editingContext().revert();
        }
        session().setIndexModuleActifGestionPersonne(null);
        return nextPage;
    }
    
    public EOFournis getSelectedFournisseur() {
        return selectedFournisseur;
    }
    
    public void setSelectedFournisseur(EOFournis selectedFournisseur) {
        // Lors de la première affectation on retient le code avant qu'il ne soit modifié 
        if (this.selectedFournisseur == null & selectedFournisseur != null)
            this.previousFouValide = selectedFournisseur.fouValide();
        this.selectedFournisseur = selectedFournisseur;
        setSelectedCompteForFournis(selectedFournisseur.toCompte());
    }
    
    public IPersonne getSelectedPersonne() {
        return selectedPersonne;
    }
    
    public void setSelectedPersonne(IPersonne selectedPersonne) {
        this.selectedPersonne = selectedPersonne;
    }
    
    public EOCompte getSelectedCompteForFournis() {
        return selectedCompteForFournis;
    }
    
    public void setSelectedCompteForFournis(EOCompte selectedCompteForFournis) {
        this.selectedCompteForFournis = selectedCompteForFournis;
    }
    
    
    public void setModeIndividu(boolean modeIndividu) {
        this.modeIndividu = modeIndividu;
    }
    
    public boolean isModeStructure() {
        return !isModeIndividu();
    }
    
    public boolean isModeIndividu() {
        return modeIndividu;
    }
    
}