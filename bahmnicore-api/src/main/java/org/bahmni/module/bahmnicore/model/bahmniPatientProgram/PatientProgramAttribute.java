package org.bahmni.module.bahmnicore.model.bahmniPatientProgram;

import org.openmrs.attribute.Attribute;
import org.openmrs.attribute.BaseAttribute;
import org.openmrs.customdatatype.CustomDatatype;
import org.openmrs.customdatatype.CustomDatatypeUtil;
import org.openmrs.customdatatype.NotYetPersistedException;

public class PatientProgramAttribute extends BaseAttribute<ProgramAttributeType, BahmniPatientProgram> implements Attribute<ProgramAttributeType, BahmniPatientProgram> {
    private Integer patientProgramAttributeId;

    @Override
    public Integer getId() {
        return getPatientProgramAttributeId();
    }

    @Override
    public void setId(Integer id) {
        setPatientProgramAttributeId(id);
    }

    public BahmniPatientProgram getPatientProgram() {
        return getOwner();
    }

    public void setPatientProgram(BahmniPatientProgram patientProgram) {
        setOwner(new BahmniPatientProgram(patientProgram));
    }

    public Integer getPatientProgramAttributeId() {
        return patientProgramAttributeId;
    }

    public void setPatientProgramAttributeId(Integer id) {
        this.patientProgramAttributeId = id;
    }
    
    public String getValueReferenceFromValue() {
		CustomDatatype datatype = CustomDatatypeUtil.getDatatype(this.getDescriptor());
		String existingValueReference = null;
		try {
			existingValueReference = this.getValueReference();
		} catch (NotYetPersistedException ex) {
			// this is expected
		}
		return datatype.save(this.getValue(), existingValueReference);
	}
}
