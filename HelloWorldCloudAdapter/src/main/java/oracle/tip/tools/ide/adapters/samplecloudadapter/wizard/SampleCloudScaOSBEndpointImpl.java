package oracle.tip.tools.ide.adapters.samplecloudadapter.wizard;

import oracle.sb.tooling.ide.sca.internal.model.jca.SCAEndpointJcaAdapter;

import oracle.tip.tools.ide.fabric.api.SCAEndpointAbstract;

public class SampleCloudScaOSBEndpointImpl extends SCAEndpointJcaAdapter {
    public SampleCloudScaOSBEndpointImpl()
    {
      super("sample", new SampleCloudScaEndpointImpl());
    }
}
