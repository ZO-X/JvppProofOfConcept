package pl.edu.agh;


import io.fd.vpp.jvpp.JVppRegistry;
import io.fd.vpp.jvpp.JVppRegistryImpl;
import io.fd.vpp.jvpp.VppCallbackException;
import io.fd.vpp.jvpp.core.JVppCore;
import io.fd.vpp.jvpp.core.JVppCoreImpl;
import io.fd.vpp.jvpp.core.callback.WantInterfaceEventsCallback;
import io.fd.vpp.jvpp.core.callfacade.CallbackJVppCoreFacade;
import io.fd.vpp.jvpp.core.dto.WantInterfaceEventsReply;

public class CallbackJVppFacadeNotificationTest {

    private static void testCallbackFacade() throws Exception {
        System.out.println("Testing CallbackJVppFacade for notifications");

        try (final JVppRegistry registry = new JVppRegistryImpl("CallbackFacadeTest");
             final JVppCore jvpp = new JVppCoreImpl()) {
            final CallbackJVppCoreFacade jvppCallbackFacade = new CallbackJVppCoreFacade(registry, jvpp);
            System.out.println("Successfully connected to VPP");

            final AutoCloseable notificationListenerReg =
                    jvppCallbackFacade.getNotificationRegistry().registerSwInterfaceSetFlagsNotificationCallback(
                            NotificationUtils::printNotification
                    );

            jvppCallbackFacade.wantInterfaceEvents(NotificationUtils.getEnableInterfaceNotificationsReq(),
                    new WantInterfaceEventsCallback() {
                        @Override
                        public void onWantInterfaceEventsReply(final WantInterfaceEventsReply reply) {
                            System.out.println("Interface events started");
                        }

                        @Override
                        public void onError(final VppCallbackException ex) {
                            System.out.printf("Received onError exception: call=%s, context=%d, retval=%d%n",
                                    ex.getMethodName(), ex.getCtxId(), ex.getErrorCode());
                        }
                    });

            System.out.println("Changing interface configuration");
            NotificationUtils.getChangeInterfaceState().send(jvpp);

            Thread.sleep(1000);

            jvppCallbackFacade.wantInterfaceEvents(NotificationUtils.getDisableInterfaceNotificationsReq(),
                    new WantInterfaceEventsCallback() {
                        @Override
                        public void onWantInterfaceEventsReply(final WantInterfaceEventsReply reply) {
                            System.out.println("Interface events stopped");
                        }

                        @Override
                        public void onError(final VppCallbackException ex) {
                            System.out.printf("Received onError exception: call=%s, context=%d, retval=%d%n",
                                    ex.getMethodName(), ex.getCtxId(), ex.getErrorCode());
                        }
                    });

            notificationListenerReg.close();

            Thread.sleep(2000);
            System.out.println("Disconnecting...");
        }
        Thread.sleep(1000);
    }

    public static void main(String[] args) throws Exception {
        testCallbackFacade();
    }
}