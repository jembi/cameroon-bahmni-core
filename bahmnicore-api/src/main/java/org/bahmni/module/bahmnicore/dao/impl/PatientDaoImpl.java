package org.bahmni.module.bahmnicore.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.bahmni.module.bahmnicore.contract.patient.mapper.PatientResponseMapper;
import org.bahmni.module.bahmnicore.contract.patient.response.DuplicatedPatientResponse;
import org.bahmni.module.bahmnicore.contract.patient.response.PatientResponse;
import org.bahmni.module.bahmnicore.contract.patient.search.PatientSearchBuilder;
import org.bahmni.module.bahmnicore.dao.PatientDao;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.ProgramAttributeType;
import org.bahmni.module.bahmnicore.service.BahmniProgramWorkflowService;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.transform.Transformers;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.visitlocation.BahmniVisitLocationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Repository
public class PatientDaoImpl implements PatientDao {

    private SessionFactory sessionFactory;

    @Autowired
    public PatientDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<PatientResponse> getPatients(String identifier, String name, String customAttribute,
                                             String addressFieldName, String addressFieldValue, Integer length,
                                             Integer offset, String[] customAttributeFields, String programAttributeFieldValue,
                                             String programAttributeFieldName, String[] addressSearchResultFields,
                                             String[] patientSearchResultFields, String loginLocationUuid, Boolean filterPatientsByLocation, Boolean filterOnAllIdentifiers) {

        validateSearchParams(customAttributeFields, programAttributeFieldName, addressFieldName);

        ProgramAttributeType programAttributeType = getProgramAttributeType(programAttributeFieldName);

        SQLQuery sqlQuery = new PatientSearchBuilder(sessionFactory)
                .withPatientName(name)
                .withPatientAddress(addressFieldName, addressFieldValue, addressSearchResultFields)
                .withPatientIdentifier(identifier, filterOnAllIdentifiers)
                .withPatientAttributes(customAttribute, getPersonAttributeIds(customAttributeFields), getPersonAttributeIds(patientSearchResultFields))
                .withProgramAttributes(programAttributeFieldValue, programAttributeType)
                .withLocation(loginLocationUuid, filterPatientsByLocation)
                .buildSqlQuery(length, offset);
        return sqlQuery.list();
    }

    @Override
    public List<DuplicatedPatientResponse> getDuplicatedPatients(String systemIdentifier,
                                                                 String givenName, String familyName,
                                                                 Date dateOfBirth, String gender, String phoneNumber,
                                                                 String subDivision) {

        String givenNameFilter = "";
        if (givenName != null) {
            givenNameFilter = "pn.given_name LIKE :givenName AND ";
        }

        String familyNameFilter = "";
        if (familyName != null) {
            familyNameFilter = "pn.family_name LIKE :familyName AND ";
        }

        String dateOfBirthFilter = "";
        if (dateOfBirth != null) {
            dateOfBirthFilter = "YEAR(per.birthdate) = YEAR(:dateOfBirth) AND ";
        }

        String genderFilter = "";
        if (gender != null) {
            genderFilter = "per.gender = :gender AND ";
        }

        String phoneNumberFilter = "";
        if (phoneNumber != null) {
            phoneNumberFilter = " AND pattr.value = :phoneNumber ";
        }

        String subDivisionFilter = "";
        if (subDivision != null) {
            subDivisionFilter = "pad.address3 LIKE :subDivision AND ";
        }

        StringBuilder queryString = new StringBuilder(
            "SELECT " +
            " per.uuid as `uuid`, " +
            "( SELECT pi.identifier " +
              "FROM patient_identifier as pi " +
              "JOIN patient_identifier_type as pit ON pi.identifier_type = pit.patient_identifier_type_id " +
              "WHERE pi.patient_id = pat.patient_id " +
                "AND pit.retired = 0 " +
                "AND pit.name = 'Patient Identifier' " +
              ") as `systemIdentifier`, " +
            "( SELECT pi.identifier " +
              "FROM patient_identifier as pi " +
              "JOIN patient_identifier_type as pit ON pi.identifier_type = pit.patient_identifier_type_id " +
              "WHERE pi.patient_id = pat.patient_id " +
                "AND pit.retired = 0 " +
                "AND pit.name = 'REGISTRATION_IDTYPE_1_CNI_KEY' " +
              ") as `cni`, " +
            "( SELECT pi.identifier " +
              "FROM patient_identifier as pi " +
              "JOIN patient_identifier_type as pit ON pi.identifier_type = pit.patient_identifier_type_id " +
              "WHERE pi.patient_id = pat.patient_id " +
                "AND pit.retired = 0 " +
                "AND pit.name = 'REGISTRATION_IDTYPE_2_ART_KEY' " +
              ") as `art`, " +
            "CONCAT( pn.given_name, ' ', pn.family_name ) as `name`, " +
            "per.gender as `gender`, " +
            "per.birthdate as `birthDate`, " +
            "per.death_date as `deathDate`, " +
            "pad.address3 as `subDivision`, " +
            "( SELECT pattr.value " +
              "FROM person_attribute as pattr " +
              "JOIN person_attribute_type as pattrt ON pattr.person_attribute_type_id = pattrt.person_attribute_type_id " +
              "WHERE pattr.person_id = per.person_id " +
                "AND pattr.voided = 0 " +
                "AND pattrt.retired = 0 " +
                "AND pattrt.name = 'PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER' " +
                phoneNumberFilter +
              ") as `phoneNumber` " +
        "FROM " +
            "person as per " +
        "JOIN patient as pat ON pat.patient_id = per.person_id AND pat.voided = 0 " +
        "JOIN person_name as pn ON pn.person_id = per.person_id AND pn.voided = 0 " +
        "LEFT JOIN person_address as pad ON pad.person_id = per.person_id AND pad.voided = 0 " +
        "WHERE " +
            genderFilter +
            dateOfBirthFilter +
            givenNameFilter +
            familyNameFilter +
            subDivisionFilter +
            " per.voided = 0 " +
        " LIMIT 0,5");

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(queryString.toString());

        if (givenName != null) {
            query.setParameter("givenName", "%" + givenName + "%");
        }

        if (familyName != null) {
            query.setParameter("familyName","%" + familyName + "%");
        }

        if (dateOfBirth != null) {
            query.setParameter("dateOfBirth",dateOfBirth);
        }

        if (gender != null) {
            query.setParameter("gender",gender);
        }

        if (phoneNumber != null) {
            query.setParameter("phoneNumber",phoneNumber);
        }

        if (subDivision != null) {
            query.setParameter("subDivision","%" + subDivision + "%");
        }

        query.setResultTransformer(Transformers.aliasToBean(DuplicatedPatientResponse.class));

        List<DuplicatedPatientResponse> result =  query.list();
        return removeEmptySystemIdentifierOrPhoneNumber(result, systemIdentifier, phoneNumber);
    }

    private List<DuplicatedPatientResponse> removeEmptySystemIdentifierOrPhoneNumber(List<DuplicatedPatientResponse> result, String systemIdentifier, String phoneNumber) {
    	List<DuplicatedPatientResponse> filteredResult = new ArrayList<>();
    	for(DuplicatedPatientResponse record: result) {
            if ((systemIdentifier != null && systemIdentifier.equalsIgnoreCase(record.getSystemIdentifier())) ||
                (phoneNumber != null && !phoneNumber.equalsIgnoreCase(record.getPhoneNumber()))) {
                // Record excluded from the final result
            } else {
            	filteredResult.add(record);
            }
        }
    	return filteredResult;
    }

    @Override
    public List<PatientResponse> getPatientsUsingLuceneSearch(String identifier, String name, String customAttribute,
                                                              String addressFieldName, String addressFieldValue, Integer length,
                                                              Integer offset, String[] customAttributeFields, String programAttributeFieldValue,
                                                              String programAttributeFieldName, String[] addressSearchResultFields,
                                                              String[] patientSearchResultFields, String loginLocationUuid,
                                                              Boolean filterPatientsByLocation, Boolean filterOnAllIdentifiers) {

        validateSearchParams(customAttributeFields, programAttributeFieldName, addressFieldName);

        List<PatientIdentifier> patientIdentifiers = getPatientIdentifiers(identifier, filterOnAllIdentifiers, offset, length);
        List<Integer> patientIds = patientIdentifiers.stream().map(patientIdentifier -> patientIdentifier.getPatient().getPatientId()).collect(toList());
        Map<Object, Object> programAttributes = Context.getService(BahmniProgramWorkflowService.class).getPatientProgramAttributeByAttributeName(patientIds, programAttributeFieldName);
        PatientResponseMapper patientResponseMapper = new PatientResponseMapper(Context.getVisitService(),new BahmniVisitLocationServiceImpl(Context.getLocationService()));
        Set<Integer> uniquePatientIds = new HashSet<>();
        List<PatientResponse> patientResponses = patientIdentifiers.stream()
                .map(patientIdentifier -> {
                    Patient patient = patientIdentifier.getPatient();
                    if(!uniquePatientIds.contains(patient.getPatientId())) {
                        PatientResponse patientResponse = patientResponseMapper.map(patient, loginLocationUuid, patientSearchResultFields, addressSearchResultFields,
                                programAttributes.get(patient.getPatientId()));
                        uniquePatientIds.add(patient.getPatientId());
                        return patientResponse;
                    }else
                        return null;
                }).filter(Objects::nonNull)
                .collect(toList());
        return patientResponses;
    }

    private List<PatientIdentifier> getPatientIdentifiers(String identifier, Boolean filterOnAllIdentifiers, Integer offset, Integer length) {
        FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
        QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(PatientIdentifier.class).get();
        identifier = identifier.replace('%','*');
        org.apache.lucene.search.Query identifierQuery = queryBuilder.keyword()
                .wildcard().onField("identifierAnywhere").matching("*" + identifier.toLowerCase() + "*").createQuery();
        org.apache.lucene.search.Query nonVoidedIdentifiers = queryBuilder.keyword().onField("voided").matching(false).createQuery();
        org.apache.lucene.search.Query nonVoidedPatients = queryBuilder.keyword().onField("patient.voided").matching(false).createQuery();
    
        List<String> identifierTypeNames = getIdentifierTypeNames(filterOnAllIdentifiers);

        BooleanJunction identifierTypeShouldJunction = queryBuilder.bool();
        for (String identifierTypeName:
                identifierTypeNames) {
            org.apache.lucene.search.Query identifierTypeQuery = queryBuilder.phrase().onField("identifierType.name").sentence(identifierTypeName).createQuery();
            identifierTypeShouldJunction.should(identifierTypeQuery);
        }

        org.apache.lucene.search.Query booleanQuery = queryBuilder.bool()
                .must(identifierQuery)
                .must(nonVoidedIdentifiers)
                .must(nonVoidedPatients)
                .must(identifierTypeShouldJunction.createQuery())
                .createQuery();

        Sort sort = new Sort( new SortField( "identifier", SortField.Type.STRING, false ) );
        FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(booleanQuery, PatientIdentifier.class);
        fullTextQuery.setSort(sort);
        fullTextQuery.setFirstResult(offset);
        fullTextQuery.setMaxResults(length);
        return (List<PatientIdentifier>) fullTextQuery.list();
    }
    
    private List<String> getIdentifierTypeNames(Boolean filterOnAllIdentifiers) {
        List<String> identifierTypeNames = new ArrayList<>();
        addIdentifierTypeName(identifierTypeNames,"bahmni.primaryIdentifierType");
        if(filterOnAllIdentifiers){
            addIdentifierTypeName(identifierTypeNames,"bahmni.extraPatientIdentifierTypes");
        }
        return identifierTypeNames;
    }

    private void addIdentifierTypeName(List<String> identifierTypeNames,String identifierProperty) {
        String identifierTypes = Context.getAdministrationService().getGlobalProperty(identifierProperty);
        if(StringUtils.isNotEmpty(identifierTypes)) {
            String[] identifierUuids = identifierTypes.split(",");
            for (String identifierUuid :
                    identifierUuids) {
                PatientIdentifierType patientIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(identifierUuid);
                if (patientIdentifierType != null) {
                    identifierTypeNames.add(patientIdentifierType.getName());
                }
            }
        }
    }

    private void validateSearchParams(String[] customAttributeFields, String programAttributeFieldName, String addressFieldName) {
        List<Integer> personAttributeIds = getPersonAttributeIds(customAttributeFields);
        if (customAttributeFields != null && personAttributeIds.size() != customAttributeFields.length) {
            throw new IllegalArgumentException(String.format("Invalid Attribute In Patient Attributes [%s]", StringUtils.join(customAttributeFields, ", ")));
        }

        ProgramAttributeType programAttributeTypeId = getProgramAttributeType(programAttributeFieldName);
        if (programAttributeFieldName != null && programAttributeTypeId == null) {
            throw new IllegalArgumentException(String.format("Invalid Program Attribute %s", programAttributeFieldName));
        }


        if (!isValidAddressField(addressFieldName)) {
            throw new IllegalArgumentException(String.format("Invalid Address Filed %s", addressFieldName));
        }
    }

    private boolean isValidAddressField(String addressFieldName) {
        if (addressFieldName == null) return true;
        String query = "SELECT DISTINCT COLUMN_NAME FROM information_schema.columns WHERE\n" +
                "LOWER (TABLE_NAME) ='person_address' and LOWER(COLUMN_NAME) IN " +
                "( :personAddressField)";
        Query queryToGetAddressFields = sessionFactory.getCurrentSession().createSQLQuery(query);
        queryToGetAddressFields.setParameterList("personAddressField", Arrays.asList(addressFieldName.toLowerCase()));
        List list = queryToGetAddressFields.list();
        return list.size() > 0;
    }

    private ProgramAttributeType getProgramAttributeType(String programAttributeField) {
        if (StringUtils.isEmpty(programAttributeField)) {
            return null;
        }

        return (ProgramAttributeType) sessionFactory.getCurrentSession().createCriteria(ProgramAttributeType.class).
                add(Restrictions.eq("name", programAttributeField)).uniqueResult();
    }

    private List<Integer> getPersonAttributeIds(String[] patientAttributes) {
        if (patientAttributes == null || patientAttributes.length == 0) {
            return new ArrayList<>();
        }

        String query = "select person_attribute_type_id from person_attribute_type where name in " +
                "( :personAttributeTypeNames)";
        Query queryToGetAttributeIds = sessionFactory.getCurrentSession().createSQLQuery(query);
        queryToGetAttributeIds.setParameterList("personAttributeTypeNames", Arrays.asList(patientAttributes));
        List list = queryToGetAttributeIds.list();
        return (List<Integer>) list;
    }

    @Override
    public Patient getPatient(String identifier) {
        Session currentSession = sessionFactory.getCurrentSession();
        List<PatientIdentifier> ident = currentSession.createQuery("from PatientIdentifier where identifier = :ident").setString("ident", identifier).list();
        if (!ident.isEmpty()) {
            return ident.get(0).getPatient();
        }
        return null;
    }

    @Override
    public List<Patient> getPatients(String patientIdentifier, boolean shouldMatchExactPatientId) {
        if (!shouldMatchExactPatientId) {
            String partialIdentifier = "%" + patientIdentifier;
            Query querytoGetPatients = sessionFactory.getCurrentSession().createQuery(
                    "select pi.patient " +
                            " from PatientIdentifier pi " +
                            " where pi.identifier like :partialIdentifier ");
            querytoGetPatients.setString("partialIdentifier", partialIdentifier);
            return querytoGetPatients.list();
        }

        Patient patient = getPatient(patientIdentifier);
        List<Patient> result = (patient == null ? new ArrayList<Patient>() : Arrays.asList(patient));
        return result;
    }

    @Override
    public List<RelationshipType> getByAIsToB(String aIsToB) {
        Query querytoGetPatients = sessionFactory.getCurrentSession().createQuery(
                "select rt " +
                        " from RelationshipType rt " +
                        " where rt.aIsToB = :aIsToB ");
        querytoGetPatients.setString("aIsToB", aIsToB);
        return querytoGetPatients.list();
    }
}
