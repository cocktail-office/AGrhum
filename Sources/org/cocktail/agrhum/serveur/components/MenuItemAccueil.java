package org.cocktail.agrhum.serveur.components;

import org.cocktail.agrhum.serveur.Application;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

public class MenuItemAccueil extends WOComponent {

    private static final long serialVersionUID = -8592059009972055088L;
    private static final String BINDING_ACTION = "action";
    private static final String BINDING_DISABLED = "disabled";
    private static final String BINDING_MENU_KEY_NAME = "cleMenu";
    private static final String BINDING_MENU_CLASS_CSS = "cssClassMenu";


    public MenuItemAccueil(WOContext context) {
        super(context);
    }
    
    @Override
    public boolean synchronizesVariablesWithBindings() {
        return false;
    }
    
    public WOActionResults action() {
        return (WOActionResults)valueForBinding(BINDING_ACTION);
    }
    
    public String cssClass() {
        return disabled() ? "disabled" : cssClassMenu();
    }
    
    public boolean disabled() {
        return valueForBinding(BINDING_DISABLED) != null &&
               (Boolean)valueForBinding(BINDING_DISABLED);
    }
    
    public String menuKey() {
        return (String)valueForBinding(BINDING_MENU_KEY_NAME);
    }
    
    public boolean isHidden() {
        return ((Application)application()).isMenuItemHidden(menuKey());
    }
    
    public String cssClassMenu() {
		return  (valueForBinding(BINDING_MENU_CLASS_CSS) != null) ? (String)valueForBinding(BINDING_MENU_CLASS_CSS) : "";
	}
    
}