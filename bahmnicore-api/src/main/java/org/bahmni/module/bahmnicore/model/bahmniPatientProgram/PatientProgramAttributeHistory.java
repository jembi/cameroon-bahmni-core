package org.bahmni.module.bahmnicore.model.bahmniPatientProgram;

import java.util.Date;
import org.openmrs.attribute.BaseAttribute;

public class PatientProgramAttributeHistory extends BaseAttribute<ProgramAttributeType, BahmniPatientProgram> {
    private static final long serialVersionUID = 1L;

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
		this.setDateCreated(new Date());
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
