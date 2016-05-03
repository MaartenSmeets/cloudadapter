package oracle.tip.tools.ide.adapters.samplecloudadapter.plugin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


import oracle.tip.tools.ide.adapters.cloud.api.connection.AbstractCloudConnection;
import oracle.tip.tools.ide.adapters.cloud.api.connection.AuthenticationScheme;
import oracle.tip.tools.ide.adapters.cloud.api.connection.PingStatus;
import oracle.tip.tools.ide.adapters.cloud.api.connection.UsernamePasswordScheme;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterConstants;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.api.service.WSDLHelperService;

public class SampleCloudAdapterConnection extends AbstractCloudConnection {

    private AdapterPluginContext context;

    private UsernamePasswordScheme authenticationScheme;

    public SampleCloudAdapterConnection(AdapterPluginContext context) {
            super(context.getReferenceBindingName());
            this.context = context;
    }

    @Override
    public AuthenticationScheme getAuthenticationScheme() {
        if (authenticationScheme == null) {
                authenticationScheme = new UsernamePasswordScheme(context,this);
        }
        return authenticationScheme;
    }
    
    /**
     * Ping method is to test end system connectivity.
     * @return
     */
    @Override
    public PingStatus ping() {
        System.out.println("SampleCloudAdapterConnection PingStatus");
        try {
                WSDLHelperService wsdlHelper = context.getServiceRegistry().getService(WSDLHelperService.class);
            
                String wsdlURL = getConnectionProperties().getProperty(AdapterConstants.SOURCE_WSDL_LOCATION);
            
                URL endpointURL = (URL) context.getContextObject(AdapterConstants.CONNECTION_URL);
            
                if (endpointURL == null) {
                    endpointURL = wsdlHelper.getEndpointAddressFromWSDL(new URL(wsdlURL), getConnectionProperties());
                    context.setContextObject(AdapterConstants.CONNECTION_URL, endpointURL);
                }           
                return PingStatus.SUCCESS_STATUS;
        } catch (MalformedURLException e) {
                return new PingStatus(e);
        } catch (IOException e) {
                return new PingStatus(e);
        } catch (Exception e) {
                return new PingStatus(e);
        }
    }
   

}
