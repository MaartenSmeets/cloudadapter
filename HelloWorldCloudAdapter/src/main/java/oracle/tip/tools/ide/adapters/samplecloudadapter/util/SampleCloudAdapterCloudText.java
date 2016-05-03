/**
 * SampleCloudAdapterCloudText class is a util class to load adapter specific resource bundle.
 * SampleCloudAdapterBundle.properties is a adapter level resource bundle
 * static and final variable value is the key in resource bundle.
 */

package oracle.tip.tools.ide.adapters.samplecloudadapter.util;

    import java.util.ResourceBundle;

    public class SampleCloudAdapterCloudText {
    public static final String SAMPLE_CLOUD_CONNECTOR_BUNDLE = "oracle.tip.tools.ide.adapters.samplecloudadapter.util.SampleCloudAdapterBundle";

    public static ResourceBundle getBundle() {
        return ResourceBundle.getBundle(SAMPLE_CLOUD_CONNECTOR_BUNDLE);
    }


    public static String getString(String key) {
        if(getBundle() == null)
            return key;
        return getBundle().getString(key);
    }

    // Adapter Type
    public static final String ADAPTER_TYPE                     = "samplecloudadapter.adapter.type";

    // Welcome page
    public static final String WELCOME_PAGE_WELCOME_TEXT = "samplecloudadapter.welcome.page.welcome.text";

    // Connection page
    public static final String CONNECTION_PAGE_WELCOME_TEXT = "samplecloudadapter.connection.page.welcome.text";

    // Operations page
    public static final String OPERATIONS_PAGE_WELCOME_TEXT = "samplecloudadapter.operations.page.welcome.text";
    
    // Summary page
    public static final String SUMMARY_PAGE_WELCOME_TEXT = "samplecloudadapter.summary.page.welcome.text";
    public static final String SAMPLE_CLOUD_ADAPTER_SUMMARY_DESCRIPTION = "samplecloudadapter.summary.desc";
    
    // Inbound Request Configuration page
    public static final String SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_NAME = "samplecloudadapter.inbound.req.config.page.name";
    public static final String SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_TITLE = "samplecloudadapter.inbound.req.config.page.title";
    public static final String SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_WELCOME_TEXT = "samplecloudadapter.inbound.req.config.page.welcome.text";
    public static final String ENDPOINT_CONFIG_PAGE_SELECT_BIZ_OBJECT_VAL = "samplecloudadapter.endpoint.config.page.select.biz.object.val";
    public static final String ENDPOINT_CONFIG_PAGE_SELECT_ENDPOINT_CONFIG_OPTION_LABEL = "samplecloudadapter.endpoint.config.page.select.endpoint.config.option.label";
    public static final String ENDPOINT_CONFIG_PAGE_SELECT_ENDPOINT_CONFIG_OPTION_DESC = "samplecloudadapter.endpoint.config.page.select.endpoint.config.option.desc";
    public static final String SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_LISTBOX_FILTER_PLACEHOLDER_TEXT = "samplecloudadapter.inbound.req.config.page.listbox.filter.placeholder.text";
    
    
    public static final String SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_BUSINESS_OBJ_LIST_LABEL = "samplecloudadapter.inbound.req.config.page.biz.ojbect.list.label";
    public static final String SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_BUSINESS_OBJ_LIST_DESC = "samplecloudadapter.inbound.req.config.page.biz.ojbect.list.desc";

    
    // Inbound Reply Configuration page
    public static final String SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_NAME = "samplecloudadapter.inbound.rep.config.page.name";
    public static final String SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_TITLE = "samplecloudadapter.inbound.rep.config.page.title";
    public static final String SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_WELCOME_TEXT = "samplecloudadapter.inbound.rep.config.page.welcome.text";    
    public static final String SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_RESPONSE_TYPE_LISTBOX_LABEL = "samplecloudadapter.inbound.rep.config.page.res.type.listbox.label";
    public static final String SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_SEND_RES_CHECKBOX_LABEL = "samplecloudadapter.inbound.rep.config.page.send.res.checkbox.label";
    public static final String SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_SEND_FAULT_CHECKBOX_LABEL = "samplecloudadapter.inbound.rep.config.page.send.fault.checkbox.label";
    public static final String SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_SEND_CHECKBOX_LABEL = "samplecloudadapter.inbound.rep.config.page.send.checkbox.label";
    public static final String SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_IMMEDIATE_RESPONSE_VALUE = "samplecloudadapter.inbound.rep.config.page.immediate.res.value";    
    
    
}
