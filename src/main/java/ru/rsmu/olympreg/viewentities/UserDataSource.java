package ru.rsmu.olympreg.viewentities;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
public class UserDataSource  implements GridDataSource {

    private UserDao userDao;

    private List<User> userList;

    private CompetitorFilter filter;

    private int startIndex;

    public UserDataSource( UserDao userDao, CompetitorFilter filter ) {
        this.userDao = userDao;
        this.filter = filter;
    }

    @Override
    public int getAvailableRows() {
        return userDao.countUsers( filter );
    }

    @Override
    public void prepare( final int startIndex, final int endIndex, final List<SortConstraint> sortConstraints ) {
        userList = userDao.findFilteredUsers( filter, toSortCriteria( sortConstraints ), startIndex, endIndex-startIndex+1 );
        this.startIndex = startIndex;
    }

    @Override
    public Object getRowValue( int i ) {
        return userList.get( i - startIndex );
    }

    @Override
    public Class<?> getRowType() {
        return User.class;
    }

    private List<SortCriterion> toSortCriteria( final List<SortConstraint> sortConstraints ) {
        return sortConstraints.stream().map( SortCriterion::new ).collect( Collectors.toList());
    }
}
