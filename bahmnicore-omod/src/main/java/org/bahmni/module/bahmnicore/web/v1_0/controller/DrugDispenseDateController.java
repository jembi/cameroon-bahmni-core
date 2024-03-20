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
	private DrugDispenseDateDao drugDispenseDateDao;
	
	@RequestMapping(value = "/rest/v1" + "/cameroonbahmni/drugDispensedate/{orderUUID}", method = RequestMethod.GET)
	@ResponseBody
	public DrugDispenseDate getDispenseDate(@PathVariable("orderUUID") String orderUUID ) throws Exception {
		DrugDispenseDate drugDispenseDate = drugDispenseDateDao.getDispenseDateByOrderUUID(orderUUID);
		return drugDispenseDate;
	}
	
	@RequestMapping(value = "/rest/v1" + "/cameroonbahmni/drugDispensedate", method = RequestMethod.POST)
	@ResponseBody
	public DrugDispenseDate save(@RequestBody DrugDispenseDate drugDispenseDate) throws Exception {
		Order order = drugDispenseDateDao.getOrderIDByUuid(drugDispenseDate.getOrderUuid());
		drugDispenseDate.setOrderId(order.getOrderId());
		DrugDispenseDate oldDrugDispenseDate = drugDispenseDateDao.getDispenseDateByOrderUUID(drugDispenseDate.getOrderUuid());

		if (oldDrugDispenseDate != null) {
			log.error(oldDrugDispenseDate.getId());
			drugDispenseDate.setId(oldDrugDispenseDate.getId());
			return drugDispenseDateDao.updateDrugDispenseDate(drugDispenseDate);
		}else {
			return drugDispenseDateDao.saveOrUpdate(drugDispenseDate);
		}
	}
}
