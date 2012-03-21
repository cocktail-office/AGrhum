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
import org.cocktail.fwkcktlpersonne.common.eospecificites.EOStructureForGroupeSpec;
import org.cocktail.fwkcktlpersonne.common.metier.EORepartPersonneAdresse;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;
import org.cocktail.fwkcktlpersonneguiajax.serveur.controleurs.NotificationCtrl;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSValidation;

import er.extensions.eof.ERXEC;

/**
 * 
 * 
 * @author Alexis TUAL <alexis.tual at cocktail.org>
 *
 */
public class GestionGroupes extends MyWOComponent {
	private static final long serialVersionUID = 3727803841626364183L;

	private EOStructure structure;
	private EOStructure selectedStructureForMove;
	private NSDictionary<String, EOQualifier> filters;
	private boolean resetTabs;
	private boolean resetLeftTreeView;
	private boolean resetNewGroupe;
	private boolean resetMoveTreeView;
	private boolean resetDelTreeView;
	private static final String GROUPE_CONTAINER_ID = "groupeContainer";
	private EORepartPersonneAdresse selectedRepartPersonneAdresse;
	private EOEditingContext gestionGroupeEc = ERXEC.newEditingContext();
	
	public GestionGroupes(WOContext context) {
		super(context);
	}

	@Override
	public EOEditingContext edc() {
	    return gestionGroupeEc;
	}
	
	public WOActionResults doNuthin() {
		return null;
	}

	private void save() {
		try {
			edc().saveChanges();
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

	public boolean peutModifierGroupeSelectionne() {
	    return unGroupeEstSelectionne() && session().applicationUser().peutModifierGroupe(getStructure())
	    		&& !EOStructureForGroupeSpec.isGroupeReserve(getStructure());
	}
	
	public boolean peutSupprimerGroupeSelectionne() {
	    return unGroupeEstSelectionne() && session().applicationUser().peutSupprimerGroupe(getStructure())
	    		&& !EOStructureForGroupeSpec.isGroupeRacine(getStructure())
	    		&& !EOStructureForGroupeSpec.isGroupeArchive(getStructure())
	    		&& !EOStructureForGroupeSpec.isGroupeReserve(getStructure())
	    		&& !getStructure().isArchivee();
	}
	
	public boolean unGroupeEstSelectionne() {
	    return getStructure() != null;
	}
	
	public WOActionResults terminer() {
		save();
		return null;
	}

	public WOActionResults annuler() {
		return null;
	}

	public WOActionResults annulerDelete() {
		CktlAjaxWindow.close(context());
		return null;
	}
	
	public WOActionResults onClose() {
		prepareRefreshLeftTreeView();
		return null;
	}

	private void prepareRefreshLeftTreeView() {
		NSMutableDictionary<String, Object> userInfo = new NSMutableDictionary<String, Object>();
		userInfo.setObjectForKey(edc(), "edc");
		NotificationCtrl.postNotificationForOnAnnulerNotification(this, userInfo);
		setResetLeftTreeView(true);
	}
	
	public WOActionResults openAjoutGroupe() {
	    setResetNewGroupe(true);
	    newCreatedStructure = EOStructure.creerInstance(edc());
//	    setNewCreatedStructure(null);
        return null;
    }
	
	public WOActionResults openDelGroupe() {
	    setResetDelTreeView(true);
	    return null;
	}
	
	public WOActionResults openMoveGroupe() {
	    setResetMoveTreeView(true);
	    return null;
	}
	
	public WOActionResults deleteGroupe() {
	    setResetNewGroupe(true);
	    prepareRefreshLeftTreeView();
	    try {
			getStructure().archiver();
			save();
//			session().addSimpleInfoMessage(session().localizer().localizedStringForKey("confirmation"), null); // TODO : PYM ajouter un message feedback pour l'archivage eventuellement
		} catch (Exception e) {
			logger().info(e.getMessage(), e);
			session().addSimpleErrorMessage(e.getLocalizedMessage(), e);
			context().response().setStatus(500);
		}
		CktlAjaxWindow.close(context());
        return null;
    }
	    
	
	public WOActionResults closeAndSelectNewGroupe() {
        CktlAjaxWindow.close(context(), "AjoutGroupeModalW");
        if (getNewCreatedStructure()!=null) {
			setStructure(getNewCreatedStructure());
		}
        return null;
    }
	
	public WOActionResults onSelectGroupe() {
		setResetTabs(true);
		edc().revert();
		return null;
	}

	public WOActionResults onSelectGroupeForMove() {
	    // déplacer le groupe vers le groupe sélectionné
	    if (getSelectedStructureForMove() != null) {
	    	if (getStructure().isArchivee()) {
	    		getStructure().desarchiverVersPere(getSelectedStructureForMove());
			} else {
				getStructure().setToStructurePereRelationship(getSelectedStructureForMove());
			}	      
	        save();
	    }
	    CktlAjaxWindow.close(context(), "MoveGroupeModalW");
	    return null;
	}

	public EOStructure getStructure() {
		return structure;
	}

	public void setStructure(EOStructure structure) {
		this.structure = structure;
	}

	public NSDictionary<String, EOQualifier> filters() {
		if (filters == null) {
			filters = 
				new NSMutableDictionary<String, EOQualifier>();
			EOQualifier qual = EOStructureForGroupeSpec.QUAL_GROUPES_SERVICES;
			filters.put("Services", qual);
			qual = EOStructureForGroupeSpec.QUAL_GROUPES_LABORATOIRES;
			filters.put("Labos", qual);
			qual = EOStructureForGroupeSpec.QUAL_GROUPE_FORUMS;
			filters.put("Forums", qual);
			qual = EOStructureForGroupeSpec.QUAL_GROUPE_ENTREPRISES;
			filters.put("Entreprises", qual);
		}
		return filters;
	}

	public String groupeContainerId() {
		return GROUPE_CONTAINER_ID;
	}

	public EORepartPersonneAdresse getSelectedRepartPersonneAdresse() {
		return selectedRepartPersonneAdresse;
	}

	public void setSelectedRepartPersonneAdresse(
			EORepartPersonneAdresse selectedRepartPersonneAdresse) {
		this.selectedRepartPersonneAdresse = selectedRepartPersonneAdresse;
	}

	public boolean isResetTabs() {
		return resetTabs;
	}

	public void setResetTabs(boolean resetTabs) {
		this.resetTabs = resetTabs;
	}

	public boolean isResetLeftTreeView() {
		return resetLeftTreeView;
	}

	public void setResetLeftTreeView(boolean resetLeftTreeView) {
		this.resetLeftTreeView = resetLeftTreeView;
	}

	public boolean isResetNewGroupe() {
        return resetNewGroupe;
    }
	
	public void setResetNewGroupe(boolean resetNewGroupe) {
        this.resetNewGroupe = resetNewGroupe;
    }
	
	public boolean isResetMoveTreeView() {
        return resetMoveTreeView;
    }
	
	public void setResetMoveTreeView(boolean resetMoveTreeView) {
        this.resetMoveTreeView = resetMoveTreeView;
    }
	
	public EOStructure getSelectedStructureForMove() {
        return selectedStructureForMove;
    }
	
	public void setSelectedStructureForMove(EOStructure selectedStructureForMove) {
        this.selectedStructureForMove = selectedStructureForMove;
    }

	public String getDeleteSelectedGroupTitle() {
		String title = "Supprimer(archiver) le groupe";
		if (getStructure()!=null) {
			title = title + " "+ getStructure().llStructure();
		}
		return title;
	}

	/**
	 * @return the resetDelTreeView
	 */
	public boolean isResetDelTreeView() {
		return resetDelTreeView;
	}

	/**
	 * @param resetDelTreeView the resetDelTreeView to set
	 */
	public void setResetDelTreeView(boolean resetDelTreeView) {
		this.resetDelTreeView = resetDelTreeView;
	}

	private EOStructure newCreatedStructure;

	/**
	 * @return the newCreatedStructure
	 */
	public EOStructure getNewCreatedStructure() {
		return newCreatedStructure;
	}

	/**
	 * @param newCreatedStructure the newCreatedStructure to set
	 */
	public void setNewCreatedStructure(EOStructure newCreatedStructure) {
		// Modification en date du 10/01/12 car à l'origine pour "créer" une nouvelle instantce, on settait à null
		// et donc on créait une 'local instance' d'une instance null.
//		this.newCreatedStructure = newCreatedStructure.localInstanceIn(edc());
		this.newCreatedStructure = newCreatedStructure;
		
	}

}
