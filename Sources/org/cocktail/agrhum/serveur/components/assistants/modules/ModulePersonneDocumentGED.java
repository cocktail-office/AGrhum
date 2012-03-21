package org.cocktail.agrhum.serveur.components.assistants.modules;

import com.webobjects.appserver.WOContext;

/**
 * Module permettant de gérer les documents avec la GED associée à un individu.
 * 
 * @author Pierre-Yves MARIE <pierre-yves.marie at cocktail.org>
 */
public class ModulePersonneDocumentGED extends ModuleAssistant implements IModuleAssistant {
	private static final long serialVersionUID = 7425878246377553461L;

	public ModulePersonneDocumentGED(WOContext context) {
        super(context);
    }
}