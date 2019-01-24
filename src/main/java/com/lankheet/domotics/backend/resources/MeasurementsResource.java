package com.lankheet.domotics.backend.resources;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.codahale.metrics.annotation.Timed;
import com.lankheet.domotics.backend.DatabaseManager;
import com.lankheet.domotics.backend.dao.DaoListener;
import com.lankheet.iot.datatypes.entities.Measurement;

@Path("/measurements")
@Produces(MediaType.APPLICATION_JSON)
public class MeasurementsResource {

    private DaoListener dao;
    
    public MeasurementsResource(DaoListener dao) {
        this.dao = dao;
    }
    
    @GET
    @Timed
    @Path("{sensorId}")
    public List<Measurement> getMeasurements(@PathParam("sensorId") int sensorId) {
        return dao.getMeasurementsBySensor(sensorId);
    }
}
