/**
 * To declare adapter level constant variables
 */
package oracle.tip.tools.ide.adapters.samplecloudadapter.util;

import javax.xml.namespace.QName;

public class SampleCloudAdapterConstants {
	
	public static final String APP_ID = "appID";
	public static final String MAX_VERSION = "1.2";
        public static final String ADAPTER_NAME = "sample";
        public static final String IS_INBOUND = "mcc_inbound";
        
        public static final String ARTIFACT_KEY_ENDPOINT = "ENDPOINT";
        public static final String ARTIFACT_KEY_REQUEST = "REQUEST";
        
        public static final String ARTIFACT_KEY_CALLBACK_SUC = "CALLBACK_SUC";
        public static final String ARTIFACT_KEY_CALLBACK_FAIL = "CALLBACK_FAIL";
        
        public static final String ENDPOINT_WSDL_DEFAULT_TARGET_NS = "http://xmlns.oracle.com/cloud/adapter/samplecloudadapter";
        public static final String ENDPOINT_WSDL_DEFAULT_TYPE_NS = "http://xmlns.oracle.com/cloud/adapter/samplecloudadapter/types";
        // Business service and Operation Name
                public static final String SAMPLE_CATALOG_SERVICE_OP_NAME="GetAccount";
        public static final String SAMPLE_MESSAGES_NAMESPACE="http://example.oracle.com/cloud/sample/SampleAccountService.wsdl/types";
        public static final String SAMPLE_SERVICE_NAMESPACE="http://example.oracle.com/cloud/sample/SampleAccountService.wsdl";
        
        
        // SOAP Header request details
        public static final String WSS_NS_URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
        public static final String WSU_NS_URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
        public static final String WSS_PWTEXT = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText";
    
        public  static final QName WSS_USERNAME_TOKEN = new QName(WSS_NS_URI,"UsernameToken");
        public static final String WSSE = "wsse";
        public static final String ENCODING_TYPE = "EncodingType";
        public static final String HTTP_DOCS_OASIS_OPEN_ORG_WSS_2004_01_OASIS_200401_WSS_SOAP_MESSAGE_SECURITY_1_0_BASE64_BINARY = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary";
        public static final String YYYY_MM_DD_T_HH_MM_SS_SSS_Z = "yyyy-MM-dd'T'HH:mm:ss.sss'Z'";
        public static final String TIMESTAMP = "Timestamp";
        public static final String WSU = "wsu";
        public static final String WSU_ID = "wsu:Id";
        public static final String EXPIRES = "Expires";
        public static final String NONCE2 = "Nonce";
        public static final String CREATED = "Created";
        public static final String TS = "TS-";
        public static final String GMT = "GMT";

        // Inbound Request Configuration Page
        public static final String SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_ID = "sampleCloudAdapterInboundRequestConfigPage";
        public static final String SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_BUSINESS_OBJ_LIST_FIELD = "inBizObjList";
        public static final String INBOUND_OPERATION_MAPPING = "inboundOperationMapping";
        public static final String INBOUND_OPERATION_OBJECT = "inboundCloudOperation";
        public static final String SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_SEPARATOR = "sampleCloudConnectorInboundRequestConfigPageSeparator";
        public static final String ENDPOINT_CONFIG_OPTION = "endpointConfigOption";
        public static final String BUSINESS_OBJECTS = "bizObjects";
        public static final String DEFAULT_OP_NAME = "process";
        public static final String INBOUND_REQUEST_OBJECT = "inboundRequestObject";
        
        // Inbound Response Configuration Page
        public static final String SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_ID = "sampleCloudAdapterInboundReplyConfigPage";
        public static final String SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_SEND_RES_CHECKBOX = "sampleCloudConnectorInboundReplyConfigPageSendResCheckbox";
        public static final String IMMEDIATE_RESPONSE = "immediateResponse";
        public static final String DELAYED_RESPONSE = "delayedResponse";
        public static final String RESPONSE_TYPE_LISTBOX = "responseTypeListbox";
        public static final String SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_SEND_FAULT_CHECKBOX = "sampleCloudConnectorInboundReplyConfigPageSendFaultCheckbox";
        public static final String CALLBACK_RESPONSE_LISTBOX = "callbackResponseListbox";
        public static final String RESPONSE_OBJECT = "responseObject";  
        
        
        //Operations
        public static final String CLOUD_API_VERSION = "1.0";
        public static final String CLOUD_API="Sample Cloud Adapter";
        public static final String SAMPLE_CLOUD_ADAPTER_BUSINESS_OBJ="createAccount";
        public static final String SAMPLE_CLOUD_ADAPTER_PROPERTIES = "properties";
        
        //Runtime Adapter Connection factory class name
        public static final String CONNECTION_FACTORY_CLASS = "oracle.tip.tools.ide.adapters.samplecloudadapter.runtime.SampleCloudAppConnectionFactory";
}
