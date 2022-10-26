package ru.rsmu.olympreg.entities;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private Integer classNumber;

    @Column(name = "school_location")
    @Enumerated( EnumType.STRING )
    private SchoolLocation schoolLocation;

    @Column
    private Integer year;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column( name = "profile_stage")
    @Enumerated( EnumType.STRING )
    private ProfileStage profileStage;

    @OneToMany(mappedBy = "profile")
    private List<AttachedFile> attachments;

    @OneToMany(mappedBy = "profile")
    private List<ParticipationInfo> participation;

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

    public Integer getClassNumber() {
        return classNumber;
    }

    public void setClassNumber( Integer classNumber ) {
        this.classNumber = classNumber;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear( Integer year ) {
        this.year = year;
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

    public List<AttachedFile> getAttachments() {
        return attachments;
    }

    public void setAttachments( List<AttachedFile> attachments ) {
        this.attachments = attachments;
    }

    public void addAttachment( AttachedFile attachedFile ) {
        attachedFile.setProfile( this );
        if ( attachments == null ) {
            attachments =  new ArrayList<>();
        }
        attachments.add( attachedFile );
    }

    public SchoolLocation getSchoolLocation() {
        return schoolLocation;
    }

    public void setSchoolLocation( SchoolLocation schoolLocation ) {
        this.schoolLocation = schoolLocation;
    }

    public List<ParticipationInfo> getParticipation() {
        return participation;
    }

    public void setParticipation( List<ParticipationInfo> participations ) {
        this.participation = participations;
    }

    @Transient
    public boolean isProfileCompleted() {
        return !( StringUtils.isBlank( sex ) || StringUtils.isBlank( schoolNumber )
                || StringUtils.isBlank( phoneNumber ) || region == null
                || classNumber == null || birthDate == null
                || schoolLocation == null
        );
    }

    @Transient
    public boolean isAttachmentsCompleted() {
        return attachments != null
                && attachments.stream().anyMatch( at -> at.getAttachmentRole() == AttachmentRole.AGREEMENT )
                && attachments.stream().anyMatch( at -> at.getAttachmentRole() == AttachmentRole.PASSPORT )
                && attachments.stream().anyMatch( at -> at.getAttachmentRole() == AttachmentRole.SCHOOL );
    }

    @Transient
    public boolean isSubjectSelected() {
        return participation != null && !participation.isEmpty();
    }
}
