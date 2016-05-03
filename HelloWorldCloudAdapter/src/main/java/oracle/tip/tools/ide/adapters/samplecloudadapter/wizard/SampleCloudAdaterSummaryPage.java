package oracle.tip.tools.ide.adapters.samplecloudadapter.wizard;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import oracle.tip.tools.adapters.cloud.api.CloudAdapterConstants;
import oracle.tip.tools.adapters.cloud.api.CloudAdapterFilter;
import oracle.tip.tools.adapters.cloud.impl.CloudAdapterSummaryPage;
import oracle.tip.tools.adapters.cloud.l10n.CloudAdapterText;
import oracle.tip.tools.adapters.cloud.utils.CloudAdapterUtils;

import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.samplecloudadapter.util.SampleCloudAdapterCloudText;

import oracle.tip.tools.ide.adapters.samplecloudadapter.util.SampleCloudAdapterConstants;
import oracle.tip.tools.presentation.uiobjects.sdk.EditField;
import oracle.tip.tools.presentation.uiobjects.sdk.UIFactory;
import oracle.tip.tools.presentation.uiobjects.sdk.ILabelObject.Alignment;
import oracle.tip.tools.presentation.uiobjects.sdk.TextBoxObject;


public class SampleCloudAdaterSummaryPage extends CloudAdapterSummaryPage {
    
    private AdapterPluginContext adaPluginContext = null;
    
    public SampleCloudAdaterSummaryPage(AdapterPluginContext adapterPluginContext) {
        super(adapterPluginContext);
        this.adaPluginContext = adapterPluginContext;       
        
    }
    
    /**
     * This method is to get Welcome Text. Welcome Text on page gives overview of the page.         
     * @return
     */
    public String getWelcomeText() {
        return SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.SUMMARY_PAGE_WELCOME_TEXT);
    }
    
    /**
     * This method returns list of pages required to show in the Wizard. 
     * Adapter Developer should create this map. The key in the map is pageId and the value is an instance of ICloudAdapterPage.
     * The sequence of pages in the map will be displayed in the same sequence in Wizard.
     * @return
     * @throws CloudAdapterException
     */
    @Override
    public LinkedList<EditField> getPageEditFields(){
        LinkedList<EditField> headerFields = new LinkedList<EditField>();
        
        String header = adaPluginContext.getReferenceBindingName();
        String iconUrl = "";
        
        this.displayLabel(headerFields, iconUrl, header, Alignment.LEFT);
        
        Locale locale = CloudAdapterUtils.getLocale(adaPluginContext);
        
        String desc = (String) adaPluginContext.getContextObject(CloudAdapterConstants.CLOUD_ADAPTER_DESCRIPTION_ARTIFACT);
        this.displayText(headerFields, desc, CloudAdapterConstants.DESCRIPTION_NAME,
                        SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.SAMPLE_CLOUD_ADAPTER_SUMMARY_DESCRIPTION),
                        CloudAdapterText.getString(locale, CloudAdapterText.WELCOME_PAGE_DESCRIPTION_SHORT_DESC));
        
        this.addSeparator(headerFields);            
        
      CloudAdapterFilter filter = (CloudAdapterFilter)adaPluginContext.getContextObject(CloudAdapterConstants.UI_CLOUD_ADAPTER_FILTER);
        
        if (filter.isInbound()) 
        { 
                this.setupInboundSummary(headerFields);
        }else{
            this.setupOutboundSummary(headerFields);
        }
        
        this.addSeparator(headerFields);
        
        return headerFields;
    }
    
    /**
     * To display summary of inbound configuration like Request Objects, Response Object, message exechange pattern etc
     * Sample Adapter displayes only Request and Response object details
     * @param headerFields
     */
    private void setupInboundSummary(List<EditField> headerFields){
        
        
        this.addSeparator(headerFields);
        this.displayText(headerFields, (String) adaPluginContext.getContextObject(CloudAdapterConstants.CLOUD_BIZ_OBJ), CloudAdapterConstants.CLOUD_BIZ_OBJ,
                        "Inbound Request Object: ",
                        "Inbound Request Object");
        this.addSeparator(headerFields);
        this.displayText(headerFields, (String) adapterPluginContext.getContextObject(SampleCloudAdapterConstants.RESPONSE_OBJECT), CloudAdapterConstants.CLOUD_BIZ_OBJ,
                        "Inbound Response Object: ",
                        "Inbound Response Object");
             
    }
         
        
    private void addSeparator(List<EditField> headerFields) {
            headerFields.add(UIFactory.createEditField("separator", null, null, UIFactory.createSeparatorObject()));
    }
    private void displayLabel(List<EditField> headerFields, String icon, String text, Alignment align){
            headerFields.add(UIFactory.createEditField("label1", null, null, UIFactory.createLabelObject(icon, text, align)));
    }
    
    private void displayText(List<EditField> headerFields, String text, String fieldName, String label, String desc){
        TextBoxObject uiObj = UIFactory.createTextBox(text, true);
        headerFields.add(UIFactory.createEditField(fieldName, label, desc, uiObj));
    }
       
    
    
    /**
     * To display summary of outbound configuration like operation name, business object etc
     * @param headerFields
     */
    private void setupOutboundSummary(List<EditField> headerFields){
      
        this.displayText(headerFields, (String) adaPluginContext.getContextObject(CloudAdapterConstants.CLOUD_BIZ_OBJ), CloudAdapterConstants.CLOUD_BIZ_OBJ,
                        "Business Object:",
                        "Business Object");
        
        this.displayText(headerFields, (String) adaPluginContext.getContextObject(CloudAdapterConstants.CLOUD_OPERATION), CloudAdapterConstants.CLOUD_OPERATION,
                        "Operation Name:",
                        "Operation Name");
        
    }
    
}
