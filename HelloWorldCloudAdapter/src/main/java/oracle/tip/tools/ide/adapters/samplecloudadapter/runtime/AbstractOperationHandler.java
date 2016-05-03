/**
 * AbstractOperationHandler is a abstract class to handle request, response and fault messages.
 * Basic implementation like normalization (root element like process) can be done in this class. Sub class will implement any additional changes to message payload.
 */

package oracle.tip.tools.ide.adapters.samplecloudadapter.runtime;

import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import oracle.cloud.connector.api.CloudInvocationContext;
import oracle.cloud.connector.api.CloudInvocationException;
import oracle.cloud.connector.api.CloudRuntimeConstants;
import oracle.cloud.connector.api.CloudAdapterLoggingService.Level;
import oracle.cloud.connector.impl.CloudAdapterUtil;
import oracle.tip.tools.ide.adapters.samplecloudadapter.util.SampleCloudAdapterConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractOperationHandler implements OperationHandler {
    
    
    @Override
    public void handleOperationRequest(CloudInvocationContext context,
                    Document requestDocument, String version)
                    throws CloudInvocationException {
        
        context.getLoggingService().logDebug(
                        "AbstractOperationHander#handleOperationRequest() invoked");
        if (context.getLoggingService().isLevel(Level.DEBUG)) {
                context.getLoggingService().logDebug(
                                "Printing request document ====>");
                context.getLoggingService().logDebug(
                                CloudAdapterUtil.getDomAsString(requestDocument, "UTF-8",
                                                true));
                context.getLoggingService().logDebug("<=====");
        }
      
        Element sourceRootElement = normalizeRootElement(context,
                        requestDocument, version);
      
        
        requestDocument.normalizeDocument();
        String opName = sourceRootElement.getLocalName();  
        

        if (context.getLoggingService().isLevel(Level.DEBUG)) {
                context.getLoggingService().logDebug(
                                "Printing transformred request document ====>");
                context.getLoggingService().logDebug(
                                CloudAdapterUtil.getDomAsString(requestDocument, "UTF-8",
                                                true));
                context.getLoggingService().logDebug("<=====");
        }
    }
    
    /**
     * @param context
     * @param requestDocument
     * @param version
     * @return
     */
    protected Element normalizeRootElement(CloudInvocationContext context,
                    Document requestDocument, String version) {
            Definition targetWSDL = (Definition) context
                            .getContextObject(CloudRuntimeConstants.TARGET_WSDL_DEFINITION);
            QName targetRootElementName = new QName(
                            targetWSDL.getTargetNamespace(),
                            context.getTargetOperationName());
            Element sourceRootElement = requestDocument.getDocumentElement();
            
            NodeList nodes = sourceRootElement.getChildNodes();
            Node node = null;
            for (int i = 0; i < nodes.getLength(); i++) {
                    node = nodes.item(i);
                    if (node instanceof Element){
                            break;
                    }
            }
            
            sourceRootElement.getParentNode().replaceChild(node, sourceRootElement);
      
            sourceRootElement.normalize();
            return sourceRootElement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * oracle.cloud.connector.mycloudadapter.OperationHandler#handleOperationResponse
     * (oracle.cloud.connector.api.CloudInvocationContext, org.w3c.dom.Document,
     * java.lang.String)
     */
    @Override
    public void handleOperationResponse(CloudInvocationContext context,
                    Document responseDocument, String version)
                    throws CloudInvocationException {
            
         
            context.getLoggingService().logDebug(
                            "AbstractOperationHander#handleOperationResponse() invoked");
            if (context.getLoggingService().isLevel(Level.DEBUG)) {
                    context.getLoggingService().logDebug(
                                    "Printing response document ====>");
                    context.getLoggingService().logDebug(
                                    CloudAdapterUtil.getDomAsString(responseDocument, "UTF-8",
                                                    true));
                    context.getLoggingService().logDebug("<=====");
            }
            String suppressResponse = (String) context
                            .getCloudOperationProperties().get(
                                            CloudRuntimeConstants.SUPPRESS_RESPONSE);
            boolean useVoid = (suppressResponse != null) ? Boolean
                            .parseBoolean(suppressResponse) : false;
                            
            context.getLoggingService().logDebug(
                            "AbstractOperationHander#handleOperationResponse suppressReponse => "
                                            + useVoid);
            if (useVoid) {
                    createVoidResponse(context, responseDocument, version);
                    //adjustReponse(context, responseDocument, version);
            } else {
                    adjustReponse(context, responseDocument, version);
            }
            if (context.getLoggingService().isLevel(Level.DEBUG)) {
                    context.getLoggingService().logDebug(
                                    "Printing response document ====>");
                    context.getLoggingService().logDebug(
                                    CloudAdapterUtil.getDomAsString(responseDocument, "UTF-8",
                                                    true));
                    context.getLoggingService().logDebug("<=====");
            }

    }
    
    protected void createVoidResponse(CloudInvocationContext context,
                    Document responseDocument, String version) {
            Element element = responseDocument.getDocumentElement();

            Node firstChild = element.getFirstChild();

            if (firstChild != null) {
                    while (firstChild.getNextSibling() != null) {
                            element.removeChild(firstChild.getNextSibling());
                    }
                    element.removeChild(firstChild);
            }
            renameResponseWrapper(context.getIntegrationWSDL(), element,
                            responseDocument);
            //responseDocument.createElementNS(arg0, arg1);
    }

    protected void renameResponseWrapper(Definition integrationWSDL,
                    Element rootElement, Document sourceDoc) {
            Operation operation = getWSDLOperation(integrationWSDL);
            Part part = operation.getOutput().getMessage().getPart("parameters");
            QName newResponseQName = part.getElementName();
            String newResponseName = (newResponseQName.getPrefix() != null) ? newResponseQName
                            .getPrefix() + ":" + newResponseQName.getLocalPart()
                            : newResponseQName.getLocalPart();
            sourceDoc.renameNode(rootElement, newResponseQName.getNamespaceURI(),
                            newResponseName);
            
    }
    
    private Operation getWSDLOperation(Definition integrationWSDL) {
            Map portTypes = integrationWSDL.getPortTypes();
            for (Object key : portTypes.keySet()) {
                    PortType pt = (PortType) portTypes.get(key);
                    return (Operation) pt.getOperations().get(0);
            }
            return null;
    }
    
    /**
     * Adjust the response coming back from MyCooudConnector server
     * 
     * @param context
     * @param responseDocument
     * @param version
     * @throws CloudInvocationException
     */
    protected void adjustReponse(CloudInvocationContext context,
                    Document responseDocument, String version)
                    throws CloudInvocationException {
            
            
            Element rootElement = responseDocument.getDocumentElement();
            
    
            Definition targetWSDL = (Definition) context
                            .getContextObject(CloudRuntimeConstants.TARGET_WSDL_DEFINITION);
            QName targetOperationElementName = new QName(
                            SampleCloudAdapterConstants.SAMPLE_MESSAGES_NAMESPACE, context
                                            .getTargetOperationName());
            
            Element element = responseDocument.getDocumentElement();

            renameResponseWrapper(context.getIntegrationWSDL(), element,
                            responseDocument, targetOperationElementName, context);
            element = responseDocument.getDocumentElement();
            
            String namespace = element.getNamespaceURI();
 
                            
    }
    
    protected void renameResponseWrapper(Definition integrationWSDL,
                    Element rootElement, Document sourceDoc, QName targetOperationElementName, CloudInvocationContext context) {
            Operation operation = getWSDLOperation(integrationWSDL);
            Part part = operation.getOutput().getMessage().getPart("parameters");
            QName newResponseQName = part.getElementName();
            String newResponseName = (newResponseQName.getPrefix() != null) ? newResponseQName
                            .getPrefix() + ":" + newResponseQName.getLocalPart()
                            : newResponseQName.getLocalPart();
            sourceDoc.renameNode(rootElement, newResponseQName.getNamespaceURI(),
                            newResponseName);
            NodeList nodes = sourceDoc.getDocumentElement().getChildNodes();
            Node resultNode = null;
            for (int i = 0; i < nodes.getLength(); i++) {
                    resultNode = nodes.item(i);
                    if(resultNode instanceof Element){
                            break;
                    }
            }
            
            NodeList resultChildNodes = resultNode.getChildNodes();
            Node valueNode = null;
            for (int i = 0; i < resultChildNodes.getLength(); i++) {
                    valueNode = resultChildNodes.item(i);
                    if(resultNode instanceof Element){
                            break;
                    }
            }
            
            
            String prefix = context.getNamespaceManager().getOrCreatePrefix(targetOperationElementName.getNamespaceURI());
            
            sourceDoc.renameNode(valueNode,targetOperationElementName.getNamespaceURI(),prefix +":"+ "account");
            
            sourceDoc.renameNode(resultNode,targetOperationElementName.getNamespaceURI(),prefix +":"+ targetOperationElementName.getLocalPart());
            
            
            
            
    }
    /*
     * (non-Javadoc)
     * 
     * @see
     * oracle.cloud.connector.mycloudadapter.OperationHandler#handleOperationError(oracle
     * .cloud.rt.api.CloudInvocationContext, org.w3c.dom.Document,
     * java.lang.String)
     */
    @Override
    public void handleOperationError(CloudInvocationContext context,
                    Document errorDocument, String version)
                    throws CloudInvocationException {
    }
}
