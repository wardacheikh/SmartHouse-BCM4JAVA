package WifiConnector;


import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025.bases.RegistrationCI;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEM;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * Port inbound pour recevoir les demandes d'enregistrement des appareils
 */
public class RegistrationInboundPort extends AbstractInboundPort implements RegistrationCI {
    
    private static final long serialVersionUID = 1L;

    public RegistrationInboundPort(ComponentI owner) throws Exception {
        super(RegistrationCI.class, owner);
        assert owner instanceof HEM;
    }

    public RegistrationInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, RegistrationCI.class, owner);
        assert owner instanceof HEM;
    }

    @Override
    public boolean registered(String uid) throws Exception {
        return this.getOwner().handleRequest(
            o -> ((HEM)o).registered(uid)
        );
    }

    @Override
    public boolean register(String uid, String controlPortURI, String xmlControlAdapter) throws Exception {
        System.out.println("📥 RegistrationInboundPort.register() called for: " + uid);
        return this.getOwner().handleRequest(
            o -> ((HEM)o).register(uid, controlPortURI, xmlControlAdapter)
        );
    }

    @Override
    public void unregister(String uid) throws Exception {
        this.getOwner().handleRequest(
            o -> { 
                ((HEM)o).unregister(uid); 
                return null; 
            }
        );
    }
}