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

import org.apache.log4j.Logger;
import org.cocktail.fwkcktlajaxwebext.serveur.component.CktlAjaxWindow;
import org.cocktail.fwkcktlpersonne.common.metier.EOAssociation;
import org.cocktail.fwkcktlpersonne.common.metier.EOAssociationReseau;
import org.cocktail.fwkcktlpersonne.common.metier.EOTypeAssociation;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSTimestamp;
 
/**
 * 
 * Composant de création simple d'un rôle ou d'une fonction.
 * 
 * Ce composant doit être englobé dans un formulaire. Ce composant gère son propre ec.
 * 
 * @binding parent le EOAssociation parent vers lequel sera créé une relation pour identifier le parent
 * dans EOAssociationReseau.
 * @binding onCreationSuccess callback appelé lorsque l'enregistrement a été fait avec succès
 * @binding wantReset flag pour indiquer à ce composant de reset son état interne
 * 
 * @author Alain MALAPLATE <alain.malaplate at cocktail.org>
 *
 */
public class CreationRoleForm extends MyWOComponent {

    private static final long serialVersionUID = 347883843978759995L;
    private static final Logger LOG = Logger.getLogger(CreationRoleForm.class);
    public static final String BINDING_PARENT = "parent";
    public static final String BINDING_ON_CREATION_SUCCESS = "onCreationSuccess";

    public static final String BINDING_NEW_ROLE = "newRole";
    public static final String BINDING_ASSO_RESEAU = "newAssociationReseau";
    public static final String BINDING_TYPE_ASSO = "typeAssociation";
    
    
    private String libelleFonction;
    private String codeFonction;
    private final String roleLocal = "O";
    private String commentaireAssoReseau;
    private String rangAssoReseau;
    
    private EOAssociationReseau nouveauLienParente;
    
    private EOAssociation parent;
    
    public CreationRoleForm(WOContext context) {
        super(context);
    }
    
    
    @Override
    public void appendToResponse(WOResponse response, WOContext context) {
        super.appendToResponse(response, context);
        
    }
    
    private EOEditingContext editingContext() {
        return getNewRole().editingContext();
    }
    
    public EOAssociation getNewRole() {
		return (EOAssociation)valueForBinding(BINDING_NEW_ROLE);
	}
    
    public EOAssociationReseau getNewAssociationReseau() {
		return (EOAssociationReseau)valueForBinding(BINDING_ASSO_RESEAU);
	}
    
    public EOTypeAssociation getTypeAssociation() {
		return (EOTypeAssociation)valueForBinding(BINDING_TYPE_ASSO);
	}
    
    public EOAssociation getParent() {
    	if (hasBinding(BINDING_PARENT) && valueForBinding(BINDING_PARENT) != null) {
			setParent((EOAssociation) valueForBinding(BINDING_PARENT));
		} else {
			 // Le Parent est la racine par défaut
			setParent(EOAssociation.fetchByKeyValue(editingContext(), EOAssociation.ASS_CODE_KEY, EOAssociation.ASS_CODE_FONCTION ) );
		}
    	return parent;
    }
    
    
    
    public void setParent(EOAssociation parent) {
		this.parent = parent;
	}

	public String getLibelleFonction() {
		return libelleFonction;
	}

	public void setLibelleFonction(String libelleFonction) {
		this.libelleFonction = libelleFonction;
	}

	public String getCodeFonction() {
		return codeFonction;
	}

	public void setCodeFonction(String codeFonction) {
		this.codeFonction = codeFonction;
	}
	
	public String getCommentaireAssoReseau() {
		return commentaireAssoReseau;
	}

	public void setCommentaireAssoReseau(String libelleAssoReseau) {
		this.commentaireAssoReseau = libelleAssoReseau;
	}

	public String getRangAssoReseau() {
		return rangAssoReseau;
	}

	public void setRangAssoReseau(String rangAssoReseau) {
		this.rangAssoReseau = rangAssoReseau;
	}

	public String getRoleLocal() {
		return roleLocal;
	}

	
	public WOActionResults annuler() {
		if (editingContext() != null) {
			editingContext().revert();
		}
		CktlAjaxWindow.close(context());
		return null;
	}
    
    
    public WOActionResults ajouterRole() {
        try {
            // Vérifier le nom
            if (getCodeFonction() == null || getCodeFonction().length() == 0
            		|| getLibelleFonction() == null
            			|| getLibelleFonction().length() == 0){
            	throw new ValidationException("Le nom du rôle ou de la fonction ne doit pas être vide");
            }
            getNewRole().setAssCode(getCodeFonction());
            getNewRole().setAssLibelle(getLibelleFonction());
            getNewRole().setAssLocale(getRoleLocal());
            getNewRole().setToTypeAssociationRelationship(getTypeAssociation());
            getNewRole().setDCreation(new NSTimestamp());
            getNewRole().setDModification(new NSTimestamp());
            getNewRole().setDOuverture(new NSTimestamp());
            
            
            getNewAssociationReseau().setAsrCommentaire(getCommentaireAssoReseau());
            if (getRangAssoReseau() != null) {
            	getNewAssociationReseau().setAsrRang(Double.valueOf(getRangAssoReseau()));
            } else {
            	getNewAssociationReseau().setAsrRang(null);
            }
            getNewAssociationReseau().setToAssociationFilsRelationship(getNewRole());
            getNewAssociationReseau().setToAssociationPereRelationship(getParent());
            getNewAssociationReseau().setDCreation(new NSTimestamp());
            getNewAssociationReseau().setDModification(new NSTimestamp());
            
            editingContext().saveChanges();
            session().addSimpleInfoMessage("Nouveau rôle " + getLibelleFonction() + " créé avec succès", 
                    "Le rôle a été enregistré dans le référentiel");
            
            if (hasBinding(BINDING_NEW_ROLE)) {
				setValueForBinding(getNewRole(), BINDING_NEW_ROLE);
			}
            
            if (hasBinding(BINDING_ON_CREATION_SUCCESS)) {
                return (WOActionResults)valueForBinding(BINDING_ON_CREATION_SUCCESS);
            }
        } catch (ValidationException e) {
            LOG.warn(e);
            session().addSimpleErrorMessage(e.getLocalizedMessage(), e);
        } catch (Exception e) {
            LOG.error(e);
            throw new NSForwardException(e);
        }
        
        CktlAjaxWindow.close(context());
        return null;
    }
    
	
	public WOActionResults onCloseResultModal() {
        return null;
    }
    
	public EOAssociationReseau getNouveauLienParente() {
		return nouveauLienParente;
	}

	public void setNouveauLienParente(EOAssociationReseau nouveauLienParente) {
		this.nouveauLienParente = nouveauLienParente;
	}

}