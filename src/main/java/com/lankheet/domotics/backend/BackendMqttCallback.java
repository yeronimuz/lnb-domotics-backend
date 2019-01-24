package com.lankheet.domotics.backend;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lankheet.domotics.backend.dao.DaoListener;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.iot.datatypes.entities.Measurement;
import com.lankheet.iot.datatypes.entities.MeasurementType;
import com.lankheet.iot.datatypes.entities.Sensor;
import com.lankheet.iot.datatypes.entities.SensorType;
import com.lankheet.utils.JsonUtil;

public class BackendMqttCallback implements MqttCallback {
    private static final Logger LOG = LoggerFactory.getLogger(BackendMqttCallback.class);
    private MqttClientManager mqttClientManager;
    private DaoListener daoListener;

    public BackendMqttCallback(MqttClientManager mqttClientManager, DaoListener dao) {
        this.mqttClientManager = mqttClientManager;
        this.daoListener = dao;
    }

    @Override
    public void connectionLost(Throwable cause) {
        LOG.warn("Connection lost: {}", cause.getMessage());
        try {
            MqttClient mqttClient = mqttClientManager.getClient();
            while (!mqttClient.isConnected()) {
                // TODO: This loop may possibly run for almost ever
                Thread.sleep(1000);
                mqttClient.reconnect();
            }
            LOG.info("Reconnected: {}", mqttClient.isConnected());
            
        } catch (MqttException e) {
            LOG.error("Could not reconnect: {}", e.getMessage());
        } catch (InterruptedException e) {
            // NOP
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        LOG.info("Topic: {}, message: {}", topic, message.toString());
        String payload = new String(message.getPayload());
        LOG.info("Payload: {}", payload);
        SensorValue sensorValue = null;
        try {
            sensorValue = JsonUtil.fromJson(payload);
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage());
            return;
        }
        Measurement newMmeasurement = new Measurement(
                new Sensor(SensorType.getType(sensorValue.getSensorNode().getSensorType()),
                        sensorValue.getSensorNode().getSensorMac(), null, null),
                sensorValue.getTimeStamp(), MeasurementType.getType(sensorValue.getMeasurementType()),
                sensorValue.getValue());

        LOG.info("Starting store action: " + newMmeasurement);
        daoListener.saveNewMeasurement(newMmeasurement);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        LOG.info("Delivery complete: {}", token);
    }
}
