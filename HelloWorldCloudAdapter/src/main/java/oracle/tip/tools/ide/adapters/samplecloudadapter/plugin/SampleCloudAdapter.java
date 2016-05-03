package oracle.tip.tools.ide.adapters.samplecloudadapter.plugin;

import oracle.tip.tools.ide.adapters.cloud.api.connection.CloudConnection;
import oracle.tip.tools.ide.adapters.cloud.api.generation.RuntimeMetadataGenerator;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.CloudMetadataBrowser;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.impl.plugin.AbstractCloudApplicationAdapter;
import oracle.tip.tools.ide.adapters.samplecloudadapter.plugin.generator.SampleRuntimeMetadataGenerator;
import oracle.tip.tools.ide.adapters.samplecloudadapter.plugin.metadata.SampleMetadataBrowser;


public class SampleCloudAdapter extends AbstractCloudApplicationAdapter {

	
	private static final long serialVersionUID = -1224252546132381389L;

        /**
         * Constructor for initialization 
         * @param pluginContext
         */
	public SampleCloudAdapter(AdapterPluginContext pluginContext) {
		super(pluginContext);
	}

	/**
         * To get adapter specific connection.
         * Returning Sample Cloud Adapter specific connection details.
         * @return
         */
        @Override
	public CloudConnection getConnection() {
		return new SampleCloudAdapterConnection(getPluginContext());
	}

        /**
         * To get adapter specific metadata browser
         * Returning Sample Cloud Adapter metadata browser.
         * @param connection
         * @return
         */
	@Override
	public CloudMetadataBrowser getMetadataBrowser(CloudConnection connection) {
		
		AdapterPluginContext context = getPluginContext();		
		return new SampleMetadataBrowser(connection,context);
	}
        
        /**
         * To get adapter specific runtime metadata generator
         * Runtime metadta generator will generate runtime artifacts like integration WSDL, Cloud Application Configuration (.jca) and related Schema.
         * @return
         */
        @Override
        public RuntimeMetadataGenerator getRuntimeMetadataGenerator() {
            return new SampleRuntimeMetadataGenerator(getPluginContext());
        }

	@Override
	public String getName() {
		return "Sample Cloud Adapter";
	}

	

}
