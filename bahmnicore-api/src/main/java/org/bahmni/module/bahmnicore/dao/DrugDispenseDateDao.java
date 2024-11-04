package org.bahmni.module.bahmnicore.dao;


import org.bahmni.module.bahmnicore.model.DrugDispenseDate;
import org.openmrs.Order;

public interface DrugDispenseDateDao {
	DrugDispenseDate saveOrUpdate(DrugDispenseDate drugDispenseDate);
	Order getOrderIDByUuid(String uuid);
	DrugDispenseDate getDispenseDateByOrderUUID(String uuid);
	DrugDispenseDate updateDrugDispenseDate(DrugDispenseDate drugDispenseDate);
}
