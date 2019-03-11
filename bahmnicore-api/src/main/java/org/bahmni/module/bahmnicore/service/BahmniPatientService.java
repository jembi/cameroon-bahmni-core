package org.bahmni.module.bahmnicore.service;

import org.bahmni.module.bahmnicore.contract.patient.PatientSearchParameters;
import org.bahmni.module.bahmnicore.contract.patient.response.DuplicatedPatientResponse;
import org.bahmni.module.bahmnicore.contract.patient.response.PatientConfigResponse;
import org.bahmni.module.bahmnicore.contract.patient.response.PatientResponse;
import org.openmrs.Patient;
import org.openmrs.RelationshipType;

import java.util.Date;
import java.util.List;

public interface BahmniPatientService {
    public PatientConfigResponse getConfig();

    public List<PatientResponse> search(PatientSearchParameters searchParameters);

    List<PatientResponse> luceneSearch(PatientSearchParameters searchParameters);

    public List<DuplicatedPatientResponse> getDuplicatedPatients(String systemIdentifier, String givenName, String familyName,
                                            Date dateOfBirth, String gender, String phoneNumber, String subDivision);

    public List<Patient> get(String partialIdentifier, boolean shouldMatchExactPatientId);

    public List<RelationshipType> getByAIsToB(String aIsToB);
}
