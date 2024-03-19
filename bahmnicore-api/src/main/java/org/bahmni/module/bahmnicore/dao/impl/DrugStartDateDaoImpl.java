package org.bahmni.module.bahmnicore.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bahmni.module.bahmnicore.dao.DrugStartDateDao;
import org.bahmni.module.bahmnicore.model.DrugStartDate;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.Session;

import org.openmrs.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import org.hibernate.Query;
import java.util.List;

@Repository
public class DrugStartDateDaoImpl implements DrugStartDateDao {
	
	protected static final Log log = LogFactory.getLog(DrugStartDateDaoImpl.class);
	
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
	public DrugStartDate saveOrUpdate(DrugStartDate drugStartDate) {
		sessionFactory.getCurrentSession().saveOrUpdate(drugStartDate);
		return drugStartDate;
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
	public DrugStartDate getStartDateByOrderUUID(String uuid) {
		String queryString = "select D from DrugStartDate D where D.orderUuid = (:uuid)";
		Query queryToGetOrderId = sessionFactory.getCurrentSession().createQuery(queryString);
		queryToGetOrderId.setString("uuid", uuid);
		queryToGetOrderId.setMaxResults(1);
		return (DrugStartDate) queryToGetOrderId.uniqueResult();
	}

	@Override
	@Transactional
	public DrugStartDate updateDrugStartDate(DrugStartDate drugStartDate) {
		Session session = sessionFactory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			session.update(drugStartDate);

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
        return drugStartDate;
    }
}



