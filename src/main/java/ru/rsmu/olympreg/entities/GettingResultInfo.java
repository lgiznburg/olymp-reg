package ru.rsmu.olympreg.entities;

/**
 * @author leonid.
 */
public class GettingResultInfo {
    private long examId;
    private int firstResult;

    public GettingResultInfo() {
    }

    public GettingResultInfo( long examId, int firstResult ) {
        this.examId = examId;
        this.firstResult = firstResult;
    }

    public long getExamId() {
        return examId;
    }

    public void setExamId( long examId ) {
        this.examId = examId;
    }

    public int getFirstResult() {
        return firstResult;
    }

    public void setFirstResult( int firstResult ) {
        this.firstResult = firstResult;
    }
}
