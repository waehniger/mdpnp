package org.mdpnp.apps.testapp.hl7;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.NumericInstanceModel;
import org.springframework.context.ApplicationContext;

import ca.uhn.fhir.context.FhirContext;

import com.rti.dds.subscription.Subscriber;

public class HL7ApplicationFactory implements IceApplicationProvider {
    private final IceApplicationProvider.AppType HL7Application =
            new IceApplicationProvider.AppType("HL7 Exporter", "NOHL7", (URL)HL7ApplicationFactory.class.getResource("hl7.png"), 0.75);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return HL7Application;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {

        final Subscriber subscriber = parentContext.getBean("subscriber", Subscriber.class);

        final EventLoop eventLoop = parentContext.getBean("eventLoop", EventLoop.class);

        final NumericFxList numericInstanceModel = parentContext.getBean("numericList", NumericFxList.class);
        
        final FhirContext fhirContext = parentContext.getBean("fhirContext", FhirContext.class);
        
        final HL7Emitter emitter = new HL7Emitter(subscriber, eventLoop, numericInstanceModel, fhirContext);

        FXMLLoader loader = new FXMLLoader(HL7Application.class.getResource("HL7Application.fxml"));
        
        final Parent ui = loader.load();
        
        final HL7Application controller = ((HL7Application)loader.getController());

        controller.setModel(emitter);
        

        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return HL7Application;
            }

            @Override
            public Parent getUI() {
                return ui;
            }

            @Override
            public void activate(ApplicationContext context) {
            }

            @Override
            public void stop() {
                emitter.stop();
            }

            @Override
            public void destroy() throws Exception {
                controller.shutdown();
            }
        };
    }

}
