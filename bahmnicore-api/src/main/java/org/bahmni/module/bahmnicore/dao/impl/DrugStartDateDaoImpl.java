package org.bahmni.module.bahmnicore.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bahmni.module.bahmnicore.dao.DrugStartDateDao;
import org.bahmni.module.bahmnicore.model.DrugStartDate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
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
		log.error("222222222222222222222222222222");
		sessionFactory.getCurrentSession().saveOrUpdate(drugStartDate);
		return drugStartDate;
	}
	
	//	@Transactional
	//	public DrugStartDate getStartDateById(int id) {
	//		Query query = sessionFactory.getCurrentSession().createQuery(
	//		    "select start_date from drug_start_date where order_id=:id");
	//		query.setInteger("id", 10);
	//		List list = query.list();
	//		if (list.size() != 0) {
	//			DrugStartDate drugStartDate = new DrugStartDate();
	//			Object[] results = (Object[]) list.get(0);
	//			log.error(results[0]);
	//			log.error(results[1]);
	//			return drugStartDate;
	//		}
	//		return null;
	//	}
	
}
