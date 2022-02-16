package ru.rsmu.olympreg.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author leonid.
 */
@Entity
@Table( name = "participation_info")
public class ParticipationInfo implements Serializable {
    private static final long serialVersionUID = -7147447919164703753L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @Column(name = "olympiad_subject")
    @Enumerated(EnumType.STRING)
    private OlympiadSubject olympiadSubject;

    @Column(name = "start_date")
    @Temporal( TemporalType.TIMESTAMP )
    private Date startDate;

    @Column(name = "end_date")
    @Temporal( TemporalType.TIMESTAMP )
    private Date endDate;

    @Column(name = "duration")
    private String duration;

    @Column(name = "exam_name")
    private String examName;

    @Column(name = "testing_plan_name")
    private String testingPlanName;

    @Column(name = "token")
    private String token;

    @Column(name = "examId")
    private Long examId;

    @Column(name = "exam_result")
    private Integer result;

    @Column(name = "stage")
    private int stage;

    @Column(name = "approved")
    private Boolean approved = true;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private CompetitorProfile profile;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public OlympiadSubject getOlympiadSubject() {
        return olympiadSubject;
    }

    public void setOlympiadSubject( OlympiadSubject olympiadSubject ) {
        this.olympiadSubject = olympiadSubject;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate( Date startDate ) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate( Date endDate ) {
        this.endDate = endDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration( String duration ) {
        this.duration = duration;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName( String examName ) {
        this.examName = examName;
    }

    public String getTestingPlanName() {
        return testingPlanName;
    }

    public void setTestingPlanName( String testingPlanName ) {
        this.testingPlanName = testingPlanName;
    }

    public String getToken() {
        return token;
    }

    public void setToken( String token ) {
        this.token = token;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId( Long examId ) {
        this.examId = examId;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult( Integer result ) {
        this.result = result;
    }

    public int getStage() {
        return stage;
    }

    public void setStage( int stage ) {
        this.stage = stage;
    }

    public CompetitorProfile getProfile() {
        return profile;
    }

    public void setProfile( CompetitorProfile profile ) {
        this.profile = profile;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved( Boolean approved ) {
        this.approved = approved;
    }
}
