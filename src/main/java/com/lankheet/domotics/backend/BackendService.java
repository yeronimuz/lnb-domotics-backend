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

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lankheet.domotics.backend.config.BackendServiceConfig;
import com.lankheet.iot.datatypes.entities.Measurement;

/**
 * The web service has the following tasks:<BR>
 * <li>It accepts measurements from the message broker
 * <li>It saves the measurements in the database
 *
 */
public class BackendService {
	private static final Logger LOG = LogManager.getLogger(BackendService.class);
	
	public static void main(String[] args) throws Exception {
		showBanner();
		InputStream is = BackendService.class.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
		Manifest manifest = new Manifest(is);
		Attributes mainAttrs = manifest.getMainAttributes();
		String title = mainAttrs.getValue("Implementation-Title");
		String version = mainAttrs.getValue("Implementation-Version");
		String classifier = mainAttrs.getValue("Implementation-Classifier");
		if (args.length != 1) {
			LOG.error("Wrong arguments");
			showUsage(version, classifier);
			return;
		} else {
			new BackendService().run(args[0]);
		}
	}

	private static void showBanner() throws URISyntaxException, IOException {
		String text = new Scanner(BackendService.class.getResourceAsStream("/banner.txt"), "UTF-8").useDelimiter("\\A")
				.next();
		System.out.println(text);
	}

	private static void showUsage(String version, String classifier) {
		System.out.println("Missing configuration file!");
		System.out.println("Usage:");
		System.out.println("java -jar lnb-powermeter-" + version + "-" + classifier + " config.yml");
		;
	}

	public void run(String configurationFile) throws Exception {
		BackendServiceConfig config = BackendServiceConfig.loadConfigurationFromFile(configurationFile);
		BlockingQueue<Measurement> queue = new ArrayBlockingQueue<>(config.getInternalQueueSize());

		DatabaseAgent dbManager = new DatabaseAgent(config.getDatabaseConfig(), queue);
		MqttReader mqttReader = new MqttReader(config.getMqttConfig(), queue);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				LOG.warn("Shutdown Hook is running !");
				// TODO: gracefully shutdown threads
				if (mqttReader.isRunning()) {
					mqttReader.setRunFlag(false);
				}
				if (dbManager.isRunning()) {
					dbManager.setRunFlag(false);
				}
			}
		});

		new Thread(dbManager).start();
		new Thread(mqttReader).start();
	}
}
