package ru.rsmu.olympreg.viewentities;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import ru.rsmu.olympreg.dao.CompetitorDao;
import ru.rsmu.olympreg.entities.CompetitorProfile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
public class CompetitorDataSource implements GridDataSource {

    private CompetitorDao competitorDao;

    private CompetitorFilter filter;

    private int startIndex;

    private List<CompetitorProfile> profiles;

    public CompetitorDataSource( CompetitorDao competitorDao, CompetitorFilter filter ) {
        this.competitorDao = competitorDao;
        this.filter = filter;
    }

    @Override
    public int getAvailableRows() {
        return competitorDao.countProfiles( filter );
    }

    @Override
    public void prepare( final int startIndex, final int endIndex, final List<SortConstraint> sortConstraints) {

        profiles = competitorDao.findProfiles( filter, toSortCriteria( sortConstraints ), startIndex, endIndex-startIndex+1 );
        this.startIndex = startIndex;
    }

    @Override
    public Object getRowValue( int index ) {
        return profiles.get( index - startIndex );
    }

    @Override
    public Class<?> getRowType() {
        return CompetitorProfile.class;
    }

    private List<SortCriterion> toSortCriteria( final List<SortConstraint> sortConstraints ) {
        return sortConstraints.stream().map( SortCriterion::new ).collect( Collectors.toList());
    }
}
