package ru.rsmu.olympreg.dao.internal;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import ru.rsmu.olympreg.dao.CountryDao;
import ru.rsmu.olympreg.entities.Country;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
public class CountryDaoImpl extends BaseDaoImpl implements CountryDao {
    @Override
    @SuppressWarnings( "unchecked" )
    public List<String> findNames( String startsWith, int maxResults ) {
        Criteria criteria = session.createCriteria( Country.class )
                .add( Restrictions.like( "name", "%" + startsWith + "%" ) )
                .addOrder( Order.asc( "name" ) )
                .setMaxResults( maxResults );
        List<Country> countries = criteria.list();
        return countries.stream().map( Country::getName ).collect( Collectors.toList());
    }

    @Override
    public Country findByName( String name ) {
        return (Country) session.createCriteria( Country.class )
                .add( Restrictions.eq( "name", name ) )
                .setMaxResults( 1 )
                .uniqueResult();
    }

    @Override
    public Country findByIso2( String iso2 ) {
        return (Country) session.createCriteria( Country.class )
                .add( Restrictions.eq( "iso2", iso2 ) )
                .setMaxResults( 1 )
                .uniqueResult();
    }

}
