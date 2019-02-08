package com.lankheet.domotics.backend.dao;

import com.lankheet.iot.datatypes.entities.Measurement;

public interface IBackendDao {

    /**
     * A new measurement has arrived and will be stored.
     * 
     * @param measurement The measurement that has arrived.
     */
    void saveNewMeasurement(Measurement measurement);

}
