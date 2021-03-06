package pl.edu.agh;


import io.fd.vpp.jvpp.JVppRegistry;
import io.fd.vpp.jvpp.JVppRegistryImpl;
import io.fd.vpp.jvpp.VppCallbackException;
import io.fd.vpp.jvpp.core.JVppCoreImpl;
import io.fd.vpp.jvpp.core.callback.GetNodeIndexCallback;
import io.fd.vpp.jvpp.core.callback.ShowVersionCallback;
import io.fd.vpp.jvpp.core.callfacade.CallbackJVppCoreFacade;
import io.fd.vpp.jvpp.core.dto.GetNodeIndex;
import io.fd.vpp.jvpp.core.dto.GetNodeIndexReply;
import io.fd.vpp.jvpp.core.dto.ShowVersionReply;

import java.nio.charset.StandardCharsets;

/**
 * CallbackJVppFacade together with CallbackJVppFacadeCallback allow for setting different callback for each request.
 * This is more convenient than the approach shown in CallbackApiTest.
 */
public class CallbackJVppFacadeTest {

    private static ShowVersionCallback showVersionCallback1 = new ShowVersionCallback() {
        @Override
        public void onShowVersionReply(final ShowVersionReply msg) {
            System.out.printf("ShowVersionCallback1 received ShowVersionReply: context=%d, program=%s,"
                            + "version=%s, buildDate=%s, buildDirectory=%s%n", msg.context,
                    new String(msg.program, StandardCharsets.UTF_8),
                    new String(msg.version, StandardCharsets.UTF_8),
                    new String(msg.buildDate, StandardCharsets.UTF_8),
                    new String(msg.buildDirectory, StandardCharsets.UTF_8));
        }

        @Override
        public void onError(VppCallbackException ex) {
            System.out.printf("Received onError exception in showVersionCallback1: call=%s, reply=%d, context=%d%n",
                    ex.getMethodName(), ex.getErrorCode(), ex.getCtxId());
        }
    };

    private static ShowVersionCallback showVersionCallback2 = new ShowVersionCallback() {
        @Override
        public void onShowVersionReply(final ShowVersionReply msg) {
            System.out.printf("ShowVersionCallback2 received ShowVersionReply: context=%d, program=%s,"
                            + "version=%s, buildDate=%s, buildDirectory=%s%n", msg.context,
                    new String(msg.program, StandardCharsets.UTF_8),
                    new String(msg.version, StandardCharsets.UTF_8),
                    new String(msg.buildDate, StandardCharsets.UTF_8),
                    new String(msg.buildDirectory, StandardCharsets.UTF_8));
        }

        @Override
        public void onError(VppCallbackException ex) {
            System.out.printf("Received onError exception in showVersionCallback2: call=%s, reply=%d, context=%d%n",
                    ex.getMethodName(), ex.getErrorCode(), ex.getCtxId());
        }

    };

    private static GetNodeIndexCallback getNodeIndexCallback = new GetNodeIndexCallback() {
        @Override
        public void onGetNodeIndexReply(final GetNodeIndexReply msg) {
            System.out.printf("Received GetNodeIndexReply: %s%n", msg);
        }

        @Override
        public void onError(VppCallbackException ex) {
            System.out.printf("Received onError exception in getNodeIndexCallback: call=%s, reply=%d, context=%d%n",
                    ex.getMethodName(), ex.getErrorCode(), ex.getCtxId());
        }
    };

    private static void testCallbackFacade() throws Exception {
        System.out.println("Testing CallbackJVppFacade");

        try (final JVppRegistry registry = new JVppRegistryImpl("CallbackFacadeTest");
             final CallbackJVppCoreFacade callbackFacade = new CallbackJVppCoreFacade(registry, new JVppCoreImpl())) {
            System.out.println("Successfully connected to VPP");

            callbackFacade.showVersion(showVersionCallback1);
            callbackFacade.showVersion(showVersionCallback2);

            GetNodeIndex getNodeIndexRequest = new GetNodeIndex();
            getNodeIndexRequest.nodeName = "dummyNode".getBytes(StandardCharsets.UTF_8);
            callbackFacade.getNodeIndex(getNodeIndexRequest, getNodeIndexCallback);

            Thread.sleep(2000);
            System.out.println("Disconnecting...");
        }
        Thread.sleep(1000);
    }

    public static void main(String[] args) throws Exception {
        testCallbackFacade();
    }
}