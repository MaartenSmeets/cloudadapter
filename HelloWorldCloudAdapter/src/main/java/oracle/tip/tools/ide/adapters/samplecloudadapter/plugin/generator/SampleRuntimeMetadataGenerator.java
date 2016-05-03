package oracle.tip.tools.ide.adapters.samplecloudadapter.plugin.generator;

import oracle.tip.tools.adapters.cloud.api.CloudAdapterConstants;
import oracle.tip.tools.ide.adapters.cloud.api.generation.GeneratorOptions;
import static oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterConstants.RUNTIME_CONNECTION_FACTORY;

import oracle.tip.tools.ide.adapters.cloud.api.generation.RuntimeGenerationContext;
import oracle.tip.tools.ide.adapters.cloud.api.generation.RuntimeMetadataGenerator;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.impl.generation.AbstractRuntimeMetadataGenerator;
import oracle.tip.tools.ide.adapters.samplecloudadapter.util.SampleCloudAdapterConstants;

public class SampleRuntimeMetadataGenerator extends AbstractRuntimeMetadataGenerator {
    
    public SampleRuntimeMetadataGenerator(AdapterPluginContext adapterPluginContext) {
        super(adapterPluginContext);
    }

    @Override
    protected void initializeContext(RuntimeGenerationContext runtimeGenerationContext) {
        
        // include connection factory class in JCA file (runtime artifact)
        runtimeGenerationContext.setContextObject(RUNTIME_CONNECTION_FACTORY, SampleCloudAdapterConstants.CONNECTION_FACTORY_CLASS);
    }
}
