/*
 * Sample cloud adapter operations page to display respective business object.
 */

/*
 * Sample cloud adapter operations page to display respective business object.
 */

package oracle.tip.tools.ide.adapters.samplecloudadapter.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import oracle.tip.tools.adapters.cloud.api.CloudAdapterConstants;
import oracle.tip.tools.adapters.cloud.api.CloudAdapterException;
import oracle.tip.tools.adapters.cloud.api.CloudAdapterPageState;
import oracle.tip.tools.adapters.cloud.api.ICloudAdapterPage;
import oracle.tip.tools.adapters.cloud.impl.CloudAdapterOperationsPage;
import oracle.tip.tools.adapters.cloud.l10n.CloudAdapterText;
import oracle.tip.tools.adapters.cloud.utils.CloudAdapterUtils;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.CloudMetadataBrowser;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.OperationMapping;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.TypeMapping;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudAPINode;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudDataObjectNode;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudOperationNode;
import oracle.tip.tools.ide.adapters.cloud.api.model.Field;
import oracle.tip.tools.ide.adapters.cloud.api.model.RequestParameter;
import oracle.tip.tools.ide.adapters.cloud.api.model.TransformationModel;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.CloudApplicationAdapterException;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.TransformationModelBuilder;
import oracle.tip.tools.ide.adapters.samplecloudadapter.util.SampleCloudAdapterCloudText;
import oracle.tip.tools.presentation.uiobjects.sdk.EditField;
import oracle.tip.tools.presentation.uiobjects.sdk.ISelectItem;
import oracle.tip.tools.presentation.uiobjects.sdk.ISelectObject;
import oracle.tip.tools.presentation.uiobjects.sdk.IShuttleObject;
import oracle.tip.tools.presentation.uiobjects.sdk.UIFactory;
import oracle.tip.tools.presentation.uiobjects.sdk.UIObject;


public class SampleCloudAdapterOperationsPage extends CloudAdapterOperationsPage {

    AdapterPluginContext adapterPluginContext;

    public SampleCloudAdapterOperationsPage(AdapterPluginContext adapterPluginContext) {
        super(adapterPluginContext);
        this.adapterPluginContext = adapterPluginContext;
        System.out.println("SampleCloudAdapterOperationsPage constructor");
    }

    public String getWelcomeText() {
        System.out.println("SampleCloudAdapterOperationsPage getWelcomeText");
        return SampleCloudAdapterCloudText.getString(SampleCloudAdapterCloudText.OPERATIONS_PAGE_WELCOME_TEXT);

    }


    @Override
    public LinkedList<EditField> getPageEditFields() {
        System.out.println("SampleCloudAdapterOperationsPage getPageEditFields");
        // inherting page properties / fields from parent class
        LinkedList<EditField> currentPageFields = super.getPageEditFields();
        try {
            // Retrieving adapter specific metadata browser class
            CloudMetadataBrowser browser = CloudAdapterUtils.getMetadataBrowser(adapterPluginContext);
            // load metadata information from metadata browser
            browser.loadMetadata(false);
            // Removing unused UI objects from parent class
            Map<String, EditField> map =
                EditField.getFieldMap(currentPageFields.toArray(new EditField[currentPageFields.size()]));
            // Remove unused UI objects from default page like Operation Mode
            EditField opModeEditField = map.get(CloudAdapterConstants.OPERATION_MODE);
            currentPageFields.remove(opModeEditField);
            // Remove unused UI objects from default page like Operation Type (API)
            EditField opTypeEditField = map.get(CloudAdapterConstants.CLOUD_API);
            currentPageFields.remove(opTypeEditField);
            populateOperationsList(currentPageFields, browser);
            populateBusinessObject(currentPageFields, browser, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return currentPageFields;

    }

    private void populateOperationsList(LinkedList<EditField> editFields, CloudMetadataBrowser browser) {
        System.out.println("SampleCloudAdapterOperationsPage populateOperationsList");
        String targetOp = null;
        try {
            List<CloudOperationNode> operationNodes = browser.getOperations();
            String[] operationNames = getSortedOperation(operationNodes);
            targetOp = getDefaultSelectedOperation(operationNodes).getName();
            Locale locale = CloudAdapterUtils.getLocale(adapterPluginContext);

            // using ISelectedObject UI control to display list of operations
            UIObject operationsObj =
                UIFactory.createSelectObject(operationNames, operationNames, targetOp, ISelectObject.DISPLAY_LIST,
                                             Boolean.TRUE);
            createOrUpdateField(editFields, CloudAdapterConstants.CLOUD_OPERATION,
                                CloudAdapterText.getString(locale,
                                                           CloudAdapterText.OPERATIONS_PAGE_CLOUD_OPERATION_LABEL),
                                CloudAdapterText.getString(locale,
                                                           CloudAdapterText.OPERATIONS_PAGE_CLOUD_OPERATION_DESC),
                                false, operationsObj, EditField.LabelFieldLayout.ONE_ROW_LAYOUT, null, 3,
                                EditField.LabelFieldAlignment.LEFT_LEFT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void populateBusinessObject(LinkedList<EditField> editFields, CloudMetadataBrowser browser,
                                        CloudOperationNode operationNode) {
        System.out.println("SampleCloudAdapterOperationsPage  populateBusinessObject");
        try {
            List<CloudOperationNode> operationNodes = browser.getOperations();
            List<CloudDataObjectNode> dataObjects = null;
            if (operationNode == null)
                dataObjects = browser.getDataObjectNodes(this.getDefaultSelectedOperation(operationNodes));
            else
                dataObjects = browser.getDataObjectNodes(operationNode);
            List<String> selectedDataObjects = new ArrayList<String>();
            Locale locale = CloudAdapterUtils.getLocale(adapterPluginContext);
            List<CloudAPINode> apis = browser.getAPIs();
            List selectItems = createSelectItems(dataObjects, null);

            // Using Shuttle object to display list of data / business objects. Shuttle object has a provision to move selected objects.
            UIObject bizObjNamesObj =
                UIFactory.createShuttleObject(selectItems,
                                              CloudAdapterText.getString(locale,
                                                                         CloudAdapterText.OPERATIONS_PAGE_AVAILABLE_LABEL,
                                                                         ""), selectedDataObjects,
                                              CloudAdapterText.getString(locale,
                                                                         CloudAdapterText.OPERATIONS_PAGE_SELECTED_LABEL),
                                              IShuttleObject.MultiSelectComponentType.SELECT_ORDER_SHUTTLE,
                                              Boolean.FALSE, true,
                                              CloudAdapterText.getString(locale,
                                                                         CloudAdapterText.OPERATIONS_PAGE_CLOUD_BIZ_OBJ_FILTER_PLACEHOLDER_TEXT),
                                              true, null, null);

            createOrReplaceField(editFields, CloudAdapterConstants.CLOUD_BIZ_OBJ,
                                 CloudAdapterText.getString(locale,
                                                            CloudAdapterText.OPERATIONS_PAGE_CLOUD_BIZ_OBJ_LABEL),
                                 CloudAdapterText.getString(locale,
                                                            CloudAdapterText.OPERATIONS_PAGE_CLOUD_BIZ_OBJ_DESC), true,
                                 bizObjNamesObj, CloudAdapterConstants.ROQL_QUERY_EDITOR,
                                 EditField.LabelFieldLayout.TWO_ROW_LAYOUT, -1);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    @Override
    public CloudAdapterPageState updateBackEndModel(LinkedHashMap<String, ICloudAdapterPage> wizardPages,
                                                    LinkedList<EditField> currentPageFields) {
        System.out.println("SampleCloudAdapterOperationsPage updateBackEndModel");
        Properties props = new Properties();
        Map map =
            EditField.getObjectMap((EditField[]) currentPageFields.toArray(new EditField[currentPageFields.size()]));
        //setupPropertiesForGeneration(props, map);
        CloudAdapterPageState state = new CloudAdapterPageState(false, wizardPages, currentPageFields);
        transferUIDataToModel(props, map);

        return state;

    }

    protected void transferUIDataToModel(Properties props, Map<String, UIObject> map) {
        System.out.println("SampleCloudAdapterOperationsPage transferUIDataToModel");
        CloudMetadataBrowser browser = CloudAdapterUtils.getMetadataBrowser(this.adapterPluginContext);
        try {
            setOperationMappingsForGenerator(this.adapterPluginContext, map, browser, props, null);
        } catch (CloudApplicationAdapterException e) {
            throw new CloudAdapterException(e);
        }
    }

    private void setOperationMappingsForGenerator(AdapterPluginContext adapterPluginContext, Map<String, UIObject> map,
                                                  CloudMetadataBrowser browser, Properties props,
                                                  OperationMapping opMapping) throws CloudAdapterException,
                                                                                     CloudApplicationAdapterException {
        System.out.println("SampleCloudAdapterOperationsPage setOperationMappingsForGenerator");
        TransformationModelBuilder modelBuilder =
            (TransformationModelBuilder) adapterPluginContext.getContextObject("_ui_ModelBuilder");

        String cloudOpName = UIFactory.getStringValue(map, "cloudOperation");
        CloudOperationNode selectedOperationNode = browser.getOperation(cloudOpName);

        opMapping = getSelectedOperation();

        if (opMapping == null) {
            opMapping = new OperationMapping(selectedOperationNode);
            modelBuilder.addOperationMapping(opMapping);
        } else {
            opMapping.setTargetOperation(selectedOperationNode);
            opMapping.setNewOperationName(cloudOpName);
            modelBuilder.addOperationMapping(opMapping);
        }

        try {
            opMapping.setResponseObjectMapping(getResponseTypeMappings(selectedOperationNode));
            opMapping.setRequestObjectMappings(getRequestTypeMappings(selectedOperationNode));
        } catch (CloudApplicationAdapterException e) {
            e.printStackTrace();
        }
        // Set BusinessObject Name and CloudOperation Name to contextObject to display in summary page.
        UIObject uiobject = (UIObject) map.get(CloudAdapterConstants.CLOUD_BIZ_OBJ);
        String bizObjName = ((IShuttleObject) uiobject).getSelectedValues().get(0);
        this.adapterPluginContext.setContextObject(CloudAdapterConstants.CLOUD_BIZ_OBJ, bizObjName);

        this.adapterPluginContext.setContextObject("cloudOperation", cloudOpName);

    }


    @Override
    public CloudAdapterPageState getUpdatedEditPages(LinkedHashMap<String, ICloudAdapterPage> wizardPages,
                                                     LinkedList<EditField> currentPageFields, String fieldName) {

        System.out.println("SampleCloudAdapterOperationsPage getUpdatedEditPages");
        CloudAdapterPageState pageState = new CloudAdapterPageState(false, wizardPages, currentPageFields);
        Map<String, EditField> map =
            EditField.getFieldMap(currentPageFields.toArray(new EditField[currentPageFields.size()]));
        String newValue =
            UIFactory.getStringValue(EditField.getObjectMap(currentPageFields.toArray(new EditField[currentPageFields.size()])),
                                     fieldName);
        CloudMetadataBrowser browser = CloudAdapterUtils.getMetadataBrowser(this.adapterPluginContext);
        //Locale locale = CloudAdapterUtils.getLocale(adapterPluginContext);
        if (fieldName.equals(CloudAdapterConstants.CLOUD_OPERATION)) {
            populateBusinessObject(currentPageFields, browser, browser.getOperation(newValue));
        }
        return pageState;


    }

    private OperationMapping getSelectedOperation() {
        System.out.println("SampleCloudAdapterOperationsPage getSelectedOperation");
        OperationMapping opMap = null;
        TransformationModelBuilder transformationModelBuilder =
            (TransformationModelBuilder) adapterPluginContext.getContextObject(CloudAdapterConstants.UI_MODEL_BUILDER);
        TransformationModel model = transformationModelBuilder.build();
        if (isValueSetForPopulation()) {
            Integer selectedIndex =
                (Integer) adapterPluginContext.getContextObject(CloudAdapterConstants.OPERATIONS_TABLE +
                                                                CloudAdapterConstants.SELECTED_ROW_INDEX);
            opMap = model.getOperationMappings().get(selectedIndex.intValue());
        } else {
            if (model.getOperationMappings().size() > 0) {
                opMap = model.getOperationMappings().get(0);
            }
        }
        return opMap;
    }

    private boolean isValueSetForPopulation() {
        System.out.println("SampleCloudAdapterOperationsPage isValueSetForPopulation");
        Integer selectedIndex =
            (Integer) adapterPluginContext.getContextObject(CloudAdapterConstants.OPERATIONS_TABLE +
                                                            CloudAdapterConstants.SELECTED_ROW_INDEX);
        return selectedIndex != null;
    }


    private CloudOperationNode getDefaultSelectedOperation(List<CloudOperationNode> operationNodes) {
        System.out.println("SampleCloudAdapterOperationsPage getDefaultSelectedOperation");
        String[] operationNames = getSortedOperation(operationNodes);
        CloudOperationNode selectedOpNode = null;
        for (CloudOperationNode opnode : operationNodes) {
            if (operationNames[0].equals(opnode.getName())) {
                selectedOpNode = opnode;
                break;
            }
        }
        return selectedOpNode;
    }

    private String[] getSortedOperation(List<CloudOperationNode> operationNodes) {
        System.out.println("SampleCloudAdapterOperationsPage getSortedOperation");
        String[] operationNames = new String[operationNodes.size()];
        for (int i = 0; i < operationNodes.size(); i++) {
            operationNames[i] = operationNodes.get(i).getName();
        }

        Arrays.sort(operationNames);
        return operationNames;
    }


    private List<ISelectItem> createSelectItems(List<CloudDataObjectNode> dataObjects, String groupFilterValue) {
        System.out.println("SampleCloudAdapterOperationsPage createSelectItems");
        List selectItems = new ArrayList(dataObjects.size());

        for (CloudDataObjectNode dataObject : dataObjects) {
            String objCategory = null;
            if (dataObject.getObjectCategory() != null)
                objCategory = dataObject.getObjectCategory().name();
            if ((groupFilterValue == null) || ("ALL".equals(groupFilterValue)) ||
                (groupFilterValue.equals(objCategory))) {
                selectItems.add(UIFactory.createSelectItem(dataObject.getName(), dataObject.getDescription()));
            }
        }
        Collections.sort(selectItems, new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                ISelectItem is1 = (ISelectItem) o1;
                ISelectItem is2 = (ISelectItem) o2;
                return is1.getValue().compareTo(is2.getValue());
            }
        });
        return selectItems;
    }

    private void createOrReplaceField(LinkedList<EditField> editFields, String fieldName, String fieldLabel,
                                      String desc, boolean required, UIObject uiObj, String fieldToBeReplaced,
                                      EditField.LabelFieldLayout layout, int rowIndex) {
        System.out.println("SampleCloudAdapterOperationsPage createOrReplaceField");
        Map<String, EditField> fieldsMap = EditField.getFieldMap(editFields.toArray(new EditField[editFields.size()]));
        EditField tobeReplacedField = fieldsMap.get(fieldToBeReplaced);
        int index = -1;
        if (tobeReplacedField != null) {
            index = editFields.indexOf(tobeReplacedField);
        }
        if (index > -1) {
            editFields.remove(index);
            EditField newField =
                UIFactory.createEditField(fieldName, fieldLabel, desc, required, false, uiObj, layout, null, rowIndex,
                                          EditField.LabelFieldAlignment.LEFT_LEFT);
            editFields.add(index, newField);
        } else {
            createOrUpdateField(editFields, fieldName, fieldLabel, desc, required, uiObj, layout, null,
                                EditField.DEFAULT_ROW_IDENTIFIER, EditField.LabelFieldAlignment.LEFT_LEFT);
        }
    }

    private void createOrUpdateField(LinkedList<EditField> editFields, String fieldName, String fieldLabel, String desc,
                                     boolean required, UIObject uiObj, EditField.LabelFieldLayout layout,
                                     String helpText, int rowIndex, EditField.LabelFieldAlignment labelFieldAlignment) {
        System.out.println("SampleCloudAdapterOperationsPage createOrUpdateField");
        EditField editField = null;
        Map<String, EditField> fieldsMap = EditField.getFieldMap(editFields.toArray(new EditField[editFields.size()]));
        if (fieldsMap != null) {
            editField = fieldsMap.get(fieldName);
        }

        if (editField != null) {
            int index = editFields.indexOf(editField);
            editFields.remove(index);
            editFields.add(index,
                           UIFactory.createEditField(fieldName, fieldLabel, desc, required, false, uiObj, layout,
                                                     helpText, rowIndex, labelFieldAlignment));
        } else
            editFields.add(UIFactory.createEditField(fieldName, fieldLabel, desc, required, false, uiObj, layout,
                                                     helpText, rowIndex, labelFieldAlignment));

    }

    private List<TypeMapping> getResponseTypeMappings(CloudOperationNode selectedOperation) throws CloudApplicationAdapterException {
        System.out.println("SampleCloudAdapterOperationsPage getResponseTypeMappings");
        List<TypeMapping> typeMappings = new ArrayList<TypeMapping>();

        if (selectedOperation.getResponse() != null && selectedOperation.getResponse().getResponseObject() != null) {
            Set<Field> fields = selectedOperation.getResponse().getResponseObject().getFields();
            Iterator<Field> it = fields.iterator();
            while (it.hasNext()) {
                Field descNode = it.next();
                CloudDataObjectNode t = descNode.getFieldType();
                TypeMapping tmap = new TypeMapping(descNode.getName(), t, true, true);
                System.out.println("Response parameter -->" + descNode.getName());
                typeMappings.add(tmap);
            }
        }

        return typeMappings;
    }

    private List<TypeMapping> getRequestTypeMappings(CloudOperationNode selectedOperation) throws CloudApplicationAdapterException {
        System.out.println("SampleCloudAdapterOperationsPage getRequestTypeMappings");
        List<TypeMapping> typeMappings = new ArrayList<TypeMapping>();

        Iterator<RequestParameter> reqIter = selectedOperation.getRequestParameters().iterator();
        while (reqIter.hasNext()) {
            RequestParameter param = reqIter.next();
            Set<Field> fields = param.getDataType().getFields();
            Iterator<Field> fieldIter = fields.iterator();
            while (fieldIter.hasNext()) {
                Field descNode = fieldIter.next();
                CloudDataObjectNode t = descNode.getFieldType();
                TypeMapping tmap = new TypeMapping(descNode.getName(), t, true, true);
                System.out.println("Request parameter -->" + descNode.getName());
                typeMappings.add(tmap);
            }
        }
        return typeMappings;
    }
}

