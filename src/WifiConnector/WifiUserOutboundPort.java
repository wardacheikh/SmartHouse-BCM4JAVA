package WifiConnector;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipement.Wifi.BoxWifiImplementationI;
import fr.sorbonne_u.components.hem2025e1.equipement.Wifi.BoxWifiUserCI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class WifiUserOutboundPort extends AbstractOutboundPort implements BoxWifiUserCI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WifiUserOutboundPort(Class<? extends OfferedCI> implementedInterface, ComponentI owner) throws Exception {
		super((Class<? extends RequiredCI>) implementedInterface, owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void turnOn() throws Exception {
		 ((BoxWifiUserCI)this.getConnector()).turnOn();		

	}

	@Override
	public void turnOff() throws Exception {
		 ((BoxWifiUserCI)this.getConnector()).turnOff();	

	}

	@Override
	public void activateWifi() throws Exception {
		 ((BoxWifiUserCI)this.getConnector()).activateWifi();	

		
	}

	@Override
	public void deactivateWifi() throws Exception {
		 ((BoxWifiUserCI)this.getConnector()).deactivateWifi();		

	}

	@Override
	public BoxWifiMode getMode() throws Exception {
		return 		 ((BoxWifiUserCI)this.getConnector()).getMode();

}

	@Override
	public boolean isOn() throws Exception {
		return 		 ((BoxWifiUserCI)this.getConnector()).isOn();	

	}




}
