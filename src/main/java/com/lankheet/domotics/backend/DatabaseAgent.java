package com.lankheet.domotics.backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lankheet.domotics.backend.config.DatabaseConfig;
import com.lankheet.domotics.backend.dao.IBackendDao;
import com.lankheet.iot.datatypes.entities.Measurement;
import com.lankheet.iot.datatypes.entities.Sensor;

/**
 * The database manager saves the received measurements in the data store
 *
 */
public class DatabaseAgent implements IBackendDao, Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseAgent.class);

    private static final String PERSISTENCE_UNIT = "meas-pu";
    private volatile boolean runFlag = true;
    private EntityManagerFactory emf;
    private EntityManager em;
    private BlockingQueue<Measurement> queue;

    public DatabaseAgent(DatabaseConfig dbConfig, BlockingQueue<Measurement> queue) {
        this.queue = queue;
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("javax.persistence.jdbc.driver", dbConfig.getDriver());
        properties.put("javax.persistence.jdbc.url", dbConfig.getUrl());
        properties.put("javax.persistence.jdbc.user", dbConfig.getUserName());
        properties.put("javax.persistence.jdbc.password", dbConfig.getPassword());

        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, properties);
        em = emf.createEntityManager();
    }

    @Override
    public void saveNewMeasurement(Measurement measurement) {
        List<Sensor> sensorList;
        Sensor sensor = measurement.getSensor();
        String query = "SELECT s FROM sensors s WHERE s.macAddress = :mac AND s.sensorType = :type";
        sensorList = em.createQuery(query).setParameter("mac", sensor.getMacAddress())
                .setParameter("type", sensor.getType()).getResultList();
        LOG.debug("Sensors in db: {}", sensorList.size());
        LOG.info("Storing: " + measurement);
        em.getTransaction().begin();
        if (!sensorList.isEmpty()) {
            sensor = sensorList.get(0);
            measurement.setSensor(sensor);
        } else {
            em.persist(sensor);
        }

        // TODO: Set reference when sensor already exists;
        em.persist(measurement);
        em.getTransaction().commit();
    }

    @Override
    public void run() {
        LOG.info("Starting DB agent");
        while (runFlag) {
            Measurement measurement = null;
            try {
                measurement = queue.take();
            } catch (InterruptedException e) {
                // Unexpected termination: Cleanup
                graceFulShutdown();
            }
            LOG.info("Saving measurement: {}", measurement);
            saveNewMeasurement(measurement);
        }
        // Termination requested, cleanup
        graceFulShutdown();
    }

    private void graceFulShutdown() {
        while (!queue.isEmpty()) {
            try {
                saveNewMeasurement(queue.take());
            } catch (InterruptedException e) {
                // Ignore
            }
        }

    }

    public boolean isRunning() {
        return runFlag;
    }

    public void setRunFlag(boolean runFlag) {
        this.runFlag = runFlag;
    }
}
