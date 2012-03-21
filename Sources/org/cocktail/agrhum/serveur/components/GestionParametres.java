package org.cocktail.agrhum.serveur.components;

import org.cocktail.fwkcktlajaxwebext.serveur.component.CktlAjaxWindow;
import org.cocktail.fwkcktlajaxwebext.serveur.component.tableview.column.CktlAjaxTableViewColumn;
import org.cocktail.fwkcktlpersonne.common.metier.EOGrhumParametres;
import org.cocktail.fwkcktlpersonne.common.metier.EOGrhumParametresType;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

import er.ajax.AjaxUpdateContainer;
import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXEOAccessUtilities;
import er.extensions.eof.ERXEOControlUtilities;
import er.extensions.eof.ERXSortOrdering;

public class GestionParametres extends MyWOComponent {

	private static final long serialVersionUID = 7955102018433119695L;
	private EOEditingContext editingContext;
	
	private EOGrhumParametres currentParameter;
	private EOGrhumParametres selectedParameter;
	
	private String dataToModify = new String();

	private static final String PARAMETERS_CONTAINER_ID = "parametresContainer";
	
	private String sectionParametres;
   
    private NSArray<EOGrhumParametres> allParametersMA;
	private boolean resetLeftTreeView;
	
	private EOGrhumParametresType currentType;
	private NSArray<EOGrhumParametresType> types;
	private EOGrhumParametres newCreatedParameter;
	
    private NSArray<CktlAjaxTableViewColumn> colonnes;

	public GestionParametres(WOContext context) {
		super(context);
	}

	
/*
 * *********************************** Beginning of Editing Contexte Area ********************************************
 */

	/**
	 * @return the editingContext
	 */
	public EOEditingContext editingContext() {
		if (editingContext == null) {
			editingContext = ERXEC.newEditingContext();
		}
		return editingContext;
	}
	
	@Override
	public void appendToResponse(WOResponse response, WOContext context) {
		if (getNewCreatedParameter() != null
				&& getSelectedParameter() != null
				&& ERXEOControlUtilities.eoEquals(newCreatedParameter,
						selectedParameter)) {
			setSelectedParameter(getNewCreatedParameter());
		}
		super.appendToResponse(response, context);
	}
	
	public EOGrhumParametres getCurrentParameter() {
		return currentParameter;
	}

	public void setCurrentParameter(EOGrhumParametres currentParameter) {
		this.currentParameter = currentParameter;
	}

	public EOGrhumParametres getSelectedParameter() {
		return selectedParameter;
	}

	public void setSelectedParameter(EOGrhumParametres selectedParameter) {
		AjaxUpdateContainer.updateContainerWithID(getParametersContainerId(), context());
		this.selectedParameter = selectedParameter;
	}

	public String getDataToModify() {
		return dataToModify;
	}


	public void setDataToModify(String dataToModify) {
		this.dataToModify = dataToModify;
	}
	

	public String getSectionParametres() {
        return sectionParametres;
    }
    
    public void setSectionParametres(String sectionParametres) {
        this.sectionParametres = sectionParametres;
    }
    
    public String getParamKeyConstante(){
    	return EOGrhumParametres.PARAM_KEY_KEY;
    }
    
    
    /**
	 * @return the parametersContainerId
	 */
	public String getParametersContainerId() {
		return PARAMETERS_CONTAINER_ID;
	}

	
	public String getDeleteSelectedParamTitle() {
		String title = "Supprimer le paramètre";
		if ( getSelectedParameter() != null ) {
			title = title + " "+ getSelectedParameter().paramKey();
		}
		return title;
	}
	
	public boolean peutAjouterParametre() {
		return session().applicationUser().hasDroitGrhumCreateur() && session().applicationUser().peutAdministrerGrhum();
	}
    
	public boolean peutSupprimerParamSelectionne() {
	    return getSelectedParameter() != null && session().applicationUser().peutAdministrerGrhum();
	}
	
	public EOGrhumParametres getNewCreatedParameter() {
		return newCreatedParameter;
	}


	public void setNewCreatedParameter(EOGrhumParametres newCreatedParameter) {
		this.newCreatedParameter = newCreatedParameter;
	}
	
	public EOGrhumParametresType getCurrentType() {
		return currentType;
	}

	public void setCurrentType(EOGrhumParametresType currentType) {
		this.currentType = currentType;
	}

	public NSArray<EOGrhumParametresType> getTypes() {
		return types;
	}

	public void setTypes(NSArray<EOGrhumParametresType> types) {
		this.types = types;
	}
    
    
	public WOActionResults deleteParametre() {
	    try {
			supprimerEnBase();
		} catch (Exception e) {
			logger().info(e.getMessage(), e);
			session().addSimpleErrorMessage(e.getLocalizedMessage(), e);
			context().response().setStatus(500);
		}
		prepareRefreshLeftTreeView();
        return null;
    }
	
	public WOActionResults openAjoutParametre() {
		editingContext().revert();
		newCreatedParameter = EOGrhumParametres.createEOGrhumParametres(editingContext());
        return null;
    }

	

	public WOActionResults annuler() {
		GestionParametres nextPage = (GestionParametres) pageWithName(GestionParametres.class.getName());
		if (editingContext() != null) {
			editingContext().revert();
		}
		return nextPage;
	}
	
	public WOActionResults closeAndSelectNewParam() {
		setSelectedParameter(getNewCreatedParameter());
		ERXEOControlUtilities.eoEquals(newCreatedParameter, selectedParameter);
        CktlAjaxWindow.close(context(), "AjoutParamModalW");
        return null;
    }
	
	public WOActionResults onClose() {
		prepareRefreshLeftTreeView();
		return null;
	}
	
	public WOActionResults selectionnerParametres() {
		editingContext().revert();
		setResetParamEditor(true);
		return null;
	}

	public WOActionResults sauvegardeEnBase() {
		
		String message;
		
		// Retour en mode affichage et non plus directement en édition pour les paramètres suivants
//		setEditingMode(false);
		
		// Récupération de l'editingContext de la session
		EOEditingContext edc = editingContext();
		
		// Réalisation d'un try-catch pour éviter les soucis lors de l'écriture en base
		/*
		 * Avec la ligne ci-dessous, nous accédons aux propriétés du modèle.
		 * Nous envisageons de pouvoir désactiver temporairement et à notre guise
		 * le "Read Only" du modèle
		 */
		
		EOEntity entityParameter = ERXEOAccessUtilities.entityForEo(selectedParameter);
		try {
			// Avant de sauvegarder les données, nous modifions le modèle
			// pour que l'on puisse avoir accès aussi en écriture sur les données
			entityParameter.setReadOnly(false);
			edc.saveChanges();
		}
		catch (EOGeneralAdaptorException e)
		{
			// Logger l'exception + Annuler les changements et les caches
			edc.invalidateAllObjects();
			// Affichage de l'exception
			message = "Une erreur est survenue lors de l'enregistrement en Base";
			System.out.println(message);
		}
		catch (ValidationException e)
		{
			// Récupération du message d'erreur de validation pour affichage
			message = e.getMessage();
			System.out.println(message);
		} finally {
			entityParameter.setReadOnly(true);
		}
		
		return null;
	}
	
public WOActionResults supprimerEnBase() {
		
		String message;
		
		// Retour en mode affichage et non plus directement en édition pour les paramètres suivants
//		setEditingMode(false);
		
		// Récupération de l'editingContext de la session
		EOEditingContext edc = editingContext();
		
		// Réalisation d'un try-catch pour éviter les soucis lors de l'écriture en base
		/*
		 * Avec la ligne ci-dessous, nous accédons aux propriétés du modèle.
		 * Nous envisageons de pouvoir désactiver temporairement et à notre guise
		 * le "Read Only" du modèle
		 */
		EOEntity entityParameter = ERXEOAccessUtilities.entityForEo(selectedParameter);
		try {
			// Avant de sauvegarder les données, nous modifions le modèle
			// pour que l'on puisse avoir accès aussi en écriture sur les données
			entityParameter.setReadOnly(false);
			edc.deleteObject(selectedParameter);
			edc.saveChanges();
			mySession().addSimpleInfoMessage("AGRHUM", "Le paramètre a été supprimé avec succès.");
			setSelectedParameter(null);
		}
		catch (EOGeneralAdaptorException e)
		{
			// Logger l'exception + Annuler les changements et les caches
			edc.invalidateAllObjects();
			// Affichage de l'exception
			message = "Une erreur est survenue lors de l'enregistrement en Base";
			System.out.println(message);
		}
		catch (ValidationException e)
		{
			// Récupération du message d'erreur de validation pour affichage
			message = e.getMessage();
			System.out.println(message);
		} finally {
			entityParameter.setReadOnly(true);
		}
		
		return null;
	}
	

	public WOActionResults onSelectParametersMA() {
		edc().revert();
		return null;
	}

	public boolean isResetLeftTreeView() {
		return resetLeftTreeView;
	}

	public void setResetLeftTreeView(boolean resetLeftTreeView) {
		this.resetLeftTreeView = resetLeftTreeView;
	}
	
	private void prepareRefreshLeftTreeView() {
		setResetLeftTreeView(true);
		allParametersMA = null;
		if (getNewCreatedParameter() != null) {
			for (EOGrhumParametres param : getAllParametersMA()){
				if (ERXEOControlUtilities.eoEquals(getNewCreatedParameter(), param)){
					setSelectedParameter(param);
					break;
				}
			}
			setNewCreatedParameter(null);
		}
		setSelectedParameter(null);
	}

	/**
	 * @return the allParametersMA
	 */
	public NSArray<EOGrhumParametres> getAllParametersMA() {
		if (null == allParametersMA) {
			NSArray sortOrderings = new NSArray(ERXSortOrdering.sortOrderingWithKey(EOGrhumParametres.PARAM_KEY_KEY, EOSortOrdering.CompareAscending));
			allParametersMA = EOGrhumParametres.fetchAll(editingContext(), sortOrderings);
		}
		return allParametersMA;
	}

	/**
	 * @param allParametersMA the allParametersMA to set
	 */
	public void setAllParametersMA(NSArray<EOGrhumParametres> allParametersMA) {
		this.allParametersMA = allParametersMA;
	}
	
	public NSDictionary<String, String> getColonnes() {
		return new NSDictionary<String, String>("Paramètres",EOGrhumParametres.PARAM_KEY_KEY);
	}

	private boolean resetParamEditor;

	/**
	 * @return the resetParamEditor
	 */
	public boolean getResetParamEditor() {
		return resetParamEditor;
	}


	public void setResetParamEditor(boolean resetParamEditor) {
		this.resetParamEditor = resetParamEditor;
	}
	
	
}
	
