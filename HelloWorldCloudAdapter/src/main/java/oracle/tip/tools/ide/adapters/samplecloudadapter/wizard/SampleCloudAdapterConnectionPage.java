package oracle.tip.tools.ide.adapters.samplecloudadapter.wizard;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import oracle.tip.tools.adapters.cloud.api.ICloudAdapterPage;
import oracle.tip.tools.adapters.cloud.impl.CloudAdapterConnectionPage;
import oracle.tip.tools.adapters.cloud.l10n.CloudAdapterText;
import oracle.tip.tools.adapters.cloud.utils.CloudAdapterUtils;
import oracle.tip.tools.ide.adapters.cloud.api.connection.AbstractCloudConnection;
import oracle.tip.tools.ide.adapters.cloud.api.connection.PingStatus;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.presentation.uiobjects.sdk.EditField;
import oracle.tip.tools.presentation.uiobjects.sdk.IPasswordObject;
import oracle.tip.tools.presentation.uiobjects.sdk.UIError;
import oracle.tip.tools.presentation.uiobjects.sdk.UIFactory;
import oracle.tip.tools.presentation.uiobjects.sdk.UIObject;

public class SampleCloudAdapterConnectionPage  extends CloudAdapterConnectionPage {
    private AdapterPluginContext context;
    
    public SampleCloudAdapterConnectionPage(AdapterPluginContext adapterPluginContext) {
        super(adapterPluginContext);
        this.adapterPluginContext = adapterPluginContext;
        
        System.out.println("SampleCloudAdapterConnectionPage constructor");
    }
    
    @Override
    public LinkedHashMap<String, UIError[]> validatePage(LinkedHashMap<String, ICloudAdapterPage> wizardPages, LinkedList<EditField> currentPageFields)
      {
        System.out.println("SampleCloudAdapterConnectionPage validatePage");
        LinkedHashMap<String, UIError[]> errorMap = new LinkedHashMap();
        if (currentPageFields.size() != getPageEditFields().size()) {
          Map<String, UIObject> objectMap = EditField.getObjectMap((EditField[])currentPageFields.toArray(new EditField[currentPageFields.size()]));
          ArrayList<UIError> errors = new ArrayList();
          Locale locale = CloudAdapterUtils.getLocale(this.adapterPluginContext);
          
          for (EditField editField : currentPageFields) {
            if ((editField.getObject().getType() == 3) && (objectMap.containsKey(editField.getName() + "confirm_"))) {
              IPasswordObject passwordObject = (IPasswordObject)objectMap.get(editField.getName() + "confirm_");
              if (!passwordObject.getValue().equals(((IPasswordObject)editField.getObject()).getValue())) {
                System.out.println("SampleCloudAdapterConnectionPage validatePage passwords do not match");
                errors.add(new UIError(editField.getName(), CloudAdapterText.getString(locale, "connection.page.error.passwords.donot.match")));
              }
            }
            if ((editField.getObject().getType() == 0) || (editField.getObject().getType() == 3)) {
              String value = UIFactory.getStringValue(objectMap, editField.getName());
              if (value.length() > 80) {
                System.out.println("SampleCloudAdapterConnectionPage validatePage error value > 80");
                errors.add(new UIError(editField.getName(), CloudAdapterText.getString(locale, "connection.page.error.text.length.exceeds", new Object[] { Integer.valueOf(80) })));
              }
            }
          }
          
          if (errors.size() > 0) {
            errorMap.put(getPageId(), errors.toArray(new UIError[errors.size()]));
          }
        }
        else {
          Map<String, UIObject> map = EditField.getObjectMap((EditField[])currentPageFields.toArray(new EditField[currentPageFields.size()]));
          AbstractCloudConnection connection = getCloudConnection(map);
          PingStatus status = connection.ping();
          boolean connSuccess = false;
          UIError error = null;
          String statusMsg = "";
          if (!status.isSuccess()) {
            System.out.println("SampleCloudAdapterConnectionPage validatePage pingStatus error! "+status.getFailureMessage());
            statusMsg = status.getFailureMessage();
            error = new UIError(null, statusMsg);
            errorMap.put(getPageId(), new UIError[] { error });
          }
          this.adapterPluginContext.setContextObject("CA_UI_connection", connection);
        }
        System.out.println("SampleCloudAdapterConnectionPage validatePage end before return");
        return errorMap;
      }
      
    
    
}
