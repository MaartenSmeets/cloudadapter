package oracle.tip.tools.ide.adapters.samplecloudadapter.wizard;


import oracle.tip.tools.adapters.cloud.impl.CloudAdapterWelcomePage;
import oracle.tip.tools.ide.adapters.samplecloudadapter.util.SampleCloudAdapterCloudText;

import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.presentation.uiobjects.sdk.EditField;

import java.util.LinkedList;

public class SampleCloudAdapterWelcomePage extends CloudAdapterWelcomePage {

    
    public SampleCloudAdapterWelcomePage(AdapterPluginContext context) {
        super(context);
    }

    public String getHelpId() {
        return "";
    }

    public String getWelcomeText() {
        return SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.WELCOME_PAGE_WELCOME_TEXT);
    }

    // To add page level UI objects. Sample adapter uses default page.
    public LinkedList<EditField> getPageEditFields() {
        LinkedList<EditField> fields = super.getPageEditFields();
        return fields;
    }
}
