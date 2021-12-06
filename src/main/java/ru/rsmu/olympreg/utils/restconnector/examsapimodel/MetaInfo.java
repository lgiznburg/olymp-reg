package ru.rsmu.olympreg.utils.restconnector.examsapimodel;

/**
 * @author leonid.
 */
public class MetaInfo {
    private int total;
    private int pageSize;
    private int firstResult;

    public int getTotal() {
        return total;
    }

    public void setTotal( int total ) {
        this.total = total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize( int pageSize ) {
        this.pageSize = pageSize;
    }

    public int getFirstResult() {
        return firstResult;
    }

    public void setFirstResult( int firstResult ) {
        this.firstResult = firstResult;
    }
}
