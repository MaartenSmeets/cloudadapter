/**
 * SampleCloudAppConnection class is required for outbound interactions.
 * This class extends AbstractCloudApplicationConnection class to create a connection(endpoint) and also to get cloud operation details.
 * Handlers can be used to transform the message according to the target system.
 */

package oracle.tip.tools.ide.adapters.samplecloudadapter.runtime;

import java.util.ArrayList;
import java.util.List;

import oracle.cloud.connector.api.CloudApplicationConnectionFactory;
import oracle.cloud.connector.api.CloudMessageHandler;
import oracle.cloud.connector.api.EndpointFactory;
import oracle.cloud.connector.api.SessionManager;
import oracle.cloud.connector.impl.AbstractCloudApplicationConnection;

public class SampleCloudAppConnection extends AbstractCloudApplicationConnection {
    
    boolean m_closed;
    private CloudApplicationConnectionFactory m_connectionFactory;
    private List<CloudMessageHandler> messageHandlers;
    
    
    public SampleCloudAppConnection(CloudApplicationConnectionFactory connectionFactory) {
        m_connectionFactory = connectionFactory;
    }

    /**
     * To get message handlers for message transformation
     * @return
     */
    @Override
    protected List<CloudMessageHandler> getMessageHandlers() {
        if(messageHandlers == null) {
                messageHandlers = new ArrayList<CloudMessageHandler>();
                messageHandlers.add(new SampleCloudAdapterMessageHandler());
        }
        return messageHandlers;
    }

    /**
     * To get endpoint TYPE. Sample Cloud Adapter uses SOAP ENDPOINT TYPE
     * @param string
     * @return
     */
    @Override
    public String getEndpointType(String string) {
        return EndpointFactory.SOAP_ENDPOINT_TYPE;
    }

    @Override
    public void close() {
        m_closed = true;
    }

    @Override
    public boolean isValid() {
        return !(m_closed);
    }

    @Override
    public SessionManager getSessionManager() {
        return null;
    }
}
