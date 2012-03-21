package org.cocktail.agrhum.serveur.components.assistants.modules;

import org.apache.log4j.Logger;
import org.cocktail.fwkcktlgrh.common.metier.EOContratHeberge;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSValidation;

import er.extensions.appserver.ERXWOContext;
import er.extensions.eof.ERXEOControlUtilities;

public class ModuleContratsHeberge extends ModuleAssistant implements IModuleAssistant {

    private static final long serialVersionUID = 6946682175571516584L;
    private static final Logger LOG = Logger.getLogger(ModuleContratsHeberge.class);

//    private String message;
    private String containerId;
    private WODisplayGroup dgContrats;
    private boolean showAvertissement;
    
    public ModuleContratsHeberge(WOContext context) {
        super(context);
    }
    
    @SuppressWarnings("rawtypes")
    private boolean hasPendingChanges() {
		NSArray objs = ERXEOControlUtilities.updatedObjects(editingContext(), new NSArray<String>(EOContratHeberge.ENTITY_NAME), null);
        if (objs == null || objs.isEmpty())
            objs = ERXEOControlUtilities.deletedObjects(editingContext(), new NSArray<String>(EOContratHeberge.ENTITY_NAME), null);
        if (objs == null || objs.isEmpty())
            objs = ERXEOControlUtilities.insertedObjects(editingContext(), new NSArray<String>(EOContratHeberge.ENTITY_NAME), null);
        return objs != null && !objs.isEmpty();
    }
    
    public WOActionResults showMessage() {
        if (hasPendingChanges()) {
            String message = 
                    "Attention, le passage à l'étape suivante enregistrera les modifications de la personne dans le référentiel, " +
                    "afin de pouvoir renseigner ses affectations";
//            AjaxUtils.  appendScript(context(), "alert('" + message + "');");
            session().addSimpleInfoMessage(message, null);
        }
        return null;
    }
    
    public boolean saveChanges() {
        boolean success = false;
        try {
            editingContext().saveChanges();
            success = true;
        } catch (NSValidation.ValidationException e) {
            LOG.warn(e.getMessage(), e);
            session().addSimpleErrorMessage(e.getLocalizedMessage(), e);
        } catch (Exception e) {
            edc().revert();
            LOG.error(e.getMessage(), e);
            throw NSForwardException._runtimeExceptionForThrowable(e);
        }
        return success;
    }
    
    @Override
    public boolean isSuivantDisabled() {
        return getDgContrats() == null || getDgContrats().displayedObjects().count() == 0;
    }
    
    @Override
    public void onSuivant() {
        saveChanges();
    }

    public String getContainerId() {
        if (containerId == null)
            containerId = ERXWOContext.safeIdentifierName(context(), true);
        return containerId;
    }
    
    public WODisplayGroup getDgContrats() {
        return dgContrats;
    }
    
    public void setDgContrats(WODisplayGroup dgContrats) {
        this.dgContrats = dgContrats;
    }
    
    public boolean showAvertissement() {
        return showAvertissement;
    }
    
    public void setShowAvertissement(boolean showAvertissement) {
        this.showAvertissement = showAvertissement;
    }
    
}