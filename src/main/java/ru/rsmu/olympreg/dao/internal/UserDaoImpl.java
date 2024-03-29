package ru.rsmu.olympreg.dao.internal;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.*;
import ru.rsmu.olympreg.utils.PasswordEncoder;
import ru.rsmu.olympreg.utils.YearHelper;
import ru.rsmu.olympreg.viewentities.CompetitorFilter;
import ru.rsmu.olympreg.viewentities.SortCriterion;

import javax.persistence.criteria.CriteriaBuilder;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author leonid.
 */
@SuppressWarnings( "unchecked" )
public class UserDaoImpl extends BaseDaoImpl implements UserDao {
    @Override
    public User findByUsername( String username ) {
        Criteria criteria = session.createCriteria( User.class )
                .add( Restrictions.eq( "username", username ) )
                .setMaxResults( 1 );
        return (User) criteria.uniqueResult();
    }


    @Override
    public String encrypt( String password ) {
        try {
            return PasswordEncoder.encrypt( password );
        } catch ( NoSuchAlgorithmException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public List<User> finUserForRole( UserRoleName roleName ) {
        Criteria criteria = session.createCriteria( User.class )
                .createAlias("roles", "roles"  )
                .add( Restrictions.eq( "roles.roleName", roleName ) )
                .setResultTransformer( CriteriaSpecification.DISTINCT_ROOT_ENTITY );
        return criteria.list();
    }

    @Override
    public int getNextPersonalNumber() {
        Calendar calendar = Calendar.getInstance();
        Criteria criteria = session.createCriteria( CompetitorCounter.class )
                .add( Restrictions.eq( "year", calendar.get( Calendar.YEAR ) ) )
                .setMaxResults( 1 );
        CompetitorCounter counter = (CompetitorCounter) criteria.uniqueResult();
        int result = counter.getCounter();
        counter.setCounter( result + 1 );
        session.saveOrUpdate( counter );
        return calendar.get( Calendar.YEAR) * 100000 + result;
    }

    @Override
    public UserRole findRole( UserRoleName roleName ) {
        Criteria criteria = session.createCriteria( UserRole.class )
                .add( Restrictions.eq( "roleName", roleName ) )
                .setMaxResults( 1 );
        return (UserRole) criteria.uniqueResult();
    }

    @Override
    public CompetitorProfile findProfile( User user ) {
        Criteria criteria =  session.createCriteria( CompetitorProfile.class )
                .add( Restrictions.eq( "user", user  ) )
                .add( Restrictions.eq( "year", YearHelper.getActualYear() ) )
                .setMaxResults( 1 );
        return (CompetitorProfile) criteria.uniqueResult();
    }

    @Override
    public List<UserCandidate> findOutdatedCandidates( Date time ) {
        Criteria criteria = session.createCriteria( UserCandidate.class )
                .add( Restrictions.lt( "createdDate", time ) );
        return criteria.list();
    }

    @Override
    public int countUsers( CompetitorFilter filter ) {
        return ((Long)buildCriteria( filter, null )
                .setProjection( Projections.rowCount() )
                .uniqueResult()).intValue();
    }

    @Override
    public List<User> findFilteredUsers( CompetitorFilter filter, List<SortCriterion> sortCriteria, int startIndex, int size ) {
        Criteria criteria = buildCriteria( filter, sortCriteria )
                .setFirstResult( startIndex )
                .setMaxResults( size );
        return criteria.list();
    }

    @Override
    public int countCandidates( CompetitorFilter filter ) {
        return ((Long)buildCandidatesCriteria( filter, null )
                .setProjection( Projections.rowCount() )
                .uniqueResult()).intValue();
    }

    @Override
    public List<UserCandidate> findFilteredCandidates( CompetitorFilter filter, List<SortCriterion> sortCriteria, int startIndex, int size ) {
        Criteria criteria = buildCandidatesCriteria( filter, sortCriteria )
                .setFirstResult( startIndex )
                .setMaxResults( size );
        return criteria.list();
    }

    private Criteria buildCriteria( CompetitorFilter filter, List<SortCriterion> sortCriteria ) {
        Criteria criteria = session.createCriteria( User.class );
        if ( StringUtils.isNotBlank( filter.getEmail() ) ) {
            criteria.add( Restrictions.like( "username", "%" + filter.getEmail() + "%" ) );
        }
        if ( StringUtils.isNotBlank( filter.getLastName() ) ) {
            criteria.add( Restrictions.like( "lastName", "%" + filter.getLastName() + "%" ) );
        }
        if ( filter.getRoleName() != null ) {
            criteria.createAlias( "roles", "roles" )
                    .add( Restrictions.like( "roles.roleName", filter.getRoleName() ) );
        }

        if ( sortCriteria != null ) {
            AtomicBoolean lastNameOrder = new AtomicBoolean( false );
            sortCriteria.forEach( sc -> {
                if ( sc.getPropertyName().equalsIgnoreCase( "lastName" ) ) {
                    lastNameOrder.set( true );
                }
                if ( sc.getDirection() == SortCriterion.ASCENDING ) {
                    criteria.addOrder( Order.asc( sc.getPropertyName() ) );
                }
                else if ( sc.getDirection() == SortCriterion.DESCENDING ) {
                    criteria.addOrder( Order.desc( sc.getPropertyName() ) );
                }
            } );
            if ( !lastNameOrder.get() ) {
                criteria.addOrder( Order.asc("lastName") );
            }
        }
        return  criteria;
    }

    private Criteria buildCandidatesCriteria( CompetitorFilter filter, List<SortCriterion> sortCriteria ) {
        Criteria criteria = session.createCriteria( UserCandidate.class );
        if ( StringUtils.isNotBlank( filter.getEmail() ) ) {
            criteria.add( Restrictions.like( "email", "%" + filter.getEmail() + "%" ) );
        }
        if ( StringUtils.isNotBlank( filter.getLastName() ) ) {
            criteria.add( Restrictions.like( "lastName", "%" + filter.getLastName() + "%" ) );
        }
        else {
            criteria.add( Restrictions.neOrIsNotNull( "lastName", "" ) );
        }
        criteria.add( Restrictions.eq( "type", UserCandidateType.account ) );
        if ( sortCriteria != null ) {
            AtomicBoolean emailOrder = new AtomicBoolean( false );
            sortCriteria.forEach( sc -> {
                if ( sc.getPropertyName().equalsIgnoreCase( "email" ) ) {
                    emailOrder.set( true );
                }
                if ( sc.getDirection() == SortCriterion.ASCENDING ) {
                    criteria.addOrder( Order.asc( sc.getPropertyName() ) );
                }
                else if ( sc.getDirection() == SortCriterion.DESCENDING ) {
                    criteria.addOrder( Order.desc( sc.getPropertyName() ) );
                }
            } );
            if ( !emailOrder.get() ) {
                criteria.addOrder( Order.asc("email") );
            }
        }
        return criteria;
    }

}
