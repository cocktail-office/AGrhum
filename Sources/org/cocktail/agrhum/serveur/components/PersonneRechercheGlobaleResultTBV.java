package org.cocktail.agrhum.serveur.components;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

import org.cocktail.agrhum.serveur.components.MyWOComponent;
import org.cocktail.fwkcktlajaxwebext.serveur.component.tableview.column.CktlAjaxTableViewColumn;
import org.cocktail.fwkcktlajaxwebext.serveur.component.tableview.column.CktlAjaxTableViewColumnAssociation;
import org.cocktail.fwkcktlpersonne.common.metier.EOIndividu;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;
import org.cocktail.fwkcktlpersonneguiajax.serveur.components.PersonneTableView;

public class PersonneRechercheGlobaleResultTBV extends MyWOComponent {
	private static final long serialVersionUID = 4791746086005006978L;
	
	private static final String PERS_KEY = "unePersonne.";
	private static final String IND_KEY = "unIndividu.";
	private static final String STR_KEY = "uneStructure.";
	
	public static final String COL_PERSID_KEY = IPersonne.PERSID_KEY;
	
	public static final String BINDING_colonnesKeys = "colonnesKeys";
	public static final String BINDING_showDetailInModalBoxId = "showDetailInModalBoxId";

	private NSMutableDictionary _colonnesMap;
	private NSArray colonnes;
	
	private IPersonne unePersonne;
	
	public PersonneRechercheGlobaleResultTBV(WOContext context) {
        super(context);
    }
   

	/**
	 * @return the _colonnesMap
	 */
	public NSMutableDictionary getColonnesMap() {
		if (_colonnesMap == null) {
			_colonnesMap = new NSMutableDictionary();
			CktlAjaxTableViewColumn col1 = new CktlAjaxTableViewColumn();
			col1.setLibelle("PersId");
			col1.setOrderKeyPath(COL_PERSID_KEY);
			//		CktlAjaxTableViewColumnAssociation ass1 = new CktlAjaxTableViewColumnAssociation(IND_KEY+COL_NUMERO_KEY, " ");
			CktlAjaxTableViewColumnAssociation ass1 = new CktlAjaxTableViewColumnAssociation(PERS_KEY + COL_PERSID_KEY, " ");
			col1.setAssociations(ass1);
			_colonnesMap.takeValueForKey(col1, COL_PERSID_KEY);
			
			_colonnesMap.addEntriesFromDictionary(PersonneTableView._colonnesMap);
			col1 = (CktlAjaxTableViewColumn) _colonnesMap.valueForKey(PersonneTableView.COL_SIRET_KEY);
			col1.setOrderKeyPath(null);
		}
		return _colonnesMap;
	}
	
	
    public NSArray getColonnes() {
		if (colonnes == null) {
			NSMutableArray res = new NSMutableArray();
			NSArray colkeys = getColonnesKeys();
			for (int i = 0; i < colkeys.count(); i++) {
				res.addObject(getColonnesMap().valueForKey((String) colkeys.objectAtIndex(i)));
			}
			colonnes = res.immutableClone();
		}
		return colonnes;
	}

	public NSArray getColonnesKeys() {
		NSArray keys = PersonneTableView.DEFAULT_COLONNES_KEYS;
		if (hasBinding(BINDING_colonnesKeys)) {
			String keysStr = (String) valueForBinding(BINDING_colonnesKeys);
			keys = NSArray.componentsSeparatedByString(keysStr, ",");
		}
		if (hasBinding(BINDING_showDetailInModalBoxId) && valueForBinding(BINDING_showDetailInModalBoxId) != null) {
			keys = new NSArray(PersonneTableView.COL_DETAIL_KEY).arrayByAddingObjectsFromArray(keys);
		}
		return keys;
	}

	public IPersonne unePersonne() {
		return unePersonne;
	}
	
	public void setUnePersonne(IPersonne unePersonne) {
		this.unePersonne = unePersonne;
	}

	public EOIndividu unIndividu() {
		return (unePersonne.isIndividu() ? (EOIndividu) unePersonne : null);
	}

	public EOStructure uneStructure() {
		return (unePersonne.isStructure() ? (EOStructure) unePersonne : null);
	}

}