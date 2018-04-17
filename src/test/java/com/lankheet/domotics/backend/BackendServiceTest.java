/**
 * 
 */
package com.lankheet.domotics.backend;

import static org.junit.Assert.assertTrue;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.lankheet.domotics.backend.config.DatabaseConfig;
import com.lankheet.domotics.testutils.TestUtils;
import com.lankheet.iot.datatypes.Measurement;
import com.lankheet.iot.datatypes.MeasurementType;
import com.lankheet.iot.datatypes.Sensor;
import com.lankheet.iot.datatypes.SensorType;

/**
 * Test for @link {@link BackendService}
 * A database is required
 * An mqtt server is required
 */
public class BackendServiceTest {
    private static final String PERSISTENCE_UNIT = "meas-pu";

    private static BackendService beService = new BackendService();
    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static MqttClient mqttClient;

    @BeforeClass
    public static void doSetup() throws Exception {
        // Start service
        beService.run("server", "src/test/resources/application.yml");

        DatabaseConfig dbConfig = beService.getConfiguration().getDatabaseConfig();
        // Overwrite production persistence unit settings with test values
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("javax.persistence.jdbc.driver", dbConfig.getDriver());
        properties.put("javax.persistence.jdbc.url", dbConfig.getUrl());
        properties.put("javax.persistence.jdbc.user", dbConfig.getUserName());
        properties.put("javax.persistence.jdbc.password", dbConfig.getPassword());
        
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, properties);
        em = emf.createEntityManager();
        // Prepare database
        Sensor sensor1 = new Sensor(SensorType.POWER_METER.getId(), "power-meter", "meterkast");
        em.getTransaction().begin();
        em.persist(sensor1);
        em.getTransaction().commit();
        mqttClient = TestUtils.createMqttClientConnection();
    }

    // @Test (disabled; it's a system test, not a unit test)
    public void testEndToEndTest() throws Exception {
        Measurement measurement = new Measurement(1, new Date(), MeasurementType.ACTUAL_CONSUMED_POWER.getId(), 1.1);
        TestUtils.sendMqttMeasurement(mqttClient, measurement);
        assertTrue(mqttClient.isConnected());
        // Give the service some time to finish
        Thread.sleep(2000);
    }
}
