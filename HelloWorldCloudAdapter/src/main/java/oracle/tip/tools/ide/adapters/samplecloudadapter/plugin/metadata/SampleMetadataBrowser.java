/**
 * This class is required to parse the WSDL (source) and generate Cloud Application Model (CAM) out of it.
 */
package oracle.tip.tools.ide.adapters.samplecloudadapter.plugin.metadata;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import oracle.tip.tools.ide.adapters.cloud.api.connection.CloudConnection;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.CloudMetadataDataSource;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.MetadataParser;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.MetadataParserRegistry;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.WSDLMetadataDataSource;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudAPINode;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudApplicationModel;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudDataObjectNode;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudOperationNode;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterConstants;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.CloudApplicationAdapterException;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.CloudAPINodeImpl;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.wsdl.AbstractMetadataBrowser;
import oracle.tip.tools.ide.adapters.samplecloudadapter.util.SampleCloudAdapterConstants;

public class SampleMetadataBrowser extends AbstractMetadataBrowser {
	
	private static final String CRUD = "CRUD";
	private static final String MAX_VERSION = "1.2";
	
	private String currentVersion;
	
	public SampleMetadataBrowser(CloudConnection connection,
			AdapterPluginContext context) {
		super(connection, context);
	}
	
        /**
         * To setup any custom API nodes and assign operations to it.
         * Sample interface do not have any API so adding API manually.
         */
	@Override
	protected void preProcess() {
		super.preProcess();
	}

       
	@Override
	protected boolean filterByAPINodes() {
		return true;
	}

	@Override
	protected List<CloudAPINode> getAPINodes() {
		return getModel().getAPIs();
	}
	
        
        /**
         * To get list of metadta parsers.
         * Parsers can be standard WSDL parser or any custom parser.
         * Sample Cloud Adapter has standard WSDL parser to process dataobject nodes and operations.
         * 
         * @return list of parsers
         */
	@Override
	protected List<MetadataParser> getMetadataParsers() {
		
		List<MetadataParser> parsers = new ArrayList<MetadataParser>();
                // To get WSDLMetadataParser. 
		MetadataParser wsdlMetadataParser = MetadataParserRegistry.getParser(AdapterConstants.WSDL_MEDIA_TYPE);
		parsers.add(wsdlMetadataParser);	
		
                return parsers;
	}

	/* (non-Javadoc)
	 * @see oracle.tip.tools.ide.adapters.cloud.impl.metadata.wsdl.AbstractMetadataBrowser#getVersion()
	 */
	@Override
	protected String getVersion() {
		return (currentVersion != null) ? currentVersion : parseVersion();
	}

	private String parseVersion() {
            
		String version = MAX_VERSION;		
		return version;
	}

	private String getWSDLURL() {
		CloudConnection connection = getConnection();
		return connection.getConnectionProperties().getProperty(
				AdapterConstants.SOURCE_WSDL_LOCATION);
	}
	
	/* (non-Javadoc)
	 * @see oracle.tip.tools.ide.adapters.cloud.impl.metadata.wsdl.AbstractMetadataBrowser#parseMetadata(oracle.tip.tools.ide.adapters.cloud.api.metadata.MetadataParser, oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext)
	 */
	@Override
	protected void parseMetadata(MetadataParser parser, AdapterPluginContext context)
			throws CloudApplicationAdapterException {
                // If condition to differentiate between parsers. Sample Cloud adapter has only WSDL Metadata parser
		if (parser.getSupportedMediaTypes().contains(AdapterConstants.WSDL_MEDIA_TYPE)) {
			parseWSDLMetadata(parser, context);
		} 
	}
	
        /**
         * Create a datasource for WSDL and hand over to MetadataParser.
         * @param parser
         * @param context
         * @throws CloudApplicationAdapterException
         */
	private void parseWSDLMetadata(MetadataParser parser,
			AdapterPluginContext context)
			throws CloudApplicationAdapterException {
		CloudConnection conn = (CloudConnection) context
				.getContextObject(AdapterConstants.CONNECTION);
		try {
			CloudApplicationModel cam = context.getCloudApplicationModel();
			CloudMetadataDataSource dataSource = new WSDLMetadataDataSource(
					context.getReferenceBindingName(), new URL(getWSDLURL()),
					conn.getConnectionProperties(), context.getRepository());
			parser.parse(dataSource, cam, conn.getConnectionProperties());
		} catch (MalformedURLException e) {
			throw new CloudApplicationAdapterException(e);
		}

	}
        	
	@Override
	public CloudOperationNode getOperation(String operationName) {
		List<CloudOperationNode> operations = getModel().findOperations(operationName);
                getModel().getOperations();
		return (operations.size() > 0) ? operations.get(0):null;
	}
	
        @Override
	public void processAPINodes() {
		
                String namespace = SampleCloudAdapterConstants.SAMPLE_SERVICE_NAMESPACE;
                Set<String> operationNames = new HashSet<String>();
                
                // Get list of operations from parsed WSDL
                List<CloudOperationNode> opNodes = getModel().getOperations();
                
                for(int i=0;i<opNodes.size();i++){
                    CloudOperationNode opNode = opNodes.get(i);
                    operationNames.add(opNode.getName());
                }
                
                // Add list of operations to API (CRUD)
                CloudAPINode api = new CloudAPINodeImpl(CRUD, namespace, getVersion(), operationNames);
                
                // Add API to cloud application model (CAM)
                getModel().addAPI(api);               
                
	}
        
    @Override
    public List<CloudDataObjectNode> getDataObjectNodes(CloudOperationNode scope) {            
          
            return super.getDataObjectNodes(scope);            
    }

}
