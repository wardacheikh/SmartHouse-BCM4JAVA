package fr.sorbonne_u.components.hem2025e1.equipement.Wifi;

import java.io.InputStream;

import java.util.concurrent.TimeUnit;

import WifiConnector.BoxWifiExternalControlInboundPort;
import WifiConnector.RegistrationConnector;
import WifiConnector.RegistrationOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025.bases.RegistrationCI;
import fr.sorbonne_u.exceptions.PreconditionException;

@OfferedInterfaces(offered={BoxWifiUserCI.class, BoxWifiExternalControlCI.class, BoxWifiImplementationI.class})
@RequiredInterfaces(required = {RegistrationCI.class})


public class BoxWifi
extends AbstractComponent
implements BoxWifiImplementationI , BoxWifiExternalControlI
{
	
	public static final String EXTERNAL_CONTROL_INBOUND_PORT_URI =
	        "BOX-WIFI-EXTERNAL-CONTROL-INBOUND-PORT-URI";

	// URI du port de réflexion
	public static final String REFLECTION_INBOUND_PORT_URI =
	    "BOX-WIFI-RIP-URI";

	// URI du port entrant utiliser dans le connecteur
	public static final String INBOUND_PORT_URI =
	    "BOX-WIFI-INBOUND-PORT-URI";

	// Le port entrant lui-même
	protected BoxWifiInboundPort bwip;
	
	public static final String HEM_REGISTRATION_URI = "HEM-REGISTRATION-PORT-URI";
    private static final String BOX_UID = "BOX-WIFI-001"; 
    
	
	/** when true, methods trace their actions in console. */
    public static boolean VERBOSE = true;

    /** initial mode of the box WiFi. */
    protected static final BoxWifiMode INITIAL_MODE = BoxWifiMode.OFF;

    /** current mode of the box WiFi. */
    protected BoxWifiMode currentMode;
    
    protected BoxWifiExternalControlInboundPort externalControlInboundPort;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
  

    /**
     * create a BoxWifi component.
     *
     * @throws Exception   if an error occurs.
     */
  
    public BoxWifi() throws Exception {
        this(INBOUND_PORT_URI);
    }

    /**
     * Constructeur avec URI spécifique du port inbound
     */
    protected BoxWifi(String boxWifiInboundPortURI) throws Exception {
        super(1, 1); 
        this.initialise(boxWifiInboundPortURI);
    }


    /**
     * create a BoxWifi component with reflection inbound port URI and main inbound port URI.
     * 1 thread and 0 thread schedular
     * @param reflectionInboundPortURI URI of the reflection inbound port.
     * @param boxWifiInboundPortURI    URI of the box WiFi inbound port.
     * @throws Exception               if an error occurs.
     */
    protected BoxWifi(
        String reflectionInboundPortURI,
        String boxWifiInboundPortURI
    ) throws Exception {
        super(reflectionInboundPortURI, 1, 1);
        this.initialise(boxWifiInboundPortURI);
    }
    
    /**
     * initialise the box WiFi component.
     *
     * @param boxWifiInboundPortURI URI of the box WiFi inbound port.
     * @throws Exception            if an error occurs.
     */
    protected void initialise(String boxWifiInboundPortURI) throws Exception {
        assert boxWifiInboundPortURI != null :
            new PreconditionException("boxWifiInboundPortURI != null");
        assert !boxWifiInboundPortURI.isEmpty() :
            new PreconditionException("!boxWifiInboundPortURI.isEmpty()");

        this.currentMode = INITIAL_MODE;
        this.bwip = new BoxWifiInboundPort(boxWifiInboundPortURI, this);
        this.bwip.publishPort();
        
        this.externalControlInboundPort = new BoxWifiExternalControlInboundPort(
        	     EXTERNAL_CONTROL_INBOUND_PORT_URI, this);
        	this.externalControlInboundPort.publishPort();



        if (BoxWifi.VERBOSE) {
            this.tracer.get().setTitle("Box WiFi component");
            this.tracer.get().setRelativePosition(0, 0);
            this.toggleTracing();
        }
    }

    // -------------------------------------------------------------------------
    // Component life-cycle
    // -------------------------------------------------------------------------

    @Override
    public synchronized void shutdown() throws ComponentShutdownException {
        System.out.println(" BoxWifi.shutdown() - Cleaning up...");
        
        try {
            if (this.bwip != null) {
                this.bwip.unpublishPort();
            }
            
            if (this.externalControlInboundPort != null) {
                this.externalControlInboundPort.unpublishPort();
            }
            
        } catch (Exception e) {
            System.err.println("BoxWifi shutdown failed: " + e.getMessage());
            throw new ComponentShutdownException(e);
        }
        
        super.shutdown();
    }
    @Override
    public synchronized void finalise() throws Exception {
        if (this.externalControlInboundPort != null) {
            this.externalControlInboundPort.unpublishPort();
        }
        super.finalise();
    }
    // -------------------------------------------------------------------------
    // Services implementation (BoxWifiImplementationI)
    // 
    @Override
    public void turnOn() throws Exception {
        this.currentMode = BoxWifiMode.BOX_ONLY;
        if (VERBOSE) this.traceMessage("Box allumée (WiFi désactivé).\n");
    }

    @Override
    public void turnOff() throws Exception {
        this.currentMode = BoxWifiMode.OFF;
        if (VERBOSE) this.traceMessage("Box éteinte.\n");
    }

    @Override
    public void activateWifi() throws Exception {
        if (this.currentMode != BoxWifiMode.OFF) {
            this.currentMode = BoxWifiMode.FULL_ON;
            if (VERBOSE) this.traceMessage("WiFi activé.\n");
        } else {
            if (VERBOSE) this.traceMessage("Impossible : Box éteinte.\n");
        }
    }

    @Override
    public void deactivateWifi() throws Exception {
        if (this.currentMode == BoxWifiMode.FULL_ON) {
            this.currentMode = BoxWifiMode.BOX_ONLY;
            if (VERBOSE) this.traceMessage("WiFi désactivé.\n");
        }
    }

    @Override
    public BoxWifiMode getMode() throws Exception {
        if (VERBOSE) this.traceMessage("Mode actuel : " + this.currentMode + ".\n");
        return this.currentMode;
    }

    @Override
    public boolean isOn() throws Exception {
        return this.currentMode != BoxWifiMode.OFF;
    }

	@Override
	public double getMaxPowerLevel() throws Exception {
		return 12.0;
	}

	@Override
	public void setCurrentPowerLevel(double powerLevel) throws Exception {
		 // Simule une adaptation de la puissance selon la valeur
	    if (powerLevel <= 0) this.turnOff();
	    else if (powerLevel < 5) this.deactivateWifi();
	    else if (powerLevel < 10) this.turnOn();
	    else this.activateWifi();
	    if (VERBOSE) this.traceMessage("Puissance actuelle ajustée à " + powerLevel + "W\n");
		
	}

	@Override
	public double getCurrentPowerLevel() throws Exception {
		 switch (this.currentMode) {
	        case OFF: return 0.0;
	        case BOX_ONLY: return 8.0;
	        case FULL_ON: return 12.0;
	        default: return 0.0;
	    }
	}

	@Override
	public void activateWifiForDuration(int minutes) throws Exception {
		this.activateWifi();
	    new Thread(() -> {
	        try {
	            Thread.sleep(minutes * 60 * 1000);
	            this.deactivateWifi();
	        } catch (Exception e) { e.printStackTrace(); }
	    }).start();		
	}

	@Override
	public void scheduleModeChange(BoxWifiMode mode, String time) throws Exception {
		 new Thread(() -> {
		        try {
		            while (true) {
		                String now = java.time.LocalTime.now().withSecond(0).toString().substring(0,5);
		                if (now.equals(time)) {
		                    switch (mode) {
		                        case OFF: this.turnOff(); break;
		                        case WIFI_ONLY: this.activateWifi(); break;
		                        case BOX_ONLY: this.turnOn(); break;
		                        case FULL_ON: this.activateWifi(); break;
		                    }
		                    break;
		                }
		                Thread.sleep(60_000); 
		            }
		        } catch (Exception e) { e.printStackTrace(); }
		    }).start();		
	}

	@Override
	public double getNetworkLoad() throws Exception {
		 switch (this.currentMode) {
	        case WIFI_ONLY: return 0.3;
	        case BOX_ONLY: return 0.5;
	        case FULL_ON: return 0.8;
	        default: return 0.0;
	    }
	}
	

    
	 @Override
	    public synchronized void start() throws ComponentStartException {
	        super.start();
	        
	        try {
	            // Publier le port de contrôle externe 
	            this.externalControlInboundPort = new BoxWifiExternalControlInboundPort(
	                EXTERNAL_CONTROL_INBOUND_PORT_URI, this);
	            this.externalControlInboundPort.publishPort();
	            
	           
	            // S'enregistrer auprès du HEM après un délai
	            this.scheduleTask(
	                new AbstractComponent.AbstractTask() {
	                    @Override
	                    public void run() {
	                        try {
	                            registerWithHEM();
	                        } catch (Exception e) {
	                            System.err.println("Failed to register with HEM: " + e.getMessage());
	                            if (VERBOSE) e.printStackTrace();
	                        }
	                    }
	                }, 3000, TimeUnit.MILLISECONDS);
	                
	        } catch (Exception e) {
	            throw new ComponentStartException(e);
	        }
	    }
    protected void registerWithHEM() throws Exception {        
        String xmlDescriptor = readXMLDescriptor();
        
        RegistrationOutboundPort registrationPort = new RegistrationOutboundPort(this);
        registrationPort.publishPort();
        
        this.doPortConnection(registrationPort.getPortURI(), 
                            HEM_REGISTRATION_URI, 
                            RegistrationConnector.class.getCanonicalName());
        
   
        boolean success = registrationPort.register(BOX_UID, 
                                                  EXTERNAL_CONTROL_INBOUND_PORT_URI, 
                                                  xmlDescriptor);
        
        if (success) {
            System.out.println("BoxWifi successfully registered with HEM");
        } else {
            System.err.println("BoxWifi registration failed");
        }
        
        this.doPortDisconnection(registrationPort.getPortURI());
        registrationPort.unpublishPort();
    }
    
    protected String readXMLDescriptor() throws Exception {
        System.out.println("Looking for XML file...");
        
        InputStream is = null;
        
        // Essayer différents chemins
        String[] possiblePaths = {
            "BoxWifi-descriptor.xml",
            "/BoxWifi-descriptor.xml",
            "fr/sorbonne_u/components/hem2025e1/resources/BoxWifi-descriptor.xml"
        };
        
        for (String path : possiblePaths) {
            is = getClass().getClassLoader().getResourceAsStream(path);
            if (is != null) {
                break;
            }
        }
        
        if (is == null) {
            // Essayer avec getResource
            is = getClass().getResourceAsStream("/BoxWifi-descriptor.xml");
            if (is != null) {
                System.out.println(" Found XML file with getResource");
            }
        }
        

        
        try {
            java.util.Scanner scanner = new java.util.Scanner(is, "UTF-8").useDelimiter("\\A");
            String xmlContent = scanner.hasNext() ? scanner.next() : "";
            scanner.close();
                       
            return xmlContent;
            
        } catch (Exception e) {
            System.err.println("Error reading XML file: " + e.getMessage());
            throw e;
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                System.err.println("Error closing stream: " + e.getMessage());
            }
        }
    }
	
}
