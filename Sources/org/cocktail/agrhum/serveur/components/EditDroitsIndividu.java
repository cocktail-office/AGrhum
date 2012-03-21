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

import org.cocktail.fwkcktldroitsutils.common.metier.EOAgentAdresse;
import org.cocktail.fwkcktlpersonne.common.metier.EOCompte;
import org.cocktail.fwkcktlpersonne.common.metier.EOIndividu;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;

import er.extensions.eof.ERXEOControlUtilities;

/**
 * 
 * Composant d'édition des droits, similaire à Annuaire.app.
 * Futur déprécié avec la nouvelle gestion des droits... à ne pas utiliser en dehors d'agrhum donc !
 * 
 * @author Alexis TUAL <alexis.tual at cocktail.org>
 *
 */
public class EditDroitsIndividu extends MyWOComponent {

    private static final long serialVersionUID = 3743681426064092106L;
    private static final String BINDING_INDIVIDU = "individu";
    private static final String BINDING_EC = "editingContext";
    private EOIndividu individu;
    private EOCompte compte;
    private EOAgentAdresse agentAdresse;

    public EditDroitsIndividu(WOContext context) {
        super(context);
    }
    
    @Override
    public boolean synchronizesVariablesWithBindings() {
        return false;
    }
    
    @Override
    public void appendToResponse(WOResponse response, WOContext context) {
        individu();
        super.appendToResponse(response, context);
    }
    
    @SuppressWarnings("unchecked")
    private EOIndividu individu() {
        EOIndividu indivTmp = (EOIndividu)valueForBinding(BINDING_INDIVIDU);
        if (individu == null || !ERXEOControlUtilities.eoEquals(individu, indivTmp)) {
            individu = indivTmp.localInstanceIn(editingContext());
            NSArray<EOCompte> comptes = individu.toComptes(EOCompte.QUAL_CPT_VLAN_P_R);
            if (!comptes.isEmpty())
                compte = comptes.objectAtIndex(0);
            agentAdresse = EOAgentAdresse.fetchByKeyValue(editingContext(), EOAgentAdresse.NO_INDIVIDU_KEY, individu.noIndividu());
        }
        return individu;
    }
    
    private EOEditingContext editingContext() {
        return (EOEditingContext)valueForBinding(BINDING_EC);
    }
    
    private boolean toBoolean(String code) {
        return EOAgentAdresse.OUI.equals(code);
    }
    
    private String toString(boolean value) {
        return value ? EOAgentAdresse.OUI : EOAgentAdresse.NON;
    }
    
    public WOActionResults creerDroits() {
        agentAdresse = EOAgentAdresse.creerInstance(editingContext());
        agentAdresse.setNoIndividu(individu().noIndividu());
        agentAdresse.setAgtVoirInfosPerso(EOAgentAdresse.NON);
        agentAdresse.setAgtLogin(compte.cptLogin());
        return null;
    }
    
    public WOActionResults supprimerDroits() {
        editingContext().deleteObject(agentAdresse());
        agentAdresse = null;
        return null;
    }
    
    public EOAgentAdresse agentAdresse() {
        return agentAdresse;
    }
    
    public boolean hasCompteAdminOuRecherche() {
        return compte != null;
    }
    
    public boolean hasDroits() {
        return agentAdresse() != null;
    }
    
    public boolean isSuperUser() {
        return toBoolean(agentAdresse().agtTout());
    }
    
    public void setSuperUser(boolean value) {
        agentAdresse().setAgtTout(toString(value));
    }
    
    public boolean isInfosPersos() {
        return toBoolean(agentAdresse().agtVoirInfosPerso());
    }
    
    public void setInfosPersos(boolean value) {
        agentAdresse().setAgtVoirInfosPerso(toString(value));
    }
    
    public boolean photos() {
        return toBoolean(agentAdresse().agtPhoto());
    }
    
    public void setPhotos(boolean value) {
        agentAdresse().setAgtPhoto(toString(value));
    }
    
    public boolean civilite() {
        return toBoolean(agentAdresse().agtCiviliteModifs());
    }
    
    public void setCivilite(boolean value) {
        agentAdresse().setAgtCiviliteModifs(toString(value));
    }
    
    public boolean forum() {
        return toBoolean(agentAdresse().agtForum());
    }
    
    public void setForum(boolean value) {
        agentAdresse().setAgtForum(toString(value));
    }
    
    public boolean valide() {
        return toBoolean(agentAdresse().agtTemValide());
    }
    
    public void setValide(boolean value) {
        agentAdresse().setAgtTemValide(toString(value));
    }
    
    public boolean suppression() {
        return toBoolean(agentAdresse().agtSuppression());
    }
    
    public void setSuppression(boolean value) {
        agentAdresse().setAgtSuppression(toString(value));
    }
    
    public boolean ajout() {
        return toBoolean(agentAdresse().agtAjout());
    }
    
    public void setAjout(boolean value) {
        agentAdresse().setAgtAjout(toString(value));
    }
    
    public boolean compteVisu() {
        return toBoolean(agentAdresse().agtCompte());
    }
    
    public void setCompteVisu(boolean value) {
        agentAdresse().setAgtCompte(toString(value));
    }
    
    public boolean compteModif() {
        return toBoolean(agentAdresse().agtCompteModifs());
    }
    
    public void setCompteModif(boolean value) {
        agentAdresse().setAgtCompteModifs(toString(value));
    }
    
    public boolean compteTemp() {
        return toBoolean(agentAdresse().agtCptTempo());
    }
    
    public void setCompteTemp(boolean value) {
        agentAdresse().setAgtCptTempo(toString(value));
    }
    
    
    public String contId() {
        return "EdroitsInd_" + uniqueDomId();
    }
    
}