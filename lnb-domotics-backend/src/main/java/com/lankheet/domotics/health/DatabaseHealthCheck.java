package com.lankheet.domotics.health;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import com.codahale.metrics.health.HealthCheck;
import com.lankheet.iot.datatypes.Measurement;

public class DatabaseHealthCheck extends HealthCheck {
	private EntityManagerFactory emf;

	public DatabaseHealthCheck(EntityManagerFactory emf) {
		this.emf = emf;
	}

	@Override
	protected Result check() {
		try {
			EntityManager em = emf.createEntityManager();
			Query query = em.createQuery("SELECT m from Measurements m");
			Measurement meas = (Measurement) query.setFirstResult(0).setMaxResults(1).getSingleResult();
		} catch (Exception ex) {
			return Result.unhealthy(ex);
		}
		return Result.healthy("Healthy");
	}

}
