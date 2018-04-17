/**
 * Lankheet Solutions. Do whatever you like, but kindly include the source as a reference.
 */
package com.lankheet.domotics.backend.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import org.junit.BeforeClass;
import org.junit.Test;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

/**
 * Test for @link {@link BackendServiceConfig}
 *
 */
public class BackendServiceConfigTest {
    // Prepare the SUT
    private static class BackendServiceConfigTester extends Application<BackendServiceConfig> {
        private static BackendServiceConfigTester instance = null;
        private BackendServiceConfig wsConfig;

        private BackendServiceConfigTester() {
            // Do not allow instantiation through constructor
        }

        @Override
        public void run(BackendServiceConfig configuration, Environment environment) throws Exception {
            this.wsConfig = configuration;
        }

        public static BackendServiceConfigTester getInstance() {
            if (instance == null) {
                instance = new BackendServiceConfigTester();
            }
            return instance;
        }
    }

    @BeforeClass
    public static void setup() throws Exception {
        BackendServiceConfigTester.getInstance().run("server", "src/test/resources/application.yml");
    }

    @Test
    public void testConfigDatabase() throws Exception {
        BackendServiceConfigTester wsTester = BackendServiceConfigTester.getInstance();
        DatabaseConfig databaseConfig = wsTester.wsConfig.getDatabaseConfig();
        assertThat(databaseConfig, is(notNullValue()));
        assertThat(databaseConfig.getDriver(), is("com.mysql.jdbc.Driver"));
        assertThat(databaseConfig.getUrl(), is("jdbc:mysql://localhost:3306/domotics"));
        assertThat(databaseConfig.getUserName(), is("testuser"));
        assertThat(databaseConfig.getPassword(), is("p@ssW0rd"));
    }

    @Test
    public void testConfigMqtt() {
        BackendServiceConfigTester wsTester = BackendServiceConfigTester.getInstance();

        MqttConfig mqttConfig = wsTester.wsConfig.getMqttConfig();
        assertThat(mqttConfig, is(notNullValue()));
        assertThat(mqttConfig.getUrl(), is("tcp://localhost:1883"));
        assertThat(mqttConfig.getUserName(), is("johndoe"));
        assertThat(mqttConfig.getPassword(), is("secret"));
    }
}
