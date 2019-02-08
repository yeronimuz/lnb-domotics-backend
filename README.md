# lnb-domotics-backend
LNB domotics measurements to database agent.
## Starting the service
<code>
  java -jar lnb-domotics-backend-<version>.jar application.yml
</code>
  
## It has the following features:
* Receive measurements from the local domotics message broker
* Store these measurements in a database.
* Reconnecting when database or mqtt connection are gone.

## It lacks the following features:
* Security (ahum); there is simply no end-point on this piece of software
* Accepting arrays of measurements, now it accepts one measurement per mqtt call
* Signalling that the mqtt port is not accessible. It just polls for the connection to re-appear
* Signalling that the database connection is gone. 
* Signalling that the internal queue is filled up (for 50, 75 and 100%)

## Technical specs
* Accepts JSON measurements from a message broker
* Uses JPA / Hibernate to store in MySQL. But it can also work with any other database as long as you specify the details in the yaml configuration file AND you put the driver JAR on the classpath.
* Plain Java 8 application
* Uses eclipse paho as mqtt client
* Uses an internal blocking queue to transfer data from the mqtt thread to database agent thread. Current capacity is a configurable parameter. When the database is not available, measurments are stored in the queue until it comes available again.
When a queue of 10000 capacity is used, then when 10 sensor values per second are received, 1000 seconds / 3600 is 16 minutes of time is left before data gets lost.

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
1. Give your JRE enough permission to read from your mosquitto mqtt broker. Open /usr/lib/jvm/java-8-oracle/jre/lib/security/java.policy (if this is the location of your current JVM).
1. Add a line: 
<code>
    // allows anyone to listen on dynamic ports
        permission java.net.SocketPermission "localhost:0", "listen";
-->        permission java.net.SocketPermission "locahost:1883", "listen,resolve";
    
</code>
1. In this case, the mqtt broker listens on local host, port 1883
1. Don't forget the semi-colon at the end, or you will not receive any measurements.
1. You are ready to start the service
Please review the settings in the application.yml configuration file for logging, mqtt and database settings.

# What's next?
So now you have a micro service that handles your home's sensor data. Okay, what then?
See the power-meter repo: https://github.com/yeronimuz/PowerMeter for reading measurements from your smart power meter.
and the web-service repo: https://github.com/yeronimuz/lnb-iot-webservice
