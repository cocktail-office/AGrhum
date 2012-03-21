package org.cocktail.agrhum.serveur.components.controlers;

import org.cocktail.agrhum.serveur.components.EditParametres;
import org.cocktail.fwkcktldroitsutils.common.util.LRLogger;
import org.cocktail.fwkcktldroitsutils.common.util.MyStringCtrl;
import org.cocktail.fwkcktlpersonne.common.eospecificites.EOStructureForFournisseurSpec;
import org.cocktail.fwkcktlpersonne.common.metier.AFinder;
import org.cocktail.fwkcktlpersonne.common.metier.EOIndividu;
import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;
import org.cocktail.fwkcktlpersonneguiajax.serveur.components.AComponent;
import org.cocktail.fwkcktlpersonneguiajax.serveur.controleurs.PersonneSrchCtrl;

import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.eof.ERXQ;

public class ParamPersSrchCtrl extends PersonneSrchCtrl {
	
	public ParamPersSrchCtrl() {
		super();
	}
	
	public ParamPersSrchCtrl(AComponent component) {
		super(component);
	}
	
	
	
	
	
	
	/**
	 * @return Le résultat de la recherche. Methode a surcharger si vous voulez specialiser la recherche.
	 */
	public NSArray getResultats() {
		NSMutableArray res = new NSMutableArray();
		
		if ( isRechercheMoraleInterne() || isRechercheMoraleExterne() ){
			System.out.println("Recherche dans les structures");
//			EOQualifier qualStructures = new EOAndQualifier(new NSArray(new Object[] {
//					EOStructure.QUAL_STRUCTURES_VALIDE, //EOStructure.QUAL_STRUCTURES_TYPE_AUTRES,
//					ERXQ.likeInsensitive(EOStructure.LL_STRUCTURE_KEY, getMyComponent().getSrchNom())
//
//			}));
//			res.addObjectsFromArray(EOStructure.fetchAll(edc(), qualStructures, new NSArray<EOSortOrdering>(EOStructure.SORT_LL_STRUCTURE_ASC)));
			
			res.addObjectsFromArray(EOStructure.structuresByNameAndSigle(edc(), getMyComponent().getSrchNom(), getMyComponent().getSrchSiret(), null, 20));
		}
		
		if ( isRechercheIndividuInterne() || isRechercheIndividuExterne() ){
//			System.out.println("Recherche dans les individus");
			EOQualifier qualIndividu = ERXQ.likeInsensitive(EOIndividu.PERS_ID_KEY, getMyComponent().getSrchNom());
			res.addObjectsFromArray(EOIndividu.fetchAllValides(edc(), qualIndividu, null));
		}
		
	

		return res.immutableClone();
	}

}
