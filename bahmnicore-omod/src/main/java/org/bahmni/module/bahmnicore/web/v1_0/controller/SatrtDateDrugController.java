package org.openmrs.module.cameroonbahmni.web.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bahmni.module.bahmnicore.dao.DrugStartDateDao;
import org.bahmni.module.bahmnicore.model.DrugStartDate;
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
public class SatrtDateDrugController extends BaseRestController {

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private DrugStartDateDao drugStartDateDao;
	
	@RequestMapping(value = "/rest/v1" + "/cameroonbahmni/startdate/{orderUUID}", method = RequestMethod.GET)
	@ResponseBody
	public DrugStartDate startDate(@PathVariable("orderUUID") String orderUUID ) throws Exception {
		DrugStartDate oldDrugStartDate = drugStartDateDao.getStartDateByOrderUUID(orderUUID);
		return oldDrugStartDate;
	}
	
	@RequestMapping(value = "/rest/v1" + "/cameroonbahmni/drugstartdate", method = RequestMethod.POST)
	@ResponseBody
	public DrugStartDate save(@RequestBody DrugStartDate drugStartDate) throws Exception {
		Order order = drugStartDateDao.getOrderIDByUuid(drugStartDate.getOrderUuid());
		drugStartDate.setOrderId(order.getOrderId());
		DrugStartDate oldDrugStartDate = drugStartDateDao.getStartDateByOrderUUID(drugStartDate.getOrderUuid());

		if (oldDrugStartDate != null) {
			log.error(oldDrugStartDate.getId());
			drugStartDate.setId(oldDrugStartDate.getId());
			return drugStartDateDao.updateDrugStartDate(drugStartDate);
		}else {
			return drugStartDateDao.saveOrUpdate(drugStartDate);
		}
	}
}
