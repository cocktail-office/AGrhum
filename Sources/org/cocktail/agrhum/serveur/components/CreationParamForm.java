package org.cocktail.agrhum.serveur.components;

import org.apache.log4j.Logger;
import org.cocktail.fwkcktlajaxwebext.serveur.component.CktlAjaxWindow;
import org.cocktail.fwkcktldroitsutils.common.util.MyDateCtrl;
import org.cocktail.fwkcktlpersonne.common.metier.EOGrhumParametres;
import org.cocktail.fwkcktlpersonne.common.metier.EOGrhumParametresType;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSValidation;

import er.extensions.eof.ERXEOAccessUtilities;
import er.extensions.eof.ERXSortOrdering;
 
/**
 * 
 * Composant de création simple d'un rôle ou d'une fonction.
 * 
 * Ce composant doit être englobé dans un formulaire. Ce composant gère son propre ec.
 * 
 * @binding parent le EOAssociation parent vers lequel sera créé une relation pour identifier le parent
 * dans EOAssociationReseau.
 * @binding onCreationSuccess callback appelé lorsque l'enregistrement a été fait avec succès
 * @binding wantReset flag pour indiquer à ce composant de reset son état interne
 * 
 * @author Alain MALAPLATE <alain.malaplate at cocktail.org>
 *
 */

public class CreationParamForm extends MyWOComponent {
	
	private static final long serialVersionUID = 347883843978759945L;
    private static final Logger LOG = Logger.getLogger(CreationParamForm.class);
	
	public static final String BINDING_ON_CREATION_SUCCESS = "onCreationSuccess";
    public static final String BINDING_NEW_PARAM = "newParametre";
	
	
	private EOGrhumParametresType currentType;
	private EOGrhumParametresType selectedType;
	private NSArray<EOGrhumParametresType> types;
	
    public CreationParamForm(WOContext context) {
        super(context);
    }
    
    @Override
    public void appendToResponse(WOResponse response, WOContext context) {
        if (getSelectedType() == null) {
        	setSelectedType(getTypes().objectAtIndex(0));
        }
        super.appendToResponse(response, context);
    }
    
    @Override
    public boolean synchronizesVariablesWithBindings() {
        return false;
    }
    
    /**
	 * @return the editingContext
	 */
	public EOEditingContext editingContext() {
		return getNewParametre().editingContext();
	}
	
	public EOGrhumParametres getNewParametre() {
		return (EOGrhumParametres)valueForBinding(BINDING_NEW_PARAM);
	}

	/* ******************************** Pour les listes déroulantes ********************************* */
	
	public EOGrhumParametresType getCurrentType() {
		return currentType;
	}

	public void setCurrentType(EOGrhumParametresType currentType) {
		this.currentType = currentType;
	}
	
	public EOGrhumParametresType getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(EOGrhumParametresType selectedType) {
		this.selectedType = selectedType;
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
	
	 public WOActionResults ajouterParametre() {
	        try {
	        	getNewParametre().setToParametresTypeRelationship(getSelectedType());
	        	getNewParametre().setDCreation(new NSTimestamp());
	        	getNewParametre().setDModification(new NSTimestamp());
	        	getNewParametre().setPersIdCreation(session().applicationUser().getPersId());
	        	getNewParametre().setPersIdModification(session().applicationUser().getPersId());
	        	valeurParDefaut();
	            // Vérifier le nom
	            if ( (getNewParametre() == null) || (getNewParametre().paramKey().length() == 0) 
	            		|| (getNewParametre().paramValue().length() == 0) || (getNewParametre().paramCommentaires().length() == 0) ){
	            	throw new ValidationException("Les champs sont obligatoires donc pas de vide");
	            }
	            
	            sauvegardeEnBase();
	            session().addSimpleInfoMessage("Nouveau paramètre " + getNewParametre().paramKey() + " créé avec succès", 
	                    "Le paramètre a été enregistré dans la table des paramètres du référentiel");
	            
	            if (hasBinding(BINDING_ON_CREATION_SUCCESS)) {
	                return (WOActionResults)valueForBinding(BINDING_ON_CREATION_SUCCESS);
	            }
	        } catch (ValidationException e) {
	            LOG.warn(e);
	            session().addSimpleErrorMessage(e.getLocalizedMessage(), e);
	        } catch (Exception e) {
	            LOG.error(e);
	            throw new NSForwardException(e);
	        }
	        CktlAjaxWindow.close(context());
	        return null;
	    }
	
	public WOActionResults annuler() {
		if (editingContext() != null) {
			editingContext().revert();
		}
		CktlAjaxWindow.close(context());
		return null;
	}
	
	public WOActionResults onCloseResultModal() {
        return null;
    }
	
	public WOActionResults sauvegardeEnBase() {
		
		// Récupération de l'editingContext de la session
		EOEditingContext edc = editingContext();
		
		// Réalisation d'un try-catch pour éviter les soucis lors de l'écriture en base
		/*
		 * Avec la ligne ci-dessous, nous accédons aux propriétés du modèle.
		 * Nous envisageons de pouvoir désactiver temporairement et à notre guise
		 * le "Read Only" du modèle
		 */
		
		EOEntity entityParameter = ERXEOAccessUtilities.entityForEo(getNewParametre());
		try {
			// Avant de sauvegarder les données, nous modifions le modèle
			// pour que l'on puisse avoir accès aussi en écriture sur les données
			entityParameter.setReadOnly(false);
			edc.saveChanges();
		} finally {
			entityParameter.setReadOnly(true);
		}
		
		return null;
	}
	
	private void valeurParDefaut() {
		// Mise par défaut d'un texte pour les commentaires
		getNewParametre().setParamCommentaires("Veuillez renseigner les commentaires concernant ce paramètre.");
		
		// A partir d'ici, nous mettrons une valeur par défaut selon le type du paramètre.
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("CHARACTERS_LIST")) {
				getNewParametre().setParamValue("vide");
			}
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("CLASSNAME")) {
				getNewParametre().setParamValue("fr.univ.agrhum.cocktail");
			}
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("CODE_ACTIVATION")) {
				getNewParametre().setParamValue("N");
			}
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("C_STRUCTURE")) {
				getNewParametre().setParamValue("0");
			}
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("DATE")) {
				getNewParametre().setParamValue(new NSTimestamp().toString());
			}
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("DOMAINE")) {
				getNewParametre().setParamValue(EOGrhumParametres.PARAM_GRHUM_DOMAINE_PRINCIPAL);
			}
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("EMAIL")) {
				getNewParametre().setParamValue(session().applicationUser().getLogin()+"@"+EOGrhumParametres.PARAM_GRHUM_DOMAINE_PRINCIPAL);
			}
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("FREE_TEXT")) {
				getNewParametre().setParamValue("A renseigner");
			}
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("HTML")) {
				getNewParametre().setParamValue("<link href="+"http://www.cocktail.org"+">");
			}
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("ID")) {
				getNewParametre().setParamValue(session().applicationUser().getPersId().toString());
			}
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("LIST")) {
				getNewParametre().setParamValue("X");
			}
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("LOGIN")) {
				getNewParametre().setParamValue(session().applicationUser().getLogin());
			}
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("PATH")) {
				getNewParametre().setParamValue("/tmp/");
			}
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("PERS_ID")) {
				getNewParametre().setParamValue(session().applicationUser().getPersId().toString());
			}
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("SCHOOL_YEAR")) {
				getNewParametre().setParamValue( ((Integer)MyDateCtrl.getYear(new NSTimestamp())).toString() );
			}
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("SERVEUR")) {
				getNewParametre().setParamValue("host." + EOGrhumParametres.PARAM_GRHUM_DOMAINE_PRINCIPAL);
			}
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("TCPIP")) {
				getNewParametre().setParamValue("8080");
			}
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("TIMEZONE")) {
				getNewParametre().setParamValue("Europe/Paris");
			}
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("URL")) {
				getNewParametre().setParamValue("http://host." + EOGrhumParametres.PARAM_GRHUM_DOMAINE_PRINCIPAL);
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("VALEUR_NUMERIQUE")) {
				getNewParametre().setParamValue("0");
			}
			if (getSelectedType() != null && getSelectedType().typeIdInterne().equals("VALIDITY_PERIODS")) {
				getNewParametre().setParamValue("7");
			}
			if ( getSelectedType() == null ) {
				throw new NSValidation.ValidationException("Pas de type sélectionné !");
			}
		}
	}
}