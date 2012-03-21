package org.cocktail.agrhum.serveur.components;

import org.apache.log4j.Logger;
import org.cocktail.agrhum.serveur.Session;
import org.cocktail.fwkcktlajaxwebext.serveur.component.CktlAjaxWindow;
import org.cocktail.fwkcktlpersonne.common.metier.EOGrhumParametres;
import org.cocktail.fwkcktlpersonne.common.metier.EOGrhumParametresType;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.eof.ERXEOAccessUtilities;
import er.extensions.eof.ERXSortOrdering;

public class EditParametres extends MyWOComponent {
	private static final long serialVersionUID = 2717708417647369975L;

	private static final Logger LOG = Logger
			.getLogger(EditParametres.class);

	private int paramIndex;
	
	private static final String EDITING_CONTEXT_BDG = "editingContext";
	private static final String PARAMETRE_BDG = "parametre";
	private static final String WANT_RESET = "wantReset";

//	private static final String PERS_KEY = "unePersonne.";
//	private static final String STRUCTURE_KEY = "uneStructure.";
	
	public static final String COL_PERSID_KEY = IPersonne.PERSID_KEY;
	public static final String COL_NOM_KEY = IPersonne.NOM_PRENOM_AFFICHAGE_KEY;
	/** Tableau contenant les clés identifiant les colonnes à afficher par défaut. */
	public static NSArray DEFAULT_COLONNES_KEYS_PERSID = new NSArray(new Object[] {
			COL_PERSID_KEY, COL_NOM_KEY
	});
	
	public static final String COL_CSTRUCTURE_KEY = EOStructure.C_STRUCTURE_KEY;
	public static final String COL_LIBELLE_KEY = EOStructure.LL_STRUCTURE_KEY;
	/** Tableau contenant les clés identifiant les colonnes à afficher par défaut pour les CStructres. */
	public static NSArray DEFAULT_COLONNES_KEYS_STRUCTURE = new NSArray(new Object[] {
			COL_CSTRUCTURE_KEY, COL_LIBELLE_KEY
	});
	
	public static final String BINDING_colonnesKeys = "colonnesKeys";
	public static final String BINDING_showDetailInModalBoxId = "showDetailInModalBoxId";
	
	
	private static final String GROUPE_CONTAINER_ID = "groupeContainer";
	
	private boolean isEditingMode = false;
	private boolean isSelected = false;

	private boolean isDisplayedPersId = false;
	private boolean isDisplayedCStructure = false;
	
	private EOGrhumParametresType currentType;
	private NSArray<EOGrhumParametresType> types;
	
	private IPersonne currentPersonneSelected;
	private WODisplayGroup displayGroup;

	public EditParametres(WOContext context) {
		super(context);
	}

	@Override
	public boolean synchronizesVariablesWithBindings() {
		return false;
	}

	@Override
	public void appendToResponse(WOResponse woresponse, WOContext wocontext) {
		if (wantReset()) {
			annuler();
			setWantReset(false);
		}
		if (parametersMA()!=null) {
			initPersId_CStructureFlags();
		}
		super.appendToResponse(woresponse, wocontext);
	}

	public EOEditingContext editingContext() {
		return (EOEditingContext) valueForBinding(EDITING_CONTEXT_BDG);
	}

	private Boolean wantReset() {
		return booleanValueForBinding(WANT_RESET, false);
	}

	private void setWantReset(Boolean value) {
		setValueForBinding(value, WANT_RESET);
	}

	public void setParametersMA(EOGrhumParametres parametersMA) {
		setValueForBinding(parametersMA, PARAMETRE_BDG);
	}

	public EOGrhumParametres parametersMA() {
		return (EOGrhumParametres) valueForBinding(PARAMETRE_BDG);
	}

	public boolean isEditingMode() {
		return isEditingMode;
	}

	public void setEditingMode(boolean isEditingMode) {
		this.isEditingMode = isEditingMode;
	}
	
	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param isSelected the isSelected to set
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	
	public class DgDelegate {
		public void displayGroupDidChangeSelection(WODisplayGroup group) {
			setCurrentPersonneSelected((IPersonne) group.selectedObject());
		}
	}
	
	public WODisplayGroup displayGroup() {
		if (displayGroup == null) {
			displayGroup = new WODisplayGroup();
		}
		displayGroup.setDelegate(new DgDelegate());
		return displayGroup;
	}
	
	public String getPersIdSearchResultModalID() {
		return getComponentId() + "_" + "PersIdSrchResultModal";
	}
	
	public String getCStructureSearchResultModalID() {
		return getComponentId() + "_" + "CStructureSrchResultModal";
	}
	
	public String getPersIdSrchContainerId() {
		return getComponentId() + "_" + "PersIdSrchContainer";
	}
	
	public String getCStructureSrchContainerId() {
		return getComponentId() + "_" + "CStructureSrchContainer";
	}
	
	public IPersonne getCurrentPersonneSelected() {
		return currentPersonneSelected;
	}

	public void setCurrentPersonneSelected(IPersonne currentPersonneSelected) {
		this.currentPersonneSelected = currentPersonneSelected;
	}
	
	public String groupeContainerId() {
		return GROUPE_CONTAINER_ID;
	}


/* ************************************************************************************************************************** */	 
	 
	public boolean isDisplayedPersId() {
		return isDisplayedPersId;
	}

	public void setDisplayedPersId(boolean isDisplayedPersId) {
		this.isDisplayedPersId = isDisplayedPersId;
	}

	public boolean isDisplayedCStructure() {
		return isDisplayedCStructure;
	}

	public void setDisplayedCStructure(boolean isDisplayedCStructure) {
		this.isDisplayedCStructure = isDisplayedCStructure;
	}

	public WOActionResults annuler() {
		if (editingContext() != null) {
			editingContext().revert();
		}
		setEditingMode(false);
		return null;
	}
	

	public WOActionResults sauvegardeEnBase() {

		String message;

		// Récupération de l'editingContext de la session
		EOEditingContext edc = editingContext();

		// Réalisation d'un try-catch pour éviter les soucis lors de l'écriture
		// en base
		/**
		 * Avec la ligne ci-dessous, nous accédons aux propriétés du modèle.
		 * Nous envisageons de pouvoir désactiver temporairement et à notre
		 * guise le "Read Only" du modèle
		 */

		EOEntity entityParameter = ERXEOAccessUtilities
				.entityForEo(parametersMA());
		try {
			// Avant de sauvegarder les données, nous modifions le modèle
			// pour que l'on puisse avoir accès aussi en écriture sur les
			// données
			if ( parametersMA().toParametresType().typeIdInterne().equals("DATE") ) {
				parametersMA().setParamValue(DateCtrl.dateCompletion(parametersMA().paramValue()));
			}
			entityParameter.setReadOnly(false);
			parametersMA().setDModification(new NSTimestamp());
			edc.saveChanges();
			// Retour en mode affichage et non plus directement en édition pour les
			// paramètres suivants
			setEditingMode(false);
			setDisplayedCStructure(false);
			setDisplayedPersId(false);
		} catch (EOGeneralAdaptorException e) {
			// Logger l'exception + Annuler les changements et les caches
			edc.invalidateAllObjects();
			// Affichage de l'exception
			message = "Une erreur est survenue lors de l'enregistrement en Base";
			LOG.error(message, e);
			session().addSimpleErrorMessage("Erreur", message);
		} catch (ValidationException e) {
			// Récupération du message d'erreur de validation pour affichage
			message = e.getMessage();
			LOG.error(message, e);
			session().addSimpleErrorMessage("Erreur", message);
		} finally {
			entityParameter.setReadOnly(true);
		}

		return null;
	}

	public WOActionResults modificationParametres() {
		setEditingMode(true);
		initPersId_CStructureFlags();
		return null;
	}

	private void initPersId_CStructureFlags() {
		setDisplayedPersId(false);
		setDisplayedCStructure(false);
		try {
			if (parametersMA().toParametresType().typeIdInterne()
					.equals("C_STRUCTURE")) {
				setDisplayedCStructure(true);
			}
			if (parametersMA().toParametresType().typeIdInterne()
					.equals("PERS_ID")) {
				setDisplayedPersId(true);
			}
		} catch (NullPointerException e) {
			String message = "Un paramètre DOIT AVOIR un type ! Veuillez attribuer en base un type à ce paramètre.";
			String messageErreur = e.getMessage();
			LOG.error(messageErreur, e);
			session().addSimpleErrorMessage("Erreur",
					messageErreur + " " + message);
		}
	}


	
/* ******************************** Pour la liste déroulante ********************************* */
	public EOGrhumParametresType getCurrentType() {
		return currentType;
	}

	public void setCurrentType(EOGrhumParametresType currentType) {
		this.currentType = currentType;
	}

	public NSArray<EOGrhumParametresType> getTypes() {
		if (null == types) {
			NSArray sortOrderings = new NSArray(ERXSortOrdering.sortOrderingWithKey(EOGrhumParametresType.TYPE_LIBELLE_KEY, EOSortOrdering.CompareAscending));
			types = EOGrhumParametresType.fetchAll(editingContext(), sortOrderings);
		}
		return types;
	}

	public void setTypes(NSArray<EOGrhumParametresType> types) {
		this.types = types;
	}
	
	/* **************************************************************************************** */
	
	public WOActionResults onCloseResultModal() {
		setParametersMA(parametersMA());
		resetComponent();
        return null;
    }
	
	protected void resetComponent() {
		setCurrentPersonneSelected(null);
		displayGroup().setObjectArray(NSArray.EmptyArray);
	}

	
	public WOActionResults selectionnerPersonne() {
		IPersonne resultat = getCurrentPersonneSelected();
		// 1) recup personne selected
		//2) si type paremeter en cours de saisie = individus
		// alors setValueParam = personneSelected.persid
		//sinon setValueParam = personneSelected.toStructure.cStucture (ou numero)...
		if ( parametersMA().toParametresType().typeIdInterne().equals("PERS_ID") ){
			parametersMA().setParamValue(resultat.persId().toString());
		} else if ( parametersMA().toParametresType().typeIdInterne().equals("C_STRUCTURE") && resultat.isStructure() ) {
			parametersMA().setParamValue( ((EOStructure)resultat).getNumero());
		}
		setSelected(false);
		CktlAjaxWindow.close(context());
		return null;
	}
	
	public Session session() {
    	return (Session)super.session();
    }

	public String getEditParamContainerID() {
		return "EditParamContainer_"+uniqueDomId();
	}

	public void validationRecherche(){
		setSelected(true);
	}

	public String getBoutonSelectionContainerID() {
		if ( currentPersonneSelected != null ){
			setSelected(true);
		}
		return "BoutonSelectionContainer_"+uniqueDomId();
	}

	/**
	 * @return the paramIndex
	 */
	public int getParamIndex() {
		return paramIndex;
	}

	/**
	 * @param compteEmailIndex the roleIndex to set
	 */
	public void setParamIndex(int paramIndex) {
		this.paramIndex = paramIndex;
	}
	
	public String getParamInPlaceID() {
		return getComponentId()+"_ParamInPlaceID_"+getParamIndex();
	}
	

	public String getInPlaceManualEditFunctionName() {
		return getParamInPlaceID()+"Edit()";
	}

	public String getRoleInPlaceFieldID() {
		return "FParamInPlace_"+getParamInPlaceID();
	}

	public String getInPlaceManualCancelEditFunctionName() {
		return getParamInPlaceID()+"Cancel()";
	}

	public String getInPlaceManualValidEditFunctionName() {
		return getParamInPlaceID()+"Save()";
	}

}