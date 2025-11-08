package fr.sorbonne_u.components.hem2025e1.equipments.hem;

import javassist.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

/**
 * Générateur de connecteurs à partir de XML avec Javassist
 */
public class XMLConnectorGenerator {
    
    public static Class<?> generateConnector(String xmlDescriptor) throws Exception {
        
        // 1. Parser le XML
        Document doc = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(new ByteArrayInputStream(xmlDescriptor.getBytes()));
        
        Element root = doc.getDocumentElement();
        String uid = root.getAttribute("uid");
        String offeredInterface = root.getAttribute("offered");
        
        // 2. Créer la classe avec Javassist
        ClassPool pool = ClassPool.getDefault();
        String className = "GeneratedConnector_" + System.currentTimeMillis();
        CtClass connectorClass = pool.makeClass(className);
        
        // 3. Définir l'héritage
        connectorClass.setSuperclass(pool.get("fr.sorbonne_u.components.connectors.AbstractConnector"));
        
        // 4. Ajouter les interfaces - SEULEMENT AdjustableCI
        connectorClass.addInterface(pool.get("fr.sorbonne_u.components.hem2025.bases.AdjustableCI"));
        
        // 5. Ajouter les variables d'instance depuis le XML
        addInstanceVariables(connectorClass, root);
        
        // 6. Ajouter les méthodes AdjustableCI uniquement
        addAdjustableMethods(connectorClass);
        
        // 7. Retourner la classe générée
        Class<?> generatedClass = connectorClass.toClass();
        System.out.println("✅ Connector generated: " + generatedClass.getCanonicalName());
        return generatedClass;
    }
    
    private static void addInstanceVariables(CtClass connectorClass, Element root) throws Exception {
        NodeList instanceVars = root.getElementsByTagName("instance-var");
        for (int i = 0; i < instanceVars.getLength(); i++) {
            Element var = (Element) instanceVars.item(i);
            String modifiers = var.getAttribute("modifiers");
            String type = var.getAttribute("type");
            String name = var.getAttribute("name");
            String staticInit = var.getAttribute("static-init");
            
            CtField field = CtField.make(
                modifiers + " " + type + " " + name + 
                (staticInit.isEmpty() ? ";" : " = " + staticInit + ";"), 
                connectorClass
            );
            connectorClass.addField(field);
            System.out.println("   Added field: " + modifiers + " " + type + " " + name);
        }
    }
    
    /**
     * VERSION CORRIGÉE - Cast direct à chaque appel de méthode
     */
    private static void addAdjustableMethods(CtClass connectorClass) throws Exception {
        String fullInterfaceName = "fr.sorbonne_u.components.hem2025e1.equipement.Wifi.BoxWifiImplementationI";
        
        // maxMode
        addMethod(connectorClass, "maxMode", "int", "return 3;");
        
        // upMode - CAST DIRECT DANS CHAQUE APPEL
        addMethod(connectorClass, "upMode", "boolean", 
            "if (this.isSuspended || this.currentMode >= 3) return false;\n" +
            "try { \n" +
            "  this.currentMode++; \n" +
            "  if (this.currentMode == 1) { " +
            "    ((" + fullInterfaceName + ")this.offering).turnOn(); " +
            "    ((" + fullInterfaceName + ")this.offering).deactivateWifi(); " +
            "  }\n" +
            "  else if (this.currentMode == 2) { " +
            "    ((" + fullInterfaceName + ")this.offering).turnOn(); " +
            "    ((" + fullInterfaceName + ")this.offering).activateWifi(); " +
            "  }\n" +
            "  else if (this.currentMode == 3) { " +
            "    ((" + fullInterfaceName + ")this.offering).turnOn(); " +
            "    ((" + fullInterfaceName + ")this.offering).activateWifi(); " +
            "  }\n" +
            "} catch(Exception e) { e.printStackTrace(); return false; }\n" +
            "return true;"
        );
        
        // downMode - CAST DIRECT DANS CHAQUE APPEL
        addMethod(connectorClass, "downMode", "boolean",
            "if (this.isSuspended || this.currentMode <= 0) return false;\n" +
            "try { \n" +
            "  this.currentMode--; \n" +
            "  if (this.currentMode == 0) { " +
            "    ((" + fullInterfaceName + ")this.offering).turnOff(); " +
            "  }\n" +
            "  else if (this.currentMode == 1) { " +
            "    ((" + fullInterfaceName + ")this.offering).turnOn(); " +
            "    ((" + fullInterfaceName + ")this.offering).deactivateWifi(); " +
            "  }\n" +
            "  else if (this.currentMode == 2) { " +
            "    ((" + fullInterfaceName + ")this.offering).turnOn(); " +
            "    ((" + fullInterfaceName + ")this.offering).activateWifi(); " +
            "  }\n" +
            "} catch(Exception e) { e.printStackTrace(); return false; }\n" +
            "return true;"
        );
        
        // setMode - CAST DIRECT DANS CHAQUE APPEL
        addMethodWithParam(connectorClass, "setMode", "boolean", "int", "modeIndex",
            "if (this.isSuspended || modeIndex < 0 || modeIndex > 3) return false;\n" +
            "try { \n" +
            "  this.currentMode = modeIndex; \n" +
            "  if (modeIndex == 0) { " +
            "    ((" + fullInterfaceName + ")this.offering).turnOff(); " +
            "  }\n" +
            "  else if (modeIndex == 1) { " +
            "    ((" + fullInterfaceName + ")this.offering).turnOn(); " +
            "    ((" + fullInterfaceName + ")this.offering).deactivateWifi(); " +
            "  }\n" +
            "  else if (modeIndex == 2) { " +
            "    ((" + fullInterfaceName + ")this.offering).turnOn(); " +
            "    ((" + fullInterfaceName + ")this.offering).activateWifi(); " +
            "  }\n" +
            "  else if (modeIndex == 3) { " +
            "    ((" + fullInterfaceName + ")this.offering).turnOn(); " +
            "    ((" + fullInterfaceName + ")this.offering).activateWifi(); " +
            "  }\n" +
            "} catch(Exception e) { e.printStackTrace(); return false; }\n" +
            "return true;"
        );
        
        // currentMode
        addMethod(connectorClass, "currentMode", "int", 
            "if (this.isSuspended) return 0; else return this.currentMode;"
        );
        
        // getModeConsumption - valeurs fixes
        addMethodWithParam(connectorClass, "getModeConsumption", "double", "int", "modeIndex",
            "if (modeIndex == 0) return 0.0;\n" +
            "else if (modeIndex == 1) return 8.0;\n" +
            "else if (modeIndex == 2) return 10.0;\n" +
            "else if (modeIndex == 3) return 12.0;\n" +
            "else return 0.0;"
        );
        
        // suspended
        addMethod(connectorClass, "suspended", "boolean", "return this.isSuspended;");
        
        // suspend - CAST DIRECT
        addMethod(connectorClass, "suspend", "boolean",
            "try { \n" +
            "  ((" + fullInterfaceName + ")this.offering).turnOff();\n" +
            "  this.isSuspended = true; \n" +
            "} catch(Exception e) { e.printStackTrace(); return false; }\n" +
            "return true;"
        );
        
        // resume - CAST DIRECT DANS CHAQUE APPEL
        addMethod(connectorClass, "resume", "boolean",
            "try { \n" +
            "  this.isSuspended = false; \n" +
            "  if (this.currentMode == 0) { " +
            "    ((" + fullInterfaceName + ")this.offering).turnOff(); " +
            "  }\n" +
            "  else if (this.currentMode == 1) { " +
            "    ((" + fullInterfaceName + ")this.offering).turnOn(); " +
            "    ((" + fullInterfaceName + ")this.offering).deactivateWifi(); " +
            "  }\n" +
            "  else if (this.currentMode == 2) { " +
            "    ((" + fullInterfaceName + ")this.offering).turnOn(); " +
            "    ((" + fullInterfaceName + ")this.offering).activateWifi(); " +
            "  }\n" +
            "  else if (this.currentMode == 3) { " +
            "    ((" + fullInterfaceName + ")this.offering).turnOn(); " +
            "    ((" + fullInterfaceName + ")this.offering).activateWifi(); " +
            "  }\n" +
            "} catch(Exception e) { e.printStackTrace(); return false; }\n" +
            "return true;"
        );
        
        // emergency - valeurs fixes basées sur le mode
        addMethod(connectorClass, "emergency", "double",
            "if (this.currentMode == 0) return 0.0;\n" +
            "else if (this.currentMode == 1) return 0.3;\n" +
            "else if (this.currentMode == 2) return 0.5;\n" +
            "else if (this.currentMode == 3) return 0.8;\n" +
            "else return 0.0;"
        );
    }
    
    private static void addMethod(CtClass connectorClass, String methodName, String returnType, String body) throws Exception {
        CtMethod method = CtMethod.make(
            "public " + returnType + " " + methodName + "() throws Exception { " + body + " }", 
            connectorClass
        );
        connectorClass.addMethod(method);
        System.out.println("   Added method: " + methodName);
    }
    
    private static void addMethodWithParam(CtClass connectorClass, String methodName, String returnType, 
                                         String paramType, String paramName, String body) throws Exception {
        CtMethod method = CtMethod.make(
            "public " + returnType + " " + methodName + "(" + paramType + " " + paramName + ") throws Exception { " + body + " }", 
            connectorClass
        );
        connectorClass.addMethod(method);
        System.out.println("   Added method: " + methodName + "(" + paramType + " " + paramName + ")");
    }
}