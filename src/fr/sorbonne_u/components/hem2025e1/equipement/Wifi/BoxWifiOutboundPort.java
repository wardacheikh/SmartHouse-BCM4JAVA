package fr.sorbonne_u.components.hem2025e1.equipement.Wifi;

import fr.sorbonne_u.components.ComponentI;

import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class BoxWifiOutboundPort extends AbstractOutboundPort implements BoxWifiUserCI {

	
	private static final long serialVersionUID = 1L;

	
	
	public	BoxWifiOutboundPort(ComponentI owner)throws Exception
	{
		super(BoxWifiUserCI.class, owner);
	}

	public	BoxWifiOutboundPort(String uri, ComponentI owner)throws Exception
	{
		super(uri, BoxWifiUserCI.class, owner);
	}


	@Override
	public void turnOn() throws Exception {
		// TODO Auto-generated method stub
		 ((BoxWifiUserCI)this.getConnector()).turnOn();

	}

	@Override
	public void turnOff() throws Exception {
		// TODO Auto-generated method stub
		 ((BoxWifiUserCI)this.getConnector()).turnOff();

	}

	@Override
	public void activateWifi() throws Exception {
		// TODO Auto-generated method stub
		 ((BoxWifiUserCI)this.getConnector()).activateWifi();

	}

	@Override
	public void deactivateWifi() throws Exception {
		// TODO Auto-generated method stub
		 ((BoxWifiUserCI)this.getConnector()).deactivateWifi();

	}

	@Override
	public BoxWifiMode getMode() throws Exception {
		// TODO Auto-generated method stub
		return  ((BoxWifiUserCI)this.getConnector()).getMode();

	}

	@Override
	public boolean isOn() throws Exception {
		// TODO Auto-generated method stub
		return 		 ((BoxWifiUserCI)this.getConnector()).isOn();

	}

}
