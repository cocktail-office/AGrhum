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

import org.cocktail.agrhum.serveur.components.MyWOComponent;
import org.cocktail.agrhum.serveur.components.assistants.Assistant;
import org.cocktail.agrhum.serveur.components.controlers.ModuleCtrl;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;

public class ModuleAssistant extends MyWOComponent implements IModuleAssistant {

	public static final String BDG_editingContext = "editingContext";
	public static final String BDG_utilisateurPersId = "utilisateurPersId";
	public static final String BDG_personne = "personne";
	public static final String MODULE_BDG = "module";
	public static String STYLE_BDG = "styleCss";
	public static String CLASS_BDG = "classeCss";
	public static String FORM_ID_BDG = "formID";

	private ModuleCtrl ctrl = null;
	
	private String styleCss;
	private String classeCss;
	
	private IPersonne personne;
	
	private IModuleAssistant module;
	private EOEditingContext editingContext;
	private Integer utilisateurPersId;
	
	private Assistant assistant;

	public String updateContainerId;
	public String updateContainerIdOnSelectionnerPersonneInTableview;
	
	public String formID;
	
	public ModuleAssistant(WOContext context) {
		super(context);
	}

	public ModuleAssistant(WOContext context, ModuleCtrl controler) {
		super(context);
		ctrl = controler;
	}

	@Override
	public void appendToResponse(WOResponse response, WOContext context) {
		//if (module() == null) {
			setModule(this);
		//}
		super.appendToResponse(response, context);
	}

	/**
	 * @return the ctrl
	 */
	public ModuleCtrl ctrl() {
		return ctrl;
	}

	/**
	 * @param ctrl the ctrl to set
	 */
	public void setCtrl(ModuleCtrl ctrl) {
		this.ctrl = ctrl;
	}

	/**
	 * @return the personne
	 */
	public IPersonne personne() {
		return (IPersonne)valueForBinding(BDG_personne);
	}

	/**
	 * @param personne the personne to set
	 */
	public void setPersonne(IPersonne personne) {
		this.personne = personne;
		setValueForBinding(personne, BDG_personne);
		//if (personne.persIdModification() == null ){
		//	personne.setPersIdModification(this.utilisateurPersId);
		//}
	}

	public WOComponent submit() {		
		return null;
	}

	/**
	 * @return the styleCss
	 */
	public String styleCss() {
		return (String)valueForBinding(STYLE_BDG);
	}

	/**
	 * @param styleCss the styleCss to set
	 */
	public void setStyleCss(String styleCss) {
		this.styleCss = styleCss;
	}

	/**
	 * @return the classeCss
	 */
	public String classeCss() {
		return (String)valueForBinding(CLASS_BDG);
	}

	/**
	 * @param classeCss the classeCss to set
	 */
	public void setClasseCss(String classeCss) {
		this.classeCss = classeCss;
	}

	/**
	 * @return the assistant
	 */
	public Assistant assistant() {
		return assistant;
	}

	/**
	 * @param assistant the assistant to set
	 */
	public void setAssistant(Assistant assistant) {
		this.assistant = assistant;
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
		setValueForBinding(module, MODULE_BDG);
	}
	/**
	 * @return the editingContext
	 */
	public EOEditingContext editingContext() {
		return (EOEditingContext)valueForBinding(BDG_editingContext);
	}

	/**
	 * @param editingContext the editingContext to set
	 */
	public void setEditingContext(EOEditingContext editingContext) {
		this.editingContext = editingContext;
	}

	public boolean isPrecedentDisabled() {
		return false;
	}
	public void onPrecedent() {
	}

	public boolean isSuivantDisabled() {
		return false;
	}
	public void onSuivant() {
	}

	public boolean isTerminerDisabled() {
		return true;
	}
	public boolean valider() {
		return true;
	}


	/**
	 * @return the utilisateurPersId
	 */
	public Integer utilisateurPersId() {
		return (Integer)valueForBinding(BDG_utilisateurPersId);
	}

	/**
	 * @param utilisateurPersId the utilisateurPersId to set
	 */
	public void setUtilisateurPersId(Integer utilisateurPersId) {
		this.utilisateurPersId = utilisateurPersId;
	}

}
