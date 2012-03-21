package org.cocktail.agrhum.serveur.components;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOAdaptorChannel;
import com.webobjects.eoaccess.EODatabaseContext;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eoaccess.EOStoredProcedure;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOObjectStoreCoordinator;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

import er.ajax.AjaxUpdateContainer;
import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXEOAccessUtilities;
import er.extensions.eof.ERXQ;

import org.apache.log4j.Logger;
import org.cocktail.fwkcktlajaxwebext.serveur.component.tableview.column.CktlAjaxTableViewColumn;
import org.cocktail.fwkcktlajaxwebext.serveur.component.tableview.column.CktlAjaxTableViewColumnAssociation;
import org.cocktail.fwkcktljefyadmin.common.finder.FinderOrgan;
import org.cocktail.fwkcktlpersonne.common.metier.EOIndividu;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;
import org.cocktail.fwkcktlpersonneguiajax.serveur.components.ASelectComponent;
import org.cocktail.fwkcktlpersonneguiajax.serveur.controleurs.NotificationCtrl;

import com.webobjects.foundation.NSArray;

/**
 * Composant de gestion des structures fournisseurs internes
 * 
 * @author Pierre-Yves MARIE <pierre-yves.marie at cocktail.org>
 *
 */
public class GestionFournisseursInternes extends MyWOComponent {
	private static final long serialVersionUID = 5040385088251442576L;
	
	private static final Logger LOG = Logger.getLogger(GestionFournisseursInternes.class);
	
	private static final String FOURNIS_INT_CONTAINER_ID = "fournisInternesContainer";
	private static final String STRUCTUREOB = ASelectComponent.CURRENT_OBJ_KEY;
	
	private String sectionFournisseurInterne;
    private EOEditingContext editingContext;
    private EOStructure structureOB;
    private NSArray allStructuresOB;
	private boolean resetLeftTreeView;
    
    private NSArray<CktlAjaxTableViewColumn> colonnes;

	private String structureOBFilter;

	public GestionFournisseursInternes(WOContext context) {
        super(context);
    }
    
    public String getSectionFournisseurInterne() {
        return sectionFournisseurInterne;
    }
    
    public void setSectionFournisseurInterne(String sectionFournisseur) {
        this.sectionFournisseurInterne = sectionFournisseur;
    }

    /**
     * @return the editingContext
     */
    public EOEditingContext editingContext() {
        if (editingContext == null) {
            editingContext = ERXEC.newEditingContext();
        }
        return editingContext;
    }

	/**
	 * @return the fournisInternesContainerId
	 */
	public String getFournisInternesContainerId() {
		return FOURNIS_INT_CONTAINER_ID;
	}

	/**
	 * @return the structureOB
	 */
	public EOStructure getStructureOB() {
		return structureOB;
	}

	/**
	 * @param structureOB the structureOB to set
	 */
	public void setStructureOB(EOStructure structureOB) {
		AjaxUpdateContainer.updateContainerWithID(getFournisInternesContainerId(), context());
		this.structureOB = structureOB;
	}

	public WOActionResults onSelectStructureOB() {
//		setResetTabs(true);
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
		NSMutableDictionary<String, Object> userInfo = new NSMutableDictionary<String, Object>();
		userInfo.setObjectForKey(edc(), "edc");
		NotificationCtrl.postNotificationForOnAnnulerNotification(this, userInfo);
		setResetLeftTreeView(true);
	}

	/**
	 * @return the allStructuresOB
	 */
	public NSArray getAllStructuresOB() {
		if (allStructuresOB==null) {
			allStructuresOB = FinderOrgan.getAllStructureAvecOrgan(editingContext());
		}
		return allStructuresOB;
	}

	/**
	 * @param allStructuresOB the allStructuresOB to set
	 */
	public void setAllStructuresOB(NSArray allStructuresOB) {
		this.allStructuresOB = allStructuresOB;
	}
	
	public NSArray<CktlAjaxTableViewColumn> getColonnes() {
		if (colonnes == null) {
			NSMutableArray<CktlAjaxTableViewColumn> colTmp = new NSMutableArray<CktlAjaxTableViewColumn>();
			// Colonne Libelle
			CktlAjaxTableViewColumn col = new CktlAjaxTableViewColumn();
			col.setLibelle("Structures pouvant jouer le rôle de fournisseur interne");
			col.setOrderKeyPath(EOStructure.LL_STRUCTURE_KEY);
			String keyPath = ERXQ.keyPath(STRUCTUREOB, EOStructure.LL_STRUCTURE_KEY);
			CktlAjaxTableViewColumnAssociation ass = new CktlAjaxTableViewColumnAssociation(
					keyPath, "");
			col.setAssociations(ass);
			colTmp.add(col);
//			// Colonne Libelle long
//			col = new CktlAjaxTableViewColumn();
//			col.setLibelle("Libellé");
//			col.setOrderKeyPath(LL_KEY);
//			keyPath = ERXQ.keyPath(CORPS, LL_KEY);
//			ass = new CktlAjaxTableViewColumnAssociation(
//					keyPath, "");
//			col.setAssociations(ass);
//			colTmp.add(col);
			colonnes = colTmp.immutableClone();
		}
		return colonnes;
	}

	public WOActionResults callInsFournisInternesProc() {
		WOActionResults result = null;
		
		try {
//				// TODO : Appel de la procedure stockée GRHUM.INS_FOURNIS_INTERNE pour rester compatible avec l'ancienne façon
//				// de déclarer un fournisseur interne.
			
			EOObjectStoreCoordinator objectStore = (EOObjectStoreCoordinator)editingContext().rootObjectStore();
			EODatabaseContext databaseContext = ERXEOAccessUtilities.databaseContextForEntityNamed(objectStore, EOIndividu.ENTITY_NAME);
			EOAdaptorChannel adaptorChannel = databaseContext.availableChannel().adaptorChannel();
			EOStoredProcedure myProcedure = EOModelGroup.defaultGroup().storedProcedureNamed("FwkPers_prcInsFournisInterne");
		    adaptorChannel.executeStoredProcedure(myProcedure, new NSDictionary());
			
//			CktlDataBus databus = new CktlDataBus(editingContext());
//			databus.executeProcedure("FwkPers_prcInsFournisInterne", new NSDictionary());
				
			session().addSimpleInfoMessage(localizer().localizedStringForKey("confirmation"), null);
		} catch (NSForwardException e) {
		    LOG.error(e.getMessage(), e);
			context().response().setStatus(500);
			if (e.originalException() != null)
			    session().addSimpleErrorMessage(e.originalException().getMessage(), e.originalException());
		} catch (Exception e1) {
		    LOG.error(e1.getMessage(), e1);
			context().response().setStatus(500);
            session().addSimpleErrorMessage(e1.getMessage(), e1);
		}
		
		return result;
	}
	
	/**
	 * @return the structureOBFilter
	 */
	public String getStructureOBFilter() {
		return structureOBFilter;
	}

	/**
	 * @param structureOBFilter the structureOBFilter to set
	 */
	public void setStructureOBFilter(String structureOBFilter) {
		this.structureOBFilter = structureOBFilter;
	}

	public EOQualifier getStructureOBFilterQualifier() {
		return ERXQ.like(EOStructure.LL_STRUCTURE_KEY, getStructureOBFilter());
	}
	
}