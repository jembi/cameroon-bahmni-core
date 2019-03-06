package org.bahmni.module.bahmnicore.contract.patient.search;

import org.bahmni.module.bahmnicore.model.WildCardParameter;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class PatientGenderQueryHelper {
	private String gender;
	public static final String BY_NAME_PARTS = " p.gender like ";

	public PatientGenderQueryHelper(String gender){
		this.gender = gender;
	}

	public String appendToWhereClause(String where){
		WildCardParameter nameParameter = WildCardParameter.create(gender);
		String nameSearchCondition = getNameSearchCondition(nameParameter);
		where = isEmpty(nameSearchCondition) ? where : combine(where, "and", enclose(nameSearchCondition));
		return where;
	}

	private String getNameSearchCondition(WildCardParameter wildCardParameter) {
		if (wildCardParameter.isEmpty())
			return "";
		String query_by_name_parts = "";
		for (String part : wildCardParameter.getParts()) {
			if (!"".equals(query_by_name_parts)) {
				query_by_name_parts += " and " + BY_NAME_PARTS + " '" + part + "'";
			} else {
				query_by_name_parts += BY_NAME_PARTS + " '" + part + "'";
			}
		}
		return query_by_name_parts;
	}

	private String combine(String query, String operator, String condition) {
		return String.format("%s %s %s", query, operator, condition);
	}

	private String enclose(String value) {
		return String.format("(%s)", value);
	}
}
