package pl.edu.agh;

import io.fd.vpp.jvpp.JVpp;
import io.fd.vpp.jvpp.JVppRegistry;
import io.fd.vpp.jvpp.JVppRegistryImpl;
import io.fd.vpp.jvpp.VppCallbackException;
import io.fd.vpp.jvpp.callback.ControlPingCallback;
import io.fd.vpp.jvpp.core.JVppCoreImpl;
import io.fd.vpp.jvpp.dto.ControlPing;
import io.fd.vpp.jvpp.dto.ControlPingReply;

public class ControlPingTest {

    private static void testControlPing() throws Exception {
        System.out.println("Testing ControlPing using Java callback API");
        try (JVppRegistry registry = new JVppRegistryImpl("ControlPingTest");
             JVpp jvpp = new JVppCoreImpl()) {

            registry.register(jvpp, new ControlPingCallback() {
                @Override
                public void onControlPingReply(final ControlPingReply reply) {
                    System.out.printf("Received ControlPingReply: %s%n", reply);
                }

                @Override
                public void onError(VppCallbackException ex) {
                    System.out.printf("Received onError exception: call=%s, reply=%d, context=%d ", ex.getMethodName(),
                            ex.getErrorCode(), ex.getCtxId());
                }

            });
            System.out.println("Successfully connected to VPP");
            Thread.sleep(1000);

            System.out.println("Sending control ping using JVppRegistry");
            registry.controlPing(jvpp.getClass());

            Thread.sleep(2000);

            System.out.println("Sending control ping using JVpp plugin");
            jvpp.send(new ControlPing());

            Thread.sleep(2000);
            System.out.println("Disconnecting...");
        }
        Thread.sleep(1000);
    }

    public static void main(String[] args) throws Exception {
        testControlPing();
    }
}