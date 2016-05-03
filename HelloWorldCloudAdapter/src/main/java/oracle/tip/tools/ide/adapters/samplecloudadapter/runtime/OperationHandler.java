/**
 * OperationHandler defines the methods to handle inbound request, response and fault messages.
 * Implementation class will have the actual logic to handle request, response and fault based on operation.
 */

package oracle.tip.tools.ide.adapters.samplecloudadapter.runtime;

import oracle.cloud.connector.api.CloudInvocationContext;
import oracle.cloud.connector.api.CloudInvocationException;

import org.w3c.dom.Document;

public interface OperationHandler {
    /**
     * @param context
     * @param requestDocument
     * @param version
     */
    void handleOperationRequest(CloudInvocationContext context, Document requestDocument, String version ) throws CloudInvocationException;
    
    /**
     * @param context
     * @param responseDocument
     * @param version
     * @throws CloudInvocationException
     */
    void handleOperationResponse(CloudInvocationContext context, Document responseDocument, String version) throws CloudInvocationException;
    
    /**
     * @param context
     * @param errorDocument
     * @param version
     * @throws CloudInvocationException
     */
    void handleOperationError(CloudInvocationContext context, Document errorDocument, String version) throws CloudInvocationException;
}
