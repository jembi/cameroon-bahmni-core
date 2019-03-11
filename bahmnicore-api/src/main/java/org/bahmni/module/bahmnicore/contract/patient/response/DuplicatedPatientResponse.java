package org.bahmni.module.bahmnicore.contract.patient.response;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DuplicatedPatientResponse {
    private String systemIdentifier;
    private String cni;
    private String art;
    private String name;
    private String gender;
    private Date birthDate;
    private Date deathDate;
    private String subDivision;
    private String phoneNumber;

    public String getAge() {
        if (birthDate == null)
            return null;

        // Use default end date as today.
        Calendar today = Calendar.getInstance();

        // If date given is after date of death then use date of death as end date
        if (getDeathDate() != null && today.getTime().after(getDeathDate())) {
            today.setTime(getDeathDate());
        }

        Calendar bday = Calendar.getInstance();
        bday.setTime(birthDate);

        int age = today.get(Calendar.YEAR) - bday.get(Calendar.YEAR);

        // Adjust age when today's date is before the person's birthday
        int todaysMonth = today.get(Calendar.MONTH);
        int bdayMonth = bday.get(Calendar.MONTH);
        int todaysDay = today.get(Calendar.DAY_OF_MONTH);
        int bdayDay = bday.get(Calendar.DAY_OF_MONTH);

        if (todaysMonth < bdayMonth) {
            age--;
        } else if (todaysMonth == bdayMonth && todaysDay < bdayDay) {
            // we're only comparing on month and day, not minutes, etc
            age--;
        }

        return Integer.toString(age);
    }

    public void setSystemIdentifier(String systemIdentifier) {
        this.systemIdentifier = systemIdentifier;
    }

    public String getSystemIdentifier() {
        return this.systemIdentifier;
    }

    public void setCni(String cni) {
        this.cni = cni;
    }

    public String getCni() {
        return this.cni;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getArt() {
        return this.art;
    }

    public void setSubDivision(String subDivision) {
        this.subDivision = subDivision;
    }

    public String getSubDivision() {
        return this.subDivision;
    }

    @JsonSerialize(using=JsonDateSerializer.class)
    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    @JsonSerialize(using=JsonDateSerializer.class)
    public Date getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(Date deathDate) {
        this.deathDate = deathDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Used to serialize Java.util.Date, which is not a common JSON
     * type, so we have to create a custom serialize method;
     */
    @Component
    public static  class JsonDateSerializer extends JsonSerializer<Date> {
        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        @Override
        public void serialize(Date date, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            String formattedDate = dateFormat.format(date);
            gen.writeString(formattedDate);
        }
    }
}
