package ru.rsmu.olympreg.viewentities;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.UserCandidate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
public class CandidateDataSource implements GridDataSource {

    private UserDao userDao;

    private CompetitorFilter filter;

    private List<UserCandidate> candidateList;

    private int startIndex;

    public CandidateDataSource( UserDao userDao, CompetitorFilter filter ) {
        this.userDao = userDao;
        this.filter = filter;
    }

    @Override
    public int getAvailableRows() {
        return userDao.countCandidates( filter );
    }

    @Override
    public void prepare( final int startIndex, final int endIndex, List<SortConstraint> sortConstraints ) {
        candidateList = userDao.findFilteredCandidates( filter, toSortCriteria( sortConstraints ), startIndex, endIndex-startIndex+1 );
        this.startIndex = startIndex;
    }

    @Override
    public Object getRowValue( int i ) {
        return candidateList.get( i - startIndex );
    }

    @Override
    public Class<?> getRowType() {
        return UserCandidate.class;
    }

    private List<SortCriterion> toSortCriteria( final List<SortConstraint> sortConstraints ) {
        return sortConstraints.stream().map( SortCriterion::new ).collect( Collectors.toList());
    }
}
