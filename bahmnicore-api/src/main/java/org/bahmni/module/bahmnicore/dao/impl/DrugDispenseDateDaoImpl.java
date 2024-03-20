package org.bahmni.module.bahmnicore.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bahmni.module.bahmnicore.dao.DrugDispenseDateDao;
import org.bahmni.module.bahmnicore.model.DrugDispenseDate;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.openmrs.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import org.hibernate.Query;

@Repository
public class DrugDispenseDateDaoImpl implements DrugDispenseDateDao {
	
	protected static final Log log = LogFactory.getLog(DrugDispenseDateDaoImpl.class);
	
	/**
	 * Hibernate session factory
	 */
	
	private SessionFactory sessionFactory;
	
	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	@Transactional
	public DrugDispenseDate saveOrUpdate(DrugDispenseDate drugDispenseDate) {
		sessionFactory.getCurrentSession().saveOrUpdate(drugDispenseDate);
		log.info("Added Dispense Date ");
		return drugDispenseDate;
	}

	@Override
	@Transactional
	public Order getOrderIDByUuid(String uuid) {
		String queryString = "select o from Order o where o.uuid = (:uuid)";
		Query queryToGetOrderId = sessionFactory.getCurrentSession().createQuery(queryString);
		queryToGetOrderId.setString("uuid", uuid);
		queryToGetOrderId.setMaxResults(1);
		return (Order) queryToGetOrderId.uniqueResult();
	}
	@Override
	@Transactional
	public DrugDispenseDate getDispenseDateByOrderUUID(String uuid) {
		String queryString = "select D from DrugDispenseDate D where D.orderUuid = (:uuid)";
		Query queryToGetOrderId = sessionFactory.getCurrentSession().createQuery(queryString);
		queryToGetOrderId.setString("uuid", uuid);
		queryToGetOrderId.setMaxResults(1);
		return (DrugDispenseDate) queryToGetOrderId.uniqueResult();
	}

	@Override
	@Transactional
	public DrugDispenseDate updateDrugDispenseDate(DrugDispenseDate drugDispenseDate) {
		Session session = sessionFactory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			session.update(drugDispenseDate);

			// Commit the transaction
			tx.commit();
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
        return drugDispenseDate;
    }
}
