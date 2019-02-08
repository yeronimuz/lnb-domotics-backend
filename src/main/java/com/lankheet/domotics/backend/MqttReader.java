package com.lankheet.domotics.backend;

import java.util.concurrent.BlockingQueue;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lankheet.domotics.backend.config.MqttConfig;
import com.lankheet.iot.datatypes.entities.Measurement;

/**
 * MQTT client manager that subscribes to the domotics topics.
 */
public class MqttReader implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(MqttReader.class);

    private volatile boolean isRunFlag = true;

    private static final int MQTT_CONNECT_RETRY_TIMEOUT = 500;

    private MqttClient client;
    private final MqttConnectOptions options = new MqttConnectOptions();

    public MqttReader(MqttConfig mqttConfig, BlockingQueue<Measurement> queue) throws MqttException {
        String userName = mqttConfig.getUserName();
        String password = mqttConfig.getPassword();
        client = new MqttClient(mqttConfig.getUrl(), MqttClient.generateClientId());

        client.setCallback(new BackendMqttCallback(this, queue));
        options.setConnectionTimeout(60);
        options.setKeepAliveInterval(60);
        options.setUserName(userName);
        options.setPassword(password.toCharArray());
    }

    /**
     * TODO: This method should be triggered by the database command table change in
     * order to configure clients.<BR>
     * Possible commands: Command to switch something, Config to set a (set of)
     * parameter(s), Measure: Invoke a sensor to generate a new measurement
     * 
     * @param topic The command topic {CMD, CONFIG, MEASURE}
     * @throws MqttPersistenceException
     * @throws MqttException
     */
    public void publish(String topic, MqttMessage message) throws MqttPersistenceException, MqttException {
        client.publish(topic, message);
    }

    public void stop() throws Exception {
        if (client.isConnected()) {
            LOG.info("Closing mqtt connection...");
            client.disconnect();
        }
    }

    public MqttClient getClient() {
        return client;
    }

    @Override
    public void run() {
        LOG.info("Connecting mqtt broker with options: {}", options);

        do {
            try {
                client.connect(options);
            } catch (MqttException | SecurityException ex) {
                LOG.error("Unable to connect to Mqtt broker!");
                try {
                    Thread.sleep(MQTT_CONNECT_RETRY_TIMEOUT);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } while (!client.isConnected());
        LOG.info("Mqtt client connected: " + client.getClientId());
        try {
            client.subscribe("#", 0);
        } catch (MqttException ex) {
            LOG.error("Could not subscribe: {}", ex);
        }
        LOG.warn("End of the universe!");
    }

    public boolean isRunning() {
        return isRunFlag;
    }

    public void setRunFlag(boolean isRunFlag) {
        this.isRunFlag = isRunFlag;
    }
}
