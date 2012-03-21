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

import org.cocktail.fwkcktlpersonne.common.metier.EORepartPersonneAdresse;
import org.cocktail.fwkcktlpersonne.common.metier.EOTypeAdresse;

import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

import er.extensions.eof.ERXQ;

public class ModuleFournisseurAdresse extends ModuleAssistant implements IModuleAssistant {

    private static final long serialVersionUID = 5273441551489018284L;
    private static final String BINDING_FOURNISSEUR = "fournisseur";
    private EORepartPersonneAdresse selectedRepartPersonneAdresse;
    private Boolean isAdresseEditing;
    private EOQualifier qualifierAdrPro;
    
    public ModuleFournisseurAdresse(WOContext context) {
        super(context);
    }
    
    @Override
    public boolean isSuivantDisabled() {
        return !hasAdresseFacturation() || super.isSuivantDisabled();
    }
    
    private boolean hasAdresseFacturation() {
        NSArray<EORepartPersonneAdresse> repartsFacturation = 
            ERXQ.filtered(personne().toRepartPersonneAdresses(), 
                    ERXQ.equals(EORepartPersonneAdresse.TADR_CODE_KEY, EOTypeAdresse.TADR_CODE_FACT));
        return repartsFacturation.count() > 0;
    }
    
    public EORepartPersonneAdresse selectedRepartPersonneAdresse() {
        return selectedRepartPersonneAdresse;
    }

    public void setSelectedRepartPersonneAdresse(EORepartPersonneAdresse repartPersonneAdresse) {
        this.selectedRepartPersonneAdresse = repartPersonneAdresse;
    }

    /**
     * @return the isAdresseEditing
     */
    public Boolean isAdresseEditing() {
        return isAdresseEditing;
    }

    /**
     * @param isAdresseEditing the isAdresseEditing to set
     */
    public void setIsAdresseEditing(Boolean isAdresseEditing) {
        this.isAdresseEditing = isAdresseEditing;
    }
    
    public void setQualifierAdrPro(){
        this.qualifierAdrPro = EOTypeAdresse.QUAL_TADR_CODE_PRO;
    }
    
    public EOQualifier qualifierAdrPro() {
        if (this.qualifierAdrPro == null) 
            this.qualifierAdrPro = EOTypeAdresse.QUAL_TADR_CODE_PRO;
        return this.qualifierAdrPro;
    }

	public String getDefaultTypeAddresseFacturation() {
		return EOTypeAdresse.TADR_CODE_FACT;
	}
    
}