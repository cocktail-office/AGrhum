package org.cocktail.agrhum.serveur.components;

import org.apache.log4j.Logger;
import org.cocktail.fwkcktlpersonne.common.eospecificites.EOStructureForFournisseurSpec;
import org.cocktail.fwkcktlpersonne.common.metier.EOAssociation;
import org.cocktail.fwkcktlpersonne.common.metier.EORepartAssociation;
import org.cocktail.fwkcktlpersonne.common.metier.EORepartStructure;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;
import org.cocktail.fwkcktlpersonneguiajax.serveur.FwkCktlPersonneGuiAjaxParamManager;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSForwardException;

public class EditFournisInterne extends MyWOComponent {
	private static final long serialVersionUID = 2717708417647369975L;
	
	private static final Logger LOG = Logger.getLogger(EditFournisInterne.class);

	private static final String EDITING_CONTEXT_BDG = "editingContext";
	private static final String STRUCTURE_BDG = "structure";
	private static final String WANT_RESET = "wantReset";

	public EditFournisInterne(WOContext context) {
        super(context);
    }
	
	@Override
    public boolean synchronizesVariablesWithBindings() {
    	return false;
    }
    
    @Override
    public void appendToResponse(WOResponse woresponse, WOContext wocontext) {
    	if (wantReset()) {
    		setWantReset(false);
    	}
    	super.appendToResponse(woresponse, wocontext);
    }
    
	public EOEditingContext editingContext() {
		return (EOEditingContext) valueForBinding(EDITING_CONTEXT_BDG);
	}
	
	private Boolean wantReset() {
		return hasBinding(WANT_RESET) && (Boolean)valueForBinding(WANT_RESET);
	}
	
	private void setWantReset(Boolean value) {
		setValueForBinding(value, WANT_RESET);
	}
	
	public void setStructureOB(EOStructure structureOB) {
		setValueForBinding(structureOB, STRUCTURE_BDG);
	}
	
	public EOStructure structureOB() {
		return (EOStructure)valueForBinding(STRUCTURE_BDG);
	}

	public boolean isFournisseurInterne() {
		return EOStructureForFournisseurSpec.isStructureFournisseurInterne(structureOB());
	}

	public WOActionResults declareFournisseurInterne() {
		EOStructure structureToAssociate = structureOB().localInstanceIn(editingContext());
		EORepartStructure repartStructureEtablissement = 
			EORepartStructure.creerInstanceSiNecessaire(editingContext(), structureToAssociate, 
					structureToAssociate.etablissement(), session().applicationUser().getPersId());
		
		EORepartAssociation repartAssociation = EORepartAssociation.creerInstance(editingContext());
		repartAssociation.setPersIdModification(session().applicationUser().getPersId());
		repartAssociation.setToAssociationRelationship(
				EOAssociation.fetchByKeyValue(editingContext(), EOAssociation.ASS_CODE_KEY, EOAssociation.ASS_CODE_FINANCIEREPIE));
		
		repartStructureEtablissement.addToToRepartAssociationsRelationship(repartAssociation);
		
		WOActionResults result = null;
		try {
			if (editingContext().hasChanges()) {
//				// TODO : Appel de la procedure stockée GRHUM.INS_FOURNIS_INTERNE pour rester compatible avec l'ancienne façon
//				// de déclarer un fournisseur interne.
//				CktlDataBus databus = new CktlDataBus(editingContext());
//				databus.executeProcedure("FwkPers_prcInsFournisInterne", new NSDictionary());
				
				// Sauvegarde du rôle PIE établissement après le passage de la procédure.
				editingContext().saveChanges();
				
//				result = pageWithName(Accueil.class.getName());
				session().addSimpleInfoMessage(localizer().localizedStringForKey("confirmation"), null);
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
	
	public Boolean allowEditNoInsee() {
//		if (structureOB() instanceof EOStructure){
//			return false;
//		}
//		return true;
		return false;
	}

	public Boolean showNoInsee() {
		return myApp().config().booleanForKey(FwkCktlPersonneGuiAjaxParamManager.PARAM_FOURNIS_FORM_SHOWINSEE);
	}
}