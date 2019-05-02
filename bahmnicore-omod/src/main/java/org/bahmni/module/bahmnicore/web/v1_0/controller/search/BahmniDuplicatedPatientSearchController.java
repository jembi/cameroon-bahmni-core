package org.bahmni.module.bahmnicore.web.v1_0.controller.search;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bahmni.module.bahmnicore.contract.patient.response.DuplicatedPatientResponse;
import org.bahmni.module.bahmnicore.service.BahmniPatientService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for REST web service access to
 * the Search resource.
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/search/duplicated-patient")
public class BahmniDuplicatedPatientSearchController extends BaseRestController {

    private BahmniPatientService bahmniPatientService;

    @Autowired
    public BahmniDuplicatedPatientSearchController(BahmniPatientService bahmniPatientService) {
        this.bahmniPatientService = bahmniPatientService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<DuplicatedPatientResponse>> search(HttpServletRequest request,
                                                  HttpServletResponse response) throws ResponseException{
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date dateOfBirth = null;
            if (request.getParameter("dateOfBirth") != null) {
                dateOfBirth = formatter.parse(request.getParameter("dateOfBirth"));
            }

            List<DuplicatedPatientResponse> patients = bahmniPatientService
                .getDuplicatedPatients(
                    request.getParameter("systemIdentifier"),
                    request.getParameter("givenName"),
                    request.getParameter("familyName"),
                    dateOfBirth,
                    request.getParameter("gender"),
                    request.getParameter("phoneNumber"),
                    request.getParameter("subDivision"));

            return new ResponseEntity<>(patients,HttpStatus.OK);
        }catch (ParseException e){
            return new ResponseEntity(RestUtil.wrapErrorResponse(e, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}
