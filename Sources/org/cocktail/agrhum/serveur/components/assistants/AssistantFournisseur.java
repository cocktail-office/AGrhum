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
 
package org.cocktail.agrhum.serveur.components.assistants;

import org.cocktail.fwkcktlpersonne.common.metier.EOFournis;
import org.cocktail.fwkcktlpersonne.common.metier.EORepartPersonneAdresse;
import org.cocktail.fwkcktlpersonne.common.metier.EOTypeAdresse;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

import er.ajax.AjaxUpdateContainer;
import er.extensions.eof.ERXQ;

/**
 * 
 * 
 * @author Alexis TUAL <alexis.tual at cocktail.org>
 * @author Pierre-Yves MARIE <pierre-yves.marie at cocktail.org>
 *
 */
public class AssistantFournisseur extends Assistant {

    private static final long serialVersionUID = 136884795941111191L;
    public static final String PERSONNE_BDG = "personne";
    public static final String FOURNISSEUR_BDG = "fournisseur";

    private IPersonne personne;
    private EOFournis fournis;

    public AssistantFournisseur(WOContext context) {
        super(context);
    }
    
    @Override
    public void reset() {
        personne = null;
        fournis = null;
        setEditMode(false);
        super.reset();
    }

    public String idContainerGestionMenu() {
        return "ContainerPersonneGestionMenu";
    }

    public WOActionResults refreshActions() {
        AjaxUpdateContainer.updateContainerWithID(idContainerGestionMenu(), context());
        return null;
    }
    
    /**
     * @return the personne
     */
    public IPersonne personne() {
        if (hasBinding(PERSONNE_BDG)) {
            personne = (IPersonne) valueForBinding(PERSONNE_BDG);
        }
        return personne;
    }

    /**
     * @param personne
     *            the personne to set
     */
    public void setPersonne(IPersonne personne) {
        this.personne = personne;
        if (hasBinding(PERSONNE_BDG)) {
            setValueForBinding(personne, PERSONNE_BDG);
        }
    }

    public EOFournis fournisseur() {
        if (hasBinding(FOURNISSEUR_BDG)) {
            fournis = (EOFournis)valueForBinding(FOURNISSEUR_BDG);
        }
        return fournis;
    }
    
    public void setFournisseur(EOFournis fournis) {
        this.fournis = fournis;
        if (hasBinding(FOURNISSEUR_BDG)) {
            setValueForBinding(fournis, FOURNISSEUR_BDG);
        }
    }
    
    @Override
    public boolean isTerminerDisabled() {
        return personne() == null || !hasAdresseFacturation();
    }

    public boolean isPrecedentEnabled() {
        return !super.isPrecedentDisabled();
    }

    public boolean isSuivantEnabled() {
        return !super.isSuivantDisabled();
    }

    public boolean isTerminerEnabled() {
        return !isTerminerDisabled();
    }
    
    private boolean hasAdresseFacturation() {
        NSArray<EORepartPersonneAdresse> repartsFacturation = 
            ERXQ.filtered(personne().toRepartPersonneAdresses(), 
                    ERXQ.equals(EORepartPersonneAdresse.TADR_CODE_KEY, EOTypeAdresse.TADR_CODE_FACT));
        return repartsFacturation.count() > 0;
    }
    
    public String tabsId() {
		return "Tabs_" + uniqueDomId();
	}
    
    public String etapeId() {
		return "etape_" + uneEtape + uniqueDomId();
	}

	public String getUneEtapeName() {
		String etapeName = "";
		if (uneEtape!=null) {
			etapeName = uneEtape;
		}
		return etapeName;
	}

	public boolean isModuleEtapeActive() {
		return (uneEtape!=null) ? isEtapeActive() : false;
	}
    
}