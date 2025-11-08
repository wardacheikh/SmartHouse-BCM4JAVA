package WifiConnector;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipement.Wifi.BoxWifiExternalControlCI;
import fr.sorbonne_u.components.hem2025e1.equipement.Wifi.BoxWifiExternalControlI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class BoxWifiExternalControlOutboundPort
extends AbstractOutboundPort
implements BoxWifiExternalControlCI
{
    private static final long serialVersionUID = 1L;

    public BoxWifiExternalControlOutboundPort(ComponentI owner) throws Exception {
        super(BoxWifiExternalControlCI.class, owner);
    }

    public BoxWifiExternalControlOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, BoxWifiExternalControlCI.class, owner);
    }

    @Override
    public double getMaxPowerLevel() throws Exception {
        return ((BoxWifiExternalControlCI)this.connector).getMaxPowerLevel();
    }

    @Override
    public void setCurrentPowerLevel(double powerLevel) throws Exception {
        ((BoxWifiExternalControlCI)this.connector).setCurrentPowerLevel(powerLevel);
    }

    @Override
    public double getCurrentPowerLevel() throws Exception {
        return ((BoxWifiExternalControlCI)this.connector).getCurrentPowerLevel();
    }

    @Override
    public BoxWifiMode getMode() throws Exception {
        return ((BoxWifiExternalControlCI)this.connector).getMode();
    }

	@Override
	public void turnOn() throws Exception {
		// TODO Auto-generated method stub
         ((BoxWifiExternalControlCI)this.connector).turnOn();

	}

	@Override
	public void turnOff() throws Exception {
        ((BoxWifiExternalControlCI)this.connector).turnOff();
		
	}

	@Override
	public void activateWifi() throws Exception {
        ((BoxWifiExternalControlCI)this.connector).activateWifi();
		
	}

	@Override
	public void deactivateWifi() throws Exception {
        ((BoxWifiExternalControlCI)this.connector).deactivateWifi();
		
	}

	@Override
	public boolean isOn() throws Exception {
		return          ((BoxWifiExternalControlCI)this.connector).isOn();

	}

	@Override
	public void activateWifiForDuration(int minutes) throws Exception {
		this.getOwner().handleRequest(
	            o -> {
	                ((BoxWifiExternalControlCI) o).activateWifiForDuration(minutes);
	                return null;
	            }
	        );		
	}

	@Override
	public void scheduleModeChange(BoxWifiMode mode, String time) throws Exception {
		 this.getOwner().handleRequest(
		            o -> {
		                ((BoxWifiExternalControlCI) o).scheduleModeChange(mode, time);
		                return null;
		            }
		        );		
	}

	@Override
	public double getNetworkLoad() throws Exception {
		return    this.getOwner().handleRequest(
	            o -> ((BoxWifiExternalControlI) o).getNetworkLoad()
		        );
	}
}