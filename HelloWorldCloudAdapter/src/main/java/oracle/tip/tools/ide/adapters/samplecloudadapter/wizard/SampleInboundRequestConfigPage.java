/**
 * This is a UI page for inbound request configuration.
 */
package oracle.tip.tools.ide.adapters.samplecloudadapter.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import oracle.tip.tools.adapters.cloud.api.CloudAdapterConstants;
import oracle.tip.tools.adapters.cloud.api.CloudAdapterException;
import oracle.tip.tools.adapters.cloud.api.CloudAdapterPageState;
import oracle.tip.tools.adapters.cloud.api.ICloudAdapterPage;
import oracle.tip.tools.adapters.cloud.utils.CloudAdapterUtils;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.CloudMetadataBrowser;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.ObjectGrouping;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.OperationMapping;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.TypeMapping;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudDataObjectNode;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.CloudApplicationAdapterException;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.CloudOperationNodeImpl;
import oracle.tip.tools.ide.adapters.samplecloudadapter.util.SampleCloudAdapterCloudText;
import oracle.tip.tools.ide.adapters.samplecloudadapter.util.SampleCloudAdapterConstants;
import oracle.tip.tools.presentation.uiobjects.sdk.EditField;
import oracle.tip.tools.presentation.uiobjects.sdk.ISelectObject;
import oracle.tip.tools.presentation.uiobjects.sdk.ISeparatorObject;
import oracle.tip.tools.presentation.uiobjects.sdk.SelectObject;
import oracle.tip.tools.presentation.uiobjects.sdk.UIError;
import oracle.tip.tools.presentation.uiobjects.sdk.UIFactory;
import oracle.tip.tools.presentation.uiobjects.sdk.UIObject;

public class SampleInboundRequestConfigPage implements ICloudAdapterPage {
    
    private Hashtable<String, CloudDataObjectNode> dataObjMap = new Hashtable<String, CloudDataObjectNode>();
    AdapterPluginContext adapterPluginContext = null;
    String[] bizObjArray = null;
    
    public SampleInboundRequestConfigPage(AdapterPluginContext context) {
        this.adapterPluginContext = context;
    }

    /**
     * This method is to provide unique ID to page.
     * @return
     */
    @Override
    public String getPageId() {
            return SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_ID;
    }

    /**
     * This method is to get page name.
     * @return
     */
    @Override
    public String getPageName() {
            return SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_NAME);
    }

    /**
     * This method is to get page title.
     * @return
     */
    @Override
    public String getPageTitle() {
            return SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_TITLE);
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
            return SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_WELCOME_TEXT);
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
                CloudMetadataBrowser browser = CloudAdapterUtils.getMetadataBrowser(adapterPluginContext);
                populate(currentPageFields, browser, null);
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
     * This methods return CloudAdapterPageState object. 
     * Method invoked whenever events are generated on the UI and the components generating these events have eventListener set on them.  Ex: ButtonClick, CheckBox value change, SelectBox value change etc.     
     */

    @Override
    public CloudAdapterPageState getUpdatedEditPages(LinkedHashMap<String, ICloudAdapterPage> wizardPages, LinkedList<EditField> currentPageFields, String fieldName) {
            
            Map<String, EditField> map = EditField.getFieldMap(currentPageFields .toArray(new EditField[currentPageFields.size()]));

            String newValue = UIFactory.getStringValue(EditField.getObjectMap(currentPageFields.toArray(new EditField[currentPageFields.size()])), fieldName);
            
            CloudMetadataBrowser browser = CloudAdapterUtils.getMetadataBrowser(adapterPluginContext);

            // change in the biz object selection
            if (fieldName.equals(SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_BUSINESS_OBJ_LIST_FIELD)) {
                    CloudDataObjectNode selectedBizObj = this.dataObjMap.get(newValue);
                    this.populate(currentPageFields, browser, selectedBizObj);
            }
            return new CloudAdapterPageState(false, wizardPages, currentPageFields);
    }

    /**
     * This method is called when navigating to next page. The request is just forwarded to corresponding page. 
     * @param linkedHashMap
     * @param linkedList
     * @return
     * @throws CloudAdapterException
     */
    @Override
    public LinkedHashMap<String, UIError[]> validatePage(LinkedHashMap<String, ICloudAdapterPage> linkedHashMap,
                                                         LinkedList<EditField> linkedList) throws CloudAdapterException {
        // No validation in Sample Adapter
        return null;
    }
    
    /**
     * To display the page UI controls like Radio buttons, ListBox etc.
     * Also, populate business / data objects in list box
     * @param currentPageFields
     * @param browser
     * @param selectedBizObj
     */
    private void populate(LinkedList<EditField> currentPageFields, CloudMetadataBrowser browser, CloudDataObjectNode selectedBizObj) {
        
        // To fetch and display business objects
        populateBusinessObjects(browser);

        String[] reqTypes = { SampleCloudAdapterConstants.BUSINESS_OBJECTS};

        String[] reqTypeValues = { SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.ENDPOINT_CONFIG_PAGE_SELECT_BIZ_OBJECT_VAL) };

        // Use SelectObject (Radio Button) to select type of service like business service.
        SelectObject reqTypeSelectObject = UIFactory.createSelectObject(reqTypes, reqTypeValues,
                        SampleCloudAdapterConstants.BUSINESS_OBJECTS,ISelectObject.DISPLAY_RADIO_HORIZONTAL, true);

        this.createOrUpdateField(currentPageFields, SampleCloudAdapterConstants.ENDPOINT_CONFIG_OPTION, 
                        SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.ENDPOINT_CONFIG_PAGE_SELECT_ENDPOINT_CONFIG_OPTION_LABEL),
                        SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.ENDPOINT_CONFIG_PAGE_SELECT_ENDPOINT_CONFIG_OPTION_DESC),
                        false, reqTypeSelectObject, EditField.LabelFieldLayout.ONE_ROW_LAYOUT, "", -1, null);

        // separator
        ISeparatorObject separator = UIFactory.createSeparatorObject();
        createOrUpdateField(currentPageFields, SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_SEPARATOR, 
                        "", "",separator);

        // Use SelectObject (ListBox) to display list of business objects
        if (selectedBizObj == null) {
                SelectObject selectBizObject = UIFactory.createSelectObject(this.bizObjArray,this.bizObjArray,
                                bizObjArray[0], ISelectObject.DISPLAY_LIST_AS_LISTBOX,false, true,
                                SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_LISTBOX_FILTER_PLACEHOLDER_TEXT));

        this.createOrUpdateField(currentPageFields, 
                                SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_BUSINESS_OBJ_LIST_FIELD, 
                                SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_BUSINESS_OBJ_LIST_LABEL),
                                SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_BUSINESS_OBJ_LIST_DESC),
                                false, selectBizObject, EditField.LabelFieldLayout.TWO_ROW_LAYOUT, "", -1, null);

        }

    }
    
    /**
     * browse all the business objects and exclude the unwanted and save the
     * results in a bizObjARray
     * 
     * @param browser
     */
    private void populateBusinessObjects(CloudMetadataBrowser browser) {            

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
    
    private void createOrUpdateField(LinkedList<EditField> editFields, 
                    String fieldName, String fieldLabel, String desc, boolean required, 
                    UIObject uiObj, EditField.LabelFieldLayout layout, String helpText, int rowIndex, 
                    EditField.LabelFieldAlignment labelFieldAlignment) {
            
            EditField editField = null;
    Map<String, EditField> fieldsMap = EditField.getFieldMap(editFields.toArray(new EditField[editFields.size()]));
    if (fieldsMap != null) {
        editField = fieldsMap.get(fieldName);
    }

    if (editField != null) {
        int index = editFields.indexOf(editField);
        editFields.remove(index);
        editFields.add(index, UIFactory.createEditField(fieldName, fieldLabel, desc, required,false, uiObj, layout, helpText, rowIndex, labelFieldAlignment));
    } else
        editFields.add(UIFactory.createEditField(fieldName, fieldLabel, desc, required,false, uiObj, layout, helpText, rowIndex, labelFieldAlignment));

    }
    
    /**
     * locate the given field from the map and update it.
     * If the field is not there, create it in the map.
     */
    private void createOrUpdateField(LinkedList<EditField> editFields,
                    String fieldName, String fieldLabel, String desc, UIObject uiObj) {
            EditField editField = null;
            Map<String, EditField> fieldsMap = EditField.getFieldMap(editFields
                            .toArray(new EditField[editFields.size()]));
            if (fieldsMap != null) {
                    editField = fieldsMap.get(fieldName);
            }

            if (editField != null) {
                    int index = editFields.indexOf(editField);
                    editFields.remove(index);
                    editFields.add(index, UIFactory.createEditField(fieldName,
                                    fieldLabel, desc, uiObj));
            } else
                    editFields.add(UIFactory.createEditField(fieldName, fieldLabel,
                                    desc, uiObj));

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
    public CloudAdapterPageState updateBackEndModel(LinkedHashMap<String, ICloudAdapterPage> wizardPages,
                    LinkedList<EditField> currentPageFields) throws CloudAdapterException {
            
            Properties props = new Properties();
            Map<String, UIObject> map = EditField.getObjectMap(currentPageFields.toArray(new EditField[currentPageFields.size()]));

            CloudAdapterPageState state = new CloudAdapterPageState(false, wizardPages, currentPageFields);
            transferUIDataToModel(props, map);

            return state;
    }
    protected void transferUIDataToModel(Properties props, Map<String, UIObject> map) {
            try {
                this.setOperationMappingsForGenerator(map, props, null);
            } catch (CloudApplicationAdapterException e) {
                    e.printStackTrace();
            }
    }
    
   
    /**
     * This method updates the transformation builder with operationMappings relevant to the inbound flow.
     * Transformation is temporary because it only represents current selections by the wizard user      * 
     * @param adapterPluginContext2 
     * @param map
     * @param props
     * @param opMapping
     * @throws CloudApplicationAdapterException 
     */
    private void setOperationMappingsForGenerator(Map<String, UIObject> map, Properties props,  OperationMapping opMapping) 
                    throws CloudApplicationAdapterException {

            // get the selected biz object
            String bizObjName = UIFactory.getStringValue(map,
                            SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_BUSINESS_OBJ_LIST_FIELD);
            
            CloudDataObjectNode selectedObject = this.dataObjMap.get(bizObjName);
            
            adapterPluginContext.setContextObject(SampleCloudAdapterConstants.INBOUND_REQUEST_OBJECT, selectedObject);
            
            adapterPluginContext.setContextObject(CloudAdapterConstants.CLOUD_BIZ_OBJ, bizObjName);

            // create the new operation (process) in the inbound wsdl
            CloudOperationNodeImpl inboundOperation = new CloudOperationNodeImpl();

            inboundOperation.setName(SampleCloudAdapterConstants.DEFAULT_OP_NAME);
            if (opMapping == null) {
                    opMapping = new OperationMapping(inboundOperation);
                    opMapping.setNewOperationName(inboundOperation.getName());
                    opMapping.setDefaultRequestObjectGrouping(ObjectGrouping.ORDERED);
                    opMapping.setRequestObjectMappings(getTypeMappings(selectedObject));

            }else{
                    // the context already had an operation mapping that must be used.
                    opMapping.setTargetOperation(inboundOperation);
                    opMapping.setNewOperationName(inboundOperation.getName());
            }

            // any operation specific properties that may be required.
            if (props != null && !props.isEmpty()) {
                    opMapping.setOperationProperties(props);
            }

            // operation mapping will be enriched with the response types mappings in the reply / response page.
            adapterPluginContext.setContextObject(SampleCloudAdapterConstants.INBOUND_OPERATION_MAPPING, opMapping);
            
    }
    private List<TypeMapping> getTypeMappings(CloudDataObjectNode objectNode) throws CloudApplicationAdapterException {
        List<TypeMapping> typeMappings = new ArrayList<TypeMapping>();
        TypeMapping typeMapping = new TypeMapping(objectNode,false,false);
        
        typeMappings.add(typeMapping);
        return typeMappings;
    }
}
