package com.lankheet.domotics;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.lankheet.domotics.config.DatabaseConfig;
import com.lankheet.iot.datatypes.Measurement;
import io.dropwizard.lifecycle.Managed;

public class DatabaseManager implements Managed, DaoListener {
    private static final Logger LOG = LogManager.getLogger(DatabaseManager.class);

	private static final String PERSISTENCE_UNIT = "meas-pu";
	private DatabaseConfig dbConfig;
	private EntityManagerFactory emf;
	private EntityManager em;

	public DatabaseManager(DatabaseConfig dbConfig) {
		this.dbConfig = dbConfig;
	}

	@Override
	public void start() throws Exception {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("javax.persistence.jdbc.driver", dbConfig.getDriver());
		properties.put("javax.persistence.jdbc.url", dbConfig.getUrl());
		properties.put("javax.persistence.jdbc.user", dbConfig.getUserName());
		properties.put("javax.persistence.jdbc.password", dbConfig.getPassword());
		
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, properties);
		em = emf.createEntityManager();
	}

	@Override
	public void stop() throws Exception {
		em.close();
		emf.close();
	}

	@Override
	public void newMeasurement(Measurement measurement) {
	    LOG.info("Storing: " + measurement.toString() );
		em.getTransaction().begin();
		em.persist(measurement);
		em.getTransaction().commit();
	}
	
	protected EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}
}
