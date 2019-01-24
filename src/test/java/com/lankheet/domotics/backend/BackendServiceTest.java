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
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.lankheet.domotics.backend.config.DatabaseConfig;
import com.lankheet.domotics.testutils.TestUtils;
import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.iot.datatypes.entities.Measurement;
import com.lankheet.iot.datatypes.entities.MeasurementType;
import com.lankheet.iot.datatypes.entities.Sensor;
import com.lankheet.iot.datatypes.entities.SensorType;

/**
 * End-to-end SystemTest for @link {@link BackendService}<BR>
 * <li>A database is required 
 * <li>An mqtt server is required
 */
public class BackendServiceTest {
    private static final String PERSISTENCE_UNIT = "meas-pu";

    private static BackendService beService = new BackendService();
    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static MqttClient mqttClient;
    private static Sensor sensor1 = new Sensor(SensorType.POWER_METER, "AA:BB:CC:DD", "power-meter", "meterkast");

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

        em.getTransaction().begin();
        em.persist(sensor1);
        em.getTransaction().commit();
        mqttClient = TestUtils.createMqttClientConnection();
    }

    @Test
    public void testEndToEndTest() throws Exception {
        SensorNode sensorNode = new SensorNode("AA:BB:CC:DD", SensorType.POWER_METER.getId());
        SensorValue sensorValue =
                new SensorValue(sensorNode, new Date(), MeasurementType.ACTUAL_CONSUMED_POWER.getId(), 1.1);

        TestUtils.sendMqttSensorValue(mqttClient, sensorValue);
        assertTrue(mqttClient.isConnected());
        // Give the service some time to finish
        Thread.sleep(2000);
    }

    @AfterClass
    public static void doCLeanup() throws MqttException {
        mqttClient.disconnect();
        mqttClient.close();
        em.close();
        emf.close();
    }
}
