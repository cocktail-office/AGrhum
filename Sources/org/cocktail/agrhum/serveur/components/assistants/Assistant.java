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

import org.apache.log4j.Logger;
import org.cocktail.agrhum.serveur.components.MyWOComponent;
import org.cocktail.agrhum.serveur.components.assistants.modules.IModuleAssistant;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSForwardException;

public abstract class Assistant extends MyWOComponent {
	
	private static final long serialVersionUID = 8857512663168160595L;
	private static final Logger LOG = Logger.getLogger(Assistant.class);
	public static final String MODULES_BDG = "modules";
	public static final String ETAPES_BDG = "etapes";
	public static final String ACTIVE_MODULE_IDX_BDG = "indexModuleActif";
	public static final String ACTION_TERMINER_BDG = "terminer";
	public static final String LIBELLE_TERMINER_BDG = "libelleTerminer";
	public static final String ACTION_ANNULER_BDG = "annuler";
	public static final String LIBELLE_ANNULER_BDG = "libelleAnnuler";
	
	public static final String EDITING_CONTEXT_BDG = "editingContext";
	public static final String UTILISATEUR_PERS_ID_BDG = "utilisateurPersId";
	
	
	public static final String ASSISTANT_EDIT_MODE_VALUE = "editMode";
	
	
	private NSArray<String> modules;
	private EOEditingContext editingContext;
	private Integer utilisateurPersId;
	
	private Integer indexModuleActif;
	private String libelleAnnuler;
	private String libelleTerminer;
	
	private IModuleAssistant module;
	
	public NSArray<String> etapes;
	public String uneEtape;

	private String failureMessage;
	
	private boolean isEditMode;
	private boolean wantRefreshEtapeModule;
	
	public Assistant(WOContext context) {
		super(context);
	}

	public void reset() {
		modules = null;
		editingContext = null;
		utilisateurPersId = null;
		indexModuleActif = null;
		module = null;
		failureMessage = null;
		isEditMode=false;
		setWantRefreshEtapeModule(true);
	}
	
	public String moduleName() {
		String moduleName = null;
		if (modules() != null && modules().count()>0) {
			moduleName = (String)modules().objectAtIndex(indexModuleActif().intValue());
		}
		return moduleName;
	}


	/**
	 * @return the modules
	 */
	@SuppressWarnings("unchecked")
	public NSArray<String> modules() {
		modules = (NSArray<String>)valueForBinding(MODULES_BDG);
		return modules;
	}
	/**
	 * @param modules the modules to set
	 */
	public void setModules(NSArray<String> modules) {
		this.modules = modules;
	}

	public Integer indexModuleActif() {
		indexModuleActif = (Integer)valueForBinding(ACTIVE_MODULE_IDX_BDG);
		return indexModuleActif;
	}
	/**
	 * @param indexModuleActif the indexModuleActif to set
	 */
	public void setIndexModuleActif(Integer indexModuleActif) {
		this.indexModuleActif = indexModuleActif;
		setValueForBinding(indexModuleActif, ACTIVE_MODULE_IDX_BDG);
		if (indexModuleActif()!=null && indexModuleActif()==0) {
			reset();
		}
	}

	/**
	 * @return the etapes
	 */
	@SuppressWarnings("unchecked")
	public NSArray<String> etapes() {
		etapes = (NSArray<String>)valueForBinding(ETAPES_BDG);
		return etapes;
	}

	/**
	 * @param etapes the etapes to set
	 */
	public void setEtapes(NSArray<String> etapes) {
		this.etapes = etapes;
	}

	public String styleForEtape() {
		String styleForEtape = null;
		if (etapes().indexOf(uneEtape) == indexModuleActif().intValue()) {
			styleForEtape = "etape selected";
		} else if (etapes().indexOf(uneEtape) < indexModuleActif().intValue()) {
			styleForEtape = "etape passed";
		} else {
			styleForEtape = "etape";
		}
		return styleForEtape;
	}

	public boolean isEtapeActive() {
		return etapes().indexOf(uneEtape) == indexModuleActif().intValue();
	}

	public WOActionResults annuler() {
		setIndexModuleActif(null);
		return performParentAction(ACTION_ANNULER_BDG);
	}


	public WOActionResults terminer() {
		WOActionResults result = null;
		try {
			if (module.valider()) {
				result = performParentAction(ACTION_TERMINER_BDG);
//				setIndexModuleActif(null);
				// Rajout du finally pour éviter des gros bugs après enregistrement dans les menus (notamment contact) et ouverture d'un autre menu
				session().reset();
			} else {
				context().response().setStatus(500);
			}
		} catch (NSForwardException e) {
		    LOG.error(e.getMessage(), e);
			context().response().setStatus(500);
			if (e.originalException() != null)
			    session().addSimpleErrorMessage(e.originalException().getMessage(), e.originalException());
		} catch (Exception e1) {
		    LOG.error(e1.getMessage(), e1);
			context().response().setStatus(500);
            session().addSimpleErrorMessage(e1.getMessage(), e1);
		}
		return result;
	}


	public boolean isPrecedentDisabled() {
		return indexModuleActif().intValue()<=0 || module().isPrecedentDisabled();
	}
	public WOActionResults precedent() {
		try {
			module().onPrecedent();
			int newIndex = indexModuleActif().intValue()-1;
			if (newIndex>=0) {
				setIndexModuleActif(Integer.valueOf(newIndex));
			}
		} catch (Exception e) {
			context().response().setStatus(500); 
		}
		return null;
	}

	public boolean isSuivantDisabled() {
		return indexModuleActif().intValue()>=modules().count()-1 || module().isSuivantDisabled();
	}

	public boolean isTerminerDisabled() {
		boolean isTerminerDisabled = module().isTerminerDisabled();
		
		return isTerminerDisabled;
	}

	public WOActionResults suivant() {
		try {
			module().onSuivant();
			if (module().valider()) {
				int newIndex = indexModuleActif().intValue()+1;
				if (newIndex<=modules().count()-1) {
					setIndexModuleActif(Integer.valueOf(newIndex));
				}
			} else {
				context().response().setStatus(500); 
			}
		} catch (Exception e) {
			context().response().setStatus(500); 
		}
		return null;
	}




	public WOComponent assistant() {
		return this;
	}


	/**
	 * @return the module
	 */
	public IModuleAssistant module() {
		return module;
	}


	/**
	 * @param module the module to set
	 */
	public void setModule(IModuleAssistant module) {
		this.module = module;
	}


	public String onFailure() {
		String onFailure = null;
		if (failureMessage() != null) {
			onFailure = "function (){ErreurContainerUpdate();}";
		}
		return onFailure;
	}

	/**
	 * @return the failureMessage
	 */
	public String failureMessage() {
		return failureMessage;
	}

	/**
	 * @return the libelleAnnuler
	 */
	public String libelleAnnuler() {
		if (hasBinding(LIBELLE_ANNULER_BDG)) {
			libelleAnnuler = (String)valueForBinding(LIBELLE_ANNULER_BDG);
		} else {
			libelleAnnuler = "Annuler";
		}
		return libelleAnnuler;
	}

	/**
	 * @param libelleAnnuler the libelleAnnuler to set
	 */
	public void setLibelleAnnuler(String libelleAnnuler) {
		this.libelleAnnuler = libelleAnnuler;
	}

	/**
	 * @return the libelleTerminer
	 */
	public String libelleTerminer() {
		if (hasBinding(LIBELLE_TERMINER_BDG)) {
			libelleTerminer = (String)valueForBinding(LIBELLE_TERMINER_BDG);
		} else {
			libelleTerminer = "Terminer";
		}
		return libelleTerminer;
	}

	/**
	 * @param libelleTerminer the libelleTerminer to set
	 */
	public void setLibelleTerminer(String libelleTerminer) {
		this.libelleTerminer = libelleTerminer;
	}

	/**
	 * @return the editingContext
	 */
	public EOEditingContext editingContext() {
		return (EOEditingContext)valueForBinding(EDITING_CONTEXT_BDG);
	}

	/**
	 * @param editingContext the editingContext to set
	 */
	public void setEditingContext(EOEditingContext editingContext) {
		this.editingContext = editingContext;
	}

	/**
	 * @return the utilisateurPersId
	 */
	public Integer utilisateurPersId() {
		if (hasBinding(UTILISATEUR_PERS_ID_BDG)) {
			utilisateurPersId = (Integer)valueForBinding(UTILISATEUR_PERS_ID_BDG);
		}
		return utilisateurPersId;
	}

	/**
	 * @param utilisateurPersId the utilisateurPersId to set
	 */
	public void setUtilisateurPersId(Integer utilisateurPersId) {
		this.utilisateurPersId = utilisateurPersId;
		if (hasBinding(UTILISATEUR_PERS_ID_BDG)) {
			setValueForBinding(utilisateurPersId, UTILISATEUR_PERS_ID_BDG);
		}
	}

	/**
	 * @return the isEditMode
	 */
	public boolean isEditMode() {
		return isEditMode;
	}

	/**
	 * @param isEditMode the isEditMode to set
	 */
	public void setEditMode(boolean isEditMode) {
		this.isEditMode = isEditMode;
	}
	
	public WOActionResults selectEtape() {
		WOActionResults result = null;
		try {
			if (module.valider()) {
				int newIndex = etapes().indexOf(uneEtape); // TODO : stabiliser en vérifiant que uneEtape n'est pas nulle....
				if (newIndex<=modules().count()-1) {
					setIndexModuleActif(Integer.valueOf(newIndex));
				}
//				if (indexModuleActif()==0) {
////					setIndexModuleActif(null);
//					reset();
//				}
//				setWantRefreshEtapeModule(true);
			} else {
				context().response().setStatus(500);
			}
		} catch (NSForwardException e) {
		    LOG.error(e.getMessage(), e);
			context().response().setStatus(500);
			if (e.originalException() != null)
			    session().addSimpleErrorMessage(e.originalException().getMessage(), e.originalException());
		} catch (Exception e1) {
		    LOG.error(e1.getMessage(), e1);
			context().response().setStatus(500);
            session().addSimpleErrorMessage(e1.getMessage(), e1);
		}
		return result;
	}

	/**
	 * @return the wantRefreshEtapeModule
	 */
	public boolean wantRefreshEtapeModule() {
		return wantRefreshEtapeModule;
	}

	/**
	 * @param wantRefreshEtapeModule the wantRefreshEtapeModule to set
	 */
	public void setWantRefreshEtapeModule(boolean wantRefreshEtapeModule) {
		this.wantRefreshEtapeModule = wantRefreshEtapeModule;
	}

}
