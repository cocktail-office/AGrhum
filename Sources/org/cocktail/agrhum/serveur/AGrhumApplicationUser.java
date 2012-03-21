/*
 * Copyright COCKTAIL (www.cocktail.org), 1995, 2010 This software 
 * is governed by the CeCILL license under French law and abiding by the
 * rules of distribution of free software. You can use, modify and/or 
 * redistribute the software under the terms of the CeCILL license as 
 * circulated by CEA, CNRS and INRIA at the following URL 
 * "http://www.cecill.info". 
 * As a counterpart to the access to the source code and rights to copy, modify 
 * and redistribute granted by the license, users are provided only with a 
 * limited warranty and the software's author, the holder of the economic 
 * rights, and the successive licensors have only limited liability. In this 
 * respect, the user's attention is drawn to the risks associated with loading,
 * using, modifying and/or developing or reproducing the software by the user 
 * in light of its specific status of free software, that may mean that it
 * is complicated to manipulate, and that also therefore means that it is 
 * reserved for developers and experienced professionals having in-depth
 * computer knowledge. Users are therefore encouraged to load and test the 
 * software's suitability as regards their requirements in conditions enabling
 * the security of their systems and/or data to be ensured and, more generally, 
 * to use and operate it in the same conditions as regards security. The
 * fact that you are presently reading this means that you have had knowledge 
 * of the CeCILL license and that you accept its terms.
 */

package org.cocktail.agrhum.serveur;

import org.apache.log4j.Logger;
import org.cocktail.fwkcktldroitsutils.common.util.AUtils;
import org.cocktail.fwkcktlgrh.common.GRHApplicationUser;
import org.cocktail.fwkcktljefyadmin.common.GFCApplicationUser;
import org.cocktail.fwkcktlpersonne.common.PersonneApplicationUser;
import org.cocktail.fwkcktlpersonne.common.metier.EOCompte;
import org.cocktail.fwkcktlpersonne.common.metier.EOFournis;
import org.cocktail.fwkcktlpersonne.common.metier.EOGrhumParametres;
import org.cocktail.fwkcktlpersonne.common.metier.EORepartStructure;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;
import org.cocktail.fwkcktlpersonne.common.metier.EOVlans;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

import er.extensions.eof.ERXQ;

/**
 * Représente un utilisateur de l'application AGrhum. Seules les specificites
 * exclusives à l'application sont decrites dans cette classe
 */

public class AGrhumApplicationUser extends PersonneApplicationUser {

	private GRHApplicationUser grhApplicationUser;
	private final static Logger LOG = Logger
			.getLogger(AGrhumApplicationUser.class);
	private Boolean peutConsulterGrhum;
	private Boolean peutAdministrerGrhum;

	public AGrhumApplicationUser(EOEditingContext ec, Session session,
			Integer persId) {
		super(ec, persId);
		initGrhApplicationUser(ec, session, persId);
	}

	/**
	 * Les apis suivantes necessitent l'identification de l'application via les
	 * tables de Jefy_admin
	 * 
	 * @link 
	 *       https://sites.google.com/a/cocktail.org/developpeurs/wo#TOC-D-l-guer
	 *       -la-gestion-des-droits-d-un
	 * 
	 */
	public AGrhumApplicationUser(EOEditingContext ec, Session session,
			String tyapStrId, Integer persId) {
		super(ec, tyapStrId, persId);
		initGrhApplicationUser(ec, session, persId);
	}

	private void initGrhApplicationUser(EOEditingContext ec, Session session,
			Integer persId) {
		try {
			this.grhApplicationUser = new GRHApplicationUser(ec, persId);
			GRHApplicationUser.registerIntoSession(grhApplicationUser, session);
		} catch (IllegalStateException e) {
			LOG.warn(
					"Impossible d'initialiser le user GRH, la raison probable est que l'utilisateur "
							+ "n'est pas enregistré dans AGENT_PERSONNEL", e);
			String isModuleGRHInUse = EOGrhumParametres.parametrePourCle(ec,
					EOGrhumParametres.PARAM_GRHUM_RH, "OUI");
			if (isModuleGRHInUse.startsWith("OUI")
					|| isModuleGRHInUse.startsWith("O")) {
				session.addSimpleErrorMessage(
						"Attention les droits GRH n'ont pu être initialisés, les fonctions des menus AGrhum seront limitées.",
						e);
			}
		}
	}

	public GRHApplicationUser getGrhApplicationUser() {
		return grhApplicationUser;
	}

	public boolean peutConsulterGrhum() {
		if (peutConsulterGrhum == null) {
			NSArray<EOCompte> comptes = iPersonne().toComptes(
					ERXQ.inObjects(EOCompte.TO_VLANS_KEY + "."
							+ EOVlans.C_VLAN_KEY, EOVlans.VLAN_P,
							EOVlans.VLAN_R));
			peutConsulterGrhum = !comptes.isEmpty();
		}
		return peutConsulterGrhum;
	}

	public boolean peutAdministrerGrhum() {
		if (peutAdministrerGrhum == null) {
			peutAdministrerGrhum = true; // Par defaut tout le monde peut
											// administrer GRhum
			if (Application.app().getCStructuresAccesGrhum() != null) {
				peutAdministrerGrhum = (Boolean
						.valueOf(hasDroitGrhumCreateur()) || isInGrhumAccessStructures());
			}
		}
		return peutAdministrerGrhum;
	}

	public boolean isInGrhumAccessStructures() {
		NSArray<String> cStructureAGrhumUsers = Application.app()
				.getCStructuresAccesGrhum().allObjects();
		EOQualifier qualifier = ERXQ.is(EORepartStructure.PERS_ID_KEY,
				getPersId()).and(
				ERXQ.in(EORepartStructure.C_STRUCTURE_KEY,
						cStructureAGrhumUsers));

		return (EORepartStructure.fetchAll(getEditingContext(), qualifier)
				.count() > 0);
	}

	public boolean peutUtiliserGrhum() {
		return peutConsulterGrhum() && peutAdministrerGrhum();
	}

	public boolean peutCreerFournisseur() {
		return hasDroitCreationEOFournis(null, AUtils.currentExercice());
	}

	public boolean peutVoirFournisseur() {
		return hasDroitVisualisationEOFournis(null, AUtils.currentExercice());
	}

	public boolean peutValiderFournisseur() {
		return hasDroitValidationEOFournis(null, AUtils.currentExercice());
	}

	public boolean peutModifierFournisseur(EOFournis fournisseur) {
		return hasDroitModificationEOFournis(fournisseur, AUtils.currentExercice());
	}

	public boolean nePeutValiderFournisseur() {
		return !peutValiderFournisseur();
	}

	public boolean peutModifierGroupe(EOStructure groupe) {
		return hasDroitModificationIPersonne(groupe);
	}

	public boolean peutSupprimerGroupe(EOStructure groupe) {
		return hasDroitModificationIPersonne(groupe);
	}

	public boolean peutGererFournisseursInternes() {
		GFCApplicationUser user = new GFCApplicationUser(getEditingContext(), getPersId());
		return user.hasDroitAssocierRoleFinancierAStructure(null) ;
	
	}
}
