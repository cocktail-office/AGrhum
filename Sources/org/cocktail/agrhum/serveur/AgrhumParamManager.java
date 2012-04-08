package org.cocktail.agrhum.serveur;

import org.cocktail.fwkcktldroitsutils.common.metier.EOCompte;
import org.cocktail.fwkcktlpersonne.common.PersonneApplicationUser;
import org.cocktail.fwkcktlpersonne.common.metier.EOGrhumParametres;
import org.cocktail.fwkcktlpersonne.common.metier.EOGrhumParametresType;
import org.cocktail.fwkcktlwebapp.server.CktlParamManager;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eocontrol.EOEditingContext;

import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXEOAccessUtilities;
import er.extensions.foundation.ERXThreadStorage;

public class AgrhumParamManager extends CktlParamManager {
	
	public static final String AGRHUM_CHECK_COHERENCE_INSEE_DISABLED = "org.cocktail.agrhum.individu.checkcoherenceinsee.disabled";
	public static final String AGRHUM_CHECK_MANGUE_INSTALLED = "org.cocktail.agrhum.grhumparametres.checkmangue.installed";
	public static final String AGRHUM_CHECK_SCOLARIX_INSTALLED = "org.cocktail.agrhum.grhumparametres.checkscolarix.installed";
	public static final String AGRHUM_ADRESSE_PERSO_DESACTIVE = "org.cocktail.agrhum.adresse.personaladressmodification.disabled";
	public static final String AGRHUM_PERSONNE_NOM_READONLY_ACTIVE = "org.cocktail.agrhum.personne.nom.readonly.enabled";
	
	private EOEditingContext ec = ERXEC.newEditingContext();

	public AgrhumParamManager() {
		getParamList().add(AGRHUM_CHECK_COHERENCE_INSEE_DISABLED);
		getParamComments().put(AGRHUM_CHECK_COHERENCE_INSEE_DISABLED, "Autoriser ou non un individu à modifier son adresse personnelle");
		getParamDefault().put(AGRHUM_CHECK_COHERENCE_INSEE_DISABLED, "NON");
		getParamTypes().put(AGRHUM_CHECK_COHERENCE_INSEE_DISABLED, EOGrhumParametresType.codeActivation);
		
		getParamList().add(AGRHUM_CHECK_MANGUE_INSTALLED);
		getParamComments().put(AGRHUM_CHECK_MANGUE_INSTALLED, "Précise si ManGUE est installée");
		getParamDefault().put(AGRHUM_CHECK_MANGUE_INSTALLED, "N");
		getParamTypes().put(AGRHUM_CHECK_MANGUE_INSTALLED, EOGrhumParametresType.codeActivation);
		
		getParamList().add(AGRHUM_CHECK_SCOLARIX_INSTALLED);
		getParamComments().put(AGRHUM_CHECK_SCOLARIX_INSTALLED, "Précise");
		getParamDefault().put(AGRHUM_CHECK_SCOLARIX_INSTALLED, "N");
		getParamTypes().put(AGRHUM_CHECK_SCOLARIX_INSTALLED, EOGrhumParametresType.codeActivation);
		
		getParamList().add(AGRHUM_ADRESSE_PERSO_DESACTIVE);
		getParamComments().put(AGRHUM_ADRESSE_PERSO_DESACTIVE, "Autoriser ou non un individu à modifier son adresse personnelle dans Agrhum");
		getParamDefault().put(AGRHUM_ADRESSE_PERSO_DESACTIVE, "N");
		getParamTypes().put(AGRHUM_ADRESSE_PERSO_DESACTIVE, EOGrhumParametresType.codeActivation);

		getParamList().add(AGRHUM_PERSONNE_NOM_READONLY_ACTIVE);
		getParamComments().put(AGRHUM_PERSONNE_NOM_READONLY_ACTIVE, "Mettre le nom des personnes en lecture seulement si utilisateur pas GrhumCreateur");
		getParamDefault().put(AGRHUM_PERSONNE_NOM_READONLY_ACTIVE, "N");
		getParamTypes().put(AGRHUM_PERSONNE_NOM_READONLY_ACTIVE, EOGrhumParametresType.codeActivation);
	}
	
	@Override
	public void createNewParam(String key, String value, String comment) {
		createNewParam(key, value, comment, EOGrhumParametresType.codeActivation);
	}
	
	@Override
	public void checkAndInitParamsWithDefault() {
		//Recuperer un grhum_createur
		String cptLogin = EOGrhumParametres.parametrePourCle(ec, EOGrhumParametres.PARAM_GRHUM_CREATEUR);
		if (cptLogin != null) {
			EOCompte cpt = EOCompte.compteForLogin(ec, cptLogin);
			if (cpt != null) {
				ERXThreadStorage.takeValueForKey(cpt.persId(), PersonneApplicationUser.PERS_ID_CURRENT_USER_STORAGE_KEY);
			}
		}
		super.checkAndInitParamsWithDefault();
	}

	@Override
	public void createNewParam(String key, String value, String comment,
			String type) {
		EOGrhumParametres newParametre = EOGrhumParametres.creerInstance(ec);
		newParametre.setParamKey(key);
		newParametre.setParamValue(value);
		newParametre.setParamCommentaires(comment);
		newParametre.setToParametresTypeRelationship(EOGrhumParametresType.fetchByKeyValue(ec, EOGrhumParametresType.TYPE_ID_INTERNE_KEY, type));
		if (ec.hasChanges()) {
			EOEntity entityParameter = ERXEOAccessUtilities.entityForEo(newParametre);
			try {

				// Avant de sauvegarder les données, nous modifions le modèle
				// pour que l'on puisse avoir accès aussi en écriture sur les données
				entityParameter.setReadOnly(false);
				ec.saveChanges();

			} catch (Exception e) {
				log.warn("Erreur lors de l'enregistrement des parametres.");
				e.printStackTrace();
			} finally {
				entityParameter.setReadOnly(true);
			}
		}
	}

	@Override
	public String getParam(String key) {
		String res = getApplication().config().stringForKey(key);
		return res;
	}


}
