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

import org.cocktail.fwkcktlwebapp.server.version.A_CktlVersion;
import org.cocktail.fwkcktlwebapp.server.version.CktlVersionJava;
import org.cocktail.fwkcktlwebapp.server.version.CktlVersionOracleServer;
import org.cocktail.fwkcktlwebapp.server.version.CktlVersionWebObjects;


/**
 * Gestion du versioning au runtime. 
 * Cette classe n'a pas trop lieu d'être avec un build maven, puisque les dépendances
 * sont définies dans le pom et embarquées dans le woa.
 * On a donc la garantie que les versions utilisés sont les bonnes.
 * 
 */
public final class Version extends A_CktlVersion {
    // Nom de l'appli
    public static final String APPLICATIONFINALNAME = VersionMe.APPLICATIONFINALNAME;
    public static final String APPLICATIONINTERNALNAME=VersionMe.APPLICATIONINTERNALNAME;
    public static final String APPLICATION_STRID = VersionMe.APPLICATION_STRID;

    // Version appli
    public static final long SERIALVERSIONUID = VersionMe.SERIALVERSIONUID;

    public static final int VERSIONNUMMAJ =     VersionMe.VERSIONNUMMAJ;
    public static final int VERSIONNUMMIN =     VersionMe.VERSIONNUMMIN;
    public static final int VERSIONNUMPATCH =   VersionMe.VERSIONNUMPATCH;
    public static final int VERSIONNUMBUILD =   VersionMe.VERSIONNUMBUILD;

    public static final String VERSIONDATE = VersionMe.VERSIONDATE;
    public static final String COMMENT = VersionMe.COMMENT;

    /** Version de WebObjects */
    private static final String WO_VERSION_MIN = "5.3.3.0";
    private static final String WO_VERSION_MAX = null;

    /** Version du JRE */
    private static final String JRE_VERSION_MIN = "1.5.0.0";
    private static final String JRE_VERSION_MAX = "1.6";

    /** Version d'ORACLE */
    private static final String ORACLE_VERSION_MIN = "9.0";
    private static final String ORACLE_VERSION_MAX = null;

    /** Version de WONDER */
    // TODO si build maven, trouver le moyen d'injecter la version
    // dans l'Info.plist
    private static final String WONDER_VERSION_MIN = "5.0";
    private static final String WONDER_VERSION_MAX = null;

    /** Version du frmk FwkCktlWebApp */
    private static final String FWKCKTLWEBAPP_VERSION_MIN = "4.0.0";
    private static final String FWKCKTLWEBAPP_VERSION_MAX = null;
    
    /** Version du frmk FwkCktlDroitsUtils */
    private static final String FWKCKTLDROITSUTILS_VERSION_MIN = "1.0.0";
    private static final String FWKCKTLDROITSUTILS_VERSION_MAX = null;
    
    /** Version du frmk FwkCktlAjax */
    private static final String FWKCKTLAJAX_VERSION_MIN = "1.0.0";
    private static final String FWKCKTLAJAX_VERSION_MAX = null;

    /** Version du frmk FwkCktlGRH */
    private static final String FWKCKTLGRH_VERSION_MIN = "1.0.0";
    private static final String FWKCKTLGRH_VERSION_MAX = null;
    
    /** Version du frmk FwkCktlGRHGuiAjax */
    private static final String FWKCKTLGRHGUIAJAX_VERSION_MIN = "1.0.0";
    private static final String FWKCKTLGRHGUIAJAX_VERSION_MAX = null;
    
    /** Version du frmk cocowork2 */
    private static final String COCOWORK2_VERSION_MIN = "1.0.0";
    private static final String COCOWORK2_VERSION_MAX = null;
    
    /** Version du frmk FwkCktlGSpot */
    private static final String FWKCKTLGSPOT_VERSION_MIN = "1.1.2.3";
    private static final String FWKCKTLGSPOT_VERSION_MAX = null;
    
    /** Version du frmk FwkCktlGSpotGuiAjax */
    private static final String FWKCKTLGSPOTGUIAJAX_VERSION_MIN = "1.1.2.3";
    private static final String FWKCKTLGSPOTGUIAJAX_VERSION_MAX = null;
    
    /** Version du frmk FwkCktlPersonne */
    private static final String FWKCKTLPERSONNE_VERSION_MIN = "1.1.0.0";
    private static final String FWKCKTLPERSONNE_VERSION_MAX = null;
    
    /** Version du frmk FwkCktlPersonneGuiAjax */
    private static final String FWKCKTLPERSONNEGUIAJAX_VERSION_MIN = "1.1.0.0";
    private static final String FWKCKTLPERSONNEGUIAJAX_VERSION_MAX = null;
    
    /** Version de BDD */
    private static final String BD_VERSION_MIN = "1.7.0.0";
    private static final String BD_VERSION_MAX = null;
    
    public String name() {
        return APPLICATIONFINALNAME;
    }

    public String internalName() {
        return APPLICATIONINTERNALNAME;
    }

    public int versionNumBuild() {
        return VERSIONNUMBUILD;
    }

    public int versionNumMaj() {
        return VERSIONNUMMAJ;
    }

    public int versionNumMin() {
        return VERSIONNUMMIN;
    }

    public int versionNumPatch() {
        return VERSIONNUMPATCH;
    }

    public String date() {
        return VERSIONDATE;
    }

    public String comment() {
        return COMMENT;
    }


    /**
     * Liste des d̩ependances 
     * @see org.cocktail.fwkcktlwebapp.server.version.A_CktlVersion#dependencies()
     */
    public CktlVersionRequirements[] dependencies() {
        return new CktlVersionRequirements[]{
                // TODO : faire marcher la verif des versions avec les fmk jar !
                new CktlVersionRequirements(new CktlVersionWebObjects(), WO_VERSION_MIN, WO_VERSION_MAX, true),
                new CktlVersionRequirements(new CktlVersionJava() , JRE_VERSION_MIN, JRE_VERSION_MAX, true)
                ,new CktlVersionRequirements(new CktlVersionOracleServer(), ORACLE_VERSION_MIN, ORACLE_VERSION_MAX, false)
                ,new CktlVersionRequirements(new VersionDatabase(), BD_VERSION_MIN, BD_VERSION_MAX, true)
                , new CktlVersionRequirements(new org.cocktail.fwkcktlgrh.server.Version(), 
                        FWKCKTLGRH_VERSION_MIN, FWKCKTLGRH_VERSION_MAX, true)
                , new CktlVersionRequirements(new org.cocktail.fwkcktlgrhguiajax.server.Version(), 
                        FWKCKTLGRHGUIAJAX_VERSION_MIN, FWKCKTLGRHGUIAJAX_VERSION_MAX, true)
                , new CktlVersionRequirements(new org.cocktail.fwkcktlpersonne.server.Version(), 
                        FWKCKTLPERSONNE_VERSION_MIN, FWKCKTLPERSONNE_VERSION_MAX, true)
                , new CktlVersionRequirements(new org.cocktail.fwkcktlpersonneguiajax.serveur.Version(), 
                        FWKCKTLPERSONNEGUIAJAX_VERSION_MIN, FWKCKTLPERSONNEGUIAJAX_VERSION_MAX, true)
                , new CktlVersionRequirements(new org.cocktail.fwkcktlajaxwebext.serveur.Version(), 
                        FWKCKTLAJAX_VERSION_MIN, FWKCKTLAJAX_VERSION_MAX, true)
                , new CktlVersionRequirements(new org.cocktail.fwkcktlwebapp.server.version.Version(), 
                        FWKCKTLWEBAPP_VERSION_MIN, FWKCKTLWEBAPP_VERSION_MAX, true)
                , new CktlVersionRequirements(new org.cocktail.fwkcktldroitsutils.server.Version(), 
                        FWKCKTLDROITSUTILS_VERSION_MIN, FWKCKTLDROITSUTILS_VERSION_MAX, true)
                , new CktlVersionRequirements(new org.cocktail.fwkgspot.serveur.Version(), 
                        FWKCKTLGSPOT_VERSION_MIN, FWKCKTLGSPOT_VERSION_MAX, true)
                , new CktlVersionRequirements(new org.cocktail.fwkgspotguiajax.serveur.Version(), 
                        FWKCKTLGSPOTGUIAJAX_VERSION_MIN, FWKCKTLGSPOTGUIAJAX_VERSION_MAX, true)
        };
    }



}
