package ru.rsmu.olympreg.viewentities;

import ru.rsmu.olympreg.entities.OlympiadSubject;
import ru.rsmu.olympreg.entities.UserRoleName;

import javax.security.auth.Subject;

/**
 * @author leonid.
 */
public class CompetitorFilter {

    private String email = "";

    private String firstName = "";

    private String personalNumber = "";

    private String lastName = "";

    private String completed = "";

    private UserRoleName roleName;

    private OlympiadSubject subject;

    private boolean secondStage;

    private boolean needApproval;

    private Integer classNumber;

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber( String personalNumber ) {
        this.personalNumber = personalNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted( String completed ) {
        this.completed = completed;
    }

    public UserRoleName getRoleName() {
        return roleName;
    }

    public void setRoleName( UserRoleName roleName ) {
        this.roleName = roleName;
    }

    public OlympiadSubject getSubject() {
        return subject;
    }

    public void setSubject( OlympiadSubject subject ) {
        this.subject = subject;
    }

    public boolean isSecondStage() {
        return secondStage;
    }

    public void setSecondStage( boolean secondStage ) {
        this.secondStage = secondStage;
    }

    public boolean isNeedApproval() {
        return needApproval;
    }

    public void setNeedApproval( boolean needApproval ) {
        this.needApproval = needApproval;
    }

    public Integer getClassNumber() {
        return classNumber;
    }

    public void setClassNumber( Integer classNumber ) {
        this.classNumber = classNumber;
    }
}
