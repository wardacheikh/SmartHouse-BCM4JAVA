package WifiConnector;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipement.Wifi.BoxWifiAdjustableAdapterCI;
import fr.sorbonne_u.components.hem2025e1.equipement.Wifi.BoxWifiExternalControlI;
import fr.sorbonne_u.components.hem2025e1.equipement.Wifi.BoxWifiImplementationI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class BoxWifiExternalControlInboundPort
        extends AbstractInboundPort
        implements BoxWifiAdjustableAdapterCI { 

    private static final long serialVersionUID = 1L;

    public BoxWifiExternalControlInboundPort(ComponentI owner) throws Exception {
        super(BoxWifiAdjustableAdapterCI.class, owner); // ⬅️ Changez ici
        assert owner instanceof BoxWifiImplementationI;
    }

    public BoxWifiExternalControlInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, BoxWifiAdjustableAdapterCI.class, owner); // ⬅️ Changez ici
        assert owner instanceof BoxWifiImplementationI;
    }

    // Toutes les méthodes de BoxWifiExternalControlCI restent identiques
    @Override
    public double getMaxPowerLevel() throws Exception {
        return this.getOwner().handleRequest(
            o -> ((BoxWifiExternalControlI) o).getMaxPowerLevel()
        );
    }

    @Override
    public void setCurrentPowerLevel(double powerLevel) throws Exception {
        this.getOwner().handleRequest(
            o -> {
                ((BoxWifiExternalControlI) o).setCurrentPowerLevel(powerLevel);
                return null;
            }
        );
    }

    @Override
    public double getCurrentPowerLevel() throws Exception {
        return this.getOwner().handleRequest(
            o -> ((BoxWifiExternalControlI) o).getCurrentPowerLevel()
        );
    }

    @Override
    public BoxWifiMode getMode() throws Exception {
        return this.getOwner().handleRequest(
            o -> ((BoxWifiExternalControlI) o).getMode()
        );
    }

    @Override
    public void turnOn() throws Exception {
        this.getOwner().handleRequest(
            o -> {
                ((BoxWifiExternalControlI) o).turnOn();
                return null;
            }
        );
    }

    @Override
    public void turnOff() throws Exception {
        this.getOwner().handleRequest(
            o -> {
                ((BoxWifiExternalControlI) o).turnOff();
                return null;
            }
        );
    }

    @Override
    public void activateWifi() throws Exception {
        this.getOwner().handleRequest(
            o -> {
                ((BoxWifiExternalControlI) o).activateWifi();
                return null;
            }
        );
    }

    @Override
    public void deactivateWifi() throws Exception {
        this.getOwner().handleRequest(
            o -> {
                ((BoxWifiExternalControlI) o).deactivateWifi();
                return null;
            }
        );
    }

    @Override
    public boolean isOn() throws Exception {
        return this.getOwner().handleRequest(
            o -> ((BoxWifiExternalControlI) o).isOn()
        );
    }

    @Override
    public void activateWifiForDuration(int minutes) throws Exception {
        this.getOwner().handleRequest(
            o -> {
                ((BoxWifiExternalControlI) o).activateWifiForDuration(minutes);
                return null;
            }
        );
    }

    @Override
    public void scheduleModeChange(BoxWifiMode mode, String time) throws Exception {
        this.getOwner().handleRequest(
            o -> {
                ((BoxWifiExternalControlI) o).scheduleModeChange(mode, time);
                return null;
            }
        );
    }

    @Override
    public double getNetworkLoad() throws Exception {
        return this.getOwner().handleRequest(
            o -> ((BoxWifiExternalControlI) o).getNetworkLoad()
        );
    }

    // ⭐⭐⭐ AJOUTEZ LES MÉTHODES DE AdjustableCI (elles seront gérées par le connecteur) ⭐⭐⭐
    
    @Override
    public int maxMode() throws Exception {
        // Cette logique sera implémentée dans le connecteur
        return 0;
    }

    @Override
    public boolean upMode() throws Exception {
        // Cette logique sera implémentée dans le connecteur
        return false;
    }

    @Override
    public boolean downMode() throws Exception {
        // Cette logique sera implémentée dans le connecteur
        return false;
    }

    @Override
    public boolean setMode(int modeIndex) throws Exception {
        // Cette logique sera implémentée dans le connecteur
        return false;
    }

    @Override
    public int currentMode() throws Exception {
        // Cette logique sera implémentée dans le connecteur
        return 0;
    }

    @Override
    public double getModeConsumption(int modeIndex) throws Exception {
        // Cette logique sera implémentée dans le connecteur
        return 0.0;
    }

    @Override
    public boolean suspended() throws Exception {
        // Cette logique sera implémentée dans le connecteur
        return false;
    }

    @Override
    public boolean suspend() throws Exception {
        // Cette logique sera implémentée dans le connecteur
        return false;
    }

    @Override
    public boolean resume() throws Exception {
        // Cette logique sera implémentée dans le connecteur
        return false;
    }

    @Override
    public double emergency() throws Exception {
        // Cette logique sera implémentée dans le connecteur
        return 0.0;
    }
}