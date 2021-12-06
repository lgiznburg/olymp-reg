package ru.rsmu.olympreg.entities;

/**
 * @author leonid.
 */
public class AssignExamInfo {
    private int classNumber;
    private OlympiadSubject olympiadSubject;
    private long examExtarnalId;

    public AssignExamInfo() {
    }

    public AssignExamInfo( int classNumber, OlympiadSubject olympiadSubject, long examExtarnalId ) {
        this.classNumber = classNumber;
        this.olympiadSubject = olympiadSubject;
        this.examExtarnalId = examExtarnalId;
    }

    public int getClassNumber() {
        return classNumber;
    }

    public void setClassNumber( int classNumber ) {
        this.classNumber = classNumber;
    }

    public OlympiadSubject getOlympiadSubject() {
        return olympiadSubject;
    }

    public void setOlympiadSubject( OlympiadSubject olympiadSubject ) {
        this.olympiadSubject = olympiadSubject;
    }

    public long getExamExtarnalId() {
        return examExtarnalId;
    }

    public void setExamExtarnalId( long examExtarnalId ) {
        this.examExtarnalId = examExtarnalId;
    }
}
