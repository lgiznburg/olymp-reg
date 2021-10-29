package ru.rsmu.olympreg.dao.internal;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.*;
import ru.rsmu.olympreg.utils.PasswordEncoder;

import javax.persistence.criteria.CriteriaBuilder;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
                .setMaxResults( 1 );
        return (CompetitorProfile) criteria.uniqueResult();
    }

    @Override
    public List<UserCandidate> findOutdatedCandidates( Date time ) {
        Criteria criteria = session.createCriteria( UserCandidate.class )
                .add( Restrictions.lt( "createdDate", time ) );
        return criteria.list();
    }

}
