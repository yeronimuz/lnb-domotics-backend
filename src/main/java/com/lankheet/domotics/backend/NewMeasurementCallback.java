package com.lankheet.domotics.backend;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lankheet.domotics.backend.dao.DaoListener;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.iot.datatypes.entities.Measurement;
import com.lankheet.utils.JsonUtil;

public class NewMeasurementCallback implements MqttCallback {
    private static final Logger LOG = LoggerFactory.getLogger(NewMeasurementCallback.class);
    private MqttClientManager mqttClientManager;
    private DaoListener daoListener;

    public NewMeasurementCallback(MqttClientManager mqttClientManager, DaoListener dao) {
        this.mqttClientManager = mqttClientManager;
        this.daoListener = dao;
    }

    @Override
    public void connectionLost(Throwable cause) {
        LOG.warn("Connection lost: {}", cause.getMessage());
        try {
            mqttClientManager.getClient().reconnect();
        } catch (MqttException e) {
            LOG.error("Could not reconnect: {}", e.getMessage());
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        LOG.info("Topic: {}, message: {}", topic, message.toString());
        String payload = new String(message.getPayload());
        LOG.info("Payload: {}", payload);
        SensorValue sensorValue = null;
        Measurement newMmeasurement = null;
        try {
            sensorValue = JsonUtil.fromJson(payload);
        } catch(JsonProcessingException e ) {
            LOG.error(e.getMessage());
            return;
        }
        LOG.info("Starting store action");
        daoListener.saveNewMeasurement(newMmeasurement);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        LOG.info("Delivery complete: {}", token);
    }
}
