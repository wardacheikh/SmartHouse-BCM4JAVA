package fr.sorbonne_u.components.hem2025e1.equipments.hem;


import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.BCMRuntimeException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025.bases.AdjustableCI;
import fr.sorbonne_u.components.hem2025.bases.RegistrationCI;
import fr.sorbonne_u.components.hem2025.tests_utils.TestsStatistics;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeterCI;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeterOutboundPort;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;


import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import WifiConnector.RegistrationInboundPort;

/**
 * HEM adapted to test and control a BoxWifi (modulable device).
 */
@RequiredInterfaces(required = { ElectricMeterCI.class,AdjustableCI.class, ClocksServerCI.class})
@OfferedInterfaces(offered={RegistrationCI.class})

public class HEM extends AbstractComponent implements RegistrationCI
{
    public static boolean VERBOSE = false;
    public static int X_RELATIVE_POSITION = 0;
    public static int Y_RELATIVE_POSITION = 0;

    protected ElectricMeterOutboundPort meterop;

    /** if true, manage the wifi in a customised way (instead of expecting it to register). */
    protected boolean isPreFirstStep;
    /** outbound port to call the wifi as an Adjustable device (via a customized connector). */
    protected AdjustableOutboundPort wifiop;

    protected boolean performTest;
    protected AcceleratedClock ac;
    protected TestsStatistics statistics;

    protected static boolean implementationInvariants(HEM hem) {
        assert hem != null : new PreconditionException("hem != null");
        return true;
    }

    protected static boolean invariants(HEM hem) {
        assert hem != null : new PreconditionException("hem != null");
        boolean ret = true;
        ret &= AssertionChecking.checkImplementationInvariant(X_RELATIVE_POSITION >= 0, HEM.class, hem, "X_RELATIVE_POSITION >= 0");
        ret &= AssertionChecking.checkImplementationInvariant(Y_RELATIVE_POSITION >= 0, HEM.class, hem, "Y_RELATIVE_POSITION >= 0");
        return ret;
    }

    protected HEM() {
        this(true);
    }

    protected HEM(boolean performTest) {
        super(1, 1);
        this.performTest = performTest;
        this.isPreFirstStep = true;
        this.statistics = new TestsStatistics();

        HEM.VERBOSE = true;
        this.tracer.get().setTitle("Home Energy Manager (WiFi) - Integration Test");
        this.tracer.get().setRelativePosition(0, 0);
        this.toggleTracing();
        
    }
    // ---------------- lifecycle ----------------
    @Override
    public synchronized void start() throws ComponentStartException {
        super.start();

        try {
            
            // ✅ AJOUTEZ CE PORT D'ENREGISTREMENT
            RegistrationInboundPort registrationPort = new RegistrationInboundPort(HEM_REGISTRATION_URI, this);
            registrationPort.publishPort();
                        
        } catch (Exception e) {
            System.err.println("HEMWifi start failed: " + e.getMessage());
            e.printStackTrace();
            throw new ComponentStartException(e);
        }
    }
   @Override
public synchronized void execute() throws Exception {
    
    Thread.sleep(2000);
    
    if (!registeredAppliances.isEmpty()) {
        this.testAllRegisteredAppliances();
    } 
    
}
    protected void testWifiOnPort(AdjustableOutboundPort port) throws Exception {
           this.testMeter();
        
        if (this.isPreFirstStep) {
            this.testWifiWithPort(port);
        }
    }
 protected void testWifiWithPort(AdjustableOutboundPort testPort) throws Exception {
    this.logMessage("\n===== DEBUT DE LA SEQUENCE DE TESTS WiFi =====");
    this.logMessage("Heure de debut: " + java.time.LocalTime.now());
    this.statistics = new TestsStatistics();
    
    try {
        // === FEATURE 1: GESTION DES MODES ===
        this.logMessage("\nFEATURE 1: Gestion des modes de l'appareil");
        
        // SCENARIO 1.1: Obtenir le mode maximum
        this.logMessage("\n  SCENARIO 1.1: Obtenir l'index du mode maximum");
        this.logMessage("      quand J'appelle maxMode()");
        final int maxMode = testPort.maxMode();
        this.statistics.updateStatistics();
        this.logMessage("       Je devrais recevoir un index de mode maximum valide");
        this.logMessage("      RESULTAT: maxMode() = " + maxMode);

        // SCENARIO 1.2: Obtenir le mode courant initial
        this.logMessage("\n  SCENARIO 1.2: Obtenir l'index du mode courant initial");
        this.logMessage("       Un appareil WiFi nouvellement connecte");
        this.logMessage("      quand J'appelle currentMode()");
        int currentMode = testPort.currentMode();
        this.logMessage("       Je devrais recevoir le mode initial (attendu: 0 - OFF)");
        this.logMessage("      RESULTAT: currentMode() = " + currentMode);
        
        boolean scenarioPassed = (currentMode == 0);
        if (scenarioPassed) {
            this.logMessage("      SUCCES: Mode initial correct (0 - OFF)");
            this.statistics.updateStatistics();
        } else {
            this.logMessage("      ECHEC: Mode initial = " + currentMode + " au lieu de 0");
            this.statistics.incorrectResult();
        }

        // === FEATURE 2: NAVIGATION DANS LES MODES ===
        this.logMessage("\nFEATURE 2: Navigation entre les modes");
        
        // SCENARIO 2.1: Augmentation du mode
        this.logMessage("\n  SCENARIO 2.1: Augmenter d'un niveau de mode");
        this.logMessage("       Le mode courant est " + currentMode);
        this.logMessage("      quand J'appelle upMode()");
        boolean operationResult = testPort.upMode();
        this.logMessage("      RESULTAT: upMode() = " + operationResult);
        
        currentMode = testPort.currentMode();
        this.logMessage("       Le mode devrait passer a " + (currentMode + 1));
        this.logMessage("      RESULTAT: Nouveau currentMode() = " + currentMode);
        
        scenarioPassed = operationResult && (currentMode == 1);
        if (scenarioPassed) {
            this.logMessage("      SUCCES: Mode augmente correctement de 0 a 1");
            this.statistics.updateStatistics();
        } else {
            this.logMessage("      ECHEC: upMode()=" + operationResult + ", currentMode=" + currentMode);
            this.statistics.incorrectResult();
        }

        // SCENARIO 2.2: Augmentation jusqu'au mode 2
        this.logMessage("\n  SCENARIO 2.2: Augmenter vers le mode 2");
        this.logMessage("       Le mode courant est " + currentMode);
        this.logMessage("      quand J'appelle upMode() a nouveau");
        operationResult = testPort.upMode();
        currentMode = testPort.currentMode();
        this.logMessage("      RESULTAT: upMode() = " + operationResult + ", currentMode() = " + currentMode);
        
        scenarioPassed = operationResult && (currentMode == 2);
        if (scenarioPassed) {
            this.logMessage("      SUCCES: Mode augmente correctement de 1 a 2");
            this.statistics.updateStatistics();
        } else {
            this.logMessage("      ECHEC: Echec de l'augmentation vers le mode 2");
            this.statistics.incorrectResult();
        }

        // SCENARIO 2.3: Diminution du mode
        this.logMessage("\n  SCENARIO 2.3: Diminuer d'un niveau de mode");
        this.logMessage("       Le mode courant est " + currentMode);
        this.logMessage("      quand J'appelle downMode()");
        operationResult = testPort.downMode();
        currentMode = testPort.currentMode();
        this.logMessage("      RESULTAT: downMode() = " + operationResult + ", currentMode() = " + currentMode);
        
        scenarioPassed = operationResult && (currentMode == 1);
        if (scenarioPassed) {
            this.logMessage("      SUCCES: Mode diminue correctement de 2 a 1");
            this.statistics.updateStatistics();
        } else {
            this.logMessage("      ECHEC: downMode()=" + operationResult + ", currentMode=" + currentMode);
            this.statistics.incorrectResult();
        }

        // === FEATURE 3: DEFINITION DIRECTE DES MODES ===
        this.logMessage("\nFEATURE 3: Definition directe des modes");
        
        // SCENARIO 3.1: Definition du mode 2 directement
        this.logMessage("\n  SCENARIO 3.1: Definir directement le mode 2");
        this.logMessage("       Le mode courant est " + currentMode);
        this.logMessage("      quand J'appelle setMode(2)");
        operationResult = testPort.setMode(2);
        currentMode = testPort.currentMode();
        this.logMessage("      RESULTAT: setMode(2) = " + operationResult + ", currentMode() = " + currentMode);
        
        scenarioPassed = operationResult && (currentMode == 2);
        if (scenarioPassed) {
            this.logMessage("      SUCCES: Mode defini directement a 2");
            this.statistics.updateStatistics();
        } else {
            this.logMessage("      ECHEC: Impossible de definir le mode 2");
            this.statistics.incorrectResult();
        }

        // SCENARIO 3.2: Retour au mode OFF (0)
        this.logMessage("\n  SCENARIO 3.2: Retour au mode OFF (0)");
        this.logMessage("       Le mode courant est " + currentMode);
        this.logMessage("      quand J'appelle setMode(0)");
        operationResult = testPort.setMode(0);
        currentMode = testPort.currentMode();
        this.logMessage("      RESULTAT: setMode(0) = " + operationResult + ", currentMode() = " + currentMode);
        
        scenarioPassed = operationResult && (currentMode == 0);
        if (scenarioPassed) {
            this.logMessage("      SUCCES: Retour au mode OFF (0) reussi");
            this.statistics.updateStatistics();
        } else {
            this.logMessage("      ECHEC: Impossible de retourner au mode OFF");
            this.statistics.incorrectResult();
        }

        // === FEATURE 4: TEST DES LIMITES ===
        this.logMessage("\nFEATURE 4: Test des limites et cas particuliers");
        
        // SCENARIO 4.1: Tentative de dépassement supérieur
        this.logMessage("\n  SCENARIO 4.1: Tentative de dépassement vers le haut");
        this.logMessage("       Le mode courant est " + currentMode);
        this.logMessage("      quand J'essaie d'atteindre le mode " + (maxMode + 1));
        
        // Monter jusqu'au maximum
        for (int i = currentMode; i < maxMode; i++) {
            testPort.upMode();
        }
        currentMode = testPort.currentMode();
        this.logMessage("      RESULTAT: Mode maximum atteint = " + currentMode);
        
        // Tenter de dépasser
        operationResult = testPort.upMode();
        int modeAfterAttempt = testPort.currentMode();
        this.logMessage("       upMode() au maximum devrait retourner false");
        this.logMessage("      RESULTAT: upMode() au max = " + operationResult + ", mode reste = " + modeAfterAttempt);
        
        scenarioPassed = !operationResult && (modeAfterAttempt == maxMode);
        if (scenarioPassed) {
            this.logMessage("      SUCCES: Protection contre le dépassement supérieur fonctionne");
            this.statistics.updateStatistics();
        } else {
            this.logMessage("      ALERTE: Comportement inattendu au dépassement");
            this.statistics.incorrectResult();
        }

        // SCENARIO 4.2: Tentative de dépassement inférieur
        this.logMessage("\n  SCENARIO 4.2: Tentative de dépassement vers le bas");
        this.logMessage("       Le mode courant est 0 (OFF)");
        testPort.setMode(0); // S'assurer d'être à 0
        this.logMessage("      quand J'appelle downMode() au minimum");
        operationResult = testPort.downMode();
        currentMode = testPort.currentMode();
        this.logMessage("      RESULTAT: downMode() au min = " + operationResult + ", mode reste = " + currentMode);
        
        scenarioPassed = !operationResult && (currentMode == 0);
        if (scenarioPassed) {
            this.logMessage("      SUCCES: Protection contre le dépassement inférieur fonctionne");
            this.statistics.updateStatistics();
        } else {
            this.logMessage("      ALERTE: Comportement inattendu au dépassement inférieur");
            this.statistics.incorrectResult();
        }

        // === FEATURE 5: TEST DE CONSOMMATION ===
        this.logMessage("\nFEATURE 5: Test des informations de consommation");
        
        // SCENARIO 5.1: Obtenir la consommation par mode
        this.logMessage("\n  SCENARIO 5.1: Obtenir la consommation pour chaque mode");
        this.logMessage("      quand J'appelle getModeConsumption() pour tous les modes");
        
        boolean consumptionTestPassed = true;
        for (int mode = 0; mode <= maxMode; mode++) {
            try {
                double consumption = testPort.getModeConsumption(mode);
                this.logMessage("      Mode " + mode + ": consommation = " + consumption + " W");
                if (consumption < 0) {
                    this.logMessage("       Consommation negative pour le mode " + mode);
                    consumptionTestPassed = false;
                }
            } catch (Exception e) {
                this.logMessage("      ERREUR: Impossible d'obtenir la consommation pour le mode " + mode);
                consumptionTestPassed = false;
            }
        }
        
        if (consumptionTestPassed) {
            this.logMessage("      SUCCES: Toutes les consommations recuperees avec succes");
            this.statistics.updateStatistics();
        } else {
            this.logMessage("      ECHEC: Probleme avec la recuperation des consommations");
            this.statistics.incorrectResult();
        }

        // === FEATURE 6: TEST DE SUSPENSION ET REPRISE ===
        this.logMessage("\nFEATURE 6: Test des fonctions de suspension et reprise");
        
        // SCENARIO 6.1: Verifier l'etat non suspendu initial
        this.logMessage("\n  SCENARIO 6.1: Verifier l'etat non suspendu initial");
        this.logMessage("       L'appareil est en fonctionnement normal");
        this.logMessage("      quand J'appelle suspended()");
        boolean suspendedState = testPort.suspended();
        this.logMessage("      RESULTAT: suspended() = " + suspendedState);
        
        scenarioPassed = !suspendedState;
        if (scenarioPassed) {
            this.logMessage("      SUCCES: Appareil correctement non suspendu au depart");
            this.statistics.updateStatistics();
        } else {
            this.logMessage("      ECHEC: Appareil suspendu anormalement au depart");
            this.statistics.incorrectResult();
        }

        // SCENARIO 6.2: Suspension de l'appareil
        this.logMessage("\n  SCENARIO 6.2: Suspension de l'appareil");
        this.logMessage("       L'appareil est en mode " + currentMode);
        this.logMessage("      quand J'appelle suspend()");
        operationResult = testPort.suspend();
        this.logMessage("      RESULTAT: suspend() = " + operationResult);
        
        suspendedState = testPort.suspended();
        this.logMessage("       suspended() devrait retourner true");
        this.logMessage("      RESULTAT: suspended() apres suspend = " + suspendedState);
        
        int modeAfterSuspend = testPort.currentMode();
        this.logMessage("      RESULTAT: currentMode() apres suspend = " + modeAfterSuspend);
        
        scenarioPassed = operationResult && suspendedState && (modeAfterSuspend == 0);
        if (scenarioPassed) {
            this.logMessage("      SUCCES: Suspension effectuee correctement");
            this.statistics.updateStatistics();
        } else {
            this.logMessage("      ECHEC: Probleme lors de la suspension");
            this.statistics.incorrectResult();
        }

        // SCENARIO 6.3: Reprise de l'appareil
        this.logMessage("\n  SCENARIO 6.3: Reprise de l'appareil");
        this.logMessage("       L'appareil est suspendu");
        this.logMessage("      quand J'appelle resume()");
        operationResult = testPort.resume();
        this.logMessage("      RESULTAT: resume() = " + operationResult);
        
        suspendedState = testPort.suspended();
        this.logMessage("       suspended() devrait retourner false");
        this.logMessage("      RESULTAT: suspended() apres resume = " + suspendedState);
        
        scenarioPassed = operationResult && !suspendedState;
        if (scenarioPassed) {
            this.logMessage("      SUCCES: Reprise effectuee correctement");
            this.statistics.updateStatistics();
        } else {
            this.logMessage("      ECHEC: Probleme lors de la reprise");
            this.statistics.incorrectResult();
        }

        // SCENARIO 6.4: Test de la fonction emergency
        this.logMessage("\n  SCENARIO 6.4: Test de la fonction emergency");
        this.logMessage("      quand J'appelle emergency()");
        double emergencyLevel = testPort.emergency();
        this.logMessage("      RESULTAT: emergency() = " + emergencyLevel);
        
        scenarioPassed = (emergencyLevel >= 0.0 && emergencyLevel <= 1.0);
        if (scenarioPassed) {
            this.logMessage("      SUCCES: Niveau d'urgence dans les limites attendues");
            this.statistics.updateStatistics();
        } else {
            this.logMessage("      ECHEC: Niveau d'urgence hors limites: " + emergencyLevel);
            this.statistics.incorrectResult();
        }

       
    } catch (Exception e) {
        this.logMessage("\nERREUR CRITIQUE pendant les tests: " + e.getMessage());
        this.logMessage("Stack trace: " + java.util.Arrays.toString(e.getStackTrace()).substring(0, 200) + "...");
        this.statistics.incorrectResult();
    }

    this.statistics.statisticsReport(this);
    this.logMessage("\n===== FIN DE LA SEQUENCE DE TESTS WiFi =====");
    this.logMessage("Heure de fin: " + java.time.LocalTime.now());
} 
    @Override
    
    public synchronized void finalise() throws Exception {
        
        // Déconnectez seulement les ports qui existent
        if (this.meterop != null) {
            try {
                this.doPortDisconnection(this.meterop.getPortURI());
            } catch (Exception e) {
                System.err.println(" ElectricMeter disconnection failed: " + e.getMessage());
            }
        } else {
            System.out.println("meterop is null - skipping disconnection");
        }
        
        if (this.isPreFirstStep && this.wifiop != null) {
            try {
                this.doPortDisconnection(this.wifiop.getPortURI());
                System.out.println("BoxWifi port disconnected");
            } catch (Exception e) {
                System.err.println("BoxWifi disconnection failed: " + e.getMessage());
            }
        }
        
        super.finalise();
        System.out.println(" HEMWifi.finalise() completed");
    }

  
    /**
     * Retourne la liste de tous les appareils enregistrés
     */
    public List<String> getRegisteredAppliances() {
        return new ArrayList<>(registeredAppliances.keySet());
    }

    /**
     * Retourne le port d'un appareil spécifique
     */
    public AdjustableOutboundPort getAppliancePort(String uid) {
        return appliancePorts.get(uid);
    }

    
    public void testAllRegisteredAppliances() throws Exception {
        this.logMessage("\n ===== TEST DE TOUS LES APPAREILS ENREGISTRÉS =====");
        this.logMessage("   Number of appliances: " + registeredAppliances.size());
        
        for (String uid : registeredAppliances.keySet()) {
            try {
                AdjustableOutboundPort port = appliancePorts.get(uid);
                if (port != null) {
                    this.logMessage("  Testing appliance: " + uid);
                    
                    this.logMessage("    When I call maxMode()");
                    int maxMode = port.maxMode();
                    this.logMessage("     maxMode() = " + maxMode);
                    
                    this.logMessage("    When I call currentMode()");
                    int currentMode = port.currentMode();
                    this.logMessage("    currentMode() = " + currentMode);
                    
                }
            } catch (Exception e) {
                this.logMessage("  " + uid + " - Test failed: " + e.getMessage());
            }
        }
        this.logMessage(" ===== ALL APPLIANCES TESTED =====");
    }


    /**
     * Nettoie tous les appareils enregistrés (pour shutdown)
     */
    protected void cleanupAllAppliances() throws Exception {
        List<String> uids = new ArrayList<>(registeredAppliances.keySet());
        for (String uid : uids) {
            try {
                unregister(uid);
            } catch (Exception e) {
                System.err.println("Failed to unregister " + uid + ": " + e.getMessage());
            }
        }
    }

    @Override
    public synchronized void shutdown() throws ComponentShutdownException {
        System.out.println("HEMWifi.shutdown() - Cleaning up...");
        
        try {
            // Nettoyer tous les appareils enregistrés
            cleanupAllAppliances();
            
            // Nettoyer les ports existants (méthode manuelle)
            if (this.meterop != null) {
                this.meterop.unpublishPort();
            }
            
            if (this.isPreFirstStep && this.wifiop != null) {
                this.wifiop.unpublishPort();
            }
        } catch (Exception e) {
            System.err.println("Cleanup failed: " + e.getMessage());
            throw new ComponentShutdownException(e);
        }
        
        super.shutdown();
        System.out.println("HEMWifi.shutdown() completed");
    }
    
    
    protected void testMeter() throws Exception {
                this.statistics.updateStatistics();
    }

    /**
     * Tests for the BoxWifi via the Adjustable interface (modes, suspend/resume).
     */
 protected void testWifi() throws Exception {
    this.logMessage("Wifi tests start.");
    this.statistics = new TestsStatistics();
    try {
        this.logMessage("Feature: adjustable appliance (BoxWifi) mode management");
        
        // === SCENARIO 1: Getting the max mode index ===
        this.logMessage("  Scenario: getting the max mode index");
        this.logMessage("    When I call maxMode()");
        final int maxMode = this.wifiop.maxMode();
        this.statistics.updateStatistics();

        // === SCENARIO 2: Getting the current mode index ===
        this.logMessage("  Scenario: getting the current mode index");
        this.logMessage("    When I call currentMode()");
        int result = this.wifiop.currentMode();
        // CORRECTION: Mode initial = 0 (OFF)
        if (result != 0) {
            this.logMessage("      but was: " + result);
            this.statistics.incorrectResult();
        } else {
            this.statistics.updateStatistics();
        }

        // === SCENARIO 3: Going up one mode index ===
        this.logMessage("  Scenario: going up one mode index");
        this.logMessage("    When I call upMode()");
        boolean bResult = this.wifiop.upMode();
        if (!bResult) {
            this.logMessage("      but was: " + bResult);
            this.statistics.incorrectResult();
        }
        result = this.wifiop.currentMode();
        // CORRECTION: Après upMode() depuis 0 → devrait être 1
        if (result != 1) {
            this.logMessage("      but was: " + result);
            this.statistics.incorrectResult();
        }
        this.statistics.updateStatistics();

        // === SCENARIO 4: Going up again ===
        this.logMessage("  Scenario: going up one more mode index");
        this.logMessage("    When I call upMode()");
        bResult = this.wifiop.upMode();
        if (!bResult) {
            this.logMessage("      but was: " + bResult);
            this.statistics.incorrectResult();
        }
        result = this.wifiop.currentMode();
        // CORRECTION: Après upMode() depuis 1 → devrait être 2
        if (result != 2) {
            this.logMessage("      but was: " + result);
            this.statistics.incorrectResult();
        }
        this.statistics.updateStatistics();

        // === SCENARIO 5: Going to max mode ===
        this.logMessage("  Scenario: going to max mode");
        this.logMessage("    When I call upMode()");
        bResult = this.wifiop.upMode();
        if (!bResult) {
            this.logMessage("      but was: " + bResult);
            this.statistics.incorrectResult();
        }
        result = this.wifiop.currentMode();
        // CORRECTION: Après upMode() depuis 2 → devrait être 3 (max)
        if (result != maxMode) {
            this.logMessage("      but was: " + result);
            this.statistics.incorrectResult();
        }
        this.statistics.updateStatistics();

        // === SCENARIO 6: Going down one mode index ===
        this.logMessage("  Scenario: going down one mode index");
        this.logMessage("    When I call downMode()");
        bResult = this.wifiop.downMode();
        if (!bResult) {
            this.logMessage("      but was: " + bResult);
            this.statistics.incorrectResult();
        }
        result = this.wifiop.currentMode();
        // CORRECTION: Après downMode() depuis 3 → devrait être 2
        if (result != 2) {
            this.logMessage("      but was: " + result);
            this.statistics.incorrectResult();
        }
        this.statistics.updateStatistics();

        // === SCENARIO 7: Setting specific mode index ===
        this.logMessage("  Scenario: setting the mode index to 1");
        this.logMessage("    When I call setMode(1)");
        bResult = this.wifiop.setMode(1);
        if (!bResult) {
            this.logMessage("      but was: " + bResult);
            this.statistics.incorrectResult();
        }
        result = this.wifiop.currentMode();
        // CORRECTION: Après setMode(1) → devrait être 1
        if (result != 1) {
            this.logMessage("      but was: " + result);
            this.statistics.incorrectResult();
        }
        this.statistics.updateStatistics();

        // === SCENARIO 8: Setting to OFF ===
        this.logMessage("  Scenario: setting the mode index to 0 (OFF)");
        this.logMessage("    When I call setMode(0)");
        bResult = this.wifiop.setMode(0);
        if (!bResult) {
            this.logMessage("      but was: " + bResult);
            this.statistics.incorrectResult();
        }
        result = this.wifiop.currentMode();
        // CORRECTION: Après setMode(0) → devrait être 0
        if (result != 0) {
            this.logMessage("      but was: " + result);
            this.statistics.incorrectResult();
        }
        this.statistics.updateStatistics();

        // === FEATURE: Suspending and resuming ===
        this.logMessage("Feature: suspending and resuming");
        
        // === SCENARIO 9: Checking if suspended when not ===
        this.logMessage("  Scenario: checking if suspended when not");
        bResult = this.wifiop.suspended();
        if (bResult) {
            this.logMessage("      but it was!");
            this.statistics.incorrectResult();
        }
        this.statistics.updateStatistics();

        // === SCENARIO 10: Suspending ===
        this.logMessage("  Scenario: suspending");
        // D'abord, aller à un mode actif pour tester la suspension
        this.wifiop.setMode(2); // Mode BOX_ONLY
        bResult = this.wifiop.suspend();
        if (!bResult) {
            this.logMessage("      but was: " + bResult);
            this.statistics.incorrectResult();
        }
        bResult = this.wifiop.suspended();
        if (!bResult) {
            this.logMessage("      but it was not!");
            this.statistics.incorrectResult();
        }
        // Vérifier que currentMode retourne 0 quand suspendu
        result = this.wifiop.currentMode();
        if (result != 0) {
            this.logMessage("      currentMode should be 0 when suspended, but was: " + result);
            this.statistics.incorrectResult();
        }
        this.statistics.updateStatistics();

        // === SCENARIO 11: Checking emergency ===
        this.logMessage("  Scenario: checking emergency");
        double dResult = this.wifiop.emergency();
        if (dResult < 0.0 || dResult > 1.0) {
            this.logMessage("      but was: " + dResult);
            this.statistics.incorrectResult();
        }
        this.statistics.updateStatistics();

        // === SCENARIO 12: Resuming ===
        this.logMessage("  Scenario: resuming");
        bResult = this.wifiop.resume();
        if (!bResult) {
            this.logMessage("      but was: " + bResult);
            this.statistics.incorrectResult();
        }
        bResult = this.wifiop.suspended();
        if (bResult) {
            this.logMessage("      but it was!");
            this.statistics.incorrectResult();
        }
        // Vérifier qu'on est revenu au mode précédent (2)
        result = this.wifiop.currentMode();
        if (result != 2) {
            this.logMessage("      should return to previous mode 2, but was: " + result);
            this.statistics.incorrectResult();
        }
        this.statistics.updateStatistics();

    } catch (Exception e) {
        this.logMessage("❌ Exception in testWifi: " + e.getMessage());
        e.printStackTrace();
    }

    this.statistics.updateStatistics();
    this.statistics.statisticsReport(this);
    this.logMessage("Wifi tests end.");
}

 
 
 
 
 
 
    protected void scheduleTestWifi() {
        // schedule testWifi after some delay based on the clock, similar to heater
        Instant wifiTestStart = this.ac.getStartInstant().plusSeconds(5); // small delay example
        this.traceMessage("HEM schedules the wifi test.\n");
        long delay = this.ac.nanoDelayUntilInstant(wifiTestStart);

        this.scheduleTaskOnComponent(
                new AbstractComponent.AbstractTask() {
                    @Override
                    public void run() {
                        try {
                            testWifi();
                        } catch (Exception e) {
                            throw new BCMRuntimeException(e);
                        }
                    }
                }, delay, TimeUnit.NANOSECONDS);
    }
    
    
        
        // Maps pour gérer les appareils enregistrés
        protected Map<String, String> registeredAppliances = new HashMap<>();
        protected Map<String, AdjustableOutboundPort> appliancePorts = new HashMap<>();
        protected Map<String, String> xmlDescriptors = new HashMap<>();
        public static final String HEM_REGISTRATION_URI = "HEM-REGISTRATION-PORT-URI";
    
 // RegistrationCI methods - IMPLEMENTATION COMPLÈTE
    @Override
    public boolean registered(String uid) throws Exception {
        boolean isRegistered = registeredAppliances.containsKey(uid);
        return isRegistered;
    }

   @Override
public boolean register(String uid, String controlPortURI, String xmlControlAdapter) throws Exception {
   
    // Vérifier si l'appareil est déjà enregistré
    if (registeredAppliances.containsKey(uid)) {
        System.out.println("Appareil déjà enregistré: " + uid);
        return false;
    }
    
    try {
        
        // 1. Générer le connecteur depuis le XML
        Class<?> connectorClass = XMLConnectorGenerator.generateConnector(xmlControlAdapter);
        String connectorClassName = connectorClass.getCanonicalName();
        
        // 2. Créer le port outbound pour cet appareil
        AdjustableOutboundPort appliancePort = new AdjustableOutboundPort(this);
        appliancePort.publishPort();
        
        // 3. Connecter le port avec le connecteur approprié
        this.doPortConnection(
            appliancePort.getPortURI(),
            controlPortURI,
            connectorClassName
        );
        
        // 4. Tester la connexion
        try {
            int maxMode = appliancePort.maxMode();
            int currentMode = appliancePort.currentMode();
            System.out.println("   Connection test successful");
            System.out.println("   Max mode: " + maxMode + ", Current mode: " + currentMode);
        } catch (Exception e) {
            System.err.println("Connection test failed: " + e.getMessage());
            // Nettoyer en cas d'échec
            this.doPortDisconnection(appliancePort.getPortURI());
            appliancePort.unpublishPort();
            return false;
        }
        
        // 5. Stocker les références
        registeredAppliances.put(uid, controlPortURI);
        appliancePorts.put(uid, appliancePort);
        xmlDescriptors.put(uid, xmlControlAdapter);
            
        // LANCER LES TESTS IMMÉDIATEMENT APRÈS ENREGISTREMENT RÉUSSI
        this.scheduleTask(
            new AbstractComponent.AbstractTask() {
                @Override
                public void run() {
                    try {
                        launchTestsAfterRegistration(uid);
                    } catch (Exception e) {
                        System.err.println("Error launching tests for " + uid + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }, 
            1000, TimeUnit.MILLISECONDS // Délai de 1 seconde pour laisser le temps à l'enregistrement de se stabiliser
        );
        
        return true;
        
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
   protected synchronized void launchTestsAfterRegistration(String applianceUID) throws Exception {
	    this.logMessage(" ===== TESTS AUTOMATIQUES APRÈS ENREGISTREMENT =====");
	        
	    AdjustableOutboundPort appliancePort = appliancePorts.get(applianceUID);
	    if (appliancePort != null) {
	        this.testWifiOnPort(appliancePort);
	    } else {
	        this.logMessage("ERREUR: Port non trouvé pour: " + applianceUID);
	        }
	}
    @Override
    public void unregister(String uid) throws Exception {
 
        if (!registeredAppliances.containsKey(uid)) {
            System.out.println("  Appareil non enregistré: " + uid);
            return;
        }
        
        try {
            // 1. Récupérer le port
            AdjustableOutboundPort port = appliancePorts.get(uid);
            
            if (port != null) {
                // 2. Déconnecter le port
                this.doPortDisconnection(port.getPortURI());
                
                // 3. Dépublier le port
                port.unpublishPort();
            }
            
            // 4. Supprimer les références
            registeredAppliances.remove(uid);
            appliancePorts.remove(uid);
            xmlDescriptors.remove(uid);
            
               
        } catch (Exception e) {
            System.err.println(" Erreur lors du désenregistrement de " + uid);
            System.err.println("   Cause: " + e.getMessage());
            throw e;
        }
    }
}
