package fr.sorbonne_u.components.hem2025e1.equipement.Wifi;

import fr.sorbonne_u.components.AbstractComponent ;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEM;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeter;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

/**
 * CVM complet pour tester l'intégration BoxWifi + HEMWifi avec enregistrement dynamique
 * 
 * <p>Ce CVM démontre le scénario complet d'enregistrement dynamique :</p>
 * <ol>
 *   <li>Déploiement des services de base (ClocksServer, ElectricMeter)</li>
 *   <li>Déploiement du HEMWifi qui offre le service d'enregistrement</li>
 *   <li>Déploiement de BoxWifi qui s'enregistre automatiquement auprès du HEM</li>
 *   <li>Exécution des tests via le HEM sur l'appareil enregistré</li>
 * </ol>
 */
public class CVMIntegrationTest extends AbstractCVM {

    // URI de l'horloge accélérée
    public static final String CLOCK_URI = "test-clock";
    
    // Délai d'exécution total (2 minutes)
    private static final long TOTAL_DURATION = 120000L;
    
    // URI du port d'enregistrement du HEM (DOIT correspondre à celui utilisé dans BoxWifi)
    public static final String HEM_REGISTRATION_URI = "HEM-REGISTRATION-PORT-URI";

    public CVMIntegrationTest() throws Exception {
        super();
        BoxWifi.VERBOSE = true;
        HEM.VERBOSE = true;
    }

    @Override
    public void deploy() throws Exception {
        
        // Créer le ClocksServer (NÉCESSAIRE en premier)
        AbstractComponent.createComponent(
            ClocksServer.class.getCanonicalName(),
            new Object[]{}
        );
        Thread.sleep(2000);
        // Créer ElectricMeter (pour la compatibilité avec HEMWifi)
        AbstractComponent.createComponent(
            ElectricMeter.class.getCanonicalName(),
            new Object[]{}
        );
        Thread.sleep(2000);

        // Créer le HEM (gestionnaire d'énergie) - DOIT être avant les appareils
       AbstractComponent.createComponent(
    		   HEM.class.getCanonicalName(),
            new Object[]{true} // true → activation des tests
        );
        Thread.sleep(3000); // Donner plus de temps pour l'initialisation

        // Créer la Box Wifi (va s'enregistrer automatiquement)
        AbstractComponent.createComponent(
            BoxWifi.class.getCanonicalName(),
            new Object[]{}
        );
        Thread.sleep(5000); // Donner du temps pour l'enregistrement automatique     
        super.deploy();
    }

    @Override
    public void start() throws Exception {
        super.start();
    }

    @Override
    public void execute() throws Exception {
        super.execute();
        Thread.sleep(3000);
    }

    @Override
    public void finalise() throws Exception {
        super.finalise();
    }

    @Override
    public void shutdown() throws Exception {
        super.shutdown();
    }

    public static void main(String[] args) {
        try {
            System.out.println("===== DÉMARRAGE DU TEST D'INTÉGRATION AVEC ENREGISTREMENT DYNAMIQUE =====");
            System.out.println("" + new java.util.Date());
     
            // Création du CVM
            CVMIntegrationTest cvm = new CVMIntegrationTest();

            
            // Lancement du cycle de vie standard
            cvm.startStandardLifeCycle(TOTAL_DURATION);
            
           
            Thread.sleep(TOTAL_DURATION);
            
            
            System.exit(0);
            
        } catch (Exception e) {
              e.printStackTrace();
            System.exit(1);
        }
    }
}