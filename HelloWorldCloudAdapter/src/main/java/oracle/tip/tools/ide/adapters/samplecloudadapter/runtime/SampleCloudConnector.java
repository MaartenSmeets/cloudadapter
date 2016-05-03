/**
 * Required only incase of inbound interaction
 * Connector class will be loaded from ConnectionFactory for inbound interactions.
 */
package oracle.tip.tools.ide.adapters.samplecloudadapter.runtime;

import oracle.cloud.connector.api.CloudApplicationConnectionFactory;
import oracle.cloud.connector.api.CloudConnector;
import oracle.cloud.connector.api.CloudEndpointFactory;
import oracle.cloud.connector.api.CloudMessageReceiver;


public class SampleCloudConnector implements CloudConnector {
    
    @Override
    public String getCloudConnectorName() {
        return "Sample Cloud Adapter";
    }

    @Override
    public String getCloudConnectorDescription() {
        return "Sample Cloud Adapter for cloud integration";
    }

    @Override
    public String getCloudConnectorVersion() {
        return "12.1.3";
    }

    @Override
    public String getCloudConnectorVendor() {
        return "Oracle";
    }

    @Override
        public String getCloudConnectorBackendName() {
        return "SampleCloudAdapter";
    }

    @Override
    public String getCloudConnectorBackendVersions() {
        return "1.1,1.2";
    }

    @Override
    public String getCloudConnectorBackendVendor() {
        return "Oracle";
    }

    /**
     * eventListenerActivation method will process the request payload.
     * @param cloudApplicationConnectionFactory
     * @param cloudEndpointFactory
     * @return
     * @throws Exception
     */
    @Override
    public CloudMessageReceiver eventListenerActivation(CloudApplicationConnectionFactory cloudApplicationConnectionFactory,
                                                        CloudEndpointFactory cloudEndpointFactory) throws Exception {
        return new SampleMessageReceiver(cloudApplicationConnectionFactory, cloudEndpointFactory);
    }

    /**
     * eventListenerDeactivation method will release the process.
     * @param cloudApplicationConnectionFactory
     * @param cloudEndpointFactory
     * @return
     * @throws Exception
     */
    @Override
    public void eventListenerDeactivation(CloudMessageReceiver cloudMessageReceiver) {
        cloudMessageReceiver.release();
    }
}
