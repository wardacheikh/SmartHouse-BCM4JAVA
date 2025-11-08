package fr.sorbonne_u.components.hem2025e1.equipement.Wifi;


import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface BoxWifiExternalControlCI
extends OfferedCI,
        RequiredCI,
        BoxWifiExternalControlI
{ 
        	/**
             * Retourne la puissance maximale consommée par la box en mode FULL_ON.
             * (Par exemple : 12 W)
             */
            public double getMaxPowerLevel() throws Exception;

            /**
             * Définit la puissance actuelle selon le mode choisi (OFF, WIFI_ONLY, BOX_ONLY, FULL_ON)
             */
            public void setCurrentPowerLevel(double powerLevel) throws Exception;

            /**
             * Retourne la puissance actuelle de la box.
             */
            public double getCurrentPowerLevel() throws Exception;
            
            /**
             * Active le WiFi pendant une durée limitée (en minutes).
             * Exemple : activer le WiFi 60 minutes pendant une plage horaire.
             */
            public void activateWifiForDuration(int minutes) throws Exception;

            /**
             * Programme un changement de mode de la box à une heure donnée.
             * Exemple : passer en WIFI_ONLY à 22h.
             */
            public void scheduleModeChange(BoxWifiMode mode, String time) throws Exception;

            /**
             * Retourne la charge actuelle du réseau (entre 0.0 et 1.0).
             * Permet au gestionnaire d'énergie d'adapter la puissance selon l'utilisation.
             */
            public double getNetworkLoad() throws Exception;
            }
