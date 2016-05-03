/**
 * Using factory pattern to return the connection classes.
 * In case of Outbound Adapter, Connection factory class will return SampleCloudAppConnection
 * In case of Inbound Adapter, Connection factory class will return SampleCloudConnector
 */
package oracle.tip.tools.ide.adapters.samplecloudadapter.runtime;

import java.util.Map;

import oracle.cloud.connector.api.CloudApplicationConnection;
import oracle.cloud.connector.api.CloudApplicationConnectionFactory;
import oracle.cloud.connector.api.CloudConnectorException;


public class SampleCloudAppConnectionFactory implements CloudApplicationConnectionFactory {
    
    private Map<String, String> m_connectionFactoryProperties;
    public SampleCloudAppConnectionFactory() {
        super();
    }

    @Override
    public void setConnectionFactoryProperties(Map<String, String> properties) {
        m_connectionFactoryProperties = properties;
    }

    @Override
    public Map<String, String> getConnectionFactoryProperties() {
        return m_connectionFactoryProperties;
    }

    /**
     * Returning SampleCloudAppConnection class, to communicate with SaaS / on-premise application.
     * @return
     * @throws CloudConnectorException
     */
    @Override
    public CloudApplicationConnection getConnection() throws CloudConnectorException {
        return new SampleCloudAppConnection(this);
    }

    /**
     * Returning Connector Class Name, incase of inbound interaction
     * @return
     */
    @Override
    public String getCloudConnectorClassName() {
        return SampleCloudConnector.class.getName();
    }
}
