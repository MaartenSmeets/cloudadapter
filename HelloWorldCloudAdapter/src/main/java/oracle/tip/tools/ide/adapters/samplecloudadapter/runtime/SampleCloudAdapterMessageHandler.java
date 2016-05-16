/**
 * SampleCloudAdapterMessageHandler implements CloudMessageHandler interface.
 * This class will redirect to operation specific implementation / handler class based on selection of operation.
 */

package oracle.tip.tools.ide.adapters.samplecloudadapter.runtime;

import java.io.StringWriter;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Random;
import java.util.TimeZone;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;

import javax.xml.transform.TransformerFactory;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import oracle.cloud.connector.api.AuthenticationManager;
import oracle.cloud.connector.api.CloudInvocationContext;
import oracle.cloud.connector.api.CloudInvocationException;
import oracle.cloud.connector.api.CloudMessage;
import oracle.cloud.connector.api.CloudMessageHandler;
import oracle.cloud.connector.api.CloudRuntimeConstants;
import oracle.cloud.connector.api.MessageHeader;
import oracle.cloud.connector.api.CloudAdapterLoggingService.Level;
import oracle.cloud.connector.impl.BasicAuthenticationManager;
import oracle.cloud.connector.impl.CloudAdapterUtil;
import oracle.cloud.connector.impl.SOAPHeaderBuilder;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.wsdl.Operation;
import javax.wsdl.Part;

import javax.wsdl.PortType;

import oracle.cloud.connector.api.CloudAdapterLoggingService;

import oracle.tip.tools.ide.adapters.samplecloudadapter.util.SampleCloudAdapterConstants;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SampleCloudAdapterMessageHandler implements CloudMessageHandler {
    
    int counter = 0;
    public static String XML_NS = "http://www.w3.org/2000/xmlns/";
    // During runtime, sample Cloud adapter handles only one service operation called "createAccount". Adapter developer should implement for all the service operations.
    private static String CREATE_OPERATION = "process";
    
    // OperationHandler (list) is an adapter specific interface to handle message request, response and fault for all the service operations. In this case, it is createAccount only
    private Map<String,OperationHandler> operationHandlers = new HashMap<String, OperationHandler>();
        
    public SampleCloudAdapterMessageHandler() {
        
        // Add createAccount operation handler to operationhanlders list.
        operationHandlers.put(CREATE_OPERATION, new CreateOperationHandler());
    }

    /**
     * To handle messasge request
     * @param context
     * @param message
     * @return
     * @throws CloudInvocationException
     */
    @Override
    public boolean handleRequestMessage(CloudInvocationContext context,
                                        CloudMessage message) throws CloudInvocationException {
      
        System.out.println("SampleCloudAdapterMessageHandler handleRequestMessage");
        try{
        this.normalizeRootElement(context, message.getMessagePayloadAsDocument());
        new XmlNamespaceTranslator()
                .addTranslation(null, SampleCloudAdapterConstants.SAMPLE_MESSAGES_NAMESPACE)
                .addTranslation("", SampleCloudAdapterConstants.SAMPLE_MESSAGES_NAMESPACE)
            .addTranslation("http://xmlns.oracle.com/pcbpel/adapter/sample/HelloWorldApp/GreetingServiceCaller/SampleReference/types", SampleCloudAdapterConstants.SAMPLE_MESSAGES_NAMESPACE)
                .translateNamespaces(message.getMessagePayloadAsDocument());
        processRequestHeaders(message, context.getCloudOperationProperties(), context.getAuthenticationManager());
            
        System.out.println("SampleCloudAdapterMessageHandler handleRequestMessage Message: " + printXML(message.getMessagePayloadAsDocument()));
        }catch (Exception e) {
                context.getLoggingService().logError("SampleCloudAdapterMessageHandler#handleRequestMessage", e);
                throw new CloudInvocationException(e);
        }
        
        return true;
        
        
    }
    
    protected Element normalizeRootElement(CloudInvocationContext context,
                    Document requestDocument) {
            
        Definition targetWSDL = (Definition) context
                        .getContextObject(CloudRuntimeConstants.TARGET_WSDL_DEFINITION);
        QName targetRootElementName = new QName(
                        targetWSDL.getTargetNamespace(),
                        context.getTargetOperationName());
        
        Element sourceRootElement = requestDocument.getDocumentElement();
        String originalNamespace = sourceRootElement.getNamespaceURI();
        String originalPrefix = sourceRootElement.getPrefix();
        String cloudAppNS = SampleCloudAdapterConstants.SAMPLE_MESSAGES_NAMESPACE;
        String prefix = context.getNamespaceManager().getOrCreatePrefix(
                        cloudAppNS);
        if (sourceRootElement.lookupNamespaceURI(prefix) == null) {
                sourceRootElement.setAttributeNS(XML_NS, "xmlns:" + prefix,
                                cloudAppNS);
        }
        sourceRootElement = (Element) requestDocument.renameNode(
                        sourceRootElement, cloudAppNS, prefix + ":"
                                        + targetRootElementName.getLocalPart());
        if (originalPrefix == null) {
                sourceRootElement
                                .setAttributeNS(XML_NS, "xmlns", originalNamespace);
        } else {
                sourceRootElement.setAttributeNS(XML_NS, "xmlns:" + originalPrefix,
                                originalNamespace);
        }
        //sourceRootElement.normalize();
        return sourceRootElement;
    }
    
    @Override
    public boolean handleResponseMessage(CloudInvocationContext context,CloudMessage message)
                    throws CloudInvocationException {
     
            Definition targetWSDL = (Definition) context
                            .getContextObject(CloudRuntimeConstants.TARGET_WSDL_DEFINITION);
            Definition integrationWsdl = context.getIntegrationWSDL();

            Document doc = message.getMessagePayloadAsDocument();
           
            message.setMessagePayload(this.getResponseAdjustedPayload(context, doc,
                            integrationWsdl, targetWSDL));
            
            return true;
    }
    
    private Object getResponseAdjustedPayload(CloudInvocationContext context,
                    Document messagePayloadAsDocument, Definition integrationWsdl,
                    Definition targetWSDL) {
            Element adapterPayload = messagePayloadAsDocument.getDocumentElement();           
      
            // To get integration WSDL operation element name
            Operation operation = getWSDLOperation(integrationWsdl);
            Part part = operation.getOutput().getMessage().getPart("parameters");
            QName newResponseQName = part.getElementName();            
        
            String newResponseName = (newResponseQName.getPrefix() != null) ? newResponseQName.getPrefix() + ":" + newResponseQName.getLocalPart()
                        : newResponseQName.getLocalPart();
            adapterPayload = (Element) messagePayloadAsDocument.renameNode(adapterPayload, newResponseQName.getNamespaceURI(),
                        newResponseName);
      
            NodeList list = adapterPayload.getChildNodes();
            int len = list.getLength();
            for (int i = 0; i < len; i++) {
                    Node n = list.item(i);
                    if (n.getNodeType() == Node.ELEMENT_NODE) {
                            messagePayloadAsDocument.renameNode(n, newResponseQName.getNamespaceURI(),
                                            newResponseQName.getPrefix() + ":" + n.getLocalName());
                    }
            }
            return adapterPayload;
    }

    private Operation getWSDLOperation(Definition integrationWSDL) {
            Map portTypes = integrationWSDL.getPortTypes();
            for (Object key : portTypes.keySet()) {
                    PortType pt = (PortType) portTypes.get(key);
                    return (Operation) pt.getOperations().get(0);
            }
            return null;
    }
    
    /**
     * To handle error message     * 
     * @param cloudInvocationContext
     * @param cloudMessage
     * @return
     * @throws CloudInvocationException
     */
    @Override
    public boolean handleErrorMessage(CloudInvocationContext cloudInvocationContext,
                                      CloudMessage cloudMessage) throws CloudInvocationException {
        return false;
    }
    
    protected void processRequestHeaders(CloudMessage message,
                    Map jcaEndpointInteractionProperties,
                    AuthenticationManager authManager) throws CloudInvocationException {
            
            //MessageHeader header = createWSSHeader(jcaEndpointInteractionProperties,authManager);
            //message.addMessageHeader(header);
            
            
            Document doc = message.getMessagePayloadAsDocument();
            try {
                    String xmlString = this.printXML(doc);
                    // Document d = this.stringToDom(xmlString);
                    // message.setMessagePayload(d.getDocumentElement());
            } catch (Exception e) {
                    e.printStackTrace();
                    throw (new CloudInvocationException(e));
            }
    }
    
    /**
     * Construct SOAP Header if any message level authentication
     * @param jcaEndpointInteractionProperties
     * @param authManager
     * @return
     * @throws CloudInvocationException
     */
    private MessageHeader createWSSHeader(
                    Map jcaEndpointInteractionProperties,
                    AuthenticationManager authManager) throws CloudInvocationException {    
        
            QName wssHeaderName = new QName(SampleCloudAdapterConstants.WSS_NS_URI, "Security",
                                            "wsse");
            QName wssUserNameToken = new QName(SampleCloudAdapterConstants.WSS_NS_URI,
                            "UsernameToken", "wsse");
            QName wssUserName = new QName(SampleCloudAdapterConstants.WSS_NS_URI, "Username",
                            "wsse");
            QName wsspasswordName = new QName(SampleCloudAdapterConstants.WSS_NS_URI, "Password",
                            "wsse");
            Map<String, String> authProperties = authManager
                            .getAuthenticationProperties();
            String userName = authProperties
                            .get(BasicAuthenticationManager.USERNAME_PROPERTY);
            /*
             * if (userName == null) { userName = (String)
             * jcaEndpointConnectionProperties.get("username"); }
             */
            String password = authProperties
                            .get(BasicAuthenticationManager.PASSWORD_PROPERTY);
            /*
             * if (password == null) { password = (String)
             * jcaEndpointConnectionProperties.get("password"); }
             */
            SOAPHeaderBuilder builder = new SOAPHeaderBuilder(wssHeaderName, true,
                            null);
            MessageHeader messageHeader = builder.createHeader(wssUserNameToken);
            //messageHeader.addHeaderAttribute(arg0, arg1);
                
            //    .setAttribute("env:mustUnderstand", "0");
            messageHeader.addHeaderAttribute("wsu:Id",
                            "UsernameToken-" + Integer.toString(counter++));
                        
            messageHeader.addChild(builder.createHeader(wssUserName, userName));
            MessageHeader passwordHeader = builder.createHeader(wsspasswordName,
                            password);
            
        //she.setAttribute("env:mustUnderstand", "0");
            
            passwordHeader.addHeaderAttribute(
                                            "Type",
                                            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
            messageHeader.addChild(passwordHeader);

            // nonce

            QName nonce = new QName(SampleCloudAdapterConstants.WSS_NS_URI, "Nonce", "wsse");
            MessageHeader nonceHeader = builder.createHeader(nonce, new String(
                            Base64.encode(this.getRandomString().getBytes())));
            nonceHeader
                            .addHeaderAttribute(
                                            "EncodingType",
                                            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary");
            messageHeader.addChild(nonceHeader);

            // created
            Date dNow = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            QName dt = new QName(SampleCloudAdapterConstants.WSU_NS_URI, "Created", "wsu");
            MessageHeader createdHeader = builder
                            .createHeader(dt, sdf.format(dNow));
            messageHeader.addChild(createdHeader);

            // Timestamp header
            QName wssTimestampToken = new QName(SampleCloudAdapterConstants.WSU_NS_URI,
                            "Timestamp", "wsu");
            MessageHeader timestampHeader = builder.createHeader(wssTimestampToken);
            timestampHeader.addHeaderAttribute("wsu:Id",
                            "TS-" + Integer.toString(counter++));

            timestampHeader.addChild(createdHeader);

            Date dLater = getLaterTime();
            dt = new QName(SampleCloudAdapterConstants.WSU_NS_URI, "Expires", "wsu");
            MessageHeader expiresHeader = builder.createHeader(dt,
                            sdf.format(dLater));
            timestampHeader.addChild(expiresHeader);

            builder.addChild(messageHeader);
            
                        builder.addChild(timestampHeader);
            
            MessageHeader wssHeader = builder.build();
            wssHeader.addHeaderAttribute("xmlns:wsu","http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
            
            
           // List<MessageHeader> mh = wssHeader.getChildren();
           
            return wssHeader;       
            
    }
    
    private String getRandomString() {
                    Random generator = new Random();
                    String str = String.valueOf(generator.nextInt(999999999));
                    return str;
            }

            private Date getLaterTime() {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    cal.add(Calendar.HOUR_OF_DAY, 1);
                    return cal.getTime();
            }
            
    /**
     * Utility method to print request and repsonse payload during runtime
     * @param doc
     * @return
     * @throws Exception
     */
    private String printXML(Document doc) throws Exception {
                    Transformer transformer = TransformerFactory.newInstance()
                                    .newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    StreamResult result = new StreamResult(new StringWriter());
                    DOMSource source = new DOMSource(doc);
                    transformer.transform(source, result);
                    String xmlString = result.getWriter().toString();
                    return xmlString;
            }
    
    
}
