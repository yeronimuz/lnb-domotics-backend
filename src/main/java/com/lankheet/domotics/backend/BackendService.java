/**
 * MIT License
 * 
 * Copyright (c) 2017 Lankheet Software and System Solutions
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.lankheet.domotics.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lankheet.domotics.backend.config.BackendServiceConfig;
import com.lankheet.domotics.backend.health.DatabaseHealthCheck;
import com.lankheet.domotics.backend.health.MqttConnectionHealthCheck;
import com.lankheet.domotics.backend.resources.BackendInfoResource;
import com.lankheet.domotics.backend.resources.BackendServiceInfo;
import com.lankheet.domotics.backend.resources.MeasurementsResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * The web service has the following tasks:<BR>
 * <li>It accepts measurements from the message broker
 * <li>It saves the measurements in the database
 * <li>It serves as a resource for the measurements database
 *
 */
public class BackendService extends Application<BackendServiceConfig> {
	private static final Logger LOG = LoggerFactory.getLogger(BackendService.class);

    private BackendServiceConfig configuration;

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			LOG.error("Missing or wrong arguments");
		} else {
			new BackendService().run(args[0], args[1]);
		}
	}

	@Override
	public void initialize(Bootstrap<BackendServiceConfig> bootstrap) {
		LOG.info("Lankheet LNB IOT web service", "");
	}

	@Override
	public void run(BackendServiceConfig configuration, Environment environment) throws Exception {
	    this.setConfiguration(configuration);
        DatabaseManager dbManager = new DatabaseManager(configuration.getDatabaseConfig());
        MqttClientManager mqttClientManager = new MqttClientManager(configuration.getMqttConfig(), dbManager);
        BackendInfoResource webServiceInfoResource = new BackendInfoResource(new BackendServiceInfo());
        MeasurementsResource measurementsResource = new MeasurementsResource(dbManager);
        environment.getApplicationContext().setContextPath("/api");
        environment.lifecycle().manage(mqttClientManager);
        environment.lifecycle().manage(dbManager);
        environment.jersey().register(webServiceInfoResource);
        environment.jersey().register(measurementsResource);

        environment.healthChecks().register("database", new DatabaseHealthCheck(dbManager));
        environment.healthChecks().register("mqtt-server", new MqttConnectionHealthCheck(mqttClientManager));
	}

    public BackendServiceConfig getConfiguration() {
        return configuration;
    }

    public void setConfiguration(BackendServiceConfig configuration) {
        this.configuration = configuration;
    }
}
