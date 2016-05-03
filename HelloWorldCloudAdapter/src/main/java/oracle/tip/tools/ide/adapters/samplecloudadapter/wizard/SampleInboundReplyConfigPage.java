/**
 * SampleInboundReplyConfigPage is a UI page to configure inbound response.
 * Invocation style will be defined based on user selection in configuration page. 
 * Ex1: NOTIFICATION STYLE - User uncheck Send option (check box)
 * Ex2: REQUEST_RESPONSE STYLE -  User checks Send option and select Immediate from dropdown.
 * Ex3: ASYNCHRONOUS STYLE - User checks Send option and select Delayed from dropdown.
 * 
 */

package oracle.tip.tools.ide.adapters.samplecloudadapter.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;

import oracle.tip.tools.adapters.cloud.api.CloudAdapterConstants;
import oracle.tip.tools.adapters.cloud.api.CloudAdapterException;
import oracle.tip.tools.adapters.cloud.api.CloudAdapterPageState;
import oracle.tip.tools.adapters.cloud.api.ICloudAdapterPage;
import oracle.tip.tools.adapters.cloud.utils.CloudAdapterUtils;

import oracle.tip.tools.ide.adapters.cloud.api.connection.AbstractCloudConnection;
import oracle.tip.tools.ide.adapters.cloud.api.generation.GeneratorOptions;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.CloudMetadataBrowser;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.ObjectGrouping;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.OperationMapping;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.TypeMapping;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudDataObjectNode;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudOperationNode;
import oracle.tip.tools.ide.adapters.cloud.api.model.InvocationStyle;
import oracle.tip.tools.ide.adapters.cloud.api.model.OperationFault;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.CloudApplicationAdapterException;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.CloudOperationNodeImpl;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.TransformationModelBuilder;
import oracle.tip.tools.ide.adapters.samplecloudadapter.util.SampleCloudAdapterCloudText;
import oracle.tip.tools.ide.adapters.samplecloudadapter.util.SampleCloudAdapterConstants;
import oracle.tip.tools.ide.adapters.samplecloudadapter.util.SampleCloudAdapterUtil;

import oracle.tip.tools.presentation.uiobjects.sdk.CheckBoxObject;
import oracle.tip.tools.presentation.uiobjects.sdk.EditField;
import oracle.tip.tools.presentation.uiobjects.sdk.ISelectObject;
import oracle.tip.tools.presentation.uiobjects.sdk.ISeparatorObject;
import oracle.tip.tools.presentation.uiobjects.sdk.SelectObject;
import oracle.tip.tools.presentation.uiobjects.sdk.UIError;
import oracle.tip.tools.presentation.uiobjects.sdk.UIFactory;
import oracle.tip.tools.presentation.uiobjects.sdk.UIObject;

public class SampleInboundReplyConfigPage implements ICloudAdapterPage {

    protected AdapterPluginContext adapterPluginContext;
    private String targetWSDLURL;
    String[] bizObjArray = null;
    private Hashtable<String, CloudDataObjectNode> dataObjMap = new Hashtable<String, CloudDataObjectNode>();
    private InvocationStyle invocationStyle = InvocationStyle.CALLBACK;
    private boolean includeFaults = true;
    
    public SampleInboundReplyConfigPage(AdapterPluginContext context) {
            this.adapterPluginContext = context;
    }
    
    /**
     * This method is to provide unique ID to page.
     * @return
     */
    
    @Override
    public String getPageId() {
            return SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_ID;
    }

    /**
     * This method is to get page name.
     * @return
     */
    @Override
    public String getPageName() {
            return SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_NAME);
    }

    /**
     * This method is to get page title.
     * @return
     */
    @Override
    public String getPageTitle() {
            return SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_TITLE);
    }

    /**
     * This method is to get Help ID. Each page can have help button to get more information about the page. 
     * Sample Adapter do not have help.
     * @return
     */
    @Override
    public String getHelpId() {
        return "";
    }

    /**
     * This method is to get Welcome Text. Welcome Text on page gives overview of the page.         
     * @return
     */
    @Override
    public String getWelcomeText() {
            return SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_WELCOME_TEXT);
    }


    /**
     * This method returns list of pages required to show in the Wizard. 
     * Adapter Developer should create this map. The key in the map is pageId and the value is an instance of ICloudAdapterPage.
     * The sequence of pages in the map will be displayed in the same sequence in Wizard.
     * @return
     * @throws CloudAdapterException
     */
    @Override
    public LinkedList<EditField> getPageEditFields() throws CloudAdapterException {
       
        LinkedList<EditField> currentPageFields = new LinkedList<EditField>();
        try {
                AbstractCloudConnection connection = (AbstractCloudConnection) adapterPluginContext.getContextObject(CloudAdapterConstants.CONNECTION);
                this.targetWSDLURL = (String) connection.getConnectionProperties().get("targetWSDLURL");
                CloudMetadataBrowser browser = CloudAdapterUtils.getMetadataBrowser(adapterPluginContext);            
                populate(currentPageFields, null, browser, null, null);
        } catch (Exception ex) {
                ex.printStackTrace();
        }
        return currentPageFields;
    }

    @Override
    public LinkedHashMap<String, ICloudAdapterPage> getChildrenEditPages() {
        return null;
    }

    /**
     * This method should update UI fields value to the backend model.  
     * This method is called just before leaving the page. 
     * So anything that needs to be updated to the backend model or needs to be saved in the context before leaving the page, needs to be done here.
     * @param wizardPages
     * @param currentPageFields
     * @return
     * @throws CloudAdapterException
     */
    @Override
    public CloudAdapterPageState updateBackEndModel(
                    LinkedHashMap<String, ICloudAdapterPage> wizardPages,
                    LinkedList<EditField> currentPageFields) {
            Properties props = new Properties();
            Map<String, UIObject> map = EditField.getObjectMap(currentPageFields
                            .toArray(new EditField[currentPageFields.size()]));

            CloudAdapterPageState state = new CloudAdapterPageState(false,
                            wizardPages, currentPageFields);
            transferUIDataToModel(props, map);

            return state;
    }

    protected void transferUIDataToModel(Properties props, Map<String, UIObject> map) {
            
        CloudMetadataBrowser browser = CloudAdapterUtils
                        .getMetadataBrowser(adapterPluginContext);
        try {   
                // Get opMapping created in request page
                OperationMapping opMapping = (OperationMapping) adapterPluginContext.getContextObject(SampleCloudAdapterConstants.INBOUND_OPERATION_MAPPING);
                
                
                setOperationMappingsForGenerator(browser, map, props, opMapping);
                
        } catch (CloudApplicationAdapterException e) {
                e.printStackTrace();
        }
    }
    
    /**
     * This method updates the transformation builder with operationMappings 
     * relevant to the inbound flow.
     * @param adapterPluginContext2 
     * @param map
     * @param props
     * @param opMapping
     * @throws CloudApplicationAdapterException 
     */
    private void setOperationMappingsForGenerator(CloudMetadataBrowser browser, Map<String, UIObject> map,
                    Properties props,  OperationMapping opMapping) throws CloudApplicationAdapterException {

            TransformationModelBuilder integrationMB = SampleCloudAdapterUtil.createTransformationModelBuilder(adapterPluginContext, SampleCloudAdapterConstants.ARTIFACT_KEY_REQUEST);

            // get the selected business or data object
            String bizObjName = UIFactory.getStringValue(map, SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_BUSINESS_OBJ_LIST_FIELD);
            
            CloudDataObjectNode selectedObject = this.dataObjMap.get(bizObjName);
            
            QName qname = selectedObject.getQualifiedName();    
            CloudOperationNodeImpl inboundOperation = (CloudOperationNodeImpl)opMapping.getTargetOperation();     
            
            // set the invocation style. 
            invocationStyle = getInvocationStyle(map);
            
            inboundOperation.setInvocationStyle(invocationStyle);
            
            // inbound synchronous
            if(InvocationStyle.CALLBACK.equals(invocationStyle)){                   
                try {
                        opMapping.setResponseObjectMapping(this.getTypeMappings(selectedObject));
                    
                } catch (CloudApplicationAdapterException e) {
                        e.printStackTrace();
                }
                
                // set faults
                inboundOperation.setFaults(getFaults(browser));                   
                    
                    
            }else if(InvocationStyle.NOTIFICATION.equals(invocationStyle)){
                    
            }else if(InvocationStyle.ASYNCHRONOUS.equals(invocationStyle)){                    
    //              
            }
        
            // set targetWSDLURL property on JCA
            String endpointWsdlName = getFormattedInternalRefName(adapterPluginContext.getReferenceBindingName() ,"ENDPOINT");
            if(props == null){
                props = new Properties();
            }
            
            props.put("endpointWSDLURL", endpointWsdlName);              

            opMapping.setOperationProperties(props);
            
            integrationMB.addOperationMapping(opMapping);
            
            setOperationMappingForEndpoint(opMapping);
            
            // set required information to adapterContext
            adapterPluginContext.setContextObject(SampleCloudAdapterConstants.RESPONSE_OBJECT, selectedObject.getName());
            adapterPluginContext.setContextObject(SampleCloudAdapterConstants.INBOUND_OPERATION_OBJECT, inboundOperation);
            adapterPluginContext.setContextObject(SampleCloudAdapterConstants.INBOUND_OPERATION_MAPPING, opMapping);
            
    }
    
    private String getFormattedInternalRefName(String refName, String key) {
        if (refName != null) {
            refName = refName.trim().replaceAll("\\\\|\\s+|\\.|/|-", "_");
        }
        StringBuilder internalName = new StringBuilder();
        internalName.append(refName);
        internalName.append("_");
        internalName.append(key);
        internalName.append(".wsdl");
        return internalName.toString();
    }
    
    private List<OperationFault> getFaults(CloudMetadataBrowser browser) {

            // set fault 
            if(includeFaults){
                    List<CloudOperationNode> cop = browser.getOperations();
                    if(cop != null && cop.size() > 0){
                            return cop.get(0).getFaults();
                    }
            }       
            
            return null;
    }
    
    /**
     * @param opMapping The integration opMapping we need to refer to for creating a new opmapping.
     * @throws CloudApplicationAdapterException 
     */
    private void setOperationMappingForEndpoint(OperationMapping opMapping) throws CloudApplicationAdapterException{
            
            TransformationModelBuilder endpointMB = SampleCloudAdapterUtil.createTransformationModelBuilder(adapterPluginContext, SampleCloudAdapterConstants.ARTIFACT_KEY_ENDPOINT);
            endpointMB.addOperationMapping(opMapping);
    }
    
    
    
    private List<TypeMapping> getTypeMappings(CloudDataObjectNode objectNode) throws CloudApplicationAdapterException {
            List<TypeMapping> typeMappings = new ArrayList<TypeMapping>();
            TypeMapping typeMapping = new TypeMapping(objectNode,false,false);  
            typeMappings.add(typeMapping);
            
            return typeMappings;
    }
    
    /**
     * This methods return CloudAdapterPageState object. 
     * Method invoked whenever events are generated on the UI and the components generating these events have eventListener set on them.  Ex: ButtonClick, CheckBox value change, SelectBox value change etc.     
     * @param wizardPages
     * @param currentPageFields
     * @param fieldName
     * @return
     */
    
    @Override
    public CloudAdapterPageState getUpdatedEditPages(
                    LinkedHashMap<String, ICloudAdapterPage> wizardPages,
                    LinkedList<EditField> currentPageFields, String fieldName) {

    CloudAdapterPageState pageState = new CloudAdapterPageState(false, wizardPages, currentPageFields);
    
    Map<String, EditField> map = EditField.getFieldMap(currentPageFields.toArray(new EditField[currentPageFields.size()]));
            
            CloudMetadataBrowser browser = CloudAdapterUtils.getMetadataBrowser(adapterPluginContext);
    Locale locale = CloudAdapterUtils.getLocale(adapterPluginContext);
    
            String newValue = UIFactory.getStringValue(EditField.getObjectMap(currentPageFields
                            .toArray(new EditField[currentPageFields.size()])),
                                                    fieldName);

            // send response checkbox - default true. if false, other options disabled.
            if(SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_SEND_RES_CHECKBOX.equals(fieldName)){
                    handleSendResponseOption(pageState, currentPageFields, map, newValue);          
            }
            
            // change in response type - default immediate. if delayed, then we need another list box for callback response.
            if(SampleCloudAdapterConstants.RESPONSE_TYPE_LISTBOX.equals(fieldName)){
                    handleResponseTypeOption(pageState, currentPageFields, map, newValue);          
            }
            
            // change in callback list option - default success. 
            if(SampleCloudAdapterConstants.CALLBACK_RESPONSE_LISTBOX.equals(fieldName)){
                    //handleCallbackResponseTypeOption(pageState, currentPageFields, map, newValue);          
            }
            
            // change in the biz object selection
            if (fieldName.equals(SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_BUSINESS_OBJ_LIST_FIELD)) {
                    CloudDataObjectNode selectedBizObj = this.dataObjMap.get(newValue);
                    this.populate(currentPageFields, selectedBizObj, browser, fieldName, newValue);
            }
            
   

            return pageState;
    }
    
    private void handleSendResponseOption(CloudAdapterPageState pageState,
                        LinkedList<EditField> currentPageFields,
                        Map<String, EditField> map, String newValue) {

                Locale locale = CloudAdapterUtils.getLocale(adapterPluginContext);
                
                // enable/disable other options.
                for (EditField editField : currentPageFields) {
                        if(!SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_SEND_RES_CHECKBOX.equals(editField.getName())){
                                editField.setDisabled(!Boolean.valueOf(newValue));
                        }               
                }               
        }
    
    /**
     * User selects immediate/delayed from the response list box. default immediate. 
     * @param pageState
     * @param currentPageFields
     * @param map
     * @param newValue immediate/delayed. 
     */
    private void handleResponseTypeOption(CloudAdapterPageState pageState,
                    LinkedList<EditField> currentPageFields,
                    Map<String, EditField> map, String newValue) {

            if(SampleCloudAdapterConstants.IMMEDIATE_RESPONSE.equals(newValue)){
                    handleImmediateResponseTypeOption(pageState, currentPageFields, map); 
            }
    }    
    
    private void handleImmediateResponseTypeOption(
                    CloudAdapterPageState pageState,
                    LinkedList<EditField> currentPageFields, Map<String, EditField> map) {
            
            Locale locale = CloudAdapterUtils.getLocale(adapterPluginContext);
            this.invocationStyle = InvocationStyle.CALLBACK;
            
    // send fault check box
            CheckBoxObject sendFaultCheckBox = UIFactory.createCheckbox(SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_SEND_FAULT_CHECKBOX, true, true);
            
            SampleCloudAdapterUtil.createOrReplaceField(currentPageFields, SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_SEND_FAULT_CHECKBOX,
                            SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_SEND_FAULT_CHECKBOX_LABEL),
                            "", false, sendFaultCheckBox, SampleCloudAdapterConstants.CALLBACK_RESPONSE_LISTBOX, EditField.LabelFieldLayout.ONE_ROW_LAYOUT, 0);
    }

    @Override
    public LinkedHashMap<String, UIError[]> validatePage(LinkedHashMap<String, ICloudAdapterPage> linkedHashMap,
                                                         LinkedList<EditField> linkedList) throws CloudAdapterException {
        return null;
    }
    
    /**
     * To display the page UI controls like Radio buttons, ListBox etc.
     * Also, populate business / data objects in list box
     * @param currentPageFields
     * @param browser
     * @param selectedBizObj
     */
    private void populate(LinkedList<EditField> currentPageFields, CloudDataObjectNode selectedBizObj, CloudMetadataBrowser browser, String fieldName, String newValue) {
                    populateBusuinessObjects(browser);
                    
                    
                    // send check box
                    CheckBoxObject sendCheckBox = UIFactory.createCheckbox(SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_SEND_RES_CHECKBOX,true, true);
                    
                    SampleCloudAdapterUtil.createOrUpdateField(currentPageFields, SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_SEND_RES_CHECKBOX, 
                                    SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_SEND_RES_CHECKBOX_LABEL), null, 
                                    false, sendCheckBox, EditField.LabelFieldLayout.ONE_ROW_LAYOUT, "", 0, null);

                    // immediate/delayed response list box
                    String[] resTypeValues = { SampleCloudAdapterConstants.IMMEDIATE_RESPONSE };
                    String[] resTypeFormattedValues = {SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_IMMEDIATE_RESPONSE_VALUE) };
                    
                    SelectObject resTypeSelectObject = UIFactory.createSelectObject(resTypeValues, resTypeFormattedValues, 
                                                                                                            SampleCloudAdapterConstants.IMMEDIATE_RESPONSE, ISelectObject.DISPLAY_LIST, true);
                    
                    SampleCloudAdapterUtil.createOrUpdateField(currentPageFields, SampleCloudAdapterConstants.RESPONSE_TYPE_LISTBOX,
                                    SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_RESPONSE_TYPE_LISTBOX_LABEL), 
                                    "", false, resTypeSelectObject, EditField.LabelFieldLayout.ONE_ROW_LAYOUT, null, 0, null);  
                                    

                    // send fault check box
                    CheckBoxObject sendFaultCheckBox = UIFactory.createCheckbox(SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_SEND_FAULT_CHECKBOX, true, true);

                    SampleCloudAdapterUtil.createOrUpdateField(currentPageFields, SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_SEND_FAULT_CHECKBOX, 
                                    SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_SEND_FAULT_CHECKBOX_LABEL),
                                    "", false, sendFaultCheckBox, EditField.LabelFieldLayout.ONE_ROW_LAYOUT, null, 0, null);

                    // separator
                    ISeparatorObject separator = UIFactory.createSeparatorObject();
                    SampleCloudAdapterUtil.createOrUpdateField(currentPageFields,SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_SEPARATOR, "", "", separator);

                    // create list box
                    if (selectedBizObj == null) {
                            SelectObject selectBizObject = UIFactory.createSelectObject(this.bizObjArray,this.bizObjArray,bizObjArray[0],
                                            ISelectObject.DISPLAY_LIST_AS_LISTBOX,false,true,
                                            SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_LISTBOX_FILTER_PLACEHOLDER_TEXT));

                            SampleCloudAdapterUtil.createOrUpdateField(currentPageFields, SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_BUSINESS_OBJ_LIST_FIELD,
                                            SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_BUSINESS_OBJ_LIST_LABEL),
                                            SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_BUSINESS_OBJ_LIST_DESC),
                                            false, selectBizObject, EditField.LabelFieldLayout.TWO_ROW_LAYOUT, "", -1, null);
                    }
    }
    
     /**
      * browse all the business objects and exclude the unwanted and save th results in a bizObjARray
      * @param browser
      */
     private void populateBusuinessObjects(CloudMetadataBrowser browser){
             if (this.bizObjArray == null) {
                     List<CloudDataObjectNode> dataObjects = browser.getDataObjectNodes();
                     ArrayList<String> boList = new ArrayList<String>();
                     for (int i = 0; i < dataObjects.size(); i++) {
                             CloudDataObjectNode dataObj = dataObjects.get(i);
                             /*if(SampleCloudAdapterUIUtil.exclude(dataObj)){
                                     continue;
                             }*/
                             String dataObjectName = dataObjects.get(i).getName().toString();
                             this.dataObjMap.put(dataObjectName, dataObj);
                             boList.add(dataObjectName);
                     }

                     bizObjArray = new String[boList.size()];
                     bizObjArray = boList.toArray(bizObjArray);
                     Arrays.sort(bizObjArray);
             }
     }
    
    private InvocationStyle getInvocationStyle(Map<String, UIObject> map){

            InvocationStyle iStyle = InvocationStyle.NOTIFICATION;

            CheckBoxObject cbo = (CheckBoxObject)map.get(SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_SEND_RES_CHECKBOX);

            // this is req-response type. 
            if(cbo != null && cbo.ischecked()){

                    // check for delayed response.
                    SelectObject responseList = (SelectObject)map.get(SampleCloudAdapterConstants.RESPONSE_TYPE_LISTBOX);

                    if(responseList != null){
                            if(SampleCloudAdapterConstants.DELAYED_RESPONSE.equalsIgnoreCase(responseList.getSelectedValue())){
                                    iStyle = InvocationStyle.ASYNCHRONOUS;
                            }else{
                                    iStyle = InvocationStyle.CALLBACK;
                            }
                    }
            }
            return iStyle;
    }
}
