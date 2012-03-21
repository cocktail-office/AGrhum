package org.cocktail.agrhum.serveur.components;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.foundation.NSValidation;

import er.extensions.appserver.ERXRedirect;

import org.cocktail.fwkcktlpersonne.common.metier.EOIndividu;

public class EditIndividuPage extends MyWOComponent {
	private static final long serialVersionUID = 2751064448292949068L;

	public EditIndividuPage(WOContext context) {
        super(context);
    }

	private EOIndividu individu;

	/**
	 * @return the individu
	 */
	public EOIndividu getIndividu() {
		return individu;
	}

	/**
	 * @param individu the individu to set
	 */
	public void setIndividu(EOIndividu individu) {
		this.individu = individu.localInstanceIn(edc());
	}
	
	public boolean isIndividuSelected() {
		return getIndividu()!=null;
	}
	
	public WOActionResults annuler() {
		ERXRedirect redirect = (ERXRedirect)pageWithName(ERXRedirect.class.getName());
        WOComponent accueil = pageWithName(Accueil.class.getName());
        redirect.setComponent(accueil);
        return redirect;
	}

	public WOActionResults valider() {
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

	public String getIndividuContainerId() {
		return getComponentId()+"_individuContainer";
	}
}