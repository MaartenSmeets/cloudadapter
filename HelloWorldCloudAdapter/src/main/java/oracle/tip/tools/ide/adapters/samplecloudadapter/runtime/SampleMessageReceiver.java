/**
 * MessageReceiver class is required to submit the payload to integration layer(ICS or integration application will transform the payload and submit it to Target system).
 * Events, publish - subscribe and request callbacks requires message handlers. Message handlers can be implemented before or after submitting the request.
 */

package oracle.tip.tools.ide.adapters.samplecloudadapter.runtime;

import java.util.logging.Level;

import javax.wsdl.Definition;
import oracle.cloud.connector.api.CloudApplicationConnectionFactory;
import oracle.cloud.connector.api.CloudConnectorException;
import oracle.cloud.connector.api.CloudEndpointFactory;
import oracle.cloud.connector.api.CloudInvocationException;
import oracle.cloud.connector.api.CloudMessage;
import oracle.cloud.connector.impl.soap.BaseSOAPMessageReceiver;


import oracle.cloud.connector.api.CloudAdapterLoggingService;
import oracle.cloud.connector.api.CloudEndpoint;
import oracle.cloud.connector.api.CloudInvocationContext;
import oracle.cloud.connector.api.CloudRuntimeConstants;
import oracle.cloud.connector.api.NamespaceManager;
import oracle.cloud.connector.impl.CloudAdapterUtil;


import oracle.cloud.connector.impl.NamespaceManagerImpl;

import oracle.tip.tools.ide.adapters.samplecloudadapter.util.SampleCloudAdapterConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import oracle.xml.parser.v2.XMLDocument;

public class SampleMessageReceiver extends BaseSOAPMessageReceiver {
    
    private CloudEndpointFactory m_cloudEndpointFactory;
    private CloudEndpoint m_cloudEndpoint;
    private CloudAdapterLoggingService m_logger;
    
    private boolean m_isReleased = false;
    private CloudInvocationContext m_context;
    private String m_requestWsdlTargetNS;
        
    public SampleMessageReceiver(
                    CloudApplicationConnectionFactory connectionFactory,
                    CloudEndpointFactory cloudEndpointFactory)
                    throws CloudInvocationException, CloudConnectorException {
        
        m_cloudEndpoint = cloudEndpointFactory.createEndpoint();
        m_cloudEndpointFactory = cloudEndpointFactory;
        m_context = cloudEndpointFactory.getCloudInvocationContext();
        //m_logger = m_context.getLoggingService();
        m_context.setContextObject(SampleCloudAdapterConstants.IS_INBOUND, "true");

        // Lookup Cloud Application Configuration (.jca file) for integration WSDL entry
        Definition intgWsdlDef = cloudEndpointFactory.getCloudInvocationContext().getIntegrationWSDL();
        this.m_requestWsdlTargetNS = intgWsdlDef.getTargetNamespace();
        setExternalWSDL(intgWsdlDef);

    }

    /**
     * Payload submission to integration layer.
     * 
     * @param cloudMessage
     * @return
     * @throws Exception
     */
    public CloudMessage onMessage(CloudMessage cloudRequestMessage) throws Exception {
                
            CloudMessage cloudResponseMessage = m_cloudEndpoint.raiseEvent(cloudRequestMessage);
            return cloudResponseMessage;
    }

    public void release() {
            m_isReleased = true;
    }

    @Override
    public void run() {
        
    }
}
