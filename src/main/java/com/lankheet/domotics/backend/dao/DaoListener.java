package com.lankheet.domotics.backend.dao;

import java.util.List;
import com.lankheet.iot.datatypes.Measurement;

public interface DaoListener {
	
    /**
     * A new measurement has arrived and will be stored only if it is a new measurement.
     * 
     * @param measurement The measurement that has arrived.
     */
    void newMeasurement(Measurement measurement);

    /**
     * A client requests all measurements by sensor from the database
     * 
     * @param sensorId The sensorId of which the measurements to get
     * 
     * @return The measurements from the database.
     */
    List<Measurement> getMeasurementsBySensor(int sensorId);
    
    List<Measurement> getMeasurementsBySensorAndType(int sensorId, int type);

}
