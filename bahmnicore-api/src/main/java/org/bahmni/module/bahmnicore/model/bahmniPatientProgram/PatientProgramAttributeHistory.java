package org.bahmni.module.bahmnicore.model.bahmniPatientProgram;

import org.openmrs.attribute.Attribute;
import org.openmrs.attribute.BaseAttribute;
import org.openmrs.customdatatype.CustomDatatype;
import org.openmrs.customdatatype.CustomDatatypeUtil;
import org.openmrs.customdatatype.InvalidCustomValueException;
import org.openmrs.customdatatype.NotYetPersistedException;
import org.openmrs.customdatatype.SingleCustomValue;

public class PatientProgramAttributeHistory extends BaseAttribute<ProgramAttributeType, BahmniPatientProgram> implements Attribute<ProgramAttributeType, BahmniPatientProgram> {
    private Integer patientProgramAttributeHistoryId;
    
    public PatientProgramAttributeHistory() {
		super();
	}

	public PatientProgramAttributeHistory(PatientProgramAttribute attribute) {
		super();
		this.setValue(attribute.getValue());
		this.setAttributeType(attribute.getAttributeType());
		this.setPatientProgram(attribute.getPatientProgram());
		this.setCreator(attribute.getCreator());
		this.setDateCreated(attribute.getDateCreated());
		this.setValueReferenceInternal(attribute.getValueReferenceFromValue());
		
	}

	@Override
    public Integer getId() {
        return getPatientProgramAttributeHistoryId();
    }

    @Override
    public void setId(Integer id) {
    	setPatientProgramAttributeHistoryId(id);
    }

    public BahmniPatientProgram getPatientProgram() {
        return getOwner();
    }

    public void setPatientProgram(BahmniPatientProgram patientProgram) {
        setOwner(new BahmniPatientProgram(patientProgram));
    }

    public Integer getPatientProgramAttributeHistoryId() {
        return patientProgramAttributeHistoryId;
    }

    public void setPatientProgramAttributeHistoryId(Integer id) {
        this.patientProgramAttributeHistoryId = id;
    }   
    
}
