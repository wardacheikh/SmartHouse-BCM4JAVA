package fr.sorbonne_u.components.hem2025e1.equipement.Wifi;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface BoxWifiImplementationI extends OfferedCI,
RequiredCI {
	
	
	public static enum BoxWifiMode {
	     	/** La Box est complètement éteinte.				*/
		    OFF,
			
		    /** La Box fonctionne  mais le WiFi est coupé.					*/
		    BOX_ONLY,
	
		    /**uniquement le WiFi fonctionne pas d’autres services de la box (téléphone, TV).				*/
		    WIFI_ONLY,
			
		    /** Tout est actif (Box + WiFi).				*/
		    FULL_ON
		    
		    
	}
	
	
 // ----------------- Méthodes principales -----------------
    
    /** Allume la box (par défaut sans WiFi). */
    public void turnOn() throws Exception;

    /** Éteint complètement la box (WiFi coupé aussi). */
    public void turnOff() throws Exception;

    /** Active le WiFi (si la box est allumée). */
    public void activateWifi() throws Exception;

    /** Désactive le WiFi (mais garde la box allumée). */
    public void deactivateWifi() throws Exception;

    /** Retourne le mode courant de la box. */
    public BoxWifiMode getMode() throws Exception;

    /** Vérifie si la box est allumée. */
    public boolean isOn() throws Exception;
    
    
    /** CHECK AVAILABLE NETWORK. */

	

}
