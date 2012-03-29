package org.cocktail.agrhum.serveur.components;

import org.cocktail.fwkcktldroitsutils.common.util.MyStringCtrl;
import org.cocktail.fwkcktlpersonne.common.metier.EOIndividu;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableSet;

import er.extensions.eof.ERXS;
import er.extensions.eof.ERXSortOrdering;
import er.extensions.foundation.ERXArrayUtilities;

public class PersonneRechercheGlobale extends MyWOComponent {
	private static final long serialVersionUID = -8648012745496408688L;

	private String srchPatern;
	private IPersonne selectedPersonne;
	private WODisplayGroup displayGroup;

	public PersonneRechercheGlobale(WOContext context) {
		super(context);
	}
	
	public String getGlobalSrchContainerId() {
		return getComponentId() + "_" + "GlobalSrchContainer";
	}

	public String srchPaternFieldId() {
		return getComponentId() + "_" + "TFSrchPatern";
	}

	public String getSrchPatern() {
		if (srchPatern != null) {
			srchPatern = srchPatern.trim();
		}
		return srchPatern;
	}

	public void setSrchPatern(String srchPatern) {
		this.srchPatern = srchPatern;
	}

//	public String btRechercherOnClick() {
//		return "jsOnRechercher(null);";
//	}

	public String getSrchFormId() {
		return getComponentId() + "_" + "GlobalSrchForm";
	}

	public WOActionResults doGlobalSearch() {
		try {
			NSArray res = NSArray.EmptyArray;
			displayGroup().setObjectArray(res);
//			checkSaisie();
			res = getResultats();
			displayGroup().setObjectArray(res);
		} catch (Exception e) {
			logger().error(e.getMessage(), e);
			session().addSimpleErrorMessage(e.getLocalizedMessage(), e);
		}
		return null;
	}

	public String getGlobalSearchResultModalID() {
		return getComponentId() + "_" + "GlobalSrchResultModal";
	}

	public Boolean hasResults() {
		return Boolean.valueOf(displayGroup().allObjects() != null && displayGroup().allObjects().count() > 0);
	}

	public class DgDelegate {
		public void displayGroupDidChangeSelection(WODisplayGroup group) {
			setSelectedPersonne((IPersonne) group.selectedObject());
		}
	}
	
	public IPersonne getSelectedPersonne() {
		return selectedPersonne;
	}

	public void setSelectedPersonne(IPersonne personne) {
		selectedPersonne = personne;
		// setValueForBinding(personne, BINDING_selectedPersonne);
		// setWantRefreshGroupes(Boolean.TRUE);
	}

	public WODisplayGroup displayGroup() {
		if (displayGroup == null) {
			// displayGroup = (WODisplayGroup)
			// valueForBinding(BINDING_displayGroup);
			// if (displayGroup == null) {
			displayGroup = new WODisplayGroup();
			// if (canSetValueForBinding(BINDING_displayGroup)) {
			// setValueForBinding(displayGroup, BINDING_displayGroup);
			// }
		}
		displayGroup.setDelegate(new DgDelegate());
		// }
		return displayGroup;
	}

	public Integer utilisateurPersId() {
		return applicationUser().getPersId();
	}

	public NSArray getResultats() {
		
		NSArray<IPersonne> resultats = new NSMutableArray<IPersonne>();
		NSArray<EOStructure> structures = new NSMutableArray<EOStructure>();
		NSArray<EOIndividu> individus = new NSMutableArray<EOIndividu>();
		
		String srchPatern = getSrchPatern();
		
		if (!MyStringCtrl.isEmpty(srchPatern)) {
			srchPatern = MyStringCtrl.replace(srchPatern, " ", "*");
		}
		

		// struture externe by name
		structures.addAll(EOStructure.structuresExternesByNameAndSigleAndSiretAndFouCode(edc(), srchPatern, null, null, null,
				null, application().getSearchFetchLimit()));
		// structure interne by name
		structures.addAll(EOStructure.structuresInternesByNameAndSigleAndSiretAndFouCode(edc(), srchPatern, null, null, null,
				null, application().getSearchFetchLimit()));
		
		
		structures = ERXArrayUtilities.arrayWithoutDuplicates(structures);
		
		structures = EOStructure.filtrerLesStructuresNonAffichables(structures, edc(), applicationUser().getPersId());
		
		individus.addAll(EOIndividu.individusWithNameLikeAndFirstNameEquals(edc(), srchPatern, null, application().getSearchFetchLimit()));
		individus.addAll(EOIndividu.individusWithFirstNameLike(edc(), srchPatern, null, application().getSearchFetchLimit()));
		
		
		individus = ERXArrayUtilities.arrayWithoutDuplicates(individus);
		
		resultats.addAll(structures);
		resultats.addAll(individus);
		
		return ERXSortOrdering.sortedArrayUsingKeyOrderArray(resultats, ERXS.ascInsensitives(IPersonne.NOM_PRENOM_RECHERCHE_KEY));
	}

	public WOActionResults doSelection() {
		WOActionResults nextPage = null;
		if (getSelectedPersonne()!=null) {
			if (getSelectedPersonne().isStructure()) {
				nextPage = (EditStructurePage) pageWithName(EditStructurePage.class.getName());
				((EditStructurePage)nextPage).setStructure((EOStructure)getSelectedPersonne());
				((EditStructurePage)nextPage).setResetTabs(true);
			} else {
				nextPage = (EditIndividuPage) pageWithName(EditIndividuPage.class.getName());
				((EditIndividuPage)nextPage).setIndividu((EOIndividu)getSelectedPersonne());
			}
		}
		return nextPage;
	}
	
	public WOActionResults onCloseResultModal() {
		resetComponent();
        return null;
    }

	protected void resetComponent() {
		setSelectedPersonne(null);
		setSrchPatern(null);
		displayGroup().setObjectArray(NSArray.EmptyArray);
	}

	public String onFormSrchObserverComplete() {
//		return "function(oC){" + getGlobalSrchContainerId() + "Update();" + listeResContainerId() + "Update();" + (detailResContainerId() != null ? detailResContainerId() + "Update();" : "") + "}";
		
//		String onSuccessAddition = "openCAW_"+getGlobalSearchResultModalID()+"($('"+getId(context())+"').readAttribute('title')";
		
//		return "function(oC){" + getGlobalSrchContainerId() + "Update();" + "openCAW_" + getGlobalSearchResultModalID() + "();" + "}";
		return "function(){" + "openCAW_" + getGlobalSearchResultModalID() + "();" + "}";

	}

	public String onFormSrchObserverSuccess() {
		return "function(oC){" + "openCAW_" + getGlobalSearchResultModalID() + "();" + "return false;"+ "}";
	}

}