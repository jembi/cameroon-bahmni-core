package org.bahmni.module.bahmnicore.contract.patient.search;

import org.bahmni.module.bahmnicore.contract.patient.response.PatientResponse;
import org.bahmni.module.bahmnicore.customdatatype.datatype.CodedConceptDatatype;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.ProgramAttributeType;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PatientDuplicateSearchBuilder {
	
	private Log log = LogFactory.getLog(this.getClass());

	private String visitJoin = " left outer join visit v on v.patient_id = p.person_id and v.date_stopped is null ";
	private static String VISIT_JOIN = "_VISIT_JOIN_";
	public static final String SELECT_STATEMENT = "select " +
			"p.uuid as uuid, " +
			"p.person_id as personId, " +
			"pn.given_name as givenName, " +
			"pn.middle_name as middleName, " +
			"pn.family_name as familyName, " +
			"p.gender as gender, " +
			"p.birthdate as birthDate, " +
			"p.death_date as deathDate, " +
			"p.date_created as dateCreated, " +
			"v.uuid as activeVisitUuid, " +
			"primary_identifier.identifier as identifier, " +
			"extra_identifiers.identifiers as extraIdentifiers, " +
			"(CASE va.value_reference WHEN 'Admitted' THEN TRUE ELSE FALSE END) as hasBeenAdmitted ";
	public static final String WHERE_CLAUSE = " where p.voided = 'false' and pn.voided = 'false' and pn.preferred=true ";
	public static final String FROM_TABLE = " from person p ";
	public static final String JOIN_CLAUSE = " left join person_name pn on pn.person_id = p.person_id" +
			" left join person_address pa on p.person_id=pa.person_id and pa.voided = 'false'" +
			" JOIN (SELECT identifier, patient_id" +
			"      FROM patient_identifier pi" +
			" JOIN patient_identifier_type pit ON pi.identifier_type = pit.patient_identifier_type_id AND pi.voided IS FALSE AND pit.retired IS FALSE" +
			" JOIN global_property gp ON gp.property = 'bahmni.primaryIdentifierType' AND gp.property_value = pit.uuid" +
			"      GROUP BY pi.patient_id) as primary_identifier ON p.person_id = primary_identifier.patient_id" +
			" LEFT JOIN (SELECT concat('{', group_concat((concat('\"', pit.name, '\":\"', pi.identifier, '\"')) SEPARATOR ','), '}') AS identifiers," +
			"        patient_id" +
			"      FROM patient_identifier pi" +
			"        JOIN patient_identifier_type pit ON pi.identifier_type = pit.patient_identifier_type_id AND pi.voided IS FALSE AND pit.retired IS FALSE "+
			" JOIN global_property gp ON gp.property = 'bahmni.primaryIdentifierType' AND gp.property_value != pit.uuid" +
			"  GROUP BY pi.patient_id) as extra_identifiers ON p.person_id = extra_identifiers.patient_id" +
			VISIT_JOIN +
			" left outer join visit_attribute va on va.visit_id = v.visit_id " +
			"   and va.attribute_type_id = (select visit_attribute_type_id from visit_attribute_type where name='Admission Status') " +
			"   and va.voided = 0";
	private static final String GROUP_BY_KEYWORD = " group by ";
	public static final String ORDER_BY = " order by primary_identifier.identifier asc LIMIT :limit OFFSET :offset";
	private static final String LIMIT_PARAM = "limit";
	private static final String OFFSET_PARAM = "offset";


	private String select;
	private String where;
	private String from;
	private String join;
	private String groupBy;
	private String orderBy;
	private SessionFactory sessionFactory;
	private Map<String,Type> types;

	public PatientDuplicateSearchBuilder(SessionFactory sessionFactory){
		select = SELECT_STATEMENT;
		where = WHERE_CLAUSE;
		from  = FROM_TABLE;
		join = JOIN_CLAUSE;
		orderBy = ORDER_BY;
		groupBy = " p.person_id";
		this.sessionFactory = sessionFactory;
		types = new HashMap<>();

	}

	public PatientDuplicateSearchBuilder withPatientName(String name){
		PatientDuplicateNameQueryHelper patientDuplicateNameQueryHelper = new PatientDuplicateNameQueryHelper(name);
		where = patientDuplicateNameQueryHelper.appendToWhereClause(where);
		return this;
	}
	
	public PatientDuplicateSearchBuilder withPatientGender(String gender){
		PatientGenderQueryHelper patientGenderQueryHelper = new PatientGenderQueryHelper(gender);
		where = patientGenderQueryHelper.appendToWhereClause(where);
		return this;
	}
	
	public PatientDuplicateSearchBuilder withPatientBirthDate(String birthDate){
		PatientDuplicateBirthDateQueryHelper patientDuplicateBirthDateQueryHelper = new PatientDuplicateBirthDateQueryHelper(birthDate);
		where = patientDuplicateBirthDateQueryHelper.appendToWhereClause(where);
		return this;
	}
	
	public PatientDuplicateSearchBuilder withPatientAddress(String addressFieldName, String addressFieldValue, String[] addressAttributeFields){
		PatientAddressFieldQueryHelper patientAddressQueryHelper = new PatientAddressFieldQueryHelper(addressFieldName,addressFieldValue, addressAttributeFields);
		where = patientAddressQueryHelper.appendToWhereClause(where);
		select = patientAddressQueryHelper.selectClause(select);
		groupBy = patientAddressQueryHelper.appendToGroupByClause(groupBy);
		types.putAll(patientAddressQueryHelper.addScalarQueryResult());
		return this;
	}
	
	public PatientDuplicateSearchBuilder withPatientAttributes(String customAttribute, List<Integer> personAttributeIds, List<Integer> attributeIds){
		if(personAttributeIds.size() == 0 && attributeIds.size() == 0){
			return this;
			
		}
		PatientAttributeQueryHelper patientAttributeQueryHelper = new PatientAttributeQueryHelper(customAttribute,personAttributeIds,attributeIds);
		select = patientAttributeQueryHelper.selectClause(select);
		join = patientAttributeQueryHelper.appendToJoinClause(join);
		where = patientAttributeQueryHelper.appendToWhereClause(where);
		types.putAll(patientAttributeQueryHelper.addScalarQueryResult());
		return this;
	}

	public SQLQuery buildSqlQuery(Integer limit, Integer offset){
		String joinWithVisit = join.replace(VISIT_JOIN, visitJoin);
		String query = select + from + joinWithVisit + where + GROUP_BY_KEYWORD + groupBy  + orderBy;
		SQLQuery sqlQuery = sessionFactory.getCurrentSession()
				.createSQLQuery(query)
				.addScalar("uuid", StandardBasicTypes.STRING)
				.addScalar("identifier", StandardBasicTypes.STRING)
				.addScalar("givenName", StandardBasicTypes.STRING)
				.addScalar("personId", StandardBasicTypes.INTEGER)
				.addScalar("middleName", StandardBasicTypes.STRING)
				.addScalar("familyName", StandardBasicTypes.STRING)
				.addScalar("gender", StandardBasicTypes.STRING)
				.addScalar("birthDate", StandardBasicTypes.DATE)
				.addScalar("deathDate", StandardBasicTypes.DATE)
				.addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("activeVisitUuid", StandardBasicTypes.STRING)
				.addScalar("hasBeenAdmitted", StandardBasicTypes.BOOLEAN)
				.addScalar("extraIdentifiers", StandardBasicTypes.STRING);

		Iterator<Map.Entry<String,Type>> iterator = types.entrySet().iterator();

		while(iterator.hasNext()){
			Map.Entry<String,Type> entry = iterator.next();
			sqlQuery.addScalar(entry.getKey(),entry.getValue());
		}

		sqlQuery.setParameter(LIMIT_PARAM, limit);
		sqlQuery.setParameter(OFFSET_PARAM, offset);
		sqlQuery.setResultTransformer(Transformers.aliasToBean(PatientResponse.class));
		return sqlQuery;
	}

	public PatientDuplicateSearchBuilder withLocation(String loginLocationUuid, Boolean filterPatientsByLocation) {
		PatientVisitLocationQueryHelper patientVisitLocationQueryHelper = new PatientVisitLocationQueryHelper(loginLocationUuid);
		visitJoin = patientVisitLocationQueryHelper.appendVisitJoinClause(visitJoin);
		if (filterPatientsByLocation) {
			where = patientVisitLocationQueryHelper.appendWhereClause(where);
		}
		return this;
	}
}
