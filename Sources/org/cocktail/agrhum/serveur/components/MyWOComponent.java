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
import org.cocktail.agrhum.serveur.AGrhumApplicationUser;
import org.cocktail.agrhum.serveur.AGrhumHelpers;
import org.cocktail.agrhum.serveur.Application;
import org.cocktail.agrhum.serveur.Session;
import org.cocktail.fwkcktlajaxwebext.serveur.CktlAjaxWOComponent;
import org.cocktail.fwkcktlajaxwebext.serveur.CktlResourceProvider;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

import er.extensions.appserver.ERXWOContext;
import er.extensions.localization.ERXLocalizer;

public class MyWOComponent extends CktlAjaxWOComponent implements CktlResourceProvider {

    private static final long serialVersionUID = -4720501391366549771L;
	
    private String onloadJS;
    private String _id;

	public MyWOComponent(WOContext context) {
		super(context);
	}

    public Session session() {
    	return (Session)super.session();
    }

    public Application application() {
        return (Application)super.application();
    }
    
    public ERXLocalizer localizer() {
    	return session().localizer();
    }
    
    public AGrhumApplicationUser applicationUser() {
        return session().applicationUser();
    }
    
	/**
	 * @return the onloadJS
	 */
	public String onloadJS() {
		return onloadJS;
	}

	/**
	 * @param onloadJS the onloadJS to set
	 */
	public void setOnloadJS(String onloadJS) {
		this.onloadJS = onloadJS;
	}

	public Logger logger() {
		return application().logger();
	}
	
	protected String uniqueDomId() {
	    if (_id == null)
	        _id = ERXWOContext.safeIdentifierName(context(), true);
	    return _id;
	}

    public void injectResources(WOResponse response, WOContext context) {
        AGrhumHelpers.insertStylesSheet(context, response);
    }
	
}
