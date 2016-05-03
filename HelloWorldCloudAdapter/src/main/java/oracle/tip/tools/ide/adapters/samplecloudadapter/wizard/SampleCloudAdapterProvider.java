package oracle.tip.tools.ide.adapters.samplecloudadapter.wizard;

import java.util.Locale;

import oracle.tip.tools.adapters.cloud.api.CloudAdapterException;
import oracle.tip.tools.adapters.cloud.api.CloudAdapterFilter;
import oracle.tip.tools.adapters.cloud.api.CloudAdapterUIProvider;
import oracle.tip.tools.adapters.cloud.api.ICloudAdapterUIBinding;

public class SampleCloudAdapterProvider extends CloudAdapterUIProvider {
    
    public ICloudAdapterUIBinding getCloudAdapterUIBinding(CloudAdapterFilter cloudAdapterFilter, Locale locale)
        throws CloudAdapterException
      {
        SampleCloudUIBinding uiBinding = null;
        try
        {
          uiBinding = new SampleCloudUIBinding(cloudAdapterFilter, locale);
        } catch (Exception ex) {
          throw new CloudAdapterException(ex.getMessage());
        }
        return uiBinding;
      }
        @Override
        public String getLocalizedAdapterType(Locale locale) {
           
            return "SampleCloudAdapter";
        }
}
