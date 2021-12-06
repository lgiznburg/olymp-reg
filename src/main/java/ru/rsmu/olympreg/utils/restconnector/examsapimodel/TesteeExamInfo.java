package ru.rsmu.olympreg.utils.restconnector.examsapimodel;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * @author leonid.
 */
public class TesteeExamInfo {

    private String caseNumber;
    private String examName;
    private String testingPlanName;
    private Date examStartTime;
    private Date examEndTime;
    private String duration;
    private String token;
    private Long examId;

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber( String caseNumber ) {
        this.caseNumber = caseNumber;
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

    public Date getExamStartTime() {
        return examStartTime;
    }

    public void setExamStartTime( Date examStartTime ) {
        this.examStartTime = examStartTime;
    }

    public Date getExamEndTime() {
        return examEndTime;
    }

    public void setExamEndTime( Date examEndTime ) {
        this.examEndTime = examEndTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration( String duration ) {
        this.duration = duration;
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
}
