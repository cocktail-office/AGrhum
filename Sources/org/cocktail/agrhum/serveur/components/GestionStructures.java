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

import org.cocktail.agrhum.serveur.Session;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSForwardException;

import er.extensions.appserver.ERXRedirect;
import er.extensions.eof.ERXEC;

public class GestionStructures extends MyWOComponent {
	
	private static final long serialVersionUID = 7955102018433119695L;
	private EOEditingContext editingContext;
	private NSArray<String> modules;
	private NSArray<String> etapes;
	private IPersonne selectedPersonne;
	
	private String sectionStructure;
	
    public GestionStructures(WOContext context) {
        super(context);
    }
    
	public WOActionResults annuler() {
		Accueil nextPage = (Accueil)pageWithName(Accueil.class.getName());
		if (editingContext() != null) {
			editingContext().revert();
		}
		session().setIndexModuleActifGestionPersonne(null);
		return nextPage;
	}

	public WOActionResults terminer() {
		IPersonne personne = selectedPersonne();
		if (personne != null) {
			try {
				if (personne.editingContext().hasChanges()){
					personne.setPersIdModification(session().applicationUser().getPersonne().persId());
					personne.editingContext().saveChanges();
					session().addSimpleInfoMessage(localizer().localizedTemplateStringForKeyWithObject("pers.valid", 
                            personne), null);
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

	public String moduleName() {
		String moduleName = null;
		if (modules() != null && modules().count()>0) {
			moduleName = (String)modules().objectAtIndex(session().indexModuleActifGestionPersonne().intValue());
		}
		return moduleName;
	}

	/**
	 * @return the modules
	 */
	public NSArray<String> modules() {
		modules = new NSArray<String>(new String[]{
				"ModulePersonneSearch",
				"ModulePersonneAdmin",
//				"ModulePersonneAdresse",
//				"ModulePersonneTelephone",
		"ModulePersonneGroupe",
		"ModulePersonneMembres",
		"ModulePersonneEffectif"});
		session().setModulesGestionPersonne(modules);
		return modules;
	}

	/**
	 * @param modules the modules to set
	 */
	public void setModules(NSArray<String> modules) {
		this.modules = modules;
		((Session)session()).setModulesGestionPersonne(modules);
	}

	public NSArray<String> etapes() {
		etapes = new NSArray<String>(new String[]{
				"Recherche",
				"Informations",
//				"Adresses",
//				"T&eacute;l&eacute;phones",
		"Groupes",
		"Membres",
		"Effectif"});
		return etapes;
	}

	/**
	 * @return the selectedPersonne
	 */
	public IPersonne selectedPersonne() {
		return selectedPersonne;
	}

	/**
	 * @param selectedPersonne the selectedPersonne to set
	 */
	public void setSelectedPersonne(IPersonne selectedPersonne) {
		this.selectedPersonne = selectedPersonne;
	}


	/**
	 * @return the editingContext
	 */
	public EOEditingContext editingContext() {
		if (editingContext == null) {
			if (selectedPersonne() != null) {
				editingContext = selectedPersonne().editingContext();
			} else {
				editingContext = ERXEC.newEditingContext();
			}
		}
		return editingContext;
	}

	/**
	 * @param editingContext the editingContext to set
	 */
	public void setEditingContext(EOEditingContext editingContext) {
		this.editingContext = editingContext;
	}
	
	/**
	 * @return 
	 */
	public String getSectionStructure() {
		return sectionStructure;
	}
	
	/**
	 * @param sectionStructure
	 */
	public void setSectionStructure(String sectionStructure) {
		this.sectionStructure = sectionStructure;
	}
    
}