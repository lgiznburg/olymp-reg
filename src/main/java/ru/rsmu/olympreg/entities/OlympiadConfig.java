package ru.rsmu.olympreg.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Stack;

/**
 * @author leonid.
 */
@Entity
@Table(name = "olympiad_config")
public class OlympiadConfig implements Serializable {
    private static final long serialVersionUID = -5734187193200349603L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @Column
    @Enumerated(EnumType.STRING)
    private OlympiadSubject subject;

    @Column(name = "class_number")
    private int classNumber;

    @Column(name = "registration_start")
    @Temporal( TemporalType.DATE )
    private Date registrationStart;

    @Column(name = "registration_end")
    @Temporal( TemporalType.DATE )
    private Date registrationEnd;

    @Column(name = "first_stage")
    @Temporal( TemporalType.DATE )
    private Date firstStage;

    @Column(name = "second_stage")
    @Temporal( TemporalType.DATE )
    private Date secondStage;

    @Column(name = "second_registration_start")
    @Temporal( TemporalType.DATE )
    private Date secondStageRegistrationStart;

    @Column(name = "second_registration_end")
    @Temporal( TemporalType.DATE )
    private Date secondStageRegistrationEnd;

    @Column(name = "second_stage_pass_score")
    private int secondStagePassScore;

    @Column(name = "active")
    private boolean active;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public OlympiadSubject getSubject() {
        return subject;
    }

    public void setSubject( OlympiadSubject subject ) {
        this.subject = subject;
    }

    public int getClassNumber() {
        return classNumber;
    }

    public void setClassNumber( int classNumber ) {
        this.classNumber = classNumber;
    }

    public Date getRegistrationStart() {
        return registrationStart;
    }

    public void setRegistrationStart( Date registrationStart ) {
        this.registrationStart = registrationStart;
    }

    public Date getRegistrationEnd() {
        return registrationEnd;
    }

    public void setRegistrationEnd( Date registrationEnd ) {
        this.registrationEnd = registrationEnd;
    }

    public Date getFirstStage() {
        return firstStage;
    }

    public void setFirstStage( Date firstStage ) {
        this.firstStage = firstStage;
    }

    public Date getSecondStage() {
        return secondStage;
    }

    public void setSecondStage( Date secondStage ) {
        this.secondStage = secondStage;
    }

    public Date getSecondStageRegistrationStart() {
        return secondStageRegistrationStart;
    }

    public void setSecondStageRegistrationStart( Date secondStageRegistrationStart ) {
        this.secondStageRegistrationStart = secondStageRegistrationStart;
    }

    public Date getSecondStageRegistrationEnd() {
        return secondStageRegistrationEnd;
    }

    public void setSecondStageRegistrationEnd( Date secondStageRegistrationEnd ) {
        this.secondStageRegistrationEnd = secondStageRegistrationEnd;
    }

    public int getSecondStagePassScore() {
        return secondStagePassScore;
    }

    public void setSecondStagePassScore( int secondStagePassScore ) {
        this.secondStagePassScore = secondStagePassScore;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive( boolean active ) {
        this.active = active;
    }
}
