package org.cocktail.agrhum.serveur.components;

import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.foundation.NSValidation;

import er.extensions.appserver.ERXRedirect;

public class EditStructurePage extends MyWOComponent {
	private static final long serialVersionUID = 9215416092233119361L;
	private EOStructure structure;
	private boolean resetTabs;

	public EditStructurePage(WOContext context) {
        super(context);
    }

	public boolean isResetTabs() {
		return resetTabs;
	}

	public void setResetTabs(boolean resetTabs) {
		this.resetTabs = resetTabs;
	}
	
	public EOStructure getStructure() {
		return structure;
	}

	public void setStructure(EOStructure structure) {
		this.structure = structure;
	}
	
	public WOActionResults annuler() {
		ERXRedirect redirect = (ERXRedirect)pageWithName(ERXRedirect.class.getName());
        WOComponent accueil = pageWithName(Accueil.class.getName());
        redirect.setComponent(accueil);
        return redirect;
	}

	public WOActionResults terminer() {
		save();
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

	public String getEditStructureContainerId() {
		return getComponentId()+"_EditStructureContainer";
	}
	
}