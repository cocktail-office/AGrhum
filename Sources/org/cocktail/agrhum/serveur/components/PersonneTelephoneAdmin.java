package org.cocktail.agrhum.serveur.components;

import org.cocktail.fwkcktlpersonne.common.metier.EOTypeTel;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;

import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOQualifier;

public class PersonneTelephoneAdmin extends MyWOComponent {
	private static final long serialVersionUID = -2778385285521872230L;
	
	public static final String BDG_editingContext = "editingContext";
	public static final String BDG_utilisateurPersId = "utilisateurPersId";
	public static final String BDG_personne = "personne";
	
	private EOQualifier qualifierTelPro;

	public PersonneTelephoneAdmin(WOContext context) {
        super(context);
    }
	
	public void setQualifierTelPro(){
		this.qualifierTelPro = EOTypeTel.QUAL_C_TYPE_TEL_PRF;
	}
	
	public EOQualifier qualifierTelPro() {
		if (this.qualifierTelPro == null) 
			this.qualifierTelPro = EOTypeTel.QUAL_C_TYPE_TEL_PRF;
		return this.qualifierTelPro;
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