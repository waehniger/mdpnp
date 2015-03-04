package org.mdpnp.devices.simulation.ecg;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class SimulatedElectroCardioGramTest {

    private static final Logger log = LoggerFactory.getLogger(SimulatedElectroCardioGramTest.class);

    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    @Test
    public void testPublishScheduler5() throws Exception {
        testPublishScheduler(5);
    }

    @Test
    public void testPublishScheduler15() throws Exception {
        testPublishScheduler(15);
    }

    public void testPublishScheduler(int msPerSample) throws Exception {

        final CountDownLatch stopOk = new CountDownLatch(10);

        SimulatedElectroCardioGram srv = new SimulatedElectroCardioGram(1000L, msPerSample, SimulatedElectroCardioGram.TimestampType.metronome, 0) {

            @Override
            protected void receiveECG(long timestamp, Number[] i, Number[] ii, Number[] iii, double heartRate, double respiratoryRate, int frequency) {

                log.info(dateFormat.format(new Date(timestamp)) + " data size=" + i.length + " heartRate=" + heartRate + " respiratoryRate=" + respiratoryRate + " frequency=" + frequency);
                stopOk.countDown();
            }
        };

        ScheduledExecutorService ses =Executors.newSingleThreadScheduledExecutor();
        srv.connect(ses);

        stopOk.await();
        srv.disconnect();

    }

    @Test
    public void testPublishSchedulerWithDrift() throws Exception {

        final CountDownLatch stopOk = new CountDownLatch(10);

        SimulatedElectroCardioGram srv = new SimulatedElectroCardioGram(1000L, 5, SimulatedElectroCardioGram.TimestampType.realtime, 10) {

            @Override
            protected void receiveECG(long timestamp, Number[] i, Number[] ii, Number[] iii, double heartRate, double respiratoryRate, int frequency) {

                log.info(dateFormat.format(new Date(timestamp)) + " data size=" + i.length + " heartRate=" + heartRate + " respiratoryRate=" + respiratoryRate + " frequency=" + frequency);
                stopOk.countDown();
            }
        };

        ScheduledExecutorService ses =Executors.newSingleThreadScheduledExecutor();
        srv.connect(ses);

        stopOk.await();
        srv.disconnect();

    }

}
