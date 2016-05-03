/**
 * This is a entry level class for JDeveloper.
 */

package oracle.tip.tools.ide.adapters.samplecloudadapter.wizard;

import oracle.tip.tools.ide.adapters.designtime.adapter.JcaEndpointAbstract;
import java.awt.Frame;

import oracle.ide.model.Project;
import oracle.ide.Context;

import oracle.tip.tools.adapters.cloud.api.ICloudAdapterUIBinding;
import oracle.tip.tools.ide.fabric.api.EndpointController;
import oracle.tip.tools.ide.fabric.api.EndpointInfo;
import oracle.tip.tools.ide.fabric.api.SCAEndpointOptions;
import oracle.tip.tools.ide.portablewizard.controller.WizardUIHandler;
import oracle.tip.tools.ide.portablewizard.controller.WizardUIHandlerForJcaAdapters;
import oracle.tip.tools.ide.portablewizard.controller.WizardController;

public class SampleCloudScaEndpointImpl extends JcaEndpointAbstract {

    public static final String adapterId = "sample";
      
    /**
     * To display wizard which has series of pages for adapter configuration
     * @param frame
     * @param jdevProject
     * @param endpointController
     * @param scaEndpointOptions
     * @param isExternalReference
     * @return
     * @throws Exception
     */
    @Override
    public EndpointInfo runCreateWizard(Frame frame,Project jdevProject,
                                        EndpointController endpointController,
                                        SCAEndpointOptions scaEndpointOptions,
                                        boolean isExternalReference) throws Exception
    {
        Context jcontext = Context.newIdeContext();
        WizardUIHandler uiHandler = new WizardUIHandlerForJcaAdapters(frame,adapterId,endpointController,
                                                                            this.endpointInfo,scaEndpointOptions, isExternalReference); 
        
        ICloudAdapterUIBinding iCloudAdapterUIBinding  = new SampleCloudUIBinding(uiHandler.getFilter(),uiHandler.getLocale());
        
        String serviceName =
            WizardController.displayDialog(frame, jcontext,
                                                             jdevProject,controller, scaEndpointOptions,
                                                           null,null,isExternalReference,
                                                           iCloudAdapterUIBinding,uiHandler); 
          if (serviceName == null) return null;  //canceled
          
          EndpointInfo endpointInfo = controller.getEndpointInfo(serviceName);
        
        return endpointInfo;
    }

    /**
     * To display wizard for any configuration updates. This method required when user selects (double click) adapter after the configuration (revisit).
     * @param frame
     * @param jdevProject
     * @param endpointController
     * @param scaEndpointOptions
     * @param isExternalReference
     * @return
     * @throws Exception
     */
    public EndpointInfo runUpdateWizard(Frame frame,Project jdevProject,
                                       EndpointController endpointController,SCAEndpointOptions scaEndpointOptions,
                                       boolean isExternalReference) throws Exception{
      
      return runCreateWizard(frame,jdevProject,endpointController,scaEndpointOptions,isExternalReference); 
    } 
}
