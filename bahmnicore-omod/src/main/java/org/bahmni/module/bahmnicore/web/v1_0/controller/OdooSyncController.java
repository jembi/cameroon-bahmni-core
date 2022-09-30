package org.bahmni.module.bahmnicore.web.v1_0.controller;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

@Controller
public class OdooSyncController {
	protected final Log log = LogFactory.getLog(getClass());
	@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/odooapi/getquantity/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> gettest(@PathVariable String uuid) throws UnsupportedEncodingException {
		String uri = "http://127.0.0.1:8069/api/products?uuid={uuid}";
		URI expanded = new UriTemplate(uri).expand(uuid);
		uri = URLDecoder.decode(expanded.toString(), "UTF-8");
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(uri, String.class);
		return new ResponseEntity<>(result, HttpStatus.OK);	
	}

}
