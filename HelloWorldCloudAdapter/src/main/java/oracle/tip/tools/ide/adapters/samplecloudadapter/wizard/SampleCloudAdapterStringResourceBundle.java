package oracle.tip.tools.ide.adapters.samplecloudadapter.wizard;

import java.util.ListResourceBundle;

public class SampleCloudAdapterStringResourceBundle  extends ListResourceBundle {
    public SampleCloudAdapterStringResourceBundle() {
        super();
    }
    
    @Override
    protected Object[][] getContents() {
        return contents;
    }
    static final Object[][] contents = {


        // extension.xml is a adapter level configuration file uses below key value pairs
        {"SAMPLECLOUD_ADAPTER_COMPONENT_NAME_L",        "Sample Cloud Adapter"},
       
        { "SAMPLECLOUD_ADAPTER_COMPONENT_DESC", "A sample adapter to support SOAP based services of SaaS Application" },
       
        { "CLOUD_ADAPTER_COMPONENT_FOLDER_NAME_L", "Cloud Adapters" },
        
        {"SCA_ADAPTER_CLOUD_CATEGORY_NAME_L", "Cloud"}
           
        };
    
    public void setParent(ListResourceBundle parent) {
                super.setParent(parent);
        }
}
