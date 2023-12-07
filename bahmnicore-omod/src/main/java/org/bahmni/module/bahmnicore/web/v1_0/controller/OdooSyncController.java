package org.bahmni.module.bahmnicore.web.v1_0.controller;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

@Controller
public class OdooSyncController {

	private static final org.apache.log4j.Logger log = Logger.getLogger(OdooSyncController.class);

	@RequestMapping(value = "/rest/" + RestConstants.VERSION_1
			+ "/odooapi/getquantity/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> gettest(@PathVariable String uuid) throws UnsupportedEncodingException {

		String odooHost = "localhost";
		String odooPort = "8069";

		String productUuid = uuid;
		String authURL = "http://" + odooHost + ":" + odooPort + "/web/session/authenticate";
		RestTemplate restTemplate = new RestTemplate();

		String sessionId = odooAuth(authURL, restTemplate);

		ResponseEntity<String> availableQtyRes = getAvailableQtyfromOdoo(odooHost, odooPort, productUuid, restTemplate,
				sessionId);

		String availableQtyResulst = availableQtyRes.getBody();
		return new ResponseEntity<>(availableQtyResulst, HttpStatus.OK);

	}

	private String odooAuth(String authURL, RestTemplate restTemplate) {
		String sessionId = "";
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("jsonrpc", "2.0");

			Map m = new LinkedHashMap(4);
			m.put("db", "odoo");
			m.put("login", "admin");
			m.put("password", "admin");
			jsonObject.put("params", m);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			String requestBody = jsonObject.toJSONString();

			HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

			ResponseEntity<String> responseEntity = restTemplate.exchange(authURL, HttpMethod.POST, requestEntity,
					String.class);

			String responseBody = responseEntity.getBody();
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(responseBody);

			JSONObject sessionidJason = (JSONObject) json.get("result");
			sessionId = (String) sessionidJason.get("session_id");

			log.warn("sessionId: " + sessionId);

		} catch (Exception e) {
			log.error(e);
		}
		return sessionId;
	}

	private ResponseEntity<String> getAvailableQtyfromOdoo(String odooHost, String odooPort, String productUuid,
			RestTemplate restTemplate, String sessionId) throws UnsupportedEncodingException {
		ResponseEntity<String> availableQtyRes = null;
		try {
			String getQtyUri = "http://" + odooHost + ":" + odooPort + "/api/products?uuid=" + productUuid;

			URI expandedGetQtyUri = new UriTemplate(getQtyUri).expand("");
			getQtyUri = URLDecoder.decode(expandedGetQtyUri.toString(), "UTF-8");

			HttpHeaders getQtyHeader = new HttpHeaders();
			getQtyHeader.add("Cookie", "session_id=" + sessionId);

			availableQtyRes = restTemplate.exchange(getQtyUri, HttpMethod.GET,
					new HttpEntity<String>(getQtyHeader), String.class);
		} catch (Exception e) {
			log.error(e);
		}
		return availableQtyRes;
	}

}