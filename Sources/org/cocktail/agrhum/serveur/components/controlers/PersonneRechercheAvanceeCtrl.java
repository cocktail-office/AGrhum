package org.cocktail.agrhum.serveur.components.controlers;

import org.cocktail.agrhum.serveur.AGrhumApplicationUser;
import org.cocktail.fwkcktldroitsutils.common.util.MyStringCtrl;
import org.cocktail.fwkcktlpersonne.common.metier.EOVlans;
import org.cocktail.fwkcktlpersonne.common.metier.EOCompte;
import org.cocktail.fwkcktlpersonne.common.metier.EOCompteEmail;
import org.cocktail.fwkcktlpersonne.common.metier.EOFournis;
import org.cocktail.fwkcktlpersonne.common.metier.EOIndividu;
import org.cocktail.fwkcktlpersonne.common.metier.EOPersonneAlias;
import org.cocktail.fwkcktlpersonne.common.metier.EOSecretariat;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXFetchSpecification;
import er.extensions.eof.ERXQ;
import er.extensions.foundation.ERXStringUtilities;

public class PersonneRechercheAvanceeCtrl {

	public static final String TOUTES_LES_CONDITIONS = "TOUTES_LES_CONDITIONS";
	public static final String AU_MOINS_UNE_CONDITION = "AU_MOINS_UNE_CONDITION";
	
	public static final NSArray<String> COMBINAISONS_CONDITIONS_CLES = new NSArray<String>(TOUTES_LES_CONDITIONS, AU_MOINS_UNE_CONDITION);
	public static final NSArray<String> COMBINAISONS_CONDITIONS_LIBELLES = new NSArray<String>("Tous les critères", "Au moins un des critères");
	public static NSDictionary<String, String> COMBINAISONS_CONDITIONS = new NSDictionary<String, String>(COMBINAISONS_CONDITIONS_LIBELLES, COMBINAISONS_CONDITIONS_CLES);
	
	public static NSArray<String> ETATS_FOURNISSEURS_CLES = new NSArray<String>(EOFournis.FOU_VALIDE_OUI, EOFournis.FOU_VALIDE_NON, EOFournis.FOU_VALIDE_ANNULE);
	public static NSArray<String> ETATS_FOURNISSEURS_LIBELLES = new NSArray<String>("Valide", "En instance de validation", "Annulé");
	public static NSDictionary<String, String> ETATS_FOURNISSEURS = new NSDictionary<String, String>(ETATS_FOURNISSEURS_LIBELLES, ETATS_FOURNISSEURS_CLES);
	
		
	public String KEY_STRUCTURE_LIBELLE_LONG = EOStructure.LL_STRUCTURE.key();
	public String KEY_STRUCTURE_C_STRUCTURE = EOStructure.C_STRUCTURE.key();
	public String KEY_STRUCTURE_PERS_ID = EOStructure.PERS_ID.key();
	public String KEY_STRUCTURE_FOU_VALIDE = EOStructure.TO_FOURNISS.dot(EOFournis.FOU_VALIDE).key();
	public String KEY_STRUCTURE_FOU_CODE = EOStructure.TO_FOURNISS.dot(EOFournis.FOU_CODE).key();
	public String KEY_STRUCTURE_SIRET = EOStructure.SIRET.key();
	public String KEY_STRUCTURE_DATE_FERMETURE = EOStructure.DATE_FERMETURE.key();
	
	private String champStructureLibelleLong;
	private String champStructureLibelleCourt;
	private String champStructureSiret;
	private String champStructureCodeFournisseur;
	private String champStructureEtatFournisseur;
	private String champStructureAlias;
	private String champStructureLogin;
	private Integer champStructurePersId;
	private Integer champStructureCStructure;
	private Boolean champStructureValide = true;
	private String champStructureApe;
	private String champStructureNaf;
	private EOIndividu champStructureResponsable;
	private EOIndividu champStructureSecretaire;
	
	
	public String KEY_INDIVIDU_NOM_USUEL = EOIndividu.NOM_USUEL.key();
	public String KEY_INDIVIDU_NOM_PATRONYMIQUE = EOIndividu.NOM_PATRONYMIQUE.key();
	public String KEY_INDIVIDU_PRENOM = EOIndividu.PRENOM.key();
	public String KEY_INDIVIDU_NO_INDIVIDU = EOIndividu.NO_INDIVIDU.key();
	public String KEY_INDIVIDU_PERS_ID = EOIndividu.PERS_ID.key();
	public String KEY_INDIVIDU_FOU_VALIDE = EOIndividu.TO_FOURNISS.dot(EOFournis.FOU_VALIDE).key();
	public String KEY_INDIVIDU_FOU_CODE = EOIndividu.TO_FOURNISS.dot(EOFournis.FOU_CODE).key();
	public String KEY_INDIVIDU_VALIDE = EOIndividu.TEM_VALIDE.key();

	
	private String champIndividuNomUsuel;
	private String champIndividuNomPatronymique;
	private String champIndividuNomAffichage;
	private String champIndividuPrenom;
	private String champIndividuPrenomAffichage;
	private String champIndividuQualite;
	private Integer champIndividuNumero;
	private String champIndividuCodeFournisseur;
	private String champIndividuEtatFournisseur;
	private String champIndividuAlias;
	private String champIndividuLogin;
	private Integer champIndividuPersId;
	private Boolean champIndividuValide = true;
	private String champIndividuEmail;
	private String champIndividuDomaine;
	private EOVlans champIndividuVlan;
	
	
	private String combinaisonConditions = TOUTES_LES_CONDITIONS;
	
	private NSArray<NSDictionary<String, Object>> resultatsDeRecherche;
	
	private AGrhumApplicationUser applicationUser;
	
	private EOEditingContext editingContext;
	
	public PersonneRechercheAvanceeCtrl() {
		
	}
	
	public PersonneRechercheAvanceeCtrl(AGrhumApplicationUser applicationUser) {
		setApplicationUser(applicationUser);
	}

	public AGrhumApplicationUser getApplicationUser() {
		return applicationUser;
	}

	public void setApplicationUser(AGrhumApplicationUser applicationUser) {
		this.applicationUser = applicationUser;
	}
	
	public EOEditingContext editingContext() {
		if(editingContext == null) {
			editingContext = ERXEC.newEditingContext();
		}
		return editingContext;
	}
	
	

	public NSArray<NSDictionary<String, Object>> getResultatsDeRecherche() {
		return resultatsDeRecherche;
	}

	public void setResultatsDeRecherche(NSArray<NSDictionary<String, Object>> resultatsDeRecherche) {
		this.resultatsDeRecherche = resultatsDeRecherche;
	}

	public Boolean getToutesLesConditionsSelected() {
		return ERXStringUtilities.stringEqualsString(getCombinaisonConditions(), TOUTES_LES_CONDITIONS);
	}

	public void setToutesLesConditionsSelected(Boolean toutesLesConditionsSelected) {
		if(toutesLesConditionsSelected) {
			setCombinaisonConditions(TOUTES_LES_CONDITIONS);
		}
	}

	public Boolean getAuMoinsUneConditionSelected() {
		return ERXStringUtilities.stringEqualsString(getCombinaisonConditions(), AU_MOINS_UNE_CONDITION);
	}

	public void setAuMoinsUneConditionSelected(Boolean auMoinsUneConditionSelected) {
		if(auMoinsUneConditionSelected) {
			setCombinaisonConditions(AU_MOINS_UNE_CONDITION);
		}	
	}
	
	public String getCombinaisonConditions() {
		return combinaisonConditions;
	}

	public void setCombinaisonConditions(String combinaisonConditions) {
		this.combinaisonConditions = combinaisonConditions;
	}
	
	
	
	
	
	public String getChampStructureLibelleLong() {
		return champStructureLibelleLong;
	}
	
	public void setChampStructureLibelleLong(String champStructureLibelleLong) {
		this.champStructureLibelleLong = MyStringCtrl.chaineSansAccents(champStructureLibelleLong);
	}
	
	
	public String getChampStructureLibelleCourt() {
		return champStructureLibelleCourt;
	}
	
	public void setChampStructureLibelleCourt(String champStructureLibelleCourt) {
		this.champStructureLibelleCourt = MyStringCtrl.chaineSansAccents(champStructureLibelleCourt);
	}
	
	
	public String getChampStructureSiret() {
		return champStructureSiret;
	}
	
	public void setChampStructureSiret(String champStructureSiret) {
		this.champStructureSiret = champStructureSiret;
	}
	
	
	public String getChampStructureCodeFournisseur() {
		return champStructureCodeFournisseur;
	}
	
	public void setChampStructureCodeFournisseur(String champStructureCodeFournisseur) {
		this.champStructureCodeFournisseur = champStructureCodeFournisseur;
	}
	
	
	public String getChampStructureEtatFournisseur() {
		return champStructureEtatFournisseur;
	}
	
	public void setChampStructureEtatFournisseur(String champStructureEtatFournisseur) {
		this.champStructureEtatFournisseur = champStructureEtatFournisseur;
	}
	
	
	public String getChampStructureAlias() {
		return champStructureAlias;
	}
	
	public void setChampStructureAlias(String champStructureAlias) {
		this.champStructureAlias = champStructureAlias;
	}
	
	
	public String getChampStructureLogin() {
		return champStructureLogin;
	}
	
	public void setChampStructureLogin(String champStructureLogin) {
		this.champStructureLogin = champStructureLogin;
	}
	
	
	public Integer getChampStructurePersId() {
		return champStructurePersId;
	}
	
	public void setChampStructurePersId(Integer champStructurePersId) {
		this.champStructurePersId = champStructurePersId;
	}
	
	
	public Integer getChampStructureCStructure() {
		return champStructureCStructure;
	}
	
	public void setChampStructureCStructure(Integer champStructureCStructure) {
		this.champStructureCStructure = champStructureCStructure;
	}
	
	
	public Boolean getChampStructureValide() {
		return champStructureValide;
	}
	
	public void setChampStructureValide(Boolean champStructureValide) {
		this.champStructureValide = champStructureValide;
	}
	
	
	public String getChampStructureApe() {
		return champStructureApe;
	}
	
	public void setChampStructureApe(String champStructureApe) {
		this.champStructureApe = champStructureApe;
	}
	
	
	public String getChampStructureNaf() {
		return champStructureNaf;
	}
	
	public void setChampStructureNaf(String champStructureNaf) {
		this.champStructureNaf = champStructureNaf;
	}
	
	
	public EOIndividu getChampStructureResponsable() {
		return champStructureResponsable;
	}
	
	public void setChampStructureResponsable(EOIndividu champStructureResponsable) {
		this.champStructureResponsable = champStructureResponsable;
	}
	
	
	public EOIndividu getChampStructureSecretaire() {
		return champStructureSecretaire;
	}
	
	public void setChampStructureSecretaire(EOIndividu champStructureSecretaire) {
		this.champStructureSecretaire = champStructureSecretaire;
	}
	
	public void resetChampsStructure() {
		
		champStructureLibelleLong = null;
		champStructureLibelleCourt = null;
		champStructureSiret = null;
		champStructureCodeFournisseur = null;
		champStructureEtatFournisseur = null;
		champStructureAlias = null;
		champStructureLogin = null;
		champStructurePersId = null;
		champStructureCStructure = null;
		champStructureValide = true;
		champStructureApe = null;
		champStructureNaf = null;
		champStructureResponsable = null;
		champStructureSecretaire = null;
		
		combinaisonConditions = TOUTES_LES_CONDITIONS;

	}
	
	protected EOQualifier structureQualifier() {
		EOQualifier structureQualifier = null;
		NSArray<EOQualifier> qualifiers = new NSMutableArray<EOQualifier>();
		EOQualifier qualifier = null;
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampStructureLibelleLong())) {
			qualifier = EOStructure.LL_STRUCTURE.startsWithInsensitive(getChampStructureLibelleLong());
			qualifiers.add(qualifier);
		}
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampStructureLibelleCourt())) {
			qualifier = EOStructure.LC_STRUCTURE.startsWithInsensitive(getChampStructureLibelleCourt());
			qualifiers.add(qualifier);
		}
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampStructureSiret())) {
			qualifier = EOStructure.SIRET.ilike(getChampStructureSiret());
			qualifiers.add(qualifier);
		}
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampStructureCodeFournisseur())) {
			qualifier = EOStructure.TO_FOURNISS.dot(EOFournis.FOU_CODE).ilike(getChampStructureCodeFournisseur());
			qualifiers.add(qualifier);
		}
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampStructureAlias())) {
			qualifier = ERXQ.or(
					EOStructure.GRP_ALIAS.ilike(getChampStructureAlias()),
					EOStructure.TO_PERSONNE_ALIASES.dot(EOPersonneAlias.ALIAS).ilike(getChampStructureAlias())
				);
			qualifiers.add(qualifier);
		}
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampStructureLogin())) {
			qualifier = EOStructure.TO_COMPTES.dot(EOCompte.CPT_LOGIN).ilike(getChampStructureLogin());
			qualifiers.add(qualifier);
		}
		
		if(getChampStructurePersId() != null) {
			qualifier = EOStructure.PERS_ID.eq(getChampStructurePersId());
			qualifiers.add(qualifier);
		}
		
		if(getChampStructureCStructure() != null) {
			qualifier = EOStructure.C_STRUCTURE.ilike(getChampStructureCStructure().toString());
			qualifiers.add(qualifier);
		}
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampStructureApe())) {
			qualifier = EOStructure.GRP_APE_CODE.ilike(getChampStructureApe());
			qualifiers.add(qualifier);
		}
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampStructureNaf())) {
			qualifier = EOStructure.C_NAF.ilike(getChampStructureNaf());
			qualifiers.add(qualifier);
		}
		
		if(getChampStructureResponsable() != null) {
			qualifier = EOStructure.TO_RESPONSABLE.eq(getChampStructureResponsable());
			qualifiers.add(qualifier);
		}
		
		if(getChampStructureSecretaire() != null) {
			qualifier = EOStructure.TO_SECRETARIATS.dot(EOSecretariat.TO_INDIVIDU).eq(getChampStructureSecretaire());
			qualifiers.add(qualifier);
		}
		
		if(ERXStringUtilities.stringEqualsString(getCombinaisonConditions(), AU_MOINS_UNE_CONDITION)) {
			structureQualifier = ERXQ.or(qualifiers);
		}
		
		if(ERXStringUtilities.stringEqualsString(getCombinaisonConditions(), TOUTES_LES_CONDITIONS)) {
			structureQualifier = ERXQ.and(qualifiers);
		}
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampStructureEtatFournisseur())) {
			structureQualifier = EOStructure.TO_FOURNISS.dot(EOFournis.FOU_VALIDE).eq(getChampStructureEtatFournisseur()).and(structureQualifier);
		}
		
		if(getChampStructureValide() != null && getChampStructureValide() == true) {
			structureQualifier = EOStructure.DATE_FERMETURE.isNull().and(structureQualifier);
		}
		
		return structureQualifier;
	}
	
	public void lancerUneRechercheStructure() {
		
		ERXFetchSpecification<EOStructure> spec = new ERXFetchSpecification<EOStructure>(EOStructure.ENTITY_NAME, structureQualifier(), EOStructure.LL_STRUCTURE.ascInsensitives());
		
		NSArray<String> keyPaths = new NSMutableArray<String>();
		
		keyPaths.add(KEY_STRUCTURE_LIBELLE_LONG);
		keyPaths.add(KEY_STRUCTURE_PERS_ID);
		keyPaths.add(KEY_STRUCTURE_C_STRUCTURE);
		keyPaths.add(KEY_STRUCTURE_FOU_CODE);
		keyPaths.add(KEY_STRUCTURE_FOU_VALIDE);
		keyPaths.add(KEY_STRUCTURE_SIRET);
		keyPaths.add(KEY_STRUCTURE_DATE_FERMETURE);
		
		spec.setRawRowKeyPaths(keyPaths);
		
		setResultatsDeRecherche(spec.fetchRawRows(editingContext()));
		
	}

	public String getChampIndividuNomUsuel() {
		return champIndividuNomUsuel;
	}

	public void setChampIndividuNomUsuel(String champIndividuNomUsuel) {
		this.champIndividuNomUsuel = MyStringCtrl.chaineSansAccents(champIndividuNomUsuel);
	}

	public String getChampIndividuNomPatronymique() {
		return champIndividuNomPatronymique;
	}

	public void setChampIndividuNomPatronymique(String champIndividuNomPatronymique) {
		this.champIndividuNomPatronymique = MyStringCtrl.chaineSansAccents(champIndividuNomPatronymique);
	}

	public String getChampIndividuNomAffichage() {
		return champIndividuNomAffichage;
	}

	public void setChampIndividuNomAffichage(String champIndividuNomAffichage) {
		this.champIndividuNomAffichage = MyStringCtrl.chaineSansAccents(champIndividuNomAffichage);
	}

	public String getChampIndividuPrenom() {
		return champIndividuPrenom;
	}

	public void setChampIndividuPrenom(String champIndividuPrenom) {
		this.champIndividuPrenom = MyStringCtrl.chaineSansAccents(champIndividuPrenom);
	}

	public String getChampIndividuPrenomAffichage() {
		return champIndividuPrenomAffichage;
	}

	public void setChampIndividuPrenomAffichage(String champIndividuPrenomAffichage) {
		this.champIndividuPrenomAffichage = MyStringCtrl.chaineSansAccents(champIndividuPrenomAffichage);
	}

	public String getChampIndividuQualite() {
		return champIndividuQualite;
	}

	public void setChampIndividuQualite(String champIndividuQualite) {
		this.champIndividuQualite = champIndividuQualite;
	}

	public Integer getChampIndividuNumero() {
		return champIndividuNumero;
	}

	public void setChampIndividuNumero(Integer champIndividuNumero) {
		this.champIndividuNumero = champIndividuNumero;
	}

	public String getChampIndividuCodeFournisseur() {
		return champIndividuCodeFournisseur;
	}

	public void setChampIndividuCodeFournisseur(String champIndividuCodeFournisseur) {
		this.champIndividuCodeFournisseur = champIndividuCodeFournisseur;
	}

	public String getChampIndividuEtatFournisseur() {
		return champIndividuEtatFournisseur;
	}

	public void setChampIndividuEtatFournisseur(String champIndividuEtatFournisseur) {
		this.champIndividuEtatFournisseur = champIndividuEtatFournisseur;
	}

	public String getChampIndividuAlias() {
		return champIndividuAlias;
	}

	public void setChampIndividuAlias(String champIndividuAlias) {
		this.champIndividuAlias = champIndividuAlias;
	}

	public String getChampIndividuLogin() {
		return champIndividuLogin;
	}

	public void setChampIndividuLogin(String champIndividuLogin) {
		this.champIndividuLogin = champIndividuLogin;
	}

	public Integer getChampIndividuPersId() {
		return champIndividuPersId;
	}

	public void setChampIndividuPersId(Integer champIndividuPersId) {
		this.champIndividuPersId = champIndividuPersId;
	}

	public Boolean getChampIndividuValide() {
		return champIndividuValide;
	}

	public void setChampIndividuValide(Boolean champIndividuValide) {
		this.champIndividuValide = champIndividuValide;
	}

	public String getChampIndividuEmail() {
		return champIndividuEmail;
	}

	public void setChampIndividuEmail(String champIndividuEmail) {
		this.champIndividuEmail = champIndividuEmail;
	}

	public String getChampIndividuDomaine() {
		return champIndividuDomaine;
	}

	public void setChampIndividuDomaine(String champIndividuDomaine) {
		this.champIndividuDomaine = champIndividuDomaine;
	}

	public EOVlans getChampIndividuVlan() {
		return champIndividuVlan;
	}

	public void setChampIndividuVlan(EOVlans champIndividuVlan) {
		this.champIndividuVlan = champIndividuVlan;
	}
	
	public void resetChampsIndividu() {
		
		champIndividuNomUsuel = null;
		champIndividuNomPatronymique = null;
		champIndividuNomAffichage = null;
		champIndividuPrenom = null;
		champIndividuPrenomAffichage = null;
		champIndividuQualite = null;
		champIndividuNumero = null;
		champIndividuCodeFournisseur = null;
		champIndividuEtatFournisseur = null;
		champIndividuAlias = null;
		champIndividuLogin = null;
		champIndividuPersId = null;
		champIndividuValide = true;
		champIndividuEmail = null;
		champIndividuDomaine = null;
		champIndividuVlan = null;
		
		combinaisonConditions = TOUTES_LES_CONDITIONS;

	}

	protected EOQualifier individuQualifier() {
		EOQualifier individuQualifier = null;
		NSArray<EOQualifier> qualifiers = new NSMutableArray<EOQualifier>();
		EOQualifier qualifier = null;
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampIndividuNomUsuel())) {
			qualifier = EOIndividu.NOM_USUEL.startsWithInsensitive(getChampIndividuNomUsuel());
			qualifiers.add(qualifier);
		}
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampIndividuNomPatronymique())) {
			qualifier = EOIndividu.NOM_PATRONYMIQUE.startsWithInsensitive(getChampIndividuNomPatronymique());
			qualifiers.add(qualifier);
		}
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampIndividuNomAffichage())) {
			qualifier = EOIndividu.NOM_AFFICHAGE.startsWithInsensitive(getChampIndividuNomAffichage());
			qualifiers.add(qualifier);
		}
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampIndividuPrenom())) {
			qualifier = EOIndividu.PRENOM.startsWithInsensitive(getChampIndividuPrenom());
			qualifiers.add(qualifier);
		}
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampIndividuPrenomAffichage())) {
			qualifier = EOIndividu.PRENOM_AFFICHAGE.startsWithInsensitive(getChampIndividuPrenomAffichage());
			qualifiers.add(qualifier);
		}

		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampIndividuCodeFournisseur())) {
			qualifier = EOIndividu.TO_FOURNISS.dot(EOFournis.FOU_CODE).ilike(getChampIndividuCodeFournisseur());
			qualifiers.add(qualifier);
		}
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampIndividuAlias())) {
			qualifier = EOIndividu.TO_PERSONNE_ALIASES.dot(EOPersonneAlias.ALIAS).ilike(getChampIndividuAlias());
			qualifiers.add(qualifier);
		}
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampIndividuLogin())) {
			qualifier = EOIndividu.TO_COMPTES.dot(EOCompte.CPT_LOGIN).ilike(getChampIndividuLogin());
			qualifiers.add(qualifier);
		}
		
		if(getChampIndividuPersId() != null) {
			qualifier = EOIndividu.PERS_ID.eq(getChampIndividuPersId());
			qualifiers.add(qualifier);
		}
		
		if(getChampIndividuNumero() != null) {
			qualifier = EOIndividu.NO_INDIVIDU.eq(getChampIndividuNumero());
			qualifiers.add(qualifier);
		}
		
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampIndividuQualite())) {
			qualifier = EOIndividu.IND_QUALITE.ilike(getChampIndividuQualite());
			qualifiers.add(qualifier);
		}
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampIndividuEmail())) {
			qualifier = EOIndividu.TO_COMPTES.dot(EOCompte.TO_COMPTE_EMAILS).dot(EOCompteEmail.CEM_EMAIL).ilike(getChampIndividuEmail());
			qualifiers.add(qualifier);
		}
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampIndividuDomaine())) {
			qualifier = EOIndividu.TO_COMPTES.dot(EOCompte.TO_COMPTE_EMAILS).dot(EOCompteEmail.CEM_DOMAINE).ilike(getChampIndividuDomaine());
			qualifiers.add(qualifier);
		}
		
		if(getChampIndividuVlan() != null) {
			qualifier = EOIndividu.TO_COMPTES.dot(EOCompte.TO_VLANS).eq(getChampIndividuVlan());
			qualifiers.add(qualifier);
		}
		
		if(ERXStringUtilities.stringEqualsString(getCombinaisonConditions(), AU_MOINS_UNE_CONDITION)) {
			individuQualifier = ERXQ.or(qualifiers);
		}
		
		if(ERXStringUtilities.stringEqualsString(getCombinaisonConditions(), TOUTES_LES_CONDITIONS)) {
			individuQualifier = ERXQ.and(qualifiers);
		}
		
		if(!ERXStringUtilities.stringIsNullOrEmpty(getChampIndividuEtatFournisseur())) {
			individuQualifier = EOIndividu.TO_FOURNISS.dot(EOFournis.FOU_VALIDE).eq(getChampIndividuEtatFournisseur()).and(individuQualifier);
		}
		
		if(getChampIndividuValide() != null && getChampIndividuValide() == true) {
			individuQualifier = EOIndividu.TEM_VALIDE.eq(EOIndividu.TEM_VALIDE_O).and(individuQualifier);
		}
		
		return individuQualifier;
	}
	
	public void lancerUneRechercheIndividu() {
		
		ERXFetchSpecification<EOIndividu> spec = new ERXFetchSpecification<EOIndividu>(EOIndividu.ENTITY_NAME, individuQualifier(), EOIndividu.NOM_USUEL.ascInsensitives());
		
		NSArray<String> keyPaths = new NSMutableArray<String>();
		
		keyPaths.add(KEY_INDIVIDU_NOM_USUEL);
		keyPaths.add(KEY_INDIVIDU_NOM_PATRONYMIQUE);
		keyPaths.add(KEY_INDIVIDU_NO_INDIVIDU);
		keyPaths.add(KEY_INDIVIDU_FOU_CODE);
		keyPaths.add(KEY_INDIVIDU_FOU_VALIDE);
		keyPaths.add(KEY_INDIVIDU_PERS_ID);
		keyPaths.add(KEY_INDIVIDU_PRENOM);
		keyPaths.add(KEY_INDIVIDU_VALIDE);

		
		spec.setRawRowKeyPaths(keyPaths);
		
		setResultatsDeRecherche(spec.fetchRawRows(editingContext()));
		
	}
	

}
