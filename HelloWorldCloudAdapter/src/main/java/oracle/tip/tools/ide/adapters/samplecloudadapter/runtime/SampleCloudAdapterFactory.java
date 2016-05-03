package oracle.tip.tools.ide.adapters.samplecloudadapter.runtime;

import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.CloudApplicationAdapter;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.CloudApplicationAdapterFactory;
import oracle.tip.tools.ide.adapters.samplecloudadapter.plugin.SampleCloudAdapter;

public class SampleCloudAdapterFactory implements CloudApplicationAdapterFactory {
    @Override
    public CloudApplicationAdapter createAdapter(AdapterPluginContext context) {

        return new SampleCloudAdapter(context);
    }

}
