package fr.sorbonne_u.components.hem2025e1.equipement.Wifi;

import fr.sorbonne_u.components.connectors.AbstractConnector;

public class BoxWifiConnector extends AbstractConnector implements BoxWifiUserCI{

	@Override
	public void turnOn() throws Exception {
		 ((BoxWifiUserCI)this.offering).turnOn();
	}

	@Override
	public void turnOff() throws Exception {
		 ((BoxWifiUserCI)this.offering).turnOff();

	}

	@Override
	public void activateWifi() throws Exception {
		 ((BoxWifiUserCI)this.offering).activateWifi();

	}

	@Override
	public void deactivateWifi() throws Exception {
		 ((BoxWifiUserCI)this.offering).deactivateWifi();

	}

	@Override
	public BoxWifiMode getMode() throws Exception {
		return ((BoxWifiUserCI)this.offering).getMode();

	}

	@Override
	public boolean isOn() throws Exception {
		return 		 ((BoxWifiUserCI)this.offering).isOn();

	}

}
