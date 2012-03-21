package org.cocktail.agrhum.serveur.components;

import org.cocktail.fwkcktlajaxwebext.serveur.component.CktlAjaxWindow;
import org.cocktail.fwkcktlpersonne.common.eospecificites.EOStructureForGroupeSpec;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSValidation;

/**
 * Page dédiée à la création d'un groupe, destiné à être affiché dans une 
 * fenêtre modale.
 * 
 * @author Alexis TUAL <alexis.tual at cocktail.org>
 *
 */
public class CreationGroupePage extends MyWOComponent {
    
    private static final long serialVersionUID = -2963417008265203001L;
    private EOStructure selectedStructureForCreation;
    private EOStructure parentStructure;
    private boolean resetCreation;
    private boolean isCreationMode;
    
    public CreationGroupePage(WOContext context) {
        super(context);
    }
    
    public WOActionResults onSelectStructureForCreation() {
        setResetCreation(true);
        setCreationMode(true);
        if (selectedStructureForCreation !=  null) {
            selectedStructureForCreation.registerSpecificite(EOStructureForGroupeSpec.sharedInstance());
            if (parentStructure != null)
                selectedStructureForCreation.initAvecParent(parentStructure);
        }
        return null;
    }
    
    public WOActionResults terminerCreation() {
        save();
        setResetCreation(true);
        CktlAjaxWindow.close(context());
        return null;
    }
    
    public WOActionResults annulerCreationAndClose() {
        CktlAjaxWindow.close(context());
        return null;
    }
    
    private void save() {
        try {
            edc().saveChanges();
        } catch (NSValidation.ValidationException e) {
            logger().info(e.getMessage(), e);
            session().addSimpleErrorMessage(e.getLocalizedMessage(), e);
            context().response().setStatus(500);
        } catch (Exception e) {
            logger().warn(e.getMessage(), e);
            context().response().setStatus(500);
            session().addSimpleErrorMessage(e.getLocalizedMessage(), e);
        }
    }
    
    public EOStructure getSelectedStructureForCreation() {
        return selectedStructureForCreation;
    }
    
    public void setSelectedStructureForCreation(EOStructure selectedStructureForCreation) {
        this.selectedStructureForCreation = selectedStructureForCreation;
    }
    
    public void setParentStructure(EOStructure parentStructure) {
        this.parentStructure = parentStructure;
    }
    
    public boolean isResetCreation() {
        return resetCreation;
    }

    public void setResetCreation(boolean resetCreation) {
        this.resetCreation = resetCreation;
        if (resetCreation)
            setCreationMode(false);
    }
    
    public void setCreationMode(boolean isCreationMode) {
        this.isCreationMode = isCreationMode;
    }
    
    public boolean isCreationMode() {
        return isCreationMode;
    }
    
}