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
package org.cocktail.agrhum.serveur.components;

import org.apache.log4j.Logger;
import org.cocktail.fwkcktlajaxwebext.serveur.component.tableview.column.CktlAjaxTableViewColumn;
import org.cocktail.fwkcktlajaxwebext.serveur.component.tableview.column.CktlAjaxTableViewColumnAssociation;
import org.cocktail.fwkcktlpersonne.common.eospecificites.EOStructureForGroupeSpec;
import org.cocktail.fwkcktlpersonne.common.metier.EOAdresse;
import org.cocktail.fwkcktlpersonne.common.metier.EOPersonneTelephone;
import org.cocktail.fwkcktlpersonne.common.metier.EORepartPersonneAdresse;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;
import org.cocktail.fwkcktlpersonne.common.metier.EOTypeGroupe;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.appserver.ERXDisplayGroup;
import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXQ;
import er.extensions.eof.ERXS;

/**
 * 
 * Composant de création simple d'un groupe.
 * On ne passe pas exceptionnellement par le composant PersonneSrch pour simplifier la saisie de groupes.
 * 
 * Ce composant doit être englober dans un form. Ce composant gère son propre ec.
 * 
 * @binding parent le EOStructure parent dans lequel sera crée le nouveau groupe.
 * @binding onCreationSuccess callback appelé lorsque l'enregistrement a été fait avec succès
 * @binding wantReset flag pour indiquer à ce composant de reset son état interne
 * 
 * @author Alexis TUAL <alexis.tual at cocktail.org>
 *
 */
public class CreationGroupeForm extends MyWOComponent {

    private static final long serialVersionUID = 347883843978759945L;
    private static final Logger LOG = Logger.getLogger(CreationGroupeForm.class);
    public static final String BINDING_PARENT = "parent";
    public static final String BINDING_ON_CREATION_SUCCESS = "onCreationSuccess";
    public static final String BINDING_WANT_RESET = "wantReset";
    public static final String BINDING_NEW_GROUP = "newGroup";
    public static final String COL_TGRP_LIBELLE_KEY = EOTypeGroupe.TGRP_LIBELLE_KEY;
    public static final String COL_TGRP_CODE_KEY = EOTypeGroupe.TGRP_CODE_KEY;
    
    private String nomNewGroupe;
    private NSMutableArray<CktlAjaxTableViewColumn> colonnes;
    private ERXDisplayGroup<EOTypeGroupe> typeGroupeDisplayGroup;
    private EOTypeGroupe currentTypeGroupe;
    private EOEditingContext ec;
    
    public CreationGroupeForm(WOContext context) {
        super(context);
    }
    
    @Override
    public boolean synchronizesVariablesWithBindings() {
        return false;
    }
    
    @Override
    public void appendToResponse(WOResponse response, WOContext context) {
        if (booleanValueForBinding(BINDING_WANT_RESET, false)) {
            resetState();
            setValueForBinding(false, BINDING_WANT_RESET);
        }
        super.appendToResponse(response, context);
        
    }
    
    public EOStructure groupeParent() {
        EOStructure parent = (EOStructure) valueForBinding(BINDING_PARENT);
        if (parent == null)
            throw new ValidationException("Un groupe parent doit être sélectionné");
        parent = parent.localInstanceIn(editingContext());
        return parent;
    }
    
    private EOEditingContext editingContext() {
        if (ec == null)
        	ec = ERXEC.newEditingContext();
        return ec;
    }
    
    private void resetState() {
        nomNewGroupe = null;
        typeGroupeDisplayGroup = null;
        ec = null;
    }
    
    public WOActionResults ajouterGroupe() {
        try {
            // Vérifier le nom
            if (nomNewGroupe == null || nomNewGroupe.length() == 0)
                throw new ValidationException("Le nom du groupe ne doit pas être vide");
            // Si père null, prendre le groupe racine ?
            EOStructure newGroupe = EOStructureForGroupeSpec.creerGroupe(
                    editingContext(), 
                    session().applicationUser().getPersId(), 
                    getNomNewGroupe(), null, 
                    getTypeGroupeDisplayGroup().selectedObjects(), 
                    groupeParent());
            copierCoordonneesFromParent(newGroupe);
            
            editingContext().saveChanges();
            session().addSimpleInfoMessage("Nouveau groupe " + nomNewGroupe + " créé avec succès", 
                    "Le groupe a été enregistré dans le référentiel");
            
            if (hasBinding(BINDING_NEW_GROUP)) {
				setValueForBinding(newGroupe, BINDING_NEW_GROUP);
			}
            
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
        return null;
    }
    
    /**
     * Méthode copiant les adresses et numéros de tel du groupe parent vers le nouveau groupe fils
     * TODO : à déplacer dans le métier !
     */
    @SuppressWarnings("unchecked")
    private void copierCoordonneesFromParent(EOStructure newGroupe) {
        if (newGroupe.toStructurePere() != null) {
            // Recopie des adresses
            NSArray<EORepartPersonneAdresse> repartsPro = 
                newGroupe.toStructurePere().toRepartPersonneAdresses(EORepartPersonneAdresse.QUAL_PERSONNE_ADRESSE_PRO);
            for (EORepartPersonneAdresse repart : repartsPro) {
                EOAdresse adresse = repart.toAdresse();
                EOAdresse adresseCopie = EOAdresse.creerInstance(editingContext());
                adresseCopie.setAdrAdresse1(adresse.adrAdresse1());
                adresseCopie.setAdrAdresse2(adresse.adrAdresse2());
                adresseCopie.setAdrBp(adresse.adrBp());
                adresseCopie.setAdrGpsLatitude(adresse.adrGpsLatitude());
                adresseCopie.setAdrGpsLongitude(adresse.adrGpsLongitude());
                adresseCopie.setAdrListeRouge(adresse.adrListeRouge());
                adresseCopie.setAdrUrlPere(adresse.adrUrlPere());
                adresseCopie.setBisTer(adresse.bisTer());
                adresseCopie.setCImplantation(adresse.cImplantation());
                adresseCopie.setCodePostal(adresse.codePostal());
                adresseCopie.setCpEtranger(adresse.cpEtranger());
                adresseCopie.setCVoie(adresse.cVoie());
                adresseCopie.setDCreation(new NSTimestamp());
                adresseCopie.setDDebVal(adresse.dDebVal());
                adresseCopie.setDFinVal(adresse.dFinVal());
                adresseCopie.setDModification(new NSTimestamp());
                adresseCopie.setLocalite(adresse.localite());
                adresseCopie.setNomVoie(adresse.nomVoie());
                adresseCopie.setNoVoie(adresse.noVoie());
                adresseCopie.setPersIdCreation(session().applicationUser().getPersId());
                adresseCopie.setPersIdModification(session().applicationUser().getPersId());
                adresseCopie.setTemPayeUtil(adresse.temPayeUtil());
                adresseCopie.setToPaysRelationship(adresse.toPays());
                adresseCopie.setVille(adresse.ville());
                EORepartPersonneAdresse repartCopie = EORepartPersonneAdresse.creerInstance(editingContext());
                repartCopie.initForPersonne(editingContext(), newGroupe, adresseCopie, repart.toTypeAdresse());
            }
            // Recopie des téléphones
            NSArray<EOPersonneTelephone> tels =
                newGroupe.toStructurePere().toPersonneTelephones(EOPersonneTelephone.QUAL_PERSONNE_TELEPHONE_PRF_OK);
            for (EOPersonneTelephone tel: tels) {
                EOPersonneTelephone telCopie = EOPersonneTelephone.creerInstance(editingContext());
                telCopie.setDCreation(new NSTimestamp());
                telCopie.setDDebVal(tel.dDebVal());
                telCopie.setDFinVal(tel.dFinVal());
                telCopie.setDModification(new NSTimestamp());
                telCopie.setIndicatif(tel.indicatif());
                telCopie.setIsTelPrincipal(tel.isTelPrincipal());
                telCopie.setListeRouge(tel.listeRouge());
                telCopie.setNoTelephone(tel.noTelephone());
                telCopie.setTelPrincipal(tel.telPrincipal());
                telCopie.setToPersonneRelationship(newGroupe);
                telCopie.setToTypeNoTelRelationship(tel.toTypeNoTel());
                telCopie.setToTypeTelRelationship(tel.toTypeTel());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public ERXDisplayGroup<EOTypeGroupe> getTypeGroupeDisplayGroup() {
        if (typeGroupeDisplayGroup == null) {
            typeGroupeDisplayGroup = new ERXDisplayGroup<EOTypeGroupe>();
            NSArray<EOTypeGroupe> types = EOTypeGroupe.getTypesGroupeForUtilisateur(editingContext(), session().applicationUser());
            types = ERXQ.filtered(types, ERXQ.not(EOTypeGroupe.QUAL_TYPE_GROUPE_G));
            types = ERXS.sorted(types, EOTypeGroupe.SORT_TGRP_LIBELLE_ASC);
            typeGroupeDisplayGroup.setObjectArray(types);
            typeGroupeDisplayGroup.setSelectedObject(null);
        }
        return typeGroupeDisplayGroup;
    }
    
    public NSArray<CktlAjaxTableViewColumn> getColonnes() {
        if (colonnes == null) {
            colonnes = new NSMutableArray<CktlAjaxTableViewColumn>();
            CktlAjaxTableViewColumn col1 = new CktlAjaxTableViewColumn();
            col1.setLibelle("Type");
            col1.setOrderKeyPath(COL_TGRP_LIBELLE_KEY);
            CktlAjaxTableViewColumnAssociation ass1 = new CktlAjaxTableViewColumnAssociation("currentTypeGroupe" + "." + COL_TGRP_LIBELLE_KEY, " ");
            col1.setAssociations(ass1);
            CktlAjaxTableViewColumn col2 = new CktlAjaxTableViewColumn();
            col2.setLibelle("Code");
            col2.setOrderKeyPath(COL_TGRP_CODE_KEY);
            CktlAjaxTableViewColumnAssociation ass2 = new CktlAjaxTableViewColumnAssociation("currentTypeGroupe" + "." + COL_TGRP_CODE_KEY, " ");
            col2.setAssociations(ass2);
            colonnes.addObjects(col1, col2);
        }
        return colonnes;
    }

    public String getNomNewGroupe() {
        return nomNewGroupe;
    }
    
    public void setNomNewGroupe(String nomNewGroupe) {
        this.nomNewGroupe = nomNewGroupe;
    }
 
    public EOTypeGroupe getCurrentTypeGroupe() {
        return currentTypeGroupe;
    }
    
    public void setCurrentTypeGroupe(EOTypeGroupe currentTypeGroupe) {
        this.currentTypeGroupe = currentTypeGroupe;
    }
    
}