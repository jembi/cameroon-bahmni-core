package org.bahmni.module.bahmnicore.dao;


import org.bahmni.module.bahmnicore.model.DrugStartDate;
import org.openmrs.Order;

public interface DrugStartDateDao {

	DrugStartDate saveOrUpdate(DrugStartDate obsRelationship);
	Order getOrderIDByUuid(String uuid);
	DrugStartDate getStartDateByOrderUUID(String uuid);
	DrugStartDate updateDrugStartDate(DrugStartDate drugStartDate);
	
}
