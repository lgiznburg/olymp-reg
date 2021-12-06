package ru.rsmu.olympreg.utils.restconnector.examsapimodel;

import java.util.List;

/**
 * @author leonid.
 */
public class ExamResult {
    private MetaInfo meta;
    private List<PersonResult> results;

    public MetaInfo getMeta() {
        return meta;
    }

    public void setMeta( MetaInfo meta ) {
        this.meta = meta;
    }

    public List<PersonResult> getResults() {
        return results;
    }

    public void setResults( List<PersonResult> results ) {
        this.results = results;
    }
}
