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

import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.cocktail.agrhum.serveur.metier.CktlConfigurationException;
import org.cocktail.fwkcktlajaxwebext.serveur.CocktailAjaxApplication;
import org.cocktail.fwkcktldroitsutils.common.util.MyStringCtrl;
import org.cocktail.fwkcktlpersonne.common.FwkCktlPersonne;
import org.cocktail.fwkcktlpersonne.common.FwkCktlPersonneParamManager;
import org.cocktail.fwkcktlpersonne.common.eospecificites.EOStructureForGroupeSpec;
import org.cocktail.fwkcktlpersonne.common.metier.EOGrhumParametres;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.server.CktlConfig;
import org.cocktail.fwkcktlwebapp.server.CktlMailBus;
import org.cocktail.fwkcktlwebapp.server.util.EOModelCtrl;
import org.cocktail.fwkcktlwebapp.server.version.A_CktlVersion;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOMessage;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSNumberFormatter;
import com.webobjects.foundation.NSPropertyListSerialization;
import com.webobjects.foundation.NSSet;
import com.webobjects.foundation.NSTimeZone;
import com.webobjects.foundation.NSTimestampFormatter;

import er.extensions.appserver.ERXApplication;
import er.extensions.appserver.ERXMessageEncoding;
import er.extensions.appserver.ERXStaticResourceRequestHandler;
import er.extensions.eof.ERXEC;
import er.extensions.foundation.ERXProperties;

public class Application extends CocktailAjaxApplication {

    public static final String TYPE_APP_STR = "AGRHUM"; // ID de l'application
    // dans JefyAdmin
    private static final String CONFIG_FILE_NAME = VersionMe.APPLICATIONINTERNALNAME
    + ".config";
    private static final String CONFIG_TABLE_NAME = "FwkCktlWebApp_GrhumParametres";
    private static final String MAIN_MODEL_NAME = "FwkCktlPersonne";
    public static final String CONFIG_AGRHUM_MENUS_EXCLUS_KEY = "AGRHUM_MENUS_EXCLUS";
    public static final String CONFIG_AGRHUM_RESULTAT_RECHERCHE_LIMITE_KEY = "AGRHUM_RESULTAT_RECHERCHE_LIMITE";
    public static final int DEFAULT_SEARCH_FETCHLIMIT = 20;
    public static final String CONFIG_C_STRUCTURE_LIST_AGRHUM_USERS_KEY = "C_STRUCTURE_LIST_AGRHUM_USERS";


    private final Logger logger = ERXApplication.log;

    public static final String DEFAULT_TIMESTAMP_FORMATTER = "%d/%m/%Y à %Hh%M";

    /**
     * Liste des parametres obligatoires (dans fichier de config ou table
     * grhum_parametres) pour que l'application se lance. Si un des parametre
     * n'est pas initialise, il y a une erreur bloquante.
     */
    public static final String[] MANDATORY_PARAMS = new String[] { "HOST_MAIL",
    "ADMIN_MAIL" };

    /**
     * Liste des parametres optionnels (dans fichier de config ou table
     * grhum_parametres). Si un des parametre n'est pas initialise, il y a un
     * warning.
     */
    public static final String[] OPTIONAL_PARAMS = new String[] {};

    /**
     * boolean qui indique si on se trouve en mode developpement ou non. Permet
     * de desactiver l'envoi de mail lors d'une exception par exemple
     */
    public static boolean isModeDebug = false;

    public static Application app(){
    	return (Application) application();
    }
    
    private Version _appVersion;

    public static NSTimeZone ntz = null;
    /**
     * Formatteur a deux decimales e utiliser pour les donnees numeriques non
     * monetaires.
     */
    public NSNumberFormatter app2DecimalesFormatter;
    /**
     * Formatteur a 5 decimales a utiliser pour les pourcentages dans la
     * repartition.
     */
    public NSNumberFormatter app5DecimalesFormatter;

    /**
     * Formatteur de dates.
     */
    public NSTimestampFormatter appDateFormatter;
    
    /**
     * Flag précisant si la gestion des photos est prise en compte par l'application. Voir le paramètre GRHUM_PHOTO.
     */
    private Boolean isPhotoEnabled;
    
    /**
     * Limite utilisée pour les fetchs effectués dans les recherches sur le referentiel
     */
	private Integer configFetchLimitInteger;
	
	/**
	 * Liste des c_structure des groupes dont les membres ont le droit d'accéder à AGrhum.
	 * cf {@link Application#CONFIG_C_STRUCTURE_LIST_AGRHUM_USERS_KEY}
	 */
	private NSSet<String> cStructuresAccesGrhum;

    /**
     * Liste des emails des utilisateurs connectes.
     */
    private static NSMutableArray utilisateurs; // Liste des emails des utilisateurs connectes

    public static void main(String[] argv) {
        ERXApplication.main(argv, Application.class);
    }

    public Application() {
        super();
        setAllowsConcurrentRequestHandling(false);
        setDefaultRequestHandler(requestHandlerForKey(directActionRequestHandlerKey()));
        WOMessage.setDefaultEncoding("UTF-8");
        WOMessage.setDefaultURLEncoding("UTF-8");
        ERXMessageEncoding.setDefaultEncoding("UTF8");
        ERXMessageEncoding.setDefaultEncodingForAllLanguages("UTF8");
        utilisateurs = new NSMutableArray();
        ERXEC.setDefaultFetchTimestampLag(2000);
        logger.info("Bienvenue sur AGhrum");
        if (isDirectConnectEnabled()) {
            registerRequestHandler(new ERXStaticResourceRequestHandler(), "_wr_");
        }
        // Calcul des groupes dynamiques par AGrhum...
        if (ERXProperties.booleanForKeyWithDefault("ENABLE_NEW_GD", false)) {
        	FwkCktlPersonne.launchCalculGroupesDynamiquesTask();
        }
    }

    @Override
    public A_CktlVersion appCktlVersion() {
        return new Version();
    }
    
    @Override
    public A_CktlVersion appCktlVersionDb() {
        return new VersionDatabase();
    }
    
    public void initApplication() {
        System.out.println("Lancement de l'application serveur " + this.name()
                + "...");
        super.initApplication();

        // Afficher les infos de connexion des modeles de donnees
        rawLogModelInfos();
        // Verifier la coherence des dictionnaires de connexion des modeles de
        // donnees
        boolean isInitialisationErreur = !checkModel();

        isModeDebug = config().booleanForKey("MODE_DEBUG");
    }

    /**
     * Execute les operations au demarrage de l'application, juste apres
     * l'initialisation standard de l'application.
     */
    public void startRunning() {
        initFormatters();
        initTimeZones();
        this.appDateFormatter = new NSTimestampFormatter();
        this.appDateFormatter.setPattern("%d/%m/%Y");

        EOStructureForGroupeSpec.getGroupeDefaut(ERXEC.newEditingContext());
        
        // Prefetch dans le sharedEditingContext des nomenclatures communes a
        // toute l'appli
        /**
         * Prefetch dans le sharedEditingContext des nomenclatures communes a
         * toute l'appli Il est necessaire de declarer dans l'eomodel, l'entite
         * a prefetecher via l'inspecteur: 'Share all objects' --> creation d'un
         * fetchspecificationnamed 'FetchAll' Il est indispensable d'utiliser
         * l'api 'bindObjectsWithFetchSpecification'
         */

        // EOSharedEditingContext sedc =
        // EOSharedEditingContext.defaultSharedEditingContext();
        // EOFetchSpecification fetchSpec =
        // EOFetchSpecification.fetchSpecificationNamed("FetchAll",
        // TypeClassificationContrat.ENTITY_NAME);
        // sedc.bindObjectsWithFetchSpecification(fetchSpec, "FetchAll");
    }

    public Logger logger() {
        return logger;
    }

    public String configFileName() {
        return CONFIG_FILE_NAME;
    }

    public String configTableName() {
        return CONFIG_TABLE_NAME;
    }

    public String[] configMandatoryKeys() {
        return MANDATORY_PARAMS;
    }

    public String[] configOptionalKeys() {
        return OPTIONAL_PARAMS;
    }

    @Override
    public boolean appShouldSendCollecte() {
        return !ERXApplication.isDevelopmentModeSafe();
    }

    public String copyright() {
        return appVersion().copyright();
    }

    public A_CktlVersion appA_CktlVersion() {
        return appVersion();
    }

    public Version appVersion() {
        if (_appVersion == null) {
            _appVersion = new Version();
        }
        return _appVersion;
    }

    public String mainModelName() {
        return MAIN_MODEL_NAME;
    }

    public void initFormatters() {
        this.app2DecimalesFormatter = new NSNumberFormatter();
        this.app2DecimalesFormatter.setDecimalSeparator(",");
        this.app2DecimalesFormatter.setThousandSeparator(" ");

        this.app2DecimalesFormatter.setHasThousandSeparators(true);
        this.app2DecimalesFormatter.setPattern("#,##0.00;0,00;-#,##0.00");

        this.app5DecimalesFormatter = new NSNumberFormatter();
        this.app5DecimalesFormatter.setDecimalSeparator(",");
        this.app5DecimalesFormatter.setThousandSeparator(" ");

        this.app5DecimalesFormatter.setHasThousandSeparators(true);
        this.app5DecimalesFormatter.setPattern("##0.00000;0,00000;-##0.00000");
    }

    public NSNumberFormatter app2DecimalesFormatter() {
        return this.app2DecimalesFormatter;
    }

    public NSNumberFormatter getApp5DecimalesFormatter() {
        return this.app5DecimalesFormatter;
    }

    /**
     * Initialise le TimeZone a utiliser pour l'application.
     */
    protected void initTimeZones() {
        logger().info("Initialisation du NSTimeZone");
        String tz = config().stringForKey("DEFAULT_NS_TIMEZONE");
        if (tz == null || tz.equals("")) {
            CktlLog
            .log("Le parametre DEFAULT_NS_TIMEZONE n'est pas defini dans le fichier .config.");
            TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
            NSTimeZone.setDefaultTimeZone(NSTimeZone.timeZoneWithName(
                    "Europe/Paris", false));
        } else {
            ntz = NSTimeZone.timeZoneWithName(tz, false);
            if (ntz == null) {
                CktlLog
                .log("Le parametre DEFAULT_NS_TIMEZONE defini dans le fichier .config n'est pas valide ("
                        + tz + ")");
                TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
                NSTimeZone.setDefaultTimeZone(NSTimeZone.timeZoneWithName(
                        "Europe/Paris", false));
            } else {
                TimeZone.setDefault(ntz);
                NSTimeZone.setDefaultTimeZone(ntz);
            }
        }
        ntz = NSTimeZone.defaultTimeZone();
        logger().info("NSTimeZone par defaut utilise dans l'application:"
                + NSTimeZone.defaultTimeZone());
        NSTimestampFormatter ntf = new NSTimestampFormatter();
        logger().info("Les NSTimestampFormatter analyseront les dates avec le NSTimeZone: "
                + ntf.defaultParseTimeZone());
        logger().info("Les NSTimestampFormatter afficheront les dates avec le NSTimeZone: "
                + ntf.defaultFormatTimeZone());
    }

    /**
     * Retourne le mot de passe du super-administrateur. Il permet de se
     * connecter a l'application avec le nom d'un autre utilisateur
     * (l'authentification local et non celle CAS doit etre activee dans ce
     * cas).
     */
    public String getRootPassword() {
        // passpar2
        return "HO4LI8hKZb81k";
    }

    @Override
    public WOResponse handleException(Exception anException, WOContext context) {
        logger().error(anException.getMessage(), anException);
        WOResponse response = null;
        if (context != null && context.hasSession()) {
            Session session = (Session) context.session();
            sendMessageErreur(anException, context, session);
            response = createResponseInContext(context);
            NSMutableDictionary formValues = new NSMutableDictionary();
            formValues.setObjectForKey(session.sessionID(), "wosid");
            String applicationExceptionUrl = context
            .directActionURLForActionNamed("applicationException",
                    formValues);
            response.appendContentString("<script>document.location.href='"
                    + applicationExceptionUrl + "';</script>");
            cleanInvalidEOFState(anException, context);
            return response;
        } else {
            return super.handleException(anException, context);
        }
    }

    private String createMessageErreur(Exception anException, WOContext context, Session session) {
        String contenu;
        // Si c'est une erreur de config, on affiche pas tout le tsoin tsoin,
        // juste une info claire
        if (anException instanceof CktlConfigurationException) {
            contenu = anException.getLocalizedMessage();
        } else if (anException instanceof NSForwardException && ((NSForwardException)anException).originalException() instanceof CktlConfigurationException) {
            Throwable cause = ((NSForwardException)anException).originalException();
            contenu = cause != null ? cause.getLocalizedMessage() : null;
        } else {
            NSDictionary extraInfo = extraInformationForExceptionInContext(
                    anException, context);
            contenu = "Date : "
                + DateCtrl.dateToString(DateCtrl.now(),
                "%d/%m/%Y %H:%M") + "\n";
            contenu += "OS: " + System.getProperty("os.name") + "\n";
            contenu += "Java vm version: "
                + System.getProperty("java.vm.version") + "\n";
            contenu += "WO version: "
                + ERXProperties.webObjectsVersion() + "\n\n";
            contenu += "User agent: "
                + context.request().headerForKey("user-agent")
                + "\n\n";
            contenu += "Utilisateur(Numero individu): "
                + session.applicationUser().getPersonne() + "("
                + session.applicationUser().getNoIndividu() + ")"
                + "\n";

            contenu += "\n\nException : " + "\n";
            if (anException instanceof InvocationTargetException) {
                contenu += getMessage(anException, extraInfo) + "\n";
                anException = (Exception) anException.getCause();
            }
            contenu += getMessage(anException, extraInfo) + "\n";
            contenu += "\n\n";
        }
        return contenu;
    }

    private void sendMessageErreur(Exception anException, WOContext context, Session session) {
        try {
            CktlMailBus cmb = session.mailBus();
            String smtpServeur = config().stringForKey(CktlConfig.CONFIG_GRHUM_HOST_MAIL_KEY);
            String destinataires = config().stringForKey("ADMIN_MAIL");

            if (cmb != null && smtpServeur != null
                    && smtpServeur.equals("") == false
                    && destinataires != null
                    && destinataires.equals("") == false) {
                String objet = "[AGRHUM]:Exception:[";
                objet += VersionMe.txtAppliVersion() + "]";
                String contenu = createMessageErreur(anException, context, session);
//                session.setMessageErreur(contenu);
                session.setGeneralErrorMessage(contenu);
//                session.addMessage(new CktlUIErrorMessage("Une erreur critique est survenue", contenu, anException));
                boolean retour = false;
                if (isModeDebug) {
                    logger().info("!!!!!!!!!!!!!!!!!!!!!!!! MODE DEVELOPPEMENT : pas de mail !!!!!!!!!!!!!!!!");
                    retour = false;
                } else {
                    retour = cmb.sendMail(destinataires, destinataires,
                            null, objet, contenu);
                }
                if (!retour) {
                    logger().warn("!!!!!!!!!!!!!!!!!!!!!!!! IMPOSSIBLE d'ENVOYER le mail d'exception !!!!!!!!!!!!!!!!");
                    logger().warn("\nMail:\n\n" + contenu);

                }
            } else {
                logger().warn("!!!!!!!!!!!!!!!!!!!!!!!! IMPOSSIBLE d'ENVOYER le mail d'exception !!!!!!!!!!!!!!!!");
                logger().warn("Veuillez verifier que les parametres HOST_MAIL et ADMIN_MAIL sont bien renseignes");
                logger().warn("HOST_MAIL = " + smtpServeur);
                logger().warn("ADMIN_MAIL = " + destinataires);
                logger().warn("cmb = " + cmb);
                logger().warn("\n\n\n");
            }
        } catch (Exception e) {
            logger().error("\n\n\n");
            logger().error("!!!!!!!!!!!!!!!!!!!!!!!! Exception durant le traitement d'une autre exception !!!!!!!!!!!!!!!!");
            logger().error(e.getMessage(), e);
            super.handleException(e, context);
            logger().error("\n");
            logger().error("Message Exception originale: "
                    + anException.getMessage());
            logger().error("Stack Exception dans exception: "
                    + anException.getStackTrace());
        }

    }

    private void cleanInvalidEOFState(Exception e, WOContext ctx) {
        if (e instanceof IllegalStateException || e instanceof EOGeneralAdaptorException) {
            ctx.session().defaultEditingContext().invalidateAllObjects();
        }
    }

    protected String getMessage(Throwable e, NSDictionary extraInfo) {
        String message = "";
        if (e != null) {
            message = stackTraceToString(e, false) + "\n\n";
            message += "Info extra :\n";
            if (extraInfo != null) {
                message += NSPropertyListSerialization
                .stringFromPropertyList(extraInfo)
                + "\n\n";
            }
        }
        return message;
    }

    /**
     * permet de recuperer la trace d'une exception au format string message +
     * trace
     * 
     * @param e
     * @return
     */
    public static String stackTraceToString(Throwable e, boolean useHtml) {
        String tagCR = "\n";
        if (useHtml) {
            tagCR = "<br>";
        }
        String stackStr = e + tagCR + tagCR;
        StackTraceElement[] stack = e.getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            stackStr += (stack[i]).toString() + tagCR;
        }
        return stackStr;
    }

    //    public NSDictionary databaseContextShouldUpdateCurrentSnapshot(
    //            EODatabaseContext dbCtxt, NSDictionary dic, NSDictionary dic2,
    //            EOGlobalID gid, EODatabaseChannel dbChannel) {
    //        return dic2;
    //    }

    public boolean _isSupportedDevelopmentPlatform() {
        return (super._isSupportedDevelopmentPlatform() || System.getProperty(
        "os.name").startsWith("Win"));
    }

    @Override
    public WOResponse handleSessionRestorationErrorInContext(WOContext context) {
        WOResponse response;
        response = createResponseInContext(context);
        String sessionExpiredUrl = context.directActionURLForActionNamed(
                "sessionExpired", null);
        response.appendContentString("<script>document.location.href='"
                + sessionExpiredUrl + "';</script>");

        return response;
    }

    public NSMutableArray utilisateurs() {
        return utilisateurs;
    }

    public static String serverBDId() {
        NSMutableArray<String> serverDBIds = new NSMutableArray<String>();
        final NSMutableDictionary mdlsDico = EOModelCtrl.getModelsDico();
        final Enumeration mdls = mdlsDico.keyEnumerator();
        while (mdls.hasMoreElements()) {
            final String mdlName = (String) mdls.nextElement();
            String serverDBId = EOModelCtrl
            .bdConnexionServerId((EOModel) mdlsDico
                    .objectForKey(mdlName));
            if (!serverDBIds.containsObject(serverDBId)) {
                serverDBIds.addObject(serverDBId);
            }
        }
        return serverDBIds.componentsJoinedByString(",");
    }

    public String getDefaultTimestampFormatter() {
        return DEFAULT_TIMESTAMP_FORMATTER;
    }

	public boolean isMenuItemHidden(String menuKey) {
		boolean isHidden = false;
		if (!MyStringCtrl.isEmpty(menuKey)) {
			String menuExclusListeParam = config().stringForKey(CONFIG_AGRHUM_MENUS_EXCLUS_KEY);
			if (!MyStringCtrl.isEmpty(menuExclusListeParam)) {
				NSSet<String> menusExclus = new NSSet<String>(menuExclusListeParam.split(","));
				isHidden = menusExclus.contains(menuKey);
			}
		}
		return isHidden;
	}

	/**
	 * @return the isPhotoEnabled
	 */
	public boolean isPhotoEnabled() {
		if (isPhotoEnabled==null) {
			String paramPhoto = (String)config().get(EOGrhumParametres.PARAM_GRHUM_PHOTO);
			if (MyStringCtrl.isEmpty(paramPhoto)) {
				paramPhoto = "NON";
			}
			setIsPhotoEnabled(Boolean.valueOf(paramPhoto.startsWith("OUI")));
		}
		return isPhotoEnabled.booleanValue();
	}

	/**
	 * @param isPhotoEnabled the isPhotoEnabled to set
	 */
	private void setIsPhotoEnabled(Boolean isPhotoEnabled) {
		this.isPhotoEnabled = isPhotoEnabled;
	}

	/**
	 * @return la valeur du fetch limit à appliquer aux recherches de personnes
	 */
	public int getSearchFetchLimit() {
		if (configFetchLimitInteger==null) {
			configFetchLimitInteger = Integer.valueOf(DEFAULT_SEARCH_FETCHLIMIT);
			if (config().stringForKey(CONFIG_AGRHUM_RESULTAT_RECHERCHE_LIMITE_KEY)!=null) {
				try {
					configFetchLimitInteger = Integer.valueOf(config().stringForKey(CONFIG_AGRHUM_RESULTAT_RECHERCHE_LIMITE_KEY));
				} catch (NumberFormatException e) {
					configFetchLimitInteger = Integer.valueOf(DEFAULT_SEARCH_FETCHLIMIT);
					logger().error("\n\n\n");
		            logger().error("Exception durant la configuration du fetch limit de la recherche à partir du paramètre "+CONFIG_AGRHUM_RESULTAT_RECHERCHE_LIMITE_KEY 
		            		+ "La valeur par défaut : "+ DEFAULT_SEARCH_FETCHLIMIT+" est appliquée.");
		            logger().error(e.getMessage(), e);
		            logger().error("\n\n\n");
				}
			}
		}
		return configFetchLimitInteger.intValue();
	}

	/**
	 * @return the cStructuresAccesGrhum : liste des cStructures dont les membres ont accés à AGrhum
	 */
	public NSSet<String> getCStructuresAccesGrhum() {
		if (cStructuresAccesGrhum==null) {
			String cStructuresListeParam = config().stringForKey(CONFIG_C_STRUCTURE_LIST_AGRHUM_USERS_KEY);
			if (!MyStringCtrl.isEmpty(cStructuresListeParam)) {
				cStructuresAccesGrhum = new NSSet<String>(cStructuresListeParam.split(","));
			}
		}
		return cStructuresAccesGrhum;
	}
	
	@Override
	public void finishInitialization() {
		super.finishInitialization();
		
		// Il y a ici des créations de fiche de personne donc pour éviter les ennuis, nous avons désactivé les tests de cohérence sur le N° INSEE
		FwkCktlPersonneParamManager.setParamValue(FwkCktlPersonneParamManager.INDIVIDU_CHECK_COHERENCE_INSEE_DISABLED, "OUI");
	}
	
}
