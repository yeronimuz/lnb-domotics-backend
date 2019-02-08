package com.lankheet.domotics.backend.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BackendServiceConfig {

    @JsonProperty
    private int internalQueueSize;

    @JsonProperty
    private MqttConfig mqttConfig = new MqttConfig();

    @JsonProperty
    private DatabaseConfig databaseConfig = new DatabaseConfig();

    public int getInternalQueueSize() {
        return internalQueueSize;
    }

    public void setInternalQueueSize(int internalQueueSize) {
        this.internalQueueSize = internalQueueSize;
    }

    public MqttConfig getMqttConfig() {
        return mqttConfig;
    }

    public void setMqttConfig(MqttConfig mqttConfig) {
        this.mqttConfig = mqttConfig;
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public void setDatabaseConfig(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public static BackendServiceConfig loadConfigurationFromFile(String configFileName) throws IOException {
        Constructor constructor = new Constructor(BackendServiceConfig.class);
        TypeDescription backendServiceConfigTypeDescription = new TypeDescription(BackendServiceConfig.class);
        backendServiceConfigTypeDescription.addPropertyParameters("internalQueueSize", BackendServiceConfig.class);
        backendServiceConfigTypeDescription.addPropertyParameters("mqttConfig", MqttConfig.class);
        backendServiceConfigTypeDescription.addPropertyParameters("datababseConfig", DatabaseConfig.class);

        TypeDescription mqttConfigTypeDescription = new TypeDescription(MqttConfig.class);
        mqttConfigTypeDescription.addPropertyParameters("url", String.class);
        mqttConfigTypeDescription.addPropertyParameters("userName", String.class);
        mqttConfigTypeDescription.addPropertyParameters("password", String.class);
        mqttConfigTypeDescription.addPropertyParameters("clientName", String.class);

        TypeDescription databaseConfigTypeDescription = new TypeDescription(DatabaseConfig.class);
        databaseConfigTypeDescription.addPropertyParameters("internalQueueSize", DatabaseConfig.class);

        constructor.addTypeDescription(backendServiceConfigTypeDescription);
        constructor.addTypeDescription(mqttConfigTypeDescription);
//        constructor.addTypeDescription(mqttTopicConfigTypeDescription);

        Yaml yaml = new Yaml(constructor);
        InputStream inputStream = Files.newInputStream(Paths.get(configFileName));

        BackendServiceConfig backendServiceConfig = (BackendServiceConfig) yaml.load(inputStream);
        return backendServiceConfig;

    }
}
