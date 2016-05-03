/**
 * This interface is responsible for interacting between the wizard and the actual adapter pages
 */

package oracle.tip.tools.ide.adapters.samplecloudadapter.wizard;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import oracle.tip.tools.adapters.cloud.api.ByteArrayResourceCreationStrategy;
import oracle.tip.tools.adapters.cloud.api.CloudAdapterArtifacts;
import oracle.tip.tools.adapters.cloud.api.CloudAdapterConstants;
import oracle.tip.tools.adapters.cloud.api.CloudAdapterException;
import oracle.tip.tools.adapters.cloud.api.CloudAdapterFilter;
import oracle.tip.tools.adapters.cloud.api.ICloudAdapterPage;
import oracle.tip.tools.adapters.cloud.impl.AbstractCloudAdapterUIBinding;
import oracle.tip.tools.adapters.cloud.impl.CloudAdapterConnectionPage;
import oracle.tip.tools.adapters.cloud.utils.CloudAdapterUtils;
import oracle.tip.tools.ide.adapters.cloud.api.connection.AbstractCloudConnection;
import oracle.tip.tools.ide.adapters.cloud.api.generation.GeneratorOptions;
import oracle.tip.tools.ide.adapters.cloud.api.generation.RuntimeMetadata;
import oracle.tip.tools.ide.adapters.cloud.api.generation.RuntimeMetadataGenerator;
import oracle.tip.tools.ide.adapters.cloud.api.model.TransformationModel;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterConstants;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.TransformationModelBuilder;
import oracle.tip.tools.ide.adapters.cloud.impl.plugin.AbstractCloudApplicationAdapter;
import oracle.tip.tools.ide.adapters.samplecloudadapter.plugin.SampleCloudAdapter;
import oracle.tip.tools.ide.adapters.samplecloudadapter.util.SampleCloudAdapterConstants;
import oracle.tip.tools.presentation.uiobjects.sdk.EditField;

public class SampleCloudUIBinding extends AbstractCloudAdapterUIBinding {

    public SampleCloudUIBinding(CloudAdapterFilter cloudAdapterFilter, Locale locale) throws CloudAdapterException {

        super(cloudAdapterFilter, locale, SampleCloudAdapterConstants.ADAPTER_NAME);
        try {
            if (cloudAdapterFilter.getApplicationInstanceId() == null) {

                // Creating Sample adapter instance
                AbstractCloudApplicationAdapter cloudApplicationAdapter = new SampleCloudAdapter(this.context);
                context.setContextObject(CloudAdapterConstants.APPLICATION_ADAPTER, cloudApplicationAdapter);

                // initialize sample cloud adapter
                super.useApplicationAdapterToInitialise(cloudApplicationAdapter);
            }
        } catch (Exception e) {
            throw new CloudAdapterException(e.getMessage());
        }
    }

    /**
     * This method returns list of pages required to show in the Wizard.
     * Adapter Developer should create this map. The key in the map is pageId and the value is an instance of ICloudAdapterPage.
     * The sequence of pages in the map will be displayed in the same sequence in Wizard.
     * @return
     * @throws CloudAdapterException
     */
    public LinkedHashMap<String, ICloudAdapterPage> getEditPages(Object adapterConfiguration) {
        LinkedHashMap<String, ICloudAdapterPage> editPages = new LinkedHashMap<String, ICloudAdapterPage>();
        editPages = new LinkedHashMap<String, ICloudAdapterPage>();
        String referenceName = context.getReferenceBindingName();

        // Add Welcome Page to Sample Adapter Wizard
        editPages.put(CloudAdapterConstants.WELCOME_PAGE_ID, new SampleCloudAdapterWelcomePage(context));

        // Skip connection page if applicationInstanceId is present. This is required only for JDeveloper.
        // ICS generates connection page by default
        CloudAdapterFilter filter =
            (CloudAdapterFilter) context.getContextObject(CloudAdapterConstants.UI_CLOUD_ADAPTER_FILTER);

        System.out.println();
        if (filter.isAddConnection()) {
            // Add Connection Page to Sample Adapter Wizard
            editPages.put(CloudAdapterConstants.CONNECTION_PAGE_ID, new SampleCloudAdapterConnectionPage(context));
        }

        // Check for inbound or outbound interaction
        if (!filter.isInbound()) {
            // Add Operations Page to Sample Adapter Wizard for outbound configuration.
            editPages.put(CloudAdapterConstants.OPERATIONS_PAGE_ID, new SampleCloudAdapterOperationsPage(context));
        } else {
            // Add Request config page for inbound interaction
            editPages.put(SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REQ_CONFIG_PAGE_ID,
                          new SampleInboundRequestConfigPage(context));

            // Add Response Config Page for inbound interaction
            editPages.put(SampleCloudAdapterConstants.SAMPLE_CLOUD_APP_INBOUND_REP_CONFIG_PAGE_ID,
                          new SampleInboundReplyConfigPage(context));
        }

        // Add Summary page to Sample Adapter Wizard
        editPages.put(CloudAdapterConstants.SUMMARY_PAGE_ID, new SampleCloudAdaterSummaryPage(context));

        return editPages;
    }

    @Override
    public CloudAdapterArtifacts generateMetadataArtifacts(LinkedHashMap<String, LinkedList<EditField>> wizardUpdatedEditFields) throws CloudAdapterException {
        System.out.println("SampleCloudUIBinding generateMetadataArtifacts");
        // Remove partnerlink element in wsdl
        System.out.println("\tRuntimeMetadataGenerator created");
        RuntimeMetadataGenerator metadataGen =
            (RuntimeMetadataGenerator) this.context.getContextObject(CloudAdapterConstants.UI_RUNTIME_METADATA_GENERATOR);
        System.out.println("\tRuntimeMetadataGenerator created");
        metadataGen.setGeneratorOption(GeneratorOptions.OPTION_GENERATE_PARTNER_LINK, "false");

        CloudAdapterArtifacts artifacts = null;
        try {
            AbstractCloudConnection connection =
                (AbstractCloudConnection) context.getContextObject(CloudAdapterConstants.CONNECTION);
            System.out.println("\tAbstractCloudConnection created");
            metadataGen.setConnection(connection);

            ByteArrayResourceCreationStrategy jcaLoc =
                new ByteArrayResourceCreationStrategy(AdapterConstants.ARTIFACT_KEY_JCA);
            ByteArrayResourceCreationStrategy wsdlLoc =
                new ByteArrayResourceCreationStrategy(AdapterConstants.ARTIFACT_KEY_WSDL);

            metadataGen.setJCAResourceOutput(jcaLoc);
            System.out.println("\tJCA loc: "+AdapterConstants.ARTIFACT_KEY_JCA);
            metadataGen.setWSDLResourceOutput(wsdlLoc);
            System.out.println("\tWSDL loc: "+AdapterConstants.ARTIFACT_KEY_WSDL);

            //Map builders = (Map)this.context.getContextObject("UI_MODEL_BUILDER_MAP");
            Map<String, TransformationModelBuilder> builders =
                (Map<String, TransformationModelBuilder>) context.getContextObject(CloudAdapterConstants.UI_MODEL_BUILDER_MAP);
            System.out.println("\tBuilders map created");
            if ((builders == null) || (builders.isEmpty())) {
                TransformationModelBuilder modelBuilder =
                    (TransformationModelBuilder) context.getContextObject(CloudAdapterConstants.UI_MODEL_BUILDER);
                System.out.println("\tTransformationModelBuilder created");
                builders = new HashMap<String, TransformationModelBuilder>();
                builders.put(CloudAdapterConstants.ARTIFACT_KEY_REQUEST, modelBuilder);
            }

            String referenceName = this.context.getReferenceBindingName();
            System.out.println("\treference binding name: "+this.context.getReferenceBindingName());
            Map<String, List<String>> selectedOpsMap = new HashMap<String, List<String>>();
            for (String key : builders.keySet()) {
                jcaLoc.setKeyName(key);
                wsdlLoc.setKeyName(key);
                System.out.println("\tsetKeyName done");
                this.context.setReferenceBindingName(getFormattedInternalRefName(referenceName, key,
                                                                                 jcaLoc.getResourceType()));
                System.out.println("\tsetReferenceBindingName done");
                TransformationModel tm = ((TransformationModelBuilder) builders.get(key)).build();
                System.out.println("\tTransformationModel created");
                metadataGen.setTransformationModel(tm);
                RuntimeMetadata runtimeMetaData = metadataGen.generate();
                System.out.println("\truntimeMetaData created");
                List selectedOps = CloudAdapterUtils.getSelectedWSDLOperations(tm);
                selectedOpsMap.put(key, selectedOps);
            }
            this.context.setReferenceBindingName(referenceName);
            artifacts = new CloudAdapterArtifacts(jcaLoc.getArtifacts(), wsdlLoc.getArtifacts(), referenceName);

            artifacts.setWsdlOperationsMap(selectedOpsMap);
            artifacts.setDescription((String) context.getContextObject(CloudAdapterConstants.CLOUD_ADAPTER_DESCRIPTION_ARTIFACT));
        } catch (Exception ex) {
            throw new CloudAdapterException(ex.getMessage(), ex);
        }
        System.out.println("\tbefore return");
        return artifacts;
    }

    public AdapterPluginContext getAdapterConfiguration() {
        return context;
    }
}
