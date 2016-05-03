/**
 * SampleCloudAdapterUtil is a adapter level util class.
 */

package oracle.tip.tools.ide.adapters.samplecloudadapter.util;

import com.sun.xml.internal.messaging.saaj.util.Base64;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;

import java.util.Random;

import java.util.TimeZone;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import javax.xml.soap.SOAPPart;

import oracle.tip.tools.adapters.cloud.api.CloudAdapterConstants;
import oracle.tip.tools.adapters.cloud.api.CloudAdapterException;
import oracle.tip.tools.adapters.cloud.utils.CloudAdapterUtils;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.CloudMetadataBrowser;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.ObjectGrouping;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.OperationMapping;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.TypeMapping;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudDataObjectNode;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudOperationNode;
import oracle.tip.tools.ide.adapters.cloud.api.model.TransformationModel;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterConstants;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.api.service.SOAPHelperService;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.TransformationModelBuilder;
import oracle.tip.tools.presentation.uiobjects.sdk.EditField;
import oracle.tip.tools.presentation.uiobjects.sdk.UIFactory;
import oracle.tip.tools.presentation.uiobjects.sdk.UIObject;


public class SampleCloudAdapterUtil {
	
    public static int counter = 0;
    
    /**
     * To create request SOAP Message to "Ping" target system operation.
     * Constructing below SOAP Message along with SOAP Header
     * <soapenv:Header>
      <wsse:Security soapenv:mustUnderstand="1" xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
         <wsu:Timestamp wsu:Id="TS-3DDF8083F1ACCFA24E14337606895992">
            <wsu:Created>2015-06-08T10:51:29.598Z</wsu:Created>
            <wsu:Expires>2015-06-08T10:52:29.598Z</wsu:Expires>
         </wsu:Timestamp>
         <wsse:UsernameToken wsu:Id="UsernameToken-3DDF8083F1ACCFA24E14337606850381">
            <wsse:Username>user</wsse:Username>
            <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">password</wsse:Password>
            <wsse:Nonce EncodingType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary">yRxVWVYBKDLOqbzd9rZLIA==</wsse:Nonce>
            <wsu:Created>2015-06-08T10:51:25.035Z</wsu:Created>
         </wsse:UsernameToken>
      </wsse:Security>
     * 
     * @param username
     * @param password
     * @param connectionProperties
     * @param helperService
     * @return
     * @throws Exception
     */
    public static SOAPMessage createRequestSOAPMessage(String username,
                    String password, Properties connectionProperties,
                    SOAPHelperService helperService) throws Exception {
        
            
            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage message = factory.createMessage();
            try{
            
            //message = factory.createMessage();
            SOAPPart part = message.getSOAPPart();
            SOAPEnvelope envelope = part.getEnvelope();
            envelope.addNamespaceDeclaration("typ", SampleCloudAdapterConstants.SAMPLE_MESSAGES_NAMESPACE);
            SOAPHeaderElement she = helperService.createWSSUsernameTokenHeader(
                            message, username, password);
            she.setAttribute("env:mustUnderstand", "0");
            Iterator it = she.getChildElements(SampleCloudAdapterConstants.WSS_USERNAME_TOKEN);
            SOAPElement unte = (SOAPElement) it.next();
            // nonce
            QName nonce = new QName(SampleCloudAdapterConstants.WSS_NS_URI, SampleCloudAdapterConstants.NONCE2,
                            SampleCloudAdapterConstants.WSSE);
            SOAPElement nonceElement = unte.addChildElement(nonce);
            nonceElement.setAttribute(
                                            SampleCloudAdapterConstants.ENCODING_TYPE,
                                            SampleCloudAdapterConstants.HTTP_DOCS_OASIS_OPEN_ORG_WSS_2004_01_OASIS_200401_WSS_SOAP_MESSAGE_SECURITY_1_0_BASE64_BINARY);
            nonceElement.setTextContent(new String(Base64.encode(SampleCloudAdapterUtil.getRandomString().getBytes())));
            // created
            Date dNow = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(SampleCloudAdapterConstants.YYYY_MM_DD_T_HH_MM_SS_SSS_Z);
            sdf.setTimeZone(TimeZone.getTimeZone(SampleCloudAdapterConstants.GMT));
            QName dt = new QName(SampleCloudAdapterConstants.WSU_NS_URI, SampleCloudAdapterConstants.CREATED,
                            SampleCloudAdapterConstants.WSU);
            SOAPElement createdElement = unte.addChildElement(dt);
            createdElement.setTextContent(sdf.format(dNow));

            // If target system expects Timestamp within header
            QName wssTimestampToken = new QName(SampleCloudAdapterConstants.WSU_NS_URI,
                            SampleCloudAdapterConstants.TIMESTAMP, SampleCloudAdapterConstants.WSU);
            SOAPElement timeStampHeaderElement = she
                            .addChildElement(wssTimestampToken);
            timeStampHeaderElement.setAttribute(SampleCloudAdapterConstants.WSU_ID, SampleCloudAdapterConstants.TS
                            + Integer.toString(counter++));
            
            timeStampHeaderElement.addChildElement(createdElement);
            Date dLater = getLaterTime();
            dt = new QName(SampleCloudAdapterConstants.WSU_NS_URI, SampleCloudAdapterConstants.EXPIRES,
                            SampleCloudAdapterConstants.WSU);
            SOAPElement expiredElement = timeStampHeaderElement.addChildElement(dt);
            expiredElement.setTextContent(sdf.format(dLater));
                // message.writeTo(System.out);
            }catch(Exception e){e.printStackTrace();}
            
            return message;
    }
    public static String getRandomString() {
            Random generator = new Random();
            String str = String.valueOf(generator.nextInt(999999999));
            return str;
    }
    public static Date getLaterTime() {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.HOUR_OF_DAY, 1);
            return cal.getTime();
    }
    
    
    public static void createOrUpdateField(LinkedList<EditField> editFields, 
            String fieldName, String fieldLabel, String desc, boolean required, 
            UIObject uiObj, EditField.LabelFieldLayout layout, String helpText, int rowIndex, 
            EditField.LabelFieldAlignment labelFieldAlignment, boolean isDisabled) {
    
            EditField editField = null;
        Map<String, EditField> fieldsMap = EditField.getFieldMap(editFields.toArray(new EditField[editFields.size()]));
        if (fieldsMap != null) {
            editField = fieldsMap.get(fieldName);
        }
    
        if (editField != null) {
            int index = editFields.indexOf(editField);
            editFields.remove(index);
            editFields.add(index, UIFactory.createEditField(fieldName, fieldLabel, desc, required,isDisabled, uiObj, layout, helpText, rowIndex, labelFieldAlignment));
        } else{
            editFields.add(UIFactory.createEditField(fieldName, fieldLabel, desc, required,isDisabled, uiObj, layout, helpText, rowIndex, labelFieldAlignment));
        }
    }
    
    public static void createOrReplaceField(LinkedList<EditField> editFields
            , String fieldName
            , String fieldLabel
            , String desc
            , boolean required
            , UIObject uiObj
            , String fieldToBeReplaced
            ,EditField.LabelFieldLayout layout
            , int rowIndex) {
        Map<String, EditField> fieldsMap = EditField.getFieldMap(editFields.toArray(new EditField[editFields.size()]));
        EditField tobeReplacedField = fieldsMap.get(fieldToBeReplaced);
        int index = -1;
        if (tobeReplacedField != null) {
            index = editFields.indexOf(tobeReplacedField);
        }
        if (index > -1) {
            editFields.remove(index);
            EditField newField = UIFactory.createEditField(fieldName, fieldLabel, desc, required, false, uiObj, layout, null, rowIndex, EditField.LabelFieldAlignment.LEFT_LEFT);
            editFields.add(index, newField);
        } else {
            createOrUpdateField(editFields, fieldName, fieldLabel, desc, required, uiObj, layout, null, EditField.DEFAULT_ROW_IDENTIFIER, EditField.LabelFieldAlignment.LEFT_LEFT);
        }
    }
    
    /**
     * locate the given field from the map and update it.
     * If the field is not there, create it in the map.
     * @param editFields
     * @param fieldName
     * @param fieldLabel
     * @param desc
     * @param uiObj
     */
    public static void createOrUpdateField(LinkedList<EditField> editFields,
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
            } else{
                    editFields.add(UIFactory.createEditField(fieldName, fieldLabel,
                                    desc, uiObj));
            }

    }
    
    public static void createOrUpdateField(LinkedList<EditField> editFields, 
            String fieldName, String fieldLabel, String desc, boolean required, 
            UIObject uiObj, EditField.LabelFieldLayout layout, String helpText, int rowIndex, 
            EditField.LabelFieldAlignment labelFieldAlignment) {
    
            createOrUpdateField(editFields, fieldName, fieldLabel, desc, required, uiObj, layout, helpText, rowIndex, labelFieldAlignment, false);
    
    }
    
    public static void createOrUpdateField(LinkedList<EditField> editFields,
                    String fieldName, String fieldLabel, String description,
                    boolean isRequired, boolean isDisabled, UIObject uiObj,
                    EditField.LabelFieldLayout labelFieldLayout, String helpText,
                    int rowIdentifier,
                    EditField.LabelFieldAlignment oneRowLabelFieldAlignment) {
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
                                    fieldLabel, description, isRequired, isDisabled, uiObj,
                                    labelFieldLayout, helpText, rowIdentifier,
                                    oneRowLabelFieldAlignment));
            } else
                    editFields.add(UIFactory.createEditField(fieldName, fieldLabel,
                                    description, isRequired, isDisabled, uiObj,
                                    labelFieldLayout, helpText, rowIdentifier,
                                    oneRowLabelFieldAlignment));

    }
    
    /**
     * This method returns one of the named transformation model builders from the model builder map. 
     * If none is present, then it returns null.
     * @param adapterPluginContext
     * @param type
     * @return
     */
    public static TransformationModelBuilder getTransformationModelBuilder(AdapterPluginContext adapterPluginContext, String type){
        Map<String, TransformationModelBuilder> modelBuilderMap = 
                        (Map<String, TransformationModelBuilder>) adapterPluginContext.getContextObject(CloudAdapterConstants.UI_MODEL_BUILDER_MAP);

        if (modelBuilderMap == null) { 
                return null; 
        }
        
        return modelBuilderMap.get(type);       
    }
    
    /**
     * return the default model builder
     * 
     *  For outbound: 
     *          CloudAdapterConstants.UI_MODEL_BUILDER
     *  For inbound: 
     *          CpqCloudCloudConstants.ARTIFACT_KEY_REQUEST
     * @param adapterPluginContext
     * @return
     */
        public static TransformationModelBuilder getTransformationModelBuilderForSummary(AdapterPluginContext adapterPluginContext){
                Map<String, TransformationModelBuilder> builders = (Map<String, TransformationModelBuilder>)
                                adapterPluginContext.getContextObject(CloudAdapterConstants.UI_MODEL_BUILDER_MAP);
                if(builders == null || builders.isEmpty()){
                        return (TransformationModelBuilder) adapterPluginContext.getContextObject(CloudAdapterConstants.UI_MODEL_BUILDER);
                }else{
                        return (TransformationModelBuilder)builders.get(CloudAdapterConstants.ARTIFACT_KEY_REQUEST);
                }
        }
    
    /**
     * Crate a new model builder for the given key and add it to the UI model builder map.
     */
    public static TransformationModelBuilder createTransformationModelBuilder(AdapterPluginContext adapterPluginContext, String type){

        
        Map<String, TransformationModelBuilder> modelBuilderMap = 
                                (Map<String, TransformationModelBuilder>) adapterPluginContext.getContextObject(CloudAdapterConstants.UI_MODEL_BUILDER_MAP);

                if (modelBuilderMap == null) { 
                        modelBuilderMap = new HashMap<String, TransformationModelBuilder>();
                        adapterPluginContext.setContextObject(CloudAdapterConstants.UI_MODEL_BUILDER_MAP, modelBuilderMap);                     
                }

                TransformationModelBuilder modelBuilder = new TransformationModelBuilder(adapterPluginContext);

                if(SampleCloudAdapterConstants.ARTIFACT_KEY_ENDPOINT.equals(type)){
                        modelBuilder.setTargetNamespace(SampleCloudAdapterConstants.ENDPOINT_WSDL_DEFAULT_TARGET_NS);
                        modelBuilder.setTypeNamespace(SampleCloudAdapterConstants.ENDPOINT_WSDL_DEFAULT_TYPE_NS);                   
                }else if (SampleCloudAdapterConstants.ARTIFACT_KEY_REQUEST.equals(type)){                    
                        
                }else if (SampleCloudAdapterConstants.ARTIFACT_KEY_CALLBACK_SUC.equals(type)){

                }else if (SampleCloudAdapterConstants.ARTIFACT_KEY_CALLBACK_FAIL.equals(type)){

                }
                
                modelBuilderMap.put(type, modelBuilder);

                return modelBuilder;
    }
    
    /**
     * Remove the given type of model builder from the adpater plugin context if it exists.
     */
    public static void removeTransformationModelBuilder(AdapterPluginContext adapterPluginContext,String type){
        
                Map<String, TransformationModelBuilder> modelBuilderMap = 
                                (Map<String, TransformationModelBuilder>) adapterPluginContext.getContextObject(CloudAdapterConstants.UI_MODEL_BUILDER_MAP);
         
                if (modelBuilderMap == null) { 
                        modelBuilderMap = new HashMap<String, TransformationModelBuilder>();
                        adapterPluginContext.setContextObject(CloudAdapterConstants.UI_MODEL_BUILDER_MAP, modelBuilderMap);
                        return; 
                }
                
                if(modelBuilderMap.containsKey(type)){
                        modelBuilderMap.remove(type); 
                }
    }
}
