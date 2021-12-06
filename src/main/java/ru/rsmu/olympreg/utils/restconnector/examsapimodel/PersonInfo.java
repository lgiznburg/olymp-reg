package ru.rsmu.olympreg.utils.restconnector.examsapimodel;

import ru.rsmu.olympreg.entities.CompetitorProfile;
import ru.rsmu.olympreg.entities.ParticipationInfo;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author leonid.
 */
@XmlRootElement( name = "personInfo" )
public class PersonInfo {
    private String caseNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;

    public PersonInfo() {
    }

    public PersonInfo( ParticipationInfo participationInfo ) {
        CompetitorProfile profile = participationInfo.getProfile();
        caseNumber = profile.getCaseNumber();
        firstName = profile.getUser().getFirstName();
        middleName = profile.getUser().getMiddleName();
        lastName = profile.getUser().getLastName();
        email = profile.getUser().getUsername();
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber( String caseNumber ) {
        this.caseNumber = caseNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName( String middleName ) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }
}
