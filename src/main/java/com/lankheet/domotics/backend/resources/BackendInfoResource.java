package com.lankheet.domotics.backend.resources;

import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.codahale.metrics.annotation.Timed;

@Path("/info")
@Produces(MediaType.APPLICATION_JSON)
public class BackendInfoResource {
    private BackendServiceInfo backEndServiceInfo;

    public BackendInfoResource(BackendServiceInfo backEndServiceInfo) {
        this.backEndServiceInfo = backEndServiceInfo;
    }

    /**
     * Returns application and version info of the BackEnd service.
     * 
     * @return BackendService info; version and description
     * @throws IOException Manifest of this JAR could not be read
     */
    @GET
    @Timed
    public BackendServiceInfo webServiceInfo() throws IOException {
        return backEndServiceInfo;
    }
}
