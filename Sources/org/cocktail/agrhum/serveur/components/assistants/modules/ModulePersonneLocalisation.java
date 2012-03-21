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
import org.cocktail.fwkgspot.serveur.metier.eof.EOTypeOccupation;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

/**
 * Module de localisation de personne.
 * 
 * @author Alexis TUAL <alexis.tual at cocktail.org>
 */
public class ModulePersonneLocalisation extends ModuleAssistant implements
IModuleAssistant {

    private static final long serialVersionUID = 7928198279382826811L;
    private Boolean hasGspotTables;
    
    private static final String WANT_RESET = "wantReset";

    public ModulePersonneLocalisation(WOContext context) {
        super(context);
    }
    
    
    @Override
	public void appendToResponse(WOResponse woresponse, WOContext wocontext) {
		if (wantReset()) {
			hasGspotTables = null;
//			setWantReset(Boolean.FALSE);
		}
		super.appendToResponse(woresponse, wocontext);
	}
    
    /**
     * @return true si les tables de Gspot sont présentes.
     *          Il faut voir si à la place, on ne peut pas carrément forcer 
     *          une vérif de dépendance au démarrage via les versions de bdd...
     */
    public Boolean hasGspotTables() {
        if (hasGspotTables == null) {
            try {
                EOTypeOccupation.fetchAllFwkGspot_TypeOccupations(editingContext());
                hasGspotTables = Boolean.TRUE;
            } catch (Exception e) {
                hasGspotTables = Boolean.FALSE;
                logger().error(e.getMessage(), e);
            }
        }
        return hasGspotTables;
    }
    
    public boolean wantReset() {
    	return (Boolean) valueForBinding(WANT_RESET);
	}

	public void setWantReset(Boolean value) {
		setValueForBinding(value, WANT_RESET);
	}
	
	public Boolean peutEditerPersonneLocalisation() {
		return applicationUser().getPersId().equals(personne().persId()) || applicationUser().hasDroitTous()
				|| applicationUser().iPersonne().getPersonneDelegate().isGroupesAffectesPourIndividusOrPeresInGroupesAdministres((EOIndividu) personne());
	}
	
}