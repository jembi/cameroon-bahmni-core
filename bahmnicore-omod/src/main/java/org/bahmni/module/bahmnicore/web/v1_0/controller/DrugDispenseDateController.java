package org.openmrs.module.cameroonbahmni.web.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bahmni.module.bahmnicore.dao.DrugDispenseDateDao;
import org.bahmni.module.bahmnicore.model.DrugDispenseDate;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.openmrs.Order;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class DrugDispenseDateController extends BaseRestController {

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private DrugDispenseDateDao DrugDispenseDateDao;
	
	@RequestMapping(value = "/rest/v1" + "/cameroonbahmni/drugDispenseDate/{orderUUID}", method = RequestMethod.GET)
	@ResponseBody
	public DrugDispenseDate dispenseDate(@PathVariable("orderUUID") String orderUUID ) throws Exception {
		DrugDispenseDate oldDrugDispenseDate = DrugDispenseDateDao.getDispenseDateByOrderUUID(orderUUID);
		return oldDrugDispenseDate;
	}
	
	@RequestMapping(value = "/rest/v1" + "/cameroonbahmni/drugDispenseDate", method = RequestMethod.POST)
	@ResponseBody
	public DrugDispenseDate save(@RequestBody DrugDispenseDate DrugDispenseDate) throws Exception {
		Order order = DrugDispenseDateDao.getOrderIDByUuid(DrugDispenseDate.getOrderUuid());
		DrugDispenseDate.setOrderId(order.getOrderId());
		DrugDispenseDate oldDrugDispenseDate = DrugDispenseDateDao.getDispenseDateByOrderUUID(DrugDispenseDate.getOrderUuid());

		if (oldDrugDispenseDate != null) {
			log.error(oldDrugDispenseDate.getId());
			DrugDispenseDate.setId(oldDrugDispenseDate.getId());
			return DrugDispenseDateDao.updateDrugDispenseDate(DrugDispenseDate);
		}else {
			return DrugDispenseDateDao.saveOrUpdate(DrugDispenseDate);
		}
	}
}
