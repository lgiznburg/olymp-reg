package ru.rsmu.olympreg.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author leonid.
 */
@Entity
@Table( name = "competitor_profile")
public class CompetitorProfile implements Serializable {
    private static final long serialVersionUID = 7121430629385756917L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @Column(name = "case_number")
    private String caseNumber;

    @Column
    private String sex;

    @Column(name = "birth_date")
    @Temporal( TemporalType.DATE )
    private Date birthDate;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private SubjectRegion region;

    @Column(name = "school_number")
    private String schoolNumber;

    @Column( name = "phone_number")
    private String phoneNumber;

    @Column(name = "class_number")
    private int classNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column( name = "profile_stage")
    @Enumerated( EnumType.STRING )
    private ProfileStage profileStage;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber( String caseNumber ) {
        this.caseNumber = caseNumber;
    }

    public String getSex() {
        return sex;
    }

    public void setSex( String sex ) {
        this.sex = sex;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate( Date birthDate ) {
        this.birthDate = birthDate;
    }

    public SubjectRegion getRegion() {
        return region;
    }

    public void setRegion( SubjectRegion region ) {
        this.region = region;
    }

    public String getSchoolNumber() {
        return schoolNumber;
    }

    public void setSchoolNumber( String schoolNumber ) {
        this.schoolNumber = schoolNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber( String phoneNumber ) {
        this.phoneNumber = phoneNumber;
    }

    public int getClassNumber() {
        return classNumber;
    }

    public void setClassNumber( int classNumber ) {
        this.classNumber = classNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser( User user ) {
        this.user = user;
    }

    public ProfileStage getProfileStage() {
        return profileStage;
    }

    public void setProfileStage( ProfileStage profileStage ) {
        this.profileStage = profileStage;
    }
}
