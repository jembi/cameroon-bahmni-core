package org.bahmni.module.bahmnicore.contract.patient.search;


import static org.apache.commons.lang.StringUtils.isEmpty;

public class PatientDuplicateBirthDateQueryHelper {
	private String previousBirthYear;
	private String nextBirthYear;
	public static final String BY_NAME_PARTS = " p.birthdate between ";

	public PatientDuplicateBirthDateQueryHelper(String previousBirthYear, String nextBirthYear){
		this.previousBirthYear = previousBirthYear;
		this.nextBirthYear = nextBirthYear;
	}

	public String appendToWhereClause(String where){
		String birthDateSearchCondition = getBirthDateSearchCondition(previousBirthYear, nextBirthYear);
		where = isEmpty(birthDateSearchCondition) ? where : combine(where, "and", enclose(birthDateSearchCondition));
		return where;
	}

	private String getBirthDateSearchCondition(String previousBirthYear, String nextBirthYear) {
		String query_by_name_parts = "";
		if (isEmpty(previousBirthYear) || isEmpty(nextBirthYear)) {
			return query_by_name_parts;
		}
		query_by_name_parts += BY_NAME_PARTS + " '" + previousBirthYear + "' and " + " '" + nextBirthYear + "' ";
		return query_by_name_parts;
	}

	private String combine(String query, String operator, String condition) {
		return String.format("%s %s %s", query, operator, condition);
	}

	private String enclose(String value) {
		return String.format("(%s)", value);
	}
}
