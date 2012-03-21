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

import org.cocktail.fwkcktlajaxwebext.serveur.component.CktlAjaxWindow;
import org.cocktail.fwkcktlpersonne.common.metier.EOAssociation;
import org.cocktail.fwkcktlpersonne.common.metier.EOAssociationReseau;
import org.cocktail.fwkcktlpersonne.common.metier.EOTypeAssociation;
import org.cocktail.fwkcktlpersonneguiajax.serveur.controleurs.NotificationCtrl;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSValidation;

import er.extensions.eof.ERXEC;

/**
 * La gestion des rôles ou des fonctions a été prévue en priorité pour
 * le Type d'Association nommé "Rôles".
 * Cependant, une gestion des Types d'Association a été mise en place qui impacte sur
 * les fonctions affichées ensuite.
 * 
 * @author Alain MALAPLATE <alain.malaplate at cocktail.org>
 *
 */
public class GestionRolesEtFonctions extends MyWOComponent {
	private static final long serialVersionUID = 3727803841626364185L;
	
	private EOAssociation roleOuFonction = null;
	private EOAssociation selectedAssociationForMove;
	private EOAssociation selectedAssociationForDel;
	private EOAssociation newCreatedAssociation;
	private EOAssociationReseau newAssociationReseau;
	
	private EOAssociation associationRacine;
	private EOAssociation fonctionParente;
	private EOTypeAssociation selectedTypeAssociation;
	private EOTypeAssociation typeAssociation;
	private NSDictionary<String, EOQualifier> filters;
	
	private boolean resetEditRole = true;
	private boolean resetLeftTreeView;
	private boolean resetMoveTreeView = true;
	
	private boolean isRole;
	
	private boolean isSelectedRole = false;
	private EditRole wocomponent;
	
	private String sectionRolesFonctions;
	private static final String ROLE_CONTAINER_ID = "roleContainer";
	private EOEditingContext editingContext;
	
	public GestionRolesEtFonctions(WOContext context) {
		super(context);
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
	
	public String getSectionRolesFonctions() {
        return sectionRolesFonctions;
    }
    
    public void setSectionRolesFonctions(String sectionRolesFonctions) {
        this.sectionRolesFonctions = sectionRolesFonctions;
    }
    
    public EOAssociation getRoleOuFonction() {
		return roleOuFonction;
	}

	public void setRoleOuFonction(EOAssociation roleOuFonction) {
		if (roleOuFonction != null){
			setSelectedRole(true);
			setResetEditRole(true);
		}
		this.roleOuFonction = roleOuFonction;
	}
    
    public String roleContainerId() {
		return ROLE_CONTAINER_ID;
	}
    
	public boolean isResetMoveTreeView() {
        return resetMoveTreeView;
    }
	
	public void setResetMoveTreeView(boolean resetMoveTreeView) {
        this.resetMoveTreeView = resetMoveTreeView;
    }
	
	
	public EOAssociation getAssociationRacine() {
		if (associationRacine == null) {
			setAssociationRacine( EOAssociation.fetchByQualifier(editingContext(), EOAssociation.QUAL_RACINE));
		}
		return associationRacine;
	}

	public void setAssociationRacine(EOAssociation associationRacine) {
		this.associationRacine = associationRacine;
	}
	
	public EOAssociation getFonctionParente() {
		return fonctionParente;
	}

	public void setFonctionParente(EOAssociation fonctionParente) {
		this.fonctionParente = fonctionParente;
	}
	
	public EOAssociation getSelectedAssociationForDel() {
		return selectedAssociationForDel;
	}

	public void setSelectedAssociationForDel(EOAssociation selectedAssociationForDel) {
		this.selectedAssociationForDel = selectedAssociationForDel;
	}

	public EOAssociation getSelectedAssociationForMove() {
        return selectedAssociationForMove;
    }
	
	public void setSelectedAssociationForMove(EOAssociation selectedAssociationForMove) {
        this.selectedAssociationForMove = selectedAssociationForMove;
    }
	
	public EOTypeAssociation getSelectedTypeAssociation() {
		return selectedTypeAssociation;
	}

	public void setSelectedTypeAssociation(EOTypeAssociation selectedTypeAssociation) {
		this.selectedTypeAssociation = selectedTypeAssociation;
	}
	
	public EOTypeAssociation getTypeAssociation() {
		return typeAssociation;
	}

	public void setTypeAssociation(EOTypeAssociation typeAssociation) {
		this.typeAssociation = typeAssociation;
	}
	
	public boolean isResetEditRole() {
		return resetEditRole;
	}

	public void setResetEditRole(boolean resetEditRole) {
		this.resetEditRole = resetEditRole;
	}
	
	public boolean isResetLeftTreeView() {
		return resetLeftTreeView;
	}

	public void setResetLeftTreeView(boolean resetLeftTreeView) {
		this.resetLeftTreeView = resetLeftTreeView;
	}
	
	public boolean isRole() {
		if ( getSelectedTypeAssociation().tasLibelle().equals("Rôle") ){
			setRole(true);
		} else {
			setRole(false);
		}
		return isRole;
	}

	public void setRole(boolean isRole) {
		this.isRole = isRole;
	}
	
	public boolean peutAjouterRole(){
		return isRole() && peutModifierRole();
	}
	
	public boolean peutModifierRole(){
		return session().applicationUser().hasDroitGrhumCreateur();
	}
	
	public boolean peutSupprimerRole(){
		return session().applicationUser().hasDroitGrhumCreateur();
	}
	
	/* Les droits de modification sont plus simples que dans le cas des groupes. */
	public boolean peutModifierRoleSelectionne() {
	    return isRoleSelectionne() && peutModifierRole() && isLocal();
	}
	
	/* Les droits de suppression sont quasi identiques à ceux pour la modification.*/
	// TODO : Les droits doivent être modifiés dans le cas d'un archivage.
	public boolean peutSupprimerRoleSelectionne() {
	    return isRoleSelectionne() && peutSupprimerRole() && isLocal();
	}
	
	
	public boolean isLocal() {
		if ( getRoleOuFonction().assLocale().equals("O")){
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isRoleSelectionne() {
		return (getRoleOuFonction() != null);
	}

	public String getDeleteSelectedRoleTitle() {
		String title = "Supprimer(fermeture) le rôle ou la fonction locale";
		if (getRoleOuFonction() != null) {
			title = title + " "+ getRoleOuFonction().assLibelle();
		}
		return title;
	}
	
	
	public WOActionResults annuler() {
		return null;
	}
	
	public WOActionResults annulerDelete() {
		CktlAjaxWindow.close(context());
		return null;
	}
	
	public WOActionResults closeAndSelectNewRole() {
        CktlAjaxWindow.close(context(), "AjoutRoleModalW");
        if (getNewCreatedAssociation() != null) {
			setRoleOuFonction(getNewCreatedAssociation());
			setSelectedRole(true);
		}
        return null;
    }
	
	public WOActionResults deleteRole() {
	    prepareRefreshLeftTreeView();
			// TODO : voir si on archive ou si on supprime pur et dur
	    try {
			getRoleOuFonction().archiver();
	    	editingContext().saveChanges();
	    } catch (Exception e) {
			logger().info(e.getMessage(), e);
			session().addSimpleErrorMessage(e.getLocalizedMessage(), e);
			context().response().setStatus(500);
		}
	    setSelectedRole(false);
        return null;
    }
	
	public WOActionResults doNuthin() {
		return null;
	}
	
	public WOActionResults onClose() {
		prepareRefreshLeftTreeView();
		return null;
	}
	
	private void prepareRefreshLeftTreeView() {
		NSMutableDictionary<String, Object> userInfo = new NSMutableDictionary<String, Object>();
		userInfo.setObjectForKey(editingContext(), "edc");
		NotificationCtrl.postNotificationForOnAnnulerNotification(this, userInfo);
		setResetLeftTreeView(true);
	}

	public WOActionResults openAjoutRole() {
		// On active la création d'un rôle et on instancie un rôle vide ainsi qu'une nouvelle instance de réseau d'association.
	    editingContext().revert();
	    newCreatedAssociation = EOAssociation.creerInstance(editingContext());
	    newAssociationReseau = EOAssociationReseau.creerInstance(editingContext());
	    setTypeAssociation(getSelectedTypeAssociation().localInstanceIn(editingContext()));
	    if (getRoleOuFonction() != null){
	    	setFonctionParente(getRoleOuFonction().localInstanceIn(editingContext()));
	    } else {
	    	setFonctionParente(null);
	    }
	    
        return null;
    }
	
	
	public WOActionResults openMoveRole() {
	    setResetMoveTreeView(true);
	    return null;
	}
	
	public WOActionResults onSelectRole() {
		return null;
	}
	
	public WOActionResults onSelectRoleForMove() {
	    // déplacer le groupe vers le groupe sélectionné
	    if (getSelectedAssociationForMove() != null) {
	    	// TODO : mettre les archives en place & la relation vers l'ID du Père
	    	if (getRoleOuFonction().dFermeture().before(new NSTimestamp())) {
	    		getRoleOuFonction().setDFermeture(null);
			}
	    	EOAssociationReseau lienAssoPereFils = EOAssociationReseau.rechercheAssociationReseau(getRoleOuFonction(), getFonctionParente(), editingContext());
	    	lienAssoPereFils.setToAssociationPereRelationship(getSelectedAssociationForMove());
				      
	        save();
	    }
	    CktlAjaxWindow.close(context(), associationTreeViewDialogID());
	    return null;
	}
	
	protected void save() {
		try {
			editingContext().saveChanges();
			session().addSimpleInfoMessage(session().localizer().localizedStringForKey("confirmation"), null);
		} catch (NSValidation.ValidationException e) {
			logger().info(e.getMessage(), e);
			session().addSimpleErrorMessage(e.getLocalizedMessage(), e);
			context().response().setStatus(500);
		} catch (EOGeneralAdaptorException e) {
		    edc().revert();
			logger().warn(e.getMessage(), e);
			context().response().setStatus(500);
			session().addSimpleErrorMessage("Une erreur est survenue lors de l'enregistrement en base", e);
		}
	}
	
	public WOActionResults terminer() {
		save();
		return null;
	}
	
	
	
	public String beforeClickOnDelete() {
		if (getRoleOuFonction() != null){
			return "confirm('Voulez-vous fermer la fonction locale sélectionnée (" + getRoleOuFonction().assLibelle() + ") ? ATTENTION : les fonctions filles vont aussi être fermées')";
		} else {
			return "alert('Vous voulez supprimer une fonction mais aucune n'est sélectionnée !')";
		}
	}

	/**
	 * @return the newCreatedAssociation
	 */
	public EOAssociation getNewCreatedAssociation() {
		return newCreatedAssociation;
	}
	
	/**
	 * @param newCreatedAssociation the newCreatedAssociation to set
	 */
	public void setNewCreatedAssociation(EOAssociation newCreatedAssociation) {
		this.newCreatedAssociation = newCreatedAssociation;
	}
	
	/**
	 * @return the newAssociationReseau
	 */
	public EOAssociationReseau getNewAssociationReseau() {
		return newAssociationReseau;
	}
	/**
	 * @param newAssociationReseau the newAssociationReseau to set
	 */
	public void setNewAssociationReseau(EOAssociationReseau newAssociationReseau) {
		this.newAssociationReseau = newAssociationReseau;
	}

	
	public NSDictionary<String, EOQualifier> filters() {
		if (filters == null) {
			filters = 
				new NSMutableDictionary<String, EOQualifier>();
			
			/** Associations valides */
			// Non rajouté car c'est le qualifier et donc le filtre par défaut
//			filters.put("Valides", EOAssociation.QUAL_ASS_VALIDE);
			/** Associations institutionnelles */
			filters.put("Institutionnelles", EOAssociation.QUAL_ASS_INSTITUTIONNELLE);
			/** Associations locales */
			filters.put("Locales", EOAssociation.QUAL_ASS_LOCALE);
			/** Associations expirées */
			filters.put("Expirées", EOAssociation.QUAL_ASS_EXPIREE);
			/** Associations : Toutes */
			filters.put("Toutes", EOAssociation.QUAL_ASS_TOUTES);
			
		}
		return filters;
	}
	
	public String associationTreeViewDialogID() {
		String id = getComponentId() + "_associationTreeViewDialog";
		return id;
	}
	
	public WOActionResults closeWindow() {
		CktlAjaxWindow.close(context(), associationTreeViewDialogID());
		return null;
	}
	
	public boolean isSelectedRole() {
		return isSelectedRole;
	}

	public void setSelectedRole(boolean isSelectedRole) {
		this.isSelectedRole = isSelectedRole;
	}

	public EditRole getWocomponent() {
		return wocomponent;
	}
	
	public String associationSelectID() {
		return getComponentId() + "_associationSelect";
	}
	
}