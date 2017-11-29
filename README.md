# lnb-domotics-backend
LNB domotics message server. Serving measurements from the local domotics servers via message brokers.
## Starting the service
<code>
  java -jar lnb-domotics-backend-<version>.jar server application.yml
</code>
  
## It has the following features:
* Receive measurements from the local domotics message broker
* Store these measurements in a database (one database per site), only if it is a new measurement. We don't need duplicate values.
* Request measurements via REST interface.
* Reconnecting when database or mqtt connection are gone.

## It lacks the following features:
* resources for requesting measurements more intelligently
* Security (ahum); login and reading permissions
* resources for adding homes, rooms, sensors, users and credentials
* Accepting arrays of measurements, now it accepts one measurement per mqtt call
* Erasing hunger and poverty from the planet's surface

## Technical specs
* Accepts JSON measurements from a message broker, no aggregation, yet!
* Uses JPA / Hibernate to store in MySQL. But it can also work with any other database as long as you specify the details in the yaml configuration file AND you put the driver JAR on the classpath.
* Uses dropwizard as framework
* Uses eclipse paho as mqtt client

# How to setup the domotics eco-system:
You could do this several ways. I installed mosquitto on 
1. Install MQTT message broker on a local or remote system. In my case I installed Mosquitto on the local (VPS) system and one on the Raspberry Pi at home. These two are bridged together.
1. Configure the central Mosquitto as follows: In /etc/mosquitto/conf.d/default.conf:
<code>
  allow_anonymous false
  password_file /etc/mosquitto/passwd
  
  listener 1883 localhost
  
  listener 8883
  certfile /etc/letsencrypt/live/mqtt.example.com/cert.pem
  cafile /etc/letsencrypt/live/mqtt.example.com/chain.pem
  keyfile /etc/letsencrypt/live/mqtt.example.com/privkey.pem  
</code>

Reference: https://www.digitalocean.com/community/tutorials/how-to-install-and-secure-the-mosquitto-mqtt-messaging-broker-on-ubuntu-16-04
Configuring the mosquitto (on site):
<code>
  connection mqtt_example_com
  address mqtt.example.com:8883
  topic test both 0 "" ""
  remote_username your_user
  remote_password your_passwd
  bridge_cafile /etc/pki/ca-trust/extracted/pem/tls-ca-bundle.pem
</code>
1. Create a database with a user that has full permissions on the database.
1. Configure the user credentials in the application.yml configuration file.
1. You are ready to start the service
Please review the settings in the application.yml configuration file for logging, mqtt and database settings.

# What's next?
So now you have a micro service that handles your home's sensor data, how about that part? 
See the power-meter repo: https://github.com/yeronimuz/PowerMeter
