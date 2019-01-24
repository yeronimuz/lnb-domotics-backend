package com.lankheet.domotics.backend.dao;

import java.util.List;
import com.lankheet.iot.datatypes.entities.Measurement;

public interface DaoListener {

    /**
     * A new measurement has arrived and will be stored only if it is a new measurement.
     * 
     * @param measurement The measurement that has arrived.
     */
    void saveNewMeasurement(Measurement measurement);

    /**
     * A client requests all measurements by sensor from the database
     * 
     * @param sensorId The sensorId of which the measurements to get
     * 
     * @return The measurements from the database.
     */
    List<Measurement> getMeasurementsBySensor(int sensorId);

    /**
     * Request all measurements of a specific type for a specific sensor.
     * 
     * @param sensorId The originated sensor
     * @param type The message type
     * @return The measurements that comply to the input criteria
     */
    List<Measurement> getMeasurementsBySensorAndType(int sensorId, int type);

}
