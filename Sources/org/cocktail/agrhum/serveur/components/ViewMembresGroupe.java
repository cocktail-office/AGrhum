package org.cocktail.agrhum.serveur.components;

import org.cocktail.fwkcktlajaxwebext.serveur.component.tableview.column.CktlAjaxTableViewColumn;
import org.cocktail.fwkcktlajaxwebext.serveur.component.tableview.column.CktlAjaxTableViewColumnAssociation;
import org.cocktail.fwkcktlpersonne.common.metier.EORepartStructure;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

import er.ajax.AjaxUpdateContainer;
import er.extensions.appserver.ERXDisplayGroup;
import er.extensions.eof.ERXQ;

public class ViewMembresGroupe extends MyWOComponent {
    
    private static final long serialVersionUID = 3670400336991142219L;
    public static final String BINDING_DG = "displayGroup";
    public static final String BINDING_TBV_HEIGHT = "tbvHeight";
    public static final String BINDING_TBV_WIDTH = "tbvWidth";

    public static final String LIBELLE_STRUCTURE_KP =  EORepartStructure.TO_PERSONNE_ELT_KEY + "." + IPersonne.NOM_PRENOM_AFFICHAGE_KEY;
    
    private String filtreMembres;
    private NSArray<CktlAjaxTableViewColumn> colonnes;
    private EORepartStructure currentRepartStructure;

    public ViewMembresGroupe(WOContext context) {
        super(context);
    }
    
    @Override
    public boolean synchronizesVariablesWithBindings() {
        return false;
    }
    
    public WOActionResults filtrerMembres() {
        EOQualifier qual = ERXQ.contains(EORepartStructure.TO_PERSONNE_ELT_KEY + "." + IPersonne.NOM_PRENOM_AFFICHAGE_KEY, filtreMembres);
        getMembresDisplayGroup().setQualifier(qual);
        getMembresDisplayGroup().updateDisplayedObjects();
        return null;
    }
    
    public WOActionResults resetFiltre() {
        setFiltreMembres(null);
        getMembresDisplayGroup().setQualifier(null);
        getMembresDisplayGroup().updateDisplayedObjects();
        return null;
    }

    public WOActionResults refreshToolbar() {
        AjaxUpdateContainer.updateContainerWithID(toolbarContId(), context());
        return null;
    }
    
    public NSArray<CktlAjaxTableViewColumn> colonnes() {
        if (colonnes == null) {
            CktlAjaxTableViewColumn col0 = new CktlAjaxTableViewColumn();
            col0.setLibelle("Nom");
            CktlAjaxTableViewColumnAssociation asso0 = new CktlAjaxTableViewColumnAssociation(
                    "currentRepartStructure."+LIBELLE_STRUCTURE_KP, "Aucun libellé");
            col0.setAssociations(asso0);
            colonnes = new NSArray<CktlAjaxTableViewColumn>(col0);
        }
        return colonnes;
    }
    
    public boolean hasMembres() {
        return getMembresDisplayGroup().allObjects().count() > 0;
    }
    
    public ERXDisplayGroup<EORepartStructure> getMembresDisplayGroup() {
        return (ERXDisplayGroup<EORepartStructure>) valueForBinding(BINDING_DG);
    }
    
    public String tbvHeight() {
        return stringValueForBinding(BINDING_TBV_HEIGHT, "200");
    }
    
    public String tbvWidth() {
        return stringValueForBinding(BINDING_TBV_WIDTH, "auto");
    }
    
    public String membresTbvId() {
        return "MemTbv_" + uniqueDomId();
    }
    
    public String membresContId() {
        return "MemCont_" + uniqueDomId();
    }
    
    public String toolbarContId() {
        return "ToolbarCont_" + uniqueDomId();
    }
    
    public String getFiltreMembres() {
        return filtreMembres;
    }
    
    public void setFiltreMembres(String filtreMembres) {
        this.filtreMembres = filtreMembres;
    }
    
    public EORepartStructure getCurrentRepartStructure() {
        return currentRepartStructure;
    }
    
    public void setCurrentRepartStructure(EORepartStructure currentRepartStructure) {
        this.currentRepartStructure = currentRepartStructure;
    }
    
}