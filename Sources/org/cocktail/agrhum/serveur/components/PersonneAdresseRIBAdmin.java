package org.cocktail.agrhum.serveur.components;

import org.cocktail.fwkcktlpersonne.common.metier.EOFournis;
import org.cocktail.fwkcktlpersonne.common.metier.EORepartPersonneAdresse;
import org.cocktail.fwkcktlpersonne.common.metier.EOTypeAdresse;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;

import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOQualifier;

public class PersonneAdresseRIBAdmin extends MyWOComponent {
	private static final long serialVersionUID = -4401373659943095406L;
	
	public static final String BDG_editingContext = "editingContext";
	public static final String BDG_utilisateurPersId = "utilisateurPersId";
	public static final String BDG_personne = "personne";
	
	private EORepartPersonneAdresse selectedRepartPersonneAdresse;
	private Boolean isAdresseEditing;
	private EOQualifier qualifierAdrPro;
	private boolean isRibEditing;

	public PersonneAdresseRIBAdmin(WOContext context) {
        super(context);
    }
	
	public EORepartPersonneAdresse selectedRepartPersonneAdresse() {
		return selectedRepartPersonneAdresse;
	}

	public void setSelectedRepartPersonneAdresse(EORepartPersonneAdresse repartPersonneAdresse) {
		this.selectedRepartPersonneAdresse = repartPersonneAdresse;
	}

	/**
	 * @return the isAdresseEditing
	 */
	public Boolean isAdresseEditing() {
		return isAdresseEditing;
	}

	/**
	 * @param isAdresseEditing the isAdresseEditing to set
	 */
	public void setIsAdresseEditing(Boolean isAdresseEditing) {
		this.isAdresseEditing = isAdresseEditing;
	}
	
	public void setQualifierAdrPro(){
		this.qualifierAdrPro = EOTypeAdresse.QUAL_TADR_CODE_PRO;
	}
	
	public EOQualifier qualifierAdrPro() {
		if (this.qualifierAdrPro == null) 
			this.qualifierAdrPro = EOTypeAdresse.QUAL_TADR_CODE_PRO;
		return this.qualifierAdrPro;
	}
	
	/**
	 * @return the isRibEditing
	 */
	public boolean isRibEditing() {
		return isRibEditing;
	}

	/**
	 * @param isRibEditing the isRibEditing to set
	 */
	public void setIsRibEditing(boolean isRibEditing) {
		this.isRibEditing = isRibEditing;
	}

	public EOFournis getFournisseur() {
		return personne().toFournis();
	}
	
	/**
	 * @return the personne
	 */
	public IPersonne personne() {
		return (IPersonne)valueForBinding(BDG_personne);
	}

	/**
	 * @param personne the personne to set
	 */
	public void setPersonne(IPersonne personne) {
		setValueForBinding(personne, BDG_personne);
	}
	
}