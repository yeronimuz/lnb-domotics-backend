package com.lankheet.domotics;

import com.lankheet.iot.datatypes.Measurement;

public interface DaoListener {
	
	void newMeasurement(Measurement measurement);

}
