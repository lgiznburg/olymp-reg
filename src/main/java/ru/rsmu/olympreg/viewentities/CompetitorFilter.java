package ru.rsmu.olympreg.viewentities;

/**
 * @author leonid.
 */
public class CompetitorFilter {

    private String email = "";

    private String firstName = "";

    private String personalNumber = "";

    private String lastName = "";

    private String completed = "";

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
}
