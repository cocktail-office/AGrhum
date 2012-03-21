package org.cocktail.agrhum.serveur.components;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.cocktail.agrhum.serveur.metier.TicketService;
import org.cocktail.fwkcktldroitsutils.common.util.MyDateCtrl;
import org.cocktail.fwkcktlpersonne.common.metier.EOCompte;
import org.cocktail.sync.ref.SynchroComptePersonne;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSValidation.ValidationException;

public class EditTicket extends MyWOComponent {

    private static final long serialVersionUID = 4688306682754203352L;
    private static final Calendar CAL = new GregorianCalendar();
    private static final Logger LOG = Logger.getLogger(EditTicket.class);
    private static final String BINDING_EDC = "editingContext";
    private static final String BINDING_DID_CANCEL = "didCancel";
    private static final String BINDING_WILL_REFRESH = "willRefresh";
    private static final String BINDING_DID_CREATE = "didCreate";
    
    private static final String BINDING_IS_CREATION = "isCreation";
    
    private static final String FIN_J = "FIN_J";
    private static final String DEMAIN = "Demain";
    private static final String FIN_SEM = "FIN_SEM";
    private static final String UN_MOIS = "1Mois";
    private static final String SIX_MOIS = "6Mois";
    private static final String FIN_ANNEE_U = "FIN_ANNEE_U";
    
    private static final String TICKET_BDG = "ticket";
    private String expiration = FIN_J;

    private String motDePasse;
    private String motDePasseBis;
    private String cptLoginCreation;
    private NSTimestamp cptDebutValideCreation;
    private NSTimestamp cptFinValideCreation;
    
    private boolean editMode;
	private boolean creationMode;
	private EOCompte ticket;
    
	public EditTicket(WOContext context) {
        super(context);
    }

    private EOEditingContext editingContext() {
        return (EOEditingContext)valueForBinding(BINDING_EDC);
    }
    
    @Override
    public void appendToResponse(WOResponse response, WOContext context) {
    	if (hasBinding(BINDING_WILL_REFRESH)) {
			if (booleanValueForBinding(BINDING_WILL_REFRESH, false)) {
				resetTicket();
			}
			if (canSetValueForBinding(BINDING_WILL_REFRESH)) {
				setValueForBinding(Boolean.FALSE, BINDING_WILL_REFRESH);
			}
		}
    	super.appendToResponse(response, context);
    }

    private void resetTicket() {
		ticket = null;
		setCptLoginCreation(null);
		setMotDePasse(null);
		setMotDePasseBis(null);
		setCptDebutValideCreation(null);
		setCptFinValideCreation(null);
		setCptDebutValideCreation(null);
		setCptFinValideCreation(null);
		setExpiration(FIN_J);
	}

	public WOActionResults editer() {
        setEditMode(true);
        setMotDePasse(null);
        setMotDePasseBis(null);
        return null;
    }
    
    public WOActionResults annuler() {
        editingContext().revert();
        setMotDePasse(null);
        setMotDePasseBis(null);
        setEditMode(false);
        setCreationMode(false);
        if (hasBinding(BINDING_DID_CANCEL))
            return (WOActionResults)valueForBinding(BINDING_DID_CANCEL);
        else
            return null;
    }
    
    public WOActionResults creer() {
    	WOActionResults result = null;
        if (motDePasse != null && !motDePasse.equals(motDePasseBis)) {
            session().addSimpleErrorMessage("Erreur lors de la création du ticket", "Les mots de passes saisies ne sont pas identiques");
            return null;
        } else {
        	if (!isCreation()) {
    			try {
    				ticket().changePassword(session().applicationUser(),motDePasse, true);
    			} catch (Exception e1) {
    	            LOG.error(e1.getMessage(), e1);
    	            context().response().setStatus(500);
    	            session().addSimpleErrorMessage(e1.getMessage(), e1);
    	            // return null;  // TODO: decommenter suivant les tests 
    	        }
    		}
        }
        result = valider();
        return result;
    }
    
    public void creerTicket() throws ValidationException, Exception {
        Integer persId = session().applicationUser().getPersId();
        EOCompte ticket = TicketService.createTicket(editingContext(), 
                    getCptLoginCreation(), 
                    getCptFinValideCreation(), 
                    getMotDePasse(),
                    persId, 
                    session().applicationUser());
        setTicket(ticket);
    }
    
    public WOActionResults printWindow() {
        PrintTicketPage print = (PrintTicketPage)pageWithName(PrintTicketPage.class.getName());
        print.setTicket(ticket());
        return print;
    }
    
    private WOActionResults valider() {
        WOActionResults result = null;
        try {
        	if (isCreation()) {
        		creerTicket();
			}
            if (editingContext().hasChanges()){
                editingContext().saveChanges();
                SynchroComptePersonne.synchroComptePersonne(ticket().persId().toString());
                session().addSimpleInfoMessage(localizer().localizedTemplateStringForKeyWithObject("ticket.valid", 
                        ticket()), null);
                setEditMode(false);
                setCreationMode(false);
                try {
					TicketService.sendMailInfoToValidAlias(ticket(), application().config());
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
		            session().addSimpleErrorMessage(e.getMessage(), e);
				}
                if (hasBinding(BINDING_DID_CREATE))
                    return (WOActionResults)valueForBinding(BINDING_DID_CREATE);
            }
        } catch (ValidationException e) {
        	editingContext().revert();
            LOG.warn(e.getMessage(), e);
            context().response().setStatus(500);
            session().addSimpleErrorMessage(e.getMessage(), e);
        } catch (Exception e1) {
        	editingContext().revert();
            LOG.error(e1.getMessage(), e1);
            context().response().setStatus(500);
            session().addSimpleErrorMessage(e1.getMessage(), e1);
        }
        return result;
    }

    public EOCompte ticket() {
    	if (ticket==null) {
			ticket = (EOCompte)valueForBinding(TICKET_BDG);
		}
        return ticket;
    }
    
    public void setTicket(EOCompte ticket) {
		this.ticket = ticket;
	}

    public WOActionResults editerValiditeTicket() {
        if (ticket() != null)
            ticket().setCptFinValide(getFinValidite());
        return null;
    }

    private NSTimestamp getFinValidite() {
        NSTimestamp now = new NSTimestamp();
        CAL.setTime(now);
        NSTimestamp expDate = now;
        if (expiration == FIN_J) {
            int hour = CAL.get(Calendar.HOUR_OF_DAY);
            int ecart = 24 - hour;
            CAL.clear(Calendar.MINUTE);
            CAL.clear(Calendar.SECOND);
            now = new NSTimestamp( CAL.getTime());
            expDate = now.timestampByAddingGregorianUnits(0, 0, 0, ecart, 0, 0);
            
        } else if (expiration == DEMAIN) {
        	CAL.clear(Calendar.HOUR_OF_DAY);
        	CAL.clear(Calendar.MINUTE);
            CAL.clear(Calendar.SECOND);
            now = new NSTimestamp( CAL.getTime());
            expDate = now.timestampByAddingGregorianUnits(0, 0, 1, 0, 0, 0);
        } else if (expiration == FIN_SEM) {
        	int hour = CAL.get(Calendar.HOUR_OF_DAY);
            int day = CAL.get(Calendar.DAY_OF_WEEK);
            int ecart = Calendar.SATURDAY - day + 1;
            int ecartH = 24 - hour;
        	CAL.clear(Calendar.MINUTE);
            CAL.clear(Calendar.SECOND);
            now = new NSTimestamp( CAL.getTime());
            expDate = now.timestampByAddingGregorianUnits(0, 0, ecart, ecartH, 0, 0);
        } else if (expiration == UN_MOIS) {
        	CAL.clear(Calendar.HOUR_OF_DAY);
        	CAL.clear(Calendar.MINUTE);
            CAL.clear(Calendar.SECOND);
            now = new NSTimestamp( CAL.getTime());
            expDate = now.timestampByAddingGregorianUnits(0, 1, 0, 0, 0, 0);
        } else if (expiration == SIX_MOIS) {
        	CAL.clear(Calendar.MINUTE);
            CAL.clear(Calendar.SECOND);
            now = new NSTimestamp( CAL.getTime());
            expDate = now.timestampByAddingGregorianUnits(0, 6, 0, 0, 0, 0);
        } else if (expiration == FIN_ANNEE_U) {
        	expDate = new NSTimestamp(MyDateCtrl.getEndDateOfUniversityYear(CAL.getTime()));
        }
        return expDate;
    }

    public boolean isCreation() {
//        return ticket() == null || ERXEOControlUtilities.isNewObject(ticket());
    	this.creationMode = booleanValueForBinding(BINDING_IS_CREATION, false);
        return creationMode;
    }

    public void setCreationMode(boolean creationMode) {
		this.creationMode = creationMode;
		setValueForBinding(creationMode, BINDING_IS_CREATION);
	}

	public boolean isEdition() {
        return isCreation() || editMode;
    }
    
    public boolean isVisu() {
        return !isEdition();
    }
    
    public String getFinJ() {
        return FIN_J;
    }

    public String getDemain() {
        return DEMAIN;
    }

    public String getFinSem() {
        return FIN_SEM;
    }

    public static String getUnMois() {
		return UN_MOIS;
	}

	public static String getSixMois() {
		return SIX_MOIS;
	}

	public static String getFinAnneeU() {
		return FIN_ANNEE_U;
	}

	public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }
    
    public String getMotDePasse() {
        return motDePasse;
    }
    
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
    
    public String getMotDePasseBis() {
        return motDePasseBis;
    }
    
    public void setMotDePasseBis(String motDePasseBis) {
        this.motDePasseBis = motDePasseBis;
    }
    
    public boolean isEditMode() {
        return editMode;
    }
    
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

	public WOActionResults setExpirationFinJ() {
		setExpiration(getFinJ());
		setDateFinValiditee();
		return null;
	}

	public WOActionResults setExpirationDemain() {
		setExpiration(getDemain());
		setDateFinValiditee();
		return null;
	}

	public WOActionResults setExpirationFinSem() {
		setExpiration(getFinSem());
		setDateFinValiditee();
		return null;
	}
	
	public WOActionResults setExpiration1Mois() {
		setExpiration(getUnMois());
		setDateFinValiditee();
		return null;
	}

	public WOActionResults setExpiration6Mois() {
		setExpiration(getSixMois());
		setDateFinValiditee();
		return null;
	}

	public WOActionResults setExpirationFinAnneeU() {
		setExpiration(getFinAnneeU());
		setDateFinValiditee();
		return null;
	}
	
	private void setDateFinValiditee() {
		if (ticket()!=null) {
			ticket().setCptFinValide(getFinValidite());
		} else {
			setCptFinValideCreation(getFinValidite());
		}
	}

	/**
	 * @return the cptLoginCreation
	 */
	public String getCptLoginCreation() {
		return cptLoginCreation;
	}

	/**
	 * @param cptLoginCreation the cptLoginCreation to set
	 */
	public void setCptLoginCreation(String cptLoginCreation) {
		this.cptLoginCreation = cptLoginCreation;
	}

	/**
	 * @return the cptDebutValideCreation
	 */
	public NSTimestamp getCptDebutValideCreation() {
		if (isCreation() && cptDebutValideCreation==null) {
			cptDebutValideCreation = MyDateCtrl.now();
		}
		return cptDebutValideCreation;
	}

	/**
	 * @param cptDebutValideCreation the cptDebutValideCreation to set
	 */
	public void setCptDebutValideCreation(NSTimestamp cptDebutValideCreation) {
		this.cptDebutValideCreation = cptDebutValideCreation;
	}

	/**
	 * @return the cptFinValideCreation
	 */
	public NSTimestamp getCptFinValideCreation() {
		if (isCreation() && cptFinValideCreation==null) {
			cptFinValideCreation = getFinValidite();
		}
		return cptFinValideCreation;
	}

	/**
	 * @param cptFinValideCreation the cptFinValideCreation to set
	 */
	public void setCptFinValideCreation(NSTimestamp cptFinValideCreation) {
		this.cptFinValideCreation = cptFinValideCreation;
	}

}